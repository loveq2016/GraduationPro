package app.logic.activity.org;

import java.util.ArrayList;
import java.util.List;
import org.ql.activity.customtitle.ActActivity;
import org.ql.views.listview.QLXListView;
import org.ql.views.listview.QLXListView.IXListViewListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import app.config.http.HttpConfig;
import app.logic.activity.ActTitleHandler;
import app.logic.adapter.YYBaseListAdapter;
import app.logic.controller.OrganizationController;
import app.logic.pojo.OrganizationInfo;
import app.utils.common.Listener;
import app.yy.geju.R;

/**
 * 
 * SiuJiYung create at 2016年6月15日 下午6:31:46
 * 
 * 组织列表
 */

public class OrganizationListActivity extends ActActivity implements OnItemClickListener, IXListViewListener {

	private QLXListView mListView;
	private EditText searchEditText;
	private String searchKey = "";

	private YYBaseListAdapter<OrganizationInfo> mAdapter = new YYBaseListAdapter<OrganizationInfo>(this) {
		@Override
		public View createView(int position, View convertView, ViewGroup parent) {

			if (getItemViewType(position) == 1) {
				if (convertView == null) {
					convertView = LayoutInflater.from(OrganizationListActivity.this).inflate(R.layout.item_row_orginfo, null);
					saveView("left_imageview", R.id.left_imageview, convertView);
					saveView("top_tv", R.id.top_tv, convertView);

				}
				OrganizationInfo info = (OrganizationInfo) getItem(position);
				if (info != null) {
					String url = HttpConfig.getUrl(info.getOrg_logo_url());

					ImageView headerView = getViewForName("left_imageview", convertView);
					setImageToImageView(url, "left_imageview", R.drawable.default_user_icon, convertView);
					setTextToViewText(info.getOrg_name(), "top_tv", convertView);

				}
			} else if (getItemViewType(position) == 0) {
				if (convertView == null) {
					convertView = LayoutInflater.from(OrganizationListActivity.this).inflate(R.layout.item0_row_orginfo, null);
					saveView("left_imageview", R.id.left_imageview, convertView);
					saveView("top_tv", R.id.top_tv, convertView);

				}
				OrganizationInfo info = (OrganizationInfo) getItem(position);
				if (info != null) {
					String url = HttpConfig.getUrl(info.getOrg_logo_url());

					ImageView headerView = getViewForName("left_imageview", convertView);
					setImageToImageView(url, "left_imageview", R.drawable.default_user_icon, convertView);
					setTextToViewText(info.getOrg_name(), "top_tv", convertView);

				}

			} else {
				if (convertView == null) {
					convertView = LayoutInflater.from(OrganizationListActivity.this).inflate(R.layout.item12_row_orginfo, null);
					saveView("left_imageview", R.id.left_imageview, convertView);
					saveView("top_tv", R.id.top_tv, convertView);

				}
				OrganizationInfo info = (OrganizationInfo) getItem(position);
				if (info != null) {
					String url = HttpConfig.getUrl(info.getOrg_logo_url());

					ImageView headerView = getViewForName("left_imageview", convertView);
					setImageToImageView(url, "left_imageview", R.drawable.default_user_icon, convertView);
					setTextToViewText(info.getOrg_name(), "top_tv", convertView);

				}

			}
			return convertView;
		}

		// 0是正在审核，10是已经通过(1)，12是拒绝(2)
		public int getItemViewType(int position) {
			OrganizationInfo orgInfo = mAdapter.getItem(position);
			if (orgInfo != null) {
				if (orgInfo.getOrg_status() == 0) {
					return 0;
				} else if (orgInfo.getOrg_status() == 10) {
					return 1;
				} else {
					return 2;
				}

			}
			return 1;
		};

		public int getViewTypeCount() {
			return 3;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActTitleHandler mHandler = new ActTitleHandler();
		setAbsHandler(mHandler);
		setContentView(R.layout.activity_friends_list);
		mHandler.setTitle("我的格局");

		mHandler.addLeftView(LayoutInflater.from(this).inflate(R.layout.title_leftview_layout, null), true);
		mHandler.getLeftLayout().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		searchEditText = (EditText) findViewById(R.id.search_et);
		searchEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				searchKey = s.toString();
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
		mListView.setPullRefreshEnable(true);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		mListView.setXListViewListener(this);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getOrgInfoList();

	}

	private void search(String key) {
		if (TextUtils.isEmpty(key)) {
			mAdapter.setDatas(orgList);
			return;
		}
		if (orgList == null || orgList.size() < 1) {
			return;
		}
		ArrayList<OrganizationInfo> tmpList = new ArrayList<OrganizationInfo>();
		for (OrganizationInfo info : orgList) {
			if (info.getOrg_name().contains(key)) {
				tmpList.add(info);
			}
		}
		mAdapter.setDatas(tmpList);
	}

	private List<OrganizationInfo> orgList = new ArrayList<OrganizationInfo>();

	private void getOrgInfoList() {
		OrganizationController.getMyOrganizationList(this, new Listener<Void, List<OrganizationInfo>>() {
			@Override
			public void onCallBack(Void status, List<OrganizationInfo> reply) {
				// orgList = reply;
				mListView.stopRefresh();
				mListView.stopLoadMore();

				if (reply != null && reply.size() > 0) {

					List<OrganizationInfo> temp10Infos = new ArrayList<OrganizationInfo>();
					List<OrganizationInfo> temp0Infos = new ArrayList<OrganizationInfo>();
					List<OrganizationInfo> temp12Infos = new ArrayList<OrganizationInfo>();
					for (OrganizationInfo info : reply) {
						if (info.getOrg_status() == 10) {
							temp10Infos.add(info);
						} else if (info.getOrg_status() == 0) {
							temp0Infos.add(info);
						} else {
							temp12Infos.add(info);
						}
					}
					temp10Infos.addAll(temp0Infos);
					temp10Infos.addAll(temp12Infos);
					orgList.clear();
					orgList = temp10Infos;
					search(searchKey);

					// mAdapter.setDatas(temp10Infos);
				}
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		OrganizationInfo info = (OrganizationInfo) mAdapter.getItem(arg2 - 1);
		if (info == null) {
			return;
		}
		if (info.getOrg_status() == 10) {
			Intent intent = new Intent();
			intent.setClass(this, DPMListActivity.class);
			intent.putExtra(DPMListActivity.kORG_ID, info.getOrg_id());
			intent.putExtra(DPMListActivity.kORG_NAME, info.getOrg_name());
			startActivity(intent);
		}

	}

	@Override
	public void onRefresh() {
		getOrgInfoList();
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub

	}
}
