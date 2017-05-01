package org.ql.utils;

/*
 * GZYY    2016-10-18  下午5:36:19
 */

public class QLSingleton {

	private static QLSingleton qlSingleton;

	public static QLSingleton getInstance() {
		if (qlSingleton == null) {
			qlSingleton = new QLSingleton();
		}
		return qlSingleton;
	}

	// token不符的时候直接跳转到登录页面
	private BackToLoginActListener backToLoginActListener;

	public BackToLoginActListener getBackToLoginActListener() {
		return backToLoginActListener;
	}

	public void setBackToLoginActListener(BackToLoginActListener listener) {
		this.backToLoginActListener = listener;
	}

	public interface BackToLoginActListener {
		void onCallBack();
	}
}
