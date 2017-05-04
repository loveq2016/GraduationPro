package app.logic.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import app.config.http.HttpConfig;
import app.logic.activity.ActTitleHandler;
import app.logic.activity.announce.AddImagesGridAdpter;
import app.logic.activity.announce.FileUploader;
import app.logic.activity.announce.ShowBigImageActivity;
import app.logic.controller.AnnounceController;
import app.logic.controller.UserManagerController;
import app.utils.common.Listener;
import app.utils.helpers.ImagePickerHelper;
import app.yy.geju.R;

/*
 * GZYY    2016-8-3  上午10:14:03
 */

public class CompanyInfoActivity extends ActActivity implements View.OnTouchListener{

    public static final String EXTRA_TEXT = "EXTRA_TEXT";
    private ActTitleHandler handler;
    private EditText  text_et;    //公告标题和内容
    private TextView create_count_editSize;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new ActTitleHandler();
        setAbsHandler(handler);
        setContentView(R.layout.activity_company_info);
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

        setTitle("公司简介");
        handler.getRightDefButton().setText("完成");
        handler.getRightDefButton().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                reportCompanyInfo();
            }
        });
        handler.addLeftView(LayoutInflater.from(this).inflate(R.layout.title_leftview_layout, null), true);
        handler.getCenterLayout().findViewById(R.id.centerIv).setVisibility(View.GONE);
        TextView tv = (TextView) handler.getLeftLayout().findViewById(R.id.left_tv);
        tv.setText("返回");
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
        create_count_editSize = (TextView) findViewById(R.id.create_count_editSize);
        text_et.setOnTouchListener( this );


        text_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s.toString())) {
                    create_count_editSize.setText("0/256");
                } else {
                    if (s.toString().length() > 256) {
                        text_et.setText(s.subSequence(0, 256));
                        text_et.setSelection( 256 );//光标的位置
                        create_count_editSize.setText("256/256");
                    } else {
                        create_count_editSize.setText(s.toString().length() + "/200");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        text_et.setText(getIntent().getStringExtra(EXTRA_TEXT));
    }


    private void reportCompanyInfo(){
        final String msg_content = text_et.getText().toString();
        if (msg_content.equals("")) {
            QLToastUtils.showToast(this,"请输入公司简介");
            return;
        }
        showWaitDialog();
        HashMap<String, String> propertys = new HashMap<String, String>();
        propertys.put("company_intro", msg_content);
        UserManagerController.updateUserInfo(this, propertys, new Listener<Integer, String>() {
            @Override
            public void onCallBack(Integer status, String reply) {
                if (status == -1) {
                    String msg = reply == null ? "操作失败" : reply;
                    QLToastUtils.showToast(CompanyInfoActivity.this, msg);
                }else {
                    QLToastUtils.showToast(CompanyInfoActivity.this, "成功");
                    finish();
                }
            }
        });



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
