/*
 * 官网地站:http://www.mob.com
 * 技术支持QQ: 4006852216
 * 官方微信:ShareSDK   （如果发布新版本的话，我们将会第一时间通过微信将版本更新内容推送给您。如果使用过程中有任何问题，也可以通过微信与我们取得联系，我们将会在24小时内给予回复）
 *
 * Copyright (c) 2013年 mob.com. All rights reserved.
 */

package app.yy.geju.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.QLConstant;

import java.util.ArrayList;

import app.config.DemoApplication;
import app.logic.activity.main.HomeActivity;
import app.logic.activity.user.BindingPhoneActivity;
import app.logic.activity.user.LoginActivity;
import app.logic.activity.user.PrepareLoginActivity;
import app.logic.activity.user.UserInfoActivity;
import app.logic.controller.UserManagerController;
import app.logic.pojo.UserInfo;
import app.utils.common.Listener;
import app.utils.helpers.SharepreferencesUtils;
import cn.jpush.android.api.JPushInterface;
import cn.sharesdk.wechat.utils.WXAppExtendObject;
import cn.sharesdk.wechat.utils.WXMediaMessage;
import cn.sharesdk.wechat.utils.WechatHandlerActivity;

/** 微信客户端回调activity示例 */
public class WXEntryActivity extends WechatHandlerActivity implements IWXAPIEventHandler { //

    private IWXAPI api ;
	private SharepreferencesUtils utils;  //本地保存对象
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		utils = new SharepreferencesUtils(this);
		api =  WXAPIFactory.createWXAPI(this, DemoApplication.WEIXN_APP_ID, true);
		api.handleIntent(this.getIntent(), this); //这两句不能少，否则接口的方法不被回调（被坑了一个早上）
	}

	/**
	 * 处理微信发出的向第三方应用请求app message
	 * <p>
	 * 在微信客户端中的聊天页面有“添加工具”，可以将本应用的图标添加到其中
	 * 此后点击图标，下面的代码会被执行。Demo仅仅只是打开自己而已，但你可
	 * 做点其他的事情，包括根本不打开任何页面
	 */
	public void onGetMessageFromWXReq(WXMediaMessage msg) {
		if (msg != null) {
			Intent iLaunchMyself = getPackageManager().getLaunchIntentForPackage(getPackageName());
			startActivity(iLaunchMyself);
		}
	}

	/**
	 * 处理微信向第三方应用发起的消息
	 * <p>
	 * 此处用来接收从微信发送过来的消息，比方说本demo在wechatpage里面分享
	 * 应用时可以不分享应用文件，而分享一段应用的自定义信息。接受方的微信
	 * 客户端会通过这个方法，将这个信息发送回接收方手机上的本demo中，当作
	 * 回调。
	 * <p>
	 * 本Demo只是将信息展示出来，但你可做点其他的事情，而不仅仅只是Toast
	 */
	public void onShowMessageFromWXReq(WXMediaMessage msg) {
		if (msg != null && msg.mediaObject != null
				&& (msg.mediaObject instanceof WXAppExtendObject)) {
			WXAppExtendObject obj = (WXAppExtendObject) msg.mediaObject;
			Toast.makeText(this, obj.extInfo, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onReq(BaseReq baseReq) {
		System.out.println(" 回调到  ： = onReq" );
	}

	@Override
	public void onResp(BaseResp baseResp) {
		switch (baseResp.errCode) {
			case BaseResp.ErrCode.ERR_OK:
				SendAuth.Resp sendResp = (SendAuth.Resp) baseResp ;
				final String code = sendResp.code ;
				utils.setWxCode(code);  //保存微信的code
				System.out.println(" code  ： = " + code) ;

				if (((SendAuth.Resp) baseResp).state.equals("wechat_sdk_userinfoactivity")){
					if (UserInfoActivity.wxHandler !=null)
						UserInfoActivity.wxHandler.obtainMessage(0,code).sendToTarget();
				}else{
//					if (LoginActivity.wxhandler !=null){
//						LoginActivity.wxhandler.obtainMessage(0,code).sendToTarget();
//					}
					if (PrepareLoginActivity.wxhandler !=null){
						PrepareLoginActivity.wxhandler.obtainMessage(0,code).sendToTarget();
					}
				}

//				finish();


//				UserManagerController.weiXinLogin(this, code, new Listener<Boolean, ArrayList<UserInfo>>() {
//					@Override
//					public void onCallBack(Boolean aBoolean, final ArrayList<UserInfo> reply) {
//						if(aBoolean){
//							final UserInfo userInfo = reply.get(0);
//							new Thread(new Runnable() {
//								public void run() {
//									EMClient.getInstance().logout(true);
//									QLConstant.client_id = userInfo.getWp_member_info_id();
//									QLConstant.user_picture_url = userInfo.getPicture_url();
//									JPushInterface.setAlias(WXEntryActivity.this, userInfo.getWp_member_info_id(), null);  //极光推送设置别名
//									// im聊天登录
//									new EMOptions().setAutoLogin(true);
//									EMClient.getInstance().login(utils.getUserName(), utils.getPassword(), new EMCallBack() {// 回调
//										@Override
//										public void onSuccess() {
//											runOnUiThread(new Runnable() {
//												public void run() {
//													Log.v("hhhh", "im登陆成功");
//													//从本地加载群组列表
//													EMClient.getInstance().groupManager().getAllGroups();
//													EMClient.getInstance().chatManager().loadAllConversations();
//													String usr = EMClient.getInstance().getCurrentUser();
//													Log.i("aaaa", EMClient.getInstance().getCurrentUser() );
//													//调到主界面
//													Intent intent = new Intent();
//													intent.setClass( WXEntryActivity.this , HomeActivity.class);
//													startActivity(intent);
//												}
//											});
//										}
//
//										@Override
//										public void onProgress(int progress, String status) {
//										}
//
//										@Override
//										public void onError(int code, String message) {
//											if (code == 200) {
//												runOnUiThread(new Runnable() {
//													public void run() {
//														Log.v("hhhh", "im登陆成功");
//														//从本地加载群组列表
//														EMClient.getInstance().groupManager().getAllGroups();
//														EMClient.getInstance().chatManager().loadAllConversations();
//													}
//												});
//											} else {
//												Message msg = new Message();
//												msg.what = 111;
//												msg.obj = message;
//												Log.v("hhhh", "登陆聊天服务器失败！");
//											}
//										}
//									});
//								} }).start();
//						}
//					}
//				});
				//发送成功
				break;
			case BaseResp.ErrCode.ERR_USER_CANCEL:
				//发送取消
				break;
			case BaseResp.ErrCode.ERR_AUTH_DENIED:
				//发送被拒绝
				break;
			default:
				//发送返回
				break;
		}
		WXEntryActivity.this.finish();
	}
}
