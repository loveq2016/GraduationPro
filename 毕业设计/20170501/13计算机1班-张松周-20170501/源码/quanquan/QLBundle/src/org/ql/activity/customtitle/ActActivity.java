package org.ql.activity.customtitle;

import java.util.zip.Inflater;

import org.ql.app.alert.AlertDialog;
import org.ql.bundle.R;
import org.ql.utils.debug.UmengInterface.DebugCountListener;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class ActActivity extends Activitys {

	protected boolean hidetitle = false;
	protected View loadingView ,statusView ;
	protected AlertDialog waitingAlertDialog;
	private String waitingDialogText;

	private DebugCountListener listener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(R.style.act__theme);
	}

	@Override
	public void setScreen(int... screen) {
		super.setScreen(screen);
		screen = Screen.filterRepeat(screen);
		for (int screenValue : screen) {
			switch (screenValue) {
			case Screen.FEATURE_NO_TITLE:
				hidetitle = true;
				break;
			}
		}
	}

	protected AbsHandler absHandler;

	public AbsHandler getAbsHandler() {
		return absHandler;
	}

	public void setAbsHandler(AbsHandler absHandler) {
		this.absHandler = absHandler;
	}

	@Override
	public void setContentView(int layoutResID) {
		if (!hidetitle && absHandler != null)
			requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.setContentView(layoutResID);

		if (!hidetitle && absHandler != null) {
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, absHandler.getLayout());
			absHandler.onCreate(this);
			absHandler.setTitle(super.getTitle());
			// 设置系统标栏颜色
			initSystemBarTitle();
		}
	}

	@Override
	public void setContentView(View view, LayoutParams params) {
		if (!hidetitle && absHandler != null)
			requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.setContentView(view, params);
		if (!hidetitle && absHandler != null) {
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, absHandler.getLayout());
			absHandler.onCreate(this);
			absHandler.setTitle(super.getTitle());
		}
	}

	@Override
	public void setContentView(View view) {
		if (!hidetitle && absHandler != null)
			requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.setContentView(view);
		if (!hidetitle && absHandler != null) {
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, absHandler.getLayout());
			absHandler.onCreate(this);
			absHandler.setTitle(super.getTitle());
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		super.setTitle(title);
		if (!hidetitle && absHandler != null) {
			absHandler.setTitle(title);
		}
	}

	@Override
	public void setTitle(int stringId) {
		super.setTitle(stringId);
		if (!hidetitle && absHandler != null) {
			absHandler.setTitle(stringId);
		}
	}

	@Override
	public void setTitleColor(int colorId) {
		super.setTitleColor(colorId);
		if (!hidetitle && absHandler != null) {
			absHandler.setTitleColor(colorId);
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (!hidetitle && absHandler != null) {
			absHandler.onWindowFocusChanged(hasFocus);
		}
	}

	public void initSystemBarTitle() {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			ViewGroup firstChildAtDecorView = ((ViewGroup) ((ViewGroup) getWindow().getDecorView()).getChildAt(0));
			statusView = new View(this);
			ViewGroup.LayoutParams statusViewLp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(this));
			// 颜色的设置可抽取出来让子类实现之
			statusView.setBackgroundColor(getResources().getColor(R.color.acttitle2_bg));
			firstChildAtDecorView.addView(statusView, 0, statusViewLp);
		}
	}

	// 获取状态栏的高度
	private int getStatusBarHeight(Context context) {
		int statusBarHeight = 0;
		Resources resources = context.getResources();
		int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			statusBarHeight = resources.getDimensionPixelSize(resourceId);
		}
		return statusBarHeight;
	}

	protected void createLoadingView() {
		if (loadingView == null) {
			loadingView = LayoutInflater.from(this).inflate(R.layout.loading_progress, null);
		}
		if (waitingDialogText != null) {
			TextView tView = (TextView) loadingView.findViewById(R.id.base_progress_txt);
			tView.setText(waitingDialogText);
		} else {
			TextView tView = (TextView) loadingView.findViewById(R.id.base_progress_txt);
			tView.setText("处理中,请稍后...");
		}
	}

	public void setWaitingDialogText(String txt) {
		waitingDialogText = txt;
		if (loadingView != null) {
			TextView tView = (TextView) loadingView.findViewById(R.id.base_progress_txt);
			tView.setText(waitingDialogText);
		}
	}

	public synchronized void showWaitDialog() {
		createLoadingView();
		if (waitingAlertDialog == null) {
			AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
			waitingAlertDialog = alertBuilder.create();
		}
		if (waitingAlertDialog.isShowing()) {
			return;
		}
		waitingAlertDialog.setCanceledOnTouchOutside(false);
		waitingAlertDialog.setCancelable(true);
		waitingAlertDialog.setView(loadingView);
		waitingAlertDialog.show();
		// loadingView.setVisibility(View.VISIBLE);
	}

	public synchronized void dismissWaitDialog() {
		if (waitingAlertDialog == null || !waitingAlertDialog.isShowing()) {
			return;
		}
		waitingAlertDialog.cancel();
		// loadingView.setVisibility(View.GONE);
	}

	/**
	 * 
	 * @param listener
	 *            setDebugCountListenerActActivity
	 */
	public void setDebugCountListener(DebugCountListener listener) {
		this.listener = listener;
	}

}
