package app.logic.activity.org;

import java.util.List;

import org.ql.activity.customtitle.ActActivity;
import org.ql.views.listview.QLXListView;
import org.ql.views.listview.QLXListView.IXListViewListener;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import app.config.http.HttpConfig;
import app.logic.activity.ActTitleHandler;
import app.logic.adapter.YYBaseListAdapter;
import app.logic.controller.OrganizationController;
import app.logic.pojo.OrganizationInfo;
import app.utils.common.Listener;
import app.yy.geju.R;

/**
 * 
 * SiuJiYung create at 2016年8月11日 下午5:20:04
 * 
 * 搜索、加入组织
 */

public class JoinOrganizationActivity extends ActActivity implements OnItemClickListener, IXListViewListener, OnEditorActionListener {

	private ActTitleHandler titleHandler = new ActTitleHandler();
	private QLXListView mListView;
	private EditText searchEditText;

	private YYBaseListAdapter<OrganizationInfo> mAdapter = new YYBaseListAdapter<OrganizationInfo>(this) {
		@Override
		public View createView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.item_row_orginfo3, null);
				saveView("item_head_iv", R.id.item_head_iv, convertView);
				saveView("item_name_tv", R.id.item_name_tv, convertView);
				saveView("item_status_tv", R.id.item_status_tv, convertView);
			}
			OrganizationInfo info = (OrganizationInfo) getItem(position);
			if (info != null) {
				String url = HttpConfig.getUrl(info.getOrg_logo_url());
				ImageView headIV = getViewForName("item_head_iv", convertView);
				Picasso.with(JoinOrganizationActivity.this).load(url).placeholder(R.drawable.default_user_icon).error(R.drawable.default_user_icon).fit().centerCrop().into(headIV);
				setTextToViewText(info.getOrg_name(), "item_name_tv", convertView);
				TextView statusTv = getViewForName("item_status_tv", convertView);
				if (info.getOrg_status() == 10) {
					statusTv.setText("已申请");
					statusTv.setTextColor(getResources().getColor(R.color.new_textView_color));
					statusTv.setBackgroundDrawable(null);
				} else if (info.getOrg_status() == 12 ) {
					statusTv.setText("已拒绝");
//					statusTv.setTextColor(getResources().getColor(R.color.white));
//					statusTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_join_org_btn_bg));
				}else if (info.getOrg_status() == 0 ) {
					statusTv.setText("待审和");
//					statusTv.setTextColor(getResources().getColor(R.color.white));
//					statusTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_join_org_btn_bg));
				}else {
					statusTv.setText("申请");
					statusTv.setTextColor(getResources().getColor(R.color.white));
					statusTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_join_org_btn_bg));
				}

			}
			return convertView;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbsHandler(titleHandler);
		setContentView(R.layout.activity_join_org);

		titleHandler.replaseLeftLayout(this, true);
		titleHandler.getLeftLayout().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		((TextView) titleHandler.getLeftLayout().findViewById(R.id.left_tv)).setText("加入组织");
		setTitle("");

		searchEditText = (EditText) findViewById(R.id.search_edt);
		searchEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String txt = s.toString();
				startSearchOrg(txt);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {

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
		startSearchOrg("");
	}

	private void loadAllDatas() {

	}

	private void startSearchOrg(String keywork) {
		if (TextUtils.isEmpty(keywork)) {
			keywork = "组织";
		}
		OrganizationController.searchOrganizations(this, "", "", new Listener<Integer, List<OrganizationInfo>>() {
			@Override
			public void onCallBack(Integer integer, List<OrganizationInfo> reply) {
				dismissWaitDialog();
				mListView.stopLoadMore();
				mListView.stopRefresh();
				if(integer == 1){
					mAdapter.setDatas(reply);
				}
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		OrganizationInfo info = (OrganizationInfo) parent.getAdapter().getItem(position);
		if (info == null) {
			return;
		}

		startActivity(new Intent(JoinOrganizationActivity.this, SearchOrgDefailsActivity.class).putExtra(SearchOrgDefailsActivity.ORG_INFO, info ));
		// Gson gson = new Gson();
		// String org_info = gson.toJson(info);

//		showWaitDialog();
//		OrganizationController.getOrganizationInfo(JoinOrganizationActivity.this, info.getOrg_id(), new Listener<Void, List<OrganizationInfo>>() {
//
//			@Override
//			public void onCallBack(Void status, List<OrganizationInfo> reply) {
//
//				dismissWaitDialog();
//				if (reply == null || reply.size() < 1) {
//					Toast.makeText(JoinOrganizationActivity.this, "找不到改组织的信息!", Toast.LENGTH_SHORT).show();
//					return;
//				}
//				// Intent intent = new Intent();
//				// intent.setClass(JoinOrganizationActivity.this,
//				// OrganizationDetailActivity.class);
//				// OrganizationInfo info = reply.get(0);
//				// Gson gson = new Gson();
//				// String org_info = gson.toJson(info);
//				// intent.putExtra(OrganizationDetailActivity.kOrganizationInfoKey,
//				// org_info);
//				// startActivity(intent);
//				startActivity(new Intent(JoinOrganizationActivity.this, SearchOrgDefailsActivity.class).putExtra(SearchOrgDefailsActivity.ORG_INFO, reply.get(0)));
//
//			}
//		});

	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		startSearchOrg("");

	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (EditorInfo.IME_ACTION_SEARCH == actionId) {
			String keyword = v.getText().toString();
			if (keyword == null || TextUtils.isEmpty(keyword)) {
				return false;
			}
			setWaitingDialogText("搜索中...");
			showWaitDialog();
			startSearchOrg(keyword);
		}
		return false;
	}
}
