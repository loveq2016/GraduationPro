package app.logic.activity.live;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import org.ql.activity.customtitle.ActActivity;
import org.ql.utils.QLToastUtils;
import org.ql.views.listview.QLXListView;
import org.taptwo.android.widget.CircleFlowIndicator;
import org.taptwo.android.widget.ViewFlow;

import java.util.ArrayList;
import java.util.List;
import app.logic.activity.ActTitleHandler;
import app.logic.activity.TYBaseActivity;
import app.logic.activity.org.ApplyToJoinActivity;
import app.logic.activity.org.CreateOranizationActivity;
import app.logic.adapter.LaunchPagerAdapter;
import app.logic.adapter.YYBaseListAdapter;
import app.logic.controller.OrganizationController;
import app.logic.live.view.MainBannerAdapter;
import app.logic.pojo.OrganizationInfo;
import app.utils.common.Listener;
import app.view.CustomViewPager;
import app.view.DialogNewStyleController;
import app.view.YYListView;
import app.yy.geju.R;

/**
 * Created by Administrator on 2017/2/14 0014.
 */

public class LiveListActivty extends ActActivity implements View.OnClickListener , QLXListView.IXListViewListener, AdapterView.OnItemClickListener {
    private ActTitleHandler titleHandler;
    private DialogNewStyleController anchorOrgListDialog ;
    private YYListView anchorOrgList ,liveorglist ;
    private View contentView        //对话框使用的布局
                 , notOrgCanLiveLl  //没有组织可开启直播时，显示提示信息
                 , creatOrgLl       //创建组织一行的ll view
                 , airView          //没有组织正在直播
                 , liveImg;         //直播按钮（弹出对话框）

    private ItmeGridViewAdpter liveAdpater ;
    private ArrayList<IsOnLiveOrgInfo> datas = new ArrayList<>();
    private ArrayList<IsOnLiveOrgInfo> allDatas = new ArrayList<>();
    private ArrayList<OrganizationInfo> userOrgDatas = new ArrayList<>();
    private int NUMBERPAGES = 20 ; //一次请求的项数
    private int reqNumber = 0  , startPag = 0 ;          //第几次请求
    private boolean isRefresh , isLoadMore ; //刷新和加载更多的标志

    //组织创建人选择组织开启直播listView的适配器
    private YYBaseListAdapter<OrganizationInfo> userOrgListAdapter =  new YYBaseListAdapter<OrganizationInfo>(this){
        @Override
        public View createView( int position , View convertView , ViewGroup parent ) {
            if( convertView == null ){
                convertView = LayoutInflater.from(getApplication()).inflate(R.layout.list_live_itme_dialog, null );
                saveView("org_name", R.id.org_nmae_itme, convertView);
            }
            TextView timeView = getViewForName("org_name", convertView);
            OrganizationInfo info = getItem(position);
            if( info != null ){
                timeView.setText(info.getOrg_name());
            }
            return convertView;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titleHandler = new ActTitleHandler();
        setAbsHandler(titleHandler);
        //setContentView( R.layout.activity_live_list);
        setContentView( R.layout.fragment_live_list);
        findViewById() ;
        initActHandler();
        intiDialog();
        //初始化轮播图
        intiViewFlow();
        //初始化轮播图
        //intihanderViewData();
        //正在直播列表设置适配器
        liveorglist.setAdapter( liveAdpater );
        //获取当前用户所创建的组织列表（异步）
        getUserCreatOrgList() ;
        //获取所有直播组织列表(异步)
        getAllLiveList();
    }

    /**
     * 初始化View
     */
    private void findViewById(){
        liveorglist = (YYListView) findViewById( R.id.liveorglist);
        liveImg = findViewById( R.id.live_ll);
        airView = findViewById( R.id.air_ll);
        liveImg.setOnClickListener(this);
        liveorglist.setPullLoadEnable(true,true);//liveorglist.setPullLoadEnable(false,true);
        liveorglist.setPullRefreshEnable(true);
        liveorglist.setXListViewListener( this );
        liveAdpater = new ItmeGridViewAdpter(this);
        liveAdpater.setItemClick(listener);
        //liveorglist.setVisibility(View.GONE);
        liveorglist.getmFooterView().hide();
    }

    private ItmeGridViewAdpter.ItemClickListener listener = new ItmeGridViewAdpter.ItemClickListener() {
        @Override
        public void itemClick(final IsOnLiveOrgInfo isOnLiveOrgInfo) {
            if (isOnLiveOrgInfo == null)
                return;
            if (isOnLiveOrgInfo.getStatus()==1){
                initStatusDialog(1,isOnLiveOrgInfo.getOrg_id());
                return;
            }

            showWaitDialog();
            OrganizationController.getIsOrgMember(LiveListActivty.this, isOnLiveOrgInfo.getOrg_id(), new Listener<Boolean, Void>() {
                @Override
                public void onCallBack(Boolean aBoolean, Void reply) {
                    dismissWaitDialog();
                    if(aBoolean){
                        Intent intent = new Intent();
                        intent.putExtra(LiveDetailsActivity.PLUG,isOnLiveOrgInfo.getPlug_id()); //直播Id
                        intent.putExtra(LiveDetailsActivity.ROOM_ID , isOnLiveOrgInfo.getRoom_id() );
                        intent.putExtra(LiveDetailsActivity.ORG_ID , isOnLiveOrgInfo.getOrg_id() );
                        intent.putExtra(LiveDetailsActivity.ORG_NAME , isOnLiveOrgInfo.getOrg_name() );
                        intent.putExtra(LiveDetailsActivity.ORG_LOG_URL , isOnLiveOrgInfo.getOrg_logo_url() );
                        intent.putExtra(LiveDetailsActivity.ORG_BUIDER_NAME , isOnLiveOrgInfo.getOrg_builder_name());
                        intent.setClass( LiveListActivty.this , LiveDetailsActivity.class);
                        startActivity( intent );
                    }else{
                        initStatusDialog(2,isOnLiveOrgInfo.getOrg_id());
                    }
                }
            });
            anchorOrgListDialog.dismiss();
        }
    };

    /**
     * 初始化Toobar
     */
    private void initActHandler() {
        setTitle("");
        titleHandler.replaseLeftLayout(this , true);
        ((TextView) titleHandler.getLeftLayout().findViewById(R.id.left_tv)).setText("直播");
        titleHandler.getLeftLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 初始化对话框
     */
    private void intiDialog(){
        contentView = LayoutInflater.from(this).inflate(R.layout.dialog_anchor_org_list, null);
        TextView titer = (TextView) contentView.findViewById( R.id.dialog_titer);
        titer.setText("选择组织开启直播");
        anchorOrgList = (YYListView) contentView.findViewById( R.id.listview);
        notOrgCanLiveLl = contentView.findViewById(R.id.notorgcanlive_ll);
        creatOrgLl = contentView.findViewById( R.id.creat_org_ll);
        creatOrgLl.setOnClickListener( this );
        anchorOrgListDialog = new DialogNewStyleController( this , contentView ) ;
        anchorOrgList.setPullRefreshEnable(false);
        anchorOrgList.setPullLoadEnable(false,false);
        //anchorOrgList.setXListViewListener( this );
        anchorOrgList.setAdapter( userOrgListAdapter );
        anchorOrgList.setOnItemClickListener( this );
    }

    /**
     *
     * @param status 1 休息中 2 加入组织
     */
    private void initStatusDialog(int status,final String org_id){
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_live_status, null);
        final DialogNewStyleController anchorOrgListDialog = new DialogNewStyleController( this , contentView ) ;
        TextView dateOff = (TextView) contentView.findViewById(R.id.dialog_dateoff);
        View viewAdd = contentView.findViewById(R.id.dialog_to);
        View btnAdd = contentView.findViewById(R.id.add_org_ll);
        ImageView dialogIcon = (ImageView) contentView.findViewById(R.id.dialog_icon);
        TextView dialogText = (TextView) contentView.findViewById(R.id.dialog_text);
        if (status == 1){
            dateOff.setVisibility(View.VISIBLE);
            viewAdd.setVisibility(View.GONE);
            dialogIcon.setImageResource(R.drawable.icon_see);
            dialogText.setText("查看组织详情");
        }else if(status == 2){
            dateOff.setVisibility(View.GONE);
            viewAdd.setVisibility(View.VISIBLE);
            dialogIcon.setImageResource(R.drawable.icon_add);
            dialogText.setText("加入组织");

        }
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LiveListActivty.this, ApplyToJoinActivity.class);
                intent.putExtra(ApplyToJoinActivity.ORG_ID, org_id);
                startActivity(intent);
                anchorOrgListDialog.dismiss();
            }
        });
        anchorOrgListDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //getIsOnLiveList();

        //开始图片轮播
//        startRotation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //停止图片轮播(不能放在onDestroy（）里面否则多次启动会发生混乱)
//        deletRotation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    /**
     * 获取所有直播列表
     */
    private void getAllLiveList(){
        showWaitDialog();
        if( isLoadMore ){
            startPag = datas.size();
        }else{
            startPag = 0;
        }
        OrganizationController.getIsOnLiveAllList(this, startPag , NUMBERPAGES  , new Listener<Integer, ArrayList<IsOnLiveOrgInfo>>() {
            @Override
            public void onCallBack(Integer integer, ArrayList<IsOnLiveOrgInfo> reply) {
                dismissWaitDialog();
                liveorglist.stopRefresh();
                liveorglist.stopLoadMore();
                if( reply!= null && reply.size()>0) {
//                    datas = reply;
                    if ( !isLoadMore ) { //第一次加载或刷新状态下
                        datas.clear();
                    }
                    datas.addAll(reply);
                    liveAdpater.setDatas( datas );

                    if (reply.size() == NUMBERPAGES)
                        liveorglist.setPullLoadEnable(true);
                    else
                        liveorglist.setPullLoadEnable(false,true);
                }else{
                    if( integer < 0){
                        QLToastUtils.showToast( LiveListActivty.this , "数据加载失败，请重试");
                    }else{
                        QLToastUtils.showToast( LiveListActivty.this , "没有更多数据");
                    }

                    if( reply!= null && reply.size()==0) {
                        liveorglist.setPullLoadEnable(false,true);
                    }
                }
                isRefresh = false ;   //刷新结束
                isLoadMore = false ;  //加载更多结束
            }
        });
    }

    /**
     * 获取当前用户所创建的组织列表
     */
    private void getUserCreatOrgList(){
        OrganizationController.getUserCreatOrgList(this, new Listener<Integer, ArrayList<OrganizationInfo>>() {
            @Override
            public void onCallBack(Integer integer, ArrayList<OrganizationInfo> reply) {
                if( reply!= null && reply.size()>0 ){
                    userOrgDatas = reply ;
                    userOrgListAdapter.setDatas( userOrgDatas );
                }
                if(userOrgDatas.size()>0) {
                    notOrgCanLiveLl.setVisibility(View.GONE);
                }else{
                    notOrgCanLiveLl.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId() ;
        if( id == R.id.live_ll){
            anchorOrgListDialog.show();
        }else if(id == R.id.creat_org_ll){
            Intent intent = new Intent();
            intent.setClass( this , CreateOranizationActivity.class );//创建组织
            startActivity( intent );
            anchorOrgListDialog.dismiss();
        }
    }

    /**
     * 下拉刷新
     */
    @Override
    public void onRefresh() {
        if(!isLoadMore){      //刷新时不在加载更多的状态下才能（访问网络）刷新，否则等加载更多结束
            isRefresh = true ;
//            reqNumber = 0 ; //只要一刷新请求次数就从头开始
            getAllLiveList();
        }
    }
    /**
     * 上拉加载更多
     */
    @Override
    public void onLoadMore() {
        if( !isRefresh ){   //加载更多时不在刷新状态下才能（访问网络）加载更多，否则等刷新结束
            isLoadMore = true ;
            reqNumber ++ ;  //
            getAllLiveList();
            System.out.println("正在加载更多");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        anchorOrgListDialog.dismiss();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //跳转到直播界面
        OrganizationInfo orgInfo = (OrganizationInfo) parent.getItemAtPosition( position );
        if( null == orgInfo ){
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(StartLiveActivity.ORG_ID , orgInfo.getOrg_id());
        intent.putExtra(StartLiveActivity.ORG_NAME, orgInfo.getOrg_name());
        intent.putExtra(StartLiveActivity.ORG_BUIDER_NAME ,orgInfo.getNickName());
        intent.putExtra(StartLiveActivity.ORG_LOGO_URL ,orgInfo.getOrg_logo_url());
        intent.putExtra(PrepareStartLiveActivity.LIVE_ID ,orgInfo.getLive_id());
        intent.setClass( this , PrepareStartLiveActivity.class );
        startActivity( intent );
        anchorOrgListDialog.dismiss();
    }

    //************ 头 **************//
    private CustomViewPager viewPager ;
    private LayoutInflater inflater;
    private View view01 ,view02,view03;
    private ImageView pointView01, pointView02, pointView03;
    private Resources resources ;
    private List<View> views = new ArrayList<View>();
    private List<ImageView> points = new ArrayList<ImageView>();
    private LaunchPagerAdapter pagerAdapter ;
    private int index = 0  ;
    private final  int ROTATION = 1001;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            index++ ;
            if( index > 2){
                index = 0;
            }
            viewPager.setCurrentItem(index);
            //changePoint(index);
            handler.sendEmptyMessageDelayed(ROTATION , 3000);
        }
    };

    private void intihanderViewData(){

        viewPager = (CustomViewPager) findViewById( R.id.viewpager);
        inflater = LayoutInflater.from(this);
        resources = getResources();

        view01 = inflater.inflate(R.layout.layout_livelist_hand, null);
        view02 = inflater.inflate(R.layout.layout_livelist_hand, null);
        view03 = inflater.inflate(R.layout.layout_livelist_hand, null);

        view01.findViewById(R.id.launch_iv).setBackgroundDrawable(resources.getDrawable(R.drawable.banner1));
        view02.findViewById(R.id.launch_iv).setBackgroundDrawable(resources.getDrawable(R.drawable.banner2));
        view03.findViewById(R.id.launch_iv).setBackgroundDrawable(resources.getDrawable(R.drawable.banner3));

        pointView01 = (ImageView) findViewById(R.id.point_01);
        pointView02 = (ImageView) findViewById(R.id.point_02);
        pointView03 = (ImageView) findViewById(R.id.point_03);

        views.add(view01);
        views.add(view02);
        views.add(view03);
        points.add(pointView01);
        points.add(pointView02);
        points.add(pointView03);

        pagerAdapter = new LaunchPagerAdapter(this, views);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(0);
        viewPager.setPagingEnabled(true);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                changePoint(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        changePoint(0);
    }

    /**
     * 点的切换
     * @param position
     */
    private void changePoint(int position) {
        for (int i = 0; i < points.size(); i++) {
            if (position == i) {
                points.get(i).setImageResource(R.drawable.point_blue);
            } else {
                points.get(i).setImageResource(R.drawable.point_gray);
            }
        }
    }

    /**
     * 开始轮播
     */
    private void startRotation(){
        if(handler!=null){
            handler.sendEmptyMessageAtTime(ROTATION ,3000);
        }
    }

    /**
     * 停止轮播
     */
    private void deletRotation(){
        if( handler!=null){
            handler.removeMessages( ROTATION );
        }
    }

    private ViewFlow viewFlow ;
    private CircleFlowIndicator circleFlowIndicator ;
    private MainBannerAdapter mainBannerAdapter ;
    /**
     * 初始化视数据 （ new ）
     */
    private void intiViewFlow(){
        View view = View.inflate(this,R.layout.activity_live_viewflow,null);
        viewFlow = (ViewFlow) view.findViewById( R.id.viewflow);
        viewFlow.setmListview(liveorglist);
        circleFlowIndicator = (CircleFlowIndicator) view.findViewById( R.id.viewflowindic);
        //circleFlowIndicator.setVisibility(View.GONE);
        mainBannerAdapter = new MainBannerAdapter(this);
        liveorglist.addHeaderView(view);
        getCarouselImg(this );
    }

    /**
     * 获取轮播的三张图片
     * @param context
     */
    public void getCarouselImg( Context context ){
        OrganizationController.getCarouselImg(context, new Listener<Boolean, ArrayList<CarouselImgInfo>>() {
            @Override
            public void onCallBack(Boolean aBoolean, ArrayList<CarouselImgInfo> reply) {
                if(aBoolean){
                    if(null!=reply && reply.size()>0){
                        viewFlow.setAdapter(mainBannerAdapter);
                        mainBannerAdapter.setData(reply);
                        viewFlow.setFlowIndicator(circleFlowIndicator);
                        viewFlow.setmSideBuffer(reply.size());
                        viewFlow.setTimeSpan(4000);
                        viewFlow.setSelection(0);
                        viewFlow.stopAutoFlowTimer();
                        viewFlow.startAutoFlowTimer();
                        circleFlowIndicator.setVisibility(View.GONE);
                        circleFlowIndicator.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }
}
