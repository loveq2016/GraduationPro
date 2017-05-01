package app.utils.helpers;

import u.aly.cp;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

/*
 * GZYY    2016-9-5  下午12:05:17
 */

public class SystemBuilderUtils {

	private static SystemBuilderUtils builderUtils;

	private SystemBuilderUtils() {
	}

	public static SystemBuilderUtils getInstance() {
		if (builderUtils == null) {
			return builderUtils = new SystemBuilderUtils();
		}
		return builderUtils;
	}

	/*
	 * 获取当前程序版本名
	 */
	public static String getAppVersionName(Context context) {
		PackageManager manager = context.getPackageManager();
		String versionName = null;
		try {
			versionName = manager.getPackageInfo(context.getPackageName(), 0).versionName;
			if (versionName.equals("") || versionName.length() <= 0) {
				return versionName;
			}
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return versionName;
	}

	/*
	 * 获取当前版本code
	 */

	public static int getAppVersionCode(Context context) {
		int versionCode = -1;
		try {
			versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return versionCode;
	}
}
