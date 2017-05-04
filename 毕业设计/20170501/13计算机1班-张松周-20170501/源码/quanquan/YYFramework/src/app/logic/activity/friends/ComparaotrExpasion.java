package app.logic.activity.friends;

import java.util.Comparator;
/*
 * GZYY    2016-12-21  下午4:46:39
 * author: zsz
 */

import app.logic.pojo.ExpansionInfo;

public class ComparaotrExpasion implements Comparator<ExpansionInfo> {

	@Override
	public int compare(ExpansionInfo lhs, ExpansionInfo rhs) {
		// TODO Auto-generated method stub
		if (lhs.getItemSortLetters().equals("@") || rhs.getItemSortLetters().equals("#")) {
			return -1;
		} else if (lhs.getItemSortLetters().equals("#") || rhs.getItemSortLetters().equals("@")) {
			return 1;
		} else {
			return lhs.getItemSortLetters().compareTo(rhs.getItemSortLetters());
		}

	}

}
