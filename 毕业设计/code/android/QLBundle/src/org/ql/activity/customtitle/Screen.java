package org.ql.activity.customtitle;

import java.util.Set;
import java.util.TreeSet;

import org.ql.bundle.R;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

public final class Screen{
	
	private Activity mActivity;
	
	public Screen(Activity mActivity){
		this.mActivity = mActivity;
	}
	
	//隐藏标题
	public static final int FEATURE_NO_TITLE 								= -0x0001;
	//全屏
	public static final int FLAG_FULLSCREEN 									= -0x0002;
	//坚屏
	public static final int SCREEN_ORIENTATION_VERTICAL 		= -0x0004;
	//横屏
	public static final int SCREEN_ORIENTATION_HORIZONTAL	= -0x0008;
	//隐藏软键盘
	public static final int SOFT_INPUT_STATE_HIDDEN					= -0x0010;
	
	//过滤重复参数
	public static int[] filterRepeat(int... filter){ 
		if(filter ==null || filter.length <= 0)
			return new int[]{};
		Set<Integer> set = new TreeSet<Integer>();
		for (int i : filter) {
			set.add(i);
		}
		int[] des = new int[set.size()];
		int j = 0;
		for (Integer i : set) {
			des[j++] = i;
		}
		return des;
	}
	
	/**
	 * Activity.setContentView 方法前调用
	 * @param screen<ul>
	 * <li>{@link Screen.FEATURE_NO_TITLE} <br/>隐藏标题</li>
	 * <li>{@link Screen.FLAG_FULLSCREEN}<br/>全屏</li>
	 * <li>{@link Screen.SCREEN_ORIENTATION_VERTICAL}<br/>竖屏</li>
	 * <li>{@link Screen.SCREEN_ORIENTATION_HORIZONTAL}<br/>横屏</li>
	 * <li>{@link Screen.SOFT_INPUT_STATE_HIDDEN}<br/>隐藏软键盘</li>
	 * </ul>
	 */
	public static void setScreen(Activity mActivity,int... screen) {
		screen = filterRepeat(screen);
		for(int screenValue : screen){
			switch (screenValue) {
			case Screen.FEATURE_NO_TITLE:
				mActivity.requestWindowFeature(Window.FEATURE_NO_TITLE);
				break;
			case Screen.FLAG_FULLSCREEN:
				mActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
				break;
			case Screen.SCREEN_ORIENTATION_VERTICAL:
				mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				break;
			case Screen.SCREEN_ORIENTATION_HORIZONTAL:
				mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				break;
			case Screen.SOFT_INPUT_STATE_HIDDEN:
				mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			break;
			}
		}
	}
	
	//#############################################################//
	public final static String INTENT_KEY_OVERRIDE 	= "key_override";
	/**左→右*/
	public final static int OVERRIDE_LEFT_TO_RIGHT 	= 10;
	/**右←左*/
	public final static int OVERRIDE_RIGHT_TO_TLEFT	= ~OVERRIDE_LEFT_TO_RIGHT;
	public final static int OVERRIDE_DEFAULT 		= OVERRIDE_RIGHT_TO_TLEFT;
	
	protected int override = OVERRIDE_DEFAULT;
	
	void setOverride(int override){
		this.override = override;
	}
	
	public static void execOverride(Activity mActivity,int override){
		if(override == OVERRIDE_LEFT_TO_RIGHT)
			transitionLeftToRight(mActivity);
		else if(override == OVERRIDE_RIGHT_TO_TLEFT)
			transitionRightToLeft(mActivity);
	}
	
	/**左→右*/
	public static void transitionLeftToRight(Activity mActivity){
		if(mActivity.getParent() != null)
			mActivity = mActivity.getParent();
		mActivity.overridePendingTransition(R.anim.zoom_in_center,R.anim.slide_out_right);
	}

	/**右←左*/
	public static void transitionRightToLeft(Activity mActivity){
		if(mActivity.getParent() != null)
			mActivity = mActivity.getParent();
		mActivity.overridePendingTransition(R.anim.slide_in_right,R.anim.zoom_out_center);
	}
	
	//########################################################################//
	private static final int FLING_MIN_DISTANCE = 120; 
    private static final int FLING_MIN_VELOCITY = 100; 
    private static final int FLING_MIN_DURATION = 350; 
    private float distance;
    private float velocity;
    private long startTime,endTime;
	private float dx, ux, dy, uy;
	
	boolean dispatchTouchEvent(ActivityInterface mActivityInterface,MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			dx = event.getX();
			dy = event.getY();
			startTime = System.currentTimeMillis();
			break;
		case MotionEvent.ACTION_UP:
			ux = event.getX();
			uy = event.getY();
			endTime = System.currentTimeMillis();
			if(distance <= 0)
				distance = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, FLING_MIN_DISTANCE, mActivity.getResources().getDisplayMetrics());
			if(velocity <= 0)
				velocity = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, FLING_MIN_VELOCITY, mActivity.getResources().getDisplayMetrics());
			
			if (ux - dx > distance && Math.abs(dy - uy) < velocity && endTime - startTime < FLING_MIN_DURATION) {
				mActivityInterface.onTouchLeft2Right();
			}else if(ux - dx < -distance && Math.abs(dy - uy) < velocity && endTime - startTime < FLING_MIN_DURATION){
				mActivityInterface.onTouchRight2Left();
			}
			break;
		}
		return false;
	}
}