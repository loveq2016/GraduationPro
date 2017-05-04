package app.logic.call.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.widget.Toast;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMCallManager;
import com.hyphenate.chat.EMCallStateChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage.Status;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.EMServiceNotReadyException;
import com.hyphenate.media.EMLocalSurfaceView;
import com.hyphenate.media.EMOppositeSurfaceView;
import com.hyphenate.util.EMLog;
import app.logic.activity.live.BaseActivity;
import app.yy.geju.R;

@SuppressLint("Registered")
public class CallActivity extends BaseActivity {
    public final static String TAG = "CallActivity";
    protected final int MSG_CALL_MAKE_VIDEO = 0;
    protected final int MSG_CALL_MAKE_VOICE = 1;
    protected final int MSG_CALL_ANSWER = 2;
    protected final int MSG_CALL_REJECT = 3;
    protected final int MSG_CALL_END = 4;
    protected final int MSG_CALL_RLEASE_HANDLER = 5;
    protected final int MSG_CALL_SWITCH_CAMERA = 6;

    protected boolean isInComingCall;
    protected boolean isRefused = false;
    protected String username;
    protected CallingState callingState = CallingState.CANCELLED;
    protected String callDruationText;
    protected String msgid;
    protected AudioManager audioManager;
    protected SoundPool soundPool;
    protected Ringtone ringtone;
    protected int outgoing;
    protected EMCallStateChangeListener callStateListener;
    protected EMLocalSurfaceView localSurface;
    protected EMOppositeSurfaceView oppositeSurface;
    protected boolean isAnswered = false;
    protected int streamID = -1;
    
    EMCallManager.EMCallPushProvider pushProvider;
    
    /**
     * 0：voice call，1：video call
     */
    protected int callType = 0;
    
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE); //AudioManager类提供了访问音量和振铃器mode控制
        pushProvider = new EMCallManager.EMCallPushProvider() {  //这个干嘛用的？ 现在还不理解
            
            void updateMessageText(final EMMessage oldMsg, final String to) {
                // update local message text
                EMConversation conv = EMClient.getInstance().chatManager().getConversation(oldMsg.getTo());
                conv.removeMessage(oldMsg.getMsgId());
            }

            @Override
            public void onRemoteOffline(final String to) {

                //this function should exposed & move to Demo
                EMLog.d(TAG, "onRemoteOffline, to:" + to);
                final EMMessage message = EMMessage.createTxtSendMessage("You have an incoming call", to);
                // set the user-defined extension field
                message.setAttribute("em_apns_ext", true);
                message.setAttribute("is_voice_call", callType == 0 ? true : false);
                message.setMessageStatusCallback(new EMCallBack(){
                    @Override
                    public void onSuccess() {
                        EMLog.d(TAG, "onRemoteOffline success");
                        updateMessageText(message, to);
                    }

                    @Override
                    public void onError(int code, String error) {
                        EMLog.d(TAG, "onRemoteOffline Error");
                        updateMessageText(message, to);
                    }

                    @Override
                    public void onProgress(int progress, String status) {
                    }
                });
                // send messages
                EMClient.getInstance().chatManager().sendMessage(message);
            }
        };
        
        EMClient.getInstance().callManager().setPushProvider(pushProvider);
    }
    
    @Override
    protected void onDestroy() {
        if (soundPool != null)
            soundPool.release();
        if (ringtone != null && ringtone.isPlaying())
            ringtone.stop();
        audioManager.setMode(AudioManager.MODE_NORMAL); //设置音频模式  NORMAL（普通）, RINGTONE（铃声）, orIN_CALL（通话）
        audioManager.setMicrophoneMute(false); //设置是否让麦克风静音
        
        if(callStateListener != null)
            EMClient.getInstance().callManager().removeCallStateChangeListener(callStateListener);  //移除状态监听器
        if (pushProvider != null) {
            EMClient.getInstance().callManager().setPushProvider(null);
            pushProvider = null;
        }
        releaseHandler();  //释放Handler 资源
        super.onDestroy();
    }
    
    @Override
    public void onBackPressed() {
        handler.sendEmptyMessage(MSG_CALL_END);
        saveCallRecord();
        finish();
        super.onBackPressed();
    }
    
    public Runnable timeoutHangup = new Runnable() {
        @Override
        public void run() {
            handler.sendEmptyMessage(MSG_CALL_END);
        }
    };

    HandlerThread callHandlerThread = new HandlerThread("callHandlerThread");
    { callHandlerThread.start(); }

    protected Handler handler = new Handler(callHandlerThread.getLooper()) {
        @Override
        public void handleMessage(Message msg) {
            EMLog.d("EMCallManager CallActivity", "handleMessage ---enter block--- msg.what:" + msg.what);
            switch (msg.what) {
            case MSG_CALL_MAKE_VIDEO:
            case MSG_CALL_MAKE_VOICE:
                try {
                    if (msg.what == MSG_CALL_MAKE_VIDEO) {
                        EMClient.getInstance().callManager().makeVideoCall(username); //发起呼叫，进行视频通话请求，进行视频呼叫前，请先在Activity.onCreate中先执行setSurfaceView。
                    } else { 
                        EMClient.getInstance().callManager().makeVoiceCall(username);  //发起呼叫，进行语音通话请求
                    }
                } catch (final EMServiceNotReadyException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        public void run() {                            
                            String st2 = e.getMessage();
                            if (e.getErrorCode() == EMError.CALL_REMOTE_OFFLINE) {
                                st2 = getResources().getString(R.string.The_other_is_not_online);
                            } else if (e.getErrorCode() == EMError.USER_NOT_LOGIN) {
                                st2 = getResources().getString(R.string.Is_not_yet_connected_to_the_server);
                            } else if (e.getErrorCode() == EMError.INVALID_USER_NAME) {
                                st2 = getResources().getString(R.string.illegal_user_name);
                            } else if (e.getErrorCode() == EMError.CALL_BUSY) {
                                st2 = getResources().getString(R.string.The_other_is_on_the_phone);
                            } else if (e.getErrorCode() == EMError.NETWORK_ERROR) {
                                st2 = getResources().getString(R.string.can_not_connect_chat_server_connection);
                            }
                            Toast.makeText(CallActivity.this, st2, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }
                break;
            case MSG_CALL_ANSWER:  //应答
                EMLog.d(TAG, "MSG_CALL_ANSWER");
                if (ringtone != null)
                    ringtone.stop();
                if (isInComingCall) {  //被呼叫
                    try {
                        EMClient.getInstance().callManager().answerCall();  //接听通话
                        isAnswered = true;
                        // meizu MX5 4G, hasDataConnection(context) return status is incorrect
                        // MX5 con.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected() return false in 4G
                        // so we will not judge it, App can decide whether judge the network status

//                        if (NetUtils.hasDataConnection(CallActivity.this)) {
//                            EMClient.getInstance().callManager().answerCall();
//                            isAnswered = true;
//                        } else {
//                            runOnUiThread(new Runnable() {
//                                public void run() {
//                                    final String st2 = getResources().getString(R.string.Is_not_yet_connected_to_the_server);
//                                    Toast.makeText(CallActivity.this, st2, Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                            throw new Exception();
//                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        saveCallRecord();
                        finish();
                        return;
                    }
                } else {
                    EMLog.d(TAG, "answer call isInComingCall:false");
                }
                break;
            case MSG_CALL_REJECT:  //拒绝
                if (ringtone != null)
                    ringtone.stop();
                try {
                    EMClient.getInstance().callManager().rejectCall();  //拒绝
                } catch (Exception e1) {
                    e1.printStackTrace();
                    saveCallRecord();
                    finish();
                }
                callingState = CallingState.REFUSED;
                break;
            case MSG_CALL_END:  //通话结速
                if (soundPool != null)
                    soundPool.stop(streamID);
                try {
                    EMClient.getInstance().callManager().endCall();  //关闭通话
                } catch (Exception e) {
                    saveCallRecord();
                    finish();
                }
                
                break;
            case MSG_CALL_RLEASE_HANDLER:  //通话结速 ，释放Handler
                try {
                    EMClient.getInstance().callManager().endCall();
                } catch (Exception e) {
                }
                handler.removeCallbacks(timeoutHangup);
                handler.removeMessages(MSG_CALL_MAKE_VIDEO);
                handler.removeMessages(MSG_CALL_MAKE_VOICE);
                handler.removeMessages(MSG_CALL_ANSWER);
                handler.removeMessages(MSG_CALL_REJECT);
                handler.removeMessages(MSG_CALL_END);
                callHandlerThread.quit();
                break;
            case MSG_CALL_SWITCH_CAMERA:  //切换相机
                EMClient.getInstance().callManager().switchCamera();
                break;
            default:
                break;
            }
            EMLog.d("EMCallManager CallActivity", "handleMessage ---exit block--- msg.what:" + msg.what);
        }
    };

    /**
     * 释放  Handler
     */
    void releaseHandler() {
        handler.sendEmptyMessage(MSG_CALL_RLEASE_HANDLER);
    }
    
    /**
     * 播放呼叫的声音
     * play the incoming call ringtone
     */
    protected int playMakeCallSounds() {
        try {
            audioManager.setMode(AudioManager.MODE_RINGTONE); //设置模式
            audioManager.setSpeakerphoneOn(false); //设置是否为扬声器通话

            // play
            int id = soundPool.play(outgoing, // sound resource
                    0.3f, // left volume
                    0.3f, // right volume
                    1,    // priority
                    -1,   // loop，0 is no loop，-1 is loop forever
                    1);   // playback rate (1.0 = normal playback, range 0.5 to 2.0)
            return id;
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     *打开扬声器
     */
    protected void openSpeakerOn() {
        try {
            if (!audioManager.isSpeakerphoneOn()){
                audioManager.setSpeakerphoneOn(true);
            }
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *关闭扬声器
     */
    protected void closeSpeakerOn() {
        try {
            if (audioManager != null) {
                // int curVolume =
                // audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
                if (audioManager.isSpeakerphoneOn())
                    audioManager.setSpeakerphoneOn(false);
                audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                // audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                // curVolume, AudioManager.STREAM_VOICE_CALL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存通话记录
     * save call record
     */
    protected void saveCallRecord() {
        @SuppressWarnings("UnusedAssignment") EMMessage message = null;
        @SuppressWarnings("UnusedAssignment") EMTextMessageBody txtBody = null;
        if (!isInComingCall) { // outgoing call  主动呼叫
            message = EMMessage.createSendMessage(EMMessage.Type.TXT);
            message.setTo(username);
        } else {                                 //被呼叫
            message = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
            message.setFrom(username);
        }

        String st1 = getResources().getString(R.string.call_duration);
        String st2 = getResources().getString(R.string.Refused);
        String st3 = getResources().getString(R.string.The_other_party_has_refused_to);
        String st4 = getResources().getString(R.string.The_other_is_not_online);
        String st5 = getResources().getString(R.string.The_other_is_on_the_phone);
        String st6 = getResources().getString(R.string.The_other_party_did_not_answer);
        String st7 = getResources().getString(R.string.did_not_answer);
        String st8 = getResources().getString(R.string.Has_been_cancelled);
        switch (callingState) {
        case NORMAL:  //正常
            txtBody = new EMTextMessageBody(st1 + callDruationText);  //
            break;
        case REFUSED:  //拒绝
            txtBody = new EMTextMessageBody(st2);
            break;
        case BEREFUSED:   //被拒绝
            txtBody = new EMTextMessageBody(st3);
            break;
        case OFFLINE:  //离线
            txtBody = new EMTextMessageBody(st4);
            break;
        case BUSY:     //忙碌的
            txtBody = new EMTextMessageBody(st5);
            break;
        case NO_RESPONSE: //没有反应
            txtBody = new EMTextMessageBody(st6);
            break;
        case UNANSWERED:  //没有应答
            txtBody = new EMTextMessageBody(st7);
            break;
        case VERSION_NOT_SAME:  //版本不一样
            txtBody = new EMTextMessageBody(getString(R.string.call_version_inconsistent));
            break;
        default:
            txtBody = new EMTextMessageBody(st8);
            break;
        }
        // set message extension
        if(callType == 0)  // 语音信息
            message.setAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, true);
        else
            message.setAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, true);

        // set message body
        message.addBody(txtBody);
        message.setMsgId(msgid);
        message.setStatus(Status.SUCCESS);

        // save
        EMClient.getInstance().chatManager().saveMessage(message);
    }

    /**
     * 呼叫状态
     */
   public enum CallingState {
        CANCELLED, //取消
        NORMAL,    //正常
        REFUSED,   //拒绝
        BEREFUSED, //被拒绝
        UNANSWERED, //没有答案
        OFFLINE,   //离线
        NO_RESPONSE,//没有反应
        BUSY,        //忙碌的
        VERSION_NOT_SAME //版本不一样
    }
}
