package app.logic.pojo;

import android.R.integer;
/*
 * GZYY    2016-9-29  上午11:00:53
 */

public class CountUnreadInfo {
	private String org_id;
	public String getOrg_id() {
		return org_id;
	}
	public void setOrg_id(String org_id) {
		this.org_id = org_id;
	}
	public int getUnread() {
		return unread;
	}
	public void setUnread(int unread) {
		this.unread = unread;
	}
	private int unread;
	
}
