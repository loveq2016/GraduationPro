package app.logic.pojo;

import app.utils.db.sqlite.Column;

/**
*
* SiuJiYung create at 2016年8月18日 下午2:58:30
*
*/

public class CalendarInfo {

	@Column(id=true)
	private int id;
	private long startTime;

	private String startDateTime;
	private String endDateTime;
	private String createTime;
	private boolean nofifyEnable;
	
	private String creatorName;
	private String member_info_id;
	
	private String title;
	private String content;
	public String getStartDateTime() {
		return startDateTime;
	}
	public void setStartDateTime(String startDateTime) {
		this.startDateTime = startDateTime;
	}
	public String getEndDateTime() {
		return endDateTime;
	}
	public void setEndDateTime(String endDateTime) {
		this.endDateTime = endDateTime;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public boolean isNofifyEnable() {
		return nofifyEnable;
	}
	public void setNofifyEnable(boolean nofifyEnable) {
		this.nofifyEnable = nofifyEnable;
	}
	public String getCreatorName() {
		return creatorName;
	}
	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}
	public String getMember_info_id() {
		return member_info_id;
	}
	public void setMember_info_id(String member_info_id) {
		this.member_info_id = member_info_id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	
}
