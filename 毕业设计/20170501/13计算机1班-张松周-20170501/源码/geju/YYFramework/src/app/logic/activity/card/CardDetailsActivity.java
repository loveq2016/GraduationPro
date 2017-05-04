package app.logic.activity.card;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ql.activity.customtitle.ActActivity;
import org.ql.activity.customtitle.DefaultHandler;
import org.ql.activity.customtitle.OnActActivityResultListener;
import org.ql.app.alert.AlertDialog;
import org.ql.utils.QLToastUtils;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import android.R.integer;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.ViewPager.LayoutParams;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import app.config.http.HttpConfig;
import app.logic.activity.ActTitleHandler;
import app.logic.activity.calendar.AddCalendarActivity;
import app.logic.adapter.YYBaseListAdapter;
import app.logic.controller.UserManagerController;
import app.logic.pojo.CardInfo;
import app.logic.singleton.ZSZSingleton;
import app.utils.common.Listener;
import app.utils.image.QLImageHelper;
import app.yy.geju.R;

/**
 * 
 * SiuJiYung create at 2016年6月24日 下午4:57:50
 * 
 */

public class CardDetailsActivity extends ActActivity implements OnItemClickListener, OnClickListener, OnTouchListener {

	public static final String kCARD_INFO = "kCARD_INFO";

	private static final int KEY_NAME = 1;
	private static final int KEY_ORG_NAME = 2;
	private static final int KEY_TITLE = 3;
	private static final int KEY_CELL_PHONE = 4;
	private static final int KEY_TEL = 5;
	private static final int KEY_EMAIL = 6;
	private static final int KEY_IM = 7;
	private static final int KEY_TAG = 8;

	private CardInfo cardInfo;
	private ImageView cardImageView;
	private ListView mListView;
	private LinearLayout cards_layout;
	private String newCardImagePath;
	private ArrayList<PropertyItem> items;
	private boolean saveStatus;
	private ActTitleHandler handler;
	private PopupWindow popupWindow_more;
	private ImageButton imageButton;

	private YYBaseListAdapter<PropertyItem> mAdapter = new YYBaseListAdapter<CardDetailsActivity.PropertyItem>(this) {
		@Override
		public View createView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(CardDetailsActivity.this).inflate(R.layout.item_card_info, null);
				saveView("card_info_title", R.id.card_info_title, convertView);
				saveView("card_info_value", R.id.card_info_value, convertView);

			}
			PropertyItem _item = (PropertyItem) getItem(position);
			if (_item != null) {
				setValueForTextView(_item.title, R.id.card_info_title, convertView);
				if (_item.title == null || TextUtils.isEmpty(_item.title)) {
					setTextColorForTextView(CardDetailsActivity.this.getResources().getColor(R.color.app_blue), R.id.card_info_value, convertView);
				} else {
					setTextColorForTextView(0xff222222, R.id.card_info_value, convertView);
				}
				setValueForTextView(_item.value, R.id.card_info_value, convertView);
			}

			return convertView;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		handler = new ActTitleHandler();
		setAbsHandler(handler);
		setContentView(R.layout.activity_card_infos);

		// if (saveStatus) {
		// try {
		// saveCardInfo();
		// } catch (IOException e) {
		// dismissWaitDialog();
		// QLToastUtils.showToast(CardDetailsActivity.this, e.getMessage());
		// e.printStackTrace();
		// }
		//
		// } else {
		// saveStatus = !saveStatus;
		// handler.getRightDefButton().setText("保存");
		//
		// }

		setTitle("名片信息");
		handler.addRightView(LayoutInflater.from(this).inflate(R.layout.homeactivity_rightlayout, null), true);
		handler.getRightLayout().setVisibility(View.VISIBLE);
		imageButton = (ImageButton) handler.getRightLayout().findViewById(R.id.imageButton02);
		imageButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.popmenu_more));
		imageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showpopupWindow(v);
			}
		});
		handler.replaseLeftLayout(this, true);
		handler.getLeftLayout().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		cardImageView = (ImageView) findViewById(R.id.card_info_img_view);
		cardImageView.setOnClickListener(this);
		mListView = (ListView) findViewById(R.id.card_info_porfer);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		cards_layout = (LinearLayout) findViewById(R.id.cards_layout);

		mListView.setFocusableInTouchMode(false);

		String info_string = getIntent().getStringExtra(kCARD_INFO);
		if (info_string != null) {
			try {
				Gson gson = new Gson();
				cardInfo = gson.fromJson(info_string, CardInfo.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		items = new ArrayList<CardDetailsActivity.PropertyItem>();
		if (cardInfo.getBc_pic_url() != null) {
			String imgPath = HttpConfig.getUrl(cardInfo.getBc_pic_url());
			Picasso.with(CardDetailsActivity.this).load(imgPath).error(R.drawable.bc_break).fit().centerInside().into(cardImageView);
		}
		createPropertyItems();
	}

	/**
	 * 保存信息
	 * 
	 * @throws IOException
	 */
	private void saveCardInfo() throws IOException {
		setWaitingDialogText("正在保存...");
		showWaitDialog();
		CardInfo newCardInfo = getEditInfo();
		UserManagerController.modifyCard(this, newCardInfo, newCardImagePath, new Listener<Integer, String>() {
			@Override
			public void onCallBack(Integer status, String reply) {
				dismissWaitDialog();
				if (status == 1) {
					QLToastUtils.showToast(CardDetailsActivity.this, "保存成功!");
					finish();
				} else if (reply != null) {
					QLToastUtils.showToast(CardDetailsActivity.this, reply);
				}
			}
		});
	}

	private void createPropertyItems() {

		createItem("姓名", cardInfo.getBc_name(), KEY_NAME);
		createItem("单位", cardInfo.getBc_orgName(), KEY_ORG_NAME);
		createItem("职位", cardInfo.getBc_title(), KEY_TITLE);
		createItem("手机", cardInfo.getBc_cellPhone(), KEY_CELL_PHONE);
		createItem("电话", cardInfo.getBc_tel(), KEY_TEL);
		createItem("Email", cardInfo.getBc_email(), KEY_EMAIL);
		createItem("IM", cardInfo.getBc_im(), KEY_IM);
		createItem("备注", cardInfo.getBc_tag(), KEY_TAG);
		mAdapter.setDatas(items);
	}

	private void createItem(String title, String value, int id) {
		PropertyItem _item = new PropertyItem();
		_item.catgory = id;
		_item.title = title;
		_item.value = value;
		items.add(_item);
	}

	private void createItem(String title, List<String> values, int id) {
		PropertyItem _item = null;
		if (values == null || values.size() < 1) {
			_item = new PropertyItem();
			_item.title = "";
			_item.catgory = id;
			_item.value = "添加" + title;
			_item.emptyItem = true;
			items.add(_item);
			return;
		}
		boolean first_item = true;
		for (String _valueString : values) {
			if (first_item) {
				createItem(title, _valueString, id);
			} else {
				createItem("", _valueString, id);
			}
		}
		_item = new PropertyItem();
		_item.catgory = id;
		_item.value = "添加" + title;
		_item.emptyItem = true;
		items.add(_item);
	}

	private void showEditBox(final PropertyItem item) {
		View contentView = LayoutInflater.from(this).inflate(R.layout.view_edit_card_property, null);
		final EditText eText = (EditText) contentView.findViewById(R.id.edit_card_property_et);
		if (item.emptyItem) {
			eText.setHint(item.value);
		} else {
			eText.setText(item.value);
		}
		
		eText.setSelection(eText.getText().toString().length());
		if (item.value.contains("手机") || item.value.contains("电话")) {
			eText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
		}
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
		AlertDialog alertDialog = alertBuilder.create();
		alertDialog.setView(contentView);
		alertDialog.setCancelable(true);
		alertDialog.setCanceledOnTouchOutside(true);
		alertDialog.setIcon(0);
		alertDialog.setTitle("编辑" + getTitleForItem(item));
		DialogInterface.OnClickListener buttonOkClickListener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == AlertDialog.BUTTON_NEUTRAL) {
					boolean addItem = item.emptyItem;
					// 保存更新
					item.value = eText.getText().toString();
					if (item.value == null || TextUtils.isEmpty(item.value)) {
						return;
					}
					item.title = getTitleForItem(item);
					item.emptyItem = false;

					for (int idx = 0; idx < items.size(); idx++) {
						PropertyItem _tmpItem = items.get(idx);
						if (_tmpItem.id == item.id) {
							items.set(idx, item);
							break;
						}
					}

					if (addItem && addItemEnable(item)) {
						// 添加空选项
						PropertyItem _item = new PropertyItem();
						_item.catgory = item.catgory;
						_item.value = "添加" + item.title;
						_item.emptyItem = true;
						int flag = 0;
						for (int idx = 0; idx < items.size(); idx++) {
							PropertyItem _tmpItem = items.get(idx);
							if (_tmpItem.catgory == item.catgory) {
								flag = 1;
							} else if (_tmpItem.catgory != item.catgory && flag == 1) {
								flag = 2;
								items.add(idx, _item);
								break;
							}
						}
					}
					mAdapter.setDatas(items);
				}
				dialog.dismiss();
			}
		};
		alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消", buttonOkClickListener);
		alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "保存", buttonOkClickListener);
		alertDialog.show();
	}

	private boolean addItemEnable(PropertyItem item) {
		switch (item.catgory) {
		case KEY_CELL_PHONE:
		case KEY_EMAIL:
		case KEY_IM:
		case KEY_TEL:
		case KEY_ORG_NAME:
			return true;
		case KEY_TITLE:
		case KEY_NAME:
		case KEY_TAG:
			return false;
		}
		return false;
	}

	private String getTitleForItem(PropertyItem item) {
		switch (item.catgory) {
		case KEY_CELL_PHONE:
			return "手机";
		case KEY_EMAIL:
			return "Email";
		case KEY_IM:
			return "IM";
		case KEY_NAME:
			return "姓名";
		case KEY_ORG_NAME:
			return "单位";
		case KEY_TAG:
			return "备注";
		case KEY_TEL:
			return "电话";
		case KEY_TITLE:
			return "头衔";
		}
		return "";
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if (saveStatus) {
			PropertyItem _item = mAdapter.getItem(arg2);
			if (_item == null) {
				return;
			}
			showEditBox(_item);
		}

	}

	private CardInfo getEditInfo() {
		CardInfo _tmpInfo = new CardInfo();
		_tmpInfo.setBc_id(cardInfo.getBc_id());
		_tmpInfo.setBc_cellPhone(getValuesForCatgory(KEY_CELL_PHONE));
		_tmpInfo.setBc_email(getValuesForCatgory(KEY_EMAIL));
		_tmpInfo.setBc_im(getValuesForCatgory(KEY_IM));
		_tmpInfo.setBc_name(getValueForCatgory(KEY_NAME));
		_tmpInfo.setBc_orgName(getValuesForCatgory(KEY_ORG_NAME));
		_tmpInfo.setBc_tag(getValueForCatgory(KEY_TAG));
		_tmpInfo.setBc_tel(getValuesForCatgory(KEY_TEL));
		_tmpInfo.setBc_title(getValueForCatgory(KEY_TITLE));
		return _tmpInfo;
	}

	private String getValueForCatgory(int catgory) {
		for (PropertyItem _item : items) {
			if (catgory == _item.catgory && !_item.emptyItem) {
				return _item.value;
			}
		}
		return null;
	}

	private List<String> getValuesForCatgory(int catgory) {
		ArrayList<String> _v = new ArrayList<String>();
		for (PropertyItem _item : items) {
			if (catgory == _item.catgory && !_item.emptyItem) {
				_v.add(_item.value);
			}
		}
		return _v;
	}

	static class PropertyItem {
		String title;
		String value;
		int catgory;
		boolean emptyItem;
		int id;
		static int _count = 0;

		public PropertyItem() {
			_count++;
			id = _count;
			emptyItem = false;
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.card_info_img_view:
			if (saveStatus) {
				QLImageHelper.openCamera(this);
			}
			break;
		case R.id.remove_ly:
			UserManagerController.removeCard(this, cardInfo.getBc_id(), null);
			finish();
			break;
		case R.id.edit_edit_layout:

			handler.addRightView(LayoutInflater.from(CardDetailsActivity.this).inflate(R.layout.title_rightlayout, null), true);
			TextView tv = (TextView) handler.getRightLayout().findViewById(R.id.right_tv);
			tv.setOnClickListener(this);

			saveStatus = !saveStatus;

			ZSZSingleton.getZSZSingleton().backgroundAlpha(this, 1f);
			popupWindow_more.dismiss();

			break;

		case R.id.right_tv:
			try {
				saveCardInfo();
			} catch (IOException e) {
				dismissWaitDialog();
				QLToastUtils.showToast(CardDetailsActivity.this, e.getMessage());
				e.printStackTrace();
			}

			break;

		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == QLImageHelper.FLAG_CHOOSE_PHONE && resultCode == Activity.RESULT_OK) {
			File f = new File(QLImageHelper.getPhotoDir(), QLImageHelper.localTempImageFileName);
			final String srcPathString = f.getAbsolutePath();
			File imgFile = new File(srcPathString);
			if (imgFile.exists()) {
				try {
					new Thread(new Runnable() {
						@Override
						public void run() {
							newCardImagePath = srcPathString;
							QLImageHelper.compressImage(srcPathString, null, 200);
							Picasso.with(CardDetailsActivity.this).load(new File(srcPathString)).into(cardImageView);
						}
					}).start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				QLToastUtils.showToast(this, "获取照片失败，找不到该照片路径。");
			}
		}
	}

	private void showpopupWindow(View v) {
		if (popupWindow_more == null) {
			View menuView = LayoutInflater.from(this).inflate(R.layout.menu_view_add_calendar, null);
			popupWindow_more = new PopupWindow(menuView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			menuView.findViewById(R.id.edit_edit_layout).setOnClickListener(this);
			menuView.findViewById(R.id.remove_ly).setOnClickListener(this);
			popupWindow_more.setOutsideTouchable(true);
			menuView.setOnTouchListener(this);

		}
		if (popupWindow_more.isShowing()) {
			return;
		}

		ZSZSingleton.getZSZSingleton().backgroundAlpha(this, 0.5f);
		popupWindow_more.update();
		popupWindow_more.showAsDropDown(v, 0, (int) getResources().getDimension(R.dimen.dp_10));
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (popupWindow_more != null && popupWindow_more.isShowing()) {
			popupWindow_more.dismiss();
			ZSZSingleton.getZSZSingleton().backgroundAlpha(this, 1f);
		}
		return false;

	}
}
