package app.logic.activity.user;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import org.QLConstant;
import org.canson.view.VerificationButton.Verificationbutton;
import org.ql.activity.customtitle.ActActivity;
import org.ql.utils.QLToastUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import app.config.http.HttpConfig;
import app.logic.activity.ActTitleHandler;
import app.logic.controller.UserManagerController;
import app.logic.pojo.UserInfo;
import app.utils.common.Listener;
import app.utils.helpers.SharepreferencesUtils;
import app.utils.helpers.YYUtils;
import app.yy.geju.R;
import cn.jpush.android.api.JPushInterface;

public class BindingPhoneActivity extends ActActivity {

    public static final String CODE = "CODE0";
    private ActTitleHandler titleHandler = new ActTitleHandler();
    private Button req_button;
    private Verificationbutton button;
    private EditText req_phoneNum, req_loginpsw, req_checkNum, req_checkpassword , image_code_et;
    private CheckBox checkbox;
    private TextView reg_userbook;
    private String phoneNum, checkNum , imageCode , sid ;
    private int mtime = 0;// 120则为2分钟
    private Timer timer;
    private View rootView , empty_tv ;
    private ImageView imageCode_img ;
    private boolean isFirst = true ;
    private  Bitmap bitmap = null ;
    private String wx_code;
    private SharepreferencesUtils utils ;
    // 2分钟之内输入则有效
    TimerTask task = new TimerTask() {
        public void run() {
            mtime++;
            if (mtime > 125) {
                timer.cancel();
            }

            if (mhanler!=null) {
                mhanler.removeCallbacksAndMessages(null);
                mhanler.sendEmptyMessage(222);
            }
        }
    };
    private Handler mhanler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 000:
                    Toast.makeText(BindingPhoneActivity.this, "注册成功,请重新登录", Toast.LENGTH_LONG).show();
                    // 跳转到完善个人信息页面
                    Intent intent = new Intent(BindingPhoneActivity.this, LoginActivity.class);
                    // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    // Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    // intent.putExtra(UserInfoActivity.kFROM_REGEDIT, true);
                    startActivity(intent);
                    BindingPhoneActivity.this.finish();
                    break;
                case 111:
                    View view = findViewById(R.id.loadingView);
                    if( view != null ){
                        view.setVisibility(View.GONE);
                    }
                    break;
                case 222:
                    if (button !=null){
                        if(button.getText().toString().equals("1秒")){
                            imageCode_img.performClick();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbsHandler(titleHandler);
        setContentView(R.layout.activity_binding);
        utils = new SharepreferencesUtils( this ) ;
        titleHandler.addLeftView(LayoutInflater.from(this).inflate(R.layout.title_leftview_layout, null), true);
        titleHandler.getCenterLayout().setBackgroundResource(R.drawable.whit);
        titleHandler.getLeftLayout().findViewById(R.id.left_iv).setBackgroundResource(R.drawable.new_bule_back_icon);
        titleHandler.getLeftLayout().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleHandler.getRightDefButton().setOnClickListener(null);
        setTitle("");
        ((TextView) titleHandler.getLeftLayout().findViewById(R.id.left_tv)).setText("绑定手机");
        ((TextView) titleHandler.getLeftLayout().findViewById(R.id.left_tv)).setTextColor(Color.parseColor("#99555555"));
        initView();
        button.onCreate(savedInstanceState);
        getImageCode(HttpConfig.IMAGE_CODE);//获取图片验证码
    }

    /**
     * 出是化View
     */
    private void initView() {
        req_phoneNum = (EditText) findViewById(R.id.req_phoneNum);
        req_loginpsw = (EditText) findViewById(R.id.req_loginpsw);
        req_checkNum = (EditText) findViewById(R.id.req_checkNum);
        req_checkpassword = (EditText) findViewById(R.id.req_checkpassword);
        checkbox = (CheckBox) findViewById(R.id.checkBox1);
        reg_userbook = (TextView) findViewById(R.id.reg_userbook);
        onClick onclick = new onClick();
        req_button = (Button) findViewById(R.id.req_button);
        req_button.setOnClickListener(onclick);
        button = (Verificationbutton) findViewById(R.id.req_getcheckNum);
        button.setOnClickListener(onclick);
        rootView = findViewById( R.id.root_view);
        empty_tv = findViewById(R.id.empty_tv);
        image_code_et = (EditText) findViewById( R.id.image_code_et);
        imageCode_img = (ImageView) findViewById( R.id.image_code_img);
        imageCode_img.setOnClickListener(onclick);
    }

    /**
     * View 的监听器
     */
    class onClick implements OnClickListener {
        @Override
        public void onClick(View view) {
            phoneNum = req_phoneNum.getText().toString();
            checkNum = req_checkNum.getText().toString();
            imageCode = image_code_et.getText().toString() ;
            switch (view.getId()) {
                case R.id.req_button:
                    if (!phoneNum.equals("") && !checkNum.equals("") && phoneNum.length() == 11) {
                        if (mtime > 120) {
                            imageCode_img.performClick();
                            Toast.makeText(BindingPhoneActivity.this, "您的验证码已超时,请重新发送验证码", Toast.LENGTH_LONG).show();
                        } else {
                            checkWork();
                        }
                    } else if("".equals(phoneNum) || phoneNum.length() != 11){
                        Toast.makeText(BindingPhoneActivity.this, "请输入有效的手机号码", Toast.LENGTH_LONG).show();
                    }else if("".equals(checkNum)){
                        Toast.makeText(BindingPhoneActivity.this, "验证码不能为空", Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.req_getcheckNum:
                    if (!phoneNum.equals("") && phoneNum.length() == 11 && !"".equals(imageCode)) {
                        // 开启网络线程 获取手机验证码
                        UserManagerController.sendVerification(BindingPhoneActivity.this, phoneNum, sid , imageCode ,"2", new Listener<Integer, String>() {
                            @Override
                            public void onCallBack(Integer status, String reply) {
                                if (status > 0) {
                                    button.startTimer();
                                    Toast.makeText(BindingPhoneActivity.this, "验证码发送成功", Toast.LENGTH_LONG).show();
                                    // 开始计时
                                    mtime = 0;
                                    timer = new Timer(true);
                                    timer.schedule(task, 1000, 1000);
                                } else if (reply != null) {
                                    imageCode_img.performClick();
                                    Toast.makeText(BindingPhoneActivity.this, reply, Toast.LENGTH_LONG).show();
                                } else {
                                    imageCode_img.performClick();
                                    Toast.makeText(BindingPhoneActivity.this, "验证码发送失败", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    } else if("".equals(phoneNum) || phoneNum.length()!=11 ){
                        Toast.makeText(BindingPhoneActivity.this, "请输入有效的手机号码", Toast.LENGTH_LONG).show();
                    }else if("".equals(imageCode)){
                        Toast.makeText(BindingPhoneActivity.this, "图形验证码不能为空", Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.image_code_img://刷新验证码
                    isFirst = false ; //不是第一次获取数据
                    getImageCode(HttpConfig.IMAGE_CODE);//获取图片验证码
                    break;
                case R.id.reg_userbook:
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        button.onDestroy();
        super.onDestroy();
    }

    /**
     * 检查环境
     */
    private void checkWork() {
        String phoneString = req_phoneNum.getText().toString();
        String loginpswString = req_loginpsw.getText().toString();
        String checkpswString = req_checkpassword.getText().toString();

        if (loginpswString != null && checkpswString != null && !loginpswString.equals("") && !checkpswString.equals("")) {
            if (loginpswString.length() < 6) {
                QLToastUtils.showToast(this, "登录密码不能小于6位数");
                return;
            }
            if (YYUtils.isContainChinese(loginpswString)) {
                QLToastUtils.showToast(this, "登录密码不能为中文");
                return;
            }
            if (loginpswString.equals(checkpswString)) {
                loginAndInit(checkpswString);
            } else {
                QLToastUtils.showToast(BindingPhoneActivity.this, "密码不一致");
            }
        } else {
            QLToastUtils.showToast(BindingPhoneActivity.this, "请检查两次密码是否输入");
        }
    }

    /**
     * 执行登录并初始化工作
     */
    private void loginAndInit(String pw) {
        showWaitDialog();
        String code = req_checkNum.getText().toString();
        UserManagerController.buiding(BindingPhoneActivity.this, phoneNum , code,pw, new Listener<Boolean, String>() {
            @Override
            public void onCallBack(Boolean aBoolean, String reply) {
                dismissWaitDialog();
               QLToastUtils.showToast( BindingPhoneActivity.this ,reply);
                if (aBoolean)
                    finish();
            }
        });
    }

    private void createIMAccount(final String phoneNum) {
        final String psw = "wudi#" + phoneNum;
        new Thread(new Runnable() {
            @Override
            public void run() {
                mhanler.sendEmptyMessage(000);
                // im聊天登录
                EMClient.getInstance().login(phoneNum, psw, new EMCallBack() {// 回调
                    @Override
                    public void onSuccess() {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Log.v("hhhh", "im登陆成功");
                                //EMGroupManager.getInstance().loadAllGroups();
                                //EMChatManager.getInstance().loadAllConversations();
                                EMClient.getInstance().groupManager().getAllGroups();
                                EMClient.getInstance().chatManager().loadAllConversations();
//                                mhanler.sendEmptyMessage(000);
                            }
                        });
                    }

                    @Override
                    public void onProgress(int progress, String status) {
                    }

                    @Override
                    public void onError(int code, String message) {
                        mhanler.sendEmptyMessage(111);
                        Log.v("hhhh", "登陆聊天服务器失败！");
                    }
                });
            }
        }).start();
    }

    /**
     * 初始化工作线程
     * @param path
     */
   private void getImageCode(final String path){
       bitmap = null ;
       imageCode_img.setEnabled(false); //按钮不可用(防止多次点击)
      new Thread(new Runnable() {
           @Override
           public void run() {
               getImageFromNet(path);
           }
       }).start();
   }

    /**
     * 网络获取图片
     * @param
     */
    private void getImageFromNet(String path) {
        try {
            URL url = new URL( HttpConfig.getUrl( path ) );
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000); //链接超时时间
            conn.connect();
            sid = conn.getHeaderField("sessionid");
            System.out.println("sessionid  " + "--->" + sid);
            sid = conn.getHeaderField("sid");
            System.out.println( "sid  --->" + sid);
            Map<String, List<String>> map =conn.getHeaderFields();
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            int code = conn.getResponseCode();
            if ( code == HttpURLConnection.HTTP_OK ) {
                InputStream inputStream = conn.getInputStream();
                //把流解码成图片
                bitmap = BitmapFactory.decodeStream(inputStream);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(bitmap != null){
                        imageCode_img.setImageBitmap(bitmap);
                    }else{
                        QLToastUtils.showToast( BindingPhoneActivity.this , "图形验证码获取失败，请点击重新获取");
                    }
                    imageCode_img.setEnabled(true);
                }
            });
        }
    }
}
