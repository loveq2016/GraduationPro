/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package app.logic.videocall.Activity;

import android.hardware.Camera;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hyphenate.chat.EMCallManager.EMCameraDataProcessor;
import com.hyphenate.chat.EMCallManager.EMVideoCallHelper;
import com.hyphenate.chat.EMCallStateChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.media.EMLocalSurfaceView;
import com.hyphenate.media.EMOppositeSurfaceView;
import com.hyphenate.util.EMLog;
import com.superrtc.sdk.VideoView;

import org.ql.activity.customtitle.FragmentActActivity;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import app.config.http.HttpConfig;
import app.logic.call.activity.CallActivity;
import app.logic.controller.UserManagerController;
import app.logic.pojo.UserInfo;
import app.utils.common.FrescoImageShowThumb;
import app.utils.common.Listener;
import app.yy.geju.R;

/**
 * 视频通话界面
 */
public class VideoCallActivity extends CallActivity implements OnClickListener {

    public static final String TARGETNICKNAME = "targetNickName";
    public static final String AVARURL = "avarUrl";
    public static final String USERNAME = "username";
    public static final String ISCOMINGCALL = "isComingCall";
    private boolean isMuteState;
    private boolean isHandsfreeState;
    private boolean isAnswered;
    private boolean endCallTriggerByMe = false;
    private boolean monitor = true;
    private TextView callStateTextView;
    private FrameLayout comingBtnContainer;
    private TextView refuseBtn;
    private TextView answerBtn;
    private ImageView hangupBtn;
    private ImageView muteImage;
//    private ImageView handsFreeImage;
    private TextView nickTextView;
    private Chronometer chronometer;
    private LinearLayout voiceContronlLayout;
    private RelativeLayout rootContainer;
    private LinearLayout topContainer;
    private LinearLayout bottomContainer;
    private TextView monitorTextView;
    private TextView netwrokStatusVeiw;
    private Handler uiHandler;
    private boolean isInCalling;
    boolean isRecording = false;
    //private Button recordBtn;
    private EMVideoCallHelper callHelper;
    private Button toggleVideoBtn;
    private BrightnessDataProcess dataProcessor = new BrightnessDataProcess();
    private String tagName , avarUrl ;
    private UserInfo TargerInfo ;
    private SimpleDraweeView avarImg ;
    
    // dynamic adjust brightness
    class BrightnessDataProcess implements EMCameraDataProcessor {
        byte yDelta = 0;
        synchronized void setYDelta(byte yDelta) {
            Log.d("VideoCallActivity", "brigntness uDelta:" + yDelta);
            this.yDelta = yDelta;
        }

        // data size is width*height*2
        // the first width*height is Y, second part is UV
        // the storage layout detailed please refer 2.x demo CameraHelper.onPreviewFrame
        @Override
        public synchronized void onProcessData(byte[] data, Camera camera, final int width, final int height, final int rotateAngel) {
            int wh = width * height;
            for (int i = 0; i < wh; i++) {
                int d = (data[i] & 0xFF) + yDelta;
                d = d < 16 ? 16 : d;
                d = d > 235 ? 235 : d;
                data[i] = (byte)d;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
        	finish();
        	return;
        }
        setContentView(R.layout.em_activity_video_call);
        //DemoHelper.getInstance().isVideoCalling = true;//这个在发送通知是用到，getIntent intent设置类，跳转到那个activity
        callType = 1;
        username = getIntent().getStringExtra(USERNAME);      //获取到的是手机号码
        tagName = getIntent().getStringExtra(TARGETNICKNAME); //备注或昵称
        avarUrl = getIntent().getStringExtra(AVARURL);        //头像链接
        isInComingCall = getIntent().getBooleanExtra(ISCOMINGCALL, false);
//        if( tagName!=null && !TextUtils.isEmpty(tagName)){
//            nickTextView.setText(tagName);
//        }else{
//            nickTextView.setText(username);
//        }
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        uiHandler = new Handler();

        avarImg = (SimpleDraweeView) findViewById( R.id.swing_card);

        callStateTextView = (TextView) findViewById(R.id.tv_call_state);
        comingBtnContainer = (FrameLayout) findViewById(R.id.ll_coming_call);
        rootContainer = (RelativeLayout) findViewById(R.id.root_layout);
        refuseBtn = (TextView) findViewById(R.id.btn_refuse_call);
        answerBtn = (TextView) findViewById(R.id.btn_answer_call);
        hangupBtn = (ImageView) findViewById(R.id.btn_hangup_call);
        muteImage = (ImageView) findViewById(R.id.iv_mute);
//        handsFreeImage = (ImageView) findViewById(R.id.iv_handsfree);
        callStateTextView = (TextView) findViewById(R.id.tv_call_state);
        nickTextView = (TextView) findViewById(R.id.tv_nick);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        voiceContronlLayout = (LinearLayout) findViewById(R.id.ll_voice_control);
        RelativeLayout btnsContainer = (RelativeLayout) findViewById(R.id.ll_btns);
        topContainer = (LinearLayout) findViewById(R.id.ll_top_container);
        bottomContainer = (LinearLayout) findViewById(R.id.ll_bottom_container);
        monitorTextView = (TextView) findViewById(R.id.tv_call_monitor);
        netwrokStatusVeiw = (TextView) findViewById(R.id.tv_network_status);
//        recordBtn = (Button) findViewById(R.id.btn_record_video);
        ImageView switchCameraBtn = (ImageView) findViewById(R.id.btn_switch_camera);
        Button captureImageBtn = (Button) findViewById(R.id.btn_capture_image);
        SeekBar YDeltaSeekBar = (SeekBar) findViewById(R.id.seekbar_y_detal);

        refuseBtn.setOnClickListener(this);
        answerBtn.setOnClickListener(this);
        hangupBtn.setOnClickListener(this);
        muteImage.setOnClickListener(this);
//        handsFreeImage.setOnClickListener(this);
        rootContainer.setOnClickListener(this);
//        recordBtn.setOnClickListener(this);
        switchCameraBtn.setOnClickListener(this);
        captureImageBtn.setOnClickListener(this);
        YDeltaSeekBar.setOnSeekBarChangeListener(new YDeltaSeekBarListener());
        msgid = UUID.randomUUID().toString();
        // local surfaceview
        localSurface = (EMLocalSurfaceView) findViewById(R.id.local_surface);
        localSurface.setZOrderMediaOverlay(true);
        localSurface.setZOrderOnTop(true);
        // remote surfaceview
        oppositeSurface = (EMOppositeSurfaceView) findViewById(R.id.opposite_surface);
        oppositeSurface.setScaleMode(VideoView.EMCallViewScaleMode.EMCallViewScaleModeAspectFill);
        // set call state listener
        addCallStateListener();
        if (!isInComingCall) {// outgoing call
            soundPool = new SoundPool(1, AudioManager.STREAM_RING, 0);
            outgoing = soundPool.load(this, R.raw.em_outgoing, 1);
            comingBtnContainer.setVisibility(View.INVISIBLE);
            hangupBtn.setVisibility(View.VISIBLE);
            String st = getResources().getString(R.string.Are_connected_to_each_other);
            callStateTextView.setText(st);
            EMClient.getInstance().callManager().setSurfaceView(localSurface, oppositeSurface);
            handler.sendEmptyMessage(MSG_CALL_MAKE_VIDEO);
            handler.postDelayed(new Runnable() {
                public void run() {
                    streamID = playMakeCallSounds();
                }
            }, 300);
        } else { // incoming call

            if(EMClient.getInstance().callManager().getCallState() == EMCallStateChangeListener.CallState.IDLE
                    || EMClient.getInstance().callManager().getCallState() == EMCallStateChangeListener.CallState.DISCONNECTED) {
                // the call has ended
                finish();
                return;
            }
            voiceContronlLayout.setVisibility(View.INVISIBLE);
            localSurface.setVisibility(View.INVISIBLE);
            Uri ringUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            audioManager.setMode(AudioManager.MODE_RINGTONE);
            audioManager.setSpeakerphoneOn(true);
            ringtone = RingtoneManager.getRingtone(this, ringUri);
            ringtone.play();
            EMClient.getInstance().callManager().setSurfaceView(localSurface, oppositeSurface);
        }
        
        final int MAKE_CALL_TIMEOUT = 50 * 1000;
        handler.removeCallbacks(timeoutHangup);
        handler.postDelayed(timeoutHangup, MAKE_CALL_TIMEOUT);
        // get instance of call helper, should be called after setSurfaceView was called
        callHelper = EMClient.getInstance().callManager().getVideoCallHelper();

        /**
         * This function is only meaningful when your app need recording
         * If not, remove it.
         * This function need be called before the video stream started, so we set it in onCreate function.
         * This method will set the preferred video record encoding codec.
         * Using default encoding format, recorded file may not be played by mobile player.
         */
//        callHelper.setPreferMovFormatEnable(true);
        EMClient.getInstance().callManager().setCameraDataProcessor(dataProcessor);

        getTargetInfo( username ) ; //根据手机号码数获取用户信息

    }
    
    class YDeltaSeekBarListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            dataProcessor.setYDelta((byte)(20.0f * (progress - 50) / 50.0f)); 
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
        
    }

    /**
     * set call state listener
     */
    void addCallStateListener() {
        callStateListener = new EMCallStateChangeListener() {

            @Override
            public void onCallStateChanged(CallState callState, final CallError error) {
                switch (callState) {

                case CONNECTING: // is connecting
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            callStateTextView.setText(R.string.Are_connected_to_each_other);
                        }

                    });
                    break;
                case CONNECTED: // connected
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (!isInComingCall) {// outgoing call
                                callStateTextView.setText(R.string.have_connected_with);
                            }else{
                                callStateTextView.setText(R.string.coming_connected_with);
                            }

                        }

                    });
                    break;

                case ACCEPTED: // call is accepted
                    handler.removeCallbacks(timeoutHangup);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                if (soundPool != null)
                                    soundPool.stop(streamID);
                            } catch (Exception e) {
                            }
                            if(!isHandsfreeState)
                                closeSpeakerOn();
//                            ((TextView)findViewById(R.id.tv_is_p2p)).setText(EMClient.getInstance().callManager().isDirectCall()
//                                    ? R.string.direct_call : R.string.relay_call);
//                            handsFreeImage.setImageResource(R.drawable.em_icon_speaker_on);
                            isHandsfreeState = true;
                            isInCalling = true;
                            chronometer.setVisibility(View.VISIBLE);
                            chronometer.setBase(SystemClock.elapsedRealtime());
                            // call durations start
                            chronometer.start();
                            nickTextView.setVisibility(View.INVISIBLE);
                            avarImg.setVisibility(View.INVISIBLE);
//                            callStateTextView.setText(R.string.In_the_call);
                            callStateTextView.setText("");
//                            recordBtn.setVisibility(View.VISIBLE);
                            callingState = CallingState.NORMAL;
                            startMonitor();
                        }

                    });
                    break;
                case NETWORK_DISCONNECTED:
                    runOnUiThread(new Runnable() {
                        public void run() {
                            netwrokStatusVeiw.setVisibility(View.VISIBLE);
                            netwrokStatusVeiw.setText(R.string.network_unavailable);
                        }
                    });
                    break;
                case NETWORK_UNSTABLE:
                    runOnUiThread(new Runnable() {
                        public void run() {
                            netwrokStatusVeiw.setVisibility(View.VISIBLE);
                            if(error == CallError.ERROR_NO_DATA){
                                netwrokStatusVeiw.setText(R.string.no_call_data);
                            }else{
                                netwrokStatusVeiw.setText(R.string.network_unstable);
                            }
                        }
                    });
                    break;
                case NETWORK_NORMAL:
                    runOnUiThread(new Runnable() {
                        public void run() {
                            netwrokStatusVeiw.setVisibility(View.INVISIBLE);
                        }
                    });
                    break;
                case VIDEO_PAUSE:
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "VIDEO_PAUSE", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case VIDEO_RESUME:
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "VIDEO_RESUME", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case VOICE_PAUSE:
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "VOICE_PAUSE", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case VOICE_RESUME:
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "VOICE_RESUME", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case DISCONNECTED: // call is disconnected
                    handler.removeCallbacks(timeoutHangup);
                    @SuppressWarnings("UnnecessaryLocalVariable")final CallError fError = error;
                    runOnUiThread(new Runnable() {
                        private void postDelayedCloseMsg() {
                            uiHandler.postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    removeCallStateListener();
                                    saveCallRecord();
                                    Animation animation = new AlphaAnimation(1.0f, 0.0f);
                                    animation.setDuration(1200);
                                    rootContainer.startAnimation(animation);
                                    finish();
                                }

                            }, 200);
                        }

                        @Override
                        public void run() {
                            chronometer.stop();
                            callDruationText = chronometer.getText().toString();
                            String s1 = getResources().getString(R.string.The_other_party_refused_to_accept);
                            String s2 = getResources().getString(R.string.Connection_failure);
                            String s3 = getResources().getString(R.string.The_other_party_is_not_online);
                            String s4 = getResources().getString(R.string.The_other_is_on_the_phone_please);
                            String s5 = getResources().getString(R.string.The_other_party_did_not_answer);
                            
                            String s6 = getResources().getString(R.string.hang_up);
                            String s7 = getResources().getString(R.string.The_other_is_hang_up);
                            String s8 = getResources().getString(R.string.did_not_answer);
                            String s9 = getResources().getString(R.string.Has_been_cancelled);
                            String s10 = getResources().getString(R.string.Refused);
                            
                            if (fError == CallError.REJECTED) {
                                callingState = CallingState.BEREFUSED;
                                callStateTextView.setText(s1);
                            } else if (fError == CallError.ERROR_TRANSPORT) {
                                callStateTextView.setText(s2);
                            } else if (fError == CallError.ERROR_UNAVAILABLE) {
                                callingState = CallingState.OFFLINE;
                                callStateTextView.setText(s3);
                            } else if (fError == CallError.ERROR_BUSY) {
                                callingState = CallingState.BUSY;
                                callStateTextView.setText(s4);
                            } else if (fError == CallError.ERROR_NORESPONSE) {
                                callingState = CallingState.NO_RESPONSE;
                                callStateTextView.setText(s5);
                            }else if (fError == CallError.ERROR_LOCAL_SDK_VERSION_OUTDATED || fError == CallError.ERROR_REMOTE_SDK_VERSION_OUTDATED){
                                callingState = CallingState.VERSION_NOT_SAME;
                                callStateTextView.setText(R.string.call_version_inconsistent);
                            } else {
                                if (isRefused) {
                                    callingState = CallingState.REFUSED;
                                    callStateTextView.setText(s10);
                                }
                                else if (isAnswered) {
                                    callingState = CallingState.NORMAL;
                                    if (endCallTriggerByMe) {
//                                        callStateTextView.setText(s6);
                                    } else {
                                        callStateTextView.setText(s7);
                                    }
                                } else {
                                    if (isInComingCall) {
                                        callingState = CallingState.UNANSWERED;
                                        callStateTextView.setText(s8);
                                    } else {
                                        if (callingState != CallingState.NORMAL) {
                                            callingState = CallingState.CANCELLED;
                                            callStateTextView.setText(s9);
                                        } else {
                                            callStateTextView.setText(s6);
                                        }
                                    }
                                }
                            }
                            postDelayedCloseMsg();
                        }

                    });

                    break;

                default:
                    break;
                }

            }
        };
        EMClient.getInstance().callManager().addCallStateChangeListener(callStateListener);
    }
    
    void removeCallStateListener() {
        EMClient.getInstance().callManager().removeCallStateChangeListener(callStateListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_refuse_call: // 拒绝按钮
            isRefused = true;
            refuseBtn.setEnabled(false);
            handler.sendEmptyMessage(MSG_CALL_REJECT);
            break;

        case R.id.btn_answer_call: // 接听按钮
            EMLog.d(TAG, "btn_answer_call clicked");
            answerBtn.setEnabled(false);
            //openSpeakerOn(); 开始接通就要打开扬声器？
            if (ringtone != null)
                ringtone.stop();
            
            callStateTextView.setText("answering...");
            handler.sendEmptyMessage(MSG_CALL_ANSWER);
//            handsFreeImage.setImageResource(R.drawable.em_icon_speaker_on);
            isAnswered = true;
            isHandsfreeState = true;
            comingBtnContainer.setVisibility(View.INVISIBLE);
            hangupBtn.setVisibility(View.VISIBLE);
            voiceContronlLayout.setVisibility(View.VISIBLE);
            localSurface.setVisibility(View.VISIBLE);
            break;

        case R.id.btn_hangup_call: // 挂断按钮
            hangupBtn.setEnabled(false);
            chronometer.stop();  //停止计时
            endCallTriggerByMe = true;
            callStateTextView.setText(getResources().getString(R.string.hanging_up));
            if(isRecording){
                callHelper.stopVideoRecord();
            }
            handler.sendEmptyMessage(MSG_CALL_END);
            break;

        case R.id.iv_mute: // 静音按钮
            if (isMuteState) {
                // resume voice transfer
                muteImage.setImageResource(R.drawable.btn_mute_n);
                try {
                    EMClient.getInstance().callManager().resumeVoiceTransfer();
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
                isMuteState = false;
            } else {
                // pause voice transfer
                muteImage.setImageResource(R.drawable.btn_mute_s);
                try {
                    EMClient.getInstance().callManager().pauseVoiceTransfer();
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
                isMuteState = true;
            }
            break;
//        case R.id.iv_handsfree: // 扬声器
//            if (isHandsfreeState) {
//                // turn off speaker
//                handsFreeImage.setImageResource(R.drawable.em_icon_speaker_normal);
//                closeSpeakerOn();
//                isHandsfreeState = false;
//            } else {
//                handsFreeImage.setImageResource(R.drawable.em_icon_speaker_on);
//                openSpeakerOn();
//                isHandsfreeState = true;
//            }
//            break;
        /*
        case R.id.btn_record_video: //record the video
            if(!isRecording){
//                callHelper.startVideoRecord(PathUtil.getInstance().getVideoPath().getAbsolutePath());
                callHelper.startVideoRecord("/sdcard/");
                EMLog.d(TAG, "startVideoRecord:" + PathUtil.getInstance().getVideoPath().getAbsolutePath());
                isRecording = true;
                recordBtn.setText(R.string.stop_record);
            }else{
                String filepath = callHelper.stopVideoRecord();
                isRecording = false;
                recordBtn.setText(R.string.recording_video);
                Toast.makeText(getApplicationContext(), String.format(getString(R.string.record_finish_toast), filepath), Toast.LENGTH_LONG).show();
            }
            break;
        */
        case R.id.root_layout:  //rootView 全屏切换
            if (callingState == CallingState.NORMAL) {
                if (bottomContainer.getVisibility() == View.VISIBLE) {
                    bottomContainer.setVisibility(View.GONE);
                    topContainer.setVisibility(View.GONE);
                    hangupBtn.setVisibility( View.GONE);
//                    oppositeSurface.setScaleMode(VideoView.EMCallViewScaleMode.EMCallViewScaleModeAspectFill);
                    
                } else {
                    bottomContainer.setVisibility(View.VISIBLE);
                    topContainer.setVisibility(View.VISIBLE);
                    hangupBtn.setVisibility(View.VISIBLE);
//                    oppositeSurface.setScaleMode(VideoView.EMCallViewScaleMode.EMCallViewScaleModeAspectFit);
                }
            }
            break;
        case R.id.btn_switch_camera: //切换摄像头
            handler.sendEmptyMessage(MSG_CALL_SWITCH_CAMERA);
            break;
        case R.id.btn_capture_image:  //抓取图片
            DateFormat df = DateFormat.getDateTimeInstance();
            Date d = new Date();
            final String filename = Environment.getExternalStorageDirectory() + df.format(d) + ".jpg";
            EMClient.getInstance().callManager().getVideoCallHelper().takePicture(filename);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(VideoCallActivity.this, "保存路径:" + filename, Toast.LENGTH_SHORT).show();
                }
            });
            break;
        default:
            break;
        }
    }
    
    @Override
    protected void onDestroy() {
        //DemoHelper.getInstance().isVideoCalling = false;//这个在发送通知是用到，getIntent intent设置类，跳转到那个activity
        stopMonitor();  //停止监控
        if(isRecording){
            callHelper.stopVideoRecord();
            isRecording = false;
        }
        localSurface.getRenderer().dispose();
        localSurface = null;
        oppositeSurface.getRenderer().dispose();
		oppositeSurface = null;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        callDruationText = chronometer.getText().toString();  //获取时间
        super.onBackPressed();
    }
    
    /**
     * 启动监控
     * for debug & testing, you can remove this when release
     */
    void startMonitor(){
        monitor = true;
        new Thread(new Runnable() {
            public void run() {
                while(monitor){
                    runOnUiThread(new Runnable() {
                        public void run() {
//                            monitorTextView.setText("WidthxHeight："+callHelper.getVideoWidth()+"x"+callHelper.getVideoHeight()
//                                    + "\nDelay：" + callHelper.getVideoLatency()
//                                    + "\nFramerate：" + callHelper.getVideoFrameRate()
//                                    + "\nLost：" + callHelper.getVideoLostRate()
//                                    + "\nLocalBitrate：" + callHelper.getLocalBitrate()
//                                    + "\nRemoteBitrate：" + callHelper.getRemoteBitrate());
//
//                            ((TextView)findViewById(R.id.tv_is_p2p)).setText(EMClient.getInstance().callManager().isDirectCall()
//                                    ? R.string.direct_call : R.string.relay_call);
                        }
                    });
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }, "CallMonitor").start();
    }

    /**
     * 停止监控
     */
    void stopMonitor(){
        monitor = false;
    }
    
    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        if(isInCalling){
            try {
                EMClient.getInstance().callManager().pauseVideoTransfer();  //实时通话时停止视频数据传输
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if(isInCalling){
            try {
                EMClient.getInstance().callManager().resumeVideoTransfer(); //实时通话时恢复视频数据传输
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据手机号码获取用户信息
     */
    private void getTargetInfo( String userId){
//        UserManagerController.getPhoneMemerInfo(this, userId, new Listener<Integer, List<UserInfo>>() {
//            @Override
//            public void onCallBack(Integer integer, List<UserInfo> reply) {
//                if( null == reply || reply.size() == 0 ){
//                    nickTextView.setText(username); //获取用户失败的情况下，昵称显示手机号码
//                    return;
//                }
//                TargerInfo  = reply.get(0);
//                if(null != TargerInfo ){
//                    if( null!= TargerInfo.getFriend_name() && !TextUtils.isEmpty(TargerInfo.getFriend_name())){
//                        nickTextView.setText(tagName);
//                    }else{
//                        nickTextView.setText(TargerInfo.getNickName());
//                    }
//                    FrescoImageShowThumb.showThrumb(Uri.parse(HttpConfig.getUrl(TargerInfo.getPicture_url())) , avarImg);
//                }
//            }
//        });

        UserManagerController.getUserInfo(this, userId, new Listener<Integer, UserInfo>() {
            @Override
            public void onCallBack(Integer status , UserInfo reply) {
                if (status == 1 && reply != null) {
                    TargerInfo = reply;
                    if(null != TargerInfo ){
                        if( null!= TargerInfo.getFriend_name() && !TextUtils.isEmpty(TargerInfo.getFriend_name())){
                            nickTextView.setText(tagName);
                        }else{
                            nickTextView.setText(TargerInfo.getNickName());
                        }
                        FrescoImageShowThumb.showThrumb(Uri.parse(HttpConfig.getUrl(TargerInfo.getPicture_url())) , avarImg);
                    }
                }else
                    nickTextView.setText(""); //获取用户失败的情况下，昵称显示手机号码
            }
        });
    }
}
