package app.logic.pojo;

import android.R.integer;

/*
 * GZYY    2016-8-31  上午11:27:52
 */

public class OrgRequestMemberInfo {

	private String phone;
	private String sex;
	private String picture_url;
	private String paymentType;
	private String type;
	// private int rank;
	// private int accountMoney;
	private String nickName;
	private String email;
	private String bindPhoneStatus;
	private String name;
	private String my_picture_url;
	private String realName;
	// private int points;
	private String departmentId;
	private String wp_member_info_id;

	private String friend_name;

	private String memberNo;
	private boolean isadmin;
	private boolean isbuilder;
	private boolean footViewStatus = false;

	private boolean showLine = false;
	//新增
	private boolean member_isadmin ;

	public boolean isMember_isadmin() {
		return member_isadmin;
	}

	public void setMember_isadmin(boolean member_isadmin) {
		this.member_isadmin = member_isadmin;
	}



	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getPicture_url() {
		return picture_url;
	}

	public void setPicture_url(String picture_url) {
		this.picture_url = picture_url;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getBindPhoneStatus() {
		return bindPhoneStatus;
	}

	public void setBindPhoneStatus(String bindPhoneStatus) {
		this.bindPhoneStatus = bindPhoneStatus;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMy_picture_url() {
		return my_picture_url;
	}

	public void setMy_picture_url(String my_picture_url) {
		this.my_picture_url = my_picture_url;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}

	public String getWp_member_info_id() {
		return wp_member_info_id;
	}

	public void setWp_member_info_id(String wp_member_info_id) {
		this.wp_member_info_id = wp_member_info_id;
	}

	public String getMemberNo() {
		return memberNo;
	}

	public void setMemberNo(String memberNo) {
		this.memberNo = memberNo;
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

	public boolean isFootViewStatus() {
		return footViewStatus;
	}

	public void setFootViewStatus(boolean footViewStatus) {
		this.footViewStatus = footViewStatus;
	}

	public boolean isShowLine() {
		return showLine;
	}

	public void setShowLine(boolean showLine) {
		this.showLine = showLine;
	}

	private int dpmStatus = 0;

	public int isDpmStatus() {
		return dpmStatus;
	}

	public void setDpmStatus(int dpmStatus) {
		this.dpmStatus = dpmStatus;
	}

	private FriendInfo friendInfo;

	public OrgRequestMemberInfo() {

	}

	public OrgRequestMemberInfo(FriendInfo info) {
		this.friendInfo = info;
	}
	public FriendInfo getFriendInfo(){
		return friendInfo;
	}

	public String getFriend_name() {
		return friend_name;
	}

	public void setFriend_name(String friend_name) {
		this.friend_name = friend_name;
	}
}
