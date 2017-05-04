package org.ql.utils.image;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Display;

public class QLImageUtil {
	public final static int WRAP_CONTENT = 0;
	public final static int FILL_PARENT = -1;
	public final static int ORIGINAL_CONTENT = -2;
	
	
	private int consultW,consultH;
	
	

	public void setConsultW(int consultW){
		this.consultW = consultW;
	}
	
	public void setConsultH(int consultH){
		this.consultH = consultH;
	}
	
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}
	
	/**
	 * 按正方形裁切图片
	 */
	public static Bitmap ImageCrop(Bitmap bitmap) {
		if(bitmap == null)
			return null;
		int w = bitmap.getWidth(); // 得到图片的宽，高
		int h = bitmap.getHeight();
		int wh = w > h ? h : w;// 裁切后所取的正方形区域边长
		int retX = w > h ? (w - h) / 2 : 0;// 基于原图，取正方形左上角x坐标
		int retY = w > h ? 0 : (h - w) / 2;
		// 下面这句是关键
		return Bitmap.createBitmap(bitmap, retX, retY, wh, wh, null, false);
	}
	
	/**
	 * @param bitmap 原图片
	 * @param w 生成新图片的宽度
	 * @param h 生成新图片的高度
	 * @return 新图片
	 */
	public Bitmap getZoomBitmap(Activity activity,Bitmap bitmap, int w, int h) {
		Zoom zoom = new Zoom(activity,bitmap,w, h);
    	w = zoom.w;
    	h = zoom.h;
		
		Bitmap desImg = null;
		int srcW = bitmap.getWidth(); // 原始图像宽
		int srcH = bitmap.getHeight(); // 原始图像高
		int[] srcBuf = new int[srcW * srcH]; // 原始图片像素信息缓存

		bitmap.getPixels(srcBuf, 0, srcW, 0, 0, srcW, srcH);

		// 计算插值表
		int[] tabY = new int[h];
		int[] tabX = new int[w];

		int sb = 0;
		int db = 0;
		int tems = 0;
		int temd = 0;
		int distance = srcH > h ? srcH : h;
		for (int i = 0; i <= distance; i++) { /* 垂直方向 */
			tabY[db] = sb;
			tems += srcH;
			temd += h;
			if (tems > distance) {
				tems -= distance;
				sb++;
			}
			if (temd > distance) {
				temd -= distance;
				db++;
			}
		}
		sb = 0;
		db = 0;
		tems = 0;
		temd = 0;
		distance = srcW > w ? srcW : w;
		for (int i = 0; i <= distance; i++) { /* 水平方向 */
			tabX[db] = (short) sb;
			tems += srcW;
			temd += w;
			if (tems > distance) {
				tems -= distance;
				sb++;
			}
			if (temd > distance) {
				temd -= distance;
				db++;
			}
		}

		// 生成放大缩小后图形像素
		int[] desBuf = new int[w * h];
		int dx = 0;
		int dy = 0;
		int sy = 0;

		int oldy = -1;
		for (int i = 0; i < h; i++) {
			if (oldy == tabY[i]) {
				System.arraycopy(desBuf, dy - w, desBuf, dy, w);
			} else {
				dx = 0;
				for (int j = 0; j < w; j++) {
					desBuf[dy + dx] = srcBuf[sy + tabX[j]];
					dx++;
				}
				sy += (tabY[i] - oldy) * srcW;
			}
			oldy = tabY[i];
			dy += w;
		}
		// 生成图片
		desImg = Bitmap.createBitmap(desBuf, w, h,
				Bitmap.Config.ARGB_8888);
		zoom = null;
		return desImg;
	}


	/**
	 * @param bitmap
	 *            原图片
	 * @param w
	 *            生成新图片的宽度
	 * @param h
	 *            生成新图片的高度
	 * @return 新图片 Drawable
	 */
	public Drawable getZoomDrawable(Activity activity,Bitmap bitmap, int w, int h) {
		Zoom zoom = new Zoom(activity,bitmap,w, h);
    	w = zoom.w;
    	h = zoom.h;
    	
		// load the origial Bitmap
		// Bitmap BitmapOrg = bitmap;

		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int newWidth = w;
		int newHeight = h;

		// calculate the scale
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;

		// create a matrix for the manipulation
		Matrix	matrix = new Matrix();
		// resize the Bitmap
		matrix.postScale(scaleWidth, scaleHeight);
		// if you want to rotate the Bitmap
		// matrix.postRotate(45);

		// recreate the new Bitmap
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);

		// make a Drawable from Bitmap to allow to set the Bitmap
		// to the ImageView, ImageButton or what ever
		zoom = null;
		return new BitmapDrawable(resizedBitmap);

	}
	
	
	
	/** 附赠两个方法，一个将字节转换成bitmap*/
	public Bitmap getPicFromBytes(byte[] bytes,BitmapFactory.Options opts) {
		if (bytes != null)
			if (opts != null)
				return BitmapFactory.decodeByteArray(bytes, 0, bytes.length,
						opts);
			else
				return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		return null;
	}

	/**一个获取byte[]数组*/
	public byte[] getBytesFromInputStream(InputStream is, int bufsiz)
			throws IOException {
		int total = 0;
		byte[] bytes = new byte[1024 * 4];
		ByteBuffer bb = ByteBuffer.allocate(bufsiz);
		while (true) {
			int read = is.read(bytes);
			if (read == -1)
				break;
			bb.put(bytes, 0, read);
			total += read;
		}
		byte[] content = new byte[total];
		bb.flip();
		bb.get(content, 0, total);
		return content;
	}
	
	
	/**
	 * @param bitmap 原图片
	 * @param w 生成新图片的宽度
	 * @param h 生成新图片的高度
	 * @return 新图片
	 */
    public Bitmap zoomBitmap(Activity activity,Bitmap bitmap,int w,int h){  
    	Zoom zoom = new Zoom(activity,bitmap,w,h);
    	w = zoom.w;
    	h = zoom.h;
        int width = bitmap.getWidth();  
        int height = bitmap.getHeight();  
        Matrix matrix = new Matrix();  
        float scaleWidht = ((float)w / width);  
        float scaleHeight = ((float)h / height);  
        matrix.postScale(scaleWidht, scaleHeight);  
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);  
		zoom = null;
        return newbmp;  
    } 
    
    public Bitmap zoomBitmap(Activity activity,Drawable drawable, int w, int h){
    	return zoomBitmap(activity,drawableToBitmap(drawable),w,h);
    }
    
    public Bitmap zoomBitmap(Activity activity,Drawable drawable, int w, int h,float roundPx){
    	//Log.i("index","index = "+(jj++));
    	return getBitmap(zoomBitmap(activity,drawableToBitmap(drawable),w,h),roundPx);
//    	return getBitmap(zoomBitmap(activity,((BitmapDrawable )drawable).getBitmap(),w,h),roundPx);
    }
    
    public Bitmap zoomBitmap(Activity activity,Bitmap bitmap, int w, int h,float roundPx){
    	//Log.i("index","index = "+(jj++));
    	return getBitmap(zoomBitmap(activity,bitmap,w,h),roundPx);
    }
    
    /**将Drawable转化为Bitmap*/ 
    public Bitmap drawableToBitmap(Drawable drawable){  
           int width = drawable.getIntrinsicWidth();  
           int height = drawable.getIntrinsicHeight();  
           Bitmap bitmap = Bitmap.createBitmap(width, height, drawable.getOpacity() != PixelFormat.OPAQUE?Bitmap.Config.ARGB_8888:Bitmap.Config.RGB_565);  
           Canvas canvas = new Canvas(bitmap);  
           drawable.setBounds(0,0,width,height);  
           drawable.draw(canvas);  
           return bitmap;  
       }  
    
    /**获得圆角图片的方法 */
    public Bitmap getBitmap(Drawable drawable,float roundPx){  
    	return getBitmap(drawableToBitmap(drawable),roundPx);
    }

    /**获得圆角图片的方法 */
    public Bitmap getBitmap(Bitmap bitmap,float roundPx){  
    	Bitmap output=null;
    	try{
	        output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);  
	        Canvas canvas = new Canvas(output);  
	        final int color = 0xff424242;  
	        final Paint paint = new Paint();  
	        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());  
	        final RectF rectF = new RectF(rect);  
	        paint.setAntiAlias(true);  
	        canvas.drawARGB(0, 0, 0, 0);  
	        paint.setColor(color);  
	        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);  
	        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));  
	        canvas.drawBitmap(bitmap, rect, rect, paint);  
    	}catch(IllegalArgumentException iae){iae=null;}
    	catch(OutOfMemoryError oome){
    	}
        return output;  
    }   
    
    /**获得带倒影的图片方法*/  
    public Bitmap createReflectionImageWithOrigin(Bitmap bitmap){  
        final int reflectionGap = 4;  
        int width = bitmap.getWidth();  
        int height = bitmap.getHeight();  
        Matrix matrix = new Matrix();  
        matrix.preScale(1, -1);  
        Bitmap reflectionImage = Bitmap.createBitmap(bitmap,0, height/2, width, height/2, matrix, false);  
        Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height/2), Config.ARGB_8888);  
        Canvas canvas = new Canvas(bitmapWithReflection);  
        canvas.drawBitmap(bitmap, 0, 0, null);  
        Paint deafalutPaint = new Paint();  
        canvas.drawRect(0, height,width,height + reflectionGap,deafalutPaint);  
        canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);  
        Paint paint = new Paint();  
        LinearGradient shader = new LinearGradient(0,bitmap.getHeight(), 0, bitmapWithReflection.getHeight()  
                + reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);  
        paint.setShader(shader);  
        // Set the Transfer mode to be porter duff and destination in  
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));  
        // Draw a rectangle using the paint with our linear gradient  
        canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + reflectionGap, paint);  
        return bitmapWithReflection;  

    }  
    
    
    
    
    
    
    public int computeSampleSize(BitmapFactory.Options options,int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,maxNumOfPixels);
		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;
	}

	private int computeInitialSampleSize(BitmapFactory.Options options,int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1)?1:(int) Math.ceil(Math.sqrt(w*h/maxNumOfPixels));
		int upperBound = (minSideLength == -1)?128:(int) Math.min(Math.floor(w/minSideLength),Math.floor(h/minSideLength));
		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}
		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
  private class Zoom{
	  	private Display display;
    	int w,h;
    	public Zoom(Activity activity,Bitmap bitmap,int w,int h){
    		this.w = w;
    		this.h = h;
    		init(activity,bitmap);
    	}
    	private void init(Activity activity,Bitmap bitmap){
    		if((w == WRAP_CONTENT && h == WRAP_CONTENT) || (w == ORIGINAL_CONTENT && h == ORIGINAL_CONTENT) || (w == ORIGINAL_CONTENT && h == WRAP_CONTENT) || (w == WRAP_CONTENT && h == ORIGINAL_CONTENT)){
    			w = bitmap.getWidth();
    			h = bitmap.getHeight();
    		}else if(w == FILL_PARENT && h == FILL_PARENT){
    			display = activity.getWindowManager().getDefaultDisplay();
    			w = display.getWidth();
    			h = display.getHeight();
    		}else if(w == WRAP_CONTENT && h == FILL_PARENT){
    			display = activity.getWindowManager().getDefaultDisplay();
    			int H = display.getHeight();
    			w = (int)(new Integer(bitmap.getWidth()).floatValue()/(new Integer(bitmap.getHeight()).floatValue()/new Integer(H).floatValue()));
    			if(w > display.getWidth())
    				w = display.getWidth();
    			h = H;
    		}else if(w == FILL_PARENT && h == WRAP_CONTENT){
    			display = activity.getWindowManager().getDefaultDisplay();
    			int W = display.getWidth();
    			h = (int)(new Integer(bitmap.getHeight()).floatValue()/(new Integer(bitmap.getWidth()).floatValue()/new Integer(W).floatValue()));
    			if(h > display.getHeight())
    				h = display.getHeight();
    			w = W;
    		}else if(w == FILL_PARENT && h == ORIGINAL_CONTENT){
    			display = activity.getWindowManager().getDefaultDisplay();
    			w = display.getWidth();
    			h  = bitmap.getHeight();
    		}else if(w == ORIGINAL_CONTENT && h == FILL_PARENT){
    			display = activity.getWindowManager().getDefaultDisplay();
    			h = display.getHeight();
    			w = bitmap.getWidth();
    		}else if(w == WRAP_CONTENT && h > 0){
    			w = (int)(new Integer(bitmap.getWidth()).floatValue()/(new Integer(bitmap.getHeight()).floatValue()/new Integer(h).floatValue()));
    			if(w <= 0)
    				w = bitmap.getWidth();
    		}else if(w > 0 && h == WRAP_CONTENT){
    			h = (int)(new Integer(bitmap.getHeight()).floatValue()/(new Integer(bitmap.getWidth()).floatValue()/new Integer(w).floatValue()));
    			if(h <= 0)
    				h = bitmap.getHeight();
    		}else if(w > 0 && h > 0){
    			
    		}else{
    			w = bitmap.getWidth();
    			h = bitmap.getHeight();
    		}
    		
    		if(w < consultW)
    			w = consultW;
    		if(h < consultH)
    			h = consultH;
    	}
    }
}
