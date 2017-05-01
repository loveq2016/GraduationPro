package org.ql.activity.customtitle;


import org.ql.bundle.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SystemHandler extends AbsHandler{

	protected TextView title;
	protected View backgroundView;
	
	@Override
	public View getBackground(){
		return backgroundView;
	}
	
	@Override
	public TextView getTitleView(){
		return title;
	}
	
	@Override
	public void setTitle(CharSequence title) {
		if(this.title != null)
			this.title.setText(title);
	}
	
	@Override
	public void setTitle(int stringId) {
		if(this.title != null)
			this.title.setText(stringId);
	}

	@Override
	public void setTitleColor(int colorId) {
		if(this.title != null)
			this.title.setTextColor(colorId);
	}

	@Override
	public int getLayout() {
		return R.layout.act__system;
	}

	@Override
	public void onCreate(Activity activity) {
		super.onCreate(activity);
		backgroundView = activity.findViewById(android.R.id.background);
		title = (TextView)activity.findViewById(android.R.id.title);
		setBackgroudColor(backgroudColor);
	}
	
	@SuppressLint("NewApi")
	public void setStatusBarColor(int color){
		backgroudColor = color;
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//			//5.0以上系统直接使用系统的api
//			activity.getWindow().setStatusBarColor(color);
//		}else 
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// 生成一个状态栏大小的矩形
            View statusView = createStatusView(activity, color);
            // 添加 statusView 到布局中
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            decorView.addView(statusView);
            // 设置根布局的参数
            ViewGroup rootView = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
           
            int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
            int statusBarHeight = activity.getResources().getDimensionPixelSize(resourceId);
            
            rootView.setPadding(0, statusBarHeight, 0, 0);
            rootView.setFitsSystemWindows(true);
//            rootView.setClipToPadding(true);
		}
	}
	
	/**
     * 生成一个和状态栏大小相同的矩形条
     *
     * @param activity 需要设置的activity
     * @param color    状态栏颜色值
     * @return 状态栏矩形条
     */
    private static View createStatusView(Activity activity, int color) {
        // 获得状态栏高度
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        int statusBarHeight = activity.getResources().getDimensionPixelSize(resourceId);

        // 绘制一个和状态栏一样高的矩形
        View statusView = new View(activity);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                statusBarHeight);
        statusView.setLayoutParams(params);
        statusView.setBackgroundColor(color);
        return statusView;
    }

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		
	}

}
