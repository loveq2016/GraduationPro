package org.ql.utils.logger;

public abstract class Appender{
  public abstract void printTrace(String tag, int level, String trace);
}
