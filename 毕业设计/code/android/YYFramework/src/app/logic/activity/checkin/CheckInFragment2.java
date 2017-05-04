package app.logic.activity.checkin;

import java.util.List;

import org.ql.app.alert.AlertDialog;
import org.ql.utils.QLDateUtils;
import org.ql.utils.QLToastUtils;
import org.ql.utils.image.QLAsyncImage;
import org.ql.views.ImageView.CircleImageView;
import org.ql.views.listview.QLXListView;
import org.ql.views.listview.QLXListView.IXListViewListener;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.LogoPosition;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager.LayoutParams;
import android.text.Html;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import app.config.http.HttpConfig;
import app.logic.adapter.YYBaseListAdapter;
import app.logic.controller.CheckInController;
import app.logic.controller.OrganizationController;
import app.logic.controller.UserManagerController;
import app.logic.pojo.CheckInInfo;
import app.logic.pojo.OrganizationInfo;
import app.logic.pojo.TYLocationInfo;
import app.logic.pojo.UserInfo;
import app.logic.singleton.ZSZSingleton;
import app.utils.common.Listener;
import app.utils.dialog.YYDialogView;
import app.utils.helpers.LocationPicker;
import app.utils.helpers.YYUtils;
import app.utils.managers.TYLocationManager;
import app.utils.managers.TYLocationManager.TYLocationListener;
import app.view.YYListView;
import app.yy.geju.R;

/**
 * 
 * SiuJiYung create at 2016年8月4日 上午11:29:22
 * 
 */
public class CheckInFragment2 extends Fragment implements TYLocationListener, OnClickListener{
	
	private TextureMapView mapView;          //地图视图
	private TextView addrTvTextView , addrPickerTextView;  //显示位置
	private TextView dateTextView,singinTextView;    //日期
	private TextView timeTextView;    //时间
	private ImageButton checkInBtn;   //签到按钮
	private OrganizationInfo currOrganizationInfo;       //组织对象
	private BaiduMap baiduMap;                           //百度地图对象
	private TYLocationInfo currLocationInfo , myStartLocationInfo;
	private View view  ;  //
	private BitmapDescriptor bitmap ;

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.from(getActivity()).inflate(R.layout.activity_main_check_in2,container, false);
			setView(view);
		}
		ViewGroup parent = (ViewGroup) view.getParent();
		if (parent != null) {
			parent.removeView(view);
		}
		return view;
	}

	/**
	 * 初始化View
	 * @param view
	 */
	private void setView(View view) {
		bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_map3x);
		mapView = (TextureMapView) view.findViewById(R.id.cell_check_in_map_view);
		mapView.showZoomControls(false);
		mapView.showScaleControl(false);
		mapView.setEnabled(false);
		baiduMap = mapView.getMap();
		baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		baiduMap.setMyLocationEnabled(false);
		baiduMap.getUiSettings().setAllGesturesEnabled(false);// 拖动

		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(18.5f);
		baiduMap.setMapStatus( msu );
		//位置
		addrTvTextView = (TextView) view.findViewById(R.id.cell_check_in_addr_tv);
		//addrTvTextView.setOnClickListener(this);
		//点击地图跳转
		addrPickerTextView = (TextView) view.findViewById(R.id.addr_Picker_TextView);
		addrPickerTextView.setOnClickListener(this);
		dateTextView = (TextView) view.findViewById(R.id.checkin_date_tv);     //日期
		timeTextView = (TextView) view.findViewById(R.id.checkin_time_tv);     //时间
		singinTextView= (TextView) view.findViewById(R.id.checkin_singin_tv);  //签到
		checkInBtn = (ImageButton) view.findViewById(R.id.check_in_btn);       //签到按钮
		//获取日期 和 时间
		updateHeadView();	
		checkInBtn.setOnClickListener(this);//签到按钮设置监听
		//获取当前自己的位置（目的：为了更快加载出地图）
		myStartLocationInfo = TYLocationManager.getShareLocationManager().getCurrLocationInfo();
		updateLocationView(myStartLocationInfo , false);
	}

	/**
	 *获取时间和日期 
	 */
	private void updateHeadView() {
		String date_week_str = QLDateUtils.getWeekDateString( null );
		String date_str = QLDateUtils.getTimeWithFormat( null , "yyyy年MM月dd日");
		String time_str = QLDateUtils.getTimeWithFormat( null , "HH:mm");
		StringBuilder dt_builder = new StringBuilder();
		dt_builder.append("<font color=#ff888888>");
		//dt_builder.append(date_week_str);
		dt_builder.append("</font>");
		dt_builder.append("<font color=#ff33a6ff>");
		dt_builder.append(date_str);
		dt_builder.append("</font>");
		dateTextView.setText(Html.fromHtml(dt_builder.toString()));
		StringBuilder time_builder = new StringBuilder();
		time_builder.append("<font color=#ff888888>");
		//time_builder.append("当前时间:");
		time_builder.append("</font>");
		time_builder.append("<font color=#ff33a6ff>");
		time_builder.append(time_str);
		time_builder.append("</font>");
		timeTextView.setText(Html.fromHtml(time_builder.toString()));
	}
	/**
	 * 更新位置
	 * @param info
	 */
	private void updateLocationView(final TYLocationInfo info , boolean b) {
		if(null == info ){
			return;
		}
//		baiduMap.clear();
//		LatLng llA = new LatLng(info.getLatitude(), info.getLongitude());
//		CoordinateConverter converter = new CoordinateConverter();
//		converter.coord(llA);
//		converter.from(CoordinateConverter.CoordType.COMMON);
//		LatLng convertLatLng = converter.convert();
//		OverlayOptions ooA = new MarkerOptions().position(convertLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_map3x)).zIndex(4).draggable(true);
//		baiduMap.addOverlay(ooA);
//		MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(convertLatLng, 17.0f);
//		baiduMap.animateMapStatus(u);
		if(b){
			TYLocationManager.getShareLocationManager().stop();
		}
//		baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(new LatLng(info.latitude,info.longitude)));
		//获取到新位置，并设置
		addrTvTextView.post( new Runnable() {
			@Override
			public void run() {
				addrTvTextView.setText(info.getLocationAddr());
				baiduMap.clear();//这里的作用是移除原来的覆盖物（覆盖物太多会OOM）
				LatLng p = new  LatLng(info.latitude,info.longitude);
				//构建Marker图标
				//构建MarkerOption，用于在地图上添加Marker
				OverlayOptions option = new MarkerOptions().position(p).icon(bitmap);
				//在地图上添加Marker，并显示
				baiduMap.addOverlay(option);
				baiduMap.setMyLocationEnabled(true);
				MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(p);
				baiduMap.animateMapStatus(mapStatusUpdate,15);
			}
		});
	}

	/**
	 * 加载位置
	 */
	@Override
	public void onLocationChange(TYLocationInfo info) {
		if (info != null && currLocationInfo == null) {
			currLocationInfo = info;
		} else if (currLocationInfo == null) {
			currLocationInfo = TYLocationManager.getShareLocationManager().getCurrLocationInfo();
		}
		//更新位置
		updateLocationView( currLocationInfo ,true );
	}

	/**
	 * 签到函数
	 */
	private void checkIn() {
		TYLocationInfo info = null ;
		if( null != currLocationInfo ){
			info = currLocationInfo ;
		}
		if ( info == null ) {			
			return;
		}
		if ( currOrganizationInfo == null ) {			
			return;
		}
		String org_id = currOrganizationInfo.getOrg_id();
		//发起签到
		CheckInController.checkIn(getContext(), info, org_id, new Listener<Boolean, String>() {
			@Override
			public void onCallBack(Boolean status, String reply) {
				if (status) {
					// 签到成功（ 图片不可用）
					checkInBtn.setEnabled(false);
					singinTextView.setText("已签到");
					QLToastUtils.showToast(getContext(), "签到成功");
					timeTextView.setVisibility( View.GONE) ;
				}else {
					singinTextView.setText("签到");
					QLToastUtils.showToast(getContext(), "签到失败");
				}			
			}
		});
	}
	
	/**
	 * 获取 签到列表
	 */
	private void getCheckInList() {
		if (currOrganizationInfo == null) {
			return;
		}
		String org_id = currOrganizationInfo.getOrg_id();
		String start_dt = QLDateUtils.getTimeWithFormat(null, "yyyy-MM-dd");
		String end_dt = start_dt;
		CheckInController.getCheckInList(getContext(), start_dt, end_dt, org_id, new Listener<Void, List<CheckInInfo>>() {
			@Override
			public void onCallBack( Void status, List<CheckInInfo> reply ) {
				if (reply != null && reply.size() > 0) {
					checkInBtn.setEnabled(false) ;//签到过，图片不可用
					singinTextView.setText("已签到");
					timeTextView.setVisibility( View.GONE) ;
				} else {
					checkInBtn.setEnabled(true) ;//没签到过，图片可用
					singinTextView.setText("签到");
				}
			}
		});
	}

	
	@Override
	public void onClick( View v ) {
		int vid = v.getId();
		switch (vid) {
		case R.id.addr_Picker_TextView:
			TYLocationInfo info = null ;
			if(null!=currLocationInfo){
				info = currLocationInfo ;
			}else{
				info = myStartLocationInfo ;
			}
			Intent intent = new Intent();
			Gson gson = new Gson();
			String location_json = gson.toJson(info);
			intent.setClass(getContext(), LocationPicker.class);
			intent.putExtra(LocationPicker.kInitLocationInfoKey , location_json );
			startActivityForResult(intent, 11);
			break;
		case R.id.check_in_btn:// 签到			
			checkIn();
			break;
		}
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		mapView.onResume();
		//获取签到列表
		currOrganizationInfo = (OrganizationInfo) getActivity().getIntent().getSerializableExtra(MyOrganizaActivity.ORGINFO);
		//获取签到列表
		getCheckInList();
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			//添加监听器
			TYLocationManager.getShareLocationManager().setLocationListener(this);
			//开始加载位置
			TYLocationManager.getShareLocationManager().start();
		} else {
			TYLocationManager.getShareLocationManager().stop();
			TYLocationManager.getShareLocationManager().setLocationListener(null);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		mapView.onPause();
//		TYLocationManager.getShareLocationManager().setLocationListener(null);
//		TYLocationManager.getShareLocationManager().stop();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if ( requestCode == 11 && resultCode == Activity.RESULT_OK && data != null ) {
			String location_json = data.getStringExtra(LocationPicker.kPickLocationResultKey);
			Gson gson = new Gson();
			currLocationInfo = gson.fromJson(location_json, TYLocationInfo.class);
			//更新位置
			updateLocationView( currLocationInfo ,true);
		}
	}
	
	@Override
	public void onGPSStatuChange(int event) {
		// TODO Auto-generated method stub

	}


	@Override
	public void onRequestLocationStart() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRequestLocationFinish() {
		// TODO Auto-generated method stub

	}

}
