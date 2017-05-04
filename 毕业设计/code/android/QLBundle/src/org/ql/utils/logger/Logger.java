package org.ql.utils.logger;

public class Logger {
	
	public static boolean DEBUG = false;
	
	public static final int VERBOSE_LEVEL = 0;
	public static final int DEBUG_LEVEL = 1;
	public static final int INFO_LEVEL = 2;
	public static final int WARN_LEVEL = 3;
	public static final int ERROR_LEVEL = 4;
	public static final int FATAL_LEVEL = 5;

	private static Appender[] appenders = { new AndroidAppender() };

	public static void setTraceLevel(int level) {
		if (level < VERBOSE_LEVEL)
			level = VERBOSE_LEVEL;
		else if (level > FATAL_LEVEL) {
			level = FATAL_LEVEL;
		}
	}

	public static void setAppenders(Appender[] appenders) {
		if(appenders == null || appenders.length == 0)
			return;
		Logger.appenders = appenders;
	}

	public static void verbose(String tag, String trace) {
		printTrace(tag, trace, VERBOSE_LEVEL);
	}

	public static void debug(String tag, String trace) {
		printTrace(tag, trace, DEBUG_LEVEL);
	}

	public static void info(String tag, String trace) {
		printTrace(tag, trace, INFO_LEVEL);
	}

	public static void warn(String tag, String trace) {
		printTrace(tag, trace, WARN_LEVEL);
	}

	public static void error(String tag, String trace) {
		printTrace(tag, trace, ERROR_LEVEL);
	}

	public static void error(String tag, String trace, Throwable e) {
		printTrace(tag, trace, ERROR_LEVEL);
		e.printStackTrace();
	}

	public static void fatal(String tag, String trace) {
		printTrace(tag, trace, FATAL_LEVEL);
	}

	public static void fatal(String tag, String trace, Throwable e) {
		printTrace(tag, trace, FATAL_LEVEL);
		e.printStackTrace();
	}

	private static void printTrace(String tag, String trace, int level) {
		if (DEBUG && (appenders != null))
			appenders = appenders!=null ? appenders : new Appender[]{new AndroidAppender()};
			for (int i = 0; i < appenders.length; i++)
				appenders[i].printTrace(tag, level, trace);
	}
}
