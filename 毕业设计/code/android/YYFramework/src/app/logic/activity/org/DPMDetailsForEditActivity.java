package app.logic.activity.org;

import java.util.ArrayList;
import java.util.List;


import org.QLConstant;
import org.ql.utils.QLToastUtils;
import org.ql.utils.image.QLAsyncImage;

import u.aly.co;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import android.R.integer;
import android.app.Activity;
import org.ql.app.alert.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import app.config.http.HttpConfig;
import app.logic.activity.ActTitleHandler;
import app.logic.activity.InitActActivity;
import app.logic.activity.friends.FriendsListActivity;
import app.logic.activity.user.Welcome2Activity;
import app.logic.adapter.YYBaseListAdapter;
import app.logic.controller.OrganizationController;
import app.logic.controller.UserManagerController;
import app.logic.pojo.DepartmentInfo;
import app.logic.pojo.FriendInfo;
import app.logic.pojo.OrgRequestMemberInfo;
import app.logic.pojo.UserInfo;
import app.utils.common.Listener;
import app.view.ZSZGridView;
import app.yy.geju.R;

/*
 * GZYY    2016-9-2  上午10:10:05
 */

public class DPMDetailsForEditActivity extends InitActActivity implements OnItemClickListener, OnClickListener {

	public static final String ALL_MEMBERLIST = "ALL_MEMBERLIST";
	public static final String DPM_ID = "DPM_ID";
	public static final String ACTION_FROM_ADDDPM = "ACTION_FROM_ADDDPM";

	private ActTitleHandler titleHandler;
	private GridView mGridView;
	private Button delectDPM_btn;
	private TextView dpm_name_tv;

	private String org_id;
	private String dpm_id;
	private String dpm_name;
	private List<UserInfo> userInfos = new ArrayList<UserInfo>();

	private boolean delectStatus;
	private boolean isAdmin = false;

	private YYBaseListAdapter<UserInfo> mAdapter = new YYBaseListAdapter<UserInfo>(this) {

		@Override
		public View createView(final int position, View convertView, ViewGroup parent) {

			if (getItemViewType(position) == 0) {
				if (convertView == null) {
					convertView = LayoutInflater.from(DPMDetailsForEditActivity.this).inflate(R.layout.item_gridview_dpm_member, null);
					saveView("iv1", R.id.iv1, convertView);
					saveView("funcation_item_title_tv", R.id.funcation_item_title_tv, convertView);
					saveView("delect_iv", R.id.delect_iv, convertView);
				}

				UserInfo info = getItem(position);
				if (info != null) {
					setTextToViewText(info.getNickName(), "funcation_item_title_tv", convertView);

					String url = HttpConfig.getUrl(info.getPicture_url());
					ImageView imageView = (ImageView) getViewForName("iv1", convertView);
					// Picasso.with(DPMDetailsForEditActivity.this).load(Uri.parse(url)).into(imageView);
					setImageToImageViewCenterCrop(url, "iv1", R.drawable.default_user_icon, convertView);
					// asyncImage.loadImage(url, imageView);
				}
			} else if (getItemViewType(position) == 2) {
				if (convertView == null) {
					convertView = LayoutInflater.from(DPMDetailsForEditActivity.this).inflate(R.layout.item_gridview_dpm_member, null);
					saveView("iv1", R.id.iv1, convertView);
				}
				ImageView imageView = (ImageView) getViewForName("iv1", convertView);
				imageView.setImageDrawable(getResources().getDrawable(R.drawable.add_dpm_member_btn));
			} else {
				if (convertView == null) {
					convertView = LayoutInflater.from(DPMDetailsForEditActivity.this).inflate(R.layout.item_gridview_dpm_member, null);
					saveView("iv1", R.id.iv1, convertView);
					saveView("funcation_item_title_tv", R.id.funcation_item_title_tv, convertView);
					saveView("delect_iv", R.id.delect_iv, convertView);
				}

				final UserInfo info = getItem(position);
				if (info != null) {
					setTextToViewText(info.getNickName(), "funcation_item_title_tv", convertView);

					String url = HttpConfig.getUrl(info.getPicture_url());
					ImageView imageView = (ImageView) getViewForName("iv1", convertView);
					// Picasso.with(DPMDetailsForEditActivity.this).load(Uri.parse(url)).into(imageView);
					setImageToImageViewCenterCrop(url, "iv1", R.drawable.default_user_icon, convertView);
					// asyncImage.loadImage(url, imageView);

					if (info.isDpm_status()) {
						View view = getViewForName("delect_iv", convertView);
						view.setVisibility(View.VISIBLE);
						view.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								if (isAdmin) {
									showWaitDialog();
									removeMemberAtIndex(position);
								}

							}
						});
					}

				}

			}

			return convertView;
		}

		public int getItemViewType(int position) {
			// 0:初始状态，1:出现右上图标,2:最后一个添加显示
			UserInfo info = mAdapter.getItem(position);
			if (info != null && info.isDPMLastMenber()) { // 最后一个
				return 2;
			}
			if (info != null && info.isDpm_status()) {
				return 1;
			}

			return 0;
		}

		public int getViewTypeCount() {
			return 3;
		}
	};

	@Override
	protected void initActTitleView() {
		titleHandler = new ActTitleHandler();
		setAbsHandler(titleHandler);
	}

	@Override
	protected void initView(Bundle savedInstanceState) {
		setContentView(R.layout.activity_dpm_details_for_edit);

		setTitle("分组编辑");
		titleHandler.getRightDefButton().setText("");
		titleHandler.getRightDefButton().setTextColor(0xfffcfcfc);
		titleHandler.getRightLayout().setVisibility(View.VISIBLE);
		titleHandler.getRightDefButton().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// if (!delectStatus) {
				// changeUserInfoDPMStatus(true);
				// delectStatus = !delectStatus;
				// titleHandler.getRightDefButton().setText("完成");
				// } else {
				// changeUserInfoDPMStatus(false);
				// delectStatus = !delectStatus;
				// titleHandler.getRightDefButton().setText("编辑");
				// // 保存分组名称
				// updateDPM(org_id, dpm_id);
				// }

			}
		});
		titleHandler.replaseLeftLayout(this, true);
		titleHandler.getLeftLayout().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();

			}
		});
		mGridView = (GridView) findViewById(R.id.all_menber_gv);
		delectDPM_btn = (Button) findViewById(R.id.delect_dpm);
		dpm_name_tv = (TextView) findViewById(R.id.dpm_name);

		dpm_name_tv.setOnClickListener(this);
		delectDPM_btn.setOnClickListener(this);

	}

	@Override
	protected void initData() {
		// String member_json = getIntent().getStringExtra(ALL_MEMBERLIST);
		org_id = getIntent().getStringExtra(DPMDetailsActivity.kORG_ID);
		dpm_id = getIntent().getStringExtra(DPM_ID);

		getMemberList();
		getDPMList();
		mGridView.setAdapter(mAdapter);

		mGridView.setOnItemClickListener(this);

		getMyOrgMember();

	}

	// 改变状态，更新view
	private void changeUserInfoDPMStatus(boolean status) {
		for (int i = 0; i < userInfos.size() - 1; i++) {
			userInfos.get(i).setDpm_status(status);
		}
		mAdapter.notifyDataSetChanged();
	}

	// 重新计算GridView 的高度
	private void countGridViewHeight() {

		int num = userInfos.size();
		int totalHeight = 0;
		if (num < 4) {
			View itemView = mAdapter.getView(0, null, mGridView);
			itemView.measure(0, 0);
			totalHeight += itemView.getMeasuredHeight();
		} else {
			int result = num / 4;
			for (int i = 0; i < result; i++) {
				View itemView = mAdapter.getView(i, null, mGridView);
				itemView.measure(0, 0);
				totalHeight += itemView.getMeasuredHeight();
			}
			if (num % 4 > 0) {
				totalHeight += totalHeight / result;
			}
		}

		ViewGroup.LayoutParams params = mGridView.getLayoutParams();
		params.height = totalHeight;
		mGridView.setLayoutParams(params);
	}

	// 解散分组
	private void removeDPM(String org_id, String dpm_id) {
		showWaitDialog();
		OrganizationController.removeDPM(this, org_id, dpm_id, new Listener<Integer, String>() {
			@Override
			public void onCallBack(Integer status, String reply) {
				dismissWaitDialog();
				if (status != 1) {
					String msg = reply == null ? "操作失败" : reply;
					QLToastUtils.showToast(DPMDetailsForEditActivity.this, msg);
				}
				finish();
			}
		});
	}

	// 在分组里面移出成员
	private void removeMemberAtIndex(final int position) {
		UserInfo info = userInfos.get(position);
		String _idString = info.getWp_member_info_id() == null ? info.getWp_friends_info_id() : info.getWp_member_info_id();
		if (org_id == null || dpm_id == null) {
			dismissWaitDialog();
			return;
		}
		OrganizationController.removeMemberFromDPM(this, org_id, dpm_id, _idString, new Listener<Integer, String>() {
			@Override
			public void onCallBack(Integer status, String reply) {
				if (status != 1) {
					String msg = reply == null ? "操作失败" : reply;
					QLToastUtils.showToast(DPMDetailsForEditActivity.this, msg + "!!");

				}
				mAdapter.removeItemAt(position);
				userInfos.remove(position);
				mAdapter.notifyDataSetChanged();
				dismissWaitDialog();
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (position < userInfos.size() - 1) {

		} else {
			if (!isAdmin) {
				QLToastUtils.showToast(DPMDetailsForEditActivity.this, "你不是管理员！");
				return;
			}

			String _selectedItems_str = null;
			if (userInfos.size() > 0) {
				Gson gson = new Gson();
				_selectedItems_str = gson.toJson(userInfos);
			}
			// Intent intent = new Intent();
			// intent.setClass(DPMDetailsForEditActivity.this,
			// FriendsListActivity.class);
			// intent.putExtra(FriendsListActivity.kTITLE, "分组成员");
			// intent.putExtra(FriendsListActivity.kSELECTED_ITEM_MODEL, true);
			// intent.putExtra(FriendsListActivity.kSELECTED_ITEMS_JSON_STRING,
			// _selectedItems_str);
			// startActivityForResult(intent,
			// FriendsListActivity.kSELECT_ITEMS);
			Intent intent = new Intent(DPMDetailsForEditActivity.this, SelectMemberToDPMEDActivity.class);
			intent.putExtra(SelectMemberToDPMEDActivity.SELECTOR_ORG_ID, org_id);
			intent.putExtra(SelectMemberToDPMEDActivity.kSELECTED_ITEM_MODEL, true);
			startActivityForResult(intent, SelectMemberToDPMEDActivity.kSELECT_ITEMS);

		}

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.delect_dpm) {

			if (!isAdmin) {
				QLToastUtils.showToast(DPMDetailsForEditActivity.this, "你不是管理员！");
				return;
			}

			View view = LayoutInflater.from(DPMDetailsForEditActivity.this).inflate(R.layout.dialog_updata_app_layout, null);
			TextView messageTv = (TextView) view.findViewById(R.id.message_tv);
			Button yesBtn = (Button) view.findViewById(R.id.yes_btn);
			Button noBtn = (Button) view.findViewById(R.id.no_btn);
			yesBtn.setText("是");
			yesBtn.setTextColor(Color.parseColor("#000000"));
			noBtn.setText("否");

			messageTv.setText("确定解散分组?");
			final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle("提示");
			// alertDialog.setMessage("确定解散分组?");
			alertDialog.setView(view);
			alertDialog.setIcon(0);

			yesBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					removeDPM(org_id, dpm_id);
					alertDialog.dismiss();
				}
			});
			noBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					alertDialog.dismiss();

				}
			});

			alertDialog.show();
		}
		if (id == R.id.dpm_name) {
			if (!isAdmin) {
				QLToastUtils.showToast(DPMDetailsForEditActivity.this, "你不是管理员！");
				return;
			}
			showUpdataDPMNameDialog(dpm_name_tv.getText().toString());
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// if (resultCode == Activity.RESULT_OK && requestCode ==
		// FriendsListActivity.kSELECT_ITEMS) {
		// if (data != null) {
		// String datas =
		// data.getStringExtra(FriendsListActivity.kSELECTED_ITEMS_JSON_STRING);
		// try {
		// Gson gson = new Gson();
		// List<FriendInfo> _items = gson.fromJson(datas, new
		// TypeToken<List<FriendInfo>>() {
		// }.getType());
		// setDPMMember(_items);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		// }
		if (resultCode == Activity.RESULT_OK && requestCode == SelectMemberToDPMEDActivity.kSELECT_ITEMS) {
			if (data != null) {
				String datas = data.getStringExtra(SelectMemberToDPMEDActivity.kSELECTED_ITEMS_JSON_STRING);
				try {
					Gson gson = new Gson();
					List<FriendInfo> _items = gson.fromJson(datas, new TypeToken<List<FriendInfo>>() {
					}.getType());
					setDPMMember(_items);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	// 编辑分组名称
	private void showUpdataDPMNameDialog(String message) {
		View contentView = LayoutInflater.from(this).inflate(R.layout.view_edit_card_property, null);
		final EditText eText = (EditText) contentView.findViewById(R.id.edit_card_property_et);
		eText.setText(message);
		eText.setSelection(eText.getText().length());
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("修改名称");
		alertDialog.setView(contentView);
		alertDialog.setIcon(0);
		alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "确认", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				updateDPM(org_id, dpm_id, eText.getText().toString());
			}
		});
		alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		alertDialog.show();

	}

	private void setDPMMember(List<FriendInfo> members) {
		if (members == null || members.size() < 1) {
			return;
		}
		StringBuilder sBuilder = new StringBuilder();
		for (int idx = 0; idx < members.size(); idx++) {
			FriendInfo _info = members.get(idx);
			sBuilder.append(_info.getWp_friends_info_id());
			if (idx != members.size() - 1) {
				sBuilder.append(",");
			}
		}
		// QLToastUtils.showToast(DPMDetailsForEditActivity.this,
		// sBuilder.toString());
		OrganizationController.addMemberToDPM(this, org_id, dpm_id, sBuilder.toString(), new Listener<Integer, String>() {
			@Override
			public void onCallBack(Integer status, String reply) {
				if (status != 1) {
					String msg = reply == null ? "添加失败" : reply;
					QLToastUtils.showToast(DPMDetailsForEditActivity.this, msg);
					return;
				}
				getMemberList();
			}
		});
	}

	// 获取分组所有成员
	private void getMemberList() {
		OrganizationController.getDPMMemberList(this, org_id, dpm_id, new Listener<Void, List<UserInfo>>() {
			@Override
			public void onCallBack(Void status, List<UserInfo> reply) {
				userInfos.clear();
				if (reply != null && reply.size() > 0) {
					for (UserInfo info : reply) {
						if (info.getPhone() != null && !TextUtils.isEmpty(info.getPhone())) {
							userInfos.add(info);
						}
					}
					// userInfos.addAll(reply);
				}
				UserInfo info = new UserInfo();
				info.setDPMLastMenber(true);
				userInfos.add(info);
				changeUserInfoDPMStatus(true);

				mAdapter.setDatas(userInfos);
				countGridViewHeight();
			}
		});
	}

	// 获取分组列表
	private void getDPMList() {
		OrganizationController.getMyDPMList(this, org_id, new Listener<Void, List<DepartmentInfo>>() {

			@Override
			public void onCallBack(Void status, List<DepartmentInfo> reply) {
				if (reply == null || reply.size() < 1) {
					return;
				}
				for (DepartmentInfo info : reply) {
					if (dpm_id.equals(info.getDpm_id())) {
						dpm_name = info.getDpm_name();
						dpm_name_tv.setText(dpm_name);
						break;
					}
				}

			}
		});
	}

	// 修改分组的信息
	private void updateDPM(String org_id, String departmentId, final String nameString) {
		if (nameString == null || TextUtils.isEmpty(nameString)) {
			QLToastUtils.showToast(DPMDetailsForEditActivity.this, "分组名称不能为空！");
			return;
		}
		showWaitDialog();
		OrganizationController.updateDepartment(DPMDetailsForEditActivity.this, org_id, dpm_id, nameString, new Listener<Boolean, String>() {

			@Override
			public void onCallBack(Boolean status, String reply) {
				dismissWaitDialog();
				if (status) {
					dpm_name_tv.setText(nameString);

				}
			}
		});

	}

	// 获取组织所有人信息，筛选出管理员
	private void getMyOrgMember() {
		OrganizationController.getOrgMemberList(this, org_id, new Listener<Void, List<OrgRequestMemberInfo>>() {

			@Override
			public void onCallBack(Void status, List<OrgRequestMemberInfo> reply) {
				if (reply == null || reply.size() < 1) {
					return;
				}
				String idString = UserManagerController.getCurrUserInfo().getWp_member_info_id();
				for (OrgRequestMemberInfo info : reply) {
					if (idString.equals(info.getWp_member_info_id())) {
						if (info.isIsadmin()) {
							isAdmin = info.isIsadmin();
						}

					}
				}
				if (!isAdmin) {
					titleHandler.getRightLayout().setVisibility(View.GONE);
				}

			}
		});

	}
	// // 添加分组
	// private void addDPM(final String org_id, String name) {
	// showWaitDialog();
	// OrganizationController.addDPM(this, org_id, name, new Listener<String,
	// DepartmentInfo>() {
	// @Override
	// public void onCallBack(String status, DepartmentInfo reply) {
	//
	// dismissWaitDialog();
	// if (status != null) {
	// String msg = status == null ? "添加分组失败" : status;
	// QLToastUtils.showToast(DPMDetailsForEditActivity.this, msg);
	// return;
	// }
	// }
	// });
	// }

}
