package app.logic.activity.card;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ql.activity.customtitle.ActActivity;
import org.ql.utils.QLToastUtils;

import app.utils.common.FrescoImageShowThumb;
import cn.jpush.a.a.a.c;
import cn.jpush.a.a.a.g;
import cn.jpush.a.a.a.k;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import u.aly.ca;
import u.aly.co;
import u.aly.da;

import android.R.integer;
import android.content.ClipData.Item;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import app.config.http.HttpConfig;
import app.logic.activity.ActTitleHandler;
import app.logic.adapter.YYBaseListAdapter;
import app.logic.controller.UserManagerController;
import app.logic.pojo.CardInfo;
import app.logic.pojo.CardItemInfo;
import app.utils.common.Listener;
import app.view.YYListView;
import app.yy.geju.R;

/*
 * GZYY    2016-12-27  上午10:54:02
 * author: zsz
 */

public class CardDetailsActivity3 extends ActActivity {

    public static final String CARD_INFO = "CARD_INFO";
    public static final String OPEN_MODE = "OPEN_MODE";
    public static final String CARD_ID = "CARD_ID";

    private CardInfo info;
    private String card_id;
    private CardInfo tempInfo;

    private ActTitleHandler titleHandler;
    private LayoutInflater inflater;
    private YYListView listView;
    private List<CardItemInfo> datas = new ArrayList<>();
    private View footView;

    private int phoneCount, emailCount, qqCount;

    private boolean edtStatus = false;

    private boolean editMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titleHandler = new ActTitleHandler();
        setAbsHandler(titleHandler);
        setContentView(R.layout.activity_card_details);

        card_id = getIntent().getStringExtra(CARD_ID);
        if (card_id != null) {
            getCardInfo(card_id);
        } else {
            info = (CardInfo) getIntent().getSerializableExtra(CARD_INFO);
            logic();
        }

    }

    private void logic() {
        initTitle();
        initView();
        initDatas();
    }


    private void initTitle() {
//        info = (CardInfo) getIntent().getSerializableExtra(CARD_INFO);
        editMode = getIntent().getBooleanExtra(OPEN_MODE, true);
        if (!editMode) {
            edtStatus = true;
        }
        tempInfo = info;

        setTitle("");
        titleHandler.replaseLeftLayout(this, true);
        ((TextView) titleHandler.getLeftLayout().findViewById(R.id.left_tv)).setText(editMode ? "名片编辑" : "添加名片");
        titleHandler.getLeftLayout().setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (editMode) {
            titleHandler.getRightDefButton().setText("编辑");
        } else {
            titleHandler.getRightDefButton().setText("保存");
            edtStatus = true;
        }

        titleHandler.getRightDefButton().setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (editMode) {
                    edtStatus = !edtStatus;
                    // initDatas();
                    changeEditStatus(edtStatus);
                    if (edtStatus) {
                        titleHandler.getRightDefButton().setText("保存");
                        addFootView();

                    } else {
                        titleHandler.getRightDefButton().setText("编辑");
                        removeFootView();
                        saveCardInfo();
                    }
                } else {
                    // 创建
                    createCard();

                }

            }
        });
    }

    private void initView() {
        inflater = LayoutInflater.from(this);

        listView = (YYListView) findViewById(R.id.listView);
        listView.setPullLoadEnable(false);
        listView.setPullLoadEnable(false, true);
        listView.setPullRefreshEnable(false);
        listView.setAdapter(mAdapter2);
    }

    private void initDatas() {
        if (info == null) {
            return;
        }
        datas.clear();
        addData(null, info.getBc_pic_url(), 5, false, InputType.TYPE_CLASS_TEXT);
        addData("姓名", info.getBc_name(), 4, false, InputType.TYPE_CLASS_TEXT);

        if (info.getBc_tel() == null) {
            phoneCount = 0;
        } else {
            phoneCount = info.getBc_tel().size();
        }
        addData("手机号", null, 1, phoneCount > 0 ? true : false, InputType.TYPE_CLASS_NUMBER);
        if (phoneCount > 0) {
            for (int i = 0; i < phoneCount; i++) {
                if (i == phoneCount - 1) {
                    addData(null, info.getBc_tel().get(i), 2, false, InputType.TYPE_CLASS_NUMBER);
                } else {
                    addData(null, info.getBc_tel().get(i), 2, true, InputType.TYPE_CLASS_NUMBER);
                }

            }
        }

        if (info.getBc_orgName() == null) {
            addData("公司信息", "", 3, false, InputType.TYPE_CLASS_TEXT);
        } else {
            addData("公司信息", info.getBc_orgName().get(0), 3, false, InputType.TYPE_CLASS_TEXT);
        }

        addData("职位", info.getBc_title(), 4, false, InputType.TYPE_CLASS_TEXT);

        if (info.getBc_email() == null) {
            emailCount = 0;
        } else {
            emailCount = info.getBc_email().size();
        }
        addData("邮箱", null, 1, emailCount > 0 ? true : false, InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        if (info.getBc_email() != null && info.getBc_email().size() > 0) {
            for (int i = 0; i < emailCount; i++) {
                if (i == emailCount - 1) {
                    addData(null, info.getBc_email().get(i), 2, false, InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                } else {
                    addData(null, info.getBc_email().get(i), 2, true, InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                }
            }
        }
        if (info.getBc_QQ() == null) {
            qqCount = 0;
        } else {
            qqCount = info.getBc_QQ().size();
        }
        addData("QQ", null, 1, qqCount > 0 ? true : false, InputType.TYPE_CLASS_NUMBER);
        if (qqCount > 0) {
            for (int i = 0; i < qqCount; i++) {
                if (i == qqCount - 1) {
                    addData(null, info.getBc_QQ().get(i), 2, false, InputType.TYPE_CLASS_NUMBER);
                } else {
                    addData(null, info.getBc_QQ().get(i), 2, true, InputType.TYPE_CLASS_NUMBER);
                }
            }
        }

        addData("备注", info.getBc_tag(), 3, false, InputType.TYPE_CLASS_TEXT);
//        mAdapter.setDatas(datas);
        mAdapter2.notifyDataSetChanged();


    }

    private void addData(String title, String content, int type, boolean enableShowLine, int inputType) {
        CardItemInfo info = new CardItemInfo();
        switch (type) {
            case 1:
                info.setTitle(title);
                break;
            case 2:
                info.setContent(content);
                break;
            case 3:
                info.setTitle(title);
                info.setContent(content);
                break;
            case 4:
                info.setTitle(title);
                info.setContent(content);
                break;
            case 5:
                info.setPictureUrl(content);
                break;

        }
        info.setEnableShowLine(enableShowLine);
        info.setType(type);

        info.setEdtStatus(edtStatus);
        info.setInputType(inputType);

        datas.add(info);

    }

    private void addFootView() {
        if (edtStatus) {
            if (footView == null) {
                footView = inflater.inflate(R.layout.footview_card_listview, null);
            }
            listView.addFooterView(footView);
            footView.findViewById(R.id.del_card_btn).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    delectCardInfo();

                }
            });
        }
    }

    private void removeFootView() {
        if (edtStatus) {
            listView.removeFooterView(footView);
        }

    }

    /**
     * @param status changeEditStatusCardDetailsActivity3
     */
    private void changeEditStatus(boolean status) {
        for (int i = 0; i < datas.size(); i++) {
            datas.get(i).setEdtStatus(status);
        }
//        mAdapter.setDatas(datas);
        mAdapter2.notifyDataSetChanged();
    }

    /**
     * @param position
     * @param keyString addItemCardDetailsActivity3
     */
    private void addItem(int position, String keyString) {
        int inputType;
        if ("手机号".equals(keyString)) {
            inputType = InputType.TYPE_CLASS_NUMBER;
        } else if ("邮箱".equals(keyString)) {
            inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
        } else {
            inputType = InputType.TYPE_CLASS_NUMBER;
        }

        for (int i = position + 1; i < datas.size(); i++) {
            if (datas.get(i).getType() != 2) {
                CardItemInfo itemInfo = new CardItemInfo();
                itemInfo.setContent("");
                itemInfo.setType(2);
                itemInfo.setEdtStatus(edtStatus);
                itemInfo.setInputType(inputType);
                itemInfo.setEnableShowLine(false);
                datas.add(i, itemInfo);
                break;
            }
        }
//        mAdapter.setDatas(datas);
        mAdapter2.notifyDataSetChanged();
    }


    /**
     * @param position
     * @param keyString removeItemCardDetailsActivity3
     */
    private void removeItem(int position, String keyString) {
        if (position < datas.size()) {
            datas.remove(position);
//            mAdapter.setDatas(datas);
            mAdapter2.notifyDataSetChanged();
        }
    }

    private void setTextToCardInfo(int position, String text) {
        if (position < datas.size()) {
            datas.get(position).setContent(text);
        }

    }

    /**
     * 保存
     * <p>
     * saveCardInfoCardDetailsActivity3
     */
    private void saveCardInfo() {
        if (!setDatasToInfo()) {
            return;
        }
        try {
            showWaitDialog();
            UserManagerController.modifyCard(this, info, "", new Listener<Integer, String>() {

                @Override
                public void onCallBack(Integer status, String reply) {
                    dismissWaitDialog();
                    if (status == 1) {
                        QLToastUtils.showToast(CardDetailsActivity3.this, "保存成功");
                        finish();
                        return;
                    }
                    QLToastUtils.showToast(CardDetailsActivity3.this, reply);

                }
            });
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * 将Datas的数据放回info
     * <p>
     * setDatasToInfoCardDetailsActivity3
     */
    private boolean setDatasToInfo() {
        CardItemInfo itemName = datas.get(1); //名字
        info.setBc_name(itemName.getContent());

        if (TextUtils.isEmpty(info.getBc_name())) {

            QLToastUtils.showToast(this, "请填写姓名");
            return false;


        }

        CardItemInfo itemPhone = datas.get(3);
        int phoneCountNum = 0;
        List<String> phoneList = new ArrayList<String>();
        for (int i = 3; i < datas.size(); i++) {
            if (datas.get(i).getType() == 2) {
                phoneList.add(datas.get(i).getContent());
                phoneCountNum++;
            } else {
                break;
            }
        }
        info.setBc_tel(phoneList);

        int comInfoIndex = 3 + phoneCountNum; // 公司信息的下标值
        CardItemInfo comItemInfo = datas.get(comInfoIndex);
        List<String> orgNameList = new ArrayList<String>();
        orgNameList.add(comItemInfo.getContent());
        info.setBc_orgName(orgNameList);
        if (TextUtils.isEmpty(info.getBc_orgName().get(0))) {
            QLToastUtils.showToast(this, "请填写公司信息");
            return false;
        }

        int jobIndex = comInfoIndex + 1;
        CardItemInfo jobItemInfo = datas.get(jobIndex);
        info.setBc_title(jobItemInfo.getContent());
        if (TextUtils.isEmpty(info.getBc_title())) {
            QLToastUtils.showToast(this, "请填写职位");
            return false;
        }


        int emailIndex = jobIndex + 2;
        int emailCountNum = 0;
        List<String> emailList = new ArrayList<String>();
        for (int i = emailIndex; i < datas.size(); i++) {
            CardItemInfo itemInfo = datas.get(i);
            if (itemInfo.getType() == 2) {
                emailList.add(itemInfo.getContent());
                emailCountNum++;
            } else {
                break;
            }
        }
        info.setBc_email(emailList);

        int qqItemIndex = emailIndex + emailCountNum + 1;
        int qqCountNum = 0;
        List<String> qqList = new ArrayList<String>();
        for (int i = qqItemIndex; i < datas.size(); i++) {
            CardItemInfo itemInfo = datas.get(i);
            if (itemInfo.getType() == 2) {
                qqList.add(itemInfo.getContent());
                qqCountNum++;
            } else {
                break;
            }
        }
        info.setBc_QQ(qqList);

        int tagItemIndex = qqItemIndex + qqCountNum;
        CardItemInfo tagItemInfo = datas.get(tagItemIndex);
        info.setBc_tag(tagItemInfo.getContent());
        return true;

    }

    /**
     * 删除
     * <p>
     * delectCardInfoCardDetailsActivity3
     */
    private void delectCardInfo() {
        UserManagerController.removeCard(this, info.getBc_id(), new Listener<Integer, String>() {

            @Override
            public void onCallBack(Integer status, String reply) {
                QLToastUtils.showToast(CardDetailsActivity3.this, reply);
                if (status == 1) {
                    finish();
                }

            }
        });
    }

    /**
     * 创建名片
     * <p>
     * createCardCardDetailsActivity3
     */
    private void createCard() {
        if (!setDatasToInfo()) {
            return;
        }
        showWaitDialog();
        try {
            UserManagerController.createCard(this, info, info.getBc_pic_url(), new Listener<Integer, String>() {

                @Override
                public void onCallBack(Integer status, String reply) {
                    dismissWaitDialog();
                    QLToastUtils.showToast(CardDetailsActivity3.this, reply);
                    if (status == 1) {
                        finish();
                    }

                }
            });
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * 获取详情
     *
     * @param card_id
     */
    private void getCardInfo(String card_id) {
        showWaitDialog();
        UserManagerController.getCardInfo(this, card_id, new Listener<Integer, CardInfo>() {
            @Override
            public void onCallBack(Integer integer, CardInfo reply) {
                dismissWaitDialog();
                if (integer == 1) {
                    info = reply;
                    logic();
                }

            }
        });

    }

    private YYBaseListAdapter<CardItemInfo> mAdapter = new YYBaseListAdapter<CardItemInfo>(this) {

        @Override
        public View createView(int position, View convertView, ViewGroup parent) {
            if (getItemViewType(position) == 0) {
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.item_card_type5, null);
                }
                logicItemTypeDatas5(position, convertView);
            } else if (getItemViewType(position) == 1) {
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.item_card_type1, null);
                }
                logicItemTypeDatas1(position, convertView);
            } else if (getItemViewType(position) == 2) {
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.item_card_type2, null);
                }
                logicItemTypeDatas2(position, convertView);
            } else if (getItemViewType(position) == 3) {
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.item_card_type3, null);
                }
                logicItemTypeDatas3(position, convertView);
            } else {
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.item_card_type4, null);
                }
                logicItemTypeDatas4(position, convertView);

            }
            return convertView;
        }

        public int getItemViewType(int position) {
            CardItemInfo info = mAdapter.getItem(position);
            int type = info.getType();
            if (type == 5) { // 图片
                return 0;
            } else if (type == 1) { // 电话
                return 1;
            } else if (type == 2) {// 电话内容
                return 2;
            } else if (type == 3) { // 公司信息
                return 3;

            } else { // 职位
                return 4;

            }
        }

        ;

        public int getViewTypeCount() {
            return 5;
        }

        ;
    };

    private BaseAdapter mAdapter2 = new BaseAdapter() {
        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int i) {
            return datas.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            if (getItemViewType(position) == 0) {
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.item_card_type5, null);
                }
                logicItemTypeDatas5(position, convertView);
            } else if (getItemViewType(position) == 1) {
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.item_card_type1, null);
                }
                logicItemTypeDatas1(position, convertView);
            } else if (getItemViewType(position) == 2) {
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.item_card_type2, null);
                }
                logicItemTypeDatas2(position, convertView);
            } else if (getItemViewType(position) == 3) {
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.item_card_type3, null);
                }
                logicItemTypeDatas3(position, convertView);
            } else {
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.item_card_type4, null);
                }
                logicItemTypeDatas4(position, convertView);

            }
            return convertView;
        }

        public int getItemViewType(int position) {
            CardItemInfo info = (CardItemInfo) mAdapter2.getItem(position);
            int type = info.getType();
            if (type == 5) { // 图片
                return 0;
            } else if (type == 1) { // 电话
                return 1;
            } else if (type == 2) {// 电话内容
                return 2;
            } else if (type == 3) { // 公司信息
                return 3;

            } else { // 职位
                return 4;

            }
        }

        public int getViewTypeCount() {
            return 5;
        }
    };

    /**
     * 类型1
     *
     * @param position
     * @param convertView logicItemTypeDatasCardDetailsActivity3
     */
    private void logicItemTypeDatas1(final int position, View convertView) {
        // final CardItemInfo info = mAdapter.getItem(position);

        final CardItemInfo info = datas.get(position);
        TextView title = (TextView) convertView.findViewById(R.id.title_tv);
        ImageView addIv = (ImageView) convertView.findViewById(R.id.add_iv);
        View lineView = convertView.findViewById(R.id.item_line);

        if (info != null) {
            lineView.setVisibility(info.getEnableShowLine() ? View.GONE : View.VISIBLE);
            title.setText(info.getTitle());
            addIv.setVisibility(edtStatus ? View.VISIBLE : View.INVISIBLE);
            addIv.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (info.isEdtStatus()) {
                        addItem(position, info.getTitle());
                    }

                }
            });

        }
    }

    /**
     * 类型2
     *
     * @param position
     * @param convertView logicItemTypeDatas2CardDetailsActivity3
     */
    private void logicItemTypeDatas2(final int position, View convertView) {
        // final CardItemInfo info = mAdapter.getItem(position);
        final CardItemInfo info = datas.get(position);
        final EditText contentEdt = (EditText) convertView.findViewById(R.id.content_edt);
        ImageView delIv = (ImageView) convertView.findViewById(R.id.del_iv);
        delIv.setVisibility(edtStatus ? View.VISIBLE : View.INVISIBLE);
        View lineView = convertView.findViewById(R.id.item_line);

        if (info != null) {
            lineView.setVisibility(info.getEnableShowLine() ? View.GONE : View.VISIBLE);
            contentEdt.setText(info.getContent());

            if (!info.isEdtStatus()) {
                contentEdt.setFocusableInTouchMode(false);

            } else {
                contentEdt.setFocusableInTouchMode(true);
                contentEdt.setInputType(info.getInputType());
                contentEdt.setOnFocusChangeListener(new OnFocusChangeListener() {

                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        String textString = ((EditText) v).getText().toString();
                        setTextToCardInfo(position, textString);

                    }
                });
                contentEdt.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        String textString = charSequence.toString();
                        setTextToCardInfo(position, textString);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                delIv.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        removeItem(position, info.getContent());
                    }
                });
            }

        }
    }

    /**
     * 类型3
     *
     * @param position
     * @param convertView logicItemTypeDatas3CardDetailsActivity3
     */
    private void logicItemTypeDatas3(final int position, View convertView) {
        // CardItemInfo info = mAdapter.getItem(position);
        final CardItemInfo info = datas.get(position);
        TextView title = (TextView) convertView.findViewById(R.id.title_tv);
        EditText contentEdt = (EditText) convertView.findViewById(R.id.content_edt);
        if (info != null) {
            title.setText(info.getTitle());
            if (TextUtils.isEmpty(info.getContent())) {
                contentEdt.setHint("请输入");
            } else {
                contentEdt.setText(info.getContent());
            }

            if (!info.isEdtStatus()) {
                contentEdt.setFocusableInTouchMode(false);
                return;
            }
            contentEdt.setFocusableInTouchMode(true);
            contentEdt.setInputType(info.getInputType());
            contentEdt.setOnFocusChangeListener(new OnFocusChangeListener() {

                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    String textString = ((EditText) v).getText().toString();
                    setTextToCardInfo(position, textString);

                }
            });
            contentEdt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    String textString = charSequence.toString();
                    setTextToCardInfo(position, textString);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });


        }
    }

    /**
     * 类型4
     *
     * @param position
     * @param convertView logicItemTypeDatas4 CardDetailsActivity3
     */
    private void logicItemTypeDatas4(final int position, View convertView) {
        // CardItemInfo info = mAdapter.getItem(position);
        final CardItemInfo info = datas.get(position);
        TextView titleTv = (TextView) convertView.findViewById(R.id.title_tv);
        EditText contentEdt = (EditText) convertView.findViewById(R.id.content_edt);
        if (info != null) {
            titleTv.setText(info.getTitle());

            if (TextUtils.isEmpty(info.getContent())) {
                contentEdt.setHint("请输入");
            } else {
                contentEdt.setText(info.getContent());
            }

            if (!info.isEdtStatus()) {
                contentEdt.setFocusableInTouchMode(false);
                return;
            }
            contentEdt.setFocusableInTouchMode(true);
//            contentEdt.setInputType(info.getInputType());
            contentEdt.setOnFocusChangeListener(new OnFocusChangeListener() {

                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    String textString = ((EditText) v).getText().toString();
                    setTextToCardInfo(position, textString);

                }
            });
            contentEdt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    String textString = charSequence.toString();
                    setTextToCardInfo(position, textString);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            contentEdt.setInputType(info.getInputType());
        }
    }

    /**
     * 类型5
     *
     * @param position
     * @param convertView logicItemTypeDatas5CardDetailsActivity3
     */
    private void logicItemTypeDatas5(final int position, View convertView) {
        // CardItemInfo info = mAdapter.getItem(position);
        final CardItemInfo info = datas.get(position);
        SimpleDraweeView imageView = (SimpleDraweeView) convertView.findViewById(R.id.card_iv);
        if (info != null) {
            if (editMode) {
                FrescoImageShowThumb.showThrumb(Uri.parse(HttpConfig.getUrl(info.getPictureUrl())),imageView);
//                imageView.setImageURI(HttpConfig.getUrl(info.getPictureUrl()));
//                Picasso.with(this).load(HttpConfig.getUrl(info.getPictureUrl())).error(R.drawable.default_user_icon).fit().centerCrop().into(imageView);
            } else {
                File file = new File(info.getPictureUrl());
//                Picasso.with(this).load(file).error(R.drawable.default_user_icon).fit().centerInside().into(imageView);
                imageView.getHierarchy().setPlaceholderImage(BitmapDrawable.createFromPath(file.getAbsolutePath()));
            }

        }
    }

}
