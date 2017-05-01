package app.logic.activity.user;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.ql.activity.customtitle.ActActivity;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import app.logic.adapter.WelcomePagerAdapter;
import app.yy.geju.R;

/*
 * Zhangsongzhou create at 2016年7月19日 上午10:39
 */

public class WelcomeActivity extends ActActivity implements OnPageChangeListener, OnClickListener {

	private ViewPager mViewPager;
	private WelcomePagerAdapter mWPAdapter;
	private List<View> mViews;

	private String[] urls = new String[] { "welcome_view01.png", "welcome_view01.png", "welcome_view01.png" };

	private ImageView[] dots;
	private LinearLayout welcome_point_ll;
	private Button login_btn;
	private Button logon_btn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		setContentView(R.layout.activity_welcome);

		setTouchLeft2RightEnable(false);

		initViews();
		initData();
		
	}

	private void initViews() {

		welcome_point_ll = (LinearLayout) findViewById(R.id.welcome_point_linearlayout);
		mViewPager = (ViewPager) findViewById(R.id.welcome_viewpager);
		login_btn = (Button) findViewById(R.id.login_btn);
		logon_btn = (Button) findViewById(R.id.logon_btn);

		mViews = new ArrayList<View>();

		getPagerView(urls);

		mWPAdapter = new WelcomePagerAdapter(this, mViews);

		mViewPager.setAdapter(mWPAdapter);
		mViewPager.setOnPageChangeListener(this);
	}

	private void initData() {
		dots = new ImageView[mViews.size()];
		LinearLayout.LayoutParams params;
		for (int i = 0; i < mViews.size(); i++) {
			dots[i] = new ImageView(this);
			params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.setMargins(2, 2, 2, 5);
			dots[i].setLayoutParams(params);
			dots[i].setBackgroundResource(R.drawable.welcome_point);
			welcome_point_ll.addView(dots[i]);
		}

		dots[0].setBackgroundResource(R.drawable.welcome_point_selected);

		login_btn.setOnClickListener(this);
		logon_btn.setOnClickListener(this);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int arg0) {
		for (int i = 0; i < mViews.size(); i++) {
			if (arg0 == i) {
				dots[i].setImageResource(R.drawable.welcome_point_selected);
			} else {
				dots[i].setImageResource(R.drawable.welcome_point);
			}
		}
	}

	/*
	 * 显示图片
	 */
	private void getPagerView(String[] urls) {

		if (urls.length < 1) {
			return;
		}
		LayoutInflater inflater = LayoutInflater.from(this);

		Bitmap bitmap;
		InputStream is;
		for (int i = 0; i < urls.length; i++) {
			View view = inflater.inflate(R.layout.welcome_pageradapter_view, null);
			ImageView iv = (ImageView) view.findViewById(R.id.pagerview_iv);
			// 处理图片资源
			try {
				is = this.getAssets().open(urls[i]);
				bitmap = BitmapFactory.decodeStream(is);
				iv.setImageBitmap(bitmap);
			} catch (IOException e) {
				e.printStackTrace();
			}
			mViews.add(view);
		}
	}

	@Override
	public void onClick(View arg0) {
		int id = arg0.getId();
		Intent intent;
		if (id == R.id.login_btn) {
			intent = new Intent();
			intent.setClass(WelcomeActivity.this, LoginActivity.class);
			startActivity(intent);
		}
		if (id == R.id.logon_btn) {
			intent = new Intent();
			intent.setClass(WelcomeActivity.this, RegActivity.class);
			startActivity(intent);
		}

	}

}
