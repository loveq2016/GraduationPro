package app.logic.activity.org;

import java.util.ArrayList;
import java.util.List;

import org.ql.utils.QLToastUtils;
import org.ql.views.listview.QLXListView;

import com.google.gson.Gson;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
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
import app.logic.controller.UserManagerController;
import app.logic.pojo.FriendInfo;
import app.logic.pojo.JoinRequestListInfo;
import app.logic.pojo.OrgRequestMemberInfo;
import app.logic.singleton.YYSingleton;
import app.utils.common.Listener;
import app.yy.geju.R;

/*
 * GZYY    2016-10-10  上午9:21:28
 */

public class AddFriendToOrg extends InitActActivity implements OnItemClickListener {
	public static final String TITLE = "TITLE";
	public static final String SELECTOR_ORG_ID = "SELECTOR_ORG_ID";
	public static final String kSELECTED_ITEMS_JSON_STRING = "kSELECTED_ITEMS_JSON_STRING";
	public static final String kSELECTED_ITEM_MODEL = "kSELECTED_ITEM_MODEL";
	public static final int kSELECT_ITEMS = 24;

	private String org_idString;
	private ActTitleHandler mHandler;
	private EditText search_et;

	private List<FriendInfo> getMyFriendInfos = new ArrayList<FriendInfo>();
	private List<OrgRequestMemberInfo> getMyOrgMemberInfos = new ArrayList<OrgRequestMemberInfo>();
	private List<FriendInfo> adapterFriendInfos = new ArrayList<FriendInfo>();
	private List<FriendInfo> selectionFriendInfos = new ArrayList<FriendInfo>();
	private boolean[] statusBox;
	private QLXListView mListView;
	private int count;

	private YYBaseListAdapter<FriendInfo> mAdapter = new YYBaseListAdapter<FriendInfo>(this) {

		@Override
		public View createView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.view_selectable_item, null);
				saveView("selected_item_cb", R.id.selected_item_cb, convertView);
				saveView("selected_item_imgview", R.id.selected_item_imgview, convertView);
				saveView("selected_item_tv", R.id.selected_item_tv, convertView);
			}
			FriendInfo info = getItem(position);
			if (info != null) {
				CheckBox cBox = getViewForName("selected_item_cb", convertView);
				cBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

						statusBox[position] = isChecked;

					}
				});
				String url = HttpConfig.getUrl(info.getPicture_url());
				setImageToImageViewCenterCrop(url, "selected_item_imgview", -1, convertView);
				String _name = info.getFriend_name() == null || TextUtils.isEmpty(info.getFriend_name()) ? info.getNickName() : info.getFriend_name();
				setTextToViewText(_name, "selected_item_tv", convertView);

				if (cBox.isChecked() != statusBox[position]) {
					cBox.setChecked(statusBox[position]);
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

		mListView = (QLXListView) findViewById(R.id.friends_list_view);
		search_et = (EditText) findViewById(R.id.search_et);
	}

	@Override
	protected void initData() {
		org_idString = getIntent().getStringExtra(SELECTOR_ORG_ID);
		String titleString = getIntent().getStringExtra(TITLE);
		if (titleString != null) {
			setTitle(titleString);
		}

		mListView.setPullLoadEnable(false, true);
		mListView.setPullRefreshEnable(true);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);

		search_et.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				searchItem(s.toString());
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

		getMyFriendList();

	}

	private void initRightTitleView() {
		mHandler.getRightDefButton().setVisibility(View.VISIBLE);
		mHandler.getRightDefButton().setText("确定");
		mHandler.getRightDefButton().setTextColor(0xfffcfcfc);
		mHandler.getRightDefButton().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showWaitDialog();
				getSelectionList();
				// exitActivity();
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

	// 查找
	private void searchItem(String s) {
		if (s == null || TextUtils.isEmpty(s)) {
			statusBox = new boolean[adapterFriendInfos.size()];
			mAdapter.setDatas(adapterFriendInfos);
		}
		List<FriendInfo> selectInfos = new ArrayList<FriendInfo>();
		for (FriendInfo info : adapterFriendInfos) {
			String friendString = info.getFriend_name() == null || TextUtils.isEmpty(info.getFriend_name()) ? info.getNickName() : info.getFriend_name();
			if (friendString.contains(s)) {
				selectInfos.add(info);
			}
		}
		statusBox = new boolean[selectInfos.size()];
		mAdapter.setDatas(selectInfos);

	}

	// 获取好友列表,获取本组织成员列表,筛选出未有加入本组织的成员
	private void getMyFriendList() {
		UserManagerController.getFriendsList(this, new Listener<List<FriendInfo>, List<FriendInfo>>() {

			@Override
			public void onCallBack(List<FriendInfo> status, List<FriendInfo> reply) {
				if (reply == null || reply.size() < 1) {
					return;
				}
				getMyFriendInfos.clear();
				getMyFriendInfos.addAll(reply);

				OrganizationController.getOrgMemberList(AddFriendToOrg.this, org_idString, new Listener<Void, List<OrgRequestMemberInfo>>() {

					@Override
					public void onCallBack(Void status, List<OrgRequestMemberInfo> reply) {
						if (reply != null && reply.size() >= 1) {
							// 过滤已经加入本组织的人
							for (int i = 0; i < getMyFriendInfos.size(); i++) {
								boolean temp = true;
								for (OrgRequestMemberInfo info : reply) {
									if (getMyFriendInfos.get(i).getWp_friends_info_id().equals(info.getWp_member_info_id())) {
										temp = false;
										break;
									}
								}
								if (temp) {
									adapterFriendInfos.add(getMyFriendInfos.get(i));
								}
							}
							mAdapter.setDatas(adapterFriendInfos);
							statusBox = new boolean[adapterFriendInfos.size()];
						}

					}
				});

			}
		});

	}

	// 获取checkbox为true的人,
	private void getSelectionList() {
		selectionFriendInfos.clear();
		for (int i = 0; i < statusBox.length; i++) {
			if (statusBox[i]) {
				selectionFriendInfos.add(adapterFriendInfos.get(i));
			}
		}
		// Gson gson = new Gson();
		// String string = gson.toJson(selectionFriendInfos);
		//
		// QLToastUtils.showToast(AddFriendToOrg.this, string);

		if (selectionFriendInfos.size() < 1) {
			dismissWaitDialog();
			return;
		}
		StringBuilder sBuilder = new StringBuilder();
		for (FriendInfo info : selectionFriendInfos) {
			sBuilder.append(info.getWp_friends_info_id() + ",");
		}
		sBuilder.deleteCharAt(sBuilder.length() - 1);

		// QLToastUtils.showToast(AddFriendToOrg.this, sBuilder.toString());
		OrganizationController.addPersonToOrganization(this, org_idString, sBuilder.toString(), new Listener<Boolean, String>() {

			@Override
			public void onCallBack(Boolean status, String reply) {
				dismissWaitDialog();
				if (status) {
					QLToastUtils.showToast(AddFriendToOrg.this, "加入成功");

					YYSingleton.getInstance().getUpdataOrgMemNumListener().onCallBack(selectionFriendInfos.size());
					finish();
				}

			}
		});

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub

	}

}
