package org.ql.activity.customtitle;

import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;

public class FragmentActivitys extends FragmentActivity implements
		ActivityInterface {

	protected String tag = FragmentActivitys.class.getSimpleName();
	private final Res res = new Res(this);
	private final Screen mScreen = new Screen(this);
	private OnActActivityResultListener onActActivityResultListener;
	private onActPermissionCheckResultListener permissionListener;
	private boolean backStatus = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tag = getClass().getSimpleName();

	}

	/**
	 * <li>左→右 <li>默认执行finish();
	 */
	@Override
	public void onTouchLeft2Right() {
		if (backStatus) {
			onBackPressed();
		}

	}

	public void setBackStatus(boolean status) {
		this.backStatus = status;
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
		if (onActActivityResultListener != null) {
			onActActivityResultListener.onActivityResult(requestCode,
					resultCode, data);
		}
	}

	@SuppressLint({ "NewApi", "Override" })
	public void onRequestPermissionsResult(int requestCode,
			String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (permissionListener != null) {
			permissionListener.onPermissionCheckResult(requestCode,
					permissions, grantResults);
		}
	}

	@Override
	public OnActActivityResultListener getActivityResultListener() {
		return onActActivityResultListener;
	}

	@Override
	public void setActivityResultListener(
			OnActActivityResultListener activityResultListener) {
		this.onActActivityResultListener = activityResultListener;
	}

	@Override
	public void setOnPermissionCheckResultListener(
			onActPermissionCheckResultListener listener) {
		permissionListener = listener;
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
