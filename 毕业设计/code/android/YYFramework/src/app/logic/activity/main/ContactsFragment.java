package app.logic.activity.main;

import java.util.ArrayList;
import java.util.List;

import org.canson.view.swipemenulistview.SwipeMenu;
import org.canson.view.swipemenulistview.SwipeMenuCreator;
import org.canson.view.swipemenulistview.SwipeMenuItem;
import org.canson.view.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import org.ql.utils.QLToastUtils;
import org.ql.utils.image.QLAsyncImage;
import org.ql.views.listview.QLXListView;
import org.ql.views.listview.QLXListView.IXListViewListener;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import app.config.http.HttpConfig;
import app.logic.activity.chat.ChatRoomListActivity;
import app.logic.activity.friends.AddFriendsActivity;
import app.logic.activity.user.PreviewFriendsInfoActivity;
import app.logic.adapter.YYBaseListAdapter;
import app.logic.controller.UserManagerController;
import app.logic.pojo.FriendInfo;
import app.logic.pojo.FriendRequestInfo;
import app.logic.pojo.UserInfo;
import app.logic.pojo.YYChatSessionInfo;
import app.utils.common.Listener;
import app.utils.helpers.ChartHelper;
import app.utils.helpers.YYUtils;
import app.view.YYListView;
import app.yy.geju.R;

/**
 * 
 * SiuJiYung create at 2016-6-2 下午8:01:20
 * 
 */

public class ContactsFragment extends Fragment implements IXListViewListener, OnClickListener {

	private YYListView listView;
	private YYBaseListAdapter<FriendInfo> adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.from(getActivity()).inflate(R.layout.fragment_contacts_list, null);
		setView(view);
		return view;
	}

	private void setView(View view) {
		adapter = new YYBaseListAdapter<FriendInfo>(getActivity()) {
			// private QLAsyncImage imgLoader = null ;

			@Override
			public View createView(int position, View convertView, ViewGroup parent) {
				// if (imgLoader == null) {
				// imgLoader = new QLAsyncImage(getActivity());
				// }
				if (convertView == null) {
					convertView = LayoutInflater.from(getActivity()).inflate(R.layout.listview_yy_contacts_item, null);
					saveView("yy_listview_item_timetv", R.id.yy_listview_item_timetv, convertView);
					saveView("yy_user_headview", R.id.yy_user_headview, convertView);
					saveView("yy_listview_item_nametv", R.id.yy_listview_item_nametv, convertView);
					saveView("yy_listview_item_tag_tv", R.id.yy_listview_item_tag_tv, convertView);
					saveView("yy_listview_item_open_flag_iv", R.id.yy_listview_item_open_flag_iv, convertView);
				}
				FriendInfo info = (FriendInfo) getItem(position);
				if (info != null) {
					((View) getViewForName("yy_listview_item_timetv", convertView)).setVisibility(View.GONE);

					if ((info.isRequest_accept() || info.isResponse()) && !info.isRequestMessage()) {
						String imgPath = HttpConfig.getUrl(info.getPicture_url());
						ImageView headview = getViewForName("yy_user_headview", convertView);
						setImageToImageView(imgPath, "yy_user_headview", R.drawable.default_user_icon, convertView);
						// imgLoader.loadImage(imgPath,
						// headview,R.drawable.default_user_icon);

						TextView nameTv = getViewForName("yy_listview_item_nametv", convertView);
						View tagView = getViewForName("yy_listview_item_tag_tv", convertView);
						View openTagView = getViewForName("yy_listview_item_open_flag_iv", convertView);

						nameTv.setText(info.getNickName());
						tagView.setVisibility(View.INVISIBLE);
						// openTagView.setVisibility(View.VISIBLE);
					} else {
						String nameString = info.getRequest_nickName() == null || TextUtils.isEmpty(info.getRequest_nickName()) ? info.getNickName() : info.getRequest_nickName();
						String path = info.getRequest_picture_url() == null || TextUtils.isEmpty(info.getRequest_picture_url()) ? info.getPicture_url() : info.getRequest_picture_url();
						String imgPath = HttpConfig.getUrl(path);

						ImageView headview = getViewForName("yy_user_headview", convertView);
						// imgLoader.loadImage(imgPath,
						// headview,R.drawable.default_user_icon);
						setImageToImageView(imgPath, "yy_user_headview", R.drawable.default_user_icon, convertView);

						TextView nameTv = getViewForName("yy_listview_item_nametv", convertView);
						TextView tagTv = getViewForName("yy_listview_item_tag_tv", convertView);
						View openTagView = getViewForName("yy_listview_item_open_flag_iv", convertView);

						nameTv.setText(nameString);
						tagTv.setText("好友请求");
						tagTv.setVisibility(View.VISIBLE);
						openTagView.setVisibility(View.INVISIBLE);
					}
					// setValueForTextView(info.getCreate_time(),
					// R.id.yy_listview_item_timetv, convertView);
				}
				return convertView;
			}
		};

		view.findViewById(R.id.contact_fragment_add_friends_tv).setOnClickListener(this);
		view.findViewById(R.id.contact_fragment_my_chatrooms_tv).setOnClickListener(this);

		listView = (YYListView) view.findViewById(R.id.contacts_list_view);
		SwipeMenuCreator menuCreator = new SwipeMenuCreator() {
			@Override
			public void create(SwipeMenu menu) {
				SwipeMenuItem item = new SwipeMenuItem(getContext());
				item.setBackground(R.drawable.menu_delete_bg);
				item.setWidth(YYUtils.dp2px(90, getContext()));
				item.setTitleSize(16);
				item.setTitle("删除");
				item.setTitleColor(0xfffcfcfc);
				menu.addMenuItem(item);
			}
		};
		listView.setMenuCreator(menuCreator);
		listView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public void onMenuItemClick(int position, SwipeMenu menu, int index) {
				FriendInfo info = adapter.getItem(position);
				if (info != null) {
					removeFriend(info);
					adapter.removeItemAt(position);
				}
			}
		});
		listView.setHeaderDividersEnabled(false);
		listView.setXListViewListener(this);
		listView.setAdapter(adapter);
		listView.setPullLoadEnable(false, true);
		listView.setPullRefreshEnable(true);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				FriendInfo info = (FriendInfo) adapter.getItem(arg2 - 1);
				if (info == null /* || !info.isResponse() */) {
					return;
				}
				if (info.isRequest_accept() || info.isResponse()) {
					Intent intent = new Intent();
					intent.setClass(getContext(), PreviewFriendsInfoActivity.class);
					intent.putExtra(PreviewFriendsInfoActivity.kFROM_CHART_ACTIVITY, false);
					intent.putExtra(PreviewFriendsInfoActivity.kUSER_MEMBER_ID, info.getWp_friends_info_id());
					startActivity(intent);
				} else {
					Intent intent = new Intent();
					intent.setClass(getContext(), AddFriendsActivity.class);
					startActivity(intent);
				}
			}
		});
		loadContacts();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.contact_fragment_my_chatrooms_tv:
			Intent intent_cr = new Intent();
			intent_cr.setClass(getContext(), ChatRoomListActivity.class);
			startActivity(intent_cr);
			// QLToastUtils.showToast(getContext(), "功能开发中...");
			break;
		case R.id.contact_fragment_add_friends_tv:
			Intent intent = new Intent();
			intent.setClass(getContext(), AddFriendsActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	private void loadContacts() {
		UserManagerController.getFriendsList(getActivity(), new Listener<List<FriendInfo>, List<FriendInfo>>() {
			@Override
			public void onCallBack(List<FriendInfo> request, List<FriendInfo> reply) {
				ArrayList<FriendInfo> tmpInfos = new ArrayList<FriendInfo>();
				if (request != null && request.size() > 0) {
					// 他人请求
					for (FriendInfo reqInfo : request) {
						if (reqInfo.isResponse()) {
							continue;
						}
						reqInfo.setOtherRequest(true);
						tmpInfos.add(reqInfo);
					}
				}

				if (reply != null && reply.size() > 0) {
					String myPhone = UserManagerController.getCurrUserInfo().getPhone();
					for (FriendInfo friendInfo : reply) {
						if (friendInfo.getPhone() != null && !friendInfo.getPhone().equals(myPhone)) {
							friendInfo.setOtherRequest(false);
							tmpInfos.add(friendInfo);
						}
					}
				}

				adapter.setDatas(tmpInfos);
				listView.stopLoadMore();
				listView.stopRefresh();
			}
		});
	}

	private void removeFriend(FriendInfo info) {
		UserManagerController.deleteFriendsById(getContext(), info.getWp_friends_info_id(), null);
	}

	@Override
	public void onRefresh() {
		loadContacts();
	}

	@Override
	public void onLoadMore() {

	}

}
