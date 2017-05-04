package app.logic.activity.org;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.QLConstant;
import org.ql.activity.customtitle.ActActivity;
import org.ql.utils.QLToastUtils;

import java.io.UnsupportedEncodingException;
import java.util.List;

import app.config.DemoApplication;
import app.config.http.HttpConfig;
import app.logic.activity.ActTitleHandler;
import app.logic.controller.OrganizationController;
import app.logic.pojo.OrganizationInfo;
import app.utils.common.Listener;
import app.utils.helpers.QRHelper;
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
    private RelativeLayout org_qrcode_layout;
    private QRHelper qrHelper;

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
        org_qrcode_layout = (RelativeLayout) findViewById(R.id.org_qrcode_layout);
        qrHelper = new QRHelper();
        org_qrcode_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrgQRCode();
            }
        });
    }

    // 生成组织二维码
    private void showOrgQRCode() {

        Gson gson = new Gson();
        QROrganization organizationinfo = new QROrganization();
        organizationinfo.setOrg_id( orgInfo.getOrg_id() );
        organizationinfo.setOrg_name( orgInfo.getOrg_name() );
        organizationinfo.setOrg_logo_url( orgInfo.getOrg_logo_url() );
        String orgInfoString = gson.toJson(organizationinfo);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int imgWidth = (int) dm.scaledDensity * 200;
        int imgHeight = (int) dm.scaledDensity * 200;

        Bitmap qriBitmap = qrHelper.createQRImage(orgInfoString, imgWidth, imgHeight);

        String orgPic = HttpConfig.getUrl(orgInfo.getOrg_logo_url());
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高

        WindowManager.LayoutParams p2 = new WindowManager.LayoutParams((int) (d.getWidth() * 0.70), (int) (d.getHeight() * 0.30));

        View dialog_view = getLayoutInflater().inflate(R.layout.dialog_qrode, null);
        ImageView qrcode = (ImageView) dialog_view.findViewById(R.id.qrcode);
        SimpleDraweeView pic = (SimpleDraweeView) dialog_view.findViewById(R.id.im_personpic);
        TextView nick = (TextView) dialog_view.findViewById(R.id.tx_name);
        TextView phone = (TextView) dialog_view.findViewById(R.id.tx_phone);
        dialog_view.findViewById(R.id.tx_phone).setVisibility(View.GONE);

        Picasso.with(this).load(Uri.parse(orgPic)).placeholder(R.drawable.default_user_icon).into(pic);
        // pic.setImageBitmap(((BitmapDrawable) logoView.get、Drawable()).getBitmap());
//        FrescoHelper.asyncLoad(Uri.parse(orgPic), pic);
        nick.setText(orgInfo.getOrg_name());
        // phone.setText("格局号：" + userInfo.getPhone());

        String decodeString = null;
        try {
            decodeString = new String(orgInfoString.getBytes(), "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            decodeString = orgInfoString;
        }
        qrcode.setImageBitmap(showHeaderQRcodeBitmap(orgHead_iv, decodeString, imgWidth, imgHeight));
        Dialog qrDialog = new Dialog(this, R.style.dialog);
        qrDialog.setContentView(dialog_view, p2);
        qrDialog.show();

    }

    // 显示装有图片的二维码
    private Bitmap showHeaderQRcodeBitmap(ImageView imageView, String text, int imgWidth, int imgHeight) {

        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        // 图片宽度的一半
        //int IMAGE_HALFWIDTH = 60;
        int IMAGE_HALFWIDTH = DemoApplication.QRInsideImg;

        // 缩放图片
        Matrix matrix = new Matrix();
        float sx = (float) 2 * IMAGE_HALFWIDTH / bitmap.getWidth();
        float sy = (float) 2 * IMAGE_HALFWIDTH / bitmap.getHeight();
        matrix.setScale(sx, sy);
        // 重新构造一个40*40的图片
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);

        return qrHelper.createBitmapToHeader(text, bitmap, imgWidth, imgHeight);
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
