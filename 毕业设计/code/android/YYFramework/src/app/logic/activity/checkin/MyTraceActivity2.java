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
import org.ql.utils.image.QLAsyncImage;
import org.ql.views.ImageView.AsyncImageView;
import org.ql.views.ImageView.CircleImageView;
import org.ql.views.listview.QLXListView;
import org.ql.views.listview.QLXListView.IXListViewListener;

import com.baidu.mapapi.map.BaiduMap;
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

public class MyTraceActivity2 extends ActActivity implements OnClickListener, IXListViewListener, TYLocationListener, OnTouchListener {

	private MapView mapViewBg;
	private BaiduMap map;
	private View dateBgView;
//	private View orgBgView;
	private TextView dateTextView;
//	private TextView tract_org_tv;
	private QLXListView trackListView;

	private Date selectedDate;
	private List<OrganizationInfo> myOrganizationList;
	private OrganizationInfo currOrganizationInfo;

	private TimePickerView timePickerView;
	private ImageView userHeadView;
	private TextView userNameView;
	private TextView checkCountView;
	private QLAsyncImage imageLoader;
	private int checkInCount;

	private int index = 0;

	private ActTitleHandler titleHandler = new ActTitleHandler();

	private YYBaseSectionListAdapter<CheckInInfo> mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbsHandler(titleHandler);
		setContentView(R.layout.activity_my_trace);
		setTitle("");
		titleHandler.replaseLeftLayout(this, true);
		titleHandler.getLeftLayout().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		// titleHandler.getRightDefButton().setOnClickListener(this);

		((TextView) titleHandler.getLeftLayout().findViewById( R.id.left_tv )).setText( "我的签到" );
		
		mAdapter = new YYBaseSectionListAdapter<CheckInInfo>(this) {

			@Override
			public String getSortName(CheckInInfo info) {
				Date dt = QLDateUtils.createDateTimeFromString(info.getCreate_time(), "yyyy-MM-dd HH:mm:ss");
				String sortName = QLDateUtils.getTimeWithFormat(dt, "yyyy,MM,dd");
				return sortName;
			}

			@Override
			public View createTitleView(String title, View convertView, ViewGroup parent) {
				View view = getConvertView(convertView, true);
				if (view == null) {
					view = LayoutInflater.from(mContext).inflate(R.layout.section_title_view_trace, null);
				}
				TextView tv = (TextView) view.findViewById(R.id.track_section_title_tv);
				String[] ymd = title.split(",");
				if (ymd.length == 3) {
					StringBuilder sBuilder = new StringBuilder();
					sBuilder.append("<h5>");
					sBuilder.append(ymd[2]);
					sBuilder.append("</h5>");
					sBuilder.append(ymd[1]);
					sBuilder.append("月");
					if (tv != null) {
						tv.setText(Html.fromHtml(sBuilder.toString()));
					}
				} else {
					if (tv != null) {
						tv.setText("");
					}
				}
				return view;
			}

			@Override
			public View createView(CheckInInfo info, int position, View convertView, ViewGroup parent) {
				View view = getConvertView(convertView, false);
				if (view == null) {
					view = LayoutInflater.from(mContext).inflate(R.layout.cell_track_my, null);
					saveView("cell_track_my_time_view", R.id.cell_track_my_time_view, view);
					saveView("cell_track_my_addr_view", R.id.cell_track_my_addr_view, view);
				}

				if (info != null) {
					TextView timeView = getViewForName("cell_track_my_time_view", view);
					TextView addrView = getViewForName("cell_track_my_addr_view", view);

					Date dt_create = QLDateUtils.createDateTimeFromString(info.getCreate_time(), "yyyy-MM-dd HH:mm:ss");
					String dtString = QLDateUtils.getTimeWithFormat(dt_create, "HH:mm");
					if (dtString != null && timeView != null)
						timeView.setText(dtString);
					if (addrView != null) {
						addrView.setText(info.getChin_addr());
					}
				}
				return view;
			}
		};
		mAdapter.setSortComparator(new Comparator<String>() {
			@Override
			public int compare(String lhs, String rhs) {
				String[] left_dt = lhs.split(",");
				String[] right_dt = rhs.split(",");
				int left_power = 0, right_power = 0;
				for (String str : left_dt) {
					left_power += Integer.valueOf(str);
				}
				for (String str : right_dt) {
					right_power += Integer.valueOf(str);
				}
				return right_power - left_power;
			}
		});

		setView();
	}

	private void setView() {
		mapViewBg = (MapView) findViewById(R.id.tract_map_bg_view);
		map = mapViewBg.getMap();
		map.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		map.setBaiduHeatMapEnabled(false);
		map.setMyLocationEnabled(false);
		map.getUiSettings().setAllGesturesEnabled(false);
		mapViewBg.showZoomControls(false);
		mapViewBg.showScaleControl(false);

		dateBgView = findViewById(R.id.tract_date_bg_view);
//		orgBgView = findViewById(R.id.tract_org_bg_view);
//		orgBgView.setOnClickListener(this);
		dateBgView.setOnClickListener(this);

		dateTextView = (TextView) findViewById(R.id.tract_date_tv);
//		tract_org_tv = (TextView) findViewById(R.id.tract_org_tv);

		View headView = LayoutInflater.from(this).inflate(R.layout.cell_track_my_info, null);
		userHeadView = (ImageView) headView.findViewById(R.id.my_track_info_head_view);
		userNameView = (TextView) headView.findViewById(R.id.my_track_info_name_view);
		checkCountView = (TextView) headView.findViewById(R.id.my_track_info_checkin_times_view);

		trackListView = (QLXListView) findViewById(R.id.tract_list_view);
		trackListView.setAdapter(mAdapter);
		trackListView.setPullLoadEnable(false, true);
		trackListView.setPullRefreshEnable(true);
		trackListView.setXListViewListener(this);
		trackListView.addHeaderView(headView);

		imageLoader = new QLAsyncImage(this);

		selectedDate = new Date();
		String endDate_month = QLDateUtils.getTimeWithFormat(selectedDate, "yyyy-MM");
		dateTextView.setText(endDate_month);

		updateUserInfo();
		TYLocationInfo info = TYLocationManager.getShareLocationManager().getCurrLocationInfo();
		updateLocation(info);
		getOrgList();

	}

	private void updateUserInfo() {
		UserInfo userInfo = UserManagerController.getCurrUserInfo();
		String headUrl = HttpConfig.getUrl(userInfo.getMy_picture_url());
		// imageLoader.loadImage(headUrl, userHeadView);
		Picasso.with(this).load(headUrl).placeholder(R.drawable.default_user_icon).fit().centerCrop().into(userHeadView);
		userNameView.setText(userInfo.getNickName());
		checkCountView.setText(Html.fromHtml("本月签到<font color=##ff3cacfd>" + checkInCount + "</font>次"));
	}

	private void updateLocation(TYLocationInfo info) {
		if (info == null) {
			return;
		}
		TYLocationManager.getShareLocationManager().stop();
		map.clear();
		LatLng llA = new LatLng(info.getLatitude(), info.getLongitude());
		CoordinateConverter converter = new CoordinateConverter();
		converter.coord(llA);
		converter.from(CoordinateConverter.CoordType.COMMON);
		LatLng convertLatLng = converter.convert();
		OverlayOptions ooA = new MarkerOptions().position(convertLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_map3x)).zIndex(4).draggable(true);
		map.addOverlay(ooA);
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(convertLatLng, 17.0f);
		map.animateMapStatus(u);
	}

	@Override
	public void onPause() {
		super.onPause();
		TYLocationManager.getShareLocationManager().setLocationListener(null);
	}

	@Override
	public void onResume() {
		super.onResume();
		TYLocationManager.getShareLocationManager().setLocationListener(this);
		TYLocationManager.getShareLocationManager().start();
		getOrgList();

	}

	private void selectOrganization(int index) {
		if (myOrganizationList == null || myOrganizationList.size() < 1) {
			return;
		}
		if (index < 0 || index > myOrganizationList.size() - 1) {
			return;
		}
		currOrganizationInfo = myOrganizationList.get(index);
//		tract_org_tv.setText(currOrganizationInfo.getOrg_name());
		getTrackList();
	}

	private void selectDate() {
		if (timePickerView == null) {
			timePickerView = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY);
		}
		//设置间距
		timePickerView.getWheelTime().getView().setPadding( 10 , 0 , 10 , 0 );
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

	private void getOrgList() {
		OrganizationController.getMyOrganizationList(this, new Listener<Void, List<OrganizationInfo>>() {
			@Override
			public void onCallBack(Void status, List<OrganizationInfo> reply) {

				if (reply == null || reply.size() < 1) {
					return;
				}
				List<OrganizationInfo> tempInfos = ZSZSingleton.getZSZSingleton().getHavePassOrg(reply);
				// myOrganizationList = reply;
				myOrganizationList = tempInfos;
				mAdapteOrg.setDatas(myOrganizationList);
				selectOrganization(index);
			}
		});
	}

	private void getTrackList() {
		if (currOrganizationInfo == null) {
			return;
		}

		String org_id = currOrganizationInfo.getOrg_id();
		String start_dt = QLDateUtils.getMonthStartDay(selectedDate, "yyyy-MM-dd");
		String end_dt = QLDateUtils.getMonthEndDay(selectedDate, "yyyy-MM-dd");
		CheckInController.getCheckInList(this, start_dt, end_dt, org_id, new Listener<Void, List<CheckInInfo>>() {
			@Override
			public void onCallBack(Void status, List<CheckInInfo> reply) {

				mAdapter.setDatas(reply);
				// trackListView.setAdapter(mAdapter);
				trackListView.stopLoadMore();
				trackListView.stopRefresh();
				checkInCount = 0;
				if (reply != null && reply.size() > 0) {
					checkInCount = reply.size();
				}
				updateUserInfo();
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
		case R.id.tract_date_bg_view:
			selectDate();
			break;

		case R.id.tract_org_bg_view:
			// showPopupWindow(v);
//			showOrgDialog();
			showYYDialog();
		default:
			break;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (popupWindow_Org != null && popupWindow_Org.isShowing()) {
			popupWindow_Org.dismiss();
		}
		return false;

	}

	private PopupWindow popupWindow_Org;

	private void showPopupWindow(View v) {
		View view = LayoutInflater.from(this).inflate(R.layout.trace_org_list, null);
		if (popupWindow_Org == null) {
			YYListView listView = (YYListView) view.findViewById(R.id.tract_org_lv);
			listView.setAdapter(mAdapteOrg);
			listView.setPullRefreshEnable(false);
			listView.setPullLoadEnable(false, true);
			popupWindow_Org = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			popupWindow_Org.setOutsideTouchable(true);
			view.setOnTouchListener(this);

			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					index = position - 1;
					OrganizationInfo info = mAdapteOrg.getItem(index);
//					tract_org_tv.setText(info.getOrg_name());
					selectOrganization(index);
					popupWindow_Org.dismiss();
				}
			});

		}
		if (popupWindow_Org.isShowing()) {
			return;
		}
		popupWindow_Org.showAsDropDown(v, 0, 0);
		popupWindow_Org.update();

	}

	// 显示组织选择
	private void showOrgDialog() {
		View view = LayoutInflater.from(this).inflate(R.layout.dialog_set_dpm, null);
		YYListView listView = (YYListView) view.findViewById(R.id.dpm_listView);

		// getActivity().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setView(view);

		if (myOrganizationList.size() > 5) {
			WindowManager manager = this.getWindowManager();
			Display display = manager.getDefaultDisplay();
			WindowManager.LayoutParams params = alertDialog.getWindow().getAttributes();
			params.width = (int) ((display.getWidth()) * 0.8);
			params.height = (int) ((display.getHeight()) * 0.45);
			alertDialog.getWindow().setAttributes(params);
		}

		listView.setAdapter(mAdapteOrg);
		listView.setPullRefreshEnable(false);
		listView.setPullLoadEnable(false, false);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				index = position - 1;
				OrganizationInfo info = mAdapteOrg.getItem(index);
//				tract_org_tv.setText(info.getOrg_name());
				selectOrganization(index);
				alertDialog.dismiss();
			}
		});
		alertDialog.show();
	}

	//
	// 使用自己封装的dialog
	private void showYYDialog() {
		YYDialogView<OrganizationInfo> dialogView = new YYDialogView<OrganizationInfo>(this, R.style.ZSZDialog, myOrganizationList) {
			@Override
			public void OnCreateDialogItemClickListener(AdapterView<?> parent, View view, int position, long id) {
				index = position - 1;
				OrganizationInfo info = mAdapteOrg.getItem(index);
//				tract_org_tv.setText(info.getOrg_name());
				selectOrganization(index);
			}
		};
		dialogView.show();
	}

	private YYBaseListAdapter<OrganizationInfo> mAdapteOrg = new YYBaseListAdapter<OrganizationInfo>(this) {
		@Override
		public View createView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(MyTraceActivity2.this).inflate(R.layout.item_checkin_selectedorg, null);
				saveView("item_tv", R.id.item_tv, convertView);
			}
			OrganizationInfo info = getItem(position);
			if (info != null) {
				setTextToViewText(info.getOrg_name(), "item_tv", convertView);
			}
			return convertView;
		}
	};
}
