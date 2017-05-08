package app.logic.activity.org;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.QLConstant;
import org.ql.activity.customtitle.ActActivity;
import org.ql.views.listview.QLXListView.IXListViewListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import app.config.http.HttpConfig;
import app.logic.activity.ActTitleHandler;
import app.logic.activity.user.PreviewFriendsInfoActivity;
import app.logic.adapter.YYBaseListAdapter;
import app.logic.controller.OrganizationController;
import app.logic.pojo.IntentInfo;
import app.logic.pojo.UserInfo;
import app.utils.common.Listener;
import app.view.YYListView;
import app.yy.geju.R;

/**
 * 
 * SiuJiYung create at 2016年7月4日 下午2:30:56
 * 
 */

public class DPMDetailsActivity extends ActActivity implements IXListViewListener, OnItemClickListener {
	private ActTitleHandler titleHandler = new ActTitleHandler();
	public static final String kORG_ID = "kORG_ID";
	public static final String KDPM_ID = "KDPM_ID";
	public static final String KTITLE = "KTITLE";
	public static final String KORG_NAME = "KORG_NAME";
	public static final String ISBUILDER = "ISBUILDER";
	public static Handler mHandler ;

	private String org_id , org_name;
	private String dpm_id , dpm_name , adinmeName ;
	private boolean isBuilder = false ; //是否是这个组织的超级管理员
	private boolean isAdmi = false ;    //是否是这个分组的管理员
	private ArrayList<UserInfo> memberList = new ArrayList<UserInfo>();
	private YYListView mListView ;
	private View view ;

	//存放管理员的集合
	//private ArrayList<UserInfo> adminList = new ArrayList<UserInfo>();
    private UserInfo adminInfo ;  //这个分组的管理员对象

	//是否可管理（默认false）
	private boolean isCanManage = false ;

	private YYBaseListAdapter<UserInfo> mAdapter = new YYBaseListAdapter<UserInfo>(this) {
		@Override
		public View createView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.item_dpm_memberinfo, null );
				saveView("dpm_memberinfo_header_iv", R.id.dpm_memberinfo_header_iv, convertView );
				saveView("dpm_memberinfo_name_tv", R.id.dpm_memberinfo_name_tv, convertView );
				saveView("item_admin_tv", R.id.item_admin_tv, convertView );
			}
			UserInfo info = getItem( position );
			if (info != null) {
				if(info.getFriend_name()!=null&& !TextUtils.isEmpty(info.getFriend_name())){
					setTextToViewText(info.getFriend_name(), "dpm_memberinfo_name_tv", convertView);
				}else{
					setTextToViewText(info.getNickName(), "dpm_memberinfo_name_tv", convertView);
				}
				String url = HttpConfig.getUrl(info.getPicture_url());
				ImageView imageView = getViewForName("dpm_memberinfo_header_iv", convertView);
				setImageToImageViewCenterCrop(url, "dpm_memberinfo_header_iv", R.drawable.default_user_icon, convertView);
				TextView mangerTv = getViewForName("item_admin_tv", convertView);
				if( info.isadmin() ){
					mangerTv.setVisibility( View.VISIBLE);
				}else {
					mangerTv.setVisibility( View.GONE );
				}
			}
			return convertView;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbsHandler(titleHandler);
		setContentView( R.layout.activity_dpm_details );
		//获取组织Id 、分组Id 、 分组名称 ，当前用户是不是这个组织的超级管理员的标志
		org_id = getIntent().getStringExtra(kORG_ID);
		dpm_id = getIntent().getStringExtra(KDPM_ID);
		dpm_name = getIntent().getStringExtra(KTITLE) ;
		isBuilder = getIntent().getBooleanExtra( ISBUILDER  , false);
		//初始化TootBar
		initTitle();
		mListView = (YYListView) findViewById(R.id.dpm_details_listview);
		mListView.setPullRefreshEnable(true);
		mListView.setPullLoadEnable(false, true);
		mListView.setXListViewListener(this);
		mListView.setOnItemClickListener(this);
		mListView.setAdapter( mAdapter );
		view = findViewById( R.id.nomamer_rl);
	}

	/**
	 * 初始化TootBar
	 */
	private void initTitle() {
		setTitle("");
		titleHandler.getRightDefButton().setText("管理");
		titleHandler.getRightLayout().setVisibility(View.GONE );
		titleHandler.getRightDefButton().setTextColor(0xfffcfcfc);
		titleHandler.getRightDefButton().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(DPMDetailsActivity.this, DPMDetailsForEditActivity2.class);
				IntentInfo info = new IntentInfo();
				info.setTitle("管理分组");
				info.setOpenMode(IntentInfo.EDIT_MODE);  //編輯模式
				info.setDpmId(dpm_id);       //部門ID
				info.setOrgId(org_id);       //協會ID
				info.setOrgName(getIntent().getStringExtra(KORG_NAME));
				info.setAdmin(true);         //分组的管理员
				intent.putExtra( IntentInfo.INTENT_INFO , info );
				intent.putExtra( DPMDetailsForEditActivity2.DPMNAME , dpm_name ) ;    //分组名称
				intent.putExtra( DPMDetailsForEditActivity2.ISBUILDER , isBuilder ); //当前用户是这个这个组织的超级管理员 ？
				intent.putExtra( DPMDetailsForEditActivity2.ISADMIE , isAdmi );      //当前用户是这个这个分组烦人管理员 ？
				//intent.putExtra( DPMDetailsForEditActivity2.ADNINLIST , adminList );  //专门存放管理员的集合dpmName
                intent.putExtra( DPMDetailsForEditActivity2.ANDIMINFO , adminInfo );
				intent.putExtra( DPMDetailsForEditActivity2.ADINMENAME , adinmeName );
				startActivity(intent);
			}
		});
		titleHandler.replaseLeftLayout(this, true);
		titleHandler.getLeftLayout().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		((TextView) titleHandler.getLeftLayout().findViewById(R.id.left_tv)).setText(dpm_name);
		mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Bundle bundle = msg.getData() ;
				((TextView) titleHandler.getLeftLayout().findViewById(R.id.left_tv)).setText(bundle.getString("name"));
			}
		};
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		adminInfo = null ;
		//获取分组下的成员列表
		getMemberList();
	}
	/**
	 * 获取分组下的成员列表
	 */
	private void getMemberList() {
		showWaitDialog();
		OrganizationController.getDPMMemberList(this, org_id , dpm_id , new Listener<Void, List<UserInfo>>() {
			@Override
			public void onCallBack(Void status, List<UserInfo> reply) {
				mListView.stopLoadMore();
				mListView.stopRefresh();
				dismissWaitDialog();
				if (reply != null ) {
					memberList.clear();
					for (UserInfo info : reply) {
//						if (info.getPhone() != null && !TextUtils.isEmpty( info.getPhone()) ) {
							memberList.add( info );
//						}
					}
					//对集合进行排序（ 管理员的在前面 ）
					memberList = sortList( memberList ) ;
				}
				//判断是否可 管理
				getIsCanManage(  memberList ) ;
				if( isBuilder || isAdmi ){  //当前用户是这个组织的超级管理员或者是之个分组的管理员
					titleHandler.getRightLayout().setVisibility(View.VISIBLE);
				}else {
					titleHandler.getRightLayout().setVisibility(View.GONE);
				}
				if( memberList.size() < 1){
					view.setVisibility( View.VISIBLE);
				}else{
					view.setVisibility( View.GONE );
				}
				mAdapter.setDatas( memberList );
			}
		});
	}
	/**
	 * 判断是否可以管理
	 */
	private void getIsCanManage(  ArrayList<UserInfo> reply ){
		if( reply == null || reply.size() <= 0 ){
			return;
		}
		isAdmi = false ;
		for ( UserInfo info : reply ) {
			if( info.isadmin() && info.getWp_member_info_id().equals( QLConstant.client_id )){ //当前用户在这个分组下，并且是这个分组的管理员
				isAdmi = true ;
				isCanManage = true ;
				break;
			}
		}
	}
	/**
	 * 对集合进行排序
	 * @param reply
	 * @return
	 */
	private ArrayList<UserInfo> sortList( List<UserInfo> reply ){
		if( reply == null ){
			return  null ;
		}
		ArrayList<UserInfo> sortListed = new ArrayList<UserInfo>();
		Iterator iterator = reply.iterator() ;//获取迭代器
		while ( iterator.hasNext() ){
			UserInfo userInfo = (UserInfo)iterator.next();
			if( userInfo.isadmin() ){
				sortListed.add( userInfo ) ;  //把管理员的添加到前面
//				adminList.add( userInfo ) ;   //专门存放管理员的集合
				if(userInfo.getFriend_name()!=null && !TextUtils.isEmpty(userInfo.getFriend_name())){
					adinmeName = userInfo.getFriend_name();
				}else{
					adinmeName = userInfo.getName() ;
				}
                adminInfo =  userInfo ;       //这个分组的管理员
				iterator.remove();            //原来集合移除当前这个的管理员项
                break;
			}
		}
		sortListed.addAll( sortListed.size() , reply );
		return  sortListed ;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position , long id ) {
		UserInfo info = mAdapter.getItem(position - 1);
		if (info != null) {
			Intent intent = new Intent(DPMDetailsActivity.this, PreviewFriendsInfoActivity.class);
			intent.putExtra(PreviewFriendsInfoActivity.FROMORG, "FROMORG");
			intent.putExtra(PreviewFriendsInfoActivity.kUSER_MEMBER_ID, info.getWp_member_info_id());
			startActivity(intent);
		}
	}

	@Override
	public void onRefresh() {
		getMemberList();
	}
	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
	}
}
