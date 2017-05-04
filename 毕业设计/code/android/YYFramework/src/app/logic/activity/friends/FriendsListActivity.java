package app.logic.activity.friends;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.ql.activity.customtitle.ActActivity;
import org.ql.activity.customtitle.DefaultHandler;
import org.ql.utils.QLToastUtils;
import org.ql.views.listview.QLXListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import app.config.http.HttpConfig;
import app.logic.activity.ActTitleHandler;
import app.logic.activity.chat.ChatRoomInfoActivity;
import app.logic.adapter.YYBaseListAdapter;
import app.logic.controller.ChatRoomController;
import app.logic.controller.UserManagerController;
import app.logic.pojo.FriendInfo;
import app.logic.pojo.UserInfo;
import app.logic.pojo.YYChatRoomInfo;
import app.utils.common.Listener;
import app.yy.geju.R;

/**
 * 
 * SiuJiYung create at 2016年6月15日 下午5:47:53 好友列表
 */

public class FriendsListActivity extends ActActivity implements OnItemClickListener {

	public static final String kSELECTED_ITEM_MODEL = "kSELECTED_ITEM_MODEL";
	public static final String kTITLE = "KTITLE";
	public static final String kSELECTED_ITEMS_JSON_STRING = "kSELECTED_ITEMS_JSON_STRING";
	public static final int kSELECT_ITEMS = 23;

	private int idx;
	private QLXListView mListView;
	private EditText searchEditText;
	private boolean selectedItemModel;
	private List<FriendInfo> initSelectedItems;
	private ArrayList<FriendInfo> allItems = new ArrayList<FriendInfo>();
	private HashMap<String, Integer> selectedMap = new HashMap<String, Integer>();
	private ActTitleHandler mHandler = new ActTitleHandler();
	private String chat_room_id;
	private boolean[] dataStatus;

	private YYBaseListAdapter<FriendInfo> mAdapter = new YYBaseListAdapter<FriendInfo>(this) {
		@Override
		public View createView(final int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.view_selectable_item, null);
				saveView("selected_item_cb", R.id.selected_item_cb, convertView);
				saveView("selected_item_imgview", R.id.selected_item_imgview, convertView);
				saveView("selected_item_tv", R.id.selected_item_tv, convertView);
				CheckBox cBox = getViewForName("selected_item_cb", convertView);
				cBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						FriendInfo _info = (FriendInfo) buttonView.getTag();
						if (_info != null) {
							setSelected(_info);
						}
					}
				});
			}
			FriendInfo info = getItem(position);
			if (info != null) {
				boolean _selected = selectedMap.containsKey(info.getWp_friends_info_id());
				CheckBox cBox = getViewForName("selected_item_cb", convertView);
				// cBox.setTag(info);
				// cBox.setChecked(_selected);
				String url = HttpConfig.getUrl(info.getPicture_url());
				setImageToImageViewCenterCrop(url, "selected_item_imgview", -1, convertView);
				String _name = info.getFriend_name() == null || TextUtils.isEmpty(info.getFriend_name()) ? info.getNickName() : info.getFriend_name();
				setTextToViewText(_name, "selected_item_tv", convertView);
				cBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						dataStatus[position] = isChecked;
						int count = 0;
						for (int i = 0; i < dataStatus.length; i++) {
							if (dataStatus[i]) {
								count++;
							}
						}
						String countString = count == 0 ? "确认" : "确认(" + count + ")";
						mHandler.getRightDefButton().setText(countString);

					}
				});
				cBox.setChecked(dataStatus[position]);
			}

			return convertView;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbsHandler(mHandler);
		setContentView(R.layout.activity_friends_list);
		mHandler.getRightDefButton().setVisibility(View.VISIBLE);
		mHandler.getRightDefButton().setText("确定");
		mHandler.getRightDefButton().setTextColor(0xfffcfcfc);
		mHandler.getRightDefButton().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				exitActivity();
			}
		});
		mHandler.replaseLeftLayout(this, true);
		mHandler.getLeftLayout().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		idx = 0;
		selectedItemModel = getIntent().getBooleanExtra(kSELECTED_ITEM_MODEL, true);
		chat_room_id = getIntent().getStringExtra(ChatRoomInfoActivity.kChatRoomID);
		String customTitle = getIntent().getStringExtra(kTITLE);
		String _select_items_json = getIntent().getStringExtra(kSELECTED_ITEMS_JSON_STRING);
		if (_select_items_json != null) {
			try {
				Gson gson = new Gson();
				initSelectedItems = gson.fromJson(_select_items_json, new TypeToken<List<FriendInfo>>() {
				}.getType());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (customTitle != null) {
			setTitle(customTitle);
		} else {
			setTitle("好友列表");
		}

		searchEditText = (EditText) findViewById(R.id.search_et);
		searchEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				search(s.toString());
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

		mListView = (QLXListView) findViewById(R.id.friends_list_view);
		mListView.setPullLoadEnable(false, true);
		mListView.setPullRefreshEnable(false);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);

		getFriendsList();
	}

	private void search(String text) {
		// if (TextUtils.isEmpty(text)) {
		// configSelectItems();
		// mListView.stopRefresh();
		// mAdapter.setDatas(allItems);
		// return;
		// }
		// if (allItems == null || allItems.size() < 1) {
		// return;
		// }
		// ArrayList<FriendInfo> tmpInfos = new ArrayList<FriendInfo>();
		// for (FriendInfo friendInfo : allItems) {
		// if (friendInfo.getNickName() != null &&
		// friendInfo.getNickName().contains(text)) {
		// tmpInfos.add(friendInfo);
		// }
		// }
		if (TextUtils.isEmpty(text)) {
			dataStatus = new boolean[allItems.size()];
			mListView.stopRefresh();
			mAdapter.setDatas(allItems);
			return;
		}
		if (allItems.size() < 1) {
			return;
		}
		ArrayList<FriendInfo> tmpInfos = new ArrayList<FriendInfo>();
		for (FriendInfo info : allItems) {
			if (info.getNickName() != null && info.getNickName().contains(text)) {
				tmpInfos.add(info);
			}
		}
		dataStatus = new boolean[tmpInfos.size()];

		mAdapter.setDatas(tmpInfos);
	}

	private void getFriendsList() {
		showWaitDialog();
		UserManagerController.getFriendsList(this, new Listener<List<FriendInfo>, List<FriendInfo>>() {
			@Override
			public void onCallBack(List<FriendInfo> request, List<FriendInfo> reply) {
				dismissWaitDialog();
				allItems.clear();
				if (reply != null && reply.size() > 0) {
					allItems.addAll(reply);
				}
				// configSelectItems();
				mListView.stopRefresh();
				dataStatus = new boolean[allItems.size()];
				mAdapter.setDatas(allItems);
			}
		});
	}

	private void configSelectItems() {
		if (initSelectedItems == null || initSelectedItems.size() < 1) {
			return;
		}
		for (FriendInfo _info : initSelectedItems) {
			selectedMap.put(_info.getWp_friends_info_id(), Integer.valueOf(1));
		}
	}

	private void setSelected(FriendInfo info) {
		if (info == null || info.getWp_friends_info_id() == null) {
			Log.i(getClass().getSimpleName(), "friendInfo or wp_friends_info_id is null !");
			return;
		}
		if (selectedMap.containsKey(info.getWp_friends_info_id())) {
			selectedMap.remove(info.getWp_friends_info_id());
		} else {
			selectedMap.put(info.getWp_friends_info_id(), Integer.valueOf(1));
			mAdapter.notifyDataSetChanged();
		}
		String txt = selectedMap.keySet().size() > 0 ? "确定(" + selectedMap.keySet().size() + ")" : "确定";
		mHandler.getRightDefButton().setText(txt);
	}

	private List<FriendInfo> getSelectedItems() {
		ArrayList<FriendInfo> _tmpList = new ArrayList<FriendInfo>();
		for (String id : selectedMap.keySet()) {
			for (FriendInfo friendInfo : allItems) {
				if (friendInfo.getWp_friends_info_id() != null && friendInfo.getWp_friends_info_id().equals(id)) {
					_tmpList.add(friendInfo);
				}
			}
		}
		return _tmpList;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		FriendInfo info = (FriendInfo) mAdapter.getItem(arg2);
		if (info == null) {
			return;
		}
		if (selectedItemModel) {
			// 选择模式
			// setSelected(info);
			// QLToastUtils.showToast(FriendsListActivity.this,
			// String.valueOf(arg2));
		} else {
			// 打开个人资料？
		}

	}

	// 过滤已经有的人

	private void exitActivity() {
		if (selectedItemModel) {
			// List<FriendInfo> _selectedItems = getSelectedItems();
			// if (_selectedItems == null) {
			// finish();
			// }
			List<FriendInfo> _selectedItems = new ArrayList<FriendInfo>();
			for (int i = 0; i < allItems.size(); i++) {
				if (dataStatus[i]) {
					_selectedItems.add(allItems.get(i));
				}
			}
			if (_selectedItems.size() < 1) {
				finish();
			}
			Gson gson = new Gson();
			String _sel_gson = gson.toJson(_selectedItems);
			Intent intent = new Intent();
			intent.putExtra(kSELECTED_ITEMS_JSON_STRING, _sel_gson);
			setResult(Activity.RESULT_OK, intent);
		}
		finish();
	}

}
