package app.logic.pojo;

import java.util.HashMap;

public class TYTableNameMapInfo {

	private int tableID;
	
	private HashMap<String, String> mapInfo;

	public int getTableID() {
		return tableID;
	}

	public void setTableID(int tableID) {
		this.tableID = tableID;
	}

	public HashMap<String, String> getMapInfo() {
		return mapInfo;
	}

	public void setMapInfo(HashMap<String, String> mapInfo) {
		this.mapInfo = mapInfo;
	}
}
