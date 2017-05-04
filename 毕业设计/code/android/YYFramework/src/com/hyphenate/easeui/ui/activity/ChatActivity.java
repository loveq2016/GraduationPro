package com.hyphenate.easeui.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import app.logic.activity.ActTitleHandler;
import app.logic.activity.chat.ChatRoomInfoActivity;
import app.logic.activity.main.HomeActivity;
import app.logic.activity.user.PreviewFriendsInfoActivity;
import app.yy.geju.R;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseBaseActivity;
import com.hyphenate.easeui.ui.EaseChatFragment;
import static com.hyphenate.easeui.EaseConstant.CHATTYPE_SINGLE;

public class ChatActivity extends EaseBaseActivity {
    public static final String FROM_ACT = "FROM_ACT";
    public static final String TAGUSERID = "TAGUSERID";
    public static final String kChatCRID = "kChatCRID";
    public static ChatActivity activityInstance;
    private EaseChatFragment chatFragment;
    String toChatUsername;
    private int chatType;
    private String targetMemberInfoID;
    private String roomInfoID;
    private String cr_id , cr_memberid_id;
    private Bundle tempBundle ;

    public static Activity activity ;
    public static final String REMARKS_NAME = "REMARKS_NAME";
    public static final String EMPTYUSER = "10000000000";
    public static final String ISFRIEND ="ISFRIEND";

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        ActTitleHandler handler = new ActTitleHandler();
        setAbsHandler(handler);
        setContentView(R.layout.activity_chat);
        activityInstance = this;
        activity = this ;
        setTitle("");
        tempBundle = getIntent().getExtras();
        chatType = tempBundle.getInt(EaseConstant.EXTRA_CHAT_TYPE, CHATTYPE_SINGLE);
        //user or group id
        toChatUsername = getIntent().getExtras().getString(EaseConstant.EXTRA_USER_ID);
        targetMemberInfoID = getIntent().getExtras().getString(EaseConstant.EXTRA_USER_ID);
        //set arguments
        chatFragment = new EaseChatFragment();
        chatFragment.setArguments(tempBundle);
        getSupportFragmentManager().beginTransaction().add(R.id.container, chatFragment).commit();
        getIntentDatas();
        handler.replaseLeftLayout(this, true);
        handler.getLeftLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        handler.addRightView(LayoutInflater.from(this).inflate(R.layout.homeactivity_rightlayout, null), true);
        handler.getRightLayout().setVisibility(View.VISIBLE);
        ImageButton imageButton = (ImageButton) handler.getRightLayout().findViewById(R.id.imageButton02);
        if(chatType == CHATTYPE_SINGLE){
            imageButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.preview_user_info));
        }else{
            imageButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.room_right));
        }
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chatType == CHATTYPE_SINGLE) {
                    if (targetMemberInfoID == null) {
                        return;
                    }
                    Intent intent = new Intent();
                    intent.setClass(ChatActivity.this, PreviewFriendsInfoActivity.class);
                    intent.putExtra(PreviewFriendsInfoActivity.kFROM_CHART_ACTIVITY, true);
                    intent.putExtra(PreviewFriendsInfoActivity.kUSER_MEMBER_ID, targetMemberInfoID);
                    intent.putExtra(PreviewFriendsInfoActivity.FROM_CHAT, true);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent();
                    intent.setClass(ChatActivity.this, ChatRoomInfoActivity.class);
                    intent.putExtra(ChatRoomInfoActivity.kChatRoomInfoID, cr_id);
                    //intent.putExtra(ChatRoomInfoActivity.kChatCRID, cr_memberid_id);
                    String temp = getIntent().getStringExtra(EaseConstant.FROM_ACTIVITY);
                    if (temp != null) {
                        intent.putExtra(EaseConstant.FROM_ACTIVITY, EaseConstant.FROM_ACTIVITY);
                    }
                    //intent.putExtra(ChatRoomInfoActivity.kChatRoomID, toChatUsername);
                    startActivity(intent);
                }
            }
        });

        if( chatType == CHATTYPE_SINGLE){
            setTitle(tempBundle.get(EaseConstant.TARGET_NICKNAME)+"");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        HomeActivity.isChatActivity = true ;
    }

    @Override
    protected void onStop() {
        super.onStop();
        HomeActivity.isChatActivity = false ;
    }

    /**
     * 获取Intent的数据
     */
    private void getIntentDatas() {

        roomInfoID = getIntent().getExtras().getString(EaseConstant.kGroupID);
        cr_id = getIntent().getExtras().getString(EaseConstant.kCHAT_ROOM_INFO_ID);
//        cr_memberid_id = getIntent().getExtras().getString(kChatCRID);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityInstance = null;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // enter to chat activity when click notification bar, here make sure only one chat activiy
        String username = intent.getStringExtra("userId");
        if (username != null && !TextUtils.isEmpty(toChatUsername) && toChatUsername.equals(username))
            super.onNewIntent(intent);
        else {
            finish();
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        chatFragment.onBackPressed();
    }

    public String getToChatUsername() {
        return toChatUsername;
    }



    //    public void onCallPermission(){
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//判断当前系统的SDK版本是否大于23
//            //如果当前申请的权限没有授权
//            if (!(checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)){
//                //第一次请求权限的时候返回false,第二次shouldShowRequestPermissionRationale返回true
//                //如果用户选择了“不再提醒”永远返回false。
//                if (shouldShowRequestPermissionRationale(android.Manifest.permission.RECORD_AUDIO)){
//                    Toast.makeText(this, "Please grant the permission this time", Toast.LENGTH_LONG).show();
//                }
//                //请求权限
//                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},1);
//            }else {//已经授权了就走这条分支
//                Log.i("wei", "onClick granted");
//            }
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        if (requestCode==1){
//            if (permissions[0].equals(Manifest.permission.RECORD_AUDIO)&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
//               //得到权限之后去做的业务
//                Toast.makeText(this,"录音权限获取成功",Toast.LENGTH_SHORT).show();
//            }else {//没有获得到权限
//                Toast.makeText(this,"你不给权限我就不好干事了啦",Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

}
