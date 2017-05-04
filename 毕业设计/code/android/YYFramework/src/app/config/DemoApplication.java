/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package app.config;

import org.ql.utils.debug.QLLog;
import android.content.Context;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.os.Vibrator;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import app.logic.activity.live.CarouselImgInfo;
import app.logic.call.activity.CallReceiver;
import app.logic.controller.OrganizationController;
import app.utils.common.AndroidFactory;
import app.utils.common.Listener;
import app.utils.debug.YYDebugHandler;
import app.utils.helpers.PropertySaveHelper;
import app.utils.managers.TYLocationManager;
import cn.jpush.android.api.JPushInterface;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.controller.EaseUI;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.ucloud.ulive.UStreamingContext;

import java.util.ArrayList;

public class DemoApplication extends MultiDexApplication {
//public class DemoApplication extends Application {

	 // user your appid the key.
    private static final String APP_ID = "2882303761517517983";
    // user your appid the key.
    private static final String APP_KEY = "5421751746983";
	public static Context applicationContext;
	private static DemoApplication instance;
	public final String PREF_USERNAME = "username";
	public static final String BMapKey = "n9nzbvkkX9nKVFlcGlqDkHfcGrgoH9WB";
	public static int QRInsideImg = 30;  //二维码内部图片大小
	public static final String WEIXN_APP_ID = "wxc7e678e1a1e3165d";  //我们的应用在微信上的ID


	/**
	 * 当前用户nickname,为了苹果推送不是userid而是昵称
	 */
	public static String currentUserNick = "";
//	public static DemoHXSDKHelper hxSDKHelper = new DemoHXSDKHelper();

	protected Vibrator vibrator;
	protected Ringtone ringtone ;

	@Override
	public void onCreate() {
		super.onCreate();

		QLLog.WRITE_FILE = false;
		AndroidFactory.setApplicationContext(getApplicationContext());
		// 初始化配置器
		YYAppConfig.shareInstance().init(getApplicationContext());
		// 初始化数据采集器
		YYDebugHandler.getShareInstance().initDataRecorder(getApplicationContext());
		applicationContext = this;
		instance = this;

		ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this).setDownsampleEnabled(true).build();
		Fresco.initialize(this,config);

		PropertySaveHelper helper = PropertySaveHelper.getHelper();
		helper.setContext(applicationContext);
		Log.v("hhhh", "jpus");
		JPushInterface.setDebugMode(true);
		JPushInterface.init(this);
		JPushInterface.setLatestNotificationNumber(getApplicationContext(), 5);// 只显示一条
//		JPushInterface.setAliasAndTags(this,);
		/**
		 * @return boolean true if caller can continue to call HuanXin related
		 *         APIs after calling onInit, otherwise false.
		 *         环信初始化SDK帮助函数
		 *         返回true如果正确初始化，否则false，如果返回为false，请在后续的调用中不要调用任何和环信相关的代码
		 *         for example: 例子：
		 *         public class DemoHXSDKHelper extends HXSDKHelper
		 *         HXHelper = new DemoHXSDKHelper();
		 *         if(HXHelper.onInit(context)){ // do HuanXin related work }
		 */
		EaseUI.getInstance().init(applicationContext, null);
//		hxSDKHelper.onInit(applicationContext);
//		EMChatManager.getInstance().setMipushConfig("2882303761517517983", "5421751746983");
		TYLocationManager.getShareLocationManager();
		UStreamingContext.init(getApplicationContext(), "krapnik_cn_ucloud_20170302");//  krapnik_ucloud_20170223   publish3-key
		// SDKInitializer.initialize(getApplicationContext());// 百度地图SDK初始化
		registerActivityLifecycleCallbacks( new MyLifecycleHandler());
		rogToWx();
		try {
			Class.forName("android.os.AsyncTask");
		} catch (Exception e) {
			e.printStackTrace();
		}
//		bindService(emPushServiceIntent,serviceConnection,Context.BIND_AUTO_CREATE);

 }

	public static DemoApplication getInstance() {
		return instance;
	}


	/**
	 * 获取内存中好友user list
	 * 
	 * @return
	 */
//	public Map<String, User> getContactList() {
//		return hxSDKHelper.getContactList();
//	}

	/**
	 * 设置好友user list到内存中
	 * 
	 * @param contactList
	 */
//	public void setContactList(Map<String, EaseUser> contactList) {
//		hxSDKHelper.setContactList(contactList);
//	}

	/**
	 * 获取当前登陆用户名
	 * 
	 * @return
	 */
//	public String getUserName() {
//		return hxSDKHelper.getHXId();
//	}

	/**
	 * 获取密码
	 * 
	 * @return
	 */
//	public String getPassword() {
//		return hxSDKHelper.getPassword();
//	}

	/**
	 * 设置用户名
	 * 
	 * @param user
	 */
//	public void setUserName(String username) {
//		hxSDKHelper.setHXId(username);
//	}

	/**
	 * 设置密码 下面的实例代码 只是demo，实际的应用中需要加password 加密后存入 preference 环信sdk
	 * 内部的自动登录需要的密码，已经加密存储了
	 * 
	 * @param pwd
	 */
//	public void setPassword(String pwd) {
//		hxSDKHelper.setPassword(pwd);
//	}

	/**
	 * 退出登录,清空数据
	 */
//	public void logout(final EMCallBack emCallBack) {
//		// 先调用sdk logout，在清理app中自己的数据
//		hxSDKHelper.logout(emCallBack);
//
//		EMChatManager.getInstance().logout(true,emCallBack);
//
//	}


	/**
	 * j将我们的应用注册到微信终端，否则微信不响应为的用应用
	 */
	private IWXAPI iwxapi ;
	private void rogToWx(){
		iwxapi = WXAPIFactory.createWXAPI(this, WEIXN_APP_ID , true);
		iwxapi.registerApp(WEIXN_APP_ID);
	}
}
