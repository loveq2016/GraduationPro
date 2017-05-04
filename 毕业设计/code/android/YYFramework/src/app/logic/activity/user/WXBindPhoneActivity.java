package app.logic.activity.user;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.squareup.picasso.Picasso;

import org.QLConstant;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ql.activity.customtitle.ActActivity;
import org.ql.activity.customtitle.OnActActivityResultListener;
import org.ql.utils.QLToastUtils;

import java.io.File;
import java.util.ArrayList;

import app.config.http.HttpConfig;
import app.logic.activity.ActTitleHandler;
import app.logic.activity.announce.FileUploader;
import app.logic.activity.live.StartLiveActivity;
import app.logic.activity.main.HomeActivity;
import app.logic.controller.LivestreamController;
import app.logic.controller.UserManagerController;
import app.logic.pojo.ChatMessageInfo;
import app.logic.pojo.UserInfo;
import app.utils.common.FrescoImageShowThumb;
import app.utils.common.Listener;
import app.utils.helpers.ImagePickerHelper;
import app.utils.helpers.SharepreferencesUtils;
import app.yy.geju.R;
import cn.jpush.android.api.JPushInterface;

public class WXBindPhoneActivity extends ActActivity implements OnClickListener {

    public static final String WX_CODE = "WX_CODE";
    private ActTitleHandler titleHandler = new ActTitleHandler();
    private SimpleDraweeView mWxImage;
    private TextView mWxNickName;

    private SharepreferencesUtils utils;
    private String wx_code,nickName,headimgurl;

    private Handler mhanler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 000:
                    dismissWaitDialog();
                    Intent intent = new Intent(WXBindPhoneActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    ChatMessageInfo info = (ChatMessageInfo) getIntent().getSerializableExtra("info");
                    if (info != null) {
                        intent.putExtra("info", info);
                    }
                    utils.setNeedLogin(false);
                    startActivity(intent);
                    finish();
                    break;
                case 111:
                    dismissWaitDialog();
                    String msg_str = (String) msg.obj;
                    if (msg_str != null) {
                        QLToastUtils.showToast(WXBindPhoneActivity.this, msg_str);
                    }
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbsHandler(titleHandler);
        setContentView(R.layout.activity_wx_bind_phone);
        utils = new SharepreferencesUtils(WXBindPhoneActivity.this);
        titleHandler.addLeftView(LayoutInflater.from(this).inflate(R.layout.title_leftview_layout, null), true);
        titleHandler.getLeftLayout().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleHandler.getRightDefButton().setOnClickListener(null);
        setTitle("");
        ((TextView) titleHandler.getLeftLayout().findViewById(R.id.left_tv)).setText("第三方登录完善信息");
        initView();

    }

    /**
     * 出是化View
     */
    private void initView() {
        wx_code = getIntent().getStringExtra(WXBindPhoneActivity.WX_CODE);
        nickName = getIntent().getStringExtra("nickname");
        headimgurl = getIntent().getStringExtra("headimgurl");
        mWxImage = (SimpleDraweeView) findViewById(R.id.wx_logo);
        mWxNickName = (TextView) findViewById(R.id.wx_nick);

        FrescoImageShowThumb.showThrumb(Uri.parse(headimgurl),mWxImage);
        mWxNickName.setText(nickName);

        findViewById(R.id.start_btn).setOnClickListener(this);
        findViewById(R.id.cancel_btn).setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancel_btn:
                wxLogin();
                break;
            case R.id.start_btn:
                startActivity(new Intent(this,BindLoginActivity.class).putExtra(WX_CODE,wx_code));
                break;
        }
    }

    private void wxLogin(){
        showWaitDialog();
        UserManagerController.weiXinRegister(this, wx_code, new Listener<Boolean, ArrayList<UserInfo>>() {
            @Override
            public void onCallBack(Boolean aBoolean, final ArrayList<UserInfo> reply) {
                if(aBoolean){
                    final UserInfo userInfo = reply.get(0);
                    utils.setLoginType(1);
                    utils.setNeedLogin(false);
                    new Thread(new Runnable() {
                        public void run() {
                            EMClient.getInstance().logout(true);
                            QLConstant.client_id = userInfo.getWp_member_info_id();
                            QLConstant.user_picture_url = userInfo.getPicture_url();
                            JPushInterface.setAlias(WXBindPhoneActivity.this, userInfo.getWp_member_info_id(), null);  //极光推送设置别名
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
                                            intent.setClass( WXBindPhoneActivity.this , HomeActivity.class);
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
                    if (reply !=null){

                    }
                }
            }
        });
    }
}
