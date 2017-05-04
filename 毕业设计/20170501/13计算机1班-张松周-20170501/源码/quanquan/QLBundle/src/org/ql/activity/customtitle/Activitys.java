package org.ql.activity.customtitle;

import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

public class Activitys extends Activity implements ActivityInterface {

	protected String tag = Activitys.class.getSimpleName();
	private final Res res = new Res(this);
	private final Screen mScreen = new Screen(this);
	private boolean touchLeft2RightEnable = false;
	private OnActActivityResultListener activityResultListener;
	private onActPermissionCheckResultListener permissionCheckResultListener;

	public boolean isTouchLeft2RightEnable() {
		return touchLeft2RightEnable;
	}

	public void setTouchLeft2RightEnable(boolean touchLeft2RightEnable) {
		this.touchLeft2RightEnable = touchLeft2RightEnable;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tag = getClass().getSimpleName();
		MobclickAgent.setDebugMode(true);
	}

	/**
	 * <li>左→右 <li>默认执行finish();
	 */
	@Override
	public void onTouchLeft2Right() {
		if (touchLeft2RightEnable) {

			onBackPressed();
		}
	}

	/**
	 * <li>右←左
	 */
	@Override
	public void onTouchRight2Left() {

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		mScreen.dispatchTouchEvent(this, event);
		return super.dispatchTouchEvent(event);
	}

	@Override
	public Res getRes() {
		return res;
	}

	/**
	 * Activity.setContentView 方法前调用
	 * 
	 * @param screen
	 *            <ul>
	 *            <li>{@link Screen.FEATURE_NO_TITLE} <br/>
	 *            隐藏标题</li>
	 *            <li>{@link Screen.FLAG_FULLSCREEN}<br/>
	 *            全屏</li>
	 *            <li>{@link Screen.SCREEN_ORIENTATION_VERTICAL}<br/>
	 *            竖屏</li>
	 *            <li>{@link Screen.SCREEN_ORIENTATION_HORIZONTAL}<br/>
	 *            横屏</li>
	 *            <li>{@link Screen.SOFT_INPUT_STATE_HIDDEN}<br/>
	 *            隐藏软键盘</li>
	 *            </ul>
	 */
	@Override
	public void setScreen(int... screen) {
		Screen.setScreen(this, screen);
	}

	/**
	 * @param screen
	 *            <ul>
	 *            <li>{@link Screen.OVERRIDE_LEFT_TO_RIGHT} <br/>
	 *            左→右</li>
	 *            <li>{@link Screen.OVERRIDE_RIGHT_TO_TLEFT}<br/>
	 *            右←左</li>
	 *            </ul>
	 */
	@Override
	public void setOverride(int override) {
		mScreen.setOverride(override);
	}

	@Override
	public void finish() {
		super.finish();
		int override = Screen.OVERRIDE_DEFAULT;
		Intent intent = getIntent();
		if (intent != null)
			override = getIntent().getIntExtra(Screen.INTENT_KEY_OVERRIDE,
					Screen.OVERRIDE_DEFAULT);
		Screen.execOverride(this, ~override);
	}

	@Override
	public void startActivity(Intent intent) {
		intent.putExtra(Screen.INTENT_KEY_OVERRIDE, mScreen.override);
		super.startActivity(intent);
		Screen.execOverride(this, mScreen.override);
		mScreen.override = Screen.OVERRIDE_DEFAULT;
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		intent.putExtra(Screen.INTENT_KEY_OVERRIDE, mScreen.override);
		super.startActivityForResult(intent, requestCode);
		Screen.execOverride(this, mScreen.override);
		mScreen.override = Screen.OVERRIDE_DEFAULT;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (activityResultListener != null) {
			activityResultListener.onActivityResult(requestCode, resultCode,
					data);
		}
	}

	@SuppressLint({ "NewApi", "Override" })
	public void onRequestPermissionsResult(int requestCode,
			String[] permissions, int[] grantResults) {
		// super.onRequestPermissionsResult(requestCode, permissions,
		// grantResults);
		if (permissionCheckResultListener != null) {
			permissionCheckResultListener.onPermissionCheckResult(requestCode,
					permissions, grantResults);
		}
	}

	public OnActActivityResultListener getActivityResultListener() {
		return activityResultListener;
	}

	public void setActivityResultListener(
			OnActActivityResultListener activityResultListener) {
		this.activityResultListener = activityResultListener;
	}

	@Override
	public void setOnPermissionCheckResultListener(
			onActPermissionCheckResultListener listener) {
		permissionCheckResultListener = listener;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}

}
