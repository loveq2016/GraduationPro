package org.ql.activity.customtitle;
public interface ActivityInterface {

	public Res getRes();
	
	public void setScreen(int... screen);
	
	public void setOverride(int override);
	
	public void onTouchLeft2Right();
	
	public void onTouchRight2Left();
	
	public OnActActivityResultListener getActivityResultListener();
	
	public void setActivityResultListener(OnActActivityResultListener activityResultListener);
	
	public void setOnPermissionCheckResultListener(onActPermissionCheckResultListener listener);
}
