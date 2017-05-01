package app.logic.activity.user;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
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

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.OptionsPickerView.OnOptionsSelectListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.squareup.picasso.Picasso;

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
import app.logic.activity.live.LiveBaseActivity;
import app.logic.activity.main.HomeActivity;
import app.logic.controller.UserManagerController;
import app.logic.live.view.RoundAngleSimpleDraweeView;
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
    private TextView nicknameTV;
    private TextView sexTV;
    private TextView regionTV;
    private boolean modifyProperty;
    private QRHelper qrHelper;
    private boolean fromRegeditActivity;
    private ImagePickerHelper pickerHelper;

    private OptionsPickerView cityPickerView;
    private ArrayList<String> proviceList = new ArrayList<String>();
    private ArrayList<ArrayList<String>> citesList = new ArrayList<ArrayList<String>>();
    private String provice_name, city_name;

    private TextView sexTv;

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
        getUserInfo();
        // 创建OptionsPickerView

        StringBuffer sBuffer = YYFileManager.readTextFileFromAssets(this, "province.json");
        if (sBuffer == null) {
            return;
        }
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
    private void showEditNickNameDialog(final TextView view) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_new_edit_view, null);
        TextView title = (TextView) contentView.findViewById(R.id.dialog_title_tv);
        final EditText contentEdt = (EditText) contentView.findViewById(R.id.dialog_content_edt);
        Button trueBtn = (Button) contentView.findViewById(R.id.dialog_true_btn);
        Button cancelBtn = (Button) contentView.findViewById(R.id.dialog_cancel_btn);


        String contentString = view.getText().toString();
        contentEdt.setText(contentString);
        contentEdt.setSelection(contentString.length());
        title.setText("编辑昵称");
        final DialogNewStyleController dialog = new DialogNewStyleController(this, contentView);
        dialog.show();

        trueBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = contentEdt.getText().toString();
                if (!TextUtils.isEmpty(msg)) {
                    if (msg.length() > 8) {
                        QLToastUtils.showToast(UserInfoActivity.this, "长度不能超过8位");
                        return;
                    }
                    view.setText(msg);
                    modifyProperty = true;
                    saveSettings();
                    dialog.dismiss();
                } else {
                    QLToastUtils.showToast(UserInfoActivity.this, "昵称不能为空");
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
        PropertySaveHelper.getHelper().save(info, UserManagerController.kUSER_INFO_KEY);
        // UserManagerController.updateName(this, nickName, null);
        HashMap<String, String> propertys = new HashMap<String, String>();
        propertys.put("nickName", info.getNickName());
        propertys.put("realName", info.getName());
        propertys.put("sex", sex);
        propertys.put("location", area);
        UserManagerController.updateUserInfo(this, propertys, new Listener<Integer, String>() {
            @Override
            public void onCallBack(Integer status, String reply) {
                if (status == -1) {
                    String msg = reply == null ? "操作失败" : reply;
                    QLToastUtils.showToast(UserInfoActivity.this, msg);
                }else{
                    LiveBaseActivity.urseName = info.getNickName() ;
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
            bitmap = BitmapFactory.decodeResource( this.getResources() , R.drawable.ic_launcher);
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
                loginIntent.setClass(this, LoginActivity.class);
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
                showEditNickNameDialog(textView);
                break;
            case R.id.user_info_nickname_tv:
                textView = (TextView) findViewById(R.id.user_info_nickname_tv);
                showEditNickNameDialog(textView);
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
                forgetPsw();
                break;
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }
}
