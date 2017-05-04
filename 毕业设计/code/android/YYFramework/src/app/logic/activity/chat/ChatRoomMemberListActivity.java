package app.logic.activity.chat;

import java.util.ArrayList;
import java.util.List;

import org.canson.view.swipemenulistview.SwipeMenu;
import org.canson.view.swipemenulistview.SwipeMenuCreator;
import org.canson.view.swipemenulistview.SwipeMenuItem;
import org.canson.view.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import org.ql.activity.customtitle.ActActivity;
import org.ql.utils.QLToastUtils;
import org.ql.views.listview.QLXListView.IXListViewListener;

import com.google.gson.Gson;

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
import android.widget.EditText;
import app.config.http.HttpConfig;
import app.logic.activity.ActTitleHandler;
import app.logic.adapter.YYBaseListAdapter;
import app.logic.controller.ChatRoomController;
import app.logic.controller.UserManagerController;
import app.logic.pojo.FriendInfo;
import app.logic.pojo.UserInfo;
import app.logic.pojo.YYChatRoomInfo;
import app.logic.pojo.YYChatSessionInfo;
import app.utils.common.Listener;
import app.utils.helpers.ChartHelper;
import app.utils.helpers.YYUtils;
import app.view.YYListView;
import app.yy.geju.R;

/**
 * 
 * SiuJiYung create at 2016年7月7日 下午6:34:07
 * 
 */

public class ChatRoomMemberListActivity extends ActActivity implements IXListViewListener {

	public static final String kROOM_INFO = "kROOM_INFO";
	private YYListView listView;
	private YYChatRoomInfo roomInfo;
	private EditText searchEt;

	private YYBaseListAdapter<UserInfo> mAdapter = new YYBaseListAdapter<UserInfo>(this) {
		public View createView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_yy_contacts_item, null);
			}
			UserInfo info = (UserInfo) getItem(position);
			if (info != null) {
				String imgPath = HttpConfig.getUrl(info.getPicture_url());
				setValueForImageViewFit(imgPath, -1, R.id.yy_user_headview, convertView);

				String name = info.getFriend_name() == null || TextUtils.isEmpty(info.getFriend_name()) ? info.getNickName() : info.getFriend_name();

				setValueForTextView(name, R.id.yy_listview_item_nametv, convertView);
				convertView.findViewById(R.id.yy_listview_item_timetv).setVisibility(View.GONE);
				convertView.findViewById(R.id.yy_listview_item_tag_tv).setVisibility(View.INVISIBLE);
				convertView.findViewById(R.id.yy_listview_item_open_flag_iv).setVisibility(View.INVISIBLE);
				// if (info.isRequest_accept()||info.isResponse()) {
				// convertView.findViewById(R.id.yy_listview_item_tag_tv).setVisibility(View.INVISIBLE);
				// convertView.findViewById(R.id.yy_listview_item_open_flag_iv).setVisibility(View.VISIBLE);
				// }else{
				// setValueForTextView("添加为好友",R.id.yy_listview_item_tag_tv,convertView);
				// convertView.findViewById(R.id.yy_listview_item_tag_tv).setVisibility(View.VISIBLE);
				// convertView.findViewById(R.id.yy_listview_item_open_flag_iv).setVisibility(View.INVISIBLE);
				// }
				// setValueForTextView(info.getCreate_time(),
				// R.id.yy_listview_item_timetv, convertView);
			}
			return convertView;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActTitleHandler handler = new ActTitleHandler();
		setAbsHandler(handler);
		setContentView(R.layout.fragment_message_list);

		handler.replaseLeftLayout(this, true);
		handler.getLeftLayout().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();

			}
		});

		searchEt = (EditText) findViewById(R.id.search_et);
		String roomInfo_json = getIntent().getStringExtra(kROOM_INFO);
		if (roomInfo_json != null) {
			Gson gson = new Gson();
			roomInfo = gson.fromJson(roomInfo_json, YYChatRoomInfo.class);
		}
		setTitle("群成员");

		listView = (YYListView) findViewById(R.id.message_list_view);
		listView.setXListViewListener(this);
		listView.setPullRefreshEnable(true);
		listView.setPullLoadEnable(false, true);
		SwipeMenuCreator menuCreator = new SwipeMenuCreator() {
			@Override
			public void create(SwipeMenu menu) {
				SwipeMenuItem item = new SwipeMenuItem(ChatRoomMemberListActivity.this);
				item.setBackground(R.drawable.menu_delete_bg);
				item.setWidth(YYUtils.dp2px(90, ChatRoomMemberListActivity.this));
				item.setTitleSize(16);
				item.setTitle("删除");
				item.setTitleColor(0xfffcfcfc);
				menu.addMenuItem(item);
			}
		};
		// listView.setMenuCreator(menuCreator);
		// listView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
		// @Override
		// public void onMenuItemClick(int position, SwipeMenu menu, int index)
		// {
		// UserInfo info = mAdapter.getItem(position);
		// removeMemberFromChatRoom(info);
		// mAdapter.removeItemAt(position);
		// }
		// });
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Object item = arg0.getAdapter().getItem(arg2);
				if (item == null) {
					return;
				}

				if (item instanceof YYChatSessionInfo) {
					YYChatSessionInfo info = (YYChatSessionInfo) item;
					ChartHelper.startChart(ChatRoomMemberListActivity.this, info.getWp_other_info_id(), info.getOrganizationName());
				} else if (item instanceof UserInfo) {
					UserInfo userInfo = UserManagerController.getCurrUserInfo();

					UserInfo info = (UserInfo) item;
					if (userInfo.getWp_member_info_id().equals(info.getWp_member_info_id())) {
						return;
					}
					ChartHelper.startChart(ChatRoomMemberListActivity.this, info.getWp_member_info_id(), "");
				}
			}
		});
		listView.setAdapter(mAdapter);
		if (roomInfo != null) {
			// mAdapter.setDatas(roomInfo.getCr_memberList());
		}

		searchEt.addTextChangedListener(new TextWatcher() {

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

		getChatRoomMemberList();
	}

	private void search(String s) {
		if (s == null || TextUtils.isEmpty(s)) {
			mAdapter.setDatas(roomInfo.getCr_memberList());
			return;
		}
		List<UserInfo> tempInfos = new ArrayList<UserInfo>();
		for (UserInfo info : roomInfo.getCr_memberList()) {
			String name = info.getFriend_name() == null || TextUtils.isEmpty(info.getFriend_name()) ? info.getNickName() : info.getFriend_name();
			if (name.contains(s)) {
				tempInfos.add(info);
			}
		}
		mAdapter.setDatas(tempInfos);
	}

	private void removeMemberFromChatRoom(UserInfo user) {
		if (user == null) {
			return;
		}
		ChatRoomController.removeMemberFromChatRoom(this, user.getWp_member_info_id(), roomInfo.getCr_id(), new Listener<Integer, String>() {
			@Override
			public void onCallBack(Integer status, String reply) {
				if (status == -1) {
					String msg = reply == null ? "移除失败" : reply;
					QLToastUtils.showToast(ChatRoomMemberListActivity.this, msg);
				}
			}
		});
	}

	private void getChatRoomMemberList() {
		ChatRoomController.getChatRoomInfo(this, roomInfo.getCr_id(), new Listener<Void, YYChatRoomInfo>() {
			@Override
			public void onCallBack(Void status, YYChatRoomInfo reply) {
				listView.stopLoadMore();
				listView.stopRefresh();
				dismissWaitDialog();
				if (reply != null) {
					roomInfo = reply;
					mAdapter.setDatas(roomInfo.getCr_memberList());
				}
			}
		});
	}

	@Override
	public void onRefresh() {
		getChatRoomMemberList();
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub

	}
}
