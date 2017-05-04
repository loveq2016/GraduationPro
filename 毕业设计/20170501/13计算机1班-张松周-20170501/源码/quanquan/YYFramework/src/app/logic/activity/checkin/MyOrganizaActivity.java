package app.logic.activity.checkin;

import java.util.ArrayList;
import java.util.List;

import org.ql.activity.customtitle.ActActivity;
import org.ql.views.listview.QLXListView.IXListViewListener;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import app.config.http.HttpConfig;
import app.logic.activity.ActTitleHandler;
import app.logic.adapter.YYBaseListAdapter;
import app.logic.controller.OrganizationController;
import app.logic.pojo.OrganizationInfo;
import app.utils.common.Listener;
import app.view.YYListView;
import app.yy.geju.R;

public class MyOrganizaActivity extends ActActivity implements IXListViewListener, OnItemClickListener {
    public static final String ORGNAME = "ORGNAME";
    public static final String ORGINFO = "ORGINFO";
    private ActTitleHandler titleHandler;
    //列表
    private YYListView listView;
    //列表为空的时候显示
    private View empty_view;
    //列表的数据源
    private List<OrganizationInfo> datas;
    //列表的适配器
    private YYBaseListAdapter<OrganizationInfo> adapter = new YYBaseListAdapter<OrganizationInfo>(MyOrganizaActivity.this) {
        @Override
        public View createView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(MyOrganizaActivity.this).inflate(R.layout.item_my_org_notice_layout, null);
                saveView("item_iv", R.id.item_iv, convertView);
                saveView("item_name_tv", R.id.item_name_tv, convertView);
                saveView("item_count", R.id.item_count, convertView);
            }
            OrganizationInfo info = getItem(position);
            if (info != null) {
                setImageToImageViewCenterCrop(HttpConfig.getUrl(info.getOrg_logo_url()), "item_iv", 0, convertView);
                setTextToViewText(info.getOrg_name(), "item_name_tv", convertView);
                TextView textView = getViewForName("item_count", convertView);
//				textView.setVisibility(info.getCount() < 1 ? View.GONE : View.VISIBLE);
                textView.setVisibility(View.GONE);//隐藏数量
            }
            return convertView;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        ((TextView) titleHandler.getLeftLayout().findViewById(R.id.left_tv)).setText("选择组织签到");
        titleHandler.getLeftLayout().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 初始化View
     */
    private void initView() {
        empty_view = findViewById(R.id.empty_view);
        ((TextView) findViewById(R.id.empty_tv01)).setText("您还没有加入任何组织");
        ((TextView) findViewById(R.id.empty_tv02)).setText("赶紧去加入组织才能查看签到");
        datas = new ArrayList<OrganizationInfo>();
        listView = (YYListView) findViewById(R.id.listview);
        listView.setAdapter(adapter);
        //设置Item监听
        listView.setOnItemClickListener(this);
        listView.setPullLoadEnable(false, false);
        listView.setXListViewListener(this);
        //获取我的组织列表
        getMyOrg();
    }

    /**
     * 获取我的组织
     */
    private void getMyOrg() {
        showWaitDialog();
        OrganizationController.getMyOrganizationList(this, new Listener<Void, List<OrganizationInfo>>() {
            @Override
            public void onCallBack(Void status, List<OrganizationInfo> reply) {
                dismissWaitDialog();
                //停止刷新
                listView.stopRefresh();
                listView.stopLoadMore();
                if (reply != null && reply.size() > 0) {
                    datas.clear(); //在请求成功的时候清出原来的数据，失败时不清楚原来的数据，目的：失败时界面上有数据
                    List<OrganizationInfo> infos = new ArrayList<OrganizationInfo>();//盛放过滤后的列表
                    for (OrganizationInfo info : reply) {
                        if (info.getOrg_status() != 10) {  //10是代表审核通过的（值获取审核通过的列表）
                            continue;
                        }
                        infos.add(info);
                    }
                    datas.addAll(infos);
                }
                adapter.setDatas(datas);
                if( datas.size()>0){
                    empty_view.setVisibility(View.GONE);     //列表不为空时不显示
                }else{
                    empty_view.setVisibility(View.VISIBLE);  //列表为空时显示
                }
            }
        });
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();
        //getMyOrg();
    }

    /**
     * OnItemClickListener 列表的点击事件
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub
        OrganizationInfo orgInfo = datas.get(position - 1);
        if (orgInfo != null) {
            Intent intent = new Intent();
            //intent.setClass(this, CheckInMainActivity2.class);
            intent.setClass(this, CheckInMainActivityYSF.class);
            intent.putExtra(ORGNAME, orgInfo.getOrg_name());
            intent.putExtra(ORGINFO, orgInfo);
            startActivity(intent);
        } else {
        }
    }

    /**
     * IXListViewListener 的接口
     * <p>
     * 刷新
     */
    @Override
    public void onRefresh() {
        // TODO Auto-generated method stub
        getMyOrg();
    }

    /**
     * 加载更多
     */
    @Override
    public void onLoadMore() {
        // TODO Auto-generated method stub
    }
}
