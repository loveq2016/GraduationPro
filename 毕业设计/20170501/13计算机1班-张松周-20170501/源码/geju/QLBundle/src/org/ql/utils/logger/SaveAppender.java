package org.ql.utils.logger;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import android.os.Environment;
import android.text.TextUtils;

public class SaveAppender extends Appender{
	
	private java.util.logging.Logger logger;
	
	public SaveAppender(String packageName){
		packageName = TextUtils.isEmpty(packageName) ? "" : packageName;
		logger = java.util.logging.Logger.getAnonymousLogger();
		try {
			String fileName = new SimpleDateFormat("yyyy-MM-dd").format(new Date())+".log";
			File file = new File(Environment.getExternalStorageDirectory(),"logger"+File.separator+packageName+File.separator+fileName);
			if(!file.getParentFile().exists())
				file.getParentFile().mkdirs();
			java.util.logging.FileHandler fh = new java.util.logging.FileHandler(file.toString(),true);
			fh.setFormatter(new java.util.logging.SimpleFormatter());
			logger.addHandler(fh);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	public synchronized void printTrace(String tag, int level, String trace) {
		tag = "[" + tag + "]";
        switch (level){
        case Logger.WARN_LEVEL:
        case Logger.ERROR_LEVEL:
        	logger.log(Level.WARNING,tag+trace);
        	break;
        default:
        	logger.log(Level.INFO,tag+trace);
            break;
        }
	}

}
