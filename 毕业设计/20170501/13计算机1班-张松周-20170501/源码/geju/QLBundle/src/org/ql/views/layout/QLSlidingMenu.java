package org.ql.views.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

/**     
 * 类名称：SlidingMenu   <br>
 * 类描述：左右挤压的布局控件
 * 创建人：anan   
 * 创建时间：2012-12-23 上午12:04:24   
 * 修改人：anan  
 * 修改时间：2012-12-23 上午12:04:24   
 * 修改备注：   
 * @version        
 * */
public class QLSlidingMenu extends RelativeLayout {

	private QLSlidingView mSlidingView;
	private View mMenuView;
	private View mDetailView;

	public QLSlidingMenu(Context context) {
		super(context);
	}

	public QLSlidingMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public QLSlidingMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * 添加视图
	 * @param left
	 * @param center
	 * @param right
	 */
	public void addViews(View left, View center, View right) {
		setLeftView(left);
		setRightView(right);
		setCenterView(center);
	}

	/**
	 * 设置左边视图
	 * @param view
	 */
	public void setLeftView(View view) {
		LayoutParams behindParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
		addView(view, behindParams);
		mMenuView = view;
	}

	/**
	 * 设置右边视图
	 * @param view
	 */
	public void setRightView(View view) {
		LayoutParams behindParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
		behindParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		addView(view, behindParams);
		mDetailView = view;
	}

	/**
	 * 设置中间的view,这个是必须的
	 * @param view
	 */
	public void setCenterView(View view) {
		LayoutParams aboveParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		mSlidingView = new QLSlidingView(getContext());
		addView(mSlidingView, aboveParams);
		mSlidingView.setView(view);
		mSlidingView.invalidate();
		mSlidingView.setMenuView(mMenuView);
		mSlidingView.setDetailView(mDetailView);
	}

	/**
	 * 显示左边视图
	 */
	public void showLeftView() {
		mSlidingView.showLeftView();
	}

	/**
	 * 显示右边视图
	 */
	public void showRightView() {
		mSlidingView.showRightView();
	}
}
