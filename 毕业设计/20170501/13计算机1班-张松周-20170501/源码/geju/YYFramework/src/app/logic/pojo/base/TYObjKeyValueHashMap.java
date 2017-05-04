package app.logic.pojo.base;

import java.util.HashMap;
import java.util.Set;

/**
 * 
 * @author SiuJiYung
 * Create at 2014-1-13 下午2:59:21
 * </br>
 */
public class TYObjKeyValueHashMap {
	public Object Name;
	public Object Value;
	
	public TYObjKeyValueHashMap(){};
	public TYObjKeyValueHashMap(HashMap<String, Integer> info){
		Set<String> keySet = info.keySet();
		String[] keys = (String[])keySet.toArray();
		if (keys != null && keys.length > 0) {
			this.Name = keys[0];
			this.Value = info.get(this.Name);
		}
	}

	public TYObjKeyValueHashMap(KeyValueItemInfo<String, Integer> info){
		this.Name = info.getItemName();
		this.Value = info.getItemID();
	}
}
