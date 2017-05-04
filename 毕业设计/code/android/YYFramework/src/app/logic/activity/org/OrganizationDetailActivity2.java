package app.logic.activity.org;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.OptionsPickerView.OnOptionsSelectListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.squareup.picasso.Picasso;

import org.QLConstant;
import org.ql.activity.customtitle.OnActActivityResultListener;
import org.ql.utils.QLToastUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import app.config.DemoApplication;
import app.config.http.HttpConfig;
import app.logic.activity.ActTitleHandler;
import app.logic.activity.InitActActivity;
import app.logic.activity.user.ShowBigImageActivity;
import app.logic.controller.OrganizationController;
import app.logic.pojo.OrganizationInfo;
import app.logic.pojo.UpdataOrgInfo;
import app.logic.singleton.YYInterface.UpdataOrgMemNumListener;
import app.logic.singleton.YYSingleton;
import app.logic.singleton.ZSZSingleton;
import app.utils.common.FrescoHelper;
import app.utils.common.Listener;
import app.utils.file.YYFileManager;
import app.utils.helpers.ImagePickerHelper;
import app.utils.helpers.QRHelper;
import app.utils.image.QLImageHelper;
import app.yy.geju.R;

/*
 * GZYY    2016-9-22  下午2:50:17
 */

public class OrganizationDetailActivity2 extends InitActActivity implements View.OnClickListener {

    public static final String kOrganizationInfoKey = "kOrganizationInfoKey";
    public static final String SHOWVIEW = "SHOWFOOTVIEW";
    public static final String ISBULIDER = "ISBULIDER";

    private ActTitleHandler mHandler;
    private QRHelper qrHelper;

    private OrganizationInfo orgInfo;
    private OptionsPickerView<String> cityPickerView;
    private ArrayList<String> proviceList = new ArrayList<String>();
    private ArrayList<ArrayList<String>> citesList = new ArrayList<ArrayList<String>>();
    private String provice_name, city_name;

    private SimpleDraweeView logoView;
    private TextView nameView;
    private EditText org_des;
    private TextView org_count;
    private EditText org_addr;
    private EditText org_number;
    private EditText org_email;
    private RelativeLayout apply_for_list;
    private RelativeLayout add_friend_to_org;
    private View footView;
    private Button joinOrgBtn;
    private ImageView org_qrcode;
    private RelativeLayout org_qrcode_layout;
    private RelativeLayout org_memberNum_layout;
//	private LinearLayout org_addr_layout;

    private boolean saveStatus = false;

    @Override
    protected void initActTitleView() {
        mHandler = new ActTitleHandler();
        setAbsHandler(mHandler);

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_review_org_info2); //org_number  org_des
        initActTitle();

        logoView = (SimpleDraweeView) findViewById(R.id.org_review_logo_view);
        nameView = (TextView) findViewById(R.id.org_review_name_view);
        org_des = (EditText) findViewById(R.id.org_des);
        org_count = (TextView) findViewById(R.id.org_count);
        org_addr = (EditText) findViewById(R.id.org_addr);
        org_number = (EditText) findViewById(R.id.org_number);
        org_email = (EditText) findViewById(R.id.org_email);
        apply_for_list = (RelativeLayout) findViewById(R.id.apply_for_list);
        footView = (View) findViewById(R.id.foot_view);
        joinOrgBtn = (Button) footView.findViewById(R.id.footview_btn);
        add_friend_to_org = (RelativeLayout) findViewById(R.id.add_friend_to_org);
        org_qrcode = (ImageView) findViewById(R.id.org_qrcode);
        org_qrcode_layout = (RelativeLayout) findViewById(R.id.org_qrcode_layout);
        org_memberNum_layout = (RelativeLayout) findViewById(R.id.org_memberNum);
//		org_addr_layout = (LinearLayout) findViewById(R.id.org_addr_layout);
        org_des.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if( s.length()> 200){
                    org_des.setText( s.subSequence( 0 , 200 ));
                    org_des.setSelection( 200 );
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        org_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if( s.length()> 11){
                    org_number.setText( s.subSequence( 0 , 11 ));
                    org_number.setSelection( 11 );
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    protected void initData() {
        // TODO Auto-generated method stub

        String org_json_info = getIntent().getStringExtra(kOrganizationInfoKey);

        qrHelper = new QRHelper();

        Gson gson = new Gson();
        try {
            orgInfo = gson.fromJson(org_json_info, OrganizationInfo.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        String logo_url = HttpConfig.getUrl(orgInfo.getOrg_logo_url());

        FrescoHelper.asyncLoad(Uri.parse(logo_url), logoView);
        // Picasso.with(this).load(logo_url).placeholder(R.drawable.default_user_icon).fit().centerCrop().into(logoView);
        nameView.setText(orgInfo.getOrg_name());

        org_des.setText(orgInfo.getOrg_des());
        org_count.setText(String.valueOf(orgInfo.getNumber()));
        org_addr.setText(orgInfo.getOrg_addr());
        org_number.setText(orgInfo.getOrg_tel());
        org_email.setText(orgInfo.getOrg_email());
        if (orgInfo.getIsadmin() != 1) {
            apply_for_list.setVisibility(View.GONE);
            mHandler.getRightLayout().setVisibility(View.GONE);
            add_friend_to_org.setVisibility(View.GONE);

        }

        org_des.setFocusable(false);
        org_number.setFocusable(false);
        org_email.setFocusable(false);
        org_addr.setFocusable(false);
        showOptionsPickerView();
        getMyOrgList(footView);

        logoView.setOnClickListener(this);
        nameView.setOnClickListener(this);
        org_des.setOnClickListener(this);
        org_number.setOnClickListener(this);
        org_email.setOnClickListener(this);
        apply_for_list.setOnClickListener(this);
        joinOrgBtn.setOnClickListener(this);
        add_friend_to_org.setOnClickListener(this);
        org_qrcode.setOnClickListener(this);
        org_qrcode_layout.setOnClickListener(this);
        org_memberNum_layout.setOnClickListener(this);
//		org_addr_layout.setOnClickListener(this);

        YYSingleton.getInstance().setUpdataOrgMemNumListener(new UpdataOrgMemNumListener() {

            @Override
            public void onCallBack(int i) {
                String temp = org_count.getText().toString();
                temp = String.valueOf(Integer.parseInt(temp) + i);
                org_count.setText(temp);

            }
        });
    }

    private void initActTitle() {
        setTitle("");
        mHandler.replaseLeftLayout(this, true);
        mHandler.getLeftLayout().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView) mHandler.getLeftLayout().findViewById(R.id.left_tv)).setText("组织信息");
        mHandler.addRightView(LayoutInflater.from(this).inflate(R.layout.title_rightview_layout, null), true);
        ((TextView) mHandler.getRightLayout().findViewById(R.id.title_right_tv)).setText("编辑");
        boolean isBulider = getIntent().getBooleanExtra(ISBULIDER, false);
        if (isBulider) {
            mHandler.getRightLayout().setVisibility(View.VISIBLE);
        } else {
            mHandler.getRightLayout().setVisibility(View.GONE);
        }
        mHandler.getRightLayout().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView) mHandler.getRightLayout().findViewById(R.id.title_right_tv)).setText("保存");
                org_des.setFocusableInTouchMode(true);
                org_addr.setFocusableInTouchMode(true);
                org_number.setFocusableInTouchMode(true);
                org_email.setFocusableInTouchMode(true);
                if (saveStatus) {
                    updataOrg();
                }
                saveStatus = true;
            }
        });
    }

    // 更换组织头像
    private void replaseHeadImageView(final String org_id, final String user_info) {
        if (orgInfo.getIsMember() == 1) {
            ImagePickerHelper helper = ImagePickerHelper.createNewImagePickerHelper(this);
            helper.setImageSourceType(ImagePickerHelper.kImageSource_UserSelect);
            helper.setOnReciveImageListener(new Listener<Void, String>() {

                @Override
                public void onCallBack(Void status, String reply) {
                    if (reply == null) {
                        return;
                    }
                    Bitmap btmBitmap = QLImageHelper.readBitmap(reply, logoView.getWidth() * 2, logoView.getHeight() * 2);
                    logoView.setImageBitmap(btmBitmap);
                    OrganizationController.replaceOrgInfoLogo(OrganizationDetailActivity2.this, org_id, user_info, reply, new Listener<Boolean, String>() {

                        @Override
                        public void onCallBack(Boolean status, String reply) {
                            dismissWaitDialog();
                            if (status) {
                            }

                        }
                    });
                }
            });
            helper.setOnActActivityResultListener(new OnActActivityResultListener() {

                @Override
                public void onActivityResult(int requestCode, int resultCode, Intent data) {
                    showWaitDialog();

                }
            });
            helper.setReplaceContentLayout(true);
            helper.openCamera();

        }
    }

    // 申请加入组织
    private void JoinOrganization() {
        if (orgInfo == null) {
            return;
        }
        showWaitDialog();
        OrganizationController.joinOrganization(this, orgInfo.getOrg_id(), "", new Listener<Boolean, String>() {
            @Override
            public void onCallBack(Boolean status, String reply) {
                dismissWaitDialog();
                if (status.booleanValue() == true) {
                    Intent intent = new Intent();
                    intent.setClass(OrganizationDetailActivity2.this, RequestJoinOrganizationActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void getMyOrgList(final View footView) {
        OrganizationController.getMyOrganizationList(this, new Listener<Void, List<OrganizationInfo>>() {
            @Override
            public void onCallBack(Void status, List<OrganizationInfo> reply) {
                boolean showJoinBtn = true;
                if (reply != null && orgInfo != null) {
                    for (OrganizationInfo organizationInfo : reply) {
                        if (orgInfo.getOrg_id().equals(organizationInfo.getOrg_id())) {
                            showJoinBtn = false;
                            break;
                        }
                    }
                }
                if (showJoinBtn) {
                    footView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    // 修改组织信息
    private void updataOrg() {

        showWaitDialog();
        UpdataOrgInfo info = new UpdataOrgInfo();

        info.setOrg_id(orgInfo.getOrg_id());
        info.setOrg_tel(org_number.getText().toString());
        if (TextUtils.isEmpty(org_addr.getText().toString())) {
            QLToastUtils.showToast(this, "组织地址不能为空");
            return;
        }
        info.setOrg_addr(org_addr.getText().toString());
        info.setOrg_des(org_des.getText().toString());
        info.setOrg_email(org_email.getText().toString());

        Gson gson = new Gson();
        String jsonInfo = gson.toJson(info);

        OrganizationController.updateAssociationInfo(OrganizationDetailActivity2.this, jsonInfo, new Listener<Boolean, String>() {

            @Override
            public void onCallBack(Boolean status, String reply) {
                dismissWaitDialog();
                if (status) {
                    finish();
                }
            }
        });

    }

    // cityPickerView
    private void showOptionsPickerView() {
        StringBuffer sBuffer = YYFileManager.readTextFileFromAssets(this, "province.json");
        if (sBuffer == null) {
            return;
        }
        try {

            Map<String, List<String>> proviceMap = ZSZSingleton.getProviceInfo(sBuffer.toString());

            cityPickerView = new OptionsPickerView<String>(this);
            cityPickerView.setCancelable(true);

            for (String key : proviceMap.keySet()) {
                proviceList.add(key);
                List<String> valuesList = proviceMap.get(key);
                ArrayList<String> _cl = new ArrayList<String>();
                _cl.addAll(valuesList);
                citesList.add(_cl);
            }
            cityPickerView.setPicker(proviceList, citesList, true);
            cityPickerView.setOnoptionsSelectListener(new OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int option2, int options3) {
                    final String provice_str = proviceList.get(options1);
                    final String city_str = citesList.get(options1).get(option2);
                    provice_name = provice_str;
                    city_name = city_str;

                    org_addr.post(new Runnable() {
                        @Override
                        public void run() {
                            org_addr.setText(provice_str + " " + city_str + "");
                        }
                    });
                }
            });

        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

    }

    private void openCityPicker() {
        if (cityPickerView == null || cityPickerView.isShowing()) {
            return;
        }
        cityPickerView.show();
    }

    // 调用隐藏系统默认的输入法
    private void hideKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
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

        LayoutParams p2 = new LayoutParams((int) (d.getWidth() * 0.70), (int) (d.getHeight() * 0.30));

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
        qrcode.setImageBitmap(showHeaderQRcodeBitmap(logoView, decodeString, imgWidth, imgHeight));
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {

            case R.id.org_review_logo_view:
                if (!saveStatus) {
                    if (orgInfo.getOrg_logo_url() == null || TextUtils.isEmpty(orgInfo.getOrg_logo_url())) {
                        startActivity(new Intent(OrganizationDetailActivity2.this, ShowBigImageActivity.class));
                        return;
                    }
                    startActivity(new Intent(OrganizationDetailActivity2.this, ShowBigImageActivity.class).putExtra(ShowBigImageActivity.PIC_URL, HttpConfig.getUrl(orgInfo.getOrg_logo_url())));
                } else {
                    replaseHeadImageView(orgInfo.getOrg_id(), QLConstant.client_id);
                }

                break;
            case R.id.org_des:
                break;
            case R.id.org_qrcode:
                showOrgQRCode();
                break;
            case R.id.org_qrcode_layout:
                showOrgQRCode();
                break;

            case R.id.org_addr_layout:
                if (saveStatus) {
                    hideKeyboard();
                    openCityPicker();
                }

                break;
            case R.id.org_number:
                break;
            case R.id.org_email:
                break;
            case R.id.apply_for_list:
                Intent intent = new Intent();
                intent.setClass(OrganizationDetailActivity2.this, RequestFormListActivity.class);
                intent.putExtra(RequestFormListActivity.GET_JOINREQUEST_KRY, orgInfo.getOrg_id());
                startActivity(intent);
                break;
            case R.id.footview_btn:
                JoinOrganization();
                break;

            case R.id.add_friend_to_org:
                Intent intent2 = new Intent(OrganizationDetailActivity2.this, AddFriendToOrg.class);
                intent2.putExtra(AddFriendToOrg.SELECTOR_ORG_ID, orgInfo.getOrg_id());
                intent2.putExtra(AddFriendToOrg.TITLE, "添加成员");
                startActivity(intent2);
                break;

            case R.id.org_memberNum:
                Intent intent3 = new Intent(OrganizationDetailActivity2.this, OrganizationAllMemberShow.class);
                intent3.putExtra(OrganizationAllMemberShow.ORG_ID, orgInfo.getOrg_id());   //组织ID
                startActivity(intent3);
                break;
            default:
                break;
        }

    }
}
