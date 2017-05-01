package org.ql.utils.network;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ql.utils.QLToastUtils;
import org.ql.utils.debug.QLLog;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
import android.net.NetworkInfo;
import android.net.Proxy;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Patterns;
import android.webkit.WebView;
import android.widget.Toast;

/**
 * 
 * @author Sim
 * 
 */
public class QLNetworkTool {
	private final static String TAG = QLNetworkTool.class.getSimpleName();

	public enum NetworkType {
		WIFI, CMWAP, CMNET, UNIWAP, UNINET, UNICOM3GWAP, UNICOM3GNET, CTNET, CTWAP, DEFAULT
	}

	/**
	 * 是否使用SIM卡网络上网 <br/>
	 * 
	 * @return
	 */
	public static boolean isPhoneConnection() {
		String ip = null;
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						ip = inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
		}
		return (ip == null || ip.equals("0.0.0.0")) ? false : true;
	}
	
	/**
	 * 判断是否为合法的网络url地址
	 * @param url
	 * @return
	 */
	public static boolean isValiableURL(String url){
		if (url == null || TextUtils.isEmpty(url)) {
			return false;
		}
		String regex = "^(https?|http)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(url);
		return matcher.matches();
	}

	/**
	 * 获取APN Port,如果失败，return -1
	 * 
	 * @param mContext
	 * @return
	 */
	public static int getApnPort(Context mContext) {
		String port = null;
		Uri uri = Uri.parse("content://telephony/carriers/preferapn");
		Cursor c = mContext.getContentResolver().query(uri, null, null, null, null);
		while (c != null && c.moveToNext())
			port = c.getString(c.getColumnIndex("port"));
		if (c != null)
			c.close();
		int iport = -1;
		try {
			iport = Integer.parseInt(port);
		} catch (NumberFormatException nfe) {
			iport = -1;
		}
		return iport;
	}

	public static String getHostAndPort(String url) {
		String host = null;
		String tmp = null;
		if (url.startsWith("http://"))
			tmp = url.substring(7);
		int index = -1;
		index = tmp.indexOf("/");
		host = tmp.substring(0, index);
		QLLog.i(TAG, "url :" + url + " 's host=" + host);
		return host;
	}

	public static String getConnectString(String url) {
		String params = null;
		String tmp = null;
		// http://hsot:port/params
		if (url.startsWith("http://")) {
			tmp = url.substring(7);
		}
		int index = -1;
		index = tmp.indexOf("/");
		params = tmp.substring(index);
		QLLog.i(TAG, "url parms= " + params);
		return params;
	}

	/**
	 * 获取主机名
	 * 
	 * @param url
	 * @return
	 */
	public static String getHost(String url) {
		String u = url;
		String host = null;
		int idx = -1;
		idx = url.indexOf("://");
		if (-1 != idx) {
			url = url.substring(idx + 3);
		}
		idx = url.indexOf("/");
		if (-1 == idx)
			host = url;
		else {
			host = url.substring(0, idx);
			idx = host.indexOf(":");
			if (-1 != idx) {
				host = host.substring(0, idx);
			}
		}
		QLLog.i("NetWorkTool", u + "->host=" + host);
		return host;
	}

	/**
	 * 得到端口
	 * 
	 * @param url
	 * @param defaultPort
	 * @return
	 */
	public static int getPort(String url, int defaultPort) {
		String u = url;
		int port = defaultPort;
		String strPort = null;
		int idx = -1;
		idx = url.indexOf("://");
		if (-1 != idx) {
			url = url.substring(idx + 3);
		}
		idx = url.indexOf("/");
		if (-1 == idx)
			strPort = url;
		else {
			strPort = url.substring(0, idx);
			idx = strPort.indexOf(":");
			if (-1 != idx) {
				strPort = strPort.substring(idx + 1);
				try {
					port = Integer.parseInt(strPort);
				} catch (NumberFormatException nfe) {
					port = defaultPort;
				}
				// port=Integer.getInteger(strPort, defaultPort);
			}
		}
		QLLog.i("NetWorkTool", u + "->port=" + port + "  strPort=" + strPort);
		return port;
	}

	/**
	 * 得到url中的相对路径
	 * 
	 * @param url
	 * @return
	 */
	public static String getRequestRelative(String url) {
		String u = url;
		;
		String relativeStr = null;
		int idx = -1;
		idx = url.indexOf("://");
		if (-1 != idx) {
			url = url.substring(idx + 3);
		}
		idx = url.indexOf("/");
		if (-1 != idx) {
			relativeStr = url.substring(idx);
		} else {
			relativeStr = "";
		}
		QLLog.i("NetWorkTool", u + "->relativeStr=" + relativeStr);
		return relativeStr;
	}

	/**
	 * @param mContext
	 * @param showToast
	 *            网络不稳定时是否《Toast》提示信息（默认：当前网络不稳定，请稍后在试）
	 * @param showMessage
	 *            网络不稳定时《Toast》提示的信息
	 * @return true = 可用、false = 不可用
	 */
	public static boolean isConnectInternet(Context context, boolean showToast, String showMessage) {
		boolean netSataus = false;
		ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
		if (networkInfo != null)
			netSataus = networkInfo.isAvailable();
		if (!netSataus && showToast){
			QLToastUtils.showToast(context, showMessage);
		}
		QLLog.i(TAG, netSataus ? "当前网络可用" : "当前网络不可用");
		return netSataus;
	}

	/**
	 * 判断当前网络是否可用<br/>
	 * 
	 * @param context
	 * @param showToast
	 *            网络不稳定时是否《Toast》提示信息（默认：当前网络不稳定，请稍后在试）
	 * @return true = 可用、false = 不可用
	 */
	public static boolean isConnectInternet(Context context, boolean showToast) {
		return isConnectInternet(context, showToast, "当前网络不稳定，请稍后再试");
	}

	public static boolean isConnectInternet(Context context) {
		return isConnectInternet(context, false);
	}

	/**
	 * 判断WIFI是否开启<br/>
	 * 
	 * @param context
	 * @return true = 开启、false = 关闭
	 */
	public static boolean isWifiOpen(Context context) {
		Context inContext = context.getApplicationContext();
		WifiManager wifiManager = (WifiManager) inContext.getSystemService(Context.WIFI_SERVICE);
		QLLog.i(TAG, wifiManager.isWifiEnabled() ? "WIFI已开启" : "WIFI已关闭");
		return wifiManager.isWifiEnabled();
	}

	private static boolean isConnected(Context context, int networkType) {
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mNetworkInfo = connManager.getNetworkInfo(networkType);
		return mNetworkInfo.isConnected();
	}

	/**
	 * 判断WIFI是否连接上<br/>
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isWifiConnected(Context context) {
		boolean connected = isConnected(context, ConnectivityManager.TYPE_WIFI);
		QLLog.i(TAG, connected ? "WIFI已连接上" : "WIFI尚未连接");
		return connected;
	}

	/**
	 * 是否使用SIM卡网络上网 <br/>
	 * 
	 * @return
	 */
	public static boolean isMobileConnected(Context context) {
		boolean connected = isConnected(context, ConnectivityManager.TYPE_MOBILE);
		QLLog.i(TAG, connected ? "MOBILE已连接上" : "MOBILE尚未连接");
		return connected;
	}

	/** 未知 */
	public final static int OPERATOR_NONE = 0;

	/** 移动 */
	public final static int OPERATOR_CMCC = 1;

	/** 联通 */
	public final static int OPERATOR_UNI = 2;

	/** 电信 */
	public final static int OPERATOR_CHINANET = 3;

	/**
	 * OPERATOR_NONE = 未知 <br/>
	 * OPERATOR_CMCC = 移动 <br/>
	 * OPERATOR_UNI = 联通 <br/>
	 * OPERATOR_CHINANET = 电信 <br/>
	 * 
	 * @param context
	 * @return
	 */
	public static int getSimOperatorInt(Context context) {
		int operato = OPERATOR_NONE;
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String operator = tm.getSimOperator() != null ? tm.getSimOperator() : "";
		QLLog.i(TAG, "运行商编号 --> " + operator);
		if ((operator.equals("46000") || operator.equals("46002") || operator.equals("46007")))
			operato = OPERATOR_CMCC;
		else if (operator.equals("46001"))
			operato = OPERATOR_UNI;
		else if (operator.equals("46003"))
			operato = OPERATOR_CHINANET;
		return operato;
	}

	public static String getIpAddress() {
		try {
			for(Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();en.hasMoreElements();){
				NetworkInterface intf = en.nextElement();
				for(Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();){
					InetAddress inetAddress = enumIpAddr.nextElement();
					if(!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address)){
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			QLLog.e(ex.getMessage());
		}
		return "";
	}
	
	public static String getIpAddress(Context context) {
		return Build.VERSION.SDK_INT >= 14 ? getIpAddress() : isWifiConnected(context) ? getIpAddressFromWifi(context) : getIpAddressFromPhone();
	}

	public static String getIpAddressFromWifi(Context context) {
		// 获取wifi服务
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		// 判断wifi是否开启
		if (wifiManager.isWifiEnabled())
			return null;
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		String ip = (ipAddress & 0xFF) + "." + ((ipAddress >> 8) & 0xFF) + "." + ((ipAddress >> 16) & 0xFF) + "." + (ipAddress >> 24 & 0xFF);
		return ip;
	}

	public static String getIpAddressFromPhone() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
		}
		return null;
	}


	/**
	 * 当前接入点是否是代理（proxy = 10.0.0.172、010.000.000.172）<br/>
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isProxy(Context context) {
		String proxy = getProxyHost(context);
		return (proxy != null && !proxy.equals("")) ? true : false;
	}

	/**
	 * @param context
	 * @return 当前APN proxy
	 */
	public static String getApnProxy(Context context) {
		String proxy = null;
		Uri u = Uri.parse("content://telephony/carriers/preferapn");// 当前使用的接入点
		Cursor c = context.getContentResolver().query(u, null, null, null, null);
		if (c != null && c.moveToFirst())
			proxy = c.getString(c.getColumnIndex("proxy"));
		if (c != null) {
			c.close();
		}
		QLLog.i(TAG, "proxy=" + proxy);
		QLLog.i(TAG, "c==null:" + (c == null));
		return proxy;
	}

	/**
	 * @param context
	 * @return 当前APN proxy
	 */
	public static int getApnProxyPort(Context context) {
		int port = 0;
		Uri u = Uri.parse("content://telephony/carriers/preferapn");// 当前使用的接入点
		Cursor c = context.getContentResolver().query(u, null, null, null, null);
		if (c != null && c.moveToFirst())
			port = c.getInt(c.getColumnIndex("port"));
		if (c != null) {
			c.close();
		}
		QLLog.i(TAG, "port=" + port);
		QLLog.i(TAG, "c==null:" + (c == null));
		return port;
	}

	// 获取代理IP
	public static String getProxyHost(Context context) {
		String proxyHost = Proxy.getHost(context);
		QLLog.i(TAG, "proxyHost=" + proxyHost);
		return !TextUtils.isEmpty(proxyHost) ? proxyHost : getApnProxy(context);
	}

	// 获取代理端口
	public static int getProxyPort(Context context) {
		int proxyPort = Proxy.getPort(context);
		QLLog.i(TAG, "proxyPort=" + proxyPort);
		proxyPort = proxyPort > 0 ? proxyPort : getApnProxyPort(context);
		return proxyPort < 0 ? 80 : proxyPort;
	}

	// 获取手机UA（webview UA）
	public static String getDeviceUA(Context context) {
		SharedPreferences sp = context.getSharedPreferences("themeHttpClient", Context.MODE_PRIVATE);
		String deviceUA = sp.getString("deviceUserAgent", "");
		if (deviceUA == null || deviceUA.equals("")) {
			deviceUA = new WebView(context).getSettings().getUserAgentString();
			sp.edit().putString("deviceUserAgent", deviceUA).commit();
		}
		return deviceUA;
	}

	public static String getNetworkName(Context context) {
		String reply;
		if (!isConnectInternet(context)) {
			reply = "no network";
		} else if (isWifiConnected(context)) {
			reply = "wifi";
		} else {
			ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
			reply = networkInfo.getTypeName();
		}
		return reply;
	}

	public final static String TYPE_NETWORK_NONET = "nonet";
	public final static String TYPE_NETWORK_WAP = "wap";
	public final static String TYPE_NETWORK_NET = "net";
	public final static String TYPE_NETWORK_WIFI = "wifi";

	private static String networkType = TYPE_NETWORK_NONET;

	private static void initNetType(Context context) {
		// 获取网络连接管理者
		ConnectivityManager connectionManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (isWifiConnected(context)) {
			networkType = TYPE_NETWORK_WIFI;
		} else {
			NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
			if (null != networkInfo) {
				QLLog.e(TAG, networkInfo.getExtraInfo());
				networkType = networkInfo.getExtraInfo();
				String proxyHost = android.net.Proxy.getDefaultHost();
				if (null != proxyHost) {
					QLLog.e(TAG, proxyHost);
					if (proxyHost.indexOf("10.0.0.") != -1) {
						networkType = TYPE_NETWORK_WAP;
					}
				} else {
					proxyHost = "";
					networkType = TYPE_NETWORK_NET;
				}
			} else {
				networkType = TYPE_NETWORK_NONET;
			}
		}

	}

	/**
	 * initNetType
	 * */
	public static String getNetWorkType(Context context) {
		initNetType(context);
		return networkType.toLowerCase();
	}

	/**
	 * 是否使用中国电信CDMA网络
	 * */
	public static boolean isCDMA(Context context) {
		boolean tmp = false;
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if (null != tm && tm.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
			QLLog.e(TAG, tm.getSimOperatorName());
			// if(tm.getSimOperatorName().equals("中国电信")){
			tmp = true;
			// }
		}

		return tmp;
	}

	public static boolean haveSIM(Context context) {
		TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		int simState = mTelephonyManager.getSimState();
		String mString = "获取sim卡状态失败";
		switch (simState) {
		case TelephonyManager.SIM_STATE_ABSENT:
			mString = "无卡";
			return false;
		case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
			mString = "需要NetworkPIN解锁";
			return true;
		}
		QLLog.e(TAG, mString);
		return true;
	}
}
