package app.utils.service;

import java.util.List;

import org.ql.utils.debug.QLLog;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScreenStatuReciver extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		ActivityManager manager=(ActivityManager)arg0.getSystemService(Activity.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> mServiceList = manager.getRunningServices(100);
		String myServiceName = DateTimeService.class.getName();
		boolean isServiceRunning = false;
		for (RunningServiceInfo runningServiceInfo : mServiceList) {
			if (myServiceName.equals(runningServiceInfo.service.getClassName())) {
				isServiceRunning = true;
				break;
			}
		}
		if (!isServiceRunning) {
			Intent intent=new Intent(arg0, DateTimeService.class);
			arg0.startService(intent);
			QLLog.i("重启服务", "重启服务成功!");
		}
		
	}

}
