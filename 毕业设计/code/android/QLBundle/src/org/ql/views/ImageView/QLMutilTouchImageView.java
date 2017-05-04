/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ql.views.ImageView;
import org.ql.bundle.R;
import org.ql.utils.QLToastUtils;
import org.ql.views.QLViewPager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.Toast;

/**    
* 类名称：QLMutilTouchImageView <br>  
* 类描述：多点触摸imageview  <br>
* 创建者：anan <br>
* 创建时间：2012-12-27 上午9:42:33 <br>   
* 修改者：anan <br>    
* 修改时间：2012-12-27 上午9:42:33 <br>    
* 修改备注：  <br>
* @version    
*/   
public class QLMutilTouchImageView extends ImageView {
	private static final int DIALOG_SHOW_INTERVAL = 3*1000;// 3s
	/**缩放速度*/
	private static final float SCALE_RATE = 3f;
	/**最小缩小到*/
	private static final float SCALE_MIN_RATE = 1.0f;
	@SuppressWarnings("unused")
	private static final String TAG = "ImageViewTouchBase";

	// This is the base transformation which is used to show the image
	// initially. The current computation for this shows the image in
	// it's entirety, letterboxing as needed. One could choose to
	// show the image as cropped instead.
	//
	// This matrix is recomputed when we go from the thumbnail image to
	// the full size image.
	protected Matrix mBaseMatrix = new Matrix();

	// This is the supplementary transformation which reflects what
	// the user has done in terms of zooming and panning.
	//
	// This matrix remains the same when we go from the thumbnail image
	// to the full size image.
	protected Matrix mSuppMatrix = new Matrix();

	// This is the final matrix which is computed as the concatentation
	// of the base matrix and the supplementary matrix.
	private final Matrix mDisplayMatrix = new Matrix();

	// Temporary buffer used for getting the values out of a matrix.
	private final float[] mMatrixValues = new float[9];

	// The current bitmap being displayed.
	protected Bitmap mBitmapDisplayed;

	int mThisWidth = -1, mThisHeight = -1;

	float mMaxZoom;

	// 两点触屏后之间的长度
	private float mBeforeLenght = -1;
	private float mAfterLenght;
	private GestureDetector mListener;
	private float mScrollLength = -1;
	private boolean mIsCanScale;
	private long mLastDialogTime;
	
	private ViewParent scrollParent;
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		mThisWidth = right - left;
		mThisHeight = bottom - top;
		Runnable r = mOnLayoutRunnable;
		if (r != null) {
			mOnLayoutRunnable = null;
			r.run();
		}
		if (mBitmapDisplayed != null) {
			getProperBaseMatrix(mBitmapDisplayed, mBaseMatrix);
			setImageMatrix(getImageViewMatrix());
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN&&null!=scrollParent&&scrollParent instanceof QLViewPager){						
			((QLViewPager)scrollParent).setTouchIntercept(false);
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {// 2点触控
			mBeforeLenght = -1;
			mScrollLength = -1;
		}
		if (mListener.onTouchEvent(event)) {
			return true;
		}
		return true;
	}
	
	// 通过多点触屏放大或缩小图像 beforeLenght用来保存前一时间两点之间的距离 afterLenght用来保存当前时间两点之间的距离
	public void scaleWithFinger(MotionEvent event) {
		float X = event.getX(1) - event.getX(0);
		float Y = event.getY(1) - event.getY(0);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mBeforeLenght = (float) Math.sqrt((X * X) + (Y * Y));
			break;
		case MotionEvent.ACTION_MOVE:
			mAfterLenght = (float) Math.sqrt((X * X) + (Y * Y));// 得到两个点之间的长度
			float gapLenght = mAfterLenght - mBeforeLenght;
			if (gapLenght == 0) {
				break;
			}
			if (mBeforeLenght != -1) {
				// 如果当前时间两点距离大于前一时间两点距离，则传0，否则传1
				float rate = getScale() + gapLenght * SCALE_RATE / getWidth();
				if (!zoomTo(rate < SCALE_MIN_RATE ? SCALE_MIN_RATE : rate)
						&& System.currentTimeMillis() > mLastDialogTime
								+ DIALOG_SHOW_INTERVAL) {
					mLastDialogTime = System.currentTimeMillis();
					QLToastUtils.showToast(getContext(), R.string.image_to_max);
				}
			}
			mBeforeLenght = mAfterLenght;
			break;
		}
	}

	protected Handler mHandler = new Handler();

	public void setImageBitmap(Bitmap bitmap) {
		super.setImageBitmap(bitmap);
		Drawable d = getDrawable();
		if (d != null) {
			d.setDither(true);
		}
		mBitmapDisplayed = bitmap;
	}

	public void clear() {
		setImageBitmapResetBase(null, true);
	}

	private Runnable mOnLayoutRunnable = null;

	// This function changes bitmap, reset base matrix according to the size
	// of the bitmap, and optionally reset the supplementary matrix.
	public void setImageBitmapResetBase(final Bitmap bitmap,
			final boolean resetSupp) {
		setImageRotateBitmapResetBase(bitmap, resetSupp, false);
	}

	public void setImageRotateBitmapResetBase(final Bitmap bitmap,
			final boolean resetSupp, final boolean canScale) {
		final int viewWidth = getWidth();
		mIsCanScale = canScale;
		if (viewWidth <= 0) {
			mOnLayoutRunnable = new Runnable() {
				public void run() {
					setImageRotateBitmapResetBase(bitmap, resetSupp, canScale);
				}
			};
			return;
		}

		if (bitmap != null) {
			getProperBaseMatrix(bitmap, mBaseMatrix);
			setImageBitmap(bitmap);
		} else {
			mBaseMatrix.reset();
			setImageBitmap(null);
		}

		if (resetSupp) {
			mSuppMatrix.reset();
		}
		setImageMatrix(getImageViewMatrix());
		mMaxZoom = maxZoom();
	}

	// Center as much as possible in one or both axis. Centering is
	// defined as follows: if the image is scaled down below the
	// view's dimensions then center it (literally). If the image
	// is scaled larger than the view and is translated out of view
	// then translate it back into view (i.e. eliminate black bars).
	protected void center(boolean horizontal, boolean vertical) {
		if (mBitmapDisplayed == null) {
			return;
		}

		Matrix m = getImageViewMatrix();

		RectF rect = new RectF(0, 0, mBitmapDisplayed.getWidth(),
				mBitmapDisplayed.getHeight());
		m.mapRect(rect);
		float height = rect.height();
		float width = rect.width();
		float deltaX = 0, deltaY = 0;
		if (vertical) {
			int viewHeight = getHeight();
			if (height < viewHeight) {
				deltaY = (viewHeight - height) / 2 - rect.top;
			} else if (rect.top > 0) {
				deltaY = -rect.top;
			} else if (rect.bottom < viewHeight) {
				deltaY = getHeight() - rect.bottom;
			}
		}

		if (horizontal) {
			int viewWidth = getWidth();
			if (width < viewWidth) {
				deltaX = (viewWidth - width) / 2 - rect.left;
			} else if (rect.left > 0) {
				deltaX = -rect.left;
			} else if (rect.right < viewWidth) {
				deltaX = viewWidth - rect.right;
			}
		}

		postTranslate(deltaX, deltaY);
		setImageMatrix(getImageViewMatrix());
	}

	/**
	 * 是否移动过界了
	 * @param distanceX
	 * @return
	 */
	public boolean isScrollOver(float distanceX) {
		if (mDisplayMatrix != null) {
			float m_x = getValue(mDisplayMatrix, Matrix.MTRANS_X);
			float width = getWidth() - m_x;
			if ((m_x == 0 && distanceX <= 0)|| (width == mBitmapDisplayed.getWidth()* getValue(mDisplayMatrix, Matrix.MSCALE_X) && distanceX >= 0)) {
				return true;
			}
		}
		return false;
	}

	public QLMutilTouchImageView(Context context) {
		super(context);
		init();
	}

	public QLMutilTouchImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		setScaleType(ImageView.ScaleType.MATRIX);
		mListener = new GestureDetector(new MyGestureListener());
	}

	/**
	 * 获取矩阵的的某个值
	 * @param matrix
	 * @param whichValue
	 * @return
	 */
	protected float getValue(Matrix matrix, int whichValue) {
		matrix.getValues(mMatrixValues);
		return mMatrixValues[whichValue];
	}

	// Get the scale factor out of the matrix.
	protected float getScale(Matrix matrix) {
		return getValue(matrix, Matrix.MSCALE_X);
	}

	protected float getScale() {
		return getScale(mSuppMatrix);
	}

	// Setup the base matrix so that the image is centered and scaled properly.
	/**
	 * 恢复默认显示
	 * @param bitmap
	 * @param matrix
	 */
	private void getProperBaseMatrix(Bitmap bitmap, Matrix matrix) {
		float viewWidth = getWidth();
		float viewHeight = getHeight();

		float w = bitmap.getWidth();
		float h = bitmap.getHeight();
		matrix.reset();

		// We limit up-scaling to 3x otherwise the result may look bad if it's
		// a small icon.
		float widthScale = Math.min(viewWidth / w, 3.0f);
		float heightScale = Math.min(viewHeight / h, 3.0f);
		float scale = Math.min(widthScale, heightScale);

		matrix.postScale(scale, scale);

		matrix.postTranslate((viewWidth - w * scale) / 2F, (viewHeight - h
				* scale) / 2F);
	}

	/**
	 * 获取当下显示的矩阵
	 * @return
	 */
	protected Matrix getImageViewMatrix() {
		// The final matrix is computed as the concatentation of the base matrix
		// and the supplementary matrix.
		mDisplayMatrix.set(mBaseMatrix);
		mDisplayMatrix.postConcat(mSuppMatrix);
		return mDisplayMatrix;
	}

	/**
	 * 获取最大放大到的大小，为原图的4倍
	 * @return
	 */
	protected float maxZoom() {
		if (mBitmapDisplayed == null) {
			return 1F;
		}

		float fw = (float) mBitmapDisplayed.getWidth() / (float) mThisWidth;
		float fh = (float) mBitmapDisplayed.getHeight() / (float) mThisHeight;
		float max = Math.max(fw, fh) * 4;
		return max;
	}

	/**
	 * 局部放大，不移动
	 * @param scale
	 * @param centerX
	 * @param centerY
	 * @return
	 */
	protected boolean zoomTo(float scale, float centerX, float centerY) {
		boolean result = true;
		if (scale > mMaxZoom) {
			scale = mMaxZoom;
			result = false;
		}

		float oldScale = getScale();
		float deltaScale = scale / oldScale;

		mSuppMatrix.postScale(deltaScale, deltaScale, centerX, centerY);
		setImageMatrix(getImageViewMatrix());
		center(true, true);
		return result;
	}

	protected void zoomTo(final float scale, final float centerX,final float centerY, final float durationMs) {
		final float incrementPerMs = (scale - getScale()) / durationMs;
		final float oldScale = getScale();
		final long startTime = System.currentTimeMillis();

		mHandler.post(new Runnable() {
			public void run() {
				long now = System.currentTimeMillis();
				float currentMs = Math.min(durationMs, now - startTime);
				float target = oldScale + (incrementPerMs * currentMs);
				zoomTo(target, centerX, centerY);

				if (currentMs < durationMs) {
					mHandler.post(this);
				}
			}
		});
	}

	/**
	 * 缩放到指定比例
	 * @param scale
	 * @return
	 */
	protected boolean zoomTo(float scale) {
		if(mIsCanScale){
			float cx = getWidth() / 2F;
			float cy = getHeight() / 2F;
			return zoomTo(scale, cx, cy);
		}
		return true;
	}

	/**
	 * 局部放大
	 * @param scale
	 * @param pointX
	 * @param pointY
	 */
	protected void zoomToPoint(float scale, float pointX, float pointY) {
		if(mIsCanScale){
			float cx = getWidth() / 2F;
			float cy = getHeight() / 2F;
			
			panBy(cx - pointX, cy - pointY);
			zoomTo(scale, cx, cy);
		}
	}

	protected void zoomIn(float rate) {
		if (getScale() >= mMaxZoom) {
			return; // Don't let the user zoom into the molecular level.
		}
		if (mBitmapDisplayed == null) {
			return;
		}

		float cx = getWidth() / 2F;
		float cy = getHeight() / 2F;

		mSuppMatrix.postScale(rate, rate, cx, cy);
		setImageMatrix(getImageViewMatrix());
	}

	protected void zoomOut(float rate) {
		if (mBitmapDisplayed == null) {
			return;
		}

		float cx = getWidth() / 2F;
		float cy = getHeight() / 2F;

		// Zoom out to at most 1x.
		Matrix tmp = new Matrix(mSuppMatrix);
		tmp.postScale(1F / rate, 1F / rate, cx, cy);

		if (getScale(tmp) < 1F) {
			mSuppMatrix.setScale(1F, 1F, cx, cy);
		} else {
			mSuppMatrix.postScale(1F / rate, 1F / rate, cx, cy);
		}
		setImageMatrix(getImageViewMatrix());
		center(true, true);
	}

	/**
	 * 移动矩阵
	 * @param dx
	 * @param dy
	 */
	protected void postTranslate(float dx, float dy) {
		mSuppMatrix.postTranslate(dx, dy);
	}

	/**
	 * 移动图片
	 * @param dx
	 * @param dy
	 */
	protected void panBy(float dx, float dy) {
		postTranslate(dx, dy);
		setImageMatrix(getImageViewMatrix());
	}

	private class MyGestureListener extends	GestureDetector.SimpleOnGestureListener {
		private static final int MIN_DISTANCE = 10;

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,	float velocityY) {
			return true;
		}

		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,float distanceX, float distanceY) {
			if (e2.getPointerCount() == 2) {// 2点触控
				scaleWithFinger(e2);// 调用控制图片大小的方法
				return false;
			} else if (e2.getPointerCount() == 1) {// 单点触摸
				if (getScale() > 1F && !isScrollOver(distanceX)) {
					postTranslateCenter(-distanceX, -distanceY);
					if(null!=scrollParent){						
						scrollParent.requestDisallowInterceptTouchEvent(true);
					}
				} else if (Math.abs(distanceX) - mScrollLength >= MIN_DISTANCE) {
					mScrollLength = Math.abs(distanceX);
					if(null!=scrollParent){						
						scrollParent.requestDisallowInterceptTouchEvent(true);
					}
				} else{
					if(null!=scrollParent){						
						scrollParent.requestDisallowInterceptTouchEvent(false);
					}
				}
			}
			return true;
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			return true;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			return true;
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			// Switch between the original scale and 3x scale.
			if (getScale() > 2F) {
				zoomTo(1f);
			} else {
				zoomToPoint(3f, e.getX(), e.getY());
			}
			return true;
		}
	}

	protected void postTranslateCenter(float dx, float dy) {
		postTranslate(dx, dy);
		center(true, true);
	}

	/**
	 * 设置父控件可以滚动的对象用于处理横向滚动事件冲突
	 * @param scrollParent
	 */
	public void setScrollParent(ViewParent scrollParent){
		this.scrollParent = scrollParent;
	}
}
