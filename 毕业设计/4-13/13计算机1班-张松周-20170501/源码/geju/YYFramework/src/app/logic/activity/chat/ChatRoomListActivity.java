package app.logic.activity.chat;

import java.util.ArrayList;
import java.util.List;

import org.canson.view.swipemenulistview.SwipeMenu;
import org.canson.view.swipemenulistview.SwipeMenuCreator;
import org.canson.view.swipemenulistview.SwipeMenuItem;
import org.canson.view.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import org.ql.activity.customtitle.ActActivity;
import org.ql.app.alert.AlertDialog;
import org.ql.utils.QLToastUtils;
import org.ql.views.listview.QLXListView.IXListViewListener;

import app.logic.activity.main.HomeActivity;
import app.view.DialogNewStyleController;
import cn.jpush.android.util.al;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroupManager;

import android.R.integer;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;

import app.config.http.HttpConfig;
import app.logic.activity.ActTitleHandler;
import app.logic.activity.card.AddCardActivity;
import app.logic.activity.friends.FriendsListActivity;
import app.logic.activity.friends.FriendsListActivity2;
import app.logic.adapter.YYBaseListAdapter;
import app.logic.controller.ChatRoomController;
import app.logic.controller.UserManagerController;
import app.logic.pojo.FriendInfo;
import app.logic.pojo.UserInfo;
import app.logic.pojo.YYChatRoomInfo;
import app.utils.common.Listener;
import app.utils.helpers.ChartHelper;
import app.utils.helpers.YYUtils;
import app.view.YYListView;
import app.yy.geju.R;
import app.yy.geju.R.id;

/**
 * SiuJiYung create at 2016年7月1日 下午6:02:14
 */

public class ChatRoomListActivity extends ActActivity implements IXListViewListener, OnItemClickListener {

    private YYListView mListView;

    private EditText search_edt;
    private View empty_view;

    private List<YYChatRoomInfo> datas = new ArrayList<YYChatRoomInfo>();
    private List<YYChatRoomInfo> selectDatas = new ArrayList<YYChatRoomInfo>();

    private YYBaseListAdapter<YYChatRoomInfo> mAdapter = new YYBaseListAdapter<YYChatRoomInfo>(this) {

        @Override
        public View createView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.view_chat_room_item, null);
                saveView("chat_room_image_view", R.id.chat_room_image_view, convertView);
                saveView("chat_room_name_tv", R.id.chat_room_name_tv, convertView);
            }
            YYChatRoomInfo info = getItem(position);
            if (info != null) {
                //String urlString = HttpConfig.getUrl(info.getCr_id());
                String urlString = HttpConfig.getUrl(info.getCr_picture());
                setTextToViewText(info.getCr_name(), "chat_room_name_tv", convertView);
                setImageToImageView(urlString, "chat_room_image_view", -1, convertView);
            }
            return convertView;
        }
    };

    private ActTitleHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        handler = new ActTitleHandler();
        setAbsHandler(handler);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom_list);

        initActTitle();

        empty_view = findViewById(R.id.empty_view);
        mListView = (YYListView) findViewById(R.id.chatroom_list_listview);
        mListView.setPullRefreshEnable(true);
        mListView.setPullLoadEnable(false, true);
        mListView.setAdapter(mAdapter);
        mListView.setXListViewListener(this);
        mListView.setOnItemClickListener(this);

        SwipeMenuCreator menuCreator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem openItem = new SwipeMenuItem(ChatRoomListActivity.this);
                openItem.setBackground(R.drawable.menu_delete_bg);
                openItem.setWidth(YYUtils.dp2px(90, ChatRoomListActivity.this));
                openItem.setTitle("移除");
                openItem.setTitleSize(16);
                openItem.setTitleColor(0xfffcfcfc);
                menu.addMenuItem(openItem);
            }
        };
//        mListView.setMenuCreator(menuCreator);
//        mListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
//            @Override
//            public void onMenuItemClick(int position, SwipeMenu menu, int index) {
//                if (index == 0) {
//                    // 删除选项
//                    removeChatRoom(position);
//                }
//            }
//        });

        search_edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyString = s.toString();

                if (!TextUtils.isEmpty(keyString)) {
                    selectDatas.clear();
                    for (YYChatRoomInfo info : datas) {
                        if (info.getCr_name().contains(keyString)) {
                            selectDatas.add(info);
                        }
                    }
                    mAdapter.setDatas(selectDatas);
                } else {
                    mAdapter.setDatas(datas);
                }
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
        getChatRoomList();
    }

    private void initActTitle() {
        handler.addRightView(LayoutInflater.from(this).inflate(R.layout.title_right_layout_view, null), true);
        ImageView addView = (ImageView) handler.getRightLayout().findViewById(R.id.title_add_iv);
        addView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ChatRoomListActivity.this, FriendsListActivity2.class);
                intent.putExtra(FriendsListActivity2.kTITLE, "发起群聊");
                intent.putExtra(FriendsListActivity2.ADD, true);
                intent.putExtra(FriendsListActivity2.kSELECTED_ITEM_MODEL, true);
                startActivityForResult(intent, FriendsListActivity2.kSELECT_ITEMS);
            }
        });
        handler.replaseLeftLayout(this, true);
        handler.getLeftLayout().setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();

            }
        });
        ((TextView) handler.getLeftLayout().findViewById(R.id.left_tv)).setText("我的群聊");

        setTitle("");
        findViewById(R.id.search_bg).setBackgroundColor(getResources().getColor(R.color.white));
        search_edt = (EditText) findViewById(R.id.search_edt);
        search_edt.setHint("群聊名称");
        findViewById(R.id.search_edt_bg).setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_search_edt_bg));
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        YYChatRoomInfo info = mAdapter.getItem(arg2 - 1);
        if (info == null) {
            return;
        }
//        ChartHelper.openChatRoom(ChatRoomListActivity.this, info);

        ChartHelper.openChatRoom(ChatRoomListActivity.this, info.getRoom_id(), info.getCr_id());
    }

    private void removeChatRoom(final int positon) {
        YYChatRoomInfo info = mAdapter.getItem(positon);
        if (info != null) {
            ChatRoomController.removeChatRoom(this, info.getCr_id(), new Listener<Integer, String>() {
                @Override
                public void onCallBack(Integer status, String reply) {
                    if (status == -1) {
                        String msg = reply == null ? "操作失败" : reply;
                        QLToastUtils.showToast(ChatRoomListActivity.this, msg);
                    } else {
                        mAdapter.removeItemAt(positon);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    private void getChatRoomList() {
        showWaitDialog();
        ChatRoomController.getChatRoomList(this, new Listener<Void, List<YYChatRoomInfo>>() {
            @Override
            public void onCallBack(Void status, List<YYChatRoomInfo> reply) {
                search_edt.setText("");
                dismissWaitDialog();
                mListView.stopLoadMore();
                mListView.stopRefresh();
                if (reply != null && reply.size() > 0) {
                    empty_view.setVisibility(View.GONE);
                    datas.clear();
                    datas.addAll(reply);
                    mAdapter.setDatas(datas);
                    return;
                }
                empty_view.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onRefresh() {
        getChatRoomList();
    }

    @Override
    public void onLoadMore() {
        // TODO Auto-generated method stub
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == FriendsListActivity2.kSELECT_ITEMS) {
            if (data != null) {
                String datas = data.getStringExtra(FriendsListActivity2.kSELECTED_ITEMS_JSON_STRING);
                try {
                    Gson gson = new Gson();
                    List<FriendInfo> _items = gson.fromJson(datas, new TypeToken<List<FriendInfo>>() {}.getType());
                    showCreateChatRoomDialog(_items);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 原来的创建群聊
     *
     * @param members
     */
    private void showCreateChatRoomDialog(final List<FriendInfo> members) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_new_edit_view, null);
        final DialogNewStyleController dialog = new DialogNewStyleController(this, contentView);
        TextView titleTv = (TextView) contentView.findViewById(id.dialog_title_tv);
        final EditText contentEdt = (EditText) contentView.findViewById(R.id.dialog_content_edt);
        Button createBtn = (Button) contentView.findViewById(R.id.dialog_cancel_btn);
        Button cancelBtn = (Button) contentView.findViewById(R.id.dialog_true_btn);

        titleTv.setText("创建群聊");
        createBtn.setText("创建");
        cancelBtn.setText("取消");
        contentEdt.setHint("聊天室名称");
        createBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String _txt = contentEdt.getText().toString();
                if (_txt == null || TextUtils.isEmpty(_txt)) {
                    QLToastUtils.showToast(ChatRoomListActivity.this, "名称不能为空");
                } else {
                    createChatRoom(members, _txt);
                    dialog.dismiss();
                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        dialog.show();
    }

    private void createChatRoom(final List<FriendInfo> members, String name) {
        showWaitDialog();
        ArrayList<UserInfo> _members = new ArrayList<UserInfo>();
//        if (members != null) {
//            for (FriendInfo friendInfo : members) {
//                UserInfo userInfo = new UserInfo();
//                userInfo.setWp_member_info_id(friendInfo.getWp_friends_info_id());
//                userInfo.setPhone(friendInfo.getPhone());
//                _members.add(userInfo);
//            }
//        }

        YYChatRoomInfo info = new YYChatRoomInfo();
        UserInfo userInfo = UserManagerController.getCurrUserInfo();
        info.setCr_creatoName(userInfo.getNickName());
        info.setCr_creatorId(userInfo.getWp_member_info_id());
        info.setCr_name(name);
        info.setCr_type("1");
//        info.setCr_memberList(_members);

        List<UserInfo> cr_memberList = new ArrayList<UserInfo>();
        for (int idx = 0; idx < members.size(); idx++){
            FriendInfo info1 = members.get(idx);
            UserInfo userInfo1 = new UserInfo();
            userInfo1.setPhone(info1.getPhone());
            userInfo1.setPicture_url(info1.getPicture_url());
            userInfo1.setNickName(info1.getNickName());
            userInfo1.setRequest_accept(info1.isRequest_accept());
            userInfo1.setWp_friends_info_id(info1.getWp_friends_info_id());
            userInfo1.setFriend_name(info1.getFriend_name());
            userInfo1.setSex(info1.getSex());
            cr_memberList.add(userInfo1);
        }
        info.setCr_memberList(cr_memberList);

        ChatRoomController.createChatRoom(this, info, 0 , new Listener<Integer, YYChatRoomInfo>() {

            @Override
            public void onCallBack(Integer status, YYChatRoomInfo reply) {
                dismissWaitDialog();
                if (reply != null) {
                    //addMembersToChatRoom(members, reply.getCr_id(), reply.getRoom_id());
                    final String cr_id = reply.getCr_id();
                    final String room_id = reply.getRoom_id();
                    ChartHelper.openChatRoom( ChatRoomListActivity.this , room_id , cr_id ,true);
                } else {
                    QLToastUtils.showToast(ChatRoomListActivity.this, "创建群聊失败");
                }
            }
        });
    }

    private void addMembersToChatRoom(List<FriendInfo> friendInfos, final String cr_id, final String room_id) {
        if (friendInfos == null || friendInfos.size() < 1) {
            dismissWaitDialog();
            ChartHelper.openChatRoom(ChatRoomListActivity.this, room_id, cr_id);
            return;
        }
        StringBuilder sBuilder = new StringBuilder();

        for (int idx = 0; idx < friendInfos.size(); idx++) {
            FriendInfo info = friendInfos.get(idx);
            sBuilder.append(info.getWp_friends_info_id());
            if (idx != (friendInfos.size() - 1)) {
                sBuilder.append(",");
            }
        }

        ChatRoomController.addMemberToChatRoom(this, cr_id, sBuilder.toString(), new Listener<Integer, String>() {
            @Override
            public void onCallBack(Integer status, String reply) {
                dismissWaitDialog();
                if (status == 1) {
                    ChartHelper.openChatRoom(ChatRoomListActivity.this, room_id, cr_id);
                } else {
                    QLToastUtils.showToast(ChatRoomListActivity.this, "创建群聊成功，添加成员失败.");
                }
            }
        });
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        //getChatRoomList();
    }
}
