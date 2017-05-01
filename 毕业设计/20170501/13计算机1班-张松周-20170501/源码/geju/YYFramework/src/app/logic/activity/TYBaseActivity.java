package app.logic.activity;

import org.ql.activity.customtitle.FragmentActActivity;
import org.ql.app.alert.AlertDialog;
import org.ql.utils.QLToastUtils;
import org.ql.views.textview.QLBadgeView;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import app.logic.activity.user.LoginActivity;
import app.utils.customview.AppDialog;
import app.yy.geju.R;

public class TYBaseActivity extends FragmentActActivity{

	protected View btnLeft;
	protected View btnRight;
	protected View maskLoadding;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void setContentView( int layoutResID) {
		super.setContentView(layoutResID);
		this.initTitleView();
		initSystemBarTitle();
	}

	@Override
	public void setContentView(View view, LayoutParams params) {
		super.setContentView(view, params);
		this.initTitleView();
	}

	@Override
	public void setContentView(View view) {
		super.setContentView(view);
		this.initTitleView();
	}
	
	private QLBadgeView badge;
	@Override
	protected void onResume() {
		Log.v("t", this.getClass().getName()+"--------onResume");
		View view = findViewById(R.id.btn_title_right);
		int followCount = 0;
		if( null!=view ){
			if( null==badge ){
				badge = new QLBadgeView(this, view);
			}
//			UserInfo userInfo = AccountManager.getInstance().getUserInfo();
//			if(null!=userInfo){				
//				int userId = userInfo.getId();
//				badge.setText(""+GameFollowManager.getInstance().getFollowCount(userId));
//				badge.setBadgePosition(QLBadgeView.POSITION_TOP_RIGHT);
//				badge.toggle();
//			}
			if(followCount==0&&badge.isShown()){
				badge.toggle();
			}
		}
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.v("t", this.getClass().getName()+"--------onPause");
	}

	private OnClickListener titleOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
//			Intent intent;
			switch (v.getId()) {
			case R.id.btn_title_right:
//				intent = new Intent(BaseHomeTabActivity.this, MoreActivity.class);
//				startActivity(intent);
				break;
			case R.id.btn_title_left:
//				intent = new Intent(BaseHomeTabActivity.this, SearchActivity.class);
//				startActivity(intent);
			break;
			}
		}
	};

	public void initTitleView() {
		btnLeft = findViewById(R.id.btn_title_left);
		btnRight = findViewById(R.id.btn_title_right);
		maskLoadding = findViewById(R.id.mask_loadding);
		if(null!=maskLoadding){
			maskLoadding.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					return true;
				}
			});
		}
		if( null!=btnLeft ){
			btnLeft.setOnClickListener(titleOnClickListener);
		}
		if( null!=btnRight ){
			btnRight.setOnClickListener(titleOnClickListener);			
		}
	}
	
	long lastKeyTime = 0;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {//LoginActivity
		if(KeyEvent.KEYCODE_BACK==keyCode){
			if( getParent()!=null && getParent() instanceof LoginActivity ){
				if(((LoginActivity) getParent()).isCanBack()){
					if( System.currentTimeMillis()-lastKeyTime>2000){
						lastKeyTime = System.currentTimeMillis();
						QLToastUtils.showToast(this, "再按一次退出程序");
					}else{
						((LoginActivity) getParent()).doFinish();
					}
				}
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@SuppressWarnings({ "unused", "deprecation" })
	private void exit() {
		AlertDialog dig = new AlertDialog.Builder(this).create();
		dig.setTitle("提示");
		dig.setMessage("要退出程序吗？");
		dig.setButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
		dig.setButton2("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		dig.show();
	}
	
	
	@Override
	protected Dialog onCreateDialog(int id) {
		return new AppDialog(this);
	}
	
	public void showLoaddingMask(){
		if(maskLoadding!=null){
			maskLoadding.setVisibility(View.VISIBLE);
		}
	}
	
	public void removeLoaddingMask(){
		if(maskLoadding!=null){
			maskLoadding.setVisibility(View.GONE);
		}
	}
	public void initSystemBarTitle() {
		if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			ViewGroup firstChildAtDecorView = ((ViewGroup) ((ViewGroup) getWindow().getDecorView()).getChildAt(0));
			View statusView = new View(this);
			ViewGroup.LayoutParams statusViewLp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					                                                         getStatusBarHeight(this));
			// 颜色的设置可抽取出来让子类实现之
			statusView.setBackgroundColor( getResources().getColor( R.color.acttitle2_bg ) );
			firstChildAtDecorView.addView( statusView , 0 , statusViewLp );
		}
	}
	// 获取状态栏的高度
	public int getStatusBarHeight( Context context ) {
		int statusBarHeight = 0 ;
		Resources resources = context.getResources() ;
		int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android") ;
		if ( resourceId > 0 ) {
			statusBarHeight = resources.getDimensionPixelSize(resourceId) ;
		}
		return statusBarHeight ;
	}
}
