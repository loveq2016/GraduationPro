package app.logic.pojo;

import java.util.List;

/**
*
* SiuJiYung create at 2016年7月1日 下午3:26:06
*
*/

public class YYChatRoomInfo {
	
	/**
	 * 创建者名称
	 */
	private String cr_creatorName;

	public String getCr_creatorName() {
		return cr_creatorName;
	}

	public void setCr_creatorName(String cr_creatorName) {
		this.cr_creatorName = cr_creatorName;
	}
	private String cr_creatoName;
	/**
	 * 聊天室ID
	 */
	private String cr_id;
	
	private String room_id;
	/**
	 * 聊天室成员
	 */
	private List<UserInfo> cr_memberList;
	/**
	 * 聊天室类型
	 */
	private String cr_type;
	/**
	 * 创建者ID
	 */
	private String cr_creatorId;
	/**
	 * 简介
	 */
	private String cr_des;
	/**
	 * 聊天室名称
	 */
	private String cr_name;
	/**
	 * 公告牌信息
	 */
	private String cr_notice;
	/**
	 * 群头像
	 */
	private String  cr_picture;

	public String getCr_picture() {
		return cr_picture;
	}

	public void setCr_picture(String cr_picture) {
		this.cr_picture = cr_picture;
	}

	public String getCr_creatoName() {
		return cr_creatoName;
	}
	public void setCr_creatoName(String cr_creatoName) {
		this.cr_creatoName = cr_creatoName;
	}
	public String getCr_id() {
		return cr_id;
	}
	public void setCr_id(String cr_id) {
		this.cr_id = cr_id;
	}
	public List<UserInfo> getCr_memberList() {
		return cr_memberList;
	}
	public void setCr_memberList(List<UserInfo> cr_memberList) {
		this.cr_memberList = cr_memberList;
	}
	public String getCr_type() {
		return cr_type;
	}
	public void setCr_type(String cr_type) {
		this.cr_type = cr_type;
	}
	public String getCr_creatorId() {
		return cr_creatorId;
	}
	public void setCr_creatorId(String cr_creatorId) {
		this.cr_creatorId = cr_creatorId;
	}
	public String getCr_des() {
		return cr_des;
	}
	public void setCr_des(String cr_des) {
		this.cr_des = cr_des;
	}
	public String getCr_name() {
		return cr_name;
	}
	public void setCr_name(String cr_name) {
		this.cr_name = cr_name;
	}
	public String getCr_notice() {
		return cr_notice;
	}
	public void setCr_notice(String cr_notice) {
		this.cr_notice = cr_notice;
	}
	public String getRoom_id() {
		return room_id;
	}
	public void setRoom_id(String room_id) {
		this.room_id = room_id;
	}
}
