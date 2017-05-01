package org.ql.utils.network;

import java.util.Map;

import android.content.Context;


public class QLHttpManager{
	
	public enum QLHttpMethod {
		HTTPPOST,//创建HttpPosts对象
		HTTPGET,//创建HttpGets对象
	}
	

	private QLHttpManager(){};
	
	public static QLHttpUtil create(Context mContext){
		return create(mContext,QLHttpMethod.HTTPGET);
	}
	
	public static QLHttpUtil create(Context mContext,QLHttpMethod mHttpMethod){
		QLHttpUtil mHttpUtils = null;
		if(mHttpMethod == QLHttpMethod.HTTPPOST){
			mHttpUtils = new QLHttpPost(mContext);
		}else{
			mHttpUtils = new QLHttpGet(mContext);
		}
		mHttpUtils.setRequestType(mHttpMethod);
		return mHttpUtils;
	}

	public static QLHttpUtil create(Context mContext,QLHttpMethod mHttpMethod,boolean useCache,boolean notityUseCache){
		QLHttpUtil mHttpUtils = null;
		if(mHttpMethod == QLHttpMethod.HTTPPOST){
			mHttpUtils = new QLHttpPost(mContext);
		}else{
			mHttpUtils = new QLHttpGet(mContext);
		}
		mHttpUtils.setRequestType(mHttpMethod);
		mHttpUtils.setUseCache(useCache);
		mHttpUtils.setNotifyUseCache(notityUseCache);
		return mHttpUtils;
	}
	
	public static QLHttpUtil create(Context mContext,String url){
		return create(mContext,url,QLHttpMethod.HTTPGET);
	}
	
	public static QLHttpUtil create(Context mContext,String url,QLHttpMethod mHttpMethod){
		QLHttpUtil mHttpUtils = create(mContext,mHttpMethod);
		mHttpUtils.setUrl(url);
		return mHttpUtils;
	}
	
	public static QLHttpUtil create(Context mContext,String url,Map<String,Object> entity){
		return create(mContext,url,entity,QLHttpMethod.HTTPGET);
	}
	
	public static QLHttpUtil create(Context mContext,String url,Map<String,Object> entity,QLHttpMethod mHttpMethod){
		QLHttpUtil mHttpUtils = create(mContext,mHttpMethod);
		mHttpUtils.setUrl(url);
		mHttpUtils.setEntity(entity);
		return mHttpUtils;
	}
	
	public static QLHttpUtil create(Context mContext,String url,String entity){
		return create(mContext,url,entity,QLHttpMethod.HTTPGET);
	}
	
	public static QLHttpUtil create(Context mContext,String url,String entity,QLHttpMethod mHttpMethod){
		QLHttpUtil mHttpUtils = create(mContext,mHttpMethod);
		mHttpUtils.setUrl(url);
		mHttpUtils.setEntity(entity);
		return mHttpUtils;
	}
	
	
}
