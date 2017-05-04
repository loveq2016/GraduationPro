package app.logic.activity.announce;

import java.util.ArrayList;
import java.util.List;

import org.ql.activity.customtitle.ActActivity;
import org.ql.utils.QLToastUtils;
/*
 * GZYY    2016-8-3  上午10:14:03
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager.LayoutParams;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import app.logic.activity.ActTitleHandler;
import app.logic.activity.main.HomeActivity;
import app.logic.adapter.YYBaseListAdapter;
import app.logic.controller.AnnounceController;
import app.logic.controller.OrganizationController;
import app.logic.controller.UserManagerController;
import app.logic.pojo.OrganizationInfo;
import app.logic.singleton.ZSZSingleton;
import app.utils.common.Listener;
import app.view.YYListView;
import app.yy.geju.R;

public class AnnounceActivity2 extends ActActivity implements OnClickListener, OnTouchListener, OnItemClickListener {

	private EditText title_et, text_et;
	private TextView name_et;
	private Button send_btn;
	private boolean haveOrgStatus;
	private PopupWindow popupWindowList;
	private List<OrganizationInfo> orgList = new ArrayList<OrganizationInfo>();
	private String org_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActTitleHandler handler = new ActTitleHandler();
		setAbsHandler(handler);
		setContentView(R.layout.activity_announce);

		handler.addLeftView(LayoutInflater.from(this).inflate(R.layout.title_leftview_layout, null), true);
		handler.getCenterLayout().findViewById(android.R.id.title).setOnClickListener(this);
		handler.getCenterLayout().findViewById(R.id.centerIv).setVisibility(View.VISIBLE);
		TextView tv = (TextView) handler.getLeftLayout().findViewById(R.id.left_tv);
		tv.setText("返回");
		setTitle("组织选择");
		initView();
		send_btn.setOnClickListener(this);

		handler.getLeftLayout().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		getOrgList();

		name_et.setText("作者：" + UserManagerController.getCurrUserInfo().getNickName());

	}

	private void initView() {

		text_et = (EditText) findViewById(R.id.text_et);
		title_et = (EditText) findViewById(R.id.title_et);
		name_et = (TextView) findViewById(R.id.name_et);
		send_btn = (Button) findViewById(R.id.send_btn);

	}

	private void sendAnnounce(String org_id) {
		final String msg_title = title_et.getText().toString();
		// String org_id = name_et.getText().toString();
		final String msg_content = text_et.getText().toString();

		if (org_id == null || TextUtils.isEmpty(org_id)) {
			QLToastUtils.showToast(AnnounceActivity2.this, "你暂时还没有组织可选择！！");
			return;
		}
		if (!msg_title.equals("") && !msg_content.equals("")) {
			showWaitDialog();

			AnnounceController.announceUser(AnnounceActivity2.this, msg_title, org_id, msg_content, new Listener<Boolean, String>() {
				@Override
				public void onCallBack(Boolean status, String reply) {

					dismissWaitDialog();
					if (status) {
						// Toast.makeText(AnnounceActivity.this, "发送成功!!",
						// Toast.LENGTH_LONG).show();
						QLToastUtils.showToast(AnnounceActivity2.this, "发送成功!!");
//						ZSZSingleton.getZSZSingleton().getSendNoticeUpdataUnreadListener().onCallBack();
						Intent intenUN = new Intent(HomeActivity.UPDATANOTICE);
						AnnounceActivity2.this.sendBroadcast(intenUN);
						finish();
					} else {
						QLToastUtils.showToast(AnnounceActivity2.this, "发送失败，没有权限！！");
					}

				}
			});

		} else {
			QLToastUtils.showToast(AnnounceActivity2.this, "请输入完整的信息!!");
		}

	}

	// 设置公告选择组织列表
	private YYBaseListAdapter<OrganizationInfo> titleOrgAdapter = new YYBaseListAdapter<OrganizationInfo>(this) {

		@Override
		public View createView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(AnnounceActivity2.this).inflate(R.layout.item_titleorg_selected, null);

				saveView("titleorg_id", R.id.titleorg_id, convertView);
				saveView("title_org_unread", R.id.title_org_unread, convertView);
			}

			OrganizationInfo info = (OrganizationInfo) getItem(position);
			if (info != null) {
				setTextToViewText(info.getOrg_name(), "titleorg_id", convertView);

				TextView umreand = getViewForName("title_org_unread", convertView);
				umreand.setVisibility(View.GONE);
			}
			return convertView;

		}
	};

	private void showTitleOrgList(View view) {
		View titleView = LayoutInflater.from(this).inflate(R.layout.popmen_titleorg, null);
		if (popupWindowList == null) {

			YYListView listView = (YYListView) titleView.findViewById(R.id.title_org_list);
			listView.setAdapter(titleOrgAdapter);
			listView.setPullRefreshEnable(false);
			listView.setPullLoadEnable(false, true);
			popupWindowList = new PopupWindow(titleView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

			popupWindowList.setOutsideTouchable(true);
			titleView.setOnTouchListener(this);
			listView.setOnItemClickListener(this);
		}
		if (popupWindowList.isShowing()) {
			return;
		}
		popupWindowList.showAsDropDown(view, 0, (int) getResources().getDimension(R.dimen.dp_10));
		popupWindowList.update();

	}

	// 获取组织列表
	private void getOrgList() {
		OrganizationController.getMyOrganizationList(this, new Listener<Void, List<OrganizationInfo>>() {

			@Override
			public void onCallBack(Void status, List<OrganizationInfo> reply) {

				if (reply != null && reply.size() > 0) {

					List<OrganizationInfo> tempInfos = ZSZSingleton.getZSZSingleton().getHavePassOrg(reply);

					orgList.addAll(tempInfos);
					titleOrgAdapter.setDatas(orgList);

					setTitle(orgList.get(0).getOrg_name());
					org_id = tempInfos.get(0).getOrg_id();

				} else {
					QLToastUtils.showToast(AnnounceActivity2.this, "你现在没有加入任何组织！！");
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case android.R.id.title:
			showTitleOrgList(v);
			break;
		case R.id.send_btn:
			sendAnnounce(org_id);
		default:
			break;
		}

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if (popupWindowList != null && popupWindowList.isShowing()) {
			popupWindowList.dismiss();
		}
		return false;

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		OrganizationInfo orgInfo = titleOrgAdapter.getItem(position - 1);
		if (orgInfo != null) {
			org_id = orgInfo.getOrg_id();
			setTitle(orgInfo.getOrg_name());
		}

		popupWindowList.dismiss();

	}

}
