package app.logic.controller;

import java.util.HashMap;
import java.util.List;

import org.QLConstant;
import org.ql.utils.network.QLHttpGet;
import org.ql.utils.network.QLHttpReply;
import org.ql.utils.network.QLHttpResult;
import org.ql.utils.network.QLHttpUtil;

import com.google.gson.reflect.TypeToken;

import android.content.Context;
import app.config.http.HttpConfig;
import app.config.http.YYResponseData;
import app.logic.pojo.CheckInInfo;
import app.logic.pojo.OrganizationInfo;
import app.logic.pojo.TYLocationInfo;
import app.logic.pojo.UnCheckInInfo;
import app.utils.common.Listener;

/**
*
* SiuJiYung create at 2016年8月3日 下午5:08:25
*
*/

public class CheckInController {

	/**
	 * 签到
	 * @param context
	 * @param location
	 * @param org_id
	 */
	public static void checkIn(Context context,TYLocationInfo location,String org_id,final Listener<Boolean, String> listener){
		QLHttpUtil httpUtil = new QLHttpGet(context);
		httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.CHECK_IN));

		HashMap<String, Object> entity = new HashMap<String, Object>();
		entity.put("member_info_id", QLConstant.client_id);
		entity.put("token", QLConstant.token);
		
		entity.put("ckin_lng", location.longitude);
		entity.put("chin_lat", location.latitude);
		entity.put("chin_addr", location.getLocationAddr());
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
	 * 获取签到列表
	 * @param context
	 * @param startDate
	 * @param endDate
	 * @param org_id
	 * @param listener
	 */
	public static void getCheckInList(Context context,String startDate,String endDate,String org_id,final Listener<Void, List<CheckInInfo>> listener){
		QLHttpUtil httpUtil = new QLHttpGet(context);
		httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.GET_CHECK_IN_LIST));

		HashMap<String, Object> entity = new HashMap<String, Object>();
		entity.put("member_info_id", QLConstant.client_id);
		entity.put("token", QLConstant.token);
		
		entity.put("start_date", startDate);
		entity.put("end_date", endDate);
		entity.put("query_member_info_id", QLConstant.client_id);
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
				YYResponseData responseData = YYResponseData.parseJsonString( reply.getReplyMsgAsString() );
				if (responseData != null) {
					List<CheckInInfo> checkInInfos = responseData.parseData("root", new TypeToken<List<CheckInInfo>>(){});
					listener.onCallBack(null, checkInInfos);
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
	 * 获取签到列表
	 * @param context
	 * @param startDate
	 * @param endDate
	 * @param org_id
	 * @param listener
	 */
	public static void getCheckAndUnchexkList(Context context,String startDate,String endDate,String org_id,final Listener<List<UnCheckInInfo>, List<CheckInInfo>> listener){
		QLHttpUtil httpUtil = new QLHttpGet(context);
		httpUtil.setUrl(HttpConfig.getUrl( HttpConfig.GET_CHECK_IN_LIST_UN ));
		HashMap<String, Object> entity = new HashMap<String, Object>();
		entity.put("member_info_id", QLConstant.client_id);
		entity.put("token", QLConstant.token);
		entity.put("start_date", startDate);
		entity.put("end_date", endDate);
		entity.put("query_member_info_id", QLConstant.client_id);
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
				if (responseData != null) {
					List<CheckInInfo> checkInInfos = responseData.parseData("checked", new TypeToken<List<CheckInInfo>>(){});
					List<UnCheckInInfo> nuCheckInInfos = responseData.parseData("unchecked", new TypeToken<List<UnCheckInInfo>>(){});
					listener.onCallBack( nuCheckInInfos, checkInInfos );
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
