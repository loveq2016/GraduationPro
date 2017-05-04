package app.logic.controller;

import android.content.Context;

import com.google.gson.reflect.TypeToken;

import org.QLConstant;
import org.ql.utils.network.QLHttpGet;
import org.ql.utils.network.QLHttpReply;
import org.ql.utils.network.QLHttpResult;
import org.ql.utils.network.QLHttpUtil;

import java.util.HashMap;
import java.util.List;

import app.config.http.HttpConfig;
import app.config.http.YYResponseData;
import app.logic.pojo.LivestreamInfo;
import app.logic.pojo.OrgListByBuilderInfo;
import app.utils.common.Listener;

/**
 * Created by GZYY on 17/2/15.
 */

public class LivestreamController {

    /**
     * 获取组织直播列表
     *
     * @param context
     * @param listener
     */
    public static void getLivestreamList(Context context, final Listener<String, List<LivestreamInfo>> listener) {

        QLHttpUtil httpUtil = new QLHttpGet(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.GET_LIVESTREAM_LIST));

        HashMap<String, Object> entity = new HashMap<>();
        entity.put("member_id", QLConstant.client_id);
        httpUtil.setUseCache(true);
        httpUtil.setEntity(entity);

        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                if (listener == null || listener.isCancel()) {
                    return;
                }

                String msg;
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null && responseData.isSuccess()) {
                    listener.onCallBack(null, responseData.parseData("root", new TypeToken<List<LivestreamInfo>>() {
                    }));
                    return;
                } else if (responseData != null) {
                    msg = responseData.getErrorMsg();
                } else {
                    msg = "位置错误";
                }

                listener.onCallBack(msg, null);
            }
        });
    }

    /**
     * 获取用户创建的组织列表
     *
     * @param context
     * @param listener
     */
    public static void getOrgListByBuilder(Context context, final Listener<String, List<OrgListByBuilderInfo>> listener) {

        QLHttpUtil httpUtil = new QLHttpGet(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.GET_ORG_LIST_BY_BUILDER));

        HashMap<String, Object> entity = new HashMap<>();
        entity.put("member_id", QLConstant.client_id);
        httpUtil.setUseCache(true);
        httpUtil.setEntity(entity);
        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                if (listener == null || listener.isCancel()) {
                    return;
                }
                String msg;
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null && responseData.isSuccess()) {
                    listener.onCallBack(null, responseData.parseData("root", new TypeToken<List<OrgListByBuilderInfo>>() {
                    }));
                    return;
                } else if (responseData != null) {
                    msg = responseData.getErrorMsg();
                } else {
                    msg = "位置错误";
                }
                listener.onCallBack(msg, null);
            }
        });
    }


    /**
     * 创建直播
     *
     * @param context
     * @param org_id
     * @param room_id
     * @param listener
     */
    public static void createLiveStream(Context context, String org_id, String room_id,String plug_id ,String live_id, final Listener<Boolean, String> listener) {
        QLHttpUtil httpUtil = new QLHttpGet(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.CREATE_LIVESTREAM));
        httpUtil.setUseCache(false);
        HashMap<String, Object> entity = new HashMap<>();
        entity.put("member_id", QLConstant.client_id);
        entity.put("org_id", org_id);
        entity.put("room_id", room_id);//
        entity.put("plug_id", plug_id);
        entity.put("live_id", live_id);
        httpUtil.setEntity(entity);
        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                if (listener == null || listener.isCancel()) {
                    return;
                }
                String msg;
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null && responseData.isSuccess()) {
                    listener.onCallBack(true, null);
                    return;
                } else if (responseData != null) {
                    msg = responseData.getErrorMsg();
                } else {
                    msg = "未知错误";
                }
                listener.onCallBack(false, msg);
            }
        });
    }

    /**
     * 关闭直播
     * @param context
     * @param org_id
     * @param
     * @param
     */
    public static void colseLiveStream(Context context , String org_id,  final Listener<Boolean, String> listener) {
        QLHttpUtil httpUtil = new QLHttpGet(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.COLSE_LIVESTREAM));
        httpUtil.setUseCache( false );
        HashMap<String, Object> entity = new HashMap<>();
        entity.put("member_id", QLConstant.client_id );
        entity.put("org_id", org_id );
        httpUtil.setEntity( entity );
        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                if (listener == null || listener.isCancel()) {
                    return;
                }
                String msg;
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null && responseData.isSuccess()) {
                    listener.onCallBack(true, null);
                    return;
                } else if (responseData != null) {
                    msg = responseData.getErrorMsg();
                } else {
                    msg = "未知错误";
                }
                listener.onCallBack(false, msg);
            }
        });
    }

    public static void setLiveInfo(Context context, String live_id, String cover_id,String title , final Listener<Boolean, String> listener) {
        QLHttpUtil httpUtil = new QLHttpGet(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.SET_LIVE_INFO));
        httpUtil.setUseCache(false);
        HashMap<String, Object> entity = new HashMap<>();
        entity.put("wp_member_info_id", QLConstant.client_id);
        entity.put("live_id", live_id);
        entity.put("cover_id", cover_id);//
        entity.put("title", title);
        entity.put("token", QLConstant.token);
        httpUtil.setEntity(entity);
        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                if (listener == null || listener.isCancel()) {
                    return;
                }
                String msg;
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null && responseData.isSuccess()) {
                    listener.onCallBack(true, null);
                    return;
                } else if (responseData != null) {
                    msg = responseData.getErrorMsg();
                } else {
                    msg = "未知错误";
                }
                listener.onCallBack(false, msg);
            }
        });
}

}
