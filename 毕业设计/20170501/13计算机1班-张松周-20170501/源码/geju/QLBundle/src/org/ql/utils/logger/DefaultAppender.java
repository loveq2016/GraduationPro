package org.ql.utils.logger;


public class DefaultAppender extends Appender {
	public synchronized void printTrace(String tag, int level, String trace) {
		String Lv = "";
		switch (level) {
		case Logger.VERBOSE_LEVEL:
			Lv = "VERBOSE";
			break;
		case Logger.DEBUG_LEVEL:
			Lv = "DEBUG";
			break;
		case Logger.INFO_LEVEL:
			Lv = "INFO";
			break;
		case Logger.WARN_LEVEL:
			Lv = "WARN";
			break;
		case Logger.ERROR_LEVEL:
			Lv = "ERROR";
			break;
		case Logger.FATAL_LEVEL:
			Lv = "FATAL";
		}
		System.out.println("[" + Lv + "][" + tag + "]" + trace);
	}
}
