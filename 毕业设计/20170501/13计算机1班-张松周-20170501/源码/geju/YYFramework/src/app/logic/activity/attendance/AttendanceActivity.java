package app.logic.activity.attendance;

import org.ql.activity.customtitle.ActActivity;
/*
 * GZYY    2016-8-3  下午2:13:47
 */

import android.os.Bundle;
import app.logic.activity.ActTitleHandler;
import app.yy.geju.R;

public class AttendanceActivity extends ActActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActTitleHandler handler = new ActTitleHandler();
		setAbsHandler(handler);
		setContentView(R.layout.activity_attendance);
		
		setTitle("签到");
	}

}
