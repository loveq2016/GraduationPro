package org.ql.utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;


/**    
* 类名称：QLJsonUtil    
* 类描述：二次封装json工具，底层使用simplejson  
* 创建人：anan
* 创建时间：2012-12-13 上午10:59:20    
* 修改人：anan    
* 修改时间：2012-12-13 上午10:59:20    
* 修改备注：  
* @version    
*/   
public class QLJsonUtil {
	
	public static byte doByte(Object obj){
		 return obj!=null?Byte.parseByte(obj.toString()):0;
	}
	public static short doShort(Object obj){
		return obj!=null?Short.parseShort(obj.toString()):0;
	}
	public static int doInt(Object obj){
		return obj!=null?Integer.parseInt(obj.toString()):0;
	}
	public static long doLong(Object obj){
		 return obj!=null?Long.parseLong(obj.toString()):0;
	}
	public static float doFloat(Object obj){
		return obj!=null?Float.parseFloat(obj.toString()):0;
	}
	public static double doDouble(Object obj){ 
		return obj!=null?Double.parseDouble(obj.toString()):0;
	}
	public static String doString(Object obj){
		 return obj!=null?obj.toString():"";
	} 
	public static boolean doBoolean(Object obj){
		return obj!=null?Boolean.parseBoolean(obj.toString()):false;
	}
	
	
	
	public static byte doByte(Object obj,byte defValue){
		return obj!=null?Byte.parseByte(obj.toString()):defValue;
	}
	public static short doShort(Object obj,short defValue){
		return obj!=null?Short.parseShort(obj.toString()):defValue;
	}
	public static int doInt(Object obj,int defValue){
		return obj!=null?Integer.parseInt(obj.toString()):defValue;
	}
	public static long doLong(Object obj,long defValue){
		return obj!=null?Long.parseLong(obj.toString()):defValue;
	}
	public static float doFloat(Object obj,float defValue){
		return obj!=null?Float.parseFloat(obj.toString()):defValue;
	}
	public static double doDouble(Object obj,double defValue){ 
		return obj!=null?Double.parseDouble(obj.toString()):defValue;
	}
	public static String doString(Object obj,String defValue){
		return obj!=null?obj.toString():defValue;
	} 
	public static boolean doBoolean(Object obj,boolean defValue){
		return obj!=null?Boolean.parseBoolean(obj.toString()):defValue;
	}
	
	
	
	
	
	public static JSONObject doJSONObject(String json){
		json=json!=null?json:"";
		 return doJSONObject(JSONValue.parse(json));
	 }
	
	public static JSONObject doJSONObject(Object obj){
		return (obj instanceof JSONObject)?(JSONObject)obj:null;
	}
	
	
	 public static JSONArray doJSONArray(String json){
		 json=json!=null?json:"";
		 return doJSONArray(JSONValue.parse(json));
	 }
	
	 public static JSONArray doJSONArray(Object obj){
		 return (obj instanceof JSONArray)?(JSONArray)obj:null;
	 }
}
