package app.logic.pojo;

import java.io.Serializable;

import app.logic.activity.org.ErrorMsg;

/**
 * 
 * SiuJiYung create at 2016年6月15日 下午6:52:16 组织机构信息
 */

public class OrganizationInfo implements Serializable {

	private String org_name;
	private String org_gegion_code;
	private String org_tag;
	private String org_tel;
	private String org_email;
	private String member_id;
	private String org_des;
	private String org_id;
	private String org_industryName;
	private String org_city;
	private String org_provice;
	private String org_industryId;
	private int org_status;
	private String org_contact_tel;
	private String org_addr;
	private String org_contact_name;
	private String org_logo_url;
	private String apply_status;

	private String org_pic;
	private String org_wenr;
	private String org_cer;

	//
	private String org_builder_name;

	private String nickName;
	private String picture_url;

	private String live_id;//可直播协会获取直播id

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

	public String getOrg_builder_name() {
		return org_builder_name;
	}

	public void setOrg_builder_name(String org_builder_name) {
		this.org_builder_name = org_builder_name;
	}

	//新增 ysf
	private ErrorMsg error_msg; //错误信息对象
	public ErrorMsg getError_msg() {
		return error_msg;
	}

	public void setError_msg(ErrorMsg error_msg) {
		this.error_msg = error_msg;
	}

	public String getMember_id() {
		return member_id;
	}

	public void setMember_id(String member_id) {
		this.member_id = member_id;
	}

	public String getApply_status() {
		return apply_status;
	}

	public void setApply_status(String apply_status) {
		this.apply_status = apply_status;
	}

	/**
	 * 
	 * contact_id_img_url 联系人身份证照片
	 *
	 * org_certificate_img_url 组织证书照片
	 */
	private String contact_id_img_url;
	private String org_certificate_img_url;

	public String getContact_id_img_url() {
		return contact_id_img_url;
	}

	public void setContact_id_img_url(String contact_id_img_url) {
		this.contact_id_img_url = contact_id_img_url;
	}

	public String getOrg_certificate_img_url() {
		return org_certificate_img_url;
	}

	public void setOrg_certificate_img_url(String org_certificate_img_url) {
		this.org_certificate_img_url = org_certificate_img_url;
	}

	public String getOrg_wenr() {
		return org_wenr;
	}

	public void setOrg_wenr(String org_wenr) {
		this.org_wenr = org_wenr;
	}

	public String getOrg_cer() {
		return org_cer;
	}

	public void setOrg_cer(String org_cer) {
		this.org_cer = org_cer;
	}

	// 新增加
	private boolean showTitle;
	private String requestStatus;

	public boolean isShowTitle() {
		return showTitle;
	}

	public void setShowTitle(boolean showTitle) {
		this.showTitle = showTitle;
	}

	public String getRequestStatus() {
		return requestStatus;
	}

	public void setRequestStatus(String requestStatus) {
		this.requestStatus = requestStatus;
	}

	private int number;
	private int isadmin;

	private int isMember;

	public int getIsMember() {
		return isMember;
	}

	public void setIsMember(int isMember) {
		this.isMember = isMember;
	}

	public String getOrg_name() {
		return org_name;
	}

	public void setOrg_name(String org_name) {
		this.org_name = org_name;
	}

	public String getOrg_id() {
		return org_id;
	}

	public void setOrg_id(String org_id) {
		this.org_id = org_id;
	}

	public String getOrg_industryName() {
		return org_industryName;
	}

	public void setOrg_industryName(String org_industryName) {
		this.org_industryName = org_industryName;
	}

	public String getOrg_gegion_code() {
		return org_gegion_code;
	}

	public void setOrg_gegion_code(String org_gegion_code) {
		this.org_gegion_code = org_gegion_code;
	}

	public String getOrg_tag() {
		return org_tag;
	}

	public void setOrg_tag(String org_tag) {
		this.org_tag = org_tag;
	}

	public String getOrg_industryId() {
		return org_industryId;
	}

	public void setOrg_industryId(String org_industryId) {
		this.org_industryId = org_industryId;
	}

	public String getOrg_addr() {
		return org_addr;
	}

	public void setOrg_addr(String org_addr) {
		this.org_addr = org_addr;
	}

	public String getOrg_des() {
		return org_des;
	}

	public void setOrg_des(String org_des) {
		this.org_des = org_des;
	}

	public String getOrg_pic() {
		return org_pic;
	}

	public void setOrg_pic(String org_pic) {
		this.org_pic = org_pic;
	}

	public String getOrg_tel() {
		return org_tel;
	}

	public void setOrg_tel(String org_tel) {
		this.org_tel = org_tel;
	}

	public String getOrg_email() {
		return org_email;
	}


	public void setOrg_city(String org_city) {
		this.org_city = org_city;
	}
	public void setOrg_email(String org_email) {
		this.org_email = org_email;
	}
	public String getOrg_city() {
		return org_city;
	}


	public String getOrg_provice() {
		return org_provice;
	}

	public void setOrg_provice(String org_provice) {
		this.org_provice = org_provice;
	}

	public int getOrg_status() {
		return org_status;
	}

	public void setOrg_status(int org_status) {
		this.org_status = org_status;
	}

	public String getOrg_contact_tel() {
		return org_contact_tel;
	}

	public void setOrg_contact_tel(String org_contact_tel) {
		this.org_contact_tel = org_contact_tel;
	}

	public String getOrg_contact_name() {
		return org_contact_name;
	}

	public void setOrg_contact_name(String org_contact_name) {
		this.org_contact_name = org_contact_name;
	}

	public String getOrg_logo_url() {
		return org_logo_url;
	}

	public void setOrg_logo_url(String org_logo_url) {
		this.org_logo_url = org_logo_url;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getIsadmin() {
		return isadmin;
	}

	public void setIsadmin(int isadmin) {
		this.isadmin = isadmin;
	}

	private int unread;

	public int getUnread() {
		return unread;
	}

	public void setUnread(int unread) {
		this.unread = unread;
	}

	public String getLive_id() {
		return live_id;
	}

	public void setLive_id(String live_id) {
		this.live_id = live_id;
	}
}
