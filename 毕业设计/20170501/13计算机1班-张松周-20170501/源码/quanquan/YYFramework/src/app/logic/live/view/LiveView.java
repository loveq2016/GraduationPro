package app.logic.live.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Administrator on 2017/3/7 0007.
 */

public class LiveView extends View {
    public LiveView(Context context) {
        super(context);
    }

    public LiveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LiveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private int startY , endY ;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        if( event.getAction() == MotionEvent.ACTION_DOWN){
            startY = (int) event.getY();
        }

        if( event.getAction() == MotionEvent.ACTION_MOVE ){

        }
        if(event.getAction() == MotionEvent.ACTION_UP ){
            endY = (int) event.getY();
        }
        if( endY - startY > 100){
            if( roomMessgeViewShow != null ) roomMessgeViewShow.viewBima();
        }else if( startY - endY > 100){
            if( roomMessgeViewShow != null ) roomMessgeViewShow.viewShow();
        }
        super.dispatchTouchEvent(event) ;
        return true ;

    }


    private RoomMessgeViewShow roomMessgeViewShow ;
    public interface RoomMessgeViewShow{
        public void viewShow();
        public void viewBima();
    }

    public void setRoomMessgeViewShow( RoomMessgeViewShow roomMessgeViewShow){
        this.roomMessgeViewShow = roomMessgeViewShow ;
    }

}
