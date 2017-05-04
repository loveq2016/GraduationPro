package app.logic.activity.live;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.widget.EaseImageView;
import com.hyphenate.exceptions.HyphenateException;
import com.squareup.picasso.Picasso;
import com.ucloud.uvod.UMediaProfile;
import com.ucloud.uvod.UPlayerStateListener;
import com.ucloud.uvod.widget.UVideoView;

import org.ql.utils.QLToastUtils;

import java.util.List;
import java.util.Random;

import app.config.http.HttpConfig;
import app.logic.activity.main.HomeActivity;
import app.logic.controller.OrganizationController;
import app.logic.controller.UserManagerController;
import app.logic.live.view.LiveView;
import app.logic.pojo.OrgRequestMemberInfo;
import app.utils.common.Listener;
import app.yy.geju.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 看直播界面
 */
public class LiveDetailsActivity extends LiveBaseActivity implements UPlayerStateListener,LiveView.RoomMessgeViewShow {

    public static final String  PLUG = "LIVEID";
    public static final String  ROOM_ID = "ROOMID";
    public static final String  ORG_NAME = "ORGNAME";
    public static final String  ORG_ID = "ORGID";
    public static final String ORG_LOG_URL="ORGLOGURL";
    public static final String ORG_BUIDER_NAME="ORGBUIDERNAME";

    //拉流地址  http://player.krapnik.cn/uload/123456.flv
    String rtmpPlayStreamUrl = "rtmp://rtmp.krapnik.cn/ucloud/";//rtmp://vlive3.rtmp.cdn.ucloud.com.cn/ucloud/ ，http://vlive3.rtmp.cdn.ucloud.com.cn/ucloud/15959.flv
    private UVideoView mVideoView;
    @BindView(R.id.loading_layout)
    RelativeLayout loadingLayout;  //加载中父view
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;       //进度条
    @BindView(R.id.loading_text)
    TextView loadingText;          //正在加载
    @BindView(R.id.head_img)
    EaseImageView headImg ;
    @BindView(R.id.cover_image)
    ImageView coverView;           //背景图
    @BindView(R.id.tv_username)
    TextView usernameView;         //主播名字
    @BindView(R.id.tv_orgname)
    TextView orgnameView;         //主播名字

    private LiveView liveback;


    private String  orgName , orgId ,orgBuiderName , orgLogUrl , uesrLiveId;

    private static final int MIN_RECONNECT_READ_FRAME_TIMEOUT_COUNT = 3;
    private static final int MIN_RECONNECT_PREPARE_TIMEOUT_COUNT = 3;
    private static final int MAX_RECONNECT_COUNT = 2 ;  //重新链接的最大次数
    private int readFrameTimeoutCount = 0;
    private int prepareTimeoutCount = 0;
    private int reconnectCount = 0;
    private  UMediaProfile profile;
    //点击关闭按钮和back建的次数
    private int closeContent = 0;
    //是否链接成功的标志
    private boolean iscounted = false ;

    @Override
    protected void onActivityCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_live_details);
        ButterKnife.bind(this);
        liveback = (LiveView) findViewById( R.id.liveback);
        liveback.setRoomMessgeViewShow( this );
        mVideoView = (UVideoView) findViewById(R.id.videoview);
        liveId = getIntent().getStringExtra( PLUG );
        chatroomId= getIntent().getStringExtra( ROOM_ID ) ;
        orgId = getIntent().getStringExtra( ORG_ID );
        //获取组织成员列表
        getAssociationMemberList( orgId );
        orgName = getIntent().getStringExtra(ORG_NAME);
        orgBuiderName = getIntent().getStringExtra(ORG_BUIDER_NAME);
        orgLogUrl = getIntent().getStringExtra(ORG_LOG_URL );
        usernameView.setText( orgBuiderName );
        orgnameView.setText( orgName );
        String logPath = HttpConfig.getUrl(orgLogUrl);
        Picasso.with(this).load(logPath).placeholder(R.drawable.default_user_icon).fit().centerCrop().into(coverView);
        Picasso.with(this).load(logPath).placeholder(R.drawable.default_user_icon).fit().centerCrop().into(headImg);
        //链接直播
        connect();
    }


    /**
     * 连接
     */
    private void connect() {
        profile = new UMediaProfile();
        profile.setInteger(UMediaProfile.KEY_START_ON_PREPARED, 1);
        profile.setInteger(UMediaProfile.KEY_ENABLE_BACKGROUND_PLAY, 0);
        profile.setInteger(UMediaProfile.KEY_LIVE_STREAMING, 1);
        profile.setInteger(UMediaProfile.KEY_PREPARE_TIMEOUT, 1000 * 5);    //live-streaming 1 default 5s 0 10s
        profile.setInteger(UMediaProfile.KEY_READ_FRAME_TIMEOUT, 1000 * 5); //live-streaming 1 default 5s 0 10s

        if (mVideoView != null && mVideoView.isInPlaybackState()) {         //处于播放状态，就要先释放资源
            mVideoView.stopPlayback();
            mVideoView.release(true);
        }
        if (mVideoView != null) {                                                //设置监听和播放地址
            mVideoView.setMediaPorfile(profile);//set before setVideoPath
            mVideoView.setOnPlayerStateListener(this);//set before setVideoPath
            mVideoView.setVideoPath( rtmpPlayStreamUrl + liveId + ".flv");       //设置播放地址
        }
        //else {
          //  Log.e(TAG, "lifecycle->dmeo->Are you findViewById(.....) bind UVideoView");
            //Toast.makeText(this, "Are you findViewById(.....) bind UVideoView", Toast.LENGTH_SHORT).show();
        //}
    }

    /**
     * 重新连接
     */
    private void reconnect() {
        readFrameTimeoutCount = 0;
        prepareTimeoutCount = 0;
        if (reconnectCount < MAX_RECONNECT_COUNT) {  //最大重新链接时间数
            reconnectCount++;
            Log.e(TAG, "lifecycle->demo->Play failed, reconnect count = " + reconnectCount);
            connect();
        } else {
            if (mVideoView != null) {
                mVideoView.stopPlayback();
                mVideoView.release(true);
            }
            Log.e(TAG, "lifecycle->demo->Play failed, reconnect MAX count = " + reconnectCount + " reconnect stop.");
            //Toast.makeText(this, "Play failed, reconnect MAX count = " + reconnectCount + " reconnect stop.", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "链接超时，请重新链接" , Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        HomeActivity.isLiveDetailsActivity = true;
        mVideoView.onResume();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //消息列表初始化 标志
        if (isMessageListInited){
            //消息列表刷新
            messageView.refresh();
        }
        //把此activity 添加到 从foreground activity 列表中
        EaseUI.getInstance().pushActivity(this);
        //添加 消息 监听
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVideoView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        HomeActivity.isLiveDetailsActivity = false;
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) ;
        //移除消息监听
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
        //把此activity 从foreground activity 列表里移除
        EaseUI.getInstance().popActivity(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVideoView != null) {
            mVideoView.onDestroy();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().groupManager().leaveGroup(chatroomId); //需异步处理
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @OnClick(R.id.img_bt_close)
    void close(){
        EMMessage message = EMMessage.createTxtSendMessage( "离开直播" , chatroomId);
        message.setChatType(EMMessage.ChatType.GroupChat);              //消息类型
        message.setAttribute( "em_ignore_notification" , true);     //取消推送通知
        message.setFrom(UserManagerController.getCurrUserInfo().getNickName());
        EMClient.getInstance().chatManager().sendMessage(message);      //发送消息内容
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);//延时0.1秒，在退出群组，确保信息发送成功
                    EMClient.getInstance().groupManager().leaveGroup(chatroomId); //需异步处理
                    finish();
                } catch (HyphenateException e) {
                    e.printStackTrace();
                   finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    finish();
                }
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        closeContent ++ ;
        EMMessage message = EMMessage.createTxtSendMessage( "离开直播" , chatroomId );
        message.setChatType(EMMessage.ChatType.GroupChat);              //消息类型
        message.setAttribute( "em_ignore_notification" , true);     //取消推送通知
        message.setFrom(UserManagerController.getCurrUserInfo().getNickName());
        EMClient.getInstance().chatManager().sendMessage(message);      //发送消息内容
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);//延时0.1秒，在退出群组，确保信息发送成功
                    EMClient.getInstance().groupManager().leaveGroup(chatroomId); //需异步处理
                    finish();
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                   finish();
                }
            }
        }).start();
    }

    @Override
    public void onPlayerStateChanged(State state, int i, Object o) {
        switch (state) {
            case PREPARING:
                Log.i(TAG, "lifecycle->demo->PREPARING");
                break;
            case PREPARED:
                prepareTimeoutCount = 0;
                Log.i(TAG, "lifecycle->demo->PREPARED");
                break;
            case START:
                Log.i(TAG, "lifecycle->demo->START");
                mVideoView.applyAspectRatio(UVideoView.VIDEO_RATIO_FILL_PARENT);//set after start
                loadingLayout.setVisibility(View.INVISIBLE); //加载背景隐藏
                //当前用户加入聊天室
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            EMClient.getInstance().groupManager().joinGroup( chatroomId ); //需异步处理
                            iscounted = true ; //加入聊天室标志
                            onMessageListInit();
                            EMMessage message = EMMessage.createTxtSendMessage("进入直播" , chatroomId);
                            message.setChatType(EMMessage.ChatType.GroupChat);          //消息类型
                            message.setAttribute( "em_ignore_notification" , true);     //取消推送通知
                            message.setFrom(UserManagerController.getCurrUserInfo().getNickName());
                            EMClient.getInstance().chatManager().sendMessage(message);  //发送消息内容
                            messageView.refreshSelectLast();
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                //开始放星星
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //当前 activtiy 没有 finish 就会一直弹出爱心
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
                break;
            case VIDEO_SIZE_CHANGED:  //视屏大小发生变化（应该是切换屏幕的时候回调这里）
                break;
            case COMPLETED:           //主播关闭直播时回调这里
                Log.i(TAG, "lifecycle->demo->COMPLETED");
                QLToastUtils.showToast( this , "直播已结束");
                finish();
                break;
        }

    }

    @Override
    public void onPlayerInfo(Info info, int i, Object o) {
        switch (info) {
            case BUFFERING_START: //缓冲开始
                Log.i(TAG, "lifecycle-demo->Play onPlayerInfo, reconnect BUFFERING_START  " );//lifecycle->demo->COMPLETED
                break;
            case BUFFERING_END:   //缓冲结束
                Log.i(TAG, "lifecycle-demo->Play onPlayerInfo, reconnect BUFFERING_END  " );
                readFrameTimeoutCount = 0;
                prepareTimeoutCount = 0;
                if (reconnectCount != 0) {
                    Log.i(TAG, "lifecycle-demo->Play onPlayerInfo, reconnect count = " + reconnectCount);
                    Toast.makeText(this, "Play Succeed, reconnect count = " + reconnectCount, Toast.LENGTH_SHORT).show();
                    reconnectCount = 0;
                }
                break;
            case BUFFERING_UPDATE:  //缓冲更新
                Log.i(TAG, "lifecycle-demo->Play onPlayerInfo, reconnect BUFFERING_END  " );
                break;
        }
    }

    @Override
    public void onPlayerError(Error error, int i, Object o) {
        switch (error) {
            case IOERROR:
                Log.w(TAG, "lifecycle->demo->IOERROR->");
                //Toast.makeText(this, "Error: " + error, Toast.LENGTH_SHORT).show();
                break;
            case PREPARE_TIMEOUT: //准备超时
                prepareTimeoutCount++;
                Log.w(TAG, "lifecycle->demo->PREPARE_TIMEOUT->" + prepareTimeoutCount);
                //Toast.makeText(this, "链接超时", Toast.LENGTH_SHORT).show();
                if (prepareTimeoutCount >= MIN_RECONNECT_PREPARE_TIMEOUT_COUNT) {//reconnect
                    reconnect();
                }
                break;
            case READ_FRAME_TIMEOUT:  //直播中，读取帧超时
                readFrameTimeoutCount++;
                Log.w(TAG, "lifecycle->demo->READ_FRAME_TIMEOUT->" + readFrameTimeoutCount);
                if (readFrameTimeoutCount >= MIN_RECONNECT_READ_FRAME_TIMEOUT_COUNT) {//reconnect
                    reconnect();
                }
                break;
            case UNKNOWN:             //未知错误
                Log.w(TAG, "lifecycle->demo->UNKNOWN->");
                //Toast.makeText(this, "Error: " + error , Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "未知错误" , Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
    }

    /**
     * @param or_id 组织ID（根据组织ID获取组织所有成员）
     * 获取组织所有成员 （后面显示头像要用到）如果这里获取失败，后面头像无法显示
     */
    public void getAssociationMemberList( String or_id ) {
        OrganizationController.getOrgMemberList( this , or_id , new Listener<Void , List<OrgRequestMemberInfo>>() {
            @Override
            public void onCallBack(Void status, List<OrgRequestMemberInfo> reply) {
                if ( reply != null && reply.size() > 0) {
                    for( int i = 0 ; i <reply.size() ; i++){
                        if( !TextUtils.isEmpty( reply.get( i).getPhone() )){
                            orgMemberList.put( reply.get( i).getPhone() ,reply.get( i));
                            messageView.orgMemberList.put( reply.get(i).getPhone() ,reply.get( i));
                        }
                    }
                }
            }
        });
    }

    /**
     * 根据手机号码查找名字
     * @param phon
     * @return
     */
    private String getUserNickNameByHXAccount(String phon) {
        if (phon == null || orgMemberList == null) {
            return "";
        }
        OrgRequestMemberInfo info = orgMemberList.get(phon);
        if (info != null) {
            return info.getNickName();
        }
        return phon ;
    }

    @Override
    public void viewShow() {
        messageView.listview.setVisibility( View.VISIBLE);
    }

    @Override
    public void viewBima() {
        messageView.listview.setVisibility( View.INVISIBLE);
    }
}