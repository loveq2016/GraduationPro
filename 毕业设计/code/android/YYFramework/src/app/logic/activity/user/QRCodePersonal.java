package app.logic.activity.user;

/**
 * Created by Administrator on 2017/3/7 0007.
 */

public class QRCodePersonal {

    private String phone;
    private String nickName;
    private String picture_url;
    private String location;
    private String wp_member_info_id;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPicture_url() {
        return picture_url;
    }

    public void setPicture_url(String picture_url) {
        this.picture_url = picture_url;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWp_member_info_id() {
        return wp_member_info_id;
    }

    public void setWp_member_info_id(String wp_member_info_id) {
        this.wp_member_info_id = wp_member_info_id;
    }
}
