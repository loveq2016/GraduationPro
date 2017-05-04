package app.logic.activity.main;

import java.util.ArrayList;
import java.util.List;
import org.ql.views.listview.QLXListView.IXListViewListener;
import com.facebook.drawee.view.SimpleDraweeView;

import app.logic.activity.TYBaseActivity;
import app.utils.common.FrescoImageShowThumb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import app.config.http.HttpConfig;
import app.logic.activity.notice.OrgNoticeDefaultActivity;
import app.logic.adapter.YYBaseListAdapter;
import app.logic.controller.OrganizationController;
import app.logic.pojo.OrgUnreadNumberInfo;
import app.utils.common.Listener;
import app.view.YYListView;
import app.yy.geju.R;

/*
 * GZYY    2016-12-7  下午2:22:59
 * author: zsz
 */

public class MyOrgNoticeListFragment extends Fragment implements IXListViewListener ,OnItemClickListener{

	private View view;   //Fragment使用的View
	private YYListView yyListView;  //列表
	private List<OrgUnreadNumberInfo> datas = new ArrayList<>(); //列表数据源
	private View empty_view;
	private Context context;

	private YYBaseListAdapter<OrgUnreadNumberInfo> adapter = new YYBaseListAdapter<OrgUnreadNumberInfo>(getContext()) {

		@Override
		public View createView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_my_org_notice_layout, null);
				saveView("item_iv", R.id.item_iv, convertView);
				saveView("item_name_tv", R.id.item_name_tv, convertView);
				saveView("item_count", R.id.item_count, convertView);
			}
			OrgUnreadNumberInfo info = getItem(position);
			if (info != null) {
//				setImageToImageViewCenterCrop( HttpConfig.getUrl(info.getOrg_logo_url()), "item_iv", 0, convertView);
				SimpleDraweeView headIv = getViewForName("item_iv", convertView);
//				Picasso.with(getContext()).load(HttpConfig.getUrl(info.getOrg_logo_url())).placeholder(R.drawable.default_user_icon).error(R.drawable.default_user_icon).fit().centerCrop().into(headIv);
//				headIv.setImageURI(HttpConfig.getUrl(info.getOrg_logo_url()));
				FrescoImageShowThumb.showThrumb(Uri.parse(HttpConfig.getUrl(info.getOrg_logo_url())),headIv);
				setTextToViewText(info.getOrg_name(), "item_name_tv", convertView);
				TextView textView = getViewForName("item_count", convertView);
				textView.setText(String.valueOf(info.getCount()));
				textView.setVisibility(info.getCount() < 1 ? View.GONE : View.VISIBLE);
			}
			return convertView;
		}
	};
	
	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		context = getActivity();
		if (view == null) {
			view = inflater.inflate(R.layout.fragment_my_org_notice_list, null);
			initView();

		}
		ViewGroup parent = ( ViewGroup ) view.getParent();
		if (parent != null) {
			parent.removeView(view);
		}
		return view;
	}

	/**
	 * 初始化View
	 */
	private void initView() {
		empty_view = view.findViewById(R.id.empty_view);
		((TextView) view.findViewById(R.id.empty_tv01)).setText("您还没有加入任何组织");
		((TextView) view.findViewById(R.id.empty_tv02)).setText("赶紧去加入组织才能查看公告");
		yyListView = (YYListView) view.findViewById(R.id.listview);
		yyListView.setAdapter(adapter);
		yyListView.setOnItemClickListener(this);
		yyListView.setPullRefreshEnable(true);
		yyListView.setPullLoadEnable(false, false);
		yyListView.setXListViewListener(this);

		IntentFilter filter = new IntentFilter();
		filter.addAction(HomeActivity.UPDATANOTICE);
		context.registerReceiver(receiver, filter);
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			switch (intent.getAction()) {
				case HomeActivity.UPDATANOTICE:
					getMyOrg();
					break;
				default:
					break;
			}
		}
	};

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		OrgUnreadNumberInfo info = null ;
		if(position-1>=0){
			info = datas.get(position-1);  //之前有减1，但是一直不知道为什么？应该是添加了头引起的
		}
		if (info != null) {
			Intent intent =  new Intent() ;
			intent.setClass( getContext() , OrgNoticeDefaultActivity.class);
			intent.putExtra(OrgNoticeDefaultActivity.ORG_ID ,info.getOrg_id());
			intent.putExtra(OrgNoticeDefaultActivity.ORG_NAME ,info.getOrg_name());
			intent.putExtra(OrgNoticeDefaultActivity.ORG_IMAGE ,info.getOrg_logo_url());
			getActivity().startActivity(intent);
		}
	}

	/**
	 * 获取发了公告的组织
	 */
	private void getMyOrg() {
//		((TYBaseActivity)context).showWaitDialog();
		OrganizationController.getOrgUnreadNumber(getContext(), new Listener<Integer, List<OrgUnreadNumberInfo>>() {
			@Override
			public void onCallBack(Integer status, List<OrgUnreadNumberInfo> reply) {
				yyListView.stopRefresh();
//				((TYBaseActivity)context).dismissWaitDialog();
				datas.clear();
				if (reply != null || reply.size() > 0) {
					List<OrgUnreadNumberInfo> unreadNumberInfos = new ArrayList<OrgUnreadNumberInfo>();
					for (OrgUnreadNumberInfo info : reply){
						if(info.getCount()>0) unreadNumberInfos.add(0,info);
						else unreadNumberInfos.add(info);
					}

					datas.addAll(unreadNumberInfos);
				}
				if( datas.size()>0){
					empty_view.setVisibility(View.GONE);
				}else{
					empty_view.setVisibility(View.VISIBLE);
				}



				adapter.setDatas(datas);
			}
		});
	}

	@Override
	public void onRefresh() {
		getMyOrg();
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//获取租住列表
		getMyOrg();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (receiver!=null)
			context.unregisterReceiver(receiver);
	}
}
