package org.ql.utils.network;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.ql.utils.debug.QLLog;
import org.ql.utils.network.QLHttpManager.QLHttpMethod;

import android.content.Context;
import android.text.TextUtils;

public abstract class QLHttpUtil {
	/** 标准缓存策略：只有在缓存在有效期内时才有效 **/
	public static final int CACHE_TYPE_NORMAL = 0;
	/** 缓存优先策略：优先使用还处于有效期内的缓存，如果有网络，但将继续请求网络，以刷新缓存 **/
	public static final int CACHE_TYPE_NETWORK_FIRST = 2;
	/** 缓存永远有效策略：在网络无效的情况下，无视缓存有效期 **/
	public static final int CACHE_TYPE_AWAYS = 4;

	private final String tag = QLHttpUtil.class.getSimpleName();
	protected Context context;
	protected String url;
	protected String name;
	protected QLHttpMethod requestType;
	protected int defaultCacheTimeOut = 216000;
	protected String cacheKey = null;
	/** 使用缓存 **/
	protected boolean useCache = true;
	protected int cacheType = CACHE_TYPE_NETWORK_FIRST;
	/** 使用缓存是否提示 **/
	protected boolean notifyUseCache = false;
	protected Map<String, ? extends Object> entity = new HashMap<String, Object>();

	/** 连接超时 **/
	protected int connectionTimeOut = 0;
	protected String encoder = HTTP.UTF_8;

	public static final int RETURN_BYTE = 0x05;
	public static final int RETURN_STRING = 0x06;
	public static final int RETURN_INPUTSTREAM = 0x07;

	//
	public static final int STATE_NONE = 0;
	// 连接停止
	public static final int STATE_ABORT = 1;
	// 连接中...
	public static final int STATE_CONNECTING = 2;
	// 连接完成...
	public static final int STATE_CONNECTED = 3;

	protected int state = STATE_NONE;

	/** 返回类型 */
	protected int returnType = RETURN_STRING;

	/** 代码块同步对象 */
	final static Object mSynchronized = new Object();

	protected QLHttpUtil(Context context) {
		this(context, null);
	}

	protected QLHttpUtil(Context context, String url) {
		this.context = context;
		this.url = url;
	}

	protected QLHttpUtil(Context context, String url, Map<String, Object> entity) {
		this.context = context;
		this.url = url;
		this.entity = entity;
	}

	protected QLHttpUtil(Context context, String url, String entity) {
		this.context = context;
		this.url = url;
		setEntity(entity);
	}

	void setState(int state) {
		this.state = state;
	}

	/** Get/Post ***/
	public void setRequestType(QLHttpMethod type) {
		requestType = type;
	}

	public void setCacheType(int cacheType) {
		this.cacheType = cacheType;
	}

	/** 返回连接状态 */
	public int getState() {
		return state;
	}

	public boolean isUseCache() {
		return useCache;
	}

	public void setUseCache(boolean useCache) {
		this.useCache = useCache;
	}

	public boolean isNotifyUseCache() {
		return notifyUseCache;
	}

	/**
	 * 设置
	 * 
	 * @param cacheTimeout
	 */
	protected void setCacheTimeOut(int cacheTimeout) {
		defaultCacheTimeOut = cacheTimeout;
	}

	/**
	 * 手动自定义获取缓存的cacheKey
	 * 
	 * @param cacheKey
	 */
	public void setCacheKey(String cacheKey) {
		this.cacheKey = cacheKey;
	}

	public void setNotifyUseCache(boolean notifyUseCache) {
		this.notifyUseCache = notifyUseCache;
	}

	public String getName() {
		return name;
	}

	public String getLogName() {
		if (TextUtils.isEmpty(name))
			return "";
		return "[ " + name + " ]";
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, ? extends Object> getEntity() {
		return entity;
	}

	/**
	 * @param entity
	 *            请求数据参数<key,value>
	 */
	public void setEntity(Map<String, ? extends Object> entity) {
		this.entity = entity;
	}

	/**
	 * @param strEntity
	 *            请求数据参数<String> key=value&key=value
	 */
	public Map<String, ? extends Object> setEntity(String strEntity) {
		HashMap<String, String> entity = new HashMap<String, String>();
		if (strEntity != null && !TextUtils.isEmpty(strEntity)) {
			if (strEntity.substring(0, 1).equals("&")
					|| strEntity.substring(0, 1).equals("?"))
				strEntity = strEntity.substring(1, strEntity.length());
			String array[] = strEntity.split("&");
			for (int i = 0; i < array.length; i++) {
				String str = array[i];
				if (TextUtils.isEmpty(str)) {
					continue;
				}
				QLLog.i(tag, getLogName() + "key=value : " + str);
				entity.put(str.substring(0, str.indexOf("=")),
						str.substring(array[i].indexOf("=") + 1, str.length()));
			}
		}
		this.entity = entity;
		return entity;
	}

	/**
	 * 重置请求url
	 * 
	 * @param url
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	protected String resetUrlWithEntity(String url,
			Map<String, ? extends Object> entity, boolean asCacheKey)
			throws UnsupportedEncodingException {
		String string = url;
		if (QLHttpMethod.HTTPPOST.equals(requestType) && !asCacheKey) {
			return string;
		}
		QLLog.i(tag, getLogName() + "url重置前 : >>>" + url);
		if (TextUtils.isEmpty(url) || entity == null || entity.isEmpty()) {

		} else if (url.lastIndexOf("?") >= 0
				&& !url.substring(url.length() - 1, url.length()).equals("?")
				&& !url.substring(url.length() - 1, url.length()).equals("&")) {
			string = url + "&" + resetEntity(entity);
		} else if (url.lastIndexOf("?") >= 0
				&& (url.substring(url.length() - 1, url.length()).equals("?") || url
						.substring(url.length() - 1, url.length()).equals("&"))) {
			string = url + resetEntity(entity);
		} else if (url.lastIndexOf("?") < 0) {
			string = url + "?" + resetEntity(entity);
		}

		QLLog.i(tag, getLogName() + "url重置后 --> " + string);
		return string;
	}

	/**
	 * 重置请求参数
	 * 
	 * @param entity
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	protected String resetEntity(Map<String, ? extends Object> entity)
			throws UnsupportedEncodingException {
		String params = "";
		if (entity != null && !entity.isEmpty()) {
			for (String name : entity.keySet()) {
				Object value = entity.get(name);
				QLLog.i(tag, getLogName() + "key=value : " + name + "=" + value);
				if (value != null) {
					params += name + "="
							+ URLEncoder.encode(value.toString(), encoder)
							+ "&";
				} else {
					QLLog.i(tag, getLogName() + "value=null");
				}
			}
			if (params.length() > 0) {
				if (params.substring(params.length() - 1, params.length())
						.equals("&"))
					params = params.substring(0, params.length() - 1);
			}

		}
		QLLog.i(tag, getLogName() + "实体重置后 : " + params);
		return params;
	}

	/**
	 * @param urlEncoder
	 *            字符编码(默认UTF-8)
	 */
	public void setEncoder(String urlEncoder) {
		this.encoder = urlEncoder;
	}

	/**
	 * @param returnType
	 *            设置返回类型
	 */
	public void setReturnType(int returnType) {
		this.returnType = returnType;
	}

	/**
	 * @param connectionTimeOut
	 *            设置连接超时
	 */
	public void setConnectionTimeOut(int connectionTimeOut) {
		this.connectionTimeOut = connectionTimeOut;
	}

	/**
	 * 设置url
	 * 
	 * @param url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/** 停止连接 */
	public abstract void abort();

	/** 设置设置请求头 */
	abstract void doHttpParams(HttpParams obj);

	/** 开始连接网络 */
	public abstract QLHttpReply startConnection();

	/** 开始连接网络 */
	public abstract void startConnection(QLHttpResult click);

}
