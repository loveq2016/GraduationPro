package org.ql.views;

import org.ql.utils.debug.QLLog;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**    
* 类名称：QLViewPager    
* 类描述：解决横向滚动冲突，需要优化子视图  
* 创建人：anan
* 创建时间：2012-12-17 下午3:02:28    
* 修改人：anan    
* 修改时间：2012-12-17 下午3:02:28    
* 修改备注：  
* @version    
*/   
public class QLViewPager extends ViewPager {
	private boolean willIntercept = true;

	public QLViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		if (willIntercept) {
			// 这个地方直接返回true会很卡
			return super.onInterceptTouchEvent(arg0);
		} else {
			return false;
		}

	}

	/**
	 * 设置ViewPager是否拦截点击事件
	 * 
	 * @param value
	 *            if true, ViewPager拦截点击事件if false,
	 *            ViewPager将不能滑动，ViewPager的子View可以获得点击事件主要受影响的点击事件为横向滑动
	 * 
	 */
	public void setTouchIntercept(boolean value) {
		willIntercept = value;
	}
	
	@Override
	public void requestDisallowInterceptTouchEvent(boolean disallowIntercept){
		QLLog.e("disallowIntercept-->"+disallowIntercept);
		willIntercept = !disallowIntercept;
		super.requestDisallowInterceptTouchEvent(disallowIntercept);
	}
}
