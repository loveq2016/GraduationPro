package app.logic.pojo;
/**
*
* SiuJiYung create at 2016年8月3日 上午11:42:55
*
*/

public class CheckInInfo {
	
//	private float ckin_lng;
//	private float chin_lat;
//	private String chin_addr;
//	private String ckin_addr;
//	private String org_id;
//	private String member_info_id;
//	private String tag_info;
//	private String ckin_date_time;
//	private String create_time;
	
    private String check_in_info_id;
	private String picture_url;
	private String member_info_id;
	private int id;
	private float chin_lat;
	private String org_id;
	private String nickName;
	private float ckin_lng;
	private String create_time;
	private String chin_addr;
	private String wp_member_info_id;
	private boolean isbuilder;
	private boolean isadmin;
	
	
	
	public String getCheck_in_info_id() {
		return check_in_info_id;
	}



	public void setCheck_in_info_id(String check_in_info_id) {
		this.check_in_info_id = check_in_info_id;
	}



	public String getPicture_url() {
		return picture_url;
	}



	public void setPicture_url(String picture_url) {
		this.picture_url = picture_url;
	}



	public String getMember_info_id() {
		return member_info_id;
	}



	public void setMember_info_id(String member_info_id) {
		this.member_info_id = member_info_id;
	}



	public int getId() {
		return id;
	}



	public void setId(int id) {
		this.id = id;
	}



	public float getChin_lat() {
		return chin_lat;
	}



	public void setChin_lat(float chin_lat) {
		this.chin_lat = chin_lat;
	}



	public String getOrg_id() {
		return org_id;
	}



	public void setOrg_id(String org_id) {
		this.org_id = org_id;
	}



	public String getNickName() {
		return nickName;
	}



	public void setNickName(String nickName) {
		this.nickName = nickName;
	}



	public float getCkin_lng() {
		return ckin_lng;
	}



	public void setCkin_lng(float ckin_lng) {
		this.ckin_lng = ckin_lng;
	}



	public String getCreate_time() {
		return create_time;
	}



	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}



	public String getChin_addr() {
		return chin_addr;
	}



	public void setChin_addr(String chin_addr) {
		this.chin_addr = chin_addr;
	}



	public String getWp_member_info_id() {
		return wp_member_info_id;
	}



	public void setWp_member_info_id(String wp_member_info_id) {
		this.wp_member_info_id = wp_member_info_id;
	}



	public boolean isIsbuilder() {
		return isbuilder;
	}



	public void setIsbuilder(boolean isbuilder) {
		this.isbuilder = isbuilder;
	}



	public boolean isIsadmin() {
		return isadmin;
	}



	public void setIsadmin(boolean isadmin) {
		this.isadmin = isadmin;
	}



	@Override
	public String toString() {
		return getCreate_time();
	}
}
