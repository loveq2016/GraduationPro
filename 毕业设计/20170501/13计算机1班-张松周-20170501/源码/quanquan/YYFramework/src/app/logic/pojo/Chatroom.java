package app.logic.pojo;

/**
 * Created by GZYY on 17/1/6.
 */

public class Chatroom {
    private String cr_creatorName;
    private String cr_id;
    private String room_id;
    private String cr_creatorId;
    private String cr_type;
    private String cr_name;
    private String cr_des;
    private String cr_notice;
    private String  cr_picture;


    private String latestChart;
    private int UnReadMessageCount;
    private long msgLastTime;
    private String msgLastTimeString;

    public String getMsgLastTimeString() {
        return msgLastTimeString;
    }

    public void setMsgLastTimeString(String msgLastTimeString) {
        this.msgLastTimeString = msgLastTimeString;
    }

    public String getLatestChart() {
        return latestChart;
    }

    public void setLatestChart(String latestChart) {
        this.latestChart = latestChart;
    }

    public int getUnReadMessageCount() {
        return UnReadMessageCount;
    }

    public void setUnReadMessageCount(int unReadMessageCount) {
        UnReadMessageCount = unReadMessageCount;
    }

    public long getMsgLastTime() {
        return msgLastTime;
    }

    public void setMsgLastTime(long msgLastTime) {
        this.msgLastTime = msgLastTime;
    }

    public String getCr_creatorName() {
        return cr_creatorName;
    }

    public void setCr_creatorName(String cr_creatorName) {
        this.cr_creatorName = cr_creatorName;
    }

    public String getCr_id() {
        return cr_id;
    }

    public void setCr_id(String cr_id) {
        this.cr_id = cr_id;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public String getCr_creatorId() {
        return cr_creatorId;
    }

    public void setCr_creatorId(String cr_creatorId) {
        this.cr_creatorId = cr_creatorId;
    }

    public String getCr_type() {
        return cr_type;
    }

    public void setCr_type(String cr_type) {
        this.cr_type = cr_type;
    }

    public String getCr_name() {
        return cr_name;
    }

    public void setCr_name(String cr_name) {
        this.cr_name = cr_name;
    }

    public String getCr_des() {
        return cr_des;
    }

    public void setCr_des(String cr_des) {
        this.cr_des = cr_des;
    }

    public String getCr_notice() {
        return cr_notice;
    }

    public void setCr_notice(String cr_notice) {
        this.cr_notice = cr_notice;
    }

    public String getCr_picture() {
        return cr_picture;
    }

    public void setCr_picture(String cr_picture) {
        this.cr_picture = cr_picture;
    }
}
