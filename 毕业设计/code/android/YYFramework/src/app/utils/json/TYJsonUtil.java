package app.utils.json;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author SiuJiYung
 * create at 2013-12-10 下午7:11:53
 *</br>
 */
public class TYJsonUtil {

	public static void put(JSONObject obj,String name,String value){
		try {
			obj.put(name, value);
		} catch (JSONException e) {}
	}
	
	public static void put(JSONObject obj,String name,boolean value){
		try {
			obj.put(name, value);
		} catch (JSONException e) {}
	}
	
	public static void put(JSONObject obj,String name,int value){
		try {
			obj.put(name, value);
		} catch (JSONException e) {}
	}
	
	public static void put(JSONObject obj,String name,long value){
		try {
			obj.put(name, value);
		} catch (JSONException e) {}
	}
	
	public static void put(JSONObject obj,String name,double value){
		try {
			obj.put(name, value);
		} catch (JSONException e) {}
	}
	
	public static void put(JSONObject obj,String name,float value){
		try {
			obj.put(name, value);
		} catch (JSONException e) {}
	}
	
	public static void put(JSONObject obj,String name,Object value){
		try {
			obj.put(name, value);
		} catch (JSONException e) {}
	}
}
