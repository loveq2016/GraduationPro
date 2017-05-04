package app.logic.activity.user;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.OptionsPickerView.OnOptionsSelectListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.squareup.picasso.Picasso;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.QLConstant;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ql.activity.customtitle.ActActivity;
import org.ql.activity.customtitle.OnActActivityResultListener;
import org.ql.app.alert.AlertDialog;
import org.ql.utils.QLToastUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.config.DemoApplication;
import app.config.http.HttpConfig;
import app.logic.activity.ActTitleHandler;
import app.logic.activity.announce.AnnounceReportActivity;
import app.logic.activity.announce.FileUploader;
import app.logic.activity.friends.AddFriendsActivity;
import app.logic.activity.live.LiveBaseActivity;
import app.logic.activity.main.HomeActivity;
import app.logic.activity.main.UserCenterFragment;
import app.logic.controller.AnnounceController;
import app.logic.controller.OrganizationController;
import app.logic.controller.UserManagerController;
import app.logic.live.view.RoundAngleSimpleDraweeView;
import app.logic.pojo.OrganizationInfo;
import app.logic.pojo.UserInfo;
import app.logic.singleton.ZSZSingleton;
import app.utils.common.FrescoHelper;
import app.utils.common.FrescoImageShowThumb;
import app.utils.common.Listener;
import app.utils.file.YYFileManager;
import app.utils.helpers.ImagePickerHelper;
import app.utils.helpers.PropertySaveHelper;
import app.utils.helpers.QRHelper;
import app.utils.helpers.SharepreferencesUtils;
import app.view.DialogBottom;
import app.view.DialogNewStyleController;
import app.yy.geju.R;
import cn.jpush.android.api.JPushInterface;

/**
 * SiuJiYung create at 2016年6月29日 下午3:52:22
 */

public class UserInfoActivity extends ActActivity implements OnClickListener {
    public static final String kFROM_REGEDIT = "kFROM_REGEDIT";
    private ImageView userHeadImgView;
    private ImageView mWxStatus;
    private TextView nicknameTV;
    private TextView sexTV;
    private TextView regionTV;
    private boolean modifyProperty;
    private QRHelper qrHelper;
    private boolean fromRegeditActivity;
    private ImagePickerHelper pickerHelper;

    private OptionsPickerView cityPickerView,countryPickerView;
    private ArrayList<String> proviceList = new ArrayList<String>();
    private ArrayList<ArrayList<String>> citesList = new ArrayList<ArrayList<String>>();
//    private String provice_name, city_name;

    private TextView sexTv;

    private View mCountryView,mCompanyView,mAddressView,mDepartView,mTradeView,mBusinessView,mWebView;
    private TextView mCountry,mCompany,mAddress,mDepart,mTrade,mBusiness,mWeb;
    private SimpleDraweeView mCompanyLogo;

    private SharepreferencesUtils utils ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActTitleHandler handler = new ActTitleHandler();
        setAbsHandler(handler);
        setContentView(R.layout.activity_user_info2); //user_info_nickname_tv
        utils = new SharepreferencesUtils( this );
        handler.getRightLayout().setVisibility(View.INVISIBLE);
        handler.replaseLeftLayout(this, true);
        handler.getLeftLayout().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setTitle("");
        ((TextView) handler.getLeftLayout().findViewById(R.id.left_tv)).setText("个人资料");
        // 来自注册页面
        fromRegeditActivity = getIntent().getBooleanExtra(kFROM_REGEDIT, false);
        if (fromRegeditActivity) {
            handler.getLeftLayout().setVisibility(View.INVISIBLE);
        }
        userHeadImgView = (ImageView) findViewById(R.id.user_info_head_img);
        nicknameTV = (TextView) findViewById(R.id.user_info_nickname_tv);
        sexTV = (TextView) findViewById(R.id.user_info_sex_tv);
        regionTV = (TextView) findViewById(R.id.user_info_region_tv);
        // findViewById(R.id.user_info_modify_psw).setOnClickListener(this);
        // findViewById(R.id.user_info_review_qr_code).setOnClickListener(this);
        sexTv = (TextView) findViewById(R.id.user_info_sex_tv);
        findViewById(R.id.user_info_review_qr_code_rtl).setOnClickListener(this);
        findViewById(R.id.user_info_name_rtl).setOnClickListener(this);
        findViewById(R.id.user_info_sex_rtl).setOnClickListener(this);
        findViewById(R.id.user_info_region_rtl).setOnClickListener(this);
        findViewById(R.id.user_info_modify_psw_rtl).setOnClickListener(this);
        findViewById(R.id.user_info_about_me).setOnClickListener(this);
        findViewById(R.id.user_info_company_info).setOnClickListener(this);
        mWxStatus = (ImageView) findViewById(R.id.user_info_wx_bind_icon);
        mWxStatus.setOnClickListener(this);
        Button actionBtn = (Button) findViewById(R.id.user_info_btn_logout);
        if (fromRegeditActivity) {
            actionBtn.setBackgroundResource(R.drawable.enter_app);
            actionBtn.setText("进入组织");
        }
        actionBtn.setOnClickListener(this);
        userHeadImgView.setOnClickListener(this);
        nicknameTV.setOnClickListener(this);
        sexTV.setOnClickListener(this);
        regionTV.setOnClickListener(this);
        modifyProperty = false;
        qrHelper = new QRHelper();
//        getUserInfo();

        initCityPicker();
        initCompanyInfo();
        inithandler();
    }

    private void initCityPicker(){
        // 创建OptionsPickerView

        StringBuffer sBuffer = YYFileManager.readTextFileFromAssets(this, "province.json");
        if (sBuffer == null) {
            return;
        }
        Map<String, List<String>> proviceMap = ZSZSingleton.getProviceInfo(sBuffer.toString());
        for (String key : proviceMap.keySet()) {
            proviceList.add(key);
            List<String> valuesList = proviceMap.get(key);
            ArrayList<String> _cl = new ArrayList<String>();
            _cl.addAll(valuesList);
            citesList.add(_cl);
        }

        cityPickerView = new OptionsPickerView<String>(this);
        cityPickerView.setCancelable(true);

        cityPickerView.setPicker(proviceList, citesList, true);
        cityPickerView.setOnoptionsSelectListener(new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                final String provice_str = proviceList.get(options1);
                final String city_str = citesList.get(options1).get(option2);
//                provice_name = provice_str;
//                city_name = city_str;

                regionTV.post(new Runnable() {
                    @Override
                    public void run() {
                        regionTV.setText(provice_str + " " + city_str + "");
                        modifyProperty = true;
                        saveSettings();
                    }
                });
            }
        });

        countryPickerView = new OptionsPickerView<String>(this);
        countryPickerView.setCancelable(true);

        countryPickerView.setPicker(proviceList, citesList, true);
        countryPickerView.setOnoptionsSelectListener(new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                final String provice_str = proviceList.get(options1);
                final String city_str = citesList.get(options1).get(option2);
//                provice_name = provice_str;
//                city_name = city_str;

                mCountry.post(new Runnable() {
                    @Override
                    public void run() {
                        mCountry.setText(provice_str + " " + city_str + "");
                        modifyProperty = true;
                        saveSettings();
                    }
                });
            }
        });
    }

    private void initCompanyInfo(){
        mCountryView = findViewById(R.id.user_info_county);
        mCompanyView = findViewById(R.id.user_info_company_layout);
        mAddressView = findViewById(R.id.user_info_address_layout);
        mDepartView = findViewById(R.id.user_info_depart_layout);
        mTradeView = findViewById(R.id.user_info_trade_layout);
        mBusinessView = findViewById(R.id.user_info_business_layout);
        mWebView = findViewById(R.id.user_info_web_layout);

        mCountry = (TextView) findViewById(R.id.user_info_county_tv);
        mCompany = (TextView) findViewById(R.id.user_info_company_tv);
        mAddress = (TextView) findViewById(R.id.user_info_address_tv);
        mDepart = (TextView) findViewById(R.id.user_info_depart_tv);
        mTrade = (TextView) findViewById(R.id.user_info_trade_tv);
        mBusiness = (TextView) findViewById(R.id.user_info_business_tv);
        mWeb = (TextView) findViewById(R.id.user_info_web_tv);

        mCompanyLogo = (SimpleDraweeView)findViewById(R.id.user_info_company_img);
        mCountryView.setOnClickListener(this);
        mCompanyView.setOnClickListener(this);
        mAddressView.setOnClickListener(this);
        mDepartView.setOnClickListener(this);
        mTradeView.setOnClickListener(this);
        mBusinessView.setOnClickListener(this);
        mWebView.setOnClickListener(this);
        mCompanyLogo.setOnClickListener(this);
    }

    private void openHome() {
        // 第一次使用
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (fromRegeditActivity) {
            return;
        }
        super.onBackPressed();
    }

    private void forgetPsw() {
        Intent intent = new Intent();
        intent.setClass(this, ForgetpswActivity.class);
//        intent.putExtra(ForgetpswActivity.TITLE_STRING, "修改密码");
        intent.putExtra(ForgetpswActivity.FORGET_PSW, ForgetpswActivity.AMEND_PSW);
        startActivity(intent);
    }

    private void getUserInfo() {
        UserInfo info = UserManagerController.getCurrUserInfo();
        if (info == null) finish();
        UserManagerController.getUserInfo(this, info.getWp_member_info_id(), new Listener<Integer, UserInfo>() {
            @Override
            public void onCallBack(Integer status, UserInfo reply) {
                if (reply != null) {
                    PropertySaveHelper.getHelper().save(reply, UserManagerController.kUSER_INFO_KEY);
                    updateUI();
                }else{
                    QLToastUtils.showToast( UserInfoActivity.this , "信息获取失败，请重新加载");
                    finish();
                }
            }
        });
    }

    private void updateUI() {
        UserInfo info = UserManagerController.getCurrUserInfo();
        if (info == null) {
            return;
        }
        String imgUrl = HttpConfig.getUrl(info.getPicture_url());
        Picasso.with(this).load(Uri.parse(imgUrl)).placeholder(R.drawable.default_user_icon).fit().centerCrop().into(userHeadImgView);
        //FrescoHelper.asyncLoad(this ,Uri.parse(imgUrl), userHeadImgView);
        nicknameTV.setText(info.getNickName());
        sexTV.setText(info.getSex());
        regionTV.setText(info.getRegion());

        mCountry.setText(info.getNative_place());
        mCompany.setText(info.getCompany_name());
        mAddress.setText(info.getCompany_addr());
        mDepart.setText(info.getCompany_duty());
        mTrade.setText(info.getCompany_industry().replace(","," "));
        mBusiness.setText(info.getCompany_scope().replace(","," "));
        mWeb.setText(info.getCompany_url());
        FrescoImageShowThumb.showThrumb(Uri.parse(HttpConfig.getUrl(info.getCompany_logo())),mCompanyLogo);

        if (!TextUtils.isEmpty(info.getOpenid())){
            mWxStatus.setImageResource(R.drawable.open_icon);
        }else
            mWxStatus.setImageResource(R.drawable.close_icon);
    }

    // -------------------dialog--------------------------

    private void showEditBox(String title, String initValue, final int tvResId) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_edittext_view, null);
        final EditText eText = (EditText) contentView.findViewById(R.id.message_et);
        Button yesBtn = (Button) contentView.findViewById(R.id.yes_btn);
        Button noBtn = (Button) contentView.findViewById(R.id.no_btn);
        if (initValue == null) {
            eText.setHint("请输入");
        } else {
            eText.setText(initValue);
            eText.setSelection(eText.getText().length());
        }
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        final AlertDialog alertDialog = alertBuilder.create();
        alertDialog.setView(contentView);
        alertDialog.setCancelable(true);
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.setTitle("编辑" + title);
        alertDialog.setIcon(0);
        yesBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                String inputValue = eText.getText().toString();
                try {
                    if (inputValue.getBytes("UTF-8").length <= 12) {
                        modifyProperty = true;
                        ((TextView) UserInfoActivity.this.findViewById(tvResId)).setText(inputValue);
                        saveSettings();
                        alertDialog.dismiss();
                    } else {
                        QLToastUtils.showToast(UserInfoActivity.this, "昵称不能超过12个字符");
                    }
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

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

    private void showSexDialogBottom(final TextView textView) {

        View contentView = LayoutInflater.from(UserInfoActivity.this).inflate(R.layout.dialog_sex_dialogfragment, null);
        final DialogBottom dialogBottom = new DialogBottom(UserInfoActivity.this, contentView, R.style.sex_dialog);

        dialogBottom.show();
        contentView.findViewById(R.id.man_tv).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                textView.setText("男");
                modifyProperty = true;
                saveSettings();
                dialogBottom.dismiss();

            }
        });
        contentView.findViewById(R.id.woman_tv).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                textView.setText("女");
                modifyProperty = true;
                saveSettings();
                dialogBottom.dismiss();
            }
        });
        contentView.findViewById(R.id.cancel_tv).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialogBottom.dismiss();

            }
        });
    }

    /**
     * 新版本修改昵称
     *
     * @param view
     */
    private void showEditNickNameDialog(String titleString,final TextView view) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_new_edit_view, null);
        TextView title = (TextView) contentView.findViewById(R.id.dialog_title_tv);
        final EditText contentEdt = (EditText) contentView.findViewById(R.id.dialog_content_edt);
        Button trueBtn = (Button) contentView.findViewById(R.id.dialog_true_btn);
        Button cancelBtn = (Button) contentView.findViewById(R.id.dialog_cancel_btn);


        String contentString = view.getText().toString();
        contentEdt.setText(contentString);
        contentEdt.setSelection(contentString.length());
        title.setText(titleString);
        final DialogNewStyleController dialog = new DialogNewStyleController(this, contentView);
        dialog.show();

        trueBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = contentEdt.getText().toString();
                if (!TextUtils.isEmpty(msg)) {
                    if (view.getId() == R.id.user_info_nickname_tv && msg.length() > 8) {
                        QLToastUtils.showToast(UserInfoActivity.this, "长度不能超过8位");
                        return;
                    }
                    view.setText(msg);
                    modifyProperty = true;
                    saveSettings();
                    dialog.dismiss();
                } else {
                    QLToastUtils.showToast(UserInfoActivity.this, "不能为空");
                }


            }
        });
        cancelBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


    // -------------------dialog--------------------------
    private void saveSettings() {
        if (!modifyProperty) {
            return;
        }
        final UserInfo info = UserManagerController.getCurrUserInfo();
        String nickName = nicknameTV.getText().toString();
        String sex = sexTV.getText().toString();
        String area = regionTV.getText().toString();

        info.setName(nickName);
        info.setNickName(nickName);
        info.setSex(sex);
        info.setRegion(area);

        info.setNative_place(mCountry.getText().toString());
        info.setCompany_name(mCompany.getText().toString());
        info.setCompany_addr(mAddress.getText().toString());
        info.setCompany_duty(mDepart.getText().toString());
        info.setCompany_url(mWeb.getText().toString());

        PropertySaveHelper.getHelper().save(info, UserManagerController.kUSER_INFO_KEY);
        // UserManagerController.updateName(this, nickName, null);
        HashMap<String, String> propertys = new HashMap<String, String>();
        propertys.put("nickName", info.getNickName());
        propertys.put("realName", info.getName());
        propertys.put("sex", sex);
        propertys.put("location", area);
        propertys.put("native_place", info.getNative_place());
        propertys.put("company_name", info.getCompany_name());
        propertys.put("company_logo", info.getCompany_logo());
        propertys.put("company_addr", info.getCompany_addr());
        propertys.put("company_url", info.getCompany_url());
        propertys.put("company_duty", info.getCompany_duty());
        UserManagerController.updateUserInfo(this, propertys, new Listener<Integer, String>() {
            @Override
            public void onCallBack(Integer status, String reply) {
                if (status == -1) {
                    String msg = reply == null ? "操作失败" : reply;
                    QLToastUtils.showToast(UserInfoActivity.this, msg);
                }else{
                    LiveBaseActivity.urseName = info.getNickName() ;
                    FrescoImageShowThumb.showThrumb(Uri.parse(HttpConfig.getUrl(info.getCompany_logo())),mCompanyLogo);
                }
            }
        });
    }

    private void showQRCode() {
        UserInfo userInfo = UserManagerController.getCurrUserInfo();
        QRCodePersonal personalInfo = new QRCodePersonal();
        personalInfo.setPhone(userInfo.getPhone());
        personalInfo.setNickName(userInfo.getNickName());
        personalInfo.setPicture_url(userInfo.getPicture_url());
        personalInfo.setLocation(userInfo.getLocation());
        personalInfo.setWp_member_info_id(userInfo.getWp_member_info_id());
        Gson gson = new Gson();
        String usrInfoJson = gson.toJson( personalInfo );  //String usrInfoJson = gson.toJson(userInfo);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int imgWidth = (int) dm.scaledDensity * 200;
        int imgHeight = (int) dm.scaledDensity * 200;
        Bitmap qriBitmap = qrHelper.createQRImage(usrInfoJson, imgWidth, imgHeight);
        String userPic = HttpConfig.getUrl(userInfo.getPicture_url());
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
        LayoutParams p2 = new LayoutParams((int) (d.getWidth() * 0.70), (int) (d.getHeight() * 0.30));
        View dialog_view = getLayoutInflater().inflate(R.layout.dialog_qrode, null);
        ImageView qrcode = (ImageView) dialog_view.findViewById(R.id.qrcode);
        SimpleDraweeView pic = (SimpleDraweeView) dialog_view.findViewById(R.id.im_personpic);
        TextView nick = (TextView) dialog_view.findViewById(R.id.tx_name);
        TextView phone = (TextView) dialog_view.findViewById(R.id.tx_phone);
        // Picasso.with(this).load(Uri.parse(userPic)).placeholder(R.drawable.default_user_icon).fit().centerCrop().into(pic);
        FrescoImageShowThumb.showThrumb(Uri.parse(userPic),pic);
        //FrescoHelper.asyncLoad(this ,Uri.parse(userPic), pic);
        nick.setText(userInfo.getNickName());
        phone.setText("格局号：" + userInfo.getPhone());
        String decodeString = null;
        try {
            decodeString = new String(usrInfoJson.getBytes(), "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            decodeString = usrInfoJson;
        }
        qrcode.setImageBitmap(showHeaderQRcodeBitmap(userHeadImgView, decodeString, imgWidth, imgHeight));
        Dialog qrDialog = new Dialog(this, R.style.dialog);
        qrDialog.setContentView(dialog_view, p2);
        qrDialog.show();
    }

    // 显示装有图片的二维码
    private Bitmap showHeaderQRcodeBitmap(ImageView imageView, String text, int imgWidth, int imgHeight) {
        Bitmap bitmap =null ;
        try {
            bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        }catch (Exception e){
            bitmap = BitmapFactory.decodeResource( this.getResources() , R.drawable.default_user_icon);
        }
        // 图片宽度的一半
        //int IMAGE_HALFWIDTH = 30;
        DemoApplication.QRInsideImg = 30 ;
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

    private void openCityPicker() {
        if (cityPickerView == null || cityPickerView.isShowing()) {
            return;
        }
        cityPickerView.show();
    }

    private void openCountryPicker() {
        if (countryPickerView == null || countryPickerView.isShowing()) {
            return;
        }
        countryPickerView.show();
    }

    @Override
    public void onClick(View v) {
        TextView textView = null;
        switch (v.getId()) {
            case R.id.user_info_btn_logout:
                if (fromRegeditActivity) {
                    openHome();
                    return;
                }
                new Thread(new Runnable() {
                    public void run() {
                        EMClient.getInstance().logout(true);       //退出环信
                    }
                }).start();

                JPushInterface.setAlias(this, "" , null);  //设置极光推送的别名（设为""）
                utils.setNeedLogin( true );                //在自己点击退出登录的情况下，下次进入应用需要点击登录按钮
                // 退出登录
                final Intent loginIntent = new Intent();
                loginIntent.setClass(this, PrepareLoginActivity.class);
                loginIntent.putExtra("ExitActivity", "ExitActivity");
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(loginIntent);
                if(!HomeActivity.act.isFinishing()){
                    HomeActivity.act.finish();
                }
                finish();
                break;
            case R.id.user_info_review_qr_code_rtl: // 二维码
                showQRCode();
                break;
            case R.id.user_info_head_img: // 头像
                pickerHelper = ImagePickerHelper.createNewImagePickerHelper(UserInfoActivity.this);
                pickerHelper.setImageSourceType(ImagePickerHelper.kImageSource_UserSelect);
                pickerHelper.setOnReciveImageListener(new Listener<Void, String>() {
                    @Override
                    public void onCallBack(Void status, String reply) {
                        if (reply != null && !TextUtils.isEmpty(reply)) {
                            File imgFile = new File(reply);
                            Picasso.with(UserInfoActivity.this).load(imgFile).fit().centerCrop().into(userHeadImgView);
                            UserManagerController.uploadUserHeadImage(UserInfoActivity.this, reply, new Listener<Integer, String>() {
                                @Override
                                public void onCallBack(Integer status, String reply) {
                                    dismissWaitDialog();
                                    // getUserInfo();
                                }
                            });
                        }
                    }
                });
                pickerHelper.setOnActActivityResultListener(new OnActActivityResultListener() {
                    @Override
                    public void onActivityResult(int requestCode, int resultCode, Intent data) {
                        showWaitDialog();

                    }
                });
                pickerHelper.setReplaceContentLayout(true);
                pickerHelper.setOpenSysCrop(true);
                pickerHelper.openCamera();
                break;
            case R.id.user_info_name_rtl: // 昵称
                textView = (TextView) findViewById(R.id.user_info_nickname_tv);
                showEditNickNameDialog("编辑昵称",textView);
                break;
            case R.id.user_info_nickname_tv:
                textView = (TextView) findViewById(R.id.user_info_nickname_tv);
                showEditNickNameDialog("编辑昵称",textView);
                break;
            case R.id.user_info_sex_rtl: // 性别
                showSexDialogBottom(sexTv);
                break;
            case R.id.user_info_sex_tv:
                showSexDialogBottom(sexTv);
                break;
            case R.id.user_info_region_rtl: // 地区
                openCityPicker();
                break;
            case R.id.user_info_region_tv:
                textView = (TextView) findViewById(R.id.user_info_region_tv);
                openCityPicker();
                break;
            case R.id.user_info_modify_psw_rtl: // 修改密码
                UserInfo userInfo = UserManagerController.getCurrUserInfo();
                if (userInfo !=null){
                    if (TextUtils.isEmpty(userInfo.getPhone())){
                        showTipsDialog(0);
                    }else
                        forgetPsw();
                }
                break;
            case R.id.user_info_wx_bind_icon: // 微信绑定
                UserInfo userInfo1 = UserManagerController.getCurrUserInfo();
                if (userInfo1 !=null){
                    if (TextUtils.isEmpty(userInfo1.getOpenid())){
                        IWXAPI api = WXAPIFactory.createWXAPI(this, DemoApplication.WEIXN_APP_ID , true);
                        SendAuth.Req req = new SendAuth.Req();
                        req.scope = "snsapi_userinfo";
                        req.state = "wechat_sdk_userinfoactivity";
                        //发送授权登陆请求
                        api.sendReq(req);
                    }else{
                        if (TextUtils.isEmpty(userInfo1.getPhone())){
                            showTipsDialog(1);
                        }else{
                            showTipsDialog(2);
                        }

                    }

                }
                break;
            case R.id.user_info_company_img:
                pickerHelper = ImagePickerHelper.createNewImagePickerHelper(UserInfoActivity.this);
                pickerHelper.setImageSourceType(ImagePickerHelper.kImageSource_UserSelect);
                pickerHelper.setOnReciveImageListener(new Listener<Void, String>() {
                    @Override
                    public void onCallBack(Void status, String reply) {
                        if (reply != null && !TextUtils.isEmpty(reply)) {
                            final File imgFile = new File(reply);
//                            Picasso.with(UserInfoActivity.this).load(imgFile).fit().centerCrop().into(mCompanyLogo);
                            FrescoImageShowThumb.showThrumb(Uri.parse("file://"+reply),mCompanyLogo);
                            showWaitDialog();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    uploadImage(imgFile);
                                }
                            }).start();

                        }
                    }
                });
                pickerHelper.setOnActActivityResultListener(new OnActActivityResultListener() {
                    @Override
                    public void onActivityResult(int requestCode, int resultCode, Intent data) {
                        showWaitDialog();

                    }
                });
                pickerHelper.setReplaceContentLayout(true);
                pickerHelper.setOpenSysCrop(true);
                pickerHelper.openCamera();
                break;
            case R.id.user_info_county:
                openCountryPicker();
                break;
            case R.id.user_info_company_layout:
                showEditNickNameDialog("公司单位",mCompany);
                break;
            case R.id.user_info_address_layout:
                showEditNickNameDialog("办公地址",mAddress);
                break;
            case R.id.user_info_depart_layout:
                showEditNickNameDialog("职务",mDepart);
                break;
            case R.id.user_info_trade_layout:
//                showEditNickNameDialog("行业范围",mTrade);
                UserInfo userInfo2 = UserManagerController.getCurrUserInfo();
                startActivity(new Intent(this,CompanyTradeActivity.class).putExtra(CompanyTradeActivity.EXTRA_TYPE,0).putExtra("INIT_VALUE",userInfo2.getCompany_industry_id()));
                break;
            case R.id.user_info_business_layout:
//                showEditNickNameDialog("业务范围",mBusiness);
                UserInfo userInfo3 = UserManagerController.getCurrUserInfo();
                startActivity(new Intent(this,CompanyTradeActivity.class).putExtra(CompanyTradeActivity.EXTRA_TYPE,1).putExtra("INIT_VALUE",userInfo3.getCompany_scope_id()));
                break;
            case R.id.user_info_web_layout:
                showEditNickNameDialog("网址",mWeb);
                break;
            case R.id.user_info_company_info:
                UserInfo userInfo4 = UserManagerController.getCurrUserInfo();
                startActivity(new Intent(this,CompanyInfoActivity.class).putExtra(CompanyInfoActivity.EXTRA_TEXT, userInfo4.getCompany_intro()));
                break;
        }
    }

    private void uploadImage(File file){
        FileUploader.uploadFile(file, HttpConfig.getUrl(HttpConfig.UPLOAD_IMAGE_URL), null, new FileUploader.Callback() {
            @Override
            public void onSuccess(String data) {
                dismissWaitDialog();
                Log.d("CHEN", "result --> " + data);
                try {
                    JSONArray array = new JSONObject(data).getJSONArray("root");
                    JSONObject object = array.getJSONObject(0);
                    String id = object.getString("file_path");
                    UserInfo userInfo = UserManagerController.getCurrUserInfo();
                    userInfo.setCompany_logo(id);
                    PropertySaveHelper.getHelper().save(userInfo, UserManagerController.kUSER_INFO_KEY);
                    modifyProperty = true;
                    saveSettings();
                } catch (JSONException ignored) {
                    ignored.printStackTrace();
                }


            }

            @Override
            public void onFailed(Exception e) {

                dismissWaitDialog();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        QLToastUtils.showToast(UserInfoActivity.this, "修改失败");
                    }
                });

            }
        });
    }

    public static Handler wxHandler;
    private void inithandler(){
        wxHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                UserManagerController.buidingUser(UserInfoActivity.this, QLConstant.client_id, msg.obj.toString(), new Listener<Boolean, String>() {
                    @Override
                    public void onCallBack(Boolean aBoolean, String reply) {
                        if (aBoolean){
                            mWxStatus.setImageResource(R.drawable.open_icon);
                            getUserInfo();
                            Toast.makeText(UserInfoActivity.this,"绑定成功",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(UserInfoActivity.this,TextUtils.isEmpty(reply)?"绑定失败":reply,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        };
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        getUserInfo();
    }

    private DialogNewStyleController dialog;

    /**
     *
     * @param dialogType 0修改密码 1微信绑定 2解除微信绑定
     */
    private void showTipsDialog(final int dialogType) {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_content_tips, null);
        dialog = new DialogNewStyleController(this, view);
        TextView titleTv = (TextView) view.findViewById(R.id.dialog_title_tv);
        TextView nameTv = (TextView) view.findViewById(R.id.dialog_content_tv);
        Button sendBtn = (Button) view.findViewById(R.id.dialog_cancel_btn);
        Button cancel = (Button) view.findViewById(R.id.dialog_true_btn);
        if (dialogType == 1) {
            titleTv.setText("解除微信绑定");
            nameTv.setText("系统检测到您尚未绑定手机\n无法解绑！");
            cancel.setText("绑定手机");
        }else if (dialogType == 0){
            titleTv.setText("修改密码");
            nameTv.setText("系统检测到您尚未绑定手机\n无法修改密码！");
            cancel.setText("绑定手机");
        }else{
            titleTv.setText("解除微信绑定");
            nameTv.setText("你确定要解除绑定？");
            cancel.setText("确定");
        }

        sendBtn.setText("取消");
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogType == 2){
                    showWaitDialog();
                    UserManagerController.unbuidWx(UserInfoActivity.this, new Listener<Boolean, String>() {
                        @Override
                        public void onCallBack(Boolean aBoolean, String reply) {
                            dismissWaitDialog();
                            if (aBoolean){
                                mWxStatus.setImageResource(R.drawable.close_icon);
                                getUserInfo();
                                Toast.makeText(UserInfoActivity.this,"解绑成功",Toast.LENGTH_SHORT).show();
                            }else
                                Toast.makeText(UserInfoActivity.this,TextUtils.isEmpty(reply)?"解绑失败":reply,Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Intent intent1 = new Intent();
                    intent1.setClass(UserInfoActivity.this, BindingPhoneActivity.class);
                    startActivity(intent1);

                }
                dialog.dismiss();

            }
        });
        sendBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
