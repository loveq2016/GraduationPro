package org.ql.views.listview;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;


/**    
* 类名称：QLPinnedHeaderListView    
* 类描述：标题固定的listview，利用多一个headrview实现，在onscorll中控制  
* 创建人：anan
* 创建时间：2012-11-29 下午4:09:13    
* 修改人：anan    
* 修改时间：2012-11-29 下午4:09:13    
* 修改备注：  
* @version    
*/   
public class QLPinnedHeaderListView extends ListView {

	/**
	 * Adapter interface. The list adapter must implement this interface.
	 */
	public interface PinnedHeaderAdapter {

		/**
		 * Pinned header state: don't show the header.
		 */
		public static final int PINNED_HEADER_GONE = 0;

		/**
		 * Pinned header state: show the header at the top of the list.
		 */
		public static final int PINNED_HEADER_VISIBLE = 1;

		/**
		 * Pinned header state: show the header. If the header extends beyond
		 * the bottom of the first shown element, push it up and clip.
		 */
		public static final int PINNED_HEADER_PUSHED_UP = 2;

		/**
		 * 用来得到分组标签状态 Computes the desired state of the pinned header for the
		 * given position of the first visible list item. Allowed return values
		 * are {@link #PINNED_HEADER_GONE}, {@link #PINNED_HEADER_VISIBLE} or
		 * {@link #PINNED_HEADER_PUSHED_UP}.
		 */
		int getPinnedHeaderState(int position);

		/**
		 * 用来设置分组标签的标题 Configures the pinned header view to match the first
		 * visible list item.
		 * 
		 * @param header
		 *            pinned header view.
		 * @param position
		 *            position of the first visible list item.
		 * @param alpha
		 *            fading of the header view, between 0 and 255.
		 */
		void configurePinnedHeader(View header, int position);
	}

	private static final int MAX_ALPHA = 255;

	private PinnedHeaderAdapter mAdapter;
	/** 显示在顶端的item */
	private View mHeaderView;
	private boolean mHeaderViewVisible;

	private int mHeaderViewWidth;

	private int mHeaderViewHeight;

	public QLPinnedHeaderListView(Context context) {
		super(context);
	}

	public QLPinnedHeaderListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public QLPinnedHeaderListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setPinnedHeaderView(View view) {
		mHeaderView = view;
		if (mHeaderView != null) {
			// 设置边框渐变的长度
			setFadingEdgeLength(0);
		}
		// requestLayout();
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
		mAdapter = (PinnedHeaderAdapter) adapter;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// 得到mHeaderViewz的宽、高
		if (mHeaderView != null) {
			measureChild(mHeaderView, widthMeasureSpec, heightMeasureSpec);
			mHeaderViewWidth = mHeaderView.getMeasuredWidth();
			mHeaderViewHeight = mHeaderView.getMeasuredHeight();
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (mHeaderView != null) {
			mHeaderView.layout(0, 0, mHeaderViewWidth, mHeaderViewHeight);
			configureHeaderView(getFirstVisiblePosition());
		}
	}

	/**
	 * 更新HeaderView显示状态
	 * @param position
	 */
	public void configureHeaderView(int position) {
		if (mHeaderView == null) {
			return;
		}
		int state = mAdapter.getPinnedHeaderState(position);
		switch (state) {
		case PinnedHeaderAdapter.PINNED_HEADER_GONE: {
			// mHeaderViewVisible = false;
			break;
		}

		case PinnedHeaderAdapter.PINNED_HEADER_VISIBLE: {
			mAdapter.configurePinnedHeader(mHeaderView, position);
			// if (mHeaderView.getTop() != 0) {
			// mHeaderView.layout(0, 0, mHeaderViewWidth, mHeaderViewHeight);
			// }
			// mHeaderViewVisible = true;
			break;
		}

		case PinnedHeaderAdapter.PINNED_HEADER_PUSHED_UP: {
			View firstView = getChildAt(0);
			int bottom = firstView.getBottom();
			int headerHeight = mHeaderView.getHeight();
			int y;
			if (bottom < headerHeight) {
				y = (bottom - headerHeight);
			} else {
				y = 0;
			}
			mAdapter.configurePinnedHeader(mHeaderView, position);
			if (mHeaderView.getTop() != y) {
				mHeaderView.layout(0, y, mHeaderViewWidth, mHeaderViewHeight
						+ y);
			}
			mHeaderViewVisible = true;
			break;
		}
		}
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (mHeaderViewVisible) {
			drawChild(canvas, mHeaderView, getDrawingTime());
		}
	}
}