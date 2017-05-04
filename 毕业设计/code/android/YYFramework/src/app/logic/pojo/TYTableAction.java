package app.logic.pojo;

import java.util.ArrayList;

public class TYTableAction {
	private int ActionType;
	
	private String Interface;
	
	private ArrayList<String> RequestParams;
	

	public int getActionType() {
		return ActionType;
	}

	public void setActionType(int actionType) {
		ActionType = actionType;
	}

	public String getInterface() {
		return Interface;
	}

	public void setInterface(String interface1) {
		Interface = interface1;
	}

	public ArrayList<String> getRequestParams() {
		return RequestParams;
	}

	public void setRequestParams(ArrayList<String> requestParams) {
		RequestParams = requestParams;
	}
	
}
