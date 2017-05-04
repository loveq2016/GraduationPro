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

public class AddCalendarActivity2 extends ActActivity implements OnClickListener, OnTouchListener {

	public static final String kDateKey = "kDateKey";
	public static final String kModifyModel = "kModifyModel";
	public static final String kCalendarInfoJson = "kCalendarInfoJson";

	private ActTitleHandler titleHander = new ActTitleHandler();
	private EditText contentView;
	private TextView startView;
	private TextView endView;
	private PopupWindow popupWindow;
	private DbUtils dbUtils;

	private Date startDateTime;
	private Date endDateTime;
	private boolean editing;
	private CalendarInfo reviewInfo;
	private boolean editStatus;
	private ImageButton imageButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbsHandler(titleHander);
		setContentView(R.layout.activity_add_calendar_event);

		// MobclickAgent.setScenarioType(this, EScenarioType.E_UM_NORMAL);

		titleHander.replaseLeftLayout(this, true);
		titleHander.getLeftLayout().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		// Toast.makeText(this, 1, 0).show();

		editing = getIntent().getBooleanExtra(kModifyModel, false);
		dbUtils = DbUtils.create(this);

		if (editing) {
			setTitle("日程详情");
			titleHander.addRightView(LayoutInflater.from(this).inflate(R.layout.homeactivity_rightlayout, null), true);
			titleHander.getRightLayout().setVisibility(View.VISIBLE);
			imageButton = (ImageButton) titleHander.getRightLayout().findViewById(R.id.imageButton02);
			titleHander.getRightLayout().findViewById(R.id.aboutMe_ib).setVisibility(View.GONE);
			imageButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.popmenu_more));
			// titleHander.getRightDefButton().setText("...");
			// titleHander.getRightDefButton().setTextColor(0xffffffff);
			// titleHander.getRightDefButton().setVisibility(View.VISIBLE);
			imageButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					showMenu(v);
				}
			});
			String calendar_json = getIntent().getStringExtra(kCalendarInfoJson);
			if (calendar_json != null) {
				Gson gson = new Gson();
				reviewInfo = gson.fromJson(calendar_json, CalendarInfo.class);
			}
		} else {
			setTitle("新建日程");
			titleHander.getRightDefButton().setText("确定");
			titleHander.getRightDefButton().setTextColor(0xffffffff);
			titleHander.getRightDefButton().setVisibility(View.VISIBLE);
			titleHander.getRightDefButton().setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String txt = contentView.getText().toString();
					if (txt == null || TextUtils.isEmpty(txt)) {
						QLToastUtils.showToast(AddCalendarActivity2.this, "日程内容不能为空");
						return;
					}
					addCalendar(txt);
				}
			});

			String dt_str = getIntent().getStringExtra(kDateKey);
			if (dt_str != null) {
				Date dtnowDate = QLDateUtils.getDateTimeNow();
				Calendar calendarNow = Calendar.getInstance();
				calendarNow.setTime(dtnowDate);
				startDateTime = QLDateUtils.createDateTimeFromString(dt_str, "yyyy-MM-dd HH:mm:ss");
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

		contentView = (EditText) findViewById(R.id.add_calendar_event_content_view);
		startView = (TextView) findViewById(R.id.add_calendar_event_start_view);
		endView = (TextView) findViewById(R.id.add_calendar_event_end_view);

		if (editing) {
			contentView.setFocusable(false);
		}

		startView.setOnClickListener(this);
		endView.setOnClickListener(this);

		String week_str = QLDateUtils.getWeekDateString(startDateTime);
		String time_str = QLDateUtils.getTimeWithFormat(startDateTime, "HH:mm");
		StringBuilder builder = new StringBuilder();
		builder.append(week_str);
		builder.append(" ");
		builder.append(time_str);
		startView.setText(builder.toString());
		endView.setText(builder.toString());
		if (editing && reviewInfo != null) {
			reviewCalendar(reviewInfo);
		}

	}

	private void reviewCalendar(CalendarInfo info) {
		contentView.setText(info.getContent());

		Date _startTimeDate = QLDateUtils.createDateTimeFromString(info.getStartDateTime(), "yyyy-MM-dd HH:mm:ss");
		Date _endTimeDate = QLDateUtils.createDateTimeFromString(info.getEndDateTime(), "yyyy-MM-dd HH:mm:ss");

		this.startDateTime = _startTimeDate;
		this.endDateTime = _endTimeDate;

		String week_str = QLDateUtils.getWeekDateString(_startTimeDate);
		String start_time_str = QLDateUtils.getTimeWithFormat(_startTimeDate, "HH:mm");
		String end_time_str = QLDateUtils.getTimeWithFormat(_endTimeDate, "HH:mm");
		StringBuilder start_builder = new StringBuilder();
		start_builder.append(week_str);
		start_builder.append(" ");
		start_builder.append(start_time_str);
		startView.setText(start_builder.toString());

		StringBuilder end_builder = new StringBuilder();
		end_builder.append(week_str);
		end_builder.append(" ");
		end_builder.append(end_time_str);
		endView.setText(end_builder.toString());
	}

	private void showMenu(View view) {
		if (popupWindow == null) {
			View menuView = LayoutInflater.from(this).inflate(R.layout.menu_view_add_calendar, null);
			popupWindow = new PopupWindow(menuView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			menuView.findViewById(R.id.save_edit_layout).setOnClickListener(this);
			menuView.findViewById(R.id.remove_ly).setOnClickListener(this);
			menuView.findViewById(R.id.edit_edit_layout).setOnClickListener(this);
			popupWindow.setOutsideTouchable(true);
			menuView.setOnTouchListener(this);
		}
		if (popupWindow.isShowing()) {
			return;
		}
		popupWindow.update();

		popupWindow.showAsDropDown(view, 0, (int) getResources().getDimension(R.dimen.dp_10));
	}

	private void addCalendar(String content) {
		if (startDateTime.getTime() > endDateTime.getTime()) {
			QLToastUtils.showToast(AddCalendarActivity2.this, "开始时间不能晚于结束时间");
			return;
		}

		Date dtNow = QLDateUtils.getDateTimeNow();

		UserInfo userInfo = UserManagerController.getCurrUserInfo();

		CalendarInfo info = new CalendarInfo();
		info.setContent(content);
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

	private void removeCalendar() {
		if (reviewInfo == null) {
			return;
		}
		dbUtils.delete(CalendarInfo.class, "id = ?", reviewInfo.getId());
		finish();
	}

	private void saveCalendar() {
		titleHander.addRightView(LayoutInflater.from(this).inflate(R.layout.homeactivity_rightlayout, null), true);
		titleHander.getRightLayout().setVisibility(View.VISIBLE);
		imageButton = (ImageButton) titleHander.getRightLayout().findViewById(R.id.imageButton02);
		titleHander.getRightLayout().findViewById(R.id.aboutMe_ib).setVisibility(View.GONE);
		imageButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.popmenu_more));
		imageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showMenu(v);
			}
		});
		editStatus = false;
		contentView.setFocusable(false);
		if (reviewInfo == null) {
			return;
		}
		if (startDateTime.getTime() > endDateTime.getTime()) {
			QLToastUtils.showToast(AddCalendarActivity2.this, "开始时间不能晚于结束时间");
			return;
		}

		reviewInfo.setContent(contentView.getText().toString());
		reviewInfo.setStartDateTime(QLDateUtils.getTimeWithFormat(startDateTime, "yyyy-MM-dd HH:mm:ss"));
		// Toast.makeText(this, reviewInfo.getStartDateTime(), 0).show();
		reviewInfo.setEndDateTime(QLDateUtils.getTimeWithFormat(endDateTime, "yyyy-MM-dd HH:mm:ss"));
		reviewInfo.setStartTime(startDateTime.getTime());
		dbUtils.update(reviewInfo);

	}

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
				String week_str = QLDateUtils.getWeekDateString(startDateTime);
				String time_str = QLDateUtils.getTimeWithFormat(date, "HH:mm");
				StringBuilder builder = new StringBuilder();
				builder.append(week_str);
				builder.append(" ");
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
		hideKeyboard();
		timePickerView.show();
	}

	@Override
	public void onClick(View v) {
		int vid = v.getId();
		switch (vid) {
		case R.id.add_calendar_event_start_view:
			if (editing) {
				if (editStatus) {
					showTimePicker(startDateTime, true);
				}
			} else {
				showTimePicker(startDateTime, true);
			}
			break;
		case R.id.add_calendar_event_end_view:
			if (editing) {
				if (editStatus) {
					showTimePicker(endDateTime, false);
				}
			} else {
				showTimePicker(endDateTime, false);
			}

			break;
		case R.id.edit_edit_layout:
			// Toast.makeText(AddCalendarActivity.this, "编辑",
			// Toast.LENGTH_SHORT).show();
			editView();
			editStatus = !editStatus;

			popupWindow.dismiss();
			break;
		case R.id.right_tv:
			// 保存修改
			saveCalendar();

			break;
		case R.id.remove_ly:
			// 删除
			showDelectCalendar();
			break;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
		}
		return false;
	}

	private void editView() {
		contentView = (EditText) findViewById(R.id.add_calendar_event_content_view);
		setTitle("编辑日程");
		titleHander.addRightView(LayoutInflater.from(AddCalendarActivity2.this).inflate(R.layout.title_rightlayout, null), true);
		TextView tv = (TextView) titleHander.getRightLayout().findViewById(R.id.right_tv);
		tv.setOnClickListener(this);
		contentView.setFocusableInTouchMode(true);
	}

	private void showDelectCalendar() {
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		View view = LayoutInflater.from(this).inflate(R.layout.dialog_delete_textview, null);
		TextView messageTV = (TextView) view.findViewById(R.id.dialog_tv);
		messageTV.setText("确定删除该日程吗");
		alertDialog.setTitle("删除");
		// alertDialog.setMessage("确定删除该日程吗");
		alertDialog.setView(view);
		alertDialog.setIcon(0);
		alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "是", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				removeCalendar();
			}
		});
		alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "否", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

			}
		});
		alertDialog.show();
	}

	// 解决日期筛选星期错乱问题
	private String getWeekDateString(Date date) {

		String[] weekdata_list = { "日", "一", "二", "三", "四", "五", "六" };

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int week = cal.get(Calendar.DAY_OF_WEEK);
		week = week < 0 ? 0 : week;
		String weekdate = weekdata_list[week];
		return ("星期" + weekdate);
	}

	// 调用隐藏系统默认的输入法
	private void hideKeyboard() {
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

}
