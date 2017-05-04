package app.utils.network;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ql.utils.network.QLHttpGet;
import org.ql.utils.network.QLHttpManager.QLHttpMethod;
import org.ql.utils.network.QLHttpPost;
import org.ql.utils.network.QLHttpReply;
import org.ql.utils.network.QLHttpResult;
import org.ql.utils.network.QLHttpUtil;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import app.utils.common.Listener;
import app.utils.common.Public;

import com.google.gson.reflect.TypeToken;


/**
 * HTTP获取数据后使用Gson解析工具类
 * @author xjm
 */
public class GsonController {
	
	private QLHttpMethod httpMethod = QLHttpMethod.HTTPGET;
	
	private String name;
	
	private String url;
	
	private final Map<String,Object> entity = new HashMap<String, Object>();
	
	public GsonController(){ 
		
	}
	
	public static GsonController newInstance(String url,Map<String,Object> entity){
		GsonController util = new GsonController();
		util.setUrl(url);
		util.setEntity(entity);
		return util;
	}
	
	public static GsonController newInstance(String url,String entity){
		GsonController util = new GsonController();
		util.setUrl(url);
		util.setEntity(entity);
		return util;
	}
	
	private QLHttpUtil builder(Context context){
		QLHttpUtil http =  httpMethod == QLHttpMethod.HTTPPOST ? new QLHttpPost(context) : new QLHttpGet(context);
		http.setName(name);
		http.setUrl(url);
		http.setEntity(entity);
		return http;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	

	public QLHttpMethod getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(QLHttpMethod httpMethod) {
		this.httpMethod = httpMethod;
	}

	public Map<String,Object> getEntity() {
		return entity;
	}

	public void setEntity(Map<String,Object> entity) {
		this.entity.clear();
		this.entity.putAll(entity);
	}
	
	public void addEntity(String key,Object value){
		entity.put(key, value);
	}
	
	/**
	 * @param entity 请求数据参数<String> key=value&key=value
	 */
	public Map<String,Object> setEntity(String entity){
		this.entity.clear();
		if(!TextUtils.isEmpty(entity)){
			if(entity.substring(0, 1).equals("&") || entity.substring(0, 1).equals("?"))
				entity = entity.substring(1, entity.length());
			String array[] = entity.split("&");
			for(int i=0;i<array.length;i++){
				String str = array[i];
				this.entity.put(str.substring(0, str.indexOf("=")),str.substring(array[i].indexOf("=")+1,str.length()));
			}
		}
		return this.entity;
	}
	
	/**
	 * @param <T>   泛型标识
	 * @param beanClass 指定转化对象类型
	 * @return 转化后的对象
	 */
	public <T> void json2Bean(Context context, final Class<T> beanClass,final Listener<Void, T> callBack){
		QLHttpUtil http = builder(context);
		http.startConnection(new QLHttpResult() {
			@Override
			public void reply(QLHttpReply reply) {
				if(callBack == null)
					return;
				String result = reply.getReplyMsgAsString();
				T bean = null;
				if(!TextUtils.isEmpty(result)){
					try {
						
						bean = Public.getGson().fromJson(result, beanClass);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				callBack.onCallBack(null, bean);
			}
		});
	}
	
	/**
	 * @param <T>   泛型标识
	 * @param beanClass 指定转化对象类型
	 * @return 转化后的对象
	 */
	public <T> T json2Bean(Context context, Class<T> beanClass){
		QLHttpUtil http = builder(context);
		QLHttpReply reply = http.startConnection();
		String result = reply.getReplyMsgAsString();
		if(!TextUtils.isEmpty(result)){
			try {
				
				return Public.getGson().fromJson(result, beanClass);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}else{
			return null;
		}
	}
	
	
	/**
	 * <br>例如：List<"String"> list = json2List("……", new TypeToken<"List<"String">">(){});
	 * <br>     List<"Map<"Integer, Object">"> maplist = json2List("……", new TypeToken<"List<"Map<"Integer, Object">">">(){});
	 * @param <T>   泛型标识
	 * @param jsonString    JSON数据格式字符串
	 * @param typeToken     目标类型器，标识需要转换成的目标List对象
	 * @return
	 */
	public <T> void json2List(Context context,final TypeToken<List<T>> typeToken,final Listener<Void, List<T>> callBack){
		QLHttpUtil http = builder(context);
		http.startConnection(new QLHttpResult() {
			@Override
			public void reply(QLHttpReply reply) {
				if(callBack == null)
					return;
				String result = reply.getReplyMsgAsString();
				List<T> list = null;
				if(!TextUtils.isEmpty(result)){
					 Type type = typeToken.getType(); 
					 list = Public.getGson().fromJson(result, type);
				}
				callBack.onCallBack(null, list != null ? list : new ArrayList<T>());
			}
		});
	}
	
	/**
	 * <br>例如：List<"String"> list = json2List("……", new TypeToken<"List<"String">">(){});
	 * <br>     List<"Map<"Integer, Object">"> maplist = json2List("……", new TypeToken<"List<"Map<"Integer, Object">">">(){});
	 * @param <T>   泛型标识
	 * @param jsonString    JSON数据格式字符串
	 * @param typeToken     目标类型器，标识需要转换成的目标List对象
	 * @return
	 */
	public <T> List<T> json2List(Context context,final TypeToken<List<T>> typeToken){
		QLHttpUtil http = builder(context);
		QLHttpReply reply = http.startConnection();
		String result = reply.getReplyMsgAsString();
		List<T> list = null;
		if(!TextUtils.isEmpty(result)){
			 Type type = typeToken.getType(); 
			 list = Public.getGson().fromJson(result, type);
		}
		return list != null ? list : new ArrayList<T>();
	}
}
