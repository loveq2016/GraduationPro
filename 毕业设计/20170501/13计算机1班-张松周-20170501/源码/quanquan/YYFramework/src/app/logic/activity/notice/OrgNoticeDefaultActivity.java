package app.logic.activity.notice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.canson.view.swipemenulistview.SwipeMenu;
import org.canson.view.swipemenulistview.SwipeMenuCreator;
import org.canson.view.swipemenulistview.SwipeMenuItem;
import org.canson.view.swipemenulistview.SwipeMenuListView;
import org.ql.activity.customtitle.ActActivity;
import org.ql.utils.QLToastUtils;
import org.ql.views.listview.QLXListView.IXListViewListener;

import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.picasso.Picasso;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import app.config.http.HttpConfig;
import app.logic.activity.ActTitleHandler;
import app.logic.activity.InitActActivity;
import app.logic.activity.TYBaseActivity;
import app.logic.activity.announce.AnnounceActivity;
import app.logic.activity.main.HomeActivity;
import app.logic.adapter.LaunchPagerAdapter;
import app.logic.adapter.YYBaseListAdapter;
import app.logic.controller.AnnounceController;
import app.logic.pojo.NoticeInfo;
import app.logic.singleton.ZSZSingleton;
import app.utils.common.FrescoImageShowThumb;
import app.utils.common.Listener;
import app.utils.helpers.YYUtils;
import app.view.CustomViewPager;
import app.view.RichTextViewActivity2;
import app.view.YYListView;
import app.yy.geju.R;

/*
 * GZYY    2016-12-7  下午3:53:43
 * author: zsz
 */

public class OrgNoticeDefaultActivity extends ActActivity {

    public static final String ORG_ID = "ORG_ID";
    public static final String ORG_NAME = "ORG_NAME";

    private String org_id;  //组织ID

    private TextView unRead_tv, hasRead_tv; //已读，未读
    private View unRead_iv, hasRead_iv;     //已读，未读下面的那条线
    private CustomViewPager viewPager;      //ViewPager
    private LaunchPagerAdapter pagerAdapter;
    private Resources resources;
    private LayoutInflater inflater;

    private View unReadView, hasReadView;
    private List<View> views = new ArrayList<View>();

    private YYListView unReedListView, hasReadListView;                  //已读列表，未读列表
    private YYBaseListAdapter<NoticeInfo> unReadAdapter, hasReadAdapter; //已读列表适配器 ，未读列表适配器
    private List<NoticeInfo> unReadDatas, hasReadDatas;                  //已读列表数据源，未读列表数据源

    private ActTitleHandler titleHandler = new ActTitleHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        setAbsHandler(titleHandler);
        setContentView(R.layout.activity_notice_list);
        org_id = getIntent().getStringExtra(ORG_ID);
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
        addDatas(-1);
    }

    /**
     * 初始化TootBar
     */
    private void initActHandler() {
        setTitle("");
        titleHandler.replaseLeftLayout(this, true);
        ((TextView) titleHandler.getLeftLayout().findViewById(R.id.left_tv)).setText(getIntent().getStringExtra(ORG_NAME));
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

        inflater = LayoutInflater.from(this);
        resources = getResources();
        unRead_tv = (TextView) findViewById(R.id.unread_tv);
        hasRead_tv = (TextView) findViewById(R.id.hasRead_tv);

        unRead_iv = findViewById(R.id.unRead_iv);
        hasRead_iv = findViewById(R.id.hasRead_iv);

        unReadView = inflater.inflate(R.layout.add_listview_for_viewpager, null);
        hasReadView = inflater.inflate(R.layout.add_listview_for_viewpager, null);
        views.add(unReadView);
        views.add(hasReadView);
        pagerAdapter = new LaunchPagerAdapter(this, views);

        viewPager = (CustomViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setPagingEnabled(false); //设置ViewPager的左右滑动

        unReedListView = (YYListView) unReadView.findViewById(R.id.add_listView);
        hasReadListView = (YYListView) hasReadView.findViewById(R.id.add_listView);
        unReedListView.setPullLoadEnable(false, true);
        hasReadListView.setPullLoadEnable(false, true);

        unReadDatas = new ArrayList<NoticeInfo>();
        hasReadDatas = new ArrayList<NoticeInfo>();
    }

    /**
     * 已读，未读切换改变 （一开始是未读）
     *
     * @param position
     */
    private void changeTab(int position) {
        if (position == 0) {

            unRead_tv.setTextColor(resources.getColor(R.color.new_app_color));
            hasRead_tv.setTextColor(resources.getColor(R.color.black));

            unRead_iv.setBackgroundColor(resources.getColor(R.color.new_app_color));
            hasRead_iv.setBackgroundColor(resources.getColor(R.color.line_bg));

        } else {
            unRead_tv.setTextColor(resources.getColor(R.color.black));

            hasRead_tv.setTextColor(resources.getColor(R.color.new_app_color));
            unRead_iv.setBackgroundColor(resources.getColor(R.color.line_bg));
            hasRead_iv.setBackgroundColor(resources.getColor(R.color.new_app_color));
        }

    }

    /**
     * 设置适配器
     */
    private void setAdapters() {
        unReadAdapter = new YYBaseListAdapter<NoticeInfo>(this) {

            @Override
            public View createView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.item_notice_unread, null);
                    saveView("msg_title", R.id.msg_title, convertView);
                    saveView("msg_user", R.id.msg_user, convertView);
                    saveView("msg_time", R.id.msg_time, convertView);

                    saveView("msg_iv", R.id.msg_iv, convertView);

                }

                NoticeInfo info = (NoticeInfo) getItem(position);

                if (info != null) {
                    setTextToViewText(info.getMsg_title(), "msg_title", convertView);
                    if(info.getFriend_name()!=null && !TextUtils.isEmpty( info.getFriend_name())){
                        setTextToViewText("发布者：" + info.getFriend_name(), "msg_user", convertView);
                    }else{
                        setTextToViewText("发布者：" + info.getMsg_creator(), "msg_user", convertView);
                    }
                    setTextToViewText(ZSZSingleton.getTimeStyle(info.getMsg_create_time()), "msg_time", convertView);
                    SimpleDraweeView noticeIv = getViewForName("msg_iv", convertView);
//                    noticeIv.setImageURI(HttpConfig.getUrl(info.getPicture_url()));
                    FrescoImageShowThumb.showThrumb(Uri.parse(HttpConfig.getUrl(info.getPicture_url())),noticeIv);
//                    Picasso.with(OrgNoticeDefaultActivity.this).load(HttpConfig.getUrl(info.getPicture_url())).error(R.drawable.item_notice_default).fit().centerCrop().into(noticeIv);
                }

                return convertView;

            }
        };
        hasReadAdapter = new YYBaseListAdapter<NoticeInfo>(this) {

            @Override
            public View createView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.item_notice_unread, null);
                    saveView("msg_title", R.id.msg_title, convertView);
                    saveView("msg_user", R.id.msg_user, convertView);
                    saveView("msg_time", R.id.msg_time, convertView);
                    saveView("msg_iv", R.id.msg_iv, convertView);
                }

                NoticeInfo info = (NoticeInfo) getItem(position);

                if (info != null) {
                    setTextToViewText(info.getMsg_title(), "msg_title", convertView);
                    if(info.getFriend_name()!=null && !TextUtils.isEmpty( info.getFriend_name())){
                        setTextToViewText("发布者：" + info.getFriend_name(), "msg_user", convertView);
                    }else{
                        setTextToViewText("发布者：" + info.getMsg_creator(), "msg_user", convertView);
                    }
                    setTextToViewText(ZSZSingleton.getTimeStyle(info.getMsg_create_time()), "msg_time", convertView);
                    SimpleDraweeView noticeIv = getViewForName("msg_iv", convertView);
//                    noticeIv.setImageURI(HttpConfig.getUrl(info.getPicture_url()));
                    FrescoImageShowThumb.showThrumb(Uri.parse(HttpConfig.getUrl(info.getPicture_url())),noticeIv);
//                    Picasso.with(OrgNoticeDefaultActivity.this).load(HttpConfig.getUrl(info.getPicture_url())).error(R.drawable.item_notice_default).fit().centerCrop().into(noticeIv);
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
                SwipeMenuItem openItem = new SwipeMenuItem(OrgNoticeDefaultActivity.this);
                openItem.setBackground(R.drawable.menu_delete_bg);
                openItem.setWidth(YYUtils.dp2px(90, OrgNoticeDefaultActivity.this));
                openItem.setTitle("移除");
                openItem.setTitleSize(16);
                openItem.setTitleColor(0xfffcfcfc);
                menu.addMenuItem(openItem);
            }
        };
        unReedListView.setMenuCreator(swipeMenuCreator);
        hasReadListView.setMenuCreator(swipeMenuCreator);
    }


    /**
     * -1是获取全部，0是未读，1是已读
     *
     * @param type addDatasOgrNoticeDefaultActivity
     */
    private void addDatas(final int type) {
        if (type == -1) {
            addDatas(0); //先获取未读的 （有点递归类似的调用）
            addDatas(1); //在获取已读的
            return;
        }
        AnnounceController.getAnnounceList(this, "0", "1000", org_id, "1", String.valueOf(type), new Listener<Void, List<NoticeInfo>>() {

            @Override
            public void onCallBack(Void status, List<NoticeInfo> reply) {
                unReedListView.stopRefresh();   //列表停止刷新
                hasReadListView.stopRefresh();
                unReadDatas.clear();
                hasReadDatas.clear();

                if (type == 0) {
                    if (reply != null && reply.size() > 0) {
                        unReadDatas.addAll(reply);
                    }

                    unReadAdapter.setDatas(unReadDatas);

                } else {
                    if (reply != null && reply.size() > 0) {
                        hasReadDatas.addAll(reply);
                    }

                    hasReadAdapter.setDatas(hasReadDatas);

                }
            }
        });

    }

    /**
     * 给View设置监听器
     */
    private void addListener() {
        unRead_tv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                addDatas(0);
                changeTab(0);
                viewPager.setCurrentItem(0);
            }
        });
        hasRead_tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addDatas(1);
                changeTab(1);
                viewPager.setCurrentItem(1);

            }
        });

        unReedListView.setXListViewListener(new IXListViewListener() {

            @Override
            public void onRefresh() {
                addDatas(0); //刷新未读的

            }

            @Override
            public void onLoadMore() {
                // TODO Auto-generated method stub

            }
        });
        hasReadListView.setXListViewListener(new IXListViewListener() {

            @Override
            public void onRefresh() {
                addDatas(1); //刷新已读的

            }

            @Override
            public void onLoadMore() {
                // TODO Auto-generated method stub

            }
        });

        unReedListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                NoticeInfo info = unReadAdapter.getItem(position - 1);
                if (info != null) {

                    Intent intent = new Intent(OrgNoticeDefaultActivity.this, DefaultNoticeActivity.class);
                    intent.putExtra(DefaultNoticeActivity.NOTICE_ID, info.getMsg_id());
                    if(info.getFriend_name()!=null && !TextUtils.isEmpty( info.getFriend_name())){
                        intent.putExtra(DefaultNoticeActivity.NAME,info.getFriend_name());
                    }else{
                        intent.putExtra(DefaultNoticeActivity.NAME,info.getMsg_creator());
                    }
                    startActivity(intent);
                    addDatas(0);

                }

            }
        });

        hasReadListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NoticeInfo info = hasReadAdapter.getItem(position - 1);

                if (info != null) {

                    Intent intent = new Intent(OrgNoticeDefaultActivity.this, DefaultNoticeActivity.class);
                    intent.putExtra(DefaultNoticeActivity.NOTICE_ID, info.getMsg_id());
                    if(info.getFriend_name()!=null && !TextUtils.isEmpty( info.getFriend_name())){
                        intent.putExtra(DefaultNoticeActivity.NAME,info.getFriend_name());
                    }else{
                        intent.putExtra(DefaultNoticeActivity.NAME,info.getMsg_creator());
                    }
                    startActivity(intent);
                }
            }
        });

        unReedListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(int position, SwipeMenu menu, int index) {
                NoticeInfo info = unReadAdapter.getItem(position);
                if (info != null) {
                    removeItem(info.getMsg_id(), 0);
                }
            }
        });
        hasReadListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(int position, SwipeMenu menu, int index) {
                NoticeInfo info = hasReadAdapter.getItem(position);
                if (info != null) {
                    removeItem(info.getMsg_id(), 1);
                }
            }
        });
    }

    /**
     * 删除一条公告
     *
     * @param msg_id
     */
    private void removeItem(String msg_id, final int type) {
        showWaitDialog();
        AnnounceController.removeAnnounceInfo(this, msg_id, new Listener<Boolean, String>() {
            @Override
            public void onCallBack(Boolean aBoolean, String reply) {
                dismissWaitDialog();
                QLToastUtils.showToast(OrgNoticeDefaultActivity.this, reply);
                if (aBoolean) {
                    if (type == 0) {
                        addDatas(0);
                        Intent intent = new Intent();
                        intent.setAction(HomeActivity.UPDATANOTICE);
                        sendBroadcast(intent);
                    } else {
                        addDatas(1);
                    }
                }
            }
        });
    }
}





