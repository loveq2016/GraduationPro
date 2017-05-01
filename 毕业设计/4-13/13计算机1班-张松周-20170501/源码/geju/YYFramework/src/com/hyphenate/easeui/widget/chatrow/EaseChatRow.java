package com.hyphenate.easeui.widget.chatrow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage.Direct;

import app.config.http.HttpConfig;
import app.logic.activity.user.PreviewFriendsInfoActivity;
import app.logic.activity.user.UserInfoActivity;
import app.logic.controller.UserManagerController;
import app.logic.pojo.UserInfo;
import app.yy.geju.R;
import okhttp3.internal.Internal;

import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.adapter.EaseMessageAdapter;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.easeui.ui.activity.ChatActivity;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.widget.EaseChatMessageList;
import com.hyphenate.easeui.widget.EaseChatMessageList.MessageListItemClickListener;
import com.hyphenate.util.DateUtils;

import org.ql.utils.QLToastUtils;

import java.util.Date;
import java.util.HashMap;

public abstract class EaseChatRow extends LinearLayout {
    protected static final String TAG = EaseChatRow.class.getSimpleName();

    protected LayoutInflater inflater;
    protected Context context;
    protected BaseAdapter adapter;
    protected EMMessage message;
    protected int position;

    protected TextView timeStampView;
    protected ImageView userAvatarView;
    protected View bubbleLayout;
    protected TextView usernickView;

    protected TextView percentageView;
    protected ProgressBar progressBar;
    protected ImageView statusView;
    protected Activity activity;

    protected TextView ackedView;
    protected TextView deliveredView;

    protected EMCallBack messageSendCallback;
    protected EMCallBack messageReceiveCallback;

    protected MessageListItemClickListener itemClickListener;

    private HashMap<String, UserInfo> memberList;
    private int chatType;

    public EaseChatRow(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context);
        this.context = context;
        this.activity = (Activity) context;
        this.message = message;
        this.position = position;
        this.adapter = adapter;
        inflater = LayoutInflater.from(context);

        initView();
    }

    private void initView() {
        onInflateView();
        timeStampView = (TextView) findViewById(R.id.timestamp);
        userAvatarView = (ImageView) findViewById(R.id.iv_userhead);
        bubbleLayout = findViewById(R.id.bubble);
        usernickView = (TextView) findViewById(R.id.tv_userid);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        statusView = (ImageView) findViewById(R.id.msg_status);
        ackedView = (TextView) findViewById(R.id.tv_ack);
        deliveredView = (TextView) findViewById(R.id.tv_delivered);

        onFindViewById();
    }

    /**
     * set property according message and postion
     *
     * @param message
     * @param position
     */
    public void setUpView(EMMessage message, HashMap<String, UserInfo> memberList, int chatType, int position,
                          EaseChatMessageList.MessageListItemClickListener itemClickListener) {
        this.message = message;
        this.position = position;
        this.itemClickListener = itemClickListener;
        this.memberList = memberList;
        this.chatType = chatType;

        setUpBaseView();
        onSetUpView();
        setClickListener();
    }

    private String getUserHeadImgUrlByHXAccount(String phone) {
        if (phone == null || memberList == null) {
            return "";
        }
        UserInfo info = memberList.get(phone);
        if (info != null) {
            String urlString = HttpConfig.getUrl(info.getPicture_url());
            if (urlString == null || TextUtils.isEmpty(urlString)) {
                return HttpConfig.getUrl(info.getMy_picture_url());
            }
            return HttpConfig.getUrl(info.getPicture_url());
        }
        return "";
    }

    private String getUserNickNameByHXAccount(String phone) {
        if (phone == null || memberList == null) {
            return "";
        }
        UserInfo info = memberList.get(phone);
        if (info != null) {
            if(!TextUtils.isEmpty(info.getFriend_name())){
                return  info.getFriend_name();
            }else{
                return info.getNickName();
            }
        }
        return "";
    }

    private void setUpBaseView() {
        String nickName = null;
        // set nickname, avatar and background of bubble
        TextView timestamp = (TextView) findViewById(R.id.timestamp);
        if (timestamp != null) {
            if (position == 0) {
                timestamp.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
                timestamp.setVisibility(View.VISIBLE);
            } else {
                // show time stamp if interval with last message is > 30 seconds
                EMMessage prevMessage = (EMMessage) adapter.getItem(position - 1);
                if (prevMessage != null && DateUtils.isCloseEnough(message.getMsgTime(), prevMessage.getMsgTime())) {
                    timestamp.setVisibility(View.GONE);
                } else {
                    timestamp.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
                    timestamp.setVisibility(View.VISIBLE);
                }
            }
        }
        //set nickname and avatar
        if (message.direct() == Direct.SEND) {
            UserInfo userInfo = UserManagerController.getCurrUserInfo();
            String url = getUserHeadImgUrlByHXAccount(userInfo.getPhone());
//            EaseUserUtils.setUserAvatar(context, EMClient.getInstance().getCurrentUser(), userAvatarView);
            if (!TextUtils.isEmpty(url)) {
                Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.ease_default_avatar).centerCrop().into(userAvatarView);
            } else {
                Glide.with(context).load(R.drawable.ease_default_avatar).into(userAvatarView);
            }
        } else {
            String url = getUserHeadImgUrlByHXAccount(message.getFrom());
            if (!TextUtils.isEmpty(url)) {
                Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.ease_default_avatar).centerCrop().into(userAvatarView);
            } else {
                Glide.with(context).load(R.drawable.ease_default_avatar).into(userAvatarView);
            }
//            EaseUserUtils.setUserAvatar(context, message.getFrom(), userAvatarView);
//            EaseUserUtils.setUserNick(message.getFrom(), usernickView);
            // 群聊时，显示接收的消息的发送人的名称
            if (chatType == EaseConstant.CHATTYPE_GROUP || chatType == EaseConstant.CHATTYPE_CHATROOM) {
                // demo里使用username代码nick
                String name = getUserNickNameByHXAccount(message.getFrom());
                nickName = name ;
                usernickView.setText(name);
            }
        }

        if (deliveredView != null) {
            if (message.isDelivered()) {
                deliveredView.setVisibility(View.VISIBLE);
            } else {
                deliveredView.setVisibility(View.INVISIBLE);
            }
        }

        if (ackedView != null) {
            if (message.isAcked()) {
                if (deliveredView != null) {
                    deliveredView.setVisibility(View.INVISIBLE);
                }
                ackedView.setVisibility(View.VISIBLE);
            } else {
                ackedView.setVisibility(View.INVISIBLE);
            }
        }

        if (adapter instanceof EaseMessageAdapter) {
            if (((EaseMessageAdapter) adapter).isShowAvatar())
                userAvatarView.setVisibility(View.VISIBLE);
            else
                userAvatarView.setVisibility(View.GONE);
            if (usernickView != null) {
                if (((EaseMessageAdapter) adapter).isShowUserNick())
                    if( nickName != null && !TextUtils.isEmpty(nickName)){   // 222 -- 224  YSF
                        usernickView.setVisibility(View.VISIBLE);
                    }
                else
                    usernickView.setVisibility(View.GONE);
            }
            if (message.direct() == Direct.SEND) {
                if (((EaseMessageAdapter) adapter).getMyBubbleBg() != null) {
                    bubbleLayout.setBackgroundDrawable(((EaseMessageAdapter) adapter).getMyBubbleBg());
                }
            } else if (message.direct() == Direct.RECEIVE) {
                if (((EaseMessageAdapter) adapter).getOtherBuddleBg() != null) {
                    bubbleLayout.setBackgroundDrawable(((EaseMessageAdapter) adapter).getOtherBuddleBg());
                }
            }
        }
    }

    /**
     * set callback for sending message
     */
    protected void setMessageSendCallback() {
        if (messageSendCallback == null) {
            messageSendCallback = new EMCallBack() {

                @Override
                public void onSuccess() {
                    updateView();
                }

                @Override
                public void onProgress(final int progress, String status) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (percentageView != null)
                                percentageView.setText(progress + "%");


                        }
                    });
                }

                @Override
                public void onError(int code, String error) {
                    updateView();
                }
            };
        }
        message.setMessageStatusCallback(messageSendCallback);
    }

    /**
     * set callback for receiving message
     */
    protected void setMessageReceiveCallback() {
        if (messageReceiveCallback == null) {
            messageReceiveCallback = new EMCallBack() {

                @Override
                public void onSuccess() {
                    updateView();
                }

                @Override
                public void onProgress(final int progress, String status) {
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            if (percentageView != null) {
                                percentageView.setText(progress + "%");
                            }
                        }
                    });
                }

                @Override
                public void onError(int code, String error) {
                    updateView();
                }
            };
        }
        message.setMessageStatusCallback(messageReceiveCallback);
    }


    private void setClickListener() {
        if (bubbleLayout != null) {
            bubbleLayout.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if ( itemClickListener != null ) {
                        if ( !itemClickListener.onBubbleClick(message) ) {
                            // if listener return false, we call default handling
                            onBubbleClick();
                        }
                    }
                }
            });

            bubbleLayout.setOnLongClickListener(new OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onBubbleLongClick(message);
                    }
                    return true;
                }
            });
        }

        if (statusView != null) {
            statusView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onResendClick(message);
                    }
                }
            });
        }

        //************** 开始 修改如下 头像点击 ****************//
        if (userAvatarView != null) {
            userAvatarView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        String hpon = "" ;
                        if (message.direct() == Direct.SEND) {
                            hpon = EMClient.getInstance().getCurrentUser();
                        } else {
                            hpon = message.getFrom() ;
                        }
                        if(TextUtils.isEmpty(hpon)){
                            QLToastUtils.showToast( ChatActivity.activity , "已被移除本群组");
                            return;
                        }
                        UserInfo userInfo = memberList.get(hpon);
                        if( userInfo == null ){
                            QLToastUtils.showToast( ChatActivity.activity , "已被移除本群组");
                            return;
                        }
                        Intent intent = new Intent();
                        intent.setClass( ChatActivity.activity , PreviewFriendsInfoActivity.class);
                        intent.putExtra(PreviewFriendsInfoActivity.kUSER_MEMBER_ID ,userInfo.getWp_member_info_id());
                        ChatActivity.activity.startActivity(intent);
                    }
                }
            });
        }
        //************** 结束 ****************//

        //**********************开始 原来的*********************************//
//        if (userAvatarView != null) {
//            userAvatarView.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (itemClickListener != null) {
//                        if (message.direct() == Direct.SEND) {
//                            itemClickListener.onUserAvatarClick(EMClient.getInstance().getCurrentUser());
//                        } else {
//                            itemClickListener.onUserAvatarClick(message.getFrom());
//                        }
//                    }
//                }
//            });
//
//            userAvatarView.setOnLongClickListener(new OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    if (itemClickListener != null) {
//                        if (message.direct() == Direct.SEND) {
//                            itemClickListener.onUserAvatarLongClick(EMClient.getInstance().getCurrentUser()); //拿到的是手机号码
//                        } else {
//                            itemClickListener.onUserAvatarLongClick(message.getFrom());                      //如果不设置，拿到的也是手机号码
//                        }
//                        return true;
//                    }
//                    return false;
//                }
//            });
//        }
        //********************** 结束 原来的*********************************//
    }

    protected void updateView() {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                if (message.status() == EMMessage.Status.FAIL) {

//                    if (message.describeContents() == EMError.MESSAGE_INCLUDE_ILLEGAL_CONTENT) {
//                        Toast.makeText(activity, activity.getString(R.string.send_fail) + activity.getString(R.string.error_send_invalid_content), Toast.LENGTH_SHORT).show();
//                    } else if (message.getError() == EMError.GROUP_NOT_JOINED) {
//                        Toast.makeText(activity, activity.getString(R.string.send_fail) + activity.getString(R.string.error_send_not_in_the_group), Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(activity, activity.getString(R.string.send_fail) + activity.getString(R.string.connect_failuer_toast), Toast.LENGTH_SHORT).show();
//                    }

                    if (message.describeContents() == EMError.MESSAGE_INCLUDE_ILLEGAL_CONTENT) {
                        Toast.makeText(activity, activity.getString(R.string.send_fail) + activity.getString(R.string.error_send_invalid_content), Toast.LENGTH_SHORT).show();
                    } else if (message.describeContents() == EMError.GROUP_NOT_JOINED) {
                        Toast.makeText(activity, activity.getString(R.string.send_fail) + activity.getString(R.string.error_send_not_in_the_group), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(activity, activity.getString(R.string.send_fail) + activity.getString(R.string.connect_failuer_toast), Toast.LENGTH_SHORT).show();
                    }
                }
                onUpdateView();
            }
        });

    }

    protected abstract void onInflateView();

    /**
     * find view by id
     */
    protected abstract void onFindViewById();

    /**
     * refresh list view when message status change
     */
    protected abstract void onUpdateView();

    /**
     * setup view
     */
    protected abstract void onSetUpView();

    /**
     * on bubble clicked
     */
    protected abstract void onBubbleClick();

}
