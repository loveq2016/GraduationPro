package app.logic.activity.org;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.ql.views.listview.QLXListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
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
import app.logic.pojo.FriendInfo;
import app.logic.pojo.OrgRequestMemberInfo;
import app.utils.common.Listener;
import app.yy.geju.R;

/*
 * GZYY    2016-9-23  上午10:20:34
 */

public class SelectMemberToDPMActivity extends InitActActivity implements OnItemClickListener {

	public static final String kSELECTED_ITEM_MODEL = "kSELECTED_ITEM_MODEL";
	public static final String kTITLE = "KTITLE";
	public static final String kSELECTED_ITEMS_JSON_STRING = "kSELECTED_ITEMS_JSON_STRING";
	public static final String KORG_ID = "KORG_ID";
	public static final int kSELECT_ITEMS = 23;

	private ActTitleHandler mHandler = new ActTitleHandler();

	private HashMap<String, Integer> selectedMap = new HashMap<String, Integer>();
	private boolean selectedItemModel;
	private int idx;
	private QLXListView mListView;
	private EditText searchEditText;
	private List<FriendInfo> initSelectedItems;
	private ArrayList<FriendInfo> allItems = new ArrayList<FriendInfo>();
	private String org_id;

	private boolean[] statusBoolean;

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
				boolean _selected = selectedMap.containsKey(info.getWp_friends_info_id());
				CheckBox cBox = getViewForName("selected_item_cb", convertView);
				cBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						FriendInfo _info = (FriendInfo) buttonView.getTag();
						if (_info != null) {
							setSelected(_info);
						}
						if (statusBoolean != null) {
							statusBoolean[position] = isChecked;
						}
					}
				});
				cBox.setTag(info);
				if (statusBoolean[position] != cBox.isChecked()) {
					cBox.setChecked(statusBoolean[position]);
				}
				// cBox.setChecked(_selected);
				String url = HttpConfig.getUrl(info.getPicture_url());
				setImageToImageViewCenterCrop(url, "selected_item_imgview", -1, convertView);
				String _name = info.getFriend_name() == null || TextUtils.isEmpty(info.getFriend_name()) ? info.getNickName() : info.getFriend_name();
				setTextToViewText(_name, "selected_item_tv", convertView);
			}

			return convertView;
		}
	};

	@Override
	protected void initActTitleView() {
		setAbsHandler(mHandler);
	}

	@Override
	protected void initView(Bundle savedInstanceState) {
		setContentView(R.layout.activity_friends_list);

		reActTitle();

		mListView = (QLXListView) findViewById(R.id.friends_list_view);
	}

	@Override
	protected void initData() {

		idx = 0;
		selectedItemModel = getIntent().getBooleanExtra(kSELECTED_ITEM_MODEL, true);
		String customTitle = getIntent().getStringExtra(kTITLE);
		String _select_items_json = getIntent().getStringExtra(kSELECTED_ITEMS_JSON_STRING);
		org_id = getIntent().getStringExtra(KORG_ID);
		if (_select_items_json != null) {
			try {
				Gson gson = new Gson();
				initSelectedItems = gson.fromJson(_select_items_json, new TypeToken<List<FriendInfo>>() {
				}.getType());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (customTitle != null) {
			setTitle(customTitle);
		} else {
			setTitle("好友列表");
		}

		searchEditText = (EditText) findViewById(R.id.search_et);
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
		mListView.setPullRefreshEnable(true);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);

		getMemberList();

	}

	private void reActTitle() {

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

	private void setSelected(FriendInfo info) {
		if (info == null || info.getWp_friends_info_id() == null) {
			Log.i(getClass().getSimpleName(), "friendInfo or wp_friends_info_id is null !");
			return;
		}
		if (selectedMap.containsKey(info.getWp_friends_info_id())) {
			selectedMap.remove(info.getWp_friends_info_id());
		} else {
			selectedMap.put(info.getWp_friends_info_id(), Integer.valueOf(1));
			mAdapter.notifyDataSetChanged();
		}
		// String txt = selectedMap.keySet().size() > 0 ? "确定(" +
		// selectedMap.keySet().size() + ")" : "确定";
		// mHandler.getRightDefButton().setText(txt);
	}

	private void search(String text) {
		if (TextUtils.isEmpty(text)) {
			configSelectItems();
			mListView.stopRefresh();
			mAdapter.setDatas(allItems);
			return;
		}
		if (allItems == null || allItems.size() < 1) {
			return;
		}
		ArrayList<FriendInfo> tmpInfos = new ArrayList<FriendInfo>();
		for (FriendInfo friendInfo : allItems) {
			if (friendInfo.getNickName() != null && friendInfo.getNickName().contains(text)) {
				tmpInfos.add(friendInfo);
			}
		}
		mAdapter.setDatas(tmpInfos);
	}

	private void exitActivity() {
		if (selectedItemModel) {
			List<FriendInfo> _selectedItems = getSelectedItems();
			Gson gson = new Gson();
			String _sel_gson = gson.toJson(_selectedItems);
			Intent intent = new Intent();
			intent.putExtra(kSELECTED_ITEMS_JSON_STRING, _sel_gson);
			setResult(Activity.RESULT_OK, intent);
		}
		finish();
	}

	private List<FriendInfo> getSelectedItems() {
		ArrayList<FriendInfo> _tmpList = new ArrayList<FriendInfo>();
		for (String id : selectedMap.keySet()) {
			for (FriendInfo friendInfo : allItems) {
				if (friendInfo.getWp_friends_info_id() != null && friendInfo.getWp_friends_info_id().equals(id)) {
					_tmpList.add(friendInfo);
				}
			}
		}
		return _tmpList;
	}

	private void configSelectItems() {
		if (initSelectedItems == null || initSelectedItems.size() < 1) {
			return;
		}
		for (FriendInfo _info : initSelectedItems) {
			selectedMap.put(_info.getWp_friends_info_id(), Integer.valueOf(1));
		}
	}

	// 获取组织成员
	private void getMemberList() {

		OrganizationController.getOrgMemberList(this, org_id, new Listener<Void, List<OrgRequestMemberInfo>>() {

			@Override
			public void onCallBack(Void status, List<OrgRequestMemberInfo> reply) {
				allItems.clear();
				if (reply == null || reply.size() < 1) {
					return;
				}
				List<OrgRequestMemberInfo> tempInfos = new ArrayList<OrgRequestMemberInfo>();

				for (OrgRequestMemberInfo info : reply) {
					if (info.getPhone() == null || TextUtils.isEmpty(info.getPhone())) {
						continue;
					}
					tempInfos.add(info);
				}
				allItems.addAll(memberListToFriendList(tempInfos));
				configSelectItems();
				mListView.stopRefresh();
				mAdapter.setDatas(allItems);
				statusBoolean = new boolean[allItems.size()];

			}
		});

	}

	// 组织成员Info转成friendInfo
	private List<FriendInfo> memberListToFriendList(List<OrgRequestMemberInfo> reply) {
		List<FriendInfo> friendInfos = new ArrayList<FriendInfo>();
		for (OrgRequestMemberInfo info : reply) {

			if (info.getDepartmentId() == null || TextUtils.isEmpty(info.getDepartmentId())) {
				FriendInfo friendInfo = new FriendInfo();
				friendInfo.setPhone(info.getPhone());
				friendInfo.setSex(info.getSex());
				friendInfo.setPicture_url(info.getPicture_url());
				friendInfo.setNickName(info.getNickName());
				friendInfo.setWp_friends_info_id(info.getWp_member_info_id());

				friendInfos.add(friendInfo);
			}
		}

		return friendInfos;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		FriendInfo info = (FriendInfo) mAdapter.getItem(position);
		if (info == null) {
			return;
		}
		if (selectedItemModel) {
			// 选择模式
			setSelected(info);
		} else {
			// 打开个人资料？
		}

	}

}
