package app.view;

import android.app.usage.UsageEvents.Event;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

/*
 * GZYY    2016-9-2  下午3:29:01
 */

public class ZSZGridView extends GridView {

	public ZSZGridView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ZSZGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public ZSZGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {

		return super.dispatchTouchEvent(ev);

	}

}
