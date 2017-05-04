package app.logic.activity.launch;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;

import org.QLConstant;
import org.ql.activity.customtitle.ActActivity;
import org.ql.app.alert.AlertDialog;
import org.ql.utils.QLToastUtils;

import app.config.DemoApplication;
import app.config.http.HttpConfig;
import app.logic.activity.live.CarouselImgInfo;
import app.logic.activity.live.LiveBaseActivity;
import app.logic.activity.main.HomeActivity;
import app.logic.activity.user.LoginActivity;
import app.logic.activity.user.PrepareLoginActivity;
import app.logic.activity.user.QRCodePersonal;
import app.logic.activity.user.Welcome2Activity;
import app.logic.adapter.LaunchPagerAdapter;
import app.logic.controller.OrganizationController;
import app.logic.controller.UpdataController;
import app.logic.controller.UserManagerController;
import app.logic.pojo.UpdataAppInfo;
import app.logic.pojo.UserInfo;
import app.logic.singleton.ZSZSingleton;
import app.utils.common.FrescoImageShowThumb;
import app.utils.common.Listener;
import app.utils.download.thread.AppVersionDownloadThread;
import app.utils.helpers.SharepreferencesUtils;
import app.utils.helpers.SystemBuilderUtils;
import app.utils.network.YYNetworkConnectUtils;
import app.view.DialogNewStyleController;
import app.yy.geju.R;
import cn.jpush.android.api.JPushInterface;

/*
 * GZYY    2016-12-5  下午5:23:47
 * author: zsz
 */

public class LaunchActivity extends ActActivity {  //ActActivity
    public static final String DOWNLOAD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/YYData/download";
    private static final int LOGIN_SUECCESS = 000;
    private static final int LOGIN_ERROR = 111;
    private ViewPager viewPager;
    private LaunchPagerAdapter pagerAdapter;
    private List<View> views = new ArrayList<View>();
    private View view01, view02, view03, view04 , view05;
    private ImageView pointView01, pointView02, pointView03, pointView04,pointView05;
    private List<ImageView> points = new ArrayList<ImageView>();
    private LinearLayout points_ll;
    private Button sign_in_btn;
    private LinearLayout btnBG_ll;
    private LayoutInflater inflater;
    private Resources resources;
    private View ll_loading;

    private DialogNewStyleController appUpDataDialog ;
    private ProgressBar progressBar ;
    private Button sendBtn , cancel ;
    private View appUpDataView  , lineView ;
    private TextView tagEdt , titleTv ;
    private boolean isDownload = false ;  //下载状态标志
    private UpdataAppInfo appInfo ;

    /**
     * 初始化对话框
     */
    private void intiAppUpDataView(){
        appUpDataView = LayoutInflater.from(this).inflate(R.layout.app_updata_view, null);
        appUpDataDialog = new DialogNewStyleController(this, appUpDataView);
        progressBar = (ProgressBar) appUpDataView.findViewById( R.id.progressbar);
        titleTv = (TextView) appUpDataView.findViewById(R.id.dialog_title_tv);
        tagEdt = (TextView) appUpDataView.findViewById(R.id.dialog_tag_edt);
        sendBtn = (Button) appUpDataView.findViewById(R.id.dialog_cancel_btn);
        cancel = (Button) appUpDataView.findViewById(R.id.dialog_true_btn);
        lineView = appUpDataView.findViewById( R.id.centerIv );
        titleTv.setText("版本更新");
        sendBtn.setText("更新");
        cancel.setText("取消");
        appUpDataDialog.setCanceledOnTouchOutside(false);  //点击对话框外面不消失
        sendBtn.setOnClickListener(new OnClickListener() {  //更新按钮
            @Override
            public void onClick(View v) {
                if(isDownload){
                    QLToastUtils.showToast( LaunchActivity.this , "正在下载");
                    return;
                }
                appUpDataDialog.setCancelable(false);  //下载过程中 ，屏蔽back建，按back对话框不消失
                isDownload = true ;  //下载状态
                checkLocationFile(appInfo);
                //appUpDataDialog.dismiss();
                //不要让对话框消失，防止用户更新了应用，但不安装的情况
            }
        });
        cancel.setOnClickListener(new OnClickListener() {  //取消按钮
            @Override
            public void onClick(View v) {
                appUpDataDialog.dismiss();
                initView();
//                if(appInfo.getApp_update_force()==0){   // 不强制更新
//
//                }else{                                  // 强制更新才可以使用
//                    finish();  //还没有登录，所以不需要退出登录  退出应用程序
//                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        setContentView(R.layout.activity_launch);
        updataApp() ;//添加更新完时的监听器
        intiAppUpDataView(); //初始化对话框
        checkUpdataApp(); //检测是否有新版本？
    }

    /**
     * 初始化View
     */
    private void initView() {

        final SharepreferencesUtils utils = new SharepreferencesUtils(this);
        //**************2017.3.16 YSF 需要点击登录按钮 *******************//
        if( utils.getNeedLogin() ){   // 上次点击退出登录的情况下 跳到登录界面

//            Intent intent = new Intent(LaunchActivity.this, LoginActivity.class);
            Intent intent = new Intent(LaunchActivity.this, PrepareLoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        //****************************************************************//
        ll_loading = findViewById(R.id.ll_loading);
        if (utils.getLoginType() == 1){
            UserInfo userInfo1 = UserManagerController.getCurrUserInfo();
            if (userInfo1 !=null && userInfo1.getWp_member_info_id()!=null && utils.getToken() !=null){
                UserManagerController.checkToken(this, userInfo1.getWp_member_info_id(), utils.getToken(), new Listener<Boolean, String>() {
                    @Override
                    public void onCallBack(Boolean aBoolean, String reply) {
                        if (!aBoolean){
//                            Intent intent = new Intent(LaunchActivity.this, LoginActivity.class);
                            Intent intent = new Intent(LaunchActivity.this, PrepareLoginActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            UserInfo userInfo = UserManagerController.getCurrUserInfo();
                            LiveBaseActivity.urseName = userInfo.getNickName() ;
                            QLConstant.client_id = userInfo.getWp_member_info_id();
                            QLConstant.token = utils.getToken();
                            QLConstant.user_picture_url = userInfo.getPicture_url();
                            JPushInterface.setAlias(LaunchActivity.this, userInfo.getWp_member_info_id(), null);  //极光推送设置别名
                            String chartpsw = "wudi#" + userInfo.getWp_member_info_id();
                            new EMOptions().setAutoLogin(true);
                            EMClient.getInstance().login(userInfo.getWp_member_info_id()/*userName*/, chartpsw, new EMCallBack() {
                                @Override
                                public void onSuccess() {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Log.v("hhhh", "im登陆成功");
                                            //从本地加载群组列表
                                            EMClient.getInstance().groupManager().getAllGroups();
                                            EMClient.getInstance().chatManager().loadAllConversations();
                                            handler.sendEmptyMessage(LOGIN_SUECCESS);
                                        }
                                    });
                                }

                                @Override
                                public void onError(int code, String message) {
                                    if (code == 200) {
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                Log.v("hhhh", "im登陆成功");
                                                //从本地加载群组列表
                                                EMClient.getInstance().groupManager().getAllGroups();
                                                EMClient.getInstance().chatManager().loadAllConversations();
                                                handler.sendEmptyMessage(000);
                                            }
                                        });
                                    } else {
                                        Message msg = new Message();
                                        msg.what = LOGIN_ERROR;
                                        msg.obj = message;
                                        handler.sendMessage(msg);
                                        Log.v("hhhh", "登陆聊天服务器失败！");
                                    }
                                }
                                @Override
                                public void onProgress(int i, String s) {
                                }
                            });
                        }
                    }
                });
                return;
            }
        }else{
            if (!TextUtils.isEmpty(utils.getUserName()) && !TextUtils.isEmpty(utils.getPassword())) {
                ll_loading.setVisibility(View.VISIBLE);
                autoLogin(utils);
                return;
            }
        }

        ll_loading.setVisibility(View.GONE);

        inflater = LayoutInflater.from(this);
        resources = getResources();
        sign_in_btn = (Button) findViewById(R.id.launch_btn);
        points_ll = (LinearLayout) findViewById(R.id.points_ll);

        viewPager = (ViewPager) findViewById(R.id.launch_vp);
        btnBG_ll = (LinearLayout) findViewById(R.id.launch_btn_bg);

        view01 = inflater.inflate(R.layout.layout_launch_activity, null);
        view02 = inflater.inflate(R.layout.layout_launch_activity, null);
        view03 = inflater.inflate(R.layout.layout_launch_activity, null);
        view04 = inflater.inflate(R.layout.layout_launch_activity, null);
        view05 = inflater.inflate(R.layout.layout_launch_activity, null);

        view01.findViewById(R.id.launch_iv).setBackgroundDrawable(resources.getDrawable(R.drawable.newlaunch001));
        view02.findViewById(R.id.launch_iv).setBackgroundDrawable(resources.getDrawable(R.drawable.newlaunch002));
        view03.findViewById(R.id.launch_iv).setBackgroundDrawable(resources.getDrawable(R.drawable.newlaunch003));
        view04.findViewById(R.id.launch_iv).setBackgroundDrawable(resources.getDrawable(R.drawable.newlaunch004));
        view05.findViewById(R.id.launch_iv).setBackgroundDrawable(resources.getDrawable(R.drawable.newlaunch005));

//        ((TextView) view01.findViewById(R.id.launch_tv01)).setText("协作从格局开始");
//        ((TextView) view01.findViewById(R.id.launch_tv02)).setText("发布需求、创建组织、管理分组成员");
//        ((TextView) view01.findViewById(R.id.launch_tv03)).setText("一步到位");
//
//        ((TextView) view02.findViewById(R.id.launch_tv01)).setText("创建自己的工作圈");
//        ((TextView) view02.findViewById(R.id.launch_tv02)).setText("组织成员、客户伙伴各种人脉快速找到");
//        ((TextView) view02.findViewById(R.id.launch_tv03)).setText("协作关系更加轻松");
//
//        ((TextView) view03.findViewById(R.id.launch_tv01)).setText("圈内公告");
//        ((TextView) view03.findViewById(R.id.launch_tv02)).setText("随时随地查看最新动态，收纳繁琐消息");
//        ((TextView) view03.findViewById(R.id.launch_tv03)).setText("不易遗漏错过");
//
//        ((TextView) view04.findViewById(R.id.launch_tv01)).setText("组织管家");
//        ((TextView) view04.findViewById(R.id.launch_tv02)).setText("一目了然，随我掌控");
//        ((TextView) view04.findViewById(R.id.launch_tv03)).setText("开启智能化管理");

        pointView01 = (ImageView) findViewById(R.id.point_01);
        pointView02 = (ImageView) findViewById(R.id.point_02);
        pointView03 = (ImageView) findViewById(R.id.point_03);
        pointView04 = (ImageView) findViewById(R.id.point_04);
        pointView05 = (ImageView) findViewById(R.id.point_05);
        views.add(view01);
        views.add(view02);
        views.add(view03);
        views.add(view04);
        views.add(view05);
        points.add(pointView01);
        points.add(pointView02);
        points.add(pointView03);
        points.add(pointView04);
        points.add(pointView05);
        pagerAdapter = new LaunchPagerAdapter(this, views);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(0);
        changePoint(0);
        initData();
    }


    /**
     * 初始化数据监听
     */
    private void initData() {
        viewPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                changePoint(arg0);
            }
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub
            }
            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub
            }
        });
        sign_in_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(LaunchActivity.this, LoginActivity.class);
                Intent intent = new Intent(LaunchActivity.this, PrepareLoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void changePoint(int position) {
        for (int i = 0; i < points.size(); i++) {
            if (position == i) {
                points.get(i).setImageResource(R.drawable.point_blue);
            } else {
                points.get(i).setImageResource(R.drawable.point_gray);
            }
            if (position == points.size() - 1) {
                points_ll.setVisibility(View.INVISIBLE);
                btnBG_ll.setVisibility(View.VISIBLE);
            } else {
                btnBG_ll.setVisibility(View.GONE);
                points_ll.setVisibility(View.VISIBLE);
            }
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            dismissWaitDialog();
            switch (msg.what) {
                case LOGIN_SUECCESS:
                    Intent intent = new Intent(LaunchActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                    break;
                case LOGIN_ERROR:
                    String msg_str = (String) msg.obj;
                    if (msg_str != null) {
                        QLToastUtils.showToast(LaunchActivity.this, msg_str);
                    }
                    break;
            }
        }
    };

    /**
     * 自动登录
     *
     * @param utils
     */
    private void autoLogin(final SharepreferencesUtils utils) {

        final String userName = utils.getUserName();
        final String password = utils.getPassword();

        if (!YYNetworkConnectUtils.isNetworkConnected(this)) {
            QLToastUtils.showToast(this, "网络连接失败,请检查网络的连接");
            return;
        }

        UserManagerController.Login(this, userName, password, new Listener<Integer, UserInfo>() {
            @Override
            public void onCallBack(Integer integer, UserInfo reply) {
                if (integer == 1) {
                    LiveBaseActivity.urseName = reply.getNickName() ;
                    QLConstant.client_id = reply.getWp_member_info_id();
                    QLConstant.user_picture_url = reply.getPicture_url();
                    JPushInterface.setAlias(LaunchActivity.this, reply.getWp_member_info_id(), null);  //极光推送设置别名
//                    String chartpsw = "wudi#" + userName;
                    String chartpsw = "wudi#" + reply.getWp_member_info_id();
                    new EMOptions().setAutoLogin(true);
                    EMClient.getInstance().login(reply.getWp_member_info_id()/*userName*/, chartpsw, new EMCallBack() {
                        @Override
                        public void onSuccess() {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Log.v("hhhh", "im登陆成功");
                                    //从本地加载群组列表
                                    EMClient.getInstance().groupManager().getAllGroups();
                                    EMClient.getInstance().chatManager().loadAllConversations();
                                    handler.sendEmptyMessage(LOGIN_SUECCESS);
                                }
                            });
                        }

                        @Override
                        public void onError(int code, String message) {
                            if (code == 200) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Log.v("hhhh", "im登陆成功");
                                        //从本地加载群组列表
                                        EMClient.getInstance().groupManager().getAllGroups();
                                        EMClient.getInstance().chatManager().loadAllConversations();
                                        handler.sendEmptyMessage(000);
                                    }
                                });
                            } else {
                                Message msg = new Message();
                                msg.what = LOGIN_ERROR;
                                msg.obj = message;
                                handler.sendMessage(msg);
                                Log.v("hhhh", "登陆聊天服务器失败！");
                            }
                        }
                        @Override
                        public void onProgress(int i, String s) {
                        }
                    });
                }else{
//                    Intent intent = new Intent(LaunchActivity.this, LoginActivity.class);
                    Intent intent = new Intent(LaunchActivity.this, PrepareLoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    /*
	 * 检查版本更新
	 */
    private void checkUpdataApp() {
        // 开始检查网络版本
        UpdataController.getAppVersion(this, new Listener<Void, List<UpdataAppInfo>>() {
            @Override
            public void onCallBack(Void status, List<UpdataAppInfo> reply) {
                String versionName = SystemBuilderUtils.getInstance().getAppVersionName(LaunchActivity.this);//当前应用的版本名称
                if (reply == null || reply.size() < 1 || null == versionName || TextUtils.isEmpty(versionName) ) {
                    initView(); //接口没有数据返回，或没有读取到的数据，用户还是可以用的。
                    return;
                }
                appInfo = reply.get(0);
                String newversionName = appInfo.getApp_version(); //获取检测到的版本名称
                versionName = versionName.replace(".","0");       //将 “.” 换成 “0” （ 以后有三个 . 这个也适合判断 ）
                newversionName = newversionName.replace(".","0");
                QLConstant.newVisionName = newversionName;
                if(Long.parseLong(newversionName) > Long.parseLong(versionName)){  //有新版本
                    showUpdataApp( SystemBuilderUtils.getInstance().getAppVersionName(LaunchActivity.this) ,appInfo );   //显示更新的对话框
                }else{
                    initView();                            //没有更新则跳过
                }
            }
        });
    }

    /**
     * 显示更新的对话框
     * @param oldVersionCode
     * @param info
     */
    private void showUpdataApp(String oldVersionCode, final UpdataAppInfo info) {
        tagEdt.setText("当前版本为" + oldVersionCode + ",检测到的最新版本为" + String.valueOf(info.getApp_version()) + ",是否要更新？？");
        if(info.getApp_update_force()!=0){
            lineView.setVisibility(View.GONE);
            cancel.setVisibility(View.GONE);
        }else{
            lineView.setVisibility(View.VISIBLE);
            cancel.setVisibility(View.VISIBLE);
        }
        appUpDataDialog.show();
    }

    // 检查本地文件
    private void checkLocationFile(final UpdataAppInfo info) {
        // 创建目录下载目录
        File dir = new File(DOWNLOAD_PATH);
        if (!dir.exists()) {
            dir.mkdir();
        }
        // 检查本地文件是否存在
        final File appFile = new File(DOWNLOAD_PATH + "/" + info.getApp_name() + ".apk");
        if (appFile.exists()) {  //目录存在
            appFile.delete();    //删除愿来的文件
        }
        updataDownloadProgress(); //添加监听下载进度监听器
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                AppVersionDownloadThread downloadThread = new AppVersionDownloadThread(LaunchActivity.this, info);
                downloadThread.start(); //开启下载工作线程
            }
        });
    }

    /**
     * 通知消息下载进度
     */
    private void updataDownloadProgress() {
        progressBar.setVisibility( View.VISIBLE); //进度条显示
        final NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("格局新版本下载").setContentText("下载进度").setSmallIcon(R.drawable.ic_launcher);
        ZSZSingleton.getZSZSingleton().setUpdataDownloadProgressListener(new ZSZSingleton.UpdataDownloadProgressListener() { //添加监听下载进度的监听器
            @Override
            public void onCallBack(final int plan) {
                if (plan < 100) {
                    LaunchActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress( plan );
                        }
                    });
                    builder.setProgress(100, plan, false);
                    builder.setAutoCancel(true);
                    manager.notify(100, builder.build());
                } else {
                    LaunchActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //progressBar.setVisibility( View.GONE );
                            appUpDataDialog.setCancelable(true);  //屏蔽back建
                            isDownload = false ;  //下载完成
                        }
                    });
                    builder.setContentText("下载完成").setProgress(0, 0, true);
                    manager.notify(100, builder.build());
                    manager.cancel(100);
                }
            }
        });
    }

    /**
     * 回调最新app下载完成后
     */
    private void updataApp() {
        ZSZSingleton.getZSZSingleton().setStatusDownloadFileCompleteListener(new ZSZSingleton.StatusDownloadFileCompleteListener() { //添加APP更新完的监听器
            @Override
            public void onCallBack(String url) {
                if (url == null || TextUtils.isEmpty(url)) {
                    return;
                }
                if (ZSZSingleton.getZSZSingleton().getHaveComplete() > 0) {//作用是什么？
                    return;
                }
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(new File(url)), "application/vnd.android.package-archive");
                startActivity(intent);
                //按照现在的逻辑，这里就不能在设为1了
                //ZSZSingleton.getZSZSingleton().setHaveComplete(1);
            }
        });
    }


    @Override
    public void onBackPressed() {

        if(null!=appUpDataDialog && appUpDataDialog.isShowing()){  //对话框为显示状态，就屏蔽回退建
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if( null != appUpDataDialog && appUpDataDialog.isShowing()){
            appUpDataDialog.dismiss();
        }
    }
}
