package app.logic.activity.checkin;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ql.activity.customtitle.ActActivity;
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
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.TimePickerView.OnTimeSelectListener;
import com.squareup.picasso.Picasso;

import android.R.integer;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import app.config.http.HttpConfig;
import app.logic.activity.ActTitleHandler;
import app.logic.adapter.YYBaseListAdapter;
import app.logic.adapter.YYBaseSectionListAdapter;
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
import app.utils.managers.TYLocationManager;
import app.utils.managers.TYLocationManager.TYLocationListener;
import app.view.YYListView;
import app.yy.geju.R;

/**
 * 
 * SiuJiYung create at 2016年8月8日 下午4:01:38
 * 
 */

public class MyTraceActivity extends ActActivity implements OnClickListener, IXListViewListener, TYLocationListener {

	public static final String ORGINFO = "ORGINFO";
	private MapView mapViewBg;
	private BaiduMap map;
	private TextView dateTextView;  //日期
	private QLXListView trackListView;  //足迹列表
	private Date selectedDate;   
	private OrganizationInfo currOrganizationInfo;
	private TimePickerView timePickerView;
	private TextView checkCountView;  //签到数TV
	private QLAsyncImage imageLoader;
	private int checkInCount = 0 ;
	private ActTitleHandler titleHandler = new ActTitleHandler();
	private BitmapDescriptor bitmap ;

	private YYBaseListAdapter<CheckInInfo> mAdapter= new YYBaseListAdapter<CheckInInfo>(this) {
		@Override
		public View createView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {				
				convertView = LayoutInflater.from(mContext).inflate(R.layout.my_trace_itme, null);
				saveView("cell_track_my_time_view", R.id.cell_track_my_time_view, convertView);
				saveView("cell_track_my_addr_view", R.id.cell_track_my_addr_view, convertView);
				saveView("track_section_title_tv", R.id.track_section_title_tv, convertView);
			}
			CheckInInfo info = getItem(position);
			if (info != null) {
				TextView timeView = getViewForName("cell_track_my_time_view", convertView);
				TextView addrView = getViewForName("cell_track_my_addr_view", convertView);
				TextView dtdeView = getViewForName("track_section_title_tv", convertView);				
				Date dt_create = QLDateUtils.createDateTimeFromString(info.getCreate_time(), "yyyy-MM-dd HH:mm:ss");
				String dtString = QLDateUtils.getTimeWithFormat(dt_create, "HH:mm");
				String sortName = QLDateUtils.getTimeWithFormat(dt_create, "yyyy,MM,dd");
				String[] ymd = sortName.split(",");
				if (ymd.length == 3) {
					StringBuilder sBuilder = new StringBuilder();
					sBuilder.append(ymd[2]);
					sBuilder.append("日");
					if (dtdeView != null) {
						dtdeView.setText(Html.fromHtml(sBuilder.toString()));
					}
				} else {
					if (dtdeView != null) {
						dtdeView.setText("");
					}
				}
				timeView.setText(dtString);
				addrView.setText(info.getChin_addr());
			}
			return convertView;
		}
	};

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate(savedInstanceState);		
		setAbsHandler(titleHandler);
		setContentView(R.layout.activity_my_trace);
		bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_map3x);
		currOrganizationInfo = (OrganizationInfo) getIntent().getSerializableExtra(ORGINFO);
		initTlter() ;
		setView();
	}
	/**
	 * 初始化头
	 */
	private void initTlter(){
		setTitle("");
		titleHandler.replaseLeftLayout(this, true);
		titleHandler.getLeftLayout().setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		((TextView) titleHandler.getLeftLayout().findViewById( R.id.left_tv )).setText( "我的签到" );
	}

	/**
	 * 初始化View
	 */
	private void setView() {
		mapViewBg = (MapView) findViewById(R.id.tract_map_bg_view);
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
		dateTextView = (TextView) findViewById(R.id.tract_date_tv);
		dateTextView.setOnClickListener(this);
		//签到数量
		checkCountView = (TextView) findViewById(R.id.signin_count_tv);
		trackListView = (QLXListView) findViewById(R.id.tract_list_view);
		trackListView.setAdapter(mAdapter);
		trackListView.setPullLoadEnable(false, true);
		trackListView.setPullRefreshEnable(true);
		trackListView.setXListViewListener(this);
		imageLoader = new QLAsyncImage(this);
		selectedDate = new Date();
		String endDate_month = QLDateUtils.getTimeWithFormat(selectedDate, "yyyy-MM");
		dateTextView.setText(endDate_month);
		//获取当前自己的位置（目的：为了更快加载出地图）
		TYLocationInfo info = TYLocationManager.getShareLocationManager().getCurrLocationInfo();
		updateLocation(info);
	}
	/**
	 * 地点更新
	 * @param info
	 */
	private void updateLocation(TYLocationInfo info) {
		if (info == null) {
			return;
		}
//		TYLocationManager.getShareLocationManager().stop();
//		map.clear();
//		LatLng llA = new LatLng(info.getLatitude(), info.getLongitude());
//		CoordinateConverter converter = new CoordinateConverter();
//		converter.coord(llA);
//		converter.from(CoordinateConverter.CoordType.COMMON);
//		LatLng convertLatLng = converter.convert();
//		OverlayOptions ooA = new MarkerOptions().position(convertLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marka)).zIndex(4).draggable(true);
//		map.addOverlay(ooA);
//		MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(convertLatLng, 17.0f);
//		map.animateMapStatus(u);
		map.clear();
		LatLng p = new LatLng(info.getLatitude() , info.getLongitude());
		OverlayOptions option = new MarkerOptions().position(p).icon(bitmap);
		map.addOverlay(option);
		map.setMyLocationEnabled(true);
		MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(p);
		map.animateMapStatus(mapStatusUpdate,15);
	}

	@Override
	public void onPause() {
		super.onPause();
		mapViewBg.onPause();
		TYLocationManager.getShareLocationManager().setLocationListener(null);
	}

	@Override
	public void onResume() {
		super.onResume();
		mapViewBg.onResume();
		TYLocationManager.getShareLocationManager().setLocationListener(this);
		TYLocationManager.getShareLocationManager().start();
		getTrackList();
	}

	/**
	 * 日期选择
	 */
	private void selectDate() {
		if (timePickerView == null) {
			timePickerView = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH); //设置模式
		}
        //设置内间距
		timePickerView.getWheelTime().getView().setPadding( 80 , 0 , 80 , 0 );
		timePickerView.setTime(QLDateUtils.getDateTimeNow());
		timePickerView.setCyclic(true);
		timePickerView.setCancelable(true);
		timePickerView.setOnTimeSelectListener(new OnTimeSelectListener() {
			@Override
			public void onTimeSelect(Date date) {
				Calendar calendar = Calendar.getInstance(Locale.getDefault());
				calendar.setTime(date);
				selectedDate = date;
				String dt_str = QLDateUtils.getTimeWithFormat(date, "yyyy-MM");
				dateTextView.setText(dt_str);
				getTrackList();
			}
		});
		if (timePickerView.isShowing()) {
			return;
		}
		timePickerView.show();
	}
	/**
	 * 获取签到列表
	 */
	private void getTrackList() {
		if ( currOrganizationInfo == null ) {
			return;
		}
		String org_id = currOrganizationInfo.getOrg_id();
		String start_dt = QLDateUtils.getMonthStartDay(selectedDate, "yyyy-MM-dd");
		String end_dt = QLDateUtils.getMonthEndDay(selectedDate, "yyyy-MM-dd");
		CheckInController.getCheckInList(this, start_dt, end_dt, org_id, new Listener<Void, List<CheckInInfo>>() {
			@Override
			public void onCallBack( Void status, List<CheckInInfo> reply ) {
				mAdapter.setDatas(reply);
				trackListView.stopLoadMore();
				trackListView.stopRefresh();
				checkCountView.setText("" + 0 );
				if ( reply != null && reply.size() > 0 ) {
					checkInCount = reply.size();
					checkCountView.setText("" + checkInCount );
				}
			}
		});
	}

	@Override
	public void onRefresh() {
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
			updateLocation(info);
		}
	}

	@Override
	public void onGPSStatuChange(int event) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tract_date_tv:
			selectDate();
			break;
		default:
			break;
		}
	}
}





