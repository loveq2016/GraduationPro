package app.logic.activity.launch;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;

import org.QLConstant;
import org.ql.activity.customtitle.ActActivity;
import org.ql.utils.QLToastUtils;

import app.config.DemoApplication;
import app.logic.activity.live.CarouselImgInfo;
import app.logic.activity.live.LiveBaseActivity;
import app.logic.activity.main.HomeActivity;
import app.logic.activity.user.LoginActivity;
import app.logic.adapter.LaunchPagerAdapter;
import app.logic.controller.OrganizationController;
import app.logic.controller.UserManagerController;
import app.logic.pojo.UserInfo;
import app.utils.common.Listener;
import app.utils.helpers.SharepreferencesUtils;
import app.utils.network.YYNetworkConnectUtils;
import app.yy.geju.R;
import cn.jpush.android.api.JPushInterface;

/*
 * GZYY    2016-12-5  下午5:23:47
 * author: zsz
 */

public class LaunchActivity extends ActActivity {  //ActActivity
    private static final int LOGIN_SUECCESS = 000;
    private static final int LOGIN_ERROR = 111;
    private ViewPager viewPager;
    private LaunchPagerAdapter pagerAdapter;
    private List<View> views = new ArrayList<View>();
    private View view01, view02, view03, view04;
    private ImageView pointView01, pointView02, pointView03, pointView04;
    private List<ImageView> points = new ArrayList<ImageView>();
    private LinearLayout points_ll;
    private Button sign_in_btn;
    private LinearLayout btnBG_ll;
    private LayoutInflater inflater;
    private Resources resources;
    private View ll_loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        setContentView(R.layout.activity_launch);
        initView();
    }

    /**
     * 初始化View
     */
    private void initView() {
        SharepreferencesUtils utils = new SharepreferencesUtils(this);
        //**************2017.3.16 YSF 需要点击登录按钮 *******************//
        if( utils.getNeedLogin() ){   // 上次点击退出登录的情况下 跳到登录界面
            Intent intent = new Intent(LaunchActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        //****************************************************************//
        ll_loading = findViewById(R.id.ll_loading);
        if (!TextUtils.isEmpty(utils.getUserName()) && !TextUtils.isEmpty(utils.getPassword())) {
            ll_loading.setVisibility(View.VISIBLE);
            autoLogin(utils);
            return;
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

        view01.findViewById(R.id.launch_iv).setBackgroundDrawable(resources.getDrawable(R.drawable.newlaunch01));
        view02.findViewById(R.id.launch_iv).setBackgroundDrawable(resources.getDrawable(R.drawable.newlaunch02));
        view03.findViewById(R.id.launch_iv).setBackgroundDrawable(resources.getDrawable(R.drawable.newlaunch03));
        view04.findViewById(R.id.launch_iv).setBackgroundDrawable(resources.getDrawable(R.drawable.newlaunch04));

//        ((TextView) view01.findViewById(R.id.launch_tv01)).setText("协作从格局开始");
//        ((TextView) view01.findViewById(R.id.launch_tv02)).setText("发布需求、创建组织、管理部门成员");
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

        views.add(view01);
        views.add(view02);
        views.add(view03);
        views.add(view04);
        points.add(pointView01);
        points.add(pointView02);
        points.add(pointView03);
        points.add(pointView04);

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
                Intent intent = new Intent(LaunchActivity.this, LoginActivity.class);
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
                    String chartpsw = "wudi#" + userName;
                    new EMOptions().setAutoLogin(true);
                    EMClient.getInstance().login(userName, chartpsw, new EMCallBack() {
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
                    Intent intent = new Intent(LaunchActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
