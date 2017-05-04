package app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class ScrollviewAllowInnerScrol extends ScrollView{

	public ScrollviewAllowInnerScrol(Context context) {
		this(context , null);
	}
	
	public ScrollviewAllowInnerScrol(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //System.out.println("MyScrollView-->onInterceptTouchEvent");
        return false;
    }


}
