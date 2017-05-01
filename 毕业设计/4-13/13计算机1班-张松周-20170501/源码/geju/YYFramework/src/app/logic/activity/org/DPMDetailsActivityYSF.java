package app.logic.activity.org;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.ql.activity.customtitle.ActActivity;
import org.ql.utils.QLToastUtils;
import org.ql.views.listview.QLXListView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import app.config.http.HttpConfig;
import app.logic.activity.ActTitleHandler;
import app.logic.activity.friends.LogicFriends;
import app.logic.controller.OrganizationController;
import app.logic.pojo.DepartmentInfo;
import app.logic.pojo.ExpansionInfo;
import app.logic.pojo.IntentInfo;
import app.logic.pojo.OrgRequestMemberInfo;
import app.logic.pojo.UserInfo;
import app.utils.common.FrescoImageShowThumb;
import app.utils.common.Listener;
import app.view.DialogNewStyleController;
import app.view.YYListView;
import app.yy.geju.R;

/**
 * Created by Administrator on 2017/1/12 0012.
 */

public class DPMDetailsActivityYSF extends ActActivity implements View.OnClickListener {

    public static final String ADNINLIST = "ADNINLIST"; //管理员的集合
    private ArrayList<UserInfo> adniList;

    public static final String ISBUILDER = "ISBUILDER";
    public static final String ISADMIE = "ISADMIE";
    public static final String DPMNAME = "DPMNAME";
    public static final String ADINMENAME = "ADINMENAME";

    private ActTitleHandler titleHandler = new ActTitleHandler();
    private IntentInfo intentInfo;
    private Gson gson;
    private Drawable nDrawable; //添加
    private Drawable yDrawable; //删减
    private String newDpmId, newDpaName;
    private View dpmllView, contentView, dpm_admin_and_img, adminView; //部门名称 ， 对部门话框名称 ， 部门管理员 ， 设置管理员时对话框View
    private Button trueBtn, cancelBtn, hAdminBtn, nAdminBtn;
    private EditText contentEdt;
    private DialogNewStyleController dialog, adminDialog;

    private TextView dpmNameEdt;     //部门的名字
    private TextView adminNameTv;    //部门管理员的名字
    private ImageView addAdminIv;   //添加部门管理员的图片按钮
    private GridView memberGv;      //显示成员头像
    private Button dpmBtn;         //解散部门 、确认按钮
    private boolean editStatus;    //是否可编辑的标志位
    private int resultPosition = -1;
    private List<ExpansionInfo> datas = new ArrayList<ExpansionInfo>();
    private boolean isBuilder = false;  //组织管理员 ？
    private boolean isAdmie = false;    //部门管理员 ？

    private String wp_member_info_id;    //被设置为管理员的Id
    private String dpm_id, dpm_name, adinmeName;    //部门Id ,名称
    private boolean but_clicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbsHandler(titleHandler);
        setContentView(R.layout.activity_dpm_details_for_edit2);
        nDrawable = getResources().getDrawable(R.drawable.icon_dpm_add);
        yDrawable = getResources().getDrawable(R.drawable.icon_dpm_del);
        intentInfo = (IntentInfo) getIntent().getSerializableExtra(IntentInfo.INTENT_INFO);
        adniList = (ArrayList<UserInfo>) getIntent().getSerializableExtra(ADNINLIST);
        isBuilder = getIntent().getBooleanExtra("ISBUILDER", false);
        isAdmie = getIntent().getBooleanExtra("ISADMIE", false);
        dpm_name = getIntent().getStringExtra("DPMNAME");
        adinmeName = getIntent().getStringExtra("ADINMENAME");
        contentView = LayoutInflater.from(this).inflate(R.layout.dialog_new_edit_view, null);
        contentEdt = (EditText) contentView.findViewById(R.id.dialog_content_edt);
        intiDiglog(contentView); //部门名名对话框
        adminView = LayoutInflater.from(this).inflate(R.layout.dialog_new_admincontent_view, null);
        intiAdminDialog(adminView);
        initTitle();
        initView();
        initDatas();
        addListener();

    }

    /**
     * 对话框
     *
     * @param
     */
    private void intiDiglog(View contentView) {
        TextView title = (TextView) contentView.findViewById(R.id.dialog_title_tv);
        final EditText contentEdt = (EditText) contentView.findViewById(R.id.dialog_content_edt);
        trueBtn = (Button) contentView.findViewById(R.id.dialog_true_btn);
        cancelBtn = (Button) contentView.findViewById(R.id.dialog_cancel_btn);
        if (intentInfo.getOpenMode() == IntentInfo.ADD_MODE) {
            title.setText("部门昵称");
            contentEdt.setHint("填写要创建的部门昵称");
        } else {
            title.setText("修改部门昵称");
            contentEdt.setHint(dpm_name);
        }
        dialog = new DialogNewStyleController(this, contentView);
        trueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newDpaName = contentEdt.getText().toString();
                String msg = contentEdt.getText().toString();
                if (!TextUtils.isEmpty(newDpaName)) {
                    if (intentInfo.getOpenMode() == IntentInfo.ADD_MODE) {
                        //添加部门
                        dpmNameEdt.setText(msg);
                    } else {
                        //修改部门名称
                        dpmNameEdt.setText(msg);
                        updateDPM(intentInfo.getOrgId(), intentInfo.getDpmId(), msg);
                    }
                    dialog.dismiss();
                } else {
                    QLToastUtils.showToast(DPMDetailsActivityYSF.this, "部门昵称不能为空");
                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 对话框初始化
     *
     * @param
     */
    private void intiAdminDialog(View contentView) {
        TextView title = (TextView) contentView.findViewById(R.id.dialog_title_tv);
        hAdminBtn = (Button) contentView.findViewById(R.id.dialog_true_btn);
        nAdminBtn = (Button) contentView.findViewById(R.id.dialog_cancel_btn);
        adminDialog = new DialogNewStyleController(this, contentView);
        hAdminBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (intentInfo.getOpenMode() == IntentInfo.ADD_MODE) {
                    setAdmieMthon(intentInfo.getOrgId(), newDpmId, wp_member_info_id, 0);
                } else {
                    if (adniList.size() > 0) {
                        wp_member_info_id = adniList.get(0).getWp_member_info_id();
                    }
                    setAdmieMthon(intentInfo.getOrgId(), intentInfo.getDpmId(), wp_member_info_id, 0);
                }
                adminDialog.dismiss();
            }
        });
        //取消按钮
        nAdminBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adminDialog.dismiss();
            }
        });
    }

    /**
     * 初始化TootBar
     */
    private void initTitle() {
        titleHandler.replaseLeftLayout(this, true);
        ((TextView) titleHandler.getLeftLayout().findViewById(R.id.left_tv)).setText(intentInfo.getTitle());
        titleHandler.getLeftLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleHandler.getRightDefButton().setText("编辑");
        titleHandler.getRightDefButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editStatus = !editStatus;
                changeEditOrCancel();
            }
        });
    }

    /**
     * 初始化View
     */
    private void initView() {
        dpm_admin_and_img = findViewById(R.id.dpm_admin_and_img);
        dpmllView = findViewById(R.id.dpm_name_ll);
        dpmNameEdt = (TextView) findViewById(R.id.dpm_name_edt);
        adminNameTv = (TextView) findViewById(R.id.dpm_admin_tv);
        addAdminIv = (ImageView) findViewById(R.id.add_or_del_iv);
        memberGv = (GridView) findViewById(R.id.dpm_gridView);
        dpmBtn = (Button) findViewById(R.id.dpm_btn);
        if (adniList != null && adniList.size() > 0) {
            adminNameTv.setText(adniList.get(0).getNickName());
        } else {
            adminNameTv.setText("");
        }
        //不是组织超级管理员 ，不显示这个按钮
        if (isBuilder) {
            dpmBtn.setText("解散部门");
            dpmBtn.setBackgroundResource(R.drawable.shape_join_org_btn_rad_bg); //设为红色背景
        } else if (isAdmie) {
            dpmBtn.setVisibility(View.GONE);
            dpmBtn.setBackgroundResource(R.drawable.shape_join_org_btn_bg); //设为綠色背景
        }
        //從添加部門界面跳转过来（按鈕變為確定）
        if (intentInfo.getOpenMode() == IntentInfo.ADD_MODE) {
            dpm_admin_and_img.setVisibility(View.GONE);
            dpmllView.setOnClickListener(this);
            dpmNameEdt.setText("");
            dpmNameEdt.setEnabled(false);
            dpmllView.setEnabled(true); //可用
            editStatus = true;
            changeEditOrCancel();
        } else {
            dpmllView.setOnClickListener(this);
            dpmNameEdt.setText(dpm_name);
            dpmNameEdt.setEnabled(false);
            dpmllView.setEnabled(true);  //不可用
        }
        memberGv.setAdapter(mAdapter);
    }

    /**
     * 初始化数据
     */
    private void initDatas() {
        setTitle("");
        gson = new Gson();
    }

    /**
     * 添加监听器
     */
    private void addListener() {
        //添加管理員按鈕
        addAdminIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editStatus) {
                    if (!TextUtils.isEmpty(adminNameTv.getText().toString())) { //只要管理员一栏有内容，在点击的时候就显示对话框
                        adminDialog.show();
                    } else {
                        String dataString = getSelectMemberList(0);  //選擇裂變（ 在datas中選擇 ）
                        if (TextUtils.isEmpty(dataString)) {
                            return;
                        }
                        Intent intent = new Intent(DPMDetailsActivityYSF.this, LogicFriends.class);
                        intent.putExtra(LogicFriends.DATAS_LIST, dataString);
                        intent.putExtra(LogicFriends.MODEL, LogicFriends.USER_INFO);
                        intent.putExtra(LogicFriends.TITLE, "选择管理员");
                        intent.putExtra(LogicFriends.SELECT_ITEM, true);
                        resultPosition = 2;     //選擇管理員的請求
                        startActivityForResult(intent, LogicFriends.REQUEST_CODE);
                    }
                }
            }
        });

        //裂變的簡體昂（添加成員和刪除成員的監聽）
        memberGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (intentInfo.getOpenMode() == IntentInfo.ADD_MODE) {    //新增模式下（添加部門接賣弄跳轉過來的）
                    Intent intent = new Intent(DPMDetailsActivityYSF.this, LogicFriends.class);
                    if (position == datas.size() - 1) {             // 刪除  點擊的是刪除圖片
                        String dataString = getSelectMemberList(1);
                        if (TextUtils.isEmpty(dataString)) {
                            return;
                        }
                        intent.putExtra(LogicFriends.DATAS_LIST, dataString);
                        intent.putExtra(LogicFriends.TITLE, "部门成员");
                        resultPosition = 1;                  //刪除成員的請求
                        startActivityForResult(intent, LogicFriends.REQUEST_CODE);
                    }
                    if (position == datas.size() - 2) { // 增加    點擊的是增加圖片
                        addMemberToDpm(intent);       //添加成員的方法
                    }
                } else {                                                  //編輯模式下（部門列表跳轉過來的）
                    if (editStatus) {
                        Intent intent = new Intent(DPMDetailsActivityYSF.this, LogicFriends.class);
                        if (position == datas.size() - 1) {// 删除
                            String dataString = getSelectMemberList(1);
                            if (TextUtils.isEmpty(dataString)) {
                                return;
                            }
                            intent.putExtra(LogicFriends.ORGID, intentInfo.getOrgId());
                            intent.putExtra(LogicFriends.DPAID, intentInfo.getDpmId());
                            intent.putExtra(LogicFriends.DATAS_LIST, dataString);
                            intent.putExtra(LogicFriends.TITLE, "部门成员");
                            resultPosition = 1;              //刪除的標誌
                            startActivityForResult(intent, LogicFriends.REQUEST_CODE);
                        }
                        if (position == datas.size() - 2) {// 增加
                            addMemberToDpm(intent);
                        }
                    }
                }
            }
        });
        //按鈕監聽器
        dpmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                but_clicked = true;  //按钮被按钮的标志
                if (intentInfo.isAdmin()) {
                    if (intentInfo.getOpenMode() == IntentInfo.EDIT_MODE) {  //修改模式
                        if (!editStatus) {
                            if (isBuilder) {  //協會的創建愛這才可以解散部門
                                removeDpm(intentInfo.getOrgId(), intentInfo.getDpmId()); //解散部门
                            } else {
                                finish();
                            }
                        } else {
                            //更新部门名称
                            if (!dpm_name.equals(dpmNameEdt.getText().toString())) {
                                updateDPM(intentInfo.getOrgId(), intentInfo.getDpmId(), dpmNameEdt.getText().toString());
                            }
                            //设置部门管理员
                            if (!TextUtils.isEmpty(adminNameTv.getText().toString()) && !adminNameTv.getText().toString().equals(wp_member_info_id)) {
                                setAdmieMthonADDMoed(intentInfo.getOrgId(), intentInfo.getDpmId(), wp_member_info_id, 1, adminNameTv.getText().toString());
                            }
                        }
                    } else if (intentInfo.getOpenMode() == IntentInfo.ADD_MODE) {    //新增模式
                        addDpmToOrg();  //添加部门方法
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (intentInfo.getOpenMode() == IntentInfo.EDIT_MODE) {
            getDPMMember();  //获取本部门成员
        }
    }

    /**
     * 获取本部门成员
     * getDPMMemberDPMDetailsForEditActivity2
     */
    private void getDPMMember() {
        if (TextUtils.isEmpty(intentInfo.getDpmId())) {
            return;
        }
        OrganizationController.getDPMMemberList(this, intentInfo.getOrgId(), intentInfo.getDpmId(), new Listener<Void, List<UserInfo>>() {
            @Override
            public void onCallBack(Void status, List<UserInfo> reply) {
                if (reply == null || reply.size() < 1) {
                    return;
                }
                datas.clear();
                for (UserInfo info : reply) {
                    ExpansionInfo expansionInfo = new ExpansionInfo();
                    expansionInfo.setItemUrl(info.getPicture_url());
                    expansionInfo.setUserInfo(info);
                    expansionInfo.setWp_member_info_id(info.getWp_member_info_id());
                    String nameString = TextUtils.isEmpty(info.getFriend_name()) ? info.getNickName() : info.getFriend_name();
                    expansionInfo.setItemName(nameString);
                    datas.add(expansionInfo);
                }
                if (editStatus) {
                    addLastItem();
                }
                mAdapter.notifyDataSetChanged();
                countGridViewHeight();
            }
        });
    }

    /**
     * 為 1 是，選擇的是部門的成員 ， 選擇的是這個部門的成員
     * 為 0 是，選擇的是部門的管理成員 ， 在這個部門下的成員中選擇
     *
     * @param type 0为选择管理员（跳转的时候没有显示checkBox） 1为正常状态
     * @return 获取当前部门的成员, 返回json getSelectMemberListDPMDetailsForEditActivity2
     */
    private String getSelectMemberList(int type) {   //是在datas集合中選擇
        if (datas.size() <= 2) {
            QLToastUtils.showToast(this, "请先选择部门成员");
            return null;
        }
        List<ExpansionInfo> memberList = new ArrayList<ExpansionInfo>();
        for (int i = 0; i < datas.size() - 2; i++) {
            ExpansionInfo expansionInfo = datas.get(i);
            if (intentInfo.getOpenMode() == IntentInfo.EDIT_MODE) {  //新增模式
                expansionInfo.setItemID(expansionInfo.getUserInfo().getWp_member_info_id());
            }
            if (type == 0) {
                expansionInfo.setItemShowCheck(false);  //選擇的是部門的噶管理員  ，管理員不能電視複選框
            } else {
                expansionInfo.setItemShowCheck(true);   //選擇的不是管理員，顯示複選框
            }
            expansionInfo.setItemIsCheck(false);
            memberList.add(expansionInfo);
        }
        String jsonString = gson.toJson(memberList);
        return jsonString;
    }

    /**
     * 改变编辑状态
     * changeEditOrCancelDPMDetailsForEditActivity2
     */
    private void changeEditOrCancel() {
        if (editStatus) {
            dpmllView.setEnabled(true);    //编辑可用
            if (intentInfo.getOpenMode() == IntentInfo.EDIT_MODE) {
                titleHandler.getRightDefButton().setText("取消");
            } else {
                titleHandler.getRightDefButton().setVisibility(View.INVISIBLE);  //編輯不顯示
            }
            addAdminIv.setImageDrawable(TextUtils.isEmpty(adminNameTv.getText()) ? getResources().getDrawable(R.drawable.icon_dpm_add) : getResources().getDrawable(R.drawable.icon_dpm_del));
            dpmBtn.setText("确定");
            dpmBtn.setBackgroundResource(R.drawable.shape_join_org_btn_bg);
            dpmBtn.setVisibility(View.VISIBLE);
            addLastItem();  //添加最后两张图片
        } else {
            if (!but_clicked) {
                dpmNameEdt.setText(dpm_name);
                adminNameTv.setText("");
            }
            dpmllView.setEnabled(false);    //编辑可用
            if (intentInfo.getOpenMode() == IntentInfo.EDIT_MODE) {
                titleHandler.getRightDefButton().setText("编辑");
                dpmBtn.setText("解散部门");
                dpmBtn.setBackgroundResource(R.drawable.shape_join_org_btn_rad_bg);
                if ((isBuilder && isAdmie) || isBuilder) {
                    dpmBtn.setVisibility(View.VISIBLE);
                } else if (isAdmie) {
                    dpmBtn.setVisibility(View.GONE);
                } else {
                    dpmBtn.setVisibility(View.GONE);
                }
                addAdminIv.setImageDrawable(null);
                removeLastItem();   //移除最后两张图片
            }
        }
    }

    /**
     * @return addMemberToDpmDPMDetailsForEditActivity2 筛选出可添加的成员
     */
    private void addMemberToDpm(final Intent intent) {
        OrganizationController.getOrgMemberList(this, intentInfo.getOrgId(), new Listener<Void, List<OrgRequestMemberInfo>>() {
            @Override
            public void onCallBack(Void status, List<OrgRequestMemberInfo> reply) {
                if (reply == null || reply.size() < 1) {
                    return;
                }
                List<OrgRequestMemberInfo> lastInfos = new ArrayList<OrgRequestMemberInfo>();
                for (OrgRequestMemberInfo memberInfo : reply) {
                    if (TextUtils.isEmpty(memberInfo.getPhone())) {        //手機號為空的，不是真實有效的數據
                        continue;
                    }
                    if (TextUtils.isEmpty(memberInfo.getDepartmentId())) {   //添加不輸與任何部門的成員
                        lastInfos.add(memberInfo);
                    }
                }
                if (lastInfos.size() < 1) {
                    QLToastUtils.showToast(DPMDetailsActivityYSF.this, "没有可以添加的成员");
                    return;
                }
                List<ExpansionInfo> seleInfos = new ArrayList<ExpansionInfo>();
                for (OrgRequestMemberInfo memberInfo : lastInfos) {
                    boolean temp = true;
                    for (ExpansionInfo expansionInfo : datas) {
                        if (memberInfo.getPhone().equals(expansionInfo.getItemPhone())) {
                            temp = false;
                        }
                    }
                    if (temp) {
                        ExpansionInfo expansionInfo = new ExpansionInfo();
                        expansionInfo.setItemUrl(memberInfo.getPicture_url());
                        expansionInfo.setItemName(memberInfo.getNickName());
                        expansionInfo.setItemID(memberInfo.getWp_member_info_id());
                        expansionInfo.setItemPhone(memberInfo.getPhone());
                        expansionInfo.setItemShowCheck(true);
                        expansionInfo.setWp_member_info_id(memberInfo.getWp_member_info_id());
                        seleInfos.add(expansionInfo);
                    }
                }
                intent.putExtra(LogicFriends.DATAS_LIST, gson.toJson(seleInfos));
                intent.putExtra(LogicFriends.TITLE, "选择联系人");
                resultPosition = 0;      //添加成員的標誌
                startActivityForResult(intent, LogicFriends.REQUEST_CODE);
            }
        });
    }

    /**
     * 解散部門
     *
     * @param org_id
     * @param dpm_id
     */
    private void removeDpm(final String org_id, String dpm_id) {
        OrganizationController.removeDPM(this, org_id, dpm_id, new Listener<Integer, String>() {
            @Override
            public void onCallBack(Integer status, String reply) {
                if (status != 1) {
                    String msg = reply == null ? "操作失败" : reply;
                    QLToastUtils.showToast(DPMDetailsActivityYSF.this, msg);
                    return;
                }
                Intent intent = new Intent(DPMDetailsActivityYSF.this, DPMListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(DPMListActivity.kORG_ID, org_id);
                intent.putExtra(DPMListActivity.kORG_NAME, intentInfo.getOrgName());
                startActivity(intent);
            }
        });
    }

    /**
     * @param result
     * @param type   0是添加，1是删除,2是添加部門管理員
     *               addMemberToDpmDPMDetailsForEditActivity2 添加成员到部门
     */
    private void addMemberToDpm(String result, int type) {
        if (TextUtils.isEmpty(result)) {
            return;
        }
        final List<ExpansionInfo> newInfos = gson.fromJson(result, new TypeToken<List<ExpansionInfo>>() {
        }.getType());
        if (newInfos == null || newInfos.size() < 1) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        for (ExpansionInfo info : newInfos) {
            builder.append(info.getItemID() + ",");
        }
        builder.deleteCharAt(builder.length() - 1);
        if (type == 0) {   //曾
            OrganizationController.addMemberToDPM(this, intentInfo.getOrgId(), intentInfo.getDpmId(), builder.toString(), new Listener<Integer, String>() {
                @Override
                public void onCallBack(Integer status, String reply) {
                    if (status != 1) {
                        String msg = reply == null ? "添加失败" : reply;
                        QLToastUtils.showToast(DPMDetailsActivityYSF.this, msg);
                        return;
                    } else {
                        getDPMMember();
                    }
                }
            });
        }
        if (type == 1) {   //删
            System.out.println(intentInfo.getOrgId() + "  " + intentInfo.getDpmId() + "  " + builder.toString());
            OrganizationController.removeMemberFromDPM(this, intentInfo.getOrgId(), intentInfo.getDpmId(), builder.toString(), new Listener<Integer, String>() {
                @Override
                public void onCallBack(Integer status, String reply) {
                    if (status != 1) {
                        getDPMMember();     //獲取本部門成員
                        String msg = reply == null ? "操作失败" : reply;
                        QLToastUtils.showToast(DPMDetailsActivityYSF.this, msg);
                        return;
                    } else {
                        getDPMMember();     //獲取本部門成員
                    }
                }
            });
        }
        if (type == 2) {   //添加管理员
            if (newInfos.size() > 0) {
                String name = newInfos.get(0).getItemName();
                setAdmieMthonADDMoed(intentInfo.getOrgId(), intentInfo.getDpmId(), wp_member_info_id, 1, name);

            }
        }
    }

    /**
     * 添加部门状态下的 （添加成員到部門）
     *
     * @param result
     * @param type   0为添加 ,1为删除 addMembetToNullDpmDPMDetailsForEditActivity2
     */
    private void addMembetToNullDpm(String result, int type) {
        if (TextUtils.isEmpty(result)) {
            return;
        }
        List<ExpansionInfo> newInfos = gson.fromJson(result, new TypeToken<List<ExpansionInfo>>() {
        }.getType());
        if (newInfos == null || newInfos.size() < 1) {
            return;
        }
        if (type == 0) {
            datas.addAll(0, newInfos);
            mAdapter.notifyDataSetChanged();
        }
        if (type == 1) {
            for (ExpansionInfo expansionInfo : newInfos) {
                for (int i = 0; i < datas.size() - 2; i++) {
                    if (datas.get(i).getItemPhone().equals(expansionInfo.getItemPhone())) {
                        datas.remove(i);
                    }
                }
            }
        }
        mAdapter.notifyDataSetChanged();
        countGridViewHeight();
    }

    /**
     * 協會添加部門（超級管理員操作）
     * <p>
     * 添加部門接賣弄跳轉過來調用的   增加部门
     * addDpmToOrgDPMDetailsForEditActivity2
     */
    private void addDpmToOrg() {
        String dpmNameString = dpmNameEdt.getText().toString();
        if (TextUtils.isEmpty(dpmNameString)) {
            QLToastUtils.showToast(this, "部门名称不能为空");
            return;
        }
        // showWaitDialog();  添加部门
        OrganizationController.addDPM(this, intentInfo.getOrgId(), dpmNameString, new Listener<String, DepartmentInfo>() {
            @Override
            public void onCallBack(String status, DepartmentInfo reply) {
                if (TextUtils.isEmpty(status) || status.length() < 7) {
                    dismissWaitDialog();
                    QLToastUtils.showToast(DPMDetailsActivityYSF.this, "部门创建失败，请重试");
                    return;
                }
                dpm_id = status; //部门创建成功后的部门ID
                StringBuilder iDBuilder = new StringBuilder();
                for (int i = 0; i < datas.size() - 2; i++) {
                    iDBuilder.append(datas.get(i).getItemID() + ",");
                }
                if (iDBuilder.length() > 0) {
                    iDBuilder.deleteCharAt(iDBuilder.length() - 1);
                    //添加成员
                    OrganizationController.addMemberToDPM(DPMDetailsActivityYSF.this, intentInfo.getOrgId(), status, iDBuilder.toString(), new Listener<Integer, String>() {
                        @Override
                        public void onCallBack(Integer status, String reply) {
                            if (status != 1) {
                                String msg = TextUtils.isEmpty(reply) ? "部门创建成功，添加成员失败" : reply;
                                QLToastUtils.showToast(DPMDetailsActivityYSF.this, msg);
                                finish();
                            } else {
                                finish();

                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 创建部门
     */
    private void addDpmToOrgYFS(String dpmNameString) {
        showWaitDialog();  //添加部门
        OrganizationController.addDPM(this, intentInfo.getOrgId(), dpmNameString, new Listener<String, DepartmentInfo>() {
            @Override
            public void onCallBack(String status, DepartmentInfo reply) {
                dismissWaitDialog();
                if (TextUtils.isEmpty(status) || status.length() < 7) {
                    dpmNameEdt.setText("");
                    QLToastUtils.showToast(DPMDetailsActivityYSF.this, "部门创建失败，请重试");
                } else {
                    dpmNameEdt.setText(newDpaName);
                    newDpmId = status;  //保存刚创建的部门Id
//					dpmBtn.setText("解散部门");
//					dpmBtn.setBackgroundResource( R.drawable.shape_join_org_btn_rad_bg ); //设为红色背景
//					dpmBtn.setVisibility( View.VISIBLE);
                }
            }
        });
    }

    /**
     * 修改部门的信息
     */
    private void updateDPM(String org_id, String departmentId, final String nameString) {
        showWaitDialog();  //添加部门
        OrganizationController.updateDepartment(DPMDetailsActivityYSF.this, org_id, departmentId, nameString, new Listener<Boolean, String>() {
            @Override
            public void onCallBack(Boolean status, String reply) {
                dismissWaitDialog();
                if (status) {
                    dpmNameEdt.setText(nameString);
                } else {
                    dpmNameEdt.setText(dpm_name);
                    QLToastUtils.showToast(DPMDetailsActivityYSF.this, "修改失败,请重试");
                }
            }
        });
    }

    /**
     * 新曾模式下的添加成员
     *
     * @param resrtList
     */
    private void addMenerToDmp(final List<ExpansionInfo> resrtList) {
        if (resrtList == null || resrtList.size() < 1) {
            QLToastUtils.showToast(DPMDetailsActivityYSF.this, "请选择部门成员");
        }
        StringBuilder iDBuilder = new StringBuilder();
        for (int i = 0; i < resrtList.size(); i++) {
            iDBuilder.append(resrtList.get(i).getItemID() + ",");
        }
        if (iDBuilder.length() > 0) {
            iDBuilder.deleteCharAt(iDBuilder.length() - 1);
            //添加成员
            OrganizationController.addMemberToDPM(DPMDetailsActivityYSF.this, intentInfo.getOrgId(), newDpmId, iDBuilder.toString(), new Listener<Integer, String>() {
                @Override
                public void onCallBack(Integer status, String reply) {
                    if (status != 1) {
                        String msg = TextUtils.isEmpty(reply) ? "添加成员失败,请重试" : reply;
                        QLToastUtils.showToast(DPMDetailsActivityYSF.this, msg);
                    } else {
//						getDPMMemberNew() ;
                        dpm_admin_and_img.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    /**
     * 设置部门管理员
     *
     * @param type 0 是取消管理员身份 1 是设置为管理员身份
     * @param
     */
    private void setAdmieMthonADDMoed(String org_id, String dpm_id, String dpm_member_info_id, int type, final String name) {
        showWaitDialog(); //设置管理员
        OrganizationController.setAdmin(DPMDetailsActivityYSF.this, org_id, dpm_id, dpm_member_info_id, type, new Listener<Boolean, String>() {
            @Override
            public void onCallBack(Boolean aBoolean, String reply) {
                dismissWaitDialog();
                if (aBoolean) {
                    addAdminIv.setImageDrawable(yDrawable);
                    adminNameTv.setText(name);
                    QLToastUtils.showToast(DPMDetailsActivityYSF.this, "设置成功");
                } else {
                    adminNameTv.setText("");
                    QLToastUtils.showToast(DPMDetailsActivityYSF.this, "设置失败");
                }
            }
        });
    }

    /**
     * 设置部门管理员
     *
     * @param type 0 是取消管理员身份 1 是设置为管理员身份
     * @param
     */
    private void setAdmieMthon(String org_id, String dpm_id, String dpm_member_info_id, int type) {
        //设置管理员
        OrganizationController.setAdmin(DPMDetailsActivityYSF.this, org_id, dpm_id, dpm_member_info_id, type, new Listener<Boolean, String>() {
            @Override
            public void onCallBack(Boolean aBoolean, String reply) {
                if (aBoolean) {
//					QLToastUtils.showToast( DPMDetailsForEditActivity2.this ,"修改成功" );
                    adminNameTv.setText("");
                    addAdminIv.setImageDrawable(nDrawable);
                } else {
                    QLToastUtils.showToast(DPMDetailsActivityYSF.this, "修改失败");
                }
            }
        });
    }


    /**
     * 增加最后两个
     * <p>
     * addLastItemDPMDetailsForEditActivity2
     */
    private void addLastItem() {
        ExpansionInfo info = new ExpansionInfo();
        info.setItemLastIv(R.drawable.icon_add_item);
        datas.add(info);
        ExpansionInfo info2 = new ExpansionInfo();
        info2.setItemLastIv(R.drawable.icon_delect_item);
        datas.add(info2);
        mAdapter.notifyDataSetChanged();
        countGridViewHeight();
    }

    /**
     * 适配器对象
     */
    private BaseAdapter mAdapter = new BaseAdapter() {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (getItemViewType(position) == 0) {
                DPMDetailsForEditActivity2.ViewHolder holder = null;
                if (convertView == null) {
                    holder = new DPMDetailsForEditActivity2.ViewHolder();
                    convertView = LayoutInflater.from(DPMDetailsActivityYSF.this).inflate(R.layout.item_dpm_member, null);
                    holder.imageView = (SimpleDraweeView) convertView.findViewById(R.id.item_head_iv);
                    convertView.setTag(holder);
                } else {
                    holder = (DPMDetailsForEditActivity2.ViewHolder) convertView.getTag();
                }
                ExpansionInfo info = (ExpansionInfo) getItem(position);
                if (info != null) {
                    String urlString = info.getItemUrl();
//                    holder.imageView.setImageURI(HttpConfig.getUrl(urlString));
                    FrescoImageShowThumb.showThrumb(Uri.parse(HttpConfig.getUrl(urlString)),holder.imageView);
//                    Picasso.with(DPMDetailsActivityYSF.this).load(HttpConfig.getUrl(urlString)).error(R.drawable.default_user_icon).fit().centerCrop().into(holder.imageView);
                }
            } else {
                DPMDetailsForEditActivity2.ViewHolder holder = null;
                if (convertView == null) {
                    holder = new DPMDetailsForEditActivity2.ViewHolder();
                    convertView = LayoutInflater.from(DPMDetailsActivityYSF.this).inflate(R.layout.item_dpm_member, null);
                    holder.imageView = (SimpleDraweeView) convertView.findViewById(R.id.item_head_iv);
                    convertView.setTag(holder);
                } else {
                    holder = (DPMDetailsForEditActivity2.ViewHolder) convertView.getTag();
                }
                ExpansionInfo info = (ExpansionInfo) getItem(position);
                if (info != null) {
                    holder.imageView.setImageDrawable(getResources().getDrawable(info.getItemLastIv()));
                }
            }
            return convertView;
        }

        public int getItemViewType(int position) {
            ExpansionInfo info = (ExpansionInfo) getItem(position);
            if (info != null && !TextUtils.isEmpty(info.getItemUrl())) {
                return 0;
            }
            return 1;
        }

        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public Object getItem(int position) {
            return datas.get(position);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return datas.size();
        }
    };

    static class ViewHolder {
        ImageView imageView;
    }

    /**
     * 去掉最后两个
     * <p>
     * removeLastItemDPMDetailsForEditActivity2
     */
    private void removeLastItem() {
        datas.remove(datas.size() - 1);
        datas.remove(datas.size() - 1);
        // mAdapter.setDatas(datas);
        mAdapter.notifyDataSetChanged();
        countGridViewHeight();
        // memberGv.setVisibility(View.GONE);
    }

    /**
     * 技术GV的高度
     */
    private void countGridViewHeight() {
        int num = datas.size();
        int totalHeight = 0;
        if (num > 0) {
            if (num < 5) {  //一行5个
                View itemView = mAdapter.getView(0, null, memberGv);
                itemView.measure(0, 0);
                totalHeight += itemView.getMeasuredHeight();
            } else {
                int result = num / 5;
                for (int i = 0; i < result; i++) {
                    View itemView = mAdapter.getView(i, null, memberGv);
                    itemView.measure(0, 0);
                    totalHeight += itemView.getMeasuredHeight();
                }
                if (num % 5 > 0) {
                    totalHeight += totalHeight / result;
                }
            }
        }
        ViewGroup.LayoutParams params = memberGv.getLayoutParams();
        params.height = totalHeight;
        memberGv.setLayoutParams(params);
    }

    //View的
    @Override
    public void onClick(View view) {
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == LogicFriends.REQUEST_CODE && data != null) {
                if (intentInfo.getOpenMode() == IntentInfo.EDIT_MODE) {
                    switch (resultPosition) {
                        case 0: // 添加成员
                            addMemberToDpm(data.getStringExtra(LogicFriends.RESULT_LIST), 0);
                            break;
                        case 1: // 删除成员
                            addMemberToDpm(data.getStringExtra(LogicFriends.RESULT_LIST), 1);
                            break;
                        case 2: // 设置部门管理员
                            wp_member_info_id = data.getStringExtra(LogicFriends.WPMEMBERINFOID);
                            addMemberToDpm(data.getStringExtra(LogicFriends.RESULT_LIST), 2);
                            break;
                    }
                } else if (intentInfo.getOpenMode() == IntentInfo.ADD_MODE) {
                    switch (resultPosition) {
                        case 0: // 添加成员
                            addMembetToNullDpm(data.getStringExtra(LogicFriends.RESULT_LIST), 0);
                            break;
                        case 1:
                            addMembetToNullDpm(data.getStringExtra(LogicFriends.RESULT_LIST), 1);
                            break;
                        case 2:// 设置部门管理员
                            wp_member_info_id = data.getStringExtra(LogicFriends.WPMEMBERINFOID);
                            addMemberToDpm(data.getStringExtra(LogicFriends.RESULT_LIST), 2);
                            break;
                    }
                }
            }
        }
    }

    /**
     * 判断当前选中的 成员 是不是已经是这个部门的管理员了
     *
     * @param dpm_member_info_id
     * @return 0 是 ， 1 不是
     */
    private int getType(ArrayList<UserInfo> adniList, String dpm_member_info_id) {
        if (adniList == null) {
            return 1;
        }
        if (adniList.size() < 1) {
            return 1;
        }
        Iterator iterator = adniList.iterator();
        while (iterator.hasNext()) {
            if (((UserInfo) iterator.next()).getWp_member_info_id().equals(dpm_member_info_id)) {
                return 0;
            }
        }
        return 1;
    }

    /**
     * 设置部门只有一个管理员的方法
     *
     * @param adniList
     * @param wp_member_info_id
     */
    private void setOneAdinle(ArrayList<UserInfo> adniList, String wp_member_info_id) {
        //这个部门这前没有管理员，就设置当前选中的这个为管理员
        if (adniList == null || adniList.size() < 1) {
            setAdmieMthon(intentInfo.getOrgId(), dpm_id, wp_member_info_id, 1);
            return;       //不在往下走
        } else if (!adniList.get(0).getWp_member_info_id().equals(wp_member_info_id)) {  //如果当前被选择的成员，不是这个部门的管理员，就把这前的管理员取消 ，在设置当前被选择的为管理员  ，选择的还是原来的管理员则发起请求
            //就把这前的管理员取消
            setAdmieMthonNew(intentInfo.getOrgId(), dpm_id, adniList.get(0).getWp_member_info_id(), 0, dpm_id);
        }
    }

    /**
     * 设置部门管理员
     *
     * @param type 0 是取消管理员身份 1 是设置为管理员身份
     * @param
     */
    private void setAdmieMthonNew(String org_id, final String dpm_id, String dpm_member_info_id, int type, final String newdpm_id) {
        //设置管理员
        OrganizationController.setAdmin(DPMDetailsActivityYSF.this, org_id, dpm_id, dpm_member_info_id, type, new Listener<Boolean, String>() {
            @Override
            public void onCallBack(Boolean aBoolean, String reply) {
                if (!aBoolean) {
                    QLToastUtils.showToast(DPMDetailsActivityYSF.this, "修改失败");
                } else {
                    //原来的管理员注销成功后在设置新的管理员
                    setAdmieMthon(intentInfo.getOrgId(), newdpm_id, wp_member_info_id, 1);
                }
            }
        });
    }

    //选择了管理管员之后才发起设置管理员请求
//	if( !TextUtils.isEmpty( adminNameTv.getText().toString()) ){
//	setAdmieMthonADDMoed( intentInfo.getOrgId() , dpm_id , wp_member_info_id , 1 , "");
//	}
}
