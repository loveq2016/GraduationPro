package app.logic.activity.navi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.navisdk.adapter.BNOuterLogUtil;
import com.baidu.navisdk.adapter.BNOuterTTSPlayerCallback;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNRoutePlanNode.CoordinateType;
import com.baidu.navisdk.adapter.BNaviSettingManager;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.baidu.navisdk.adapter.BaiduNaviManager.NaviInitListener;
import com.baidu.navisdk.adapter.BaiduNaviManager.RoutePlanListener;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import app.yy.geju.R;

public class BNDemoMainActivity extends Activity {


	public static List<Activity> activityList = new LinkedList<Activity>();

	private static final String APP_FOLDER_NAME = "BNSDKSimpleDemo";

	private Button mWgsNaviBtn = null;  //国际
	private Button mGcjNaviBtn = null;  //国测
	private Button mBdmcNaviBtn = null; //百度墨卡
	private Button mDb06ll = null;      //百度经纬度
	private String mSDCardPath = null;  //SDC 卡路径

	public static final String ROUTE_PLAN_NODE = "routePlanNode";
	public static final String SHOW_CUSTOM_ITEM = "showCustomItem";
	public static final String RESET_END_NODE = "resetEndNode";
	public static final String VOID_MODE = "voidMode";
	String authinfo = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		activityList.add(this);
//		setContentView(R.layout.activity_main);
//		mWgsNaviBtn = (Button) findViewById(R.id.wgsNaviBtn);
//		mGcjNaviBtn = (Button) findViewById(R.id.gcjNaviBtn);
//		mBdmcNaviBtn = (Button) findViewById(R.id.bdmcNaviBtn);
//		mDb06ll = (Button) findViewById(R.id.mDb06llNaviBtn);
		activityList.add(this);
		// 打开log开关（百度导航日志）
		BNOuterLogUtil.setLogSwitcher(true);
		//初始化 四个按钮 监听器
		initListener();
		if (initDirs()) {   //文件初始化
			initNavi();     //百度 导航初始化
		}
	}

	/**
	 *  四个按钮 初始化监听器
	 */
	private void initListener() {
		if (mWgsNaviBtn != null) {   //国际
			mWgsNaviBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (BaiduNaviManager.isNaviInited()) {
						routeplanToNavi(CoordinateType.WGS84);  //计算路线
					}
				}

			});
		}
		if (mGcjNaviBtn != null) {   //国测
			mGcjNaviBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (BaiduNaviManager.isNaviInited()) {
						routeplanToNavi(CoordinateType.GCJ02);  //计算路线
					}
				}

			});
		}
		if (mBdmcNaviBtn != null) {  //百度墨卡
			mBdmcNaviBtn.setOnClickListener( new OnClickListener() {
				@Override
				public void onClick( View arg0 ) {
					if (BaiduNaviManager.isNaviInited()) {
						routeplanToNavi(CoordinateType.BD09_MC);  //计算路线
					}
				}
			});
		}

		if (mDb06ll != null) {   //百度经纬度
			mDb06ll.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (BaiduNaviManager.isNaviInited()) {
						routeplanToNavi(CoordinateType.BD09LL);   //计算路线
					}
				}
			});
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
		BaiduNaviManager.getInstance().init(this, mSDCardPath , APP_FOLDER_NAME, new NaviInitListener() {
			@Override
			public void onAuthResult(int status, String msg) {
				if (0 == status) {
					authinfo = "key校验成功!";
				} else {
					authinfo = "key校验失败, " + msg;
					System.out.println(" authinfo = "+authinfo);
				}
				BNDemoMainActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(BNDemoMainActivity.this, authinfo, Toast.LENGTH_LONG).show();
					}
				});
			}

			/**
			 * 初始化成功
			 */
			public void initSuccess() {
				Toast.makeText(BNDemoMainActivity.this, "百度导航引擎初始化成功", Toast.LENGTH_SHORT).show();
				//导航 配置 设置
				initSetting();
			}

			/**
			 * 初始化开始
			 */
			public void initStart() {
				Toast.makeText(BNDemoMainActivity.this, "百度导航引擎初始化开始", Toast.LENGTH_SHORT).show();
			}

			/**
			 *初始化失败
			 */
			public void initFailed() {
				Toast.makeText(BNDemoMainActivity.this, "百度导航引擎初始化失败", Toast.LENGTH_SHORT).show();
			}

		} ,  null , ttsHandler , ttsPlayStateListener );

	}

	/**
	 * 内部TTS播报状态回传handler
	 */
	private Handler ttsHandler = new Handler() {
		public void handleMessage(Message msg) {
			int type = msg.what;
			switch (type) {
				case BaiduNaviManager.TTSPlayMsgType.PLAY_START_MSG: {
					showToastMsg("Handler : TTS play start");
					break;
				}
				case BaiduNaviManager.TTSPlayMsgType.PLAY_END_MSG: {
					showToastMsg("Handler : TTS play end");
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
			showToastMsg("TTSPlayStateListener : TTS play end");
		}

		@Override
		public void playStart() {
			showToastMsg("TTSPlayStateListener : TTS play start");
		}
	};

	/**
	 * 显示消息
	 * @param msg
	 */
	public void showToastMsg(final String msg) {
		BNDemoMainActivity.this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(BNDemoMainActivity.this, msg, Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 *导航路线计算 （ 根据不同的格式计算 ）
	 * @param coType
	 */
	private void routeplanToNavi(CoordinateType coType) {
		BNRoutePlanNode sNode = null;   //开始点
		BNRoutePlanNode eNode = null;   //结束点
		switch (coType) {
			case GCJ02: {
				sNode = new BNRoutePlanNode(116.30142, 40.05087, "百度大厦", null, coType);
				eNode = new BNRoutePlanNode(116.39750, 39.90882, "北京天安门", null, coType);
				break;
			}
			case WGS84: {
				sNode = new BNRoutePlanNode(116.300821, 40.050969, "百度大厦", null, coType);
				eNode = new BNRoutePlanNode(116.397491, 39.908749, "北京天安门", null, coType);
				break;
			}
			case BD09_MC: {
				sNode = new BNRoutePlanNode(12947471, 4846474, "百度大厦", null, coType);
				eNode = new BNRoutePlanNode(12958160, 4825947, "北京天安门", null, coType);
				break;
			}
			case BD09LL: {
				sNode = new BNRoutePlanNode(116.30784537597782, 40.057009624099436, "百度大厦", null, coType);
				eNode = new BNRoutePlanNode(116.40386525193937, 39.915160800132085, "北京天安门", null, coType);
				break;
			}
			default:
				;
		}

		if (sNode != null && eNode != null) {
			List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
			list.add(sNode);
			list.add(eNode);
			//启动导航
			BaiduNaviManager.getInstance().launchNavigator( this, list , 1 , true , new DemoRoutePlanListener(sNode));
		}
	}

	/**
	 *路由监听
	 */
	public class DemoRoutePlanListener implements RoutePlanListener {
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
				if (ac.getClass().getName().endsWith("BNDemoGuideActivity")) {
					return;
				}
			}
			Intent intent = new Intent( BNDemoMainActivity.this, BNDemoGuideActivity.class );
			Bundle bundle = new Bundle();
			bundle.putSerializable( ROUTE_PLAN_NODE,  mBNRoutePlanNode );
			intent.putExtras( bundle );
			startActivity( intent );
		}

		@Override
		public void onRoutePlanFailed() {
			// TODO Auto-generated method stub
			Toast.makeText(BNDemoMainActivity.this, "算路失败", Toast.LENGTH_SHORT).show();
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

	@Override
	protected void onResume() {
		super.onResume();
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
