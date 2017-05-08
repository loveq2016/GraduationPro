package app.logic.activity.friends;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.ql.activity.customtitle.ActActivity;
import org.ql.utils.QLToastUtils;
import org.ql.views.listview.QLXListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sort.sortlistview.CharacterParser;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
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
import app.config.http.HttpConfig;
import app.logic.activity.ActTitleHandler;
import app.logic.activity.chat.ChatRoomInfoActivity;
import app.logic.activity.user.PreviewFriendsInfoActivity;
import app.logic.adapter.YYBaseListAdapter;
import app.logic.controller.ChatRoomController;
import app.logic.controller.UserManagerController;
import app.logic.pojo.ComparatorFriends;
import app.logic.pojo.FriendInfo;
import app.logic.pojo.FriendsInfoExt;
import app.logic.pojo.UserInfo;
import app.logic.pojo.YYChatRoomInfo;
import app.utils.common.Listener;
import app.yy.geju.R;

/*
 * GZYY    2016-12-13  上午10:38:47
 * author: zsz
 * 
 * 
 * 这个类迟早会被抛弃掉的。。。。warning
 */

public class FriendsListActivity2 extends ActActivity implements OnItemClickListener ,QLXListView.IXListViewListener{

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
    private ComparatorFriends comparatorFriends;
    private Resources resources;
    private List<FriendsInfoExt> datas = new ArrayList<FriendsInfoExt>();
    private List<FriendsInfoExt> selectDatas = new ArrayList<FriendsInfoExt>();
    private List<UserInfo> selectUserInfos = new ArrayList<UserInfo>();
    private List<UserInfo> userInfosDatas = new ArrayList<UserInfo>();
    private boolean selectStatus = false;
    private boolean delectStatus = false , addStatus = false , roomAll =false;
    private boolean showCheckBoxStatus = true;
    private Gson gson;
    private String chatRoom_id;

    private Button setBut ;
    private LinearLayout ariView ;

    private YYBaseListAdapter<FriendsInfoExt> mAdapter = new YYBaseListAdapter<FriendsInfoExt>(this) {

        @Override
        public View createView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(FriendsListActivity2.this).inflate(R.layout.item_selectable_view, null);
                saveView("item_index_tv", R.id.item_index_tv, convertView);
                saveView("selected_item_imgview", R.id.selected_item_imgview, convertView);
                saveView("selected_item_tv", R.id.selected_item_tv, convertView);
                saveView("selected_item_cb", R.id.selected_item_cb, convertView);
            }
            FriendsInfoExt info = getItem(position);
            if (info != null) {

                TextView index_tv = (TextView) getViewForName("item_index_tv", convertView);
                CheckBox box = (CheckBox) getViewForName("selected_item_cb", convertView);
                int section = getSectionForPosition(position);
                if (position == getPositionForSection(section)) {
                    index_tv.setText(info.getSortLetters());
                    index_tv.setVisibility(View.VISIBLE);
                } else {
                    index_tv.setVisibility(View.GONE);
                }
                String _name = "";
                String url = "";
                if (delectStatus) {   //删除聊天室成员状态下
                    UserInfo userInfo = info.getUserInfo();
                    //_name = !TextUtils.isEmpty(userInfo.getNickName()) ? userInfo.getNickName() : userInfo.getName();
                    _name = !TextUtils.isEmpty(userInfo.getFriend_name()) ? userInfo.getFriend_name() : userInfo.getNickName();
                    url = HttpConfig.getUrl(userInfo.getPicture_url());

                } else if(roomAll){  //点击聊天室所有成员跳转过来的
                    UserInfo userInfo = info.getUserInfo();
                    _name = !TextUtils.isEmpty(userInfo.getFriend_name())?userInfo.getFriend_name():userInfo.getNickName();
                    url = HttpConfig.getUrl(userInfo.getPicture_url());
                }else{              //添加聊天室成员或是发起群聊的状态下
                    _name = info.getFriendInfo().getFriend_name() == null || TextUtils.isEmpty(info.getFriendInfo().getFriend_name()) ? info.getFriendInfo().getNickName() : info.getFriendInfo()
                            .getFriend_name();
                    url = HttpConfig.getUrl(info.getFriendInfo().getPicture_url());
                }

                setImageToImageViewCenterCrop(url, "selected_item_imgview", -1, convertView);
                setTextToViewText(_name, "selected_item_tv", convertView);
                box.setChecked(info.isCheck());
                box.setVisibility(showCheckBoxStatus ? View.VISIBLE : View.INVISIBLE);  //只有在聊天室全体成员时不显示复选框
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

    }

    @Override
    protected void onResume() {
        super.onResume();
        if( addStatus ){  //添加成员下，获取朋友列表
            getFriendsListDatas();
        }else if( roomAll ){  //获取聊天室成员信息
            getChatRoomDatas();
        }
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
        ((TextView) titleHandler.getLeftLayout().findViewById(R.id.left_tv)).setText(getIntent().getStringExtra(kTITLE));
        setBut = titleHandler.getRightDefButton();
        setBut.setVisibility( View.GONE );      //一开始先不显示
        setBut.setText("确定");
        setBut.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                saveInfosToExit();
            }
        });
    }

    /**
     * 初始化view
     */
    private void initView() {
        ariView = (LinearLayout) findViewById(R.id.empty_view);
        search_bg = (LinearLayout) findViewById(R.id.search_bg);
        search_bg.setBackgroundColor(resources.getColor(R.color.white));
        search_edt = (EditText) findViewById(R.id.search_edt);
        search_edt.setHint("手机号/昵称");
        search_edt_bg = (LinearLayout) findViewById(R.id.search_edt_bg);
        search_edt_bg.setBackgroundDrawable(resources.getDrawable(R.drawable.shape_search_edt_bg));
        listView = (QLXListView) findViewById(R.id.friends_list_view);
        listView.setAdapter(mAdapter);
        characterParser = CharacterParser.getInstance();
        comparatorFriends = new ComparatorFriends();
        search_edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyString = s.toString();
                if (!TextUtils.isEmpty(keyString)) {
                    selectStatus = true;
                    selectInfos(keyString);
                } else {
                    for (FriendsInfoExt infoExt : datas) {
                        if (infoExt.isCheck()) {
                            setBut.setVisibility( View.VISIBLE );
                            break;
                        }else{
                            setBut.setVisibility( View.GONE );
                        }
                    }
                    selectStatus = false;
                    mAdapter.setDatas(datas);
                }
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
        roomAll = getIntent().getBooleanExtra( ROOMALL , false );   //来自群组全部成员的标志
        delectStatus = getIntent().getBooleanExtra( DEL , false );   //删除成员状态下
        addStatus = getIntent().getBooleanExtra( ADD , false );      //添加成员状态下
        chatRoom_id = getIntent().getStringExtra(CHATROOM_ID);
        // 删除,和显示
        String userInfoDatasString = getIntent().getStringExtra(DETELE_FRIENDS);

        // ---------仅显示 所有成员是不显示右上角按钮---------------
        if (getIntent().getStringExtra(IS_SHOW_CHECKBOX) != null) {
            showCheckBoxStatus = false;
            setBut.setVisibility(View.INVISIBLE);
        }
        // -----------------------------
        if (userInfoDatasString != null) {  //删除状态下
            //delectStatus = true;
            List<UserInfo> userInfosDatasTemp = gson.fromJson(userInfoDatasString, new TypeToken<List<UserInfo>>() {}.getType());
            datas.clear();
            datas.addAll(fillSortDatasUserInfos(userInfosDatasTemp));
            mAdapter.setDatas(datas);
            if (showCheckBoxStatus) {
                setBut.setText("删除");
            }
        }

        // 增加
        String userInfoString = getIntent().getStringExtra(ADD_FRIENDS);
        if (userInfoString != null) {
            selectUserInfos.clear();
            List<UserInfo> userInfosTemp = gson.fromJson(userInfoString, new TypeToken<List<UserInfo>>() {}.getType());
            if (userInfosTemp != null && userInfosTemp.size() > 0) {
                selectUserInfos.addAll(userInfosTemp);
            }
        }
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

    private void getFriendsListDatas() {
        showWaitDialog();
        UserManagerController.getFriendsList(this, new Listener<List<FriendInfo>, List<FriendInfo>>() {
            @Override
            public void onCallBack(List<FriendInfo> request, List<FriendInfo> reply) {
                dismissWaitDialog();
                listView.stopRefresh();
                datas.clear();
                if (reply != null && reply.size() > 0) {
                    // TODO
                    // 筛选已存在的
                    List<FriendInfo> tempFriendInfos = new ArrayList<FriendInfo>();
                    for (FriendInfo friendInfo : reply) {
                        boolean status = true;
                        for (UserInfo userInfo : selectUserInfos) {
                            if (friendInfo.getWp_friends_info_id() != null &&
                                    friendInfo.getWp_friends_info_id().equals(userInfo.getWp_member_info_id()))
                            {
                                status = false;
                                break;
                            }
                        }
                        if (status && friendInfo.isRequest_accept() ) {
                            tempFriendInfos.add(friendInfo);
                        }
                    }
                    datas.addAll(fillSortDatas(tempFriendInfos));

                }else{
                    QLToastUtils.showToast( FriendsListActivity2.this , "数据获取失败，请重新加载");
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

    /**
     * 获取数据
     * <p>
     * getDatasFriendsListActivity2
     */

    private void getChatRoomDatas() {
        showWaitDialog();
        ChatRoomController.getChatRoomInfo(this, chatRoom_id, new Listener<Void, YYChatRoomInfo>() {
            @Override
            public void onCallBack(Void status, YYChatRoomInfo reply) {
                dismissWaitDialog();
                listView.stopRefresh();
                datas.clear();
                if (reply != null) {
                    List<UserInfo> list= getRoomMember( reply.getCr_memberList() );
                    datas.addAll(fillSortDatasUserInfos( list));
                }else{
                    //root_view.setVisibility(View.GONE);
                    QLToastUtils.showToast( FriendsListActivity2.this , "数据加载失败，请重新加载");
                }
                mAdapter.setDatas(datas);
            }
        });
    }

    /**
     * 处理联系人数据，以及增加排序
     *
     * @param list
     * @return fillSortDatasFriendsListActivity2
     */
    private List<FriendsInfoExt> fillSortDatas(List<FriendInfo> list) {
        List<FriendsInfoExt> temp = new ArrayList<FriendsInfoExt>();
        for (FriendInfo info : list) {
            FriendsInfoExt infoExt = new FriendsInfoExt();
            infoExt.setFriendInfo(info);
            String nameString = info.getFriend_name() == null || TextUtils.isEmpty(info.getFriend_name()) ? info.getNickName() : info.getFriend_name();
            infoExt.setName(nameString);
            String pinyinString = characterParser.getSelling(nameString);
            String sortString = pinyinString.substring(0, 1).toUpperCase();
            if (sortString.matches("[A-Z]")) {  //!"unknown".equals(pinyinString)&&
                infoExt.setSortLetters(sortString.toUpperCase());
            } else {
                infoExt.setSortLetters("#");
            }
            if(nameString!=null && "昵".equals(nameString.substring(0,1))){  //对“昵”字做特殊处理（目前没有找到更好的库）
                infoExt.setSortLetters("N");
            }
            temp.add(infoExt);
        }
        Collections.sort(temp, comparatorFriends);
        return temp;
    }

    /**
     * 处理群聊人员，以及增加排序
     *
     * @param list
     * @return fillSortDatasUserInfosFriendsListActivity2
     */
    private List<FriendsInfoExt> fillSortDatasUserInfos(List<UserInfo> list) {
        List<FriendsInfoExt> temp = new ArrayList<FriendsInfoExt>();
        for (UserInfo info : list) {
            FriendsInfoExt infoExt = new FriendsInfoExt();
            infoExt.setUserInfo(info);
            String nameString = info.getFriend_name()==null||TextUtils.isEmpty(info.getFriend_name()) ? info.getNickName() : info.getFriend_name();
            infoExt.setName(nameString);
            String pinyinString = characterParser.getSelling(nameString);
            if (!TextUtils.isEmpty(pinyinString)) {
                String sortString = pinyinString.substring(0, 1).toUpperCase();
                if (sortString.matches("[A-Z]")) { //!"unknown".equals(pinyinString)&&
                    infoExt.setSortLetters(sortString.toUpperCase());
                } else {
                    infoExt.setSortLetters("#");
                }
            } else {
                infoExt.setSortLetters("#");
            }
            if(nameString!=null && "昵".equals(nameString.substring(0,1))){  //对“昵”字做特殊处理（目前没有找到更好的库）
                infoExt.setSortLetters("N");
            }

            temp.add(infoExt);
        }
        Collections.sort(temp, comparatorFriends);
        return temp;

    }

    /**
     * 退出，保存操作的数据
     * <p>
     * saveInfosToExitFriendsListActivity2
     */
    private void saveInfosToExit() {
        if (delectStatus) {
            chatRoom_id = getIntent().getStringExtra(CHATROOM_ID);
            List<UserInfo> removeList = new ArrayList<>();
            for (FriendsInfoExt infoExt : datas) {
                if (infoExt.isCheck()) {
                    removeList.add(infoExt.getUserInfo());
                }
            }
            if (removeList.size() < 1) {
                finish();
            }
            Intent intent = new Intent();
            intent.putExtra(kSELECTED_ITEMS_JSON_STRING, gson.toJson(removeList));
            setResult(Activity.RESULT_OK, intent);
            finish();

//            removeUserInfos(chatRoom_id);
        } else {
            List<FriendInfo> save = new ArrayList<FriendInfo>();
            for (FriendsInfoExt info : datas) {
                if (info.isCheck()) {
                    save.add(info.getFriendInfo());
                }
            }
            String result = gson.toJson(save);
            Intent intent = new Intent();
            intent.putExtra(kSELECTED_ITEMS_JSON_STRING, result);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }

    }

    private void removeUserInfos(String cr_id) {
        StringBuffer buffer = new StringBuffer();
        for (FriendsInfoExt infoExt : datas) {
            if (infoExt.isCheck()) {
                buffer.append(infoExt.getUserInfo().getWp_member_info_id() + ",");
            }
        }
        if (buffer.toString().length() <= 0) {
            finish();
        } else {
            buffer.deleteCharAt(buffer.toString().length() - 1);
            if (buffer.toString().length() > 5) {
                showWaitDialog();
                ChatRoomController.removeMemberFromChatRoom(this, buffer.toString(), cr_id, new Listener<Integer, String>() {

                    @Override
                    public void onCallBack(Integer status, String reply) {
                        dismissWaitDialog();
                        if (status == 1) {
                            QLToastUtils.showToast(FriendsListActivity2.this, "移除成功");
                            finish();
                            return;
                        }
                        QLToastUtils.showToast(FriendsListActivity2.this, reply);

                    }
                });
            }
        }
    }

    /**
     * 检索
     *
     * @param keyString selectInfosFriendsListActivity2
     */
    private void selectInfos(String keyString) {

        selectDatas.clear();
        if (delectStatus) {
            for (FriendsInfoExt info : datas) {
                UserInfo userInfo = info.getUserInfo();
                if ((userInfo.getPhone()!=null&&userInfo.getPhone().contains(keyString)) || (userInfo.getNickName()!=null&&userInfo.getNickName().contains(keyString)) ||
                        (userInfo.getName()!=null&&userInfo.getName().contains(keyString))||(userInfo.getFriend_name()!=null&&userInfo.getFriend_name().contains(keyString))) {
                    selectDatas.add(info);
                }
            }

        } else if(addStatus){
            for (FriendsInfoExt info : datas) {
                FriendInfo infoFriendInfo = info.getFriendInfo();
                if ((infoFriendInfo.getPhone()!=null&&infoFriendInfo.getPhone().contains(keyString)) || (infoFriendInfo.getNickName()!=null&&infoFriendInfo.getNickName().contains(keyString)) ||
                        (infoFriendInfo.getFriend_name()!=null&&infoFriendInfo.getFriend_name().contains(keyString))) {
                    selectDatas.add(info);
                }
            }
        }else{
            for (FriendsInfoExt info : datas) {
                UserInfo userInfo = info.getUserInfo();
                if ((userInfo.getPhone()!=null&&userInfo.getPhone().contains(keyString)) || (userInfo.getNickName()!=null&&userInfo.getNickName().contains(keyString)) ||
                        (userInfo.getName()!=null&&userInfo.getName().contains(keyString))||(userInfo.getFriend_name()!=null&&userInfo.getFriend_name().contains(keyString))) {
                    selectDatas.add(info);
                }
            }
        }
        if( selectDatas.size() == 0 || roomAll){
            setBut.setVisibility(View.GONE);
        }else{
            setBut.setVisibility(View.VISIBLE);
        }
        Collections.sort(selectDatas, comparatorFriends);
        mAdapter.setDatas(selectDatas);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(roomAll){
            FriendsInfoExt infoExt = (FriendsInfoExt) parent.getItemAtPosition( position);
            UserInfo userInfo ;
            if( infoExt != null &&  (userInfo = infoExt.getUserInfo())!= null ){
                Intent intent = new Intent();
                intent.setClass( this ,PreviewFriendsInfoActivity.class);
                intent.putExtra(PreviewFriendsInfoActivity.kUSER_MEMBER_ID , userInfo.getWp_member_info_id());
                startActivity( intent);
            }
            return;
        }
        if (!selectStatus) {
            boolean status = datas.get(position - 1).isCheck();
            datas.get(position - 1).setCheck(!status);
            for (FriendsInfoExt infoExt : datas) {
                if (infoExt.isCheck()) {
                    setBut.setVisibility( View.VISIBLE);
                    break;
                }else{
                    setBut.setVisibility( View.GONE);
                }
            }
        } else {
            FriendsInfoExt infoExt = selectDatas.get(position - 1);
            boolean status = infoExt.isCheck();
            selectDatas.get(position - 1).setCheck(!status);
            for (FriendsInfoExt ext : selectDatas) {
                if (ext.isCheck()) {
                    setBut.setVisibility( View.VISIBLE);
                    break;
                }else{
                    setBut.setVisibility( View.GONE);
                }
            }
            if( delectStatus ){ //点击删除按钮图片
                for (int i = 0; i < datas.size(); i++) {
                    if( infoExt.getUserInfo().getWp_member_info_id().equals( datas.get(i).getUserInfo().getWp_member_info_id())){
                        datas.get(i).setCheck(!status);
                    }
                }
            }else{              //点击添加按钮图片
                for (int i = 0; i < datas.size(); i++) {
                    if( infoExt.getFriendInfo().getWp_friends_info_id().equals( datas.get(i).getFriendInfo().getWp_friends_info_id())){
                        datas.get(i).setCheck(!status);
                    }
                }
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 过滤群成员集合
     * @param userInfos
     * @return
     */
    private List<UserInfo> getRoomMember( List<UserInfo> userInfos){
        if(userInfos == null ){
            return null ;
        }
        List<UserInfo> roomMemberList = new ArrayList<>();
        for( int i = 0 ; i< userInfos.size() ; i++){
            if( userInfos.get(i).getIs_remove()==0){  //0，还在此聊天室的， 1 ， 已被移除此聊天室的
                roomMemberList.add(userInfos.get(i));
            }
        }
        return roomMemberList ;
    }

    @Override
    public void onRefresh() {
        if( addStatus){
            search_edt.setText("");
            getFriendsListDatas();
        }else if( roomAll ){
            search_edt.setText("");
             getChatRoomDatas();
        }
    }

    @Override
    public void onLoadMore() {

    }
}
