package app.logic.controller;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.QLConstant;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.ql.utils.QLJsonUtil;
import org.ql.utils.network.QLHttpGet;
import org.ql.utils.network.QLHttpPost;
import org.ql.utils.network.QLHttpReply;
import org.ql.utils.network.QLHttpResult;
import org.ql.utils.network.QLHttpUtil;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import android.R.integer;
import android.content.Context;
import android.util.Log;

import app.config.http.HttpConfig;
import app.config.http.YYResponseData;
import app.logic.pojo.CardInfo;
import app.logic.pojo.Chatroom;
import app.logic.pojo.FriendInfo;
import app.logic.pojo.OrgRecommendMemberInfo;
import app.logic.pojo.SearchInfo;
import app.logic.pojo.TradeInfo;
import app.logic.pojo.UserInfo;
import app.logic.pojo.YYChatSessionInfo;
import app.utils.common.EncryptUtils;
import app.utils.common.Listener;
import app.utils.file.YYFileManager;
import app.utils.helpers.PropertySaveHelper;
import app.utils.helpers.SharepreferencesUtils;
import cn.jpush.android.api.JPushInterface;
import internal.org.apache.http.entity.mime.MultipartEntity;
import internal.org.apache.http.entity.mime.content.FileBody;
import internal.org.apache.http.entity.mime.content.StringBody;

/**
 * SiuJiYung create at 2016-6-2 上午10:45:28
 */

public class UserManagerController {

    private static UserInfo _usinfo;
    public static final String kUSER_INFO_KEY = "kUSER_INFO_KEY";

    public static UserInfo getCurrUserInfo() {
//        if (_usinfo == null) {
            String info_json = PropertySaveHelper.getHelper().stringForKey(kUSER_INFO_KEY);
            if (info_json != null) {
                try {
                    Gson gson = new Gson();
                    _usinfo = gson.fromJson(info_json, UserInfo.class);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }
//        }
        return _usinfo;
    }

    public static void updateUserInfo(UserInfo info) {
        PropertySaveHelper.getHelper().save(info, "kUSER_INFO_KEY");
        _usinfo = info;
    }

    /**
     * @param context
     * @param filePath
     * @param callback
     */
    public static void uploadUserHeadImage(Context context, final String filePath, final Listener<Integer, String> callback) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String url = HttpConfig.getUrl(HttpConfig.OPERATING_MEMBER_PICTURE_URL);
                HttpClient httpClient = new DefaultHttpClient();
                try {
                    HttpPost httpPost = new HttpPost(url);
                    httpPost.setHeader("User-Agent", "SOHUWapRebot");
                    httpPost.setHeader("Accept-Language", "zh-cn,zh;q=0.5");
                    httpPost.setHeader("Accept-Charset", "GBK,utf-8;q=0.7,*;q=0.7");
                    httpPost.setHeader("Connection", "keep-alive");
                    MultipartEntity mutiEntity = new MultipartEntity();
                    File file = new File(filePath);

                    mutiEntity.addPart("img", new FileBody(file));
                    mutiEntity.addPart("uid", new StringBody("HelpAuction"));
                    // mutiEntity.addPart("primarykey", new
                    // StringBody(primarykey,Charset.forName("utf-8")));
                    mutiEntity.addPart("token", new StringBody(QLConstant.token, Charset.forName("utf-8")));
                    mutiEntity.addPart("wp_member_info_id", new StringBody(QLConstant.client_id, Charset.forName("utf-8")));

                    httpPost.setEntity(mutiEntity);
                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        Log.v("hhhh", "连接成功");
                        HttpEntity httpEntity = httpResponse.getEntity();
                        String content = EntityUtils.toString(httpEntity);
                        Log.v("hhhh", content);
                        JSONObject json = QLJsonUtil.doJSONObject(content);
                        Boolean flag = QLJsonUtil.doBoolean(json.get("success"));
                        String message = QLJsonUtil.doString(json.get("msg"));
                        if (flag && callback != null) {
                            callback.onCallBack(1, null);
                            return;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (callback != null) {
                    callback.onCallBack(-1, null);
                }
            }
        };
        new Thread(runnable).start();
    }

    /**
     * 获取会话列表
     *
     * @param context  // * @param memberId
     * @param callback
     */
    public static void getChatList(Context context, final Listener<Integer, List<YYChatSessionInfo>> callback) {
        QLHttpUtil httpUtil = new QLHttpPost(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.GET_CHAT_LIST));
        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("wp_member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        httpUtil.setEntity(entity);
        httpUtil.setUseCache(true);
        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                if (callback == null || callback.isCancel()) {
                    return;
                }
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null && responseData.getCode() == 0) {
                    List<YYChatSessionInfo> chatList = responseData.parseData("root", new TypeToken<List<YYChatSessionInfo>>() {
                    });

                    List<Chatroom> groupList = responseData.parseData("chatroom", new TypeToken<List<Chatroom>>() {
                    });
                    List<YYChatSessionInfo> tempList = new ArrayList<YYChatSessionInfo>();
                    for (Chatroom chatroom : groupList) {
                        YYChatSessionInfo info = new YYChatSessionInfo();
                        info.setChatroom(chatroom);
                        tempList.add(info);
                    }
                    if (tempList.size() > 0) {
                        chatList.addAll(tempList);
                    }

                    callback.onCallBack(1, chatList);
                    return;
                }
                callback.onCallBack(-1, null);
            }
        });
    }

    /**
     * 移除一个会话
     *
     * @param context
     * @param memberId
     * @param callback
     */
    public static void removeChatWith(Context context, String memberId, final Listener<Integer, String> callback) {
        QLHttpUtil httpUtil = new QLHttpPost(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.REMOVE_CHAT));
        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("wp_member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        entity.put("wp_dialogue_info_id", memberId);
        httpUtil.setEntity(entity);
        httpUtil.setUseCache(true);
        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                if (callback == null || callback.isCancel()) {
                    return;
                }
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null && responseData.getCode() == 0) {
                    callback.onCallBack(1, null);
                    return;
                }
                callback.onCallBack(-1, null);
            }
        });
    }

    /**
     * 添加一个会话
     *
     * @param context
     * @param memberId
     * @param tag
     * @param callback
     */
    public static void addChatWith(Context context, String memberId, String tag, final Listener<Integer, String> callback) {
        QLHttpUtil httpUtil = new QLHttpPost(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.ADD_CHAT));
        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("wp_member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        entity.put("wp_other_info_id", memberId);
        entity.put("remark", tag);
        httpUtil.setEntity(entity);
        httpUtil.setUseCache(true);
        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                if (callback == null || callback.isCancel()) {
                    return;
                }
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null && responseData.getCode() == 0) {
                    callback.onCallBack(1, null);
                    return;
                }
                callback.onCallBack(-1, null);
            }
        });
    }

    /**
     * 用户登录
     *
     * @param context
     * @param phone
     * @param password
     * @param callBack
     */
    public static void Login(final Context context, String phone, String password, final Listener<Integer, UserInfo> callBack) {
        QLHttpUtil httpUtil = new QLHttpPost(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.LOGIN_URL));
        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("phone", phone);


        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = sdf.format(new Date());
        entity.put("password", EncryptUtils.getMD5(date+EncryptUtils.getMD5(password)));
//        entity.put("password",password);
        entity.put("sec", date);
        // entity.put("pushId", JPushInterface.getRegistrationID(context));
        httpUtil.setEntity(entity);
        httpUtil.setUseCache(false);
        httpUtil.startConnection(new QLHttpResult() {

            @Override
            public void reply(QLHttpReply reply) {
                String msg = null;
                try {
                    if (callBack == null || callBack.isCancel())
                        return;
                    JSONObject json = QLJsonUtil.doJSONObject(reply.getReplyMsgAsString());
                    if (json == null) {
                        callBack.onCallBack(-2, null);
                    } else {
                        QLConstant.token = QLJsonUtil.doString(json.get("token"));
                        SharepreferencesUtils utils = new SharepreferencesUtils(context);
                        utils.setToken(QLConstant.token);
                        msg = QLJsonUtil.doString("wp_error_msg");
                        YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                        List<UserInfo> list = null;
                        if (responseData != null) {
                            list = responseData.parseData("root", new TypeToken<List<UserInfo>>() {
                            });
                            UserInfo info = null;
                            if (list != null && list.size() > 0) {
                                info = list.get(0);
                                JPushInterface.setAlias(context, info.getWp_member_info_id(), null);  //极光托送设置别名
                                _usinfo = info;
                                PropertySaveHelper.getHelper().save(info, kUSER_INFO_KEY);
                                callBack.onCallBack(1, info);
                                return;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (msg == null) {
                        msg = e.getMessage();
                    }
                }
                UserInfo info = new UserInfo();
                info.setWp_error_msg(msg);
                callBack.onCallBack(-1, info);
            }
        });
    }

    // 忘记密码
    public static void FotgetPsw(Context context, String phone, String password, String registerCode, final Listener<Integer, String> callBack) {
        QLHttpUtil httpUtil = new QLHttpPost(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.FORGET_MEMBER_PASSWORD_URL));
        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("phone", phone);
        entity.put("password", EncryptUtils.getMD5(password));
        entity.put("registerCode", registerCode);
        httpUtil.setEntity(entity);
        httpUtil.setUseCache(false);
        httpUtil.startConnection(new QLHttpResult() {

            @Override
            public void reply(QLHttpReply reply) {
                try {
                    if (callBack == null || callBack.isCancel())
                        return;
                    JSONObject json = QLJsonUtil.doJSONObject(reply.getReplyMsgAsString());

                    if (json == null) {
                        callBack.onCallBack(-2, null);
                    } else {
                        Boolean flag = QLJsonUtil.doBoolean(json.get("success"));
                        String msg = QLJsonUtil.doString(json.get("msg"));
                        if (flag) {
                            callBack.onCallBack(1, msg);
                        } else {
                            callBack.onCallBack(-1, msg);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 注册用户
     *
     * @param context
     * @param phone
     * @param password
     * @param callBack
     */
    public static void Register(final Context context, String phone, String password,String code , final Listener<Boolean, List<UserInfo>> callBack) {
        QLHttpUtil httpUtil = new QLHttpPost(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.REGISTER_BY_ID));
        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("phone", phone);
        entity.put("password", EncryptUtils.getMD5(password));
        entity.put("code", code);
        httpUtil.setEntity(entity);
        httpUtil.setUseCache(false);
        httpUtil.startConnection(new QLHttpResult() {

            @Override
            public void reply(QLHttpReply reply) {
                try {
                    if (callBack == null || callBack.isCancel())
                        return;
                    JSONObject json = QLJsonUtil.doJSONObject(reply.getReplyMsgAsString());
                    YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());

                    if(json !=null && responseData != null && responseData.isSuccess() ){
                        String data = QLJsonUtil.doString(json.get("root"));
                        int code = QLJsonUtil.doInt(json.get("wp_error_code"));
                        QLConstant.token = QLJsonUtil.doString(json.get("token"));
                        SharepreferencesUtils utils = new SharepreferencesUtils(context);
                        utils.setToken(QLConstant.token);
                        List<UserInfo> list = responseData.parseData("root", new TypeToken<List<UserInfo>>() {});
                        String msg = QLJsonUtil.doString(json.get("wp_error_msg"));
                        UserInfo info = list.get(0);
                        PropertySaveHelper.getHelper().save(info, kUSER_INFO_KEY);
                        callBack.onCallBack(true, list);
                        return;
                    }else if(responseData != null && !responseData.isSuccess()){
                        String msg = QLJsonUtil.doString(json.get("msg"));
                        List<UserInfo> list2 = new ArrayList<UserInfo>();
                        UserInfo info = new UserInfo();
                        info.setWp_error_msg(responseData.getMsg());
                        list2.add(info);
                        callBack.onCallBack(false , list2);
                        return;
                    }else{
                        List<UserInfo> list3 = new ArrayList<UserInfo>();
                        UserInfo info = new UserInfo();
                        info.setWp_error_msg("未知错误");
                        list3.add(info);
                        callBack.onCallBack(false , list3);
                    }

//                    if (json != null && responseData != null) {
//                        String data = QLJsonUtil.doString(json.get("root"));
//                        int code = QLJsonUtil.doInt(json.get("wp_error_code"));
//                        QLConstant.token = QLJsonUtil.doString(json.get("token"));
//                        List<UserInfo> list = responseData.parseData("root", new TypeToken<List<UserInfo>>() {});
//                        String msg = QLJsonUtil.doString(json.get("wp_error_msg"));
//                        Log.v("hhhh", "msg" + msg);
//                        if (code == 0) {  //
//                            UserInfo info = list.get(0);
//                            PropertySaveHelper.getHelper().save(info, kUSER_INFO_KEY);
//                            callBack.onCallBack(1, list);
//                            return;
//                        } else{
//                            List<UserInfo> list2 = new ArrayList<UserInfo>();
//                            UserInfo info = new UserInfo();
//                            info.setWp_error_msg(responseData.getErrorMsg());
//                            list2.add(info);
//                            callBack.onCallBack(-1, list2);
//                            return;
//                        }
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                callBack.onCallBack(-2, null);
            }
        });
    }

    /**
     * 修改用户属性
     *
     * @param context
     * @param propertys
     * @param callback
     */
    public static void updateUserInfo(Context context, Map<String, String> propertys, final Listener<Integer, String> callback) {
        if (propertys == null) {
            return;
        }
        QLHttpUtil httpUtil = new QLHttpPost(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.UPDATE_MEMBER_LIST_INFO_URL));
        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("wp_member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        entity.putAll(propertys);

        httpUtil.setEntity(entity);
        httpUtil.setUseCache(false);
        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                String msgString = null;
                try {
                    if (callback == null || callback.isCancel())
                        return;
                    YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                    if (responseData != null && responseData.isSuccess()) {
                        callback.onCallBack(1, null);
                        return;
                    } else if (responseData != null) {
                        msgString = responseData.getErrorMsg();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                callback.onCallBack(-1, msgString);
            }
        });
    }

    // 修改昵称
    public static void updateName(Context context, String nickName, final Listener<Integer, String> callBack) {
        QLHttpUtil httpUtil = new QLHttpPost(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.UPDATE_MEMBER_NAME));
        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("wp_member_info_id", QLConstant.client_id);
        entity.put("primarykey", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        entity.put("nickName", nickName);
        httpUtil.setEntity(entity);
        httpUtil.setUseCache(false);
        httpUtil.startConnection(new QLHttpResult() {

            @Override
            public void reply(QLHttpReply reply) {
                String msg = null;
                try {
                    if (callBack == null || callBack.isCancel())
                        return;
                    JSONObject json = QLJsonUtil.doJSONObject(reply.getReplyMsgAsString());
                    Boolean flag = QLJsonUtil.doBoolean(json.get("success"));
                    msg = QLJsonUtil.doString(json.get("msg"));
                    if (json != null && flag) {
                        callBack.onCallBack(1, msg);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                callBack.onCallBack(-1, msg);
            }
        });
    }

    // 显示客户本人的的详细信息
    public static void showMemberList(Context context, String id, final Listener<Integer, List<UserInfo>> callBack) {
        QLHttpUtil httpUtil = new QLHttpPost(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.SHOW_MEMBER_LIST_INFO_URL));
        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("token", QLConstant.token);
        entity.put("wp_member_info_id", QLConstant.client_id);
        entity.put("o_wp_member_info_id", QLConstant.client_id);
        entity.put("primarykey", id);
        httpUtil.setEntity(entity);
        httpUtil.setUseCache(true);
        httpUtil.startConnection(new QLHttpResult() {

            @Override
            public void reply(QLHttpReply reply) {
                try {
                    if (callBack == null || callBack.isCancel())
                        return;
                    YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                    if (responseData != null && responseData.getWp_error_code() == 0) {
                        List<UserInfo> list = responseData.parseData("root", new TypeToken<List<UserInfo>>() {
                        });
                        callBack.onCallBack(1, list);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                callBack.onCallBack(-1, null);
            }
        });
    }

    /**
     * 获取聊天方的信息
     *
     * @param context
     * @param memberID
     * @param callBack
     */
    public static void getChatUserInfo(Context context, String memberID, final Listener<Integer, List<UserInfo>> callBack) {
        QLHttpUtil httpUtil = new QLHttpPost(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.GET_CHATUSER_INFO));
        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("token", QLConstant.token);
        entity.put("wp_member_info_id", QLConstant.client_id);
        entity.put("memberID", memberID);
        httpUtil.setEntity(entity);
        httpUtil.setUseCache(true);
        httpUtil.startConnection(new QLHttpResult() {

            @Override
            public void reply(QLHttpReply reply) {
                try {
                    if (callBack == null || callBack.isCancel())
                        return;
                    YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                    if (responseData != null && responseData.getWp_error_code() == 0) {
                        List<UserInfo> list = responseData.parseData("root", new TypeToken<List<UserInfo>>() {
                        });
                        callBack.onCallBack(1, list);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                callBack.onCallBack(-1, null);
            }
        });
    }

    /**
     * 请求验证码
     *
     * @param context
     * @param phone
     * @param callBack
     */
    public static void sendVerification(Context context, String phone, String sid , String imageCode , String type, final Listener<Integer, String> callBack) {
        QLHttpUtil httpUtil = new QLHttpPost(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.SEND_VERIFICATION));
        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("token", QLConstant.token);
        entity.put("wp_member_info_id", QLConstant.client_id);
        entity.put("phone", phone);
        entity.put("type", type);
        entity.put("sid", sid);
        entity.put("imgCode", imageCode);

        httpUtil.setEntity(entity);
        httpUtil.setUseCache(false);
        httpUtil.startConnection(new QLHttpResult() {

            @Override
            public void reply(QLHttpReply reply) {
                String msg = null;
                try {
                    if (callBack == null || callBack.isCancel())
                        return;
                    JSONObject json = QLJsonUtil.doJSONObject(reply.getReplyMsgAsString());
                    Boolean flag = QLJsonUtil.doBoolean(json.get("success"));
                    msg = QLJsonUtil.doString(json.get("msg"));
                    if (json != null && flag) {
                        callBack.onCallBack(1, msg);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (msg == null) {
                        msg = e.getMessage();
                    }
                }
                callBack.onCallBack(-1, msg);
            }
        });
    }

    /**
     * 检查验证码
     *
     * @param context
     * @param phone
     * @param registerCode
     * @param callBack
     */
    public static void checkVerification(Context context, String phone, String registerCode, final Listener<Integer, String> callBack) {

        QLHttpUtil httpUtil = new QLHttpPost(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.REGISTER_ISEFFECTIVE));
        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("token", QLConstant.token);
        entity.put("wp_member_info_id", QLConstant.client_id);
        entity.put("phone", phone);
        entity.put("code", registerCode);
        httpUtil.setEntity(entity);
        httpUtil.setUseCache(false);
        httpUtil.startConnection(new QLHttpResult() {

            @Override
            public void reply(QLHttpReply reply) {
                try {
                    if (callBack == null || callBack.isCancel())
                        return;
                    JSONObject json = QLJsonUtil.doJSONObject(reply.getReplyMsgAsString());
                    if (json != null) {
                        Boolean flag = QLJsonUtil.doBoolean(json.get("success"));
                        String msg = QLJsonUtil.doString(json.get("msg"));
                        if (flag) {
                            callBack.onCallBack(1, msg);
                            return;
                        } else {
                            callBack.onCallBack(-1, msg);
                            return;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                callBack.onCallBack(-2, null);
            }
        });
    }

    /**
     * 修改用户密码
     *
     * @param context
     * @param primarykey
     * @param oldPassword
     * @param newPassword
     * @param callBack
     */
    public static void changePsw(Context context, String primarykey, String oldPassword, String newPassword, final Listener<Integer, String> callBack) {
        QLHttpUtil httpUtil = new QLHttpPost(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.CHANGE_PSW_URL));
        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("token", QLConstant.token);
        entity.put("wp_member_info_id", QLConstant.client_id);
        entity.put("primarykey", primarykey);
        entity.put("oldPassword", oldPassword);
        entity.put("newPassword", newPassword);
        httpUtil.setEntity(entity);
        httpUtil.setUseCache(false);
        httpUtil.startConnection(new QLHttpResult() {

            @Override
            public void reply(QLHttpReply reply) {
                try {
                    if (callBack == null || callBack.isCancel())
                        return;
                    JSONObject json = QLJsonUtil.doJSONObject(reply.getReplyMsgAsString());
                    Boolean flag = QLJsonUtil.doBoolean(json.get("success"));
                    String msg = QLJsonUtil.doString(json.get("msg"));
                    if (json != null && flag) {
                        callBack.onCallBack(1, msg);
                        return;
                    } else {
                        callBack.onCallBack(-1, msg);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                callBack.onCallBack(-1, null);
            }
        });
    }

    // 根据电话号码获取用户信息
    // SHOW_PHONEMEMBER_INFO_URL
    public static void getPhoneMemerInfo(Context context, String phone, final Listener<Integer, List<UserInfo>> callBack) {
        QLHttpUtil httpUtil = new QLHttpPost(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.SHOW_PHONEMEMBER_INFO_URL));
        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("token", QLConstant.token);
        entity.put("wp_member_info_id", QLConstant.client_id);
        entity.put("wp_friends_info_id", phone);
        httpUtil.setEntity(entity);
        httpUtil.setUseCache(true);
        httpUtil.startConnection(new QLHttpResult() {

            @Override
            public void reply(QLHttpReply reply) {
                try {
                    if (callBack == null || callBack.isCancel())
                        return;
                    YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                    if (responseData != null && responseData.getWp_error_code() == 0) {
                        List<UserInfo> list = responseData.parseData("root", new TypeToken<List<UserInfo>>() {
                        });
                        callBack.onCallBack(1, list);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                callBack.onCallBack(-1, null);
            }
        });

    }

    /**
     * 获取用户信息
     *
     * @param context
     * @param memberId
     * @param callback
     */
    public static void getUserInfo(Context context, String memberId, final Listener<Integer, UserInfo> callback) {
        QLHttpUtil httpUtil = new QLHttpPost(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.GET_USER_INFO));
        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("wp_member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        entity.put("o_wp_member_info_id", memberId);
        httpUtil.setEntity(entity);
        httpUtil.setUseCache(true);
        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                if (callback == null || callback.isCancel()) {
                    return;
                }
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null && responseData.getCode() == 0) {
                    List<UserInfo> tmp_userInfos = responseData.parseData("root", new TypeToken<List<UserInfo>>() {
                    });
                    if (tmp_userInfos != null && tmp_userInfos.size() > 0) {
                        callback.onCallBack(1, tmp_userInfos.get(0));
                        return;
                    }
                }
                callback.onCallBack(-1, null);
            }
        });
    }

    /**
     * 修改用户昵称
     *
     * @param context
     * @param wp_child_manager_id
     * @param child_name
     * @param callBack
     */
    public static void modifyNick(Context context, String wp_child_manager_id, String child_name, final Listener<Integer, String> callBack) {
        QLHttpUtil httpUtil = new QLHttpPost(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.MODIFY_NICK_SUB));
        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("wp_member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        entity.put("wp_child_user_info_id", wp_child_manager_id);
        entity.put("device_child_name", child_name);
        httpUtil.setEntity(entity);
        httpUtil.setUseCache(false);
        httpUtil.startConnection(new QLHttpResult() {

            @Override
            public void reply(QLHttpReply reply) {
                try {
                    if (callBack == null || callBack.isCancel())
                        return;
                    JSONObject json = QLJsonUtil.doJSONObject(reply.getReplyMsgAsString());
                    Boolean flag = QLJsonUtil.doBoolean(json.get("success"));
                    String msg = QLJsonUtil.doString(json.get("msg"));
                    if (json != null && flag) {
                        callBack.onCallBack(1, msg);
                        return;
                    } else {
                        callBack.onCallBack(-1, msg);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                callBack.onCallBack(-1, "修改失败");
            }
        });
    }

    /**
     * 获取好友列表
     *
     * @param context
     * @param callBack
     */
    public static void getFriendsList(Context context, final Listener<List<FriendInfo>, List<FriendInfo>> callBack) {
        QLHttpUtil httpUtil = new QLHttpPost(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.SHOW_FRIEND_LIST_INFO_URL));
        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("wp_member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        httpUtil.setEntity(entity);
//        httpUtil.setUseCache(true);
        httpUtil.setUseCache(false);
        httpUtil.setConnectionTimeOut(1000 * 60);
        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                try {
                    if (callBack == null || callBack.isCancel())
                        return;
                    YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                    if (responseData != null && responseData.getWp_error_code() == 0) {
                        List<FriendInfo> requestInfos = responseData.parseData("request", new TypeToken<List<FriendInfo>>() {});
                        List<FriendInfo> list = responseData.parseData("root", new TypeToken<List<FriendInfo>>() {});
                        callBack.onCallBack(requestInfos, list);
                        return;
                    }
                } catch (Exception e) {
                    Log.v("hhhh", "error" + e.toString());
                    e.printStackTrace();
                }
                callBack.onCallBack(null, null);
            }
        });
    }

    /**
     * 添加好友请求
     *
     * @param context
     * @param phone
     * @param validation
     * @param callBack
     */
    public static void addFriends(Context context, String phone, String validation, final Listener<Integer, String> callBack) {
        QLHttpUtil httpUtil = new QLHttpPost(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.ADD_FRIEND_LIST_INFO_URL));
        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("wp_member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        entity.put("phone", phone);
        entity.put("validation", validation);
        httpUtil.setEntity(entity);
        httpUtil.setUseCache(false);
        httpUtil.startConnection(new QLHttpResult() {

            @Override
            public void reply(QLHttpReply reply) {
                String msg = null;
                try {
                    if (callBack == null || callBack.isCancel())
                        return;
                    JSONObject json = QLJsonUtil.doJSONObject(reply.getReplyMsgAsString());
                    Boolean flag = QLJsonUtil.doBoolean(json.get("success"));
                    msg = QLJsonUtil.doString(json.get("wp_error_msg"));
                    if (json != null && flag) {
                        callBack.onCallBack(1, msg);
                        return;
                    }
                } catch (Exception e) {
                    Log.v("hhhh", "error" + e.toString());
                    e.printStackTrace();
                }
                callBack.onCallBack(-1, msg);
            }
        });
    }

    /**
     * 删除好友
     *
     * @param context
     * @param wp_friends_info_id
     * @param callBack
     */
    public static void deleteFriends(Context context, String wp_friends_info_id, final Listener<Integer, String> callBack) {
        QLHttpUtil httpUtil = new QLHttpPost(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.DELETE_FRIEND_LIST_INFO_URL));
        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("wp_member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        entity.put("wp_friends_info_id", wp_friends_info_id);
        httpUtil.setEntity(entity);
        httpUtil.setUseCache(false);
        httpUtil.startConnection(new QLHttpResult() {

            @Override
            public void reply(QLHttpReply reply) {
                String msg = null;
                try {
                    if (callBack == null || callBack.isCancel())
                        return;
                    JSONObject json = QLJsonUtil.doJSONObject(reply.getReplyMsgAsString());
                    Boolean flag = QLJsonUtil.doBoolean(json.get("success"));
                    msg = QLJsonUtil.doString(json.get("wp_error_msg"));
                    if (json != null && flag) {
                        callBack.onCallBack(1, msg);
                        return;
                    }
                } catch (Exception e) {
                    Log.v("hhhh", "error" + e.toString());
                    e.printStackTrace();
                }
                callBack.onCallBack(-1, msg);
            }
        });
    }

    /**
     * 确认添加好友
     *
     * @param context
     * @param add_friend_id
     * @param request_accept
     * @param message_id
     * @param callBack
     */
    public static void ensureFriends(Context context, String add_friend_id, int request_accept, String message_id, final Listener<Integer, String> callBack) {
        QLHttpUtil httpUtil = new QLHttpPost(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.CONFIRM_FRIEND_LIST_INFO_URL));
        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("wp_member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        entity.put("add_friend_id", add_friend_id);
        entity.put("request_accept", request_accept);
        entity.put("message_id", message_id);
        httpUtil.setEntity(entity);
        httpUtil.setUseCache(false);
        httpUtil.startConnection(new QLHttpResult() {

            @Override
            public void reply(QLHttpReply reply) {
                String msg = null;
                try {
                    if (callBack == null || callBack.isCancel())
                        return;
                    JSONObject json = QLJsonUtil.doJSONObject(reply.getReplyMsgAsString());
                    Boolean flag = QLJsonUtil.doBoolean(json.get("success"));
                    msg = QLJsonUtil.doString(json.get("error"));
                    if (json != null && flag) {
                        callBack.onCallBack(1, msg);
                        return;
                    }
                } catch (Exception e) {
                    Log.v("hhhh", "error" + e.toString());
                    e.printStackTrace();
                }
                callBack.onCallBack(-1, msg);
            }
        });
    }

    // ---------------------接口转向member_id by 2017-04-21------------------------start
    /**
     * 添加好友请求
     *
     * @param context
     * @param friend_id
     * @param validation
     * @param callBack
     */
    public static void addFriendsById(Context context, String friend_id, String validation, final Listener<Integer, String> callBack) {
        QLHttpUtil httpUtil = new QLHttpPost(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.ADD_FRIEND_BY_ID_INFO_URL));
        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("wp_member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        entity.put("wp_friends_info_id", friend_id);
        entity.put("validation", validation);
        httpUtil.setEntity(entity);
        httpUtil.setUseCache(false);
        httpUtil.startConnection(new QLHttpResult() {

            @Override
            public void reply(QLHttpReply reply) {
                String msg = null;
                try {
                    if (callBack == null || callBack.isCancel())
                        return;
                    JSONObject json = QLJsonUtil.doJSONObject(reply.getReplyMsgAsString());
                    Boolean flag = QLJsonUtil.doBoolean(json.get("success"));
                    msg = QLJsonUtil.doString(json.get("wp_error_msg"));
                    if (json != null && flag) {
                        callBack.onCallBack(1, msg);
                        return;
                    }
                } catch (Exception e) {
                    Log.v("hhhh", "error" + e.toString());
                    e.printStackTrace();
                }
                callBack.onCallBack(-1, msg);
            }
        });
    }

    /**
     * 删除好友
     *
     * @param context
     * @param wp_friends_info_id
     * @param callBack
     */
    public static void deleteFriendsById(Context context, String wp_friends_info_id, final Listener<Integer, String> callBack) {
        QLHttpUtil httpUtil = new QLHttpPost(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.DELETE_FRIEND_BY_ID_INFO_URL));
        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("wp_member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        entity.put("wp_friends_info_id", wp_friends_info_id);
        httpUtil.setEntity(entity);
        httpUtil.setUseCache(false);
        httpUtil.startConnection(new QLHttpResult() {

            @Override
            public void reply(QLHttpReply reply) {
                String msg = null;
                try {
                    if (callBack == null || callBack.isCancel())
                        return;
                    JSONObject json = QLJsonUtil.doJSONObject(reply.getReplyMsgAsString());
                    Boolean flag = QLJsonUtil.doBoolean(json.get("success"));
                    msg = QLJsonUtil.doString(json.get("wp_error_msg"));
                    if (json != null && flag) {
                        callBack.onCallBack(1, msg);
                        return;
                    }
                } catch (Exception e) {
                    Log.v("hhhh", "error" + e.toString());
                    e.printStackTrace();
                }
                callBack.onCallBack(-1, msg);
            }
        });
    }
    // ---------------------接口转向member_id by 2017-04-21------------------------end

    /**
     * 加载名片列表
     *
     * @param context
     * @param callback
     */
    public static void getMyCardList(Context context, final Listener<Void, List<CardInfo>> callback) {
        QLHttpUtil httpUtil = new QLHttpGet(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.GET_MY_CAR_LIST));
        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        httpUtil.setEntity(entity);
        httpUtil.setUseCache(true);
        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                if (callback == null || callback.isCancel()) {
                    return;
                }
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null && responseData.isSuccess()) {
                    List<CardInfo> tmp_info = responseData.parseData("root", new TypeToken<List<CardInfo>>() {
                    });
                    if (tmp_info != null) {
                        callback.onCallBack(null, tmp_info);
                        return;
                    }
                }
                callback.onCallBack(null, null);
            }
        });
    }

    /**
     * 创建名片
     *
     * @param context
     * @param info
     * @param img
     * @param callback
     * @throws IOException
     */
    public static void createCard(Context context, CardInfo info, String img, final Listener<Integer, String> callback) throws IOException {
        QLHttpUtil httpUtil = new QLHttpPost(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.ADD_CARD_INFO));
        Gson gson = new Gson();
        String cardInfoString = gson.toJson(info);
        String base64_str = YYFileManager.imgToBase64(img);
        // byte[] imgBase64Byte = null;
        // byte[] srcByte = YYFileManager.readFile(context, img);
        // if (srcByte != null) {
        // imgBase64Byte = Base64.decode(srcByte);
        // srcByte = null;
        // base64_str = new String(imgBase64Byte);
        // imgBase64Byte = null;
        // }

        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        entity.put("info", cardInfoString);
        if (base64_str != null) {
            entity.put("img_file", base64_str);
        }

        httpUtil.setEntity(entity);
        httpUtil.setUseCache(true);
        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                if (callback == null || callback.isCancel()) {
                    return;
                }
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null && responseData.isSuccess()) {
                    callback.onCallBack(1, null);
                    return;
                }
                callback.onCallBack(-1, null);
            }
        });
        base64_str = null;
    }

    /**
     * @param context
     * @param info
     * @param img
     * @param callback
     * @throws IOException
     */
    public static void modifyCard(Context context, CardInfo info, String img, final Listener<Integer, String> callback) throws IOException {

        QLHttpUtil httpUtil = new QLHttpPost(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.MODIFY_CARD_INFO));
        Gson gson = new Gson();
        String cardInfoString = gson.toJson(info);
        String base64_str = YYFileManager.imgToBase64(img);

        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        entity.put("info", cardInfoString);
        if (base64_str != null) {
            entity.put("img_file", base64_str);
        }

        httpUtil.setEntity(entity);
        httpUtil.setUseCache(true);
        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                if (callback == null || callback.isCancel()) {
                    return;
                }
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                String msg = null;
                if (responseData != null && responseData.isSuccess()) {
                    callback.onCallBack(1, null);
                    return;
                } else if (responseData != null) {
                    msg = responseData.getErrorMsg();
                }
                callback.onCallBack(-1, msg);
            }
        });
        base64_str = null;
    }

    /**
     * 删除名片
     *
     * @param context
     * @param cardId
     * @param callback
     */
    public static void removeCard(Context context, String cardId, final Listener<Integer, String> callback) {
        QLHttpUtil httpUtil = new QLHttpGet(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.REMOVE_CARD_INFO));

        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        entity.put("bc_id", cardId);

        httpUtil.setEntity(entity);
        httpUtil.setUseCache(true);
        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                if (callback == null || callback.isCancel()) {
                    return;
                }
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                String msg = null;
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
     * 获取名片信息
     *
     * @param context
     * @param cardId
     * @param callback
     */
    public static void getCardInfo(Context context, String cardId, final Listener<Integer, CardInfo> callback) {

        QLHttpUtil httpUtil = new QLHttpGet(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.GET_CARD_INFO));

        HashMap<String, Object> entity = new HashMap<>();
        entity.put("member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        entity.put("bc_id", cardId);
        httpUtil.setEntity(entity);

        httpUtil.setUseCache(true);

        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                if (callback == null || callback.isCancel()) {
                    return;
                }
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null && responseData.isSuccess()) {
                    List<CardInfo> tempList = responseData.parseData("root", new TypeToken<List<CardInfo>>() {
                    });

                    callback.onCallBack(1, tempList.get(0));
                    return;
                }
                callback.onCallBack(-1, null);
            }
        });
    }

    /*
     * 更改好友备注
     */
    public static void updataFriendName(Context context, String friend_name, String friend_id, final Listener<Boolean, String> listener) {
        QLHttpUtil httpUtil = new QLHttpGet(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.UPDATA_FRIEND_NAME));

        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("wp_member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        entity.put("friend_name", friend_name);
        entity.put("add_friend_id", friend_id);
        httpUtil.setEntity(entity);
        httpUtil.setUseCache(true);

        httpUtil.startConnection(new QLHttpResult() {

            @Override
            public void reply(QLHttpReply reply) {
                if (listener == null || listener.isCancel()) {
                    return;
                }
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                String msg = null;
                if (responseData != null && responseData.isSuccess()) {
                    listener.onCallBack(true, null);
                    return;
                }else  if(responseData!=null){
                    listener.onCallBack(false, responseData.getErrorMsg());
                }else{
                    listener.onCallBack(false, "未知错误");
                }
            }
        });

    }

    // 提交用户对app的问题
    public static void postHelpAndFeedback(Context context, String content, final Listener<Integer, String> listener) {
        QLHttpUtil httpUtil = new QLHttpPost(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.POST_CONTENT));

        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("wp_member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        entity.put("helpInfo", content);

        httpUtil.setEntity(entity);
        httpUtil.setUseCache(false);
        httpUtil.startConnection(new QLHttpResult() {

            @Override
            public void reply(QLHttpReply reply) {
                if (listener == null || listener.isCancel()) {
                    return;
                }
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                String msg = null;
                if (responseData != null) {
                    listener.onCallBack(1, null);
                    return;
                }
                listener.onCallBack(-1, null);

            }
        });
    }

    public static void getSearchAllMessage(Context context, String keyword, final Listener<Integer, SearchInfo> listener) {
        QLHttpUtil httpUtil = new QLHttpPost(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.SEARCH_DATAS));
        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("member_info_id", QLConstant.client_id);
        entity.put("keyword", keyword);
        httpUtil.setEntity(entity);
        httpUtil.setUseCache(true);
        httpUtil.startConnection(new QLHttpResult() {

            @Override
            public void reply(QLHttpReply reply) {
                if (listener == null || listener.isCancel()) {
                    return;
                }
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null && responseData.isSuccess()) {
                    Gson gson = new Gson();
                    SearchInfo info = gson.fromJson(responseData.getSouceJsonString(), SearchInfo.class);
                    listener.onCallBack(1, info);
                    return;
                }
                listener.onCallBack(-1, null);

            }
        });

    }

    /**
     * 添加好友的列表
     *
     * @param context
     * @param callBack
     */
    public static void getListFriendMessage(Context context, final Listener<List<FriendInfo>, List<FriendInfo>> callBack) {
        QLHttpUtil httpUtil = new QLHttpPost(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.GET_LIST_FRIEND_MESSAGE));
        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("wp_member_info_id", QLConstant.client_id);
        httpUtil.setEntity(entity);
        httpUtil.setUseCache(true);
        httpUtil.startConnection(new QLHttpResult() {

            @Override
            public void reply(QLHttpReply reply) {
                try {
                    if (callBack == null || callBack.isCancel())
                        return;
                    YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                    if (responseData != null && responseData.getWp_error_code() == 0) {
                        List<FriendInfo> requestInfos = responseData.parseData("request", new TypeToken<List<FriendInfo>>() {
                        });
                        List<FriendInfo> list = responseData.parseData("root", new TypeToken<List<FriendInfo>>() {
                        });
                        callBack.onCallBack(requestInfos, list);
                        return;
                    }
                } catch (Exception e) {
                    Log.v("hhhh", "error" + e.toString());
                    e.printStackTrace();
                }
                callBack.onCallBack(null, null);
            }
        });
    }

    /**
     * 搜索
     *
     * @param context
     * @param callBack
     */
    public static void searchFriend(Context context,String keyword,int start,int limit, final Listener<List<FriendInfo>, String> callBack) {
        QLHttpUtil httpUtil = new QLHttpPost(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.SEARCH_RESULT));
        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("wp_member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        entity.put("keyword", keyword);
        entity.put("start", start);
        entity.put("limit", limit);
        httpUtil.setEntity(entity);
        httpUtil.setUseCache(true);
        httpUtil.startConnection(new QLHttpResult() {

            @Override
            public void reply(QLHttpReply reply) {
                try {
                    if (callBack == null || callBack.isCancel())
                        return;
                    YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                    if (responseData != null && responseData.getWp_error_code() == 0) {
                        List<FriendInfo> list = responseData.parseData("root", new TypeToken<List<FriendInfo>>() {
                        });
                        callBack.onCallBack(list, null);
                        return;
                    }
                } catch (Exception e) {
                    Log.v("hhhh", "error" + e.toString());
                    e.printStackTrace();
                }
                callBack.onCallBack(null, null);
            }
        });
    }

    public static void getRandomRecommendMember(Context context,String org_id, final Listener<List<OrgRecommendMemberInfo>, String> callBack) {
        QLHttpUtil httpUtil = new QLHttpPost(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.GET_RECOMMEND_MEM));
        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("wp_member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        entity.put("org_id", org_id);
        httpUtil.setEntity(entity);
        httpUtil.setUseCache(true);
        httpUtil.startConnection(new QLHttpResult() {

            @Override
            public void reply(QLHttpReply reply) {
                try {
                    if (callBack == null || callBack.isCancel())
                        return;
                    YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                    if (responseData != null && responseData.getWp_error_code() == 0) {
                        List<OrgRecommendMemberInfo> list = responseData.parseData("root", new TypeToken<List<OrgRecommendMemberInfo>>() {
                        });
                        callBack.onCallBack(list, null);
                        return;
                    }
                } catch (Exception e) {
                    Log.v("hhhh", "error" + e.toString());
                    e.printStackTrace();
                }
                callBack.onCallBack(null, null);
            }
        });
    }

    /**
     * 删除好友请求
     *
     * @param context
     * @param listener deleteFriendRequestMessageUserManagerController
     */
    public static void deleteFriendRequestMessage(Context context, final Listener<Integer, String> listener) {
        QLHttpUtil httpUtil = new QLHttpPost(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.DELE_REQUEST_FRIEND_MESSAGE));

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
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if (responseData != null && responseData.isSuccess()) {
                    listener.onCallBack(1, null);
                    return;
                }
                if (responseData != null) {
                    listener.onCallBack(-1, responseData.getErrorMsg());
                }
            }
        });
    }


    /**
     * 微信登录
     * @param context
     * @param listener
     */
    public static void weiXinLogin(final Context context , String code , final Listener<Boolean , ArrayList<UserInfo>> listener ){
        QLHttpUtil httpUtil = new QLHttpPost(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.WEIXING_SIGN_IN));
        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("code", code );
        httpUtil.setEntity(entity);
        httpUtil.setUseCache(false);
        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                if(null == listener || listener.isCancel()){
                    return;
                }
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if( null != responseData && responseData.isSuccess()){
                    JSONObject json = QLJsonUtil.doJSONObject(reply.getReplyMsgAsString());
                    QLConstant.token = QLJsonUtil.doString(json.get("token"));
                    SharepreferencesUtils utils = new SharepreferencesUtils(context);
                    utils.setToken(QLConstant.token);
                    ArrayList<UserInfo> list = responseData.parseData("root", new TypeToken<ArrayList<UserInfo>>() {});

                    UserInfo info = null;
                    if (list != null && list.size() > 0) {
                        info = list.get(0);
                        _usinfo = info;
                        PropertySaveHelper.getHelper().save(info, kUSER_INFO_KEY);
                    }
                    listener.onCallBack(true , list);
                }else{
                    if (null != responseData && responseData.getWp_error_code()== 222){
                        ArrayList<UserInfo> list = new ArrayList<UserInfo>();
                        UserInfo info = new UserInfo();
                        JSONObject json = QLJsonUtil.doJSONObject(reply.getReplyMsgAsString());
                        info.setNickName(QLJsonUtil.doString(json.get("nickname")));
                        info.setPicture_url(QLJsonUtil.doString(json.get("headimgurl")));
                        list.add(info);
                        listener.onCallBack(false , list);
                    }else
                        listener.onCallBack(false , null);

                }
            }
        });
    }

    /**
     * 微信注册
     * @param context
     * @param listener
     */
    public static void weiXinRegister(final Context context , String code , final Listener<Boolean , ArrayList<UserInfo>> listener ){
        QLHttpUtil httpUtil = new QLHttpPost(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.WEIXING_REGISTER));
        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("code", code );
        httpUtil.setEntity(entity);
        httpUtil.setUseCache(false);
        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                if(null == listener || listener.isCancel()){
                    return;
                }
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if( null != responseData && responseData.isSuccess()){
                    JSONObject json = QLJsonUtil.doJSONObject(reply.getReplyMsgAsString());
                    QLConstant.token = QLJsonUtil.doString(json.get("token"));
                    SharepreferencesUtils utils = new SharepreferencesUtils(context);
                    utils.setToken(QLConstant.token);
                    ArrayList<UserInfo> list = responseData.parseData("root", new TypeToken<ArrayList<UserInfo>>() {});

                    UserInfo info = null;
                    if (list != null && list.size() > 0) {
                        info = list.get(0);
                        _usinfo = info;
                        PropertySaveHelper.getHelper().save(info, kUSER_INFO_KEY);
                    }
                    listener.onCallBack(true , list);
                }else{
                    if (null != responseData && responseData.getCode() == 222){
                        listener.onCallBack(false , new ArrayList<UserInfo>());
                    }else
                        listener.onCallBack(false , null);

                }
            }
        });
    }

    /**
     * 微信绑定已有账号
     * @param context
     * @param listener
     */
    public static void weiXinBindAccount(final Context context , String code ,String phone,String psd, final Listener<Boolean , UserInfo> listener ){
        QLHttpUtil httpUtil = new QLHttpPost(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.WEIXING_BINBING_BINBING));
        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("code", code );
        entity.put("phone", phone );
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = sdf.format(new Date());
        entity.put("password", EncryptUtils.getMD5(date+EncryptUtils.getMD5(psd)));
        entity.put("sec", date);
        httpUtil.setEntity(entity);
        httpUtil.setUseCache(false);
        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                if(null == listener || listener.isCancel()){
                    return;
                }
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if( null != responseData && responseData.isSuccess()){
                    JSONObject json = QLJsonUtil.doJSONObject(reply.getReplyMsgAsString());
                    QLConstant.token = QLJsonUtil.doString(json.get("token"));
                    SharepreferencesUtils utils = new SharepreferencesUtils(context);
                    utils.setToken(QLConstant.token);
                    ArrayList<UserInfo> list = responseData.parseData("root", new TypeToken<ArrayList<UserInfo>>() {});

                    UserInfo info = null;
                    if (list != null && list.size() > 0) {
                        info = list.get(0);
                        _usinfo = info;
                        PropertySaveHelper.getHelper().save(info, kUSER_INFO_KEY);
                    }
                    listener.onCallBack(true , info);
                }else{
                        listener.onCallBack(false , null);

                }
            }
        });
    }

    /**
     * 绑定用户
     *
     * @param context
     * @param phone
     * @param
     * @param callBack
     */
    public static void buiding(final Context context, String phone ,String code,String password, final Listener<Boolean, String> callBack) {
        QLHttpUtil httpUtil = new QLHttpPost(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.WEIXING_BUIBING_PHONE));
        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("wp_member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        entity.put("phone", phone );
        entity.put("code", code );

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = sdf.format(new Date());
        entity.put("password", EncryptUtils.getMD5(date+EncryptUtils.getMD5(password)));
        httpUtil.setEntity(entity);
        httpUtil.setUseCache(false);
        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                if (callBack == null || callBack.isCancel()){
                    return;
                }
                String msg = "";
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if( null != responseData && responseData.isSuccess() ){
                    msg = "绑定成功";
                    callBack.onCallBack(true , msg );
                    return;
                }else if(null != responseData){
                    msg = responseData.getError() ;
                }else{
                    msg = "未知错误" ;
                }
                callBack.onCallBack(false , msg );
            }
        });
    }

    /**
     * 绑定用户
     * @param context
     * @param
     * @param
     * @param code
     * @param
     * @param callBack
     */
    public static void buidingUser(final Context context, String wp_member_info_id , String code , final Listener<Boolean, String> callBack) {
        QLHttpUtil httpUtil = new QLHttpPost(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.WEIXING_BINBING_USER));
        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("wp_member_info_id", wp_member_info_id );
        entity.put("code", code );
        entity.put("token", QLConstant.token );
        httpUtil.setEntity(entity);
        httpUtil.setUseCache(false);
        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                if (callBack == null || callBack.isCancel()){
                    return;
                }
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if ( null != responseData && responseData.isSuccess()) {
                    callBack.onCallBack(true, null);
                    return;
                }
                if (responseData != null) {
                    callBack.onCallBack(false, responseData.getError());
                }

            }
        });
    }

    /**
     * 解绑微信
     * @param context
     * @param
     * @param
     * @param code
     * @param
     * @param callBack
     */
    public static void unbuidWx(final Context context, final Listener<Boolean, String> callBack) {
        QLHttpUtil httpUtil = new QLHttpPost(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.WEIXING_RELIEVE_BINBING));
        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("wp_member_info_id", QLConstant.client_id );
        entity.put("token", QLConstant.token );
        httpUtil.setEntity(entity);
        httpUtil.setUseCache(false);
        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                if (callBack == null || callBack.isCancel()){
                    return;
                }
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if ( null != responseData && responseData.isSuccess()) {
                    callBack.onCallBack(true, null);
                    return;
                }
                if (responseData != null) {
                    callBack.onCallBack(false, responseData.getError());
                }

            }
        });
    }

    public static void checkToken(final Context context,String member_id,String token, final Listener<Boolean, String> callBack) {
        QLHttpUtil httpUtil = new QLHttpPost(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.CHECK_TOKEN));
        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("wp_member_info_id", member_id);
        entity.put("token", token );
        httpUtil.setEntity(entity);
        httpUtil.setUseCache(false);
        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                if (callBack == null || callBack.isCancel()){
                    return;
                }
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if ( null != responseData && responseData.isSuccess()) {
                    callBack.onCallBack(true, null);
                    return;
                }
                if (responseData != null) {
                    callBack.onCallBack(false, responseData.getError());
                }

            }
        });
    }

    public static void getCompanyTrade(final Context context , int searchType ,final Listener<Boolean , List<TradeInfo>> listener ){
        QLHttpUtil httpUtil = new QLHttpPost(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.GET_INDUSTRY_OR_SCOPE));
        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("searchType", searchType);
        httpUtil.setEntity(entity);
        httpUtil.setUseCache(false);
        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                if(null == listener || listener.isCancel()){
                    return;
                }
                YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                if( null != responseData && responseData.isSuccess()){
                    ArrayList<TradeInfo> list = responseData.parseData("root", new TypeToken<ArrayList<TradeInfo>>() {});

                    listener.onCallBack(true , list);
                }else{
                    listener.onCallBack(false , null);

                }
            }
        });
    }

    /**
     * 获取好友列表
     *
     * @param context
     * @param callBack
     */
    /**
     *
     * 通讯录查询好友关系
     *
     * @param context
     * @param callBack
     */
    public static void contactCheck(Context context,String phones, final Listener<List<UserInfo>, String> callBack) {
        QLHttpUtil httpUtil = new QLHttpPost(context);
        httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.CHECK_CONTACT_URL));
        HashMap<String, Object> entity = new HashMap<String, Object>();
        entity.put("wp_member_info_id", QLConstant.client_id);
        entity.put("token", QLConstant.token);
        entity.put("phones", phones);
        httpUtil.setEntity(entity);
        httpUtil.setUseCache(true);
        httpUtil.setConnectionTimeOut(1000 * 60);
        httpUtil.startConnection(new QLHttpResult() {
            @Override
            public void reply(QLHttpReply reply) {
                try {
                    if (callBack == null || callBack.isCancel())
                        return;
                    YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
                    if (responseData != null && responseData.getWp_error_code() == 0) {
                        List<UserInfo> requestInfos = responseData.parseData("root", new TypeToken<List<UserInfo>>() {});
                        callBack.onCallBack(requestInfos, null);
                        return;
                    }
                } catch (Exception e) {
                    Log.v("hhhh", "error" + e.toString());
                    e.printStackTrace();
                }
                callBack.onCallBack(null, null);
            }
        });
    }
}
