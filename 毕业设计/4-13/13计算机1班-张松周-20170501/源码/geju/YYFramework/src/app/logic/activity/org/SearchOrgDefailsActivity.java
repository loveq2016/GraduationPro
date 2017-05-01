package app.logic.activity.org;

import org.ql.activity.customtitle.ActActivity;
import org.ql.utils.QLToastUtils;

import com.squareup.picasso.Picasso;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import app.config.http.HttpConfig;
import app.logic.activity.ActTitleHandler;
import app.logic.controller.OrganizationController;
import app.logic.pojo.OrganizationInfo;
import app.utils.common.Listener;
import app.yy.geju.R;

/*
 * GZYY    2016-12-12  下午2:22:15
 * author: zsz
 */

public class SearchOrgDefailsActivity extends ActActivity {

    public static final String ORG_INFO = "ORG_INFO";
    public static final String QR_RESTLT_ID = "QR_RESTLT_ID";

    private ImageView orgHead_iv;
    private TextView orgName_tv, orgNum_tv, orgIvt_tv, orgPho_tv, orgAdd_tv, orgMem_tv;
    private Button join_btn;

    private OrganizationInfo orgInfo;
    private String org_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        ActTitleHandler actTitleHandler = new ActTitleHandler();
        setAbsHandler(actTitleHandler);
        setContentView(R.layout.activity_search_org_default);

        actTitleHandler.replaseLeftLayout(this, true);
        actTitleHandler.getLeftLayout().setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        orgInfo = (OrganizationInfo) getIntent().getSerializableExtra(ORG_INFO);

        initView();
        setTitle("");
        if (orgInfo != null) {
            ((TextView) actTitleHandler.getLeftLayout().findViewById(R.id.left_tv)).setText(orgInfo.getOrg_name());
            updataUI();
        }
        org_id = getIntent().getStringExtra(QR_RESTLT_ID);
        if (org_id != null) {
            getOrgInfo(org_id);
        }
    }

    private void initView() {
        orgHead_iv = (ImageView) findViewById(R.id.org_head_iv);
        orgName_tv = (TextView) findViewById(R.id.org_name_tv);
        orgIvt_tv = (TextView) findViewById(R.id.org_ivt_tv);
        orgPho_tv = (TextView) findViewById(R.id.org_phone_tv);
        orgAdd_tv = (TextView) findViewById(R.id.org_addr_tv);
        orgMem_tv = (TextView) findViewById(R.id.org_member_tv);
        join_btn = (Button) findViewById(R.id.org_join_btn);


        join_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                joinOrg();

            }
        });

    }

    private void updataUI() {

        Picasso.with(this).load(HttpConfig.getUrl(orgInfo.getOrg_logo_url())).error(R.drawable.default_user_icon).fit().centerCrop().into(orgHead_iv);
        orgName_tv.setText(orgInfo.getOrg_name());
        orgIvt_tv.setText(orgInfo.getOrg_industryName());
        orgPho_tv.setText(orgInfo.getOrg_tel());
        orgAdd_tv.setText(orgInfo.getOrg_city());
        orgMem_tv.setText(String.valueOf(orgInfo.getNumber()));

        if (orgInfo != null) {
            if (orgInfo.getOrg_status() == 0) {
                join_btn.setText("申请中");
            } else if (orgInfo.getOrg_status() == 10) {
                join_btn.setText("已申请");
            } else if (orgInfo.getOrg_status() == 12) {
                join_btn.setText("重新申请");
            } else {
                join_btn.setText("申请加入");
            }
        }

    }

    private void joinOrg() {
        OrganizationController.joinOrganization(this, orgInfo.getOrg_id(), "", new Listener<Boolean, String>() {
            @Override
            public void onCallBack(Boolean status, String reply) {
                dismissWaitDialog();
                QLToastUtils.showToast(SearchOrgDefailsActivity.this, reply);
            }
        });
    }

    /**
     * 扫一扫
     *
     * @param org_id
     */
    private void getOrgInfo(String org_id) {
        OrganizationController.getOrganizationInfo(this, org_id, new Listener<Void, List<OrganizationInfo>>() {
            @Override
            public void onCallBack(Void aVoid, List<OrganizationInfo> reply) {
                if (reply == null || reply.size() < 1) {
                    QLToastUtils.showToast(SearchOrgDefailsActivity.this, "获取组织信息失败");
                    return;
                }
                orgInfo = reply.get(0);
                updataUI();

            }
        });
    }

}
