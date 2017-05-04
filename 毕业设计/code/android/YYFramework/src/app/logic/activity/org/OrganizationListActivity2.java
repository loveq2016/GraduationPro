package app.logic.activity.org;

import java.util.ArrayList;
import java.util.List;

import org.ql.activity.customtitle.ActActivity;
import org.ql.views.listview.QLXListView.IXListViewListener;

import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.picasso.Picasso;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import app.config.http.HttpConfig;
import app.logic.activity.ActTitleHandler;
import app.logic.activity.main.HomeActivity;
import app.logic.adapter.YYBaseListAdapter;
import app.logic.controller.OrganizationController;
import app.logic.pojo.OrgUnreadNumberInfo;
import app.logic.pojo.OrganizationInfo;
import app.logic.singleton.YYSingleton;
import app.utils.common.FrescoImageShowThumb;
import app.utils.common.Listener;
import app.view.YYListView;
import app.yy.geju.R;
/*
 * GZYY    2016-12-15  下午4:32:40
 * author: zsz
 */

public class OrganizationListActivity2 extends ActActivity implements View.OnClickListener {

    private ActTitleHandler titleHandler;
    private YYListView listView;
    private View empty_view;
    private TextView tv1 , tv2 ;

    private boolean refreshStatus = true;

    private List<OrganizationInfo> datas = new ArrayList<OrganizationInfo>();
	private YYBaseListAdapter<OrganizationInfo> mAdapter = new YYBaseListAdapter<OrganizationInfo>(this) {
		@Override
		public View createView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(OrganizationListActivity2.this).inflate(R.layout.item_org_list_default, null);
				saveView("item_title_View", R.id.item_title_View, convertView);
				saveView("item_org_iv", R.id.item_org_iv, convertView);
				saveView("item_org_name_tv", R.id.item_org_name_tv, convertView);
				saveView("item_org_status_tv", R.id.item_org_status_tv, convertView);
                saveView("item_org_point_view", R.id.org_point_view, convertView);
			}
			OrganizationInfo info = getItem(position);
			if (info != null) {
				View titleView = getViewForName("item_title_View", convertView);
				SimpleDraweeView headView = getViewForName("item_org_iv", convertView);
				TextView nameTextView = getViewForName("item_org_name_tv", convertView);
				TextView statusTextView = getViewForName("item_org_status_tv", convertView);
                View pointView = getViewForName("item_org_point_view", convertView);
				titleView.setVisibility(info.isShowTitle() ? View.VISIBLE : View.GONE);
				String url = HttpConfig.getUrl(info.getOrg_logo_url());
//				Picasso.with(OrganizationListActivity2.this).load(url).error(R.drawable.default_user_icon).fit().into(headView);
//                headView.setImageURI(url);
                FrescoImageShowThumb.showThrumb(Uri.parse(url),headView);
				nameTextView.setText(info.getOrg_name());
				if(info.getOrg_status() == 0 ){
					statusTextView.setText("审核中");
					//statusTextView.setText(info.getRequestStatus());
				}else if(info.getOrg_status() == 12 ){
					statusTextView.setText("申请失败");
					//statusTextView.setText(info.getRequestStatus());
				}else{
					statusTextView.setText("");
                    pointView.setVisibility(View.GONE);
                    for (OrgUnreadNumberInfo numberInfo : YYSingleton.getInstance().getOrgUnreadDatas()){
                        if (numberInfo.getOrg_id().equals(info.getOrg_id())) {
                            pointView.setVisibility(View.VISIBLE);
                            break;
                        }
                    }

				}
				statusTextView.setVisibility(View.VISIBLE);
			}
			return convertView;
		}
	};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titleHandler = new ActTitleHandler();
        setAbsHandler(titleHandler);
        setContentView(R.layout.fragment_my_org_notice_list);
        initActHandler();
        initView();
        getDatas();

        IntentFilter filter = new IntentFilter();
        filter.addAction(HomeActivity.UPDATA_ORG_POINT);
        registerReceiver(mBroadcastReceiver,filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter !=null)
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBroadcastReceiver!=null)
            unregisterReceiver(mBroadcastReceiver);
    }

    private void initActHandler() {
        setTitle("");
        titleHandler.replaseLeftLayout(this, true);
        ((TextView) titleHandler.getLeftLayout().findViewById(R.id.left_tv)).setText("我的格局");
        titleHandler.getLeftLayout().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        titleHandler.addRightView(LayoutInflater.from(this).inflate(R.layout.homeactivity_rightlayout, null), true);
        titleHandler.getRightLayout().setVisibility(View.VISIBLE);
        ImageButton imageButton = (ImageButton) titleHandler.getRightLayout().findViewById(R.id.imageButton02);
        imageButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.nav_add2x));
        imageButton.setOnClickListener(this);
    }

    /**
     * 初始化View
     */
    private void initView() {
        tv1 = (TextView) findViewById(R.id.empty_tv01);
        tv2 = (TextView) findViewById(R.id.empty_tv02);
        empty_view = findViewById(R.id.empty_view);
        listView = (YYListView) findViewById(R.id.listview);
        listView.setPullLoadEnable(false, false);
        //listView.setPullLoadEnable(false);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OrganizationInfo info = (OrganizationInfo) mAdapter.getItem(position - 1);
                if (info == null) {
                    return;
                }
                Intent intent = new Intent();
                if (info.getOrg_status() == 10) {  //通过审核
                    intent.setClass(OrganizationListActivity2.this, DPMListActivity.class);
                    intent.putExtra(DPMListActivity.kORG_ID, info.getOrg_id());
                    intent.putExtra(DPMListActivity.kORG_NAME, info.getOrg_name());
                }
                if (info.getOrg_status() == 0 || info.getOrg_status() == 12) {   // 0 等待审核 ， 12 拒绝
                    intent.setClass(OrganizationListActivity2.this, CreateOranizationActivity.class);
                    intent.putExtra(CreateOranizationActivity.ORG_ID, info.getOrg_id());
                    intent.putExtra(CreateOranizationActivity.OPEN_MODE, CreateOranizationActivity.AUDIT_MODE);
                }
                startActivityForResult(intent,111);
            }
        });
        listView.setAdapter(mAdapter);
        listView.setXListViewListener(new IXListViewListener() {

            @Override
            public void onRefresh() {
                getDatas();
            }

            @Override
            public void onLoadMore() {
                // TODO Auto-generated method stub

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 111 && resultCode == RESULT_OK){
            getDatas();
        }
    }

    private void getDatas() {
        if (!refreshStatus) {
            return;
        }
        refreshStatus = false;
        showWaitDialog();
        OrganizationController.getMyOrganizationList(this, new Listener<Void, List<OrganizationInfo>>() {
            @Override
            public void onCallBack(Void status, List<OrganizationInfo> reply) {
                dismissWaitDialog();
                listView.stopRefresh();
                listView.stopLoadMore();
                if (reply == null || reply.size() < 1) {

                }else{

                }
                addStatusDatas(reply); //这个方法中datas有清楚数据
                mAdapter.setDatas(datas);
                if(datas.size()>0){
                    empty_view.setVisibility(View.GONE);
                }else{
                    tv1.setText("您还没有加入任何组织");
                    tv2.setText("赶紧去找个组织加入吧");
                    empty_view.setVisibility(View.VISIBLE);
                }
                refreshStatus = true;
            }
        });
    }

    /**
     * 对数据的分类处理 getOrg_status(); 0是正在审核，10是已经通过(1)，12是拒绝(2)
     * <p>
     * addStatusDatasOrganizationListActivity2
     */
    private void addStatusDatas(List<OrganizationInfo> list) {
        if (list==null || list.size()==0){
            return;
        }
        List<OrganizationInfo> passInfos = new ArrayList<OrganizationInfo>();
        List<OrganizationInfo> proInfos = new ArrayList<OrganizationInfo>();
        boolean oneShow = true;
        for (OrganizationInfo info : list) {
				System.out.println(" info RequestStatus is = "+info.getRequestStatus() );
			if (info.getOrg_status() == 0 ){
				if (oneShow) {
					info.setShowTitle(true);
					oneShow = false;
				}
				proInfos.add(info);
			} else if (info.getOrg_status() == 12) {
				info.setRequestStatus("申请失败");
				System.out.println(" info RequestStatus is = "+info.getRequestStatus() );
				if (oneShow) {
					info.setShowTitle(true);
					oneShow = false;
				}
				proInfos.add(info);
			} else {
				passInfos.add(info);
			}
		}
		passInfos.addAll(proInfos);
		datas.clear();
		datas.addAll(passInfos);
	}

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.imageButton02) {
            startActivity(new Intent(this, ApplyAssociActivity.class));  //搜索组织
        }
    }

    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case HomeActivity.UPDATA_ORG_POINT:
                    onResume();
                    break;
                default:
                    break;
            }

        }
    };
}
