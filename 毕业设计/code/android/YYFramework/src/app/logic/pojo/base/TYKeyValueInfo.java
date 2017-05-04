package app.logic.pojo.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * 
 * @author SiuJiYung
 * Create at 2014-1-12 下午7:53:44
 * </br>
 * 
 * 配置结构体
 */
public class TYKeyValueInfo {
	public int Value;
	public String Name;
	
	public static List<TYKeyValueInfo> HashMap2KeyValueInfoList(HashMap<String, Integer> map){
		if (map == null) {
			return null;
		}
		ArrayList<TYKeyValueInfo> list = new ArrayList<TYKeyValueInfo>();
		Set<String> keySet = map.keySet();
		TYKeyValueInfo tmpInfo = null;
		for(String key:keySet){
			tmpInfo = new TYKeyValueInfo();
			tmpInfo.Name = key;
			tmpInfo.Value = map.get(key);
			list.add(tmpInfo);
		}
		return list;
	}
	
	public static List<TYKeyValueInfo> HashMapString2KeyValueInfoList(HashMap<String, String> map){
		if (map == null) {
			return null;
		}
		ArrayList<TYKeyValueInfo> list = new ArrayList<TYKeyValueInfo>();
		Set<String> keySet = map.keySet();
		TYKeyValueInfo tmpInfo = null;
		String tmpV = null;
		for(String key:keySet){
			tmpInfo = new TYKeyValueInfo();
			tmpInfo.Name = key;
			try {
				tmpV = map.get(key);
				tmpInfo.Value = Integer.parseInt(tmpV);
				list.add(tmpInfo);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}			
		}
		return list;
	}
	
}

