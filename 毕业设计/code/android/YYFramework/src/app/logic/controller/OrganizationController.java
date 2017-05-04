package app.logic.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.QLConstant;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ql.utils.network.QLHttpGet;
import org.ql.utils.network.QLHttpPost;
import org.ql.utils.network.QLHttpReply;
import org.ql.utils.network.QLHttpResult;
import org.ql.utils.network.QLHttpUtil;

import app.logic.activity.live.CarouselImgInfo;
import app.logic.activity.live.IsOnLiveOrgInfo;
import app.logic.pojo.DepartmentInfoYSF;
import app.logic.pojo.IsOrgMember;
import app.logic.pojo.OrgNotifyInfo;
import u.aly.co;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.utils.L;

import android.R.integer;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import app.config.http.HttpConfig;
import app.config.http.YYResponseData;
import app.logic.activity.org.DPMListActivity;
import app.logic.pojo.DepartmentInfo;
import app.logic.pojo.JoinRequestListInfo;
import app.logic.pojo.OrgRequestMemberInfo;
import app.logic.pojo.OrgUnreadNumberInfo;
import app.logic.pojo.OrganizationInfo;
import app.logic.pojo.UserInfo;
import app.logic.pojo.YYChatRoomInfo;
import app.utils.common.Listener;
import app.utils.file.YYFileManager;

/**
 * SiuJiYung create at 2016年6月15日 下午7:42:51
 */

public class OrganizationController {

    /**
     * 获取我加入的组织列表
     *
     * @param context
     * @param listener
     */
    public static void getMyOrganizationList(Context context, final Listener<Void, List<OrganizationInfo>> listener) {
        QLHttpUtil httpUtil = new QLHttpGet(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.GET_ORG_LIST));
        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        httpUtil.setEntity(entity);
//		httpUtil.setUseCache(true);
        httpUtil.setUseCache(false);
        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                if (listener == null || listener.isCancel()) {
                    return;
                }
                String msg = null;
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null && responseData.isSuccess()) {
                    List<OrganizationInfo> list = responseData.parseData("root", new TypeToken<List<OrganizationInfo>>() {
                    });
                    listener.onCallBack(null, list);
                    return;
                }
                listener.onCallBack(null, null);
            }
        });
    }

    /**
     * 获取所有组织列表
     *
     * @param context
     * @param start
     * @param limitCount
     * @param listener
     */
    public static void getOrganizationList(Context context, int start, int limitCount, final Listener<Void, List<OrganizationInfo>> listener) {
        QLHttpUtil httpUtil = new QLHttpGet(context);
        // 接口和参数没有处理
        httpUtil.setUrl(HttpConfig.getUrl(""));

        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);

        httpUtil.setEntity(entity);
        httpUtil.setUseCache(false);
        httpUtil.startConnection(new QLHttpResult() {

            @Override
            public void reply(QLHttpReply reply) {
                if (listener == null && listener.isCancel()) {
                    return;
                }
                String msg = null;
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null && responseData.isSuccess()) {
                    List<OrganizationInfo> list = responseData.parseData("root", new TypeToken<List<OrganizationInfo>>() {
                    });
                    listener.onCallBack(null, list);
                    return;
                }
                listener.onCallBack(null, null);

            }
        });
    }

    /**
     * 获取组织信息
     *
     * @param context
     * @param org_id   组织ID
     * @param listener
     */
    public static void getOrganizationInfo(final Context context, String org_id, final Listener<Void, List<OrganizationInfo>> listener) {
        QLHttpUtil httpUtil = new QLHttpGet(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.GET_ORG_INFO));

        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        entity.put("org_id", org_id);

        httpUtil.setEntity(entity);
        httpUtil.setUseCache(false);
        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                if (listener == null || listener.isCancel()) {
                    return;
                }
                String msg = null;
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null && responseData.isSuccess()) {
                    List<OrganizationInfo> list = responseData.parseData("root", new TypeToken<List<OrganizationInfo>>() {
                    });
                    listener.onCallBack(null, list);
                    return;
                }
                listener.onCallBack(null, null);

            }
        });
    }

    public static void deleteOrganizationInfo(final Context context, String org_id, final Listener<Boolean, String> listener) {
        QLHttpUtil httpUtil = new QLHttpGet(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.DELETE_ORG_INFO));

        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("member_id", QLConstant.client_id);
        entity.put("wp_member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        entity.put("org_id", org_id);

        httpUtil.setEntity(entity);
        httpUtil.setUseCache(false);
        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                if (listener == null || listener.isCancel()) {
                    return;
                }
                String msg = null;
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null && responseData.isSuccess()) {
                    listener.onCallBack(responseData.isSuccess(), "成功");
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
     * 搜索组织
     *
     * @param context
     * @param searchKey 搜索关键字
     * @param listener
     */
    public static void searchOrganizations(Context context, String searchKey, String search_id, final Listener<Integer, List<OrganizationInfo>> listener) {
        QLHttpUtil httpUtil = new QLHttpGet(context);
//		QLHttpUtil httpUtil = new QLHttpPost(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.SEARCH_ORG_LIST_NEW));
        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("member_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        entity.put("keyword", searchKey);
        entity.put("search_id", search_id);
        httpUtil.setEntity(entity);
        httpUtil.setUseCache(true);
        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                if (listener == null || listener.isCancel()) {
                    return;
                }
                String msg = null;
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null && responseData.isSuccess()) {
                    List<OrganizationInfo> list = responseData.parseData("root", new TypeToken<List<OrganizationInfo>>() {
                    });
                    listener.onCallBack(1, list);
                    return;
                }
                listener.onCallBack(-1, null);
            }
        });
    }

    /**
     * 申请加入组织
     *
     * @param context
     * @param org_id   申请组织ID
     * @param msg      验证消息
     * @param listener
     */
    public static void joinOrganization(Context context, String org_id, String msg, final Listener<Boolean, String> listener) {
        QLHttpUtil httpUtil = new QLHttpGet(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.JOIN_ORG));

        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("member_info_id", TextUtils.isEmpty(QLConstant.client_id) ? UserManagerController.getCurrUserInfo().getId() : QLConstant.client_id);

        entity.put("token", QLConstant.token);
        entity.put("org_id", org_id);
        entity.put("request_msg", msg);

        httpUtil.setEntity(entity);
        httpUtil.setUseCache(false);
        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                if (listener == null || listener.isCancel()) {
                    return;
                }
                String msg = null;
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null && responseData.isSuccess()) {
                    listener.onCallBack(responseData.isSuccess(), null);
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
     * 处理入会申请
     *
     * @param context
     * @param request_id     申请单ID
     * @param respone_action 处理动作，1：同意加入；0：拒绝加入；2：不再显示该用户加入请求
     * @param listener
     */
    public static void responseJoinOrganizationRequest(Context context, String request_id, int respone_action, final Listener<Boolean, String> listener) {

        QLHttpUtil httpUtil = new QLHttpGet(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.REQUEST_JOIN_ORG));

        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        entity.put("request_id", request_id);
        entity.put("respone_action", respone_action);

        httpUtil.setEntity(entity);
        httpUtil.setUseCache(false);
        httpUtil.startConnection(new QLHttpResult() {

            @Override
            public void reply(QLHttpReply reply) {
                if (listener == null || listener.isCancel()) {
                    return;
                }
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null && responseData.isSuccess()) {
                    listener.onCallBack(responseData.isSuccess(), null);
                    return;
                }
                listener.onCallBack(responseData.isSuccess(), responseData.getErrorMsg());// responseData.getError();

            }
        });

    }

    public static void replaceResponseJoinOrganizationRequest(Context context, String request_id, int respone_action, final Listener<Boolean, String> listener) {

        QLHttpUtil httpUtil = new QLHttpGet(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.REQUEST_JOIN_ORG));

        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        entity.put("request_id", request_id);
        entity.put("respone_action", respone_action);

        httpUtil.setEntity(entity);
        httpUtil.setUseCache(false);
        httpUtil.startConnection(new QLHttpResult() {

            @Override
            public void reply(QLHttpReply reply) {
                if (listener == null || listener.isCancel()) {
                    return;
                }
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null && responseData.isSuccess()) {
                    listener.onCallBack(responseData.isSuccess(), null);
                    return;
                }
                listener.onCallBack(responseData.isSuccess(), responseData.getErrorMsg());// responseData.getError();

            }
        });

    }

    /**
     * 创建组织
     *
     * @param context
     * @param org_info
     * @param org_cer_img_path
     * @param org_owner_id_img_path
     * @param org_logo_path
     * @param listener
     */
    public static void createOrganization(Context context, String org_info, String org_cer_img_path, String org_owner_id_img_path, String org_logo_path,
                                          final Listener<Boolean, OrganizationInfo> listener) {

        QLHttpPost httpUtil = new QLHttpPost(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.CREATE_ORG));

        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);

        String org_cer_img = YYFileManager.imgToBase64(org_cer_img_path);
        String owner_id_img = YYFileManager.imgToBase64(org_owner_id_img_path);
        String logo_img = YYFileManager.imgToBase64(org_logo_path);

        entity.put("info", org_info);
        entity.put("org_certificate_img", org_cer_img);
        entity.put("contact_id_img", owner_id_img);
        if (logo_img != null) {
            entity.put("org_logo", logo_img);
        }

        httpUtil.setEntity(entity);
        httpUtil.setUseCache(false);
        httpUtil.startConnection(new QLHttpResult() {

            @Override
            public void reply(QLHttpReply reply) {
                if (listener == null || listener.isCancel()) {
                    return;
                }
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null && responseData.isSuccess()) {
                    OrganizationInfo oInfo = responseData.parseData("root", new TypeToken<OrganizationInfo>() {
                    });
                    listener.onCallBack(responseData.isSuccess(), oInfo);
                    return;
                }
                listener.onCallBack(false, null);
            }
        });
    }

    /**
     * 创建组织
     *
     * @param context
     * @param org_info
     * @param org_cer_img_path
     * @param org_owner_id_img_path
     * @param org_logo_path
     * @param listener
     */
    public static void createOrganizationNew(Context context, String org_id, String org_info, String org_cer_img_path, String org_owner_id_img_path, String org_logo_path,
                                             final Listener<Boolean, OrganizationInfo> listener) {

        QLHttpPost httpUtil = new QLHttpPost(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.CREATE_ORG));

        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);

        entity.put("org_id", org_id); //新增

        String org_cer_img = YYFileManager.imgToBase64(org_cer_img_path);
        String owner_id_img = YYFileManager.imgToBase64(org_owner_id_img_path);
        String logo_img = YYFileManager.imgToBase64(org_logo_path);

        entity.put("info", org_info);
        entity.put("org_certificate_img", org_cer_img);
        entity.put("contact_id_img", owner_id_img);
        if (logo_img != null) {
            entity.put("org_logo", logo_img);
        }

        httpUtil.setEntity(entity);
        httpUtil.setUseCache(false);
        httpUtil.startConnection(new QLHttpResult() {

            @Override
            public void reply(QLHttpReply reply) {
                if (listener == null || listener.isCancel()) {
                    return;
                }
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null && responseData.isSuccess()) {
                    OrganizationInfo oInfo = responseData.parseData("root", new TypeToken<OrganizationInfo>() {
                    });
                    listener.onCallBack(responseData.isSuccess(), oInfo);
                    return;
                }
                listener.onCallBack(false, null);
            }
        });
    }

    /**
     * 创建组织New
     *
     * @param context
     * @param org_info
     * @param listener
     */
    public static void createOrganization(Context context, String org_id, String org_info, String org_cer_img_id, String org_owner_id_img_id, String org_logo_id,
                                             final Listener<Boolean, OrganizationInfo> listener) {

        QLHttpPost httpUtil = new QLHttpPost(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.CREATE_ORG_NEW));

        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);

        entity.put("org_id", org_id); //新增

//        String org_cer_img = YYFileManager.imgToBase64(org_cer_img_path);
//        String owner_id_img = YYFileManager.imgToBase64(org_owner_id_img_path);
//        String logo_img = YYFileManager.imgToBase64(org_logo_path);

        entity.put("info", org_info);
        entity.put("org_certificate_img_url", org_cer_img_id);
        entity.put("contact_id_img_url", org_owner_id_img_id);
        entity.put("org_logo_url", org_logo_id);

        httpUtil.setEntity(entity);
        httpUtil.setUseCache(false);
        httpUtil.startConnection(new QLHttpResult() {

            @Override
            public void reply(QLHttpReply reply) {
                if (listener == null || listener.isCancel()) {
                    return;
                }
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null && responseData.isSuccess()) {
                    OrganizationInfo oInfo = responseData.parseData("root", new TypeToken<OrganizationInfo>() {
                    });
                    listener.onCallBack(responseData.isSuccess(), oInfo);
                    return;
                }
                listener.onCallBack(false, null);
            }
        });
    }


    /**
     * 退出组织
     *
     * @param context
     * @param org_id
     * @param member_info_id
     * @param listener
     */
    public static void exitOrganization(Context context, String org_id, String member_info_id, final Listener<Boolean, String> listener) {

        QLHttpUtil httpUtil = new QLHttpGet(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.EXIT_ORG));

        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        entity.put("org_id", org_id);
        entity.put("member_id", member_info_id);

        httpUtil.setEntity(entity);
        httpUtil.setUseCache(false);
        httpUtil.startConnection(new QLHttpResult() {

            @Override
            public void reply(QLHttpReply reply) {
                if (listener == null || listener.isCancel()) {
                    return;
                }
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null && responseData.isSuccess()) {
                    listener.onCallBack(responseData.isSuccess(), null);
                    return;
                }
                listener.onCallBack(responseData.isSuccess(), responseData.getErrorMsg());
            }
        });

    }

    /**
     * 添加分组成员
     *
     * @param context
     * @param org_id
     * @param add_member_info_id
     * @param listener
     */
    public static void addPersonToOrganization(Context context, String org_id, String add_member_info_id, final Listener<Boolean, String> listener) {

        QLHttpUtil httpUtil = new QLHttpGet(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.ADD_PERSON_ORG));

        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        entity.put("org_id", org_id);
        entity.put("add_member_info_id", add_member_info_id);

        httpUtil.setEntity(entity);
        httpUtil.setUseCache(false);
        httpUtil.startConnection(new QLHttpResult() {

            @Override
            public void reply(QLHttpReply reply) {
                if (listener == null || listener.isCancel()) {
                    return;
                }
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null && responseData.isSuccess()) {
                    listener.onCallBack(responseData.isSuccess(), null);
                    return;
                }
                if (responseData != null) {
                    listener.onCallBack(responseData.isSuccess(), responseData.getErrorMsg());
                }
            }
        });
    }

    /**
     * 删除组织成员
     *
     * @param context
     * @param org_id
     * @param rm_member_info_id
     * @param listener
     */
    public static void removePersionFromOrganization(Context context, String org_id, String rm_member_info_id, final Listener<Boolean, String> listener) {
        QLHttpUtil httpUtil = new QLHttpGet(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.GET_DEPARTMENT_LIST));

        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        entity.put("org_id", org_id);

        httpUtil.setEntity(entity);
        httpUtil.setUseCache(false);
        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                if (listener == null || listener.isCancel()) {
                    return;
                }
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null && responseData.isSuccess()) {
                    listener.onCallBack(responseData.isSuccess(), null);
                    return;
                }
                listener.onCallBack(responseData.isSuccess(), responseData.getErrorMsg());
                // String msg = null;
                // YYResponseData responseData =
                // YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                // if (responseData != null && responseData.isSuccess()) {
                // List<DepartmentInfo> info = responseData.parseData("root",
                // new TypeToken<List<DepartmentInfo>>(){});
                // listener.onCallBack(null, info);
                // return;
                // }
                // listener.onCallBack(null, null);
            }
        });
    }

    /**
     * 获取分组列表
     *
     * @param context
     * @param org_id
     * @param callback
     */
    public static void getMyDPMList(Context context, String org_id, final Listener<Void, List<DepartmentInfo>> callback) {
        QLHttpUtil httpUtil = new QLHttpGet(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.GET_DEPARTMENT_LIST));

        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        entity.put("org_id", org_id);

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
                    List<DepartmentInfo> info = responseData.parseData("root", new TypeToken<List<DepartmentInfo>>() {
                    });
                    callback.onCallBack(null, info);
                    return;
                }
                callback.onCallBack(null, null);
            }
        });
    }

    /**
     * 添加分组
     *
     * @param context
     * @param org_id
     * @param dpm_name
     * @param callback
     */
    public static void addDPM(Context context, String org_id, String dpm_name, final Listener<String, DepartmentInfo> callback) {
        QLHttpUtil httpUtil = new QLHttpGet(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.ADD_DEPARTMENT));

        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        entity.put("org_id", org_id);
        entity.put("dpm_name", dpm_name);
        entity.put("dpm_short_index", "0");

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

                    String jsonString = responseData.getSouceJsonString();
                    String[] strings = jsonString.split(":");
                    String id = null;
                    if (strings[strings.length - 1] != null) {
                        id = strings[strings.length - 1].substring(1, strings[strings.length - 1].length() - 2);
                    }
                    DepartmentInfo info = responseData.parseData("root", new TypeToken<DepartmentInfo>() {
                    });
                    callback.onCallBack(id, info);
                    return;
                } else if (responseData != null) {
                    msg = responseData.getErrorMsg();
                }
                callback.onCallBack(msg, null);
            }
        });
    }

    /**
     * 添加分组
     *
     * @param context
     * @param org_id
     * @param dpm_name
     * @param callback
     */
    public static void addDPMYSF(Context context, String org_id, String dpm_name, final Listener<String, DepartmentInfoYSF> callback) {
        QLHttpUtil httpUtil = new QLHttpGet(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.ADD_DEPARTMENT));
        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        entity.put("org_id", org_id);
        entity.put("dpm_name", dpm_name);
        entity.put("dpm_short_index", 0);
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
                    String jsonString = responseData.getSouceJsonString();
                    String[] strings = jsonString.split(":");
                    String id = null;
                    if (strings[strings.length - 1] != null) {
                        id = strings[strings.length - 1].substring(1, strings[strings.length - 1].length() - 2);
                    }
                    DepartmentInfoYSF info = responseData.parseData("root", new TypeToken<DepartmentInfoYSF>() {
                    });
                    callback.onCallBack(id, info);
                    return;
                } else if (responseData != null) {
                    msg = responseData.getErrorMsg();
                    callback.onCallBack(msg, null);
                }
            }
        });
    }

    /**
     * 删除分组
     *
     * @param context
     * @param org_id
     * @param dpm_id
     * @param callback
     */
    public static void removeDPM(Context context, String org_id, String dpm_id, final Listener<Integer, String> callback) {
        QLHttpUtil httpUtil = new QLHttpGet(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.REMOVE_DEPARTMENT));

        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        entity.put("org_id", org_id);
        entity.put("dpm_id", dpm_id);

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
                }
                callback.onCallBack(-1, null);
            }
        });
    }

    public static void getDPMMemberList(Context context, String org_id, String dpm_id, final Listener<Void, List<UserInfo>> callback) {
        QLHttpUtil httpUtil = new QLHttpGet(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.GET_DEPARTMENT_MEMBER_LIST));
        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        entity.put("org_id", org_id);
        entity.put("dpm_id", dpm_id);
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
					List<UserInfo> tmpList = responseData.parseData("root", new TypeToken<List<UserInfo>>() {
					});
					callback.onCallBack(null, tmpList);
					return;
				}
				callback.onCallBack(null, null);
			}
		});
	}

    /**
     * 添加成员到分组
     *
     * @param context
     * @param org_id
     * @param dpm_id
     * @param add_member_id
     * @param callback
     */
    public static void addMemberToDPM(Context context, String org_id, String dpm_id, String add_member_id, final Listener<Integer, String> callback) {
        QLHttpUtil httpUtil = new QLHttpPost(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.ADD_MEMBER_TO_DPM));

        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        entity.put("org_id", org_id);
        entity.put("dpm_id", dpm_id);
        entity.put("dpm_member_info_id", add_member_id);

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
//					callback.onCallBack( 1 , null );
                    callback.onCallBack(1, responseData.getMsg());
                    return;
                }
                if (responseData != null) {
                    callback.onCallBack(-1, responseData.getErrorMsg());
                }

            }
        });
    }

    /**
     * 移除分组成员
     *
     * @param context
     * @param org_id
     * @param dpm_id
     * @param mv_member_id
     * @param callback
     */
    public static void removeMemberFromDPM(Context context, String org_id, String dpm_id, String mv_member_id, final Listener<Integer, String> callback) {
        QLHttpUtil httpUtil = new QLHttpGet(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.REMOVE_MEMBER_FROM_DPM));

        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        entity.put("org_id", org_id);
        entity.put("dpm_id", dpm_id);
        entity.put("dpm_member_info_id", mv_member_id);

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
                }
                callback.onCallBack(-1, null);
            }
        });
    }

    /*
     * 设置管理员权限
     */
    public static void setAdmin(Context context, String org_id, String dpm_id, String dpm_member_info_id, int type, final Listener<Boolean, String> listener) {
        QLHttpUtil httpUtil = new QLHttpGet(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.GET_SET_ADMIN));
        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("org_id", org_id);
        entity.put("dpm_id", dpm_id);
        entity.put("member_info_id", QLConstant.client_id);
        entity.put("dpm_member_info_id", dpm_member_info_id);
        entity.put("isadmin", type);
        httpUtil.setEntity(entity);
        httpUtil.setUseCache(true);
        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                if (listener == null || listener.isCancel()) {
                    return;
                }
                String msg = null;
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null && responseData.isSuccess()) {
                    listener.onCallBack(responseData.isSuccess(), null);
                    return;
                } else if (responseData != null) {
                    msg = responseData.getError();
                } else {
                    msg = "未知错误";
                }
                listener.onCallBack(false, msg);
            }
        });
    }

    // 获取申请入会列表
    public static void getJoinRequestList(Context context, String org_id, final Listener<Void, List<JoinRequestListInfo>> listener) {
        QLHttpUtil httpUtil = new QLHttpGet(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.GET_JOINREQUEST));
        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        entity.put("org_id", org_id);
        httpUtil.setEntity(entity);
        httpUtil.setUseCache(false);
        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                if (listener == null || listener.isCancel()) {
                    return;
                }
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null && responseData.isSuccess()) {
                    List<JoinRequestListInfo> list = responseData.parseData("root", new TypeToken<List<JoinRequestListInfo>>() {
                    });
                    listener.onCallBack(null, list);
                    return;
                }
                listener.onCallBack(null, null);
            }
        });
    }

    // 清空申请列表
    public static void deleteJoinRequest(Context context, String org_id, final Listener<Integer, String> listener) {
        QLHttpUtil httpUtil = new QLHttpPost(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.GET_EMPTY_APPLY_LIST));
        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        entity.put("org_id", org_id);
        httpUtil.setEntity(entity);
        httpUtil.setUseCache(false);
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
                } else if (responseData != null) {
                    listener.onCallBack(-1, responseData.getErrorMsg());
                } else {
                    listener.onCallBack(-1, "未知错误");
                }
            }
        });
    }

    // 更改组织logo
    public static void replaceOrgInfoLogo(Context context, String org_id, String user_info, String org_logo, final Listener<Boolean, String> listener) {
        QLHttpUtil httpUtil = new QLHttpPost(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.REPLACE_ORG_LOGO));
        String org_logo_base = YYFileManager.imgToBase64(org_logo);
        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        entity.put("org_id", org_id);
        entity.put("org_logo", org_logo_base);
        httpUtil.setEntity(entity);
        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                if (listener == null || listener.isCancel()) {
                    return;
                }
                String msg = null;
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null && responseData.isSuccess()) {
                    listener.onCallBack(responseData.isSuccess(), null);
                    return;
                } else if (responseData != null) {
                    msg = responseData.getError();
                } else {
                    msg = "未知错误";
                }
                listener.onCallBack(false, msg);
            }
        });
    }

    // 获取组织所有成员
    public static void getOrgMemberList(Context context, String org_id, final Listener<Void, List<OrgRequestMemberInfo>> listenner) {
        QLHttpUtil httpUtil = new QLHttpGet(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.GET_ORG_ALL_NAME));
        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        entity.put("associationId", org_id);
        httpUtil.setEntity(entity);
        httpUtil.startConnection(new QLHttpResult() {

            @Override
            public void reply(QLHttpReply reply) {
                if (listenner == null || listenner.isCancel()) {
                    return;
                }
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null && responseData.isSuccess()) {
                    List<OrgRequestMemberInfo> list = responseData.parseData("root", new TypeToken<List<OrgRequestMemberInfo>>() {
                    });
                    listenner.onCallBack(null, list);
                    return;
                }
                listenner.onCallBack(null, null);
            }
        });
    }

    // 退出组织
    public static void delectOrgMember(Context context, String org_id, String member_id, final Listener<Boolean, String> listener) {
        QLHttpUtil httpUtil = new QLHttpGet(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.DELECT_ORG_MEMBER));
        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        entity.put("org_id", org_id);
        entity.put("member_id", member_id);
        httpUtil.setEntity(entity);
        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                if (listener == null || listener.isCancel()) {
                    return;
                }
                String msg = null;
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null && responseData.isSuccess()) {
                    listener.onCallBack(responseData.isSuccess(), null);
                    return;
                } else if (responseData != null) {
                    msg = responseData.getError();
                }
                listener.onCallBack(false, msg);
            }
        });
    }

    // 修改组织信息
    public static void updateAssociationInfo(Context context, String info, final Listener<Boolean, String> listener) {
        QLHttpUtil httpUtil = new QLHttpGet(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.UPDATAORG_INFO));
        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("member_info_id", QLConstant.client_id);
        entity.put("info", info);
        httpUtil.setEntity(entity);
        httpUtil.setUseCache(false);
        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                if (listener == null && listener.isCancel()) {
                    return;
                }
                String msg = null;
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null && responseData.isSuccess()) {
                    listener.onCallBack(responseData.isSuccess(), null);
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

    // 修改分组信息
    public static void updateDepartment(Context context, String org_id, String departmentId, String dpm_name, final Listener<Boolean, String> listener) {
        QLHttpUtil httpUtil = new QLHttpGet(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.UPDATA_DPM));

        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("member_info_id", QLConstant.client_id);
        entity.put("org_id", org_id);
        entity.put("departmentId", departmentId);
        entity.put("dpm_name", dpm_name);
        httpUtil.setEntity(entity);
        httpUtil.setUseCache(false);
        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                if (listener == null && listener.isCancel()) {
                    return;
                }
                String msg = null;
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null && responseData.isSuccess()) {
                    listener.onCallBack(responseData.isSuccess(), null);
                    return;
                } else if (responseData != null) {
                    msg = responseData.getErrorMsg();
                } else {
                    msg = "未知错误";
                }
                listener.onCallBack(responseData.isSuccess(), msg);
            }
        });
    }

	/*
     * 获取各个组织的公告未读数量
	 */

    public static void getOrgUnreadNumber(Context context, final Listener<Integer, List<OrgUnreadNumberInfo>> listener) {
        QLHttpUtil httpUtil = new QLHttpGet(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.ORG_UNREAD_COUNT));

        HashMap<String, Object> entity = new HashMap<>();
        entity.put("member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        entity.put("start", 0);
        entity.put("limit", 1000);
        entity.put("msg_type", 1);

        httpUtil.setEntity(entity);
        httpUtil.setUseCache(true);

        httpUtil.startConnection(new QLHttpResult() {

            @Override
            public void reply(QLHttpReply reply) {
                if (listener == null || listener.isCancel()) {
                    return;
                }
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null) {
                    List<OrgUnreadNumberInfo> list = responseData.parseData("root", new TypeToken<List<OrgUnreadNumberInfo>>() {
                    });
                    listener.onCallBack(1, list);
                    return;
                }
                listener.onCallBack(-1, null);

            }
        });
    }

    /*
     * 获取各个组织的入会申请列表
	 */

    public static void getOrgJoinRequest(Context context,final Listener<Integer, List<OrgUnreadNumberInfo>> listener) {
        QLHttpUtil httpUtil = new QLHttpGet(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.ORG_JOIN_REQURST));

        HashMap<String, Object> entity = new HashMap<>();
        entity.put("member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);

        httpUtil.setEntity(entity);
        httpUtil.setUseCache(true);

        httpUtil.startConnection(new QLHttpResult() {

            @Override
            public void reply(QLHttpReply reply) {
                if (listener == null || listener.isCancel()) {
                    return;
                }
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null) {
                    List<OrgUnreadNumberInfo> list = responseData.parseData("root", new TypeToken<List<OrgUnreadNumberInfo>>() {
                    });
                    listener.onCallBack(1, list);
                    return;
                }
                listener.onCallBack(-1, null);

            }
        });
    }

    /**
     * 获取正在直播列表
     * @param context
     * @param listener
     */
    public static void getIsOnLiveList(Context context , final Listener< Integer ,ArrayList<IsOnLiveOrgInfo>> listener){
        QLHttpUtil httpUtil = new QLHttpGet(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.GET_LIVESTREAM_LIST));

        HashMap<String, Object> entity = new HashMap<>();
        entity.put("member_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        httpUtil.setEntity(entity);
        httpUtil.setUseCache(false);

        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                if (listener == null || listener.isCancel()) {
                    return;
                }
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null) {
                    ArrayList<IsOnLiveOrgInfo> list = responseData.parseData("root", new TypeToken<ArrayList<IsOnLiveOrgInfo>>() {});
                    listener.onCallBack(1, list);
                    return;
                }
                listener.onCallBack(-1, null);
            }
        });
    }

    /**
     * 获取正在直播列表
     * @param context
     * @param listener
     */
    public static void getIsOnLiveAllList(Context context ,int start , int limit , final Listener< Integer ,ArrayList<IsOnLiveOrgInfo>> listener){
        QLHttpUtil httpUtil = new QLHttpGet(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.GET_LIVESTREAM_ALLLIST));
        HashMap<String, Object> entity = new HashMap<>();
        entity.put("member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        entity.put("start", start );
        entity.put("limit", limit );
        httpUtil.setEntity(entity);
        httpUtil.setUseCache(false);

        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                if (listener == null || listener.isCancel()) {
                    return;
                }
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null) {
                    ArrayList<IsOnLiveOrgInfo> list = responseData.parseData("root", new TypeToken<ArrayList<IsOnLiveOrgInfo>>() {});
                    listener.onCallBack(1, list);
                    return;
                }
                listener.onCallBack(-1, null);
            }
        });
    }


    /**
     * 获取当前用户创建的组织列表
     * @param context
     * @param listener
     */
    public static void getUserCreatOrgList(Context context , final Listener< Integer ,ArrayList<OrganizationInfo>> listener){
        QLHttpUtil httpUtil = new QLHttpGet(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.GET_ORG_LIST_BY_BUILDER));

        HashMap<String, Object> entity = new HashMap<>();
        entity.put("member_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        httpUtil.setEntity(entity);
        httpUtil.setUseCache(false);

        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                if (listener == null || listener.isCancel()) {
                    return;
                }
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null) {
                    ArrayList<OrganizationInfo> list = responseData.parseData("root", new TypeToken<ArrayList<OrganizationInfo>>() {});
                    listener.onCallBack(1, list);
                    return;
                }
                listener.onCallBack(-1, null);
            }
        });
    }

    /**
     * 直播状态监听
     * @param context
     * @param org_id
     * @param listener
     */
    public static void liveStateLister(Context context , String org_id , final Listener<Boolean , String> listener) {
        QLHttpUtil httpUtil = new QLHttpGet(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.LIEV_STATE_LISTER));

        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("org_id", org_id);
        httpUtil.setEntity(entity);
        httpUtil.setUseCache(false);
        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                if (listener == null && listener.isCancel()) {
                    return;
                }
                String msg = null;
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null && responseData.isSuccess()) {
                    listener.onCallBack(responseData.isSuccess(), null);
                    return;
                } else if (responseData != null) {
                    msg = responseData.getErrorMsg();
                } else {
                    msg = "未知错误";
                }
                if( responseData != null ){
                    listener.onCallBack(responseData.isSuccess(), msg);
                }
            }
        });
    }

    /**
     * 获取用户是否组织成员
     * @param context
     * @param listener
     */
    public static void getIsOrgMember(Context context ,String org_id, final Listener< Boolean ,Void> listener){
        QLHttpUtil httpUtil = new QLHttpGet(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.IS_THE_ORG_MEMBER));

        HashMap<String, Object> entity = new HashMap<>();
        entity.put("member_id", QLConstant.client_id);
        entity.put("org_id", org_id);
        httpUtil.setEntity(entity);
        httpUtil.setUseCache(false);

        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                if (listener == null || listener.isCancel()) {
                    return;
                }
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null && responseData.isSuccess()) {
                    List<IsOrgMember> isOrgMember = responseData.parseData("root", new TypeToken<List<IsOrgMember>>() {});
                    listener.onCallBack(isOrgMember.get(0).is_org_member(), null);
                    return;
                }
                listener.onCallBack(false, null);
            }
        });
    }

    /**
     * 获取图片轮播的三张图片
     * @param context
     * @param listener
     */
    public static void getCarouselImg( Context context , final Listener<Boolean , ArrayList<CarouselImgInfo>> listener ){
        QLHttpUtil httpUtil = new QLHttpGet(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.GET_LIVE_CAROUSEL_IMG));
        //HashMap<String, Object> entity = new HashMap<>();
        //entity.put("member_id", QLConstant.client_id);
        //entity.put("token", QLConstant.token);
        //httpUtil.setEntity(entity);
        httpUtil.setUseCache(true);
        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                if (listener == null || listener.isCancel()) {
                    return;
                }
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null) {
                    ArrayList<CarouselImgInfo> list = responseData.parseData("root", new TypeToken<ArrayList<CarouselImgInfo>>() {});
                    listener.onCallBack(true, list);
                }
                listener.onCallBack(false, null);
            }
        });
    }

    public static void getOrgNotifyList(Context context, final Listener<Void, List<OrgNotifyInfo>> callback) {
        QLHttpUtil httpUtil = new QLHttpGet(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.GET_NOTIFY_LIST));

        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("wp_member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);

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
                    List<OrgNotifyInfo> info = responseData.parseData("root", new TypeToken<List<OrgNotifyInfo>>() {
                    });
                    callback.onCallBack(null, info);
                    return;
                }
                callback.onCallBack(null, null);
            }
        });
    }

    public static void getOrgNotifyUnreadCount(Context context, final Listener<Boolean, Integer> callback) {
        QLHttpUtil httpUtil = new QLHttpGet(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.GET_NOTIFY_UNREAD_COUNT));

        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("wp_member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);

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
                    try {
                        JSONArray array = new JSONObject(reply.getReplyMsgAsString()).getJSONArray("root");
                        int count = array.getJSONObject(0).getInt("count");
                        callback.onCallBack(true, count);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return;
                }
                callback.onCallBack(false, 0);
            }
        });
    }

    /**
     * 协会邀请更改状态
     * @param context
     * @param listener
     */
    public static void modifyInviteStatus(Context context ,int isAccept, String message_id , final Listener<Boolean , String> listener) {
        QLHttpUtil httpUtil = new QLHttpGet(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.MODIFY_ORG_INVITE));

        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("isAccept", isAccept);
        entity.put("message_id", message_id);
        entity.put("wp_member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        httpUtil.setEntity(entity);
        httpUtil.setUseCache(false);
        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                if (listener == null && listener.isCancel()) {
                    return;
                }
                String msg = null;
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null && responseData.isSuccess()) {
                    listener.onCallBack(responseData.isSuccess(), null);
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

    public static void orgInviteMember(Context context ,String  o_member_info_id, String org_id , final Listener<Boolean , String> listener) {
        QLHttpUtil httpUtil = new QLHttpGet(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.MODIFY_ORG_INVITE_MEM));

        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("org_id", org_id);
        entity.put("o_member_info_id", o_member_info_id);
        entity.put("wp_member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        httpUtil.setEntity(entity);
        httpUtil.setUseCache(false);
        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                if (listener == null && listener.isCancel()) {
                    return;
                }
                String msg = null;
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null && responseData.isSuccess()) {
                    listener.onCallBack(responseData.isSuccess(), "邀请成功");
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

    public static void clearOrgInviteMember(Context context , final Listener<Boolean , String> listener) {
        QLHttpUtil httpUtil = new QLHttpGet(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.MODIFY_ORG_INVITE_CLEAR));

        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("wp_member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        httpUtil.setEntity(entity);
        httpUtil.setUseCache(false);
        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                if (listener == null && listener.isCancel()) {
                    return;
                }
                String msg = null;
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null && responseData.isSuccess()) {
                    listener.onCallBack(responseData.isSuccess(), null);
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

    public static void recommendAssociation(Context context ,String  o_member_info_id, String org_id , final Listener<Boolean , String> listener) {
        QLHttpUtil httpUtil = new QLHttpGet(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.RECOMMEND_ASSOCIATION));

        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("wp_member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        entity.put("org_id", org_id);
        entity.put("o_member_info_id", o_member_info_id);
        httpUtil.setEntity(entity);
        httpUtil.setUseCache(false);
        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                if (listener == null && listener.isCancel()) {
                    return;
                }
                String msg = null;
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null && responseData.isSuccess()) {
                    listener.onCallBack(responseData.isSuccess(), "成功");
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
