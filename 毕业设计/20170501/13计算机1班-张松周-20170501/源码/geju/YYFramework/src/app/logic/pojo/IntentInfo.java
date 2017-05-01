package app.logic.pojo;

import java.io.Serializable;

import android.R.integer;

/*
 * GZYY    2016-12-23  上午9:33:14
 * author: zsz
 */

public class IntentInfo implements Serializable {

	public static final String INTENT_INFO = "INTENT_INFO";

	public static final int ADD_MODE = 10;
	public static final int EDIT_MODE = 11;

	private String title; // 标题
	private int openMode; // 打开方式 ：10为新增，11为编辑
	private String orgId; // 组织ID
	private String orgName;
	private String dpmId; // 部门id
	private boolean isAdmin; // 是否为管理员
	private String listString; // 数据列表
	private boolean isBuilder ;

	public boolean isBuilder() {
		return isBuilder;
	}

	public void setBuilder(boolean builder) {
		isBuilder = builder;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getOpenMode() {
		return openMode;
	}

	/**
	 * 
	 * @param openMode
	 *            0为新增，1为编辑 setOpenModeIntentInfo
	 */
	public void setOpenMode(int openMode) {
		this.openMode = openMode;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getDpmId() {
		return dpmId;
	}

	public void setDpmId(String dpmId) {
		this.dpmId = dpmId;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public String getListString() {
		return listString;
	}

	public void setListString(String listString) {
		this.listString = listString;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

}
