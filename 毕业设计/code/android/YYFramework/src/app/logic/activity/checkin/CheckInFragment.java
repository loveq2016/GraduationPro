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
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.LogoPosition;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
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

//import cn.jpush.android.a.b;

/**
 * 
 * SiuJiYung create at 2016年8月4日 上午11:29:22
 * 
 */

public class CheckInFragment extends Fragment implements TYLocationListener, OnClickListener, OnTouchListener {

	// private CircleImageView userHeadView;
	private ImageView userHeadView;//用户头像
	private TextView userNameView;//用户名字
	private TextView checkInStatusView;//签到的状态
	private TextView orgNameView;//组织的名字

	private MapView mapView;//地图视图
	private TextView addrTvTextView;  //显示位置
	private TextView addrPickerTextView;  //微调地址

	private TextView dateTextView;  //日期
	private TextView timeTextView;  //时间

	private ImageButton checkInBtn;  //签到按钮
	private List<OrganizationInfo> myOrganizationList;   //陈放组织的List集合
	private OrganizationInfo currOrganizationInfo;       //组织对象
	private BaiduMap baiduMap;                           //百度地图对象
	private TYLocationInfo currLocationInfo;

	private int index = 0;
	private PopupWindow popupWindow_Org;
	private boolean haveOrgListStatus = false;

	private View view;

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// view =
		// inflater.from(getActivity()).inflate(R.layout.activity_main_check_in,
		// null);
		//
		//
		// setView(view);

		if (view == null) {
			view = inflater.from(getActivity()).inflate(R.layout.activity_main_check_in, null);
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

		mapView = (MapView) view.findViewById(R.id.cell_check_in_map_view);
		
		baiduMap = mapView.getMap();
		baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		mapView.showZoomControls(false);
		mapView.showScaleControl(false);
		mapView.setEnabled(false);
		baiduMap.setMyLocationEnabled(false);
		baiduMap.getUiSettings().setAllGesturesEnabled(false);// 拖动
		
		addrTvTextView = (TextView) view.findViewById(R.id.cell_check_in_addr_tv);
		addrPickerTextView = (TextView) view.findViewById(R.id.cell_check_in_pick_addr_tv);

		//给  微调地址   设置监听器
		addrPickerTextView.setOnClickListener(this);

		dateTextView = (TextView) view.findViewById(R.id.checkin_date_tv);  //日期
		timeTextView = (TextView) view.findViewById(R.id.checkin_time_tv);  //时间
		checkInBtn = (ImageButton) view.findViewById(R.id.check_in_btn);    //签到按钮

		userHeadView = (ImageView) view.findViewById(R.id.check_in_user_head_view);
		userNameView = (TextView) view.findViewById(R.id.check_in_user_name_tv);
		checkInStatusView = (TextView) view.findViewById(R.id.check_in_status_tv);
		orgNameView = (TextView) view.findViewById(R.id.check_in_org_name_tv);  //选择组织签到

		orgNameView.setOnClickListener(this);

		//获取用户信息
		UserInfo currUserInfo = UserManagerController.getCurrUserInfo();
		String userHeadUrl = HttpConfig.getUrl(currUserInfo.getMy_picture_url());

		// imageLoader.loadImage(userHeadUrl, userHeadView);  （ 加载头像 ）
		Picasso.with(getContext()).load(userHeadUrl).placeholder(R.drawable.default_user_icon).fit().centerCrop().into(userHeadView);

		//设置名字
		userNameView.setText(currUserInfo.getNickName());
		
		//获取日期 和 时间
		updateHeadView();
		//签到按钮设置监听
		checkInBtn.setEnabled(true);
		checkInBtn.setOnClickListener(this);

		getOrgList();
	}

	/**
	 * 更新位置
	 * @param info
	 */
	private void updateLocationView(final TYLocationInfo info) {

		baiduMap.clear();
		LatLng llA = new LatLng(info.getLatitude(), info.getLongitude());
		CoordinateConverter converter = new CoordinateConverter();
		converter.coord(llA);
		converter.from(CoordinateConverter.CoordType.COMMON);
		LatLng convertLatLng = converter.convert();
		OverlayOptions ooA = new MarkerOptions().position(convertLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_map3x)).zIndex(4).draggable(true);
		baiduMap.addOverlay(ooA);
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(convertLatLng, 17.0f);
		baiduMap.animateMapStatus(u);

		TYLocationManager.getShareLocationManager().stop();
		//获取到新位置，并设置
		addrTvTextView.post(new Runnable() {
			@Override
			public void run() {
				addrTvTextView.setText(info.getLocationAddr());
			}
		});
	}

	/**
	 * 获取到焦点
	 */
	@Override
	public void onResume() {
		super.onResume();
		//添加监听器
		TYLocationManager.getShareLocationManager().setLocationListener(this);
		//开始加载位置
		TYLocationManager.getShareLocationManager().start();
		//获取签到列表
		getCheckInList();
	}

	/**
	 * 失去焦点
	 */
	@Override
	public void onPause() {
		super.onPause();
		//取消监听
		TYLocationManager.getShareLocationManager().setLocationListener(null);
		//停止加载位置
		TYLocationManager.getShareLocationManager().stop();
	}

	/**
	 *获取时间和日期 
	 */
	private void updateHeadView() {
		String date_week_str = QLDateUtils.getWeekDateString(null);
		String date_str = QLDateUtils.getTimeWithFormat(null, "yyyy.MM.dd");
		String time_str = QLDateUtils.getTimeWithFormat(null, "HH:mm");
		StringBuilder dt_builder = new StringBuilder();
		dt_builder.append("<font color=#ff888888>");
		dt_builder.append(date_week_str);
		dt_builder.append("</font>");
		dt_builder.append("<font color=#ff33a6ff>");
		dt_builder.append(date_str);
		dt_builder.append("</font>");
		dateTextView.setText(Html.fromHtml(dt_builder.toString()));
		StringBuilder time_builder = new StringBuilder();
		time_builder.append("<font color=#ff888888>");
		time_builder.append("当前时间:");
		time_builder.append("</font>");
		time_builder.append("<font color=#ff33a6ff>");
		time_builder.append(time_str);
		time_builder.append("</font>");
		timeTextView.setText(Html.fromHtml(time_builder.toString()));
	}

	/**
	 * 选择组织
	 * @param index
	 */
	private void selectOrganization(int index) {
		if (myOrganizationList == null || myOrganizationList.size() < 1) {
			return;
		}
		if (index < 0 || index > myOrganizationList.size() - 1) {
			return;
		}
		currOrganizationInfo = myOrganizationList.get(index);
		orgNameView.setText(currOrganizationInfo.getOrg_name()); //获取组织的名字
		//获取签到列表
		getCheckInList();
	}

	/**
	 * 获取组织列表
	 */
	private void getOrgList() {
		OrganizationController.getMyOrganizationList(getContext(), new Listener<Void, List<OrganizationInfo>>() {
			@Override
			public void onCallBack(Void status, List<OrganizationInfo> reply) {

				List<OrganizationInfo> tempInfos = ZSZSingleton.getZSZSingleton().getHavePassOrg(reply);
				if (tempInfos == null || tempInfos.size() < 1) {
					haveOrgListStatus = false;
				} else {
					haveOrgListStatus = true;
				}
				myOrganizationList = tempInfos;
				selectOrgAdapter.setDatas(myOrganizationList);
				//选择组织
				selectOrganization(index);
			}
		});
	}

	@Override
	public void onRequestLocationStart() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRequestLocationFinish() {
		// TODO Auto-generated method stub

	}

	/**
	 * 位置发生改变
	 */
	@Override
	public void onLocationChange(TYLocationInfo info) {
		if (info != null && currLocationInfo == null) {
			currLocationInfo = info;
		} else if (currLocationInfo == null) {
			currLocationInfo = TYLocationManager.getShareLocationManager().getCurrLocationInfo();
		}
		//更新位置
		updateLocationView(currLocationInfo);
	}

	@Override
	public void onGPSStatuChange(int event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		int vid = v.getId();
		switch (vid) {
		case R.id.cell_check_in_pick_addr_tv:
			// 微调地址
			TYLocationInfo info = currLocationInfo;//
			Intent intent = new Intent();
			Gson gson = new Gson();
			String location_json = gson.toJson(info);
			intent.setClass(getContext(), LocationPicker.class);
			intent.putExtra(LocationPicker.kInitLocationInfoKey, location_json);
			//有返回值的跳转
			startActivityForResult(intent, 11);
			break;
		case R.id.check_in_btn:
			// 签到
			checkIn();
			break;
		case R.id.check_in_org_name_tv:
			if (haveOrgListStatus) {
				// showPopupWindow_Org(v);
				// showOrgDialog();
				showYYDialog();
			} else {
				QLToastUtils.showToast(getActivity(), "未加入任何组织");
			}

			break;
		}
	}

	private void getCheckInList() {
		if (currOrganizationInfo == null) {
			return;
		}
		String org_id = currOrganizationInfo.getOrg_id();
		String start_dt = QLDateUtils.getTimeWithFormat(null, "yyyy-MM-dd");
		String end_dt = start_dt;
		CheckInController.getCheckInList(getContext(), start_dt, end_dt, org_id, new Listener<Void, List<CheckInInfo>>() {
			@Override
			public void onCallBack(Void status, List<CheckInInfo> reply) {
				if (reply != null && reply.size() > 0) {
					checkInBtn.setEnabled(false);
				} else {
					checkInBtn.setEnabled(true);
				}
			}
		});
	}

	
	/**
	 * 签到函数
	 */
	private void checkIn() {
		TYLocationInfo info = currLocationInfo;
		if (info == null) {
			return;
		}
		if (currOrganizationInfo == null) {
			return;
		}
		String org_id = currOrganizationInfo.getOrg_id();
		//发起签到网络请求
		CheckInController.checkIn(getContext(), info, org_id, new Listener<Boolean, String>() {
			@Override
			public void onCallBack(Boolean status, String reply) {
				if (status) {
					// 签到成功
					checkInBtn.setEnabled(false);
					return;
				}
				// TODO show error message here.
				QLToastUtils.showToast(getContext(), "签到失败");
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 11 && resultCode == Activity.RESULT_OK && data != null) {
			String location_json = data.getStringExtra(LocationPicker.kPickLocationResultKey);
			Gson gson = new Gson();
			currLocationInfo = gson.fromJson(location_json, TYLocationInfo.class);
			updateLocationView(currLocationInfo);
		}
	}

	
	private void showPopupWindow_Org(View v) {

		View view = LayoutInflater.from(getContext()).inflate(R.layout.popmenu_checkin_org_select, null);
		if (popupWindow_Org == null) {
			YYListView listView = (YYListView) view.findViewById(R.id.checkin_org_select_lv);
			listView.setAdapter(selectOrgAdapter);
			listView.setPullRefreshEnable(false);
			listView.setPullLoadEnable(false, true);
			int width = getActivity().getWindowManager().getDefaultDisplay().getWidth();
			popupWindow_Org = new PopupWindow(view, width / 2, LayoutParams.WRAP_CONTENT);
			popupWindow_Org.setOutsideTouchable(true);
			view.setOnTouchListener(this);

			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					index = position - 1;
					// OrganizationInfo itemInfo =
					// selectOrgAdapter.getItem(index);
					// orgNameView.setText(itemInfo.getOrg_name());

					// getCheckInList();
					selectOrganization(index);
					ZSZSingleton.getZSZSingleton().backgroundAlpha(getActivity(), 1f);
					popupWindow_Org.dismiss();

				}
			});

		}
		if (popupWindow_Org.isShowing()) {
			return;
		}
		popupWindow_Org.showAsDropDown(v, 0, 0);
		popupWindow_Org.update();
		// ZSZSingleton.getZSZSingleton().backgroundAlpha(getActivity(), f);
		ZSZSingleton.getZSZSingleton().backgroundAlpha(getActivity(), 0.5f);
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
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if (popupWindow_Org != null && popupWindow_Org.isShowing()) {
			popupWindow_Org.dismiss();
			ZSZSingleton.getZSZSingleton().backgroundAlpha(getActivity(), 1f);
		}
		return false;

	}

}
