package app.utils.service;

import java.io.File;
import java.io.FileInputStream;

import android.app.Service;
/*
 * GZYY    2016-9-5  下午2:42:05
 */
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import app.logic.pojo.UpdataAppInfo;
import app.logic.singleton.ZSZSingleton;
import app.utils.download.thread.AppVersionDownloadThread;
import app.utils.download.thread.AppVersionDownloadThread.DownnloadFileSizeListener;

public class YYDownloadService extends Service {

	public static final String DOWNLOAD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/YYData/download";

	public static final String NEW_VERSIONAPK = "NEW_VERSIONAPK";
	public static final String APP_DOWNLOAD_ACTION = "APP_DOWNLOAD_ACTION";
	public static final String UPDATAINFO = "UPDATAINFO";

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		if (APP_DOWNLOAD_ACTION.equals(intent.getAction())) {
			UpdataAppInfo info = (UpdataAppInfo) intent.getSerializableExtra(UPDATAINFO);
			checkLocalAppFile(info);
		}

		return super.onStartCommand(intent, flags, startId);

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;

	}

	
	private void checkLocalAppFile(final UpdataAppInfo info) {

		// 创建目录下载目录
		File dir = new File(DOWNLOAD_PATH);
		if (!dir.exists()) {
			dir.mkdir();
		}
		// 检查本地文件是否存在
		final File appFile = new File(DOWNLOAD_PATH + "/" + info.getApp_name() + ".apk");
		if (appFile.exists()) {
			appFile.delete();
		}
		Handler handler = new Handler();
		handler.post(new Runnable() {

			@Override
			public void run() {
				AppVersionDownloadThread downloadThread = new AppVersionDownloadThread(YYDownloadService.this, info);
				downloadThread.start();

			}
		});

	}

}
