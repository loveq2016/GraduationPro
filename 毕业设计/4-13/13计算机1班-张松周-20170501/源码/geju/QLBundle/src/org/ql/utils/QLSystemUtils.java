package org.ql.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

public class QLSystemUtils {
	/** 转换dip为px*/
	public static int convertDIP2PX(Context context, int dip) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dip * scale + 0.5f * (dip >= 0 ? 1 : -1));
	}

	/** 转换px为dip*/
	public static int convertPX2DIP(Context context, int px) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (px / scale + 0.5f * (px >= 0 ? 1 : -1));
	}
	
	public static int screenHeight=0;
	public static int screenWidth=0;
	public static float screenDensity=0;
	public static int getScreenHeight(Activity context){
		if(screenWidth==0||screenHeight==0){
			DisplayMetrics dm = new DisplayMetrics();
			context.getWindowManager().getDefaultDisplay().getMetrics(dm);
			screenDensity = dm.density;
			screenHeight = dm.heightPixels;
			screenWidth = dm.widthPixels;
		}
		return screenHeight;
	}
	public static int getScreenWidth(Activity context){
		if(screenWidth==0||screenHeight==0){
			DisplayMetrics dm = new DisplayMetrics();
			context.getWindowManager().getDefaultDisplay().getMetrics(dm);
			screenDensity = dm.density;
			screenHeight = dm.heightPixels;
			screenWidth = dm.widthPixels;
		}
		return screenWidth;
	}
}
