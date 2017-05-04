package app.logic.activity.friends;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.ql.activity.customtitle.ActActivity;
import org.ql.utils.QLToastUtils;
import org.ql.views.listview.QLXListView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sort.sortlistview.CharacterParser;
import com.squareup.picasso.Picasso;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import app.config.http.HttpConfig;
import app.logic.activity.ActTitleHandler;
import app.logic.activity.org.DPMListActivity;
import app.logic.adapter.YYBaseListAdapter;
import app.logic.pojo.ExpansionInfo;
import app.logic.pojo.FriendInfo;
import app.utils.common.FrescoImageShowThumb;
import app.yy.geju.R;

/*
 * GZYY    2016-12-21  上午11:53:37
 * author: zsz
 */

public class LogicFriends extends ActActivity {

	public static final int REQUEST_CODE = 23;
	public static final String  WPMEMBERINFOID ="WPMEMBERINFOID";
	public static final String ORGID = "ORGID";
	public static final String DPAID = "DPAID";
	/**
	 * Friends 实体处理
	 */
	public static final int FRIENDS_INFO = 0;

	/**
	 * UserInfo 实体处理
	 */
	public static final int USER_INFO = 1;

	public static final String TITLE = "TITLE";
	public static final String MODEL = "MODEL";
	public static final String RIGHT_TEXT = "RIGHT_TEXT";
	public static final String DATAS_LIST = "DATAS_LIST";
	public static final String RESULT_LIST = "RESULT_LIST";
	public static final String SELECT_ITEM = "SELECT_ITEM";

	private int model;
	private boolean selectItem = false;
	private ActTitleHandler titleHandler;
	private Resources resources;
	private Gson gson;
	private CharacterParser characterParser;
	private ComparaotrExpasion comparaotrExpasion;
	private EditText searchEditText;
	private QLXListView listView;
	private List<ExpansionInfo> selectDatas = new ArrayList<ExpansionInfo>();
	private List<ExpansionInfo> datas = new ArrayList<ExpansionInfo>();
	private boolean searchStatus = false;
	private String org_id ;
	private String dpm_id ;
	private String wp_member_info_id ;
	private Button determineBut;
	// YSF 新增
	public static final String SETADMINI="SET_ADMINI";
	private boolean isAdmini = false ;


	private YYBaseListAdapter<ExpansionInfo> mAdapter = new YYBaseListAdapter<ExpansionInfo>(this) {
		@Override
		public View createView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = LayoutInflater.from(LogicFriends.this).inflate(R.layout.item_selectable_view, null);
				saveView("item_index_tv", R.id.item_index_tv, convertView);
				saveView("selected_item_imgview", R.id.selected_item_imgview, convertView);
				saveView("selected_item_tv", R.id.selected_item_tv, convertView);
				saveView("selected_item_cb", R.id.selected_item_cb, convertView);
			}
			ExpansionInfo info = getItem(position);
			if (info != null) {
				TextView index_tv = (TextView) getViewForName("item_index_tv", convertView);
				SimpleDraweeView headIv = (SimpleDraweeView) getViewForName("selected_item_imgview", convertView);
				TextView nameTv = (TextView) getViewForName("selected_item_tv", convertView);
				CheckBox box = (CheckBox) getViewForName("selected_item_cb", convertView);

				int section = getSectionForPosition(position);
				if (position == getPositionForSection(section)) {
					index_tv.setText(info.getItemSortLetters());
					index_tv.setVisibility(View.VISIBLE);
				} else {
					index_tv.setVisibility(View.GONE);
				}
//				headIv.setImageURI(HttpConfig.getUrl(info.getItemUrl()));
				FrescoImageShowThumb.showThrumb(Uri.parse(HttpConfig.getUrl(info.getItemUrl())),headIv);
//				Picasso.with(LogicFriends.this).load(HttpConfig.getUrl(info.getItemUrl())).placeholder(R.drawable.default_user_icon)
//						.error(R.drawable.default_user_icon).fit().centerCrop().into(headIv);
				if(info.getFriend_name()!=null&& !TextUtils.isEmpty(info.getFriend_name())){
					nameTv.setText(info.getFriend_name());
				}else{
					nameTv.setText(info.getNickName());
				}
				box.setVisibility(info.isItemShowCheck() ? View.VISIBLE : View.INVISIBLE);
				box.setChecked(info.isItemIsCheck());
			}
			return convertView;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		titleHandler = new ActTitleHandler();
		setAbsHandler(titleHandler);
		setContentView(R.layout.activity_friends_list2);
		isAdmini = getIntent().getBooleanExtra(SETADMINI , false );   //选中分组管理员的标志
		selectItem = getIntent().getBooleanExtra(SELECT_ITEM, false); //选中分组管理员的标志
		model = getIntent().getIntExtra(MODEL, -1);
//		org_id = getIntent().getStringExtra("ORGID") ;
//		dpm_id = getIntent().getStringExtra("DPAID") ;
		initTitle();
		initView();
		initData();
		addListener();
	}

	/**
	 * 初始化titleView
	 * 
	 * initTitleLogicFriends
	 */
	private void initTitle() {
		String titleString = getIntent().getStringExtra(TITLE);
		setTitle("");
		titleHandler.replaseLeftLayout(this, true);
		((TextView) titleHandler.getLeftLayout().findViewById(R.id.left_tv)).setText(titleString);
		titleHandler.getLeftLayout().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		String textString = getIntent().getStringExtra(RIGHT_TEXT);
		determineBut = titleHandler.getRightDefButton();
		determineBut.setText(TextUtils.isEmpty(textString) ? "确定" : textString);
		determineBut.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				exitActivity();
			}
		});
		determineBut.setVisibility( View.GONE);
	}

	/**
	 * 初始化view
	 * 
	 * initViewLogicFriends
	 */
	private void initView() {
		resources = getResources();
		searchEditText = (EditText) findViewById(R.id.search_edt);
		findViewById(R.id.search_edt_bg).setBackgroundDrawable(resources.getDrawable(R.drawable.shape_search_edt_bg));
		findViewById(R.id.search_bg).setBackgroundColor(resources.getColor(R.color.white));
		searchEditText.setHint("手机号/昵称");
		listView = (QLXListView) findViewById(R.id.friends_list_view);
		listView.setPullLoadEnable(false, false);
		listView.setPullRefreshEnable(false);
		listView.setAdapter(mAdapter);
	}

	/**
	 * 初始化数据
	 * 
	 * initDataLogicFriends
	 */
	private void initData() {
		gson = new Gson();
		characterParser = CharacterParser.getInstance();
		comparaotrExpasion = new ComparaotrExpasion();
		String dataString = getIntent().getStringExtra(DATAS_LIST);
		if (dataString != null) {
			datas = gson.fromJson(dataString, new TypeToken<List<ExpansionInfo>>() {}.getType());
			fillDatas();
			mAdapter.setDatas(datas);
		}
	}

	/**
	 * 添加监听
	 * 
	 * addListenerLogicFriends
	 */
	private void addListener() {
		searchEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String string = s.toString();
				if (TextUtils.isEmpty(string)) {
					searchStatus = false;
					mAdapter.setDatas(datas);
					judgment( datas );
				} else {
					searchStatus = true;
					selectDatasList(string);
					mAdapter.setDatas(selectDatas);
					judgment( selectDatas ) ;
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				int location = position - 1;
				ExpansionInfo info = mAdapter.getItem( location );
				if (info == null) {
					return;
				}
				if (!searchStatus) {    //不是在搜索状态下
					boolean isCheck = datas.get(location).isItemIsCheck();
					datas.get(location).setItemIsCheck(!isCheck);
					if( !isAdmini ){
						judgment( datas ); //判断 “确认” 按钮时候显示
					}
				} else {
					boolean isCheck = selectDatas.get(location).isItemIsCheck();
					selectDatas.get(location).setItemIsCheck(!isCheck);
//					for (int i = 0; i < datas.size(); i++) {
//						for (int j = 0; j < selectDatas.size(); j++) {
//							if (datas.get(i).getItemID().equals(selectDatas.get(j).getItemID())) {
//								datas.get(i).setItemIsCheck(!isCheck);
//								break;
//							}
//						}
//					}
					if( !isAdmini ){
						judgment( selectDatas ); //判断 “确认” 按钮时候显示
					}
				}
				wp_member_info_id = info.getWp_member_info_id();
				mAdapter.notifyDataSetChanged();

				if ( selectItem ) {
					exitActivity();
				}
			}
		});
	}

	/**
	 * 判断按钮是否显示
	 * @param list
	 */
	private void judgment( List<ExpansionInfo> list ){
		if( list == null ){
			determineBut.setVisibility( View.GONE );
			return;
		}
		Iterator iterator = list.iterator();
		while ( iterator.hasNext() ){
			ExpansionInfo expansionInfo = (ExpansionInfo) iterator.next();
			if ( expansionInfo.isItemIsCheck()){
				determineBut.setVisibility( View.VISIBLE );
				return;
			}
		}
		determineBut.setVisibility( View.GONE );

	}

	/**
	 * forResult的处理
	 * 
	 * exitActivityLogicFriends
	 */
	private void exitActivity() {
		// TODO
		if (gson == null) {
			gson = new Gson();
		}
		String resultString = null;
		Intent intent = new Intent();
		switch (model) {
		case FRIENDS_INFO:
			List<FriendInfo> resultList = new ArrayList<FriendInfo>();
			for (ExpansionInfo info : datas) {
				if (info.isItemIsCheck()) {
					resultList.add(info.getFriendInfo());
					wp_member_info_id = info.getWp_member_info_id() ;
				}
			}
			if( resultList.size() < 1 ){
//				determineBut.setVisibility( View.VISIBLE );
//				QLToastUtils.showToast( this , "没有选择联系人");
			}
			resultString = gson.toJson(resultList);
			break;
		default:
			List<ExpansionInfo> resultDefaultList = new ArrayList<ExpansionInfo>();
			for (ExpansionInfo info : datas) {
				if (info.isItemIsCheck()) {
					resultDefaultList.add(info);
					wp_member_info_id = info.getWp_member_info_id() ;
				}
			}
			resultString = gson.toJson(resultDefaultList);
			break;
		}
		intent.putExtra( WPMEMBERINFOID , wp_member_info_id ) ;
		intent.putExtra( RESULT_LIST, resultString);
		setResult( Activity.RESULT_OK, intent );
		finish();
	}
	/**
	 * 检索数据
	 * 
	 * selectDatasListLogicFriends
	 */
	private void selectDatasList(String keyString) {
		selectDatas.clear();
		for (ExpansionInfo info : datas) {
			if ((info.getNickName()!=null&&info.getNickName().contains(keyString)) || (info.getItemPhone()!=null&&info.getItemPhone().contains(keyString))||
					(info.getFriend_name()!=null&&info.getFriend_name().contains(keyString))) {
				selectDatas.add(info);
			}
		}
	}

	/**
	 * 根据position返回item数据第一个字符的ASCII码
	 * 
	 * @param position
	 * @return getSectionForPositionLogicFriends
	 */
	private int getSectionForPosition(int position) {
		return datas.get(position).getItemSortLetters().charAt(0);
	}
	/**
	 * 
	 * 根据 ASCII码，返回数据列表中第一个出现的item的第一个字符position
	 * 
	 * @param section
	 * @return getPositionForSectionLogicFriends
	 */
	private int getPositionForSection(int section) {
		for (int i = 0; i < datas.size(); i++) {
			String sortStr = datas.get(i).getItemSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		return -1;
	}
	/**
	 * 添加拼音索引，增加排序
	 * 
	 * fillDatasLogicFriends
	 */
	private void fillDatas() {
		for (int i = 0; i < datas.size(); i++) {
			String pinyinString ="";
			String name ="";
			if(datas.get(i).getFriend_name()!=null&&!TextUtils.isEmpty(datas.get(i).getFriend_name())){
				name = datas.get(i).getFriend_name();
			}else{
				name = datas.get(i).getNickName();
			}
			pinyinString = characterParser.getSelling(name);
			String sortString = pinyinString.substring(0, 1).toUpperCase();
			if (sortString.matches("[A-Z]")) { //!"unknown".equals(pinyinString)&&
				datas.get(i).setItemSortLetters(sortString.toUpperCase());
			} else {
				datas.get(i).setItemSortLetters("#");
			}
			if(name!=null && "昵".equals(name.substring(0,1))){  //对“昵”字做特殊处理（目前没有找到更好的库）
				datas.get(i).setItemSortLetters("N");
			}
		}
		Collections.sort(datas, comparaotrExpasion);
	}
}
