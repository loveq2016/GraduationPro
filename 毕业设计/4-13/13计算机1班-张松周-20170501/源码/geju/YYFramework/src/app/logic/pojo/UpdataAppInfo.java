package app.logic.pojo;

import java.io.Serializable;

/*
 * GZYY    2016-9-5  下午3:26:59
 * 
 * 
 */

public class UpdataAppInfo implements Serializable{

	private String app_update_msg;

	private int app_udpate_type;
	private String app_realse_datetime;
	private String app_update_url;
	private int app_type;
	private int app_version;
	private String app_name;

	public String getApp_update_msg() {
		return app_update_msg;
	}

	public void setApp_update_msg(String app_update_msg) {
		this.app_update_msg = app_update_msg;
	}

	public int getApp_udpate_type() {
		return app_udpate_type;
	}

	public void setApp_udpate_type(int app_udpate_type) {
		this.app_udpate_type = app_udpate_type;
	}

	public String getApp_realse_datetime() {
		return app_realse_datetime;
	}

	public void setApp_realse_datetime(String app_realse_datetime) {
		this.app_realse_datetime = app_realse_datetime;
	}

	public String getApp_update_url() {
		return app_update_url;
	}

	public void setApp_update_url(String app_update_url) {
		this.app_update_url = app_update_url;
	}

	public int getApp_type() {
		return app_type;
	}

	public void setApp_type(int app_type) {
		this.app_type = app_type;
	}

	public int getApp_version() {
		return app_version;
	}

	public void setApp_version(int app_version) {
		this.app_version = app_version;
	}

	public String getApp_name() {
		return app_name;
	}

	public void setApp_name(String app_name) {
		this.app_name = app_name;
	}
	

}
