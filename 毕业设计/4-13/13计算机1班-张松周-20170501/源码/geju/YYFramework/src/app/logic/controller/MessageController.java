package app.logic.controller;

import java.util.HashMap;
import java.util.List;

import org.QLConstant;
import org.ql.utils.network.QLHttpGet;
import org.ql.utils.network.QLHttpPost;
import org.ql.utils.network.QLHttpReply;
import org.ql.utils.network.QLHttpResult;
import org.ql.utils.network.QLHttpUtil;

import com.google.gson.reflect.TypeToken;

import android.content.Context;
import app.config.http.HttpConfig;
import app.config.http.YYResponseData;
import app.logic.pojo.CheckInInfo;
import app.logic.pojo.MessageInfo;
import app.utils.common.Listener;

/**
*
* SiuJiYung create at 2016年8月3日 下午5:08:38
*
*/

public class MessageController {

	/**
	 * 获取消息列表
	 * @param context
	 * @param start
	 * @param org_id
	 * @param listener
	 */
	public static void getMessageList(Context context,int start,String org_id,final Listener<Void, List<MessageInfo>> listener){
		QLHttpUtil httpUtil = new QLHttpGet(context);
		httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.GET_MESSAGE_LIST));

		HashMap<String, Object> entity = new HashMap<String, Object>();
		entity.put("member_info_id", QLConstant.client_id);
		entity.put("token", QLConstant.token);
		
		entity.put("start", start);
		entity.put("limit", 30);
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
					List<MessageInfo> msgList = responseData.parseData("root", new TypeToken<List<MessageInfo>>(){});
					listener.onCallBack(null, msgList);
					return;
				}
				if (responseData != null) {
					msg = responseData.getErrorMsg();
				}
				listener.onCallBack(null, null);
			}
		});
	}
	
	/**
	 * 发送公告
	 * @param context
	 * @param msg
	 * @param org_id
	 * @param coverBase64
	 * @param pictrues
	 * @param listener
	 */
	public static void sendMessage(Context context,MessageInfo msg,String org_id,String coverBase64,List<String> pictrues,final Listener<Boolean, String> listener){
		QLHttpUtil httpUtil = new QLHttpPost(context);
		httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.POST_MESSAGE));

		HashMap<String, Object> entity = new HashMap<String, Object>();
		entity.put("member_info_id", QLConstant.client_id);
		entity.put("token", QLConstant.token);
		
		entity.put("msg_title", msg.getMsg_title());
		entity.put("msg_content", msg.getMsg_content());
		entity.put("org_id", org_id);
		entity.put("msg_type", msg.getMsg_type());
		if (coverBase64 != null) {
			entity.put("msg_cover", coverBase64);
		}
		if (pictrues != null && pictrues.size() > 0) {
			StringBuilder sBuilder = new StringBuilder();
			for (int idx = 0; idx < pictrues.size(); idx++) {
				String pic = pictrues.get(idx);
				sBuilder.append(pic);
				if (idx != pictrues.size() - 1) {
					sBuilder.append(",");
				}
			}
			entity.put("msg_pictrues", sBuilder.toString());
		}

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
	 * 获取消息明细
	 * @param context
	 * @param msg_id
	 * @param listener
	 */
	public static void getMessageDetail(Context context,String msg_id,final Listener<Void, MessageInfo> listener){
		QLHttpUtil httpUtil = new QLHttpGet(context);
		httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.GET_MESSAGE_DETAIL));

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
					List<MessageInfo> msgList = responseData.parseData("root", new TypeToken<List<MessageInfo>>(){});
					MessageInfo info = null;
					if (msgList != null && msgList.size() > 0) {
						info = msgList.get(0);
					}
					listener.onCallBack(null, info);
					return;
				}
				if (responseData != null) {
					msg = responseData.getErrorMsg();
				}
				listener.onCallBack(null, null);
			}
		});
	}
	
}
