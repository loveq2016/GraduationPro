package app.logic.pojo;

import android.graphics.drawable.Drawable;

/*
 * GZYY    2016-12-21  下午2:01:21
 * author: zsz
 */

public class ExpansionInfo {

	private String itemSortLetters;
	private String itemUrl;
	private String itemName;

	private boolean itemShowCheck;
	private boolean itemIsCheck;

	private String itemID;
	private String itemPhone;

	private int itemLastIv;

	//新增 (ysf)
	private String wp_member_info_id ;
	private String friend_name;
	private String nickName;

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getFriend_name() {
		return friend_name;
	}

	public void setFriend_name(String friend_name) {
		this.friend_name = friend_name;
	}

	public String getWp_member_info_id() {
		return wp_member_info_id;
	}

	public void setWp_member_info_id(String wp_member_info_id) {
		this.wp_member_info_id = wp_member_info_id;
	}



	public int getItemLastIv() {
		return itemLastIv;
	}

	public void setItemLastIv(int itemLastIv) {
		this.itemLastIv = itemLastIv;
	}

	public String getItemPhone() {
		return itemPhone;
	}

	public void setItemPhone(String itemPhone) {
		this.itemPhone = itemPhone;
	}

	public String getItemID() {
		return itemID;
	}

	public void setItemID(String itemID) {
		this.itemID = itemID;
	}

	public String getItemSortLetters() {
		return itemSortLetters;
	}

	public void setItemSortLetters(String itemSortLetters) {
		this.itemSortLetters = itemSortLetters;
	}

	public String getItemUrl() {
		return itemUrl;
	}

	public void setItemUrl(String itemUrl) {
		this.itemUrl = itemUrl;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public boolean isItemShowCheck() {
		return itemShowCheck;
	}

	public void setItemShowCheck(boolean itemShowCheck) {
		this.itemShowCheck = itemShowCheck;
	}

	public boolean isItemIsCheck() {
		return itemIsCheck;
	}

	public void setItemIsCheck(boolean itemIsCheck) {
		this.itemIsCheck = itemIsCheck;
	}

	/**
	 * ------------------------------实体类------------------------
	 */

	private FriendInfo friendInfo;
	private UserInfo userInfo;

	public FriendInfo getFriendInfo() {
		return friendInfo;
	}

	public void setFriendInfo(FriendInfo friendInfo) {
		this.friendInfo = friendInfo;
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

}
