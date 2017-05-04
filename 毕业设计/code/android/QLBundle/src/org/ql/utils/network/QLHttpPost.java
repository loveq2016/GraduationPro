package org.ql.utils.network;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.QLConstant;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.ql.utils.debug.QLLog;

import android.content.Context;

public class QLHttpPost extends QLHttpRequestBase{
	private final String tag = QLHttpPost.class.getSimpleName();
	
	public QLHttpPost(Context mContext) {
		super(mContext);
	}
	
	public QLHttpPost(Context mContext,String url) {
		super(mContext,url);
	}
	
	public QLHttpPost(Context mContext,String url,Map<String,Object> entity) {
		super(mContext,url,entity);
	}
	
	public QLHttpPost(Context mContext,String url,String entity) {
		super(mContext,url,entity);
	}
	
	protected HttpEntity getHttpEntity(){
		List<NameValuePair> listEntity = new ArrayList<NameValuePair>();
		if (entity != null && !entity.isEmpty()) {
			for (String name : entity.keySet()) {
				Object value = entity.get(name);
				QLLog.i(tag, getLogName()+"key=value : "+name+"="+value);
				if(value != null){
					listEntity.add(new BasicNameValuePair(name,value.toString()));
				}else{
					QLLog.i(tag, getLogName()+"value=null");
				}
			}
		}
		try {
			return new UrlEncodedFormEntity(listEntity, encoder);
		} catch (UnsupportedEncodingException e) {
			//e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 设置请求实体
	 * @param entity 请求数据参数(strParams结构key=value&key=value)
	 */
	@Override
	public Map<String,? extends Object> setEntity(String entity){
		Map<String,? extends Object> params = super.setEntity(entity);
		setEntity(params);
		return params;
	}

	protected final HttpPost mHttpPost = new HttpPost();
	@Override
	protected HttpResponse getHttpResponse(HttpClient mHttpClinet) throws IOException {
		boolean valiableUrlString = QLNetworkTool.isValiableURL(url);
		if (!valiableUrlString) {
			throw new IOException(url + "is not a avaliabe url.");
		}
		QLLog.v(tag, getLogName()+"url = "+url);
		mHttpPost.setURI(URI.create(url));
		HttpEntity mHttpEntity = getHttpEntity();
		if(mHttpEntity != null){
			mHttpPost.setEntity(mHttpEntity);
			mHttpEntity.consumeContent();
		}
//		mHttpPost.addHeader("smc-client-model", QLConstant.smc_client_model);
//		mHttpPost.addHeader("smc-client-version", QLConstant.smc_client_version);
//		mHttpPost.addHeader("smc-connect-mode", QLConstant.smc_connect_mode);
//		mHttpPost.addHeader("smc-imei", QLConstant.smc_imei);
//		mHttpPost.addHeader("smc-imsi", QLConstant.smc_imsi);
//		mHttpPost.addHeader("smc-user-account", QLConstant.smc_user_account);
//		mHttpPost.addHeader("smc-user-mobile", QLConstant.smc_user_mobile);
//		mHttpPost.addHeader("smc-rid", QLConstant.smc_rid);
		return mHttpClinet.execute(mHttpPost);
	}

	@Override
	public void abort() {
		super.abort();
		mHttpPost.abort();
	}

}
