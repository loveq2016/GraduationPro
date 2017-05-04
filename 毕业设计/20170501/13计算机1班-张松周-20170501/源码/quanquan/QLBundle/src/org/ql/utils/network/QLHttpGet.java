package org.ql.utils.network;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import org.QLConstant;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import android.content.Context;

public class QLHttpGet extends QLHttpRequestBase{
	protected final String tag = QLHttpGet.class.getSimpleName();
	
	public QLHttpGet(Context mContext) {
		super(mContext);
	}
	
	public QLHttpGet(Context mContext,String url) {
		super(mContext,url);
	}
	
	public QLHttpGet(Context mContext,String url,Map<String,Object> entity) {
		super(mContext,url,entity);
	}
	
	public QLHttpGet(Context mContext,String url,String entity) {
		super(mContext,url,entity);
	}
	
	protected final HttpGet mHttpGet = new HttpGet();
	@Override
	protected HttpResponse getHttpResponse(HttpClient mHttpClinet) throws IOException {
		mHttpGet.addHeader("smc-client-model", QLConstant.smc_client_model);
		mHttpGet.addHeader("smc-client-version", QLConstant.smc_client_version);
		mHttpGet.addHeader("smc-connect-mode", QLConstant.smc_connect_mode);
		mHttpGet.addHeader("smc-imei", QLConstant.smc_imei);
		mHttpGet.addHeader("smc-imsi", QLConstant.smc_imsi);
		mHttpGet.addHeader("smc-user-account", QLConstant.smc_user_account);
		mHttpGet.addHeader("smc-user-mobile", QLConstant.smc_user_mobile);
		mHttpGet.addHeader("smc-rid", QLConstant.smc_rid);
		boolean valiableUrlString = QLNetworkTool.isValiableURL(url);
		if (!valiableUrlString) {
			throw new IOException(url + "is not a avaliabe url.");
		}
		String urlString = resetUrlWithEntity(url, entity, false);
		URI uri = URI.create(urlString);
		mHttpGet.setURI(uri);
//		String entiyString = resetEntity(entity).trim();
//		if ("".equals(entiyString)) {
//			mHttpGet.setURI(URI.create(url));
//		}else{
//			mHttpGet.setURI(URI.create(resetUrl(url)+resetEntity(entity)));
//		}
		
		return mHttpClinet.execute(mHttpGet);
	}

	@Override
	public void abort() {
		super.abort();
		mHttpGet.abort();
	}
}
