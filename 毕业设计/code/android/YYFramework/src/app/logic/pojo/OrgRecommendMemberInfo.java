package app.logic.pojo;

/**
 * Created by apple on 17/4/28.
 */

public class OrgRecommendMemberInfo {

    private String nickName;
    private String picture_url;
    private String phone;
    private String wp_member_info_id;
    private int status;

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWp_member_info_id() {
        return wp_member_info_id;
    }

    public void setWp_member_info_id(String wp_member_info_id) {
        this.wp_member_info_id = wp_member_info_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
