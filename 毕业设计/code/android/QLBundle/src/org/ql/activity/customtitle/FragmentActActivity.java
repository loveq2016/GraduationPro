package org.ql.activity.customtitle;

import org.ql.app.alert.AlertDialog;
import org.ql.bundle.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import android.view.Window;

public class FragmentActActivity extends FragmentActivitys {
	
	protected boolean hidetitle = false;
	protected AlertDialog waitingAlertDialog;
	protected View loadingView;
	private String waitingDialogText;
	
	/**修复
	 * java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
	 * */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    //No call for super(). Bug on API Level > 11.
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(R.style.act__theme);
	}
	
	@Override
	public void setScreen(int... screen) {
		super.setScreen(screen);
		screen = Screen.filterRepeat(screen);
		for(int screenValue : screen){
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
		if(!hidetitle && absHandler != null)
			requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.setContentView(layoutResID);
		if(!hidetitle && absHandler != null){
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,absHandler.getLayout());
			absHandler.onCreate(this);
			absHandler.setTitle(super.getTitle());
		}
	}

	@Override
	public void setContentView(View view, LayoutParams params) {
		if(!hidetitle && absHandler != null)
			requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.setContentView(view, params);
		if(!hidetitle && absHandler != null){
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,absHandler.getLayout());
			absHandler.onCreate(this);
			absHandler.setTitle(super.getTitle());
		}
	}

	@Override
	public void setContentView(View view) {
		if(!hidetitle && absHandler != null)
			requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.setContentView(view);
		if(!hidetitle && absHandler != null){
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,absHandler.getLayout());
			absHandler.onCreate(this);
			absHandler.setTitle(super.getTitle());
		}
	}
	
	@Override
	public void setTitle(CharSequence title) {
		super.setTitle(title);
		if(!hidetitle && absHandler != null){
			absHandler.setTitle(title);
		}
	}

	@Override
	public void setTitle(int stringId) {
		super.setTitle(stringId);
		if(!hidetitle && absHandler != null){
			absHandler.setTitle(stringId);
		}
	}

	@Override
	public void setTitleColor(int colorId) {
		super.setTitleColor(colorId);
		if(!hidetitle && absHandler != null){
			absHandler.setTitleColor(colorId);
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if(!hidetitle && absHandler != null){
			absHandler.onWindowFocusChanged(hasFocus);
		}
	}
	
	protected void createLoadingView(){
		if (loadingView == null) {
			loadingView = LayoutInflater.from(this).inflate(R.layout.loading_progress, null);
		}
		if (waitingDialogText != null) {
			TextView tView = (TextView)loadingView.findViewById(R.id.base_progress_txt);
			tView.setText(waitingDialogText);
		}else {
			TextView tView = (TextView)loadingView.findViewById(R.id.base_progress_txt);
			tView.setText("处理中,请稍后...");
		}
	}
	
	public void setWaitingDialogText(String txt){
		waitingDialogText = txt;
		if (loadingView != null) {
			TextView tView = (TextView)loadingView.findViewById(R.id.base_progress_txt);
			tView.setText(waitingDialogText);
		}
	}
	
	public synchronized void showWaitDialog(){
		createLoadingView();
		if (waitingAlertDialog == null) {
			AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
			waitingAlertDialog = alertBuilder.create();
		}
		if ( waitingAlertDialog.isShowing() ) {
			return;
		}
		waitingAlertDialog.setCanceledOnTouchOutside(false);
		waitingAlertDialog.setCancelable(true);
		waitingAlertDialog.setView(loadingView);
		waitingAlertDialog.show();
//		loadingView.setVisibility(View.VISIBLE);
	}
	
	public synchronized void dismissWaitDialog(){
		if (waitingAlertDialog == null || !waitingAlertDialog.isShowing()) {
			return;
		}
		waitingAlertDialog.cancel();
//		loadingView.setVisibility(View.GONE);
	}
}
