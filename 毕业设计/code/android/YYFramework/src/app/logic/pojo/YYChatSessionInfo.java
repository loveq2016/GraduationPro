package app.logic.pojo;


/**
 * @author SiuJiYung
 *         create at 2016-5-13下午2:18:04
 *         聊天会话信息
 */
public class YYChatSessionInfo {

    private String remark;
    private String create_time;
    private String wp_other_info_id;
    private String wp_member_info_id;
    private String wp_dialogue_info_id;
    //真实姓名
    private String name;
    //昵称
    private String nickName;
    //电话号码
    private String phoneNumber;
    private String phone;
    //头像
    private String picture_url;
    //组织机构名称
    private String organizationName;
    //组织机构ID
    private String organizationId;
    private String latestChart;
    private int unreadCount;
    private long messTime;
    private String friend_name;
    private String lastTime;


    private Chatroom chatroom;

    public Chatroom getChatroom() {
        return chatroom;
    }

    public void setChatroom(Chatroom chatroom) {
        this.chatroom = chatroom;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public String getFriend_name() {
        return friend_name;
    }

    public void setFriend_name(String friend_name) {
        this.friend_name = friend_name;
    }

    public long getMessTime() {
        return messTime;
    }

    public void setMessTime(long messTime) {
        this.messTime = messTime;
    }

    public String toString() {
        return "与来自" + remark + "的 " + nickName + " 聊天";
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getWp_other_info_id() {
        return wp_other_info_id;
    }

    public void setWp_other_info_id(String wp_other_info_id) {
        this.wp_other_info_id = wp_other_info_id;
    }

    public String getWp_member_info_id() {
        return wp_member_info_id;
    }

    public void setWp_member_info_id(String wp_member_info_id) {
        this.wp_member_info_id = wp_member_info_id;
    }

    public String getWp_dialogue_info_id() {
        return wp_dialogue_info_id;
    }

    public void setWp_dialogue_info_id(String wp_dialogue_info_id) {
        this.wp_dialogue_info_id = wp_dialogue_info_id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPhoneNumber() {
        return phoneNumber == null ? phone : phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.phone = phoneNumber;
    }

    public String getPicture_url() {
        return picture_url;
    }

    public void setPicture_url(String picture_url) {
        this.picture_url = picture_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getLatestChart() {
        return latestChart;
    }

    public void setLatestChart(String latestChart) {
        this.latestChart = latestChart;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

}
