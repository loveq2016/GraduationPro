package app.utils.helpers;

import java.util.ArrayList;
import java.util.List;

import org.ql.activity.customtitle.ActActivity;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerDragListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiBoundSearchOption;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.bigkoo.pickerview.listener.OnItemSelectedListener;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.inputmethod.EditorInfo;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.ZoomControls;

import app.logic.activity.ActTitleHandler;
import app.logic.adapter.YYBaseListAdapter;
import app.logic.pojo.TYLocationInfo;
import app.utils.managers.TYLocationManager;
import app.yy.geju.R;

/**
*
* SiuJiYung create at 2016年8月17日 下午6:41:36
*
*/

public class LocationPicker extends ActActivity implements OnGetGeoCoderResultListener, OnGetPoiSearchResultListener {

	private View selectedView;
	private Integer index;
	private ActTitleHandler titleHandler = new ActTitleHandler();
	private MapView baiduMapView; //地图
	private BaiduMap baiduMap;    //百度地图
	private ListView mListView;   //列表
	private GeoCoder geoCoder;  
	private PoiSearch poiSearch;
//	private EditText searchEditText;  //编辑框
	private BitmapDescriptor bitmap;
	
	public static final String kInitLocationInfoKey = "kInitLocationInfoKey";
	public static final String kPickLocationResultKey = "kPickLocationResultKey";
	
	private TYLocationInfo locationInfo;
	//适配器
	private YYBaseListAdapter<TYLocationInfo> mAdapter = new YYBaseListAdapter<TYLocationInfo>(this) {
		@Override
		public View createView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.view_map_picker_addr_cell, null);
				saveView("map_addr_view", R.id.map_addr_view, convertView);
			}
			TYLocationInfo info = getItem(position);
			TextView tView = getViewForName("map_addr_view", convertView);
			tView.setText(info.getLocationAddr());
			boolean check_item = isSelected(info);
//			if (check_item) {
//				Drawable right_icon = mContext.getResources().getDrawable(R.drawable.check_item);
//				right_icon.setBounds(0, 0, right_icon.getMinimumWidth(), right_icon.getMinimumHeight());
//				tView.setCompoundDrawables(null, null, right_icon, null);
//			}else{
//				tView.setCompoundDrawables( null, null, null, null );
//			}
			Resources resources = getResources();
			//判断当前重画行与被选中行是否是同一行
			//如是则修改当前重画的行背景为灰色
			//如不是则为默认白色
			if(LocationPicker.this.index!=null && position==index){
				convertView.setBackgroundColor(resources.getColor(R.color.new_create_org_hintcolor_new ));
			//注：此行不能少，否则会有新的BUG(本人开始的时候就是在此处没重新在此做缓存，导致新的BUG出现。结果纠结了几天。)
			//具体是什么BUG自己研究吧
				LocationPicker.this.selectedView = convertView;
			}else{
				convertView.setBackgroundColor(resources.getColor(R.color.white));
			}
			return convertView;
		}
	};
	
	
	protected void onCreate( android.os.Bundle savedInstanceState ) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(this);
		setAbsHandler(titleHandler);
		setContentView(R.layout.activity_map_picker);
		//初始化TootBar
		intiTootBar();
		//初始化View
		intiView() ;
		bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_map3x);
		poiSearch = PoiSearch.newInstance();
		poiSearch.setOnGetPoiSearchResultListener(this);
		geoCoder = GeoCoder.newInstance();
		geoCoder.setOnGetGeoCodeResultListener(this);	
		
//		searchEditText.setOnEditorActionListener(new OnEditorActionListener() {
//			@Override
//			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//					String txt = v.getText().toString();
//					if (TextUtils.isEmpty(txt)) {
//						return false;
//					}
//					TYLocationInfo info = locationInfo==null?TYLocationManager.getShareLocationManager().getCurrLocationInfo():locationInfo;
//					LatLng ll = new LatLng(info.latitude, info.longitude);
//					PoiCitySearchOption option = new PoiCitySearchOption();
//					option.city(info.city);
//					option.keyword(txt);
//					poiSearch.searchInCity( option );
//					return false;
//				}
//				return false;
//			}
//		});

		baiduMap = baiduMapView.getMap();
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(18.5f);
		baiduMap.setMapStatus(msu);
		mAdapter.setMultableSelectEnable(false);
		mListView.setOnItemClickListener( new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				TYLocationInfo info = (TYLocationInfo)parent.getAdapter().getItem(position);
				mAdapter.selectItem(info);
				selectListItem(info);
				mAdapter.notifyDataSetChanged();
				Resources resources = getResources();
               //修改当前选中行背景色为灰色
				view.setBackgroundColor(resources.getColor(R.color.new_create_org_hintcolor_new));
				if( LocationPicker.this.selectedView != null && LocationPicker.this.index!=position){
				//修改原被选中行为白色
					selectedView.setBackgroundColor(resources.getColor(R.color.white));
				}
				//修改被选中行的缓存数据
				LocationPicker.this.selectedView = view;
				LocationPicker.this.index = position;
			}
		});
		mListView.setAdapter(mAdapter);
		
		String location_json_str = getIntent().getStringExtra(kInitLocationInfoKey);
		if (location_json_str != null) {
			try {
				Gson gson = new Gson();
				locationInfo = gson.fromJson(location_json_str, TYLocationInfo.class);
				moveToLocation(locationInfo);
				poiAddrs(locationInfo);
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
			}			
		}else{
			locationInfo = TYLocationManager.getShareLocationManager().getCurrLocationInfo();
			moveToLocation(locationInfo);
			poiAddrs(locationInfo);
			if (locationInfo == null) {
				locationInfo = new TYLocationInfo();
			}
		}
		
		baiduMap.setOnMarkerDragListener( new OnMarkerDragListener() {
			@Override
			public void onMarkerDragStart(Marker arg0) {
				// TODO Auto-generated method stub
			}
			@Override
			public void onMarkerDragEnd(Marker arg0) {
				locationInfo.latitude = arg0.getPosition().latitude;
				locationInfo.longitude = arg0.getPosition().longitude;
				changeLocation(arg0.getPosition());
			}
			@Override
			public void onMarkerDrag(Marker arg0) {
				// TODO Auto-generated method stub
			}
		});
	}
	
	/**
	 * 初始化TootBar
	 */
	private void intiTootBar(){
		setTitle("");
		titleHandler.replaseLeftLayout(this , true);
		((TextView)titleHandler.getLeftLayout().findViewById( R.id.left_tv)).setText( "地址微调" );
		titleHandler.getLeftLayout().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		titleHandler.getRightDefButton().setText("确定");
		titleHandler.getRightDefButton().setVisibility(View.VISIBLE);
		titleHandler.getRightDefButton().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (locationInfo == null) {
					return;
				}
				Gson gson = new Gson();
				String location_jsonString = gson.toJson(locationInfo);
				Intent resultIntent = new Intent();
				resultIntent.putExtra(kPickLocationResultKey, location_jsonString);
				setResult(Activity.RESULT_OK, resultIntent);
				finish();
			}
		});
	}
	
	/**
	 * 初始化View
	 */
	private void intiView(){
//		searchEditText = (EditText)findViewById(R.id.map_picker_search_et);
		baiduMapView = (MapView)findViewById(R.id.map_picker_map_view);
		mListView = (ListView)findViewById(R.id.map_picker_addr_listview);
	}
	
	/**
	 * 选择列表Itme
	 * @param info
	 */
	private void selectListItem(TYLocationInfo info){
		locationInfo = info;
		moveToLocation(locationInfo);
	}
	
	/**
	 * 位置发生变化
	 * @param latLng
	 */
	private void changeLocation(LatLng latLng){
		ReverseGeoCodeOption option = new ReverseGeoCodeOption();
		option.location(latLng);
		geoCoder.reverseGeoCode(option);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (baiduMapView != null) {
			baiduMapView.onPause();
		}
		
	}
	
	@Override
	protected void onDestroy() {
		if (poiSearch != null) {
			poiSearch.destroy();
		}
		if (geoCoder != null) {
			geoCoder.destroy();
		}
		super.onDestroy();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (baiduMapView != null) {
			baiduMapView.onResume();
		}
	}

	
	private void poiAddrs(TYLocationInfo info){
		if (info == null) {
			return;
		}
		PoiNearbySearchOption option = new PoiNearbySearchOption();
		LatLng ll = new LatLng(info.latitude, info.longitude);
		String keyword = "号";
		if (keyword == null) {
			keyword = "号";
		}
		option.keyword(keyword);
		option.location(ll);
		option.radius(1000);
		option.sortType(PoiSortType.distance_from_near_to_far);
		option.pageNum(0);
		option.pageCapacity(20);
		poiSearch.searchNearby(option);
	}
	
	private void moveToLocation(TYLocationInfo info){
		if (info == null) {
			return;
		}
		//显示位置
		showMap(info.latitude, info.longitude, info.getLocationAddr());
	}
	
	/**
	 * 显示位置
	 * @param latitude
	 * @param longtitude
	 * @param address
	 */
	private void showMap( double latitude, double longtitude, String address ) {
//		baiduMap.clear();
//		LatLng llA = new LatLng(latitude, longtitude);
//		CoordinateConverter converter = new CoordinateConverter();
//		converter.coord(llA);
//		converter.from(CoordinateConverter.CoordType.COMMON);
//		LatLng convertLatLng = converter.convert();
//		OverlayOptions ooA = new MarkerOptions()
//				.position(convertLatLng)
//				.icon(BitmapDescriptorFactory
//				.fromResource(R.drawable.icon_marka)).zIndex(4)
//				.draggable(true);
//		baiduMap.addOverlay(ooA);
//		MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(convertLatLng,17.0f);
//		baiduMap.animateMapStatus(u);

		baiduMap.clear();//这里的作用是移除原来的覆盖物（覆盖物太对会OOM）
		LatLng p = new LatLng(latitude , longtitude);
		//构建Marker图标
		//构建MarkerOption，用于在地图上添加Marker
		OverlayOptions option = new MarkerOptions().position(p).icon(bitmap);
		//在地图上添加Marker，并显示
		baiduMap.addOverlay(option);
		baiduMap.setMyLocationEnabled(true);
		MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(p);
		baiduMap.animateMapStatus(mapStatusUpdate,15);
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult arg0) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {
		if (arg0 != null && arg0.error == SearchResult.ERRORNO.NO_ERROR) {
			locationInfo.setLocationAddr(arg0.getAddress());
			poiAddrs(locationInfo);
		}
	}

	@Override
	public void onGetPoiDetailResult(PoiDetailResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetPoiIndoorResult( PoiIndoorResult arg0 ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetPoiResult(PoiResult result) {
		if (result != null && result.error == SearchResult.ERRORNO.NO_ERROR) {
			List<PoiInfo> poiResults = result.getAllPoi();
			if (poiResults != null) {
				TYLocationInfo info = null;
				ArrayList<TYLocationInfo> tmpInfos = new ArrayList<TYLocationInfo>();
				//保留当前最接近的位置
				info = TYLocationManager.getShareLocationManager().getCurrLocationInfo();
				tmpInfos.add(info);
				//获取推荐
				for (PoiInfo poiInfo : poiResults) {
					info = new TYLocationInfo();
					info.latitude = poiInfo.location.latitude;
					info.longitude = poiInfo.location.longitude;
					info.setLocationAddr(poiInfo.address);
					tmpInfos.add(info);
				}
				mAdapter.setDatas(tmpInfos);
			}
		}
	}
}
