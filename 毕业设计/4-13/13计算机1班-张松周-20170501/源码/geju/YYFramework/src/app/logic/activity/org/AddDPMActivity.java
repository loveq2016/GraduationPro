package app.logic.activity.org;

import java.util.ArrayList;
import java.util.List;

import org.ql.app.alert.AlertDialog;
import org.ql.utils.QLToastUtils;

import cn.jpush.android.data.l;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.Activity;
import android.content.ClipData.Item;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
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
import app.logic.adapter.YYBaseListAdapter;
import app.logic.controller.OrganizationController;
import app.logic.pojo.DepartmentInfo;
import app.logic.pojo.FriendInfo;
import app.logic.pojo.UserInfo;
import app.utils.common.Listener;
/*
 * GZYY    2016-9-23  上午9:43:04
 */
import app.yy.geju.R;

public class AddDPMActivity extends InitActActivity implements OnItemClickListener, OnClickListener {

	public static final String ORG_ID = "ORG_ID";

	private ActTitleHandler titleHandler;

	private List<UserInfo> userInfos = new ArrayList<UserInfo>();
	private List<FriendInfo> friendInfos = new ArrayList<FriendInfo>();
	private List<DepartmentInfo> frishDPMList = new ArrayList<DepartmentInfo>();
	private List<DepartmentInfo> lastDPMList = new ArrayList<DepartmentInfo>();
	private GridView mGridView;
	private Button createDPM_btn;
	private TextView dpm_name_tv;
	private DepartmentInfo tempDPMInfo;

	private String org_id;
	private String dpm_id;

	@Override
	protected void initActTitleView() {
		titleHandler = new ActTitleHandler();
		setAbsHandler(titleHandler);

	}

	@Override
	protected void initView(Bundle savedInstanceState) {
		setContentView(R.layout.activity_dpm_details_for_edit);

		setTitle("添加部门");
		titleHandler.replaseLeftLayout(this, true);
		titleHandler.getLeftLayout().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		mGridView = (GridView) findViewById(R.id.all_menber_gv);
		createDPM_btn = (Button) findViewById(R.id.delect_dpm);
		dpm_name_tv = (TextView) findViewById(R.id.dpm_name);

		mGridView.setOnItemClickListener(this);
		createDPM_btn.setOnClickListener(this);
		dpm_name_tv.setOnClickListener(this);

	}

	@Override
	protected void initData() {
		createDPM_btn.setText("创建");

		org_id = getIntent().getStringExtra(ORG_ID);

		addDefaultMemberInfo();
		mGridView.setAdapter(mAdapter);

		mAdapter.setDatas(userInfos);
		countGridViewHeight();
	}

	private YYBaseListAdapter<UserInfo> mAdapter = new YYBaseListAdapter<UserInfo>(this) {

		@Override
		public View createView(final int position, View convertView, ViewGroup parent) {

			if (getItemViewType(position) == 0) {
				if (convertView == null) {
					convertView = LayoutInflater.from(AddDPMActivity.this).inflate(R.layout.item_gridview_dpm_member, null);
					saveView("iv1", R.id.iv1, convertView);
					saveView("funcation_item_title_tv", R.id.funcation_item_title_tv, convertView);
					saveView("delect_iv", R.id.delect_iv, convertView);
				}

				UserInfo info = getItem(position);
				if (info != null) {
					setTextToViewText(info.getNickName(), "funcation_item_title_tv", convertView);
					String url = HttpConfig.getUrl(info.getPicture_url());
					ImageView imageView = (ImageView) getViewForName("iv1", convertView);
					setImageToImageViewCenterCrop(url, "iv1", R.drawable.default_user_icon, convertView);
					View view = getViewForName("delect_iv", convertView);
					view.setVisibility(View.VISIBLE);
					view.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
						}
					});
				}

			} else {
				if (convertView == null) {
					convertView = LayoutInflater.from(AddDPMActivity.this).inflate(R.layout.item_gridview_dpm_member, null);
					saveView("iv1", R.id.iv1, convertView);
				}
				ImageView imageView = (ImageView) getViewForName("iv1", convertView);
				imageView.setImageDrawable(getResources().getDrawable(R.drawable.add_dpm_member_btn));

			}

			return convertView;
		}

		public int getItemViewType(int position) {
			UserInfo info = mAdapter.getItem(position);

			if (info != null && info.isDPMLastMenber()) {
				return 1;
			}
			return 0;
		}

		public int getViewTypeCount() {
			return 2;
		}

	};

	// 填充一个数据
	private void addDefaultMemberInfo() {
		UserInfo info = new UserInfo();
		info.setDPMLastMenber(true);
		userInfos.add(info);
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

	private List<UserInfo> friendInfosToUserInfos(List<FriendInfo> friendInfos) {
		List<UserInfo> userInfos = new ArrayList<UserInfo>();
		for (FriendInfo info : friendInfos) {
			UserInfo userInfo = new UserInfo();
			userInfo.setNickName(info.getNickName());
			userInfo.setPhone(info.getPhone());
			userInfo.setPicture_url(info.getPicture_url());
			userInfo.setSex(info.getSex());
			userInfo.setWp_member_info_id(info.getWp_friends_info_id());

			userInfos.add(userInfo);
		}
		UserInfo info = new UserInfo();
		info.setDPMLastMenber(true);
		userInfos.add(info);
		return userInfos;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		UserInfo info = mAdapter.getItem(position);
		if (info == null) {
			return;
		}
		if (info.isDPMLastMenber()) {
			String _selectedItems_str = null;
			if (userInfos.size() > 0) {
				Gson gson = new Gson();
				_selectedItems_str = gson.toJson(userInfos);
			}
			Intent intent = new Intent();
			intent.setClass(AddDPMActivity.this, SelectMemberToDPMActivity.class);
			intent.putExtra(SelectMemberToDPMActivity.kTITLE, "部门成员");
			intent.putExtra(SelectMemberToDPMActivity.kSELECTED_ITEM_MODEL, true);
			intent.putExtra(SelectMemberToDPMActivity.kSELECTED_ITEMS_JSON_STRING, _selectedItems_str);
			intent.putExtra(SelectMemberToDPMActivity.KORG_ID, org_id);
			startActivityForResult(intent, SelectMemberToDPMActivity.kSELECT_ITEMS);
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK && requestCode == SelectMemberToDPMActivity.kSELECT_ITEMS) {
			if (data != null) {
				String datas = data.getStringExtra(SelectMemberToDPMActivity.kSELECTED_ITEMS_JSON_STRING);
				try {
					Gson gson = new Gson();
					List<FriendInfo> _items = gson.fromJson(datas, new TypeToken<List<FriendInfo>>() {
					}.getType());
					// QLToastUtils.showToast(AddDPMActivity.this, datas);
					friendInfos.clear();
					friendInfos.addAll(_items);
					mAdapter.setDatas(friendInfosToUserInfos(_items));

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.dpm_name:
			showDialog(dpm_name_tv.getText().toString());
			break;
		case R.id.delect_dpm:

			addDPM(org_id, dpm_name_tv.getText().toString());

			break;

		default:
			break;
		}

	}

	// 显示编辑部门名称
	private void showDialog(String message) {

		View contentView = LayoutInflater.from(this).inflate(R.layout.view_edit_card_property, null);
		View layoutView = contentView.findViewById(R.id.foot_view_dialog);
		layoutView.setVisibility(View.VISIBLE);
		Button trueButton = (Button) contentView.findViewById(R.id.true_btn);
		Button cancleButton = (Button) contentView.findViewById(R.id.cancel_btn);

		final EditText eText = (EditText) contentView.findViewById(R.id.edit_card_property_et);
		eText.setText(message);
		final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("编辑名称");
		alertDialog.setView(contentView);
		alertDialog.setIcon(0);

		trueButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String msg = eText.getText().toString();
				if (msg == null || TextUtils.isEmpty(msg)) {
					QLToastUtils.showToast(AddDPMActivity.this, "部门名称不能为空");
					return;
				}
				dpm_name_tv.setText(msg);
				alertDialog.dismiss();
			}
		});
		cancleButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
			}
		});
		alertDialog.show();
	}

	// 创建部门
	private void addDPM(final String org_id, final String name) {
		if (name == null || TextUtils.isEmpty(name)) {
			QLToastUtils.showToast(AddDPMActivity.this, "部门名不能为空！");
			return;
		}
		showWaitDialog();
		OrganizationController.getMyDPMList(AddDPMActivity.this, org_id, new Listener<Void, List<DepartmentInfo>>() {

			@Override
			public void onCallBack(Void status, List<DepartmentInfo> reply) {
				if (reply == null || reply.size() < 1) {
					// 第一次添加部门
					addDPMFrist(name, friendInfos);
					return;
				}
				frishDPMList.addAll(reply);
				OrganizationController.addDPM(AddDPMActivity.this, org_id, name, new Listener<String, DepartmentInfo>() {
					@Override
					public void onCallBack(String status, DepartmentInfo reply) {
						if (status == null) {
							String msg = status == null ? "添加部门失败" : status;
							QLToastUtils.showToast(AddDPMActivity.this, msg);
							dismissWaitDialog();
							return;
						}
						setDPMMember(friendInfos);

					}
				});
			}
		});

	}

	private void setDPMMember(List<FriendInfo> members) {
		if (members == null || members.size() < 1) {
			QLToastUtils.showToast(AddDPMActivity.this, "部门创建成功");
			finish();
			return;
		}
		final StringBuilder sBuilder = new StringBuilder();
		for (int idx = 0; idx < members.size(); idx++) {
			FriendInfo _info = members.get(idx);
			sBuilder.append(_info.getWp_friends_info_id());
			if (idx != members.size() - 1) {
				sBuilder.append(",");
			}
		}
		// QLToastUtils.showToast(DPMDetailsForEditActivity.this,
		// sBuilder.toString());

		OrganizationController.getMyDPMList(AddDPMActivity.this, org_id, new Listener<Void, List<DepartmentInfo>>() {

			@Override
			public void onCallBack(Void status, List<DepartmentInfo> reply) {
				if (reply == null || reply.size() < 1) {
					return;
				}
				lastDPMList.addAll(reply);
				if (frishDPMList.size() >= lastDPMList.size()) {
					dismissWaitDialog();
					QLToastUtils.showToast(AddDPMActivity.this, "创建部门失败！");
					return;
				}

				for (int i = 0; i < lastDPMList.size(); i++) {
					tempDPMInfo = lastDPMList.get(i);
					for (DepartmentInfo fInfo : frishDPMList) {
						if (tempDPMInfo.getDpm_id().equals(fInfo.getDpm_id())) {
							break;
						}
					}
				}
				tempDPMInfo = reply.get(reply.size() - 1);

				OrganizationController.addMemberToDPM(AddDPMActivity.this, org_id, tempDPMInfo.getDpm_id(), sBuilder.toString(), new Listener<Integer, String>() {
					@Override
					public void onCallBack(Integer status, String reply) {
						dismissWaitDialog();
						if (status != 1) {
							String msg = reply == null ? "添加失败" : reply;
							QLToastUtils.showToast(AddDPMActivity.this, msg);
							return;
						}
						finish();
						// getMemberList();
					}
				});
			}
		});

	}

	// 获取第一次获取部门列表,创建部门后，再次获取部门列表，进行对比，获取最近创建的部门ID，然后根据部门的ID，将成员拉进去
	private void getDPMAndJoinList() {
		OrganizationController.getMyDPMList(AddDPMActivity.this, org_id, new Listener<Void, List<DepartmentInfo>>() {

			@Override
			public void onCallBack(Void status, List<DepartmentInfo> reply) {
				if (reply == null || reply.size() < 1) {
					return;
				}
				frishDPMList.addAll(reply);

			}
		});

	}

	// 解决第一次增加部门，拉人失败
	private void addDPMFrist(String name, final List<FriendInfo> members) {
		OrganizationController.addDPM(AddDPMActivity.this, org_id, name, new Listener<String, DepartmentInfo>() {
			@Override
			public void onCallBack(String status, DepartmentInfo reply) {
				if (status != null && status.length() < 7) {
					String msg = status == null ? "添加部门失败" : status;
					QLToastUtils.showToast(AddDPMActivity.this, msg);
					return;
				}

				if (members == null || members.size() < 1) {
					QLToastUtils.showToast(AddDPMActivity.this, "部门创建成功");
					finish();
					return;
				}
				final StringBuilder sBuilder = new StringBuilder();
				for (int idx = 0; idx < members.size(); idx++) {
					FriendInfo _info = members.get(idx);
					sBuilder.append(_info.getWp_friends_info_id());
					if (idx != members.size() - 1) {
						sBuilder.append(",");
					}
				}
//				if (reply == null) {
//					QLToastUtils.showToast(AddDPMActivity.this, "部门创建成功,添加人失败");
//					finish();
//					return;
//				}
				OrganizationController.addMemberToDPM(AddDPMActivity.this, org_id, status, sBuilder.toString(), new Listener<Integer, String>() {
					@Override
					public void onCallBack(Integer status, String reply) {
						dismissWaitDialog();
						if (status != 1) {
							String msg = reply == null ? "添加失败" : reply;
							QLToastUtils.showToast(AddDPMActivity.this, msg);
							return;
						}
						finish();
					}
				});

			}
		});
	}

}
