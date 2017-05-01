package app.utils.managers;

import java.io.UnsupportedEncodingException;

import org.ql.utils.debug.QLLog;
import org.ql.utils.storage.QLSp;

import android.content.Context;
import android.content.Intent;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import app.logic.pojo.ECoordinateSysType;
import app.logic.pojo.TYLocationInfo;
import app.utils.common.AndroidFactory;
import app.utils.common.Listener;
import app.utils.common.Public;
import app.utils.debug.YYDebugHandler;
import app.yy.geju.R;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.CoordinateConverter.CoordType;
import com.google.gson.Gson;

/***
 * 
 * @author SiuJiYung create at 2013-12-3 下午2:40:14 </br>
 * 
 *         位置服务管理
 */
public class TYLocationManager implements BDLocationListener, android.location.GpsStatus.Listener, LocationListener, OnGetGeoCoderResultListener {

	
	public interface TYLocationListener{
		public void onRequestLocationStart();
		public void onRequestLocationFinish();
		public void onLocationChange(TYLocationInfo info);
		public void onGPSStatuChange(int event);
	}
	
	private static final double ee = 0.00669342162296594323;
	private static final double a = 6378245.0;
	private static final double pi = 3.14159265358979324;

	public static final String TYLOCATION_BORADCASE_KEY = "TYLOCATION_BORADCASE_KEY";
	/** 最大重试定位次数 **/
	private static final int maxInitRequestCount = 3;
	/** 自动刷新定位信息时间间隔 **/
	private int autoRequestLoctionScanTime = 6000;
	/** 失败重试计数器 **/
	private int requestLocationTimes = 0;
	private TYLocationListener listener = null;
	
	private LocationManager osLocationManager = null;
	private TYLocationInfo currLocationInfo;
	private LocationClient locationClient = null;
	private LocationClientOption locationClientOption = null;
//	private Listener<RequestResultCode, TYLocationInfo> locationListener = null;
	private Context mContext;
	private boolean gpsEnable = false;
	private GeoCoder gCoder;

	private static TYLocationManager shareInstance = null;

	public static TYLocationManager getShareLocationManager() {
		if (shareInstance == null) {
			shareInstance = new TYLocationManager();
		}
		return shareInstance;
	}

	private static final String SP_KEY_LOCATION_INFO = "SP_KEY_LOCATION_INFO";

	private TYLocationManager() {
		mContext = AndroidFactory.getApplicationContext();
//		osLocationManager = (LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);
//		osLocationManager.addGpsStatusListener(this);
//		osLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1, this);
//		osLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		SDKInitializer.initialize(mContext);
		gCoder = GeoCoder.newInstance();
		gCoder.setOnGetGeoCodeResultListener(this);
		locationClient = new LocationClient(mContext);     //声明LocationClient类
		locationClientOption = new LocationClientOption();
		String product_name = mContext.getString(R.string.app_name);
		locationClientOption.setProdName(product_name);
		locationClientOption.setLocationMode(LocationMode.Hight_Accuracy);//设置定位模式
		
		locationClientOption.setCoorType("gcj02");//返回的定位结果是百度经纬度,默认值gcj02 //bd09ll
		locationClientOption.setScanSpan(9000);//设置发起定位请求的间隔时间为5000ms
		locationClientOption.setIsNeedAddress(true);//返回的定位结果包含地址信息
		locationClientOption.setNeedDeviceDirect(true);//返回的定位结果包含手机机头的方向
		
		locationClient.setLocOption(locationClientOption);
		locationClient.registerLocationListener(this);
		
		// 读取上次缓存
		currLocationInfo = readLoactionFromDB();
	}
	
	public static TYLocationInfo WGS84TOBD0911(TYLocationInfo wgs84){
		LatLng sourceLatLng = new LatLng(wgs84.getLatitude(), wgs84.getLongitude());
		TYLocationInfo bd0911 = new TYLocationInfo();
		CoordinateConverter converter = new CoordinateConverter();
		converter.from(CoordType.GPS);
		converter.coord(sourceLatLng);
		LatLng desLatLng = converter.convert();
		bd0911.setLatitude(desLatLng.latitude);
		bd0911.setLongitude(desLatLng.longitude);
		return bd0911;
	}
	
	public static TYLocationInfo WGS84TOBD0911(double lat,double lnt){
		LatLng sourceLatLng = new LatLng(lat, lnt);
		TYLocationInfo bd0911 = new TYLocationInfo();
		CoordinateConverter converter = new CoordinateConverter();
		converter.from(CoordType.GPS);
		converter.coord(sourceLatLng);
		LatLng desLatLng = converter.convert();
		bd0911.setLatitude(desLatLng.latitude);
		bd0911.setLongitude(desLatLng.longitude);
		return bd0911;
	}
	
	public void setLocationListener(TYLocationListener l){
		listener = l;
	}
	
	private void saveLoactionToDB(TYLocationInfo info) {
		if (info == null) {
			return;
		}
		Gson gson = new Gson();
		String infoString = gson.toJson(info);
		QLSp sp = Public.getSp(mContext);
		sp.put(SP_KEY_LOCATION_INFO, infoString);
	}

	private TYLocationInfo readLoactionFromDB() {
		TYLocationInfo tmpInfo = null;
		QLSp sp = Public.getSp(mContext);
		String infoString = sp.get(SP_KEY_LOCATION_INFO, null);
		if (infoString != null) {
			Gson gson = new Gson();
			try {
				tmpInfo = gson.fromJson(infoString, TYLocationInfo.class);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return tmpInfo;
	}


	public double distanceBetweenStation(TYLocationInfo location){
		if(currLocationInfo == null || location == null || location.latitude < 10 || location.longitude < 10){
			return -1;
		}
		float[] results = new float[1];
		Location.distanceBetween(location.latitude, location.longitude, currLocationInfo.latitude, currLocationInfo.longitude, results);
		return results[0];
	}

	public TYLocationInfo getCurrLocationInfo() {
		if (currLocationInfo == null) {
			TYLocationInfo _locationInfo = new TYLocationInfo();
			_locationInfo.latitude = -1;
			_locationInfo.longitude = -1;
			_locationInfo.setGpsDate(Public.getTimeNow());
			_locationInfo.setGpsDateTime(Public.getCurrMillis());
			return _locationInfo;
		}
		return currLocationInfo;
	}

	/**
	 * 发送位置信息广播
	 * 
	 * @param locationInfo
	 */
	private void sendLocationBroadcase(TYLocationInfo locationInfo) {
		if (locationInfo == null) {
			return;
		}
		enableAutoRequestLoction(false);
		currLocationInfo = locationInfo;
		saveLoactionToDB(locationInfo);
		Gson gson = new Gson();
		String jsonString = gson.toJson(locationInfo);
		if (jsonString == null) {
			return;
		}
		Intent intent = new Intent(TYLOCATION_BORADCASE_KEY);
		intent.putExtra(TYLOCATION_BORADCASE_KEY, jsonString);
		mContext.sendBroadcast(intent);
	}

	public void setDateTimeSynchronizeListener(
			Listener<Long, Object> synListener) {
		// synchronizeListener = synListener;
	}

//	public void setLocationListener(
//			Listener<RequestResultCode, TYLocationInfo> l) {
//		locationListener = l;
//	}

	public boolean isGpsEnable(Context context) {
		if (context == null) {
			context = AndroidFactory.getApplicationContext();
		}
		LocationManager locationmng = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		return locationmng.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	public void openGpsSettings(Context context) {
		if (context == null) {
			context = AndroidFactory.getApplicationContext();
		}
		Intent intent = new Intent(
				android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		context.startActivity(intent);
	}


	public void enableAutoRequestLoction(boolean enable) {
		locationClientOption.setScanSpan((enable ? autoRequestLoctionScanTime
				: 9000));
	}

	public boolean isStart() {
		return locationClient.isStarted();
//		return true;
	}

	public void start() {
		locationClient.start();
	}

	public void stop() {
		if (locationClient != null && locationClient.isStarted()) {
			locationClient.stop();
		}
	}

	public void requestLocation() {
//		osLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, this);
		if (locationClient != null && locationClient.isStarted()) {
			enableAutoRequestLoction(false);
			locationClient.requestLocation();
		} else if(locationClient != null && !locationClient.isStarted()){
			locationClient.start();
			locationClient.requestLocation();
			QLLog.i(this.getClass().getName(),
					"locationClient is null or not started.");
		}else{
			YYDebugHandler.getShareInstance().reportError(mContext, " we can't request location.");
		}
	}

	public boolean locationResultInfo(BDLocation location) {
		if (location == null) {
			return false;
		}
		int locationCode = location.getLocType();
		switch (locationCode) {
		case 61:
		case 65:
		case 161:
			// 成功定位
//			QLLog.e(this.getClass().getName(), "定位成功,Code:" + locationCode);
			return true;
		case 63:
		case 68:
			// 网络异常，无法定位
			QLLog.e(this.getClass().getName(), "定位失败,网络异常,Code:" + locationCode);
			break;
		case 62:
			// 扫描整合定位依据失败，定位结果无效
			QLLog.e(this.getClass().getName(), "定位结果无效,Code:" + locationCode);
		default:
			QLLog.e(this.getClass().getName(), "定位失败，Code:" + locationCode);
			YYDebugHandler.getShareInstance().reportError(mContext, "request location failed, code "+ locationCode);
		}
		return false;
	}

	/**
	 * baidu接口返回监听
	 */
	@Override
	public void onReceiveLocation(BDLocation location) {
		if (location == null || !locationResultInfo(location)) {
			requestLocationTimes++;
			if (requestLocationTimes < maxInitRequestCount) {
				// 未达到重试次数时，继续重试定位。
				this.requestLocation();
			} else {
				requestLocationTimes = 0;
				if (listener != null) {
					listener.onLocationChange(null);
					listener.onRequestLocationFinish();
				}
			}
			return;
		}
		requestLocationTimes = 0;

		StringBuffer sb = new StringBuffer(256);
		sb.append("time : ");
		sb.append(Public.getTimeNow());

		sb.append("\nlontitude : ");
		sb.append(location.getLongitude());
		sb.append("\nlatitude : ");
		sb.append(location.getLatitude());

		if (location.hasRadius()) {
			sb.append("\nradius : ");
			sb.append(location.getRadius());
		}
		String locationAddr = null;

		if (location.getLocType() == BDLocation.TypeGpsLocation) {
			sb.append("\nspeed : ");
			sb.append(location.getSpeed());
			sb.append("\nsatellite : ");
			sb.append(location.getSatelliteNumber());
		} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
			locationAddr = location.getAddrStr();
			sb.append("\naddr : ");
			sb.append(locationAddr);
		} else if (location.getLocType() == BDLocation.TypeOffLineLocation) {
			locationAddr = location.getAddrStr();
			sb.append("\noffLine addr:");
			sb.append(location.getAddrStr());
		}

//		Log.i(this.getClass().getName(), sb.toString());

		TYLocationInfo locationInfo = new TYLocationInfo();

		com.baidu.mapapi.utils.CoordinateConverter converter = new com.baidu.mapapi.utils.CoordinateConverter();
		converter.coord(new LatLng(location.getLatitude(),location.getLongitude()));
		converter.from(CoordinateConverter.CoordType.COMMON);
		LatLng point = converter.convert();
		locationInfo.latitude = point.latitude;
		locationInfo.longitude = point.longitude;

//		locationInfo.latitude = location.getLatitude();
//		locationInfo.longitude = location.getLongitude();
		locationInfo.city = location.getCity();
		//To WGS84坐标系统
//		locationInfo.setCoordinateSysType(ECoordinateSysType.BaiduCoordinateSys);
//		PointDF p = gcj2wgs_exact(location.getLatitude(), location.getLongitude());
//		locationInfo.setLongitude(p.y);
//		locationInfo.setLatitude(p.x);
		
		String newAddrString = null;
		if (locationAddr != null) {
			byte[] addrBytes = locationAddr.getBytes();
			try {
				newAddrString = new String(addrBytes, "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		locationInfo.setLocationAddr(newAddrString);
		locationInfo.setHasRadius(location.hasRadius());
		locationInfo.setGpsDate(Public.getTimeNow());
		if (location.hasRadius()) {
			locationInfo.setRadius(location.getRadius());
		}

		//如果在11秒内重新获取位置信息，则尝试使用旧的地址。
		if(TextUtils.isEmpty(locationInfo.getLocationAddr())){
			geoAddr(locationInfo);
		}	
//		parseBD09ToWgs84OnLine(location);
		sendLocationBroadcase(locationInfo);
		if (listener != null) {
			listener.onLocationChange(currLocationInfo);
			listener.onRequestLocationFinish();
		}

	}

	public void onReceivePoi(BDLocation location) {
		if (location == null) {
			if (listener != null) {
				listener.onRequestLocationFinish();
			}
			return;
		}
		StringBuffer sb = new StringBuffer(256);
		sb.append("time : ");
		sb.append(Public.getTimeNow());
		sb.append("\nerror code : ");
		sb.append(location.getLocType());
		sb.append("\nlatitude : ");
		sb.append(location.getLatitude());
		sb.append("\nlontitude : ");
		sb.append(location.getLongitude());
		sb.append("\nradius : ");
		sb.append(location.getRadius());
		if (location.getLocType() == BDLocation.TypeGpsLocation){
			sb.append("\nspeed : ");
			sb.append(location.getSpeed());
			sb.append("\nsatellite : ");
			sb.append(location.getSatelliteNumber());
		} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
			sb.append("\naddr : ");
			sb.append(location.getAddrStr());
		} 

//		Log.i(this.getClass().getName(), sb.toString());

		String locationAddr = location.hasAddr() ? location.getAddrStr()
				: null;
		TYLocationInfo locationInfo = new TYLocationInfo();
		locationInfo.setCoordinateSysType(ECoordinateSysType.GuoCeCoordinateSys);
		locationInfo.latitude = location.getLatitude();
		locationInfo.longitude = location.getLongitude();
		locationInfo.city = location.getCity();
		
		//To WGS84坐标系统
//		locationInfo.setCoordinateSysType(ECoordinateSysType.Wgs84);
//		PointDF p = gcj2wgs_exact(location.getLatitude(), location.getLongitude());
//		locationInfo.setLongitude(p.y);
//		locationInfo.setLatitude(p.x);
		
		
		String newAddrString = null;
		if (locationAddr != null) {
			byte[] addrBytes = locationAddr.getBytes();
			try {
				newAddrString = new String(addrBytes, "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		locationInfo.setLocationAddr(newAddrString);
		locationInfo.setGpsDate(Public.getTimeNow());
		locationInfo.setHasRadius(location.hasRadius());
		if (location.hasRadius()) {
			locationInfo.setRadius(location.getRadius());
		}

//		parseBD09ToWgs84OnLine(location);
		sendLocationBroadcase(locationInfo);
		if (listener != null) {
			listener.onLocationChange(currLocationInfo);
			listener.onRequestLocationFinish();
		}

	}
	
//	private void parseBD09ToWgs84OnLine(BDLocation bdLocation){
//		QLHttpUtil httpUtil = QLHttpManager.create(mContext, QLHttpMethod.HTTPGET);
//		httpUtil.setUrl("http://api.zdoz.net/bd2wgs.aspx");
//		HashMap<String, String> paraMap = new HashMap<String, String>();
//		paraMap.put("", ""+bdLocation.getLatitude());
//		paraMap.put("", ""+bdLocation.getLongitude());
//		httpUtil.startConnection(new QLHttpResult() {
//			@Override
//			public void reply(QLHttpReply reply) {
//				if (reply != null) {
//					reply.
//				}
//			}
//		});
//		sendLocationBroadcase(locationInfo);
//		if (locationListener != null) {
//			locationListener.onCallBack(RequestResultCode.RequestOk,
//					locationInfo);
//		}
//	}
	
	private void geoAddr(TYLocationInfo info){
		LatLng ll = new LatLng(info.getLatitude(), info.getLongitude());
		CoordinateConverter ccConvert = new CoordinateConverter();
		ccConvert.from(CoordType.GPS);
		ccConvert.coord(ll);
		LatLng bdll = ccConvert.convert();
		
		ReverseGeoCodeOption rgco = new ReverseGeoCodeOption();
		rgco.location(bdll);
		gCoder.reverseGeoCode(rgco);
	}

	@Override
	public void onGpsStatusChanged(int event) {
		gpsEnable = event == GpsStatus.GPS_EVENT_STARTED;
	}

	@Override
	public void onLocationChanged(Location location) {
		if (location == null) {
			return;
		}
		TYLocationInfo locationInfo = new TYLocationInfo();
		locationInfo.latitude = location.getLatitude();
		locationInfo.longitude = location.getLongitude();
		long _currTime = Public.getCurrMillis();
		locationInfo.setGpsDateTime(_currTime);
		locationInfo.setGpsDate(Public.getTimeNow());
		locationInfo.setCoordinateSysType(ECoordinateSysType.GuoCeCoordinateSys);
		locationInfo.setHasRadius(false);
		//如果在11秒内重新获取位置信息，则尝试使用旧的地址。
		if(currLocationInfo == null || TextUtils.isEmpty(currLocationInfo.getLocationAddr()) || (_currTime - currLocationInfo.getUpgradAddrDateTime()) > 11000){
			geoAddr(locationInfo);
		}else{
			locationInfo.setLocationAddr(currLocationInfo.getLocationAddr());
		}
		
		sendLocationBroadcase(locationInfo);
		if (listener != null) {
			listener.onLocationChange(currLocationInfo);
			listener.onRequestLocationFinish();
		}
		
		QLLog.i(this.getClass().getName(), "gps lat:"+location.getLatitude() + "  lng:"+location.getLongitude());
	}

	@Override
	public void onProviderDisabled(String provider) {
		QLLog.e(provider +" disable.");
	}

	@Override
	public void onProviderEnabled(String provider) {
		QLLog.e(provider + " enable.");
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		QLLog.i(this.getClass().getName(), provider + "status change to:"+status);
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {
		if (currLocationInfo != null) {
			currLocationInfo.setUpgradAddrDateTime(Public.getCurrMillis());
			currLocationInfo.setLocationAddr(arg0.getAddress());
			sendLocationBroadcase(currLocationInfo);
			if (listener != null) {
				listener.onLocationChange(currLocationInfo);
				listener.onRequestLocationFinish();
			}
		}
	}
	
	private boolean outOfChina(double lat, double lng) {
		if (lng < 72.004 || lng > 137.8347) {
			return true;
		}
		if (lat < 0.8293 || lat > 55.8271) {
			return false;
		}
		return false;
	}
	
//	private double M_PI = 3.141592687;
	class PointDF{
		double x;
		double y;
		PointDF(double x,double y){
			this.x = x;
			this.y = y;
		}
	}

	private double transformLat(double x,double y) {
		double ret = -100.0 + 2.0*x + 3.0*y + 0.2*y*y + 0.1*x*y + 0.2*Math.sqrt(Math.abs(x));
		ret += (20.0*Math.sin(6.0*x*Math.PI) + 20.0*Math.sin(2.0*x*Math.PI)) * 2.0 / 3.0;
		ret += (20.0*Math.sin(y*Math.PI) + 40.0*Math.sin(y/3.0*Math.PI)) * 2.0 / 3.0;
		ret += (160.0*Math.sin(y/12.0*Math.PI) + 320*Math.sin(y*Math.PI/30.0)) * 2.0 / 3.0;
		return ret;
	}

	private double transformLon(double x,double y) {
		double ret = 300.0 + x + 2.0*y + 0.1*x*x + 0.1*x*y + 0.1*Math.sqrt(Math.abs(x));
		ret += (20.0*Math.sin(6.0*x*Math.PI) + 20.0*Math.sin(2.0*x*Math.PI)) * 2.0 / 3.0;
		ret += (20.0*Math.sin(x*Math.PI) + 40.0*Math.sin(x/3.0*Math.PI)) * 2.0 / 3.0;
		ret += (150.0*Math.sin(x/12.0*Math.PI) + 300.0*Math.sin(x/30.0*Math.PI)) * 2.0 / 3.0;
		return ret;
	}

	private PointDF delta(double lat,double lng) {
		double a = 6378245.0;
		double ee = 0.00669342162296594323;
		double dLat = transformLat(lng-105.0, lat-35.0);
		double dLng = transformLon(lng-105.0, lat-35.0);
		double radLat = lat / 180.0 * Math.PI;
		double magic = Math.sin(radLat);
		magic = 1 - ee*magic*magic;
		double sqrtMagic = Math.sqrt(magic);
		dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * Math.PI);
		dLng = (dLng * 180.0) / (a / sqrtMagic * Math.cos(radLat) * Math.PI);
		return new PointDF(dLat,dLng);
	}

	private PointDF wgs2gcj(double wgsLat,double wgsLng) {
		if (outOfChina(wgsLat, wgsLng)) {
			return new PointDF(wgsLat,wgsLng);
		}
		PointDF d = delta(wgsLat, wgsLng);
		return new PointDF(wgsLat + d.x,wgsLng+d.y);
	}

	private PointDF gcj2wgs(double gcjLat,double gcjLng) {
		if (outOfChina(gcjLat, gcjLng)) {
			return new PointDF(gcjLat,gcjLng);
		}
		PointDF d = delta(gcjLat, gcjLng);
		return new PointDF(gcjLat - d.x,gcjLng - d.y);
	}

	private PointDF gcj2wgs_exact(double gcjLat,double gcjLng) {
		double initDelta = 0.01;
		double threshold = 0.000001;
		double dLat = initDelta, dLng = initDelta;
		double mLat = gcjLat-dLat, mLng = gcjLng-dLng;
		double pLat = gcjLat+dLat, pLng = gcjLng+dLng;
		double wgsLat = 0, wgsLng = 0;
		for (int i = 0; i < 30; i++) {
			wgsLat = (mLat+pLat)/2;
			wgsLng = (mLng+pLng)/2;
			PointDF tmp = wgs2gcj(wgsLat, wgsLng);
			dLat = tmp.x-gcjLat;
			dLng = tmp.y-gcjLng;
			if ((Math.abs(dLat) < threshold) && (Math.abs(dLng) < threshold)) {
				return new PointDF(wgsLat,wgsLng);
			}
			if (dLat > 0) {
				pLat = wgsLat;
			} else {
				mLat = wgsLat;
			}
			if (dLng > 0) {
				pLng = wgsLng;
			} else {
				mLng = wgsLng;
			}
		}
		return new PointDF(wgsLat,wgsLng);
	}

	public static LatLng baiduSetGCJ02(double latitude,double longitude){
		com.baidu.mapapi.utils.CoordinateConverter converter = new com.baidu.mapapi.utils.CoordinateConverter();
		converter.coord(new LatLng(latitude,longitude));
		converter.from(CoordinateConverter.CoordType.COMMON);
		LatLng point = converter.convert();
		return point;
	}
	
}
