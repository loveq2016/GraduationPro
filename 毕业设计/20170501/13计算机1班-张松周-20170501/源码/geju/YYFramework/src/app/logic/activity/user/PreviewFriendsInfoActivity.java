package app.logic.activity.user;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.easeui.ui.activity.ChatActivity;
import com.hyphenate.exceptions.HyphenateException;
import com.squareup.picasso.Picasso;

import org.ql.activity.customtitle.ActActivity;
import org.ql.utils.QLToastUtils;

import java.io.UnsupportedEncodingException;
import java.util.List;

import app.config.DemoApplication;
import app.config.http.HttpConfig;
import app.logic.activity.ActTitleHandler;
import app.logic.controller.UserManagerController;
import app.logic.pojo.FriendInfo;
import app.logic.pojo.UserInfo;
import app.logic.singleton.YYSingleton;
import app.utils.common.FrescoHelper;
import app.utils.common.FrescoImageShowThumb;
import app.utils.common.Listener;
import app.utils.helpers.ChartHelper;
import app.utils.helpers.QRHelper;
import app.view.DialogNewStyleController;
import app.yy.geju.R;

/**
 * SiuJiYung create at 2016年6月30日 下午4:47:13
 */

public class PreviewFriendsInfoActivity extends ActActivity implements OnClickListener {

    public static final String kUSER_MEMBER_ID = "kUSER_MEMBER_ID";
    public static final String kFROM_CHART_ACTIVITY = "kFROM_CHART_ACTIVITY";
    private static final int kMESSAGE_WHAT_UPDATE_UI = 21;
    public static final String FROM_CHAT = "FROM_CHAT";
    public static final String FROMORG = "FROMORG";
    private View set_and_tagname , root_view ;     //设置备注，名称
    private View lin_view;
    private TextView tagTextView;     //设置备注，名称
    private ImageView userHeadImgView;  //用户头像
    private UserInfo userInfo;
    private boolean fromChartActivity;
    private String info_str;
    private FriendInfo friendInfo;
    private TextView nameTextView;
    private TextView nicknameTV;
    private TextView phoneTextView;
    private TextView areaTextView;
    private ImageView sexImgView , QRimg ;
    private Button delectBtn, sendBtn;  //删除和发送按钮
    private QRHelper qrHelper;
    private boolean isFriend = false;   //是好友的标志
    private String fromOrg;
    private Handler roadingTimeHandler;
    private ActTitleHandler handler = new ActTitleHandler();

    private Handler updateUIHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == kMESSAGE_WHAT_UPDATE_UI) {
                //更新UI
                updateUI();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbsHandler(handler);
        setContentView(R.layout.activity_user_property_info2);
        qrHelper = new QRHelper();
        //初始化TootBra
        intiTootBar();
        info_str = getIntent().getStringExtra(kUSER_MEMBER_ID);
        fromChartActivity = getIntent().getBooleanExtra(kFROM_CHART_ACTIVITY, false);  //标志来自单聊时的界面
        fromOrg = getIntent().getStringExtra(FROMORG);
        initView();
        if (info_str != null) {
            getUserInfo(info_str);  //获取用户信息
        }
    }

    /**
     * 初始化TootBar
     */
    private void intiTootBar() {
        setTitle("");
        handler.replaseLeftLayout(this, true);
        ((TextView) handler.getLeftLayout().findViewById(R.id.left_tv)).setText("详细资料");
        handler.getLeftLayout().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        lin_view = findViewById(R.id.set_lin_view);
        set_and_tagname = findViewById(R.id.set_and_tagname);
    }

    private void initView() {
        root_view = findViewById(R.id.root_view);
        userHeadImgView = (ImageView) findViewById(R.id.preview_user_info_userhead);
        nameTextView = (TextView) findViewById(R.id.property_info_name_tv);
        nicknameTV = (TextView) findViewById(R.id.preview_user_info_nickname);
        tagTextView = (TextView) findViewById(R.id.preview_user_info_tag);
        phoneTextView = (TextView) findViewById(R.id.preview_user_info_phone);
        areaTextView = (TextView) findViewById(R.id.preview_user_info_area);
        sexImgView = (ImageView) findViewById(R.id.preview_user_info_sex);
        sendBtn = (Button) findViewById(R.id.preview_user_info_send_msg_btn);     //发送信息
        QRimg = (ImageView) findViewById( R.id.qr_code_img);
        QRimg.setOnClickListener( this );
        sendBtn.setOnClickListener(this);
        delectBtn = (Button) findViewById(R.id.preview_user_info_delect_msg_btn);  //删除好友preview_user_info_delect_msg_btn
        delectBtn.setOnClickListener(this);
        tagTextView.setOnClickListener(this);
        userHeadImgView.setOnClickListener(this);
    }

    /**
     * 更新UI
     */
    private void updateUI() {

        Uri headUri = Uri.parse(HttpConfig.getUrl(userInfo.getPicture_url()));
        Picasso.with(this).load(headUri).placeholder(R.drawable.default_user_icon).fit().centerCrop().into(userHeadImgView);
        //FrescoHelper.asyncLoad(this , Uri.parse(HttpConfig.getUrl(userInfo.getPicture_url())), userHeadImgView);
        nicknameTV.setText("格局号:" + userInfo.getPhone());
        String nickNameString = userInfo.getFriend_name() == null || TextUtils.isEmpty(userInfo.getFriend_name()) ? userInfo.getNickName() : userInfo.getFriend_name();
        nameTextView.setText(nickNameString);  //有备注的显示备注名
        tagTextView.setText(nickNameString);   //没备注的显示NickName
        phoneTextView.setText(userInfo.getPhone());
        areaTextView.setText(userInfo.getRegion());
        int sexResId = userInfo.getSex() == null || userInfo.equals("男") ? R.drawable.sex_man : R.drawable.sex_woment;
        sexImgView.setImageResource(sexResId);
        dismissWaitDialog();
        QRimg.setVisibility(View.VISIBLE);

    }

    /**
     * 获取用户信息
     *
     * @param member_id
     */
    private void getUserInfo(String member_id) {
        showWaitDialog();
        UserManagerController.getUserInfo(this, member_id, new Listener<Integer, UserInfo>() {
            @Override
            public void onCallBack(Integer status , UserInfo reply) {
                if (status == 1 && reply != null) {
                    userInfo = reply;
                    //判断是否是好友
                    surfaceUpData( reply );
                    //showDelectBtn();
                }else{
                    QLToastUtils.showToast( PreviewFriendsInfoActivity.this , "信息获取失败，请重新加载");
                    root_view.setVisibility(View.GONE);
                    //finish();
                }
            }
        });
    }


    /**
     * 获取数据后更新界面
     * @param userInfo
     */
    private void surfaceUpData( UserInfo userInfo ){
        if("00".equals(userInfo.getFriendStatus())){       //非好友
            delectBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_add_friend_bg));
            delectBtn.setText("添加好友");
            set_and_tagname.setVisibility(View.GONE);       //修改备注隐藏
            delectBtn.setVisibility(View.VISIBLE);
            sendBtn.setVisibility(View.GONE);
            isFriend = false;
        }else if("10".equals(userInfo.getFriendStatus())){  //申请中
            delectBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_application_friend_bg));
            delectBtn.setText("申请中");   //shape_application_friend_bg
            delectBtn.setEnabled(true);
            set_and_tagname.setVisibility(View.GONE);
            delectBtn.setVisibility(View.VISIBLE);
            sendBtn.setVisibility(View.GONE);
            isFriend = false;
        }else if ("11".equals(userInfo.getFriendStatus())){ //好友
            handler.getRightDefButton().setText("");
            set_and_tagname.setVisibility(View.VISIBLE);
            isFriend = true;
            delectBtn.setVisibility(View.VISIBLE);
            sendBtn.setVisibility(View.VISIBLE);
        }
        tagTextView.setText(userInfo.getFriend_name() == null || TextUtils.isEmpty(userInfo.getFriend_name()) ? userInfo.getNickName() : userInfo.getFriend_name());
        //是用户自己
        if (userInfo.getWp_member_info_id().equals(UserManagerController.getCurrUserInfo().getWp_member_info_id())) {
            sendBtn.setVisibility(View.GONE);
            delectBtn.setVisibility(View.GONE);
            set_and_tagname.setVisibility(View.GONE);  //修改备注隐藏
        }
        updateUIHandler.sendEmptyMessage(kMESSAGE_WHAT_UPDATE_UI);
    }

    // 保存修改
    private void saveSeting(final String name) {
        showWaitDialog();
        UserManagerController.updataFriendName(PreviewFriendsInfoActivity.this, name, userInfo.getAdd_friend_id(), new Listener<Boolean, String>() {

            @Override
            public void onCallBack(Boolean status, String reply) {
                dismissWaitDialog();
                if (status) {

                    if(EaseChatFragment.mHandler !=null ){
                        Bundle bundle = new Bundle();
                        bundle.putString(ChatActivity.REMARKS_NAME, name);
                        Message message = new Message();
                        message.what = 1 ;
                        message.setData(bundle);
                        EaseChatFragment.mHandler.sendMessage(message);  //更新聊天界面的备注(单聊下)
                    }
                    nameTextView.setText(name);
                    tagTextView.setText(name);
                    if (getIntent().getBooleanExtra(FROM_CHAT, false) && YYSingleton.getInstance().getIUpdataChatTitle() != null) {
                        YYSingleton.getInstance().getIUpdataChatTitle().onCallBack(name);
                    }
                    return;
                }
                QLToastUtils.showToast(PreviewFriendsInfoActivity.this, "修改失败，系统繁忙");
            }
        });
    }


    // 添加好友
    private void addFriend(String phone, String msg) {
        UserManagerController.addFriends(this, phone, msg, new Listener<Integer, String>() {
            @Override
            public void onCallBack(Integer status, String reply) {
                if (status == -1) {
                    String msg = reply == null ? "操作失败" : reply;
                    QLToastUtils.showToast(PreviewFriendsInfoActivity.this, msg);
                    return;
                }
                delectBtn.setText("申请中");
                delectBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_application_friend_bg));
                QLToastUtils.showToast(PreviewFriendsInfoActivity.this, "请求已发送");
            }
        });
    }

    /**
     * 显示增加好友的dialog
     * @param phone
     */
    private void showAddFriendsDialog(final String phone) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_friend_view, null);
        final DialogNewStyleController addFriendsDailog = new DialogNewStyleController(this, view);
        TextView titleTv = (TextView) view.findViewById(R.id.dialog_title_tv);
        ImageView headIv = (ImageView) view.findViewById(R.id.dialog_head_iv);
        TextView nameTv = (TextView) view.findViewById(R.id.dialog_user_name_tv);
        final EditText tagEdt = (EditText) view.findViewById(R.id.dialog_tag_edt);
        Button sendBtn = (Button) view.findViewById(R.id.dialog_cancel_btn);
        Button cancel = (Button) view.findViewById(R.id.dialog_true_btn);

        titleTv.setText("添加好友");
        sendBtn.setText("发送");
        cancel.setText("取消");
        sendBtn.setTextColor(getResources().getColor(R.color.new_app_color));
        cancel.setTextColor(Color.parseColor("#ff0000"));
        Picasso.with(this).load(HttpConfig.getUrl(userInfo.getMy_picture_url())).error(R.drawable.default_user_icon).fit().centerInside().into(headIv);
        nameTv.setText(userInfo.getNickName());

        sendBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriend(phone, tagEdt.getText().toString());
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
     * 新版本
     * @param title
     * @param initValue
     */
    private void showUpdateNickNameDialog(String title, String initValue) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_new_edit_view, null);
        final DialogNewStyleController dialog = new DialogNewStyleController(this, view);
        TextView titleTv = (TextView) view.findViewById(R.id.dialog_title_tv);
        final EditText contentEdt = (EditText) view.findViewById(R.id.dialog_content_edt);
        Button updateBtn = (Button) view.findViewById(R.id.dialog_cancel_btn); //左边
        Button cancelBtn = (Button) view.findViewById(R.id.dialog_true_btn); //右边
        titleTv.setText(title);
        contentEdt.setText(initValue);
        contentEdt.setSelection(initValue.length());
        updateBtn.setText("确定");
        cancelBtn.setText("取消");
        updateBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String nickName = contentEdt.getText().toString();
                if (nickName.length() <= 8) {
                    saveSeting(nickName);
                    dialog.dismiss();
                } else {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(contentEdt.getWindowToken(),0);
                    QLToastUtils.showToast(PreviewFriendsInfoActivity.this, "长度不能超过8位");
                }
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

    private void showDeleteDialog(String title) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_new_content_view, null);
        TextView titleTv = (TextView) view.findViewById(R.id.dialog_title_tv);
        TextView contentTv = (TextView) view.findViewById(R.id.dialog_content_tv);
        Button deleteBtn = (Button) view.findViewById(R.id.dialog_cancel_btn);
        Button cancelBtn = (Button) view.findViewById(R.id.dialog_true_btn);
        titleTv.setText(title);
        contentTv.setText("确定删除该好友？");
        deleteBtn.setText("确定");
        cancelBtn.setText("取消");
        final DialogNewStyleController dialog = new DialogNewStyleController(this, view);
        dialog.show();
        deleteBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                roadingTimeHandler = new Handler();
                showWaitDialog();
                UserManagerController.deleteFriends(PreviewFriendsInfoActivity.this, info_str, new Listener<Integer, String>() {
                    @Override
                    public void onCallBack(Integer status, String reply) {
                        dismissWaitDialog();
                        if (status == 1) {
                            //deletFride();
                            if(EaseChatFragment.mHandler !=null ){
                                Message message = new Message();
                                message.what = 2 ;
                                EaseChatFragment.mHandler.sendMessage(message); //发送信息，当前这个好友已被删除
                            }
                            if (YYSingleton.getInstance().getUpdataFriendListListener() != null) {
                                YYSingleton.getInstance().getUpdataFriendListListener().onCallBack();
                            }
                            finish();
                        } else {
                            QLToastUtils.showToast(PreviewFriendsInfoActivity.this, "删除失败！");
                        }
                    }
                });
                dialog.dismiss();
            }
        });
        cancelBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    // 显示好友头像放大dialog
    private void showFriendImg(String imgUrl) {

        int[] location = new int[2];
        userHeadImgView.getLocationOnScreen(location);
        startActivity(new Intent(this, ShowBigImageActivity.class).putExtra(ShowBigImageActivity.PIC_URL, HttpConfig.getUrl(imgUrl)));
    }

    private void showQRCode() {
        if( userInfo ==null )return;
        QRCodePersonal personalInfo = new QRCodePersonal();
        personalInfo.setPhone(userInfo.getPhone());
        personalInfo.setNickName(userInfo.getNickName());
        personalInfo.setPicture_url(userInfo.getPicture_url());
        personalInfo.setLocation(userInfo.getLocation());
        Gson gson = new Gson();
        String usrInfoJson = gson.toJson( personalInfo );  //String usrInfoJson = gson.toJson(userInfo);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int imgWidth = (int) dm.scaledDensity * 200;
        int imgHeight = (int) dm.scaledDensity * 200;
        Bitmap qriBitmap = qrHelper.createQRImage(usrInfoJson, imgWidth, imgHeight);
        String userPic = HttpConfig.getUrl(userInfo.getPicture_url());
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
        WindowManager.LayoutParams p2 = new WindowManager.LayoutParams((int) (d.getWidth() * 0.70), (int) (d.getHeight() * 0.30));
        View dialog_view = getLayoutInflater().inflate(R.layout.dialog_qrode, null);
        ImageView qrcode = (ImageView) dialog_view.findViewById(R.id.qrcode);
        SimpleDraweeView pic = (SimpleDraweeView) dialog_view.findViewById(R.id.im_personpic);
        TextView nick = (TextView) dialog_view.findViewById(R.id.tx_name);
        TextView phone = (TextView) dialog_view.findViewById(R.id.tx_phone);
        // Picasso.with(this).load(Uri.parse(userPic)).placeholder(R.drawable.default_user_icon).fit().centerCrop().into(pic);

        FrescoImageShowThumb.showThrumb(Uri.parse(userPic),pic);
        //FrescoHelper.asyncLoad( this ,Uri.parse(userPic), pic);
        nick.setText(userInfo.getNickName());
        phone.setText("格局号：" + userInfo.getPhone());
        String decodeString = null;
        try {
            decodeString = new String(usrInfoJson.getBytes(), "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            decodeString = usrInfoJson;
        }
        qrcode.setImageBitmap(showHeaderQRcodeBitmap(userHeadImgView, decodeString, imgWidth, imgHeight));
        Dialog qrDialog = new Dialog(this, R.style.dialog);
        qrDialog.setContentView(dialog_view, p2);
        qrDialog.show();
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.preview_user_info_tag:
                if (isFriend) {
                    showUpdateNickNameDialog("编辑备注", tagTextView.getText().toString());
                }
                break;
            case R.id.preview_user_info_delect_msg_btn:
                if (isFriend) {
                    //showDeletedDialog("提示");
                    showDeleteDialog("提示");
                } else {
                    //addFriend(userInfo.getPhone(), "");
                    showAddFriendsDialog(userInfo.getPhone());
                }
                break;
            case R.id.preview_user_info_send_msg_btn:
//                if (fromOrg != null) {
//                    String targetId = userInfo.getWp_friends_info_id() == null ? userInfo.getWp_member_info_id() : userInfo.getWp_friends_info_id();
//                    ChartHelper.startChart(this, targetId, "");
//                    return;
//                }
                if (fromChartActivity) {
                    finish();
                    return;
                }
                String targetId = userInfo.getWp_friends_info_id() == null ? userInfo.getWp_member_info_id() : userInfo.getWp_friends_info_id();
                ChartHelper.startChart(this, targetId, "");
                break;
            case R.id.preview_user_info_userhead:
                showFriendImg(userInfo.getPicture_url());
                break;
            case R.id.qr_code_img:  //显示二维码
                showQRCode();
                break;
            default:
                break;
        }
    }

    /**
     * 删除环信上的朋友
     */
    private void deletFride(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if( userInfo!=null){
                    try {
                        EMClient.getInstance().contactManager().deleteContact(userInfo.getPhone());
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * 不是好友就不显示“删除好友”
     */
    private void showDelectBtn() {
        UserManagerController.getFriendsList(this, new Listener<List<FriendInfo>, List<FriendInfo>>() {
            @Override
            public void onCallBack(List<FriendInfo> status, List<FriendInfo> reply) {
                if (reply != null && reply.size() > 0) {
                    for (FriendInfo info : reply) {
                        if (userInfo.getWp_member_info_id().equals(info.getWp_friends_info_id())) {
                            if (info.isResponse()) {
                                userInfo.setAdd_friend_id(info.getAdd_friend_id());
                                userInfo.setFriend_name(info.getFriend_name());
                                isFriend = true;
                                break;
                            }
                        }
                    }
                }
                if (isFriend) {                               //好友状态
                    handler.getRightDefButton().setText("");
                    set_and_tagname.setVisibility(View.VISIBLE);
                } else {
                    delectBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_add_friend_bg));
                    delectBtn.setText("添加好友");
                    set_and_tagname.setVisibility(View.GONE);  //修改备注隐藏
                }
                delectBtn.setVisibility(View.VISIBLE);
                sendBtn.setVisibility(View.VISIBLE);
                //set_and_tagname.setVisibility(View.VISIBLE);
                tagTextView.setText(userInfo.getFriend_name() == null || TextUtils.isEmpty(userInfo.getFriend_name()) ? userInfo.getNickName() : userInfo.getFriend_name());

                if (userInfo.getWp_member_info_id().equals(UserManagerController.getCurrUserInfo().getWp_member_info_id())) {
                    sendBtn.setVisibility(View.GONE);
                    delectBtn.setVisibility(View.GONE);
                }
                updateUIHandler.sendEmptyMessage(kMESSAGE_WHAT_UPDATE_UI);
            }
        });
    }
}
