package app.logic.pojo;

import java.util.Comparator;

/*
 * GZYY    2016-12-13  下午5:01:32
 * author: zsz
 */

public class ComparatorFriends implements Comparator<FriendsInfoExt> {

	@Override
	public int compare(FriendsInfoExt lhs, FriendsInfoExt rhs) {
		// TODO Auto-generated method stub
		if (lhs.getSortLetters().equals("@") || rhs.getSortLetters().equals("#")) {
			return -1;
		} else if (lhs.getSortLetters().equals("#") || rhs.getSortLetters().equals("@")) {
			return 1;
		} else {
			return lhs.getSortLetters().compareTo(rhs.getSortLetters());
		}

	}

}
