package app.logic.controller;

import android.content.Context;

import com.google.gson.reflect.TypeToken;

import org.QLConstant;
import org.ql.utils.network.QLHttpGet;
import org.ql.utils.network.QLHttpPost;
import org.ql.utils.network.QLHttpReply;
import org.ql.utils.network.QLHttpResult;
import org.ql.utils.network.QLHttpUtil;

import java.util.HashMap;
import java.util.List;

import app.config.http.HttpConfig;
import app.config.http.YYResponseData;
import app.logic.pojo.NoticeInfo;
import app.logic.pojo.UserInfo;
import app.utils.common.Listener;

/*
 * GZYY    2016-8-8  上午9:10:34
 */

public class AnnounceController {

	/*
	 * 发布公告
	 */
	public static void announceUser(Context context, String msg_title, String org_id, String msg_content, final Listener<Boolean, String> listener) {
		QLHttpUtil httpUtil = new QLHttpPost(context);
		httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.POST_MESSAGE));

		UserInfo user = UserManagerController.getCurrUserInfo();
		HashMap<String, Object> entity = new HashMap<String, Object>();
		
		
		if(QLConstant.client_id ==null){
			entity.put("member_info_id",user.getWp_member_info_id());
		}else{
			entity.put("member_info_id", QLConstant.client_id);
			entity.put("token", QLConstant.token);
		}
		
		entity.put("msg_title", msg_title);
		entity.put("org_id", org_id);
		entity.put("msg_content", msg_content);
		entity.put("msg_type", 1);
		
		entity.put("msg_cover", 1);
		entity.put("msg_pictrues", 1);

		httpUtil.setEntity(entity);
		httpUtil.setUseCache(false);
		httpUtil.startConnection(new QLHttpResult() {

			@Override
			public void reply(QLHttpReply reply) {
				if (listener == null || listener.isCancel()) {
					return;
				}
				String msgString = null;
				YYResponseData responseData = YYResponseData.parseJsonString( reply.getReplyMsgAsString() );
				if (responseData != null && responseData.isSuccess()) {
					listener.onCallBack( responseData.isSuccess(), null );
					return;
				}
				if( responseData != null ){
					listener.onCallBack(responseData.isSuccess(), responseData.getErrorMsg());
			    }
			}
		});

	}
	
	/*
	 * 发布公告
	 */
	public static void announceUser2(Context context, String msg_title, String org_id, String msg_content, String msg_notice_img_id, final Listener<Boolean, String> listener) {
		QLHttpUtil httpUtil = new QLHttpPost(context);
		httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.POST_MESSAGE));

		UserInfo user = UserManagerController.getCurrUserInfo();
		HashMap<String, Object> entity = new HashMap<>();
		
		if(QLConstant.client_id ==null){		
			entity.put("member_info_id", user.getWp_member_info_id());		
		}else{
			entity.put("member_info_id", QLConstant.client_id);
		}
		
		entity.put("token", QLConstant.token);
		entity.put("msg_title", msg_title);
		entity.put("org_id", org_id);
		entity.put("msg_content", msg_content);
		entity.put("msg_type", 1);
		entity.put("msg_notice_img_id", msg_notice_img_id);
		// String msg_notice_img = YYFileManager.imgToBase64( msg_notice_img_path );
		// entity.put("msg_notice_img", msg_notice_img_path );

		httpUtil.setEntity(entity);
		httpUtil.setUseCache(false);
		httpUtil.startConnection( new QLHttpResult() {

			@Override
			public void reply(QLHttpReply reply) {
				if (listener == null || listener.isCancel()) {
					return;
				}
				String msgString = null;
				YYResponseData responseData = YYResponseData.parseJsonString( reply.getReplyMsgAsString() );
				if (responseData != null && responseData.isSuccess()) {
					listener.onCallBack( responseData.isSuccess(), null );
					return;
				}
				if( responseData != null ){
					listener.onCallBack( responseData.isSuccess(), responseData.getErrorMsg() );
				}			
			}
		});

	}

	/*
	 * 分页页码，分页最大条数，组织ID，公告类型
	 */

	public static void getAnnounceList(Context context, String start, String limit, String org_id, String msg_type,String msg_unread, final Listener<Void, List<NoticeInfo>> listener) {

		QLHttpUtil httpUtil = new QLHttpGet(context);
		httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.GET_MESSAGE_LIST));

		HashMap<String, Object> entity = new HashMap<String, Object>();
		entity.put("member_info_id", QLConstant.client_id);
		entity.put("token", QLConstant.token);
		entity.put("start", start);
		entity.put("limit", limit);
		entity.put("org_id", org_id);
		entity.put("msg_type", msg_type);
		entity.put("msg_unread", msg_unread);
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
				if (responseData != null) {
					List<NoticeInfo> list = responseData.parseData("root", new TypeToken<List<NoticeInfo>>() {
					});
					listener.onCallBack(null, list);
					return;
				}
				listener.onCallBack(null, null);
			}
		});
	}

	public static void getAnnounceDetail(Context context, String msg_id, final Listener<Void, List<NoticeInfo>> listener) {
		QLHttpUtil httpUtil = new QLHttpGet(context);
		httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.GET_MESSAGE_DETAIL));

		HashMap<String, Object> entity = new HashMap<String, Object>();
		entity.put("member_info_id", QLConstant.client_id);
		entity.put("token", QLConstant.token);
		entity.put("msg_id", msg_id);

		httpUtil.setEntity(entity);
		httpUtil.setUseCache(false);
		httpUtil.startConnection( new QLHttpResult() {

			@Override
			public void reply(QLHttpReply reply) {
				if (listener == null || listener.isCancel()) {
					return;
				}
				YYResponseData responseData = YYResponseData.parseJsonString( reply.getReplyMsgAsString() );
				if (responseData != null) {
					List<NoticeInfo> list = responseData.parseData("root", new TypeToken<List<NoticeInfo>>() {
					});
					listener.onCallBack(null, list);
					return;
				}
				listener.onCallBack(null, null);
			}
		});

	}

	public static void removeAnnounceInfo(Context context, String msg_id, final Listener<Boolean, String> listener) {
		QLHttpUtil httpUtil = new QLHttpGet(context);
		httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.REMOVE_MSG));
		HashMap<String, Object> entity = new HashMap<String, Object>();
		entity.put("member_info_id", QLConstant.client_id);
		entity.put("token", QLConstant.token);
		entity.put("msg_id", msg_id);
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
					listener.onCallBack(Boolean.valueOf(true), null);
					return;
				}
				if (responseData != null) {
					msg = responseData.getErrorMsg();
				}
				listener.onCallBack(Boolean.valueOf(false), msg);
			}
		});
	}

	/**
	 * 添加公告点赞，参与，举报
	 * @param context
	 * @param content 点赞和参数，0代表取消，1代表确定举报的时候是举报内容
	 * @param type 类型0点赞，1参与，2举报
	 * @param contentPicUrl 举报上传的图片，多张用逗号隔开
	 * @param msg_id 公告id
     * @param listener
     */
	public static void addMsgExtentionInfo(Context context,String content,int type,String contentPicUrl, String msg_id, final Listener<Boolean, String> listener) {
		QLHttpUtil httpUtil = new QLHttpGet(context);
		httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.Add_MSG_EXTENTION));
		HashMap<String, Object> entity = new HashMap<String, Object>();
		entity.put("member_info_id", QLConstant.client_id);
		entity.put("wp_member_info_id", QLConstant.client_id);
		entity.put("token", QLConstant.token);
		entity.put("msg_id", msg_id);
		entity.put("content", content);
		entity.put("type", type);
		entity.put("content_picture_url", contentPicUrl);
		httpUtil.setEntity(entity);
		httpUtil.setUseCache(false);
		httpUtil.startConnection(new QLHttpResult() {

			@Override
			public void reply(QLHttpReply reply) {
				if (listener == null || listener.isCancel()) {
					return;
				}

				String msg = "操作失败";
				YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
				if (responseData != null && responseData.isSuccess()) {
					listener.onCallBack(Boolean.valueOf(true), responseData.getMsg());
					return;
				}
				if (responseData != null) {
					msg = responseData.getErrorMsg();
				}
				listener.onCallBack(Boolean.valueOf(false), msg);
			}
		});
	}
}
