package org.ql.views.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 类名称：QLSuperTouchListView <br>
 * 类描述：解决listview与其他滚动view冲突 <br>
 * 创建者：anan <br>
 * 创建时间：2013-1-25 上午11:52:29 <br>
 * 修改者：anan <br>
 * 修改时间：2013-1-25 上午11:52:29 <br>
 * 修改备注： <br>
 * 
 * @version
 */
public class QLSuperTouchListView extends QLXListView {
	private int flowViewPosition=-1;

	public QLSuperTouchListView(Context context) {
		super(context);
	}

	public QLSuperTouchListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setFadingEdgeLength(0);
	}

	public QLSuperTouchListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public int getFlowViewPosition() {
		return flowViewPosition;
	}

	public void setFlowViewPosition(int flowViewPosition) {
		this.flowViewPosition = flowViewPosition;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		final int y = (int) ev.getY();
		final int x = (int) ev.getX();
		int position = pointToPosition(x, y);
		if (flowViewPosition!=-1&&position == flowViewPosition) {
				int firstVisablePos = getFirstVisiblePosition();
				View firstView = this.getChildAt(firstVisablePos);
				int bottom = firstView.getBottom();
				if (bottom == firstView.getHeight()) {
					return false;
				} else if (bottom < firstView.getHeight()) {
					smoothScrollToPosition(0);
					return false;
				}
		}
		return super.onInterceptTouchEvent(ev);
	}
}