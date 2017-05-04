package app.logic.activity.org;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ql.activity.customtitle.ActActivity;
import org.ql.activity.customtitle.DefaultHandler;
import org.ql.utils.image.QLAsyncImage;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.squareup.picasso.Picasso;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import app.config.http.HttpConfig;
import app.logic.activity.ActTitleHandler;
import app.logic.adapter.YYBaseListAdapter;
import app.logic.controller.OrganizationController;
import app.logic.controller.UserManagerController;
import app.logic.pojo.OrganizationInfo;
import app.utils.common.Listener;
import app.utils.helpers.ImagePickerHelper;
import app.utils.image.QLImageHelper;
import app.yy.geju.R;

/**
 * 
 * SiuJiYung create at 2016年6月17日 下午3:31:23
 * 
 */

public class OrganizationDetailActivity extends ActActivity implements OnClickListener, OnItemClickListener {

	public static final String kOrganizationInfoKey = "kOrganizationInfoKey";
	public static final String SHOWVIEW = "SHOWFOOTVIEW";

	private ListView mListView;
	private YYBaseListAdapter<Map<String, String>> mAdapter;
	private OrganizationInfo orgInfo;
	private QLAsyncImage imgLoader;
	private List<OrganizationInfo> myOrgList;
	private ImageView logoView;
	private ActTitleHandler mHandler;
	private boolean editStatus = false;
	private boolean isAdmin = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHandler = new ActTitleHandler();
		setAbsHandler(mHandler);
		setContentView(R.layout.activity_review_org_info);

		setTitle("组织信息");
		mHandler.replaseLeftLayout(this, true);
		mHandler.getLeftLayout().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mHandler.addRightView(LayoutInflater.from(this).inflate(R.layout.title_rightview_layout, null), true);
		((TextView) mHandler.getRightLayout().findViewById(R.id.title_right_tv)).setText("编辑");
		mHandler.getRightLayout().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((TextView) mHandler.getRightLayout().findViewById(R.id.title_right_tv)).setText("保存");
				editStatus = true;
				List<Map<String, String>> displayInfos = getOrgDisplayInfo(orgInfo);
				mAdapter.setDatas(displayInfos);
			}
		});

		mAdapter = new YYBaseListAdapter<Map<String, String>>(this) {
			@Override
			public View createView(int position, View convertView, ViewGroup parent) {

				if (convertView == null) {
					convertView = LayoutInflater.from(mContext).inflate(R.layout.cell_org_info, null);
					saveView("cell_org_info_title_view", R.id.cell_org_info_title_view, convertView);
					saveView("cell_org_info_content_view", R.id.cell_org_info_content_view, convertView);
				}
				Map<String, String> info = getItem(position);
				if (info != null) {
					String title_str = (String) ((Object[]) info.keySet().toArray())[0];
					String content_str = info.get(title_str);

					setTextToViewText(title_str, "cell_org_info_title_view", convertView);

					if (content_str.equals("true")) {
						convertView.findViewById(R.id.cell_org_info_iv).setVisibility(View.VISIBLE);
						convertView.findViewById(R.id.cell_org_info_content_view).setVisibility(View.GONE);
					} else {
						// setTextToViewText(content_str,
						// "cell_org_info_content_view", convertView);
						EditText editText = getViewForName("cell_org_info_content_view", convertView);
						editText.setText(content_str);
						if (!editStatus) {
							editText.setFocusable(false);
						} else {
							editText.setFocusableInTouchMode(true);
						}

					}

				}
				return convertView;
			}
		};

		String org_json_info = getIntent().getStringExtra(kOrganizationInfoKey);

		Gson gson = new Gson();
		try {
			orgInfo = gson.fromJson(org_json_info, OrganizationInfo.class);
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		}

		List<Map<String, String>> displayInfos = getOrgDisplayInfo(orgInfo);
		mAdapter.setDatas(displayInfos);
		if (orgInfo.getIsadmin() == 0) {
			mHandler.getRightLayout().setVisibility(View.GONE);
		}

		mListView = (ListView) findViewById(R.id.review_info_list);

		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		/*
		 * if (orgInfo.getIsMember() == 0) { showFootView(); }
		 */
		logoView = (ImageView) findViewById(R.id.org_review_logo_view);
		logoView.setOnClickListener(this);
		TextView nameView = (TextView) findViewById(R.id.org_review_name_view);

		String logo_url = HttpConfig.getUrl(orgInfo.getOrg_logo_url());

		imgLoader = new QLAsyncImage(this);

		imgLoader.loadImage(logo_url, logoView, R.drawable.default_user_icon);
		nameView.setText(orgInfo.getOrg_name());
		getMyOrgList();
	}

	private void getMyOrgList() {
		OrganizationController.getMyOrganizationList(this, new Listener<Void, List<OrganizationInfo>>() {
			@Override
			public void onCallBack(Void status, List<OrganizationInfo> reply) {
				myOrgList = reply;
				boolean showJoinBtn = true;
				if (myOrgList != null && orgInfo != null) {
					for (OrganizationInfo organizationInfo : myOrgList) {
						if (orgInfo.getOrg_id().equals(organizationInfo.getOrg_id())) {
							showJoinBtn = false;
							break;
						}
					}
				}
				if (showJoinBtn) {
					showFootView();
				}
			}
		});
	}

	private List<Map<String, String>> getOrgDisplayInfo(OrganizationInfo info) {
		if (info == null) {
			return null;
		}
		ArrayList<Map<String, String>> tmpList = new ArrayList<Map<String, String>>();
		HashMap<String, String> tmpInfoMap = null;

		tmpInfoMap = new HashMap<String, String>();
		tmpInfoMap.put("组织介绍", info.getOrg_des());
		tmpList.add(tmpInfoMap);

		tmpInfoMap = new HashMap<String, String>();
		tmpInfoMap.put("组织成员", String.valueOf(info.getNumber()));
		tmpList.add(tmpInfoMap);

		tmpInfoMap = new HashMap<String, String>();
		tmpInfoMap.put("办公地址", info.getOrg_addr());
		tmpList.add(tmpInfoMap);

		tmpInfoMap = new HashMap<String, String>();
		tmpInfoMap.put("联系电话", info.getOrg_tel());
		tmpList.add(tmpInfoMap);

		tmpInfoMap = new HashMap<String, String>();
		tmpInfoMap.put("电子邮箱", info.getOrg_email());
		tmpList.add(tmpInfoMap);

		if (info.getIsadmin() == 1) {
			tmpInfoMap = new HashMap<String, String>();
			tmpInfoMap.put("申请列表", "true");
			tmpList.add(tmpInfoMap);
			isAdmin = true;
		}

		return tmpList;
	}

	// 申请加入组织
	private void JoinOrganization() {
		if (orgInfo == null) {
			return;
		}
		showWaitDialog();
		OrganizationController.joinOrganization(this, orgInfo.getOrg_id(), "", new Listener<Boolean, String>() {
			@Override
			public void onCallBack(Boolean status, String reply) {
				dismissWaitDialog();
				if (status.booleanValue() == true) {
					Intent intent = new Intent();
					intent.setClass(OrganizationDetailActivity.this, RequestJoinOrganizationActivity.class);
					startActivity(intent);
					finish();
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.footview_btn) {
			// 加入组织
			JoinOrganization();
		}
		if (id == R.id.org_review_logo_view) {
			showWaitDialog();
			String user_id = UserManagerController.getCurrUserInfo().getId();
			String org_id = orgInfo.getOrg_id();
			replaseHeadImageView(org_id, user_id);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (position == 5) {
			// JoinOrganization();
			Intent intent = new Intent();
			intent.setClass(OrganizationDetailActivity.this, RequestFormListActivity.class);
			intent.putExtra(RequestFormListActivity.GET_JOINREQUEST_KRY, orgInfo.getOrg_id());
			startActivity(intent);
			return;
		}
		if (position == 2) {

		}

	}

	// 更换组织头像
	private void replaseHeadImageView(final String org_id, final String user_info) {
		if (orgInfo.getIsMember() == 1) {
			ImagePickerHelper helper = ImagePickerHelper.createNewImagePickerHelper(this);
			helper.setImageSourceType(ImagePickerHelper.kImageSource_UserSelect);
			helper.setOnReciveImageListener(new Listener<Void, String>() {

				@Override
				public void onCallBack(Void status, String reply) {
					if (reply == null) {
						return;
					}
					// File imgFile = new File(reply);
					// asyncImage.loadImage(reply, logoView);
					// Picasso.with(OrganizationDetailActivity.this).load(imgFile).into(logoView);
					// imgLoader.loadImage(reply, logoView,
					// R.drawable.default_user_icon);
					Bitmap btmBitmap = QLImageHelper.readBitmap(reply, logoView.getWidth() * 2, logoView.getHeight() * 2);
					logoView.setImageBitmap(btmBitmap);
					OrganizationController.replaceOrgInfoLogo(OrganizationDetailActivity.this, org_id, user_info, reply, new Listener<Boolean, String>() {

						@Override
						public void onCallBack(Boolean status, String reply) {
							dismissWaitDialog();
							if (status) {
								// Toast.makeText(OrganizationDetailActivity.this,
								// "Logo更换成功!", Toast.LENGTH_SHORT).show();
							}

						}
					});
				}
			});
			helper.openCamera();

		}
	}

	private void showFootView() {
		View footview = LayoutInflater.from(this).inflate(R.layout.org_review_footview, null);
		Button btn = (Button) footview.findViewById(R.id.footview_btn);
		btn.setOnClickListener(this);
		mListView.addFooterView(footview);
	}

	//

	private void getOrgInfo() {

	}

}
