package app.logic.activity.announce;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.TextView;

import org.QLConstant;
import org.ql.activity.customtitle.ActActivity;
import org.ql.utils.QLToastUtils;
import org.ql.views.listview.QLXListView.IXListViewListener;

import java.util.ArrayList;
import java.util.List;

import app.config.http.HttpConfig;
import app.logic.activity.ActTitleHandler;
import app.logic.activity.org.CreateOranizationActivity;
import app.logic.adapter.YYBaseListAdapter;
import app.logic.controller.OrganizationController;
import app.logic.pojo.OrganizationInfo;
import app.utils.common.Listener;
import app.view.YYListView;
import app.yy.geju.R;

public class MyOrgActivity extends ActActivity implements IXListViewListener,OnItemClickListener{
	
	//是否是一个组织的管理员的标志变量
	private boolean haveOrgListStatus = false ;
	private ActTitleHandler titleHandler ;
	private OrganizationInfo orgInfo ;
	//列表
	private YYListView listView;
	//列表为空的时候显示
	private View empty_view;
	//列表的数据源
	private List<OrganizationInfo> datas = new ArrayList<>();
	private Button craetOrgBut ;
	//列表的适配器
	private YYBaseListAdapter<OrganizationInfo> adapter = new YYBaseListAdapter<OrganizationInfo>(MyOrgActivity.this) {
		@Override
		public View createView( int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(MyOrgActivity.this).inflate(R.layout.item_my_org_notice_layout2, null);
				saveView("item_iv", R.id.item_iv, convertView);
				saveView("item_name_tv", R.id.item_name_tv, convertView);
			}
			OrganizationInfo info = getItem( position );
			if (info != null) {
				//判断是否是管理员
				//getOrgInfo2( info.getOrg_id() , convertView );
				setImageToImageViewCenterCrop(HttpConfig.getUrl(info.getOrg_logo_url()), "item_iv", 0, convertView); //logo
				setTextToViewText(info.getOrg_name(), "item_name_tv", convertView);                                  //名字
			}
			return convertView ;
		}
	};

	
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate(savedInstanceState);
		if( savedInstanceState != null  ){
			QLConstant.client_id = savedInstanceState.getString("client_id");
			QLConstant.token = savedInstanceState.getString("token");
			//QLToastUtils.showToast( this , QLConstant.client_id ) ;
		}
		titleHandler = new ActTitleHandler();
		setAbsHandler(titleHandler);
		setContentView(R.layout.activity_my_organiza);
		//初始化TootBar
		initActHandler();
		//初始化View
		initView();
	}
	
	/**
	 * 初始化TootBar
	 */
	private void initActHandler() {
		setTitle("");
		titleHandler.replaseLeftLayout(this, true);
		((TextView) titleHandler.getLeftLayout().findViewById(R.id.left_tv)).setText("选择组织发布");
		titleHandler.getLeftLayout().setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		craetOrgBut = titleHandler.getRightDefButton();
		craetOrgBut.setVisibility( View.GONE );
		craetOrgBut.setText("创建组织");
		craetOrgBut.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent( MyOrgActivity.this , CreateOranizationActivity.class);
				startActivity(intent);
			}
		});
	}
	/**
	 * 初始化View
	 */
	private void initView() {
		empty_view = findViewById(R.id.empty_view);
		((TextView) findViewById(R.id.empty_tv01)).setText("暂时还没有创建过任何组织");
		((TextView) findViewById(R.id.empty_tv02)).setText("赶紧去创建组织才能发布公告");
		listView = (YYListView)findViewById(R.id.listview);
		listView.setAdapter(adapter);
		//设置Item监听
		listView.setOnItemClickListener( this );
		listView.setPullLoadEnable(false, false);
		listView.setXListViewListener(this);
		//获取我的组织列表
		getUserCreatOrgList();
	}

	/**
	 * 获取当前用户所创建的组织列表
	 */
	private void getUserCreatOrgList(){
		datas.clear(); //清出原来的数据
		OrganizationController.getUserCreatOrgList(this, new Listener<Integer, ArrayList<OrganizationInfo>>() {
			@Override
			public void onCallBack(Integer integer, ArrayList<OrganizationInfo> reply) {
				//停止刷新
				listView.stopRefresh();
				//停止加载更多
				listView.stopLoadMore();
				if (reply != null && reply.size() > 0) {
					datas = reply;
				}
				adapter.setDatas(datas);
				if( datas.size() > 0){
					//列表不为空时不显示
					empty_view.setVisibility(View.GONE);
					craetOrgBut.setVisibility(View.GONE);
				} else {
					//列表为空时显示
					empty_view.setVisibility(View.VISIBLE);
					craetOrgBut.setVisibility(View.VISIBLE);
				}
			}
		});
	}
	

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//getUserCreatOrgList();
	}
	
	@Override
	protected void onSaveInstanceState( Bundle outState ) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState( outState );
		//打开相册后QLConstant.client_id，QLConstant.token的值会变成null（不知道原因），说以要在这个方法中包这两个值保存起来
		outState.putString("client_id", QLConstant.client_id ) ;
		outState.putString("token", QLConstant.token ) ;
		//QLToastUtils.showToast(this, "onSaveInstanceState被执行");
	}
	
	/**
	 * OnItemClickListener 列表的点击事件
	 * 
	 */
	@Override
	public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
		// TODO Auto-generated method stub
		haveOrgListStatus = false;  //每次进入点击函数时都假设先不是这个组织的管理员
//		if (datas ==null || position<0 || datas.size()<position )
//			return;
		if(position-1>=0){
			orgInfo = datas.get( position - 1 );
		}
		if( orgInfo != null ){
			Intent intent = new Intent();
			intent.setClass( MyOrgActivity.this , AnnounceActivity.class);
			intent.putExtra(AnnounceActivity.ORGNAME, orgInfo.getOrg_name());
			intent.putExtra(AnnounceActivity.ORGINFO, orgInfo);
			startActivity( intent );
			//getOrgInfo( orgInfo.getOrg_id() ); //判断是否是管理员
		}
	}
	
	// 获取组织详情判断是否是管理员
	private void getOrgInfo2( String org_id , final View convertView ) {
		OrganizationController.getOrganizationInfo( this , org_id, new Listener<Void, List<OrganizationInfo>>() {
			@Override
			public void onCallBack( Void status, List<OrganizationInfo> reply) {
				if (reply == null || reply.size() < 1) {
					return;
				}
				if ( reply.get(0).getIsadmin() == 1 ) {
					convertView.findViewById(R.id.manger).setVisibility(View.VISIBLE) ;
				} 
			}
		});
	}
	
	// 获取组织详情判断是否是管理员
	private synchronized void getOrgInfo( String org_id ) {
		OrganizationController.getOrganizationInfo( this , org_id , new Listener<Void , List<OrganizationInfo>>() {
			@Override
			public void onCallBack(Void status, List<OrganizationInfo> reply) {
				if (reply == null || reply.size() < 1) {
					return;
				}
				if ( reply.get(0).getIsadmin() == 1 ) {
					haveOrgListStatus = true ;
				} else {
					haveOrgListStatus = false ;
				}
				if ( haveOrgListStatus ) {
					Intent intent = new Intent();
					intent.setClass( MyOrgActivity.this , AnnounceActivity.class);
					intent.putExtra(AnnounceActivity.ORGNAME, orgInfo.getOrg_name());
					intent.putExtra(AnnounceActivity.ORGINFO, orgInfo);
					startActivity( intent );
				} else {
					QLToastUtils.showToast( MyOrgActivity.this , "没有权限");
				}
			}
		});
	}

	/**
	 * 获取我的组织
	 */
	private void getMyOrg() {
		OrganizationController.getMyOrganizationList( this , new Listener< Void, List<OrganizationInfo> >() {
			@Override
			public void onCallBack( Void status, List<OrganizationInfo> reply) {
				//停止刷新
				listView.stopRefresh();
				if (reply != null && reply.size() > 0) {
					datas.clear(); //清出原来的数据
					List<OrganizationInfo> infos = new ArrayList<OrganizationInfo>();//盛放过滤后的列表
					for (OrganizationInfo info : reply) {
						if (info.getOrg_status() != 10) {  //10是代表审核通过的（值获取审核通过的列表）
							continue;
						}
						infos.add( info );
					}
					datas.addAll(infos);
					adapter.setDatas(datas);
					//列表不为空时不显示
					empty_view.setVisibility(View.GONE);
				} else {
					//列表为空时显示
					empty_view.setVisibility(View.VISIBLE);
				}
			}
		});
	}


	/**
	 * IXListViewListener 的接口
	 * 刷新
	 */
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		getUserCreatOrgList();
	}

	/**
	 * 加载更多
	 */
	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
	}
}
