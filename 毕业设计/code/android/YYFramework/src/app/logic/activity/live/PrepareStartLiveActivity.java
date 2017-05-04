package app.logic.activity.live;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.squareup.picasso.Picasso;

import org.QLConstant;
import org.canson.view.VerificationButton.Verificationbutton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ql.activity.customtitle.ActActivity;
import org.ql.activity.customtitle.OnActActivityResultListener;
import org.ql.utils.QLToastUtils;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import app.config.http.HttpConfig;
import app.logic.activity.ActTitleHandler;
import app.logic.activity.announce.AnnounceActivity;
import app.logic.activity.announce.FileUploader;
import app.logic.activity.main.HomeActivity;
import app.logic.activity.user.LoginActivity;
import app.logic.activity.user.UserInfoActivity;
import app.logic.controller.AnnounceController;
import app.logic.controller.LivestreamController;
import app.logic.controller.UserManagerController;
import app.logic.pojo.UserInfo;
import app.utils.common.FrescoImageShowThumb;
import app.utils.common.Listener;
import app.utils.helpers.ImagePickerHelper;
import app.utils.helpers.SharepreferencesUtils;
import app.utils.helpers.YYUtils;
import app.yy.geju.R;
import cn.jpush.android.api.JPushInterface;

public class PrepareStartLiveActivity extends ActActivity implements OnClickListener {

    public static final String LIVE_ID = "LIVE_ID";
    private ActTitleHandler titleHandler = new ActTitleHandler();
    private SimpleDraweeView mCover;
    private EditText liveTitle;

    private String liveId,orgId,orgName,orgBuilderName,orgLogo;
    private File imgFile;
    private ImagePickerHelper pickerHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbsHandler(titleHandler);
        setContentView(R.layout.activity_prepare_start_live);
        titleHandler.addLeftView(LayoutInflater.from(this).inflate(R.layout.title_leftview_layout, null), true);
        titleHandler.getCenterLayout().setBackgroundResource(R.drawable.whit);
        titleHandler.getLeftLayout().setVisibility(View.INVISIBLE);
        titleHandler.getRightDefButton().setOnClickListener(null);
        setTitle("");
        initView();

    }

    /**
     * 出是化View
     */
    private void initView() {
        liveId = getIntent().getStringExtra(PrepareStartLiveActivity.LIVE_ID);
        orgId = getIntent().getStringExtra(StartLiveActivity.ORG_ID);
        orgName = getIntent().getStringExtra(StartLiveActivity.ORG_NAME) ;
        orgBuilderName = getIntent().getStringExtra( StartLiveActivity.ORG_BUIDER_NAME ) ;
        orgLogo = getIntent().getStringExtra( StartLiveActivity.ORG_LOGO_URL);
        mCover = (SimpleDraweeView) findViewById(R.id.live_cover);
        liveTitle = (EditText) findViewById(R.id.live_title);

        mCover.setOnClickListener(this);
        findViewById(R.id.start_btn).setOnClickListener(this);
        findViewById(R.id.cancel_btn).setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.live_cover:
                pickerHelper = ImagePickerHelper.createNewImagePickerHelper(PrepareStartLiveActivity.this);
                pickerHelper.setImageSourceType(ImagePickerHelper.kImageSource_UserSelect);
                pickerHelper.setOnReciveImageListener(new Listener<Void, String>() {
                    @Override
                    public void onCallBack(Void status, String reply) {
                        dismissWaitDialog();
                        if (reply != null && !TextUtils.isEmpty(reply)) {
                            imgFile = new File(reply);
//                            Picasso.with(PrepareStartLiveActivity.this).load(imgFile).fit().centerCrop().into(mCover);
                            FrescoImageShowThumb.showThrumb(Uri.parse("file://"+reply),mCover);
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
            case R.id.cancel_btn:
                finish();
                break;
            case R.id.start_btn:
                showWaitDialog();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        uploadFile();
                    }
                }).start();

                break;
        }
    }

    private void uploadFile(){
        if (imgFile !=null){

            FileUploader.uploadFile(imgFile, HttpConfig.getUrl(HttpConfig.UPLOAD_IMAGE_URL), null, new FileUploader.Callback() {
                @Override
                public void onSuccess(String data) {
                    try {
                        JSONArray array = new JSONObject(data).getJSONArray("root");
                        JSONObject object = array.getJSONObject(0);
                        String id = object.getString("file_path");
                        LivestreamController.setLiveInfo(PrepareStartLiveActivity.this, liveId, id, liveTitle.getText().toString(), new Listener<Boolean, String>() {
                            @Override
                            public void onCallBack(Boolean aBoolean, String reply) {
                                dismissWaitDialog();
                                if (aBoolean){
                                    Intent intent = new Intent();
                                    intent.putExtra(StartLiveActivity.ORG_ID , orgId);
                                    intent.putExtra(StartLiveActivity.ORG_NAME, orgName);
                                    intent.putExtra(StartLiveActivity.ORG_BUIDER_NAME ,orgBuilderName);
                                    intent.putExtra(StartLiveActivity.ORG_LOGO_URL ,orgLogo);
                                    intent.putExtra(PrepareStartLiveActivity.LIVE_ID ,liveId);
                                    intent.setClass( PrepareStartLiveActivity.this , StartLiveActivity.class );
                                    startActivity( intent );
                                    finish();
                                }else{
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            QLToastUtils.showToast(PrepareStartLiveActivity.this, "创建直播失败");
                                        }
                                    });

                                }
                            }
                        });
                    } catch (JSONException ignored) {
                        ignored.printStackTrace();
                        dismissWaitDialog();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                QLToastUtils.showToast(PrepareStartLiveActivity.this, "创建直播失败");
                            }
                        });
                    }
                }

                @Override
                public void onFailed(Exception e) {
                    dismissWaitDialog();
                    QLToastUtils.showToast(PrepareStartLiveActivity.this, "创建直播失败");
                }
            });
        }else{
            LivestreamController.setLiveInfo(PrepareStartLiveActivity.this, liveId, "", liveTitle.getText().toString(), new Listener<Boolean, String>() {
                @Override
                public void onCallBack(Boolean aBoolean, String reply) {
                    dismissWaitDialog();
                    if (aBoolean){
                        Intent intent = new Intent();
                        intent.putExtra(StartLiveActivity.ORG_ID , orgId);
                        intent.putExtra(StartLiveActivity.ORG_NAME, orgName);
                        intent.putExtra(StartLiveActivity.ORG_BUIDER_NAME ,orgBuilderName);
                        intent.putExtra(StartLiveActivity.ORG_LOGO_URL ,orgLogo);
                        intent.putExtra(PrepareStartLiveActivity.LIVE_ID ,liveId);
                        intent.setClass( PrepareStartLiveActivity.this , StartLiveActivity.class );
                        startActivity( intent );
                        finish();
                    }else{
                        QLToastUtils.showToast(PrepareStartLiveActivity.this, "创建直播失败");
                    }
                }
            });
        }
    }
}
