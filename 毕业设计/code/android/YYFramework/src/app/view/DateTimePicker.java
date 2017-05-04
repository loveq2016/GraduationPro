package app.view;

import android.app.Activity;
import android.os.Bundle;

/**
 *
 * SiuJiYung create at 2016-8-7 下午7:06:12
 *
 */

public class DateTimePicker extends Activity {

	public static final int kPickerType_Date = 1;
	public static final int kPickerType_Time = 2;
	public static final int kPickerType_DateTime = 3;
	
	public static final String kPickerType= "kPickerType_Date";
	
	private int pickerType;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pickerType = getIntent().getIntExtra(kPickerType, 1);
		
	}
	
	
}
