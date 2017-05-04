package app.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.view.View;

public class DashedLine extends View {

	public DashedLine(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override  
    protected void onDraw(Canvas canvas) {  
        super.onDraw(canvas);          
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);

        Path path = new Path();
        path.moveTo(0, 5);
        path.lineTo(this.getWidth(), 5);

        PathEffect effects = new DashPathEffect(new float[] { 5, 5, 5, 5 }, 1);
        paint.setPathEffect(effects);
        canvas.drawPath(path, paint);
    } 

}
