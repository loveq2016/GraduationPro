package app.logic.activity.org;

import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.picasso.Picasso;

import org.canson.view.swipemenulistview.SwipeMenu;
import org.canson.view.swipemenulistview.SwipeMenuCreator;
import org.canson.view.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import org.ql.activity.customtitle.ActActivity;
import org.ql.app.alert.AlertDialog;
import org.ql.utils.QLToastUtils;
import org.ql.views.listview.QLXListView.IXListViewListener;

import java.util.ArrayList;
import java.util.List;

import app.config.http.HttpConfig;
import app.logic.activity.ActTitleHandler;
import app.logic.activity.user.PreviewFriendsInfoActivity;
import app.logic.activity.user.QRCodePersonal;
import app.logic.adapter.YYBaseListAdapter;
import app.logic.controller.OrganizationController;
import app.logic.controller.UserManagerController;
import app.logic.pojo.FriendInfo;
import app.logic.pojo.OrgRecommendMemberInfo;
import app.logic.pojo.OrganizationInfo;
import app.logic.pojo.UserInfo;
import app.utils.common.FrescoImageShowThumb;
import app.utils.common.Listener;
import app.view.DialogNewStyleController;
import app.view.YYListView;
import app.yy.geju.R;

/**
 * SiuJiYung create at 2016年6月14日 下午5:29:05
 * <p>
 * 添加好友
 */

public class OrgFindMemActivity extends ActActivity implements OnClickListener, IXListViewListener, OnItemClickListener {

    public static final String SEARCH_KEY = "SEARCH_KEY";

    private ActTitleHandler mHandler = new ActTitleHandler();
    private YYListView requestListView;
    private AlertDialog alertDialog;
    private AlertDialog alertDialogFriends;
    private AlertDialog alertDialogOrg;

    String org_id;

    private OnClickListener btnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == null) {
                return;
            }
            showWaitDialog();
            final OrgRecommendMemberInfo info = (OrgRecommendMemberInfo) v.getTag();
            OrganizationController.orgInviteMember(OrgFindMemActivity.this, info.getWp_member_info_id(), org_id, new Listener<Boolean, String>() {
                @Override
                public void onCallBack(Boolean aBoolean, String reply) {
                    dismissWaitDialog();
                    if (aBoolean) {
                        info.setStatus(2);
                        mAdapter.notifyDataSetChanged();
                    }
                    QLToastUtils.showToast(OrgFindMemActivity.this, reply);
                }
            });
                // showAddFriendsDialog(info);
        }
    };

    private YYBaseListAdapter<OrgRecommendMemberInfo> mAdapter = new YYBaseListAdapter<OrgRecommendMemberInfo>(this) {
        @Override
        public View createView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(OrgFindMemActivity.this).inflate(R.layout.item_row_request_friends, null);
                Button okBtn = (Button) convertView.findViewById(R.id.item_row_rq_friends_ok_btn);
                okBtn.setOnClickListener(btnClickListener);
            }
            SimpleDraweeView userHeadImageView = ((SimpleDraweeView) convertView.findViewById(R.id.item_row_rq_friends_imgview));
            TextView nameTv = (TextView) convertView.findViewById(R.id.item_row_rq_friends_name_tv);
            TextView msgTv = (TextView) convertView.findViewById(R.id.item_row_rq_friends_msg_tv);
            Button okBtn = (Button) convertView.findViewById(R.id.item_row_rq_friends_ok_btn);

            OrgRecommendMemberInfo info = (OrgRecommendMemberInfo) getItem(position);
            if (info != null) {
                okBtn.setTag(info);
                msgTv.setVisibility(View.GONE );
                nameTv.setText(info.getNickName());
                FrescoImageShowThumb.showThrumb(Uri.parse(HttpConfig.getUrl(info.getPicture_url())),userHeadImageView);

                if (info.getStatus()==1){
                    okBtn.setEnabled(false);
                    okBtn.setText("已同意");
                    okBtn.setTextColor(0xffc6c6c6);

                }else if(info.getStatus()==2){
                    okBtn.setEnabled(false);
                    okBtn.setText("已邀请");
                    okBtn.setTextColor(0xffc6c6c6);
                }else{
                    okBtn.setEnabled(true);
                    okBtn.setText("邀请TA");
                    okBtn.setTextColor(0xfffcfcfc);
                }

            }
            return convertView;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbsHandler(mHandler);
        setContentView(R.layout.activity_add_friends2);
        mHandler.getRightLayout().setVisibility(View.VISIBLE);
        mHandler.replaseLeftLayout(this, true);
        mHandler.getLeftLayout().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView) mHandler.getLeftLayout().findViewById(R.id.left_tv)).setText("推荐成员");
        mHandler.getRightDefButton().setText("换一批");
        mHandler.getRightDefButton().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getList();
            }
        });

        setTitle("");
        findViewById(R.id.add_friends_head).setVisibility(View.GONE);
        findViewById(R.id.add_friend_title).setVisibility(View.GONE);
        org_id = getIntent().getStringExtra("ORG_ID");

        requestListView = (YYListView) findViewById(R.id.add_friends_lv);
        requestListView.setPullRefreshEnable(false);
        requestListView.setPullLoadEnable(false,true);
        requestListView.setAdapter(mAdapter);
        requestListView.setXListViewListener(this);
        requestListView.setOnItemClickListener(this);

        findViewById(R.id.add_friends_scan_qr_tv).setOnClickListener(this);
        findViewById(R.id.add_friends_contact).setOnClickListener(this);

        getList();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();


    }

    private void getList(){
        showWaitDialog();
        UserManagerController.getRandomRecommendMember(this, org_id,new Listener<List<OrgRecommendMemberInfo>, String>() {

            @Override
            public void onCallBack(List<OrgRecommendMemberInfo> reply, String mag) {
                dismissWaitDialog();
                requestListView.stopLoadMore();
                requestListView.stopRefresh();

                if (reply !=null && reply.size()>0) {
                    mAdapter.setDatas(reply);

                }else{
                    QLToastUtils.showToast(OrgFindMemActivity.this,"无数据");
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
    }


    @Override
    public void onRefresh() {
    }

    @Override
    public void onLoadMore() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        OrgRecommendMemberInfo info = mAdapter.getItem(position - 1);
        if (info != null) {
            startActivity(new Intent(OrgFindMemActivity.this, PreviewFriendsInfoActivity.class).putExtra(PreviewFriendsInfoActivity.kUSER_MEMBER_ID, info.getWp_member_info_id()).putExtra(PreviewFriendsInfoActivity.kFROM_CHART_ACTIVITY, false).putExtra("FIND_MEMBER",true));
        }
    }
}
