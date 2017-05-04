package app.logic.pojo;
/**
*
* SiuJiYung create at 2016年8月3日 上午11:42:55
*
*/

public class UnCheckInInfo {
	

	private String picture_url;
	private String nickName;
	private String wp_member_info_id;
	private boolean isadmin;
	private boolean isbuilder;
	
	public String getPicture_url() {
		return picture_url;
	}
	public void setPicture_url(String picture_url) {
		this.picture_url = picture_url;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getWp_member_info_id() {
		return wp_member_info_id;
	}
	public void setWp_member_info_id(String wp_member_info_id) {
		this.wp_member_info_id = wp_member_info_id;
	}
	public boolean isIsadmin() {
		return isadmin;
	}
	public void setIsadmin(boolean isadmin) {
		this.isadmin = isadmin;
	}
	public boolean isIsbuilder() {
		return isbuilder;
	}
	public void setIsbuilder(boolean isbuilder) {
		this.isbuilder = isbuilder;
	}
	
	@Override
	public String toString() {
		return "UnCheckInInfo [picture_url=" + picture_url + ", nickName=" + nickName + ", wp_member_info_id=" + wp_member_info_id + ", isadmin=" + isadmin + ", isbuilder=" + isbuilder + "]";
	}
	
}
