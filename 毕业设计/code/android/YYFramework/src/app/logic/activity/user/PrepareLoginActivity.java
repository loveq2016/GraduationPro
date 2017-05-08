package app.logic.activity.user;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.QLConstant;
import org.ql.activity.customtitle.ActActivity;
import org.ql.utils.QLToastUtils;

import java.util.ArrayList;
import java.util.List;

import app.config.DemoApplication;
import app.config.http.HttpConfig;
import app.logic.activity.main.HomeActivity;
import app.logic.controller.UserManagerController;
import app.logic.pojo.ChatMessageInfo;
import app.logic.pojo.UserInfo;
import app.logic.view.web.WebBrowserActivity;
import app.utils.common.Listener;
import app.utils.helpers.SharepreferencesUtils;
import app.yy.geju.R;
import cn.jpush.android.api.JPushInterface;

import static app.config.http.HttpConfig.JOIN_NOTICE_CENTER;

/**
 * Created by apple on 17/4/28.
 */

public class PrepareLoginActivity extends ActActivity implements View.OnClickListener{

    private SharepreferencesUtils utils;  //本地保存对象
    private IWXAPI api ;
    public static Handler wxhandler ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepare_login);
        api = WXAPIFactory.createWXAPI(this, DemoApplication.WEIXN_APP_ID , true);

        findViewById(R.id.login_phone).setOnClickListener(this);
        findViewById(R.id.login_wechat).setOnClickListener(this);
        findViewById(R.id.login_role).setOnClickListener(this);

        ((TextView)findViewById(R.id.login_role)).setText(Html.fromHtml("登录即代表您同意《<u><font color=\"#ff0000\">格局软件使用服务协议</font></u>》"));
        utils = new SharepreferencesUtils(this);

        initWxHandler();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_phone:
                startActivity(new Intent(this,LoginActivity.class));
                break;
            case R.id.login_wechat:
                if (isWeixinAvilible(this)) {
                    SendAuth.Req req = new SendAuth.Req();
                    req.scope = "snsapi_userinfo";
                    req.state = "wechat_sdk_demo_test";
                    //发送授权登陆请求
                    api.sendReq(req);
                }else
                    QLToastUtils.showToast(this,"未安装微信，请先安装");

                break;
            case R.id.login_role:
                startActivity(new Intent(this, WebBrowserActivity.class).putExtra(WebBrowserActivity.KBROWSER_HOME_URL, HttpConfig.SERVICE_TERM));


                break;
        }
    }

    public static boolean isWeixinAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }

        return false;
    }


    private Handler mhanler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 000:
                    dismissWaitDialog();
                    Intent intent = new Intent(PrepareLoginActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    ChatMessageInfo info = (ChatMessageInfo) getIntent().getSerializableExtra("info");
                    if (info != null) {
                        intent.putExtra("info", info);
                    }
                    utils.setNeedLogin(false);
                    startActivity(intent);
                    break;
                case 111:
                    dismissWaitDialog();
                    String msg_str = (String) msg.obj;
                    if (msg_str != null) {
                        QLToastUtils.showToast(PrepareLoginActivity.this, msg_str);
                    }
                default:
                    break;
            }
        }
    };

    private void initWxHandler(){
        wxhandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                String wxCode = msg.obj.toString();
                if (!TextUtils.isEmpty(wxCode)){
                    wxLogin(wxCode);
                }
            }
        };
    }

    private void wxLogin(final String wxCode){
        showWaitDialog();
        UserManagerController.weiXinLogin(this, wxCode, new Listener<Boolean, ArrayList<UserInfo>>() {
            @Override
            public void onCallBack(Boolean aBoolean, final ArrayList<UserInfo> reply) {
//                dismissWaitDialog();
                if(aBoolean){
                    final UserInfo userInfo = reply.get(0);
                    utils.setLoginType(1);
                    utils.setNeedLogin(false);
                    new Thread(new Runnable() {
                        public void run() {
                            EMClient.getInstance().logout(true);
                            QLConstant.client_id = userInfo.getWp_member_info_id();
                            QLConstant.user_picture_url = userInfo.getPicture_url();
                            JPushInterface.setAlias(PrepareLoginActivity.this, userInfo.getWp_member_info_id(), null);  //极光推送设置别名
                            // im聊天登录
                            new EMOptions().setAutoLogin(true);
                            String chartpsw = "wudi#" + userInfo.getWp_member_info_id();
                            EMClient.getInstance().login(userInfo.getWp_member_info_id(), chartpsw, new EMCallBack() {// 回调
                                @Override
                                public void onSuccess() {
                                    dismissWaitDialog();
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Log.v("hhhh", "im登陆成功");
                                            //从本地加载群组列表
                                            EMClient.getInstance().groupManager().getAllGroups();
                                            EMClient.getInstance().chatManager().loadAllConversations();
                                            String usr = EMClient.getInstance().getCurrentUser();
                                            Log.i("aaaa", EMClient.getInstance().getCurrentUser() );
                                            //调到主界面
                                            Intent intent = new Intent();
                                            intent.setClass( PrepareLoginActivity.this , HomeActivity.class);
                                            startActivity(intent);
                                        }
                                    });
                                }

                                @Override
                                public void onProgress(int progress, String status) {
                                }

                                @Override
                                public void onError(int code, String message) {
                                    dismissWaitDialog();
                                    if (code == 200) {
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                Log.v("hhhh", "im登陆成功");
                                                //从本地加载群组列表
                                                EMClient.getInstance().groupManager().getAllGroups();
                                                EMClient.getInstance().chatManager().loadAllConversations();
                                                mhanler.sendEmptyMessage(000);
                                            }
                                        });
                                    } else {
                                        Message msg = new Message();
                                        msg.what = 111;
                                        msg.obj = message;
                                        mhanler.sendMessage(msg);
                                        Log.v("hhhh", "登陆聊天服务器失败！");
                                    }
                                }
                            });
                        } }).start();
                }else{
                    if (reply !=null && reply.size()>0){
                        UserInfo info = reply.get(0);
                        startActivityForResult(new Intent(PrepareLoginActivity.this,WXBindPhoneActivity.class).putExtra(WXBindPhoneActivity.WX_CODE,wxCode).putExtra("nickname",info.getNickName()).putExtra("headimgurl",info.getPicture_url()),0);
                    }else
                        QLToastUtils.showToast(PrepareLoginActivity.this,"登录失败，请重试");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        dismissWaitDialog();
    }
}
