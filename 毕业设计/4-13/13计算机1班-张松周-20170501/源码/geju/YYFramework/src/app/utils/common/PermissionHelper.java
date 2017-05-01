package app.utils.common;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 *
 * SiuJiYung create at 2016年8月23日 上午11:59:45
 *
 */

public class PermissionHelper {

	private Activity mContext;
	private int requestCode = 10;
	private PermissionHelper(){}
	
	public static PermissionHelper getHelper(){
		return new PermissionHelper();
	}

	/**
	 * 检查权限是否已请求到 (6.0)
	 */
	public  boolean checkPermissions(Activity activity,int requestCode, Listener<Boolean, Void> callback, String... permissions) {
		mContext = activity;
		if (Build.VERSION.SDK_INT < 23) {
			if (callback != null) {
				callback.onCallBack(Boolean.valueOf(true), null);
			}
			return true;
		}
		this.requestCode = requestCode;
		// 版本兼容 && 判断缺失哪些必要权限
		if (lacksPermissions(permissions)) {
			// 如果缺失,则申请
			requestPermissions(permissions);
			return false;
		} else {
			if (callback != null) {
				callback.onCallBack(Boolean.valueOf(true), null);
			}
			return true;
		}
	}

	/**
	 * 判断是否缺失权限集合中的权限
	 */
	private boolean lacksPermissions(String... permissions) {
		for (String permission : permissions) {
			if (lacksPermission(permission)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断是否缺少某个权限
	 */
	private boolean lacksPermission(String permission) {
		return ContextCompat.checkSelfPermission(mContext, permission) == PackageManager.PERMISSION_DENIED;
	}

	/**
	 * 请求权限
	 */
	private void requestPermissions(String... permissions) {
		ActivityCompat.requestPermissions((Activity)mContext, permissions, requestCode);
	}

	/**
	 * 启动应用的设置,进入手动配置权限页面
	 */
	private void startAppSettings() {
		Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
		intent.setData(uri);
		mContext.startActivity(intent);
	}

}
