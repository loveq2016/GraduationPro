package app.logic.adapter;

import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.NoCopySpan.Concrete;
import android.view.View;
import android.view.ViewGroup;

/*
 * GZYY    2016-12-6  上午9:33:50
 * author: zsz
 */

public class LaunchPagerAdapter extends PagerAdapter {

	private List<View> views;

	private Context context;

	public LaunchPagerAdapter(Context context, List<View> views) {
		this.context = context;
		this.views = views;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
		// super.destroyItem(container, position, object);
		((ViewPager) container).removeView(views.get(position));
	}

	@Override
	public Object instantiateItem(ViewGroup view, int position) {

		// View v = views.get(position);
		// ViewGroup paGroup = (ViewGroup) v.getParent();
		// if (paGroup != null) {
		// paGroup.removeView(v);
		// view.addView(views.get(position));
		// } else {
		// view.addView(views.get(position));
		// }

		((ViewGroup) view).addView(views.get(position));
		return views.get(position);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return views.size();

	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == arg1;

	}

}
