package app.logic.activity.card;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.canson.view.swipemenulistview.SwipeMenu;
import org.canson.view.swipemenulistview.SwipeMenuCreator;
import org.canson.view.swipemenulistview.SwipeMenuItem;
import org.canson.view.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import org.ql.activity.customtitle.ActActivity;
import org.ql.activity.customtitle.DefaultHandler;
import org.ql.activity.customtitle.OnActActivityResultListener;
import org.ql.utils.QLToastUtils;
import org.ql.utils.image.QLAsyncImage;
import org.ql.views.listview.QLXListView;
import org.ql.views.listview.QLXListView.IXListViewListener;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import app.config.http.HttpConfig;
import app.logic.activity.ActTitleHandler;
import app.logic.activity.card.CardDetailsActivity.PropertyItem;
import app.logic.activity.user.UserInfoActivity;
import app.logic.adapter.YYBaseListAdapter;
import app.logic.controller.UserManagerController;
import app.logic.pojo.CardInfo;
import app.logic.pojo.HWBusinessCardInfo;
import app.utils.common.Listener;
import app.utils.helpers.BusinessCardHelper;
import app.utils.helpers.ImagePickerHelper;
import app.utils.helpers.YYUtils;
import app.utils.helpers.BusinessCardHelper.OnScanPictureListener;
import app.utils.image.SMCImageHelper;
import app.view.YYListView;
import app.yy.geju.R;

/**
 * 
 * SiuJiYung create at 2016年6月24日 下午3:22:39
 * 
 * 
 * 丢弃
 */

public class CardListActivity extends ActActivity implements OnItemClickListener, IXListViewListener, OnScanPictureListener {

	private Timer timer;
	private int timeCount = 1;

	private YYListView mListview;
	// private BusinessCardHelper cardHelper;
	private ImagePickerHelper pickerHelper;
	private boolean status = true;
	private List<CardInfo> list = new ArrayList<CardInfo>();

	private YYBaseListAdapter<CardInfo> mAdapter = new YYBaseListAdapter<CardInfo>(this) {

		@Override
		public View createView(int position, View convertView, ViewGroup parent) {
			if (getItemViewType(position) == 0) {
				if (convertView == null) {
					convertView = LayoutInflater.from(CardListActivity.this).inflate(R.layout.item_card_list_view2, parent, false);
					saveView("card_info_name", R.id.card_info_name, convertView);
					saveView("card_info_tel", R.id.card_info_tel, convertView);
					// saveView("card_info_title_name",
					// R.id.card_info_title_name,
					// convertView);
					saveView("card_info_img", R.id.card_info_img, convertView);
				}
				CardInfo _item = (CardInfo) getItem(position);
				if (_item != null) {
					setValueForTextView(_item.getBc_name(), R.id.card_info_name, convertView);
					if (_item.getBc_cellPhone() != null && _item.getBc_cellPhone().size() > 0) {
						setValueForTextView(_item.getBc_cellPhone().get(0), R.id.card_info_tel, convertView);
					}
					// setValueForTextView(_item.getBc_title(),
					// R.id.card_info_title_name, convertView);
					ImageView imageView = getViewForName("card_info_img", convertView);
					Uri uri = Uri.parse(HttpConfig.getUrl(_item.getBc_pic_url()));
					// Picasso.with(mContext).load(uri).error(R.drawable.default_card).fit().centerCrop().into(imageView);
					Picasso.with(mContext).load(uri).error(R.drawable.default_card).placeholder(R.drawable.default_user_icon).fit().centerInside().into(imageView);
				}

			} else {
				if (convertView == null) {
					convertView = LayoutInflater.from(CardListActivity.this).inflate(R.layout.item_card_list_view2, parent, false);
					saveView("card_info_name", R.id.card_info_name, convertView);
					saveView("card_info_tel", R.id.card_info_tel, convertView);
					// saveView("card_info_title_name",
					// R.id.card_info_title_name,
					// convertView);
					saveView("card_info_img", R.id.card_info_img, convertView);
				}
				CardInfo _item = (CardInfo) getItem(position);
				if (_item != null) {
					setValueForTextView(_item.getBc_name(), R.id.card_info_name, convertView);
					if (_item.getBc_cellPhone() != null && _item.getBc_cellPhone().size() > 0) {
						setValueForTextView(_item.getBc_cellPhone().get(0), R.id.card_info_tel, convertView);
					}
					// setValueForTextView(_item.getBc_title(),
					// R.id.card_info_title_name, convertView);
					ImageView imageView = getViewForName("card_info_img", convertView);
					Uri uri = Uri.parse(HttpConfig.getUrl(_item.getBc_pic_url()));
					Picasso.with(mContext).load(uri).error(R.drawable.default_card).fit().centerCrop().into(imageView);
				}

			}

			return convertView;
		}

		public int getViewTypeCount() {
			return 2;
		};

		public int getItemViewType(int position) {
			CardInfo info = mAdapter.getItem(position);
			if (info != null && info.getBc_pic_url() != null) {
				return 0;
			}
			return 1;
		};

		@Override
		public boolean menuEnable(int position) {
			if (getItemViewType(position) == 1)
				return false;
			return true;
		};

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActTitleHandler handler = new ActTitleHandler();
		setAbsHandler(handler);
		setContentView(R.layout.activity_card_list);

		// cardHelper = new BusinessCardHelper();
		// cardHelper.setOnScanPictureListener(this);

		setTouchLeft2RightEnable(false);
		handler.addLeftView(LayoutInflater.from(this).inflate(R.layout.title_leftview_layout, null), true);
		handler.getLeftLayout().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		// handler.getRightDefButton().setBackgroundResource(R.drawable.add_btn_default);
		handler.addRightView(LayoutInflater.from(this).inflate(R.layout.title_rightview_layout, null), true);
		handler.getRightLayout().findViewById(R.id.title_right_tv).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// BusinessCardHelper cardHelper = new BusinessCardHelper();
				// cardHelper.setOnScanPictureListener(CardListActivity.this);
				// cardHelper.startScanCard(CardListActivity.this);
				openCamera();

			}
		});

		setTitle("名片管理");
		mListview = (YYListView) findViewById(R.id.card_list_view);
		mListview.setPullRefreshEnable(true);
		mListview.setPullLoadEnable(false, true);
		mListview.setAdapter(mAdapter);
		mListview.setOnItemClickListener(this);
		mListview.setXListViewListener(this);
		SwipeMenuCreator menuCreator = new SwipeMenuCreator() {
			@Override
			public void create(SwipeMenu menu) {
				SwipeMenuItem openItem = new SwipeMenuItem(CardListActivity.this);
				openItem.setBackground(R.drawable.menu_delete_bg);
				openItem.setWidth(YYUtils.dp2px(90, CardListActivity.this));
				openItem.setTitle("移除");
				openItem.setTitleSize(16);
				openItem.setTitleColor(0xfffcfcfc);
				menu.addMenuItem(openItem);
			}
		};
		mListview.setMenuCreator(menuCreator);
		mListview.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public void onMenuItemClick(int position, SwipeMenu menu, int index) {
				if (index == 0) {
					// 删除选项
					removeCard(position);

				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		loadCardList();
	}

	private void removeCard(int position) {
		CardInfo info = mAdapter.getItem(position);
		if (info == null) {
			return;
		}
		list.remove(position);
		showWaitDialog();
		UserManagerController.removeCard(this, info.getBc_id(), new Listener<Integer, String>() {

			@Override
			public void onCallBack(Integer status, String reply) {
				dismissWaitDialog();
				if (status == 1) {
					loadCardList();
				}

			}
		});
	}

	private void loadCardList() {
		UserManagerController.getMyCardList(this, new Listener<Void, List<CardInfo>>() {
			@Override
			public void onCallBack(Void status, List<CardInfo> reply) {
				mListview.stopLoadMore();
				mListview.stopRefresh();
				list.clear();
				if (reply != null && reply.size() > 0) {
					list.addAll(reply);
				} else {
					CardInfo info = new CardInfo();
					info.setBc_name("请添加名片！！");
					list.add(info);
				}
				mAdapter.setDatas(list);
			}
		});
	}

	// 启动相机
	private void openCamera() {
		pickerHelper = ImagePickerHelper.createNewImagePickerHelper(CardListActivity.this);
		pickerHelper.setImageSourceType(ImagePickerHelper.kImageSource_UserSelect);
		pickerHelper.setOnReciveImageListener(new Listener<Void, String>() {
			@Override
			public void onCallBack(Void status, String reply) {
				dismissWaitDialog();
				if (reply != null && !TextUtils.isEmpty(reply)) {
					Intent intent = new Intent();
					intent.setClass(CardListActivity.this, AddCardActivity.class);
					intent.putExtra(AddCardActivity.kCARD_IMAGE_PATH, reply);
					// intent.putExtra(AddCardActivity.kCARD_IMAGE_PATH,
					// pickerHelper.getSDFilePath());
					intent.putExtra(AddCardActivity.kCARD_SCAN_INFO, reply);
					startActivity(intent);
				}
			}
		});
		pickerHelper.setOnActActivityResultListener(new OnActActivityResultListener() {

			@Override
			public void onActivityResult(int requestCode, int resultCode, Intent data) {
				// TODO Auto-generated method stub
				showWaitDialog();
			}
		});
		pickerHelper.openCamera();

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		int position = arg2 - 1;
		CardInfo info = (CardInfo) mAdapter.getItem(position);
		if (info == null) {
			return;
		}
		if (info.getBc_id() == null || TextUtils.isEmpty(info.getBc_id())) {

			openCamera();
		} else {

			if (info.getBc_pic_url() == null || TextUtils.isEmpty(info.getBc_pic_url())) {
				return;
			}
			Gson gson = new Gson();
			String jsonString = gson.toJson(info);
			Intent intent = new Intent();
			intent.setClass(this, CardDetailsActivity.class);
			intent.putExtra(CardDetailsActivity.kCARD_INFO, jsonString);
			startActivity(intent);
		}
	}

	@Override
	public void onRefresh() {
		loadCardList();
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onAnalyzing() {
		setWaitingDialogText("正在分析名片...请稍后");
		CardListActivity.this.showWaitDialog();
	}

	@Override
	public void onScanResult(String result, String imgPath) {
		dismissWaitDialog();
		if (result != null && !TextUtils.isEmpty(result) && imgPath != null) {
			Intent intent = new Intent();
			intent.setClass(this, AddCardActivity.class);
			intent.putExtra(AddCardActivity.kCARD_IMAGE_PATH, imgPath);
			intent.putExtra(AddCardActivity.kCARD_SCAN_INFO, result);
			startActivity(intent);
		} else {
			QLToastUtils.showToast(this, result);
		}
	}

}
