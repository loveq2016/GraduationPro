package app.logic.pojo;

/*
 * GZYY    2016-8-16  下午3:59:07
 */

public class JoinRequestListInfo {

	private String picture_url;

	private String location;
	private String nickName;
	private String nameString;
	private int org_status;
	private String message_id;
	private String realName;
	private String member_id;
	private String request_status;
	private String request_id;
	private String respone_time;
	public String getRespone_time() {
		return respone_time;
	}

	public void setRespone_time(String respone_time) {
		this.respone_time = respone_time;
	}

	public String getRequest_time() {
		return request_time;
	}

	public void setRequest_time(String request_time) {
		this.request_time = request_time;
	}

	private String request_time;
	
	
	

	public String getRequest_id() {
		return request_id;
	}

	public void setRequest_id(String request_id) {
		this.request_id = request_id;
	}

	public String getRequest_status() {
		return request_status;
	}

	public void setRequest_status(String request_status) {
		this.request_status = request_status;
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

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getNameString() {
		return nameString;
	}

	public void setNameString(String nameString) {
		this.nameString = nameString;
	}

	public int getOrg_status() {
		return org_status;
	}

	public void setOrg_status(int org_status) {
		this.org_status = org_status;
	}

	public String getMessage_id() {
		return message_id;
	}

	public void setMessage_id(String message_id) {
		this.message_id = message_id;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getMember_id() {
		return member_id;
	}

	public void setMember_id(String member_id) {
		this.member_id = member_id;
	}

}
