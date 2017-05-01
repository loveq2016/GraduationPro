package app.logic.activity.checkin;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.logic.activity.ActTitleHandler;
import app.logic.activity.TYBaseActivity;
import app.logic.adapter.LaunchPagerAdapter;
import app.logic.pojo.OrganizationInfo;
import app.utils.helpers.YYUtils;
import app.view.CustomViewPager;
import app.yy.geju.R;

/**
 * 
 * SiuJiYung create at 2016年8月3日 上午9:09:12
 * 
 */

public class CheckInMainActivityYSF extends TYBaseActivity implements OnTabChangeListener , View.OnClickListener {
	private ActTitleHandler titleHandler ;
	private OrganizationInfo mOrgInfo ;
	private TextView checkInTv ,  traceTv ;
	private CheckInFragment2 checkInFragment ;
	private TraceFragment2 traceFragment ;
	private CustomViewPager viewPager;
	private Drawable chesDrawable_y , chesDrawable_n , traceDrawable_y , traceDrawable_n ;
	private  List<Fragment> fragments = new ArrayList<>();
	private MyAdapter myAdapter ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		titleHandler = new ActTitleHandler();
		setAbsHandler(titleHandler);
		setContentView( R.layout.activity_home_xiehui_ysf );
		//初始化TootBar
		initActHandler();
		//初始化View
		intiView() ;
		setBackStatus(false);//取消左滑返回功能，TYBaseActivity带有这个功能
	}
	
	/**
	 * 初始化TootBar
	 */
	private void initActHandler() {
		checkInFragment = new CheckInFragment2();
		traceFragment = new TraceFragment2() ;
		fragments.add( checkInFragment ) ;
		fragments.add( traceFragment ) ;
		Intent intent = getIntent();
		mOrgInfo = (OrganizationInfo) intent.getSerializableExtra(MyOrganizaActivity.ORGINFO);
		setTitle("");
		titleHandler.getRightDefButton().setText("我的");
		titleHandler.getRightDefButton().setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent showMyTrackIntent = new Intent();
				showMyTrackIntent.setClass(CheckInMainActivityYSF.this, MyTraceActivity.class);
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
	 * 初始View
	 */
	private void intiView(){
		chesDrawable_y = getResources().getDrawable( R.drawable.bottom_sign_s );
		chesDrawable_n = getResources().getDrawable( R.drawable.bottom_sign_d );
		traceDrawable_y = getResources().getDrawable( R.drawable.bottom_footprint_s );
		traceDrawable_n = getResources().getDrawable( R.drawable.bottom_footprint_d );
		viewPager = (CustomViewPager) findViewById(R.id.viewpager );
		myAdapter = new MyAdapter(getSupportFragmentManager());
		viewPager.setAdapter(myAdapter);
		viewPager.setPagingEnabled(false); //设置ViewPager的左右滑动
		checkInTv = (TextView) findViewById( R.id.checkin_tab_tv) ;
		traceTv = (TextView) findViewById(R.id.footprint_tab_tv) ;
		checkInTv.setOnClickListener( this );
		traceTv.setOnClickListener( this );
		titleHandler.getRightDefButton().setVisibility(View.INVISIBLE);//我的  不显示
		// 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
		chesDrawable_y.setBounds(0,0,chesDrawable_y.getMinimumWidth(),chesDrawable_y.getMinimumHeight());
		chesDrawable_n.setBounds(0,0,chesDrawable_y.getMinimumWidth(),chesDrawable_y.getMinimumHeight());
		traceDrawable_y.setBounds(0,0,traceDrawable_n.getMinimumWidth(),traceDrawable_n.getMinimumHeight());
		traceDrawable_n.setBounds(0,0,traceDrawable_n.getMinimumWidth(),traceDrawable_n.getMinimumHeight());
		checkInTv.setCompoundDrawables( null , chesDrawable_y , null , null );
		traceTv.setCompoundDrawables( null , traceDrawable_n , null , null );
		viewPager.setCurrentItem(0);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId() ;
		switch ( id ){
			case R.id.checkin_tab_tv :
				titleHandler.getRightDefButton().setVisibility(View.INVISIBLE);//我的  不显示
				checkInTv.setCompoundDrawables( null , chesDrawable_y , null , null );
				traceTv.setCompoundDrawables( null , traceDrawable_n , null , null );
				viewPager.setCurrentItem(0);
				break;
			case R.id.footprint_tab_tv :
				titleHandler.getRightDefButton().setVisibility(View.VISIBLE);
				checkInTv.setCompoundDrawables( null , chesDrawable_n , null , null );
				traceTv.setCompoundDrawables( null , traceDrawable_y , null , null );
				viewPager.setCurrentItem(1);
				break;
		}
	}

	/**
	 * 适配器
	 */
	private class MyAdapter extends FragmentPagerAdapter {

		public MyAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return fragments.get(position);
		}

		@Override
		public int getCount() {
			return fragments.size();
		}
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
			//titleHandler.setTitle("签到");
			titleHandler.getRightDefButton().setVisibility(View.INVISIBLE);//我的  不显示
		} else {
			//titleHandler.setTitle("足迹");
			titleHandler.getRightDefButton().setVisibility(View.VISIBLE);
		}
	}
}
