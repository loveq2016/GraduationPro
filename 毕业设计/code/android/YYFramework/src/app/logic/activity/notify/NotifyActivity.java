package app.logic.activity.notify;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.ql.activity.customtitle.ActActivity;
import org.ql.app.alert.AlertDialog;
import org.ql.utils.QLToastUtils;
import org.ql.views.listview.QLXListView.IXListViewListener;

import java.util.ArrayList;
import java.util.List;

import app.config.http.HttpConfig;
import app.logic.activity.ActTitleHandler;
import app.logic.activity.org.ApplyToJoinActivity;
import app.logic.activity.user.PreviewFriendsInfoActivity;
import app.logic.adapter.YYBaseListAdapter;
import app.logic.controller.OrganizationController;
import app.logic.controller.UserManagerController;
import app.logic.pojo.FriendInfo;
import app.logic.pojo.OrgNotifyInfo;
import app.logic.pojo.OrgRecommendMemberInfo;
import app.utils.common.FrescoImageShowThumb;
import app.utils.common.Listener;
import app.view.YYListView;
import app.yy.geju.R;

/**
 * SiuJiYung create at 2016年6月14日 下午5:29:05
 * <p>
 * 添加好友
 */

public class NotifyActivity extends ActActivity implements OnClickListener, IXListViewListener, OnItemClickListener {

    private ActTitleHandler mHandler = new ActTitleHandler();
    private YYListView requestListView;

    private OnClickListener btnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == null) {
                return;
            }
            OrgNotifyInfo info = (OrgNotifyInfo) v.getTag();
            switch (v.getId()){
                case R.id.item_row_rq_org_ok_btn:
                    modifyStatus(1,info.getMessage_id());
                    break;
                case R.id.item_row_rq_org_refuse_btn:
                    modifyStatus(0,info.getMessage_id());
                    break;
            }

                // showAddFriendsDialog(info);
        }
    };

    private YYBaseListAdapter<OrgNotifyInfo> mAdapter = new YYBaseListAdapter<OrgNotifyInfo>(this) {
        @Override
        public View createView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(NotifyActivity.this).inflate(R.layout.item_row_request_notify, null);

            }
            SimpleDraweeView userHeadImageView = ((SimpleDraweeView) convertView.findViewById(R.id.item_row_rq_org_imgview));
            TextView nameTv = (TextView) convertView.findViewById(R.id.item_row_rq_org_name_tv);
            TextView msgTv = (TextView) convertView.findViewById(R.id.item_row_rq_org_msg_tv);
            Button okBtn = (Button) convertView.findViewById(R.id.item_row_rq_org_ok_btn);
            Button refuseBtn = (Button) convertView.findViewById(R.id.item_row_rq_org_refuse_btn);


            OrgNotifyInfo info = (OrgNotifyInfo) getItem(position);
            if (info != null) {

                if (info.getMessage_type_id()==20){
                    if (info.getOperation_type()==2){
                        refuseBtn.setVisibility(View.GONE);
                        okBtn.setVisibility(View.VISIBLE);
                        okBtn.setEnabled(false);
                        okBtn.setText("已同意");
                        okBtn.setTextColor(0xffc6c6c6);
                    }else if (info.getOperation_type()==1){
                        refuseBtn.setVisibility(View.GONE);
                        okBtn.setVisibility(View.VISIBLE);
                        okBtn.setEnabled(false);
                        okBtn.setText("已拒绝");
                        okBtn.setTextColor(0xffc6c6c6);
                    }else{
                        okBtn.setTag(info);
                        refuseBtn.setTag(info);
                        okBtn.setEnabled(true);
                        okBtn.setVisibility(View.VISIBLE);
                        refuseBtn.setVisibility(View.VISIBLE);
                        okBtn.setOnClickListener(btnClickListener);
                        refuseBtn.setOnClickListener(btnClickListener);
                        okBtn.setTextColor(0xfffcfcfc);
                    }

                    nameTv.setText("协会邀请");
                }else{
                    nameTv.setText("协会推荐");
                    okBtn.setVisibility(View.GONE);
                    refuseBtn.setVisibility(View.GONE);
                }
                msgTv.setText(info.getDescriptions());
                FrescoImageShowThumb.showThrumb(Uri.parse(HttpConfig.getUrl(info.getOrg_logo_url())),userHeadImageView);
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
        ((TextView) mHandler.getLeftLayout().findViewById(R.id.left_tv)).setText("系统通知");
        mHandler.getRightDefButton().setText("清空");
        mHandler.getRightDefButton().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showWaitDialog();
                OrganizationController.clearOrgInviteMember(NotifyActivity.this, new Listener<Boolean, String>() {
                    @Override
                    public void onCallBack(Boolean aBoolean, String reply) {
                        dismissWaitDialog();
                        if (aBoolean){
                            mAdapter.setDatas(new ArrayList<OrgNotifyInfo>());
                        }else
                            QLToastUtils.showToast(NotifyActivity.this,reply);
                    }
                });
            }
        });

        setTitle("");
        findViewById(R.id.add_friends_head).setVisibility(View.GONE);
        findViewById(R.id.add_friend_title).setVisibility(View.GONE);

        requestListView = (YYListView) findViewById(R.id.add_friends_lv);
        requestListView.setPullRefreshEnable(true);
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
        OrganizationController.getOrgNotifyList(this, new Listener<Void, List<OrgNotifyInfo>>() {
            @Override
            public void onCallBack(Void aVoid, List<OrgNotifyInfo> reply) {
                dismissWaitDialog();
                requestListView.stopLoadMore();
                requestListView.stopRefresh();

                if (reply !=null && reply.size()>0) {
                    mAdapter.setDatas(reply);

                }else{
                    QLToastUtils.showToast(NotifyActivity.this,"无数据");
                }
            }
        });
    }

    private void modifyStatus(int status,String message_id){
        showWaitDialog();
        OrganizationController.modifyInviteStatus(this, status, message_id, new Listener<Boolean, String>() {
            @Override
            public void onCallBack(Boolean aBoolean, String reply) {
                dismissWaitDialog();
                if (aBoolean){
                    getList();
                }else
                    QLToastUtils.showToast(NotifyActivity.this,reply);
            }
        });
    }

    @Override
    public void onClick(View v) {
    }


    @Override
    public void onRefresh() {
        getList();
    }

    @Override
    public void onLoadMore() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        OrgNotifyInfo info = mAdapter.getItem(position - 1);
        if (info != null) {
            Intent intent = new Intent(this, ApplyToJoinActivity.class);
            intent.putExtra(ApplyToJoinActivity.ORG_ID, info.getOrg_id());
            startActivity(intent);
            }
    }
}
