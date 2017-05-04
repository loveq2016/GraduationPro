package app.logic.activity.org;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.QLConstant;
import org.ql.activity.customtitle.ActActivity;
import org.ql.utils.QLToastUtils;

import java.util.List;

import app.config.http.HttpConfig;
import app.logic.activity.ActTitleHandler;
import app.logic.controller.OrganizationController;
import app.logic.pojo.OrganizationInfo;
import app.utils.common.Listener;
import app.yy.geju.R;

/**
 * Created by Administrator on 2017/1/16 0016.
 */

public class ApplyToJoinActivity extends ActActivity {

    public static final String ORG_ID = "ORG_ID";
    private ImageView orgHead_iv;
    private TextView orgName_tv, orgNum_tv, orgIvt_tv, orgPho_tv, orgAdd_tv, orgMem_tv, orgEmail_tv;
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
        actTitleHandler.getLeftLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setTitle("");

        initView();
        //org_id = getIntent().getStringExtra("orgid");
        org_id = getIntent().getStringExtra(ORG_ID);
        if (org_id != null) {
            getOrgInfo(org_id);
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
                if (reply == null || reply.size() < 1) {
                    return;
                }
                orgInfo = reply.get(0);
                updataUI();
            }
        });
    }

    /**
     * 初始化View
     */
    private void initView() {
        orgHead_iv = (ImageView) findViewById(R.id.org_head_iv);
        orgName_tv = (TextView) findViewById(R.id.org_name_tv);
        orgIvt_tv = (TextView) findViewById(R.id.org_ivt_tv);
        orgPho_tv = (TextView) findViewById(R.id.org_phone_tv);
        orgAdd_tv = (TextView) findViewById(R.id.org_addr_tv);
        orgMem_tv = (TextView) findViewById(R.id.org_member_tv);
        orgEmail_tv = (TextView) findViewById(R.id.org_email);
        join_btn = (Button) findViewById(R.id.org_join_btn);
    }

    /**
     * 更新UI
     */
    private void updataUI() {
        Picasso.with(this).load(HttpConfig.getUrl(orgInfo.getOrg_logo_url())).error(R.drawable.default_user_icon).fit().centerCrop().into(orgHead_iv);
        orgName_tv.setText(orgInfo.getOrg_name());
        orgIvt_tv.setText(orgInfo.getOrg_des());
        orgPho_tv.setText(orgInfo.getOrg_tel());
        orgAdd_tv.setText(orgInfo.getOrg_addr());
        orgMem_tv.setText(orgInfo.getNumber() + "");
        orgEmail_tv.setText(orgInfo.getOrg_email());

        if ("2".equals(orgInfo.getApply_status())) {
            join_btn.setText("已加入");
            join_btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_btn_gray));
            join_btn.setTextColor(getResources().getColor(R.color.white));
            join_btn.setEnabled(false); //设置为不可用的状态
            //join_btn.setVisibility( View.GONE );
        } else if ("1".equals(orgInfo.getApply_status())) {
            join_btn.setText("审核中...");
            join_btn.setEnabled(false);
            join_btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_btn_gray));
            join_btn.setTextColor(getResources().getColor(R.color.white));
        } else if ("3".equals(orgInfo.getApply_status())) {
            join_btn.setText("重新申请加入");
        } else {
            join_btn.setText("申请加入");
        }

        join_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                joinOrg(org_id == null ? orgInfo.getOrg_id() : org_id);
            }
        });
    }

    /**
     * 加入組織
     */
    private void joinOrg(String org_id) {
        OrganizationController.joinOrganization(this, org_id, "", new Listener<Boolean, String>() {
            @Override
            public void onCallBack(Boolean status, String reply) {
                dismissWaitDialog();
                if (status) {
                    join_btn.setText("审核中...");
                    join_btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_btn_gray));
                    join_btn.setTextColor(getResources().getColor(R.color.white));
                } else {
                    QLToastUtils.showToast(ApplyToJoinActivity.this, reply);
                }
            }
        });
    }
}
