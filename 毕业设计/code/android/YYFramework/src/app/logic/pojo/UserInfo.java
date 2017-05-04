package app.logic.pojo;

import java.io.Serializable;
import java.util.List;

public class UserInfo implements Serializable {

	private String name;
	private String nickName;
	private String picture_url;
	private String my_picture_url;
	private String phone;

	private String sex;
	private String paymentType;
	private String type;
	private String password;
	private String id;
	// private String rank;
	// private String accountMoney;
	private String email;
	private String bindPhoneStatus;
	private String birthday_date;
	private String realName;
	// private String points;
	private String wp_member_info_id;
	private String memberNo;
	private String wp_error_msg;
	private String location;
	private boolean isShowDelect = false;

	//公司
	private String native_place;
	private String company_addr;
	private String company_duty;
	private String company_industry;
	private String company_industry_id;
	private String company_logo;
	private String company_name;
	private String company_scope;
	private String company_scope_id;
	private String company_url;
	private String company_intro;

	private String sortLetters;

	//微信唯一性id
	private String openid;

	//2017.3.17 YSF   "string 00为非好友，10申请中，11已是好友"
	private String friendStatus;
	private int is_remove ;

	public int getIs_remove() {
		return is_remove;
	}

	public void setIs_remove(int is_remove) {
		this.is_remove = is_remove;
	}

	public String getFriendStatus() {
		return friendStatus;
	}

	public void setFriendStatus(String friendStatus) {
		this.friendStatus = friendStatus;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	private List<FriendInfo> cr_memberList;

	// ysf 新增
	private boolean isadmin ;
	private boolean isbuilder ;

	public boolean isadmin() {
		return isadmin;
	}

	public void setIsadmin(boolean isadmin) {
		this.isadmin = isadmin;
	}

	public boolean isbuilder() {
		return isbuilder;
	}

	public void setIsbuilder(boolean isbuilder) {
		this.isbuilder = isbuilder;
	}

	private boolean lastItem = false;

	public boolean isLastItem() {
		return lastItem;
	}

	public void setLastItem(boolean lastItem) {
		this.lastItem = lastItem;
	}

	public boolean isShowDelect() {
		return isShowDelect;
	}

	public void setShowDelect(boolean isShowDelect) {
		this.isShowDelect = isShowDelect;
	}

	// 好友信息
	private boolean response;
	private String add_friend_id;
	private String wp_friends_info_id;// 好友的ID
	private int isAccess;
	private boolean request_accept;

	// 所属的分组列表，标志删除
	private boolean dpm_status;
	// 标志最后一个
	private boolean isDPMLastMenber;

	private String friend_name;

	public String getFriend_name() {
		return friend_name;
	}

	public void setFriend_name(String friend_name) {
		this.friend_name = friend_name;
	}

	public boolean isDPMLastMenber() {
		return isDPMLastMenber;
	}

	public void setDPMLastMenber(boolean isDPMLastMenber) {
		this.isDPMLastMenber = isDPMLastMenber;
	}

	public boolean isDpm_status() {
		return dpm_status;
	}

	public void setDpm_status(boolean dpm_status) {
		this.dpm_status = dpm_status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getMy_picture_url() {
		String result = my_picture_url == null ? picture_url : my_picture_url;
		return result;
	}

	public void setMy_picture_url(String my_picture_url) {
		this.my_picture_url = my_picture_url;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	// public String getRank() {
	// return rank;
	// }
	//
	// public void setRank(String rank) {
	// this.rank = rank;
	// }
	//
	// public String getAccountMoney() {
	// return accountMoney;
	// }
	//
	// public void setAccountMoney(String accountMoney) {
	// this.accountMoney = accountMoney;
	// }

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

	public String getBirthday_date() {
		return birthday_date;
	}

	public void setBirthday_date(String birthday_date) {
		this.birthday_date = birthday_date;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	// public String getPoints() {
	// return points;
	// }
	//
	// public void setPoints(String points) {
	// this.points = points;
	// }

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

	public String getWp_error_msg() {
		return wp_error_msg;
	}

	public void setWp_error_msg(String wp_error_msg) {
		this.wp_error_msg = wp_error_msg;
	}

	public boolean isResponse() {
		return response;
	}

	public void setResponse(boolean response) {
		this.response = response;
	}

	public String getAdd_friend_id() {
		return add_friend_id;
	}

	public void setAdd_friend_id(String add_friend_id) {
		this.add_friend_id = add_friend_id;
	}

	public String getWp_friends_info_id() {
		return wp_friends_info_id;
	}

	public void setWp_friends_info_id(String wp_friends_info_id) {
		this.wp_friends_info_id = wp_friends_info_id;
	}

	public int getIsAccess() {
		return isAccess;
	}

	public void setIsAccess(int isAccess) {
		this.isAccess = isAccess;
	}

	public boolean isRequest_accept() {
		return request_accept;
	}

	public void setRequest_accept(boolean request_accept) {
		this.request_accept = request_accept;
	}

	public String getRegion() {
		return location;
	}

	public void setRegion(String region) {
		this.location = region;
	}

	public List<FriendInfo> getCr_memberList() {
		return cr_memberList;
	}

	public void setCr_memberList(List<FriendInfo> cr_memberList) {
		this.cr_memberList = cr_memberList;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getNative_place() {
		return native_place;
	}

	public void setNative_place(String native_place) {
		this.native_place = native_place;
	}

	public String getCompany_addr() {
		return company_addr;
	}

	public void setCompany_addr(String company_addr) {
		this.company_addr = company_addr;
	}

	public String getCompany_duty() {
		return company_duty;
	}

	public void setCompany_duty(String company_duty) {
		this.company_duty = company_duty;
	}

	public String getCompany_industry() {
		return company_industry;
	}

	public void setCompany_industry(String company_industry) {
		this.company_industry = company_industry;
	}

	public String getCompany_industry_id() {
		return company_industry_id;
	}

	public void setCompany_industry_id(String company_industry_id) {
		this.company_industry_id = company_industry_id;
	}

	public String getCompany_logo() {
		return company_logo;
	}

	public void setCompany_logo(String company_logo) {
		this.company_logo = company_logo;
	}

	public String getCompany_name() {
		return company_name;
	}

	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}

	public String getCompany_scope() {
		return company_scope;
	}

	public void setCompany_scope(String company_scope) {
		this.company_scope = company_scope;
	}

	public String getCompany_scope_id() {
		return company_scope_id;
	}

	public void setCompany_scope_id(String company_scope_id) {
		this.company_scope_id = company_scope_id;
	}

	public String getCompany_url() {
		return company_url;
	}

	public void setCompany_url(String company_url) {
		this.company_url = company_url;
	}

	public String getSortLetters() {
		return sortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}

	public String getCompany_intro() {
		return company_intro;
	}

	public void setCompany_intro(String company_intro) {
		this.company_intro = company_intro;
	}
}
