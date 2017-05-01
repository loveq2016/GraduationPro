package app.utils.helpers;

import java.util.ArrayList;
import java.util.List;

import org.ql.utils.QLToastUtils;

import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.activity.ChatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import app.logic.activity.chat.ChatRoomInfoActivity;
import app.logic.controller.ChatRoomController;
import app.logic.controller.UserManagerController;
import app.utils.common.Listener;
import app.logic.pojo.FriendInfo;
import app.logic.pojo.UserInfo;
import app.logic.pojo.YYChatRoomInfo;

/**
 * @author SiuJiYung create at 2016-5-13上午10:48:36
 */
public class ChartHelper {

    /**
     * 启动一个聊天界面
     *
     * @param context
     * @param targetUserId
     * @param tag
     */
    public synchronized static void startChart(final Context context, final String targetUserId, String tag) {
        addChart(context, targetUserId, tag);
        UserManagerController.getUserInfo(context, targetUserId, new Listener<Integer, UserInfo>() {
            @Override
            public void onCallBack(Integer status, UserInfo reply) {
                if (reply != null) {
                    ArrayList<UserInfo> membersInfos = new ArrayList<UserInfo>();
                    membersInfos.add(reply);
                    Gson gson = new Gson();
                    String members_json = gson.toJson(membersInfos);

                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString(EaseConstant.EXTRA_USER_ID, targetUserId);
                    bundle.putInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
                    bundle.putString(EaseConstant.TARGET_MEMBER_ID, targetUserId);
                    if("11".equals(reply.getFriendStatus())){  //好友
                        bundle.putBoolean(ChatActivity.ISFRIEND , true );
                    }else{                                     //非好友 ,发送到10000000000
                        bundle.putBoolean(ChatActivity.ISFRIEND , false );
                    }
                    bundle.putString(EaseConstant.TARGET_ACCOUNT, reply.getPhone());
                    bundle.putString(EaseConstant.kCHAT_MEMBER_LIST, members_json);
                    if (reply.getPicture_url() != null) {
                        bundle.putString(EaseConstant.TARGET_HEAD_IMG, reply.getPicture_url());
                    }
                    if (reply.getNickName() != null) {
                        bundle.putString(EaseConstant.TARGET_NICKNAME, reply.getFriend_name() == null || TextUtils.isEmpty(reply.getFriend_name()) ? reply.getNickName() : reply.getFriend_name());
                    }

                    intent.setClass(context, ChatActivity.class);
                    intent.putExtras(bundle);
//					intent.putExtra(EaseConstant.EXTRA_USER_ID, targetUserId);
//					intent.putExtra("chatType", ChatActivity.CHATTYPE_SINGLE);
//					intent.putExtra(ChatActivity.TARGET_MEMBER_ID, targetUserId);
//					intent.putExtra(ChatActivity.TARGET_ACCOUNT, reply.getPhone());
//					intent.putExtra(ChatActivity.kCHAT_MEMBER_LIST, members_json);
//
//					if (reply.getPicture_url() != null) {
//						intent.putExtra(ChatActivity.TARGET_HEAD_IMG, reply.getPicture_url());
//					}
//					if (reply.getNickName() != null) {
//						intent.putExtra(ChatActivity.TARGET_NICKNAME, reply.getFriend_name() == null || TextUtils.isEmpty(reply.getFriend_name()) ? reply.getNickName() : reply.getFriend_name());
//					}
                    context.startActivity(intent);
                } else {
                    QLToastUtils.showToast(context, "找不到该用户");
                }
            }
        });
    }

    public synchronized static void startChart(final Context context, final FriendInfo tagetUserInfo, String tag) {
        addChart(context, tagetUserInfo.getWp_friends_info_id(), tag);

        ArrayList<FriendInfo> membersInfos = new ArrayList<FriendInfo>();
        membersInfos.add(tagetUserInfo);
        Gson gson = new Gson();
        String members_json = gson.toJson(membersInfos);

        Intent intent = new Intent();
        intent.setClass(context, ChatActivity.class);
        intent.putExtra(EaseConstant.EXTRA_USER_ID, tagetUserInfo.getWp_friends_info_id());
//		intent.putExtra("chatType", ChatActivity.CHATTYPE_SINGLE);
//		intent.putExtra(ChatActivity.TARGET_MEMBER_ID, tagetUserInfo.getWp_friends_info_id());
//		intent.putExtra(ChatActivity.TARGET_ACCOUNT, tagetUserInfo.getPhone());
//		intent.putExtra(ChatActivity.kCHAT_MEMBER_LIST, members_json);
//		if (tagetUserInfo.getPicture_url() != null) {
//			intent.putExtra(ChatActivity.TARGET_HEAD_IMG, tagetUserInfo.getPicture_url());
//		}
//		if (tagetUserInfo.getNickName() != null) {
//			intent.putExtra(ChatActivity.TARGET_NICKNAME, tagetUserInfo.getNickName());
//		}
        context.startActivity(intent);
    }

    /**
     * 注册一个会话
     *
     * @param context
     * @param targetUserMemberId
     * @param tag
     */
    public static void addChart(Context context, String targetUserMemberId, String tag) {
        UserManagerController.addChatWith(context, targetUserMemberId, tag, null);
    }

    /**
     * 移除一个会话
     *
     * @param context
     * @param targetUserMemberId
     */
    public static void removeChart(Context context, String targetUserMemberId, final Listener<Void, Void> callback) {
        UserManagerController.removeChatWith(context, targetUserMemberId, new Listener<Integer, String>() {
            @Override
            public void onCallBack(Integer status, String reply) {
                if (callback != null) {
                    callback.onCallBack(null, null);
                }
            }
        });
    }

    /**
     * 打开聊天室
     *
     * @param context
     * @param roomInfo
     */
    public static void openChatRoom(final Context context, YYChatRoomInfo roomInfo) {
        if (roomInfo == null || roomInfo.getRoom_id() == null) {
            QLToastUtils.showToast(context, "无法打开聊天室");
            return;
        }
        List<UserInfo> _membersInfos = roomInfo.getCr_memberList();
        String members_json = null;
        if (_membersInfos != null) {
            Gson gson = new Gson();
            members_json = gson.toJson(_membersInfos);
        }
        final Intent intent = new Intent();
        intent.setClass(context, ChatActivity.class);
        Bundle bundle = new Bundle();
//        bundle.putString(EaseConstant.EXTRA_USER_ID, roomInfo.getRoom_id());
        bundle.putInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_GROUP); //打开类型
        bundle.putString(EaseConstant.kGroupID, roomInfo.getRoom_id()); //环信Id
        bundle.putString(EaseConstant.kCHAT_ROOM_INFO_ID, roomInfo.getCr_id()); //后台获取参数ID

        if (members_json != null) {
            bundle.putString(EaseConstant.kCHAT_MEMBER_LIST, members_json);
        }
        intent.putExtras(bundle);

        intent.putExtra(EaseConstant.EXTRA_USER_ID, roomInfo.getRoom_id());

        ChatRoomController.registerChatToMesssageList(context, roomInfo.getCr_id(), "1", new Listener<Integer, String>() {
            @Override
            public void onCallBack(Integer integer, String reply) {
                if (integer == 1) {
                    context.startActivity(intent);
                } else {
                    QLToastUtils.showToast(context, reply);
                }
            }
        });

    }

    /**
     * 打开群聊
     *
     * @param context
     * @param cr_id
     */
    public static void openChatRoom(final Context context, final String room_id, final String cr_id) {
        if (room_id == null || cr_id == null) {
            QLToastUtils.showToast(context, "无法打开聊天室");
            return;
        }
        /**
         *
         */
        ChatRoomController.registerChatToMesssageList(context, cr_id, "1", new Listener<Integer, String>() {
            @Override
            public void onCallBack(Integer integer, String reply) {
                if (integer == 1) {
                    Intent intent = new Intent();
                    intent.setClass(context, ChatActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_GROUP);
                    bundle.putString(EaseConstant.kCHAT_ROOM_INFO_ID, cr_id);//获取后台群聊参数ID
                    bundle.putString(EaseConstant.kGroupID, room_id);

                    intent.putExtras(bundle);
                    context.startActivity(intent);
                } else {
                    QLToastUtils.showToast(context, reply);
                }
            }
        });
    }

    /**
     * 打开群聊
     *
     * @param context
     * @param cr_id
     */
    public static void openChatRoom(final Context context, final String room_id, final String cr_id, final boolean b ) {
        if (room_id == null || cr_id == null) {
            QLToastUtils.showToast(context, "无法打开聊天室");
            return;
        }
        /**
         *
         */
        ChatRoomController.registerChatToMesssageList(context, cr_id, "1",   new Listener<Integer, String>() {
            @Override
            public void onCallBack(Integer integer, String reply) {
                if (integer == 1) {
                    Intent intent = new Intent();
                    intent.setClass(context, ChatActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_GROUP);
                    bundle.putString(EaseConstant.kCHAT_ROOM_INFO_ID, cr_id);//获取后台群聊参数ID
                    bundle.putString(EaseConstant.kGroupID, room_id);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                    if(b){  //目前找不到更好的办法来排序刚创建的群聊，如果不发一条信息，该群组就会排在最后（参考添加好友那里的逻辑，置顶）
                        //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
                        EMMessage message = EMMessage.createTxtSendMessage("Hi,我们可以一起聊天了", room_id);
                        //如果是群聊，设置chattype，默认是单聊
                        message.setChatType(EMMessage.ChatType.GroupChat);
                        //发送消息
                        EMClient.getInstance().chatManager().sendMessage(message);
                    }
                } else {
                    QLToastUtils.showToast(context, reply);
                }
            }
        });
    }

//    /**
//     * 打开群聊
//     *
//     * @param context
//     * @param cr_id
//     */
//    public static void openChatRoom(final Context context, final String room_id, final String cr_id, final String cr_meber_id , final String from) {
//        if (room_id == null || cr_id == null) {
//            QLToastUtils.showToast(context, "无法打开聊天室");
//            return;
//        }
//        /**
//         *
//         */
//        ChatRoomController.registerChatToMesssageList(context, cr_id, "1", new Listener<Integer, String>() {
//            @Override
//            public void onCallBack(Integer integer, String reply) {
//                if (integer == 1) {
//                    Intent intent = new Intent();
//                    intent.setClass(context, ChatActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_GROUP);
//                    bundle.putString(EaseConstant.kCHAT_ROOM_INFO_ID, cr_id);//获取后台群聊参数ID
//                    bundle.putString(EaseConstant.kGroupID, room_id);
//                    bundle.putString(ChatActivity.kChatCRID, cr_meber_id);
//                    if (!TextUtils.isEmpty(from)) {
//                        intent.putExtra(EaseConstant.FROM_ACTIVITY, EaseConstant.FROM_ACTIVITY);
//                    }
//                    intent.putExtras(bundle);
//                    context.startActivity(intent);
//                } else {
//                    QLToastUtils.showToast(context, reply);
//                }
//            }
//        });
//    }

    /**
     * 打开群聊
     *
     * @param context
     * @param cr_id
     */
    public static void openChatRoom(final Context context, final String room_id, final String cr_id, final String from) {
        if (room_id == null || cr_id == null) {
            QLToastUtils.showToast(context, "无法打开聊天室");
            return;
        }
        /**
         *
         */
        ChatRoomController.registerChatToMesssageList(context, cr_id, "1", new Listener<Integer, String>() {
            @Override
            public void onCallBack(Integer integer, String reply) {
                if (integer == 1) {
                    Intent intent = new Intent();
                    intent.setClass(context, ChatActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_GROUP);
                    bundle.putString(EaseConstant.kCHAT_ROOM_INFO_ID, cr_id);//获取后台群聊参数ID
                    bundle.putString(EaseConstant.kGroupID, room_id);
                    if (!TextUtils.isEmpty(from)) {
                        intent.putExtra(EaseConstant.FROM_ACTIVITY, EaseConstant.FROM_ACTIVITY);
                    }
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                } else {
                    QLToastUtils.showToast(context, reply);
                }
            }
        });
    }


    /**
     * 加入群聊
     *
     * @param context
     * @param info
     */
    public static void joinChatRoom(final Context context, final YYChatRoomInfo info) {
        UserInfo userInfo = UserManagerController.getCurrUserInfo();
        ChatRoomController.addMemberToChatRoom(context, info.getCr_id(), userInfo.getWp_member_info_id(), new Listener<Integer, String>() {
            @Override
            public void onCallBack(Integer status, String reply) {
                if (status == -1) {
                    String msg = reply == null ? "加入聊天室失败" : reply;
                    QLToastUtils.showToast(context, msg);
                    return;
                }
                openChatRoom(context, info);
            }
        });
    }

    // 减少二维码的密集度
    public static void joinChatRoomUserId(final Context context, final String cr_id) {
        UserInfo userInfo = UserManagerController.getCurrUserInfo();
        ChatRoomController.addMemberToChatRoom(context, cr_id, userInfo.getWp_member_info_id(), new Listener<Integer, String>() {
            @Override
            public void onCallBack(Integer status, String reply) {
                if (status == -1) {
                    String msg = reply == null ? "加入聊天室失败" : reply;
                    QLToastUtils.showToast(context, msg);
                    return;
                }

                ChatRoomController.getChatRoomInfo(context, cr_id, new Listener<Void, YYChatRoomInfo>() {

                    @Override
                    public void onCallBack(Void status, YYChatRoomInfo reply) {
                        if (reply == null) {
                            QLToastUtils.showToast(context, "加入失败");
                            return;
                        }
                        openChatRoom(context, reply);

                    }
                });

                // openChatRoom(context, info);
            }
        });
    }

}
