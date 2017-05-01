package com.hyphenate.easeui.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.EMChatRoomChangeListener;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMContactManager;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage.ChatType;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.EaseConstant;
import app.config.MyLifecycleHandler;
import app.logic.controller.ChatRoomController;
import app.logic.controller.UserManagerController;
import app.logic.pojo.FriendInfo;
import app.logic.pojo.UserInfo;
import app.logic.pojo.YYChatRoomInfo;
import app.logic.pojo.YYChatSessionInfo;
import app.logic.singleton.YYInterface;
import app.logic.singleton.YYSingleton;
import app.utils.common.Listener;
import app.utils.managers.TYLocationManager;
import app.view.DialogBottom;
import app.yy.geju.R;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.domain.EaseEmojicon;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseAtMessageHelper;
import com.hyphenate.easeui.ui.activity.ChatActivity;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.hyphenate.easeui.widget.EaseAlertDialog.AlertDialogUser;
import com.hyphenate.easeui.widget.EaseChatExtendMenu;
import com.hyphenate.easeui.widget.EaseChatInputMenu;
import com.hyphenate.easeui.widget.EaseChatInputMenu.ChatInputMenuListener;
import com.hyphenate.easeui.widget.EaseChatMessageList;
import com.hyphenate.easeui.widget.EaseVoiceRecorderView;
import com.hyphenate.easeui.widget.EaseVoiceRecorderView.EaseVoiceRecorderCallback;
import com.hyphenate.easeui.widget.chatrow.EaseCustomChatRowProvider;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.NetUtils;
import com.hyphenate.util.PathUtil;
import org.ql.activity.customtitle.FragmentActActivity;
import org.ql.utils.QLToastUtils;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static com.hyphenate.easeui.EaseConstant.CHATTYPE_CHATROOM;
import static com.hyphenate.easeui.EaseConstant.CHATTYPE_GROUP;

/**
 * you can new an EaseChatFragment to use or you can inherit it to expand.
 * You need call setArguments to pass chatType and userId
 * <br/>
 * <br/>
 * you can see ChatActivity in demo for your reference
 */
public class EaseChatFragment extends EaseBaseFragment implements EMMessageListener, EMConnectionListener {
    protected static final String TAG = "EaseChatFragment";
    protected static final int REQUEST_CODE_MAP = 1;
    protected static final int REQUEST_CODE_CAMERA = 2;
    protected static final int REQUEST_CODE_VIDEO = 3;
    protected static final int REQUEST_CODE_LOCAL = 4;
    protected static final int REQUEST_CODE_CAMERA_VIDEO = 5;

    /**
     * params to fragment
     */
    private Context context;
    protected Bundle fragmentArgs;
    protected int chatType;
    protected String toChatUsername;
    protected EaseChatMessageList messageList;
    protected EaseChatInputMenu inputMenu;
    protected EMConversation conversation;
    protected InputMethodManager inputManager; //input_menu
    protected ClipboardManager clipboard;
    protected Handler handler = new Handler();
    protected File cameraFile;
    protected File videoFile;
    protected EaseVoiceRecorderView voiceRecorderView;
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected ListView listView;
    protected boolean isloading;
    protected boolean haveMoreData = true;
    protected int pagesize = 20;
    protected GroupListener groupListener;
    protected EMMessage contextMenuMessage;
    static final int ITEM_TAKE_PICTURE = 1;
    static final int ITEM_PICTURE = 2;
    static final int ITEM_VIDEO = 3;
    static final int ITEM_LOCATION = 4;
    private int nullCount = 0;

    private FriendListener friendListener ;
    public static Handler mHandler ;
    private boolean isFriend  = true ;


    protected int[] itemStrings = {R.string.attach_picture, R.string.attach_take_pic, R.string.attach_video, R.string.attach_location};
    //    protected int[] itemdrawables = {R.drawable.ease_chat_takepic_selector, R.drawable.ease_chat_image_selector,
//            R.drawable.ease_chat_location_selector};
    protected int[] itemdrawables = {R.drawable.icon_ease_chat_picture, R.drawable.icon_ease_chat_carmen, R.drawable.icon_ease_chat_video,
            R.drawable.icon_ease_chat_location};
    protected int[] itemIds = {ITEM_PICTURE, ITEM_TAKE_PICTURE, ITEM_VIDEO, ITEM_LOCATION};
    private EMChatRoomChangeListener chatRoomChangeListener;
    private boolean isMessageListInited;
    protected MyItemClickListener extendMenuItemClickListener;

    //自定义修改
    private String targetAccount ;
    private String targetMemberInfoID;
    private YYChatSessionInfo sessionInfo;
    private String my_head_picture;
    private String target_head_picture;
    private String targetNickName;
    private String roomInfoID;
    private List<UserInfo> singleList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ease_fragment_chat, container, false);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        context = getActivity();
        fragmentArgs = getArguments();
        // check if single chat or group chat
        chatType = fragmentArgs.getInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
        // userId you are chat with or group id
        toChatUsername = fragmentArgs.getString(EaseConstant.EXTRA_USER_ID);
        setTitleNew() ;
        super.onActivityCreated(savedInstanceState);
    }


    /**
     * init view
     */
    protected void initView() {
        // hold to record voice
        //noinspection ConstantConditions
        voiceRecorderView = (EaseVoiceRecorderView) getView().findViewById(R.id.voice_recorder);
        // message list layout
        messageList = (EaseChatMessageList) getView().findViewById(R.id.message_list);
        if (chatType != EaseConstant.CHATTYPE_SINGLE)
            messageList.setShowUserNick(true);
        listView = messageList.getListView();
        extendMenuItemClickListener = new MyItemClickListener();
        inputMenu = (EaseChatInputMenu) getView().findViewById(R.id.input_menu);
        registerExtendMenuItem();
        // init input menu
        inputMenu.init(null);
        /**
         * 输入框的
         */
        setMyInputMenu();
        swipeRefreshLayout = messageList.getSwipeRefreshLayout();
        swipeRefreshLayout.setColorSchemeResources(R.color.holo_blue_bright, R.color.holo_green_light,
                                                   R.color.holo_orange_light, R.color.holo_red_light);
        inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

//        EMClient.getInstance().addConnectionListener(this);
    }

    /**
     * 设置输入框的布局监听
     */
    private void setMyInputMenu() {
        inputMenu.setChatInputMenuListener(new ChatInputMenuListener() {
            @Override
            public void onSendMessage(String content) {
                sendTextMessage(content);
            }
            @Override
            public boolean onPressToSpeakBtnTouch(View v, MotionEvent event) {
                return voiceRecorderView.onPressToSpeakBtnTouch(v, event, new EaseVoiceRecorderCallback() {
                    @Override
                    public void onVoiceRecordComplete(String voiceFilePath, int voiceTimeLength) {
                        sendVoiceMessage(voiceFilePath, voiceTimeLength);
                    }
                });
            }
            @Override
            public void onBigExpressionClicked(EaseEmojicon emojicon) {
                sendBigExpressionMessage(emojicon.getName(), emojicon.getIdentityCode());
            }
        });
    }


    protected void setUpView() {
        if (chatType == EaseConstant.CHATTYPE_SINGLE) {
            // set title
            final String userId = fragmentArgs.getString(EaseConstant.TARGET_ACCOUNT);
            isFriend = fragmentArgs.getBoolean(ChatActivity.ISFRIEND , true );
            friendListener = new FriendListener();
            EMClient.getInstance().contactManager().setContactListener(friendListener);

            UserManagerController.getPhoneMemerInfo(context, userId, new Listener<Integer, List<UserInfo>>() {
                @Override
                public void onCallBack(Integer status, List<UserInfo> reply) {
                    ((FragmentActActivity) context).dismissWaitDialog();
                    if (reply == null || reply.size() < 1) {
                        return;
                    }
                    UserInfo info = reply.get(0);
                    targetMemberInfoID = info.getWp_member_info_id();
                    targetAccount = userId;
                    toChatUsername = userId;
                    targetNickName = fragmentArgs.getString(EaseConstant.TARGET_NICKNAME);
                    singleList = new ArrayList<UserInfo>();

                    my_head_picture = UserManagerController.getCurrUserInfo().getMy_picture_url();
                    target_head_picture = info.getPicture_url();
                    info.setPhone(userId);
                    singleList.add(info);
                    if (targetNickName != null) {
                        ((FragmentActActivity) context).setTitle("" + targetNickName + "");
                    }
                    onConversationInit();
                    onMessageListInit();
                    String forward_msg_id = fragmentArgs.getString("forward_msg_id");
                    if (forward_msg_id != null) {
                        // 显示发送要转发的消息
                        forwardMessage(forward_msg_id);
                    }
                }
            });
        } else {
            toChatUsername = fragmentArgs.getString(EaseConstant.kGroupID);//环信ID
            roomInfoID = fragmentArgs.getString(EaseConstant.kCHAT_ROOM_INFO_ID);//后台参数
            targetAccount = toChatUsername;
            ((FragmentActActivity) context).setTitle("");
            //getChatRoomInfo(roomInfoID);  //获取聊天室成员信息
            if (chatType == EaseConstant.CHATTYPE_GROUP) {
                // listen the event that user moved out group or group is dismissed
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            EMGroup group = EMClient.getInstance().groupManager().getGroupFromServer(toChatUsername);
                            List<String> groupxx = group.getMembers();//获取群成员
                            for(String string : groupxx){
                                System.out.println("=====member==="+string);
                            }
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                groupListener = new GroupListener();
                EMClient.getInstance().groupManager().addGroupChangeListener(groupListener);
            } else {
                onChatRoomViewCreation();
            }
            onConversationInit();
            onMessageListInit();
        }
        setRefreshLayoutListener();
        // show forward message if the message is not null
        String forward_msg_id = getArguments().getString("forward_msg_id");
        if (forward_msg_id != null) {
            forwardMessage(forward_msg_id);
        }

        /**
         * 更改好友昵称，回调刷新title
         */
        YYSingleton.getInstance().setIUpdataChatTitle(new YYInterface.IUpdataChatTitle() {
            @Override
            public void onCallBack(String name) {
                if (!TextUtils.isEmpty(name)) {
                    getActivity().setTitle(name);
                }
            }
        });
    }

    private YYChatRoomInfo chatRoomInfo;
    private void getChatRoomInfo(String cr_id) {
        nullCount = 0;   //清一次零，防止多减
        ChatRoomController.getChatRoomInfo(context, cr_id, new Listener<Void, YYChatRoomInfo>() {
            @Override
            public void onCallBack(Void status, YYChatRoomInfo reply) {
                chatRoomInfo = reply;
                if (reply != null) {
                    List<UserInfo> list = reply.getCr_memberList();
                    for ( UserInfo a : list){
                        if (a.getPhone() == null || TextUtils.isEmpty(a.getPhone()) || a.getIs_remove()==1 ) {
                            nullCount++;
                            continue;
                        }
                    }
                    int a = 1 ;
                    if( list != null )a = list.size() - nullCount ;
                    ((FragmentActActivity) context).setTitle(reply.getCr_name()+"("+ a +"人)");
                    initChatMemberInfos(chatRoomInfo);
                }
            }
        });
    }

    private void initChatMemberInfos(YYChatRoomInfo roomInfo) {
        if (messageList == null) {
            return;
        }
        String memberList = fragmentArgs.getString(EaseConstant.kCHAT_MEMBER_LIST);
        if (roomInfo != null && roomInfo.getCr_memberList() != null) {
            messageList.setMassageList(roomInfo.getCr_memberList(), chatType);
        } else if (memberList != null) {
            try {
                // 设置聊天人员列表
                Gson gson = new Gson();
                if (memberList.contains("wp_friends_info_id")) {
                    List<FriendInfo> friendInfos = gson.fromJson(memberList, new TypeToken<List<FriendInfo>>() {
                    }.getType());
                    if (friendInfos != null) {
                        UserInfo _tmpInfo = null;
                        ArrayList<UserInfo> _tmpList = new ArrayList<UserInfo>();
                        for (FriendInfo friendInfo : friendInfos) {
                            _tmpInfo = new UserInfo();
                            _tmpInfo.setPhone(friendInfo.getPhone());
                            _tmpInfo.setPicture_url(friendInfo.getPicture_url());
                            _tmpList.add(_tmpInfo);
                        }
                        messageList.setMassageList(_tmpList, chatType);
                    }
                } else {
                    List<UserInfo> userInfos = gson.fromJson(memberList, new TypeToken<List<UserInfo>>() {
                    }.getType());
                    messageList.setMassageList(userInfos, chatType);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (singleList != null) {
                messageList.setMassageList(singleList, chatType);
            }
        }
    }

    /**
     * register extend menu, item id need > 3 if you override this method and keep exist item
     */
    protected void registerExtendMenuItem() {
        for (int i = 0; i < itemStrings.length; i++) {
            inputMenu.registerExtendMenuItem(itemStrings[i], itemdrawables[i], itemIds[i], extendMenuItemClickListener);
        }
    }


    protected void onConversationInit() {
//        conversation = EMClient.getInstance().chatManager().getConversation(toChatUsername, EaseCommonUtils.getConversationType(chatType), true);
        conversation = EMClient.getInstance().chatManager().getConversation(targetAccount, EaseCommonUtils.getConversationType(chatType), true);
        conversation.markAllMessagesAsRead();
        // the number of messages loaded into conversation is getChatOptions().getNumberOfMessagesLoaded
        // you can change this number
        List<EMMessage> msgs = conversation.getAllMessages();
        int msgCount = msgs != null ? msgs.size() : 0;
        if (msgCount < conversation.getAllMsgCount() && msgCount < pagesize) {
            String msgId = null;
            if (msgs != null && msgs.size() > 0) {
                msgId = msgs.get(0).getMsgId();
            }
            conversation.loadMoreMsgFromDB(msgId, pagesize - msgCount);
        }
    }

    protected void onMessageListInit() {
//        messageList.init(toChatUsername, chatType, chatFragmentHelper != null ?
//                chatFragmentHelper.onSetCustomChatRowProvider() : null);
        messageList.init(targetAccount, chatType, chatFragmentHelper != null ?
                chatFragmentHelper.onSetCustomChatRowProvider() : null);
        setListItemClickListener();
        initChatMemberInfos(chatRoomInfo);
        messageList.getListView().setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                inputMenu.hideExtendMenuContainer();
                return false;
            }
        });
        isMessageListInited = true;
    }

    /**
     * 设置对话列表的监听
     */
    protected void setListItemClickListener() {
        messageList.setItemClickListener(new EaseChatMessageList.MessageListItemClickListener() {
            @Override
            public void onUserAvatarClick(String username) {
                if (chatFragmentHelper != null) {
                    chatFragmentHelper.onAvatarClick(username);
                }
            }
            @Override
            public void onUserAvatarLongClick(String username) {
                if (chatFragmentHelper != null) {
                    chatFragmentHelper.onAvatarLongClick(username);
                }
            }

            @Override
            public void onResendClick(final EMMessage message) {
//                new EaseAlertDialog(getActivity(), R.string.resend, R.string.confirm_resend, null, new AlertDialogUser() {
//                    @Override
//                    public void onResult(boolean confirmed, Bundle bundle) {
//                        if (!confirmed) {
//                            return;
//                        }
//                        resendMessage(message);
//                    }
//                }, true).show();
                resendMessage(message);
            }

            @Override
            public void onBubbleLongClick(EMMessage message) {
                contextMenuMessage = message;
                if (chatFragmentHelper != null) {
                    chatFragmentHelper.onMessageBubbleLongClick(message);
                }
            }

            /**
             * 主要是这里的点击事件
             * @param message
             * @return
             */
            @Override
            public boolean onBubbleClick(EMMessage message) {
                if (chatFragmentHelper == null) {
                    return false;
                }
                return chatFragmentHelper.onMessageBubbleClick(message);
            }

        });
    }

    protected void setRefreshLayoutListener() {
        swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (listView.getFirstVisiblePosition() == 0 && !isloading && haveMoreData) {
                            List<EMMessage> messages;
                            try {
                                if (chatType == EaseConstant.CHATTYPE_SINGLE) {
                                    messages = conversation.loadMoreMsgFromDB(messageList.getItem(0).getMsgId(),
                                            pagesize);
                                } else {
                                    messages = conversation.loadMoreMsgFromDB(messageList.getItem(0).getMsgId(),
                                            pagesize);
                                }
                            } catch (Exception e1) {
                                swipeRefreshLayout.setRefreshing(false);
                                return;
                            }
                            if (messages.size() > 0) {
                                messageList.refreshSeekTo(messages.size() - 1);
                                if (messages.size() != pagesize) {
                                    haveMoreData = false;
                                }
                            } else {
                                haveMoreData = false;
                            }
                            isloading = false;
                        } else {
                            Toast.makeText(getActivity(), getResources().getString(R.string.no_more_messages), Toast.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 600);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (!inputMenu.onBackPressed()) {
            inputMenu.hideExtendMenuContainer();
        }
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_CAMERA:
                    if (cameraFile != null && cameraFile.exists())
                        sendImageMessage(cameraFile.getAbsolutePath());
                    break;
                case REQUEST_CODE_LOCAL:
                    if (data != null) {
                        Uri selectedImage = data.getData();
                        if (selectedImage != null) {
                            sendPicByUri(selectedImage);
                        }
                    }
                    break;
                case REQUEST_CODE_MAP:
                    double latitude = data.getDoubleExtra("latitude", 0);
                    double longitude = data.getDoubleExtra("longitude", 0);
                    String locationAddress = data.getStringExtra("address");
                    if (locationAddress != null && !locationAddress.equals("")) {
                        sendLocationMessage(latitude, longitude, locationAddress);
                    } else {
                        Toast.makeText(getActivity(), R.string.unable_to_get_loaction, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case REQUEST_CODE_VIDEO:
                    sendVideoByUri(data.getData());
                    break;
                case REQUEST_CODE_CAMERA_VIDEO:
                    if (videoFile != null && videoFile.exists()) {
                        sendVideoByPath(videoFile);
                    }
                    break;
            }
        }else{
            Toast.makeText(getActivity(), "获取失败" , Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (isMessageListInited)
            messageList.refresh();
        EaseUI.getInstance().pushActivity(getActivity());
        // register the event listener when enter the foreground
        EMClient.getInstance().chatManager().addMessageListener(this);

        if (chatType == EaseConstant.CHATTYPE_GROUP) {
            EaseAtMessageHelper.get().removeAtMeGroup(toChatUsername);
        }

        if( chatType != EaseConstant.CHATTYPE_SINGLE ){
            getChatRoomInfo(roomInfoID);    //获取群组的成员列表
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // unregister this event listener when this activity enters the
        // background
        // remove activity from foreground activity list
        EaseUI.getInstance().popActivity(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (groupListener != null) {
            EMClient.getInstance().groupManager().removeGroupChangeListener(groupListener);
        }
        if(friendListener!=null){
            EMClient.getInstance().contactManager().removeContactListener(friendListener);
        }
        if (chatType == CHATTYPE_CHATROOM) {
            EMClient.getInstance().chatroomManager().leaveChatRoom(toChatUsername);
        }
        if (chatRoomChangeListener != null) {
            EMClient.getInstance().chatroomManager().removeChatRoomChangeListener(chatRoomChangeListener);
        }
        EMClient.getInstance().chatManager().removeMessageListener(this);
    }

    public void onBackPressed() {
        if (inputMenu.onBackPressed()) {
            getActivity().finish();
            if (chatType == EaseConstant.CHATTYPE_GROUP) {
                EaseAtMessageHelper.get().removeAtMeGroup(toChatUsername);
                EaseAtMessageHelper.get().cleanToAtUserList();
            }
            if (chatType == CHATTYPE_CHATROOM) {
                EMClient.getInstance().chatroomManager().leaveChatRoom(toChatUsername);
            }
        }
    }

    protected void onChatRoomViewCreation() {
        final ProgressDialog pd = ProgressDialog.show(getActivity(), "", "Joining......");
        EMClient.getInstance().chatroomManager().joinChatRoom(toChatUsername, new EMValueCallBack<EMChatRoom>() {
            @Override
            public void onSuccess(final EMChatRoom value) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (getActivity().isFinishing() || !toChatUsername.equals(value.getId()))
                            return;
                        pd.dismiss();
                        EMChatRoom room = EMClient.getInstance().chatroomManager().getChatRoom(toChatUsername);
//                        if (room != null) {
//                            titleBar.setTitle(room.getName());
//                            EMLog.d(TAG, "join room success : " + room.getName());
//                        } else {
//                            titleBar.setTitle(toChatUsername);
//                        }
                        addChatRoomChangeListenr();
                        onConversationInit();
                        onMessageListInit();
                    }
                });
            }

            @Override
            public void onError(final int error, String errorMsg) {
                // TODO Auto-generated method stub
                EMLog.d(TAG, "join room failure : " + error);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                    }
                });
                getActivity().finish();
            }
        });
    }


    protected void addChatRoomChangeListenr() {
        chatRoomChangeListener = new EMChatRoomChangeListener() {
            @Override
            public void onChatRoomDestroyed(String roomId, String roomName) {
                if (roomId.equals(toChatUsername)) {
                    showChatroomToast(" room : " + roomId + " with room name : " + roomName + " was destroyed");
                    getActivity().finish();
                }
            }

            @Override
            public void onMemberJoined(String roomId, String participant) {
                showChatroomToast("member : " + participant + " join the room : " + roomId);
            }

            @Override
            public void onMemberExited(String roomId, String roomName, String participant) {
                showChatroomToast("member : " + participant + " leave the room : " + roomId + " room name : " + roomName);
            }

            @Override
            public void onRemovedFromChatRoom(String roomId, String roomName, String participant) {
                if (roomId.equals(toChatUsername)) {
                    String curUser = EMClient.getInstance().getCurrentUser();
                    if (curUser.equals(participant)) {
                        EMClient.getInstance().chatroomManager().leaveChatRoom(toChatUsername);
                        getActivity().finish();
                    } else {
                        showChatroomToast("member : " + participant + " was kicked from the room : " + roomId + " room name : " + roomName);
                    }
                }
            }
        };
        EMClient.getInstance().chatroomManager().addChatRoomChangeListener(chatRoomChangeListener);
    }

    protected void showChatroomToast(final String toastContent) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getActivity(), toastContent, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // implement methods in EMMessageListener

    /**
     * ---------------------------------------------------------------------------------------------------------------------------
     * <p>
     * 接收消息
     */
    @Override
    public void onMessageReceived(List<EMMessage> messages) {
        for (EMMessage message : messages) {
            String username = null;
            if (message.getChatType() == ChatType.GroupChat || message.getChatType() == ChatType.ChatRoom) {
                username = message.getTo();
            } else {
                username = message.getFrom();
            }
            if( chatType == EaseConstant.CHATTYPE_SINGLE && !isFriend && (((EMTextMessageBody)message.getBody()).getMessage().equals("我们已经是好友了，现在开始对话吧！"))){
                isFriend = true ;  //重新添加为好友
            }
            // if the message is for current conversation
            if (username.equals(toChatUsername) || message.getTo().equals(toChatUsername)) {   //当前会话消息
                messageList.refreshSelectLast();
                if(!MyLifecycleHandler.isApplicationInForeground()) {   //应用程序在后台
                    EaseUI.getInstance().getNotifier().vibrateAndPlayTone(message);
                }
            } else {
                EaseUI.getInstance().getNotifier().onNewMsg(message);
            }
        }
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {
        if (isMessageListInited) {
            messageList.refresh();
        }

    }

    @Override
    public void onMessageRead(List<EMMessage> list) {
        if (isMessageListInited) {
            messageList.refresh();
        }
    }

    @Override
    public void onMessageDelivered(List<EMMessage> list) {
        if (isMessageListInited) {
            messageList.refresh();
        }
    }


    @Override
    public void onMessageChanged(EMMessage emMessage, Object change) {
        if (isMessageListInited) {
            messageList.refresh();
        }
    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onDisconnected(final int error) {

        QLToastUtils.showToast(getActivity(), String.valueOf(error));
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (error == EMError.USER_REMOVED) {

                    // 显示帐号已经被移除
                } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                    // 显示帐号在其他设备登录
                } else {
                    if (NetUtils.hasNetwork(getActivity())) {
                    }
                    //连接不到聊天服务器
                    else {

                    }
                    //当前网络不可用，请检查网络设置
                }
            }
        });

    }
    /**
     * ---------------------------------------------------------------------------------------------------------------------------
     */


    /**
     * handle the click event for extend menu
     * <p>
     * 发送其他类型
     */
    class MyItemClickListener implements EaseChatExtendMenu.EaseChatExtendMenuItemClickListener {

        @Override
        public void onClick(int itemId, View view) {
            if (chatFragmentHelper != null) {
                if (chatFragmentHelper.onExtendMenuItemClick(itemId, view)) {
                    return;
                }
            }
            switch (itemId) {
                case ITEM_TAKE_PICTURE:
                    selectPicFromCamera();
                    break;
                case ITEM_PICTURE:
                    selectPicFromLocal();
                    break;
                case ITEM_VIDEO:
                    startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI), REQUEST_CODE_VIDEO);
                    break;
                case ITEM_LOCATION:
//                    startActivityForResult(new Intent(getActivity(), EaseBaiduMapActivity.class), REQUEST_CODE_MAP);
                    requestPermissionByLocal();
                    break;

                default:
                    break;
            }
        }

    }

    /**
     * input @
     *
     * @param username
     */
    protected void inputAtUsername(String username, boolean autoAddAtSymbol) {
        if (EMClient.getInstance().getCurrentUser().equals(username) ||
                chatType != EaseConstant.CHATTYPE_GROUP) {
            return;
        }
        EaseAtMessageHelper.get().addAtUser(username);
        EaseUser user = EaseUserUtils.getUserInfo(username);
        if (user != null) {
            username = user.getNick();
        }
        if (autoAddAtSymbol)
            inputMenu.insertText("@" + username + " ");
        else
            inputMenu.insertText(username + " ");
    }


    /**
     * input @
     *
     * @param username
     */
    protected void inputAtUsername(String username) {
        inputAtUsername(username, true);
    }

    /**
     * 发送文本信息（表情也走这里）
     * @param content
     */
    protected void sendTextMessage(String content) {
        if (EaseAtMessageHelper.get().containsAtUsername(content)) {
            sendAtMessage(content);
        } else {
            //setEmptyUesr();
            EMMessage message = EMMessage.createTxtSendMessage(content, toChatUsername);
            sendMessage(message);
        }
        messageList.refresh();
    }

    /**
     * send @ message, only support group chat message
     *
     * @param content
     */
    @SuppressWarnings("ConstantConditions")
    private void sendAtMessage(String content) {
        if (chatType != EaseConstant.CHATTYPE_GROUP) {
            EMLog.e(TAG, "only support group chat message");
            return;
        }
        //setEmptyUesr();
        EMMessage message = EMMessage.createTxtSendMessage(content, toChatUsername);
        EMGroup group = EMClient.getInstance().groupManager().getGroup(toChatUsername);
        if (EMClient.getInstance().getCurrentUser().equals(group.getOwner()) && EaseAtMessageHelper.get().containsAtAll(content)) {
            message.setAttribute(EaseConstant.MESSAGE_ATTR_AT_MSG, EaseConstant.MESSAGE_ATTR_VALUE_AT_MSG_ALL);
        } else {
            message.setAttribute(EaseConstant.MESSAGE_ATTR_AT_MSG,
                    EaseAtMessageHelper.get().atListToJsonArray(EaseAtMessageHelper.get().getAtMessageUsernames(content)));
        }
        sendMessage(message);

    }


    protected void sendBigExpressionMessage(String name, String identityCode) {
        //setEmptyUesr();
        EMMessage message = EaseCommonUtils.createExpressionMessage(toChatUsername, name, identityCode);
        sendMessage(message);
    }

    /**
     * 发送语音
     * @param filePath
     * @param length
     */
    protected void sendVoiceMessage(String filePath, int length) {
        //setEmptyUesr();
        EMMessage message = EMMessage.createVoiceSendMessage(filePath, length, toChatUsername);
        sendMessage(message);
    }

    /**
     * 发送图片
     * @param imagePath
     */
    protected void sendImageMessage(String imagePath) {
        //setEmptyUesr();
        EMMessage message = EMMessage.createImageSendMessage(imagePath, false, toChatUsername);
        sendMessage(message);
    }

    /**
     *发送位置
     * @param latitude
     * @param longitude
     * @param locationAddress
     */
    protected void sendLocationMessage(double latitude, double longitude, String locationAddress) {
        //setEmptyUesr();

        EMMessage message = EMMessage.createLocationSendMessage(latitude, longitude, locationAddress, toChatUsername);
        sendMessage(message);
    }

    /**
     *发送视屏
     * @param videoPath
     * @param thumbPath
     * @param videoLength
     */
    protected void sendVideoMessage(String videoPath, String thumbPath, int videoLength) {
        //setEmptyUesr();
        EMMessage message = EMMessage.createVideoSendMessage(videoPath, thumbPath, videoLength, toChatUsername);
        sendMessage(message);
    }

    /**
     * 发送文件
     * @param filePath
     */
    protected void sendFileMessage(String filePath) {
        //setEmptyUesr();
        EMMessage message = EMMessage.createFileSendMessage(filePath, toChatUsername);
        sendMessage(message);
    }

    /**
     * 发送信息
     * @param message
     */
    protected void sendMessage(EMMessage message) {
        if(!isFriend){
            Toast.makeText(getActivity(), "已不是您的好友，不能发信息", Toast.LENGTH_LONG).show();
            return;
        }
        if (message == null) {
            return;
        }
        if (chatFragmentHelper != null) {
            //set extension
            chatFragmentHelper.onSetMessageAttributes(message);
        }
        if (chatType == EaseConstant.CHATTYPE_GROUP) {
            message.setChatType(ChatType.GroupChat);
        } else if (chatType == CHATTYPE_CHATROOM) {
            message.setChatType(ChatType.ChatRoom);
        }
        //send message
        EMClient.getInstance().chatManager().sendMessage(message);
        //refresh ui
        if (isMessageListInited) {
            messageList.refreshSelectLast();
        }
    }


    public void resendMessage(EMMessage message) {
        message.setStatus(EMMessage.Status.CREATE);
        EMClient.getInstance().chatManager().sendMessage(message);
        messageList.refresh();
    }

    //===================================================================================


    private final static int REQUEST_PERMISSION_LOCAL_CODE = 16;

    /**
     * 打开地理位置
     */
    private void requestPermissionByLocal() {
        //TODO
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCAL_CODE);
            } else {
                startActivityForResult(new Intent(getActivity(), EaseBaiduMapActivity.class), REQUEST_CODE_MAP);
            }
        } else {
            startActivityForResult(new Intent(getActivity(), EaseBaiduMapActivity.class), REQUEST_CODE_MAP);
        }
    }

    /**
     * send image
     *
     * @param selectedImage
     */
    protected void sendPicByUri(Uri selectedImage) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            cursor = null;

            if (picturePath == null || picturePath.equals("null")) {
                Toast toast = Toast.makeText(getActivity(), R.string.cant_find_pictures, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            sendImageMessage(picturePath);
        } else {
            File file = new File(selectedImage.getPath());
            if (!file.exists()) {
                Toast toast = Toast.makeText(getActivity(), R.string.cant_find_pictures, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;

            }
            sendImageMessage(file.getAbsolutePath());
        }

    }

    /**
     * send file
     *
     * @param uri
     */
    protected void sendFileByUri(Uri uri) {
        String filePath = null;
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = null;

            try {
                cursor = getActivity().getContentResolver().query(uri, filePathColumn, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(column_index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            filePath = uri.getPath();
        }
        if (filePath == null) {
            return;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            Toast.makeText(getActivity(), R.string.File_does_not_exist, Toast.LENGTH_SHORT).show();
            return;
        }
        //limit the size < 10M
        if (file.length() > 10 * 1024 * 1024) {
            Toast.makeText(getActivity(), R.string.The_file_is_not_greater_than_10_m, Toast.LENGTH_SHORT).show();
            return;
        }
        sendFileMessage(filePath);
    }


    /**
     * 发送Video（视频）
     *
     * @param uri
     */
    protected void sendVideoByUri(Uri uri) {
        String[] videoPathColumn = {MediaStore.Video.Media.DATA, MediaStore.Video.Media.SIZE, MediaStore.Video.Media._ID, MediaStore.Video.Thumbnails.DATA,MediaStore.Video.Media.DURATION};
        Cursor cursor = getActivity().getContentResolver().query(uri, videoPathColumn, null, null, null);
        String videoPath;
        final String videoThumb;
        int videoSize , videoDuration;
        if (cursor != null) {
            cursor.moveToFirst();
            videoPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)); //获取路径
            videoSize = (int) cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)); //文件大小
            videoDuration = (int) cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)); //视频文件的时长
            cursor.close();
            /**
             * 保存缩略图
             */
            videoThumb = saveThumbImage(videoPath);
            if (videoSize >= 10 * 1024 * 1024) {
                QLToastUtils.showToast(getActivity(), "视频太大，不能超过10M");
                return;
            }
            if(videoThumb==null || TextUtils.isEmpty(videoThumb)){
                QLToastUtils.showToast( getContext() , "无效的视频文件");
                return;
            }
            /**
             * video的路径，缩略图的路径，大小
             */
            sendVideoMessage(videoPath, videoThumb, videoDuration);
        }
    }

    /**
     * 发送录像
     *
     * @param file
     */
    private void sendVideoByPath(File file) {
        if (file.length() >= 10 * 1024 * 1024) {
            QLToastUtils.showToast(getActivity(), "视频太大，不能超过10M，请重新拍摄");
            return;
        }
        String videoPath = file.getAbsolutePath();
        String videoThumb = saveThumbImage(videoPath);
        int videoSize = 0;
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(file);
            videoSize = stream.available();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        int getVideoLength = (int) getVideoLength(videoPath);  //获取视频文件的时长
        sendVideoMessage(videoPath, videoThumb, getVideoLength);
    }

    /**
     * 获取视屏文件的时长
     * @param videoPath
     */
    private double getVideoLength( String videoPath ){
        MediaPlayer player = new MediaPlayer();
        try {
            player.setDataSource(videoPath);  //videoPath 为音频文件的路径
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        double duration= player.getDuration();//获取音频的时间
        player.release();//记得释放资源
        return duration ;
    }


    /**
     * 缩略图
     *
     * @param videoPath
     * @return
     */
    private String saveThumbImage(String videoPath) {
        Bitmap thumbMap = ThumbnailUtils.createVideoThumbnail(videoPath, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        if(thumbMap==null){  //发现选择发送视频是，选择的是图片或是无效的视频文件时，这个对象是null
            return "";
        }
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/YYData/images/thumbs";
        File dir = new File(filePath);
        if (!dir.exists()) {
            dir.mkdir();
        }
        File thumbImage = new File(dir, System.currentTimeMillis() + ".jpg");
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(thumbImage));
            thumbMap.compress(Bitmap.CompressFormat.JPEG, 50, bos);
            bos.flush();
            bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return thumbImage.getAbsolutePath();
    }

    private static final int REQUEST_PERMISSION_CODE = 15;

    /**
     * capture new image
     */
    protected void selectPicFromCamera() {
        if (!EaseCommonUtils.isSdcardExist()) {
            Toast.makeText(getActivity(), R.string.sd_card_does_not_exist, Toast.LENGTH_SHORT).show();
            return;
        }
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.CAMERA}, REQUEST_PERMISSION_CODE);
            } else {
                openCameraSelect();
            }
        } else {
            openCameraSelect();
        }
    }

    /**
     * 打开相机的选择
     */
    private void openCameraSelect() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_sex_dialogfragment, null);
        final DialogBottom dialog = new DialogBottom(getActivity(), view);
        TextView carmenTv = (TextView) view.findViewById(R.id.man_tv);
        TextView videoTV = (TextView) view.findViewById(R.id.woman_tv);
        TextView cancelTv = (TextView) view.findViewById(R.id.cancel_tv);
        carmenTv.setText("拍照");
        videoTV.setText("录像");
        carmenTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraFile = new File(PathUtil.getInstance().getImagePath(), EMClient.getInstance().getCurrentUser() + System.currentTimeMillis() + ".jpg");
                //noinspection ResultOfMethodCallIgnored
                cameraFile.getParentFile().mkdirs();
                startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)), REQUEST_CODE_CAMERA);
                dialog.dismiss();
            }
        });
        videoTV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                videoFile = new File(PathUtil.getInstance().getImagePath(), EMClient.getInstance().getCurrentUser() + System.currentTimeMillis() + ".mp4");
                videoFile.getParentFile().mkdirs();
                startActivityForResult(new Intent(MediaStore.ACTION_VIDEO_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(videoFile)), REQUEST_CODE_CAMERA_VIDEO);
                dialog.dismiss();
            }
        });
        cancelTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public static final int REQUEST_PERMISSION_VOICE_ = 17;


    /**
     * 回调权限受理
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCameraSelect();
                } else {
                    QLToastUtils.showToast(getActivity(), "请先在应用管理设置\"相机\"访问权限");
                }
                break;

            case REQUEST_PERMISSION_LOCAL_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivityForResult(new Intent(getActivity(), EaseBaiduMapActivity.class), REQUEST_CODE_MAP);
                } else {
                    QLToastUtils.showToast(getActivity(), "请先在应用管理设置\"相机\"访问权限");
                }
                break;
            case REQUEST_PERMISSION_VOICE_:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    setMy();
                } else {
                    QLToastUtils.showToast(getActivity(), "请先在应用管理设置\"录音\"访问权限");

                }
                break;
        }
    }

    /**
     * select local image
     */
    protected void selectPicFromLocal() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");

        } else {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, REQUEST_CODE_LOCAL);
    }


    /**
     * clear the conversation history
     */
    protected void emptyHistory() {
        String msg = getResources().getString(R.string.Whether_to_empty_all_chats);
        new EaseAlertDialog(getActivity(), null, msg, null, new AlertDialogUser() {
            @Override
            public void onResult(boolean confirmed, Bundle bundle) {
                if (confirmed) {
                    EMClient.getInstance().chatManager().deleteConversation(toChatUsername, true);
                    messageList.refresh();
                }
            }
        }, true).show();
    }

    /**
     * open group detail
     */
    protected void toGroupDetails() {
        if (chatType == EaseConstant.CHATTYPE_GROUP) {
            EMGroup group = EMClient.getInstance().groupManager().getGroup(toChatUsername);
            if (group == null) {
                Toast.makeText(getActivity(), R.string.gorup_not_found, Toast.LENGTH_SHORT).show();
                return;
            }
            if (chatFragmentHelper != null) {
                chatFragmentHelper.onEnterToChatDetails();
            }
        } else if (chatType == CHATTYPE_CHATROOM) {
            if (chatFragmentHelper != null) {
                chatFragmentHelper.onEnterToChatDetails();
            }
        }
    }

    /**
     * hide
     */
    protected void hideKeyboard() {
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null)
                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * forward message
     *
     * @param forward_msg_id
     */
    protected void forwardMessage(String forward_msg_id) {
        final EMMessage forward_msg = EMClient.getInstance().chatManager().getMessage(forward_msg_id);
        EMMessage.Type type = forward_msg.getType();
        switch (type) {
            case TXT:
                if (forward_msg.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false)) {
                    sendBigExpressionMessage(((EMTextMessageBody) forward_msg.getBody()).getMessage(),
                            forward_msg.getStringAttribute(EaseConstant.MESSAGE_ATTR_EXPRESSION_ID, null));
                } else {
                    // get the content and send it
                    String content = ((EMTextMessageBody) forward_msg.getBody()).getMessage();
                    sendTextMessage(content);
                }
                break;
            case IMAGE:
                // send image
                String filePath = ((EMImageMessageBody) forward_msg.getBody()).getLocalUrl();
                if (filePath != null) {
                    File file = new File(filePath);
                    if (!file.exists()) {
                        // send thumb nail if original image does not exist
                        filePath = ((EMImageMessageBody) forward_msg.getBody()).thumbnailLocalPath();
                    }
                    sendImageMessage(filePath);
                }
                break;
            default:
                break;
        }

        if (forward_msg.getChatType() == EMMessage.ChatType.ChatRoom) {
            EMClient.getInstance().chatroomManager().leaveChatRoom(forward_msg.getTo());
        }
    }

    /**
     * listen the group event
     */
    class GroupListener extends EaseGroupRemoveListener {

        @Override
        public void onRequestToJoinReceived(String s, String s1, String s2, String s3) {

        }

        @Override
        public void onRequestToJoinAccepted(String s, String s1, String s2) {

        }

        @Override
        public void onRequestToJoinDeclined(String s, String s1, String s2, String s3) {

        }

        @Override
        public void onUserRemoved(final String groupId, String groupName) {
            final Activity activity = getActivity();
            if( activity != null){
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        if ( toChatUsername!=null && toChatUsername.equals(groupId)) {
                            //Toast.makeText(getActivity(), R.string.you_are_group, Toast.LENGTH_LONG).show();
                            Toast.makeText(getActivity(), "您已被移除群组", Toast.LENGTH_LONG).show();
                            if (!activity.isFinishing()) {
                                activity.finish();
                            }
                        }
                    }
                });
            }
        }

        @Override
        public void onGroupDestroyed(final String groupId, String groupName) {
            // prompt group is dismissed and finish this activity
            final Activity activity = getActivity();
            if( activity != null ) {
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        if (toChatUsername != null && toChatUsername.equals(groupId)) {
                            //Toast.makeText(getActivity(), R.string.the_current_group, Toast.LENGTH_LONG).show();
                            Toast.makeText(getActivity(), "群组被解散", Toast.LENGTH_LONG).show();
                            if (!activity.isFinishing()) {
                                activity.finish();
                            }
                        }
                    }
                });
            }
        }
    }

    /**
     * 朋友状态监听器
     */
    public class FriendListener implements EMContactListener {
        @Override  //增加了联系人时回调此方法
        public void onContactAdded(String s) {
            System.out.println("增加了联系人时回调此方法");
        }

        @Override  //被删除时回调此方法
        public void onContactDeleted(final String s) {
            System.out.println("被删除时回调此方法");
            isFriend = false ;
//            final Activity activity = getActivity();
//            if( activity != null){
//                activity.runOnUiThread(new Runnable() {
//                    public void run() {
//                        if ( toChatUsername!=null && toChatUsername.equals(s)) {
//                            //Toast.makeText(getActivity(), R.string.you_are_group, Toast.LENGTH_LONG).show();
//                            Toast.makeText(getActivity(), "您已被好友删除", Toast.LENGTH_LONG).show();
//                            if (!activity.isFinishing()) {
//                                activity.finish();
//                            }
//                        }
//                    }
//                });
//            }
        }

        @Override  //收到好友邀请
        public void onContactInvited(String s, String s1) {
            System.out.println("收到好友邀请");
        }

        @Override  //被接受朋友请求
        public void onFriendRequestAccepted(String s) {
            System.out.println("被接受朋友请求");
        }

        @Override   //被朋友请求拒绝
        public void onFriendRequestDeclined(String s) {
            System.out.println("被朋友请求拒绝");
        }
    }

    protected EaseChatFragmentHelper chatFragmentHelper;

    public void setChatFragmentListener(EaseChatFragmentHelper chatFragmentHelper) {
        this.chatFragmentHelper = chatFragmentHelper;
    }

    public interface EaseChatFragmentHelper {
        /**
         * set message attribute
         */
        void onSetMessageAttributes(EMMessage message);

        /**
         * enter to chat detail
         */
        void onEnterToChatDetails();

        /**
         * on avatar clicked
         *
         * @param username
         */
        void onAvatarClick(String username);

        /**
         * on avatar long pressed
         *
         * @param username
         */
        void onAvatarLongClick(String username);

        /**
         * on message bubble clicked
         */
        boolean onMessageBubbleClick(EMMessage message);

        /**
         * on message bubble long pressed
         */
        void onMessageBubbleLongClick(EMMessage message);

        /**
         * on extend menu item clicked, return true if you want to override
         *
         * @param view
         * @param itemId
         * @return
         */
        boolean onExtendMenuItemClick(int itemId, View view);

        /**
         * on set custom chat row provider
         *
         * @return
         */
        EaseCustomChatRowProvider onSetCustomChatRowProvider();
    }

    /**
     * 设置标题栏
     */
    private void setTitleNew(){
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what==1){        //更新备注名
                    Bundle bundle = msg.getData();
                    Activity ac1tivity = getActivity();
                    if(ac1tivity!=null) ac1tivity.setTitle(bundle.getString(ChatActivity.REMARKS_NAME));
                }else if(msg.what==2){  //更新接收消息者不是自己的好友标志
                    isFriend = false ;
                    if( getActivity()!=null && !getActivity().isFinishing()){
                        getActivity().finish();
                    }
                }
            }
        };
    }
    /**
     * 设置空用户(10000000000)
     */
//    private void setEmptyUesr(){
//        if( chatType == EaseConstant.CHATTYPE_SINGLE && !isFriend ){
//            toChatUsername = ChatActivity.EMPTYUSER ;
//        }
//    }
}




