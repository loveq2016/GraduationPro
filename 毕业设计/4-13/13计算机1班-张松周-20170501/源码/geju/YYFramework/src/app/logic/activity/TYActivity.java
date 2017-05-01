package app.logic.activity;

import org.ql.app.alert.AlertDialog;
import org.ql.utils.QLToastUtils;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import app.logic.activity.user.LoginActivity;
import app.utils.customview.AppDialog;
import app.yy.geju.R;

public abstract class TYActivity extends Activity {

	/**
	 * 如果设置父Activity Intent,那么当前Activity结束后返回改Activity
	 */
	public static final String INTENT_INFO_OF_PAREN_ACTIVITY = "INTENT_INFO_PAREN_ACTIVITY";
	protected static final String LEFT_BTN_TAG = "LEFT_BTN_TAG";
	protected static final String RIGHT_BTN_TAG = "RIGHT_BTN_TAG";
	
	protected View loadingMask;
	protected View btnLeft;
	protected View btnRight;
	protected TextView titleView;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		this.initTitleView();
	}

	@Override
	public void setContentView(View view, LayoutParams params) {
		super.setContentView(view, params);
		this.initTitleView();
	}

	@Override
	public void setContentView(View view) {
		super.setContentView(view);
		this.initTitleView();
	}
	
	public void showLeftButton(boolean show){
		if (btnLeft != null) {
			int visibility = show? View.VISIBLE :View.INVISIBLE;
			btnLeft.setVisibility(visibility);
		}
	}
	
	public void showRightButton(boolean show){
		if (btnRight != null) {
			int visibility = show? View.VISIBLE :View.INVISIBLE;
			btnRight.setVisibility(visibility);
		}
	}
	
	public void setTitle(String title){
		super.setTitle(title);
		if (titleView != null) {
			titleView.setText(title);
		}
	}
	
	public void initTitleView() {
		btnLeft = findViewById(R.id.btn_title_left);
		btnRight = findViewById(R.id.btn_title_right);
		loadingMask = findViewById(R.id.mask_loadding);
		titleView = (TextView)findViewById(android.R.id.title);
		if(null!=loadingMask){
			this.hideLoadingMask();
			loadingMask.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					return true;
				}
			});
		}
	}
	
	public void setLeftButtonImage(int res){
		if (btnLeft != null) {
			btnLeft.setBackgroundResource(res);
		}
	}
	
	public void setRightButtonBackgroud(int res){
		if (btnRight != null) {
			btnRight.setBackgroundResource(res);
		}
	}
	
	public void setLeftButtonListener(OnClickListener l){
		if (btnLeft != null) {
			btnLeft.setTag(LEFT_BTN_TAG);
			btnLeft.setOnClickListener(l);
		}
	}
	
	public void setRightButtonListener(OnClickListener l){
		if (btnRight != null) {
			btnRight.setTag(RIGHT_BTN_TAG);
			btnRight.setOnClickListener(l);
		}
	}
	
	public void onGoHome(){
//		Intent intent = null;
//		if (android.os.Build.VERSION.SDK_INT > 15) {
//			intent = new Object(){
//				@TargetApi(Build.VERSION_CODES.JELLY_BEAN) public Intent setParentIntent(){
//					Intent it = getParentActivityIntent();
//					return it;
//				}
//			}.setParentIntent();
//		}else{
//		String clstr = this.getIntent().getStringExtra(INTENT_INFO_OF_PAREN_ACTIVITY);
//		if (clstr != null) {
//			try {
//				Class<?> class1 = Class.forName(clstr);
//				intent = new Intent();
//				intent.setClass(this, class1);
//			} catch (ClassNotFoundException e) {}
//		}
////		}
//		
//		if (intent == null) {
//			intent = new Intent();
//			intent.setClass(this, TYHomeActivity.class);
//		}
//		startActivity(intent);
		finish();
	}
	
	long lastKeyTime = 0;
	public boolean onKeyDown(int keyCode, KeyEvent event) {//LoginActivity
		if(KeyEvent.KEYCODE_BACK==keyCode){
			if(getParent()!=null && getParent() instanceof LoginActivity){
				if(((LoginActivity) getParent()).isCanBack()){
					if(System.currentTimeMillis()-lastKeyTime>2000){
						lastKeyTime = System.currentTimeMillis();
						QLToastUtils.showToast(this, "再按一次退出程序");
					}else{
						((LoginActivity) getParent()).doFinish();
					}
				}
				return true;
			}
			if(onBackKeyPressed()){
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public void showLoadingMask(){
		if (loadingMask != null) {
			loadingMask.setVisibility(View.VISIBLE);
		}
	}
	
	public void hideLoadingMask(){
		if (loadingMask != null) {
			loadingMask.setVisibility(View.GONE);
		}
	}
	
	protected boolean defaultBackKeyPressed() {
		ComponentName parenName = this.getCallingActivity();
		Intent intent = new Intent(this,parenName.getClass());
		startActivity(intent);
		finish();
		return true;
	}
	
	@SuppressWarnings({ "unused", "deprecation" })
	private void exit() {
		AlertDialog dig = new AlertDialog.Builder(this).create();
		dig.setTitle("提示");
		dig.setMessage("要退出程序吗？");
		dig.setButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
		dig.setButton2("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		dig.show();
	}
	
	
	@Override
	protected Dialog onCreateDialog(int id) {
		return new AppDialog(this);
	}
	
	/**
	 * 用户按下后退键操作
	 */
	public abstract boolean onBackKeyPressed();
}
