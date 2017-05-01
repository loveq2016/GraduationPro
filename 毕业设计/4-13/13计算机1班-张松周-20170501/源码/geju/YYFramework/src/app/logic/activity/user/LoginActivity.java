package app.logic.activity.user;


import org.QLConstant;
import org.ql.activity.customtitle.ActActivity;
import org.ql.utils.QLToastUtils;
import org.w3c.dom.Text;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;

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

import app.config.DemoApplication;
import app.logic.activity.ActTitleHandler;
import app.logic.activity.live.LiveBaseActivity;
import app.logic.activity.main.HomeActivity;
import app.logic.controller.UserManagerController;
import app.utils.common.Listener;
import app.utils.helpers.SharepreferencesUtils;
import app.yy.geju.R;
import app.logic.pojo.ChatMessageInfo;
import app.logic.pojo.UserInfo;
import cn.jpush.android.api.JPushInterface;

/**
 * SiuJiYung create at 2016-5-31 下午5:32:14
 */

public class LoginActivity extends ActActivity {
    private Button login;   //登录按钮
    private TextView forgetPsw;  //忘记密码
    private TextView logon;      //注册
    private EditText phonenum, password;  //用户名和密码编辑框
    private String mphone, mpsw;          //用户名和密码
    private SharepreferencesUtils utils;  //本地保存对象
    private String fromActivity;
    private String tokenStatus;
    private String message;
    private ImageView userName_delect_iv;
    private ImageView password_show_iv;
    private boolean showStatus;

    private Handler mhanler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 000:
                    dismissWaitDialog();
//                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
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
                        QLToastUtils.showToast(LoginActivity.this, msg_str);
                    }
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActTitleHandler titleHandler = new ActTitleHandler();
        // setAbsHandler(titleHandler);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login_3);
        // initActtitle(titleHandler);

        utils = new SharepreferencesUtils(LoginActivity.this);
        fromActivity = getIntent().getStringExtra("ExitActivity");
        tokenStatus = getIntent().getStringExtra("token");
        message = getIntent().getStringExtra("message");
        //初始化View
        initView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        autoLogin(); //自动登录
    }


    private void autoLogin() {
        String musername = utils.getUserName(); //读取保存的用户名
        String mpassword = utils.getPassword(); //读取密码
        if (musername != null && !TextUtils.isEmpty(musername)) {
            phonenum.setText(musername);
        }
        if (mpassword != null && !TextUtils.isEmpty(mpassword)) {
            password.setText(mpassword);
        }
        if (musername != null && !TextUtils.isEmpty(musername) && mpassword != null && !TextUtils.isEmpty(mpassword)) {
            mphone = phonenum.getText().toString();
            mpsw = password.getText().toString();
            // 调用登录方法
            if (tokenStatus != null) {
                if (message != null) {
                    QLToastUtils.showToast(LoginActivity.this, message);
                }
                return;
            }

//            if(fromActivity == null ){
//                login(mphone, mpsw); //登录方法
//            }
            //************* 2017.3.16 YSF 原逻辑上 修改下*****************//
            if ( !utils.getNeedLogin() && fromActivity == null) {
                login(mphone, mpsw); //登录方法
            }
            //******************************//

        }
    }

    /**
     * 初始化View
     */
    private void initView() {

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
        //忘记密码
        forgetPsw = (TextView) findViewById(R.id.forgetPsw);
        forgetPsw.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgetpswActivity.class);
                intent.putExtra(ForgetpswActivity.FORGET_PSW, ForgetpswActivity.FORGET_PSW);
                startActivity(intent);
            }
        });
        //注册
        logon = (TextView) findViewById(R.id.logon);
        logon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, RegActivity.class);
                startActivity(intent);
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
            UserManagerController.Login(LoginActivity.this, username, password, new Listener<Integer, UserInfo>() {

                @Override
                public void onCallBack(Integer status, final UserInfo reply) {
                    if (status == 1) {
                        new Thread(new Runnable() {
                            public void run() {
                                EMClient.getInstance().logout(true); //强制退出一次（萧总林总讨论后加上）
                                // 登录成功
                                LiveBaseActivity.urseName = reply.getNickName() ;
                                QLConstant.client_id = reply.getWp_member_info_id();
                                QLConstant.user_picture_url = reply.getPicture_url();
                                JPushInterface.setAlias(LoginActivity.this, reply.getWp_member_info_id(), null);  //极光推送设置别名
                                utils.setUserName(username); //本地保存用户名
                                utils.setPassword(password); //本地保存密码
                                // im聊天登录


                                String chartpsw = "wudi#" + username;
                                new EMOptions().setAutoLogin(true);
                                EMClient.getInstance().login(username, chartpsw, new EMCallBack() {// 回调
                                    @Override
                                    public void onSuccess() {
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                Log.v("hhhh", "im登陆成功");
                                                //从本地加载群组列表
                                                EMClient.getInstance().groupManager().getAllGroups();
                                                EMClient.getInstance().chatManager().loadAllConversations();
//												EMGroupManager.getInstance().loadAllGroups();
//												EMChatManager.getInstance().loadAllConversations();
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
//                        // 登录成功
//                        LiveBaseActivity.urseName = reply.getNickName() ;
//                        QLConstant.client_id = reply.getWp_member_info_id();
//                        QLConstant.user_picture_url = reply.getPicture_url();
//                        JPushInterface.setAlias(LoginActivity.this, reply.getWp_member_info_id(), null);  //极光推送设置别名
//                        utils.setUserName(username); //本地保存用户名
//                        utils.setPassword(password); //本地保存密码
//                        // im聊天登录
//
//
//                        String chartpsw = "wudi#" + username;
//                        new EMOptions().setAutoLogin(true);
//                        EMClient.getInstance().login(username, chartpsw, new EMCallBack() {// 回调
//                            @Override
//                            public void onSuccess() {
//                                runOnUiThread(new Runnable() {
//                                    public void run() {
//                                        Log.v("hhhh", "im登陆成功");
//                                        //从本地加载群组列表
//                                        EMClient.getInstance().groupManager().getAllGroups();
//                                        EMClient.getInstance().chatManager().loadAllConversations();
////												EMGroupManager.getInstance().loadAllGroups();
////												EMChatManager.getInstance().loadAllConversations();
//                                        String usr = EMClient.getInstance().getCurrentUser();
//                                        Log.i("aaaa", EMClient.getInstance().getCurrentUser() );
//                                        mhanler.sendEmptyMessage(000);
//                                    }
//                                });
//                            }
//
//                            @Override
//                            public void onProgress(int progress, String status) {
//                            }
//
//                            @Override
//                            public void onError(int code, String message) {
////                                Message msg = new Message();
////                                msg.what = 111;
////                                msg.obj = message;
////                                mhanler.sendMessage(msg);
////                                Log.v("hhhh", "登陆聊天服务器失败！");
//                                if (code == 200) {
//                                    runOnUiThread(new Runnable() {
//                                        public void run() {
//                                            Log.v("hhhh", "im登陆成功");
//                                            //从本地加载群组列表
//                                            EMClient.getInstance().groupManager().getAllGroups();
//                                            EMClient.getInstance().chatManager().loadAllConversations();
////												EMGroupManager.getInstance().loadAllGroups();
////												EMChatManager.getInstance().loadAllConversations();
//                                            mhanler.sendEmptyMessage(000);
//                                        }
//                                    });
//                                } else {
//                                    Message msg = new Message();
//                                    msg.what = 111;
//                                    msg.obj = message;
//                                    mhanler.sendMessage(msg);
//                                    Log.v("hhhh", "登陆聊天服务器失败！");
//                                }
//                            }
//                        });
                    } else if (status == -1) {
                        dismissWaitDialog();
                        Toast.makeText(LoginActivity.this, "请检查账号或者密码是否输入正确！", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            dismissWaitDialog();
            Toast.makeText(LoginActivity.this, "用户名或者密码不能为空", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isCanBack() {
        return false;
    }

    /**
     * 退出应用程序代码写在这里
     */
    public void doFinish() {
    }
}
