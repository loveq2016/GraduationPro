package app.logic.activity.user;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.canson.view.VerificationButton.Verificationbutton;
import org.ql.activity.customtitle.ActActivity;
import org.ql.utils.QLStringUtils;
import org.ql.utils.QLToastUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import app.config.http.HttpConfig;
import app.logic.activity.ActTitleHandler;
import app.logic.controller.UserManagerController;
import app.utils.common.Listener;
import app.utils.helpers.SharepreferencesUtils;
import app.yy.geju.R;

public class ForgetpswActivity extends ActActivity {

    private Verificationbutton button;
    private String forget_phoneNum, forget_yzm, forget_newpsw, forget_ensure , imageCode , sid;
    private EditText forgetpw_phoneNum, forgetpw_yzm, forgetpw_newpsw, forgetpw_ensure , image_code_et;
    private int mtime = 0;// 120则为2分钟
    private SharepreferencesUtils utils;
    private Timer timer;
    public static final String TITLE_STRING = "TITLE_STRING";
    public static String FORGET_PSW = "forgetPsw";//忘記密碼標誌
    public static String AMEND_PSW = "amendPsw";//修改密碼標誌
    private ActTitleHandler titleHandler = new ActTitleHandler();

    private View rootView , empty_tv ;
    private ImageView imageCode_img ;
    private boolean isFirst = true ;
    private Bitmap bitmap = null ;

    // 2分钟之内输入则有效
    TimerTask task = new TimerTask() {
        public void run() {
            mtime++;
            if (mtime > 125) {
                timer.cancel();
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbsHandler(titleHandler);
        setContentView(R.layout.activity_fogpsw3);
        intiTootBar();
        utils = new SharepreferencesUtils(ForgetpswActivity.this);
        initview();
        button.onCreate(savedInstanceState);
        getImageCode(HttpConfig.IMAGE_CODE);
    }

    /**
     * 初始化TootBar
     */
    private void intiTootBar() {
        setTitle("");
        titleHandler.getRightLayout().setVisibility(View.GONE);
        titleHandler.getCenterLayout().setBackgroundResource(R.drawable.whit);
        titleHandler.addLeftView(LayoutInflater.from(this).inflate(R.layout.title_leftview_layout, null), true);
        ((ImageView) titleHandler.getLeftLayout().findViewById(R.id.left_iv)).setBackgroundResource(R.drawable.new_bule_back_icon);
        titleHandler.getLeftLayout().findViewById(R.id.left_tv).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        String title_str = getIntent().getStringExtra(ForgetpswActivity.FORGET_PSW);
        if (FORGET_PSW.equals(title_str)) {  //忘記密碼
            ((TextView) findViewById(R.id.titer_fag)).setText("忘记密码");
            ((TextView) titleHandler.getLeftLayout().findViewById(R.id.left_tv)).setText("忘记密码");
            ((TextView) titleHandler.getLeftLayout().findViewById(R.id.left_tv)).setTextColor(Color.parseColor("#99555555"));
        }
        if (AMEND_PSW.equals(title_str)) {  //重置密碼
            ((TextView) findViewById(R.id.titer_fag)).setText("重置密码");
            ((TextView) titleHandler.getLeftLayout().findViewById(R.id.left_tv)).setText("重置密码");
            ((TextView) titleHandler.getLeftLayout().findViewById(R.id.left_tv)).setTextColor(Color.parseColor("#99555555"));
        }
    }

    /**
     * 初始化View
     */
    private void initview() {

        rootView = findViewById( R.id.root_view);
        empty_tv  = findViewById( R.id.empty_tv);
        imageCode_img = (ImageView) findViewById( R.id.image_code_img);
        image_code_et = (EditText) findViewById( R.id.image_code_et);

        onClick onclick = new onClick();
        imageCode_img.setOnClickListener(onclick);

        forgetpw_phoneNum = (EditText) findViewById(R.id.forgetpw_phoneNum);
        forgetpw_yzm = (EditText) findViewById(R.id.forgetpw_yzm);
        forgetpw_newpsw = (EditText) findViewById(R.id.forgetpw_newpsw);
        forgetpw_ensure = (EditText) findViewById(R.id.forgetpw_ensure);
        findViewById(R.id.forgetpw_next).setOnClickListener(onclick);
        button = (Verificationbutton) findViewById(R.id.req_getcheckNum1);
        button.setOnClickListener(onclick);

        String title_str = getIntent().getStringExtra(ForgetpswActivity.FORGET_PSW);
        if (AMEND_PSW.equals(title_str)) {  //重置密碼
            forgetpw_phoneNum.setText(new SharepreferencesUtils(this).getUserName());
            forgetpw_phoneNum.setFocusableInTouchMode(true); //fasle
        }else
        //forgetpw_phoneNum.setFocusableInTouchMode(false);
            //forgetpw_phoneNum.setText(new SharepreferencesUtils(this).getUserName());
            forgetpw_phoneNum.setFocusableInTouchMode(true);
    }

    class onClick implements OnClickListener {
        public void onClick(View view) {
            forget_phoneNum = forgetpw_phoneNum.getText().toString();
            forget_yzm = forgetpw_yzm.getText().toString();
            forget_newpsw = forgetpw_newpsw.getText().toString();
            forget_ensure = forgetpw_ensure.getText().toString();
            imageCode = image_code_et.getText().toString();
            switch (view.getId()) {
                case R.id.forgetpw_next:
                    if (!forget_phoneNum.equals("") && !forget_yzm.equals("") && !forget_newpsw.equals("") && !forget_ensure.equals("")) {
                        if (forget_newpsw.equals(forget_ensure)) {
                            if (forget_newpsw.length() >= 6) {
                                if (mtime > 120) {
                                    Toast.makeText(ForgetpswActivity.this, "您的验证码已超时,请重新发送验证码", Toast.LENGTH_SHORT).show();
                                } else {
                                    // findViewById(R.id.loadingView).setVisibility(View.VISIBLE);
                                    showWaitDialog();
                                    UserManagerController.FotgetPsw(ForgetpswActivity.this, forget_phoneNum, forget_newpsw, forget_yzm, new Listener<Integer, String>() {

                                        @Override
                                        public void onCallBack(Integer status, String reply) {
                                            // findViewById(R.id.loadingView).setVisibility(View.GONE);
                                            dismissWaitDialog();
                                            if (status == 1) {
                                                utils.setUserName(forget_phoneNum);
                                                utils.setPassword("");
                                                utils.setRemenber(false);
                                                Toast.makeText(ForgetpswActivity.this, "修改密码成功", Toast.LENGTH_SHORT).show();
                                                ForgetpswActivity.this.finish();
                                            } else if (status == -1) {
                                                Toast.makeText(ForgetpswActivity.this, "修改密码失败", Toast.LENGTH_SHORT).show();
                                            } else if (status == -2) {
                                                Toast.makeText(ForgetpswActivity.this, "网络连接失败或服务器异常", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            } else {
                                Toast.makeText(ForgetpswActivity.this, "密码至少由6位数组成", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ForgetpswActivity.this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ForgetpswActivity.this, "手机号或验证码或密码不能为空", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.req_getcheckNum1:
                    if (!forget_phoneNum.equals("") && forget_phoneNum.length() == 11 && !"".equals(imageCode)) {///*QLStringUtils.isPhoneNumberValid(forget_phoneNum)*/
                        // 开启网络线程 获取手机验证码
                        UserManagerController.sendVerification(ForgetpswActivity.this, forget_phoneNum ,sid , imageCode , "1", new Listener<Integer, String>() {

                            @Override
                            public void onCallBack(Integer status, String reply) {
                                if (status > 0) {
                                    button.startTimer();
                                    Toast.makeText(ForgetpswActivity.this, "验证码发送成功", Toast.LENGTH_SHORT).show();
                                    // 开始计时
                                    mtime = 0;
                                    timer = new Timer(true);
                                    timer.schedule(task, 1000, 1000);
                                } else {
                                    Toast.makeText(ForgetpswActivity.this, reply , Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else if("".equals(forget_phoneNum)){
                        Toast.makeText(ForgetpswActivity.this, "请输入有效的手机号码", Toast.LENGTH_SHORT).show();
                    }else if("".equals(imageCode)){
                        Toast.makeText(ForgetpswActivity.this, "图形验证码不能为空", Toast.LENGTH_SHORT).show();

                    }
                    break;
                case R.id.image_code_img:
                    isFirst = false ; //不是第一次获取数据
                    getImageCode(HttpConfig.IMAGE_CODE);//获取图片验证码
                    break;
            }
        }
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
            sid = conn.getHeaderField("sid");
            System.out.println(sid + "--->" + sid);
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
                        rootView.setVisibility(View.VISIBLE); //按钮可用
                    }else{
                        if(isFirst){
                            rootView.setVisibility(View.GONE);
                            empty_tv.setVisibility(View.VISIBLE);
                        }else{
                            QLToastUtils.showToast( ForgetpswActivity.this , "图形验证码获取失败，请点击重新获取");
                        }
                    }
                    imageCode_img.setEnabled(true);
                }
            });
        }
    }
}
