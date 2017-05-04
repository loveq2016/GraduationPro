package app.logic.activity.calendar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.ql.activity.customtitle.ActActivity;
import org.ql.app.alert.AlertDialog;
import org.ql.utils.QLDateUtils;
import org.ql.utils.QLToastUtils;

import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.TimePickerView.OnTimeSelectListener;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.MobclickAgent.EScenarioType;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.view.ViewPager.LayoutParams;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import app.logic.activity.ActTitleHandler;
import app.logic.controller.UserManagerController;
import app.logic.pojo.CalendarInfo;
import app.logic.pojo.UserInfo;
import app.utils.common.Listener;
import app.utils.db.sqlite.DbUtils;
import app.yy.geju.R;

/**
 * 
 * SiuJiYung create at 2016年8月19日 上午10:22:33
 * 
 */

public class AddCalendarActivity extends ActActivity implements OnClickListener {

	public static final String kDateKey = "kDateKey";
	public static final String kModifyModel = "kModifyModel";
	public static final String kCalendarInfoJson = "kCalendarInfoJson";

	private ActTitleHandler titleHander = new ActTitleHandler();
	private EditText contentView;     //日程内容
	private TextView startView;       //开始时间
	private TextView endView;         //结束时间
	private DbUtils dbUtils;          //数据可

	private Date startDateTime;       //开始日期
	private Date endDateTime;         //结束日期
	private boolean editing;          //表示从哪里跳转的
	private CalendarInfo reviewInfo;   //日程信息对象
	private boolean editStatus;        //是否可编辑的装填
	
	private Button but ;//确定_保存按钮蛋妞

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbsHandler(titleHander);
		setContentView(R.layout.activity_add_calendar_event);
		//初始化TootBar
		initTitier();
		//初始化View
		initView();
		//初始数据库对象
		dbUtils = DbUtils.create(this);
		//获取标记位
		editing = getIntent().getBooleanExtra( kModifyModel, false );
		//判断从哪里跳转（列表还是添加按钮）
		setEditing();
	}
	
	/**
	 * 初始化TootBar
	 * @param
	 */
	
	private void initTitier(){
		setTitle("");
		titleHander.replaseLeftLayout( this, true );
		((TextView) titleHander.getLeftLayout().findViewById(R.id.left_tv)).setText("添加日程");
		titleHander.getLeftLayout().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	/**
	 * 初始View
	 * @param
	 */
	private void initView(){		
		contentView = (EditText) findViewById(R.id.add_calendar_event_content_view);//日程的内容		
		startView = (TextView) findViewById(R.id.add_calendar_event_start_view);//开始时间		
		endView = (TextView) findViewById(R.id.add_calendar_event_end_view);//结束时间		
		but = (Button) findViewById( R.id.certain_save_but );//确定按钮
		but.setOnClickListener( this ) ;
		startView.setOnClickListener(this);
		endView.setOnClickListener(this);
	}
	
	/**
	 * 判断点击点击什么跳转的
	 */
	private void setEditing(){
		
       if ( editing ) { //点击的是日程列表		
			
			String calendar_json = getIntent().getStringExtra( kCalendarInfoJson );
			
			if (calendar_json != null) {
				Gson gson = new Gson();
				reviewInfo = gson.fromJson(calendar_json, CalendarInfo.class);
			}
			
		} else { //点击的添加日程按钮

			String dt_str = getIntent().getStringExtra(kDateKey);
			
			if ( dt_str != null ) {
				Date dtnowDate = QLDateUtils.getDateTimeNow();
				Calendar calendarNow = Calendar.getInstance();
				calendarNow.setTime( dtnowDate );
				startDateTime = QLDateUtils.createDateTimeFromString( dt_str, "yyyy-MM-dd HH:mm:ss" );
				
				endDateTime = new Date(startDateTime.getTime());
				Calendar calendarStart = Calendar.getInstance();
				calendarStart.setTime(startDateTime);
				calendarStart.set(Calendar.HOUR_OF_DAY, calendarNow.get(Calendar.HOUR_OF_DAY));
				calendarStart.set(Calendar.MINUTE, calendarNow.get(Calendar.MINUTE));
				startDateTime = calendarStart.getTime();
				
				Calendar calendarEnd = Calendar.getInstance();
				calendarEnd.setTime(endDateTime);
				calendarEnd.set(Calendar.HOUR_OF_DAY, calendarNow.get(Calendar.HOUR_OF_DAY));
				calendarEnd.set(Calendar.MINUTE, calendarNow.get(Calendar.MINUTE));
				endDateTime = calendarEnd.getTime();

			}
		}			
		//时：分
		String time_str = QLDateUtils.getTimeWithFormat(startDateTime, "HH:mm");
		StringBuilder builder = new StringBuilder();

		builder.append(time_str);
		startView.setText(builder.toString());
		endView.setText(builder.toString());
		
		if (editing && reviewInfo != null) {
			reviewCalendar( reviewInfo ); //点击列表跳转过来时，且信息不为null是就设置
		}
	}
	
	
	/**
	 * 点击列表跳转到这里是，填充日程的内容
	 * @param info
	 */
	private void reviewCalendar(CalendarInfo info) {
		
		contentView.setText(info.getContent());

		Date _startTimeDate = QLDateUtils.createDateTimeFromString(info.getStartDateTime(), "yyyy-MM-dd HH:mm:ss");
		Date _endTimeDate = QLDateUtils.createDateTimeFromString(info.getEndDateTime(), "yyyy-MM-dd HH:mm:ss");

		this.startDateTime = _startTimeDate;
		this.endDateTime = _endTimeDate;

		String start_time_str = QLDateUtils.getTimeWithFormat(_startTimeDate, "HH:mm");
		String end_time_str = QLDateUtils.getTimeWithFormat(_endTimeDate, "HH:mm");
		//开始是时间
		StringBuilder start_builder = new StringBuilder();
		start_builder.append(start_time_str);
		startView.setText(start_builder.toString());
		
		//结束时间
		StringBuilder end_builder = new StringBuilder();
		end_builder.append(end_time_str);
		endView.setText(end_builder.toString());
		
	}
	
	/**
	 * 添加日程
	 * @param content
	 */
	private void addCalendar(  ) {
		if (startDateTime.getTime() > endDateTime.getTime()) {
			QLToastUtils.showToast(AddCalendarActivity.this, "开始时间不能晚于结束时间");
			return;
		}
		
		String txt = contentView.getText().toString();
		if (txt == null || TextUtils.isEmpty(txt)) {
			QLToastUtils.showToast(AddCalendarActivity.this, "日程内容不能为空");
			return;
		}

		Date dtNow = QLDateUtils.getDateTimeNow();

		UserInfo userInfo = UserManagerController.getCurrUserInfo();

		CalendarInfo info = new CalendarInfo();
		info.setContent(txt);
		info.setCreateTime(QLDateUtils.getTimeWithFormat(dtNow, "yyyy-MM-dd HH:mm:ss"));
		info.setCreatorName(userInfo.getNickName());
		info.setMember_info_id(userInfo.getWp_member_info_id());
		info.setNofifyEnable(false);
		info.setTitle("");
		info.setStartDateTime(QLDateUtils.getTimeWithFormat(startDateTime, "yyyy-MM-dd HH:mm:ss"));
		info.setEndDateTime(QLDateUtils.getTimeWithFormat(endDateTime, "yyyy-MM-dd HH:mm:ss"));
		info.setStartTime(startDateTime.getTime());
		dbUtils.insert(info);
		finish();
	}

	/**
	 * 移除日程
	 */
	private void removeCalendar() {
		if (reviewInfo == null) {
			return;
		}
		dbUtils.delete(CalendarInfo.class, "id = ?", reviewInfo.getId());
		finish();
	}

	/**
	 * 保存日程
	 */
	private void saveCalendar() {
		if ( reviewInfo == null ) {
			QLToastUtils.showToast(AddCalendarActivity.this, "原有日程没有信息");
			return;
		}
		
		String txt = contentView.getText().toString();
		if (txt == null || TextUtils.isEmpty(txt)) {
			QLToastUtils.showToast(AddCalendarActivity.this, "日程内容不能为空");
			return;
		}
		
		if( contentView.isEnabled()){}
		if ( startDateTime.getTime() > endDateTime.getTime()) {
			QLToastUtils.showToast(AddCalendarActivity.this, "开始时间不能晚于结束时间");
			return;
		}

		reviewInfo.setContent(contentView.getText().toString());
		reviewInfo.setStartDateTime(QLDateUtils.getTimeWithFormat(startDateTime, "yyyy-MM-dd HH:mm:ss"));
		reviewInfo.setEndDateTime(QLDateUtils.getTimeWithFormat(endDateTime, "yyyy-MM-dd HH:mm:ss"));
		reviewInfo.setStartTime(startDateTime.getTime());
		dbUtils.update(reviewInfo);//数据更新
		finish();//关闭地掉本界面
	}

	/**
	 * 显示时 和分 对话框
	 * @param dt
	 * @param startTime
	 */
	private void showTimePicker(final Date dt, final boolean startTime) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dt);
		TimePickerView timePickerView = new TimePickerView(this, TimePickerView.Type.HOURS_MINS);
		timePickerView.setCancelable(true);
		timePickerView.setCyclic(true);
		timePickerView.setTime(dt);
		timePickerView.setOnTimeSelectListener(new OnTimeSelectListener() {
			@Override
			public void onTimeSelect(Date date) {
				// String week_str = getWeekDateString(date);
//				String week_str = QLDateUtils.getWeekDateString(startDateTime);
				String time_str = QLDateUtils.getTimeWithFormat(date, "HH:mm");
				StringBuilder builder = new StringBuilder();
//				builder.append(week_str);
//				builder.append(" ");
				builder.append(time_str);
				Calendar calendar = Calendar.getInstance();
				Calendar selectedDateCalendar = Calendar.getInstance();
				selectedDateCalendar.setTime(date);
				if (startTime) {
					calendar.setTime(startDateTime);
					calendar.set(Calendar.HOUR_OF_DAY, selectedDateCalendar.get(Calendar.HOUR_OF_DAY));
					// calendar.set(Calendar.SECOND,
					// selectedDateCalendar.get(Calendar.SECOND));
					calendar.set(Calendar.MINUTE, selectedDateCalendar.get(Calendar.MINUTE));
					startDateTime = calendar.getTime();
					// Toast.makeText(AddCalendarActivity.this,
					// QLDateUtils.getTimeWithFormat(startDateTime,
					// "yyyy-MM-dd HH:mm:ss"), 0).show();
					startView.setText(builder.toString());
				} else {
					calendar.setTime(endDateTime);
					calendar.set(Calendar.HOUR_OF_DAY, selectedDateCalendar.get(Calendar.HOUR_OF_DAY));
					// calendar.set(Calendar.SECOND,
					// selectedDateCalendar.get(Calendar.SECOND));
					calendar.set(Calendar.MINUTE, selectedDateCalendar.get(Calendar.MINUTE));
					endDateTime = calendar.getTime();
					endView.setText(builder.toString());
				}

				long startTimeL = startDateTime.getTime();
				long endTimeL = endDateTime.getTime();
				if (startTimeL > endTimeL) {
					calendar.setTime(endDateTime);
					calendar.set(Calendar.HOUR_OF_DAY, selectedDateCalendar.get(Calendar.HOUR_OF_DAY));
					calendar.set(Calendar.SECOND, selectedDateCalendar.get(Calendar.SECOND));
					endDateTime = calendar.getTime();
					endView.setText(builder.toString());
				}

			}
		});
		hideKeyboard(); //调用系统的输入模式
		timePickerView.show();
	}

	@Override
	public void onClick(View v) {
		int vid = v.getId();
		
		switch (vid) {
			case R.id.certain_save_but://添加日程按钮
				
				if( !editing ){ //添加按钮
					
					addCalendar();//添加日程
				}else{         //点击列表
					// 保存修改
					saveCalendar();
				}
				
				break;
			case R.id.add_calendar_event_start_view://开始时间
				if (editing) {					
					showTimePicker( startDateTime, true );					
				} else {
					showTimePicker( startDateTime, true );
				}
				break;
			case R.id.add_calendar_event_end_view: //结束时间
				if (editing) {					
					showTimePicker( endDateTime, false );					
				} else {
					showTimePicker( endDateTime, false );
				}
	
				break;
		}
	}

	/**
	 * 修改日程
	 */
	private void editView() {
		
		but.setVisibility(View.GONE);//隐藏确定按钮
		
		contentView = (EditText) findViewById(R.id.add_calendar_event_content_view);
		setTitle("编辑日程");
		titleHander.addRightView(LayoutInflater.from(AddCalendarActivity.this).inflate(R.layout.title_rightlayout, null), true);
		TextView tv = (TextView) titleHander.getRightLayout().findViewById(R.id.right_tv);
		tv.setOnClickListener( this );
		contentView.setFocusableInTouchMode( true );
	}

	/**
	 * 显示是否删除对日程对话框
	 */
	private void showDelectCalendar() {
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		View view = LayoutInflater.from(this).inflate(R.layout.dialog_delete_textview, null);
		TextView messageTV = (TextView) view.findViewById(R.id.dialog_tv);
		messageTV.setText("确定删除该日程吗");
		alertDialog.setTitle("删除");
		// alertDialog.setMessage("确定删除该日程吗");
		alertDialog.setView(view);
		alertDialog.setIcon(0);
		//确定删除
		alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "是", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				removeCalendar();//删除日程
			}
		});
		//取消删除
		alertDialog.setButton( DialogInterface.BUTTON_NEGATIVE, "否", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

			}
		});
		alertDialog.show();//显示对话框
	}

	/**
	 *  解决日期筛选星期错乱问题
	 * @param date
	 * @return
	 */
	private String getWeekDateString(Date date) {

		String[] weekdata_list = { "日", "一", "二", "三", "四", "五", "六" };

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int week = cal.get(Calendar.DAY_OF_WEEK);
		week = week < 0 ? 0 : week;
		String weekdate = weekdata_list[week];
		return ("星期" + weekdate);
	}

	/**
	 *  调用隐藏系统默认的输入法
	 */
	private void hideKeyboard() {
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

}
