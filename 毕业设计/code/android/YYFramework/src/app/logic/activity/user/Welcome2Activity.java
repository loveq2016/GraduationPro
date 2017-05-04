package app.logic.activity.user;

import java.io.File;
import java.util.List;
import org.ql.activity.customtitle.ActActivity;
import org.ql.app.alert.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import app.logic.activity.about.SettingActivity;
import app.logic.controller.UpdataController;
import app.logic.pojo.ChatMessageInfo;
import app.logic.pojo.UpdataAppInfo;
import app.logic.singleton.ZSZSingleton;
import app.logic.singleton.ZSZSingleton.StatusDownloadFileCompleteListener;
import app.logic.singleton.ZSZSingleton.UpdataDownloadProgressListener;
import app.utils.common.Listener;
import app.utils.download.thread.AppVersionDownloadThread;
import app.utils.helpers.SharepreferencesUtils;
import app.utils.helpers.SystemBuilderUtils;
import app.yy.geju.R;
import cn.jpush.android.api.JPushInterface;

/*
 * zhangsongzhou    2016-7-27  上午11:24:27
 */

public class Welcome2Activity extends ActActivity {
	public static final String DOWNLOAD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/YYData/download";
	private SharepreferencesUtils utils;
	private ChatMessageInfo info;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//JPushInterface.setAlias(this, "" , null);  //设置极光推送的别名（设为""）
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_welcome2);
		utils = new SharepreferencesUtils(this);
		findViewById(R.id.login_btn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(Welcome2Activity.this, LoginActivity.class);
				startActivity(intent);
				finish();
			}
		});
		findViewById(R.id.logon_btn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(Welcome2Activity.this, RegActivity.class);
				startActivity(intent);
			}
		});
		checkUpdataApp();
		updataApp();
		info = (ChatMessageInfo) getIntent().getSerializableExtra("info");
		autoLogin();
	}

	// 自动登录
	private void autoLogin() {
		String userName = utils.getUserName();
		String password = utils.getPassword();
		if (userName != null && !TextUtils.isEmpty(userName) && password != null && !TextUtils.isEmpty(password)) {
			Handler handler = new Handler();
			handler.postAtTime(new Runnable() {

				@Override
				public void run() {
					if (info == null) {
						startActivity(new Intent(Welcome2Activity.this, LoginActivity.class));
						finish();
					} else {
						startActivity(new Intent(Welcome2Activity.this, LoginActivity.class).putExtra("info", info));
						finish();
					}
				}
			}, 2000);
		}
	}

	/*
	 * 检查版本更新
	 */
	private void checkUpdataApp() {
		// 开始检查网络版本
		UpdataController.getAppVersion(this, new Listener<Void, List<UpdataAppInfo>>() {
			@Override
			public void onCallBack(Void status, List<UpdataAppInfo> reply) {
				if (reply == null || reply.size() < 1) {
					return;
				}
				UpdataAppInfo info = reply.get(0);
//				int versionCode = SystemBuilderUtils.getInstance().getAppVersionCode(Welcome2Activity.this);
//				if (versionCode == -1) {
//					return;
//				}
//				if (versionCode < Integer.parseInt(info.getApp_version())) {
//					showUpdataApp(versionCode, info);
//				}

				String versionName = SystemBuilderUtils.getInstance().getAppVersionName(Welcome2Activity.this);//当前应用的版本名称
				if( null == versionName || TextUtils.isEmpty(versionName)){
					return;
				}
				String newversionName = info.getApp_version();
				if(!versionName.equals(newversionName)){
					showUpdataApp(versionName, info);
				}
			}
		});
	}

	private void showUpdataApp(String oldVersionCode, final UpdataAppInfo info) {

		final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setIcon(0);
		alertDialog.setTitle(info.getApp_update_msg());

		View view = LayoutInflater.from(Welcome2Activity.this).inflate(R.layout.dialog_updata_app_layout, null);
		alertDialog.setView(view);
		TextView message_tv = (TextView) view.findViewById(R.id.message_tv);
		//message_tv.setText("当前版本为" + String.valueOf(oldVersionCode) + "，检测到的最新版本为" + String.valueOf(info.getApp_version()) + ",是否要更新？？");
		message_tv.setText("当前版本为" + oldVersionCode + "，检测到的最新版本为" + String.valueOf(info.getApp_version()) + ",是否要更新？？");
		Button yes_btn = (Button) view.findViewById(R.id.yes_btn);
		final Button no_btn = (Button) view.findViewById(R.id.no_btn);

		yes_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				checkLocationFile(info);
				alertDialog.dismiss();
			}
		});
		no_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (info.getApp_udpate_type() == 1) {
					no_btn.setTextColor(getResources().getColor(R.color.line_bg));
					return;
				}
				alertDialog.dismiss();

			}
		});

		alertDialog.show();
	}

	// 检查本地文件
	private void checkLocationFile(final UpdataAppInfo info) {
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
		updataDownloadProgress();

		Handler handler = new Handler();
		handler.post(new Runnable() {

			@Override
			public void run() {
				AppVersionDownloadThread downloadThread = new AppVersionDownloadThread(Welcome2Activity.this, info);
				downloadThread.start();

			}
		});
	}

	// 回调最新app下载完成后,打开apk
	private void updataApp() {

		ZSZSingleton.getZSZSingleton().setStatusDownloadFileCompleteListener(new StatusDownloadFileCompleteListener() {

			@Override
			public void onCallBack(String url) {
				if (url == null || TextUtils.isEmpty(url)) {
					return;
				}
				if (ZSZSingleton.getZSZSingleton().getHaveComplete() > 0) {
					return;
				}
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.fromFile(new File(url)), "application/vnd.android.package-archive");
				startActivity(intent);
				ZSZSingleton.getZSZSingleton().setHaveComplete(1);

			}
		});
	}

	// 通知消息下载进度
	private void updataDownloadProgress() {
		final NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
		builder.setContentTitle("格局新版本下载").setContentText("下载进度").setSmallIcon(R.drawable.ic_launcher);

		ZSZSingleton.getZSZSingleton().setUpdataDownloadProgressListener(new UpdataDownloadProgressListener() {

			@Override
			public void onCallBack(int plan) {
				if (plan < 100) {
					builder.setProgress(100, plan, false);
					builder.setAutoCancel(true);
					manager.notify(100, builder.build());
				} else {
					builder.setContentText("下载完成").setProgress(0, 0, true);
					manager.notify(100, builder.build());
					manager.cancel(100);
				}

			}
		});

	}
}
