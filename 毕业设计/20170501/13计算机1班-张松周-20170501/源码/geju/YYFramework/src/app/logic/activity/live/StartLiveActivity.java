package app.logic.activity.live;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.widget.EaseImageView;
import com.hyphenate.exceptions.HyphenateException;
import com.squareup.picasso.Picasso;
import com.ucloud.ulive.UAudioProfile;
import com.ucloud.ulive.UCameraProfile;
import com.ucloud.ulive.UCameraSessionListener;
import com.ucloud.ulive.UEasyStreaming;
import com.ucloud.ulive.UFilterProfile;
import com.ucloud.ulive.UNetworkListener;
import com.ucloud.ulive.USize;
import com.ucloud.ulive.UStreamStateListener;
import com.ucloud.ulive.UStreamingProfile;
import com.ucloud.ulive.UVideoProfile;
import com.ucloud.ulive.widget.UAspectFrameLayout;
import org.ql.utils.QLToastUtils;
import java.util.List;
import java.util.Random;
import app.config.http.HttpConfig;
import app.logic.activity.main.HomeActivity;
import app.logic.controller.LivestreamController;
import app.logic.controller.OrganizationController;
import app.logic.live.view.LiveView;
import app.logic.pojo.OrgRequestMemberInfo;
import app.utils.common.FrescoImageShowThumb;
import app.utils.common.Listener;
import app.view.DialogNewStyleController;
import app.yy.geju.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/2/17 0017.
 */

public class StartLiveActivity extends LiveBaseActivity  implements TextureView.SurfaceTextureListener, UCameraSessionListener,
        UStreamStateListener, UNetworkListener ,LiveView.RoomMessgeViewShow {
    public static final String ORG_ID ="ORGID";
    public static final String ORG_NAME="ORGNAEN";
    public static final String ORG_BUIDER_NAME ="ORGBUIDERNAME";
    public static final String ORG_LOGO_URL ="ORGLOGOURL";
    private static final String TAG = StartLiveActivity.class.getSimpleName();
    private  final int MESSGWAHT = 1001;
    private  final int PSOTLISTER = 1002;

    private boolean isRecording , isNeedRePreview ;          //加载标志
    private boolean isNeedInitStreamingEnv = true;          //流 初始化标志
    private boolean isFrontCameraOutputNeedFlip = false;    //是否需要前置摄像机
    boolean isDependActivityLifecycleWhenFirstTime = true;  //第一次依赖Activty的生命周期
    boolean isNeedContinueCaptureAfterBackToMainHome = false;
    protected String mRtmpAddress = "";    //流地址
    protected TextureView mTexturePreview;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.container)
    UAspectFrameLayout mPreviewContainer;
    @BindView(R.id.start_container)
    RelativeLayout startContainer;
    @BindView(R.id.countdown_txtv)
    TextView countdownView;    // 3 2 1
    @BindView(R.id.avatar_img)
    EaseImageView avatarImg ;  //组织头像
    @BindView(R.id.tv_username)
    TextView usernameView;     //主播名字
    @BindView(R.id.tv_orgname)
    TextView orgnameView;      //组织名字
    @BindView(R.id.btn_start)
    Button startBtn;           //开始直播按钮
//    @BindView(R.id.finish_frame)
//    ViewStub liveEndLayout;     //直播结果的view
    @BindView(R.id.cover_image)
    ImageView coverImage;       //背景图
    @BindView(R.id.img_bt_switch_light)
    ImageButton lightSwitch;    //闪光灯
    @BindView(R.id.img_bt_switch_voice)
    ImageButton voiceSwitch;    //麦克风
    @BindView(R.id.img_bt_close)
    ImageButton closeLive;           //关闭直播按钮
    @BindView(R.id.tutu)
    LinearLayout tutu ;
    @BindView(R.id.name)
    LinearLayout name ;

//    @BindView(R.id.live_close_confirm)
    Button closeConfirmBtn ;
//    @BindView(R.id.tv_username1)
    TextView usernameView1 ;
//    @BindView(R.id.watch_number)
    TextView watchTv ;
//    @BindView(R.id.tiem_long)
    TextView timeTv ;
//    @BindView(R.id.live_org_name)
    TextView orgnameTv ;
//    @BindView(R.id.org_logo_eiv)
    SimpleDraweeView imageView ;  //
//    @BindView(R.id.finish_frame)
//    ViewGroup finish_frame ;
    private LiveView liveBack;

    private boolean isOpend ;
    private int c = 1 , m = 3  ;
    protected UEasyStreaming mEasyStreaming;
    protected String rtmpPushStreamDomain = "rtmp://publish.krapnik.cn/ucloud/";
    public static final int MSG_UPDATE_COUNTDOWN = 1;
    public static final int COUNTDOWN_DELAY = 1000;
    public static final int COUNTDOWN_START_INDEX = 3;
    public static final int COUNTDOWN_END_INDEX = 1;
    protected boolean isShutDownCountdown = false;
    private UStreamingProfile mStreamingProfile;
    //是否开启直播的标志
    boolean isLive ,canBack = true , canClose = true ;
    private String orgId , orgName , buiderName , orglog ;
    private long startLiveTime , endLiveTime  ,end ;
    private SurfaceTexture tempTexture;
    private int tempStWidth, tempStHeight  ;
    private Button  trueBtn , cancelBtn; //关闭直播
    private DialogNewStyleController dialog ;
    private Dialog liveRseltDialog ;

    private int videoCodecType = UVideoProfile.CODEC_MODE_HARD;
    private int videoFilterType = UFilterProfile.FilterMode.GPU;
    private int videoCaptureOrientation = UVideoProfile.ORIENTATION_PORTRAIT;
    private int videoCaptureFps = 20;
    private int videoBitrate = UVideoProfile.VIDEO_BITRATE_NORMAL;
    private int currentCameraIndex = UCameraProfile.CAMERA_FACING_FRONT;
    private UVideoProfile.Resolution videoResolution = UVideoProfile.Resolution.RATIO_AUTO;
    private int networkBlockCount = 0;

    //203138620012364216
    @Override protected void onActivityCreate(@Nullable Bundle savedInstanceState) {
        setContentView( R.layout.activity_start_live ); //finish_frame
        ButterKnife.bind(this);
        liveBack = (LiveView) findViewById( R.id.liveback);
        liveBack.setRoomMessgeViewShow( this );    //上滑显示消息列表，下滑隐藏消息列表
        orgId = getIntent().getStringExtra(ORG_ID);
        //获取当前组织的所有成员
        getAssociationMemberList( orgId ) ;
        orgName = getIntent().getStringExtra(ORG_NAME) ;
        buiderName = getIntent().getStringExtra( ORG_BUIDER_NAME ) ;
        orglog = getIntent().getStringExtra( ORG_LOGO_URL);
        usernameView.setText( buiderName );
        orgnameView.setText( orgName );
        Picasso.with(this).load(HttpConfig.getUrl( orglog)).placeholder(R.drawable.default_user_icon).fit().centerCrop().into(avatarImg);
        //初始化配置（开启直播的流ID）
        liveId = System.currentTimeMillis() + "" ;
        //初始化配置
        initConfig() ;
        //初始对话框（关闭直播对话框）
        intiDialog();
        //直播结果对话框
        intiLiveResultDialog() ;

    }
    /**
     * 对话框初始化
     * @param
     */
    private void intiDialog( ) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.del_calendar_dialog_view, null);
        TextView title = (TextView) contentView.findViewById(R.id.dialog_title_tv);
        title.setText("确定要关闭直播吗？");
        trueBtn = (Button) contentView.findViewById(R.id.dialog_true_btn);
        cancelBtn = (Button) contentView.findViewById(R.id.dialog_cancel_btn);
        dialog = new DialogNewStyleController( this, contentView );
        //取消 关闭直播
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        //确定按钮
        trueBtn.setText("确定");
        trueBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( mEasyStreaming.isRecording()) mEasyStreaming.stopRecording();
                //finish_frame.setVisibility( View.VISIBLE);
                canClose = false ;
                canBack = false ;
                //获取关闭直播这一刻的时间戳
                endLiveTime = System.currentTimeMillis() ;
                //设置直播结果
                showConfirmCloseLayout();
                //关闭对话框
                dialog.dismiss();
                //隐藏消息栏 和 发消息栏
                messageView.setVisibility( View.GONE );
                //显示直播结果View
                liveRseltDialog.show();
                //关闭直播
                colseLiveStream( orgId ) ;
                //停止心跳
                stopPost();
            }
        });
    }

    /**
     * 初始化 直播结果对话框
     */
    private void intiLiveResultDialog(){
        View contentView = LayoutInflater.from(this).inflate(R.layout.live_layout_live_room_record_finish_pannel, null);
        closeConfirmBtn = (Button) contentView.findViewById( R.id.live_close_confirm);
        usernameView1 = (TextView) contentView.findViewById( R.id.tv_username1);
        watchTv = (TextView) contentView.findViewById( R.id.watch_number);
        timeTv = (TextView) contentView.findViewById( R.id.tiem_long);
        orgnameTv = (TextView) contentView.findViewById( R.id.live_org_name);
        imageView = (SimpleDraweeView) contentView.findViewById( R.id.org_logo_eiv);
        liveRseltDialog = new Dialog( this  ) ;
        liveRseltDialog.setContentView( contentView );

        Window dialogWindow = liveRseltDialog.getWindow();
        dialogWindow.setBackgroundDrawableResource( R.color.transparent);
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.97 ); //高度设置为屏幕的1
        p.width = (int) (d.getWidth() * 1 );   //宽度设置为屏幕的1
        dialogWindow.setAttributes(p);

        liveRseltDialog.setCancelable(false);
        liveRseltDialog.setOnKeyListener( new DialogInterface.OnKeyListener(){
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_SEARCH) {
                    return true;
                } else {
                    return false; //默认返回 false
                }
            }
        });

        //确定按钮
        closeConfirmBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeLive.setEnabled( true );
                startLiveTime = 0 ;
                endLiveTime = 0 ;
                watchNumber = 0 ;
                //finish_frame.setVisibility( View.GONE);
                liveRseltDialog.dismiss();
                finish();
            }
        });
    }

    /**
     * 初始化配置
     */
    private void initConfig() {
        mRtmpAddress = rtmpPushStreamDomain  + liveId ;
        videoFilterType = UFilterProfile.FilterMode.GPU ;
        videoCodecType = UVideoProfile.CODEC_MODE_HARD ;
        videoCaptureOrientation  = UVideoProfile.ORIENTATION_PORTRAIT ;
        videoCaptureFps = 20 ;
        videoBitrate = UVideoProfile.VIDEO_BITRATE_NORMAL;
        videoResolution = UVideoProfile.Resolution.valueOf( UVideoProfile.Resolution.RATIO_AUTO.ordinal() );
    }

    /**
     *初始化预览
     */
    private void initPreviewTextureView() {
        if (mTexturePreview == null) {
            mTexturePreview = new TextureView(this);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.CENTER;
            mPreviewContainer.removeAllViews();
            mPreviewContainer.addView(mTexturePreview);
            mTexturePreview.setKeepScreenOn(true);
            mTexturePreview.setSurfaceTextureListener(this);
        }
    }

    /**
     * 初始化流（配置设备）
     */
    public void initStreamingEnv() {
        mPreviewContainer.setShowMode(UAspectFrameLayout.Mode.FULL);
        mEasyStreaming = UEasyStreaming.Factory.newInstance();
        //视屏配置
        UVideoProfile videoProfile = new UVideoProfile().fps(videoCaptureFps)
                .bitrate(videoBitrate)
                .resolution(videoResolution)
                .codecMode(videoCodecType)
                .captureOrientation(videoCaptureOrientation);
        //音频配置
        UAudioProfile audioProfile = new UAudioProfile()
                .bitrate(UAudioProfile.AUDIO_BITRATE_NORMAL)
                .channels(UAudioProfile.CHANNEL_IN_STEREO)
                .source(UAudioProfile.AUDIO_SOURCE_MIC)
                .format(UAudioProfile.FORMAT_PCM_16BIT)
                .samplerate(UAudioProfile.SAMPLE_RATE_44100_HZ);
        //过滤器配置
        UFilterProfile filterProfile = new UFilterProfile().mode(videoFilterType);
        //相机配置
        UCameraProfile cameraProfile = new UCameraProfile().frontCameraFlip(isFrontCameraOutputNeedFlip).setCameraIndex(currentCameraIndex);
        mStreamingProfile = new UStreamingProfile.Builder()
                .setAudioProfile(audioProfile)
                .setVideoProfile(videoProfile)
                .setFilterProfile(filterProfile)
                .setCameraProfile(cameraProfile)
                .build();
        //设置在相机会话侦听器
        mEasyStreaming.setOnCameraSessionListener(this);
        //设置流状态变化监听器
        mEasyStreaming.setOnStreamStateListener(this);
        //设置网络变化监听器
        mEasyStreaming.setOnNetworkStateListener(this);
        //准备
        mEasyStreaming.prepare(mStreamingProfile);
        //预览
        initPreviewTextureView();
        //初始化标志
        isNeedInitStreamingEnv = false;
    }

    /**
     *Live
     */
    private void uliveOnResume() {
        if (mEasyStreaming != null) {
            mEasyStreaming.onResume();
        }
        if (isNeedInitStreamingEnv) {  //是否初始化的标志
            //初始化流
            initStreamingEnv();
        }
        if (isRecording) {  //一开始还没有加载（如果是加载过了就直接开始加载了）
            //设置流地址
            mStreamingProfile.setStreamUrl(mRtmpAddress);
            mEasyStreaming.startRecording();
        }
    }

    @Override protected void onResume() {
        super.onResume();
        HomeActivity.isStartLiveActivity = true ;
        //屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (isDependActivityLifecycleWhenFirstTime || !isNeedInitStreamingEnv) {
            uliveOnResume();
        }
        if (isMessageListInited) messageView.refresh();
        // 把此activity 从添加到 foreground activity 列表里
        EaseUI.getInstance().pushActivity(this);
        // 注册消息监听
        EMClient.getInstance().chatManager().addMessageListener( msgListener );
    }

    private void uliveOnPause() {
        if (mEasyStreaming != null) {
            mEasyStreaming.onPause();
            isRecording = mEasyStreaming.isRecording();
            if ( !isNeedContinueCaptureAfterBackToMainHome && isRecording ) {
                mEasyStreaming.stopRecording();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isDependActivityLifecycleWhenFirstTime || !isNeedInitStreamingEnv) {
            uliveOnPause();
        }
    }

    @Override public void onStop() {
        super.onStop();
        HomeActivity.isStartLiveActivity = false ;
        //允许屏幕熄灭
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) ;
        // 移除消息监听
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
        // 把此activity 从foreground activity 列表里移除
        EaseUI.getInstance().popActivity(this);
        //解散群组
        if(chatroomId !=null)
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().groupManager().destroyGroup(chatroomId);//需异步处理
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mEasyStreaming != null) {
            mEasyStreaming.onDestroy();
        }

    }

    /**
     * 开始直播
     */
    @OnClick(R.id.btn_start)
    void startLive() {
        canClose = false ;             //不能点击 close
        canBack = false ;              //不能点击 back
        startBtn.setEnabled( false );  //防止多次创建直播
        createChatRoom() ;             //创建直播
    }

    /**
     * 开始直播之前要创建一个聊天室，来控制观众
     */
    private void createChatRoom() {
        startBtn.setText("创建直播中，请稍等。。。");
        EMGroupManager.EMGroupOptions option = new EMGroupManager.EMGroupOptions();
        option.maxUsers = 200 ;
        option.style = EMGroupManager.EMGroupStyle.EMGroupStylePublicOpenJoin;
        try {
            group = EMClient.getInstance().groupManager().createGroup(orgName, "", new String[]{}, "", option);
            if (group != null){
                chatroomId = group.getGroupId();
                //创建直播
                createLiveStream( orgId , chatroomId );

            }
        } catch (HyphenateException e) {
            e.printStackTrace();
            startBtn.setText("确认开播");
            startBtn.setEnabled( true ); //可以重新创建直播
            canBack = true ;             //可以点击back
            canClose = true ;            //可以点击 close
            QLToastUtils.showToast( StartLiveActivity.this , "直播创建失败");
        }
    }

    /**
     * 创建后台直播
     * @param org_id   组织id
     * @param room_id  聊天室id
     */
    private void createLiveStream(String org_id , String room_id  ) {
        LivestreamController.createLiveStream(this, org_id , room_id ,liveId , new Listener<Boolean, String>() {
            @Override
            public void onCallBack(Boolean aBoolean, String reply) {
                if (aBoolean) {
                    canBack = false ;               //不能点击 back
                    canClose = false ;              //不能点击 close
                    isLive = true ;                 //直播创建成功
                    startBtn.setEnabled( false );   //可以重新开启直播
                    startTimMessg();                //开启三秒定时
                } else {
                    QLToastUtils.showToast( StartLiveActivity.this , "直播创建失败");
                    isLive = false ;  //直播创建失败
                    canBack = true ;  //可以点击 back
                    canClose = true ; //可以点击 close
                    startBtn.setEnabled( true );  //可以重新开启直播
                    startBtn.setText("确认开播");
                }
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    /**
     *  切换 麦克风
     */
    @OnClick(R.id.img_bt_switch_voice)
    void toggleMicrophone(){
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if(audioManager.isMicrophoneMute()){
            audioManager.setMicrophoneMute(false);
            voiceSwitch.setSelected(false);
        }else{
            audioManager.setMicrophoneMute(true);
            voiceSwitch.setSelected(true);
        }
    }

    /**
     * 切换 闪光灯
     */
    @OnClick(R.id.img_bt_switch_light)
    void switchLight() {
        //打开或关闭闪关灯
        if( isOpend ){
            boolean succeed = mEasyStreaming.toggleFlashMode();
            if(succeed){
                if(lightSwitch.isSelected()){
                    lightSwitch.setSelected(false);
                }else{
                    lightSwitch.setSelected(true);
                }
            }
        }
    }

    /**
     * 切换摄像头
     */
    @OnClick(R.id.img_bt_switch_camera)
    void switchCamera() {
        isOpend = ! isOpend ;
        c++ ;
        mEasyStreaming.switchCamera();
        if( c == 2){
            c = 1 ;
            lightSwitch.setSelected(false);
        }
    }

    /**
     * 关闭直播   显示直播成果
     */
    @OnClick(R.id.img_bt_close)
    void closeLive() {
        //直播没有创建成功
        if ( !isLive ) {
            if( mEasyStreaming.isRecording() ){
                mEasyStreaming.stopRecording();
            }
            finish();
            return;
        }
        if( !canClose && liveRseltDialog.isShowing()){
            liveRseltDialog.show();
            return;
        }
        //正在创建直播中，不能点击关闭 close 防止报无法预测的错误
        if( !canClose ){
            return;
        }
        if( canClose && isLive ){  //直播创建成功 且在可 close 的情况下
            if(dialog.isShowing()){
                dialog.dismiss();
            }else{
                dialog.show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if( !isLive ){    //还没有开启直播状态
            //停止推流
            if( mEasyStreaming.isRecording() ){
                mEasyStreaming.stopRecording();
            }
            finish();
            return;
        }
        if( !canBack ){           //正在创建直播中，不能点击关闭 back 防止报无法预测的错误
            return;
        }
        if( canBack && isLive ){  //直播以创建成功 且在可 back 的情况下
            if(dialog.isShowing()){
                dialog.dismiss();
            }else{
                dialog.show();
            }
        }
    }

    /**
     * 定时器1   维持心跳
     */
    Handler handlerPost = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //发起维持直播的心跳
            if( msg.what == PSOTLISTER ){
                sendHeartbeat();
                handlerPost.sendEmptyMessageDelayed( PSOTLISTER , 3*60*1000 ) ;
            }
            super.handleMessage(msg);
        }
    };
    //开启心跳
    private void startPost(){
        handlerPost.sendEmptyMessageAtTime( PSOTLISTER , 0 ) ;
    }
    //停止心跳
    private void stopPost(){
        handlerPost.removeMessages( PSOTLISTER );
    }

    /**
     * 三秒
     */
    private Handler timHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSGWAHT: // 1001
                    // 3  2  1
                    handleUpdateCountdown( m );
                    m--;
                    if( m== 0){
                        stopTimMessg();
                        return;
                    }
                    //接到消息后在发消息
                    if( timHandler!= null ) timHandler.sendEmptyMessageDelayed( MESSGWAHT , 1000 ) ;
                    break;
            }
        }
    };

    /**
     * handler 发送执行的消息类型
     */
    private void startTimMessg(){
        if( timHandler!= null ) timHandler.sendEmptyMessageAtTime( MESSGWAHT , 0 ) ;
    }

    /**
     * handler 移除指定的消息类型
     */
    private void stopTimMessg(){
        if( timHandler!= null ) timHandler.removeMessages( MESSGWAHT );
    }



    /**
     * 显示直播的成果
     */
    private void showConfirmCloseLayout() {
        //显示封面
        coverImage.setVisibility(View.GONE);
        String logPath = HttpConfig.getUrl( orglog );
        FrescoImageShowThumb.showThrumb(Uri.parse(logPath) , imageView);
        //Picasso.with( this).load(logPath).placeholder(R.drawable.default_user_icon).fit().centerCrop().into(imageView);
        if( endLiveTime - startLiveTime > 0){
            String tiem = getLiveTime( endLiveTime - startLiveTime );
            timeTv.setText( tiem );
        }else{
            timeTv.setText( "00:00" );
        }
        int a = (int) (watchNumber -1);
        if( a< 0 ) a = 0 ;
        watchTv.setText( a   + "");
        orgnameTv.setText( orgName );
        usernameView1.setText( buiderName );
    }

    /**
     *  3 秒时间到 ， 开启直播
     * @param count
     */
    public void handleUpdateCountdown(final int count) {
        canBack = false ;             // 不能点击 back
        canClose = false ;            // 不能点击 close
        isLive = true ;               //直播创建成功
        startBtn.setEnabled( false ); // 3 秒中
        if (countdownView != null) {
            countdownView.setVisibility(View.VISIBLE);
            countdownView.setText(String.format("%d", count));
            //textView 添加对象
            ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0f, 1.0f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            scaleAnimation.setDuration(COUNTDOWN_DELAY);
            scaleAnimation.setFillAfter(false);
            scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                //动画开始时 调用
                @Override
                public void onAnimationStart(Animation animation) {

                }
                //动画结束时 调用
                @Override
                public void onAnimationEnd( Animation animation ) {
                    countdownView.setVisibility( View.GONE );
                    if (count == COUNTDOWN_END_INDEX && mEasyStreaming != null && !isShutDownCountdown) {
                        mStreamingProfile.setStreamUrl(mRtmpAddress);
                        mEasyStreaming.startRecording();
                        startContainer.setVisibility( View.GONE );
                        onMessageListInit();                            //初始化消息列表
                        startLiveTime = System.currentTimeMillis();     //获取开启直播时的时间
                        QLToastUtils.showToast( StartLiveActivity.this , "开始直播");
                    }
                    //最后一次 （ 1 ） 显示的动画结束后 才让其点击
                    if( count == COUNTDOWN_END_INDEX ){
                        startBtn.setEnabled( true );
                        canBack = true ;  //可以点击back
                        canClose = true ; //可以点击close
                    }
                }
                //
                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            if (!isShutDownCountdown) {
                countdownView.startAnimation(scaleAnimation);  //开启动画
            } else {
                countdownView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (mEasyStreaming != null) {
            mEasyStreaming.startPreview(surface, width, height);  //开启预览
        }
        tempTexture = surface;
        tempStWidth = width;
        tempStHeight = height;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        if (mEasyStreaming != null) {
            mEasyStreaming.updatePreview(width, height);
        }
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (mEasyStreaming != null) {
            mEasyStreaming.stopPreview(true);
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        //这里实时调用
    }

    @Override
    public void onPreviewFrame(int i, byte[] bytes, int i1, int i2) {

    }

    @Override
    public USize[] onPreviewSizeChoose(int i, List<USize> list) {
        return new USize[0];
    }

    @Override
    public void onCameraOpenSucceed(int cameraId , List<Integer> supportCameraIndex , int width, int height) {
        currentCameraIndex = cameraId;
        if (videoCaptureOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            mPreviewContainer.setAspectRatio(((float) width) / height);
        } else {
            mPreviewContainer.setAspectRatio(((float) height) / width);
        }
    }

    @Override
    public void onCameraError(UCameraSessionListener.Error error, Object o) {
        switch (error) {
            case NO_NV21_PREVIEW_FORMAT:
                break;
            case NO_SUPPORT_PREVIEW_SIZE:
                break;
            case NO_PERMISSION:
                break;
            case REQUEST_FLASH_MODE_FAILED:
                break;
            case START_PREVIEW_FAILED:
                break;
        }
    }

    @Override
    public void onCameraFlashSwitched(int i, boolean b) {

    }

    @Override
    public void onNetworkStateChanged(UNetworkListener.State state, Object o) {
        switch (state) {
            case NETWORK_SPEED:
                //当前手机实时全局网络速度
                break;
            case PUBLISH_STREAMING_TIME:
                //sdk内部记录的推流时间,若推流被中断stop之后，该值会重新从0开始计数
                break;
            case DISCONNECT:
                //当前网络状态处于断开状态
                Log.i(TAG, "lifecycle->demo->event->network disconnect.");
                break;
            case RECONNECT:
                //网络重新连接
                Log.i(TAG, "lifecycle->demo->event->restart->after network reconnect:" + "," + mEasyStreaming.isRecording());
                if (mEasyStreaming != null) {
                    mEasyStreaming.restart(); //todo reconnect
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onStateChanged(UStreamStateListener.State state, Object o) {
        switch (state) {
            case PREPARING:
                Log.i(TAG, "lifecycle->demo->stream->preparing");
                break;
            case PREPARED:
                Log.i(TAG, "lifecycle->demo->stream->prepared");
                break;
            case CONNECTING:
                Log.i(TAG, "lifecycle->demo->stream->connecting");
                break;
            case CONNECTED:
                Log.i(TAG, "lifecycle->demo->stream->connected");
                break;
            case START:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //当前 activtiy 没有 finish 就会一直弹出图像
                        while (!isFinishing()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    periscopeLayout.addHeart();
                                }
                            });
                            try {
                                Thread.sleep(new Random().nextInt(400) + 200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
                //起动心跳
                startPost();
                Log.i(TAG, "lifecycle->demo->stream->start");
                break;
            case STOP:
                Log.i(TAG, "lifecycle->demo->stream->stop");
                break;
            case NETWORK_BLOCK:
                networkBlockCount++;
                Log.e(TAG, "lifecycle->dmeo->nework block total = " + networkBlockCount + ", server ip = " + mEasyStreaming.getServerIPAddress() + ", current free buffer = " );
                break;
        }
    }

    @Override
    public void onStreamError(UStreamStateListener.Error error, Object o) {
        switch (error) {
            case AUDIO_PREPARE_FAILED:
                mEasyStreaming = null;
                Log.i(TAG, "lifecycle->demo->stream->audio env prepare failed");
                finish();
                break;
            case VIDEO_PREPARE_FAILED:
                mEasyStreaming = null;
                Log.i(TAG, "lifecycle->demo->stream->video env prepare failed.");
                finish();
                break;
            case INVALID_STREAMING_URL:
                Log.i(TAG, "lifecycle->demo->stream->invalid streaming url.");
                break;
            case SIGNATRUE_FAILED:
                Log.i(TAG, "lifecycle->demo->stream->signature failed.");
                break;
            case IOERROR:
                Log.i(TAG, "lifecycle->demo->stream->io error");
                if (mEasyStreaming != null) {
                    mEasyStreaming.restart(); // to do reconnect
                }
                break;
            case UNKNOWN:
                Log.i(TAG, "lifecycle->demo->stream->unkown error");
                break;
        }
    }

    /**
     * 关闭直播
     * @param
     * @param
     */
    private void colseLiveStream( String org_id  ) {
        LivestreamController.colseLiveStream(this , org_id  , new Listener<Boolean, String>() {
            @Override
            public void onCallBack(Boolean aBoolean, String reply) {
                if ( aBoolean ) {
                } else {
                }
            }
        });
    }

    /**
     * 发起维持直播的心跳
     */
    private void sendHeartbeat(){
        OrganizationController.liveStateLister(this, orgId, new Listener<Boolean, String>() {
            @Override
            public void onCallBack(Boolean aBoolean, String reply) {

            }
        });
    }

    /**
     * @param or_id 组织ID（根据组织ID获取组织所有成员）
     * 获取组织所有成员 （后面显示头像用到） 如果这里获取失败，后面头像无法显示
     */
    public void getAssociationMemberList( String or_id ) {
        OrganizationController.getOrgMemberList( this , or_id , new Listener<Void , List<OrgRequestMemberInfo>>() {
            @Override
            public void onCallBack(Void status, List<OrgRequestMemberInfo> reply) {
                if (reply != null && reply.size() > 0) {
                    for( int i = 0 ; i <reply.size() ; i++){
                        if( !TextUtils.isEmpty( reply.get( i).getPhone() )){
                            orgMemberList.put( reply.get( i).getPhone() ,reply.get( i));
                            messageView.orgMemberList.put( reply.get(i).getPhone() ,reply.get( i ));
                        }
                    }
                }
            }
        });
    }

    /**
     * 计算时间
     * @param time
     * @return
     */
    private String getLiveTime( long time ){

        long a = time/1000;//得到秒数
        if( a<10 ){
            return "00:0"+a;
        }
        if(  a >= 10 && a < 60 ){
            return "00:"+a;
        }
        int b = (int) (a/60);//得到分钟

        int s = (int) (b % 60);

        if( s < 10 ){
            return b +":0"+s ;
        }
        return b+":"+s;
    }

    @Override
    public void viewShow() {
        messageView.listview.setVisibility( View.VISIBLE );    //上滑消息列表显示
    }

    @Override
    public void viewBima() {
        messageView.listview.setVisibility( View.INVISIBLE );  //下滑消息列表隐藏
    }
}
