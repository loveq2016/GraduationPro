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

package app.logic.voice.activity;

import android.media.AudioManager;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.http.HttpClient;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hyphenate.chat.EMCallStateChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;

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
 * 语音通话页面
 */
public class VoiceCallActivity extends CallActivity implements OnClickListener {

    public static final String TARGETNICKNAME = "targetNickName";
    public static final String AVARURL = "avarUrl";
    public static final String USERNAME = "username";
    public static final String ISCOMINGCALL = "isComingCall";

	private LinearLayout comingBtnContainer;
	private ImageView hangupBtn;  //挂断按钮
	private TextView refuseBtn;  //拒接按钮
	private TextView answerBtn;  //接听按钮
	private ImageView muteImage;  //静音按钮
	private ImageView handsFreeImage; //免提按钮

	private boolean isMuteState;       //静音 状态标志
	private boolean isHandsfreeState;  //扬声器 状态标志
	
	private TextView callStateTextView ;  //链接状态的提示语
    private TextView nickTextView , durationTextView;
	private boolean endCallTriggerByMe = false;
	private Chronometer chronometer;  //时间
	String st1;
	private LinearLayout voiceContronlLayout;
    private TextView netwrokStatusVeiw;  //网路状态提示语
    private boolean monitor = false;

    private String tagName ,avarUrl ;
    private SimpleDraweeView avarImg ;
    private UserInfo TargerInfo ;


    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(savedInstanceState != null){
        	finish();
        	return;
        }
		setContentView(R.layout.em_activity_voice_call);
		//DemoHelper.getInstance().isVoiceCalling = true; //这个在发送通知是用到，getIntent intent设置类，跳转到那个activity
		callType = 0;  //呼叫类型 语音

        username = getIntent().getStringExtra(USERNAME); //获取到的是手机号码
        tagName = getIntent().getStringExtra(TARGETNICKNAME); //备注名或是昵称
        avarUrl = getIntent().getStringExtra(AVARURL);      //头像的链接
        avarImg = (SimpleDraweeView) findViewById( R.id.swing_card);
//        if(tagName != null && !TextUtils.isEmpty(tagName)){
//            nickTextView.setText(tagName);
//        }else{
//            nickTextView.setText(username);
//        }
//        FrescoImageShowThumb.showThrumb(Uri.parse(HttpConfig.getUrl(avarUrl)) , avarImg);

        comingBtnContainer = (LinearLayout) findViewById(R.id.ll_coming_call);
		refuseBtn = (TextView) findViewById(R.id.btn_refuse_call);
		answerBtn = (TextView) findViewById(R.id.btn_answer_call);
		hangupBtn = (ImageView) findViewById(R.id.btn_hangup_call);
		muteImage = (ImageView) findViewById(R.id.iv_mute);
		handsFreeImage = (ImageView) findViewById(R.id.iv_handsfree);
		callStateTextView = (TextView) findViewById(R.id.tv_call_state);
        nickTextView = (TextView) findViewById(R.id.tv_nick);
        durationTextView = (TextView) findViewById(R.id.tv_calling_duration);
		chronometer = (Chronometer) findViewById(R.id.chronometer);
		voiceContronlLayout = (LinearLayout) findViewById(R.id.ll_voice_control);
		netwrokStatusVeiw = (TextView) findViewById(R.id.tv_network_status);

		refuseBtn.setOnClickListener(this);
		answerBtn.setOnClickListener(this);
		hangupBtn.setOnClickListener(this);
		muteImage.setOnClickListener(this);
		handsFreeImage.setOnClickListener(this);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON   | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                           | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON   );

		addCallStateListener();  //添加链接状态监听器
		msgid = UUID.randomUUID().toString();

		isInComingCall = getIntent().getBooleanExtra(ISCOMINGCALL, false);

		if (!isInComingCall) {// outgoing call     主动呼叫
			soundPool = new SoundPool(1, AudioManager.STREAM_RING, 0);
			outgoing = soundPool.load(this, R.raw.em_outgoing, 1);

			comingBtnContainer.setVisibility(View.INVISIBLE);
//			hangupBtn.setVisibility(View.VISIBLE);
			st1 = getResources().getString(R.string.Are_connected_to_each_other);
			callStateTextView.setText(st1);
			handler.sendEmptyMessage(MSG_CALL_MAKE_VOICE);
            handler.postDelayed(new Runnable() {
                public void run() {
                    streamID = playMakeCallSounds();
                }
            }, 300);
        } else { // incoming call           被呼叫
			voiceContronlLayout.setVisibility(View.INVISIBLE);
			Uri ringUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
			audioManager.setMode(AudioManager.MODE_RINGTONE);
			audioManager.setSpeakerphoneOn(true);
			ringtone = RingtoneManager.getRingtone(this, ringUri);
			ringtone.play();
		}
        final int MAKE_CALL_TIMEOUT = 50 * 1000;
        handler.removeCallbacks(timeoutHangup);
        handler.postDelayed(timeoutHangup, MAKE_CALL_TIMEOUT);

        getTargetInfo( username );//根据手机号码获取用户信息
	}

	/**
	 * set call state listener
     * 添加通话链接状态监听
	 */
	void addCallStateListener() {
	    callStateListener = new EMCallStateChangeListener() {
            
            @Override
            public void onCallStateChanged(CallState callState, final CallError error) {
                // Message msg = handler.obtainMessage();
                EMLog.d("EMCallManager", "onCallStateChanged:" + callState);
                switch (callState) {
                
                case CONNECTING:  //链接中（请求链接中）
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callStateTextView.setText(st1);
                        }
                    });
                    break;
                case CONNECTED:  //链接结速（已建立连接）
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!isInComingCall) {// outgoing call     主动呼叫
                                String st3 = getResources().getString(R.string.have_connected_with_new);
                                callStateTextView.setText(st3);
                            }else { // incoming call           被呼叫
                                String st3 = getResources().getString(R.string.coming_each_other);
                                callStateTextView.setText(st3);
                            }

                        }
                    });
                    break;

                case ACCEPTED:  //
                    handler.removeCallbacks(timeoutHangup);  //取消定时器
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
                            //show relay or direct call, for testing purpose
//                            ((TextView)findViewById(R.id.tv_is_p2p)).setText(EMClient.getInstance().callManager().isDirectCall()  //返回当前通话时是否为P2P直连
//                                    ? R.string.direct_call : R.string.relay_call);
                            chronometer.setVisibility(View.VISIBLE);
                            chronometer.setBase(SystemClock.elapsedRealtime());
                            // duration start
                            chronometer.start();  //开始计时
                            String str4 = getResources().getString(R.string.In_the_call);
                            callStateTextView.setText(str4);
                            callingState = CallingState.NORMAL;
                            startMonitor();  //启动监控
                        }
                    });
                    break;
                case NETWORK_UNSTABLE:  //网络不稳定
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
                case NETWORK_NORMAL:  //网络正常
                    runOnUiThread(new Runnable() {
                        public void run() {
                            netwrokStatusVeiw.setVisibility(View.INVISIBLE);
                        }
                    });
                    break;
                case VOICE_PAUSE:    //对方设为静音
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "VOICE_PAUSE", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case VOICE_RESUME:  //对方关闭静音
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "VOICE_RESUME", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case DISCONNECTED:  //断开的
                    handler.removeCallbacks(timeoutHangup);  //取消定时器
                    @SuppressWarnings("UnnecessaryLocalVariable") final CallError fError = error;
                    runOnUiThread(new Runnable() {
                        private void postDelayedCloseMsg() {
                            handler.postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.d("AAA", "CALL DISCONNETED");
                                            removeCallStateListener();
                                            saveCallRecord();
                                            Animation animation = new AlphaAnimation(1.0f, 0.0f);
                                            animation.setDuration(800);
                                            findViewById(R.id.root_layout).startAnimation(animation);
                                            finish();
                                        }
                                    });
                                }
                            }, 200);
                        }

                        @Override
                        public void run() {
                            chronometer.stop();  //停止计时
                            callDruationText = chronometer.getText().toString();
                            String st1 = getResources().getString(R.string.Refused);
                            String st2 = getResources().getString(R.string.The_other_party_refused_to_accept);
                            String st3 = getResources().getString(R.string.Connection_failure);
                            String st4 = getResources().getString(R.string.The_other_party_is_not_online);
                            String st5 = getResources().getString(R.string.The_other_is_on_the_phone_please);
                            
                            String st6 = getResources().getString(R.string.The_other_party_did_not_answer_new);
                            String st7 = getResources().getString(R.string.hang_up);
                            String st8 = getResources().getString(R.string.The_other_is_hang_up);
                            
                            String st9 = getResources().getString(R.string.did_not_answer);
                            String st10 = getResources().getString(R.string.Has_been_cancelled);
                            String st11 = getResources().getString(R.string.hang_up);
                            
                            if (fError == CallError.REJECTED) {
                                callingState = CallingState.BEREFUSED;
                                callStateTextView.setText(st2);
                            } else if (fError == CallError.ERROR_TRANSPORT) {
                                callStateTextView.setText(st3);
                            } else if (fError == CallError.ERROR_UNAVAILABLE) {
                                callingState = CallingState.OFFLINE;
                                callStateTextView.setText(st4);
                            } else if (fError == CallError.ERROR_BUSY) {
                                callingState = CallingState.BUSY;
                                callStateTextView.setText(st5);
                            } else if (fError == CallError.ERROR_NORESPONSE) {
                                callingState = CallingState.NO_RESPONSE;
                                callStateTextView.setText(st6);
                            } else if (fError == CallError.ERROR_LOCAL_SDK_VERSION_OUTDATED || fError == CallError.ERROR_REMOTE_SDK_VERSION_OUTDATED){
                                callingState = CallingState.VERSION_NOT_SAME;
                                callStateTextView.setText(R.string.call_version_inconsistent);
                            } else {
                                if (isRefused) {
                                    callingState = CallingState.REFUSED;
                                    callStateTextView.setText(st1);
                                }
                                else if (isAnswered) {
                                    callingState = CallingState.NORMAL;
                                    if (endCallTriggerByMe) {
//                                        callStateTextView.setText(st7);
                                    } else {
                                        callStateTextView.setText(st8);
                                    }
                                } else {
                                    if (isInComingCall) {
                                        callingState = CallingState.UNANSWERED;
                                        callStateTextView.setText(st9);
                                    } else {
                                        if (callingState != CallingState.NORMAL) {
                                            callingState = CallingState.CANCELLED;
                                            callStateTextView.setText(st10);
                                        }else {
                                            callStateTextView.setText(st11);
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

    /**
     * 移除通话链接状态监听
     */
    void removeCallStateListener() {
        EMClient.getInstance().callManager().removeCallStateChangeListener(callStateListener);
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_refuse_call:  //  拒绝按钮
		    isRefused = true;
		    refuseBtn.setEnabled(false);
		    handler.sendEmptyMessage(MSG_CALL_REJECT);
			break;

		case R.id.btn_answer_call:  //接听按钮
		    answerBtn.setEnabled(false);
		    closeSpeakerOn();
            callStateTextView.setText("正在接听...");
			comingBtnContainer.setVisibility(View.INVISIBLE);
//            hangupBtn.setVisibility(View.VISIBLE);
            voiceContronlLayout.setVisibility(View.VISIBLE);
            handler.sendEmptyMessage(MSG_CALL_ANSWER);
			break;

		case R.id.btn_hangup_call:   //挂断按钮
		    hangupBtn.setEnabled(false);
			chronometer.stop();
			endCallTriggerByMe = true;
			callStateTextView.setText(getResources().getString(R.string.hanging_up));
            handler.sendEmptyMessage(MSG_CALL_END);
			break;

		case R.id.iv_mute:   //静音按钮
			if (isMuteState) {
				muteImage.setImageResource(R.drawable.btn_mute_n);
                try {
                    EMClient.getInstance().callManager().resumeVoiceTransfer();  //实时通话时恢复语音数据传输
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
				isMuteState = false;
			} else {
				muteImage.setImageResource(R.drawable.btn_mute_s);
                try {
                    EMClient.getInstance().callManager().pauseVoiceTransfer();  //实时通话时暂停语音数据传输
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
				isMuteState = true;
			}
			break;
		case R.id.iv_handsfree:   //扬声器
			if (isHandsfreeState) {
				handsFreeImage.setImageResource(R.drawable.btn_free_n);
				closeSpeakerOn();  //关闭扬声器
				isHandsfreeState = false;
			} else {
				handsFreeImage.setImageResource(R.drawable.btn_free_s);
				openSpeakerOn();  //打开扬声器
				isHandsfreeState = true;
			}
			break;
		default:
			break;
		}
	}
	
    @Override
    protected void onDestroy() {
        //DemoHelper.getInstance().isVoiceCalling = false; //这个在发送通知是用到，getIntent intent设置类，跳转到那个activity
        stopMonitor();   //停止监控
        super.onDestroy();
    }

	@Override
	public void onBackPressed() {
		callDruationText = chronometer.getText().toString();  //获取通话时间
	}

    /**
     * for debug & testing, you can remove this when release
     * 启动监控
     */
    void startMonitor(){
        monitor = true;
        new Thread(new Runnable() {
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
//                        ((TextView)findViewById(R.id.tv_is_p2p)).setText(EMClient.getInstance().callManager().isDirectCall()
//                                ? R.string.direct_call : R.string.relay_call);
                    }
                });
                while(monitor){
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }, "CallMonitor").start();
    }

    /**
     *停止监控
     */
    void stopMonitor() {
    }


    /**
     * 根据手机号码获取用户信息
     */
    private void getTargetInfo( String userId){
//        UserManagerController.getPhoneMemerInfo(this, phoneNumber, new Listener<Integer, List<UserInfo>>() {
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
