package app.logic.activity.user;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import app.config.DemoApplication;
import app.logic.activity.ActTitleHandler;
import app.logic.activity.live.LiveBaseActivity;
import app.logic.activity.main.HomeActivity;
import app.logic.controller.UserManagerController;
import app.logic.pojo.ChatMessageInfo;
import app.logic.pojo.UserInfo;
import app.utils.common.Listener;
import app.utils.helpers.SharepreferencesUtils;
import app.yy.geju.R;
import cn.jpush.android.api.JPushInterface;

/**
 * SiuJiYung create at 2016-5-31 下午5:32:14
 */

public class BindLoginActivity extends ActActivity {
    private Button login;   //登录按钮
    private EditText phonenum, password;  //用户名和密码编辑框
    private String mphone, mpsw;          //用户名和密码
    private SharepreferencesUtils utils;  //本地保存对象
    private ImageView userName_delect_iv;
    private ImageView password_show_iv;
    private boolean showStatus;
    private String wx_code;

    ActTitleHandler titleHandler;

    private Handler mhanler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 000:
                    dismissWaitDialog();
                    Intent intent = new Intent(BindLoginActivity.this, HomeActivity.class);
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
                        QLToastUtils.showToast(BindLoginActivity.this, msg_str);
                    }
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titleHandler = new ActTitleHandler();
         setAbsHandler(titleHandler);
        setContentView(R.layout.activity_bind_login);
        // initActtitle(titleHandler);

        utils = new SharepreferencesUtils(BindLoginActivity.this);
        wx_code = getIntent().getStringExtra(WXBindPhoneActivity.WX_CODE);
        //初始化View
        initView();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }
    /**
     * 初始化View
     */
    private void initView() {
        setTitle("");
        titleHandler.addLeftView(LayoutInflater.from(this).inflate(R.layout.title_leftview_layout, null), true);
        titleHandler.getLeftLayout().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView) titleHandler.getLeftLayout().findViewById(R.id.left_tv)).setText("绑定登录");  //设置左标题
        phonenum = (EditText) findViewById(R.id.phonenum);
        password = (EditText) findViewById(R.id.password);
        userName_delect_iv = (ImageView) findViewById(R.id.userName_delect_iv);
        password_show_iv = (ImageView) findViewById(R.id.password_show_iv);
        userName_delect_iv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                phonenum.setText("");
                userName_delect_iv.setVisibility(View.INVISIBLE);
            }
        });
        password_show_iv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showStatus) {
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    showStatus = !showStatus;
                    password_show_iv.setImageResource(R.drawable.eye_icon_blue);
                    password.setSelection(password.getText().length());
                } else {
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    showStatus = !showStatus;
                    password_show_iv.setImageResource(R.drawable.eye_icon);
                    password.setSelection(password.getText().length());
                }
            }
        });
        //用户名编辑框的监听
        phonenum.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 0) {
                    userName_delect_iv.setVisibility(View.VISIBLE);
                } else {
                    userName_delect_iv.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        //密码编辑框的监听
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s.toString())) {
                    password_show_iv.setVisibility(View.VISIBLE);
                } else {
                    password_show_iv.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });
        //登录按钮
        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mphone = phonenum.getText().toString();
                mpsw = password.getText().toString();
                // 调用登录方法
                login(mphone, mpsw);
            }
        });
    }

    /**
     * 用户登录方法
     *
     * @param username
     * @param password
     */
    private void login(final String username, final String password) {
        showWaitDialog();
        if (!username.equals("") && !password.equals("")) {
            UserManagerController.weiXinBindAccount(BindLoginActivity.this,wx_code, username, password, new Listener<Boolean, UserInfo>() {
                @Override
                public void onCallBack(Boolean status, final UserInfo reply) {
                    if (status) {

                        // 登录成功
                        LiveBaseActivity.urseName = reply.getNickName() ;
                        new Thread(new Runnable() {
                            public void run() {
                                EMClient.getInstance().logout(true); //强制退出一次（萧总林总讨论后加上）
                                QLConstant.client_id = reply.getWp_member_info_id();
                                QLConstant.user_picture_url = reply.getPicture_url();
                                JPushInterface.setAlias(BindLoginActivity.this, reply.getWp_member_info_id(), null);  //极光推送设置别名
                                utils.setUserName(username); //本地保存用户名
                                utils.setPassword(password); //本地保存密码
                                utils.setLoginType(0);
                                // im聊天登录
//                                String chartpsw = "wudi#" + username;
                                String chartpsw = "wudi#" + reply.getWp_member_info_id();
                                new EMOptions().setAutoLogin(true);
                                EMClient.getInstance().login(reply.getWp_member_info_id()/*username*/, chartpsw, new EMCallBack() {// 回调
                                    @Override
                                    public void onSuccess() {
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                Log.v("hhhh", "im登陆成功");
                                                //从本地加载群组列表
                                                EMClient.getInstance().groupManager().getAllGroups();
                                                EMClient.getInstance().chatManager().loadAllConversations();
                                                String usr = EMClient.getInstance().getCurrentUser();
                                                Log.i("aaaa", EMClient.getInstance().getCurrentUser() );
                                                mhanler.sendEmptyMessage(000);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onProgress(int progress, String status) {
                                    }

                                    @Override
                                    public void onError(int code, String message) {
//                                Message msg = new Message();
//                                msg.what = 111;
//                                msg.obj = message;
//                                mhanler.sendMessage(msg);
//                                Log.v("hhhh", "登陆聊天服务器失败！");
                                        if (code == 200) {
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    Log.v("hhhh", "im登陆成功");
                                                    //从本地加载群组列表
                                                    EMClient.getInstance().groupManager().getAllGroups();
                                                    EMClient.getInstance().chatManager().loadAllConversations();
//												EMGroupManager.getInstance().loadAllGroups();
//												EMChatManager.getInstance().loadAllConversations();
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
                    } else {
                        dismissWaitDialog();
                        Toast.makeText(BindLoginActivity.this, "请检查账号或者密码是否输入正确！", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            dismissWaitDialog();
            Toast.makeText(BindLoginActivity.this, "用户名或者密码不能为空", Toast.LENGTH_SHORT).show();
        }
    }

}
