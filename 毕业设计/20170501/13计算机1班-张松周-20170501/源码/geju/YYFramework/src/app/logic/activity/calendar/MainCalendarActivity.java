package app.logic.activity.calendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.canson.view.swipemenulistview.SwipeMenu;
import org.canson.view.swipemenulistview.SwipeMenuCreator;
import org.canson.view.swipemenulistview.SwipeMenuItem;
import org.canson.view.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import org.ql.activity.customtitle.ActActivity;
import org.ql.utils.QLDateUtils;
import org.ql.utils.QLToastUtils;
import org.ql.views.pagerindicator.TitlePageIndicator;

import com.bumptech.glide.load.model.ModelCache;
import com.google.gson.Gson;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.prolificinteractive.materialcalendarview.WeekView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import app.logic.activity.ActTitleHandler;
import app.logic.activity.chat.ChatRoomListActivity;
import app.logic.activity.user.UserInfoActivity;
import app.logic.adapter.YYBaseListAdapter;
import app.logic.controller.UserManagerController;
import app.logic.pojo.CalendarInfo;
import app.logic.pojo.UserInfo;
import app.logic.singleton.ZSZSingleton;
import app.utils.db.sqlite.DbUtils;
import app.utils.db.sqlite.WhereBuilder;
import app.utils.dialog.Test;
import app.utils.helpers.YYUtils;
import app.view.DialogNewStyleController;
import app.view.YYListView;
import app.yy.geju.R;
import app.yy.geju.R.color;
/**
 * 
 * SiuJiYung create at 2016年8月18日 下午2:03:39
 * 
 */
public class MainCalendarActivity extends ActActivity implements OnClickListener,OnDateSelectedListener, OnMonthChangedListener, OnItemClickListener {

	private Button  trueBtn , cancelBtn; //是否删除日程的标志
	private  DialogNewStyleController dialog ;
	private ActTitleHandler titleHandler = new ActTitleHandler();
	private View handerView ;
	//日曆對象
	private MaterialCalendarView calenderView;
	//日程列表
	private YYListView calendarListView;
	//添加日程
	private ImageButton addCalendarBtn;
	//辅助数据库类
	private DbUtils dbUtils;
	//數據源（備份）
	private List<CalendarInfo> datas = new ArrayList<CalendarInfo>();
	private String todyDtae ,selDtae ;
	private CalendarDay today ;
	//适配器
	private YYBaseListAdapter<CalendarInfo> mAdapter =  new YYBaseListAdapter<CalendarInfo>(this) {
		@Override
		public View createView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.view_calendar_cell, null);
				saveView("calendar_time_view", R.id.calendar_time_view, convertView);
				saveView("calendar_content_view", R.id.calendar_content_view, convertView);
			}
			TextView timeView = getViewForName("calendar_time_view", convertView);
			TextView contentView = getViewForName("calendar_content_view", convertView);
			CalendarInfo info = getItem(position);
			if (info != null) {
				Date startDate = QLDateUtils.createDateTimeFromString(info.getStartDateTime(), "yyyy-MM-dd HH:mm:ss");
				Date endDate = QLDateUtils.createDateTimeFromString(info.getEndDateTime(), "yyyy-MM-dd HH:mm:ss");
				String time_str = QLDateUtils.getTimeWithFormat(startDate, "HH:mm");
				String end_time_str = QLDateUtils.getTimeWithFormat(endDate, "HH:mm");
				StringBuilder builder = new StringBuilder();
				builder.append(time_str);
				builder.append("-");
				builder.append(end_time_str);
				timeView.setText( builder.toString());
				contentView.setText( info.getContent());
			}
			return convertView;
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbsHandler(titleHandler);
		setContentView(R.layout.activity_main_calendar);
		today = ZSZSingleton.getZSZSingleton().getToday();  //获取当天日期对象
		todyDtae = new SimpleDateFormat("yyyy-MM-dd").format(today.getDate()) ; //当天日期转化为指定格式
		selDtae = todyDtae.substring( 0 ,7 );               //截取当天日期的年月
		handerView = LayoutInflater.from( this ).inflate( R.layout.hander_listview_calendar_layout , null) ;
		dbUtils = DbUtils.create(this);
		//初始化titer
		initTiter() ;
		//找View
		initView() ;
		//初始化calenderView
		setCalenderView();
		//初始化calenderListView
		setCalendarListView();
		intiDialog( ) ;
	}
	/**
	 * 初始化Titer
	 */
	private void initTiter(){		
		setTitle( new SimpleDateFormat("yyyy-MM-dd").format( new Date()));
		titleHandler.replaseLeftLayout(this, true);
		RelativeLayout relativeLayout = titleHandler.getCenterLayout();
		if( relativeLayout != null ){
			relativeLayout.setBackgroundDrawable( getResources().getDrawable(R.drawable.calnerback) );	
		}
		titleHandler.getLeftLayout().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	/**
	 * 找View
	 */
	private void initView(){
		calenderView = (MaterialCalendarView) handerView.findViewById(R.id.calendarView);
		calendarListView = (YYListView) findViewById(R.id.calendar_list_view);
		calendarListView.addHeaderView( handerView );
		addCalendarBtn = (ImageButton) findViewById(R.id.add_btn);
		addCalendarBtn.setOnClickListener(this);			
	}
	/**
	 * 初始化 calenderView
	 */
	private void setCalenderView(){
		calenderView.setSelectionColor(0xcccccccc);//設置選擇后的顏色0xff56abe4
		calenderView.setCurrentDate( QLDateUtils.getDateTimeNow());
		calenderView.setSelectedDate( QLDateUtils.getDateTimeNow());
		calenderView.setLeftArrowMask(getResources().getDrawable(R.drawable.arrow_left));
		calenderView.setRightArrowMask(getResources().getDrawable(R.drawable.arrow_right));
//		calenderView.setArrowColor( Color.parseColor("#0000ff") );
//		calenderView.addDecorators(new Test());
		calenderView.setOnDateChangedListener(this);  //日期改變監聽器
		calenderView.setOnMonthChangedListener(this); //月改變監聽器
//      calenderView.postInvalidate();
		calenderView.setTopbarVisible(false);//添加头 false不添加
		String[] week = new String[]{ "日","一" , "二","三","四","五","六" };
		calenderView.setWeekDayLabels(week) ;
		calenderView.setWeekDayTextAppearance( Color.WHITE) ; //设为白色
	}
	/**
	 * 初始化calendarListView
	 */
    private void setCalendarListView(){
    	calendarListView.setPullLoadEnable(false, true);
		calendarListView.setPullRefreshEnable(false);
		calendarListView.setOnItemClickListener(this);
		calendarListView.setAdapter(mAdapter);
		 //創建側滑菜單
		SwipeMenuCreator menuCreator = new SwipeMenuCreator() {
			@Override
			public void create(SwipeMenu menu) {
				SwipeMenuItem openItem = new SwipeMenuItem(MainCalendarActivity.this);
				openItem.setBackground(R.drawable.menu_delete_bg);
				openItem.setWidth(YYUtils.dp2px(90, MainCalendarActivity.this));
				openItem.setTitle("刪除");
				openItem.setTitleSize(16);
				openItem.setTitleColor(0xfffcfcfc);
				menu.addMenuItem( openItem );
			}
		};
		//添加側滑菜單
		calendarListView.setMenuCreator( menuCreator );
		//給側滑菜單添加簡體昂器
		calendarListView.setOnMenuItemClickListener( new OnMenuItemClickListener() {
			@Override
			public void onMenuItemClick( int position, SwipeMenu menu , int index ) {
				delCalendar( position ) ;
			}
		});
	}

	/**
	 * 对话框初始化
	 * @param
	 */
	private void intiDialog( ) {
		View contentView = LayoutInflater.from(this).inflate(R.layout.del_calendar_dialog_view, null);
		TextView title = (TextView) contentView.findViewById(R.id.dialog_title_tv);
		trueBtn = (Button) contentView.findViewById(R.id.dialog_true_btn);
		cancelBtn = (Button) contentView.findViewById(R.id.dialog_cancel_btn);
		dialog = new DialogNewStyleController( this, contentView );
		//取消按钮
		cancelBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}

	/**
	 * 显示对话框
	 * @param position
	 */
	private void delCalendar( final int position ){
		dialog.show();
		//确定按钮
		trueBtn.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 删除选项（數據庫和數據源都要移除相對應的項，不然對應不上）
				removeCalendar( position );//只是移除數據庫
				//setListViewHeightBasedOnChildren( calendarListView ) ;//计算listView Itme 的高度
				//mAdapter.removeItemAt( position );
				//datas.remove( position );//數據源也要移除一個
				dialog.dismiss();
			}
		});
	}
	/**
	 * 移除日程（移除數據庫）
	 */
	private void removeCalendar( int index ) {
		dbUtils.delete( CalendarInfo.class , "id = ?", datas.get(index).getId() );
		onResume();
	}
	/**
	 * 计算ListViewd的 Itme的高度
	 * @param listView
	 */
	public static void setListViewHeightBasedOnChildren( YYListView listView ) { 
		ListAdapter listAdapter =  listView.getAdapter();  
		if (listAdapter == null) { 
		    return; 
		}   
		int totalHeight = 0; 
	    for (int i = 0; i < listAdapter.getCount(); i++) { 
		    View listItem = listAdapter.getView(i, null, listView); 
		    listItem.measure(0, 0); 
		    totalHeight += listItem.getMeasuredHeight(); 
		}   
		ViewGroup.LayoutParams params = listView.getLayoutParams(); 
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)); 
		listView.setLayoutParams(params); 
    }
	@Override
	protected void onResume() {
		super.onResume();
		//根據日期找日程列表
		loadCalendarList( calenderView.getSelectedDate().getDate());
		Date day = calenderView.getCurrentMonth().getDate();
		Date from = QLDateUtils.getFirstDayOfMonth(day);
		Date to = QLDateUtils.getTheLastDayOfMonth(day);
		//月發生變化
		flagThisMonthCalendars(from , to);
		//从数据库获取flag
		List<CalendarInfo> calendarInfos = dbUtils.queryAll(CalendarInfo.class);
		List<CalendarDay> calendarDays = new ArrayList<CalendarDay>();
		for (CalendarInfo info : calendarInfos) {
			Date startDate = QLDateUtils.createDateTimeFromString( info.getStartDateTime(), "yyyy-MM-dd HH:mm:ss" );
			calendarDays.add(CalendarDay.from(startDate));
		}
		if (ZSZSingleton.getZSZSingleton().getFlagDays() != null) {
			ZSZSingleton.getZSZSingleton().getFlagDays().clear();
		}
		if (calendarDays.size() >= 1) {
			ZSZSingleton.getZSZSingleton().setFlagDays(calendarDays);
		}
		calenderView.postInvalidate();//重新绘制View
		calenderView.frashDayView();

		if(ZSZSingleton.getZSZSingleton().getDate() == null){
			ZSZSingleton.getZSZSingleton().setDate(calenderView.getSelectedDate().getDate());
		}
	}
	/**
	 * 根據日期來查找日程列表
	 * @param dt
	 */
	private void loadCalendarList( Date dt ) {
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
		datas = calendarInfos ;
		mAdapter.setDatas(datas);
		//setListViewHeightBasedOnChildren( calendarListView ) ;//计算listView Itme 的高度
	}
	/**
	 * 标记这个月的日程
	 */
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
	/**
	 * 添加日程
	 */
	private void addCalendar() {
		Intent addIntent = new Intent();
		Date dt = calenderView.getSelectedDate().getDate();
		String dt_str = QLDateUtils.getTimeWithFormat(dt, "yyyy-MM-dd HH:mm:ss");
		addIntent.setClass( this, AddCalendarActivity.class);
		addIntent.putExtra( AddCalendarActivity.kDateKey, dt_str );
		startActivity( addIntent );
	}
	/**
	 * View的監聽
	 */
	@Override
	public void onClick(View v) {
		int vid = v.getId();
		if (vid == R.id.add_btn) {
			addCalendar();
		}
	}
	/**
	 * CalendarView的監聽
	 */
	@Override
	public void onDateSelected( MaterialCalendarView widget, CalendarDay date, boolean selected) {
		todyDtae = new SimpleDateFormat("yyyy-MM-dd").format(date.getDate()) ;
		selDtae = todyDtae.substring(0,7);
		setTitle( todyDtae );

		ZSZSingleton.getZSZSingleton().setDate(date.getDate());

		if (selected && date != null) {
			loadCalendarList( date.getDate() );
		}
		if (selected && date != null) {
			// ZSZSingleton.getZSZSingleton().getchangeOrgToBlUListener().onCallBack(date);
		}


	}
	/**
	 * 月發生變化
	 */
	@Override
	public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		String da = sdf.format(date.getDate());

		if(selDtae.equals( da.substring( 0,7 ))){
			setTitle( da.substring(0,7)+"-"+todyDtae.substring(8,10));
		}else {
			setTitle( da.substring(0,7));
		}


		try {
			ZSZSingleton.getZSZSingleton().setDate(sdf.parse(da.substring(0,8)+selDtae));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Date from , to ;
		from = QLDateUtils.getFirstDayOfMonth(date.getDate());
		to = QLDateUtils.getTheLastDayOfMonth(date.getDate());
		flagThisMonthCalendars(from, to);

		calenderView.postInvalidate();//重新绘制View
	}
	/**
	 * ListView的監聽
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		CalendarInfo info = (CalendarInfo) parent.getAdapter().getItem(position);
		if (info != null) {
			Gson gson = new Gson();
			String json_str = gson.toJson(info);
			Intent intent = new Intent();
			intent.setClass(this, AddCalendarActivity.class);
			intent.putExtra(AddCalendarActivity.kCalendarInfoJson, json_str);
			intent.putExtra( AddCalendarActivity.kModifyModel, true);
			startActivity(intent);
		}
	}
}

