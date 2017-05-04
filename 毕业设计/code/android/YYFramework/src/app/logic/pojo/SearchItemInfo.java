package app.logic.pojo;

import java.util.List;

/*
 * GZYY    2016-12-9  上午11:00:03
 * author: zsz
 */

public class SearchItemInfo {

	private OrganizationInfo orgDatas;
	private NoticeInfo noticeDatas;
	private YYChatSessionInfo chatDatas;

	private boolean titleStatus;

	public boolean isTitleStatus() {
		return titleStatus;
	}

	public void setTitleStatus(boolean titleStatus) {
		this.titleStatus = titleStatus;
	}

	public OrganizationInfo getOrgDatas() {
		return orgDatas;
	}

	public void setOrgDatas(OrganizationInfo orgDatas) {
		this.orgDatas = orgDatas;
	}

	public NoticeInfo getNoticeDatas() {
		return noticeDatas;
	}

	public void setNoticeDatas(NoticeInfo noticeDatas) {
		this.noticeDatas = noticeDatas;
	}

	public YYChatSessionInfo getChatDatas() {
		return chatDatas;
	}

	public void setChatDatas(YYChatSessionInfo chatDatas) {
		this.chatDatas = chatDatas;
	}

}
