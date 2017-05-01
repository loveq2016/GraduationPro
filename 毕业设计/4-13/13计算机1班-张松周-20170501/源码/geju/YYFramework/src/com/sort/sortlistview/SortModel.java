package com.sort.sortlistview;

import app.logic.pojo.FriendInfo;

public class SortModel {

	private String name;
	private String sortLetters;
	private FriendInfo friendInfo;
	private String nickName;
	
	
	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public FriendInfo getFriendInfo() {
		return friendInfo;
	}

	public void setFriendInfo(FriendInfo friendInfo) {
		this.friendInfo = friendInfo;
	}

	public SortModel(){
	}
	
	public SortModel(FriendInfo info){
		friendInfo = info;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSortLetters() {
		return sortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}
}
