package app.logic.activity.chat;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.UserManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.easeui.EaseConstant;

import org.ql.activity.customtitle.ActActivity;
import org.ql.utils.QLToastUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import app.config.DemoApplication;
import app.config.http.HttpConfig;
import app.logic.activity.ActTitleHandler;
import app.logic.activity.friends.FriendsListActivity2;
import app.logic.activity.main.HomeActivity;
import app.logic.activity.org.DPMDetailsForEditActivity2;
import app.logic.activity.user.PreviewFriendsInfoActivity;
import app.logic.adapter.YYBaseListAdapter;
import app.logic.controller.ChatRoomController;
import app.logic.controller.UserManagerController;
import app.logic.pojo.FriendInfo;
import app.logic.pojo.IntentInfo;
import app.logic.pojo.UserInfo;
import app.logic.pojo.YYChatRoomInfo;
import app.logic.roomavatar.CreatAvatar;
import app.logic.roomavatar.NetControl;
import app.utils.common.Listener;
import app.utils.helpers.QRHelper;
import app.utils.helpers.YYUtils;
import app.view.DialogNewStyleController;
import app.yy.geju.R;

/**
 * SiuJiYung create at 2016年7月1日 下午3:05:41
 * <p>
 * 聊天室信息页面
 */

public class ChatRoomInfoActivity extends ActActivity implements OnClickListener, OnItemClickListener {

    public static final String kChatRoomID = "kChatRoomID";
    public static final String kChatRoomInfoID = "kChatRoomInfoID";
    public static final String kChatCRID = "kChatCRID";
    private GridView memberGridView ;
    private TextView chatRoomNumTv  ,chatRoomName ;
    private YYChatRoomInfo roomInfo;
    private UserInfo userInfo;
    private String chat_room_id , cr_member_id;
    private QRHelper qrHelper;
    private Button exitButton;
    private int nullCount = 0;
    private boolean isCreat = false;
    private int removeCount = 0;
    private Gson gson;
    private boolean isLord = false ;//是否是群主的标志

    private List<UserInfo> qrList = new ArrayList<>();
    private NetControl mNetControl ;
    private CreatAvatar mCreatAvatar ;
    private List<Bitmap> bitmaps = new ArrayList<>();
    private int number = 0 ;

//    private View dialog_view ;
//    private ImageView qrcode ,pic ;
//    private Dialog qrDialog ;
//    private TextView nick ;
    private Bitmap avariBitMap ;
    private View root_view ,chat_room_name_ll;


    private YYBaseListAdapter<UserInfo> mAdapter = new YYBaseListAdapter<UserInfo>(this) {
        @Override
        public View createView(final int position, View convertView, ViewGroup parent) {
            if (getItemViewType(position) == 0) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.view_chatroom_member, null);
                    saveView("chatroom_member_headview", R.id.chatroom_member_headview, convertView);
                    saveView("funcation_item_title_tv", R.id.funcation_item_title_tv, convertView);
                }
                UserInfo info = getItem(position);
                if (info != null) {
                    String url = HttpConfig.getUrl(info.getPicture_url());
                    setImageToImageViewCenterCrop(url, "chatroom_member_headview", -1, convertView);
                    String nameString = info.getFriend_name() == null || TextUtils.isEmpty(info.getFriend_name()) ? info.getNickName() : info.getFriend_name();
                    setTextToViewText(nameString, "funcation_item_title_tv", convertView);
                }
            } else if (getItemViewType(position) == 2) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.view_chatroom_member, null);
                    saveView("chatroom_member_headview", R.id.chatroom_member_headview, convertView);
                    saveView("funcation_item_title_tv", R.id.funcation_item_title_tv, convertView);
                    saveView("delect_iv", R.id.delect_iv, convertView);
                }
                final UserInfo info = getItem(position);
                if (info != null) {
                    String url = HttpConfig.getUrl(info.getPicture_url());
                    setImageToImageViewCenterCrop(url, "chatroom_member_headview", -1, convertView);
                    String nameString = info.getFriend_name() == null || TextUtils.isEmpty(info.getFriend_name()) ? info.getNickName() : info.getFriend_name();
                    setTextToViewText(nameString, "funcation_item_title_tv", convertView);
                    ImageView delect_iv = getViewForName("delect_iv", convertView);
                    // delect_iv.setVisibility(View.VISIBLE);
                    delect_iv.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // QLToastUtils.showToast(ChatRoomInfoActivity.this,
                            // String.valueOf(position));
                            removeChatRoomMember(info.getWp_member_info_id(), true, position);
                        }
                    });
                }
            } else if (getItemViewType(position) == 1) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.view_chatroom_member, null);
                    saveView("chatroom_member_headview", R.id.chatroom_member_headview, convertView);
                    saveView("funcation_item_title_tv", R.id.funcation_item_title_tv, convertView);
                    saveView("delect_iv", R.id.delect_iv, convertView);
                }
                UserInfo info = getItem(position);
                if (info != null) {
                    String url = HttpConfig.getUrl(info.getPicture_url());
                    setImageToImageViewCenterCrop(url, "chatroom_member_headview", -1, convertView);
                    String nameString = info.getFriend_name() == null || TextUtils.isEmpty(info.getFriend_name()) ? info.getNickName() : info.getFriend_name();
                    setTextToViewText(nameString, "funcation_item_title_tv", convertView);
                    ImageView delect_iv = getViewForName("delect_iv", convertView);
                    delect_iv.setVisibility(View.INVISIBLE);
                }
            } else if (getItemViewType(position) == 4) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.view_chatroom_member, null);
                    saveView("chatroom_member_headview", R.id.chatroom_member_headview, convertView);
                    saveView("funcation_item_title_tv", R.id.funcation_item_title_tv, convertView);
                }
                UserInfo info = getItem(position);
                if (info != null) {
                    ImageView iView = getViewForName("chatroom_member_headview", convertView);
                    iView.setImageResource(R.drawable.icon_delect_item);// emptyiem
                }
            } else {
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.view_chatroom_member, null);
                    saveView("chatroom_member_headview", R.id.chatroom_member_headview, convertView);
                    saveView("funcation_item_title_tv", R.id.funcation_item_title_tv, convertView);
                }
                UserInfo info = getItem(position);
                if (info != null) {
                    ImageView iView = getViewForName("chatroom_member_headview", convertView);
                    iView.setImageResource(R.drawable.icon_add_item);// emptyiem
                }
            }
            return convertView;
        }

        public int getItemViewType(int position) {
            UserInfo info = getItem(position);
            if (TextUtils.isEmpty(info.getPhone()) && info.isLastItem()) {
                return 4;
            } else if (info.getPhone() == null || TextUtils.isEmpty(info.getPhone())) {
                return 3;// 最后一张
            } else if (info.getWp_member_info_id().equals(roomInfo.getCr_creatorId())) {
                return 0;// 创造者
            } else if (info.isShowDelect()) {
                return 2;// 显示删除
            } else {
                return 1;// 不显示删除
            }

        }
        public int getViewTypeCount() {
            return 5;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActTitleHandler handler = new ActTitleHandler();
        setAbsHandler(handler);
        setContentView(R.layout.activity_chatroom_info2);
        userInfo = UserManagerController.getCurrUserInfo();
        gson = new Gson();
        showWaitDialog();
        handler.getRightLayout().setVisibility(View.INVISIBLE);
        handler.replaseLeftLayout(this, true);
        ((TextView) handler.getLeftLayout().findViewById(R.id.left_tv)).setText("群聊信息");
        handler.getLeftLayout().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setTitle("");
        memberGridView = (GridView) findViewById(R.id.yy_chatroom_gridview);
        memberGridView.setOnItemClickListener(this);
        memberGridView.setAdapter(mAdapter);
        root_view = findViewById(R.id.root_view);
        chatRoomName = (TextView) findViewById(R.id.yy_chatroom_name_tv);
        findViewById(R.id.yy_chatroom_all_member_tv).setOnClickListener(this);
        findViewById(R.id.yy_chatroom_ql_view).setOnClickListener(this);
        exitButton = (Button) findViewById( R.id.yy_chatroom_exit_btn );
        exitButton.setOnClickListener(this);
        qrHelper = new QRHelper();
        chat_room_id = getIntent().getStringExtra(kChatRoomInfoID);
//        cr_member_id = getIntent().getStringExtra(kChatCRID);
        //当前用户是群的创建者时
        chat_room_name_ll = findViewById( R.id.chat_room_name_ll);
        chat_room_name_ll.setOnClickListener( this );
        //初始化修改部门昵称对话框
        intiRoomNameDialog();
    }

    private void intiDloag(){
//        dialog_view = getLayoutInflater().inflate(R.layout.dialog_chatroominfo_qrode, null);
//        qrcode = (ImageView) dialog_view.findViewById(R.id.qrcode);
//        pic = (ImageView) dialog_view.findViewById(R.id.im_personpic);
//        nick = (TextView) dialog_view.findViewById(R.id.tx_name);
//        Point size = YYUtils.getDisplaySize(this);
//        LayoutParams p2 = new LayoutParams((int) (size.x * 0.70), (int) (size.y * 0.53));
//        qrDialog = new Dialog(this, R.style.dialog);
//        qrDialog.setContentView(dialog_view, p2);
    }

    private void refrashUI(YYChatRoomInfo info) {
        if(info ==null){
            return;
        }
        removeCount = 0;
        roomInfo = info;
        if(roomInfo.getCr_creatorId().equals(UserManagerController.getCurrUserInfo().getWp_member_info_id())){
            isLord = true;
        }else{
            isLord = false ;
        }
        chatRoomName.setText(info.getCr_name());
        UserInfo myInfo = UserManagerController.getCurrUserInfo();
        ((TextView) findViewById(R.id.yy_chatroom_nick_name_tv)).setText(myInfo.getNickName());
        int _number_of_item = roomInfo.getCr_memberList() == null ? 0 : roomInfo.getCr_memberList().size();
        _number_of_item++;
        int _line = _number_of_item / 5;
        int _h = _line * 70 + (_line - 1) * 5;
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, _h);
        // memberGridView.setLayoutParams(lp);
        if (roomInfo.getCr_creatorId().equals(userInfo.getWp_member_info_id())) {   //当前用户是这个群组的群主
            isCreat = true;
            exitButton.setText("移除聊天室");
        } else {
            exitButton.setText("退出群聊");
        }
        List<UserInfo> roomMenmber = getRoomMember(roomInfo.getCr_memberList()) ;
        List<UserInfo> userInfos = createAdapterData(roomMenmber);
        int _member_count = info.getCr_memberList() == null ? 0 : (info.getCr_memberList().size() - nullCount);
        chatRoomNumTv = ((TextView) findViewById(R.id.yy_chatroom_all_member_tv));
        if(roomMenmber!=null){
            chatRoomNumTv.setText("全部群成员(" + roomMenmber.size() + ")");
        }else{
            chatRoomNumTv.setText("全部群成员(" + 0 + ")");
        }

        mAdapter.setDatas(userInfos);
        countGridViewHeight(userInfos);

        String url = HttpConfig.getUrl(roomInfo.getCr_picture());
        System.out.println("URL = "+ url );
        //流转化BitMap图片
        fromNet(roomInfo.getCr_picture());
        dismissWaitDialog();
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        //获取群组信息
        getChatRoomInfo(chat_room_id, false);
    }

    // 重新计算GridView 的高度
    private void countGridViewHeight(List<UserInfo> userInfos) {
        int num = userInfos.size();
        int totalHeight = 0;
        if (num < 4) {
            View itemView = mAdapter.getView(0, null, memberGridView);
            itemView.measure(0, 0);
            totalHeight += itemView.getMeasuredHeight();
        } else {
            int result = num / 4;
            for (int i = 0; i < result; i++) {
                View itemView = mAdapter.getView(i, null, memberGridView);
                itemView.measure(0, 0);
                totalHeight += itemView.getMeasuredHeight();
            }
            if (num % 4 > 0) {
                totalHeight += totalHeight / result;
            }
        }
        ViewGroup.LayoutParams params = memberGridView.getLayoutParams();
        params.height = totalHeight;
        memberGridView.setLayoutParams(params);
    }

    private List<UserInfo> createAdapterData(List<UserInfo> list) {
        if (list == null) {
            return null;
        }
        ArrayList<UserInfo> _tmpList = new ArrayList<UserInfo>();
        for (UserInfo info : list) {
            if (info.getPhone() == null || TextUtils.isEmpty(info.getPhone())) {
                nullCount++;
                continue;
            }
            if (isCreat && info.getWp_member_info_id() != userInfo.getWp_member_info_id()) {
                info.setShowDelect(true);
            }
            _tmpList.add(info);

        }
        UserInfo emptyItem = new UserInfo();
        _tmpList.add(emptyItem);
        // 增加最后一个
        if (isCreat) {
            UserInfo lastInfo = new UserInfo();
            lastInfo.setLastItem(true);
            _tmpList.add(lastInfo);
        }

        return _tmpList;
    }

    /**
     * 解散群
     */
    private void removeChatRoom() {
        ChatRoomController.removeChatRoom(this, roomInfo.getCr_id(), new Listener<Integer, String>() {
            @Override
            public void onCallBack(Integer status, String reply) {
                if (status == 1) {
                    Intent intent = new Intent(ChatRoomInfoActivity.this, ChatRoomListActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else if (reply != null) {
                    QLToastUtils.showToast(ChatRoomInfoActivity.this, reply);
                } else {
                    QLToastUtils.showToast(ChatRoomInfoActivity.this, "解散失败，请重试");
                }
            }
        });
    }

    private void getChatRoomInfo(String cr_id, boolean showWaitingDialog) {
        if (showWaitingDialog) {
            showWaitDialog();
        }
        ChatRoomController.getChatRoomInfo(this, cr_id, new Listener<Void, YYChatRoomInfo>() {
            @Override
            public void onCallBack(Void status, YYChatRoomInfo reply) {
                dismissWaitDialog();
                if (reply != null) {
                    refrashUI(reply);
                }else{
                    root_view.setVisibility(View.GONE);
                    QLToastUtils.showToast( ChatRoomInfoActivity.this , "数据加载失败，请重新加载");
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int vid = v.getId();
        UserInfo userInfo = UserManagerController.getCurrUserInfo();
        switch (vid) {
            case R.id.yy_chatroom_all_member_tv:
                if (roomInfo == null) {
                    return;
                }
                gson = new Gson();
                List<UserInfo> roomMember = getRoomMember(roomInfo.getCr_memberList());
                String roomInfo_josn = gson.toJson(roomMember);
                // Intent memberListIntent = new Intent();
                // memberListIntent.setClass(ChatRoomInfoActivity.this,
                // ChatRoomMemberListActivity.class);
                // memberListIntent.putExtra(ChatRoomMemberListActivity.kROOM_INFO,roomInfo_josn);
                // startActivity(memberListIntent);
                Intent memberIntent = new Intent(ChatRoomInfoActivity.this, FriendsListActivity2.class);
                //memberIntent.putExtra(FriendsListActivity2.DETELE_FRIENDS, roomInfo_josn);
                memberIntent.putExtra(FriendsListActivity2.IS_SHOW_CHECKBOX, "true");
                if(roomMember!=null){
                    memberIntent.putExtra(FriendsListActivity2.kTITLE, "全部群成员("+roomMember.size()+"人)");
                }else{
                    memberIntent.putExtra(FriendsListActivity2.kTITLE, "全部群成员("+0+"人)");
                }
                memberIntent.putExtra(FriendsListActivity2.ROOMALL , true);//chat_room_id
                memberIntent.putExtra(FriendsListActivity2.CHATROOM_ID ,  roomInfo.getCr_id() );
                startActivity(memberIntent);
                break;
            case R.id.yy_chatroom_exit_btn:
                // 退出
                if (roomInfo != null && roomInfo.getCr_creatorId() != null) {
                    // 管理员,解散群
                    if (userInfo.getWp_member_info_id().equals(roomInfo.getCr_creatorId())) {
                        // removeChatRoom();
                        showExitDialog("注意", "确定移除聊天室？", userInfo, true);
                    } else {
                        // removeChatRoomMember(userInfo.getWp_member_info_id(),
                        // false);
                        showExitDialog("注意", "确定退出群聊？", userInfo, false);
                    }
                }

                break;
            case R.id.yy_chatroom_name_tv:
                if (roomInfo != null && roomInfo.getCr_creatorId() != null && roomInfo.getCr_creatorId().equals(userInfo.getWp_member_info_id())) {
                    // 管理员，修改名字
                }
                break;
            case R.id.yy_chatroom_ql_view:
                // 二维码
                showQRCode();
                break;
            case R.id.chat_room_name_ll:
                // 修改群名称
                if( isLord ){
                    if(roomInfo!=null){
                        contentEdt.setText(roomInfo.getCr_name());
                    }
                    roomNameDialog.show();
                }else{
                    QLToastUtils.showToast( this , "您不是群主");
                }

                break;
        }
    }

    private void showQRCode() {
        showWaitDialog();
        if (roomInfo == null) {
            QLToastUtils.showToast(this, "未获取到群信息，请稍后再试");
            return;
        }
        Gson gson = new Gson();
        String roomInfo_json = gson.toJson(roomInfo);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int imgWidth = (int) dm.scaledDensity * 200;
        int imgHeight = (int) dm.scaledDensity * 200;

        Bitmap qriBitmap = qrHelper.createQRImage("cr_id:" + roomInfo.getCr_id(), imgWidth, imgHeight);
        Point size = YYUtils.getDisplaySize(this);
        LayoutParams p2 = new LayoutParams((int) (size.x * 0.70), ViewGroup.LayoutParams.WRAP_CONTENT); /*(int) (size.y * 0.53)*/
        View dialog_view = getLayoutInflater().inflate(R.layout.dialog_chatroominfo_qrode, null);
        ImageView qrcode = (ImageView) dialog_view.findViewById(R.id.qrcode);
        SimpleDraweeView pic = (SimpleDraweeView) dialog_view.findViewById(R.id.im_personpic);
        TextView nick = (TextView) dialog_view.findViewById(R.id.tx_name);
        if(avariBitMap !=null){
            pic.setImageBitmap(avariBitMap);
        }else{
            pic.setImageBitmap( BitmapFactory.decodeResource(this.getResources(),R.drawable.default_user_icon));
        }
        nick.setText(roomInfo.getCr_name());
        qrcode.setImageBitmap(showHeaderQRcodeBitmap(pic, "cr_id:" + roomInfo.getCr_id(), imgWidth, imgHeight));
        Dialog qrDialog = new Dialog(this, R.style.dialog);
        qrDialog.setContentView(dialog_view, p2);
        qrDialog.show();
        dismissWaitDialog();
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

    private void addMembersToChatRoom(List<FriendInfo> list) {
        if (list == null || list.size() < 1) {
            return;
        }
        showWaitDialog();
        StringBuilder sBuilder = new StringBuilder();
        for (int idx = 0; idx < list.size(); idx++) {
            FriendInfo info = list.get(idx);
            sBuilder.append(info.getWp_friends_info_id());
            if (idx < list.size() - 1) {
                sBuilder.append(",");
            }
        }
        ChatRoomController.addMemberToChatRoom(this, chat_room_id, sBuilder.toString(), new Listener<Integer, String>() {
            @Override
            public void onCallBack(Integer status, String reply) {
                dismissWaitDialog();
                if (status == 1) {
                    getChatRoomInfo(chat_room_id, false);
                } else {
                    String msg = reply == null ? "添加成员失败" : reply;
                    QLToastUtils.showToast(ChatRoomInfoActivity.this, msg);
                }
            }
        });
    }

    // 移除聊天室成员
    private void removeChatRoomMember(String member_id, final boolean isCreator, final int position) {
        ChatRoomController.removeMemberFromChatRoom(this, member_id, roomInfo.getCr_id(), new Listener<Integer, String>() {

            @Override
            public void onCallBack(Integer status, String reply) {
                if (status == 1) {
                    if (isCreator) {
                        getChatRoomInfo(chat_room_id, true);
                        // mAdapter.removeItemAt(position);
                        // mAdapter.notifyDataSetChanged();
                    }
                } else {
                    QLToastUtils.showToast(ChatRoomInfoActivity.this, reply);
                }
            }
        });
    }

    // 显示dialog
    private void showExitDialog(String title, String content, final UserInfo userInfo, final boolean status) {
        View view = LayoutInflater.from(ChatRoomInfoActivity.this).inflate(R.layout.dialog_new_notitle_view, null);
        final DialogNewStyleController dialog = new DialogNewStyleController(this, view);
        TextView contentTv = (TextView) view.findViewById(R.id.dialog_content_tv);
        Button deleteBtn = (Button) view.findViewById(R.id.dialog_cancel_btn);
        Button cancelBtn = (Button) view.findViewById(R.id.dialog_true_btn);
        deleteBtn.setText("确定");
        cancelBtn.setText("取消");
        contentTv.setText(content);
        deleteBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status) {
                    removeChatRoom();
                } else {
                    removeChatRoomMember(userInfo.getWp_member_info_id(), false, -1);
                }
                Intent intent = new Intent();
                if (getIntent().getStringExtra(EaseConstant.FROM_ACTIVITY) == null) {
                    intent.setClass(ChatRoomInfoActivity.this, ChatRoomListActivity.class);
                } else {
                    intent.setClass(ChatRoomInfoActivity.this, HomeActivity.class);
                }

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                dialog.dismiss();
            }
        });
        cancelBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    // 仅更新群成员数量
    private void refrashChatRoomInfo(String cr_id) {
        ChatRoomController.getChatRoomInfo(this, cr_id, new Listener<Void, YYChatRoomInfo>() {
            @Override
            public void onCallBack(Void status, YYChatRoomInfo reply) {
                if (reply != null) {
                    int _member_count = reply.getCr_memberList() == null ? 0 : (reply.getCr_memberList().size() - nullCount);
                    chatRoomNumTv.setText("全部群成员(" + _member_count + ")");
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        UserInfo info = (UserInfo) arg0.getAdapter().getItem(arg2);
        if (info ==null)return;
        if (info.getWp_friends_info_id() == null && info.getWp_member_info_id() == null) {
            Intent intent = new Intent(this, FriendsListActivity2.class);
            List<UserInfo> roomMember = getRoomMember(roomInfo.getCr_memberList()) ;
            String userInfoString = gson.toJson(roomMember);
            if (info.isLastItem()) {
                intent.putExtra(FriendsListActivity2.DETELE_FRIENDS, userInfoString);
                intent.putExtra(FriendsListActivity2.CHATROOM_ID, roomInfo.getCr_id());
                if(roomMember!=null){
                    intent.putExtra(FriendsListActivity2.kTITLE, "聊天成员(" + roomMember.size() + ")");
                }else{
                    intent.putExtra(FriendsListActivity2.kTITLE, "聊天成员(" + 0 + ")");
                }
                intent.putExtra(FriendsListActivity2.ADD, false );
                intent.putExtra(FriendsListActivity2.DEL, true );  //删除成员状态下
                //startActivity(intent);
                startActivityForResult(intent, REMOVE_FRIENDS);
                return;
            }
            intent.putExtra(FriendsListActivity2.ADD, true );     //添加成员状态下
            intent.putExtra(FriendsListActivity2.DEL, false );
            intent.putExtra(FriendsListActivity2.ADD_FRIENDS, userInfoString);
            intent.putExtra(FriendsListActivity2.kTITLE, "选择联系人");
            startActivityForResult(intent, FriendsListActivity2.kSELECT_ITEMS);

        }else{
            Intent intent = new Intent(this, PreviewFriendsInfoActivity.class);
            intent.putExtra(PreviewFriendsInfoActivity.kUSER_MEMBER_ID, info.getWp_member_info_id());
            startActivity( intent );
        }
    }

    private static final int REMOVE_FRIENDS = 24;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == FriendsListActivity2.kSELECT_ITEMS) {
            if (data != null) {
                String datas = data.getStringExtra(FriendsListActivity2.kSELECTED_ITEMS_JSON_STRING);
                try {
                    List<FriendInfo> _items = gson.fromJson(datas, new TypeToken<List<FriendInfo>>() {}.getType());
                    addMembersToChatRoom(_items);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        }
        if (resultCode == Activity.RESULT_OK && requestCode == REMOVE_FRIENDS) {
            if (data != null) {
                String datas = data.getStringExtra(FriendsListActivity2.kSELECTED_ITEMS_JSON_STRING);
                List<UserInfo> _items = gson.fromJson(datas, new TypeToken<List<UserInfo>>() {}.getType());
                removeFriends(_items);
                return;
            }
        }
    }

    /**
     * 移除
     *
     * @param list
     */
    private boolean isAdmin = false;
    private void removeFriends(List<UserInfo> list) {
        if (list == null || list.size() < 1) {
            return;
        }

        StringBuilder builder = new StringBuilder();
        for (UserInfo info : list) {
            builder.append(info.getWp_member_info_id() + ",");
            if (info.getWp_member_info_id().equals(roomInfo.getCr_creatorId())) {
                isAdmin = true;
            }
        }
        builder.deleteCharAt(builder.toString().length() - 1);
        ChatRoomController.removeMemberFromChatRoom(this, builder.toString(), roomInfo.getCr_id(), new Listener<Integer, String>() {
            @Override
            public void onCallBack(Integer status, String reply) {
                dismissWaitDialog();
                if (status == 1) {
                    QLToastUtils.showToast(ChatRoomInfoActivity.this, "移除成功");
                    if (isAdmin) {
                        removeChatRoom();
                    } else {
                        getChatRoomInfo(chat_room_id, false);
                    }
                    return;
                }
                QLToastUtils.showToast(ChatRoomInfoActivity.this, reply);
            }
        });
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
            if( !TextUtils.isEmpty(userInfos.get(i).getPhone()) && userInfos.get(i).getIs_remove()==0){  //0，还在此聊天室的， 1 ， 已被移除此聊天室的
                roomMemberList.add(userInfos.get(i));
            }
        }
       return roomMemberList ;
    }

    /**
     * 网络获取图片
     * @param path
     */
    private void fromNet(final String path) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL( HttpConfig.getUrl( path ) );
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(8000);
                    conn.connect();
                    int code = conn.getResponseCode();
                    if ( code == HttpURLConnection.HTTP_OK ) {
                        InputStream inputStream = conn.getInputStream();
                        //把流解码成Bitmap
                        avariBitMap = BitmapFactory.decodeStream(inputStream);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    avariBitMap = BitmapFactory.decodeResource(ChatRoomInfoActivity.this.getResources() , R.drawable.ic_launcher);
                }
            }
        }).start();
    }


    //******************************************//
    private View contentView;
    private Button trueBtn,cancelBtn;
    private Dialog roomNameDialog;
    private EditText contentEdt ;
    /**
     * 初始化群名称对话框
     */
    private void intiRoomNameDialog(){
        contentView = LayoutInflater.from(this).inflate(R.layout.dialog_new_edit_view, null);
        TextView title = (TextView) contentView.findViewById(R.id.dialog_title_tv);
        contentEdt = (EditText) contentView.findViewById(R.id.dialog_content_edt);
        trueBtn = (Button) contentView.findViewById(R.id.dialog_true_btn);
        cancelBtn = (Button) contentView.findViewById(R.id.dialog_cancel_btn);

        title.setText("修改群昵称");
        if(roomInfo!=null){
            contentEdt.setText(roomInfo.getCr_name());
        }
        contentEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length()>8){
                    contentEdt.setText(s.subSequence(0,8).toString());
                    contentEdt.setSelection(8);
                    //隐藏键盘
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(contentEdt.getWindowToken(),0);
                    QLToastUtils.showToast( ChatRoomInfoActivity.this , "群昵称不能超过8位");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        roomNameDialog = new DialogNewStyleController(this, contentView);
        trueBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String roomName = contentEdt.getText().toString();
                if (!TextUtils.isEmpty(roomName)) {
                    //修改部门名称
                    if( roomInfo!=null ){
                        updataRoomName( roomInfo.getRoom_id() , roomName );
                    }
                    if(roomNameDialog.isShowing()){
                        roomNameDialog.dismiss();
                    }
                } else {
                    QLToastUtils.showToast(ChatRoomInfoActivity.this, "群昵称不能为空");
                }
            }
        });
        cancelBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(roomNameDialog.isShowing()){
                    roomNameDialog.dismiss();
                }
            }
        });
    }

    /**
     * 更新群的名称
     * @param roomNaem
     */
    private void updataRoomName(String room_id, final String roomNaem ){
        showWaitDialog();
        ChatRoomController.modifyRoomName(this, room_id , roomNaem , new Listener<Boolean, String>() {
            @Override
            public void onCallBack(Boolean aBoolean, String reply) {
                dismissWaitDialog();
                if(aBoolean ){
                    chatRoomName.setText(roomNaem);
                }else{
                    QLToastUtils.showToast( ChatRoomInfoActivity.this , reply);
                }
            }
        });
    }
}
