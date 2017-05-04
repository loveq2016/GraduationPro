package app.config.http;



import org.ql.utils.QLToastUtils;
import org.ql.utils.network.QLHttpReply;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import app.logic.activity.user.LoginActivity;
import app.utils.helpers.SharepreferencesUtils;

/**
 *
 *@author SiuJiYung  
 * create at 2016-5-18下午11:53:17
 * 
 * 处理token错误（多设备登陆）
 */
public class YYTokenErrorHandler {

	public static boolean handlResult(Context context,QLHttpReply reply){
		boolean token_ok = true;
		YYResponseData responseData = YYResponseData.parseJsonString(reply.getReplyMsgAsString());
		if (responseData != null) {
			String errorMessage = responseData.getErrorMsg();
			String result = responseData.getResult();
			if (errorMessage == null) {
				errorMessage = responseData.getMsg();
			}
			if (errorMessage == null) {
				errorMessage = responseData.getError();
			}
			if ((errorMessage != null && errorMessage.contains("token不符")) ||(result != null && result.contains("token不符"))) {
				token_ok = false;
				SharepreferencesUtils utils = new SharepreferencesUtils(context);
				String psw = utils.getPassword();
				if (psw == null || TextUtils.isEmpty(psw) || psw.equals("")) {
					return token_ok;
				}
				utils.setPassword("");//清空登陆密码
				Intent intent = new Intent();
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS|Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(context, LoginActivity.class);
				context.startActivity(intent);
				QLToastUtils.showToast(context, "你的账号已经在其他地方登陆");
				
			}
		}
		return token_ok;
	}
}
