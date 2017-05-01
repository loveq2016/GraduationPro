package org.ql.utils.debug;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;


/**    
* 类名称：QLLog    
* 类描述：日志打印类，可以写入文件，通过DEBUG和WRITE_FILE控制
* 创建人：anan
* 创建时间：2012-11-23 下午4:52:54    
* 修改人：anan    
* 修改时间：2012-11-23 下午4:52:54    
* 修改备注：  
* @version    
*/   
public class QLLog {
	public final static boolean DEBUG = true;
	public static boolean WRITE_FILE = false;
	private static StringBuffer sb=new StringBuffer();
	private static final String DEFAULT_TAG="UNKNOW_MODEL";
	
	public static void pln(Object obj){
		if(DEBUG&&null!=obj)
			System.out.println(obj);
		if(obj!=null)
			saveLog('I',DEFAULT_TAG,obj.toString());
	}
	
	public static void p(Object obj){
		if(DEBUG&&null!=obj)
			System.out.print(obj);
		if(obj!=null)
			saveLog('I',DEFAULT_TAG,obj.toString());
	}
	
	public static void i(String tag,String msg){
		if(DEBUG&&null!=msg)
			android.util.Log.i(tag, msg);
		saveLog('I',tag,msg);
	}
	public static void d(String tag,String msg){
		if(DEBUG&&null!=msg)
			android.util.Log.d(tag, msg);
		saveLog('D',tag,msg);
	}
	public static void e(String tag,String msg){
		if(DEBUG&&null!=msg)
			android.util.Log.e(tag, msg);
		saveLog('E',tag,msg);
	}
	public static void e(String msg){
		if(DEBUG&&null!=msg)
			android.util.Log.e(QLLog.class.getSimpleName(), msg);
		saveLog('E',QLLog.class.getSimpleName(),msg);
	}
	public static void v(String tag,String msg){
		if(DEBUG&&null!=msg)
			android.util.Log.v(tag, msg);
		saveLog('V',tag,msg);
	}
	
	public static void WriteLog(String sender,String msg)
	{
		if(null!=msg)
			android.util.Log.i(sender,msg);
	}
	
	private static SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd__HH_mm_ss");
	private static long lineNum=0;
	private static final int MAX_BUFFER_LINE=6;
	private static final String LogFileName="QLLog.txt";
	
	private static void saveLog(char logLever,String tag,String msg)
	{
		if(WRITE_FILE){			
			lineNum++;
			long time=System.currentTimeMillis();
			Date dt=new Date(time);
			String tmp=df.format(dt);
			if(lineNum==1)
				sb.append("\r\n\r\n=================Log Start=================\r\n\r\n");
			sb.append(logLever);
			sb.append('\t');
			sb.append("Line:");
			sb.append(lineNum);
			sb.append('\t');
			sb.append(tmp);
			sb.append('\t');
			sb.append(tag);
			sb.append('\t');
			sb.append(msg);
			sb.append("\r\n");
			dt=null;
			tmp=null;
			if(lineNum%MAX_BUFFER_LINE==0){
				writeLog();
			}
		}
	}
	
	public static void writeLog()
	{
		if(!DEBUG){return;}
//		QLLog.i("SMCLog","start to  write log.");
		Date dt = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
		String dtstr = sdf.format(dt);
		File RootTmp=Environment.getExternalStorageDirectory();
		File tmpPath = new File(RootTmp, "QLLog");
		if(!tmpPath.exists()){
			tmpPath.mkdirs();
		}
		File logfile=new File(tmpPath,dtstr+LogFileName);
		StringBuffer buff=sb;
		try {
			if (!logfile.exists()) {
				logfile.createNewFile();
			}
			FileOutputStream fs=new FileOutputStream(logfile,true);
			byte[] bf=buff.toString().getBytes();
			fs.write(bf);
			fs.flush();
			fs.close();
			bf=null;
			fs=null;
//			QLLog.i("SMCLog","write log success.");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			buff=null;
			int l=sb.length();
			sb.delete(0, l);
		}
	}
	
//	private static void makeDir(String dir){
//		File file=new File(dir); 
//		if(!file.exists()) 
//			file.mkdir(); 
//	}
}
