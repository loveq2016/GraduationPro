package app.logic.pojo;

import java.util.Date;

/**
 * 版块信息
 */
public class BoardInfo {

	/**
	 * 主键
	 */
	private int id;

	/**
	 * 板块名称
	 */
	private String boardName;

	/**
	 * LOGO地址
	 */
	private String logoPath;

	/**
	 * 板块类型(1.资讯板块、2.联赛板块、3.网页板块)
	 */
	private int boardType;

	/**
	 * JSON参数
	 */
	private String json;

	/**
	 * 排序号
	 */
	private int sortNo;

	/**
	 * 显示方式(1：固定显示，在客户端板块界面中固定显示，不能修改显示位置，一般是位置最前的两个板块、0：不固定显示，由用户决定是否显示及显示位置)
	 */
	private int showMode;

	/**
	 * 是否推荐(0：不推荐，将不出现在默认的板块选择中、1：推荐，将出现在默认的板块界面中)
	 */
	private int recommendFlag;

	/**
	 * 创建时间
	 */
	private Date createTime;
	
	private int showNumber;

	/** default constructor */
	public BoardInfo() {
		this.createTime = new Date();
	}
	
	public void setShowNumber(int number){
		this.showNumber = number;
	}
	
	public int getShowNumber(){
		return this.showNumber;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getBoardName() {
		return boardName;
	}

	public void setBoardName(String boardName) {
		this.boardName = boardName;
	}

	public String getLogoPath() {
		return logoPath;
	}

	public void setLogoPath(String logoPath) {
		this.logoPath = logoPath;
	}

	public int getBoardType() {
		return boardType;
	}

	public void setBoardType(int boardType) {
		this.boardType = boardType;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	public int getSortNo() {
		return sortNo;
	}

	public void setSortNo(int sortNo) {
		this.sortNo = sortNo;
	}

	public int getShowMode() {
		return showMode;
	}

	public void setShowMode(int showMode) {
		this.showMode = showMode;
	}

	public int getRecommendFlag() {
		return recommendFlag;
	}

	public void setRecommendFlag(int recommendFlag) {
		this.recommendFlag = recommendFlag;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	private int drawableId;

	public int getDrawableId() {
		return drawableId;
	}
	
	public void setDrawableId(int drawableId){
		this.drawableId = drawableId;
	}
}