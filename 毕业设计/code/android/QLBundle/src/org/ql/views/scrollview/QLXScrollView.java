package org.ql.views.scrollview;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

/**    
* 类名称：QLXScrollView    
* 类描述：实现下拉刷新，上拉加载更多的  ScrollView
* 创建人：anan
* 创建时间：2012-11-28 下午5:53:48    
* 修改人：anan    
* 修改时间：2012-11-28 下午5:53:48    
* 修改备注：  
* @version    
*/   
public class QLXScrollView extends ScrollView {

	public QLXScrollView(Context context) {
		super(context);
		init(context);

	}

	public QLXScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);

	}

	public QLXScrollView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);

	}

	private void init(Context context) {

	}

	
	public static void ScrollToPoint(final View scroll, final View inner,final int i)
	{
		Handler mHandler = new Handler();

		mHandler.post(new Runnable() {
			public void run() {
				if (scroll == null || inner == null) {
					return;
				}

				int offset =inner.getMeasuredHeight() - scroll.getHeight()-i;
				
				if (offset < 0) {
					offset = 0;
				}

				scroll.scrollTo(0, offset);
				
				scroll.invalidate();
			}
		});
	}
	

}