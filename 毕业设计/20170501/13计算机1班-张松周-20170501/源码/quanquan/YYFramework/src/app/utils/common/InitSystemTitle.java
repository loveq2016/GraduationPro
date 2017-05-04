package app.utils.common;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import app.yy.geju.R;

public class InitSystemTitle {
	private Context context;

	public InitSystemTitle(Context context) {
		this.context = context;

	}

//	private void initSystemTitle() {
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//
//			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//			ViewGroup firstChildAtDecorView = ((ViewGroup) ((ViewGroup) getWindow().getDecorView()).getChildAt(0));
//			View statusView = new View(this);
//			ViewGroup.LayoutParams statusViewLp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(this));
//			// 颜色的设置可抽取出来让子类实现之
//			statusView.setBackgroundColor(getResources().getColor(R.color.acttitle_bg));
//			firstChildAtDecorView.addView(statusView, 0, statusViewLp);
//		}
//	}
//
//	// 获取状态栏的高度
//	private int getStatusBarHeight(Context context) {
//		int statusBarHeight = 0;
//		Resources resources = context.getResources();
//		int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
//		if (resourceId > 0) {
//			statusBarHeight = resources.getDimensionPixelSize(resourceId);
//		}
//		return statusBarHeight;
//	}
}
