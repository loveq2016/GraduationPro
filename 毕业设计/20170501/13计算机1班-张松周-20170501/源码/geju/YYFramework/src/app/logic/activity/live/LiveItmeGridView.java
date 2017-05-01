package app.logic.activity.live;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by Administrator on 2017/2/15 0015.
 */

public class LiveItmeGridView extends GridView {

    public LiveItmeGridView(Context context) {
        super(context);
    }

    public LiveItmeGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LiveItmeGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //想让GridView默认完全展开
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 1, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
