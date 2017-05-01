package org.ql.activity.customtitle;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

public abstract class AbsHandler {

	protected Activity activity;
	protected int backgroudColor;
	
	public Activity getActivity() {
		return activity;
	}
	
	public abstract TextView getTitleView();
	
	public abstract View getBackground();

	public abstract void setTitle(CharSequence title);

	public abstract void setTitle(int stringId);

	public abstract void setTitleColor(int colorId) ;
	
	public void setBackgroudColor(int bgColor){
		backgroudColor = bgColor;
	}
	
	public int getBackgroudColor(){
		return backgroudColor;
	}
	
	public abstract void setStatusBarColor(int color);
	
	public abstract int getLayout();
	
	public abstract void onWindowFocusChanged(boolean hasFocus);
	
	public void onCreate(Activity activity){
		this.activity = activity;
	}
	
}
