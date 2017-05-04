package app.logic.activity.notice;

import java.util.ArrayList;
import java.util.List;

import org.canson.view.swipemenulistview.SwipeMenu;
import org.canson.view.swipemenulistview.SwipeMenuCreator;
import org.canson.view.swipemenulistview.SwipeMenuItem;
import org.canson.view.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import org.ql.utils.QLToastUtils;
import org.ql.views.listview.QLXListView.IXListViewListener;

import com.squareup.picasso.Picasso;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
/*
 * GZYY    2016-7-29  上午11:00:25
 */
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import app.config.http.HttpConfig;
import app.logic.activity.card.CardListActivity;
import app.logic.activity.main.HomeActivity;
import app.logic.adapter.YYBaseListAdapter;
import app.logic.controller.AnnounceController;
import app.logic.controller.OrganizationController;
import app.logic.pojo.CountUnreadInfo;
import app.logic.pojo.NoticeInfo;
import app.logic.pojo.OrgUnreadNumberInfo;
import app.logic.pojo.OrganizationInfo;
import app.logic.singleton.YYInterface.UndataTitleToUnreadFragmentListener;
import app.logic.singleton.YYSingleton;
import app.logic.singleton.ZSZSingleton;
import app.logic.singleton.ZSZSingleton.NotiToFragmentUnreadListener;
import app.logic.singleton.ZSZSingleton.OrgInfoInterface;
import app.logic.singleton.ZSZSingleton.OrgInfoInterfaceEd;
import app.logic.singleton.ZSZSingleton.UpdataNoticeCountListener;
import app.utils.common.AndroidFactory;
import app.utils.common.Listener;
import app.utils.helpers.YYUtils;
import app.view.RichTextViewActivity2;
import app.view.YYListView;
import app.yy.geju.R;
import app.yy.geju.R.layout;

public class FragmentUnread extends Fragment implements IXListViewListener, OnMenuItemClickListener, OnItemClickListener {

	public static final String CLICK_ITEMINFO = "CLICK_ITEMINFO";

	private View view;
	private YYListView mListView;
	private List<String> org_id_list = new ArrayList<String>();
	private List<NoticeInfo> dataList = new ArrayList<NoticeInfo>();
	private OrgUnreadNumberInfo selectedOrgInfo;
	private List<NoticeInfo> reList = new ArrayList<NoticeInfo>();
	private Intent intentUN = new Intent();

	private int reStart = 0;//
	private int reLimit = 25;// 下拉刷新每次获取的数据条数

	private int loadStart = reLimit;
	private int loadLimit = 25;// 上拉加载更多数据的条数

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
				Picasso.with(getContext()).load(HttpConfig.getUrl(info.getPicture_url())).error(R.drawable.item_notice_default).fit().centerCrop().into(noticeIv); //
			}
			return convertView;

		}
	};

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.inflate(R.layout.fragment_unread, null);
		}
		ViewGroup paraent = (ViewGroup) view.getParent();
		if (paraent != null) {
			paraent.removeView(view);
		}

		initView();
		initData();

		return view;
	}

	private void initView() {
		mListView = (YYListView) view.findViewById(R.id.unread_lv);

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
		mListView.setMenuCreator(menuCreator);
		mListView.setOnItemClickListener(this);
		mListView.setOnMenuItemClickListener(this);
		mListView.setXListViewListener(this);
		mListView.setPullLoadEnable(true, true);
		mListView.setPullRefreshEnable(true);

	}

	private void initData() {
		mListView.setAdapter(mAdapter);

		selectedOrgInfo = ZSZSingleton.getZSZSingleton().getOrganizationInfo();
		interfaceCount();

		onCallBackNoticeJPush();

	}

	// 定义接口刷新
	private void interfaceCount() {

		YYSingleton.getInstance().setUndataTitleToUnreadFragmentListener(new UndataTitleToUnreadFragmentListener() {

			@Override
			public void onCallBack(OrgUnreadNumberInfo info) {
				ZSZSingleton.getZSZSingleton().setOrganizationInfo(info);
				selectedOrgInfo = info;
				getRefreshNoticeData(info.getOrg_id());
			}
		});

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		if (ZSZSingleton.getZSZSingleton().getOrganizationInfo() != null) {
			selectedOrgInfo = ZSZSingleton.getZSZSingleton().getOrganizationInfo();
			getRefreshNoticeData(selectedOrgInfo.getOrg_id());
		}

		intentUN.setAction(HomeActivity.UPDATANOTICE);
		getContext().sendBroadcast(intentUN);

	}

	@Override
	public void onRefresh() {
		if (selectedOrgInfo != null) {
			getRefreshNoticeData(selectedOrgInfo.getOrg_id());
		}
		mListView.stopRefresh();
		mListView.stopLoadMore();

	}

	@Override
	public void onLoadMore() {
		if (selectedOrgInfo != null) {
			getRefreshNoticeData(selectedOrgInfo.getOrg_id());
		}

		mListView.stopRefresh();
		mListView.stopLoadMore();
	}

	@Override
	public void onPause() {
		super.onPause();

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		NoticeInfo itemInfo = mAdapter.getItem(position - 1);
		if (itemInfo != null) {
			Intent intent = new Intent();
			intent.setClass(getContext(), RichTextViewActivity2.class);
			intent.putExtra(RichTextViewActivity2.CLICKINFO_ID, itemInfo.getMsg_id());
			startActivity(intent);
			reList.remove(position - 1);
			mAdapter.setDatas(reList);
		}

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
					intentUN.setAction(HomeActivity.UPDATANOTICE);
					getContext().sendBroadcast(intentUN);
				}

			}
		});
	}

	// 回调推送的接口
	private void onCallBackNoticeJPush() {
		ZSZSingleton.getZSZSingleton().setNotiToFragmentUnreadListener(new NotiToFragmentUnreadListener() {

			@Override
			public void onCallBack() {
				getRefreshNoticeData(selectedOrgInfo.getOrg_id());

			}
		});

	}

	// ------------------------------ 下拉刷新 -------------------------------
	private void getRefreshNoticeData(String org_id) {

		if (org_id == null || TextUtils.isEmpty(org_id)) {
			Toast.makeText(getContext(), "你当前加入没有组织", Toast.LENGTH_SHORT).show();
			mListView.stopRefresh();
			mListView.stopLoadMore();
			return;
		}
		AnnounceController.getAnnounceList(AndroidFactory.getApplicationContext(), String.valueOf(reStart), String.valueOf(reLimit), org_id, "1", "0", new Listener<Void, List<NoticeInfo>>() {

			@Override
			public void onCallBack(Void status, List<NoticeInfo> reply) {
				if (reply != null) {
					if (reList.size() > 0) {
						reList.clear();
					}
					for (NoticeInfo info : reply) {
						if (info.getMsg_unread() == 0) {
							reList.add(info);
						}
					}
					loadStart = reLimit;

					mAdapter.setDatas(reList);
					mListView.setAdapter(mAdapter);

				}
				mListView.stopRefresh();
				mListView.stopLoadMore();

			}
		});

	}

	// ------------------------------ 上拉加载更多 -------------------------------

	private void getLoadingNoticeData(String org_id) {
		if (org_id == null || TextUtils.isEmpty(org_id)) {
			mListView.stopLoadMore();
			return;
		}
		AnnounceController.getAnnounceList(getContext(), String.valueOf(loadStart), String.valueOf(loadLimit), org_id, "1", "0", new Listener<Void, List<NoticeInfo>>() {

			@Override
			public void onCallBack(Void status, List<NoticeInfo> reply) {
				if (reply == null) {
					mListView.stopLoadMore();
					return;
				}
				for (NoticeInfo info : reply) {
					if (info.getMsg_unread() == 0) {
						reList.add(info);
					}
				}
				// Toast.makeText(getContext(), String.valueOf(reList.size()),
				// Toast.LENGTH_SHORT).show();
				mAdapter.setDatas(reList);
				loadStart += loadLimit;

				mListView.stopLoadMore();
			}
		});
	}
	// ------------------------------ 上拉加载更多 -------------------------------
}
