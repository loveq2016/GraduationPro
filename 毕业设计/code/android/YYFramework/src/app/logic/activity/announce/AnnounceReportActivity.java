package app.logic.activity.announce;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ql.activity.customtitle.ActActivity;
import org.ql.activity.customtitle.OnActActivityResultListener;
import org.ql.utils.QLToastUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import app.config.http.HttpConfig;
import app.logic.activity.ActTitleHandler;
import app.logic.activity.main.HomeActivity;
import app.logic.activity.notice.DefaultNoticeActivity;
import app.logic.controller.AnnounceController;
import app.logic.pojo.OrganizationInfo;
import app.utils.common.Listener;
import app.utils.helpers.ImagePickerHelper;
import app.view.DialogBottom;
import app.yy.geju.R;

/*
 * GZYY    2016-8-3  上午10:14:03
 */

public class AnnounceReportActivity extends ActActivity implements OnClickListener, AdapterView.OnItemClickListener ,View.OnTouchListener{

    private ImagePickerHelper pickerHelper;
    private ActTitleHandler handler;
    private EditText  text_et;    //公告标题和内容
    private ArrayList<String> mPicPaths;
    private GridView mImagePickGridView;
    private AddImagesGridAdpter mImagePickAdapter;
    private String notice_id;
    private TextView create_count_editSize,create_count_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new ActTitleHandler();
        setAbsHandler(handler);
        setContentView(R.layout.activity_announce_report);
        //初始化TooBar
        initActHandler();
        //初始化View
        initView();
        //初始化对话框
        //initDialog();
    }

    /**
     * 初始化TootBar
     */
    private void initActHandler() {
        //获取Intent对象
        notice_id = getIntent().getStringExtra("notice_id");

        setTitle("");
        handler.getRightDefButton().setText("提交");
        handler.getRightDefButton().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                reportNotice();
            }
        });
        handler.addLeftView(LayoutInflater.from(this).inflate(R.layout.title_leftview_layout, null), true);
        handler.getCenterLayout().findViewById(android.R.id.title).setOnClickListener(this);
        handler.getCenterLayout().findViewById(R.id.centerIv).setVisibility(View.GONE);
        TextView tv = (TextView) handler.getLeftLayout().findViewById(R.id.left_tv);
        tv.setText("举报");
        handler.getLeftLayout().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 初始化View
     */
    private void initView() {
        text_et = (EditText) findViewById(R.id.text_et);   //公告内容
        create_count_image = (TextView) findViewById(R.id.create_count_image);
        create_count_editSize = (TextView) findViewById(R.id.create_count_editSize);
        mImagePickGridView = (GridView) findViewById(R.id.grid_view_image_pick);
        mPicPaths = new ArrayList<>();
        mImagePickAdapter = new AddImagesGridAdpter(mPicPaths, this, mImagePickGridView,4);
        mImagePickGridView.setOnItemClickListener(this);
        mImagePickGridView.setAdapter(mImagePickAdapter);
        mImagePickAdapter.fixGridViewHeight(mImagePickGridView);
        text_et.setOnTouchListener( this );

        text_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s.toString())) {
                    create_count_editSize.setText("0/200");
                } else {
                    if (s.toString().length() > 200) {
                        text_et.setText(s.subSequence(0, 200));
                        text_et.setSelection( 200 );//光标的位置
                        create_count_editSize.setText("200/200");
                    } else {
                        create_count_editSize.setText(s.toString().length() + "/200");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    private void reportNotice(){
        final String msg_content = text_et.getText().toString();
        if (msg_content.equals("")) {
            QLToastUtils.showToast(this,"请输入举报内容");
            return;
        }
        showWaitDialog();
        if (mPicPaths == null || mPicPaths.size()==0 || (mPicPaths.size() == 1 && mPicPaths.get(0).equals(""))){
            AnnounceController.addMsgExtentionInfo(this, msg_content, 2, null, notice_id, new Listener<Boolean, String>() {
                @Override
                public void onCallBack(Boolean aBoolean, String reply) {
                    dismissWaitDialog();
                    QLToastUtils.showToast(AnnounceReportActivity.this,reply);
                    if (aBoolean){
                        finish();
                    }
                }
            });
        }else{
            // 去掉最后的空串
            if(mPicPaths.size()>0 && mPicPaths.get(mPicPaths.size()-1).equals("")){
                mPicPaths.remove(mPicPaths.size() - 1);
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    final ArrayList<String> ids = new ArrayList<>();
                    for (int i = 0; i < mPicPaths.size(); i++) {
                        final int ii = i;
                        FileUploader.uploadFile(new File(mPicPaths.get(i)), HttpConfig.getUrl(HttpConfig.UPLOAD_IMAGE_URL), null, new FileUploader.Callback() {
                            @Override
                            public void onSuccess(String data) {
                                Log.d("CHEN", "result --> " + data);
                                try {
                                    JSONArray array = new JSONObject(data).getJSONArray("root");
                                    ids.add(array.getString(0));
                                } catch (JSONException ignored) {
                                    ignored.printStackTrace();
                                }

                                if (ii == mPicPaths.size() - 1) {
                                    String newIds = "";
                                    for (int i = 0; i < ids.size(); i++) {
                                        newIds = newIds + ids.get(i) + ",";
                                    }
                                    newIds = newIds.substring(0, newIds.length() - 1);
                                    Log.d("CHEN", newIds);

                                    AnnounceController.addMsgExtentionInfo(AnnounceReportActivity.this, msg_content, 2, newIds, notice_id, new Listener<Boolean, String>() {
                                        @Override
                                        public void onCallBack(Boolean aBoolean, String reply) {
                                            dismissWaitDialog();
                                            QLToastUtils.showToast(AnnounceReportActivity.this,reply);
                                            if (aBoolean){
                                                finish();
                                            }
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onFailed(Exception e) {
                                if (mPicPaths != null && !mPicPaths.get(mPicPaths.size()-1).equals("")){
                                    mPicPaths.add("");
                                }
                                dismissWaitDialog();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        QLToastUtils.showToast(AnnounceReportActivity.this, "发送失败");
                                    }
                                });

                            }
                        });
                    }
                }
            }).start();
        }



    }

    //打开相机或相册
    private void open() {
        pickerHelper = ImagePickerHelper.createNewImagePickerHelper(this);//获取实例对象
        pickerHelper.setImageSourceType(ImagePickerHelper.kImageSource_Atlas);
        pickerHelper.setOnReciveImageListener(new Listener<Void, String>() {
            @Override
            public void onCallBack(Void status, String reply) {
                //关闭对话框
                dismissWaitDialog();
                if (!TextUtils.isEmpty(reply)) {
                    if (mPicPaths.size() > 0 && mPicPaths.get(mPicPaths.size() - 1).equals("")) {
                        mPicPaths.remove(mPicPaths.size() - 1);
                    }
                    mImagePickAdapter.add(reply);
                    create_count_image.setText((mPicPaths.size()-1)+"/4");
                }
            }
        });
        pickerHelper.setOnActActivityResultListener(new OnActActivityResultListener() {
            @Override
            public void onActivityResult(int requestCode, int resultCode, Intent data) {
                showWaitDialog();
            }
        });
        pickerHelper.setCropStyle(1, 1, 1800, 1800);
        pickerHelper.setOpenSysCrop(false); //打开系统裁剪功能
        pickerHelper.setReplaceContentLayout(true);
        pickerHelper.openCamera();//打开相机del
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == mPicPaths.size() - 1) {
            open();
        } else {
            Intent intent = new Intent(this, ShowBigImageActivity.class);
            intent.putExtra(ShowBigImageActivity.KEY_PIC_LOCAL_PATH, mPicPaths.get(position));
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {

    }


    /**
     * EditText竖直方向是否可以滚动
     * @param editText  需要判断的EditText
     * @return  true：可以滚动   false：不可以滚动
     */
    private boolean canVerticalScroll(EditText editText) {
        //滚动的距离
        int scrollY = editText.getScrollY();
        //控件内容的总高度
        int scrollRange = editText.getLayout().getHeight();
        //控件实际显示的高度
        int scrollExtent = editText.getHeight() - editText.getCompoundPaddingTop() -editText.getCompoundPaddingBottom();
        //控件内容总高度与实际显示高度的差值
        int scrollDifference = scrollRange - scrollExtent;
        if(scrollDifference == 0) {
            return false;
        }
        return (scrollY > 0) || (scrollY < scrollDifference - 1);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        //触摸的是EditText并且当前EditText可以滚动则将事件交给EditText处理；否则将事件交由其父类处理
        if (view.getId() == R.id.text_et && canVerticalScroll(text_et)) {
            view.getParent().requestDisallowInterceptTouchEvent(true);
            if (event.getAction() == MotionEvent.ACTION_UP) {
                view.getParent().requestDisallowInterceptTouchEvent(false);
            }
        }
        return false;
    }
}
