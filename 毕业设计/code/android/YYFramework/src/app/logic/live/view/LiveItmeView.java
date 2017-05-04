package app.logic.live.view;

import android.content.Context;
import android.icu.text.MessagePattern;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.RelativeLayout;

/**
 * Created by Administrator on 2017/3/3 0003.
 */

public class LiveItmeView extends RelativeLayout {
    public LiveItmeView(Context context) {
        super(context);
    }

    public LiveItmeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LiveItmeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec , int heightMeasureSpec) {
//        int a = getMeasuredWidth();
        widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) (getMeasuredWidth()*0.5F),MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (getMeasuredWidth()*0.5F),MeasureSpec.EXACTLY);
//        DisplayMetrics dm = new DisplayMetrics();
//        //获取屏幕信息
////         getWindowManager().getDefaultDisplay().getMetrics(dm);
//        int screenWidth = dm.widthPixels;
//        int screenHeigh = dm.heightPixels;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
