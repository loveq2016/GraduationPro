package app.logic.activity.org;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ql.activity.customtitle.ActActivity;
import org.ql.activity.customtitle.OnActActivityResultListener;
import org.ql.app.alert.AlertDialog;
import org.ql.utils.QLToastUtils;

import com.bumptech.glide.Glide;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.sort.sortlistview.ClearEditText;
import com.squareup.picasso.Picasso;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import app.config.http.HttpConfig;
import app.logic.activity.ActTitleHandler;
import app.logic.activity.announce.AnnounceActivity;
import app.logic.activity.announce.FileUploader;
import app.logic.activity.main.HomeActivity;
import app.logic.controller.AnnounceController;
import app.logic.controller.OrganizationController;
import app.logic.pojo.OrganizationInfo;
import app.utils.common.FrescoImageShowThumb;
import app.utils.common.Listener;
import app.utils.helpers.ImagePickerHelper;
import app.utils.image.QLImageHelper;
import app.yy.geju.R;

/**
 * SiuJiYung create at 2016年8月12日 上午11:01:44
 */

public class CreateOranizationActivity extends ActActivity implements OnClickListener {

    public static final int AUDIT_MODE = 1;
    public static final int CREATE_MODE = 2;
    public static final String CREATE_ORG = "CREATE_ORG";
    public static final String ORG_INFO = "ORG_INFO";
    public static final String ORG_CREATE_MODEL = "ORG_CREATE_MODEL";
    public static final String ORG_ID = "ORG_ID";
    public static final String OPEN_MODE = "OPEN_MODE";
    private ActTitleHandler titleHandler = new ActTitleHandler();
    private OrganizationInfo orgInfo;
    private ErrorMsg error_msg; //错误信息对象
    private String org_id;
    private int mode ;
    private ImagePickerHelper imgPicker;
    private TextView titleTv;
    private EditText orgName_edt, orgPhone_edt, orgAddr_edt, orgEmail_edt, orgOrgdes_edt, orgReadName_edt, orgCreatorPho_edt;
    private TextView orgCountEditSize_tv;
    private SimpleDraweeView orgLogo_iv, orgOwner_iv, orgCer_iv;
    private Button orgPost_btn;
    private String orgLogoUrl, orgoWnerUrl, orgCerUrl;
    private Drawable iconDef;
    private boolean createOrgTage = false ;

    //新增
    private TextView eorg_nameTv, eorg_logo_urlTv, eorg_telTv, eorg_addrTv, eorg_emailTv,
            econtact_id_img_urlTv, eorg_certificate_img_urlTv, eorg_desTv, eorg_contact_nameTv, eorg_contact_telTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbsHandler(titleHandler);
        setContentView(R.layout.activity_create_org2);
        intiTitler();
        initView();
        intiErrorView();
        initListener();
        mode = getIntent().getIntExtra(OPEN_MODE, 2); //出错时等于2  从那一界面跳转过来的
        createOrgTage = getIntent().getBooleanExtra(CREATE_ORG , false) ;
        viewGONE();
        if( createOrgTage ){ //创建组织模式下
            viewGONE();
        }
        if (mode != CREATE_MODE) {
            org_id = getIntent().getStringExtra(ORG_ID);  //获取组织的ID
            getOrgInfo(); //获取组织信息

            titleHandler.getRightDefButton().setText("删除");
            titleHandler.getRightDefButton().setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteOrgInfo();
                }
            });
        } else {
            orgInfo = new OrganizationInfo();
        }
        iconDef = getResources().getDrawable(R.drawable.default_user_icon);


    }

    /**
     * 初始化TootBar
     */
    private void intiTitler() {
        setTitle("");
        titleHandler.replaseLeftLayout(this, true);
        titleHandler.getLeftLayout().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleTv = (TextView) titleHandler.getLeftLayout().findViewById(R.id.left_tv);
        titleTv.setText("创建组织");
    }

    /**
     * 初始化View
     */
    private void initView() {
        imgPicker = ImagePickerHelper.createNewImagePickerHelper(this);
        orgName_edt = (EditText) findViewById(R.id.create_org_orgname_et);
        orgLogo_iv = (SimpleDraweeView) findViewById(R.id.create_org_logo_iv);
        orgPhone_edt = (EditText) findViewById(R.id.create_org_tel_et);
        orgAddr_edt = (EditText) findViewById(R.id.create_org_addr_et);
        orgEmail_edt = (EditText) findViewById(R.id.create_org_email_et);
        orgOwner_iv = (SimpleDraweeView) findViewById(R.id.create_org_owner_id_iv);
        orgCer_iv = (SimpleDraweeView) findViewById(R.id.create_org_cer_iv);
        orgOrgdes_edt = (EditText) findViewById(R.id.create_org_orgdes_et);
        orgCountEditSize_tv = (TextView) findViewById(R.id.create_count_editSize);
        orgReadName_edt = (EditText) findViewById(R.id.create_org_contact_name_et);
        orgCreatorPho_edt = (EditText) findViewById(R.id.create_org_contact_tel_et);
        orgPost_btn = (Button) findViewById(R.id.create_org_post_btn);
    }

    /**
     * 初始化错误的信息View
     */
    private void intiErrorView() {
        eorg_nameTv = (TextView) findViewById(R.id.eorg_nameTv);
        eorg_logo_urlTv = (TextView) findViewById(R.id.eorg_logo_urlTv);
        eorg_telTv = (TextView) findViewById(R.id.eorg_telTv);
        eorg_addrTv = (TextView) findViewById(R.id.eorg_addrTv);
        eorg_emailTv = (TextView) findViewById(R.id.eorg_emailTv);
        econtact_id_img_urlTv = (TextView) findViewById(R.id.econtact_id_img_urlTv);
        eorg_certificate_img_urlTv = (TextView) findViewById(R.id.eorg_certificate_img_urlTv);
        eorg_desTv = (TextView) findViewById(R.id.eorg_desTv);
        eorg_contact_nameTv = (TextView) findViewById(R.id.eorg_contact_nameTv);
        eorg_contact_telTv = (TextView) findViewById(R.id.eorg_contact_telTv);
    }


    /**
     * 更新数据
     */
    private void updataUI() {
        titleTv.setText(orgInfo.getOrg_name());
        orgName_edt.setText(orgInfo.getOrg_name());
//        Picasso.with(this).load(HttpConfig.getUrl(orgInfo.getOrg_logo_url())).error(iconDef).placeholder(iconDef).fit().centerCrop().into(orgLogo_iv);
        GenericDraweeHierarchy hierarchy = orgLogo_iv.getHierarchy();
        hierarchy.setRoundingParams(RoundingParams.fromCornersRadius(10));
//        orgLogo_iv.setImageURI(HttpConfig.getUrl(orgInfo.getOrg_logo_url()));
        FrescoImageShowThumb.showThrumb(Uri.parse(HttpConfig.getUrl(orgInfo.getOrg_logo_url())),orgLogo_iv);

        orgPhone_edt.setText(orgInfo.getOrg_tel());
        orgAddr_edt.setText(orgInfo.getOrg_addr());
        orgEmail_edt.setText(orgInfo.getOrg_email());
//        Picasso.with(this).load(HttpConfig.getUrl(orgInfo.getContact_id_img_url())).error(iconDef).placeholder(iconDef).fit().centerCrop().into(orgOwner_iv);
//        Picasso.with(this).load(HttpConfig.getUrl(orgInfo.getOrg_certificate_img_url())).error(iconDef).placeholder(iconDef).fit().centerCrop().into(orgCer_iv);
//        orgOwner_iv.setImageURI(HttpConfig.getUrl(orgInfo.getContact_id_img_url()));
//        orgCer_iv.setImageURI(HttpConfig.getUrl(orgInfo.getOrg_certificate_img_url()));
        FrescoImageShowThumb.showThrumb(Uri.parse(HttpConfig.getUrl(orgInfo.getContact_id_img_url())),orgOwner_iv);
        FrescoImageShowThumb.showThrumb(Uri.parse(HttpConfig.getUrl(orgInfo.getOrg_certificate_img_url())),orgCer_iv);
        orgOwner_iv.getHierarchy().setRoundingParams(RoundingParams.fromCornersRadius(10));
        orgCer_iv.getHierarchy().setRoundingParams(RoundingParams.fromCornersRadius(10));
        orgOrgdes_edt.setText(orgInfo.getOrg_des());
        orgReadName_edt.setText(orgInfo.getOrg_contact_name());
        orgCreatorPho_edt.setText(orgInfo.getOrg_contact_tel());
        orgCountEditSize_tv.setText(orgInfo.getOrg_des().length() + "/200");
        if (orgInfo.getOrg_status() == 0) {
            orgPost_btn.setText("审核中...");
            viewGONE() ;  //隐藏错误提示信息的view
            setEditTextEnable(false);
            orgPost_btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_btn_gray));
        } else if (orgInfo.getOrg_status() == 12) {
            orgPost_btn.setText("重新申请");
        }else{
            viewGONE() ; //隐藏错误提示信息的view
        }
    }

    /**
     * 设置focus状态
     *
     * @param status
     */
    private void setEditTextEnable(boolean status) {

        orgName_edt.setFocusableInTouchMode(status);
        orgPhone_edt.setFocusableInTouchMode(status);
        orgAddr_edt.setFocusableInTouchMode(status);
        orgEmail_edt.setFocusableInTouchMode(status);
        orgOrgdes_edt.setFocusableInTouchMode(status);
        orgReadName_edt.setFocusableInTouchMode(status);
        orgCreatorPho_edt.setFocusableInTouchMode(status);

        orgLogo_iv.setEnabled(status);
        orgOwner_iv.setEnabled(status);
        orgCer_iv.setEnabled(status);

    }


    /**
     * 填写错误的信息
     *
     * @param error_msg
     */
    private void setErrormsg(ErrorMsg error_msg) {
        if (error_msg == null) {
            eorg_nameTv.setText("");
            eorg_logo_urlTv.setText("");
            eorg_telTv.setText("");
            eorg_addrTv.setText("");
            eorg_emailTv.setText("");
            econtact_id_img_urlTv.setText("");
            eorg_certificate_img_urlTv.setText("");
            eorg_desTv.setText("");
            eorg_contact_nameTv.setText("");
            eorg_contact_telTv.setText("");
            return;
        }
        String eorg_name = error_msg.getOrg_name();
        String eorg_logo_url = error_msg.getOrg_logo_url();
        String eorg_tel = error_msg.getOrg_tel();
        String eorg_addr = error_msg.getOrg_addr();
        String eorg_email = error_msg.getOrg_email();
        String econtact_id_img_url = error_msg.getContact_id_img_url();
        String eorg_certificate_img_url = error_msg.getOrg_certificate_img_url();
        String eorg_des = error_msg.getOrg_des();
        String eorg_contact_name = error_msg.getOrg_contact_name();
        String eorg_contact_tel = error_msg.getOrg_contact_tel();

        if (!TextUtils.isEmpty(eorg_name)) {
            eorg_nameTv.setText("*" + eorg_name);
        } else {
            eorg_nameTv.setText("");
        }
        if (!TextUtils.isEmpty(eorg_logo_url)) {
            eorg_logo_urlTv.setText("*" + eorg_logo_url);
        } else {
            eorg_logo_urlTv.setText("");
        }
        if (!TextUtils.isEmpty(eorg_tel)) {
            eorg_telTv.setText("*" + eorg_tel);
        } else {
            eorg_telTv.setText("");
        }
        if (!TextUtils.isEmpty(eorg_addr)) {
            eorg_addrTv.setText("*" + eorg_addr);
        } else {
            eorg_addrTv.setText("");
        }
        if (!TextUtils.isEmpty(eorg_email)) {
            eorg_emailTv.setText("*" + eorg_email);
        } else {
            eorg_emailTv.setText("");
        }
        if (!TextUtils.isEmpty(econtact_id_img_url)) {
            econtact_id_img_urlTv.setText("*" + econtact_id_img_url);
        } else {
            econtact_id_img_urlTv.setText("");
        }
        if (!TextUtils.isEmpty(eorg_certificate_img_url)) {
            eorg_certificate_img_urlTv.setText("*" + eorg_certificate_img_url);
        } else {
            eorg_certificate_img_urlTv.setText("");
        }

        if (!TextUtils.isEmpty(eorg_des)) {
            eorg_desTv.setText("*" + eorg_des);
        } else {
            eorg_desTv.setText("");
        }
        if (!TextUtils.isEmpty(eorg_contact_name)) {
            eorg_contact_nameTv.setText("*" + eorg_contact_name);
        } else {
            eorg_contact_nameTv.setText("");
        }
        if (!TextUtils.isEmpty(eorg_contact_tel)) {
            eorg_contact_telTv.setText("*" + eorg_contact_tel);
        } else {
            eorg_contact_telTv.setText("");
        }

    }

    /**
     * 隐藏掉view
     */
    private void viewGONE() {
        eorg_nameTv.setVisibility(View.GONE);
        eorg_logo_urlTv.setVisibility(View.GONE);
        eorg_telTv.setVisibility(View.GONE);
        eorg_addrTv.setVisibility(View.GONE);
        eorg_emailTv.setVisibility(View.GONE);
        econtact_id_img_urlTv.setVisibility(View.GONE);
        eorg_certificate_img_urlTv.setVisibility(View.GONE);
        eorg_desTv.setVisibility(View.GONE);
        eorg_contact_nameTv.setVisibility(View.GONE);
        eorg_contact_telTv.setVisibility(View.GONE);
    }

    /**
     * 添加监听器
     */
    private void initListener() {
        orgLogo_iv.setOnClickListener(this); //组织标志
        orgOwner_iv.setOnClickListener(this); //身份证正面照
        orgCer_iv.setOnClickListener(this);  //组织登记证书
        orgPost_btn.setOnClickListener(this);  //创建组织按钮
        orgOrgdes_edt.addTextChangedListener(new TextWatcher() {  //组织简介
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s.toString())) {
                    orgCountEditSize_tv.setText("0/200");
                } else {
                    if (s.toString().length() > 200) {
                        orgOrgdes_edt.setText(s.subSequence(0, 200));
                        orgOrgdes_edt.setSelection( 200 );//光标的位置
                        orgCountEditSize_tv.setText("200/200");
                    } else {
                        orgCountEditSize_tv.setText(s.toString().length() + "/200");
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });

        //电话号码
        orgPhone_edt.addTextChangedListener( new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 11 ) {
                    orgPhone_edt.setText(s.subSequence(0, 11));
                    orgPhone_edt.setSelection( 11 );//光标的为位置
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //申请人的电话号码
//        orgCreatorPho_edt.addTextChangedListener( new TextWatcher(){
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (s.toString().length() > 11 ) {
//                    orgCreatorPho_edt.setText(s.subSequence(0, 11));
//                    orgCreatorPho_edt.setSelection( 11 );//光标的置
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
    }

    /**
     * 检查信息是否完整
     *
     * @return checkFullInfoCreateOranizationActivity
     */
    private boolean checkFullInfo() {
        String orgNameString = orgName_edt.getText().toString();
        String orgPhoneString = orgPhone_edt.getText().toString();
        String orgAddrString = orgAddr_edt.getText().toString();
        String orgEmailString = orgEmail_edt.getText().toString();
        String orgDesString = orgOrgdes_edt.getText().toString();
        String orgCreatorNameString = orgReadName_edt.getText().toString();
        String orgCreatorPhoneString = orgCreatorPho_edt.getText().toString();
        if (!TextUtils.isEmpty(orgNameString) && !TextUtils.isEmpty(orgPhoneString) && !TextUtils.isEmpty(orgAddrString) && !TextUtils.isEmpty(orgEmailString) && !TextUtils.isEmpty(orgDesString)
                && (orgDesString.length() <= 200) && !TextUtils.isEmpty(orgCreatorNameString) && !TextUtils.isEmpty(orgCreatorPhoneString)) {
            if (!TextUtils.isEmpty(orgInfo.getOrg_logo_url()) && !TextUtils.isEmpty(orgInfo.getContact_id_img_url()) && !TextUtils.isEmpty(orgInfo.getOrg_certificate_img_url())) {
                orgInfo.setOrg_name(orgNameString);
                orgInfo.setOrg_tel(orgPhoneString);
                orgInfo.setOrg_addr(orgAddrString);
                orgInfo.setOrg_email(orgEmailString);
                orgInfo.setOrg_des(orgDesString);
                orgInfo.setOrg_contact_name(orgCreatorNameString);
                orgInfo.setOrg_contact_tel(orgCreatorPhoneString);

                return true;
            } else {
                QLToastUtils.showToast(this, "请填写完整的信息");
                return false;
            }
        }
        QLToastUtils.showToast(this, "请填写完整的信息");
        return false;
    }

    /**
     * 提交数据
     */
    private void sendData() {
        if (!checkFullInfo()) {
            return;
        }
        try {
            Gson gson = new Gson();
            String infoString = gson.toJson(orgInfo);
            showWaitDialog();
            // 证件，身份证，logo
            OrganizationController.createOrganization(this, infoString , orgInfo.getOrg_certificate_img_url(), orgInfo.getContact_id_img_url(), orgInfo.getOrg_logo_url(),
                    new Listener<Boolean, OrganizationInfo>() {
                        @Override
                        public void onCallBack(Boolean status, OrganizationInfo reply) {
                            dismissWaitDialog();
                            if (status.booleanValue()) {
                                QLToastUtils.showToast(CreateOranizationActivity.this, "创建成功");
                                Intent intent = new Intent();
                                intent.setClass(CreateOranizationActivity.this, HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            } else {
                                QLToastUtils.showToast(CreateOranizationActivity.this, "创建失败");
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 提交数据
     */
    private void sendDataNew() {
        if ( !checkFullInfo() ) {
            return;
        }
        try {
//            Gson gson = new Gson();
//            final String infoString = gson.toJson(orgInfo);
            showWaitDialog();
            if( mode == CREATE_MODE ){  //创建模式下，组织ID设为空
                orgInfo.setOrg_id("");
            }else{
                orgInfo.setOrg_id(org_id);
            }

            //上传证件，身份证，logo
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final List<String> mPicPaths = new ArrayList<String>();
                        mPicPaths.add(orgInfo.getOrg_certificate_img_url());
                        mPicPaths.add(orgInfo.getContact_id_img_url());
                        mPicPaths.add(orgInfo.getOrg_logo_url());


                    final ArrayList<String> ids = new ArrayList<>();
                    for (int i = 0; i < mPicPaths.size(); i++) {
                        final int ii = i;
                        File file = new File(mPicPaths.get(i));
                        if (!file.exists()){
                            if (ii==2){
                                request();
                            }
                            continue;
                        }
                        FileUploader.uploadFile(file, HttpConfig.getUrl(HttpConfig.UPLOAD_IMAGE_URL), null, new FileUploader.Callback() {
                            @Override
                            public void onSuccess(String data) {
                                Log.d("CHEN", "result --> " + data);
                                try {
//                                    JSONArray array = new JSONObject(data).getJSONArray("root");
//                                    ids.add(array.getString(0));
                                    JSONArray array = new JSONObject(data).getJSONArray("root");
                                    JSONObject object = array.getJSONObject(0);
                                    String id = object.getString("file_path");
                                    if (ii==0)
                                        orgInfo.setOrg_certificate_img_url(id);
                                    else if (ii==1)
                                        orgInfo.setContact_id_img_url(id);
                                    else
                                        orgInfo.setOrg_logo_url(id);
                                } catch (JSONException ignored) {
                                    ignored.printStackTrace();
                                }

                                if (ii == mPicPaths.size() - 1) {
                                    request();
//                                    String newIds = "";
//                                    for (int i = 0; i < ids.size(); i++) {
//                                        newIds = newIds + ids.get(i) + ",";
//                                    }
//                                    newIds = newIds.substring(0, newIds.length() - 1);
//                                    Log.d("CHEN", newIds);

//                                    OrganizationController.createOrganization(CreateOranizationActivity.this ,orgInfo.getOrg_id() , infoString , orgInfo.getOrg_certificate_img_url(), orgInfo.getContact_id_img_url(), orgInfo.getOrg_logo_url(),
//                                            new Listener<Boolean, OrganizationInfo>() {
//                                                @Override
//                                                public void onCallBack(Boolean status, OrganizationInfo reply) {
//                                                    dismissWaitDialog();
//                                                    if (status.booleanValue()) {
//                                                        if( mode == CREATE_MODE ){ //创建模式下
//                                                            QLToastUtils.showToast(CreateOranizationActivity.this, "创建成功");
//                                                            Intent intent = new Intent();
//                                                            intent.setClass(CreateOranizationActivity.this, HomeActivity.class);
//                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                                            startActivity(intent);
//                                                            finish();
//                                                        }else{
//                                                            orgPost_btn.setText("审核中...");
//                                                            setEditTextEnable(false);  //变为不可比编辑的状态
//                                                            orgPost_btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_btn_gray));
//                                                            viewGONE();
//                                                        }
//                                                    } else {
//                                                        if( mode == CREATE_MODE ){
//                                                            QLToastUtils.showToast(CreateOranizationActivity.this, "创建失败");
//                                                        }
//                                                    }
//                                                }
//                                            });
                                }
                            }

                            @Override
                            public void onFailed(Exception e) {
                                dismissWaitDialog();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        QLToastUtils.showToast(CreateOranizationActivity.this, "创建失败");
                                    }
                                });

                            }
                        });
                    }

                }
            }).start();

            // 证件，身份证，logo
//            OrganizationController.createOrganizationNew(this ,orgInfo.getOrg_id() , infoString , orgInfo.getOrg_certificate_img_url(), orgInfo.getContact_id_img_url(), orgInfo.getOrg_logo_url(),
//                    new Listener<Boolean, OrganizationInfo>() {
//                        @Override
//                        public void onCallBack(Boolean status, OrganizationInfo reply) {
//                            dismissWaitDialog();
//                            if (status.booleanValue()) {
//                                if( mode == CREATE_MODE ){ //创建模式下
//                                    QLToastUtils.showToast(CreateOranizationActivity.this, "创建成功");
//                                    Intent intent = new Intent();
//                                    intent.setClass(CreateOranizationActivity.this, HomeActivity.class);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                    startActivity(intent);
//                                    finish();
//                                }else{
//                                    orgPost_btn.setText("审核中...");
//                                    setEditTextEnable(false);  //变为不可比编辑的状态
//                                    orgPost_btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_btn_gray));
//                                    viewGONE();
//                                }
//                            } else {
//                                if( mode == CREATE_MODE ){
//                                    QLToastUtils.showToast(CreateOranizationActivity.this, "创建失败");
//                                }
//                            }
//                        }
//                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void request(){
        Gson gson = new Gson();
        final String infoString = gson.toJson(orgInfo);
        OrganizationController.createOrganization(CreateOranizationActivity.this ,orgInfo.getOrg_id() , infoString , orgInfo.getOrg_certificate_img_url(), orgInfo.getContact_id_img_url(), orgInfo.getOrg_logo_url(),
                new Listener<Boolean, OrganizationInfo>() {
                    @Override
                    public void onCallBack(Boolean status, OrganizationInfo reply) {
                        dismissWaitDialog();
                        if (status.booleanValue()) {
                            if( mode == CREATE_MODE ){ //创建模式下
                                QLToastUtils.showToast(CreateOranizationActivity.this, "创建成功");
                                Intent intent = new Intent();
                                intent.setClass(CreateOranizationActivity.this, HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }else{
                                orgPost_btn.setText("审核中...");
                                setEditTextEnable(false);  //变为不可比编辑的状态
                                orgPost_btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_btn_gray));
                                viewGONE();
                            }
                        } else {
                            if( mode == CREATE_MODE ){
                                QLToastUtils.showToast(CreateOranizationActivity.this, "创建失败");
                            }
                        }
                    }
                });
    }

    /**
     * @param imgview
     * @param type    1是logo，2是身份证，3是证书
     *                openImagePickerForImageViewCreateOranizationActivity
     */
    private void openImagePickerForImageView(final SimpleDraweeView imgview, final int type) {
        imgPicker.setOnReciveImageListener(new Listener<Void, String>() {
            @Override
            public void onCallBack(Void status, String reply) {
                 dismissWaitDialog();
                if (reply != null) {
//                    Bitmap btmBitmap = QLImageHelper.readBitmap(reply, imgview.getWidth() * 2, imgview.getHeight() * 2);
                    switch (type) {
                        case 1:
                            orgInfo.setOrg_logo_url(reply);
                            break;
                        case 2:
                            orgInfo.setContact_id_img_url(reply);
                            break;
                        case 3:
                            orgInfo.setOrg_certificate_img_url(reply);
                            break;
                    }
                    Glide.with(CreateOranizationActivity.this).load(reply).placeholder(R.drawable.add_icon).into(imgview);
//                    imgview.setImageBitmap(btmBitmap);
                } else {
                    imgview.setImageResource(R.drawable.add_icon);
                    // imgview.setTag(null);
                }
            }
        });
        imgPicker.setImageSourceType(ImagePickerHelper.kImageSource_UserSelect);  //让用户自己选择
        imgPicker.setReplaceContentLayout(true);
        imgPicker.openCamera();
        // showWaitDialog();
    }

    @Override
    public void onClick(View v) {
        int vid = v.getId();
        switch (vid) {
            case R.id.create_org_logo_iv:
                imgPicker.setOpenSysCrop(true);//打开系统裁剪功能
                imgPicker.setOnActActivityResultListener(new OnActActivityResultListener() {
                    @Override
                    public void onActivityResult(int requestCode, int resultCode, Intent data) {
                        showWaitDialog();
                    }
                });
                openImagePickerForImageView(orgLogo_iv, 1);
                break;
            case R.id.create_org_owner_id_iv:
                imgPicker.setOpenSysCrop(false);//打开系统裁剪功能
                imgPicker.setOnActActivityResultListener(new OnActActivityResultListener() {
                    @Override
                    public void onActivityResult(int requestCode, int resultCode, Intent data) {
                        showWaitDialog();
                    }
                });
                openImagePickerForImageView(orgOwner_iv, 2);
                break;
            case R.id.create_org_cer_iv:
                imgPicker.setOpenSysCrop(false);//打开系统裁剪功能
                imgPicker.setOnActActivityResultListener(new OnActActivityResultListener() {
                    @Override
                    public void onActivityResult(int requestCode, int resultCode, Intent data) {
                        showWaitDialog();
                    }
                });
                openImagePickerForImageView(orgCer_iv, 3);
                break;
            case R.id.create_org_post_btn:
//                if (mode == CREATE_MODE) {
//                    sendData();  //创建组织或是重新申请
//                }
                sendDataNew();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    /**
     * 获取组织信息
     */
    private void getOrgInfo() {
        showWaitDialog();
        OrganizationController.getOrganizationInfo(this, org_id, new Listener<Void, List<OrganizationInfo>>() {
            @Override
            public void onCallBack(Void status, List<OrganizationInfo> reply) {
                dismissWaitDialog();
                if (reply != null && reply.size() > 0) {
                    orgInfo = reply.get(0);  //获取对象
                    updataUI();   //更新UI数据
                    setErrormsg(orgInfo.getError_msg());//填写错误的信息
                }
            }
        });
    }

    private void deleteOrgInfo(){
        showWaitDialog();
        OrganizationController.deleteOrganizationInfo(this, org_id, new Listener<Boolean, String>() {
            @Override
            public void onCallBack(Boolean aBoolean, String reply) {
                dismissWaitDialog();
                QLToastUtils.showToast(CreateOranizationActivity.this,reply);
                if (aBoolean){
                    setResult(RESULT_OK);
                    finish();
                }

            }
        });
    }

    // 调用隐藏系统默认的输入法
    private void hideKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    // 显示样图
    private void showImageViewDiagrams(Drawable drawable) {
        View view = LayoutInflater.from(this).inflate(R.layout.alerdialog_imageview, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView_dialog);
        imageView.setBackgroundDrawable(drawable);
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setCancelable(true);
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.setView(view);
        alertDialog.setIcon(0);
        alertDialog.show();
    }
}
