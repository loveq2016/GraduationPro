package app.logic.activity.friends;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sort.sortlistview.CharacterParser;

import org.ql.activity.customtitle.ActActivity;
import org.ql.activity.customtitle.onActPermissionCheckResultListener;
import org.ql.utils.QLToastUtils;
import org.ql.views.listview.QLXListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import app.config.http.HttpConfig;
import app.logic.activity.ActTitleHandler;
import app.logic.activity.user.PreviewFriendsInfoActivity;
import app.logic.adapter.YYBaseListAdapter;
import app.logic.controller.ChatRoomController;
import app.logic.controller.UserManagerController;
import app.logic.pojo.ComparatorFriends;
import app.logic.pojo.ComparatorUserInfo;
import app.logic.pojo.ContactInfo;
import app.logic.pojo.FriendInfo;
import app.logic.pojo.FriendsInfoExt;
import app.logic.pojo.UserInfo;
import app.logic.pojo.YYChatRoomInfo;
import app.utils.common.FrescoImageShowThumb;
import app.utils.common.Listener;
import app.view.DialogNewStyleController;
import app.yy.geju.R;

/*
 * GZYY    2016-12-13  上午10:38:47
 * author: zsz
 * 
 * 
 * 这个类迟早会被抛弃掉的。。。。warning
 */

public class ContactListActivity extends ActActivity implements OnItemClickListener ,QLXListView.IXListViewListener {

    public static final String ROOMALL="ROOMALL";
    public static final String kSELECTED_ITEM_MODEL = "kSELECTED_ITEM_MODEL";
    public static final String kTITLE = "KTITLE";
    public static final String kSELECTED_ITEMS_JSON_STRING = "kSELECTED_ITEMS_JSON_STRING";
    public static final int kSELECT_ITEMS = 23;
    public static final String ADD_FRIENDS = "ADD_FRIENDS";
    public static final String DETELE_FRIENDS = "DETELE_FRIENDS";
    public static final String CHATROOM_ID = "CHATROOM_ID";
    public static final String IS_SHOW_CHECKBOX = "IS_SHOW_CHECKBOX";
    public static final String ADD = "ADD";
    public static final String DEL = "DEL";

    private ActTitleHandler titleHandler;
    private LinearLayout search_bg;
    private LinearLayout search_edt_bg;
    private EditText search_edt;
    private QLXListView listView;
    private CharacterParser characterParser;
    private ComparatorUserInfo comparatorFriends;
    private Resources resources;
    private List<UserInfo> datas = new ArrayList<UserInfo>();
    private List<UserInfo> selectDatas = new ArrayList<UserInfo>();
    private List<UserInfo> selectUserInfos = new ArrayList<UserInfo>();
    private List<UserInfo> userInfosDatas = new ArrayList<UserInfo>();
    private boolean selectStatus = false;
    private boolean delectStatus = false , addStatus = false , roomAll =false;
    private boolean showCheckBoxStatus = true;
    private Gson gson;

    private LinearLayout ariView ;
    private List<ContactInfo> contactList = new ArrayList<>();

    private static final int REQUEST_CODE_CONTACT= 101;

    private YYBaseListAdapter<UserInfo> mAdapter = new YYBaseListAdapter<UserInfo>(this) {

        @Override
        public View createView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(ContactListActivity.this).inflate(R.layout.item_contact, null);
                saveView("catalog", R.id.catalog, convertView);
                saveView("name_item_tv", R.id.name_item_tv, convertView);
                saveView("conract_item_imgview", R.id.conract_item_imgview, convertView);
                saveView("geju_item_tv", R.id.geju_item_tv, convertView);
                saveView("geju_status_tv", R.id.geju_status_tv, convertView);
                saveView("add_friend_btn", R.id.add_friend_btn, convertView);
            }
            UserInfo info = getItem(position);
            if (info != null) {
                TextView catalog = (TextView) getViewForName("catalog", convertView);
                TextView index_tv = (TextView) getViewForName("name_item_tv", convertView);
                index_tv.setText(info.getName());

                TextView geju_item_tv = (TextView) getViewForName("geju_item_tv", convertView);
                String nameString = info.getFriend_name()==null||TextUtils.isEmpty(info.getFriend_name()) ? info.getNickName() : info.getFriend_name();
                geju_item_tv.setText("格局："+nameString);
                SimpleDraweeView imageeview = (SimpleDraweeView) getViewForName("conract_item_imgview", convertView);
                FrescoImageShowThumb.showThrumb(Uri.parse(HttpConfig.getUrl(info.getPicture_url())),imageeview);

                Button button = (Button)  getViewForName("add_friend_btn", convertView);
                TextView statusTv = (TextView) getViewForName("geju_status_tv", convertView);
                button.setVisibility(View.VISIBLE);
                statusTv.setVisibility(View.VISIBLE);
                if (info.getFriendStatus().equals("11")){
                    button.setVisibility(View.GONE);
                    statusTv.setText("已添加");
                }else if(info.getFriendStatus().equals("10")){
                    button.setVisibility(View.GONE);
                    statusTv.setText("申请中");
                }else{
                    statusTv.setVisibility(View.GONE);
                }

                int section = getSectionForPosition(position);
                if (position == getPositionForSection(section)) {
                    catalog.setText(info.getSortLetters());
                    catalog.setVisibility(View.VISIBLE);
                } else {
                    catalog.setVisibility(View.GONE);
                }

                button.setTag(info);
                button.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UserInfo info = (UserInfo) v.getTag();
                        // showAddFriendsDialog(info);
                        showAddFriendsDialog2(info);
                    }
                });
            }
            return convertView;
        }
    };

    private int getSectionForPosition(int position) {
        return datas.get(position).getSortLetters().charAt(0);
    }

    private int getPositionForSection(int section) {
        for (int i = 0; i < datas.size(); i++) {
            String sortStr = datas.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    private DialogNewStyleController addFriendsDailog;
    /**
     * @param
     */
    private void showAddFriendsDialog2(final UserInfo personalInfo) {
        if (addFriendsDailog != null && addFriendsDailog.isShowing()) {
            return;
        }
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_friend_view, null);
        addFriendsDailog = new DialogNewStyleController(this, view);
        TextView titleTv = (TextView) view.findViewById(R.id.dialog_title_tv);
        SimpleDraweeView headIv = (SimpleDraweeView) view.findViewById(R.id.dialog_head_iv);
        TextView nameTv = (TextView) view.findViewById(R.id.dialog_user_name_tv);
        final EditText tagEdt = (EditText) view.findViewById(R.id.dialog_tag_edt);
        Button sendBtn = (Button) view.findViewById(R.id.dialog_cancel_btn);
        Button cancel = (Button) view.findViewById(R.id.dialog_true_btn);

        titleTv.setText("添加好友");
        sendBtn.setText("发送");
        cancel.setText("取消");
        sendBtn.setTextColor(getResources().getColor(R.color.new_app_color));
        cancel.setTextColor(Color.parseColor("#ff0000"));
        FrescoImageShowThumb.showThrumb(Uri.parse(HttpConfig.getUrl(personalInfo.getPicture_url())),headIv);
        //Picasso.with(this).load(HttpConfig.getUrl(personalInfo.getPicture_url())).error(R.drawable.default_user_icon).fit().centerInside().into(headIv);
        nameTv.setText(personalInfo.getNickName());
        sendBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = tagEdt.getText().toString();
//                requestFriends(personalInfo.getPhone(), msg);
                requestFriendsById(personalInfo.getWp_member_info_id(),msg);
                addFriendsDailog.dismiss();

            }
        });
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriendsDailog.dismiss();
            }
        });
        addFriendsDailog.show();
    }

    /**
     * 发送添加请求
     *
     * @param friend_id
     * @param msg
     */
    private void requestFriendsById(String friend_id, String msg) {
        UserManagerController.addFriendsById(this, friend_id, msg, new Listener<Integer, String>() {
            @Override
            public void onCallBack(Integer status, String reply) {
                // hideKeyboard();
                if (status == -1) {
                    String msg = reply == null ? "操作失败" : reply;
                    QLToastUtils.showToast(ContactListActivity.this, msg);
                    return;
                }
                QLToastUtils.showToast(ContactListActivity.this, "请求已发送");
                // getRequestFrendsList();
                onRefresh();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        titleHandler = new ActTitleHandler();
        setAbsHandler(titleHandler);
        setContentView(R.layout.activity_friends_list2);
        resources = getResources();
        gson = new Gson();
        intiTitle();
        initView();
        intiData();
        intiListViewListener() ;

        showWaitDialog();
        ContactsFetcherHelper.queryContactInfo(this, new ContactsFetcherHelper.OnFetchContactsListener() {
            @Override
            public void onFetcherContactsComplete(List<ContactInfo> list) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissWaitDialog();
                    }
                });

                contactList.clear();
                contactList.addAll(list);
                String string = "";
                for (ContactInfo info:list){
                    string +=info.getPhoneNumber()+",";
                }
                if (string.length()>0){
                    getFriendsListDatas(string.substring(0,string.length()-1));
                }else{
                    search_bg.setVisibility(View.GONE);
                    ariView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    /**
     * 初始化TITLE
     */
    private void intiTitle(){
        setTitle("");
        titleHandler.replaseLeftLayout(this, true);
        titleHandler.getLeftLayout().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView) titleHandler.getLeftLayout().findViewById(R.id.left_tv)).setText("查看手机通讯录");
    }

    /**
     * 初始化view
     */
    private void initView() {
        ariView = (LinearLayout) findViewById(R.id.empty_view);
        search_bg = (LinearLayout) findViewById(R.id.search_bg);
        search_bg.setBackgroundColor(resources.getColor(R.color.white));
        search_edt = (EditText) findViewById(R.id.search_edt);
        search_edt.setHint("搜索");
        search_edt_bg = (LinearLayout) findViewById(R.id.search_edt_bg);
        search_edt_bg.setBackgroundDrawable(resources.getDrawable(R.drawable.shape_search_edt_bg));
        ((TextView)findViewById(R.id.empty_tv01)).setText("无匹配");
        listView = (QLXListView) findViewById(R.id.friends_list_view);
        listView.setAdapter(mAdapter);
        characterParser = CharacterParser.getInstance();
        comparatorFriends = new ComparatorUserInfo();
        search_edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyString = s.toString();
                if (!TextUtils.isEmpty(keyString)) {
                    selectInfos(keyString);
                }else
                    mAdapter.setDatas(datas);
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
    }

    /**
     * 初始化数据
     */
    private void intiData(){

    }

    /**
     * 初始化ListView的监听
     */
    private void intiListViewListener(){
        listView.setPullLoadEnable(false);
        listView.setPullLoadEnable(false, true);
        if(addStatus || roomAll){ //邀请成员，或是全部群成员的时候可以刷新
            listView.setPullRefreshEnable(true);
            listView.setXListViewListener(this);
        }else{
            listView.setPullRefreshEnable(false);
            listView.setXListViewListener(null);
        }
        listView.setOnItemClickListener(this);
    }

    /**
     * 获取数据
     * <p>
     * getDatasFriendsListActivity2
     */

    private void getFriendsListDatas(String phones) {
//        // 删除,和显示
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showWaitDialog();
            }
        });

        UserManagerController.contactCheck(this, phones, new Listener<List<UserInfo>, String>() {
            @Override
            public void onCallBack(List<UserInfo> userInfos, String reply) {
                dismissWaitDialog();
                datas.clear();
                if (userInfos != null && userInfos.size() > 0) {
                    // TODO

                    datas.addAll(fillSortDatasUserInfos(userInfos));

                }else{
                    if (userInfos !=null && userInfos.size() == 0){
                        QLToastUtils.showToast( ContactListActivity.this , "无匹配");
                    }else
                        QLToastUtils.showToast( ContactListActivity.this , "数据获取失败，请重新加载");
                }
                mAdapter.setDatas(datas);
                if( datas.size() >0 ){
                    ariView.setVisibility(View.GONE);
                }else{
                    search_bg.setVisibility(View.GONE);
                    ariView.setVisibility(View.VISIBLE);
                }
            }
        });






    }

    private List<UserInfo> fillSortDatasUserInfos(List<UserInfo> list) {
        List<UserInfo> temp = new ArrayList<UserInfo>();
        for (UserInfo info : list) {
            String phone = info.getPhone();
            for (ContactInfo contactInfo:contactList){
                if (phone.equals(contactInfo.getPhoneNumber())){
                    info.setName(contactInfo.getName());
                    break;
                }
            }


//            String nameString = info.getFriend_name()==null||TextUtils.isEmpty(info.getFriend_name()) ? info.getNickName() : info.getFriend_name();
//            infoExt.setName(nameString);
            String pinyinString = characterParser.getSelling(info.getName());
            if (!TextUtils.isEmpty(pinyinString)) {
                String sortString = pinyinString.substring(0, 1).toUpperCase();
                if (sortString.matches("[A-Z]")) { //!"unknown".equals(pinyinString)&&
                    info.setSortLetters(sortString.toUpperCase());
                } else {
                    info.setSortLetters("#");
                }
            } else {
                info.setSortLetters("#");
            }
            if(info.getName()!=null && "昵".equals(info.getName().substring(0,1))){  //对“昵”字做特殊处理（目前没有找到更好的库）
                info.setSortLetters("N");
            }

            temp.add(info);
        }
        Collections.sort(temp, comparatorFriends);
        return temp;

    }


    /**
     * 检索
     *
     * @param keyString selectInfosFriendsListActivity2
     */
    private void selectInfos(String keyString) {

        selectDatas.clear();
        for (UserInfo userInfo : datas) {
            if ((userInfo.getPhone()!=null&&userInfo.getPhone().contains(keyString)) || (userInfo.getNickName()!=null&&userInfo.getNickName().contains(keyString)) ||
                    (userInfo.getName()!=null&&userInfo.getName().contains(keyString))||(userInfo.getFriend_name()!=null&&userInfo.getFriend_name().contains(keyString))) {
                selectDatas.add(userInfo);
            }
        }
        Collections.sort(selectDatas, comparatorFriends);
        mAdapter.setDatas(selectDatas);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            UserInfo userInfo = (UserInfo) parent.getItemAtPosition( position);
            if( userInfo != null ){
                Intent intent = new Intent();
                intent.setClass( this ,PreviewFriendsInfoActivity.class);
                intent.putExtra(PreviewFriendsInfoActivity.kUSER_MEMBER_ID , userInfo.getWp_member_info_id());
                startActivity( intent);
            }

    }


    @Override
    public void onRefresh() {
    }

    @Override
    public void onLoadMore() {

    }
}
