package org.ql.utils.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.CookiePolicy;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.QLConstant;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.ql.utils.QLSingleton;
import org.ql.utils.QLStringUtils;
import org.ql.utils.QLToastUtils;
import org.ql.utils.cache.QLFileCacheUtils;
import org.ql.utils.debug.QLLog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

public abstract class QLHttpRequestBase extends QLHttpUtil {

	private final String TAG = QLHttpRequestBase.class.getSimpleName();
	private boolean cache;
	private HttpClient mHttpClinet;
	final Object objConnection = new Object();
	/** 读取超时 */
	protected int osTimeOut = 0;

	private final Object lock = new Object();

	private QLFileCacheUtils fileCacheUtils;

	protected QLHttpRequestBase(Context context) {
		super(context);
		fileCacheUtils = new QLFileCacheUtils(context);
	}

	protected QLHttpRequestBase(Context context, String url) {
		super(context, url);
		fileCacheUtils = new QLFileCacheUtils(context);
	}

	protected QLHttpRequestBase(Context context, String url,
			Map<String, Object> entity) {
		super(context, url, entity);
		fileCacheUtils = new QLFileCacheUtils(context);
	}

	protected QLHttpRequestBase(Context context, String url, String entity) {
		super(context, url, entity);
		fileCacheUtils = new QLFileCacheUtils(context);
	}

	/**
	 * MD5加密
	 * 
	 * @param source
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static String encode(byte[] source) {
		try {
			char hexDigits[] = { // 用来将字节转换成 16 进制表示的字符
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c',
					'd', 'e', 'f' };
			java.security.MessageDigest md = java.security.MessageDigest
					.getInstance("MD5");
			md.update(source);
			// MD5 的计算结果是一个 128 位的长整数，
			byte tmp[] = md.digest();
			// 用字节表示就是 16 个字节
			// 每个字节用 16 进制表示的话，使用两个字符，
			char str[] = new char[16 * 2];
			// 所以表示成 16 进制需要 32 个字符
			// 表示转换结果中对应的字符位置
			int k = 0;
			for (int i = 0; i < 16; i++) {
				// 从第一个字节开始，对 MD5 的每一个字节
				// 转换成 16 进制字符的转换
				// 取第 i 个字节
				byte byte0 = tmp[i];
				// 取字节中高 4 位的数字转换,
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				// >>> 为逻辑右移，将符号位一起右移
				// 取字节中低 4 位的数字转换
				str[k++] = hexDigits[byte0 & 0xf];
			}
			String s = new String(str);
			md = null;
			// 换后的结果转换为字符串
			return s != null ? s : null;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 设置读取超时
	 * 
	 * @param osTimeOut
	 */
	public void setOsTimeOut(int osTimeOut) {
		this.osTimeOut = osTimeOut;
	}

	protected abstract HttpResponse getHttpResponse(HttpClient mHttpClinet)
			throws IOException;

	public String getCacheKey() {
		if (this.cacheKey != null) {
			return this.cacheKey;
		}
		try {
			return resetUrlWithEntity(url, entity, true);// resetUrl(url)+resetEntity(entity);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void abort() {
		state = STATE_ABORT;
	}

	public void onCallBack(QLHttpReply reply, QLHttpResult listener) {
		if (listener != null)
			listener.reply(reply);
	}

	@SuppressLint("NewApi")
	@Override
	public void startConnection(final QLHttpResult onHttpResule) {
		AsyncTask<Void, Void, QLHttpReply> task = new AsyncTask<Void, Void, QLHttpReply>() {
			@Override
			protected QLHttpReply doInBackground(Void... params) {
				return connect();
			}

			@Override
			protected void onPostExecute(QLHttpReply result) {
				QLLog.v(TAG, getLogName() + "state != STATE_ABORT ? "
						+ (state != STATE_ABORT));
				QLLog.v(TAG, getLogName() + "onHttpResule != null ? "
						+ (onHttpResule != null));
				QLLog.v(TAG, getLogName() + "context != null ? "
						+ (context != null));
				QLLog.v(TAG, getLogName() + "context instanceof Activity ? "
						+ (context instanceof Activity));
				if (context instanceof Activity) {
					QLLog.v(TAG, getLogName()
							+ "!((Activity)context).isFinishing() ? "
							+ (!((Activity) context).isFinishing()));
				}
				QLLog.v(TAG, getLogName() + "!(context instanceof Activity) ? "
						+ (!(context instanceof Activity)));

				if (state != STATE_ABORT && onHttpResule != null
						&& context != null) {
					if (context instanceof Activity
							&& !((Activity) context).isFinishing()) {
						QLLog.v(TAG, getLogName()
								+ " Activity ................................");
						if (useCache && cache && notifyUseCache) {
							QLToastUtils.showToast(context, "网络不给力！");
						}
						onCallBack(result, onHttpResule);
					} else if (!(context instanceof Activity)) {
						QLLog.v(TAG, getLogName()
								+ " Context ................................");
						onCallBack(result, onHttpResule);
					}
				}
				super.onPostExecute(result);
			}

			@Override
			protected void onCancelled() {
				super.onCancelled();
			}
		};

		int version = android.os.Build.VERSION.SDK_INT;
		if (version >= 11) {
			task.executeOnExecutor((ExecutorService) Executors
					.newCachedThreadPool());
		} else {
			task.execute();
		}

	}

	@Override
	public QLHttpReply startConnection() {
		return connect();
	}

	String getCacheWithKey(String key) {

		return null;
	}

	QLHttpReply connect() {
		synchronized (lock) {
			String fillUrl = getCacheKey();
			/*
			 * if(QLConstant.portalId!=null&&fillUrl.indexOf("portalId=")==-1){
			 * url = QLStringUtils.addParamToUrl(url, "portalId",
			 * QLConstant.portalId); fillUrl =
			 * QLStringUtils.addParamToUrl(fillUrl, "portalId",
			 * QLConstant.portalId); }
			 */
			QLLog.e(getLogName() + "完整地址：" + fillUrl);
			String cacheKey = encode(fillUrl.getBytes());
			QLHttpReply reply = new QLHttpReply();
			if (state == STATE_ABORT || cacheKey == null) {
				return reply;
			}
			state = STATE_CONNECTING;
			Object result = null;

			// 检查是否使用缓存(标准缓存策略)
			if (useCache && fileCacheUtils.containsKey(cacheKey)
					&& !fileCacheUtils.isTimeOut(cacheKey)
					&& QLHttpUtil.CACHE_TYPE_NORMAL == cacheType) {
				result = fileCacheUtils.getString(cacheKey);
				if (null != result) {
					cache = true;
					reply.setCode(200);
					reply.setReplyMsg(result);
					reply.setCache(cache);
					QLLog.e(getLogName() + "缓存未超时，使用缓存！" + fillUrl);
					return reply;
				}
			} else {
				// 正常路线
				try {
					// https 请求逻辑代码
					/*InputStream ins = null;
					ins = context.getAssets().open("tomcat.cer"); //下载的证书放到项目中的assets目录中
		            CertificateFactory cerFactory = CertificateFactory
		                    .getInstance("X.509");
		            Certificate cer = cerFactory.generateCertificate(ins);
		            KeyStore keyStore = KeyStore.getInstance("PKCS12", "BC");
		            keyStore.load(null, null);
		            keyStore.setCertificateEntry("trust", cer);

		            SSLSocketFactory socketFactory = new SSLSocketFactory(keyStore);
		            Scheme sch = new Scheme("https", socketFactory, 443);*/
					mHttpClinet = new DefaultHttpClient();
					/*mHttpClinet.getConnectionManager().getSchemeRegistry()
                    .register(sch);*/
					
					doHttpParams(mHttpClinet.getParams());
					HttpResponse httpResponse = getHttpResponse(mHttpClinet);
					int code = httpResponse.getStatusLine().getStatusCode();
					reply.setCode(code);
					QLLog.v(TAG, getLogName() + "code=" + code + "\n");
					if (state != STATE_ABORT && reply.isSuccessCode()) {
						if (returnType == RETURN_BYTE) {
							result = EntityUtils.toByteArray(httpResponse
									.getEntity());
						} else if (returnType == RETURN_STRING) {
							result = EntityUtils.toString(
									httpResponse.getEntity(), encoder);
							Header[] headers = httpResponse
									.getHeaders("smc-cache-time");
							if (headers != null && headers.length > 0) {
								String str = headers[0].getValue();
								if (!QLStringUtils.isEmpty(str)) {
									int timeOut = Integer.parseInt(str);
									fileCacheUtils.putString(fillUrl, cacheKey,
											result.toString(), timeOut);
								}
							} else {
								fileCacheUtils.putString(fillUrl, cacheKey,
										result.toString(), defaultCacheTimeOut);
							}
						} else if (returnType == RETURN_INPUTSTREAM) {
							result = httpResponse.getEntity().getContent();
						}
						state = STATE_CONNECTED;
						cache = false;
					}
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (mHttpClinet != null) {
						mHttpClinet.getConnectionManager().shutdown();
						mHttpClinet = null;
					}
				}
				if (result == null) {
					if (useCache) {
						QLLog.e("网络连接失败，使用缓存！" + fillUrl);
						result = fileCacheUtils.getString(cacheKey);
						if (null != result) {
							cache = true;
						}
					}
				} else if (result instanceof String) {
					if (((String) result).contains("token不符")) {
						//TODO by10-28
						if (QLSingleton.getInstance()
								.getBackToLoginActListener() != null) {
							QLSingleton.getInstance()
									.getBackToLoginActListener().onCallBack();

						}

					}
				}
				QLLog.v(TAG, getLogName() + "result==null?" + (result == null));
				QLLog.v(TAG, getLogName() + "result=" + result);
				reply.setReplyMsg(result);
				reply.setCache(cache);
			}
			return reply;
		}
	}

	@Override
	void doHttpParams(HttpParams obj) {
		// 创建 HttpParams 以用来设置 HTTP 参数
		final HttpParams httpParams = obj;
		connectionTimeOut = connectionTimeOut > 9000 ? connectionTimeOut
				: 15 * 1000;
		osTimeOut = osTimeOut > 9000 ? osTimeOut : 15 * 1000;
		QLLog.i(TAG, getLogName() + "连接超时=" + connectionTimeOut);
		QLLog.i(TAG, getLogName() + "读取超时=" + osTimeOut);
		// 设置连接超时和 Socket 超时，以及 Socket 缓存大小
		HttpConnectionParams
				.setConnectionTimeout(httpParams, connectionTimeOut);
		HttpConnectionParams.setSoTimeout(httpParams, osTimeOut);
		HttpConnectionParams.setSocketBufferSize(httpParams, 1024 * 8);
		HttpConnectionParams.setStaleCheckingEnabled(httpParams, true);
		HttpClientParams
				.setCookiePolicy(
						httpParams,
						org.apache.http.client.params.CookiePolicy.BROWSER_COMPATIBILITY);
		// 设置代理
		// if(!QLNetworkTool.isWifiConnected(context) &&
		// QLNetworkTool.isProxy(context)){
		// String proxyHost = QLNetworkTool.getProxyHost(context);
		// if(!httpHost.getHostName().trim().equals(proxyHost))
		// HttpHost httpHost = new
		// HttpHost(proxyHost,QLNetworkTool.getProxyPort(context));
		// ConnRouteParams.setDefaultProxy(httpParams,httpHost);
		// QLLog.i(TAG,getLogName()+"connection mode: WAP");
		// }else{
		// QLLog.i(TAG,getLogName()+"connection mode: "+(QLNetworkTool.isWifiConnected(context)?"WIFI":"NET"));
		// }
		// 设置重定向，缺省为 true
		HttpClientParams.setRedirecting(httpParams, true);
	}
}
