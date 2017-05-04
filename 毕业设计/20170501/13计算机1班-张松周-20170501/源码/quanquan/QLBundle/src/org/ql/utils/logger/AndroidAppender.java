package org.ql.utils.logger;

import android.util.Log;


/**
 * android 平台中日志输出
 * @author Rinvay.T
 * @date 2012-8-17
 * @time 下午4:33:27
 */
public class AndroidAppender extends Appender{

    public synchronized void printTrace(String tag, int level, String trace) {
        tag = "[" + tag + "]";
        switch (level){
        case Logger.DEBUG_LEVEL:
            Log.d(tag, trace);
            break;
        case Logger.INFO_LEVEL:
            Log.i(tag, trace);
            break;
        case Logger.WARN_LEVEL:
            Log.w(tag, trace);
            break;
        case Logger.ERROR_LEVEL:
        case Logger.FATAL_LEVEL:
            Log.e(tag, trace);
            break;
        default:
            Log.v(tag, trace);
            break;
        }
    }

}
