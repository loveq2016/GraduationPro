package app.logic.activity.org;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.canson.view.swipemenulistview.SwipeMenu;
import org.canson.view.swipemenulistview.SwipeMenuCreator;
import org.canson.view.swipemenulistview.SwipeMenuItem;
import org.canson.view.swipemenulistview.SwipeMenuListView;
import org.ql.activity.customtitle.ActActivity;
import org.ql.utils.QLToastUtils;
import org.ql.views.listview.QLXListView.IXListViewListener;

import java.util.ArrayList;
import java.util.List;

import app.config.http.HttpConfig;
import app.logic.activity.ActTitleHandler;
import app.logic.activity.main.HomeActivity;
import app.logic.activity.notice.DefaultNoticeActivity;
import app.logic.adapter.LaunchPagerAdapter;
import app.logic.adapter.YYBaseListAdapter;
import app.logic.controller.AnnounceController;
import app.logic.controller.OrganizationController;
import app.logic.pojo.NoticeInfo;
import app.logic.pojo.OrgUnreadNumberInfo;
import app.logic.pojo.OrganizationInfo;
import app.logic.singleton.YYSingleton;
import app.utils.common.FrescoImageShowThumb;
import app.utils.common.Listener;
import app.utils.helpers.YYUtils;
import app.view.CustomViewPager;
import app.view.YYListView;
import app.yy.geju.R;

/*
 * GZYY    2016-12-7  下午3:53:43
 * author: zsz
 */

public class DPMListNewActivity extends ActActivity {

    public static final String ORG_ID = "ORG_ID";
    public static final String ORG_NAME = "ORG_NAME";
    public static final String ORG_IMAGE = "ORG_IMAGE";

    private String org_id;  //组织ID
    private String orgImageUrl;

    private TextView unRead_tv, hasRead_tv; //已读，未读
    private View unRead_iv, hasRead_iv;     //已读，未读下面的那条线
    private CustomViewPager viewPager;      //ViewPager
    private LaunchPagerAdapter pagerAdapter;
    private Resources resources;
    private LayoutInflater inflater;

    private View unReadView, hasReadView,topUnReadBg,topHasReadBg;
    private List<View> views = new ArrayList<View>();
    private View empty_view1,empty_view2;
    private TextView tv1,tv2,tv3,tv4;

    private YYListView unReedListView, hasReadListView;                  //已读列表，未读列表
    private YYBaseListAdapter<OrganizationInfo> unReadAdapter, hasReadAdapter; //已读列表适配器 ，未读列表适配器
    private List<OrganizationInfo> unReadDatas, hasReadDatas;                  //已读列表数据源，未读列表数据源

    private ActTitleHandler titleHandler = new ActTitleHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        setAbsHandler(titleHandler);
        setContentView(R.layout.activity_org_list_new);
        org_id = getIntent().getStringExtra(ORG_ID);
        orgImageUrl = getIntent().getStringExtra(ORG_IMAGE);
        initActHandler();
        initView();
        setAdapters();
        addListener();
        setSwipeMenu();

        changeTab(0);
        viewPager.setCurrentItem(0);

    }

    @Override
    protected void onResume() {
        super.onResume();
//        addDatas(-1);
        getDatas();
    }

    /**
     * 初始化TootBar
     */
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
        imageButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DPMListNewActivity.this, ApplyAssociActivity.class));  //搜索组织
            }
        });
    }

    /**
     * 初始化View
     */
    private void initView() {

        inflater = LayoutInflater.from(this);
        resources = getResources();
        unRead_tv = (TextView) findViewById(R.id.unread_tv);
        hasRead_tv = (TextView) findViewById(R.id.hasRead_tv);

//        unRead_iv = findViewById(R.id.unRead_iv);
//        hasRead_iv = findViewById(R.id.hasRead_iv);

        unReadView = inflater.inflate(R.layout.add_listview_for_viewpager, null);
        hasReadView = inflater.inflate(R.layout.add_listview_for_viewpager, null);
        views.add(unReadView);
        views.add(hasReadView);

        topUnReadBg = findViewById(R.id.unread_bg);
        topHasReadBg = findViewById(R.id.hasRead_bg);
        empty_view1 = unReadView.findViewById(R.id.empty_view_un);
        tv1 = (TextView) unReadView.findViewById(R.id.empty_tv01);
        tv2 = (TextView) unReadView.findViewById(R.id.empty_tv02);
        empty_view2 = hasReadView.findViewById(R.id.empty_view_un);
        tv3 = (TextView) hasReadView.findViewById(R.id.empty_tv01);
        tv4 = (TextView) hasReadView.findViewById(R.id.empty_tv02);

        pagerAdapter = new LaunchPagerAdapter(this, views);

        viewPager = (CustomViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setPagingEnabled(false); //设置ViewPager的左右滑动

        unReedListView = (YYListView) unReadView.findViewById(R.id.add_listView);
        hasReadListView = (YYListView) hasReadView.findViewById(R.id.add_listView);
        unReedListView.setPullLoadEnable(false, true);
        hasReadListView.setPullLoadEnable(false, true);
        unReedListView.setPullRefreshEnable(false);
        hasReadListView.setPullRefreshEnable(false);

        unReadDatas = new ArrayList<OrganizationInfo>();
        hasReadDatas = new ArrayList<OrganizationInfo>();
    }

    /**
     * 已读，未读切换改变 （一开始是未读）
     *
     * @param position
     */
    private void changeTab(int position) {
        if (position == 0) {

            unRead_tv.setTextColor(resources.getColor(R.color.white));
            hasRead_tv.setTextColor(Color.parseColor("#00a3ff"));

            topUnReadBg.setBackgroundResource(R.drawable.sharp_blue_rect_solid);
            topHasReadBg.setBackgroundColor(Color.TRANSPARENT);
            if (unReadDatas!=null &&unReadDatas.size()>0){
                unReedListView.setVisibility(View.VISIBLE);
                empty_view1.setVisibility(View.GONE);
            }else{
                unReedListView.setVisibility(View.GONE);
                tv1.setText("您还没有创建任何组织");
                tv2.setText("赶紧去创建组织吧");
                empty_view1.setVisibility(View.VISIBLE);
            }

        } else {
            hasRead_tv.setTextColor(resources.getColor(R.color.white));
            unRead_tv.setTextColor(Color.parseColor("#00a3ff"));

            topHasReadBg.setBackgroundResource(R.drawable.sharp_blue_rect_solid);
            topUnReadBg.setBackgroundColor(Color.TRANSPARENT);
            if (hasReadDatas!=null &&hasReadDatas.size()>0){
                hasReadListView.setVisibility(View.VISIBLE);
                empty_view2.setVisibility(View.GONE);
            }else{
                hasReadListView.setVisibility(View.GONE);
                tv3.setText("您还没有加入任何组织");
                tv4.setText("赶紧去找个组织加入吧");
                empty_view2.setVisibility(View.VISIBLE);
            }
        }

    }

    /**
     * 设置适配器
     */
    private void setAdapters() {
        unReadAdapter = new YYBaseListAdapter<OrganizationInfo>(this) {

            @Override
            public View createView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(DPMListNewActivity.this).inflate(R.layout.item_org_list_default, null);
                    saveView("item_title_View", R.id.item_title_View, convertView);
                    saveView("item_org_iv", R.id.item_org_iv, convertView);
                    saveView("item_org_name_tv", R.id.item_org_name_tv, convertView);
                    saveView("item_org_status_tv", R.id.item_org_status_tv, convertView);
                    saveView("item_org_point_view", R.id.org_point_view, convertView);

                }

                OrganizationInfo info = getItem(position);
                if (info != null) {
                    View titleView = getViewForName("item_title_View", convertView);
                    titleView.setVisibility(View.GONE);
                    SimpleDraweeView headView = getViewForName("item_org_iv", convertView);
                    TextView nameTextView = getViewForName("item_org_name_tv", convertView);
                    TextView statusTextView = getViewForName("item_org_status_tv", convertView);
                    View pointView = getViewForName("item_org_point_view", convertView);
//                    titleView.setVisibility(info.isShowTitle() ? View.VISIBLE : View.GONE);
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
        hasReadAdapter = new YYBaseListAdapter<OrganizationInfo>(this) {

            @Override
            public View createView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(DPMListNewActivity.this).inflate(R.layout.item_org_list_default, null);
                    saveView("item_title_View", R.id.item_title_View, convertView);
                    saveView("item_org_iv", R.id.item_org_iv, convertView);
                    saveView("item_org_name_tv", R.id.item_org_name_tv, convertView);
                    saveView("item_org_status_tv", R.id.item_org_status_tv, convertView);
                    saveView("item_org_point_view", R.id.org_point_view, convertView);
                }

                OrganizationInfo info = getItem(position);
                if (info != null) {
                    View titleView = getViewForName("item_title_View", convertView);
                    titleView.setVisibility(View.GONE);
                    SimpleDraweeView headView = getViewForName("item_org_iv", convertView);
                    TextView nameTextView = getViewForName("item_org_name_tv", convertView);
                    TextView statusTextView = getViewForName("item_org_status_tv", convertView);
                    View pointView = getViewForName("item_org_point_view", convertView);
//                    titleView.setVisibility(info.isShowTitle() ? View.VISIBLE : View.GONE);
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

        unReedListView.setAdapter(unReadAdapter);
        hasReadListView.setAdapter(hasReadAdapter);

    }

    /**
     * 设置侧滑
     */
    private void setSwipeMenu() {
        SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem openItem = new SwipeMenuItem(DPMListNewActivity.this);
                openItem.setBackground(R.drawable.menu_delete_bg);
                openItem.setWidth(YYUtils.dp2px(90, DPMListNewActivity.this));
                openItem.setTitle("移除");
                openItem.setTitleSize(16);
                openItem.setTitleColor(0xfffcfcfc);
                menu.addMenuItem(openItem);
            }
        };
//        unReedListView.setMenuCreator(swipeMenuCreator);
//        hasReadListView.setMenuCreator(swipeMenuCreator);
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
        unReadDatas.clear();
        hasReadDatas.clear();
//        List<OrganizationInfo> passInfos = new ArrayList<OrganizationInfo>();
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
//                passInfos.add(info);
                if (info.getIsadmin() == 1){
                    unReadDatas.add(info);
                }else{
                    hasReadDatas.add(info);
                }
            }
        }
        unReadDatas.addAll(proInfos);
        unReadAdapter.setDatas(unReadDatas);
        hasReadAdapter.setDatas(hasReadDatas);
        unReadAdapter.notifyDataSetChanged();
        hasReadAdapter.notifyDataSetChanged();
        changeTab(0);
        viewPager.setCurrentItem(0);
//        datas.clear();
//        datas.addAll(passInfos);
    }

    private void getDatas() {
        showWaitDialog();
        OrganizationController.getMyOrganizationList(this, new Listener<Void, List<OrganizationInfo>>() {
            @Override
            public void onCallBack(Void status, List<OrganizationInfo> reply) {
                dismissWaitDialog();
                if (reply == null || reply.size() < 1) {

                }else{

                }
                addStatusDatas(reply); //这个方法中datas有清楚数据
//                mAdapter.setDatas(datas);
//                if(datas.size()>0){
//                    empty_view.setVisibility(View.GONE);
//                }else{
//                    tv1.setText("您还没有加入任何组织");
//                    tv2.setText("赶紧去找个组织加入吧");
//                    empty_view.setVisibility(View.VISIBLE);
//                }
//                refreshStatus = true;
            }
        });
    }

    /**
     * -1是获取全部，0是未读，1是已读
     *
     * @param type addDatasOgrNoticeDefaultActivity
     */
//    private void addDatas(final int type) {
//        if (type == -1) {
//            addDatas(0); //先获取未读的 （有点递归类似的调用）
//            addDatas(1); //在获取已读的
//            return;
//        }
//        AnnounceController.getAnnounceList(this, "0", "1000", org_id, "1", String.valueOf(type), new Listener<Void, List<NoticeInfo>>() {
//
//            @Override
//            public void onCallBack(Void status, List<NoticeInfo> reply) {
//                unReedListView.stopRefresh();   //列表停止刷新
//                hasReadListView.stopRefresh();
//                unReadDatas.clear();
//                hasReadDatas.clear();
//
//                if (type == 0) {
//                    if (reply != null && reply.size() > 0) {
//                        unReadDatas.addAll(reply);
//                    }
//
//                    unReadAdapter.setDatas(unReadDatas);
//
//                } else {
//                    if (reply != null && reply.size() > 0) {
//                        hasReadDatas.addAll(reply);
//                    }
//
//                    hasReadAdapter.setDatas(hasReadDatas);
//
//                }
//            }
//        });
//
//    }

    /**
     * 给View设置监听器
     */
    private void addListener() {
        topUnReadBg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
//                addDatas(0);
                changeTab(0);
                viewPager.setCurrentItem(0);
            }
        });
        topHasReadBg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                addDatas(1);
                changeTab(1);
                viewPager.setCurrentItem(1);

            }
        });

        unReedListView.setXListViewListener(new IXListViewListener() {

            @Override
            public void onRefresh() {
//                addDatas(0); //刷新未读的

            }

            @Override
            public void onLoadMore() {
                // TODO Auto-generated method stub

            }
        });
        hasReadListView.setXListViewListener(new IXListViewListener() {

            @Override
            public void onRefresh() {
//                addDatas(1); //刷新已读的

            }

            @Override
            public void onLoadMore() {
                // TODO Auto-generated method stub

            }
        });

        unReedListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                OrganizationInfo info = (OrganizationInfo) unReadAdapter.getItem(position - 1);
                if (info == null) {
                    return;
                }
                Intent intent = new Intent();
                if (info.getOrg_status() == 10) {  //通过审核
                    intent.setClass(DPMListNewActivity.this, DPMListActivity.class);
                    intent.putExtra(DPMListActivity.kORG_ID, info.getOrg_id());
                    intent.putExtra(DPMListActivity.kORG_NAME, info.getOrg_name());
                    startActivityForResult(intent,111);
                }else{// if (info.getOrg_status() == 0 || info.getOrg_status() == 12) {   // 0 等待审核 ， 12 拒绝
                    intent.setClass(DPMListNewActivity.this, CreateOranizationActivity.class);
                    intent.putExtra(CreateOranizationActivity.ORG_ID, info.getOrg_id());
                    intent.putExtra(CreateOranizationActivity.OPEN_MODE, CreateOranizationActivity.AUDIT_MODE);
                    startActivityForResult(intent,111);
                }


            }
        });

        hasReadListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OrganizationInfo info = (OrganizationInfo) hasReadAdapter.getItem(position - 1);
                if (info == null) {
                    return;
                }
                Intent intent = new Intent();
                intent.setClass(DPMListNewActivity.this, DPMListActivity.class);
                intent.putExtra(DPMListActivity.kORG_ID, info.getOrg_id());
                intent.putExtra(DPMListActivity.kORG_NAME, info.getOrg_name());
                startActivityForResult(intent,111);
            }
        });

        unReedListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(int position, SwipeMenu menu, int index) {

            }
        });
        hasReadListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(int position, SwipeMenu menu, int index) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 111 && resultCode == RESULT_OK){
            getDatas();
        }
    }
}





