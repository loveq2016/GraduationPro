package app.logic.singleton;

import android.R.integer;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import app.logic.pojo.OrgUnreadNumberInfo;
import app.logic.pojo.OrganizationInfo;

/*
 * GZYY    2016-10-12  上午11:19:04
 */

public class YYInterface {


    private IUpdataChatTitle iUpdataChatTitle;

    public interface IUpdataChatTitle {
        void onCallBack(String name);
    }

    public void setIUpdataChatTitle(IUpdataChatTitle iUpdataChatTitle) {
        this.iUpdataChatTitle = iUpdataChatTitle;
    }

    public IUpdataChatTitle getIUpdataChatTitle() {
        return iUpdataChatTitle;
    }


    // 是否好友请求，
    private FriendRequestListener friendRequestListener;

    public void setFriendRequestListener(FriendRequestListener listener) {
        this.friendRequestListener = listener;
    }

    public FriendRequestListener getFriendRequestListener() {
        return friendRequestListener;
    }

    public interface FriendRequestListener {
        void onCallBack(int count);
    }

    // 打开添加好友的界面后，tabhost上取消显示好友数量
    private CancleFriendRequestListener cancleFriendRequestListenerl;

    public CancleFriendRequestListener getCancleFriendRequestListener() {
        return cancleFriendRequestListenerl;
    }

    public void setCancleFriendRequestListener(CancleFriendRequestListener listener) {
        this.cancleFriendRequestListenerl = listener;
    }

    public interface CancleFriendRequestListener {
        void onCallBack();
    }

    // 回调好友删除，及时刷新联系人列表
    private UpdataFriendListListener updataFriendListListener;

    public UpdataFriendListListener getUpdataFriendListListener() {
        return updataFriendListListener;
    }

    public void setUpdataFriendListListener(UpdataFriendListListener listener) {
        this.updataFriendListListener = listener;
    }

    public interface UpdataFriendListListener {
        void onCallBack();
    }

    // 重发跳过onActivityResult，直接触发重发事件
    private ResendMessageListener mResendMessageListener;

    public void setResendMessage(ResendMessageListener listener) {
        this.mResendMessageListener = listener;
    }

    public ResendMessageListener getResendMessageListener() {
        return mResendMessageListener;
    }

    public interface ResendMessageListener {
        void onCallBack();
    }

    // 拉人进组织，回调更新组织成员数
    private UpdataOrgMemNumListener updataOrgMemNumListener;

    public void setUpdataOrgMemNumListener(UpdataOrgMemNumListener l) {
        this.updataOrgMemNumListener = l;
    }

    public UpdataOrgMemNumListener getUpdataOrgMemNumListener() {
        return updataOrgMemNumListener;
    }

    public interface UpdataOrgMemNumListener {
        void onCallBack(int i);
    }

    // title点击，刷新未读Fragment
    public interface UndataTitleToUnreadFragmentListener {
        void onCallBack(OrgUnreadNumberInfo info);
    }

    private UndataTitleToUnreadFragmentListener undataTitleToUnreadFragmentListener;

    public void setUndataTitleToUnreadFragmentListener(UndataTitleToUnreadFragmentListener listener) {
        this.undataTitleToUnreadFragmentListener = listener;
    }

    public UndataTitleToUnreadFragmentListener getUndataTitleToUnreadFragmentListener() {
        return undataTitleToUnreadFragmentListener;
    }

    // title点击，刷新已读Fragment
    private UpdataTitleToReadFragmentListener updataTitleToReadFragmentListener;

    public interface UpdataTitleToReadFragmentListener {
        void onCallBack(OrgUnreadNumberInfo info);
    }

    public void setUpdataTitleToReadFragmentListener(UpdataTitleToReadFragmentListener listener) {
        this.updataTitleToReadFragmentListener = listener;
    }

    public UpdataTitleToReadFragmentListener getUpdataTitleToReadFragmentListener() {
        return updataTitleToReadFragmentListener;
    }


    public interface OnHasFriendsStatusPoint {
        void onCallBack(boolean status);
    }

    private OnHasFriendsStatusPoint onHasFriendsStatusPoint;

    public OnHasFriendsStatusPoint getOnHasFriendsStatusPoint() {
        return onHasFriendsStatusPoint;
    }

    public void setOnHasFriendsStatusPoint(OnHasFriendsStatusPoint onHasFriendsStatusPoint) {
        this.onHasFriendsStatusPoint = onHasFriendsStatusPoint;
    }

    //格局申请列表未处理
    private List<String> orgIdList = new ArrayList<>();
    public List<String> getOrgIdList(){
        return orgIdList;
    }
    public void setOrgStatusPoint(boolean isRemove,String orgId){
        if (isRemove){
            if (orgIdList.contains(orgId)){
                orgIdList.remove(orgId);
            }
        }else{
            if (!orgIdList.contains(orgId)){
                orgIdList.add(orgId);
            }
        }
    }

    // 是否好友请求，
    private OrgRequestListener orgRequestListener;

    public void setOrgRequestListener(OrgRequestListener listener) {
        this.orgRequestListener = listener;
    }

    public OrgRequestListener getOrgRequestListener() {
        return orgRequestListener;
    }

    public interface OrgRequestListener {
        void onCallBack(int count);
    }

    private List<OrgUnreadNumberInfo> datas = new ArrayList<>(); //列表数据源
    public void setOrgUnreadDatas(List<OrgUnreadNumberInfo> datas){
        this.datas = datas;
    }
    public List<OrgUnreadNumberInfo> getOrgUnreadDatas(){
        return datas;
    }
}
