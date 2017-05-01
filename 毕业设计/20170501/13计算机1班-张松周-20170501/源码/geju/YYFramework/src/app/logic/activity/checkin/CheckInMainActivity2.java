package app.logic.activity.checkin;

import org.ql.activity.customtitle.ActActivity;
import org.ql.utils.image.QLAsyncImage;
import org.ql.utils.image.QLImageUtil;
import org.ql.views.ImageView.CircleImageView;
import org.ql.views.listview.QLXListView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import app.logic.activity.ActTitleHandler;
import app.logic.activity.TYBaseActivity;
import app.logic.pojo.OrganizationInfo;
import app.utils.helpers.YYUtils;
import app.yy.geju.R;

/**
 * 
 * SiuJiYung create at 2016年8月3日 上午9:09:12
 * 
 */

public class CheckInMainActivity2 extends TYBaseActivity implements OnTabChangeListener {
	private ActTitleHandler titleHandler ;
	private OrganizationInfo mOrgInfo ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		titleHandler = new ActTitleHandler();
		setAbsHandler(titleHandler);
		setContentView( R.layout.activity_home_xiehui );
		//初始化TootBar
		initActHandler();
		//设置mTabHost
		setTabHost() ;
		setBackStatus(false);
	}
	
	/**
	 * 初始化TootBar
	 */
	private void initActHandler() {
		Intent intent = getIntent();
		mOrgInfo = (OrganizationInfo) intent.getSerializableExtra(MyOrganizaActivity.ORGINFO);
		setTitle("");
		titleHandler.getRightDefButton().setText("我的");
		titleHandler.getRightDefButton().setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent showMyTrackIntent = new Intent();
				showMyTrackIntent.setClass(CheckInMainActivity2.this, MyTraceActivity.class);
				showMyTrackIntent.putExtra(MyTraceActivity.ORGINFO, mOrgInfo);
				startActivity(showMyTrackIntent);
			}
		});
		titleHandler.getRightDefButton().setVisibility(View.INVISIBLE);// 一开始  我的  不显示
		titleHandler.replaseLeftLayout(this, true);
		((TextView) titleHandler.getLeftLayout().findViewById(R.id.left_tv)).setText(intent.getStringExtra(MyOrganizaActivity.ORGNAME));
		titleHandler.getLeftLayout().setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	/**
	 * 设置mTabHost
	 */
	private void setTabHost(){
		FragmentManager fManager = getSupportFragmentManager();
		FragmentTabHost mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, fManager, android.R.id.tabcontent);
		// 添加tab名称和图标
		View msgIndicator = getIndicatorView("签到", R.drawable.selector_tab_check_in);
		mTabHost.addTab(mTabHost.newTabSpec("签到").setIndicator(msgIndicator), CheckInFragment2.class, null);//签到界面
		View contactIndicator = getIndicatorView("足迹", R.drawable.selector_tab_track);
		mTabHost.addTab(mTabHost.newTabSpec("足迹").setIndicator(contactIndicator), TraceFragment2.class, null);//足迹界面
		//给mTabHost设置监听器
		mTabHost.setOnTabChangedListener(this);
	}
	/**
	 * 设置mTabHost使用的View
	 * @param name
	 * @param drawableRes
	 * @return
	 */
	private View getIndicatorView(String name, int drawableRes) {
		View v = getLayoutInflater().inflate(R.layout.check_in_tab_item, null);
		TextView tv = (TextView) v.findViewById(R.id.tab_item_title);
		Drawable drawable = getResources().getDrawable( drawableRes );
		int _h = YYUtils.dp2px((52-25 ), this);
		drawable.setBounds(0, 0, _h, _h);
		tv.setCompoundDrawables( null , drawable , null , null ); 
		tv.setText(name);
		return v;
	}
	/**
	 * FragmentTabHost 的监听
	 */
	@Override
	public void onTabChanged(String tabId) {
		if (tabId.equalsIgnoreCase("签到")) {
//			titleHandler.setTitle("签到");
			titleHandler.getRightDefButton().setVisibility(View.INVISIBLE);//我的  不显示
		} else {
//			titleHandler.setTitle("足迹");
			titleHandler.getRightDefButton().setVisibility(View.VISIBLE);
		}
	}
}
