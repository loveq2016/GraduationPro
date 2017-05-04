package app.logic.activity.friends;

import java.util.ArrayList;
import java.util.List;

import org.ql.views.listview.QLXListView;

import cn.jpush.a.a.a.f;

import com.google.gson.Gson;

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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import app.config.http.HttpConfig;
import app.logic.activity.ActTitleHandler;
import app.logic.activity.InitActActivity;
import app.logic.activity.chat.ChatRoomInfoActivity;
import app.logic.adapter.YYBaseListAdapter;
import app.logic.controller.ChatRoomController;
import app.logic.controller.UserManagerController;
import app.logic.pojo.FriendInfo;
import app.logic.pojo.UserInfo;
import app.logic.pojo.YYChatRoomInfo;
/*
 * GZYY    2016-9-30  下午3:12:28
 */
import app.logic.pojo.OrgRequestMemberInfo;
import app.utils.common.Listener;
import app.yy.geju.R;

public class AddFriendToChatRoom extends InitActActivity implements OnItemClickListener {

	public static final String SELECTOR_ORG_ID = "SELECTOR_ORG_ID";
	public static final String kSELECTED_ITEMS_JSON_STRING = "kSELECTED_ITEMS_JSON_STRING";
	public static final String kTITLE = "KTITLE";
	public static final String kSELECTED_ITEM_MODEL = "kSELECTED_ITEM_MODEL";
	public static final int kSELECT_ITEMS = 24;

	private ActTitleHandler mHandler;
	private QLXListView mListView;
	private EditText searchEditText;

	private String chat_room_id;
	private List<FriendInfo> allItemList = new ArrayList<FriendInfo>();
	private List<UserInfo> chatRoomMemberList = new ArrayList<UserInfo>();
	private List<FriendInfo> flagList = new ArrayList<FriendInfo>();
	private boolean[] statusSelect;

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

						statusSelect[position] = isChecked;

					}
				});
				String url = HttpConfig.getUrl(info.getPicture_url());
				setImageToImageViewCenterCrop(url, "selected_item_imgview", -1, convertView);
				String _nameString = info.getFriend_name() == null || TextUtils.isEmpty(info.getFriend_name()) ? info.getNickName() : info.getFriend_name();
				setTextToViewText(_nameString, "selected_item_tv", convertView);

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

		setTitle("添加成员");
		mListView = (QLXListView) findViewById(R.id.friends_list_view);
		searchEditText = (EditText) findViewById(R.id.search_et);

	}

	@Override
	protected void initData() {
		chat_room_id = getIntent().getStringExtra(ChatRoomInfoActivity.kChatRoomID);

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

		getMyChatRoomMemberList();

	}

	// 搜索
	private void search(String keyString) {
		if (keyString == null || TextUtils.isEmpty(keyString)) {
			statusSelect = new boolean[allItemList.size()];
			mAdapter.setDatas(allItemList);
			return;
		}
		List<FriendInfo> selectList = new ArrayList<FriendInfo>();
		for (FriendInfo info : allItemList) {
			String infoKey = info.getFriend_name() == null || TextUtils.isEmpty(info.getFriend_name()) ? info.getNickName() : info.getFriend_name();
			if (infoKey.contains(keyString)) {
				selectList.add(info);
			}
		}
		statusSelect = new boolean[selectList.size()];
		mAdapter.setDatas(selectList);
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

	// 获取联系人列表，过滤掉已经加本群聊的人
	private void getMyChatRoomMemberList() {
		ChatRoomController.getChatRoomInfo(this, chat_room_id, new Listener<Void, YYChatRoomInfo>() {

			@Override
			public void onCallBack(Void status, YYChatRoomInfo reply) {
				if (reply == null) {
					return;
				}
				chatRoomMemberList = reply.getCr_memberList();

				UserManagerController.getFriendsList(AddFriendToChatRoom.this, new Listener<List<FriendInfo>, List<FriendInfo>>() {
					@Override
					public void onCallBack(List<FriendInfo> request, List<FriendInfo> reply) {
						if (reply == null || reply.size() < 1) {
							return;
						}
						allItemList.clear();

						for (FriendInfo friendInfo : reply) {
							boolean status = true;
							for (int i = 0; i < chatRoomMemberList.size(); i++) {
								if (friendInfo.getPhone().equals(chatRoomMemberList.get(i).getPhone())) {
									status = false;
									break;
								}
							}
							if (status) {
								allItemList.add(friendInfo);
							}

						}
						mAdapter.setDatas(allItemList);
						statusSelect = new boolean[allItemList.size()];
					}
				});

			}
		});

	}

	// 选出选中的item
	private void selectHaveTrue() {
		if (statusSelect == null || allItemList.size() != statusSelect.length) {
			return;
		}

		for (int i = 0; i < allItemList.size(); i++) {
			if (statusSelect[i]) {
				flagList.add(allItemList.get(i));
			}
		}

	}

	private void exitActivity() {
		selectHaveTrue();
		if (flagList.size() < 1) {
			finish();
		}

		Gson gson = new Gson();
		String _sel_gson = gson.toJson(flagList);
		Intent intent = new Intent();
		intent.putExtra(kSELECTED_ITEMS_JSON_STRING, _sel_gson);
		setResult(Activity.RESULT_OK, intent);

		finish();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub

	}

}
