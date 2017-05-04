package app.logic.activity.user;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ql.activity.customtitle.ActActivity;
import org.ql.activity.customtitle.OnActActivityResultListener;
import org.ql.utils.QLToastUtils;
import org.ql.utils.image.QLImageUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.config.http.HttpConfig;
import app.logic.activity.ActTitleHandler;
import app.logic.activity.announce.*;
import app.logic.activity.live.LiveBaseActivity;
import app.logic.controller.AnnounceController;
import app.logic.controller.UserManagerController;
import app.logic.pojo.TradeInfo;
import app.utils.common.Listener;
import app.utils.helpers.ImagePickerHelper;
import app.yy.geju.R;

/*
 * GZYY    2016-8-3  上午10:14:03
 */

public class CompanyTradeActivity extends ActActivity {

    public static final String EXTRA_TYPE = "EXTRA_TYPE";
    private ActTitleHandler handler;
    private LinearLayout linearLayout;
    private int tradeType;//0 所在行业 1业务范围

    private List<Integer> enableIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new ActTitleHandler();
        setAbsHandler(handler);
        setContentView(R.layout.activity_trade_select);
        //初始化TooBar
        initActHandler();
        //初始化View
        initView();
        getData();
    }

    /**
     * 初始化TootBar
     */
    private void initActHandler() {

        setTitle("");
        handler.getRightDefButton().setText("完成");
        handler.getRightDefButton().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setData();
            }
        });
        handler.addLeftView(LayoutInflater.from(this).inflate(R.layout.title_leftview_layout, null), true);
        handler.getCenterLayout().findViewById(R.id.centerIv).setVisibility(View.GONE);
        handler.getLeftLayout().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView) handler.getLeftLayout().findViewById(R.id.left_tv)).setText("返回");
    }

    /**
     * 初始化View
     */
    private void initView() {
        tradeType = getIntent().getIntExtra(EXTRA_TYPE,0);
        TextView tv = (TextView) handler.getLeftLayout().findViewById(R.id.left_tv);
        if (tradeType == 1)
            tv.setText("业务范围");
        else
            tv.setText("所在行业");

        linearLayout = (LinearLayout) findViewById(R.id.linearlayout);

        String initIds = getIntent().getStringExtra("INIT_VALUE");
        if (!TextUtils.isEmpty(initIds)){
            String[] initList = initIds.split(",");
            for (int i=0;i<initList.length;i++){
                enableIds.add(Integer.parseInt(initList[i]));
            }
        }

    }

    private void getData(){
        showWaitDialog();
        UserManagerController.getCompanyTrade(this, tradeType, new Listener<Boolean, List<TradeInfo>>() {
            @Override
            public void onCallBack(Boolean aBoolean, List<TradeInfo> reply) {
                dismissWaitDialog();
                if (reply !=null && reply.size()>0){
                    setView(reply);
                }
            }
        });
    }

    private void setView(List<TradeInfo> tradeInfos){
        linearLayout.removeAllViews();
        for (TradeInfo info : tradeInfos){
            TextView textview = new TextView(this);
            textview.setText(info.getName());
            textview.setTextColor(Color.parseColor("#282828"));
            textview.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
            linearLayout.addView(textview);
            ((LinearLayout.LayoutParams)textview.getLayoutParams()).topMargin = 10;

            if (info.getChild_node().size()>0){
                for (int i=0;i<=(info.getChild_node().size()-1)/4;i++){
                    View contentView = View.inflate(this,R.layout.item_trade,null);
                    linearLayout.addView(contentView);
                    TradeInfo tInfo1 = info.getChild_node().get(i*4);
                    final TextView view1 = (TextView) contentView.findViewById(R.id.item1);
                    view1.setText(tInfo1.getName());
                    view1.setTag(tInfo1);
                    view1.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TradeInfo info = (TradeInfo)v.getTag();
                            if (enableIds.contains(info.getId())){
                                view1.setBackgroundResource(R.drawable.sharp_white_rect_stroke);
                                view1.setTextColor(Color.BLACK);
                                enableIds.remove((Integer) info.getId());
                            }else{
                                if (enableIds.size()>=5){
                                    QLToastUtils.showToast(CompanyTradeActivity.this,"最多选择五个关键字");
                                    return;
                                }
                                view1.setBackgroundResource(R.drawable.shape_default_btn);
                                view1.setTextColor(Color.WHITE);
                                enableIds.add(info.getId());
                            }
                        }
                    });
                    if (enableIds.contains(tInfo1.getId())){
                        view1.setBackgroundResource(R.drawable.shape_default_btn);
                        view1.setTextColor(Color.WHITE);
                    }else{
                        view1.setBackgroundResource(R.drawable.sharp_white_rect_stroke);
                        view1.setTextColor(Color.BLACK);
                    }

                    if (i*4+1<info.getChild_node().size()){
                        TradeInfo tInfo2 = info.getChild_node().get(i*4+1);
                        final TextView view2 = (TextView) contentView.findViewById(R.id.item2);
                        view2.setText(tInfo2.getName());
                        view2.setVisibility(View.VISIBLE);
                        view2.setTag(tInfo2);
                        view2.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                TradeInfo info = (TradeInfo)v.getTag();
                                if (enableIds.contains(info.getId())){
                                    view2.setBackgroundResource(R.drawable.sharp_white_rect_stroke);
                                    view2.setTextColor(Color.BLACK);
                                    enableIds.remove((Integer)info.getId());
                                }else{
                                    if (enableIds.size()>=5){
                                        QLToastUtils.showToast(CompanyTradeActivity.this,"最多选择五个关键字");
                                        return;
                                    }
                                    view2.setBackgroundResource(R.drawable.shape_default_btn);
                                    view2.setTextColor(Color.WHITE);
                                    enableIds.add(info.getId());
                                }
                            }
                        });
                        if (enableIds.contains(tInfo2.getId())){
                            view2.setBackgroundResource(R.drawable.shape_default_btn);
                            view2.setTextColor(Color.WHITE);
                        }else{
                            view2.setBackgroundResource(R.drawable.sharp_white_rect_stroke);
                            view2.setTextColor(Color.BLACK);
                        }
                        if (i*4+2<info.getChild_node().size()){
                            TradeInfo tInfo3 = info.getChild_node().get(i*4+2);
                            final TextView view3 = (TextView) contentView.findViewById(R.id.item3);
                            view3.setText(tInfo3.getName());
                            view3.setVisibility(View.VISIBLE);
                            view3.setTag(tInfo3);
                            view3.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    TradeInfo info = (TradeInfo)v.getTag();
                                    if (enableIds.contains(info.getId())){
                                        view3.setBackgroundResource(R.drawable.sharp_white_rect_stroke);
                                        view3.setTextColor(Color.BLACK);
                                        enableIds.remove((Integer)info.getId());
                                    }else{
                                        if (enableIds.size()>=5){
                                            QLToastUtils.showToast(CompanyTradeActivity.this,"最多选择五个关键字");
                                            return;
                                        }
                                        view3.setBackgroundResource(R.drawable.shape_default_btn);
                                        view3.setTextColor(Color.WHITE);
                                        enableIds.add(info.getId());
                                    }
                                }
                            });
                            if (enableIds.contains(tInfo3.getId())){
                                view3.setBackgroundResource(R.drawable.shape_default_btn);
                                view3.setTextColor(Color.WHITE);
                            }else{
                                view3.setBackgroundResource(R.drawable.sharp_white_rect_stroke);
                                view3.setTextColor(Color.BLACK);
                            }
                            if (i*4+3<info.getChild_node().size()){
                                TradeInfo tInfo4 = info.getChild_node().get(i*4+3);
                                final TextView view4 = (TextView) contentView.findViewById(R.id.item4);
                                view4.setText(tInfo4.getName());
                                view4.setVisibility(View.VISIBLE);
                                view4.setTag(tInfo4);
                                view4.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        TradeInfo info = (TradeInfo)v.getTag();
                                        if (enableIds.contains(info.getId())){
                                            view4.setBackgroundResource(R.drawable.sharp_white_rect_stroke);
                                            view4.setTextColor(Color.BLACK);
                                            enableIds.remove((Integer)info.getId());
                                        }else{
                                            if (enableIds.size()>=5){
                                                QLToastUtils.showToast(CompanyTradeActivity.this,"最多选择五个关键字");
                                                return;
                                            }
                                            view4.setBackgroundResource(R.drawable.shape_default_btn);
                                            view4.setTextColor(Color.WHITE);
                                            enableIds.add(info.getId());
                                        }
                                    }
                                });
                                if (enableIds.contains(tInfo4.getId())){
                                    view4.setBackgroundResource(R.drawable.shape_default_btn);
                                    view4.setTextColor(Color.WHITE);
                                }else{
                                    view4.setBackgroundResource(R.drawable.sharp_white_rect_stroke);
                                    view4.setTextColor(Color.BLACK);
                                }
                            }
                        }
                    }
                }
            }

        }
    }

    private void setData(){
        String ids = "";
        for (Integer id: enableIds){
            ids +=id+",";
        }
        if (ids.length()>0)
            ids = ids.substring(0,ids.length()-1);
        HashMap<String, String> propertys = new HashMap<String, String>();
        if (tradeType == 1){
            propertys.put("company_scope_id", ids);
        }else{
            propertys.put("company_industry_id", ids);
        }
        UserManagerController.updateUserInfo(this, propertys, new Listener<Integer, String>() {
            @Override
            public void onCallBack(Integer status, String reply) {
                if (status == -1) {
                    String msg = reply == null ? "操作失败" : reply;
                    QLToastUtils.showToast(CompanyTradeActivity.this, msg);
                }else {
                    QLToastUtils.showToast(CompanyTradeActivity.this, "成功");
                    finish();
                }
            }
        });
    }

}
