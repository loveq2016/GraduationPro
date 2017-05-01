package app.logic.activity.card;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.ql.activity.customtitle.ActActivity;
import org.ql.utils.QLToastUtils;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import app.config.http.HttpConfig;
import app.logic.activity.ActTitleHandler;
import app.logic.controller.UserManagerController;
import app.logic.pojo.CardInfo;
import app.utils.common.Listener;
import app.yy.geju.R;

/*
 * GZYY    2016-8-4  下午4:45:02
 */


public class CardDetailsActivity2 extends ActActivity implements View.OnClickListener {

    public static final String CARD_INFO = "CARD_INFO";
    public static final String CARD_ID = "CARD_ID";

    private ActTitleHandler titleHandler = new ActTitleHandler();
    private boolean editMode = true;
    private CardInfo info;
    private String card_id;
    private Boolean editStatus = false;
    private LayoutInflater inflater;
    private ScrollView scrollView;

    private ImageView cardHeadView;
    private ImageView phoneAddIv, emailAddIv, qqAddIv;
    private EditText nameEdt, companyEdt, jobEdt, tagEdt;
    private LinearLayout phoneLL, emailLL, qqLL;
    /**
     * 对于存储减号的监听事件的存储，散列码为key
     */
    private HashMap<Integer, View> phoneIvList, emailIvList, qqIvList;
    /**
     * itemView，散列码为key
     */
    private HashMap<Integer, View> phoneItemHashMap, emailItemHashMap, qqItemHashMap;

    private LinearLayout footLL;
    private Button deleteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbsHandler(titleHandler);
        setContentView(R.layout.activity_card_details2);

        initIntent();

    }

    /**
     * 逻辑处理
     */
    private void logic() {
        initTitle();
        initView();
        initListener();
        initData();
    }

    private void initIntent() {
        card_id = getIntent().getStringExtra(CARD_ID);
        if (card_id != null) {
            editMode = true;
        } else {
            editMode = false;
        }
        if (editMode) {
            getCardData(card_id);
        } else {
            info = (CardInfo) getIntent().getSerializableExtra(CARD_INFO);
            editStatus = true;
            logic();
        }
    }

    private void initTitle() {

        inflater = LayoutInflater.from(this);
        setTitle("");
        titleHandler.replaseLeftLayout(this, true);
        titleHandler.getLeftLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TextView titleTv = ((TextView) titleHandler.getLeftLayout().findViewById(R.id.left_tv));
        final Button rightBtn = titleHandler.getRightDefButton();
        if (editMode) {
            titleTv.setText("名片编辑");
            rightBtn.setText("编辑");

        } else {
            titleTv.setText("添加名片");
            rightBtn.setText("保存");
        }
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editMode) {
                    editStatus = !editStatus;

                    if (editStatus) {
                        changeAddIvStatus(editStatus);
                        scrollView.postInvalidate();
                        rightBtn.setText("保存");
                    } else {
                        rightBtn.setText("编辑");
                        saveCard();
                    }
                } else {
                    createCard();
                }
            }
        });
    }


    private void initView() {

        scrollView = (ScrollView) findViewById(R.id.sv_cardbg);

        cardHeadView = (ImageView) findViewById(R.id.card_head_iv);
        nameEdt = (EditText) findViewById(R.id.card_name_edt);
        companyEdt = (EditText) findViewById(R.id.company_edt);
        jobEdt = (EditText) findViewById(R.id.job_edt);
        tagEdt = (EditText) findViewById(R.id.tag_edt);

        phoneLL = (LinearLayout) findViewById(R.id.phone_add_ll);
        emailLL = (LinearLayout) findViewById(R.id.email_add_ll);
        qqLL = (LinearLayout) findViewById(R.id.qq_add_ll);
        phoneAddIv = (ImageView) findViewById(R.id.iv_phone_add);
        emailAddIv = (ImageView) findViewById(R.id.email_add_iv);
        qqAddIv = (ImageView) findViewById(R.id.qq_add_iv);

        footLL = (LinearLayout) findViewById(R.id.delete_card_ll);
        deleteBtn = (Button) findViewById(R.id.del_card_btn);

    }

    private void initListener() {
        phoneAddIv.setOnClickListener(this);
        emailAddIv.setOnClickListener(this);
        qqAddIv.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);
    }

    /**
     * 开始装载数据
     */
    private void initData() {

        phoneIvList = new HashMap<>();
        emailIvList = new HashMap<>();
        qqIvList = new HashMap<>();

        phoneItemHashMap = new HashMap<>();
        emailItemHashMap = new HashMap<>();
        qqItemHashMap = new HashMap<>();

        if (editMode) {
            Picasso.with(this).load(HttpConfig.getUrl(info.getBc_pic_url())).placeholder(R.drawable.default_user_icon).error(R.drawable.default_user_icon).fit().centerCrop().into(cardHeadView);

        } else {
            Picasso.with(this).load(new File(info.getBc_pic_url())).placeholder(R.drawable.default_user_icon).error(R.drawable.default_user_icon).fit().centerCrop().into(cardHeadView);

        }
        nameEdt.setText(info.getBc_name());
        if (info.getBc_tel() != null) {

            for (int i = 0; i < info.getBc_tel().size(); i++) {
                setViewForData(PHONE_TYPE, info.getBc_tel().get(i));
            }
        }

        if (info.getBc_orgName() != null) {
            companyEdt.setText(info.getBc_orgName().get(0));
        }

        jobEdt.setText(info.getBc_title());

        if (info.getBc_email() != null) {
            for (int i = 0; i < info.getBc_email().size(); i++) {
                setViewForData(EMAIL_TYPE, info.getBc_email().get(i));
            }
        }
        if (info.getBc_QQ() != null) {
            for (int i = 0; i < info.getBc_QQ().size(); i++) {
                setViewForData(QQ_TYPE, info.getBc_QQ().get(i));
            }
        }
        tagEdt.setText(info.getBc_tag());

        changeAddIvStatus(editStatus);

        scrollView.postInvalidate();
    }

    /**
     * 0为电话，1为email， 2为qq
     */
    private static final int PHONE_TYPE = 0;
    private static final int EMAIL_TYPE = 1;
    private static final int QQ_TYPE = 2;

    /**
     * 填充可变的view
     *
     * @param type
     * @param content
     */
    private void setViewForData(final int type, String content) {
        View view = inflater.inflate(R.layout.item_card_type2, null);
        EditText contentEdt = (EditText) view.findViewById(R.id.content_edt);
        ImageView deleteIv = (ImageView) view.findViewById(R.id.del_iv);

        if (TextUtils.isEmpty(content)) {
            return;
        }
        contentEdt.setText(content);

        deleteIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeItemView(type, view.hashCode());
            }
        });

        switch (type) {
            case PHONE_TYPE:
                contentEdt.setInputType(InputType.TYPE_CLASS_NUMBER);
                phoneLL.addView(view);
                phoneItemHashMap.put(deleteIv.hashCode(), view);
                phoneIvList.put(deleteIv.hashCode(), deleteIv);
                break;
            case EMAIL_TYPE:
                contentEdt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                emailLL.addView(view);
                emailItemHashMap.put(deleteIv.hashCode(), view);
                emailIvList.put(deleteIv.hashCode(), deleteIv);
                break;
            case QQ_TYPE:
                contentEdt.setInputType(InputType.TYPE_CLASS_NUMBER);
                qqLL.addView(view);
                qqItemHashMap.put(deleteIv.hashCode(), view);
                qqIvList.put(deleteIv.hashCode(), deleteIv);
                break;
        }
    }

    /**
     * 获取名片详情
     *
     * @param card_id
     */
    private void getCardData(String card_id) {
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

    /**
     * 删除名片
     */
    private void deleteCard() {
        UserManagerController.removeCard(this, info.getBc_id(), new Listener<Integer, String>() {
            @Override
            public void onCallBack(Integer integer, String reply) {
                QLToastUtils.showToast(CardDetailsActivity2.this, reply);
                if (integer == 1) {
                    finish();
                }
            }
        });
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.iv_phone_add:
                addItemView(PHONE_TYPE, phoneItemHashMap, phoneLL);

                break;
            case R.id.email_add_iv:
                addItemView(EMAIL_TYPE, emailItemHashMap, emailLL);
//                tempView = addItemViewView(EMAIL_TYPE);
//                emailItemHashMap.put(tempView.hashCode(), tempView);
//                emailLL.addView(tempView);
                break;
            case R.id.qq_add_iv:
//                tempView = addItemViewView(QQ_TYPE);
//                qqItemHashMap.put(tempView.hashCode(), tempView);
//                qqLL.addView(tempView);
                addItemView(QQ_TYPE, qqItemHashMap, qqLL);
                break;
            case R.id.del_card_btn:
                deleteCard();
                break;
        }
        scrollView.postInvalidate();
    }

    /**
     * 增加Item
     */
    private void addItemView(int type, HashMap<Integer, View> hashMap, LinearLayout viewLL) {
        boolean addStatus = true;
        if (!hashMap.isEmpty()) {

            Set<Integer> set = hashMap.keySet();
            for (Integer integer : set) {
                if (TextUtils.isEmpty(getContentByViewId(hashMap.get(integer)))) {
                    addStatus = false;
                    break;
                }
            }
        }

        if (addStatus) {
            View tempView = addItemViewView(type);
            hashMap.put(tempView.hashCode(), tempView);
            viewLL.addView(tempView);
        }
    }


    /**
     * 增加可变的View
     *
     * @param type
     * @return
     */
    private View addItemViewView(final int type) {

        final View tempView = inflater.inflate(R.layout.item_card_type2, null);
        ImageView imageView = (ImageView) tempView.findViewById(R.id.del_iv);
        EditText contentEdt = (EditText) tempView.findViewById(R.id.content_edt);
        contentEdt.requestFocus();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeItemView(type, tempView.hashCode());
            }
        });

        switch (type) {
            case PHONE_TYPE:
                contentEdt.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case EMAIL_TYPE:
                contentEdt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                break;
            case QQ_TYPE:
                contentEdt.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
        }
        return tempView;
    }


    /**
     * 移除可变的View
     *
     * @param type
     * @param key
     */
    private void removeItemView(int type, int key) {
        View tempView = null;
        switch (type) {
            case PHONE_TYPE:
                tempView = phoneItemHashMap.get(key);
                phoneLL.removeView(tempView);
                phoneItemHashMap.remove(key);
                phoneIvList.remove(key);
                break;
            case EMAIL_TYPE:
                tempView = emailItemHashMap.get(key);
                emailLL.removeView(tempView);
                emailIvList.remove(key);
                emailItemHashMap.remove(key);
                break;
            case QQ_TYPE:
                tempView = qqItemHashMap.get(key);
                qqLL.removeView(tempView);
                qqIvList.remove(key);
                qqItemHashMap.remove(key);
                break;
        }

        scrollView.postInvalidate();
    }

    /**
     * 切换状态
     *
     * @param status
     */
    private void changeAddIvStatus(boolean status) {

        Set<Integer> phoneSet = phoneItemHashMap.keySet();
        Set<Integer> emailSet = emailItemHashMap.keySet();
        Set<Integer> qqSet = qqItemHashMap.keySet();
        if (status) {

            int VISIBLE = View.VISIBLE;

            for (Integer integer : phoneSet) {
                phoneItemHashMap.get(integer).findViewById(R.id.del_iv).setVisibility(VISIBLE);
                getEditTextBykey(phoneItemHashMap.get(integer)).setFocusableInTouchMode(status);
            }
            for (Integer integer : emailSet) {
                emailItemHashMap.get(integer).findViewById(R.id.del_iv).setVisibility(VISIBLE);
                getEditTextBykey(emailItemHashMap.get(integer)).setFocusableInTouchMode(status);
            }
            for (Integer integer : qqSet) {
                qqItemHashMap.get(integer).findViewById(R.id.del_iv).setVisibility(VISIBLE);
                getEditTextBykey(qqItemHashMap.get(integer)).setFocusableInTouchMode(status);
            }
            phoneAddIv.setVisibility(VISIBLE);
            emailAddIv.setVisibility(VISIBLE);
            qqAddIv.setVisibility(VISIBLE);

        } else {

            int INVISIBLE = View.INVISIBLE;

            for (Integer integer : phoneSet) {
                phoneItemHashMap.get(integer).findViewById(R.id.del_iv).setVisibility(INVISIBLE);
                getEditTextBykey(phoneItemHashMap.get(integer)).setFocusableInTouchMode(status);
            }
            for (Integer integer : emailSet) {
                emailItemHashMap.get(integer).findViewById(R.id.del_iv).setVisibility(INVISIBLE);
                getEditTextBykey(emailItemHashMap.get(integer)).setFocusableInTouchMode(status);
            }
            for (Integer integer : qqSet) {
                qqItemHashMap.get(integer).findViewById(R.id.del_iv).setVisibility(INVISIBLE);
                getEditTextBykey(qqItemHashMap.get(integer)).setFocusableInTouchMode(status);
            }
            phoneAddIv.setVisibility(INVISIBLE);
            emailAddIv.setVisibility(INVISIBLE);
            qqAddIv.setVisibility(INVISIBLE);

        }
        nameEdt.setFocusableInTouchMode(status);
        companyEdt.setFocusableInTouchMode(status);
        jobEdt.setFocusableInTouchMode(status);
        tagEdt.setFocusableInTouchMode(status);
        footLL.setVisibility(editMode && status ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     *
     */
    private EditText getEditTextBykey(View view) {

        return ((EditText) view.findViewById(R.id.content_edt));
    }

    /**
     * 保存名片信息
     */
    private void saveCard() {
        if (!checkInfoFull()) {
            return;
        }
        try {
            showWaitDialog();
            UserManagerController.modifyCard(this, info, "", new Listener<Integer, String>() {
                @Override
                public void onCallBack(Integer integer, String reply) {
                    dismissWaitDialog();
                    if (integer == 1) {
                        QLToastUtils.showToast(CardDetailsActivity2.this, "保存成功");
                        finish();
                        return;
                    }
                    QLToastUtils.showToast(CardDetailsActivity2.this, reply);

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建名片
     */
    private void createCard() {
        if (!checkInfoFull()) {
            return;
        }
        try {
            showWaitDialog();
            UserManagerController.createCard(this, info, info.getBc_pic_url(), new Listener<Integer, String>() {
                @Override
                public void onCallBack(Integer integer, String reply) {
                    dismissWaitDialog();
                    QLToastUtils.showToast(CardDetailsActivity2.this, reply);
                    if (integer == 1) {
                        finish();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查信息完整
     *
     * @return
     */
    private boolean checkInfoFull() {
        String cardName = nameEdt.getText().toString();
        if (TextUtils.isEmpty(cardName)) {
            QLToastUtils.showToast(this, "请填写姓名");
            return false;
        }
        info.setBc_name(cardName);

        //手机号码
        List<String> phoneList = new ArrayList<>();
        if (!phoneItemHashMap.isEmpty()) {
            Set<Integer> phoneSet = phoneItemHashMap.keySet();
            for (Integer integer : phoneSet) {
                String text = getContentByViewId(phoneItemHashMap.get(integer));
                if (!TextUtils.isEmpty(text)) {
                    phoneList.add(text);
                }
            }
        }
        info.setBc_tel(phoneList);
        //公司信息
        List<String> orgNameList = new ArrayList<>();
        orgNameList.add(companyEdt.getText().toString());
        info.setBc_orgName(orgNameList);
        if (TextUtils.isEmpty(info.getBc_orgName().get(0))) {
            QLToastUtils.showToast(this, "请填写公司信息");
            return false;
        }

        //职位
        info.setBc_title(jobEdt.getText().toString());
        if (TextUtils.isEmpty(info.getBc_title())) {
            QLToastUtils.showToast(this, "请填写职位");
            return false;
        }
        //邮箱
        List<String> emailList = new ArrayList<>();
        if (!emailItemHashMap.isEmpty()) {
            Set<Integer> emailSet = emailItemHashMap.keySet();
            for (Integer integer : emailSet) {
                String text = getContentByViewId(emailItemHashMap.get(integer));
                if (!TextUtils.isEmpty(text)) {
                    emailList.add(text);
                }
            }
        }
        info.setBc_email(emailList);
        //qq
        List<String> qqList = new ArrayList<>();
        if (!qqItemHashMap.isEmpty()) {
            Set<Integer> qqSet = qqItemHashMap.keySet();
            for (Integer integer : qqSet) {
                String text = getContentByViewId(qqItemHashMap.get(integer));
                if (!TextUtils.isEmpty(text)) {
                    qqList.add(text);
                }
            }
        }
        info.setBc_QQ(qqList);
        //设置备注
        info.setBc_tag(tagEdt.getText().toString());
        return true;
    }

    /**
     * 从item editText获取文本内容
     *
     * @param view
     * @return
     */
    private String getContentByViewId(View view) {
        return ((EditText) view.findViewById(R.id.content_edt)).getText().toString();
    }


}
