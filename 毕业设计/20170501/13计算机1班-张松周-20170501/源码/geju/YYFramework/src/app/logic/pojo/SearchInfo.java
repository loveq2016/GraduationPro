package app.logic.pojo;

import java.util.List;

/*
 * GZYY    2016-12-7  下午2:05:58
 * author: zsz
 */

public class SearchInfo {

	private boolean success;

	private String wp_error_msg;
	private int code;
	private List<OrganizationInfo> association;

	private List<NoticeInfo> message;
	private List<YYChatSessionInfo> member;

	public List<NoticeInfo> getMessage() {
		return message;
	}

	public void setMessage(List<NoticeInfo> message) {
		this.message = message;
	}

	public List<YYChatSessionInfo> getMember() {
		return member;
	}

	public void setMember(List<YYChatSessionInfo> member) {
		this.member = member;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getWp_error_msg() {
		return wp_error_msg;
	}

	public void setWp_error_msg(String wp_error_msg) {
		this.wp_error_msg = wp_error_msg;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public List<OrganizationInfo> getAssociation() {
		return association;
	}

	public void setAssociation(List<OrganizationInfo> association) {
		this.association = association;
	}

}
