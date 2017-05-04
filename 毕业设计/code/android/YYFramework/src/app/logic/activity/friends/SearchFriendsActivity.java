package app.logic.activity.friends;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.squareup.picasso.Picasso;

import org.canson.view.swipemenulistview.SwipeMenu;
import org.canson.view.swipemenulistview.SwipeMenuCreator;
import org.canson.view.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import org.ql.activity.customtitle.ActActivity;
import org.ql.activity.customtitle.onActPermissionCheckResultListener;
import org.ql.app.alert.AlertDialog;
import org.ql.utils.QLToastUtils;
import org.ql.views.listview.QLXListView.IXListViewListener;

import java.util.ArrayList;
import java.util.List;

import app.config.http.HttpConfig;
import app.logic.activity.ActTitleHandler;
import app.logic.activity.org.ApplyToJoinActivity;
import app.logic.activity.user.PreviewFriendsInfoActivity;
import app.logic.activity.user.QRCodePersonal;
import app.logic.adapter.YYBaseListAdapter;
import app.logic.controller.OrganizationController;
import app.logic.controller.UserManagerController;
import app.logic.pojo.FriendInfo;
import app.logic.pojo.OrganizationInfo;
import app.logic.pojo.UserInfo;
import app.logic.singleton.YYSingleton;
import app.utils.common.FrescoImageShowThumb;
import app.utils.common.Listener;
import app.utils.helpers.ChartHelper;
import app.utils.helpers.QRHelper;
import app.view.DialogNewStyleController;
import app.view.YYListView;
import app.yy.geju.R;

/**
 * SiuJiYung create at 2016年6月14日 下午5:29:05
 * <p>
 * 添加好友
 */

public class SearchFriendsActivity extends ActActivity implements OnClickListener, IXListViewListener, OnItemClickListener {

    public static final String SEARCH_KEY = "SEARCH_KEY";

    private ActTitleHandler mHandler = new ActTitleHandler();
    private YYListView requestListView;
    private AlertDialog alertDialog;
    private AlertDialog alertDialogFriends;
    private AlertDialog alertDialogOrg;

    String search_key;
    private List<FriendInfo> friendList = new ArrayList<>();
    private int start = 0;
    private int limit = 20;

    private OnClickListener btnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == null) {
                return;
            }
                FriendInfo info = (FriendInfo) v.getTag();
                // showAddFriendsDialog(info);
            showAddFriendsDialog2(info);
        }
    };

    private YYBaseListAdapter<FriendInfo> mAdapter = new YYBaseListAdapter<FriendInfo>(this) {
        @Override
        public View createView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(SearchFriendsActivity.this).inflate(R.layout.item_row_request_friends, null);
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
                msgTv.setVisibility(View.GONE );
                if(info.getFriend_name()!=null && !TextUtils.isEmpty(info.getFriend_name())){
                    nameTv.setText(info.getFriend_name());
                }else {
                    nameTv.setText(info.getNickName());
                }
                FrescoImageShowThumb.showThrumb(Uri.parse(HttpConfig.getUrl(info.getPicture_url())),userHeadImageView);

                if (info.getFriendStatus().equals("11")){
                    okBtn.setEnabled(false);
                    okBtn.setText("已添加");
                    okBtn.setTextColor(0xffc6c6c6);

                }else if(info.getFriendStatus().equals("10")){
                    okBtn.setEnabled(false);
                    okBtn.setText("已请求");
                    okBtn.setTextColor(0xffc6c6c6);
                }else{
                    okBtn.setEnabled(true);
                    okBtn.setText("添加");
                    okBtn.setTextColor(0xfffcfcfc);
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
        ((TextView) mHandler.getLeftLayout().findViewById(R.id.left_tv)).setText("搜索结果");

        setTitle("");
        findViewById(R.id.add_friends_head).setVisibility(View.GONE);
        findViewById(R.id.add_friend_title).setVisibility(View.GONE);

        requestListView = (YYListView) findViewById(R.id.add_friends_lv);
        requestListView.setPullRefreshEnable(true);
        requestListView.setPullLoadEnable(true);
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
        findViewById(R.id.add_friends_contact).setOnClickListener(this);

        search_key = getIntent().getStringExtra(SEARCH_KEY);

        onRefresh();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();


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
                    personalInfo.setWp_member_info_id(info.getWp_member_info_id());
                    showAddFriendsDialog2(personalInfo);

                    return;
                }
                QLToastUtils.showToast(SearchFriendsActivity.this, "没有该用户");
            }
        });
    }

    /**
     * 响应请求
     *
     * @param info
     */
//    private void showAddFriendsDialog(final FriendInfo info) {
//        if (alertDialog != null && alertDialog.isShowing()) {
//            return;
//        }
//        View view = LayoutInflater.from(this).inflate(R.layout.alert_add_friends, null);
//        if (alertDialog == null) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            alertDialog = builder.create();
//            alertDialog.setView(view);
//            alertDialog.setCancelable(true);
//            alertDialog.setIcon(0);
//            alertDialog.setCanceledOnTouchOutside(true);
//            alertDialog.setTitle("同意添加好友");
//        }
//
//        if (info.isRequestMessage()) {
//            ImageView iView = ((ImageView) view.findViewById(R.id.add_friends_userhead_iv));
//            Uri uri = Uri.parse(HttpConfig.getUrl(info.getRequest_picture_url()));
//            Picasso.with(this).load(uri).placeholder(R.drawable.default_user_icon).into(iView);
//            ((TextView) view.findViewById(R.id.add_friends_name_tv)).setText(info.getRequest_nickName());
//            ((TextView) view.findViewById(R.id.add_friends_org_tv)).setText("");
//        } else {
//            ImageView iView = ((ImageView) view.findViewById(R.id.add_friends_userhead_iv));
//            Uri uri = Uri.parse(HttpConfig.getUrl(info.getPicture_url()));
//            Picasso.with(this).load(uri).placeholder(R.drawable.default_user_icon).into(iView);
//            ((TextView) view.findViewById(R.id.add_friends_name_tv)).setText(info.getNickName());
//            ((TextView) view.findViewById(R.id.add_friends_org_tv)).setText("");
//        }
//        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if (which == AlertDialog.BUTTON_NEGATIVE) {
//                    // 添加
//                    if (info.isRequestMessage()) {
//                        responseFriends(info, true);
//                    }
//                }
//                dialog.dismiss();
//            }
//        };
//
//        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "添加", dialogClickListener);
//        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "取消", dialogClickListener);
//        alertDialog.show();
//    }


//    private void showAddFriendsDialog(final UserInfo info) {
//        if (alertDialogFriends != null && alertDialogFriends.isShowing()) {
//            return;
//        }
//        final View view = LayoutInflater.from(this).inflate(R.layout.alert_add_friends2, null);
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        alertDialogFriends = builder.create();
//        alertDialogFriends.setView(view);
//        alertDialogFriends.setCancelable(true);
//        alertDialogFriends.setIcon(0);
//        alertDialogFriends.setCanceledOnTouchOutside(true);
//        alertDialogFriends.setTitle("添加好友");
//
//        ImageView iView = ((ImageView) view.findViewById(R.id.add_friends_userhead_iv));
//        Uri uri = Uri.parse(HttpConfig.getUrl(info.getMy_picture_url()));
//        Picasso.with(this).load(uri).placeholder(R.drawable.default_user_icon).fit().centerInside().into(iView);
//        ((TextView) view.findViewById(R.id.add_friends_name_tv)).setText(info.getNickName());
//        ((TextView) view.findViewById(R.id.add_friends_org_tv)).setText("");
//        android.content.DialogInterface.OnClickListener dialogClickListener = new android.content.DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//                if (which == AlertDialog.BUTTON_NEGATIVE) {
//                    // hideKeyboard();
//
//                    // 添加
//                    String msg = ((EditText) view.findViewById(R.id.add_friends_msg_et)).getText().toString();
//                    requestFriends(info.getPhone(), msg);
//                }
//                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(searchET.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//                dialog.dismiss();
//
//            }
//        };
//
//        alertDialogFriends.setButton(AlertDialog.BUTTON_NEGATIVE, "添加", dialogClickListener);
//        alertDialogFriends.setButton(AlertDialog.BUTTON_NEUTRAL, "取消", dialogClickListener);
//        alertDialogFriends.show();
//    }


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
//                requestFriends(personalInfo.getPhone(), msg);
                requestFriendsById(personalInfo.getWp_member_info_id(),msg);
                addFriendsDailog.dismiss();
                onRefresh();

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
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == AlertDialog.BUTTON_NEGATIVE) {
                    // 添加
                    String msg = ((EditText) view.findViewById(R.id.add_friends_msg_et)).getText().toString();
                    OrganizationController.joinOrganization(SearchFriendsActivity.this, info.getOrg_id(), msg, new Listener<Boolean, String>() {

                        @Override
                        public void onCallBack(Boolean status, String reply) {
                            if (status) {
                                QLToastUtils.showToast(SearchFriendsActivity.this, "申请成功！等待管理员的审核。。。");
                                return;
                            }
                            QLToastUtils.showToast(SearchFriendsActivity.this, reply);
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
                OrganizationController.joinOrganization(SearchFriendsActivity.this, info.getOrg_id(), msg, new Listener<Boolean, String>() {

                    @Override
                    public void onCallBack(Boolean status, String reply) {
                        if (status) {
                            QLToastUtils.showToast(SearchFriendsActivity.this, "申请成功！等待管理员的审核。。。");
                            return;
                        }
                        QLToastUtils.showToast(SearchFriendsActivity.this, reply);
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
        UserManagerController.deleteFriendsById(this, info.getWp_friends_info_id(), new Listener<Integer, String>() {
            @Override
            public void onCallBack(Integer status, String reply) {
                if (status == -1) {
                    String msg = reply == null ? "移除失败" : reply;
                    QLToastUtils.showToast(SearchFriendsActivity.this, msg);
                }
            }
        });
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
                    QLToastUtils.showToast(SearchFriendsActivity.this, msg);
                    return;
                }
                QLToastUtils.showToast(SearchFriendsActivity.this, "请求已发送");
                // getRequestFrendsList();
                onRefresh();
            }
        });
    }
//    private void requestFriends(String phone, String msg) {
//        UserManagerController.addFriends(this, phone, msg, new Listener<Integer, String>() {
//            @Override
//            public void onCallBack(Integer status, String reply) {
//                // hideKeyboard();
//                if (status == -1) {
//                    String msg = reply == null ? "操作失败" : reply;
//                    QLToastUtils.showToast(AddFriendsActivity.this, msg);
//                    return;
//                }
//                QLToastUtils.showToast(AddFriendsActivity.this, "请求已发送");
//                // getRequestFrendsList();
//                getListFriendMessage();
//            }
//        });
//    }

    /**
     * 响应请求
     *
     * @param info
     * @param ok
     */
    private void responseFriends(final FriendInfo info) {
        showWaitDialog();
//        UserManagerController.ensureFriends(this, info.getAdd_friend_id(), ok ? 1 : 0, info.getWp_friends_info_id(), new Listener<Integer, String>() {
//            @Override
//            public void onCallBack(Integer status, String reply) {
//                dismissWaitDialog();
//                if (status == 1) {
//
//                    //将该人置顶
//                    UserManagerController.addChatWith(SearchFriendsActivity.this, info.getWp_member_info_id(), "", new Listener<Integer, String>() {
//                        @Override
//                        public void onCallBack(Integer integer, String reply) {
//                            if (integer == 1) {
//
//                                EMMessage message = EMMessage.createTxtSendMessage("我们已经是好友了，现在开始对话吧！", info.getRequest_phone());
//                                EMClient.getInstance().chatManager().sendMessage(message);
//                                onRefresh();
//                            }
//                        }
//                    });
////                    EMMessage message = EMMessage.createTxtSendMessage("我们已经是好友了，现在开始对话吧！", info.getRequest_phone());
////                    EMClient.getInstance().chatManager().sendMessage(message);
//                    // getRequestFrendsList();
//                }
//            }
//        });
        UserManagerController.getPhoneMemerInfo(this, info.getWp_member_info_id(), new Listener<Integer, List<UserInfo>>() {
            @Override
            public void onCallBack(Integer status, List<UserInfo> reply) {
                dismissWaitDialog();
                if (reply != null && reply.size() > 0) {
                    UserInfo info = reply.get(0);
//                    info.setPhone(txt);
                    //showAddFriendsDialog(info);
                    //showAddFriendsDialog2(info);
                    QRCodePersonal personalInfo = new QRCodePersonal();
                    personalInfo.setPhone( info.getPhone());
                    personalInfo.setNickName( info.getNickName());
                    personalInfo.setPicture_url( info.getPicture_url());
                    personalInfo.setLocation( info.getLocation());
                    personalInfo.setWp_member_info_id(info.getWp_member_info_id());
                    showAddFriendsDialog2(personalInfo);

                    return;
                }
                QLToastUtils.showToast(SearchFriendsActivity.this, "没有该用户");
            }
        });
    }

    /**
     * @param
     */
    private void showAddFriendsDialog2(final FriendInfo personalInfo) {
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
    private void getListFriendMessage(final int start) {
        UserManagerController.searchFriend(this, search_key,start,limit,new Listener<List<FriendInfo>, String>() {

            @Override
            public void onCallBack(List<FriendInfo> reply, String mag) {
                requestListView.stopLoadMore();
                requestListView.stopRefresh();

                if (reply !=null) {
                    if (start == 0)
                        friendList.clear();
                    friendList.addAll(reply);
                    mAdapter.setDatas(friendList);

                    if (reply.size()<limit){
                        requestListView.setPullLoadEnable(false,true);
                    }else{
                        requestListView.setPullLoadEnable(true);
                    }
                }else{
                    QLToastUtils.showToast(SearchFriendsActivity.this,"无匹配");
                }
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
                    onRefresh();
                    return;
                }
                QLToastUtils.showToast(SearchFriendsActivity.this, reply);
            }
        });
    }

    @Override
    public void onClick(View v) {
    }


    @Override
    public void onRefresh() {
        // getRequestFrendsList();
        start = 0;
        getListFriendMessage(start);
    }

    @Override
    public void onLoadMore() {
        // TODO Auto-generated method stub
        start+=limit;
        getListFriendMessage(start);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FriendInfo info = mAdapter.getItem(position - 1);
        if (info != null) {
            if (TextUtils.isEmpty(info.getWp_member_info_id())) {
                startActivity(new Intent(SearchFriendsActivity.this, PreviewFriendsInfoActivity.class).putExtra(PreviewFriendsInfoActivity.kUSER_MEMBER_ID, info.getWp_friends_info_id()).putExtra(PreviewFriendsInfoActivity.kFROM_CHART_ACTIVITY, false));
            } else {
                startActivity(new Intent(SearchFriendsActivity.this, PreviewFriendsInfoActivity.class).putExtra(PreviewFriendsInfoActivity.kUSER_MEMBER_ID, info.getWp_member_info_id()).putExtra(PreviewFriendsInfoActivity.kFROM_CHART_ACTIVITY, false));
            }
        }
    }
}
