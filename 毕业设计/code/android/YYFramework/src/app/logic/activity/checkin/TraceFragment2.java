package app.logic.activity.checkin;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ql.app.alert.AlertDialog;
import org.ql.utils.QLDateUtils;
import org.ql.utils.QLToastUtils;
import org.ql.utils.image.QLAsyncImage;
import org.ql.views.ImageView.AsyncImageView;
import org.ql.views.ImageView.CircleImageView;
import org.ql.views.listview.QLXListView;
import org.ql.views.listview.QLXListView.IXListViewListener;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.TimePickerView.OnTimeSelectListener;
import com.bigkoo.pickerview.listener.OnDismissListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.picasso.Picasso;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserManager;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.support.v4.view.ViewPager.LayoutParams;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import app.config.http.HttpConfig;
import app.logic.activity.notice.DefaultNoticeActivity;
import app.logic.adapter.YYBaseListAdapter;
import app.logic.controller.CheckInController;
import app.logic.controller.OrganizationController;
import app.logic.controller.UserManagerController;
import app.logic.pojo.CheckInInfo;
import app.logic.pojo.OrganizationInfo;
import app.logic.pojo.TYLocationInfo;
import app.logic.pojo.UnCheckInInfo;
import app.logic.pojo.UserInfo;
import app.logic.singleton.ZSZSingleton;
import app.utils.common.FrescoImageShowThumb;
import app.utils.common.Listener;
import app.utils.common.Public;
import app.utils.dialog.YYDialogView;
import app.utils.managers.TYLocationManager;
import app.utils.managers.TYLocationManager.TYLocationListener;
import app.view.YYListView;
import app.yy.geju.R;


/**
 * SiuJiYung create at 2016年8月4日 下午5:14:26
 */

public class TraceFragment2 extends Fragment implements IXListViewListener, TYLocationListener, OnClickListener {

	private GridView mGridView;  //未签到列表
	private QLXListView trackListView ; //签到列表
	private View listView_rl ;
	private View gridView_rl ;
	private View view ;
    private TextView yesNnbTv;
    private TextView yesTv;
    private TextView noNubTv;
    private TextView noTv;
    private TextureMapView mapViewBg;
    private BaiduMap map;
    private TextView dateTextView;  //日期
	private String currDate;
	private OrganizationInfo currOrganizationInfo;     //组织对象
	private TimePickerView timePickerView;
	private int index = 0;
	private BitmapDescriptor bitmap ;

	/**
	 * 没有签到适配器
	 */
	private YYBaseListAdapter<UnCheckInInfo> mNAdapter = new YYBaseListAdapter<UnCheckInInfo>( getContext()) {
		@Override
		public View createView( int position, View convertView, ViewGroup parent ) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.n_signin_item, null);
				saveView("n_signin_img", R.id.n_signin_img, convertView);
				saveView("n_signin_name", R.id.n_signin_name, convertView);
			}
			UnCheckInInfo unInfo = getItem(position);
			if (unInfo != null) {
				SimpleDraweeView headView = getViewForName("n_signin_img", convertView);
				TextView nameView = getViewForName("n_signin_name", convertView);
//				Picasso.with(getContext()).load(HttpConfig.getUrl(unInfo.getPicture_url())).
//                        placeholder(R.drawable.default_user_icon).error(R.drawable.default_user_icon).
//                        fit().centerCrop().into(headView);
//				headView.setImageURI(HttpConfig.getUrl(unInfo.getPicture_url()));
				FrescoImageShowThumb.showThrumb(Uri.parse(HttpConfig.getUrl(unInfo.getPicture_url())),headView);
				if(unInfo.getFriend_name()!=null&& !TextUtils.isEmpty(unInfo.getFriend_name())){
					nameView.setText(unInfo.getFriend_name());
				}else{
					nameView.setText(unInfo.getNickName());
				}
			}
			return convertView;
		}
	};

	/**
	 * 签到的适配器
	 */
    private YYBaseListAdapter<CheckInInfo> mYAdapter = new YYBaseListAdapter<CheckInInfo>(getContext()) {
		@Override
		public View createView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.cell_track_today, null);
				saveView("cell_track_user_head_view", R.id.cell_track_user_head_view, convertView);
				saveView("cell_track_name_view", R.id.cell_track_name_view, convertView);
				saveView("cell_track_time_view", R.id.cell_track_time_view, convertView);
				saveView("cell_track_addr_view", R.id.cell_track_addr_view, convertView);
			}
			CheckInInfo info = getItem(position);
			if (info != null) {
				SimpleDraweeView headView = getViewForName("cell_track_user_head_view", convertView);
				TextView timeView = getViewForName("cell_track_time_view", convertView);
				TextView nameView = getViewForName("cell_track_name_view", convertView);
				TextView addrView = getViewForName("cell_track_addr_view", convertView);
				FrescoImageShowThumb.showThrumb(Uri.parse(HttpConfig.getUrl(info.getPicture_url())),headView);
//				headView.setImageURI(HttpConfig.getUrl(info.getPicture_url()));
//				Picasso.with(getContext()).load(HttpConfig.getUrl(info.getPicture_url())).placeholder(R.drawable.default_user_icon).error(R.drawable.default_user_icon).fit().centerCrop().into(headView);
				Date dt_create = QLDateUtils.createDateTimeFromString(info.getCreate_time(), "yyyy-MM-dd HH:mm:ss");
				String dtString = QLDateUtils.getTimeWithFormat(dt_create, "HH:mm");
				timeView.setText(dtString);
				if(info.getFriend_name()!=null&& !TextUtils.isEmpty(info.getFriend_name())){
					nameView.setText(info.getFriend_name());
				}else{
					nameView.setText(info.getNickName());
				}

				addrView.setText(info.getChin_addr());
			}
			return convertView;
		}
	};

	@Override
	public View onCreateView( LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if ( view == null) {
			view = inflater.inflate(R.layout.activity_trace2, null);
			setLLView( view ) ;
			setView( view ) ;
		}
		ViewGroup parent = ( ViewGroup ) view.getParent();
		if (parent != null) {
			parent.removeView(view);
		}
		return view;
	}

	private void setLLView( View view){
		listView_rl = view.findViewById( R.id.listeView_rl) ;
		gridView_rl = view.findViewById( R.id.gridView_rl) ;
		mGridView = (GridView) view.findViewById( R.id.n_frag_singin_gv );
		trackListView = (QLXListView) view.findViewById( R.id.y_frag_tract_list_view );
		listView_rl.setVisibility( View.VISIBLE ); //签到列表
		gridView_rl.setVisibility( View.GONE);     //未签到列表
		trackListView.setXListViewListener(this) ;
		trackListView.setPullRefreshEnable(true);
		trackListView.setPullLoadEnable(false, true);
		//设置适配器
		trackListView.setAdapter( mYAdapter );
		mGridView.setAdapter( mNAdapter );
		yesNnbTv =(TextView) view.findViewById(R.id.textYesReadNub);
		yesTv =(TextView) view.findViewById(R.id.textYesRead);
		noNubTv =(TextView) view.findViewById(R.id.textNoReadNub);
		noTv =(TextView) view.findViewById(R.id.textNoRead);
		yesTv.setOnClickListener( this ) ;
		noTv.setOnClickListener( this ) ;		
		yesNnbTv.setOnClickListener( this ) ;
		noNubTv.setOnClickListener( this ) ;
		yesNnbTv.setText("0");
		noNubTv.setText("0");
		yesNnbTv.setTextColor(getResources().getColor(R.color.new_y_singnin_text_col));
		yesTv.setTextColor(getResources().getColor(R.color.new_y_singnin_text_col)); 
		noNubTv.setTextColor(getResources().getColor(R.color.new_n_singnin_text_col)); 
		noTv.setTextColor(getResources().getColor(R.color.new_n_singnin_text_col)); 
	}
	
	/**
	 * 初始化View
	 * @param view
	 */
	private void setView( View view ) {
		bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_map3x);
		mapViewBg = (TextureMapView) view.findViewById(R.id.tract_map_bg_view);
		mapViewBg.showZoomControls(false);
		mapViewBg.showScaleControl(false);
		map = mapViewBg.getMap();
		map.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		map.setBaiduHeatMapEnabled(false);
		map.setMyLocationEnabled(false);
		map.getUiSettings().setAllGesturesEnabled(false);
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(18.5f);
		map.setMapStatus( msu );
		//日期
	    dateTextView = (TextView) view.findViewById(R.id.tract_date_tv);
	    dateTextView.setOnClickListener(this);
		//获取日期(系统的当前的日期)
		currDate = QLDateUtils.getTimeWithFormat( null, "yyyy-MM-dd" );
		dateTextView.setText(currDate);
		//获取当前自己的位置（加载比较快，但有时后会出现蓝屏，还不知道原因？）
		TYLocationInfo info = TYLocationManager.getShareLocationManager().getCurrLocationInfo();
		//获取地点
		updateLocation(info,false);
	}

	@Override
	public void onResume() {
		super.onResume() ;
//		TYLocationManager.getShareLocationManager().setLocationListener(this);
//		TYLocationManager.getShareLocationManager().start();
		mapViewBg.onResume();
		currOrganizationInfo = (OrganizationInfo) getActivity().getIntent().getSerializableExtra(MyOrganizaActivity.ORGINFO);
		//获取足迹列表
		getTrackList();
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
			TYLocationManager.getShareLocationManager().setLocationListener(null);
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		mapViewBg.onPause();
//		TYLocationManager.getShareLocationManager().setLocationListener(null);
	}
	/**
	 * 选择日期
	 */
	private void selectDate() {
		if (timePickerView == null) {
			timePickerView = new TimePickerView( getContext(), TimePickerView.Type.YEAR_MONTH_DAY);
		}
		timePickerView.setTime(QLDateUtils.getDateTimeNow());
		timePickerView.setCyclic(true);
		timePickerView.setCancelable(true);
		timePickerView.setOnTimeSelectListener( new OnTimeSelectListener() {
			@Override
			public void onTimeSelect( Date date ) {
				String dt_str = QLDateUtils.getTimeWithFormat(date, "yyyy-MM-dd");
				dateTextView.setText(dt_str);
				currDate = dt_str;
				//获取列表（重新获取签到列表）
				getTrackList();
			}
		});
		if ( timePickerView.isShowing() ) {
			return;
		}
		timePickerView.show();//日期选择器
	}
    /**
     * 获取地点
     *
     * @param info
     */
    private void updateLocation( TYLocationInfo info , boolean b ) {
        if (info == null) {
            return;
        }
        if (map == null) {
            return;
        }
        if( b ){
			TYLocationManager.getShareLocationManager().stop();
		}
//        map.clear();
//        LatLng llA = new LatLng(info.getLatitude(), info.getLongitude());
//        CoordinateConverter converter = new CoordinateConverter();
//        converter.coord(llA);
//        converter.from(CoordinateConverter.CoordType.COMMON);
//        LatLng convertLatLng = converter.convert();
//        OverlayOptions ooA = new MarkerOptions().position(convertLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marka)).zIndex(4).draggable(true);
//        map.addOverlay(ooA);
//        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(convertLatLng, 17.0f);
//        map.animateMapStatus(u);
		map.clear();
		LatLng p = new LatLng(info.getLatitude() , info.getLongitude());
		OverlayOptions option = new MarkerOptions().position(p).icon(bitmap);
		map.addOverlay(option);
		map.setMyLocationEnabled(true);
		MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(p);
		map.animateMapStatus(mapStatusUpdate,15);
    }

	/**
	 * 获取足迹列表
	 */
	private void getTrackList() {
		if (currOrganizationInfo == null) {
			return;
		}
		String org_id = currOrganizationInfo.getOrg_id();
		String start_dt = currDate;
		String end_dt = start_dt;
		CheckInController.getCheckAndUnchexkList( getContext(), start_dt, end_dt, org_id, new Listener< List<UnCheckInInfo> , List<CheckInInfo> >() {
			@Override
			public void onCallBack(List<UnCheckInInfo> nuCheckReply, List<CheckInInfo> checkReply) {
				trackListView.stopRefresh();
				trackListView.stopLoadMore();
				//给适配器重新设置数据，里面有通知适配器更新的方法		
				mYAdapter.setDatas( checkReply );
				nuCheckReply = doFilterList( nuCheckReply ) ;//过滤掉集合
				mNAdapter.setDatas( nuCheckReply );
				yesNnbTv.setText(""+ 0 );
				if( checkReply != null ){
					yesNnbTv.setText(""+ checkReply.size());
				}
				noNubTv.setText(""+ 0 );
				if( nuCheckReply != null ){
					noNubTv.setText(""+ nuCheckReply.size());
				}							
			}
		});
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId() ;		
		switch ( id ) {
			//选择日期
			case R.id.tract_date_tv:
				selectDate();
				break;
			case R.id.textYesReadNub:
			case R.id.textYesRead://已签到
				yesNnbTv.setTextColor(getResources().getColor(R.color.new_y_singnin_text_col)); 
				yesTv.setTextColor(getResources().getColor(R.color.new_y_singnin_text_col)); 
				noNubTv.setTextColor(getResources().getColor(R.color.new_n_singnin_text_col)); 
				noTv.setTextColor(getResources().getColor(R.color.new_n_singnin_text_col));
				gridView_rl.setVisibility( View.GONE);
				listView_rl.setVisibility( View.VISIBLE );
				break ;
			case R.id.textNoReadNub:
			case R.id.textNoRead://未签到
				yesNnbTv.setTextColor(getResources().getColor(R.color.new_n_singnin_text_col));
				yesTv.setTextColor(getResources().getColor(R.color.new_n_singnin_text_col)); 
				noNubTv.setTextColor(getResources().getColor(R.color.new_y_singnin_text_col)); 
				noTv.setTextColor(getResources().getColor(R.color.new_y_singnin_text_col));
				listView_rl.setVisibility( View.GONE );
				gridView_rl.setVisibility( View.VISIBLE);
				break;
			default:
				break;
		}
	}

    @Override
    public void onRefresh() {
        //获取足迹列表
        getTrackList();
    }

    @Override
    public void onLoadMore() {
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

    @Override
    public void onLocationChange(TYLocationInfo info) {
        if (info != null) {
            updateLocation(info ,true);
        }
    }

    @Override
    public void onGPSStatuChange(int event) {
        // TODO Auto-generated method stub
    }

    /**
     * 过滤集合
     *
     * @param nuCheckReply
     * @return
     */
    private List<UnCheckInInfo> doFilterList(List<UnCheckInInfo> nuCheckReply) {
        List<UnCheckInInfo> list = new ArrayList<UnCheckInInfo>();
        if (nuCheckReply == null) {
            return null;
        }
        for (UnCheckInInfo unCheckInInfo : nuCheckReply) {
            if (!TextUtils.isEmpty(unCheckInInfo.getPicture_url())) {
                list.add(unCheckInInfo);
            }
        }
        return list;
    }
}
