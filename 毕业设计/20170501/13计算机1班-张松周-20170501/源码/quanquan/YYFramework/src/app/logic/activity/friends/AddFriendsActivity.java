package app.logic.activity.friends;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.canson.view.swipemenulistview.SwipeMenu;
import org.canson.view.swipemenulistview.SwipeMenuCreator;
import org.canson.view.swipemenulistview.SwipeMenuItem;
import org.canson.view.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import org.ql.activity.customtitle.ActActivity;
import org.ql.activity.customtitle.DefaultHandler;
import org.ql.app.alert.AlertController;
import org.ql.app.alert.AlertDialog;
import org.ql.utils.QLToastUtils;
import org.ql.views.ImageView.CircleImageView;
import org.ql.views.listview.QLXListView;
import org.ql.views.listview.QLXListView.IXListViewListener;
import org.w3c.dom.Text;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.squareup.picasso.Picasso;

import android.R.integer;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.SearchView;
import android.text.Layout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import app.config.http.HttpConfig;
import app.logic.activity.ActTitleHandler;
import app.logic.activity.main.HomeActivity;
import app.logic.activity.org.ApplyToJoinActivity;
import app.logic.activity.org.SearchOrgDefailsActivity;
import app.logic.activity.user.PreviewFriendsInfoActivity;
import app.logic.activity.user.QRCodePersonal;
import app.logic.adapter.YYBaseListAdapter;
import app.logic.controller.OrganizationController;
import app.logic.controller.UserManagerController;
import app.logic.pojo.FriendInfo;
import app.logic.pojo.OrganizationInfo;
import app.logic.pojo.UserInfo;
import app.logic.pojo.YYChatSessionInfo;
import app.logic.singleton.YYSingleton;
import app.logic.singleton.ZSZSingleton;
import app.utils.common.FrescoImageShowThumb;
import app.utils.common.Listener;
import app.utils.helpers.ChartHelper;
import app.utils.helpers.QRHelper;
import app.utils.helpers.YYUtils;
import app.view.DialogNewStyleController;
import app.view.YYListView;
import app.yy.geju.R;

/**
 * SiuJiYung create at 2016年6月14日 下午5:29:05
 * <p>
 * 添加好友
 */

public class AddFriendsActivity extends ActActivity implements OnClickListener, IXListViewListener, OnItemClickListener {

    public static final String kADD_FRIEND_JSON_INFO = "kADD_FRIEND_JSON_INFO";

    private ActTitleHandler mHandler = new ActTitleHandler();
    private EditText searchET;
    private ImageView searchBtn;
    // private SearchView searchView;
    private YYListView requestListView;
    private QRHelper qrHelper;
    private AlertDialog alertDialog;
    private AlertDialog alertDialogFriends;
    private AlertDialog alertDialogOrg;

    private OnClickListener btnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == null) {
                return;
            }
            if (v.getId() == R.id.item_row_rq_friends_ok_btn) {
                FriendInfo info = (FriendInfo) v.getTag();
                // showAddFriendsDialog(info);
                responseFriends(info, true);
            } else if (v.getId() == R.id.item_row_rq_friends_refuse_btn) {
                FriendInfo info = (FriendInfo) v.getTag();
                responseFriends(info, false);
            }
        }
    };

    private YYBaseListAdapter<FriendInfo> mAdapter = new YYBaseListAdapter<FriendInfo>(this) {
        @Override
        public View createView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(AddFriendsActivity.this).inflate(R.layout.item_row_request_friends, null);
                Button okBtn = (Button) convertView.findViewById(R.id.item_row_rq_friends_ok_btn);
                // (Button)convertView.findViewById(R.id.item_row_rq_friends_refuse_btn);
                okBtn.setOnClickListener(btnClickListener);
                // refuseBtn.setOnClickListener(btnClickListener);
            }
            SimpleDraweeView userHeadImageView = ((SimpleDraweeView) convertView.findViewById(R.id.item_row_rq_friends_imgview));
            TextView nameTv = (TextView) convertView.findViewById(R.id.item_row_rq_friends_name_tv);
            TextView msgTv = (TextView) convertView.findViewById(R.id.item_row_rq_friends_msg_tv);
            Button okBtn = (Button) convertView.findViewById(R.id.item_row_rq_friends_ok_btn);
            // (Button)convertView.findViewById(R.id.item_row_rq_friends_refuse_btn);

            FriendInfo info = (FriendInfo) getItem(position);
            if (info != null) {
                okBtn.setTag(info);
                okBtn.setEnabled(!info.isResponse());


//                if (info.isResponse() && !info.isRequestMessage()) {
                if (info.isResponse() && info.isRequest_accept()) {
                    okBtn.setText("已添加");
                    okBtn.setTextColor(0xffc6c6c6);
                    msgTv.setText(info.getRequestMessage());
                    msgTv.setVisibility(TextUtils.isEmpty(info.getRequestMessage()) ? View.GONE : View.VISIBLE);

                    String nameString = info.getRequest_nickName() == null || TextUtils.isEmpty(info.getRequest_nickName()) ? info.getNickName() : info.getRequest_nickName();
                    nameTv.setText(nameString);
                    String path = info.getRequest_picture_url() == null || TextUtils.isEmpty(info.getRequest_picture_url()) ? info.getPicture_url() : info.getRequest_picture_url();
                    String imgPath = HttpConfig.getUrl(path);
                    Uri uri = Uri.parse(imgPath);
//                    Picasso.with(mContext).load(uri).placeholder(R.drawable.default_user_icon).fit().centerCrop().into(userHeadImageView);
//                    userHeadImageView.setImageURI(uri);
                    FrescoImageShowThumb.showThrumb(uri,userHeadImageView);

                } else if (!info.isOtherRequest() && !info.isResponse()) {
                    // 我的请求
                    okBtn.setText("已请求");
                    okBtn.setEnabled(false);
                    okBtn.setTextColor(0xffff4439);//#FF4439
                    msgTv.setText(info.getValidation());
                    msgTv.setVisibility(TextUtils.isEmpty(info.getValidation()) ? View.GONE : View.VISIBLE);
                    String nameString = info.getRequest_nickName() == null || TextUtils.isEmpty(info.getRequest_nickName()) ? info.getNickName() : info.getRequest_nickName();
                    nameTv.setText(nameString);
                    String path = info.getRequest_picture_url() == null || TextUtils.isEmpty(info.getRequest_picture_url()) ? info.getPicture_url() : info.getRequest_picture_url();
                    String imgPath = HttpConfig.getUrl(path);
                    Uri uri = Uri.parse(imgPath);
//                    Picasso.with(mContext).load(uri).placeholder(R.drawable.default_user_icon).fit().centerCrop().into(userHeadImageView);
//                    userHeadImageView.setImageURI(uri);
                    FrescoImageShowThumb.showThrumb(uri,userHeadImageView);
                } else if (info.isOtherRequest() && !info.isResponse()) {
                    // 他人请求
                    okBtn.setText("添加");
                    okBtn.setTextColor(0xfffcfcfc);
                    msgTv.setText(info.getValidation());
                    msgTv.setVisibility(TextUtils.isEmpty(info.getValidation()) ? View.GONE : View.VISIBLE);
                    String nameString = info.getRequest_nickName() == null || TextUtils.isEmpty(info.getRequest_nickName()) ? info.getNickName() : info.getRequest_nickName();
                    nameTv.setText(nameString);
                    String path = info.getRequest_picture_url() == null || TextUtils.isEmpty(info.getRequest_picture_url()) ? info.getPicture_url() : info.getRequest_picture_url();
                    String imgPath = HttpConfig.getUrl(path);
                    Uri uri = Uri.parse(imgPath);
//                    Picasso.with(mContext).load(uri).placeholder(R.drawable.default_user_icon).fit().centerCrop().into(userHeadImageView);
//                    userHeadImageView.setImageURI(uri);
                    FrescoImageShowThumb.showThrumb(uri,userHeadImageView);
                }

            }
            return convertView;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbsHandler(mHandler);
        setContentView(R.layout.activity_add_friends2);
        mHandler.getRightLayout().setVisibility(View.VISIBLE);
        mHandler.replaseLeftLayout(this, true);
        mHandler.getLeftLayout().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView) mHandler.getLeftLayout().findViewById(R.id.left_tv)).setText("新的朋友");
        mHandler.getRightDefButton().setText("清空");
        mHandler.getRightDefButton().setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                deleteFriendRequestMessage();
            }
        });

        setTitle("");
        searchET = (EditText) findViewById(R.id.add_friends_search_et);
        searchBtn = (ImageView) findViewById(R.id.add_friends_search_btn);
        searchBtn.setOnClickListener(this);
        addSearcEditText();

        requestListView = (YYListView) findViewById(R.id.add_friends_lv);
        requestListView.setPullRefreshEnable(true);
        requestListView.setPullLoadEnable(false, true);
        requestListView.setAdapter(mAdapter);
        requestListView.setXListViewListener(this);
        requestListView.setOnItemClickListener(this);
        // getRequestFrendsList();
        SwipeMenuCreator menuCreator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // SwipeMenuItem item = new
                // SwipeMenuItem(AddFriendsActivity.this);
                // item.setBackground(R.drawable.menu_delete_bg);
                // item.setWidth(YYUtils.dp2px(90, AddFriendsActivity.this));
                // item.setTitleSize(16);
                // item.setTitle("删除");
                // item.setTitleColor(0xfffcfcfc);
                // menu.addMenuItem(item);
            }
        };
        requestListView.setMenuCreator(menuCreator);
        requestListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(int position, SwipeMenu menu, int index) {
                FriendInfo info = mAdapter.getItem(position);
                if (info == null) {
                    return;
                }
                removeFriend(info);
                mAdapter.removeItemAt(position);
            }
        });

        findViewById(R.id.add_friends_scan_qr_tv).setOnClickListener(this);

        qrHelper = new QRHelper();
        qrHelper.setOnScanResultListener(new Listener<Void, String>() {
            @Override
            public void onCallBack(Void status, String reply) {
                if (reply == null || TextUtils.isEmpty(reply)) {
                    return;
                }
                try {
//                    try {
//                        reply = new String(reply.getBytes("ISO-8859-1"), "UTF-8");
//                    } catch (UnsupportedEncodingException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
                    Gson gson = new Gson();
                    if (reply.contains("org_logo_url")) {
                        OrganizationInfo orgInfo = gson.fromJson(reply, OrganizationInfo.class);
                        if (orgInfo != null) {
                            //showJoinOrgDialog(orgInfo);
                            //showJoinOrgDialog2(orgInfo);
                            startActivity(new Intent(AddFriendsActivity.this, ApplyToJoinActivity.class).putExtra(ApplyToJoinActivity.ORG_ID, orgInfo.getOrg_id()));
                        }
                    } else if (reply.contains("cr_id")) {
                        // 加入群聊
                        try {
                            String[] temps = reply.split(":");
                            ChartHelper.joinChatRoomUserId(AddFriendsActivity.this, temps[1]);
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                        }
                    } else {
                        //UserInfo userInfo = gson.fromJson(reply, UserInfo.class);
                        QRCodePersonal personalInfo = gson.fromJson(reply, QRCodePersonal.class);
                        if (personalInfo != null && personalInfo.getPhone() != null) {
                            //showAddFriendsDialog(userInfo);
                            showAddFriendsDialog2(personalInfo);
                            return;
                        }
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
                // QLToastUtils.showToast(AddFriendsActivity.this,"请扫描个人中心的二维码");
            }
        });


        String _friends_json_info = getIntent().getStringExtra(kADD_FRIEND_JSON_INFO);
        if (_friends_json_info != null) {
            Gson gson = new Gson();

//            try {
//                _friends_json_info = new String(_friends_json_info.getBytes("ISO-8859-1"), "UTF-8");
//            } catch (UnsupportedEncodingException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }

            //UserInfo _uInfo = gson.fromJson(_friends_json_info, UserInfo.class);
            QRCodePersonal personal = gson.fromJson( _friends_json_info , QRCodePersonal.class);
            //showAddFriendsDialog(_uInfo);
            showAddFriendsDialog2(personal);
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        getListFriendMessage();

    }

    private void searchByPhoneNumber(final String txt) {
        // boolean isMobilePhone = YYUtils.isMobilePhoneNumber(txt);
        if (txt == null || TextUtils.isEmpty(txt)) {
            QLToastUtils.showToast(this, "请输入手机号码");
            return;
        }
        showWaitDialog();
        UserManagerController.getPhoneMemerInfo(this, txt, new Listener<Integer, List<UserInfo>>() {
            @Override
            public void onCallBack(Integer status, List<UserInfo> reply) {
                dismissWaitDialog();
                if (reply != null && reply.size() > 0) {
                    UserInfo info = reply.get(0);
                    info.setPhone(txt);
                    //showAddFriendsDialog(info);
                    //showAddFriendsDialog2(info);
                    QRCodePersonal personalInfo = new QRCodePersonal();
                    personalInfo.setPhone( info.getPhone());
                    personalInfo.setNickName( info.getNickName());
                    personalInfo.setPicture_url( info.getPicture_url());
                    personalInfo.setLocation( info.getLocation());
                    showAddFriendsDialog2(personalInfo);

                    return;
                }
                QLToastUtils.showToast(AddFriendsActivity.this, "没有该用户");
            }
        });
    }

    /**
     * 响应请求
     *
     * @param info
     */
    private void showAddFriendsDialog(final FriendInfo info) {
        if (alertDialog != null && alertDialog.isShowing()) {
            return;
        }
        View view = LayoutInflater.from(this).inflate(R.layout.alert_add_friends, null);
        if (alertDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            alertDialog = builder.create();
            alertDialog.setView(view);
            alertDialog.setCancelable(true);
            alertDialog.setIcon(0);
            alertDialog.setCanceledOnTouchOutside(true);
            alertDialog.setTitle("同意添加好友");
        }

        if (info.isRequestMessage()) {
            ImageView iView = ((ImageView) view.findViewById(R.id.add_friends_userhead_iv));
            Uri uri = Uri.parse(HttpConfig.getUrl(info.getRequest_picture_url()));
            Picasso.with(this).load(uri).placeholder(R.drawable.default_user_icon).into(iView);
            ((TextView) view.findViewById(R.id.add_friends_name_tv)).setText(info.getRequest_nickName());
            ((TextView) view.findViewById(R.id.add_friends_org_tv)).setText("");
        } else {
            ImageView iView = ((ImageView) view.findViewById(R.id.add_friends_userhead_iv));
            Uri uri = Uri.parse(HttpConfig.getUrl(info.getPicture_url()));
            Picasso.with(this).load(uri).placeholder(R.drawable.default_user_icon).into(iView);
            ((TextView) view.findViewById(R.id.add_friends_name_tv)).setText(info.getNickName());
            ((TextView) view.findViewById(R.id.add_friends_org_tv)).setText("");
        }
        android.content.DialogInterface.OnClickListener dialogClickListener = new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == AlertDialog.BUTTON_NEGATIVE) {
                    // 添加
                    if (info.isRequestMessage()) {
                        responseFriends(info, true);
                    }
                }
                dialog.dismiss();
            }
        };

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "添加", dialogClickListener);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "取消", dialogClickListener);
        alertDialog.show();
    }


    private void showAddFriendsDialog(final UserInfo info) {
        if (alertDialogFriends != null && alertDialogFriends.isShowing()) {
            return;
        }
        final View view = LayoutInflater.from(this).inflate(R.layout.alert_add_friends2, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        alertDialogFriends = builder.create();
        alertDialogFriends.setView(view);
        alertDialogFriends.setCancelable(true);
        alertDialogFriends.setIcon(0);
        alertDialogFriends.setCanceledOnTouchOutside(true);
        alertDialogFriends.setTitle("添加好友");

        ImageView iView = ((ImageView) view.findViewById(R.id.add_friends_userhead_iv));
        Uri uri = Uri.parse(HttpConfig.getUrl(info.getMy_picture_url()));
        Picasso.with(this).load(uri).placeholder(R.drawable.default_user_icon).fit().centerInside().into(iView);
        ((TextView) view.findViewById(R.id.add_friends_name_tv)).setText(info.getNickName());
        ((TextView) view.findViewById(R.id.add_friends_org_tv)).setText("");
        android.content.DialogInterface.OnClickListener dialogClickListener = new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (which == AlertDialog.BUTTON_NEGATIVE) {
                    // hideKeyboard();

                    // 添加
                    String msg = ((EditText) view.findViewById(R.id.add_friends_msg_et)).getText().toString();
                    requestFriends(info.getPhone(), msg);
                }
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(searchET.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                dialog.dismiss();

            }
        };

        alertDialogFriends.setButton(AlertDialog.BUTTON_NEGATIVE, "添加", dialogClickListener);
        alertDialogFriends.setButton(AlertDialog.BUTTON_NEUTRAL, "取消", dialogClickListener);
        alertDialogFriends.show();
    }


    private DialogNewStyleController addFriendsDailog;

    /**
     * @param
     */
    private void showAddFriendsDialog2(final QRCodePersonal personalInfo) {
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
                requestFriends(personalInfo.getPhone(), msg);
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(searchET.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                addFriendsDailog.dismiss();
                getListFriendMessage();

            }
        });
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(searchET.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                addFriendsDailog.dismiss();
            }
        });
        addFriendsDailog.show();
    }


    private void showJoinOrgDialog(final OrganizationInfo info) {
        if (alertDialogOrg != null && alertDialogOrg.isShowing()) {
            return;
        }
        final View view = LayoutInflater.from(this).inflate(R.layout.alert_add_friends2, null);
        if (alertDialogOrg == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            alertDialogOrg = builder.create();
            alertDialogOrg.setView(view);
            alertDialogOrg.setCancelable(true);
            alertDialogOrg.setIcon(0);
            alertDialogOrg.setCanceledOnTouchOutside(true);
            alertDialogOrg.setTitle("加入组织");
        }

        ImageView iView = ((ImageView) view.findViewById(R.id.add_friends_userhead_iv));
        Uri uri = Uri.parse(HttpConfig.getUrl(info.getOrg_logo_url()));
        Picasso.with(this).load(uri).placeholder(R.drawable.ty_user_cion).fit().centerCrop().into(iView);
        ((TextView) view.findViewById(R.id.add_friends_name_tv)).setText(info.getOrg_name());
        ((TextView) view.findViewById(R.id.add_friends_org_tv)).setText("");
        android.content.DialogInterface.OnClickListener dialogClickListener = new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == AlertDialog.BUTTON_NEGATIVE) {
                    // 添加
                    String msg = ((EditText) view.findViewById(R.id.add_friends_msg_et)).getText().toString();
                    OrganizationController.joinOrganization(AddFriendsActivity.this, info.getOrg_id(), msg, new Listener<Boolean, String>() {

                        @Override
                        public void onCallBack(Boolean status, String reply) {
                            if (status) {
                                QLToastUtils.showToast(AddFriendsActivity.this, "申请成功！等待管理员的审核。。。");
                                return;
                            }
                            QLToastUtils.showToast(AddFriendsActivity.this, reply);
                        }
                    });
                }
                dialog.dismiss();
            }
        };
        alertDialogOrg.setButton(AlertDialog.BUTTON_NEGATIVE, "加入", dialogClickListener);
        alertDialogOrg.setButton(AlertDialog.BUTTON_NEUTRAL, "取消", dialogClickListener);
        alertDialogOrg.show();
    }

    private DialogNewStyleController dialogOrg;

    private void showJoinOrgDialog2(final OrganizationInfo info) {
        if (dialogOrg != null && dialogOrg.isShowing()) {
            return;
        }
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_friend_view, null);
        dialogOrg = new DialogNewStyleController(this, view);
        TextView titleTv = (TextView) view.findViewById(R.id.dialog_title_tv);
        ImageView headIv = (ImageView) view.findViewById(R.id.dialog_head_iv);
        TextView nameTv = (TextView) view.findViewById(R.id.dialog_user_name_tv);
        final EditText tagEdt = (EditText) view.findViewById(R.id.dialog_tag_edt);
        Button sendBtn = (Button) view.findViewById(R.id.dialog_cancel_btn);
        Button cancel = (Button) view.findViewById(R.id.dialog_true_btn);
        titleTv.setText("加入组织");
        Picasso.with(this).load(HttpConfig.getUrl(info.getOrg_logo_url())).placeholder(R.drawable.ty_user_cion).fit().centerCrop().into(headIv);
        nameTv.setText(info.getOrg_name());
        sendBtn.setText("发送");
        cancel.setText("取消");
        sendBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = tagEdt.getText().toString();
                OrganizationController.joinOrganization(AddFriendsActivity.this, info.getOrg_id(), msg, new Listener<Boolean, String>() {

                    @Override
                    public void onCallBack(Boolean status, String reply) {
                        if (status) {
                            QLToastUtils.showToast(AddFriendsActivity.this, "申请成功！等待管理员的审核。。。");
                            return;
                        }
                        QLToastUtils.showToast(AddFriendsActivity.this, reply);
                    }
                });
                dialogOrg.dismiss();
            }
        });
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogOrg.dismiss();
            }
        });
        dialogOrg.show();
    }


    private void removeFriend(FriendInfo info) {
        UserManagerController.deleteFriends(this, info.getWp_friends_info_id(), new Listener<Integer, String>() {
            @Override
            public void onCallBack(Integer status, String reply) {
                if (status == -1) {
                    String msg = reply == null ? "移除失败" : reply;
                    QLToastUtils.showToast(AddFriendsActivity.this, msg);
                }
            }
        });
    }

    /**
     * 发送添加请求
     *
     * @param phone
     * @param msg
     */
    private void requestFriends(String phone, String msg) {
        UserManagerController.addFriends(this, phone, msg, new Listener<Integer, String>() {
            @Override
            public void onCallBack(Integer status, String reply) {
                // hideKeyboard();
                if (status == -1) {
                    String msg = reply == null ? "操作失败" : reply;
                    QLToastUtils.showToast(AddFriendsActivity.this, msg);
                    return;
                }
                QLToastUtils.showToast(AddFriendsActivity.this, "请求已发送");
                // getRequestFrendsList();
                getListFriendMessage();
            }
        });
    }

    /**
     * 响应请求
     *
     * @param info
     * @param ok
     */
    private void responseFriends(final FriendInfo info, boolean ok) {
        showWaitDialog();
        UserManagerController.ensureFriends(this, info.getAdd_friend_id(), ok ? 1 : 0, info.getWp_friends_info_id(), new Listener<Integer, String>() {
            @Override
            public void onCallBack(Integer status, String reply) {
                dismissWaitDialog();
                if (status == 1) {

                    //将该人置顶
                    UserManagerController.addChatWith(AddFriendsActivity.this, info.getWp_member_info_id(), "", new Listener<Integer, String>() {
                        @Override
                        public void onCallBack(Integer integer, String reply) {
                            if (integer == 1) {

                                EMMessage message = EMMessage.createTxtSendMessage("我们已经是好友了，现在开始对话吧！", info.getRequest_phone());
                                EMClient.getInstance().chatManager().sendMessage(message);
                                getListFriendMessage();
                            }
                        }
                    });
//                    EMMessage message = EMMessage.createTxtSendMessage("我们已经是好友了，现在开始对话吧！", info.getRequest_phone());
//                    EMClient.getInstance().chatManager().sendMessage(message);
                    // getRequestFrendsList();
                }
            }
        });
    }

    /**
     * 获取添加好友请求
     */
    private void getRequestFrendsList() {
        UserManagerController.getFriendsList(this, new Listener<List<FriendInfo>, List<FriendInfo>>() {
            @Override
            public void onCallBack(List<FriendInfo> request, List<FriendInfo> reply) {
                ArrayList<FriendInfo> tmpInfos = new ArrayList<FriendInfo>();
                if (request != null && request.size() > 0) {
                    // 他人请求
                    for (FriendInfo reqInfo : request) {
                        if (reqInfo.isResponse()) {
                            continue;
                        }
                        reqInfo.setOtherRequest(true);
                        tmpInfos.add(reqInfo);
                    }
                }
                if (reply != null && reply.size() > 0) {
                    String myPhone = UserManagerController.getCurrUserInfo().getPhone();
                    for (FriendInfo friendInfo : reply) {
                        if (friendInfo.getPhone() != null && !friendInfo.getPhone().equals(myPhone)) {
                            friendInfo.setOtherRequest(false);
                            tmpInfos.add(friendInfo);
                        }
                    }
                }
                // Collections.reverse(tmpInfos);
                mAdapter.setDatas(tmpInfos);
                // if (tmpInfos.size() > 0) {
                // // 调用显示主界面popupwindow_more添加好友的接口
                // ZSZSingleton.getZSZSingleton().getShowPointImageListener().callbackPoint(true);
                // }
                requestListView.stopLoadMore();
                requestListView.stopRefresh();
                dismissWaitDialog();
            }
        });
    }

    /**
     * 新
     * <p>
     * getListFriendMessageAddFriendsActivity
     */
    private void getListFriendMessage() {
        UserManagerController.getListFriendMessage(this, new Listener<List<FriendInfo>, List<FriendInfo>>() {

            @Override
            public void onCallBack(List<FriendInfo> request, List<FriendInfo> reply) {
                requestListView.stopLoadMore();
                requestListView.stopRefresh();

                ArrayList<FriendInfo> tmpInfos = new ArrayList<FriendInfo>();
                ArrayList<FriendInfo> noResponseInfos = new ArrayList<FriendInfo>();
                if (request != null && request.size() > 0) {
                    // 他人请求
                    for (FriendInfo reqInfo : request) {
//                        if (reqInfo.isResponse()) {
//                            continue;
//                        }
                        reqInfo.setOtherRequest(true);
                        tmpInfos.add(reqInfo);
                        if (reqInfo.isResponse()) {
                            continue;
                        }
                        noResponseInfos.add(reqInfo);
                    }
                }
//                YYSingleton.getInstance().getFriendRequestListener().onCallBack(tmpInfos.size());
                if(YYSingleton.getInstance().getFriendRequestListener()!=null)
                    YYSingleton.getInstance().getFriendRequestListener().onCallBack(noResponseInfos.size());
                if(YYSingleton.getInstance().getOnHasFriendsStatusPoint()!=null)
                    YYSingleton.getInstance().getOnHasFriendsStatusPoint().onCallBack(noResponseInfos.size()>0? true : false);
                if (reply != null && reply.size() > 0) {
                    String myPhone = UserManagerController.getCurrUserInfo().getPhone();
                    for (FriendInfo friendInfo : reply) {
                        if (friendInfo.getPhone() != null && !friendInfo.getPhone().equals(myPhone)) {
                            friendInfo.setOtherRequest(false);
                            tmpInfos.add(friendInfo);
                        }
                    }
                }
                // Collections.reverse(tmpInfos);
                mAdapter.setDatas(tmpInfos);
            }
        });
    }

    /**
     * 删除好友请求
     * <p>
     * deleteFriendRequestMessageAddFriendsActivity
     */
    private void deleteFriendRequestMessage() {
        UserManagerController.deleteFriendRequestMessage(this, new Listener<Integer, String>() {
            @Override
            public void onCallBack(Integer status, String reply) {
                if (status == 1) {
//					QLToastUtils.showToast(AddFriendsActivity.this, "删除成功");
                    getListFriendMessage();
                    return;
                }
                QLToastUtils.showToast(AddFriendsActivity.this, reply);
            }
        });
    }

    /**
     * 监听搜索栏，搜索手机号
     * <p>
     * addSearcEditTextAddFriendsActivity
     */
    private void addSearcEditText() {
        searchET.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    hideKeyboard();
                    String inputstrString = searchET.getText().toString();
                    searchByPhoneNumber(inputstrString);
                }
                return false;
            }
        });
    }

    // 调用隐藏系统默认的输入法
    private void hideKeyboard() {
        // if (getWindow().getAttributes().softInputMode !=
        // WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
        // if (getCurrentFocus() != null)
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        // }
    }

    @Override
    public void onClick(View v) {
        if (v == null) {
            return;
        }
        int vid = v.getId();
        switch (vid) {
            case R.id.add_friends_scan_qr_tv:
                // 启动扫描二维码
                qrHelper.scanQRCode(this);
                break;
            case R.id.add_friends_search_btn:

                String inputstrString = searchET.getText().toString();
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(searchET.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                searchByPhoneNumber(inputstrString);
                break;
            default:
                break;
        }
    }

    @Override
    public void onRefresh() {
        // getRequestFrendsList();
        getListFriendMessage();
    }

    @Override
    public void onLoadMore() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FriendInfo info = mAdapter.getItem(position - 1);
        if (info != null) {
            if (TextUtils.isEmpty(info.getWp_member_info_id())) {
                startActivity(new Intent(AddFriendsActivity.this, PreviewFriendsInfoActivity.class).putExtra(PreviewFriendsInfoActivity.kUSER_MEMBER_ID, info.getWp_friends_info_id()).putExtra(PreviewFriendsInfoActivity.kFROM_CHART_ACTIVITY, false));
            } else {
                startActivity(new Intent(AddFriendsActivity.this, PreviewFriendsInfoActivity.class).putExtra(PreviewFriendsInfoActivity.kUSER_MEMBER_ID, info.getWp_member_info_id()).putExtra(PreviewFriendsInfoActivity.kFROM_CHART_ACTIVITY, false));
            }
        }
    }
}
