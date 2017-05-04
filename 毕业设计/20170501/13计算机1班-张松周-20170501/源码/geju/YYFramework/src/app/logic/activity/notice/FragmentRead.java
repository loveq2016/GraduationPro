package app.logic.activity.notice;

import java.util.ArrayList;
import java.util.List;

import org.canson.view.swipemenulistview.SwipeMenu;
import org.canson.view.swipemenulistview.SwipeMenuCreator;
import org.canson.view.swipemenulistview.SwipeMenuItem;
import org.ql.views.listview.QLXListView.IXListViewListener;

import com.squareup.picasso.Picasso;

import cn.jpush.android.data.o;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.ActionMenuView.OnMenuItemClickListener;
import android.text.TextUtils;
/*
 * GZYY    2016-7-29  上午11:10:07
 */
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import app.config.http.HttpConfig;
import app.logic.activity.main.HomeActivity;
import app.logic.adapter.YYBaseListAdapter;
import app.logic.controller.AnnounceController;
import app.logic.controller.OrganizationController;
import app.logic.pojo.NoticeInfo;
import app.logic.pojo.OrgUnreadNumberInfo;
import app.logic.pojo.OrganizationInfo;
import app.logic.singleton.YYInterface.UpdataTitleToReadFragmentListener;
import app.logic.singleton.YYSingleton;
import app.logic.singleton.ZSZSingleton;
import app.logic.singleton.ZSZSingleton.OrgInfoInterface;
import app.logic.singleton.ZSZSingleton.OrgInfoInterfaceEd;
import app.utils.common.Listener;
import app.utils.helpers.YYUtils;
import app.view.RichTextViewActivity2;
import app.view.YYListView;
import app.yy.geju.R;

public class FragmentRead extends Fragment implements IXListViewListener, OnItemClickListener, org.canson.view.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener {

	public static final String CLICK_ITEMINFO_ED = "CLICK_ITEMINFO_ED";

	private View view;
	private YYListView mListView;

	private OrgUnreadNumberInfo organizationInfo;
	private List<NoticeInfo> mList = new ArrayList<NoticeInfo>();

	private int reStart = 0;
	private int reLimit = 25;
	private int loadStart = reLimit;
	private int loadLimit = 25;

	private YYBaseListAdapter<NoticeInfo> mAdapter = new YYBaseListAdapter<NoticeInfo>(getContext()) {

		@Override
		public View createView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_notice_unread, null);
				saveView("msg_iv", R.id.msg_iv, convertView);
				saveView("msg_title", R.id.msg_title, convertView);
				saveView("msg_user", R.id.msg_user, convertView);
				saveView("msg_time", R.id.msg_time, convertView);
			}
			NoticeInfo info = (NoticeInfo) getItem(position);
			if (info != null) {
				setTextToViewText(info.getMsg_title(), "msg_title", convertView);
				setTextToViewText(info.getMsg_creator(), "msg_user", convertView);
				setTextToViewText(ZSZSingleton.getTimeStyle(info.getMsg_create_time()), "msg_time", convertView);
				ImageView noticeIv = getViewForName("msg_iv", convertView);
				Picasso.with(getContext()).load(HttpConfig.getUrl(info.getPicture_url())).error(R.drawable.item_notice_default).fit().centerCrop().into(noticeIv);
			}

			return convertView;
		}
	};

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_read, null);

		ViewGroup paraent = (ViewGroup) view.getParent();
		if (paraent != null) {
			paraent.removeView(view);
		}
		initView();
		initData();

		return view;
	}

	private void initView() {
		mListView = (YYListView) view.findViewById(R.id.read_lv);

		SwipeMenuCreator menuCreator = new SwipeMenuCreator() {
			@Override
			public void create(SwipeMenu menu) {
				SwipeMenuItem openItem = new SwipeMenuItem(getContext());
				openItem.setBackground(R.drawable.menu_delete_bg);
				openItem.setWidth(YYUtils.dp2px(90, getContext()));
				openItem.setTitle("移除");
				openItem.setTitleSize(16);
				openItem.setTitleColor(0xfffcfcfc);
				menu.addMenuItem(openItem);
			}
		};
		mListView.setOnMenuItemClickListener(this);
		mListView.setMenuCreator(menuCreator);
		mListView.setXListViewListener(this);
		mListView.setPullLoadEnable(true, false);
		mListView.setPullRefreshEnable(true);
		mListView.setOnItemClickListener(this);

	}

	private void initData() {

		mListView.setAdapter(mAdapter);

		if (ZSZSingleton.getZSZSingleton().getOrganizationInfo() != null) {
			organizationInfo = ZSZSingleton.getZSZSingleton().getOrganizationInfo();
			getRefreshNoticeData(organizationInfo.getOrg_id());
		}

		interfaceCount();

	}

	// 接口回调更新
	private void interfaceCount() {
		YYSingleton.getInstance().setUpdataTitleToReadFragmentListener(new UpdataTitleToReadFragmentListener() {

			@Override
			public void onCallBack(OrgUnreadNumberInfo info) {
				organizationInfo = ZSZSingleton.getZSZSingleton().getOrganizationInfo();
				organizationInfo = info;
				getRefreshNoticeData(organizationInfo.getOrg_id());

			}
		});

	}

	@Override
	public void onRefresh() {
		if (organizationInfo != null) {
			getRefreshNoticeData(organizationInfo.getOrg_id());
		}
		mListView.stopLoadMore();
		mListView.stopRefresh();
	}

	@Override
	public void onLoadMore() {
		if (organizationInfo != null) {
			getRefreshNoticeData(organizationInfo.getOrg_id());
		}
		mListView.stopLoadMore();
		mListView.stopRefresh();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		NoticeInfo info = (NoticeInfo) mAdapter.getItem(position - 1);
		if (info != null) {
			Intent intent = new Intent();
			intent.setClass(getContext(), RichTextViewActivity2.class);
			intent.putExtra(RichTextViewActivity2.CLICKINFO_ID, info.getMsg_id());
			startActivity(intent);

		}
	}

	// 获取组织列表
	private void getOrgList() {
		OrganizationController.getMyOrganizationList(getContext(), new Listener<Void, List<OrganizationInfo>>() {

			@Override
			public void onCallBack(Void status, List<OrganizationInfo> reply) {
				if (reply != null && reply.size() > 0) {
					// organizationInfo = reply.get(0);
					OrgUnreadNumberInfo info = new OrgUnreadNumberInfo();
					info.setOrg_id(reply.get(0).getOrg_id());
					info.setOrg_name(reply.get(0).getOrg_name());
					organizationInfo = info;
					getRefreshNoticeData(organizationInfo.getOrg_id());
				}
			}
		});

	}

	// 下拉刷新加载数据
	private void getRefreshNoticeData(String org_id) {
		if (org_id == null || TextUtils.isEmpty(org_id)) {
			return;
		}
		AnnounceController.getAnnounceList(getContext(), String.valueOf(reStart), String.valueOf(reLimit), org_id, "1", "1", new Listener<Void, List<NoticeInfo>>() {

			@Override
			public void onCallBack(Void status, List<NoticeInfo> reply) {

				if (reply == null || reply.size() < 1) {
					mListView.stopRefresh();
					mListView.stopLoadMore();

				}
				if (mList.size() > 0) {
					mList.clear();
				}

				mList.addAll(reply);
				loadStart = reLimit;
				mAdapter.setDatas(mList);
				mListView.setAdapter(mAdapter);
				mListView.stopRefresh();
				mListView.stopLoadMore();
			}
		});
	}

	// 上拉加载更多
	private void getLoadNoticeData(String org_id) {
		if (org_id == null || TextUtils.isEmpty(org_id)) {
			mListView.stopLoadMore();
			return;
		}
		AnnounceController.getAnnounceList(getContext(), String.valueOf(loadStart), String.valueOf(loadLimit), org_id, "1", "1", new Listener<Void, List<NoticeInfo>>() {
			@Override
			public void onCallBack(Void status, List<NoticeInfo> reply) {
				if (reply == null) {
					mListView.stopLoadMore();
					return;
				}
				for (NoticeInfo info : reply) {
					if (info.getMsg_unread() == 1) {
						mList.add(info);
					}
				}
				mAdapter.setDatas(mList);
				loadStart += loadLimit;
				mListView.stopLoadMore();

			}
		});
	}

	@Override
	public void onMenuItemClick(int position, SwipeMenu menu, int index) {
		// Toast.makeText(getContext(), String.valueOf(position),
		// Toast.LENGTH_SHORT).show();
		removeItem(position);

	}

	// 删除item
	private void removeItem(final int position) {
		NoticeInfo info = mAdapter.getItem(position);
		if (info == null) {
			return;
		}
		mAdapter.removeItemAt(position);
		mAdapter.notifyDataSetChanged();
		AnnounceController.removeAnnounceInfo(getContext(), info.getMsg_id(), new Listener<Boolean, String>() {

			@Override
			public void onCallBack(Boolean status, String reply) {
				if (status) {
					Toast.makeText(getActivity(), "删除成功!", Toast.LENGTH_SHORT).show();
				}

			}
		});
	}

}
