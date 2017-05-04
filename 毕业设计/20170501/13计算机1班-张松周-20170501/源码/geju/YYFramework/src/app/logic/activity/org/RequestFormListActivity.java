package app.logic.activity.org;

import java.util.ArrayList;
import java.util.List;

import org.ql.utils.QLToastUtils;
import org.ql.utils.image.QLAsyncImage;
import org.ql.views.listview.QLXListView.IXListViewListener;
import org.ql.views.textview.FTextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.picasso.Picasso;

import android.R.integer;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import app.config.http.HttpConfig;
import app.logic.activity.ActTitleHandler;
import app.logic.activity.InitActActivity;
import app.logic.activity.user.PreviewFriendsInfoActivity;
import app.logic.adapter.YYBaseListAdapter;
import app.logic.controller.OrganizationController;
import app.logic.pojo.NoticeInfo;
import app.logic.pojo.JoinRequestListInfo;
import app.utils.common.FrescoImageShowThumb;
import app.utils.common.Listener;
import app.view.YYListView;
import app.yy.geju.R;
import app.yy.geju.R.drawable;

/*
 * GZYY    2016-8-16  下午3:40:42
 */

public class RequestFormListActivity extends InitActActivity implements IXListViewListener, OnItemClickListener {

    public static final String GET_JOINREQUEST_KRY = "GET_JOINREQUEST_KRY";
    private ActTitleHandler titleHandler;
    private List<JoinRequestListInfo> listInfos;
    private YYListView listView;
    private String org_idString;
    private Drawable defaultD;
    private Resources resources;
    private YYBaseListAdapter<JoinRequestListInfo> mAdapter = new YYBaseListAdapter<JoinRequestListInfo>(this) {
        @Override
        public View createView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(RequestFormListActivity.this).inflate(R.layout.item_requestformlistactivity, null);
                saveView("item_head_iv", R.id.item_head_iv, convertView);
                saveView("item_name_tv", R.id.item_name_tv, convertView);
                saveView("item_request_message_tv", R.id.item_request_message_tv, convertView);
                saveView("item_re_status_tv", R.id.item_re_status_tv, convertView);
            }
            final JoinRequestListInfo info = getItem(position);
            if (info != null) {
                SimpleDraweeView headIv = getViewForName("item_head_iv", convertView);
                TextView nameTv = getViewForName("item_name_tv", convertView);
                TextView reMessageTv = getViewForName("item_request_message_tv", convertView);
                TextView statusTv = getViewForName("item_re_status_tv", convertView);
                String url = HttpConfig.getUrl(info.getPicture_url());
//                headIv.setImageURI(url);
                FrescoImageShowThumb.showThrumb(Uri.parse(url),headIv);
//                Picasso.with(RequestFormListActivity.this).load(url).placeholder(R.drawable.default_user_icon).error(defaultD).fit().centerCrop().into(headIv);
                nameTv.setText(info.getNickName());
                // reMessageTv.setText(info.getRe)
                switch (info.getRequest_status()) {
                    case "":
                        statusTv.setText("同意");
                        statusTv.setTextColor(0xffffffff);
                        statusTv.setBackgroundDrawable(resources.getDrawable(R.drawable.shape_join_org_btn_bg));
                        break;
                    case "1":
                        statusTv.setText("已通过");
                        statusTv.setTextColor(resources.getColor(R.color.new_textView_color));
                        statusTv.setBackgroundDrawable(null);
                        break;
                }
                statusTv.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        joinFunction(position, 1);
                    }
                });
            }
            return convertView;
        }
    };

    @Override
    protected void initActTitleView() {
        titleHandler = new ActTitleHandler();
        setAbsHandler(titleHandler);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_requestformlist);
        setTitle("");
        titleHandler.replaseLeftLayout(this, true);
        titleHandler.getLeftLayout().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView) titleHandler.getLeftLayout().findViewById(R.id.left_tv)).setText("申请列表");
        titleHandler.getRightDefButton().setText("清空");
        titleHandler.getRightDefButton().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clearList();  //清空列表
            }
        });
        defaultD = getResources().getDrawable(R.drawable.default_user_icon);
        resources = getResources();
        listView = (YYListView) findViewById(R.id.listView);
    }

    @Override
    protected void initData() {
        org_idString = getIntent().getStringExtra(GET_JOINREQUEST_KRY);
        listInfos = new ArrayList<JoinRequestListInfo>();
        listView.setXListViewListener(this);
        listView.setPullLoadEnable(false, false);
        listView.setPullRefreshEnable( true );
        listView.setOnItemClickListener(this);
        listView.setAdapter(mAdapter);
        getJoinRequestLis(org_idString);
    }

    /**
     * ""是申请中，0是申请拒绝，1是申请通过 获取入会申请列表信息
     * @param org_id getJoinRequestLisRequestFormListActivity
     */
    private void getJoinRequestLis(String org_id) {
        showWaitDialog();
        OrganizationController.getJoinRequestList(this, org_id, new Listener<Void, List<JoinRequestListInfo>>() {
            @Override
            public void onCallBack(Void status, List<JoinRequestListInfo> reply) {
                listView.stopRefresh();
                listView.stopLoadMore();
                dismissWaitDialog();
                if (reply != null && reply.size() > 0) {
                    List<JoinRequestListInfo> tempInfos = new ArrayList<JoinRequestListInfo>();
                    for (JoinRequestListInfo info : reply) {
                        if (!"0".equals(info.getRequest_status())) {
                            tempInfos.add(info);
                        }
                    }
                    listInfos.clear();
                    if (tempInfos.size() > 0) {
                        listInfos.addAll(tempInfos);
                    }
                    mAdapter.setDatas(listInfos);
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        getJoinRequestLis(org_idString);
    }

    @Override
    public void onLoadMore() {
    }

    /**
     * 对于入会申请的操作，1：同意加入；0：拒绝加入；2：不再显示该用户加入请求,"":申请中
     *
     * @param position
     * @param action
     */
    private void joinFunction(int position, int action) {
        JoinRequestListInfo info = mAdapter.getItem(position);
        if (info == null) {
            return;
        }
        if (TextUtils.isEmpty(info.getRequest_status())) {
            OrganizationController.responseJoinOrganizationRequest(this, info.getRequest_id(), action, new Listener<Boolean, String>() {
                @Override
                public void onCallBack(Boolean status, String reply) {
                    if (!status) {
                        return;
                    }
                    QLToastUtils.showToast(RequestFormListActivity.this, "处理成功");
                    getJoinRequestLis(org_idString);  //获取申请列表
                }
            });
        }
    }

    /**
     * 清空列表
     * clearListRequestFormListActivity
     */
    private void clearList() {
        if (listInfos.size() < 1) {
            return;
        }
        OrganizationController.deleteJoinRequest(this, org_idString, new Listener<Integer, String>() {
            @Override
            public void onCallBack(Integer integer, String reply) {
                if (integer == 1) {
                    finish();
                } else if (integer == -1) {
                    QLToastUtils.showToast(RequestFormListActivity.this, reply == null ? "操作失败" : reply);
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        JoinRequestListInfo info = null ;
        if(position -1 >= 0){
            info = listInfos.get(position -1 );
        }
        if( info!=null){
            Intent intent  = new Intent();
            intent.setClass( this , PreviewFriendsInfoActivity.class );
            intent.putExtra(PreviewFriendsInfoActivity.kUSER_MEMBER_ID, info.getMember_id());//kUSER_MEMBER_ID;
            startActivity(intent);
        }
    }
}
