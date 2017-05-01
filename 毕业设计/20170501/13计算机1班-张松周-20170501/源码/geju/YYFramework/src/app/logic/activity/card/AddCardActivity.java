package app.logic.activity.card;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.canson.view.swipemenulistview.SwipeMenu;
import org.canson.view.swipemenulistview.SwipeMenuCreator;
import org.canson.view.swipemenulistview.SwipeMenuItem;
import org.canson.view.swipemenulistview.SwipeMenuListView;
import org.canson.view.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import org.ql.activity.customtitle.ActActivity;
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
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import app.logic.activity.ActTitleHandler;
import app.logic.adapter.YYBaseListAdapter;
import app.logic.controller.UserManagerController;
import app.logic.pojo.CardInfo;
import app.logic.pojo.HWBusinessCardInfo;
import app.logic.singleton.YYSingleton;
import app.utils.common.Listener;
import app.utils.common.Public;
import app.utils.helpers.YYUtils;
import app.utils.image.QLImageHelper;
import app.yy.geju.R;

/**
 * 
 * SiuJiYung create at 2016年6月27日 下午3:47:34
 * 
 */

public class AddCardActivity extends ActActivity implements OnItemClickListener, OnClickListener {

	public static final String kCARD_SCAN_INFO = "kCARD_SCAN_INFO";
	public static final String kCARD_IMAGE_PATH = "kCARD_IMAGE_PATH";

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
	private SwipeMenuListView mListView;
	private String newCardImagePath;
	private ArrayList<PropertyItem> items;

	private YYBaseListAdapter<PropertyItem> mAdapter = new YYBaseListAdapter<AddCardActivity.PropertyItem>(this) {
		@Override
		public View createView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(AddCardActivity.this).inflate(R.layout.item_card_info, null);
				saveView("card_info_title", R.id.card_info_title, convertView);
				saveView("card_info_value", R.id.card_info_value, convertView);

			}
			PropertyItem _item = (PropertyItem) getItem(position);
			if (_item != null) {
				setValueForTextView(_item.title, R.id.card_info_title, convertView);
				if (_item.title == null || TextUtils.isEmpty(_item.title)) {
					setTextColorForTextView(0xff3462ff, R.id.card_info_value, convertView);
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
		ActTitleHandler handler = new ActTitleHandler();
		setAbsHandler(handler);
		setContentView(R.layout.activity_card_infos);
		handler.replaseLeftLayout(this, true);
		handler.getLeftLayout().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		handler.getRightDefButton().setText("保存");
		handler.getRightDefButton().setTextColor(0xfffcfcfc);
		handler.getRightDefButton().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					saveCardInfo();
				} catch (IOException e) {
					dismissWaitDialog();
					e.printStackTrace();
					QLToastUtils.showToast(AddCardActivity.this, e.getMessage());
				}
			}
		});

		setTitle("添加名片");

		cardImageView = (ImageView) findViewById(R.id.card_info_img_view);
		cardImageView.setOnClickListener(this);
		mListView = (SwipeMenuListView) findViewById(R.id.card_info_porfer);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		SwipeMenuCreator menuCreator = new SwipeMenuCreator() {
			@Override
			public void create(SwipeMenu menu) {
				SwipeMenuItem openItem = new SwipeMenuItem(AddCardActivity.this);
				openItem.setBackground(R.drawable.menu_delete_bg);
				openItem.setWidth(YYUtils.dp2px(90, AddCardActivity.this));
				openItem.setTitle("移除");
				openItem.setTitleSize(16);
				openItem.setTitleColor(0xfffcfcfc);
				menu.addMenuItem(openItem);
			}
		};
		mListView.setMenuCreator(menuCreator);
		mListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public void onMenuItemClick(int position, SwipeMenu menu, int index) {
				if (index == 0) {
					// 删除选项
					removeItemAtPosition(position);
				}
			}
		});

		cardInfo = new CardInfo();
		String img_path = getIntent().getStringExtra(kCARD_IMAGE_PATH);
		String info_string = getIntent().getStringExtra(kCARD_SCAN_INFO);
		cardInfo.setBc_pic_url(img_path);
		newCardImagePath = img_path;
//		int degree =  YYSingleton.getInstance().getBitmapDegree(newCardImagePath);
		
//		Bitmap tempBitmap = YYSingleton.getInstance().rotateBitmapByDegree(BitmapFactory.decodeFile(newCardImagePath), degree);
		Picasso.with(this).load(new File(newCardImagePath)).fit().centerInside().into(cardImageView);
//		cardImageView.setImageBitmap(tempBitmap);
		if (info_string != null) {
			try {
				Gson gson = new Gson();
				HWBusinessCardInfo hwCardInfo = gson.fromJson(info_string, HWBusinessCardInfo.class);
				cardInfo.setBc_cellPhone(getValuesMatchRex(hwCardInfo.getMobile(), "\\d{11,}"));// 使用模糊匹配即可
				cardInfo.setBc_email(getValuesMatchRex(hwCardInfo.getEmail(), "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*"));
				if (hwCardInfo.getIm() != null && hwCardInfo.getIm().size() > 0) {
					ArrayList<String> _tmpList = new ArrayList<String>();
					for (String _tmp : hwCardInfo.getIm()) {
						if (YYUtils.isTencenQQNumber(_tmp) || YYUtils.isEmail(_tmp) || YYUtils.canUserForAccount(_tmp)) {
							_tmpList.add(_tmp);
						}
					}
					cardInfo.setBc_im(_tmpList);
				}

				if (hwCardInfo.getName() != null && hwCardInfo.getName().size() > 0) {
					cardInfo.setBc_name(hwCardInfo.getName().get(0));
				}
				if (hwCardInfo.getComp() != null && hwCardInfo.getComp().size() > 0) {
					ArrayList<String> _tmpList = new ArrayList<String>();
					for (String _tmp : hwCardInfo.getComp()) {
						if (!YYUtils.matchRex(_tmp, "^[1-9]\\d*")) {
							_tmpList.add(_tmp);
						}
					}
					cardInfo.setBc_orgName(_tmpList);
				}
				cardInfo.setBc_tel(getValuesMatchRex(hwCardInfo.getHtel(), "\\d{3}-\\d{8}|\\d{4}-\\d{7}"));
				if (hwCardInfo.getTitle() != null && hwCardInfo.getTitle().size() > 0) {
					cardInfo.setBc_title(hwCardInfo.getTitle().get(0));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		items = new ArrayList<PropertyItem>();
		createPropertyItems();
	}

	private void removeItemAtPosition(int position) {
		if (position > -1 && position < items.size()) {
			PropertyItem _item = items.get(position);
			if (removeItemEnable(_item) && !_item.emptyItem) {
				items.remove(position);
			} else if (!_item.emptyItem) {
				_item.value = null;
			}
			mAdapter.setDatas(items);
		}
	}

	private List<String> getValuesMatchRex(List<String> list, String rex) {
		if (list == null || list.size() < 1) {
			return null;
		}
		if (rex == null || TextUtils.isEmpty(rex)) {
			return list;
		}
		ArrayList<String> _tmpList = new ArrayList<String>();
		for (String _tmp : list) {
			if (YYUtils.matchRex(_tmp, rex)) {
				_tmpList.add(_tmp);
			}
		}
		return _tmpList;
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
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
		AlertDialog alertDialog = alertBuilder.create();
		alertDialog.setView(contentView);
		alertDialog.setCancelable(true);
		alertDialog.setIcon(0);
		alertDialog.setCanceledOnTouchOutside(true);
		alertDialog.setTitle("编辑" + getTitleForItem(item));
		DialogInterface.OnClickListener buttonOkClickListener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == AlertDialog.BUTTON_NEUTRAL) {
					boolean addItem = item.emptyItem;
					// 保存更新
					item.value = eText.getText().toString();
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

	private boolean removeItemEnable(PropertyItem item) {
		switch (item.catgory) {
		case KEY_CELL_PHONE:
		case KEY_EMAIL:
		case KEY_IM:
		case KEY_TEL:
		case KEY_ORG_NAME:
		case KEY_TITLE:
			return true;
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
		PropertyItem _item = mAdapter.getItem(arg2);
		if (_item == null) {
			return;
		}
		showEditBox(_item);
	}

	private CardInfo getEditInfo() {
		CardInfo _tmpInfo = new CardInfo();
		// _tmpInfo.setBc_id(cardInfo.getBc_id());
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

	/**
	 * 保存信息
	 * 
	 * @throws IOException
	 */
	private void saveCardInfo() throws IOException {
		setWaitingDialogText("正在保存...");
		showWaitDialog();
		CardInfo newCardInfo = getEditInfo();
		UserManagerController.createCard(this, newCardInfo, newCardImagePath, new Listener<Integer, String>() {
			@Override
			public void onCallBack(Integer status, String reply) {
				dismissWaitDialog();
				if (status == 1) {
					QLToastUtils.showToast(AddCardActivity.this, "保存成功!");
					finish();
				} else if (reply != null) {
					QLToastUtils.showToast(AddCardActivity.this, reply);
				}
			}
		});
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
		if (v.getId() == R.id.card_info_img_view) {
			QLImageHelper.openCamera(this);
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
							QLImageHelper.compressImage(srcPathString, null, 200);
							Picasso.with(AddCardActivity.this).load(new File(srcPathString)).fit().into(cardImageView);
							newCardImagePath = srcPathString;
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

}
