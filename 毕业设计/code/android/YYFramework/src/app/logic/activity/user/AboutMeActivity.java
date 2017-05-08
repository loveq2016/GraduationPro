package app.logic.activity.user;

import java.io.File;
import java.util.List;

import org.ql.app.alert.AlertDialog;
import org.ql.utils.QLToastUtils;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import app.logic.activity.ActTitleHandler;
import app.logic.activity.InitActActivity;
import app.logic.activity.about.SettingActivity;
import app.logic.controller.UpdataController;
import app.logic.pojo.UpdataAppInfo;
import app.logic.singleton.ZSZSingleton;
import app.logic.singleton.ZSZSingleton.StatusDownloadFileCompleteListener;
import app.logic.singleton.ZSZSingleton.UpdataDownloadProgressListener;
import app.utils.common.Listener;
import app.utils.download.thread.AppVersionDownloadThread;
import app.utils.helpers.SystemBuilderUtils;
import app.view.DialogNewStyleController;
import app.yy.geju.R;

/*
 * GZYY    2016-10-20  下午4:52:47
 */

public class AboutMeActivity extends InitActActivity implements OnClickListener {
	public static final String DOWNLOAD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/YYData/download";

	private ActTitleHandler titleHandler;
	private TextView app_PackbackName;
	private ImageView app_UpdataStatus;

	private UpdataAppInfo updataAppInfo;
	private DialogNewStyleController appUpDataDialog ;

	@Override
	protected void initActTitleView() {
		titleHandler = new ActTitleHandler();
		setAbsHandler(titleHandler);
	}

	@Override
	protected void initView(Bundle savedInstanceState) {
		setContentView(R.layout.activity_about_me);
		setTitle("关于");
		titleHandler.replaseLeftLayout(this, true);
		titleHandler.getLeftLayout().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		app_PackbackName = (TextView) findViewById(R.id.app_packbackNameTV);
		app_UpdataStatus = (ImageView) findViewById(R.id.app_updataStatusIv);

		findViewById(R.id.app_Updata_layout).setOnClickListener(this);

	}

	@Override
	protected void initData() {

		app_PackbackName.setText("Android for V:" + String.valueOf(SystemBuilderUtils.getInstance().getAppVersionName(AboutMeActivity.this)));

		checkUpdataApp();
		updataApp();

	}

	/*
	 * 检查版本更新
	 */
	private void checkUpdataApp() {
		// 开始检查网络版本
		UpdataController.getAppVersion(this, new Listener<Void, List<UpdataAppInfo>>() {
			@Override
			public void onCallBack(Void status, List<UpdataAppInfo> reply) {
				if (reply == null || reply.isEmpty()) {
					return;
				}
				try{
					UpdataAppInfo info = reply.get(0);
					int currVersionCode = SystemBuilderUtils.getInstance().getAppVersionCode(AboutMeActivity.this);
					String newversionName = info.getApp_version();
					int latestVersion = Integer.parseInt(newversionName);
					if (latestVersion > currVersionCode){
						updataAppInfo = info;
						app_UpdataStatus.setVisibility(View.VISIBLE);
					}
				}catch (Exception e){
					e.printStackTrace();
				}

			}
		});

	}

//	private void showUpdataApp(int oldVersionCode, final UpdataAppInfo info) {
//
//		final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
//		alertDialog.setIcon(0);
//		alertDialog.setTitle(info.getApp_update_msg());
//
//		View view = LayoutInflater.from(this).inflate(R.layout.dialog_updata_app_layout, null);
//		alertDialog.setView(view);
//		TextView message_tv = (TextView) view.findViewById(R.id.message_tv);
//		message_tv.setText("当前版本为" + String.valueOf(oldVersionCode) + "，检测到的最新版本为" + String.valueOf(info.getApp_version()) + ",是否要更新？？");
//		Button yes_btn = (Button) view.findViewById(R.id.yes_btn);
//		final Button no_btn = (Button) view.findViewById(R.id.no_btn);
//
//		yes_btn.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				checkLocationFile(info);
//				alertDialog.dismiss();
//			}
//		});
//		no_btn.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if (info.getApp_udpate_type() == 1) {
//					no_btn.setTextColor(getResources().getColor(R.color.line_bg));
//					return;
//				}
//				alertDialog.dismiss();
//
//			}
//		});
//
//		alertDialog.show();
//	}

	private void showUpdataApp(String oldVersionCode, final UpdataAppInfo info) {

		View view = LayoutInflater.from(this).inflate(R.layout.app_updata_view, null);
		appUpDataDialog = new DialogNewStyleController(this, view);
		TextView titleTv = (TextView) view.findViewById(R.id.dialog_title_tv);
		TextView tagEdt = (TextView) view.findViewById(R.id.dialog_tag_edt);
		Button sendBtn = (Button) view.findViewById(R.id.dialog_cancel_btn);
		final Button cancel = (Button) view.findViewById(R.id.dialog_true_btn);
		titleTv.setText("版本更新");
		sendBtn.setText("更新");
		cancel.setText("取消");

//        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
//        alertDialog.setIcon(0);
//        alertDialog.setTitle(info.getApp_update_msg());
//        View view = LayoutInflater.from(LaunchActivity.this).inflate(R.layout.dialog_updata_app_layout, null);
//        alertDialog.setView(view);
//        TextView message_tv = (TextView) view.findViewById(R.id.message_tv);
//        message_tv.setText("当前版本为" + String.valueOf(oldVersionCode) + "，检测到的最新版本为" + String.valueOf(info.getApp_version()) + ",是否要更新？？");
//        message_tv.setText("当前版本为" + oldVersionCode + "，检测到的最新版本为" + String.valueOf(info.getApp_version()) + ",是否要更新？");
//        Button yes_btn = (Button) view.findViewById(R.id.yes_btn);
//        final Button no_btn = (Button) view.findViewById(R.id.no_btn);
		tagEdt.setText("当前版本为" + oldVersionCode + "，检测到的最新版本为" + String.valueOf(info.getApp_version()) + ",是否要更新？");
		sendBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				checkLocationFile(info);
				appUpDataDialog.dismiss();
			}
		});
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (info.getApp_udpate_type() == 1) {
					cancel.setTextColor(getResources().getColor(R.color.line_bg));
					return;
				}
				appUpDataDialog.dismiss();
			}
		});
		appUpDataDialog.show();

//		final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
//		alertDialog.setIcon(0);
//		alertDialog.setTitle(info.getApp_update_msg());
//
//		View view = LayoutInflater.from(this).inflate(R.layout.dialog_updata_app_layout, null);
//		alertDialog.setView(view);
//		TextView message_tv = (TextView) view.findViewById(R.id.message_tv);
//		//message_tv.setText("当前版本为" + String.valueOf(oldVersionCode) + "，检测到的最新版本为" + String.valueOf(info.getApp_version()) + ",是否要更新？？");
//		message_tv.setText("当前版本为" + oldVersionCode + "，检测到的最新版本为" + String.valueOf(info.getApp_version()) + ",是否要更新？？");
//		Button yes_btn = (Button) view.findViewById(R.id.yes_btn);
//		final Button no_btn = (Button) view.findViewById(R.id.no_btn);
//
//		yes_btn.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				checkLocationFile(info);
//				alertDialog.dismiss();
//			}
//		});
//		no_btn.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if (info.getApp_udpate_type() == 1) {
//					no_btn.setTextColor(getResources().getColor(R.color.line_bg));
//					return;
//				}
//				alertDialog.dismiss();
//
//			}
//		});
//
//		alertDialog.show();
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
				AppVersionDownloadThread downloadThread = new AppVersionDownloadThread(AboutMeActivity.this, info);
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

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.app_Updata_layout:
			if (updataAppInfo != null) {
				//showUpdataApp(SystemBuilderUtils.getInstance().getAppVersionCode(AboutMeActivity.this), updataAppInfo);
				showUpdataApp(SystemBuilderUtils.getInstance().getAppVersionName(AboutMeActivity.this), updataAppInfo);
			} else {
				QLToastUtils.showToast(AboutMeActivity.this, "当前已经是最新版本了");
			}
			break;

		default:
			break;
		}
	}

}
