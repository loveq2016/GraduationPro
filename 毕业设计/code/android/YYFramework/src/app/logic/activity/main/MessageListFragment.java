package app.logic.activity.main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMLocationMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.EMVideoMessageBody;
import com.hyphenate.chat.EMVoiceMessageBody;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.controller.EaseUI;

import org.QLConstant;
import org.canson.view.swipemenulistview.SwipeMenu;
import org.canson.view.swipemenulistview.SwipeMenuCreator;
import org.canson.view.swipemenulistview.SwipeMenuItem;
import org.canson.view.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import org.ql.utils.QLDateUtils;
import org.ql.utils.QLToastUtils;
import org.ql.views.listview.QLXListView.IXListViewListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import app.config.http.HttpConfig;
import app.logic.activity.notify.NotifyActivity;
import app.logic.activity.user.PreviewFriendsInfoActivity;
import app.logic.adapter.YYBaseListAdapter;
import app.logic.controller.ChatRoomController;
import app.logic.controller.OrganizationController;
import app.logic.controller.UserManagerController;
import app.logic.pojo.Chatroom;
import app.logic.pojo.UserInfo;
import app.logic.pojo.YYChatSessionInfo;
import app.logic.singleton.ZSZSingleton;
import app.utils.common.FrescoImageShowThumb;
import app.utils.common.Listener;
import app.utils.helpers.ChartHelper;
import app.utils.helpers.SharepreferencesUtils;
import app.utils.helpers.YYUtils;
import app.view.YYListView;
import app.yy.geju.R;

/**
 * SiuJiYung create at 2016-6-2 下午7:59:47
 */

public class MessageListFragment extends Fragment implements EMMessageListener ,IXListViewListener, OnClickListener {

    private View nullDataView;
    private YYListView listView;
    private YYBaseListAdapter<YYChatSessionInfo> adapter;
    private ArrayList<YYChatSessionInfo> sessionList = new ArrayList<YYChatSessionInfo>();
    private View view;
    private Context mContext;

    private TextView notify_unReadCount;
    private TextView notify_name;
    private TextView notify_msg;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.from(getActivity()).inflate(R.layout.fragment_message_list, null);
            setView(view);
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
    }

    private void setView(View view) {
        adapter = new YYBaseListAdapter<YYChatSessionInfo>(getActivity()) {
            @Override
            public View createView(int position, View convertView, ViewGroup parent) {

                if (convertView == null) {
                    convertView = LayoutInflater.from(getActivity()).inflate(R.layout.item_message_view_cell2, null);
                    saveView("yy_user_headview", R.id.yy_user_headview, convertView);
                    saveView("yy_listview_item_open_flag_btn", R.id.yy_listview_item_open_flag_btn, convertView);
                    saveView("yy_listview_item_nametv", R.id.yy_listview_item_nametv, convertView);
                    saveView("yy_listview_item_timetv", R.id.yy_listview_item_timetv, convertView);
                    saveView("yy_listview_item_tag_tv", R.id.yy_listview_item_tag_tv, convertView);
                }
                YYChatSessionInfo info = (YYChatSessionInfo) getItem(position);
                if (info != null) {
                    // onNewMessage(info);
                    SimpleDraweeView headIv = getViewForName("yy_user_headview", convertView);
                    TextView titleView = getViewForName("yy_listview_item_nametv", convertView);
                    TextView latesChartView = getViewForName("yy_listview_item_timetv", convertView);
                    TextView timeTV = getViewForName("yy_listview_item_tag_tv", convertView);
                    TextView btnButton = getViewForName("yy_listview_item_open_flag_btn", convertView);

                    if (info.getChatroom() != null) { //群聊信息
                        Chatroom chatroom = info.getChatroom();
                        //Picasso.with(getContext()).load(R.drawable.default_user_icon).fit().centerCrop().into(headIv);
                        latesChartView.setText(chatroom.getLatestChart());
                        titleView.setText(info.getChatroom().getCr_name());
                        timeTV.setText(info.getChatroom().getMsgLastTimeString());
                        String unReadCount;
                        if (chatroom.getUnReadMessageCount() > 99) {
                            unReadCount = 99 + "+";
                        } else {
                            unReadCount = chatroom.getUnReadMessageCount() + "";
                        }
                        btnButton.setText(unReadCount);
                        timeTV.setVisibility(View.VISIBLE);
                        btnButton.setVisibility(chatroom.getUnReadMessageCount() > 0 ? View.VISIBLE : View.INVISIBLE);

                        if (TextUtils.isEmpty(chatroom.getCr_picture())) {
                            //Picasso.with(getContext()).load(R.drawable.default_user_icon).error(R.drawable.default_user_icon).fit().centerCrop().into(headIv);
                        } else {
                            String url = HttpConfig.getUrl(chatroom.getCr_picture());
                            FrescoImageShowThumb.showThrumb(Uri.parse(url),headIv);
                        }
                    } else { //单聊
                        if (TextUtils.isEmpty(info.getPicture_url())) {
                            //Picasso.with(getContext()).load(R.drawable.default_user_icon).error(R.drawable.default_user_icon).fit().centerCrop().into(headIv);
                        } else {
                            String url = HttpConfig.getUrl(info.getPicture_url());
                            FrescoImageShowThumb.showThrumb(Uri.parse(url),headIv);
                        }
                        btnButton.setVisibility(info.getUnreadCount() > 0 ? View.VISIBLE : View.INVISIBLE);
                        String unReadCount;
                        if (info.getUnreadCount() > 99) {
                            unReadCount = 99 + "+";
                        } else {
                            unReadCount = info.getUnreadCount() + "";
                        }
                        btnButton.setText(unReadCount);
                        String title_str = info.getFriend_name() == null || TextUtils.isEmpty(info.getFriend_name()) ? info.getNickName() : info.getFriend_name();
                        timeTV.setText(info.getLastTime());
                        titleView.setText(title_str);

                        SharepreferencesUtils utils = new SharepreferencesUtils(mContext);
                        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(info.getWp_other_info_id());
                        if (conversation !=null){
                            if(!utils.getConversion(info.getWp_other_info_id()+conversation.conversationId()).equals("")){
                                latesChartView.setText(Html.fromHtml("<font color=\"#ff00000\">[草稿]</font>"+utils.getConversion(info.getWp_other_info_id()+conversation.conversationId())));
                            }else{
                                latesChartView.setText(info.getLatestChart());
                            }
                        }else{
                            latesChartView.setText(info.getLatestChart());
                        }


                        if (info.getLatestChart() == null || TextUtils.isEmpty(info.getLatestChart())) {
                            timeTV.setVisibility(View.INVISIBLE);
                        } else {
                            timeTV.setVisibility(View.VISIBLE);
                        }
                    }
                }
                return convertView;
            }
        };

        nullDataView = view.findViewById(R.id.empty_view);
        ((TextView) view.findViewById(R.id.empty_tv01)).setText("您还没有跟朋友聊过天呢");
        ((TextView) view.findViewById(R.id.empty_tv02)).setText("赶紧找个朋友畅谈吧");
        listView = (YYListView) view.findViewById(R.id.message_list_view);
        listView.setXListViewListener(this);
        listView.setPullRefreshEnable(true);
        listView.setPullLoadEnable(false, true);
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
                YYChatSessionInfo sessionInfo = adapter.getItem(position);
                if (sessionInfo == null) {
                    return;
                }
                if (index == 0) {
                    adapter.removeItemAt(position);
                    if (sessionInfo.getChatroom() == null) {
                        ChartHelper.removeChart(getContext(), sessionInfo.getWp_dialogue_info_id(), new Listener<Void, Void>() {
                            @Override
                            public void onCallBack(Void status, Void reply) {
                            }
                        });
                    } else {
                        ChatRoomController.registerChatToMesssageList(getActivity(), sessionInfo.getChatroom().getCr_id(), "0", new Listener<Integer, String>() {
                            @Override
                            public void onCallBack(Integer integer, String reply) {
                                loadMessageList();
                            }
                        });
                    }
                }
            }
        });
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if( HomeActivity.isOpeningChat ){ //正在打开一个聊天界面的标志 ing ，正在打开中 ， ed 聊天界面打开结束
                    return;
                }
                HomeActivity.isOpeningChat = true ; //正在打开一个聊天界面
                YYChatSessionInfo info = (YYChatSessionInfo) arg0.getAdapter().getItem(arg2);
                if (info == null) {
                    return;
                }
                if (info.getChatroom() == null) {
                    ChartHelper.startChart(getActivity(), info.getWp_other_info_id(), info.getOrganizationName());
                } else {
                    ChartHelper.openChatRoom(getActivity(), info.getChatroom().getRoom_id(), info.getChatroom().getCr_id(),EaseConstant.FROM_ACTIVITY);
                }
            }
        });
        listView.setAdapter(adapter);

        notify_msg = (TextView) view.findViewById(R.id.yy_listview_item_timetv);
        notify_name = (TextView) view.findViewById(R.id.yy_listview_item_nametv);
        notify_unReadCount = (TextView) view.findViewById(R.id.yy_listview_item_open_flag_btn);
        ((ImageView)view.findViewById(R.id.yy_user_headview)).setImageResource(R.drawable.icon_notify);
        notify_name.setText("系统通知");
        notify_msg.setVisibility(View.GONE);
        notify_unReadCount.setVisibility(View.INVISIBLE);
        view.findViewById(R.id.notify_layout).setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        EMClient.getInstance().chatManager().addMessageListener(this);
        // bgn_tv.setVisibility(View.VISIBLE);
		//EMChatManager.getInstance().registerEventListener(this);
        EaseUI.getInstance().pushActivity(getActivity());
		//DemoHXSDKHelper sdkHelper = (DemoHXSDKHelper) DemoHXSDKHelper.getInstance();
        // 把此activity 从foreground activity 列表里移除
		//sdkHelper.popActivity(getActivity());
        loadMessageList();
        getNotifyCount();
    }

    @Override
    public void onPause() {
        super.onPause();
        EMClient.getInstance().chatManager().addMessageListener(this);
        //EMClient.getInstance().chatManager().removeMessageListener(this); //
        EaseUI.getInstance().pushActivity(getActivity());
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onMessageRead(List<EMMessage> list) {
    }

    @Override
    public void onMessageDelivered(List<EMMessage> list) {
    }

    @Override
    public void onMessageChanged(EMMessage arg0, Object arg1) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onMessageReceived(List<EMMessage> messages) {
        // TODO Auto-generated method stub
        for (EMMessage message : messages) {
            if (message.getChatType() == EMMessage.ChatType.Chat) {
                addChart(message.getFrom(), message);
            } else {
                addChatGroupToMessage(message.getTo());
            }
        }
    }


    // 获取用户信息，注册回话
    private void addChart(final String member_id, final EMMessage msg) {
//        UserManagerController.getPhoneMemerInfo(mContext, phone, new Listener<Integer, List<UserInfo>>() {
//            @Override
//            public void onCallBack(Integer status, List<UserInfo> reply) {
//                if (reply != null && reply.size() > 0) {
//                    String latestChart = null;
//                    if (msg != null && msg.getBody() != null) {
//                        if (msg.getBody() instanceof EMTextMessageBody) {
//                            latestChart = ((EMTextMessageBody) msg.getBody()).getMessage();
//                        } else if (msg.getBody() instanceof EMLocationMessageBody) {
//                            latestChart = ((EMLocationMessageBody) msg.getBody()).getAddress();
//                        } else if (msg.getBody() instanceof EMImageMessageBody) {
//                            //latestChart = "图片[" + ((EMImageMessageBody) msg.getBody()).getFileName() + "]";
//                            latestChart = "[图片]";
//                        } else if (msg.getBody() instanceof EMVideoMessageBody) {
//                            latestChart = "[视频]";
//                        } else if (msg.getBody() instanceof EMVoiceMessageBody) {
//                            latestChart = "[语音]";
//                        }
//                    }
//                    UserManagerController.addChatWith(getContext(), reply.get(0).getWp_member_info_id(), latestChart, new Listener<Integer, String>() {
//                        @Override
//                        public void onCallBack(Integer status, String reply) {
//                            loadMessageList();
//                        }
//                    });
//                }
//            }
//        });

        UserManagerController.getUserInfo(mContext, member_id, new Listener<Integer, UserInfo>() {
            @Override
            public void onCallBack(Integer status , UserInfo reply) {
                if (status == 1 && reply != null) {
                    String latestChart = null;
                    if (msg != null && msg.getBody() != null) {
                        if (msg.getBody() instanceof EMTextMessageBody) {
                            latestChart = ((EMTextMessageBody) msg.getBody()).getMessage();
                        } else if (msg.getBody() instanceof EMLocationMessageBody) {
                            latestChart = ((EMLocationMessageBody) msg.getBody()).getAddress();
                        } else if (msg.getBody() instanceof EMImageMessageBody) {
                            //latestChart = "图片[" + ((EMImageMessageBody) msg.getBody()).getFileName() + "]";
                            latestChart = "[图片]";
                        } else if (msg.getBody() instanceof EMVideoMessageBody) {
                            latestChart = "[视频]";
                        } else if (msg.getBody() instanceof EMVoiceMessageBody) {
                            latestChart = "[语音]";
                        }
                    }


                    UserManagerController.addChatWith(getContext(), reply.getWp_member_info_id(), latestChart, new Listener<Integer, String>() {
                        @Override
                        public void onCallBack(Integer status, String reply) {
                            loadMessageList();
                        }
                    });
                }
            }
        });
    }

    private void loadMessageList() {
        sessionList.clear();
        UserManagerController.getChatList(mContext, new Listener<Integer, List<YYChatSessionInfo>>() {
            @Override
            public void onCallBack(Integer status, List<YYChatSessionInfo> reply) {
                ArrayList<YYChatSessionInfo> _tmpList = new ArrayList<YYChatSessionInfo>();
                int unReadCountTemp = 0;
                listView.stopLoadMore();
                listView.stopRefresh();
                if (reply != null && reply.size() > 0) {
                    for (YYChatSessionInfo yyChatSessionInfo : reply) {
                        if (yyChatSessionInfo.getChatroom() == null) { //单聊
                            // 过滤掉和自己的聊天记录
                            if (yyChatSessionInfo.getWp_member_info_id().equals(yyChatSessionInfo.getWp_other_info_id())) {
                                continue;
                            }
                            // sessionList.add(yyChatSessionInfo);
                            // 计算未读
                            EMConversation conversation = EMClient.getInstance().chatManager().getConversation(yyChatSessionInfo.getWp_other_info_id());
//                            EMConversation conversation = EMClient.getInstance().chatManager().getConversation(yyChatSessionInfo.getPhoneNumber());
                            if (conversation != null) {
                                unReadCountTemp += conversation.getUnreadMsgCount();
                            }
                        } else { //群聊
                            EMConversation conversation = EMClient.getInstance().chatManager().getConversation(yyChatSessionInfo.getChatroom().getRoom_id());
                            if (conversation != null) {
                                unReadCountTemp += conversation.getUnreadMsgCount();
                            }
                        }
                        _tmpList.add(yyChatSessionInfo);
                    }
                    //设置未读数量
                    List<YYChatSessionInfo> sortList = sortYYChatSessionInfos(setUnreadYYChatSessionInfos(_tmpList));
                    sessionList.addAll(sortList);
                    if (sortList == null || sortList.size() < 1) {
                        nullDataView.setVisibility(View.VISIBLE);
                    } else {
                        nullDataView.setVisibility(View.GONE);
                    }
                    adapter.setDatas(sortList);
                    // 读完的时候回调下,刷新Tab
                    ZSZSingleton.getZSZSingleton().getStatusMessageListener().callbackStatusUpdata(unReadCountTemp);
                } else {
                    nullDataView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    // 排序
    private List<YYChatSessionInfo> sortYYChatSessionInfos(List<YYChatSessionInfo> reply) {
        if (reply.size() < 1) {
            return reply;
        }
        List<YYChatSessionInfo> tempChatSessionInfos = new ArrayList<YYChatSessionInfo>();
        int size = reply.size();
        int i = 0 ;
        boolean[] temp = new boolean[size];
        while (true) {
            long time = 0;
            int tempI = 0;
            for (int j = size-1; j >=0 ; j--) {
                if (temp[j]) {
                    continue;
                }
                if (reply.get(j).getChatroom() == null) {      //单聊
                    if (reply.get(j).getMessTime() >= time) {
                        time = reply.get(j).getMessTime();
                        tempI = j;
                    }
                } else {                                       //群聊
                    if (reply.get(j).getChatroom().getMsgLastTime() >= time) {
                        time = reply.get(j).getChatroom().getMsgLastTime();
                        tempI = j;
                    }
                }
            }
            temp[tempI] = true;
            tempChatSessionInfos.add(reply.get(tempI));
            if (i >= size-1) {
                break;
            }
            i++;
        }
        return tempChatSessionInfos;
    }

    // 设置显示未读消息
    private List<YYChatSessionInfo> setUnreadYYChatSessionInfos(List<YYChatSessionInfo> reply) {
        for (int i = 0; i < reply.size(); i++) {
            int unreadCount = 0;
            String latestChart = null;
            long time = 0;
            if (reply.get(i).getChatroom() == null) {
//                EMConversation conversation = EMClient.getInstance().chatManager().getConversation(reply.get(i).getPhoneNumber());
                EMConversation conversation = EMClient.getInstance().chatManager().getConversation(reply.get(i).getWp_other_info_id());
                if (conversation != null) {
                    unreadCount = conversation.getUnreadMsgCount();
                    EMMessage msg = conversation.getLastMessage();

                    if (msg != null && msg.getBody() != null) {
                        if (msg.getBody() instanceof EMTextMessageBody) {
                            latestChart = ((EMTextMessageBody) msg.getBody()).getMessage();
                        } else if (msg.getBody() instanceof EMLocationMessageBody) {
                            latestChart = ((EMLocationMessageBody) msg.getBody()).getAddress();
                        } else if (msg.getBody() instanceof EMImageMessageBody) {
                            latestChart = "[图片]";
                        } else if (msg.getBody() instanceof EMVideoMessageBody) {
                            latestChart = "[视频]";
                        } else if (msg.getBody() instanceof EMVoiceMessageBody) {
                            latestChart = "[语音]";
                        }
                        time = msg.getMsgTime();
                    }
                }
                reply.get(i).setUnreadCount(unreadCount);
                reply.get(i).setLatestChart(latestChart);
                reply.get(i).setMessTime(time);
                reply.get(i).setLastTime(setTime(time));
            } else {
                EMConversation conversation = EMClient.getInstance().chatManager().getConversation(reply.get(i).getChatroom().getRoom_id());
                if (conversation != null) {
                    unreadCount = conversation.getUnreadMsgCount();
                    EMMessage msg = conversation.getLastMessage();
                    if (msg != null && msg.getBody() != null) {
                        if (msg.getBody() instanceof EMTextMessageBody) {
                            latestChart = ((EMTextMessageBody) msg.getBody()).getMessage();
                        } else if (msg.getBody() instanceof EMLocationMessageBody) {
                            latestChart = ((EMLocationMessageBody) msg.getBody()).getAddress();
                        } else if (msg.getBody() instanceof EMImageMessageBody) {
                            latestChart = "[图片]";
                        } else if (msg.getBody() instanceof EMVideoMessageBody) {
                            latestChart = "[视频]";
                        } else if (msg.getBody() instanceof EMVoiceMessageBody) {
                            latestChart = "[语音]";
                        }
                        time = msg.getMsgTime();
                    }
                }
                reply.get(i).getChatroom().setUnReadMessageCount(unreadCount);
                reply.get(i).getChatroom().setLatestChart(latestChart);
                reply.get(i).getChatroom().setMsgLastTime(time);
                //reply.get(i).getChatroom().setMsgLastTimeString(QLDateUtils.getTimeWithFormat(new Date(time), "HH:mm"));
                if (time != 0) {
                    reply.get(i).getChatroom().setMsgLastTimeString(setTime(time));
                }
            }
        }
        return reply;
    }

    private String setTime(long time) {
        if (QLDateUtils.isToday(new Date(time))) {
            return QLDateUtils.getTimeWithFormat(new Date(time), "HH:mm");
        }
        return QLDateUtils.getTimeWithFormat(new Date(time), "MM-dd HH:mm");
    }

    private void getNotifyCount(){
        OrganizationController.getOrgNotifyUnreadCount(mContext, new Listener<Boolean, Integer>() {
            @Override
            public void onCallBack(Boolean aBoolean, Integer reply) {
                if (aBoolean){
                    QLConstant.orgNotifyUnreadCount = reply;
                    // 读完的时候回调下,刷新Tab
                    ZSZSingleton.getZSZSingleton().getStatusMessageListener().callbackStatusUpdata(-100);
                    notify_unReadCount.setText(reply+"");
                    if (reply>0){
                        notify_unReadCount.setVisibility(View.VISIBLE);
                    }else
                        notify_unReadCount.setVisibility(View.GONE);

                }
            }
        });
    };

    @Override
    public void onRefresh() {
        loadMessageList();
        // bgn_tv.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoadMore() {
    }

    private static final int kRefrashUI = 212;
    private Handler msgHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == kRefrashUI) {
                adapter.notifyDataSetChanged();
                ZSZSingleton.getZSZSingleton().getShowPointMessageListener().callbackPoint(true);
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.notify_layout:
                startActivity(new Intent(mContext, NotifyActivity.class));
                break;
        }
    }



    /**
     * add group to messagelist
     * @param room_id
     */
    private void addChatGroupToMessage(String room_id) {
        ChatRoomController.registerChatToMessageList(mContext, room_id, "", "1", new Listener<Integer, String>() {
            @Override
            public void onCallBack(Integer integer, String reply) {
                loadMessageList();
            }
        });
    }

    private void onNewMessage(YYChatSessionInfo info) {
        int unreadCount = 0;
        String latestChart = null;
        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(info.getPhoneNumber());
        if (conversation != null) {
            unreadCount = conversation.getUnreadMsgCount();
            EMMessage msg = conversation.getLastMessage();
            if (msg != null && msg.getBody() != null) {
                //if (msg.getType() == EMMessage.Type.TXT) {
                if (msg.getBody() instanceof EMTextMessageBody) {
                    //latestChart = ((TextMessageBody) msg.getBody()).getMessage();
                    latestChart = ((EMTextMessageBody) msg.getBody()).getMessage();
                } else if (msg.getBody() instanceof EMLocationMessageBody) {
                    latestChart = ((EMLocationMessageBody) msg.getBody()).getAddress();
                } else if (msg.getBody() instanceof EMImageMessageBody) {
                    latestChart = "[图片]";
                } else if (msg.getBody() instanceof EMVideoMessageBody) {
                    latestChart = "[视频]";
                } else if (msg.getBody() instanceof EMVoiceMessageBody) {
                    latestChart = "[语音]";
                }
            }
        }
        info.setUnreadCount(unreadCount);
        info.setLatestChart(latestChart);
        // Toast.makeText(getContext(), latestChart, Toast.LENGTH_SHORT).show();
    }

    // 判断对话列表是否有对方信息
    private void haveMessageList(final String phone, final EMMessage message) {
        UserManagerController.getChatList(getActivity(), new Listener<Integer, List<YYChatSessionInfo>>() {

            @Override
            public void onCallBack(Integer status, List<YYChatSessionInfo> reply) {
                if (reply == null || reply.size() < 1) {
                    boolean temp = true;
                    for (YYChatSessionInfo info : reply) {
                        if (info.getPhoneNumber().equals(phone)) {
                            temp = false;
                            break;
                        }
                    }
                    if (temp) {
                        addChart(phone, message);
                    } else {
                        msgHandler.sendEmptyMessage(kRefrashUI);
                    }
                }
            }
        });
    }
}
