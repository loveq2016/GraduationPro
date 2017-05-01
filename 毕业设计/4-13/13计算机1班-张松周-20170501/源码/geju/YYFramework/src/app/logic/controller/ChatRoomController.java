package app.logic.controller;

import java.util.HashMap;
import java.util.List;

import org.QLConstant;
import org.ql.utils.network.QLHttpGet;
import org.ql.utils.network.QLHttpPost;
import org.ql.utils.network.QLHttpReply;
import org.ql.utils.network.QLHttpResult;
import org.ql.utils.network.QLHttpUtil;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.exceptions.HyphenateException;

import android.content.Context;
import android.content.Intent;

import app.config.http.HttpConfig;
import app.config.http.YYResponseData;
import app.logic.pojo.YYChatRoomInfo;
import app.logic.pojo.YYChatSessionInfo;
import app.utils.common.Listener;

/**
 * SiuJiYung create at 2016年7月1日 下午3:08:46
 */

public class ChatRoomController {


    /**
     * 获取聊天室列表
     *
     * @param context
     * @param callback
     */
    public static void getChatRoomList(Context context, final Listener<Void, List<YYChatRoomInfo>> callback) {
        QLHttpUtil httpUtil = new QLHttpGet(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.GET_CHAT_ROOM_LIST));
        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        httpUtil.setEntity(entity);
        httpUtil.setUseCache(false);
        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                if (callback == null || callback.isCancel()) {
                    return;
                }
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null && responseData.isSuccess()) {
                    List<YYChatRoomInfo> chatList = responseData.parseData("root", new TypeToken<List<YYChatRoomInfo>>() {
                    });
                    callback.onCallBack(null, chatList);
                    return;
                }
                callback.onCallBack(null, null);
            }
        });
    }

    /**
     * 获取聊天室信息
     *
     * @param context
     * @param cr_id
     * @param callback
     */
    public static void getChatRoomInfo(Context context, String cr_id, final Listener<Void, YYChatRoomInfo> callback) {
        QLHttpUtil httpUtil = new QLHttpGet(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.GET_CHAT_ROOM_INFO));
        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        entity.put("cr_id", cr_id);
        httpUtil.setEntity(entity);
        httpUtil.setUseCache(false);
        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                if (callback == null || callback.isCancel()) {
                    return;
                }
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null && responseData.isSuccess()) {
                    List<YYChatRoomInfo> info = responseData.parseData("root", new TypeToken<List<YYChatRoomInfo>>() {
                    });
                    if (info != null && info.size() > 0) {
                        callback.onCallBack(null, info.get(0));
                        return;
                    }
                }
                callback.onCallBack(null, null);
            }
        });
    }

    /**
     * 创建聊天室
     *
     * @param context
     * @param info
     * @param
     */
    public static void createChatRoom(Context context, YYChatRoomInfo info ,int type , final Listener<Integer, YYChatRoomInfo> callback) {
        QLHttpUtil httpUtil = new QLHttpPost(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.CREATE_CHAT_ROOM));
        Gson gson = new Gson();
        String roomInfoString = gson.toJson(info);

        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        entity.put("info", roomInfoString);
        entity.put("type", type);
        httpUtil.setEntity(entity);
        httpUtil.setUseCache(false);
        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                if (callback == null || callback.isCancel()) {
                    return;
                }
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null && responseData.isSuccess()) {
                    YYChatRoomInfo info = responseData.parseData("root", new TypeToken<YYChatRoomInfo>() {});
                    callback.onCallBack(1, info);
                    return;
                }
                callback.onCallBack(-1 , null );
            }
        });
    }

    /**
     * 添加聊天室成员
     *
     * @param context
     * @param cr_id
     * @param addMemberIDs
     * @param callback
     */
    public static void addMemberToChatRoom(Context context, String cr_id,String addMemberIDs, final Listener<Integer, String> callback) {
        QLHttpUtil httpUtil = new QLHttpGet(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.ADD_MEMBER_TO_CHAT_ROOM));
        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        entity.put("add_member_info_id", addMemberIDs);
        entity.put("cr_id", cr_id);
        httpUtil.setEntity(entity);
        httpUtil.setUseCache(false);
        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                if (callback == null || callback.isCancel()) {
                    return;
                }
                String msg = "未知错误";
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null && responseData.isSuccess()) {
                    callback.onCallBack(1, null);
                    return;
                } else if (responseData != null) {
                    msg = responseData.getErrorMsg();
                }
                callback.onCallBack(-1, msg);
            }
        });
        //在聊天服务器添加人
		//EMChatManager.getInstance().joinChatRoom(cr_id, null);
		//EMClient.getInstance().groupManager().joinGroup(cr_id);//需异步处理
        //String[] strings = addMemberIDs.split(",");
        //私有群里，如果开放了群成员邀请，群成员邀请调用下面方法
        //try {
            //EMClient.getInstance().groupManager().inviteUser(cr_id, strings, null);
            //EMClient.getInstance().groupManager().addUsersToGroup(cr_id , strings);//需异步处理
        //} catch (HyphenateException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        //}
    }

    /**
     * 移除聊天室成员
     *
     * @param context
     * @param add_member_info_ids
     * @param cr_id
     * @param callback
     */
    public static void removeMemberFromChatRoom(Context context, String add_member_info_ids, String cr_id, final Listener<Integer, String> callback) {
        QLHttpUtil httpUtil = new QLHttpGet(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.REMOVE_MEMBER_FROM_CHAT_ROOM));

        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        entity.put("remove_member_info_id", add_member_info_ids);
        entity.put("cr_id", cr_id);

        httpUtil.setEntity(entity);
        httpUtil.setUseCache(false);
        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                if (callback == null || callback.isCancel()) {
                    return;
                }
                String msg = null;
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null && responseData.isSuccess()) {
                    callback.onCallBack(1, null);
                    return;
                } else if (responseData != null) {
                    msg = responseData.getErrorMsg();
                }
                callback.onCallBack(-1, msg);
            }
        });
    }


    /**
     * 解散聊天室
     *
     * @param context
     * @param cr_id
     * @param callback
     */
    public static void removeChatRoom(Context context, String cr_id, final Listener<Integer, String> callback) {
        QLHttpUtil httpUtil = new QLHttpGet(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.REMOVE_CHAT_ROOM));

        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        entity.put("cr_id", cr_id);

        httpUtil.setEntity(entity);
        httpUtil.setUseCache(false);
        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                if (callback == null || callback.isCancel()) {
                    return;
                }
                String msg = null;
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null && responseData.isSuccess()) {
                    callback.onCallBack(1, null);
                    return;
                } else if (responseData != null) {
                    msg = responseData.getErrorMsg();
                }
                callback.onCallBack(-1, msg);
            }
        });
    }

    /**
     * 将群组注册到消息列表
     *
     * @param context
     * @param cr_id
     * @param isDisplay
     * @param listener
     */
    public static void registerChatToMesssageList(Context context, String cr_id, String isDisplay, final Listener<Integer, String> listener) {
        QLHttpUtil httpUtil = new QLHttpPost(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.REGISTE_CHAT_GROUP_TO_MESSAGELIST));
        httpUtil.setUseCache(true);
        HashMap<String, Object> entity = new HashMap<>();
        entity.put("wp_member_info_id", QLConstant.client_id);
        entity.put("isDisplay", isDisplay);
        entity.put("cr_id", cr_id);
        httpUtil.setEntity(entity);
        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                if (listener == null || listener.isCancel()) {
                    return;
                }
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null && responseData.isSuccess()) {
                    listener.onCallBack(1, null);
                    return;
                }
                if( responseData != null ){
                    listener.onCallBack(-1, responseData.getErrorMsg());
                }
            }
        });
    }


    /**
     * 将群组注册到消息列表 2
     *
     * @param context
     * @param room_id   环信
     * @param cr_id     后台
     * @param isDisplay
     * @param listener
     */
    public static void registerChatToMessageList(Context context, String room_id, String cr_id, String isDisplay, final Listener<Integer, String> listener) {
        QLHttpUtil httpUtil = new QLHttpPost(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.REGISTE_CHAT_GROUP_TO_MESSAGELIST));
        httpUtil.setUseCache(true);
        HashMap<String, Object> entity = new HashMap<>();
        entity.put("wp_member_info_id", QLConstant.client_id);
        entity.put("isDisplay", isDisplay);
        entity.put("cr_id", cr_id);
        entity.put("room_id", room_id);
        httpUtil.setEntity(entity);
        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                if (listener == null || listener.isCancel()) {
                    return;
                }
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null && responseData.isSuccess()) {
                    listener.onCallBack(1, null);
                    return;
                }
                if( responseData != null ){
                    listener.onCallBack(-1, responseData.getErrorMsg());
                }
            }
        });
    }

    /**
     * 修改群的昵称
     * @param context
     * @param
     * @param room_id
     * @param cr_name
     * @param listener
     */
    public static void modifyRoomName(Context context  , String room_id , String cr_name, final Listener< Boolean , String> listener){
        QLHttpUtil httpUtil = new QLHttpPost(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.MODIYF_CAHT_ROOM_NAME));
        httpUtil.setUseCache(true);
        HashMap<String, Object> entity = new HashMap<>();
        entity.put("member_id",QLConstant.client_id );
        entity.put("room_id", room_id);
        entity.put("cr_name", cr_name);
        httpUtil.setEntity(entity);
        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                if (listener == null || listener.isCancel()) {
                    return;
                }
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null && responseData.isSuccess()) {
                    listener.onCallBack(true, null);
                }else if( responseData != null ){
                    listener.onCallBack(false , responseData.getErrorMsg());
                }else {
                    listener.onCallBack(false , "未知错误");
                }
            }
        });

    }

}
