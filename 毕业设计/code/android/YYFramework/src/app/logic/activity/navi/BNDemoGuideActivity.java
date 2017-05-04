package app.logic.activity.navi;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.baidu.navisdk.adapter.BNRouteGuideManager;
import com.baidu.navisdk.adapter.BNRouteGuideManager.CustomizedLayerItem;
import com.baidu.navisdk.adapter.BNRouteGuideManager.OnNavigationListener;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNRoutePlanNode.CoordinateType;
import com.baidu.navisdk.adapter.BNaviBaseCallbackModel;
import com.baidu.navisdk.adapter.BaiduNaviCommonModule;
import com.baidu.navisdk.adapter.NaviModuleFactory;
import com.baidu.navisdk.adapter.NaviModuleImpl;
import com.hyphenate.easeui.ui.EaseBaiduMapActivity;

import java.util.ArrayList;
import java.util.List;

import app.yy.geju.R;

/**
 * 诱导界面
 * @author sunhao04
 */
public class BNDemoGuideActivity extends Activity {

	private final String TAG = BNDemoGuideActivity.class.getName();
	private BNRoutePlanNode mBNRoutePlanNode = null;                //导航起始点
	private BaiduNaviCommonModule mBaiduNaviCommonModule = null ;
	private final static String RET_COMMON_MODULE = "module.ret";
	private static final int MSG_SHOW = 1;
	private static final int MSG_HIDE = 2;
	private static final int MSG_RESET_NODE = 3;
	private Handler hd = null;
	private String adder = "";

	/**
     * 对于导航模块有两种方式来实现发起导航。 1：使用通用接口来实现 2：使用传统接口来实现
     */
	// 是否使用通用接口
	private boolean useCommonInterface = true ; // true

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EaseBaiduMapActivity.activityList.add(this);
		createHandler();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		}
		View view = null;
		if (useCommonInterface) {
			//使用通用接口
			mBaiduNaviCommonModule = NaviModuleFactory.getNaviModuleManager().getNaviCommonModule(
					NaviModuleImpl.BNaviCommonModuleConstants.ROUTE_GUIDE_MODULE, this,
					BNaviBaseCallbackModel.BNaviBaseCallbackConstants.CALLBACK_ROUTEGUIDE_TYPE, mOnNavigationListener);
			if(mBaiduNaviCommonModule != null) {
				mBaiduNaviCommonModule.onCreate();
				view = mBaiduNaviCommonModule.getView();
			}
		} else {
			//使用传统接口
			view = BNRouteGuideManager.getInstance().onCreate(this,mOnNavigationListener);
		}
		//设置view
		if (view != null) {
			setContentView(view);
		}
		//获取起始点
		Intent intent = getIntent();
		if (intent != null) {
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				mBNRoutePlanNode = (BNRoutePlanNode) bundle.getSerializable(EaseBaiduMapActivity.ROUTE_PLAN_NODE);
				adder = bundle.getString(EaseBaiduMapActivity.ADDER);
			}
		}
		//显示自定义图标
		if (hd != null) {
			hd.sendEmptyMessageAtTime(MSG_SHOW, 5000);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(useCommonInterface) {
			if(mBaiduNaviCommonModule != null) {
				mBaiduNaviCommonModule.onResume();
			}
		} else {
			BNRouteGuideManager.getInstance().onResume();
		}
	}

	protected void onPause() {
		super.onPause();
		if(useCommonInterface) {
			if(mBaiduNaviCommonModule != null) {
				mBaiduNaviCommonModule.onPause();
			}
		} else {
			BNRouteGuideManager.getInstance().onPause();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(useCommonInterface) {
			if(mBaiduNaviCommonModule != null) {
				mBaiduNaviCommonModule.onDestroy();
			}
		} else {
			BNRouteGuideManager.getInstance().onDestroy();
		}
		EaseBaiduMapActivity.activityList.remove(this);

	}

	@Override
	protected void onStop() {
		super.onStop();
		if(useCommonInterface) {
			if(mBaiduNaviCommonModule != null) {
				mBaiduNaviCommonModule.onStop();
			}
		} else {
			BNRouteGuideManager.getInstance().onStop();
		}
	}

	@Override
	public void onBackPressed() {
		if(useCommonInterface) {
			if(mBaiduNaviCommonModule != null) {
				mBaiduNaviCommonModule.onBackPressed(false);
			}
		} else {
			BNRouteGuideManager.getInstance().onBackPressed(false);
		}
	}

	/**
     * 配置发生变化
	 * @param newConfig
	 */
	public void onConfigurationChanged(android.content.res.Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if(useCommonInterface) {
			if(mBaiduNaviCommonModule != null) {
				mBaiduNaviCommonModule.onConfigurationChanged(newConfig);
			}
		} else {
			BNRouteGuideManager.getInstance().onConfigurationChanged(newConfig);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if(useCommonInterface) {   //使用通用接口
			if(mBaiduNaviCommonModule != null) {
				Bundle mBundle = new Bundle();
				mBundle.putInt(RouteGuideModuleConstants.KEY_TYPE_KEYCODE, keyCode);
				mBundle.putParcelable(RouteGuideModuleConstants.KEY_TYPE_EVENT, event);
				mBaiduNaviCommonModule.setModuleParams(RouteGuideModuleConstants.METHOD_TYPE_ON_KEY_DOWN, mBundle);
				try {
					Boolean ret = (Boolean)mBundle.get(RET_COMMON_MODULE);
					if(ret) {
						return true;
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onStart() {
		super.onStart();
		// TODO Auto-generated method stub
		if(useCommonInterface) {
			if(mBaiduNaviCommonModule != null) {
				mBaiduNaviCommonModule.onStart();
			}
		} else {
			BNRouteGuideManager.getInstance().onStart();
		}
	}

	/**
	 * 显示自定义图标（demo 中使用的是 BD SDK 正方形图片）
	 */
	private void addCustomizedLayerItems() {
		List<CustomizedLayerItem> items = new ArrayList<CustomizedLayerItem>();
		CustomizedLayerItem item1 = null;
		if ( mBNRoutePlanNode != null ) {
			//获取传过来的地址 经度和纬度
			item1 = new CustomizedLayerItem(mBNRoutePlanNode.getLongitude(), mBNRoutePlanNode.getLatitude(),
					mBNRoutePlanNode.getCoordinateType(), getResources().getDrawable(R.drawable.icon_map3x),
					CustomizedLayerItem.ALIGN_CENTER);
			items.add(item1);
			BNRouteGuideManager.getInstance().setCustomizedLayerItems(items);
		}
		BNRouteGuideManager.getInstance().showCustomizedLayer(true);
	}

	/**
	 *创建 handler 是否显示自定义图标 和 地址复位的功能
	 */
	private void createHandler() {
		if (hd == null) {
			hd = new Handler(getMainLooper()) {  //绑定住线程的 Looper
				public void handleMessage( android.os.Message msg ) {
					if (msg.what == MSG_SHOW) {
						addCustomizedLayerItems();                                     //显示自定义图标
					} else if (msg.what == MSG_HIDE) {
						BNRouteGuideManager.getInstance().showCustomizedLayer(false);  //不显示自定义图标
					} else if (msg.what == MSG_RESET_NODE) {                           //复位到起始地点
						BNRouteGuideManager.getInstance().resetEndNodeInNavi(          //起始点的经度 纬度 还有 导航的类型
								//new BNRoutePlanNode(116.21142, 40.85087, "百度大厦11", null, CoordinateType.BD09LL));
								new BNRoutePlanNode(mBNRoutePlanNode.getLongitude() , mBNRoutePlanNode.getLatitude(), adder , null, CoordinateType.BD09LL));
					}
				}
			};
		}
	}

	/**
	 *导航监听
	 */
	private OnNavigationListener mOnNavigationListener = new OnNavigationListener() {
		@Override
		public void onNaviGuideEnd() {
			//退出导航
			finish();
		}
		@Override
		public void notifyOtherAction(int actionType, int arg1, int arg2, Object obj) {
			if (actionType == 0) {
				//导航到达目的地 自动退出
				Log.i(TAG, "notifyOtherAction actionType = " + actionType + ",导航到达目的地！");
			}
			Log.i(TAG, "actionType:" + actionType + "arg1:" + arg1 + "arg2:" + arg2 + "obj:" + obj.toString());
		}
	};

	/**
	 * 常量存放在在接口中
	 */
	private interface RouteGuideModuleConstants {
		final static int METHOD_TYPE_ON_KEY_DOWN = 0x01;
		final static String KEY_TYPE_KEYCODE = "keyCode";
		final static String KEY_TYPE_EVENT = "event";
	}
}
