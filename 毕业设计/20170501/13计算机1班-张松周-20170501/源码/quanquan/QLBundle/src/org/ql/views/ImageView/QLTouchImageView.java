package org.ql.views.ImageView;
import org.ql.utils.debug.QLLog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class QLTouchImageView extends ImageView {

    private static final String TAG = QLTouchImageView.class.getSimpleName();
    // These matrices will be used to move and zoom image
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    // We can be in one of these 3 states
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    // Remember some things for zooming
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;
    Context context;
    
    private int screenWidth,screenHeight;
    
    private float titleHeight;
    
    public float getTitleHeight() {
		return titleHeight;
	}

	public void setTitleHeight(float titleHeight) {
		this.titleHeight = titleHeight;
	}

	private float scWidth=320,scHeight=480;


    public float getScWidth() {
		return scWidth;
	}

	public void setScWidth(float scWidth) {
		this.scWidth = scWidth;
	}

	public float getScHeight() {
		return scHeight;
	}

	public void setScHeight(float scHeight) {
		this.scHeight = scHeight;
	}

	public QLTouchImageView(Context context) {
        super(context);
        super.setClickable(true);
        this.context = context;
        this.init();
    }
    
    public QLTouchImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setClickable(true);
        this.context = context;
        this.init();
    }
    
    private void reSet(Bitmap bm){
    	matrix = new Matrix();
    	//matrix.setScale(scWidth/bm.getWidth(),scHeight/bm.getHeight());
    	matrix.setScale(scWidth/bm.getWidth(),scWidth/bm.getWidth());
    	//matrix.setScale(1,1);
    	setImageMatrix(matrix);
    }
    
    @Override
	public void setImageResource(int resId) {
    	this.reSet(((BitmapDrawable)getResources().getDrawable(resId)).getBitmap());
    	//BoLog.e(TAG, "setImageResource");
		super.setImageResource(resId);
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		this.reSet(bm);
		super.setImageBitmap(bm);
		initPlace(bm);
	}

	private void initPlace(Bitmap bm){
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		screenWidth = wm.getDefaultDisplay().getWidth();
		screenHeight = wm.getDefaultDisplay().getHeight();
		matrix.set(savedMatrix);
        matrix.postTranslate((screenWidth-bm.getWidth())/2, (screenHeight-bm.getHeight())/2-getTitleHeight());
        setImageMatrix(matrix);
	}
	

	
	private void init(){
        matrix.setTranslate(0f, 0f);
        setImageMatrix(matrix);
        setScaleType(ScaleType.MATRIX);

        setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent rawEvent) {
                WrapMotionEvent event = WrapMotionEvent.wrap(rawEvent);

                dumpEvent(event);

                // Handle touch events here...
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    savedMatrix.set(matrix);
                    start.set(event.getX(), event.getY());
                    QLLog.d(TAG, "mode=DRAG "+event.getX()+" "+ event.getY());
                    mode = DRAG;
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    oldDist = spacing(event);
                    QLLog.d(TAG, "oldDist=" + oldDist);
                    if (oldDist > 10f) {
                        savedMatrix.set(matrix);
                        midPoint(mid, event);
                        mode = ZOOM;
                        QLLog.d(TAG, "mode=ZOOM");
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    int xDiff = (int) Math.abs(event.getX() - start.x);
                    int yDiff = (int) Math.abs(event.getY() - start.y);
                    if (xDiff < 8 && yDiff < 8){
                        performClick();
                        //Toast.makeText(MainAct.this,"abxc", Toast.LENGTH_SHORT);
                    }
                case MotionEvent.ACTION_POINTER_UP:
                    mode = NONE;
                    QLLog.d(TAG, "mode=NONE");
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mode == DRAG) {
                        // ...
                        matrix.set(savedMatrix);
                        matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
                    } else if (mode == ZOOM) {
                        float newDist = spacing(event);
                        QLLog.d(TAG, "newDist=" + newDist);
                        if (newDist > 10f) {
                            matrix.set(savedMatrix);
                            float scale = newDist / oldDist;
                            matrix.postScale(scale, scale, mid.x, mid.y);
                        }
                    }
                    break;
                }

                setImageMatrix(matrix);
                return true; // indicate event was handled
            }

        });
    }
	
	


    public void setImage(Bitmap bm, int displayWidth, int displayHeight) { 
        super.setImageBitmap(bm);

        //Fit to screen.
        float scale;
        if ((displayHeight / bm.getHeight()) >= (displayWidth / bm.getWidth())){
            scale =  (float)displayWidth / (float)bm.getWidth();
        } else {
            scale = (float)displayHeight / (float)bm.getHeight();
        }

        savedMatrix.set(matrix);
        matrix.set(savedMatrix);
        matrix.postScale(scale, scale, mid.x, mid.y);
        setImageMatrix(matrix);


        // Center the image
        float redundantYSpace = (float)displayHeight - (scale * (float)bm.getHeight()) ;
        float redundantXSpace = (float)displayWidth - (scale * (float)bm.getWidth());

        redundantYSpace /= (float)2;
        redundantXSpace /= (float)2;


        savedMatrix.set(matrix);
        matrix.set(savedMatrix);
        matrix.postTranslate(redundantXSpace, redundantYSpace);
        setImageMatrix(matrix);
    }


    /** Show an event in the LogCat view, for debugging */
    private void dumpEvent(WrapMotionEvent event) {
        // ...
        String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE",
            "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
        StringBuilder sb = new StringBuilder();
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        sb.append("event ACTION_").append(names[actionCode]);
        if (actionCode == MotionEvent.ACTION_POINTER_DOWN
                || actionCode == MotionEvent.ACTION_POINTER_UP) {
            sb.append("(pid ").append(
                    action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
            sb.append(")");
        }
        sb.append("[");
        for (int i = 0; i < event.getPointerCount(); i++) {
            sb.append("#").append(i);
            sb.append("(pid ").append(event.getPointerId(i));
            sb.append(")=").append((int) event.getX(i));
            sb.append(",").append((int) event.getY(i));
            if (i + 1 < event.getPointerCount())
            sb.append(";");
        }
        sb.append("]");
        QLLog.d(TAG, sb.toString());
    }

    /** Determine the space between the first two fingers */
    private float spacing(WrapMotionEvent event) {
        // ...
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt(x * x + y * y);
    }

    /** Calculate the mid point of the first two fingers */
    private void midPoint(PointF point, WrapMotionEvent event) {
        // ...
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }
}

class EclairMotionEvent extends WrapMotionEvent {

	protected EclairMotionEvent(MotionEvent event) {
		super(event);
	}

	public float getX(int pointerIndex) {
		return event.getX(pointerIndex);
	}

	public float getY(int pointerIndex) {
		return event.getY(pointerIndex);
	}

	public int getPointerCount() {
		return event.getPointerCount();
	}

	public int getPointerId(int pointerIndex) {
		return event.getPointerId(pointerIndex);
	}
}

class WrapMotionEvent {
	protected MotionEvent event;

	protected WrapMotionEvent(MotionEvent event) {
		this.event = event;
	}

	static public WrapMotionEvent wrap(MotionEvent event) {
		try {
			return new EclairMotionEvent(event);
		} catch (VerifyError e) {
			return new WrapMotionEvent(event);
		}
	}

	public int getAction() {
		return event.getAction();
	}

	public float getX() {
		return event.getX();
	}

	public float getX(int pointerIndex) {
		verifyPointerIndex(pointerIndex);
		return getX();
	}

	public float getY() {
		return event.getY();
	}

	public float getY(int pointerIndex) {
		verifyPointerIndex(pointerIndex);
		return getY();
	}

	public int getPointerCount() {
		return 1;
	}

	public int getPointerId(int pointerIndex) {
		verifyPointerIndex(pointerIndex);
		return 0;
	}

	private void verifyPointerIndex(int pointerIndex) {
		if (pointerIndex > 0) {
			throw new IllegalArgumentException("Invalid pointer index for Donut/Cupcake");
		}
	}

}