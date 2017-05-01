package app.logic.activity.main;

//import cn.jpush.android.b.a.f;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
/*
 * GZYY    2016-7-28  上午11:32:22
 */
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TabHost.OnTabChangeListener;
import app.logic.activity.notice.FragmentRead;
import app.logic.activity.notice.FragmentUnread;
import app.yy.geju.R;

public class NoticeFragment extends Fragment {

	private View view;
	private FragmentTabHost fragmentTabHost;

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_notice_list, null);

		fragmentTabHost = (FragmentTabHost) view.findViewById(R.id.notice_ftg);

		fragmentTabHost.setup(view.getContext(), getChildFragmentManager(), R.id.notic_fragment);
		View unreadView = getIndicatorView("未读", getResources().getDrawable(R.drawable.selector_tabnotice_unread));
		fragmentTabHost.addTab(fragmentTabHost.newTabSpec("unread").setIndicator(unreadView), FragmentUnread.class, null);

		View readView = getIndicatorView("已读", getResources().getDrawable(R.drawable.selector_tabnotice_unread));
		fragmentTabHost.addTab(fragmentTabHost.newTabSpec("read").setIndicator(readView), FragmentRead.class, null);

		// getActivity().getResources().getDimension(R.dimen.dabc_action_bar_default_height_material);
		// getActivity().getActionBar().getHeight();
		// 解决tabhost会被压缩问题
		fragmentTabHost.getTabWidget().getChildAt(0).getLayoutParams().height = (int) getActivity().getResources().getDimension(R.dimen.actionbar_height);

		fragmentTabHost.setCurrentTab(0);

		fragmentTabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				// for (int i = 0; i < fragmentTabHost.getChildCount(); i++) {
				// System.out.println(fragmentTabHost.getTabWidget().getChildAt(i).getTag());
				// }
			}
		});

		return view;
	}

	private View getIndicatorView(String name, Drawable dramableIds) {
		View v = LayoutInflater.from(view.getContext()).inflate(R.layout.notice_tab_item, null);
		TextView tView = (TextView) v.findViewById(R.id.notice_item_btn);
		tView.setText(name);
		tView.setBackgroundDrawable(dramableIds);
		return v;
	}
}
