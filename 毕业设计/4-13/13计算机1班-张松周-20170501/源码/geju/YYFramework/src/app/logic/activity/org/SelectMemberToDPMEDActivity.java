package app.logic.activity.org;

import java.util.ArrayList;
import java.util.List;

import org.ql.views.listview.QLXListView;

import com.google.gson.Gson;

import u.aly.co;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import app.config.http.HttpConfig;
import app.logic.activity.ActTitleHandler;
import app.logic.activity.InitActActivity;
import app.logic.adapter.YYBaseListAdapter;
import app.logic.controller.OrganizationController;
import app.logic.pojo.FriendInfo;
import app.logic.pojo.OrgRequestMemberInfo;
import app.logic.pojo.UserInfo;
import app.utils.common.Listener;
import app.yy.geju.R;

/*
 * GZYY    2016-9-29  下午5:17:41
 */

public class SelectMemberToDPMEDActivity extends InitActActivity implements OnItemClickListener {

	public static final String SELECTOR_ORG_ID = "SELECTOR_ORG_ID";
	public static final String kSELECTED_ITEMS_JSON_STRING = "kSELECTED_ITEMS_JSON_STRING";
	public static final String kSELECTED_ITEM_MODEL = "kSELECTED_ITEM_MODEL";
	public static final int kSELECT_ITEMS = 24;

	private ActTitleHandler mHandler;
	private QLXListView mListView;
	private EditText searchEditText;

	private String org_id;
	private List<OrgRequestMemberInfo> allItemList = new ArrayList<OrgRequestMemberInfo>();
	private boolean[] statusSelect;

	private YYBaseListAdapter<OrgRequestMemberInfo> mAdapter = new YYBaseListAdapter<OrgRequestMemberInfo>(this) {

		@Override
		public View createView(final int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.view_selectable_item, null);
				saveView("selected_item_cb", R.id.selected_item_cb, convertView);
				saveView("selected_item_imgview", R.id.selected_item_imgview, convertView);
				saveView("selected_item_tv", R.id.selected_item_tv, convertView);
			}

			OrgRequestMemberInfo info = getItem(position);
			if (info != null) {
				CheckBox cBox = getViewForName("selected_item_cb", convertView);
				cBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

						statusSelect[position] = isChecked;

					}
				});
				String url = HttpConfig.getUrl(info.getPicture_url());
				setImageToImageViewCenterCrop(url, "selected_item_imgview", -1, convertView);
				String _name = info.getName() == null || TextUtils.isEmpty(info.getName()) ? info.getNickName() : info.getName();
				setTextToViewText(_name, "selected_item_tv", convertView);

				if (cBox.isChecked() != statusSelect[position]) {
					cBox.setChecked(statusSelect[position]);
				}
			}

			return convertView;

		}
	};

	@Override
	protected void initActTitleView() {
		mHandler = new ActTitleHandler();
		setAbsHandler(mHandler);

	}

	@Override
	protected void initView(Bundle savedInstanceState) {
		setContentView(R.layout.activity_friends_list);
		initRightTitleView();

		setTitle("成员选择");
		mListView = (QLXListView) findViewById(R.id.friends_list_view);
		searchEditText = (EditText) findViewById(R.id.search_et);
	}

	@Override
	protected void initData() {
		org_id = getIntent().getStringExtra(SELECTOR_ORG_ID);

		searchEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				search(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		mListView.setPullLoadEnable(false, true);
		mListView.setPullRefreshEnable(false);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);

		getMyMemberList();

	}

	private void initRightTitleView() {
		mHandler.getRightDefButton().setVisibility(View.VISIBLE);
		mHandler.getRightDefButton().setText("确定");
		mHandler.getRightDefButton().setTextColor(0xfffcfcfc);
		mHandler.getRightDefButton().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				exitActivity();
			}
		});
		mHandler.replaseLeftLayout(this, true);
		mHandler.getLeftLayout().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	// 获取所有组织成员
	private void getMyMemberList() {
		OrganizationController.getOrgMemberList(this, org_id, new Listener<Void, List<OrgRequestMemberInfo>>() {

			@Override
			public void onCallBack(Void status, List<OrgRequestMemberInfo> reply) {

				if (reply == null || reply.size() < 1) {
					return;
				}
				// 筛选剩下未有加入的部门成员
				for (OrgRequestMemberInfo info : reply) {
					if (info.getDepartmentId() == null || TextUtils.isEmpty(info.getDepartmentId())) {
						if (info.getPhone() == null || TextUtils.isEmpty(info.getPhone())) {
							continue;
						}
						allItemList.add(info);
					}
				}
				mAdapter.setDatas(allItemList);
				statusSelect = new boolean[allItemList.size()];

			}
		});
	}

	// 筛选出选择的item,转成FriendInfo
	private List<OrgRequestMemberInfo> getHaveTrueMember() {
		List<OrgRequestMemberInfo> haveList = new ArrayList<OrgRequestMemberInfo>();
		if (allItemList.size() != statusSelect.length) {
			return haveList;
		}
		for (int i = 0; i < statusSelect.length; i++) {
			if (statusSelect[i]) {
				haveList.add(allItemList.get(i));
			}
		}
		return haveList;
	}

	private void exitActivity() {
		if (getHaveTrueMember().size() < 1) {
			finish();
		}
		List<FriendInfo> _selectedItems = new ArrayList<FriendInfo>();
		for (OrgRequestMemberInfo info : getHaveTrueMember()) {
			FriendInfo friendInfo = new FriendInfo();
			friendInfo.setPicture_url(info.getPicture_url());
			friendInfo.setWp_friends_info_id(info.getWp_member_info_id());
			friendInfo.setFriend_name(info.getNickName());
			friendInfo.setSex(info.getSex());
			friendInfo.setPhone(info.getPhone());
			_selectedItems.add(friendInfo);
		}

		Gson gson = new Gson();
		String _sel_gson = gson.toJson(_selectedItems);
		Intent intent = new Intent();
		intent.putExtra(kSELECTED_ITEMS_JSON_STRING, _sel_gson);
		setResult(Activity.RESULT_OK, intent);

		finish();
	}

	private void search(String text) {
		if (TextUtils.isEmpty(text)) {
			mListView.stopRefresh();
			statusSelect = new boolean[allItemList.size()];
			mAdapter.setDatas(allItemList);
			return;
		}
		if (allItemList == null || allItemList.size() < 1) {
			return;
		}
		ArrayList<OrgRequestMemberInfo> tmpInfos = new ArrayList<OrgRequestMemberInfo>();
		for (OrgRequestMemberInfo info : allItemList) {
			if (info.getNickName() != null && info.getNickName().contains(text)) {
				tmpInfos.add(info);
			}
		}
		statusSelect = new boolean[tmpInfos.size()];
		mAdapter.setDatas(tmpInfos);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

	}
}
