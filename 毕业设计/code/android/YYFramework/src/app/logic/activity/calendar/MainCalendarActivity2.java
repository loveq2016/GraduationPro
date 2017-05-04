package app.logic.activity.calendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.ql.activity.customtitle.ActActivity;
import org.ql.utils.QLDateUtils;
import org.ql.views.pagerindicator.TitlePageIndicator;

import com.google.gson.Gson;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.TextView;
import app.logic.activity.ActTitleHandler;
import app.logic.adapter.YYBaseListAdapter;
import app.logic.controller.UserManagerController;
import app.logic.pojo.CalendarInfo;
import app.logic.pojo.UserInfo;
import app.logic.singleton.ZSZSingleton;
import app.utils.db.sqlite.DbUtils;
import app.utils.db.sqlite.WhereBuilder;
import app.utils.dialog.Test;
import app.view.YYListView;
import app.yy.geju.R;

/**
 * 
 * SiuJiYung create at 2016年8月18日 下午2:03:39
 * 
 */

public class MainCalendarActivity2 extends ActActivity implements OnClickListener, OnDateSelectedListener, OnMonthChangedListener, OnItemClickListener {

	private MaterialCalendarView calenderView;
	//日程列表
	private YYListView calendarListView;
	private ImageButton addCalendarBtn;

	private ActTitleHandler titleHandler = new ActTitleHandler();
	//适配器
	private YYBaseListAdapter<CalendarInfo> mAdapter = null;
	private DbUtils dbUtils;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbsHandler(titleHandler);
		setContentView(R.layout.activity_main_calendar);
		setTitle("我的日程");

		titleHandler.replaseLeftLayout(this, true);
		titleHandler.getLeftLayout().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		dbUtils = DbUtils.create(this);

		calenderView = (MaterialCalendarView) findViewById(R.id.calendarView);
		calendarListView = (YYListView) findViewById(R.id.calendar_list_view);
		addCalendarBtn = (ImageButton) findViewById(R.id.add_btn);

		calenderView.setSelectionColor(0xff56abe4);
		calenderView.setCurrentDate(QLDateUtils.getDateTimeNow());
		calenderView.setSelectedDate(QLDateUtils.getDateTimeNow());
		calenderView.setLeftArrowMask(getResources().getDrawable(R.drawable.arrow_left));
		calenderView.setRightArrowMask(getResources().getDrawable(R.drawable.arrow_right));
		calenderView.setArrowColor(Color.parseColor("#0000ff"));
		
//		calenderView.addDecorators(new Test());
		
		calenderView.setOnDateChangedListener(this);
		calenderView.setOnMonthChangedListener(this);
		// calenderView.postInvalidate();
		addCalendarBtn.setOnClickListener(this);

		mAdapter = new YYBaseListAdapter<CalendarInfo>(this) {
			@Override
			public View createView(int position, View convertView, ViewGroup parent) {
				if (convertView == null) {
					convertView = LayoutInflater.from(mContext).inflate(R.layout.view_calendar_cell, null);
					saveView("calendar_time_view", R.id.calendar_time_view, convertView);
					saveView("calendar_content_view", R.id.calendar_content_view, convertView);
//					saveView("calendar_creator_name", R.id.calendar_creator_name, convertView);
				}
				TextView timeView = getViewForName("calendar_time_view", convertView);
				TextView contentView = getViewForName("calendar_content_view", convertView);
//				TextView creatorView = getViewForName("calendar_creator_name", convertView);

				CalendarInfo info = getItem(position);
				if (info != null) {
					Date startDate = QLDateUtils.createDateTimeFromString(info.getStartDateTime(), "yyyy-MM-dd HH:mm:ss");
					Date endDate = QLDateUtils.createDateTimeFromString(info.getEndDateTime(), "yyyy-MM-dd HH:mm:ss");
					String time_str = QLDateUtils.getTimeWithFormat(startDate, "HH:mm");
					String end_time_str = QLDateUtils.getTimeWithFormat(endDate, "HH:mm");
					// boolean sameDay = QLDateUtils.isSameDay(startDate,
					// endDate);
					StringBuilder builder = new StringBuilder();
					builder.append(time_str);
					builder.append("\n~\n");
					builder.append(end_time_str);

					timeView.setText(builder.toString());
					contentView.setText(info.getContent());
//					creatorView.setText(info.getCreatorName());
				}

				return convertView;
			}
		};

		calendarListView.setPullLoadEnable(false, true);
		calendarListView.setPullRefreshEnable(false);
		calendarListView.setOnItemClickListener(this);
		calendarListView.setAdapter(mAdapter);
		
		setTouchLeft2RightEnable(false);
	}

	@Override
	protected void onResume() {
		super.onResume();
		loadCalendarList(calenderView.getSelectedDate().getDate());
		Date day = calenderView.getCurrentMonth().getDate();
		Date from = QLDateUtils.getFirstDayOfMonth(day);
		Date to = QLDateUtils.getTheLastDayOfMonth(day);
		flagThisMonthCalendars(from, to);

		// 从数据库获取flag
		List<CalendarInfo> calendarInfos = dbUtils.queryAll(CalendarInfo.class);
		List<CalendarDay> calendarDays = new ArrayList<CalendarDay>();
		for (CalendarInfo info : calendarInfos) {
			Date startDate = QLDateUtils.createDateTimeFromString(info.getStartDateTime(), "yyyy-MM-dd HH:mm:ss");
			calendarDays.add(CalendarDay.from(startDate));
		}
		if (ZSZSingleton.getZSZSingleton().getFlagDays() != null) {
			ZSZSingleton.getZSZSingleton().getFlagDays().clear();
		}
		if (calendarDays.size() >= 1) {
			ZSZSingleton.getZSZSingleton().setFlagDays(calendarDays);
		}
		calenderView.postInvalidate();
		calenderView.frashDayView();
	}

	private void loadCalendarList(Date dt) {
		UserInfo userInfo = UserManagerController.getCurrUserInfo();
		Calendar calendar = Calendar.getInstance(Locale.getDefault());
		calendar.setTime(dt);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		long start = calendar.getTime().getTime();
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		long end = calendar.getTime().getTime();

		QLDateUtils.getTimeWithFormat(dt, "yyyy-MM-dd HH:mm:ss");
		String start_dt_str = QLDateUtils.getTimeWithFormat(dt, "yyyy-MM-dd") + "%";
		List<CalendarInfo> calendarInfos = dbUtils.queryList(CalendarInfo.class, "startDateTime like ? and member_info_id=?", start_dt_str, userInfo.getWp_member_info_id());
		mAdapter.setDatas(calendarInfos);
	}

	private void flagThisMonthCalendars(Date from, Date to) {
		UserInfo userInfo = UserManagerController.getCurrUserInfo();
		long startDate = from.getTime();
		long endDate = to.getTime();
		List<CalendarInfo> calendarInfos = dbUtils.queryList(CalendarInfo.class, "(startTime BETWEEN ? and ?) and member_info_id =?", startDate, endDate, userInfo.getWp_member_info_id());
		calenderView.cleanFlags();
		if (calendarInfos != null) {
			for (CalendarInfo calendarInfo : calendarInfos) {
				Date dt = QLDateUtils.createDateTimeFromString(calendarInfo.getStartDateTime(), "yyyy-MM-dd HH:mm:ss");
				calenderView.flagDay(dt, true);
			}
			calenderView.frashDayView();
		}
	}

	private void addCalendar() {
		Intent addIntent = new Intent();
		Date dt = calenderView.getSelectedDate().getDate();
		String dt_str = QLDateUtils.getTimeWithFormat(dt, "yyyy-MM-dd HH:mm:ss");
		addIntent.setClass(this, AddCalendarActivity.class);
		addIntent.putExtra(AddCalendarActivity.kDateKey, dt_str);
		startActivity(addIntent);
	}

	@Override
	public void onClick(View v) {
		int vid = v.getId();
		if (vid == R.id.add_btn) {
			addCalendar();
		}
	}

	@Override
	public void onDateSelected(MaterialCalendarView widget, CalendarDay date, boolean selected) {
		if (selected && date != null) {
			loadCalendarList(date.getDate());
		}

		if (selected && date != null) {
			// ZSZSingleton.getZSZSingleton().getchangeOrgToBlUListener().onCallBack(date);
		}
	}

	@Override
	public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
		Date from, to;

		from = QLDateUtils.getFirstDayOfMonth(date.getDate());
		to = QLDateUtils.getTheLastDayOfMonth(date.getDate());
		flagThisMonthCalendars(from, to);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		CalendarInfo info = (CalendarInfo) parent.getAdapter().getItem(position);
		if (info != null) {
			Gson gson = new Gson();
			String json_str = gson.toJson(info);
			Intent intent = new Intent();
			intent.setClass(this, AddCalendarActivity.class);
			intent.putExtra(AddCalendarActivity.kCalendarInfoJson, json_str);
			intent.putExtra(AddCalendarActivity.kModifyModel, true);
			startActivity(intent);
		}
	}
}
