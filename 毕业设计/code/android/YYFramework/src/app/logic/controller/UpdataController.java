package app.logic.controller;

import java.util.List;

import org.ql.utils.network.QLHttpGet;
import org.ql.utils.network.QLHttpReply;
import org.ql.utils.network.QLHttpResult;
import org.ql.utils.network.QLHttpUtil;

import com.google.gson.reflect.TypeToken;

import android.content.Context;
import app.config.http.HttpConfig;
import app.config.http.YYResponseData;
import app.logic.pojo.UpdataAppInfo;
import app.utils.common.Listener;

/*
 * GZYY    2016-9-5  下午3:20:01
 */

public class UpdataController {

	public static void getAppVersion(Context context, final Listener<Void, List<UpdataAppInfo>> listener) {
		QLHttpUtil httpUtil = new QLHttpGet(context);
		httpUtil.setUrl(HttpConfig.getUrl(HttpConfig.GET_CHECK_UPDATA_APP));
		httpUtil.setUseCache(false);
		httpUtil.startConnection(new QLHttpResult() {
			@Override
			public void reply(QLHttpReply reply) {
				if (listener == null || listener.isCancel()) {
					return;
				}
				YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
				if (responseData != null && responseData.isSuccess()) {
					List<UpdataAppInfo> list = responseData.parseData("root", new TypeToken<List<UpdataAppInfo>>() {

					});
					listener.onCallBack(null, list);
					return;
				}
				listener.onCallBack(null, null);
			}
		});
	}
}
