package app.logic.activity.main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import org.ql.utils.QLToastUtils;
import org.ql.views.listview.QLXListView;
import org.taptwo.android.widget.CircleFlowIndicator;
import org.taptwo.android.widget.ViewFlow;

import java.util.ArrayList;

import app.config.http.HttpConfig;
import app.logic.activity.ActTitleHandler;
import app.logic.activity.TYBaseActivity;
import app.logic.activity.live.CarouselImgInfo;
import app.logic.activity.live.IsOnLiveOrgInfo;
import app.logic.activity.live.ItmeGridViewAdpter;
import app.logic.activity.live.LiveDetailsActivity;
import app.logic.activity.live.PrepareStartLiveActivity;
import app.logic.activity.live.StartLiveActivity;
import app.logic.activity.org.ApplyToJoinActivity;
import app.logic.activity.org.CreateOranizationActivity;
import app.logic.adapter.YYBaseListAdapter;
import app.logic.controller.OrganizationController;
import app.logic.live.view.MainBannerAdapter;
import app.logic.pojo.OrganizationInfo;
import app.utils.common.Listener;
import app.view.DialogNewStyleController;
import app.view.YYListView;
import app.yy.geju.R;

/**
 * Created by Administrator on 2017/2/14 0014.
 */

public class LiveListFragment extends Fragment implements View.OnClickListener , QLXListView.IXListViewListener, AdapterView.OnItemClickListener {
    private ActTitleHandler titleHandler;
    private DialogNewStyleController anchorOrgListDialog ;
    private YYListView anchorOrgList ,liveorglist ;
    private View contentView        //对话框使用的布局
                 , notOrgCanLiveLl  //没有组织可开启直播时，显示提示信息
                 , creatOrgLl       //创建组织一行的ll view
                 , airView          //没有组织正在直播
                 , liveImg;         //直播按钮（谭弹出对话框）

//    private LiveListAdapter liveAdpater = new LiveListAdapter( this );
    private ItmeGridViewAdpter liveAdpater ;
    private ArrayList<IsOnLiveOrgInfo> datas = new ArrayList<>();
    private ArrayList<OrganizationInfo> userOrgDatas = new ArrayList<>();

    private Context context;
    private View view;
    private MainBannerAdapter mainBannerAdapter ;

    //组织创建人选择组织开启直播listView的适配器
    private YYBaseListAdapter<OrganizationInfo> userOrgListAdapter =  new YYBaseListAdapter<OrganizationInfo>(getActivity()){
        @Override
        public View createView( int position , View convertView , ViewGroup parent ) {
            if( convertView == null ){
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.list_live_itme_dialog, null );
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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getActivity();
        if (view == null) {
            view = inflater.from(getActivity()).inflate(R.layout.fragment_live_list, null);
            findViewById(view);
//            inithanderViewData(view);
            initViewFlow();
            liveAdpater = new ItmeGridViewAdpter(context);
            liveAdpater.setItemClick(listener);
            intiDialog();
            initData();
        }

        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }

        return view;
    }


    private void initData(){
        //正在直播列表设置适配器
        liveorglist.setAdapter( liveAdpater );
        //获取正在直播组织列表(异步)
        //getAllLiveList();
        //获取当前用户所创建的组织列表（异步）
//        getUserCreatOrgList() ;
    }

    /**
     * 初始化View
     */
    private void findViewById(View view){
        liveorglist = (YYListView) view.findViewById( R.id.liveorglist);
        liveImg = view.findViewById( R.id.live_ll);
        airView = view.findViewById( R.id.air_ll);
        liveImg.setOnClickListener(this);
//        liveorglist.setPullLoadEnable(false ,true);
        liveorglist.setPullLoadEnable(true ,true);
        liveorglist.setPullRefreshEnable(true);
        liveorglist.setXListViewListener( this );
        liveorglist.getmFooterView().hide();  //一开始隐藏footView
    }

    private ItmeGridViewAdpter.ItemClickListener listener = new ItmeGridViewAdpter.ItemClickListener() {
        @Override
        public void itemClick(final IsOnLiveOrgInfo isOnLiveOrgInfo) {
            if (isOnLiveOrgInfo == null)
                return;
            if (isOnLiveOrgInfo.getStatus()==1){
                initStatusDialog(1,isOnLiveOrgInfo.getOrg_id() );
                return;
            }

            ((TYBaseActivity)context).showWaitDialog();
            OrganizationController.getIsOrgMember(context, isOnLiveOrgInfo.getOrg_id(), new Listener<Boolean, Void>() {
                @Override
                public void onCallBack(Boolean aBoolean, Void reply) {
                    ((TYBaseActivity)context).dismissWaitDialog();
                    if(aBoolean){
                        Intent intent = new Intent();
                        intent.putExtra(LiveDetailsActivity.PLUG,isOnLiveOrgInfo.getPlug_id()); //直播Id
                        intent.putExtra(LiveDetailsActivity.ROOM_ID , isOnLiveOrgInfo.getRoom_id() );
                        intent.putExtra(LiveDetailsActivity.ORG_ID , isOnLiveOrgInfo.getOrg_id() );
                        intent.putExtra(LiveDetailsActivity.ORG_NAME , isOnLiveOrgInfo.getOrg_name() );
                        intent.putExtra(LiveDetailsActivity.ORG_LOG_URL , isOnLiveOrgInfo.getOrg_logo_url() );
                        intent.putExtra(LiveDetailsActivity.ORG_BUIDER_NAME , isOnLiveOrgInfo.getOrg_builder_name());
                        intent.setClass( context , LiveDetailsActivity.class);
                        context.startActivity( intent );
                    }else{
                        initStatusDialog(2,isOnLiveOrgInfo.getOrg_id() );
                    }
                }
            });
            anchorOrgListDialog.dismiss();
        }
    };

    /**
     * 初始化对话框
     */
    private void intiDialog(){
        contentView = LayoutInflater.from(context).inflate(R.layout.dialog_anchor_org_list, null);
        TextView titer = (TextView) contentView.findViewById( R.id.dialog_titer);
        titer.setText("选择组织开启直播");
        anchorOrgList = (YYListView) contentView.findViewById( R.id.listview);
        notOrgCanLiveLl = contentView.findViewById(R.id.notorgcanlive_ll);
        creatOrgLl = contentView.findViewById( R.id.creat_org_ll);
        creatOrgLl.setOnClickListener( this );
        anchorOrgListDialog = new DialogNewStyleController( context , contentView ) ;
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
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_live_status, null);
        final DialogNewStyleController anchorOrgListDialog = new DialogNewStyleController( context , contentView ) ;
        TextView dateOff = (TextView) contentView.findViewById(R.id.dialog_dateoff);
        View viewAdd = contentView.findViewById(R.id.dialog_to);
        View btnAdd = contentView.findViewById(R.id.add_org_ll);
        ImageView dialogIcon = (ImageView) contentView.findViewById(R.id.dialog_icon);
        TextView dialogText = (TextView) contentView.findViewById(R.id.dialog_text);
        if (status == 1){
            dateOff.setVisibility(View.VISIBLE);
            dialogIcon.setImageResource(R.drawable.icon_see);
            dialogText.setText("查看组织详情");
            viewAdd.setVisibility(View.GONE);
        }else if(status == 2){
            dateOff.setVisibility(View.GONE);
            viewAdd.setVisibility(View.VISIBLE);
            dialogIcon.setImageResource(R.drawable.icon_add);
            dialogText.setText("加入组织");
        }

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ApplyToJoinActivity.class);
                intent.putExtra(ApplyToJoinActivity.ORG_ID, org_id);
                startActivity(intent);
                anchorOrgListDialog.dismiss();
            }
        });
        anchorOrgListDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        //获取正在直播组织列表(异步)
        getAllLiveList();
        getUserCreatOrgList();
    }

    /**
     * 获取正在直播列表
     */
    private void getIsOnLiveList(){
        ((TYBaseActivity)context).showWaitDialog();
        datas.clear();
        OrganizationController.getIsOnLiveAllList(context, 0 , 50  ,new Listener<Integer, ArrayList<IsOnLiveOrgInfo>>() {
            @Override
            public void onCallBack(Integer integer, ArrayList<IsOnLiveOrgInfo> reply) {
                ((TYBaseActivity)context).dismissWaitDialog();
                liveorglist.stopRefresh();
                liveorglist.stopLoadMore();
                if( reply!= null && reply.size()>0) {
                    datas = reply;
                }
                liveAdpater.setDatas( datas );
                if( datas.size() > 0 ){
                    airView.setVisibility( View.GONE );
                }else {
                    airView.setVisibility( View.VISIBLE );
                }
            }
        });
    }

    private boolean isRefresh , isLoadMore ; //刷新和加载更多的标志
    private int NUMBERPAGES = 20 ; //一次请求的项数
    private int reqNumber = 0  , startPag = 0 ;
    /**
     * 获取所有直播列表
     */
    private void getAllLiveList(){
        //((TYBaseActivity)context).showWaitDialog();
        if( isLoadMore ){
            startPag = datas.size();
        }else{
            startPag = 0;
        }
        OrganizationController.getIsOnLiveAllList(context, startPag , NUMBERPAGES  , new Listener<Integer, ArrayList<IsOnLiveOrgInfo>>() {
            @Override
            public void onCallBack(Integer integer, ArrayList<IsOnLiveOrgInfo> reply) {
                //((TYBaseActivity)context).dismissWaitDialog();
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
                        QLToastUtils.showToast( context  , "数据加载失败，请重试");
                    }else{
                        QLToastUtils.showToast( context , "没有更多数据");
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
        OrganizationController.getUserCreatOrgList(context, new Listener<Integer, ArrayList<OrganizationInfo>>() {
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
            if (anchorOrgListDialog !=null && !anchorOrgListDialog.isShowing())
                anchorOrgListDialog.show();

        }else if(id == R.id.creat_org_ll){
            Intent intent = new Intent();
            intent.setClass( context , CreateOranizationActivity.class );//创建组织
            startActivity( intent );
            if (anchorOrgListDialog !=null && anchorOrgListDialog.isShowing())
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
//        getIsOnLiveList();
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
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (anchorOrgListDialog !=null && anchorOrgListDialog.isShowing())
            anchorOrgListDialog.dismiss();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final OrganizationInfo orgInfo = (OrganizationInfo) parent.getItemAtPosition( position );
        if (orgInfo !=null){
            //跳转到直播界面
            Intent intent = new Intent();
            intent.putExtra(StartLiveActivity.ORG_ID , orgInfo.getOrg_id());
            intent.putExtra(StartLiveActivity.ORG_NAME, orgInfo.getOrg_name());
            intent.putExtra(StartLiveActivity.ORG_BUIDER_NAME ,orgInfo.getNickName());
            intent.putExtra(StartLiveActivity.ORG_LOGO_URL ,orgInfo.getOrg_logo_url());
            intent.putExtra(PrepareStartLiveActivity.LIVE_ID ,orgInfo.getLive_id());
            intent.setClass( context , PrepareStartLiveActivity.class );
            startActivity( intent );
        }

        if (anchorOrgListDialog !=null && anchorOrgListDialog.isShowing())
            anchorOrgListDialog.dismiss();
    }


    //************ 头 **************//
    private ViewFlow viewFlow ;
    private CircleFlowIndicator circleFlowIndicator ;

    /**
     * 初始化视数据 （ new ）
     */
    private void initViewFlow(){
        View view = View.inflate(context,R.layout.activity_live_viewflow,null);

        viewFlow = (ViewFlow) view.findViewById( R.id.viewflow);
        viewFlow.setmListview(liveorglist);
        circleFlowIndicator = (CircleFlowIndicator) view.findViewById( R.id.viewflowindic);
        mainBannerAdapter = new MainBannerAdapter(getContext());
//        viewFlow.setClickable(true);
//        viewFlow.setEnabled(true);
//        viewFlow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                CarouselImgInfo carouselImgInfo = mainBannerAdapter.getItem(position);
//                if( null == carouselImgInfo ){
//                    return;
//                }
//                Intent intent = new Intent();
//                intent.setAction("android.intent.action.VIEW");
//                Uri content_url = Uri.parse(HttpConfig.getUrl(carouselImgInfo.getAddress()));
//                intent.setData(content_url);
//                startActivity(intent);
//            }
//        });
        liveorglist.addHeaderView(view);
        //获取图片
        getCarouselImg(getContext());
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
