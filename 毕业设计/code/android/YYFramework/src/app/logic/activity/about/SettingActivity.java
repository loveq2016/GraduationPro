package app.logic.activity.about;

import java.io.File;
import java.util.List;
import java.util.Random;

import org.ql.app.alert.AlertDialog;
import org.ql.utils.QLToastUtils;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import app.logic.activity.ActTitleHandler;
import app.logic.activity.InitActActivity;
import app.logic.activity.launch.LaunchActivity;
import app.logic.activity.user.AboutMeActivity;
import app.logic.controller.UpdataController;
import app.logic.pojo.UpdataAppInfo;
import app.logic.singleton.ZSZSingleton;
import app.logic.singleton.ZSZSingleton.UpdataDownloadProgressListener;
import app.utils.common.Listener;
import app.utils.download.thread.AppVersionDownloadThread;
import app.utils.helpers.SystemBuilderUtils;
import app.view.DialogNewStyleController;
import app.yy.geju.R;

/*
 * GZYY    2016-10-25  上午9:04:00
 */

public class SettingActivity extends InitActActivity implements OnClickListener {
	public static final String DOWNLOAD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/YYData/download";

	private ActTitleHandler mHandler;
	private RelativeLayout aboutMeLayout, appUpdataLayout, helpAndFeedbackLayout;
	private TextView app_codeNameTv;
	private ImageView appUpdataStatusIv;

	private UpdataAppInfo updataAppInfo;
	private DialogNewStyleController appUpDataDialog ;
	private ProgressBar progressBar ;
	private Button sendBtn , cancel ;
	private View appUpDataView ;
	private TextView tagEdt , titleTv ;

	@Override
	protected void initActTitleView() {
		mHandler = new ActTitleHandler();
		setAbsHandler(mHandler);
	}

	@Override
	protected void initView(Bundle savedInstanceState) {
		setContentView(R.layout.activity_setting);
		setTitle("");
		mHandler.replaseLeftLayout(this, true);
		mHandler.getLeftLayout().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		((TextView) mHandler.getLeftLayout().findViewById(R.id.left_tv)).setText("设置");

		aboutMeLayout = (RelativeLayout) findViewById(R.id.setting_about_me);
		appUpdataLayout = (RelativeLayout) findViewById(R.id.setting_app_updata);
		helpAndFeedbackLayout = (RelativeLayout) findViewById(R.id.setting_help_feedback);
		appUpdataStatusIv = (ImageView) findViewById(R.id.app_updataStatusIv);
		app_codeNameTv = (TextView) findViewById(R.id.app_codeName_tv);

		aboutMeLayout.setOnClickListener(this);
		appUpdataLayout.setOnClickListener(this);
		helpAndFeedbackLayout.setOnClickListener(this);

		intiAppUpDataView() ; //初始化对话框

	}

	@Override
	protected void initData() {
		app_codeNameTv.setText("V" + SystemBuilderUtils.getInstance().getAppVersionName(this));
		checkUpdataApp();
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

//				int versionCode = SystemBuilderUtils.getInstance().getAppVersionCode(SettingActivity.this);
//				if (versionCode == -1) {
//					return;
//				}
//				int newVersionCode = Integer.parseInt(info.getApp_version());
//				if (versionCode < newVersionCode) {
//					// showUpdataApp(versionCode, info);
//					updataAppInfo = info;
//					appUpdataStatusIv.setVisibility(View.VISIBLE);
//				}

				String versionName = SystemBuilderUtils.getInstance().getAppVersionName(SettingActivity.this);//当前应用的版本名称
				if( null == versionName || TextUtils.isEmpty(versionName)){
					return;
				}
				String newversionName = info.getApp_version();
				versionName = versionName.replace(".","0");       //将 “.” 换成 “0” （ 以后有三个 . 这个也适合判断 ）
				newversionName = newversionName.replace(".","0");

				if(Long.parseLong(newversionName) > Long.parseLong(versionName)){  //有新版本
					updataAppInfo = info;
					appUpdataStatusIv.setVisibility(View.VISIBLE);
				}else{
					appUpdataStatusIv.setVisibility(View.GONE);
				}
			}
		});
	}

	/**
	 * 显示APP更新的对话框
	 * @param oldVersionCode
	 * @param info
	 */
	private void showUpdataApp(String oldVersionCode, final UpdataAppInfo info) {

//		View view = LayoutInflater.from(this).inflate(R.layout.app_updata_view, null);
//		appUpDataDialog = new DialogNewStyleController(this, view);
//		TextView titleTv = (TextView) view.findViewById(R.id.dialog_title_tv);
//		TextView tagEdt = (TextView) view.findViewById(R.id.dialog_tag_edt);
//		Button sendBtn = (Button) view.findViewById(R.id.dialog_cancel_btn);
//		final Button cancel = (Button) view.findViewById(R.id.dialog_true_btn);
//		titleTv.setText("版本更新");
//		sendBtn.setText("更新");
//		cancel.setText("取消");

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
				//appUpDataDialog.dismiss();
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
				AppVersionDownloadThread downloadThread = new AppVersionDownloadThread(SettingActivity.this, info);
				downloadThread.start();
			}
		});
	}

	// 通知消息下载进度
	private void updataDownloadProgress() {
		progressBar.setVisibility( View.VISIBLE);
		final NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
		builder.setContentTitle("格局新版本下载").setContentText("下载进度").setSmallIcon(R.drawable.ic_launcher);

		ZSZSingleton.getZSZSingleton().setUpdataDownloadProgressListener(new UpdataDownloadProgressListener() {

			@Override
			public void onCallBack(final int plan) {
				if (plan < 100) {
					SettingActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							progressBar.setProgress(plan);
						}
					});
					builder.setProgress(100, plan, false);
					builder.setAutoCancel(true);
					manager.notify(100, builder.build());
				} else {
					SettingActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							//progressBar.setVisibility(View.GONE);
							appUpDataDialog.dismiss();  //下载完成对话框消失
						}
					});
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
		case R.id.setting_about_me:
			startActivity(new Intent(SettingActivity.this, AboutYueyunActivity.class));
			break;
		case R.id.setting_app_updata:
			if (updataAppInfo != null) {
				//showUpdataApp(SystemBuilderUtils.getInstance().getAppVersionCode(SettingActivity.this), updataAppInfo);
				showUpdataApp(SystemBuilderUtils.getInstance().getAppVersionName(SettingActivity.this), updataAppInfo);
			} else {
				QLToastUtils.showToast(SettingActivity.this, "当前已经是最新版本了");
			}
			break;
		case R.id.setting_help_feedback:
			startActivity(new Intent(SettingActivity.this, HelpAndFeedbackActivity.class));
			break;
		default:
			break;
		}
	}

	/**
	 * 初始化对话框
	 */
	private void intiAppUpDataView(){
		appUpDataView = LayoutInflater.from(this).inflate(R.layout.app_updata_view, null);
		appUpDataDialog = new DialogNewStyleController(this, appUpDataView);
		progressBar = (ProgressBar) appUpDataView.findViewById( R.id.progressbar);
		titleTv = (TextView) appUpDataView.findViewById(R.id.dialog_title_tv);
		tagEdt = (TextView) appUpDataView.findViewById(R.id.dialog_tag_edt);
		sendBtn = (Button) appUpDataView.findViewById(R.id.dialog_cancel_btn);
		cancel = (Button) appUpDataView.findViewById(R.id.dialog_true_btn);
		titleTv.setText("版本更新");
		sendBtn.setText("更新");
		cancel.setText("取消");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if( null != appUpDataDialog && appUpDataDialog.isShowing()){
			appUpDataDialog.dismiss();
		}
	}
}
