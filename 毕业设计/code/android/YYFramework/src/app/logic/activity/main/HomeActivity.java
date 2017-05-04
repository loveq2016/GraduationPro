package app.logic.activity.main;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager.LayoutParams;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMLocationMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.EMVideoMessageBody;
import com.hyphenate.chat.EMVoiceMessageBody;
import com.hyphenate.chat.adapter.message.EMAMessage;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.ui.activity.ChatActivity;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.util.NetUtils;
import com.squareup.picasso.Picasso;

import org.QLConstant;
import org.ql.app.alert.AlertDialog;
import org.ql.utils.QLSingleton;
import org.ql.utils.QLSingleton.BackToLoginActListener;
import org.ql.utils.QLToastUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import app.config.DemoApplication;
import app.config.MyLifecycleHandler;
import app.config.http.HttpConfig;
import app.logic.activity.ActTitleHandler;
import app.logic.activity.TYBaseActivity;
import app.logic.activity.about.SettingActivity;
import app.logic.activity.card.AddCardActivity;
import app.logic.activity.friends.AddFriendsActivity;
import app.logic.activity.friends.FriendsListActivity2;
import app.logic.activity.live.CarouselImgInfo;
import app.logic.activity.org.ApplyAssociActivity;
import app.logic.activity.org.ApplyToJoinActivity;
import app.logic.activity.org.CreateOranizationActivity;
import app.logic.activity.search.SearchActivity;
import app.logic.activity.user.LoginActivity;
import app.logic.activity.user.PrepareLoginActivity;
import app.logic.activity.user.QRCodePersonal;
import app.logic.adapter.YYBaseListAdapter;
import app.logic.call.activity.CallReceiver;
import app.logic.call.activity.Constant;
import app.logic.controller.ChatRoomController;
import app.logic.controller.OrganizationController;
import app.logic.controller.UserManagerController;
import app.logic.pojo.ChatMessageInfo;
import app.logic.pojo.CountUnreadInfo;
import app.logic.pojo.FriendInfo;
import app.logic.pojo.OrgUnreadNumberInfo;
import app.logic.pojo.OrganizationInfo;
import app.logic.pojo.UserInfo;
import app.logic.pojo.YYChatRoomInfo;
import app.logic.pojo.YYChatSessionInfo;
import app.logic.singleton.YYInterface;
import app.logic.singleton.YYInterface.CancleFriendRequestListener;
import app.logic.singleton.YYInterface.FriendRequestListener;
import app.logic.singleton.YYSingleton;
import app.logic.singleton.ZSZSingleton;
import app.logic.singleton.ZSZSingleton.ShowPointMessageListener;
import app.logic.singleton.ZSZSingleton.StatusDownloadFileCompleteListener;
import app.logic.singleton.ZSZSingleton.StatusMessageListener;
import app.logic.view.web.WebBrowserActivity;
import app.utils.common.Listener;
import app.utils.helpers.BusinessCardHelper.OnScanPictureListener;
import app.utils.helpers.ChartHelper;
import app.utils.helpers.QRHelper;
import app.utils.helpers.SharepreferencesUtils;
import app.utils.helpers.SystemBuilderUtils;
import app.utils.helpers.YYUtils;
import app.view.DialogNewStyleController;
import app.view.YYListView;
import app.yy.geju.R;
import app.yy.geju.R.id;
import cn.jpush.android.api.JPushInterface;

//import app.utils.service.YYIMService;

/**
 * SiuJiYung create at 2016-6-2 下午4:15:38
 */

public class HomeActivity extends TYBaseActivity implements EMMessageListener , OnClickListener, OnScanPictureListener, OnTouchListener {

    public static final String UPDATANOTICE = "UPDATANOTICE";
    public static final String UPDATA_ORG = "UPDATA_ORG";
    public static final String UPDATA_ORG_POINT = "UPDATA_ORG_POINT";
    private FragmentTabHost mTabHost = null;
    private ActTitleHandler handler = new ActTitleHandler();
    private PopupWindow popupWindow;
    private AlertDialog alertDialog;
    public static boolean haveFriendsRequest = false,haveOrgRequest = false ;
    public static boolean isHomeActivity  = true  , isChatActivity = false , isStartLiveActivity = false , isLiveDetailsActivity = false ;
    private View rightView;
    private ImageButton addMenu_Ibt;
    private ImageButton aboutMe_Ibt, searchButton;
    private View liveView,messageView, noticeIndicator, contactIndicator,myIndicator;
    public static Activity act  ;
    public static boolean isOpeningChat = false ;
    private CallReceiver callReceiver ;


    public interface ShowUnreadNoticeListener {
        void onCallBack(int count);
        void onCallBackCountList(List<CountUnreadInfo> list);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setAbsHandler(handler);
        act = this ;
        setContentView(R.layout.activity_home_xiehui);
        initActTitleView();
        FragmentManager fManager = getSupportFragmentManager();
        setTitle("首页");
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        // mTabHost.setup(this, getSupportFragmentManager());
        mTabHost.setup(this, fManager, android.R.id.tabcontent);

        // // 添加tab名称和图标
        liveView = getIndicatorView("首页", R.drawable.selector_tab_live);
        mTabHost.addTab(mTabHost.newTabSpec("liveList").setIndicator(liveView), LiveListFragment.class, null);

        messageView = getIndicatorView("消息", R.drawable.selector_tab_msg);
        mTabHost.addTab(mTabHost.newTabSpec("messageList").setIndicator(messageView), MessageListFragment.class, null);

        contactIndicator = getIndicatorView("联系人", R.drawable.selector_tab_contact);
        mTabHost.addTab(mTabHost.newTabSpec("contactsList").setIndicator(contactIndicator), ContactsFragment2.class, null);

        noticeIndicator = getIndicatorView("公告", R.drawable.selector_tab_notice);
        mTabHost.addTab(mTabHost.newTabSpec("notice").setIndicator(noticeIndicator), MyOrgNoticeListFragment.class, null);

        myIndicator = getIndicatorView("我的", R.drawable.selector_tab_my);
        mTabHost.addTab(mTabHost.newTabSpec("UserCenter").setIndicator(myIndicator), UserCenterFragment.class, null);



        mTabHost.setCurrentTab(0); //设置当前选中的状态
        if (aboutMe_Ibt != null) {
            aboutMe_Ibt.setVisibility(View.GONE);
        }
        searchButton.setVisibility(View.VISIBLE);
        mTabHost.setOnTabChangedListener(new OnTabChangeListener() {

            @Override
            public void onTabChanged(String tabId) {
                if (tabId.equals("liveList")) {
                    HomeActivity.this.setTitle("首页");
                    addMenu_Ibt.setVisibility(View.VISIBLE);
                    searchButton.setVisibility(View.VISIBLE);
                    aboutMe_Ibt.setVisibility(View.GONE);
                }
                if (tabId.equals("messageList")) {
                    // HomeActivity.this.setTitle("格局");
                    // handler.getCenterLayout().findViewById(R.id.centerIv).setVisibility(View.GONE);
                     //getFriendsList();
                    // View view = rightView.findViewById(R.id.title_point);
                    // getFriendsListToshowPoint(view);
                    HomeActivity.this.setTitle("消息");
                    addMenu_Ibt.setVisibility(View.VISIBLE);
                    aboutMe_Ibt.setVisibility(View.GONE);
//                    if (haveFriendsRequest) {
//                        rightView.findViewById(R.id.title_point).setVisibility(View.VISIBLE);
//                    } else {
//                        rightView.findViewById(R.id.title_point).setVisibility(View.GONE);
//                    }
                    searchButton.setVisibility(View.VISIBLE);
                    return;
                }
                if (tabId.equals("contactsList")) {
                    // HomeActivity.this.setTitle("通讯录");
                    // handler.getCenterLayout().findViewById(R.id.centerIv).setVisibility(View.GONE);
                    // View view = rightView.findViewById(R.id.title_point);
                    // getFriendsListToshowPoint(view);
                    HomeActivity.this.setTitle("联系人");
                    addMenu_Ibt.setVisibility(View.VISIBLE);
                    aboutMe_Ibt.setVisibility(View.GONE);
//                    if (haveFriendsRequest) {
//                        rightView.findViewById(R.id.title_point).setVisibility(View.VISIBLE);
//                    } else {
//                        rightView.findViewById(R.id.title_point).setVisibility(View.GONE);
//                    }
                    searchButton.setVisibility(View.VISIBLE);
                    return;
                }
                if (tabId.equals("notice")) {

                    HomeActivity.this.setTitle("公告");
                    // handler.getCenterLayout().findViewById(R.id.centerIv).setVisibility(View.VISIBLE);
                    addMenu_Ibt.setVisibility(View.VISIBLE);
                    aboutMe_Ibt.setVisibility(View.GONE);

                    rightView.findViewById(R.id.title_point).setVisibility(View.GONE);
                    searchButton.setVisibility(View.VISIBLE);
                    return;
                }
                if (tabId.equals("UserCenter")) {
                    HomeActivity.this.setTitle("我");
                    // HomeActivity.this.setTitle("我");
                    // handler.getCenterLayout().findViewById(R.id.centerIv).setVisibility(View.GONE);
                    addMenu_Ibt.setVisibility(View.VISIBLE);
                    aboutMe_Ibt.setVisibility(View.GONE);
                    rightView.findViewById(R.id.title_point).setVisibility(View.GONE);
                    searchButton.setVisibility(View.VISIBLE);
                    return;
                }
            }
        });

        EMClient.getInstance().addConnectionListener(new MyConnectionListenet());

        // getFriendsList();// popupwindow显示红点
        showPointMessage();// 显示未读消息

        updataFriendRequest();// 回调好友请求，显示好友请求数量

        updataOrgRequest();// 回调好友请求，显示好友请求数量

        updataApp();// 应用更新

        backToLogin();// 回到登录页面

        // getMyOrgListId();
        //getAllOrgUnreanNTListId();

        // 广播的使用，公告
        IntentFilter filter = new IntentFilter();
        filter.addAction(UPDATANOTICE);
        filter.addAction(UPDATA_ORG);
        registerReceiver(mBroadcastReceiver, filter);
        ChatMessageInfo info = (ChatMessageInfo) getIntent().getSerializableExtra("info");
        if (info != null) {
            Intent intent = new Intent(this, ChatActivity.class);
			//intent.putExtra(DemoHXSDKHelper.FROM_NOTIFICATION, info.getFromActivity());
            intent.putExtra("chatType", info.getChatType());
            intent.putExtra("userId", info.getUserId());
			//intent.putExtra(ChatActivity.TARGET_MEMBER_ID, info.getMessageTo());
            startActivity(intent);
        }
        setCallLister() ;//注册音视频监听器
    }

    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case UPDATANOTICE:
                    // QLToastUtils.showToast(HomeActivity.this, UPDATANOTICE);
                    // getMyOrgListId();
                    getAllOrgUnreanNTListId();
                    break;
                case UPDATA_ORG:
                    getAllOrgUnreadDatas();
                    break;
                default:
                    break;
            }

        }
    };

    // -------------------------------初始化标栏-----------------------------------------

    private void initActTitleView() {
        rightView = LayoutInflater.from(HomeActivity.this).inflate(R.layout.homeactivity_rightlayout, null);
        handler.addRightView(rightView, true);

        addMenu_Ibt = (ImageButton) rightView.findViewById(R.id.imageButton02);
        addMenu_Ibt.setOnClickListener(this);

        aboutMe_Ibt = (ImageButton) rightView.findViewById(R.id.aboutMe_ib);
        searchButton = (ImageButton) rightView.findViewById(R.id.open_search_act);

        searchButton.setOnClickListener(this);
        aboutMe_Ibt.setOnClickListener(this);
        handler.getCenterLayout().findViewById(android.R.id.title).setOnClickListener(this);
        handler.replaseLeftLayout(this, true);
        handler.getLeftLayout().findViewById(R.id.left_iv).setVisibility(View.GONE);
        ((TextView) handler.getLeftLayout().findViewById(R.id.left_tv)).setText("");
        setTitle("格局");
    }

    @Override
    protected void onPause() {
        super.onPause();
        // EMChatManager.getInstance().unregisterEventListener(this);
        // DemoHXSDKHelper sdkHelper = (DemoHXSDKHelper)
        // DemoHXSDKHelper.getInstance();
        // // 把此activity 从foreground activity 列表里移除
        // sdkHelper.popActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EMClient.getInstance().chatManager().addMessageListener(this);
        isHomeActivity = true ;
        //获取朋友请求列表
        getListFriendMessage();
        //获取公告未读数
        getAllOrgUnreanNTListId();

        //获取协会未处理申请列表
        getAllOrgUnreadDatas();
        //
        // DemoHXSDKHelper sdkHelper = (DemoHXSDKHelper)
        // DemoHXSDKHelper.getInstance();
        // sdkHelper.pushActivity(this);
        // EMChatManager.getInstance().registerEventListener(
        // this,
        // new EMNotifierEvent.Event[] {
        // EMNotifierEvent.Event.EventNewMessage});
    }

    @Override
    protected void onStop() {
        super.onStop();
//        EMClient.getInstance().chatManager().addMessageListener(this); // onPause()还要添加 ？ 因为跳转到其他界面还需要接收信息，发送通知
        isHomeActivity = false ;  //HomeActivity 失去焦点
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBroadcastReceiver !=null)
            unregisterReceiver(mBroadcastReceiver);
        if (callReceiver !=null)
            unregisterReceiver(callReceiver);
    }

    // 更新消息提醒point
    private void showPointMessage() {
        // 环信接收回调
        ZSZSingleton.getZSZSingleton().setShowPointMessageListener(new ShowPointMessageListener() {
            @Override
            public void callbackPoint(boolean status) {
                if (status) {
                    if (messageView != null) {
                        UserManagerController.getChatList(HomeActivity.this, new Listener<Integer, List<YYChatSessionInfo>>() {
                            @Override
                            public void onCallBack(Integer status, List<YYChatSessionInfo> reply) {
                                int unReadCount = 0;
                                if (reply != null && reply.size() > 0) {
                                    for (YYChatSessionInfo yyChatSessionInfo : reply) {
                                        // 过滤掉和自己的聊天记录
                                        if (yyChatSessionInfo.getWp_member_info_id().equals(yyChatSessionInfo.getWp_other_info_id())) {
                                            continue;
                                        }
                                        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(yyChatSessionInfo.getPhoneNumber());
                                        if (conversation != null) {
                                        }
                                        unReadCount += conversation.getUnreadMsgCount();
                                    }
                                    // 做好统计未读的条数
                                    TextView tv = ((TextView) messageView.findViewById(R.id.tab_item_count));
                                    if (unReadCount > 0) {
                                        String count;
                                        if (unReadCount > 99) {
                                            count = 99 + "+";
                                        } else {
                                            count = unReadCount + "";
                                        }
                                        tv.setVisibility(View.VISIBLE);
                                        tv.setText(count);
                                    } else {
                                        tv.setVisibility(View.GONE);
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
        // 消息列表刷新，回调显示未读的数量

        ZSZSingleton.getZSZSingleton().setStatusMessageListener(new StatusMessageListener() {
            @Override
            public void callbackStatusUpdata(int unreadCount) {
                TextView tv = ((TextView) messageView.findViewById(R.id.tab_item_count));

                if (unreadCount == -100){
                    if (!TextUtils.isEmpty(tv.getText().toString())){
                        unreadCount = Integer.parseInt(tv.getText().toString())+QLConstant.orgNotifyUnreadCount;
                    }else
                        unreadCount = QLConstant.orgNotifyUnreadCount;
                }else
                    unreadCount += QLConstant.orgNotifyUnreadCount;
                if (unreadCount == 0) {
                    tv.setVisibility(View.GONE);
                    tv.setText("0");
                    return;
                }
                tv.setVisibility(View.VISIBLE);
                String count;
                if (unreadCount > 99) {
                    count = 99 + "+";
                } else {
                    count = unreadCount + "";
                }
                tv.setText(count);
            }
        });
    }

    private View getIndicatorView(String name, int drawableRes) {
        View v = getLayoutInflater().inflate(R.layout.tab_item, null);
        TextView tv = (TextView) v.findViewById(R.id.tab_item_title);
        Drawable drawable = getResources().getDrawable(drawableRes);
        int _h = YYUtils.dp2px((52 - 25), this);
        drawable.setBounds(0, 0, _h, _h);
        tv.setCompoundDrawables(null, drawable, null, null);
        tv.setText(name);
        return v;
    }

    // -------------------------------popupWindom-----------------------------------------

    private void showMenu(View view) {
        if (popupWindow == null) {
            View menuView = LayoutInflater.from(this).inflate(R.layout.popmenu_add2, null);
            popupWindow = new PopupWindow(menuView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            menuView.findViewById(R.id.transmit_layout).setOnClickListener(this);
            menuView.findViewById(R.id.add_friends_ly).setOnClickListener(this);
            menuView.findViewById(R.id.joid_chatroom_ly).setOnClickListener(this);
            menuView.findViewById(R.id.add_card_ly).setOnClickListener(this);
            menuView.findViewById(R.id.join_org_ly).setOnClickListener(this);
            // popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.nav_more_list));
            popupWindow.setOutsideTouchable(true);
            menuView.setOnTouchListener(this);
        }
        if (haveFriendsRequest) {
            popupWindow.getContentView().findViewById(R.id.add_friends_point_view).setVisibility(View.VISIBLE);
        } else {
            popupWindow.getContentView().findViewById(R.id.add_friends_point_view).setVisibility(View.GONE);
        }
        if (popupWindow.isShowing()) {
            return;
        }

        popupWindow.setFocusable(true);
        popupWindow.update();
        popupWindow.showAsDropDown(view, 0, 0);
        // popupWindow.showAtLocation(findViewById(R.id.lll), Gravity.CENTER, 0,
        // 0);
        // int[] viewLocation = new int[2];
        // view.getLocationOnScreen(viewLocation);
        // popupWindow.showAtLocation(view, Gravity.TOP, 0, 0);
        ZSZSingleton.getZSZSingleton().backgroundAlpha(this, 0.5f);
        popupWindow.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                // TODO Auto-generated method stub
                ZSZSingleton.getZSZSingleton().backgroundAlpha(HomeActivity.this, 1f);
            }
        });
    }

    // --------------------------------------------------------------------------------
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // case android.R.id.button2:
            // // 右上角按钮菜单
            // showMenu(v);
            // break;
            case R.id.open_search_act:
                startActivity(new Intent(HomeActivity.this, SearchActivity.class));
                break;
            case android.R.id.title:
                break;
            case R.id.aboutMe_ib:
                startActivity(new Intent(HomeActivity.this, SettingActivity.class));
                break;
            case R.id.imageButton02:
                showMenu(v);
                break;
            case R.id.transmit_layout:
                // 扫一扫
                QRHelper qrHelper = new QRHelper();
                qrHelper.setOnScanResultListener(new Listener<Void, String>() {
                    @Override
                    public void onCallBack(Void status, String reply) {
                        handleQRString(reply);
                    }
                });
                qrHelper.scanQRCode(HomeActivity.this);
                popupWindow.dismiss();
                break;
            case R.id.add_friends_ly:
                // 添加好友
                Intent intent = new Intent();
                intent.setClass(HomeActivity.this, AddFriendsActivity.class);
                startActivity(intent);
                YYSingleton.getInstance().getCancleFriendRequestListener().onCallBack();
                popupWindow.dismiss();
                break;
            case R.id.joid_chatroom_ly:
                // 加入群聊
			    //Intent intent2 = new Intent();
			    //intent2.setClass(this, FriendsListActivity.class);
			    //intent2.putExtra(FriendsListActivity.kTITLE, "发起群聊");
			    //intent2.putExtra(FriendsListActivity.kSELECTED_ITEM_MODEL, true);
			    //startActivityForResult(intent2, FriendsListActivity.kSELECT_ITEMS);
                Intent intentFriends = new Intent();
                intentFriends.setClass(HomeActivity.this, FriendsListActivity2.class);
                intentFriends.putExtra(FriendsListActivity2.kTITLE, "发起群聊");
                intentFriends.putExtra(FriendsListActivity2.kSELECTED_ITEM_MODEL, true);
                intentFriends.putExtra(FriendsListActivity2.ADD, true );
                startActivityForResult(intentFriends, FriendsListActivity2.kSELECT_ITEMS);
                popupWindow.dismiss();
                break;
            case R.id.add_card_ly:
                // 加入组织
                startActivity(new Intent(HomeActivity.this, ApplyAssociActivity.class));
                popupWindow.dismiss();
                break;
            case R.id.join_org_ly:
                // 创建组织
                // TODO
                Intent joinOrgIntent = new Intent();
                // joinOrgIntent.setClass(HomeActivity.this,
                // JoinOrganizationActivity.class);
                joinOrgIntent.putExtra(CreateOranizationActivity.CREATE_ORG, true);
                joinOrgIntent.setClass(HomeActivity.this, CreateOranizationActivity.class);
                startActivity(joinOrgIntent);
                popupWindow.dismiss();
                break;
            default:
                break;
        }
    }

    private void handleQRString(String txt) {
        if (txt != null && !TextUtils.isEmpty(txt)) {
            if (YYUtils.isURL(txt)) {
                // URL
                Intent intent = new Intent();
                intent.setClass(HomeActivity.this, WebBrowserActivity.class);
                intent.putExtra(WebBrowserActivity.KBROWSER_HOME_URL, txt);
                startActivity(intent);
            } else if (txt.contains("cr_id")) {
                // 加入群聊
                try {
                    String[] temps = txt.split(":");
                    ChartHelper.joinChatRoomUserId(HomeActivity.this, temps[1]);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            } else if (txt.contains("member_info_id")) {
                if (txt != null) {
                    Gson gson = new Gson();
                    //UserInfo _uInfo = gson.fromJson(txt, UserInfo.class);
                    QRCodePersonal personal = gson.fromJson(txt, QRCodePersonal.class);
                    if( personal == null ){
                        QLToastUtils.showToast(this , "扫描失败，请重新扫描");
                        return;
                    }
                    showAddFriendView(personal);
                }

            } else if (txt.contains("org_logo_url")) {
                Gson gson = new Gson();
                OrganizationInfo info = gson.fromJson(txt, OrganizationInfo.class);
                //QRCodePersonal organizationInfo = gson.fromJson(string, QRCodePersonal.class);
                startActivity(new Intent(HomeActivity.this, ApplyToJoinActivity.class).putExtra(ApplyToJoinActivity.ORG_ID, info.getOrg_id()));
                //showJoinOrgDialog(info);
            }
        }
    }

    /**
     * @param info
     */
    private void showJoinOrgDialog(final OrganizationInfo info) {
        final View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_friend_view, null);
        final DialogNewStyleController dialog = new DialogNewStyleController(this, view);
        TextView titleTv = (TextView) view.findViewById(R.id.dialog_title_tv);
        ImageView headIv = (ImageView) view.findViewById(R.id.dialog_head_iv);
        TextView nameTv = (TextView) view.findViewById(R.id.dialog_user_name_tv);
        final EditText tagEdt = (EditText) view.findViewById(R.id.dialog_tag_edt);
        Button sendBtn = (Button) view.findViewById(R.id.dialog_cancel_btn);
        Button cancel = (Button) view.findViewById(R.id.dialog_true_btn);
        titleTv.setText("加入组织");
        sendBtn.setText("加入");
        cancel.setText("取消");
        sendBtn.setTextColor(getResources().getColor(R.color.new_app_color));
        cancel.setTextColor(Color.parseColor("#ff0000"));
        Picasso.with(this).load(HttpConfig.getUrl(info.getOrg_logo_url())).placeholder(R.drawable.default_user_icon).error(R.drawable.default_user_icon).into(headIv);
        nameTv.setText(info.getOrg_name());
        sendBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = tagEdt.getText().toString();
                OrganizationController.joinOrganization(HomeActivity.this, info.getOrg_id(), msg, new Listener<Boolean, String>() {
                    @Override
                    public void onCallBack(Boolean status, String reply) {
                        if (status) {
                            QLToastUtils.showToast(HomeActivity.this, "申请成功！等待管理员的审核。。。");
                            return;
                        }
                        QLToastUtils.showToast(HomeActivity.this, reply);
                    }
                });
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void onAnalyzing() {
        setWaitingDialogText("正在分析名片...请稍后");
        showWaitDialog();
    }

    @Override
    public void onScanResult(String result, String imgPath) {
        dismissWaitDialog();
        if (result != null && imgPath != null) {
            Intent intent = new Intent();
            intent.setClass(this, AddCardActivity.class);
            intent.putExtra(AddCardActivity.kCARD_IMAGE_PATH, imgPath);
            intent.putExtra(AddCardActivity.kCARD_SCAN_INFO, result);
            startActivity(intent);
        } else if (result != null) {

        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
        return true;
    }

    private long lastKeyTime;

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
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
                    if(_items != null && _items.size()>0){
                        showCreateChatRoomDialog(_items);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 显示添加好友
    private void showAddFriendView(final QRCodePersonal personal ) {
        final View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_friend_view, null);
        final DialogNewStyleController dialog = new DialogNewStyleController(this, view);
        TextView titleTv = (TextView) view.findViewById(R.id.dialog_title_tv);
        ImageView headIv = (ImageView) view.findViewById(R.id.dialog_head_iv);
        TextView nameTv = (TextView) view.findViewById(R.id.dialog_user_name_tv);
        final EditText tagEdt = (EditText) view.findViewById(R.id.dialog_tag_edt);
        Button sendBtn = (Button) view.findViewById(R.id.dialog_cancel_btn);
        Button cancel = (Button) view.findViewById(R.id.dialog_true_btn);
        titleTv.setText("添加好友");
        sendBtn.setText("发送");
        cancel.setText("取消");
        sendBtn.setTextColor(getResources().getColor(R.color.new_app_color));
        cancel.setTextColor(Color.parseColor("#ff0000"));
        Picasso.with(this).load(HttpConfig.getUrl(personal.getPicture_url())).placeholder(R.drawable.default_user_icon).error(R.drawable.default_user_icon).into(headIv);
        nameTv.setText(personal.getNickName());
        sendBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//                requestFriends(personal.getPhone(), tagEdt.getText().toString());
                requestFriendsById(personal.getWp_member_info_id(), tagEdt.getText().toString());
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

//    private void requestFriends(String phone, String msg) {
//        UserManagerController.addFriends(this, phone, msg, new Listener<Integer, String>() {
//            @Override
//            public void onCallBack(Integer status, String reply) {
//                if (status == -1) {
//                    String msg = reply == null ? "操作失败" : reply;
//                    QLToastUtils.showToast(HomeActivity.this, msg);
//                    return;
//                }
//                QLToastUtils.showToast(HomeActivity.this, "请求已发送");
//            }
//        });
//    }
    private void requestFriendsById(String friend_id, String msg) {
        UserManagerController.addFriendsById(this, friend_id, msg, new Listener<Integer, String>() {
            @Override
            public void onCallBack(Integer status, String reply) {
                if (status == -1) {
                    String msg = reply == null ? "操作失败" : reply;
                    QLToastUtils.showToast(HomeActivity.this, msg);
                    return;
                }
                QLToastUtils.showToast(HomeActivity.this, "请求已发送");
            }
        });
    }


    private void showCreateChatRoomDialog(final List<FriendInfo> members) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_new_edit_view, null);
        final DialogNewStyleController dialog = new DialogNewStyleController(this, contentView);
        TextView titleTv = (TextView) contentView.findViewById(id.dialog_title_tv);
        final EditText contentEdt = (EditText) contentView.findViewById(R.id.dialog_content_edt);
        Button createBtn = (Button) contentView.findViewById(R.id.dialog_cancel_btn);
        Button cancelBtn = (Button) contentView.findViewById(R.id.dialog_true_btn);
        //限制聊天室的长度（8为数）
        contentEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if( s.length()>8){
                    s = s.subSequence(0 , 8);
                    contentEdt.setText(s);
                    contentEdt.setSelection(8);  //设置光标的位置
                    //隐藏键盘
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(contentEdt.getWindowToken(),0);
                    QLToastUtils.showToast( HomeActivity.this , "长度不能超过8位");
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        titleTv.setText("创建群聊");
        createBtn.setText("创建");
        cancelBtn.setText("取消");
        contentEdt.setHint("聊天室名称");
        createBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String _txt = contentEdt.getText().toString();
                if (TextUtils.isEmpty(_txt)) {
                    QLToastUtils.showToast(HomeActivity.this, "名称不能为空");
                    return;
                }else{
                    createChatRoom( members, _txt ); //创建聊天室
                }
                if(dialog !=null && dialog.isShowing()) dialog.dismiss();
            }
        });
        cancelBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialog !=null && dialog.isShowing()) dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void createChatRoom(final List<FriendInfo> members, String name) {
        showWaitDialog();
        YYChatRoomInfo info = new YYChatRoomInfo();
        UserInfo userInfo = UserManagerController.getCurrUserInfo();
        info.setCr_creatoName( userInfo.getNickName() );
        //String a = userInfo.getNickName();
        //System.out.println(" a = "+ a );
        info.setCr_creatorId(userInfo.getWp_member_info_id());
        info.setCr_name(name);
        //System.out.println(" name = "+ name );
        info.setCr_type("1");

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
            //System.out.println( info1.getPhone() + " "+info1.getNickName()+" "+info1.getWp_friends_info_id()+" "+ info1.getFriend_name()+ " "+ info1.getSex()+" "+ " "+info1.isRequest_accept()+" "+info1.getPicture_url() );
        }
        info.setCr_memberList(cr_memberList);

        ChatRoomController.createChatRoom(this, info, 0 ,new Listener<Integer, YYChatRoomInfo>() {
            @Override
            public void onCallBack(Integer status, final YYChatRoomInfo reply) {
                dismissWaitDialog();
                if ( status == 1 && reply!= null ) {
                    QLToastUtils.showToast(HomeActivity.this, "创建成功");
                    final String cr_id = reply.getCr_id();
                    final String room_id = reply.getRoom_id();
                    ChartHelper.openChatRoom( HomeActivity.this , room_id , cr_id , true );
                    return;
                }
                QLToastUtils.showToast(HomeActivity.this, "创建失败");
            }
        });
    }

    //

    @Override
    public void onTouchLeft2Right() {
    }

    // 保存显示，防止错乱
    private boolean[] statusRead;

    /**
     * 回调极光推送，，联系人tab请求显示数量
     */
    private void updataFriendRequest() {
        // 显示
        YYSingleton.getInstance().setFriendRequestListener(new FriendRequestListener() {
            @Override
            public void onCallBack(int count) {
                TextView tvTextView = (TextView) contactIndicator.findViewById(R.id.tab_item_count);
                if (count == 0) {
                    tvTextView.setVisibility(View.GONE);
                    //rightView.findViewById(R.id.title_point).setVisibility(View.INVISIBLE);
                    haveFriendsRequest = false;
                    return;
                }
                tvTextView.setText(String.valueOf(count));
                tvTextView.setVisibility(View.VISIBLE);
                if (YYSingleton.getInstance().getOnHasFriendsStatusPoint() != null) {
                    YYSingleton.getInstance().getOnHasFriendsStatusPoint().onCallBack(true);
                }
                haveFriendsRequest = true;

            }
        });
        // 取消显示
        YYSingleton.getInstance().setCancleFriendRequestListener(new CancleFriendRequestListener() {
            @Override
            public void onCallBack() {
                TextView tvTextView = (TextView) contactIndicator.findViewById(R.id.tab_item_count);
                tvTextView.setVisibility(View.GONE);
                //rightView.findViewById(R.id.title_point).setVisibility(View.INVISIBLE);
                haveFriendsRequest = false;
                if (YYSingleton.getInstance().getOnHasFriendsStatusPoint() != null) {
                    YYSingleton.getInstance().getOnHasFriendsStatusPoint().onCallBack(false);
                }
            }
        });
    }

    /**
     * 回调极光推送，，我的tab请求显示
     */
    private void updataOrgRequest() {
        // 显示
        YYSingleton.getInstance().setOrgRequestListener(new YYInterface.OrgRequestListener() {
            @Override
            public void onCallBack(int count) {
                TextView tvTextView = (TextView) myIndicator.findViewById(R.id.tab_item_count);
                tvTextView.getLayoutParams().width = 20;
                tvTextView.getLayoutParams().height = 20;


                if (count == 0) {
                    String versionName = SystemBuilderUtils.getInstance().getAppVersionName(HomeActivity.this);//当前应用的版本名称
                    versionName = versionName.replace(".","0");       //将 “.” 换成 “0” （ 以后有三个 . 这个也适合判断 ）
                    if (!TextUtils.isEmpty(QLConstant.newVisionName) && Long.parseLong(QLConstant.newVisionName) > Long.parseLong(versionName)){
                        tvTextView.setVisibility(View.VISIBLE);
                    }else
                        tvTextView.setVisibility(View.GONE);
                    haveOrgRequest = false;
                    //rightView.findViewById(R.id.title_point).setVisibility(View.INVISIBLE);
                    return;
                }
//                tvTextView.setText(String.valueOf(count));
                tvTextView.setVisibility(View.VISIBLE);
                haveOrgRequest = true;
            }
        });
    }

    /**
     * 回调最新app下载完成后
     */
    private void updataApp() {
        ZSZSingleton.getZSZSingleton().setStatusDownloadFileCompleteListener(new StatusDownloadFileCompleteListener() {
            @Override
            public void onCallBack(String url) {
                if (url == null || TextUtils.isEmpty(url)) {
                    return;
                }
                if (ZSZSingleton.getZSZSingleton().getHaveComplete() > 0) {  //作用是什么？
                    return;
                }
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(new File(url)), "application/vnd.android.package-archive");
                startActivity(intent);
                ZSZSingleton.getZSZSingleton().setHaveComplete(1);
            }
        });
    }

    private void backToLogin() {
        QLSingleton.getInstance().setBackToLoginActListener(new BackToLoginActListener() {
            @Override
            public void onCallBack() {
                anotherLogin();
            }
        });
    }

    /**
     * 异地登录回到登录界面
     */
    private void anotherLogin() {

        new Thread(new Runnable() {
            public void run() {
                EMClient.getInstance().logout(true);       //退出环信
            }
        }).start();

        SharepreferencesUtils utils = new SharepreferencesUtils( this );
        utils.setNeedLogin( true );
        Intent intent = new Intent();
        intent.setClass(HomeActivity.this, PrepareLoginActivity.class);
        intent.putExtra("token", "token");
        intent.putExtra("message", "该用户已经在别的手机登录");
        JPushInterface.setAlias(this, "" , null); //设置极光推送的别名（设为""）
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    /**
     * 获取所有组织公告未读数
     */
    private void getAllOrgUnreanNTListId() {
        OrganizationController.getOrgUnreadNumber(this, new Listener<Integer, List<OrgUnreadNumberInfo>>() {
            @Override
            public void onCallBack(Integer status, List<OrgUnreadNumberInfo> reply) {
                if (reply != null && reply.size() > 0) {
                    statusRead = new boolean[reply.size()];
                    int orgCount = 0;
                    for (int i = 0; i < reply.size(); i++) {
                        orgCount += reply.get(i).getCount();
                        if (reply.get(i).getCount() > 0) {
                            statusRead[i] = true;
                        }
                    }
                    TextView textView = (TextView) noticeIndicator.findViewById(R.id.tab_item_count);
                    String count;
                    if (orgCount > 99) {
                        count = 99 + "+";
                    } else {
                        count = orgCount + "";
                    }
                    textView.setText(count);

                    if (orgCount == 0) {
                        textView.setVisibility(View.GONE);
                        return;
                    }
                    textView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * 获取所有组织申请成员未处理数
     */
    private void getAllOrgUnreadDatas(){
        OrganizationController.getOrgJoinRequest(this, new Listener<Integer, List<OrgUnreadNumberInfo>>() {
            @Override
            public void onCallBack(Integer status, List<OrgUnreadNumberInfo> reply) {
                sendBroadcast(new Intent(HomeActivity.UPDATA_ORG_POINT));
                if (reply !=null && reply.size()>0){
                    YYSingleton.getInstance().setOrgUnreadDatas(reply);
                    YYSingleton.getInstance().getOrgRequestListener().onCallBack(reply.size());
                    return;
                }

                if (status>0){
                    YYSingleton.getInstance().setOrgUnreadDatas(new ArrayList<OrgUnreadNumberInfo>());
                    YYSingleton.getInstance().getOrgRequestListener().onCallBack(0);
                }


            }
        });
    }

    /**
     * 监听环信连接状态
     */
    private class MyConnectionListenet implements EMConnectionListener {
        @Override
        public void onConnected() {
        }
        @Override
        public void onDisconnected(final int error) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (error == EMError.USER_REMOVED) {
                        // 显示帐号已经被移除
                    } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                        // 显示帐号在其他设备登录
                        anotherLogin();
                    } else {
                        if (NetUtils.hasNetwork(HomeActivity.this)) {
                            //QLToastUtils.showToast(HomeActivity.this, "聊天服务器连接失败");
                        } else {
                            //当前网络不可用，请检查网络设置
                        }
                    }
                }
            });
        }
    }


    //******************  消息监听  *******************//

    private ArrayList<YYChatSessionInfo> sessionList = new ArrayList<YYChatSessionInfo>();
    private YYListView listView;
    private YYBaseListAdapter<YYChatSessionInfo> adapter;
//    private View nullDataView;

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
            if( !MyLifecycleHandler.isApplicationInForeground() || (!HomeActivity.isHomeActivity && !HomeActivity.isChatActivity &&
                    !HomeActivity.isStartLiveActivity && !HomeActivity.isLiveDetailsActivity )){  //应用程序在后台
                EaseUI.getInstance().getNotifier().vibrateAndPlayTone(message);
                EaseUI.getInstance().getNotifier().onNewMsg(message);
            }
            if (message.getChatType() == EMMessage.ChatType.Chat) {
                addChart(message.getFrom(), message);
            } else {
                addChatGroupToMessage(message.getTo());
            }
        }
    }

    // 获取用户信息，注册回话
    private void addChart(String member_id, final EMMessage msg) {
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

        UserManagerController.addChatWith( HomeActivity.this , member_id, latestChart, new Listener<Integer, String>() {
            @Override
            public void onCallBack(Integer status, String reply) {
                loadMessageList();
            }
        });

//        UserManagerController.getPhoneMemerInfo(this, phone, new Listener<Integer, List<UserInfo>>() {
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
//                    UserManagerController.addChatWith( HomeActivity.this , reply.get(0).getWp_member_info_id(), latestChart, new Listener<Integer, String>() {
//                        @Override
//                        public void onCallBack(Integer status, String reply) {
//                            loadMessageList();
//                        }
//                    });
//                }
//            }
//        });
    }

    /**
     * add group to messagelist
     * @param room_id
     */
    private void addChatGroupToMessage(String room_id) {
        ChatRoomController.registerChatToMessageList(this, room_id, "", "1", new Listener<Integer, String>() {
            @Override
            public void onCallBack(Integer integer, String reply) {
                loadMessageList();
            }
        });
    }

    private void loadMessageList() {
        UserManagerController.getChatList(this, new Listener<Integer, List<YYChatSessionInfo>>() {
            @Override
            public void onCallBack(Integer status, List<YYChatSessionInfo> reply) {
                ArrayList<YYChatSessionInfo> _tmpList = new ArrayList<YYChatSessionInfo>();
                int unReadCountTemp = 0;
                if (reply != null && reply.size() > 0) {
                    for (YYChatSessionInfo yyChatSessionInfo : reply) {
                        if (yyChatSessionInfo.getChatroom() == null) { //单聊
                            // 过滤掉和自己的聊天记录
                            if (yyChatSessionInfo.getWp_member_info_id().equals(yyChatSessionInfo.getWp_other_info_id())) {
                                continue;
                            }
                            // sessionList.add(yyChatSessionInfo);
                            // 计算未读
                            EMConversation conversation = EMClient.getInstance().chatManager().getConversation(yyChatSessionInfo.getPhoneNumber());
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
                    // 读完的时候回调下,刷新Tab
                    ZSZSingleton.getZSZSingleton().getStatusMessageListener().callbackStatusUpdata(unReadCountTemp);
                } else {

                }
            }
        });
    }

    /**
     * 获取朋友列表
     * <p>
     * getListFriendMessageAddFriendsActivity
     */
    private void getListFriendMessage() {
        UserManagerController.getListFriendMessage(this, new Listener<List<FriendInfo>, List<FriendInfo>>() {
            @Override
            public void onCallBack(List<FriendInfo> request, List<FriendInfo> reply) {
                ArrayList<FriendInfo> tmpInfos = new ArrayList<FriendInfo>();
                ArrayList<FriendInfo> noResponseInfos = new ArrayList<FriendInfo>();
                if (request != null && request.size() > 0) {
                    // 他人请求
                    for (FriendInfo reqInfo : request) {
                        reqInfo.setOtherRequest(true);
                        if (reqInfo.isResponse()) {
                            continue;
                        }
                        noResponseInfos.add(reqInfo);
                    }
                }
                if(YYSingleton.getInstance().getFriendRequestListener()!=null)
                    YYSingleton.getInstance().getFriendRequestListener().onCallBack(noResponseInfos.size());
                if(YYSingleton.getInstance().getOnHasFriendsStatusPoint()!=null)
                    YYSingleton.getInstance().getOnHasFriendsStatusPoint().onCallBack(noResponseInfos.size()>0? true : false);
            }
        });
    }

    /**
     * 注册音视频的广播监听
     */
    private void setCallLister(){
        IntentFilter callFilter = new IntentFilter(EMClient.getInstance().callManager().getIncomingCallBroadcastAction());
        if(callReceiver == null){
            callReceiver = new CallReceiver();
        }
        //register incoming call receiver
        registerReceiver(callReceiver, callFilter);    //注册视频通话和语音通话的广播
    }

}
