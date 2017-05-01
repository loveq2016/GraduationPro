package app.logic.activity.checkin;

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
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.TimePickerView.OnTimeSelectListener;
import com.bigkoo.pickerview.listener.OnDismissListener;

import android.os.Bundle;
import android.os.UserManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager.LayoutParams;
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
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
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
import app.utils.common.Public;
import app.utils.dialog.YYDialogView;
import app.utils.managers.TYLocationManager;
import app.utils.managers.TYLocationManager.TYLocationListener;
import app.view.YYListView;
import app.yy.geju.R;

/**
 * 
 * SiuJiYung create at 2016年8月4日 下午5:14:26
 * 
 */

public class TraceFragment extends Fragment implements IXListViewListener, TYLocationListener, OnClickListener, OnTouchListener {

	private MapView mapViewBg;
	private BaiduMap map;
	private View dateBgView;
	private TextView dateTextView;
	private QLXListView trackListView;

	private String currDate;
	private List<OrganizationInfo> myOrganizationList;
	private OrganizationInfo currOrganizationInfo;

	private TextView tract_org_tv;
	private View tract_org_bg_view;

	private TimePickerView timePickerView;
	private PopupWindow popupWindowOrgList = null;
	private int index = 0;
	private boolean haveOrgListStatus = false;

	private YYBaseListAdapter<CheckInInfo> mAdapter = new YYBaseListAdapter<CheckInInfo>(getContext()) {
		private QLAsyncImage imageLoader;

		@Override
		public View createView(int position, View convertView, ViewGroup parent) {
			if (imageLoader == null) {
				imageLoader = new QLAsyncImage(getActivity());
			}
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.cell_track_today, null);
				saveView("cell_track_user_head_view", R.id.cell_track_user_head_view, convertView);
				saveView("cell_track_name_view", R.id.cell_track_name_view, convertView);
				saveView("cell_track_time_view", R.id.cell_track_time_view, convertView);
				saveView("cell_track_addr_view", R.id.cell_track_addr_view, convertView);
			}
			CheckInInfo info = getItem(position);
			if (info != null) {
				ImageView headView = getViewForName("cell_track_user_head_view", convertView);
				TextView timeView = getViewForName("cell_track_time_view", convertView);
				TextView nameView = getViewForName("cell_track_name_view", convertView);
				TextView addrView = getViewForName("cell_track_addr_view", convertView);

				UserInfo currUsr = UserManagerController.getCurrUserInfo();
				String headUrl = HttpConfig.getUrl(currUsr.getMy_picture_url());
				imageLoader.loadImage(headUrl, headView);
				Date dt_create = QLDateUtils.createDateTimeFromString(info.getCreate_time(), "yyyy-MM-dd HH:mm:ss");
				String dtString = QLDateUtils.getTimeWithFormat(dt_create, "HH:mm");
				timeView.setText(dtString);
				nameView.setText(currUsr.getNickName());
				addrView.setText(info.getChin_addr());
			}

			return convertView;
		}
	};

	private View view;

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// View view =
		// LayoutInflater.from(getContext()).inflate(R.layout.activity_trace,
		// null);
		// setView(view);

		if (view == null) {
			view = inflater.inflate(R.layout.activity_trace, null);
			setView(view);
		}
		ViewGroup parent = (ViewGroup) view.getParent();
		if (parent != null) {
			parent.removeView(view);
		}
		return view;
	}

	private void setView(View view) {
		mapViewBg = (MapView) view.findViewById(R.id.tract_map_bg_view);
		map = mapViewBg.getMap();
		map.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		map.setBaiduHeatMapEnabled(false);
		map.setMyLocationEnabled(false);
		map.getUiSettings().setAllGesturesEnabled(false);
		mapViewBg.showZoomControls(false);
		mapViewBg.showScaleControl(false);

		dateBgView = view.findViewById(R.id.tract_date_bg_view);
		dateBgView.setOnClickListener(this);

		dateTextView = (TextView) view.findViewById(R.id.tract_date_tv);
		tract_org_bg_view = (View) view.findViewById(R.id.tract_org_bg_view);
		tract_org_bg_view.setOnClickListener(this);
		tract_org_tv = (TextView) view.findViewById(R.id.tract_org_tv);
		trackListView = (QLXListView) view.findViewById(R.id.tract_list_view);
		// trackListView.setEmptyView(view.findViewById(R.id.empty_view));
		trackListView.setPullLoadEnable(false, true);
		trackListView.setPullRefreshEnable(true);
		trackListView.setXListViewListener(this);
		trackListView.setAdapter(mAdapter);

		currDate = QLDateUtils.getTimeWithFormat(null, "yyyy-MM-dd");
		dateTextView.setText(currDate);
		TYLocationInfo info = TYLocationManager.getShareLocationManager().getCurrLocationInfo();
		updateLocation(info);
		getOrgList();
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
		getTrackList();
	}

	private void selectOrganization(int index) {
		if (myOrganizationList == null || myOrganizationList.size() < 1) {
			return;
		}
		if (index < 0 || index > myOrganizationList.size() - 1) {
			return;
		}
		currOrganizationInfo = myOrganizationList.get(index);

		tract_org_tv.setText(currOrganizationInfo.getOrg_name());
		getTrackList();
	}

	private void selectDate() {
		if (timePickerView == null) {
			timePickerView = new TimePickerView(getContext(), TimePickerView.Type.YEAR_MONTH_DAY);
		}
		timePickerView.setTime(QLDateUtils.getDateTimeNow());
		timePickerView.setCyclic(true);
		timePickerView.setCancelable(true);
		timePickerView.setOnTimeSelectListener(new OnTimeSelectListener() {
			@Override
			public void onTimeSelect(Date date) {
				String dt_str = QLDateUtils.getTimeWithFormat(date, "yyyy-MM-dd");
				dateTextView.setText(dt_str);
				currDate = dt_str;
				getTrackList();
			}
		});
		if (timePickerView.isShowing()) {
			return;
		}
		timePickerView.show();
	}

	private void getOrgList() {
		OrganizationController.getMyOrganizationList(getContext(), new Listener<Void, List<OrganizationInfo>>() {
			@Override
			public void onCallBack(Void status, List<OrganizationInfo> reply) {
				if (reply == null || reply.size() < 1) {
					return;
				}
				List<OrganizationInfo> tempInfos = ZSZSingleton.getZSZSingleton().getHavePassOrg(reply);
				if (tempInfos == null || tempInfos.size() < 1) {
					haveOrgListStatus = false;
				} else {
					haveOrgListStatus = true;
				}
				myOrganizationList = tempInfos;
				selectOrgAdapter.setDatas(myOrganizationList);
				selectOrganization(index);
			}
		});
	}

	private void getTrackList() {
		if (currOrganizationInfo == null) {

			return;
		}
		String org_id = currOrganizationInfo.getOrg_id();
		String start_dt = currDate;
		String end_dt = start_dt;
		CheckInController.getCheckInList(getContext(), start_dt, end_dt, org_id, new Listener<Void, List<CheckInInfo>>() {
			@Override
			public void onCallBack(Void status, List<CheckInInfo> reply) {
				mAdapter.setDatas(reply);
				trackListView.stopLoadMore();
				trackListView.stopRefresh();
			}
		});
	}

	private void showPopupWindowOrgList(View v) {
		View view = LayoutInflater.from(getContext()).inflate(R.layout.popmenu_checkin_org_select, null);
		if (popupWindowOrgList == null) {
			YYListView listView = (YYListView) view.findViewById(R.id.checkin_org_select_lv);
			listView.setAdapter(selectOrgAdapter);
			listView.setPullRefreshEnable(false);
			listView.setPullLoadEnable(false, true);
			int width = getActivity().getWindowManager().getDefaultDisplay().getWidth();
			popupWindowOrgList = new PopupWindow(view, width / 2, LayoutParams.WRAP_CONTENT);
			popupWindowOrgList.setOutsideTouchable(true);
			view.setOnTouchListener(this);
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					index = position - 1;
					selectOrganization(index);
					popupWindowOrgList.dismiss();
				}
			});
		}
		if (popupWindowOrgList.isShowing()) {
			return;
		}
		popupWindowOrgList.showAsDropDown(v, 0, 0);
		popupWindowOrgList.update();
		// ZSZSingleton.getZSZSingleton().backgroundAlpha(getActivity(), 0.5f);
	}

	private YYBaseListAdapter<OrganizationInfo> selectOrgAdapter = new YYBaseListAdapter<OrganizationInfo>(getContext()) {

		@Override
		public View createView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_dialog_select_dpm, null);
				saveView("dpm_tv", R.id.dpm_tv, convertView);
			}
			OrganizationInfo info = getItem(position);
			if (info != null) {
				setTextToViewText(info.getOrg_name(), "dpm_tv", convertView);
			}
			return convertView;

		}
	};

	// 显示组织选择
	private void showOrgDialog() {
		View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_set_dpm, null);
		YYListView listView = (YYListView) view.findViewById(R.id.dpm_listView);

		// getActivity().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
		alertDialog.setView(view);

		if (myOrganizationList.size() > 5) {
			WindowManager manager = getActivity().getWindowManager();
			Display display = manager.getDefaultDisplay();
			WindowManager.LayoutParams params = alertDialog.getWindow().getAttributes();
			params.width = (int) ((display.getWidth()) * 0.8);
			params.height = (int) ((display.getHeight()) * 0.45);
			alertDialog.getWindow().setAttributes(params);
		}

		listView.setAdapter(selectOrgAdapter);
		listView.setPullRefreshEnable(false);
		listView.setPullLoadEnable(false, false);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				index = position - 1;
				selectOrganization(index);
				alertDialog.dismiss();
			}
		});
		alertDialog.show();

	}

	// 使用自己封装的dialog
	private void showYYDialog() {

		YYDialogView<OrganizationInfo> dialogView = new YYDialogView<OrganizationInfo>(getContext(), R.style.ZSZDialog, myOrganizationList) {

			@Override
			public void OnCreateDialogItemClickListener(AdapterView<?> parent, View view, int position, long id) {
				index = position - 1;
				selectOrganization(index);

			}
		};
		dialogView.show();
	}

	@Override
	public void onRefresh() {
		if (currOrganizationInfo == null) {
			trackListView.stopLoadMore();
			trackListView.stopRefresh();
			return;
		}
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
			if (haveOrgListStatus) {
				// showPopupWindowOrgList(v);
//				showOrgDialog();
				showYYDialog();
			} else {
				QLToastUtils.showToast(getActivity(), "未加入任何的组织");
			}

			break;
		default:
			break;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (popupWindowOrgList != null && popupWindowOrgList.isShowing()) {
			popupWindowOrgList.dismiss();
		}
		return false;

	}

}
