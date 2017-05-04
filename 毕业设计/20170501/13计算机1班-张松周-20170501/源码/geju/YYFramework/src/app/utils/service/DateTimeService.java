package app.utils.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;

public class DateTimeService extends Service {

	private static final int SYNDATETIME_PENDINGINTENT_REQUEST_CODE = 12254;
	private static final long NEXT_DELAY_TIME = 3*60*1000;
	private boolean isStart = false;
	private AlarmManager alarmManager;
	private PendingIntent pendingIntent;
	
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	public void onCreate(){
		super.onCreate();
		
	}
	
	@Override 
    //当调用者使用startService()方法启动Service时，该方法被调用  
    public void onStart(Intent intent, int startId)  
    {  
        super.onStart(intent, startId);
        startTask();
    }  
	
	public void onDestroy() {
		isStart = false;
		if (alarmManager != null && pendingIntent != null) {
			alarmManager.cancel(pendingIntent);
		}
		super.onDestroy();
	}

	private void startTask(){
		Intent broadIntent = new Intent(getApplicationContext(), DateTimeSynModel.class);
		pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), DateTimeService.SYNDATETIME_PENDINGINTENT_REQUEST_CODE, broadIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);	
		long startTime = SystemClock.elapsedRealtime() + 2 * 1000;
		alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,startTime, NEXT_DELAY_TIME, pendingIntent);
	}
	
}
