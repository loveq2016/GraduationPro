package app.utils.common;

import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.ql.app.alert.AlertDialog;
import org.ql.utils.debug.QLLog;

import android.R.anim;
import android.content.Context;
import android.content.DialogInterface;

public class UnHanleException implements UncaughtExceptionHandler {
	private final String TAG = getClass().getSimpleName();
	// 获取application 对象；
	private Context mContext;
	private Thread.UncaughtExceptionHandler defaultExceptionHandler;
	// 单例声明CustomException;
	private static UnHanleException customException;

	private UnHanleException() {
	}

	public static UnHanleException getInstance() {
		if (customException == null) {
			customException = new UnHanleException();
		}
		return customException;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable exception) {
		if (defaultExceptionHandler != null) {
			// 强制打开Log记录，记录致命的异常。
			QLLog.WRITE_FILE = true;
			QLLog.e(TAG, "========================================");
			StringBuilder sBuilder = new StringBuilder();
			sBuilder.append("System Info:\n\r\t");
			sBuilder.append("\n\r\t device model:");
			sBuilder.append(android.os.Build.MODEL);
			sBuilder.append("\n\r\t SDK Version:");
			sBuilder.append(android.os.Build.VERSION.SDK);
			sBuilder.append("\n\r\t System Version:");
			sBuilder.append(android.os.Build.VERSION.RELEASE);
			sBuilder.append("\n\r\t App Name:");
			sBuilder.append(mContext.getPackageName());
			sBuilder.append("\n\r\t App Version Code:");
			sBuilder.append(Public.getAppVersionCode(mContext));
			sBuilder.append("\n\r\t\n\r\t");
			sBuilder.append(exception.getLocalizedMessage());
			for(StackTraceElement ste : exception.getStackTrace()){
				sBuilder.append("\r\n\t\t");
				sBuilder.append(ste.toString());
			}
			QLLog.e(TAG, sBuilder.toString());
			QLLog.e(TAG, "========================================");
			QLLog.writeLog();

			defaultExceptionHandler.uncaughtException(thread, exception);
//			Date dt = new Date(System.currentTimeMillis());
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
//			String dtstr = sdf.format(dt);
//
//			StringBuilder sb = new StringBuilder();
//			sb.append(mContext.getPackageName());
//			sb.append("遇到一个致命的错误，将无法继续运行，你可以把 SD卡上的/QLLog/");
//			sb.append(dtstr);
//			sb.append("/QLLog.txt 文件提交给我们以处理该错误。");
//			showErrorMessage(sb.toString(),thread,exception);
		}
	}

	public void init(Context context) {
		mContext = context;
		defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

//	private void showErrorMessage(String msg,final Thread thread, final Throwable exception) {
//		AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
//		alert.setCancelable(false);
//		alert.setTitle("程序错误");
//		alert.setMessage(msg);
//		alert.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				if (defaultExceptionHandler != null) {
//					defaultExceptionHandler.uncaughtException(thread, exception);
//				}				
//			}
//		});
//		alert.show();
//	}
}
