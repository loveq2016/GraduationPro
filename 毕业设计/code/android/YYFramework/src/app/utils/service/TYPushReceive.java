package app.utils.service;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.ql.utils.storage.QLSp;

import app.logic.activity.live.LiveListActivty;
import app.logic.activity.notice.OrgNoticeDefaultActivity;
import app.logic.activity.org.RequestFormListActivity;
import cn.jpush.android.api.JPushInterface;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import app.logic.activity.friends.AddFriendsActivity;
import app.logic.activity.main.HomeActivity;
import app.logic.controller.UserManagerController;
import app.logic.pojo.FriendInfo;
import app.logic.singleton.YYSingleton;
import app.utils.common.AndroidFactory;
import app.utils.common.Listener;
import app.utils.common.Public;

public class TYPushReceive extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // cn.jpush.android.intent.NOTIFICATION_RECEIVED
        // TODO Auto-generated method stub
        Bundle bundle = intent.getExtras();
        if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {  //收到推送时的逻辑
            String pushContent = bundle.getString(JPushInterface.EXTRA_ALERT);
            Log.i("jpush", "收到push通知" + pushContent);
            if (pushContent.contains("想添加你为好友")) {
                friendRequest(context);
            } else if( pushContent.contains("新的公告") ){
                noticeRequest(context);
            }else if( pushContent.contains("申请加入") ){
                System.out.println("XXXXX申请加入组织");
                orgUndealRequest(context);
            }
            int notificationId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            QLSp sp = Public.getSp(AndroidFactory.getApplicationContext());
            sp.put("PUSH", "收到push通知--" + pushContent);
        }
        if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {  //点击通知时的逻辑
            String pushContent = bundle.getString(JPushInterface.EXTRA_ALERT);
            if (pushContent.contains("想添加你为好友")) {
                context.startActivity(new Intent(context, AddFriendsActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }else if(pushContent.contains("新的公告")){
                //context.startActivity(new Intent(context, HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                Intent intent1 = new Intent(context, OrgNoticeDefaultActivity.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                String rog_id = bundle.getString(JPushInterface.EXTRA_EXTRA);
                String name="";
                try {
                    JSONObject jsonObject = new JSONObject(rog_id);
                    rog_id = jsonObject.getString("org_id");
                    name = jsonObject.getString("org_name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                intent1.putExtra(OrgNoticeDefaultActivity.ORG_ID, rog_id );
                intent1.putExtra(OrgNoticeDefaultActivity.ORG_NAME, name );
                context.startActivity(intent1);
            }else if(pushContent.contains("正在直播")){
                context.startActivity(new Intent(context, HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }else if( pushContent.contains("申请加入") ){
                Intent intent1 = new Intent(context, RequestFormListActivity.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                String rog_id = bundle.getString(JPushInterface.EXTRA_EXTRA);
                try {
                    JSONObject jsonObject = new JSONObject(rog_id);
                    rog_id = jsonObject.getString("org_id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                intent1.putExtra(RequestFormListActivity.GET_JOINREQUEST_KRY, rog_id);
                context.startActivity(intent1);
            }
        }
    }

    // 好友请求
    private void friendRequest(final Context context) {
        // 获取好友请求列表
        UserManagerController.getFriendsList(context, new Listener<List<FriendInfo>, List<FriendInfo>>() {
            @Override
            public void onCallBack(List<FriendInfo> status, List<FriendInfo> reply) {
                if (status != null && status.size() > 0) {
                    int count = 0;
                    for (FriendInfo info : status) {
                        if (!info.isResponse()) {
                            count++;
                        }
                    }
                    YYSingleton.getInstance().getFriendRequestListener().onCallBack(count);
                }
            }
        });
    }

    // 公告
    private void noticeRequest(Context context) {
        Intent intentUN = new Intent(HomeActivity.UPDATANOTICE);
        context.sendBroadcast(intentUN);
    }

    // 格局申请成员状态更新
    private void orgUndealRequest(Context context) {
        Intent intentUN = new Intent(HomeActivity.UPDATA_ORG);
        context.sendBroadcast(intentUN);
    }
}
