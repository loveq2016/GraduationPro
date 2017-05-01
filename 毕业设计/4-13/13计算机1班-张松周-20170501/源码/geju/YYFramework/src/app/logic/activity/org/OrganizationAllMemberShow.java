package app.logic.activity.org;

import java.util.ArrayList;
import java.util.List;
import org.ql.utils.QLToastUtils;
import org.ql.views.listview.QLXListView.IXListViewListener;
import com.google.gson.Gson;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import app.config.http.HttpConfig;
import app.logic.activity.ActTitleHandler;
import app.logic.activity.InitActActivity;
import app.logic.activity.user.PreviewFriendsInfoActivity;
import app.logic.adapter.YYBaseListAdapter;
import app.logic.controller.OrganizationController;
import app.logic.pojo.OrgRequestMemberInfo;
import app.logic.pojo.UserInfo;
import app.utils.common.Listener;
import app.view.YYListView;
/*
 * GZYY    2016-10-18  下午2:26:46
 */
import app.yy.geju.R;

public class OrganizationAllMemberShow extends InitActActivity implements IXListViewListener, OnItemClickListener {
	public static final String ORG_ID = "ORG_ID";
	private ActTitleHandler actTitleHandler;
	private YYListView listView;
	private String org_id;

	private YYBaseListAdapter<OrgRequestMemberInfo> mAdapter = new YYBaseListAdapter<OrgRequestMemberInfo>(this) {

		@Override
		public View createView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(OrganizationAllMemberShow.this).inflate(R.layout.item_dpm_memberinfo, null);
				saveView("dpm_memberinfo_header_iv", R.id.dpm_memberinfo_header_iv, convertView);
				saveView("dpm_memberinfo_name_tv", R.id.dpm_memberinfo_name_tv, convertView);
			}
			OrgRequestMemberInfo info = mAdapter.getItem(position);
			if (info != null) {
				if(!TextUtils.isEmpty(info.getFriend_name())){
					setTextToViewText(info.getFriend_name(), "dpm_memberinfo_name_tv", convertView);
				}else{
					setTextToViewText(info.getNickName(), "dpm_memberinfo_name_tv", convertView);
				}
				String url = HttpConfig.getUrl(info.getPicture_url());
				ImageView imageView = getViewForName("dpm_memberinfo_header_iv", convertView);
				setImageToImageViewCenterCrop(url, "dpm_memberinfo_header_iv", R.drawable.default_user_icon, convertView);
			}
			return convertView;

		}
	};

	@Override
	protected void initActTitleView() {
		actTitleHandler = new ActTitleHandler();
		setAbsHandler(actTitleHandler);

	}

	@Override
	protected void initView(Bundle savedInstanceState) {
		setContentView(R.layout.activity_allmember_show);
		
		setTitle("所有成员");
		actTitleHandler.replaseLeftLayout(this, true);
		actTitleHandler.getLeftLayout().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		listView = (YYListView) findViewById(R.id.allmember_listview);

	}

	@Override
	protected void initData() {

		listView.setOnItemClickListener(this);

		listView.setPullRefreshEnable(true);
		listView.setPullLoadEnable(false, true);
		listView.setXListViewListener(this);

		listView.setAdapter(mAdapter);

		org_id = getIntent().getStringExtra(ORG_ID);
		getOrgMemberList(org_id);
	}

	private void getOrgMemberList(String org_id) {
		OrganizationController.getOrgMemberList(this, org_id, new Listener<Void, List<OrgRequestMemberInfo>>() {

			@Override
			public void onCallBack(Void status, List<OrgRequestMemberInfo> reply) {
				listView.stopLoadMore();
				listView.stopRefresh();

				if (reply == null || reply.size() < 1) {
					return;
				}
				List<OrgRequestMemberInfo> tempList = new ArrayList<OrgRequestMemberInfo>();
				for (OrgRequestMemberInfo info : reply) {
					if (info.getPhone() != null && !TextUtils.isEmpty(info.getPhone())) {
						tempList.add(info);
					}
				}
				mAdapter.setDatas(tempList);

			}
		});
	}

	@Override
	public void onRefresh() {
		getOrgMemberList(org_id);
	}

	@Override
	public void onLoadMore() {
		getOrgMemberList(org_id);

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		OrgRequestMemberInfo info = mAdapter.getItem(position - 1);
		if (info == null) {
			return;
		}
		// QLToastUtils.showToast(this, info.getNickName());
		Intent intent = new Intent(this, PreviewFriendsInfoActivity.class);
		intent.putExtra(PreviewFriendsInfoActivity.FROMORG , "FROMORG");
		intent.putExtra(PreviewFriendsInfoActivity.kUSER_MEMBER_ID, info.getWp_member_info_id());
		startActivity(intent);
	}

}
