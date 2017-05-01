package app.logic.pojo;

import java.io.Serializable;

/*
 * GZYY    2016-10-28  上午11:33:33
 */

public class ChatMessageInfo implements Serializable {
	private int chatType;
	private String userId;
	private String messageTo;
	private String fromActivity;

	public ChatMessageInfo() {
	}

	public ChatMessageInfo(String fromActivity, int chatType, String userId, String messageTo) {
		this.fromActivity = fromActivity;
		this.chatType = chatType;
		this.userId = userId;
		this.messageTo = messageTo;
	}

	public int getChatType() {
		return chatType;
	}

	public void setChatType(int chatType) {
		this.chatType = chatType;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getMessageTo() {
		return messageTo;
	}

	public void setMessageTo(String messageTo) {
		this.messageTo = messageTo;
	}

	public String getFromActivity() {
		return fromActivity;
	}

	public void setFromActivity(String fromActivity) {
		this.fromActivity = fromActivity;
	}

}
