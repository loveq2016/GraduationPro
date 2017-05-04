/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hyphenate.easeui.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.navisdk.adapter.BNOuterLogUtil;
import com.baidu.navisdk.adapter.BNOuterTTSPlayerCallback;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNaviSettingManager;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import org.ql.app.alert.AlertDialog;
import org.ql.utils.QLToastUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import app.logic.activity.navi.BNDemoGuideActivity;
import app.logic.activity.navi.other.Location;
import app.logic.activity.navi.testdelan.APPUtil;
import app.logic.activity.navi.testdelan.AppInfo;
import app.logic.activity.navi.testdelan.NativeDialog;
import app.logic.pojo.TYLocationInfo;
import app.utils.managers.TYLocationManager;
import app.view.DialogLoading;
import app.yy.geju.R;

public class EaseBaiduMapActivity extends EaseBaseActivity implements TYLocationManager.TYLocationListener {

    private boolean needNavi = false ;
    private final static String TAG = "map";
    MapView mMapView = null;
    FrameLayout mMapViewContainer = null;
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    EditText indexText = null;
    int index = 0;
    // LocationData locData = null;
    //static BDLocation lastLocation = null;
    public static EaseBaiduMapActivity instance = null;
    ProgressDialog progressDialog;
    private BaiduMap mBaiduMap;
    private Marker marker ;
    private View rl_mask;
    //private LatLng sendLatLng;
    TYLocationInfo locationInfo;
    TYLocationInfo myLocationInfo;
    //private String sendAdress;

    //<----------新增--------->
    private LinearLayout addNavigaLl ;  //位值 and 导航栏
    private TextView addTv ;            //位置
    private ImageView navigaImg ;       //导航ImageView
    //自己写的导航包下面的
    private Location loc_now ;
    private Location loc_end ;
    private ImageView locaImg ;

    /**
     * 初始化 位置 and 导航栏
     */
    private void findViewById(){
        addNavigaLl = (LinearLayout) findViewById( R.id.add_naviga_ll);
        addTv = (TextView) findViewById( R.id.add_tv);
        navigaImg = (ImageView) findViewById( R.id.naviga_img) ;
        //点击导航
        navigaImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<AppInfo> apps = APPUtil.getMapApps(EaseBaiduMapActivity.this);
                if ( apps == null || apps.size() == 0){
                    QLToastUtils.showToast( EaseBaiduMapActivity.this ,"请先下载百度地图或高德地图" );
                    return;
                }
                if( loc_end == null ){
                    return;
                }else{
                    NativeDialog msgDialog = new NativeDialog( EaseBaiduMapActivity.this, loc_now , loc_end);
                    msgDialog.show();
                }
            }
        });
    }

    @Override
    public void onRequestLocationStart() {

    }

    @Override
    public void onRequestLocationFinish() {

    }

    @Override
    public void onLocationChange(TYLocationInfo info) {
        if (info != null && !needNavi) {
            if (dialogLoading != null) {
                dialogLoading.dismiss();
            }
            rl_mask = findViewById(R.id.rl_mask);
            rl_mask.setVisibility(View.VISIBLE);
            sendTv.setVisibility( View.VISIBLE);
//            com.baidu.mapapi.utils.CoordinateConverter converter = new com.baidu.mapapi.utils.CoordinateConverter();
//            converter.coord(new LatLng(info.latitude,info.longitude));
//            converter.from(CoordinateConverter.CoordType.COMMON);
//            LatLng latLng = converter.convert();

            myLocationInfo.latitude = info.latitude;
            myLocationInfo.longitude = info.longitude;
            myLocationInfo.setLocationAddr(info.getLocationAddr());
            TYLocationManager.getShareLocationManager().stop();  //停止轮寻 设置的是9秒 刷新一次
            //TYLocationManager.getShareLocationManager().getCurrLocationInfo();  //获取当前最新的位置
//            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(latLng));
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(new LatLng(info.latitude,info.longitude)));
        }else if(info!= null && needNavi ){
            loc_now = new Location(  info.latitude ,info.longitude ,info.getLocationAddr() );  //导航的起点
            if (dialogLoading != null) {
                dialogLoading.dismiss();
            }
        }
    }

    @Override
    public void onGPSStatuChange(int event) {

    }

    public class BaiduSDKReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String s = intent.getAction();
            String st1 = getResources().getString(R.string.Network_error);
            if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
                String st2 = getResources().getString(R.string.please_check);
                Toast.makeText(instance, st2, Toast.LENGTH_SHORT).show();
            } else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
                Toast.makeText(instance, st1, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private BaiduSDKReceiver mBaiduReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        //initialize SDK with context, should call this before setContentView  bmapView
        SDKInitializer.initialize(getApplicationContext());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.ease_activity_baidumap);
        initTitleBar();
        findViewById();
        locaImg = (ImageView) findViewById(R.id.loca_img);
        mMapView = (MapView) findViewById(R.id.bmapView);
        LocationMode mCurrentMode = LocationMode.NORMAL;
        mBaiduMap = mMapView.getMap();
        mBaiduMap.getUiSettings().setScrollGesturesEnabled(true);//设置可拖拽
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
        mBaiduMap.setMapStatus( msu );
        Intent intent = getIntent();
        needNavi = intent.getBooleanExtra("navi" , false ) ;
        initMapView();
        locationInfo = new TYLocationInfo();
        myLocationInfo = new TYLocationInfo();
        TYLocationInfo currLocation = TYLocationManager.getShareLocationManager().getCurrLocationInfo();
        if (currLocation != null){
            myLocationInfo.latitude = currLocation.latitude;
            myLocationInfo.longitude = currLocation.longitude;
            myLocationInfo.setLocationAddr(currLocation.getLocationAddr());
        }
        if ( !needNavi ) {  // latitude == 0
            mBaiduMap.setMyLocationConfigeration( new MyLocationConfiguration(mCurrentMode, true, null));
            mBaiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
                @Override
                public void onTouch(MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                        getScreenCenterLocation();
                    }
                }
            });
            showMapWithLocationClient();
            addNavigaLl.setVisibility( View.GONE);   //位置 and 导航栏 隐藏
            locaImg.setVisibility( View.VISIBLE);
        } else {
            showMapWithLocationClient();
            double latitude = intent.getDoubleExtra("latitude", 0);    //纬度
            double longitude = intent.getDoubleExtra("longitude", 0);  //经度
            String address = intent.getStringExtra("address");         //地址
            locationInfo.latitude = latitude;
            locationInfo.longitude = longitude;
            locationInfo.setLocationAddr(address);
            loc_end = new Location( latitude , longitude , address );   //导航的中点

            com.baidu.mapapi.utils.CoordinateConverter converter = new com.baidu.mapapi.utils.CoordinateConverter();
            converter.coord(new LatLng(locationInfo.latitude,locationInfo.longitude));
            converter.from(CoordinateConverter.CoordType.COMMON);
            LatLng point = converter.convert();

            //定义Maker坐标点
//            LatLng point = new LatLng(locationInfo.latitude , locationInfo.longitude);
            //构建Marker图标
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_map3x); // icon_marka
            //构建MarkerOption，用于在地图上添加Marker
            OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
            //在地图上添加Marker，并显示
            mBaiduMap.addOverlay(option);
            mBaiduMap.setMyLocationEnabled(true);
            MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(point);
            mBaiduMap.animateMapStatus(mapStatusUpdate , 17);
            //naviInit();  //导航的初始化
            if( address != null ) addTv.setText( address );
            addNavigaLl.setVisibility( View.VISIBLE);  //位置 and 导航栏 显示
            if (dialogLoading != null) {
                dialogLoading.dismiss();
            }
        }
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        mBaiduReceiver = new BaiduSDKReceiver();
        registerReceiver(mBaiduReceiver, iFilter);
    }
    private TextView sendTv;

    /**
     * 獲取屏幕中間坐標點
     */
    private void getScreenCenterLocation(){
        android.graphics.Point screenCenterPoint = mMapView.getMap().getMapStatus().targetScreen;
        LatLng latlng =mBaiduMap.getProjection().fromScreenLocation(screenCenterPoint);
        if (latlng == null)
            return;
        myLocationInfo.latitude = latlng.latitude;
        myLocationInfo.longitude = latlng.longitude;
        GeoCoder geoCoder = GeoCoder.newInstance();
        geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                if (reverseGeoCodeResult != null){
                    myLocationInfo.setLocationAddr(reverseGeoCodeResult.getAddress());
                }else{
                    QLToastUtils.showToast(EaseBaiduMapActivity.this, "获取地址失败,请重新获取");
                }
            }
        });
        ReverseGeoCodeOption option = new ReverseGeoCodeOption();
        option.location(latlng);
        geoCoder.reverseGeoCode(option);
    }

    /**
     * 处理titlebar
     */
    private void initTitleBar() {
        sendTv = (TextView) findViewById(R.id.send_tv);
        sendTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendLocation(v);
            }
        });
        findViewById(R.id.leftLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setBackStatus(false);
    }


//    private void showMap(double latitude, double longtitude, String address) {
//        LatLng llA = new LatLng(latitude, longtitude);
//        CoordinateConverter converter = new CoordinateConverter();
//        converter.coord(llA);
//        converter.from(CoordinateConverter.CoordType.COMMON);
//        LatLng convertLatLng = converter.convert();
//        OverlayOptions ooA = new MarkerOptions()
//                .position(convertLatLng)
//                .icon(BitmapDescriptorFactory
//                .fromResource(R.drawable.icon_map3x))
//                .zIndex(4).draggable(true);
//        mBaiduMap.addOverlay(ooA);
//        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(convertLatLng, 17.0f);
//        mBaiduMap.animateMapStatus(u);
//
//    }


    private DialogLoading dialogLoading;

    private void showMapWithLocationClient() {
        dialogLoading = new DialogLoading(this);
        dialogLoading.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (dialogLoading.isShowing()) {
                    dialogLoading.dismiss();
                }
                Log.d("map", "cancel retrieve location");
                finish();
            }
        });
        dialogLoading.show();
        mLocClient = new LocationClient(this);
        //mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// open gps
        // option.setCoorType("bd09ll");
        // Johnson change to use gcj02 coordination. chinese national standard
        // so need to conver to bd09 everytime when draw on baidu map
        option.setCoorType("gcj02");
        option.setScanSpan(30000);
        option.setAddrType("all");
        mLocClient.setLocOption(option);
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        if (mLocClient != null) {
            mLocClient.stop();
        }
        TYLocationManager.getShareLocationManager().setLocationListener(null);
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        if (mLocClient != null) {
            mLocClient.start();
        }
        TYLocationManager.getShareLocationManager().setLocationListener(this);
        TYLocationManager.getShareLocationManager().requestLocation();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (mLocClient != null)
            mLocClient.stop();
        mMapView.onDestroy();
        unregisterReceiver(mBaiduReceiver);
        super.onDestroy();
    }

    private void initMapView() {
        mMapView.setLongClickable(true);
    }

    public void back(View v) {
        finish();
    }

    public void sendLocation(View view) {
        Intent intent = this.getIntent();
        double latitude = myLocationInfo.latitude;
        double longitude = myLocationInfo.longitude;

        LatLng latLng = bd09_To_Gcj02(latitude,longitude);
        String address = myLocationInfo.getLocationAddr();
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        intent.putExtra("latitude", latLng.latitude);
        intent.putExtra("longitude", latLng.longitude);
        intent.putExtra("address", address);
        this.setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }

    /**
     * * 火星坐标系 (GCJ-02) 与百度坐标系 (BD-09) 的转换算法 * * 将 BD-09 坐标转换成GCJ-02 坐标 * * @param
     * bd_lat * @param bd_lon * @return
     */
    public static double pi = 3.1415926535897932384626;
    public static LatLng bd09_To_Gcj02(double bd_lat, double bd_lon) {
        double x = bd_lon - 0.0065, y = bd_lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * pi);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * pi);
        double gg_lon = z * Math.cos(theta);
        double gg_lat = z * Math.sin(theta);
        return new LatLng(gg_lat, gg_lon);
    }

    /**
     * format new location to string and show on screen
     */
    public class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null) {
                return;
            }
            Log.d("map", "On location change received:" + location);
            Log.d("map", "addr:" + location.getAddrStr());
            sendTv.setEnabled(true);
            if (dialogLoading != null) {
                dialogLoading.dismiss();
            }
            rl_mask = findViewById(R.id.rl_mask);
            rl_mask.setVisibility(View.VISIBLE);
//            myLocationInfo.latitude = location.getLatitude();
//            myLocationInfo.longitude = location.getLongitude();
//            myLocationInfo.setLocationAddr(location.getAddrStr());
//            mBaiduMap.clear();
//            mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
//                @Override
//                public void onMapStatusChangeStart(MapStatus mapStatus) {
//
//                }
//                @Override
//                public void onMapStatusChange(MapStatus mapStatus) {
////                    marker.setPosition(mapStatus.target);
//                }
//                @Override
//                public void onMapStatusChangeFinish(MapStatus mapStatus) {
//                    if( marker != null ){
//                        marker.setPosition(mapStatus.target);
//                    }
//                    GeoCoder geoCoder = GeoCoder.newInstance();
//                    geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(mapStatus.target));
//                    geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
//                        @Override
//                        public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
//                        }
//                        @Override
//                        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
//                            String address = reverseGeoCodeResult.getAddress();
//                            if (!TextUtils.isEmpty(address)) {
//                                myLocationInfo.setLocationAddr(address);
//                                return;
//                            }
//                            QLToastUtils.showToast(EaseBaiduMapActivity.this, "获取地址失败,请重新获取");
//                        }
//                    });
//                }
//            });
        }

        public void onReceivePoi(BDLocation poiLocation) {
            if (poiLocation == null) {
                return;
            }
        }
    }




    /*************  百度导航  ***********/

    public static List<Activity> activityList = new LinkedList<Activity>();
    private static final String APP_FOLDER_NAME = "GZYYXIEHUI";
    public static final String ROUTE_PLAN_NODE = "routePlanNode";
    public static final String ADDER = "adder";
    public static final String SHOW_CUSTOM_ITEM = "showCustomItem";
    public static final String RESET_END_NODE = "resetEndNode";
    public static final String VOID_MODE = "voidMode";
    private String mSDCardPath = null;  //SDC 卡路径
    String authinfo = null;
    private boolean hasInitSuccess ;

    /**
     * 初始化百度导航
     */
    private void naviInit(){
        activityList.add(this);
        // 打开log开关（百度导航日志）
        BNOuterLogUtil.setLogSwitcher(true);
        if (initDirs()) {   //文件初始化
            initNavi();     //百度 导航初始化
        }
    }

    /**
     * 初始化 （文件夹）
     * @return
     */
    private boolean initDirs() {
        mSDCardPath = getSdcardDir();   //获取SDK 路径
        if (mSDCardPath == null) {
            return false;
        }
        File f = new File(mSDCardPath, APP_FOLDER_NAME);
        if (!f.exists()) {
            try {
                f.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /**
     * 获取 SDK 的目录
     * @return
     */
    private String getSdcardDir() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }

    /**
     * 初始化 百度导航
     */
    private void initNavi() {
        BNOuterTTSPlayerCallback ttsCallback = null;
        BaiduNaviManager.getInstance().init(this, mSDCardPath , APP_FOLDER_NAME, new BaiduNaviManager.NaviInitListener() {
            @Override
            public void onAuthResult(int status, String msg) {
                if (0 == status) {
                    authinfo = "key校验成功!";
                } else {
                    authinfo = "key校验失败, " + msg;
                    //System.out.println(" authinfo = "+authinfo);
                }
                EaseBaiduMapActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(EaseBaiduMapActivity.this, authinfo, Toast.LENGTH_LONG).show();
                    }
                });
            }
            /**
             * 初始化成功
             */
            public void initSuccess() {
                //Toast.makeText(EaseBaiduMapActivity.this, "百度导航引擎初始化成功", Toast.LENGTH_SHORT).show();
                //导航 配置 设置
                initSetting();
                hasInitSuccess = true ;  //初始化成功的标志
            }
            /**
             * 初始化开始
             */
            public void initStart() {
                //Toast.makeText(EaseBaiduMapActivity.this, "百度导航引擎初始化开始", Toast.LENGTH_SHORT).show();
            }
            /**
             *初始化失败
             */
            public void initFailed() {
                Toast.makeText(EaseBaiduMapActivity.this, "百度导航引擎初始化失败", Toast.LENGTH_SHORT).show();
                hasInitSuccess = false ;
            }

        },null,
                ttsHandler,   //后面定义过的tts播报回传handler
                ttsPlayStateListener
        );
    }

    /**
     * 内部TTS播报状态回传handler
     */
    private Handler ttsHandler = new Handler() {
        public void handleMessage(Message msg) {
            int type = msg.what;
            switch (type) {
                case BaiduNaviManager.TTSPlayMsgType.PLAY_START_MSG: {
                    //showToastMsg("Handler : TTS play start");
                    break;
                }
                case BaiduNaviManager.TTSPlayMsgType.PLAY_END_MSG: {
                    //showToastMsg("Handler : TTS play end");
                    break;
                }
                default :
                    break;
            }
        }
    };

    /**
     * 内部TTS播报状态回调接口
     */
    private BaiduNaviManager.TTSPlayStateListener ttsPlayStateListener = new BaiduNaviManager.TTSPlayStateListener() {
        @Override
        public void playEnd() {
            //showToastMsg("TTSPlayStateListener : TTS play end");
        }
        @Override
        public void playStart() {
            //showToastMsg("TTSPlayStateListener : TTS play start");
        }
    };
    /**
     * 显示消息 ToastMsg
     * @param msg
     */
    public void showToastMsg(final String msg) {
        EaseBaiduMapActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(EaseBaiduMapActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
    /**
     *导航路线计算 （ 根据不同的格式计算 ）
     * @param coType
     */
    private void routeplanToNavi(BNRoutePlanNode.CoordinateType coType) {
        BNRoutePlanNode sNode = null;   //开始点
        BNRoutePlanNode eNode = null;   //结束点
        switch (coType) {
            case BD09LL: {
                if( myLocationInfo == null ){
                    QLToastUtils.showToast( EaseBaiduMapActivity.this , "正在确定您的位置，请稍等");
                    return;
                }
                System.out.println(" S = "+ myLocationInfo.getLongitude() + "  "+ myLocationInfo.getLatitude() + "  "+ myLocationInfo.getLocationAddr());
                System.out.println(" N = "+ locationInfo.getLongitude() + "  "+ locationInfo.getLatitude()+"  "+ locationInfo.getLocationAddr());
                sNode = new BNRoutePlanNode(myLocationInfo.getLongitude() , myLocationInfo.getLatitude() , myLocationInfo.getLocationAddr(), null, coType);
                eNode = new BNRoutePlanNode(locationInfo.getLongitude() , locationInfo.getLatitude() , locationInfo.getLocationAddr(), null, coType);
                break;
            }
            default:
        }
        if (sNode != null && eNode != null) {
            List<BNRoutePlanNode> list = new ArrayList<>();
            list.add(sNode);
            list.add(eNode);
            //启动导航
            BaiduNaviManager.getInstance().launchNavigator(
                    this,  //建议是应用的主Activity
                    list , //传入的算路节点，顺序是起点、途经点、终点，其中途经点最多三个
                    1 ,    //算路偏好 1:推荐 8:少收费 2:高速优先 4:少走高速 16:躲避拥堵
                    true , //true表示真实GPS导航，false表示模拟导航,据我所知，新的SDK中，模拟导航不支持语音播报
                    new DemoRoutePlanListener(sNode) //开始导航回调监听器，在该监听器里一般是进入导航过程页面
            );
        }
    }

    /**
     *路由监听
     */
    public class DemoRoutePlanListener implements BaiduNaviManager.RoutePlanListener {
        private BNRoutePlanNode mBNRoutePlanNode = null;
        public DemoRoutePlanListener(BNRoutePlanNode node) {
            mBNRoutePlanNode = node;
        }

        @Override
        public void onJumpToNavigator() {
			/*
			 * 设置途径点以及resetEndNode会回调该接口 （ 第二个界面有地点复位的逻辑代码 ， 所以需要添加这里for 的判断 ）
			 */
            for (Activity ac : activityList) {
                if (ac.getClass().getName().endsWith("ChatActivity")) {
                    return;
                }
            }
            Intent intent = new Intent( EaseBaiduMapActivity.this, BNDemoGuideActivity.class );
            Bundle bundle = new Bundle();
            bundle.putSerializable( ROUTE_PLAN_NODE , mBNRoutePlanNode );
            bundle.putString(ADDER , myLocationInfo.getLocationAddr());
            intent.putExtras( bundle );
            startActivity( intent );
        }

        @Override
        public void onRoutePlanFailed() {
            // TODO Auto-generated method stub
            Toast.makeText(EaseBaiduMapActivity.this, "算路失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 百度导航 设置
     */
    private void initSetting(){
        // 设置是否双屏显示
        BNaviSettingManager.setShowTotalRoadConditionBar(BNaviSettingManager.PreViewRoadCondition.ROAD_CONDITION_BAR_SHOW_ON);
        // 设置导航播报模式
        BNaviSettingManager.setVoiceMode(BNaviSettingManager.VoiceMode.Veteran);
        // 是否开启路况
        BNaviSettingManager.setRealRoadCondition(BNaviSettingManager.RealRoadCondition.NAVI_ITS_ON);
    }

    /**
     *TTS 播放回调
     */
    private BNOuterTTSPlayerCallback mTTSCallback = new BNOuterTTSPlayerCallback() {
        @Override
        public void stopTTS() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "stopTTS");
        }

        @Override
        public void resumeTTS() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "resumeTTS");
        }

        @Override
        public void releaseTTSPlayer() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "releaseTTSPlayer");
        }

        @Override
        public int playTTSText(String speech, int bPreempt) {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "playTTSText" + "_" + speech + "_" + bPreempt);
            return 1;
        }

        @Override
        public void phoneHangUp() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "phoneHangUp");
        }

        @Override
        public void phoneCalling() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "phoneCalling");
        }

        @Override
        public void pauseTTS() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "pauseTTS");
        }

        @Override
        public void initTTSPlayer() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "initTTSPlayer");
        }

        @Override
        public int getTTSState() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "getTTSState");
            return 1;
        }
    };
}
