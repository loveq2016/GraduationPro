package app.logic.activity.navi.testdelan;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;
import com.baidu.mapapi.utils.CoordinateConverter;

import app.logic.activity.navi.other.Location;
import app.logic.activity.navi.other.MyDistanceUtil;

public class APPUtil {

	public static String[] paks = new String[]{"com.baidu.BaiduMap","com.autonavi.minimap"};

	/**
	 * 打开百度导航
	 * @param context
	 * @param loc1
	 * @param loc2
	 */
	public static void startNative_Baidu(Context context ,Location loc1 ,Location loc2){
		if (loc1==null || loc2==null) {
			return;
		}
		if (loc1.getAddress()==null || "".equals(loc1.getAddress())) {
			loc1.setAddress("我的位置");
		}
		if (loc2.getAddress()==null || "".equals(loc2.getAddress())) {
			loc2.setAddress("目的地");
		}
		try {
			com.baidu.mapapi.utils.CoordinateConverter converter = new com.baidu.mapapi.utils.CoordinateConverter();
            converter.coord(new LatLng(loc2.getLat(),loc2.getLng()));
            converter.from(CoordinateConverter.CoordType.COMMON);
            LatLng dest = converter.convert();

			Intent intent = Intent.getIntent("intent://map/direction?destination=latlng:"+dest.latitude+","+dest.longitude+"|name:"+loc2.getAddress()+"&mode=driving&src=yueyun|geju#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
			//"intent://map/direction?origin=latlng:"+loc1.getStringLatLng()+"|name:"+loc1.getAddress()+"&destination=latlng:"+loc2.getStringLatLng()+"|name:"+loc2.getAddress()+"&mode=driving&src=yueyun|geju#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end"
//			Intent intent = Intent.getIntent("intent://map/direction?destination=latlng:"+loc2.getStringLatLng()+"|name:"+loc2.getAddress()+"&mode=driving&src=yueyun|geju#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
			context.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(context, "地址解析错误", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 打开高德导航
	 * @param context
	 * @param loc
	 */
	public static void startNative_Gaode(Context context,Location loc){
		if (loc==null) {
			return;
		}
		if (loc.getAddress()==null || "".equals(loc.getAddress())) {
			loc.setAddress("目的地");
		}
		try {
			//androidamap://navi?sourceApplication=geju&poiname=yueyun&lat="+gd_lat_lon[0]+"&lon="+gd_lat_lon[1]+"&dev=0&style=2
			//百度转为高德
			double[] gd_lat_lon = bdToGaoDe(  loc.getLng() , loc.getLat() );
			Intent intent = new Intent("android.intent.action.VIEW",
//			Uri.parse("androidamap://navi?sourceApplication=geju&poiname=yueyun&lat="+gd_lat_lon[0]+"&lon="+gd_lat_lon[1]+"&dev=0&style=2"));  //&dev=0 传0 不要传1 否则偏差较大
					Uri.parse("androidamap://navi?sourceApplication=geju&poiname=yueyun&lat="+loc.getLat()+"&lon="+loc.getLng()+"&dev=0&style=2"));  //&dev=0 传0 不要传1 否则偏差较大
			intent.setPackage("com.autonavi.minimap");
			context.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(context, "地址解析错误", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 获取百度地图，高德地图应用
	 * @param context
	 * @return
	 */
	public static List<AppInfo> getMapApps(Context context) {
		LinkedList<AppInfo> apps = new LinkedList<AppInfo>();
		for (String pak : paks) {
			AppInfo appinfo = getAppInfoByPak(context,pak);
			if (appinfo!=null) {
				apps.add(appinfo);
			}
		}
		return apps;
	}

	/**
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static AppInfo getAppInfoByPak(Context context, String packageName){
		PackageManager packageManager = context.getPackageManager();
		List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
		for (PackageInfo packageInfo : packageInfos) {
			if (packageName.equals(packageInfo.packageName)) {
				AppInfo tmpInfo =new AppInfo();
				tmpInfo.setAppName(packageInfo.applicationInfo.loadLabel(packageManager).toString());
				tmpInfo.setPackageName(packageInfo.packageName);
				tmpInfo.setVersionName(packageInfo.versionName);
				tmpInfo.setVersionCode(packageInfo.versionCode);
				tmpInfo.setAppIcon(packageInfo.applicationInfo.loadIcon(packageManager));
				return tmpInfo;
			}
		}
		return null;
	}


	/**
	 * 百度转高德
	 * @param bd_lat
	 * @param bd_lon
	 * @return
	 */
	private static double[] bdToGaoDe(double bd_lat, double bd_lon) {
		double[] gd_lat_lon = new double[2];
		double PI = 3.1415926535897932384626 * 3000.0 / 180.0;
		double x = bd_lon - 0.0065, y = bd_lat - 0.006;
		double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * PI);
		double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * PI);
		gd_lat_lon[0] = z * Math.cos(theta);
		gd_lat_lon[1] = z * Math.sin(theta);
		return gd_lat_lon;
	}

	/**
	 * 高德转百度
	 * @param gd_lon
	 * @param gd_lat
	 * @return
	 */
	private double[] gaoDeToBaidu(double gd_lon, double gd_lat) {
		double[] bd_lat_lon = new double[2];
		double PI = 3.14159265358979324 * 3000.0 / 180.0;
		double x = gd_lon, y = gd_lat;
		double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * PI);
		double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * PI);
		bd_lat_lon[0] = z * Math.cos(theta) + 0.0065;
		bd_lat_lon[1] = z * Math.sin(theta) + 0.006;
		return bd_lat_lon;
	}


	public static void startNative_Tengxun(Context context,Location loc1,Location loc2){

		return;
	}

	public static void startNativeBySDK_Baidu(Context context,Location loc1,Location loc2){
		if (loc1==null || loc2==null) {
			return;
		}
		if (loc1.getAddress()==null || "".equals(loc1.getAddress())) {
			loc1.setAddress("我的位置");
		}
		if (loc2.getAddress()==null || "".equals(loc2.getAddress())) {
			loc2.setAddress("目的地");
		}
		NaviParaOption para = new NaviParaOption().startPoint(MyDistanceUtil.entity2Baidu(loc1))
												  .startName(loc1.getAddress())
												  .endPoint(MyDistanceUtil.entity2Baidu(loc2))
												  .endName(loc2.getAddress());
		try {
			BaiduMapNavigation.openBaiduMapNavi(para, context);
		} catch (BaiduMapAppNotSupportNaviException e) {
			e.printStackTrace();
			Toast.makeText(context, "地址解析错误", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 如果用户手机没有装地图工具，那么就启调默认浏览器调用web导航，目前就只有百度提供了在浏览器中的导航支持
	 * @param loc1
	 * @param loc2
	 * @return
	 */
	public static String getWebUrl_Baidu(Location loc1,Location loc2){
		if (loc1==null || loc2==null) {
			return null;
		}
		if (loc1.getAddress()==null || "".equals(loc1.getAddress())) {
			loc1.setAddress("我的位置");
		}
		if (loc2.getAddress()==null || "".equals(loc2.getAddress())) {
			loc2.setAddress("目的地");
		}
		return "http://api.map.baidu.com/direction?origin=latlng:"+loc1.getStringLatLng()+"|name:"+loc1.getAddress()+"&destination=latlng:"+loc2.getStringLatLng()+"|name:"+loc2.getAddress()+"&mode=driving&src=yueyun|geju";
	}

	/**
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static boolean isAvilible(Context context, String packageName) {
		final PackageManager packageManager = context.getPackageManager();
		List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
		packageManager.getInstalledApplications(packageManager.GET_META_DATA);
		List<String> packageNames = new ArrayList<String>();
		if (packageInfos != null) {
			for (int i = 0; i < packageInfos.size(); i++) {
				String packName = packageInfos.get(i).packageName;
				packageNames.add(packName);
			}
		}
		return packageNames.contains(packageName);
	}

	/**
	 * @param context
	 * @return
	 */
	public static List<AppInfo> getWebApps(Context context){
		LinkedList<AppInfo> apps = new LinkedList<>();
		String default_browser = "android.intent.category.DEFAULT";
        String browsable = "android.intent.category.BROWSABLE";
        String view = "android.intent.action.VIEW";
		Intent intent = new Intent(view);
        intent.addCategory(default_browser);
        intent.addCategory(browsable);
        Uri uri = Uri.parse("http://");
        intent.setDataAndType(uri, null);
		
		PackageManager packageManager = context.getPackageManager();
//		List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(intent, PackageManager.GET_INTENT_FILTERS);
//		for (ResolveInfo resolveInfo : resolveInfoList) {
//			AppInfo tmpInfo =new AppInfo();
//			tmpInfo.setAppName(resolveInfo.loadLabel(packageManager).toString());
//			tmpInfo.setAppIcon(resolveInfo.loadIcon(packageManager));
//			tmpInfo.setPackageName(resolveInfo.activityInfo.packageName);
//			apps.add(tmpInfo);
//		}
		return apps;
	}
}
