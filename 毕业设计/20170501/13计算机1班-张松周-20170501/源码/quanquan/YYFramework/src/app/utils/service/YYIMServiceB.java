package app.utils.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.model.EaseNotifier;

import org.ql.utils.debug.QLLog;

import java.util.List;

/**
 * Created by dyj on 2017/3/20.
 */

public class YYIMServiceB extends Service implements EMMessageListener{


    EaseNotifier notifier;
    BroadcastReceiver mBR;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setIMSettings();
        QLLog.i("====","setIMSettings=========onCreateB========");
        mBR = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO Auto-generated method stub
                Intent a = new Intent(YYIMServiceB.this, YYIMService.class);
                startService(a);
            }
        };
        IntentFilter mIF = new IntentFilter();
        mIF.addAction("listenerB");
        registerReceiver(mBR, mIF);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        setIMSettings();
        QLLog.i("====","setIMSettings=======onStartB==========");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        QLLog.i("====","setIMSettings=======onStartCommandB==========");
//        startForeground(1,null);
//        return START_STICKY_COMPATIBILITY;
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        QLLog.i("====","setIMSettings=======onDestroyB==========");
        Intent intent = new Intent();
        intent.setAction("listenerA");
        sendBroadcast(intent);

        unregisterReceiver(mBR);
    }

    private void setIMSettings(){
        EMClient.getInstance().chatManager().removeMessageListener(this);
        EMClient.getInstance().chatManager().addMessageListener(this);
        notifier = new EaseNotifier();
        notifier.init(getApplicationContext());
        QLLog.i("====","setIMSettingsB=================");
    }


    private void sendNotification(List<EMMessage> emMessages){
        notifier.onNewMesg(emMessages);
    }

    @Override
    public void onMessageReceived(List<EMMessage> list) {
        sendNotification(list);
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> list) {

    }

    @Override
    public void onMessageRead(List<EMMessage> list) {

    }

    @Override
    public void onMessageDelivered(List<EMMessage> list) {

    }

    @Override
    public void onMessageChanged(EMMessage emMessage, Object o) {

    }
}
