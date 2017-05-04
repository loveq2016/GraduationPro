package app.logic.activity.org;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.QLConstant;
import org.canson.view.swipemenulistview.SwipeMenu;
import org.canson.view.swipemenulistview.SwipeMenuCreator;
import org.canson.view.swipemenulistview.SwipeMenuItem;
import org.canson.view.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import org.ql.activity.customtitle.ActActivity;
import org.ql.app.alert.AlertDialog;
import org.ql.utils.QLToastUtils;
import org.ql.views.listview.QLXListView.IXListViewListener;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager.LayoutParams;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import app.config.http.HttpConfig;
import app.logic.activity.ActTitleHandler;
import app.logic.activity.friends.LogicFriends;
import app.logic.activity.main.HomeActivity;
import app.logic.activity.user.PreviewFriendsInfoActivity;
import app.logic.adapter.YYBaseListAdapter;
import app.logic.controller.OrganizationController;
import app.logic.controller.UserManagerController;
import app.logic.pojo.DepartmentInfo;
import app.logic.pojo.ExpansionInfo;
import app.logic.pojo.FriendInfo;
import app.logic.pojo.IntentInfo;
import app.logic.pojo.OrgRequestMemberInfo;
import app.logic.pojo.OrgUnreadNumberInfo;
import app.logic.pojo.OrganizationInfo;
import app.logic.singleton.YYSingleton;
import app.logic.singleton.ZSZSingleton;
import app.utils.common.FrescoImageShowThumb;
import app.utils.common.Listener;
import app.utils.helpers.ShareHelper;
import app.utils.helpers.YYUtils;
import app.view.DialogNewStyleController;
import app.view.YYListView;
import app.view.ZSZDialogView;
import app.view.ZSZDialogView.SuccessAddMenberToDPMListener;
import app.yy.geju.R;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * SiuJiYung create at 2016年7月4日 上午6:45:25
 */

public class DPMListActivity extends ActActivity implements IXListViewListener, OnItemClickListener, OnClickListener, OnTouchListener {

    private View contentView;
    private Button hAdminBtn, nAdminBtn;
    private DialogNewStyleController dialog;
    //private  DepartmentInfo noDppTger ;
    public static final String kORG_ID = "kORG_ID";
    public static final String kORG_NAME = "kORG_NAME";
    private static String org_name;  //组织名称
    private static String org_id;    //组织ID
    //private boolean isAdminStatus = false;   //自己是否是这个组织的管理员的标志
    private boolean refreshAlone = false;//刷新使用的标志量
    private boolean isBulider = false; //当前用户是不是这的组织的超级管理员的标志
    private ActTitleHandler handler = new ActTitleHandler();
    private YYListView mListView;
    private LayoutInflater inflater;
    private Resources resources;
    private PopupWindow popupWindow_more;
    private List<DepartmentInfo> setDPMList;    //组织下的部所有分组列表
    private List<DepartmentInfo> dataList = new ArrayList<DepartmentInfo>(); //列表的数据源
    private List<OrgRequestMemberInfo> allMemberList = new ArrayList<OrgRequestMemberInfo>(); //组织下的所有成员 集合

    private boolean isResult = true ;
    private OrganizationInfo orgInfo ;


    /**
     * 存储ImageView，是否显示
     */

    private HashMap<Integer, View> imgHashMap = new HashMap<>();
    private YYBaseListAdapter<DepartmentInfo> mAdapter = new YYBaseListAdapter<DepartmentInfo>(this) {
        @Override
        public View createView(final int position, View convertView, ViewGroup parent) {
            if (getItemViewType(position) == 0) {
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.view_dpm_item, null);
                    saveView("dpm_view_tv", R.id.dpm_view_tv, convertView);
                }
                DepartmentInfo info = getItem(position);
                if (info != null) {
                    setTextToViewText(info.getDpm_name(), "dpm_view_tv", convertView);
                }
            } else if (getItemViewType(position) == 2) {// 该组织的管理员
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.item_dpm_member_isadmin, null);
                    saveView("item_head_iv", R.id.item_head_iv, convertView);
                    saveView("item_name_tv", R.id.item_name_tv, convertView);
                    saveView("item_show_admin_iv", R.id.item_show_admin_iv, convertView);
                    saveView("item_title_view", R.id.item_title_view, convertView);
                }
                DepartmentInfo info = getItem(position);
                if (info != null) {
                    View titleView = getViewForName("item_title_view", convertView);
                    SimpleDraweeView headIv = getViewForName("item_head_iv", convertView);
                    TextView nameTv = getViewForName("item_name_tv", convertView);
                    ImageView statusIv = getViewForName("item_show_admin_iv", convertView);
//                    headIv.setImageURI(info.getOrgRequestMemberInfo().getPicture_url());
                    FrescoImageShowThumb.showThrumb(Uri.parse(HttpConfig.getUrl(info.getOrgRequestMemberInfo().getPicture_url())),headIv);
//                    Picasso.with(DPMListActivity.this).load(HttpConfig.getUrl(info.getOrgRequestMemberInfo().getPicture_url())).error(R.drawable.default_user_icon).fit().centerCrop().into(headIv);
                    if(info.getOrgRequestMemberInfo()!=null){
                        if(!TextUtils.isEmpty(info.getOrgRequestMemberInfo().getFriend_name())){
                            nameTv.setText(info.getOrgRequestMemberInfo().getFriend_name());
                        }else{
                            nameTv.setText(info.getOrgRequestMemberInfo().getNickName());
                        }
                    }
                    if (info.isBuilder()) {  //该组织的超级管理员
                        statusIv.setImageDrawable(resources.getDrawable(R.drawable.icon_super_admin));
                        titleView.setVisibility(View.VISIBLE);
                    } else {                //该组织的普通管理员
                        statusIv.setImageDrawable(resources.getDrawable(R.drawable.icon_admin));
                        titleView.setVisibility(View.GONE);
                    }
                }
            } else if (getItemViewType(position) == 3) {  //“多余”成员使用的View
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.item_department_titleview, null);
                    saveView("item_no_title_view", R.id.item_no_title_view, convertView);
                }
                DepartmentInfo info = getItem(position);
                if (info != null) {
                    TextView titleView = getViewForName("item_no_title_view", convertView);
                    titleView.setText("");
                }
            } else {// 不属于该组织任一分组的 成员 使用的布局
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.footview_dpmmenber_layout, null);
                    saveView("footView_name_tv", R.id.footView_name_tv, convertView);
                    saveView("footView_header_iv", R.id.footView_header_iv, convertView);
                    saveView("footView_edit_iv", R.id.footView_edit_iv, convertView);
                }
                final DepartmentInfo info = getItem(position);
                if (info != null) {
                    if(info.getOrgRequestMemberInfo()!=null){
                        if(!TextUtils.isEmpty(info.getOrgRequestMemberInfo().getFriend_name())){
                            setTextToViewText(info.getOrgRequestMemberInfo().getFriend_name(), "footView_name_tv", convertView);
                        }else{
                            setTextToViewText(info.getOrgRequestMemberInfo().getNickName(), "footView_name_tv", convertView);
                        }
                    }
                    //setTextToViewText(!TextUtils.isEmpty(info.getOrgRequestMemberInfo().getFriend_name())?info.getOrgRequestMemberInfo().getFriend_name():info.getOrgRequestMemberInfo().getNickName(), "footView_name_tv", convertView);
                    String url = HttpConfig.getUrl(info.getOrgRequestMemberInfo().getPicture_url());
                    SimpleDraweeView imageView = getViewForName("footView_header_iv", convertView);
                    setImageToImageViewCenterCrop(url, "footView_header_iv", R.drawable.default_user_icon, convertView);
                    final ImageView edit_img = getViewForName("footView_edit_iv", convertView);

                    if (isBulider) {   //当前用户不是这个组织的超级管理员 则隐藏编辑按钮
                        edit_img.setVisibility(View.VISIBLE);
                    } else {
                        edit_img.setVisibility(View.GONE);
                    }

                    edit_img.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mListView.smoothOpenMenu(position + 1);
                        }
                    });

                }
            }
            return convertView;
        }

        // 0：分组，1：没有分配的人员；2：管理员
        public int getItemViewType(int position) {
            DepartmentInfo dpmInfo = mAdapter.getItem(position);
            if (dpmInfo != null && dpmInfo.isShowTitleView()) {
                return 3;
            }
            if (dpmInfo != null && dpmInfo.getOrgRequestMemberInfo() == null) {
                return 0;
            }
            if (dpmInfo != null && dpmInfo.isNotDpmAndNotAdmin()) {
                return 1;
            }
            return (dpmInfo == null) ? 0 : 2;
        }

        //类型的数量
        public int getViewTypeCount() {
            return 4;
        }

        @Override
        public boolean menuEnable(int position) {
            if (getItemViewType(position) == 0 || getItemViewType(position) == 3) {
                return false;
            } else if (getItemViewType(position) == 2) {
                return false;
            } else if (getItemViewType(position) != 0 && getItemViewType(position) != 2 && isBulider) {//非管理员的成员，并且当前用户是超级管理员  可侧滑
                return true;
            } else {
                return false;
            }
        }
    };

    /**
     * @param key
     */
    private void changeShowImg(int position, int key) {
        Set<Integer> imgSet = imgHashMap.keySet();
        for (Integer integer : imgSet) {
            if (integer == key) {
                if (mListView.openMenuEnable(position)) {
                    imgHashMap.get(integer).setVisibility(View.INVISIBLE);
                    continue;
                }
            }
            imgHashMap.get(integer).setVisibility(View.VISIBLE);

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbsHandler(handler);
        setContentView(R.layout.activity_dpm_list);
        //初始化TootBar
        initActTitleBar();
        contentView = LayoutInflater.from(this).inflate(R.layout.dialog_new_admincontent_view, null);
        intiDialog(contentView);
        inflater = LayoutInflater.from(this);
        resources = getResources();
        //初始化ListView
        initListView();
        IntentFilter filter = new IntentFilter();
        filter.addAction(HomeActivity.UPDATA_ORG_POINT);
        registerReceiver(mBroadcastReceiver,filter);
    }

    /**
     * 初始化TootBar
     */
    private void initActTitleBar() {
        org_name = getIntent().getStringExtra(kORG_NAME); //获取组织名字
        org_id = getIntent().getStringExtra(kORG_ID);     //获取组织ID
        setTitle("");
        handler.addRightView(LayoutInflater.from(this).inflate(R.layout.homeactivity_rightlayout, null), true);
        handler.getRightLayout().setVisibility(View.VISIBLE);
        ImageButton imageButton = (ImageButton) handler.getRightLayout().findViewById(R.id.imageButton02);
        imageButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.popmenu_more));
        imageButton.setOnClickListener(this);
        handler.addLeftView(LayoutInflater.from(this).inflate(R.layout.title_leftview_layout, null), true);
        handler.getLeftLayout().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView) handler.getLeftLayout().findViewById(R.id.left_tv)).setText(org_name);  //设置左标题

    }

    /**
     * 初始化ListView
     */
    private void initListView() {
        mListView = (YYListView) findViewById(R.id.dpm_list_view);
        mListView.setPullRefreshEnable(true);
        mListView.setPullLoadEnable(false, true);
        mListView.setXListViewListener(this);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        SwipeMenuCreator menuCreator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem openItem = new SwipeMenuItem(DPMListActivity.this);
                openItem.setBackground(R.drawable.menu_delete_bg);
                openItem.setWidth(YYUtils.dp2px(80, DPMListActivity.this));
                openItem.setTitle("移除");
                openItem.setTitleSize(16);
                openItem.setTitleColor(0xfffcfcfc);
                SwipeMenuItem openItem2 = new SwipeMenuItem(DPMListActivity.this);
                openItem2.setBackground(R.drawable.menu_delete_blue_bg);
                openItem2.setWidth(YYUtils.dp2px(75, DPMListActivity.this));
                openItem2.setTitle("设置分组");
                openItem2.setTitleSize(16);
                openItem2.setTitleColor(0xfffcfcfc);
                menu.addMenuItem(openItem2);
                menu.addMenuItem(openItem);
            }
        };

        mListView.setMenuCreator(menuCreator);
        mListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(int position, SwipeMenu menu, int index) {
                DepartmentInfo info = dataList.get(position);
                if ( info == null || info.getOrgRequestMemberInfo() == null) {
                    return;
                }
                if (index == 0) {
                    showSetDPMList(DPMListActivity.this, setDPMList, info.getOrgRequestMemberInfo().getWp_member_info_id(), position);
                }
                if (index == 1) {
                    delCanmer(info, position);  //删除成员
                }
            }
        });
    }

    /**
     * 对话框初始化
     *
     * @param
     */
    private void intiDialog(View contentView) {
        TextView title = (TextView) contentView.findViewById(R.id.dialog_title_tv);
        hAdminBtn = (Button) contentView.findViewById(R.id.dialog_true_btn);
        nAdminBtn = (Button) contentView.findViewById(R.id.dialog_cancel_btn);
        dialog = new DialogNewStyleController(this, contentView);
        title.setText("确定要删除该成员吗？");
        //取消按钮
        nAdminBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 删除成员
     */
    private void delCanmer(final DepartmentInfo info, final int position) {
        dialog.show();
        hAdminBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (info != null && info.getOrgRequestMemberInfo() != null ) {
                    removeOrgMember(org_id, info.getOrgRequestMemberInfo().getWp_member_info_id());
                }
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        getOrgInfo(org_id); //获取组织信息
        if(isResult){ //上一次数据获取完成，不管是失败还是成功，才能下一次获取数据
            updataUI();        //获取组织中的分组列表
        }

        View titlePoint = handler.getRightLayout().findViewById(R.id.title_point);
        if (titlePoint !=null)
            titlePoint.setVisibility(View.GONE);
        if (orgPoint !=null)
            orgPoint.setVisibility(View.GONE);
        for (OrgUnreadNumberInfo numberInfo : YYSingleton.getInstance().getOrgUnreadDatas()){
            if (numberInfo.getOrg_id().equals(org_id) && titlePoint !=null) {
                titlePoint.setVisibility(View.VISIBLE);
                if (orgPoint !=null)
                    orgPoint.setVisibility(View.VISIBLE);
                break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBroadcastReceiver!=null)
            unregisterReceiver(mBroadcastReceiver);
    }


    /**
     * 根据组织ID 获取组织的分组列表）
     */
    private void updataUI() {
        refreshAlone = false;    //listView刷新标志
        dataList.clear();
        getDPM(org_id);          //获取组织中的分组列表
    }

    /**
     * @param org_id 组织ID
     *               getDPMDPMListActivity 获取分组列表
     */
    private void getDPM(final String org_id) {
        showWaitDialog();  //显示等待对话框
        OrganizationController.getMyDPMList(this, org_id, new Listener<Void, List<DepartmentInfo>>() {
            @Override
            public void onCallBack(Void status, List<DepartmentInfo> reply) {
                if (reply != null && reply.size() >= 1) {
                    setDPMList = reply;     //分组集合
                    dataList.addAll(reply); //列表集合
                }
                //获取组织的所有成员
                getAssociationMemberList(org_id);
            }
        });
    }

    /**
     * @param or_id 组织ID（根据组织ID获取组织所有成员）
     *              getAssociationMemberListDPMListActivity 获取组织所有成员
     */
    private void getAssociationMemberList(String or_id) {
        OrganizationController.getOrgMemberList(this, or_id, new Listener<Void, List<OrgRequestMemberInfo>>() {
            @Override
            public void onCallBack(Void status, List<OrgRequestMemberInfo> reply) {
                dismissWaitDialog();
                mListView.stopLoadMore();
                mListView.stopRefresh();
//                if (reply == null || reply.size() < 1) {
//                    refreshAlone = true;
//                    isResult = true ;
//                    return;
//                }
                allMemberList = reply ;
                //allMemberList.clear();
                //allMemberList.addAll(reply);   //组织成员集合
                myIsBuliser(allMemberList);      //判断当前用户是不是这个组织的创建者
                fillDatas(allMemberList);        //填充数据 添加到dataList集和中
                //imgHashMap.clear();
                mAdapter.setDatas(dataList);    //从新适配数据
                refreshAlone = true;            //可以刷新的标志，需要这个标志为，否则刷新太快，会引起显示错乱
                isResult = true ;               //标志当前这次数据请求完毕
            }
        });
    }

    /**
     * 判断当前用户是不是这个组织的超级管理员
     */
    private void myIsBuliser(List<OrgRequestMemberInfo> allMemberList) {
        if (allMemberList == null) {
            return;
        }
        for (OrgRequestMemberInfo list : allMemberList) {
            if (list != null) {
                //是这个组织的创建者对象
                if (list.isIsbuilder()) {
                    //判断这个对象是不是当前这个用户对象
                    if (list.getWp_member_info_id().equals(UserManagerController.getCurrUserInfo().getWp_member_info_id())) {
                        isBulider = true;
                        return;
                    } else {
                        isBulider = false;
                    }
                }
            }
        }
    }

    /**
     * new ----->分类填充数据
     *
     * @param list fillDatasDPMListActivity
     */

    private void fillDatas(List<OrgRequestMemberInfo> list) {
        if( list == null ){
            return ;
        }
        List<DepartmentInfo> noDpm = new ArrayList<DepartmentInfo>();    // 没有分组的成员的集合
        List<DepartmentInfo> adminDpm = new ArrayList<DepartmentInfo>(); // 管理员的集合
        DepartmentInfo isbulider = null;                                 //用来保存这个组织的创建者的对象
        noDpm.clear();
        adminDpm.clear();
        for (OrgRequestMemberInfo info : list) {
//            if (TextUtils.isEmpty(info.getPhone())) {
            if (TextUtils.isEmpty(info.getWp_member_info_id())) {
                continue;
            }
            //超级管理员 （先找超级管理员，因为一个组织是一定有创建者的）
            if (info.isIsbuilder()) {
                isbulider = new DepartmentInfo(info);
                String firdenNmae = info.getFriend_name();
                isbulider.setWp_member_info_id( info.getWp_member_info_id());
                isbulider.setShowTitle(true);
                isbulider.setBuilder(true); //该对象是超级管理员
                continue;
            }
            // 管理员    （再找分组管理员，分组管理员可以没有）
            if (info.isMember_isadmin()) {
                DepartmentInfo isadmin = new DepartmentInfo(info);
                isadmin.setAdmin(true);
                isadmin.setWp_member_info_id( info.getWp_member_info_id() );
                adminDpm.add(isadmin);  //该对象是普通管理员
                continue;
            }
            // 没有分组的成员 （最后找不属于任何分组的组织成员）
            if (TextUtils.isEmpty(info.getDepartmentId()) ) {
                DepartmentInfo dpmInfo = new DepartmentInfo(info);
                String firdenNmae = info.getFriend_name();
                dpmInfo.setNotDpmAndNotAdmin(true);
                dpmInfo.setWp_member_info_id(info.getWp_member_info_id());
                noDpm.add(dpmInfo);
                continue;
            }
        }

        if (noDpm.size() > 0) {
            DepartmentInfo info = new DepartmentInfo();
            info.setShowTitleView(true);
            dataList.add(info);     //添加一条(“多余”的数据 )
            dataList.addAll(noDpm); //添加不属于任何分组的 成员
        }
        if (isbulider != null) {    //一个组织只有一个超级管理员
            dataList.add(isbulider);
        }
        if (adminDpm.size() > 0) {  //添加普通管理员
            dataList.addAll(adminDpm);
        }
    }

    /**
     * 显示showPopupWindow
     *
     * @param v
     */
    private View orgPoint;
    private void showPopupWindow(View v) {
        if (popupWindow_more == null) {
            View view = LayoutInflater.from(this).inflate(R.layout.popmenu_more2, null);
            View orgInfo_ll = view.findViewById(R.id.org_info);
            View orgRequest_ll = view.findViewById(R.id.org_request_list);
            View orgAddMember_ll = view.findViewById(R.id.org_add_member);
            View orgAddDpm_ll = view.findViewById(R.id.org_add_dpm);
            View orgshera_ll = view.findViewById(R.id.org_shear);
            View orgMemList = view.findViewById(R.id.org_member_list);
            View orgFindMem = view.findViewById(R.id.org_find_member);
            TextView inviteText = (TextView)view.findViewById(R.id.invite_text);
            orgPoint = view.findViewById(R.id.org_point_view);
            //设置监听
            orgInfo_ll.setOnClickListener(this);
            orgRequest_ll.setOnClickListener(this);
            orgAddMember_ll.setOnClickListener(this);
            orgAddDpm_ll.setOnClickListener(this);
            orgshera_ll.setOnClickListener( this );
            orgMemList.setOnClickListener( this );
            orgFindMem.setOnClickListener( this );
            if (!isBulider) {  //当前用户不是个组织的创建者时，就隐藏下面三个字段
                orgRequest_ll.setVisibility(View.GONE);   //申请列表
//                orgAddMember_ll.setVisibility(View.GONE); //邀请成员
                orgAddDpm_ll.setVisibility(View.GONE);    //添加分组
                orgFindMem.setVisibility(View.GONE);
                inviteText.setText("推荐好友");
            }
            popupWindow_more = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            view.setOnTouchListener(this);
            popupWindow_more.setOutsideTouchable(true);
            popupWindow_more.setFocusable(true);
        }
        if (popupWindow_more.isShowing()) {
            return;
        }
        if(orgPoint !=null && handler.getRightLayout().findViewById(R.id.title_point).getVisibility() == View.VISIBLE){
            orgPoint.setVisibility(View.VISIBLE);
        }else if (orgPoint !=null)
            orgPoint.setVisibility(View.GONE);


        popupWindow_more.showAsDropDown(v, 0, (int) getResources().getDimension(R.dimen.dp_10));
        popupWindow_more.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                // TODO Auto-generated method stub
                ZSZSingleton.getZSZSingleton().backgroundAlpha(DPMListActivity.this, 1f);
            }
        });
        popupWindow_more.update();
        ZSZSingleton.getZSZSingleton().backgroundAlpha(this, 0.5f);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageButton02:
                // 点击右上角的按钮
                showPopupWindow(v);
                break;
            case R.id.org_shear: //分享协会
                dimssPopupWindow();
                if(null == orgInfo){
                    QLToastUtils.showToast(this , "组织信息获取失败");
                    return;
                }
//                showShare( orgInfo.getOrg_name() ,orgInfo.getOrg_des() );
                ShareHelper.showShare(this,"我的【格局】看到一个不错协会【"+ orgInfo.getOrg_name() +"】，向您推荐一下！",orgInfo.getOrg_des(),orgInfo.getOrg_logo_url(),HttpConfig.SHARE_ORG+orgInfo.getOrg_id());
                break;
            case R.id.org_info:
                //组织信息
                //getOrgInfo(org_id);
                dimssPopupWindow();
                if(null == orgInfo){
                    QLToastUtils.showToast(this , "组织信息获取失败");
                    return;
                }
                Gson gson = new Gson();
                String data = gson.toJson(orgInfo);
                Intent intent = new Intent();
                intent.putExtra(OrganizationDetailActivity2.ISBULIDER, isBulider);  //是否是创建者
                intent.putExtra(OrganizationDetailActivity2.SHOWVIEW, true);
                intent.putExtra(OrganizationDetailActivity2.kOrganizationInfoKey, data);
                intent.setClass(DPMListActivity.this, OrganizationDetailActivity2.class);
                startActivity(intent);
                break;
            case R.id.org_request_list:
                //申请列表
                Intent intent2 = new Intent();
                intent2.setClass(DPMListActivity.this, RequestFormListActivity.class);
                intent2.putExtra(RequestFormListActivity.GET_JOINREQUEST_KRY, org_id);
                startActivity(intent2);
                dimssPopupWindow();
                break;
            case R.id.org_add_member:
                //邀请成员
                openAddMember();
                dimssPopupWindow();
                break;
            case R.id.org_add_dpm:
                //添加分组
                addDpm();
                dimssPopupWindow();
                break;
            case R.id.org_member_list:
                dimssPopupWindow();
                Intent intent3 = new Intent(DPMListActivity.this, OrganizationAllMemberShow.class);
                intent3.putExtra(OrganizationAllMemberShow.ORG_ID, org_id);   //组织ID
                startActivity(intent3);
                break;
            case R.id.org_find_member:
                dimssPopupWindow();
                Intent intent4 = new Intent(DPMListActivity.this, OrgFindMemActivity.class);
                intent4.putExtra("ORG_ID", org_id);   //组织ID
                startActivity(intent4);
                break;
            default:
                break;
        }
    }

    /**
     * 移除分组
     *
     * @param position
     */
    private void removeDPM(final int position) {
        DepartmentInfo info = mAdapter.getItem(position);
        if (info != null) {
            OrganizationController.removeDPM(this, org_id, info.getDpm_id(), new Listener<Integer, String>() {
                @Override
                public void onCallBack(Integer status, String reply) {
                    dismissWaitDialog();
                    if (status != 1) {
                        String msg = reply == null ? "操作失败" : reply;
                        QLToastUtils.showToast(DPMListActivity.this, msg);
                    } else {
                        mAdapter.removeItemAt(position);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    /**
     * 获取组织信息
     *
     * @param org_id
     */
    private void getOrgInfo(String org_id) {
        OrganizationController.getOrganizationInfo(this, org_id, new Listener<Void, List<OrganizationInfo>>() {
            @Override
            public void onCallBack(Void status, List<OrganizationInfo> reply) {
                if (reply != null) {
                    orgInfo = reply.get(0);
                }
            }
        });
    }

    /**
     * 邀请成员
     * openAddMemberDPMListActivity
     */
    private void openAddMember() {
        UserManagerController.getFriendsList(this, new Listener<List<FriendInfo>, List<FriendInfo>>() {
            @Override
            public void onCallBack(List<FriendInfo> status, List<FriendInfo> reply) {
                if (reply == null || reply.size() < 1) {
                    return;
                }
                // 过滤已经存在的成员
                List<FriendInfo> lastFriendInfos = new ArrayList<FriendInfo>();
                for (FriendInfo friendInfo : reply) {
                    boolean temp = true;
                    for (OrgRequestMemberInfo memberInfo : allMemberList) {
                        if (friendInfo.getPhone().equals(memberInfo.getPhone())) {
                            temp = false;
                            continue;
                        }
                    }
                    if (temp && friendInfo.isRequest_accept()) {
                        lastFriendInfos.add(friendInfo);
                    }
                }
                if (lastFriendInfos.size() < 1) {
                    QLToastUtils.showToast(DPMListActivity.this, "暂没有联系人可以添加");
                    return;
                }
                // 装载数据
                List<ExpansionInfo> intentList = new ArrayList<ExpansionInfo>();
                for (FriendInfo info : lastFriendInfos) {
                    ExpansionInfo expansionInfo = new ExpansionInfo();
//                    String nameString = TextUtils.isEmpty(info.getFriend_name()) ? info.getNickName() : info.getFriend_name();
//                    expansionInfo.setItemName(nameString);
//                    expansionInfo.setItemID(info.getWp_friends_info_id());
//                    expansionInfo.setItemShowCheck(true);
//                    expansionInfo.setItemUrl(info.getPicture_url());
//                    expansionInfo.setItemPhone(info.getPhone());
//                    expansionInfo.setFriendInfo(info);
//                    String nameString = TextUtils.isEmpty(info.getFriend_name()) ? info.getNickName() : info.getFriend_name();
//                    expansionInfo.setItemName(nameString);
                    expansionInfo.setItemUrl(info.getPicture_url());
                    expansionInfo.setItemName(info.getName());
                    expansionInfo.setNickName(info.getNickName());
                    expansionInfo.setFriend_name(info.getFriend_name());
                    expansionInfo.setItemID(info.getWp_member_info_id());
                    expansionInfo.setItemPhone(info.getPhone());
                    expansionInfo.setWp_member_info_id(info.getWp_member_info_id());
                    expansionInfo.setFriendInfo(info);
                    expansionInfo.setItemShowCheck(true);
                    intentList.add(expansionInfo);
                }
                Gson gson = new Gson();
                String string = gson.toJson(intentList);
                Intent addMemberIntent = new Intent(DPMListActivity.this, LogicFriends.class);
                addMemberIntent.putExtra(LogicFriends.TITLE, "选择联系人");
                addMemberIntent.putExtra(LogicFriends.MODEL, LogicFriends.FRIENDS_INFO); // 0
                addMemberIntent.putExtra(LogicFriends.DATAS_LIST, string);
                startActivityForResult(addMemberIntent, LogicFriends.REQUEST_CODE);
            }
        });
    }

    /**
     * 添加分组
     * addDpmDPMListActivity
     */
    private void addDpm() {
        Intent intent = new Intent(this, DPMDetailsForEditActivity2.class);
        IntentInfo info = new IntentInfo();
        info.setTitle("添加分组");
        info.setOrgId(org_id);                   //協會ID
        info.setOpenMode(IntentInfo.ADD_MODE);  //新增模式
        info.setAdmin(true);                    //設為是管理員
        intent.putExtra(IntentInfo.INTENT_INFO, info);
        startActivity(intent);
    }

    /**
     * 关闭PopupWindow
     * dimssPopupWindowDPMListActivity
     */
    private void dimssPopupWindow() {
        ZSZSingleton.getZSZSingleton().backgroundAlpha(this, 1f);
        popupWindow_more.dismiss();
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        DepartmentInfo info = mAdapter.getItem(arg2 - 1);
        if (info == null ) {
            return;
        }
       if (info.isBuilder() || info.isNotDpmAndNotAdmin() ||info.isAdmin()) {// 点击的是超级管理员、管理员、不属于任何分组的成员 de 也不能点
            Intent intent = new Intent(this, PreviewFriendsInfoActivity.class);
            intent.putExtra(PreviewFriendsInfoActivity.FROMORG, "FROMORG");
            intent.putExtra(PreviewFriendsInfoActivity.kUSER_MEMBER_ID, info.getWp_member_info_id());//kUSER_MEMBER_ID;
            startActivity(intent);
        }else {
            Intent intent = new Intent();
            intent.setClass(this, DPMDetailsActivity.class);
            intent.putExtra(DPMDetailsActivity.ISBUILDER, isBulider);      //当前用户是这个组织的超级管理员
            intent.putExtra(DPMDetailsActivity.kORG_ID, org_id);           //该组织的ID
            intent.putExtra(DPMDetailsActivity.KDPM_ID, info.getDpm_id());  //分组ID
            intent.putExtra(DPMDetailsActivity.KTITLE, info.getDpm_name()); //分组名称
            intent.putExtra(DPMDetailsActivity.KORG_NAME, org_name);        //该组织的名称
            startActivity(intent);
        }
    }

    /**
     * 超级管理员将不属于任何分组的成员添加到一个分组中
     *
     * @param context
     * @param list
     * @param user_id
     * @param position
     */
    private void showSetDPMList(Context context, List<DepartmentInfo> list, String user_id, final int position) {
        if (list == null || list.size() < 1) {
            QLToastUtils.showToast(context, "没有分组可以加进");
            return;
        }
        ZSZDialogView dialogView = new ZSZDialogView(context, R.style.ZSZDialog, list, org_id, user_id);
        dialogView.show();
        SuccessAddMenberToDPMListener dpmListener = new SuccessAddMenberToDPMListener() {
            @Override
            public void onSuccess() {
                updataUI();
            }
        };
        dialogView.setSuccessAddMenberToDPMListener(dpmListener);
    }

    /**
     * 超级管理员移除不属于任何分组的成员时 显示移除对话框提示信息
     *
     * @param text
     * @param position
     * @param info
     */
    private void showDelectDialog(String text, final int position, final DepartmentInfo info) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_updata_app_layout, null);
        TextView messageTv = (TextView) view.findViewById(R.id.message_tv);
        Button yesBtn = (Button) view.findViewById(R.id.yes_btn);
        Button noBtn = (Button) view.findViewById(R.id.no_btn);
        yesBtn.setText("是");
        yesBtn.setTextColor(Color.parseColor("#000000"));
        noBtn.setText("否");
        messageTv.setText(text);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("删除");
        // alertDialog.setMessage(text);
        alertDialog.setView(view);
        alertDialog.setIcon(0);
        yesBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (info != null) {
                    removeOrgMember(org_id, info.getOrgRequestMemberInfo().getWp_member_info_id());
                } else {
                    removeDPM(position);  //移除没有添加到任何分组的成员的方法（侧滑菜单 移除 ）
                }
                alertDialog.dismiss();
            }
        });
        noBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    /**
     * 移除没有添加到任何分组的成员的方法（侧滑菜单 移除 超级管理员才可以）
     *
     * @param org_id
     * @param member_id
     */
    private void removeOrgMember(String org_id, String member_id) {
        showWaitDialog();
        OrganizationController.delectOrgMember(DPMListActivity.this, org_id, member_id, new Listener<Boolean, String>() {
            @Override
            public void onCallBack(Boolean status, String reply) {
                dismissWaitDialog();
                if (status) {
                    QLToastUtils.showToast(DPMListActivity.this, "移出成功");
                    updataUI();
                }
            }
        });
    }

    /**
     * 邀请成员回调数据的处理(将联系人添加到组织)
     *
     * @param
     */
    private void addFriendsToOrg(String friendsString) {
        if (TextUtils.isEmpty(friendsString) || friendsString == null ) {
            QLToastUtils.showToast(DPMListActivity.this, "没有选择联系人");
            return;
        }
        List<FriendInfo> tempInfos = new Gson().fromJson(friendsString, new TypeToken<List<FriendInfo>>() {}.getType());
        if (tempInfos == null || tempInfos.size() < 1) {
            return;
        }
        StringBuilder idBuilder = new StringBuilder();
        for (FriendInfo info : tempInfos) {
            idBuilder.append(info.getWp_friends_info_id() + ",");
        }
        idBuilder.deleteCharAt(idBuilder.toString().length() - 1);
        System.out.println(" idBuilder = " + idBuilder.toString());
        isResult = false ; //查看onResum()和onActivityResult（）方法的执行结果，需要加这个标志为来判断，防止界面布局错乱的问题
        OrganizationController.addPersonToOrganization(this, org_id , idBuilder.toString(),  new Listener<Boolean, String>() {
            @Override
            public void onCallBack(Boolean status, String reply) {
                if (status) {
                    QLToastUtils.showToast(DPMListActivity.this, "添加成功");
                    System.out.println(" addFriendsToOrg is tiem = "+ new Date().toString());
                    //return;
                }else {
                    QLToastUtils.showToast(DPMListActivity.this, reply);
                }
                updataUI();   //从新获取列表
                //isResult = true ;
                System.out.println(" reply = " + reply);
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (popupWindow_more != null && popupWindow_more.isShowing()) {
            ZSZSingleton.getZSZSingleton().backgroundAlpha(this, 1f);
            popupWindow_more.dismiss();
        }
        return false;
    }

    @Override
    public void onRefresh() {  //刷新
        if (refreshAlone) {
            updataUI();
        }
    }

    @Override
    public void onLoadMore() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == LogicFriends.REQUEST_CODE && data != null) {
//                addFriendsToOrg(data.getStringExtra(LogicFriends.RESULT_LIST));  //添加朋友回调的方法
                //System.out.println(data.getStringExtra(LogicFriends.RESULT_LIST));
                if (isBulider)
                    addFriendsToOrg(data.getStringExtra(LogicFriends.RESULT_LIST));  //添加朋友回调的方法
                else
                    recommendAssociation(data.getStringExtra(LogicFriends.RESULT_LIST));
            }
        }
    }

    private void recommendAssociation(String member_id){
        showWaitDialog();
        List<FriendInfo> tempInfos = new Gson().fromJson(member_id, new TypeToken<List<FriendInfo>>() {}.getType());
        if (tempInfos == null || tempInfos.size() < 1) {
            return;
        }
        StringBuilder idBuilder = new StringBuilder();
        for (FriendInfo info : tempInfos) {
            idBuilder.append(info.getWp_friends_info_id() + ",");
        }
        idBuilder.deleteCharAt(idBuilder.toString().length() - 1);
        OrganizationController.recommendAssociation(this, idBuilder.toString(), org_id, new Listener<Boolean, String>() {
            @Override
            public void onCallBack(Boolean aBoolean, String reply) {
                dismissWaitDialog();
                QLToastUtils.showToast(DPMListActivity.this,reply);
            }
        });
    }


    private void showDPMBox(final boolean create, final String name) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.view_edit_card_property, null);
        final EditText eText = (EditText) contentView.findViewById(R.id.edit_card_property_et);
        if (!create) {
            eText.setText(name);
        } else {
            eText.setHint("输入分组名称");
        }
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.setView(contentView);
        alertDialog.setCancelable(true);
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.setTitle("请输入");
        alertDialog.setIcon(0);
        DialogInterface.OnClickListener buttonOkClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == AlertDialog.BUTTON_NEUTRAL) {
                    String txt = eText.getText().toString();
                    if (create) {
                        addDPM(org_id, txt);
                    } else {
                        // modifyName()
                    }
                }
                dialog.dismiss();
            }
        };
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消", buttonOkClickListener);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "保存", buttonOkClickListener);
        alertDialog.show();
    }

    private void addDPM(final String org_id, String name) {
        showWaitDialog();
        OrganizationController.addDPM(this, org_id, name, new Listener<String, DepartmentInfo>() {
            @Override
            public void onCallBack(String status, DepartmentInfo reply) {
                dismissWaitDialog();
                if (status != null) {
                    String msg = status == null ? "添加分组失败" : status;
                    QLToastUtils.showToast(DPMListActivity.this, msg);
                    return;
                }
                updataUI(); //获取数据
            }
        });
    }

    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case HomeActivity.UPDATA_ORG_POINT:
                    View titlePoint = handler.getRightLayout().findViewById(R.id.title_point);
                    if (titlePoint !=null)
                        titlePoint.setVisibility(View.GONE);
                    if (orgPoint !=null)
                        orgPoint.setVisibility(View.GONE);
                    for (OrgUnreadNumberInfo numberInfo : YYSingleton.getInstance().getOrgUnreadDatas()){
                        if (numberInfo.getOrg_id().equals(org_id) && titlePoint !=null) {
                            titlePoint.setVisibility(View.VISIBLE);
                            if (orgPoint !=null)
                                orgPoint.setVisibility(View.VISIBLE);
                            break;
                        }
                    }
                    break;
                default:
                    break;
            }

        }
    };

    /**
     *显示
     */
    private void showShare( String orgName  ,String orgDes ) {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间等使用
        oks.setTitle("我的【格局】看到一个不错协会【"+ orgName +"】，向您推荐一下！");
        // titleUrl是标题的网络链接，QQ和QQ空间等使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText(orgDes);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");
        // 启动分享GUI
        oks.show(this);
    }
}
