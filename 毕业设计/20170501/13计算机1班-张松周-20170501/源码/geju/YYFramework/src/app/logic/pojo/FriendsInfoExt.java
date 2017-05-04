package app.logic.pojo;

/*
 * GZYY    2016-12-13  下午2:29:33
 * author: zsz
 */

public class FriendsInfoExt {

	private FriendInfo friendInfo;

	private boolean isCheck;

	private String sortLetters;

	private String name;

	private UserInfo userInfo;

	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public FriendInfo getFriendInfo() {
		return friendInfo;
	}

	public void setFriendInfo(FriendInfo friendInfo) {
		this.friendInfo = friendInfo;
	}

	public boolean isCheck() {
		return isCheck;
	}

	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}

	public String getSortLetters() {
		return sortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}

}
