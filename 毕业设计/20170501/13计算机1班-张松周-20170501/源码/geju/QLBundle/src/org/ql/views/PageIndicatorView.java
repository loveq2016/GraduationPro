package org.ql.views;

import org.ql.bundle.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

//走马灯指示器
public class PageIndicatorView extends View {
	private Context mContext;
	private int currentPage = -1;
	private int totalPage = 0;
	private boolean cycle = false;
	private int space = 5;
	private int iconWidth = 20;
	private int iconHeight = 20;
	
	private Bitmap bmpDefault,bmpSelected = null;



	public PageIndicatorView(Context context) {
		super(context);
		this.mContext = context;
	}

	public PageIndicatorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
	}

	public PageIndicatorView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
	}


	public boolean isCycle() {
		return cycle;
	}

	public void setCycle(boolean cycle) {
		this.cycle = cycle;
	}

	public int getSpace() {
		return space;
	}

	public void setSpace(int space) {
		this.space = space;
	}

	public int getIconWidth() {
		return iconWidth;
	}

	public void setIconWidth(int iconWidth) {
		this.iconWidth = iconWidth;
	}

	public int getIconHeight() {
		return iconHeight;
	}

	public void setIconHeight(int iconHeight) {
		this.iconHeight = iconHeight;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int nPageNum) {
		totalPage = nPageNum;
		if (currentPage >= totalPage)
			currentPage = totalPage - 1;
	}
	
	public int getCurrentPage() {
		return currentPage;
	}
	
	public void setCurrentPage(int nPageIndex) {
		if (!cycle && (nPageIndex < 0 || nPageIndex >= totalPage))
			return;
		else if(nPageIndex < 0)
			nPageIndex = totalPage-1;
		else if(nPageIndex >= totalPage)
			nPageIndex = 0;
		if (currentPage != nPageIndex) {
			currentPage = nPageIndex;
			this.invalidate();
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(bmpDefault == null)
			bmpDefault = BitmapFactory.decodeResource(getResources(), R.drawable.round_image_switcher_default);
		if(bmpSelected == null)
			bmpSelected = BitmapFactory.decodeResource(getResources(), R.drawable.round_image_selected);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.BLACK);
		
		Rect r = new Rect();
		this.getDrawingRect(r);

		int x = (r.width() - (iconWidth * totalPage + space * (totalPage - 1))) / 2;
		int y = (r.height() - iconHeight) / 2;

		for (int i = 0; i < totalPage; i++) {
			Rect r1 = new Rect();
			r1.left = x;
			r1.top = y;
			r1.right = x + iconWidth;
			r1.bottom = y + iconHeight;
			canvas.drawBitmap(i==currentPage?bmpSelected:bmpDefault, null, r1, paint);
			x += iconWidth + space;
		}
	}

	public void DrawImage(Canvas canvas, Bitmap mBitmap, int x, int y, int w, int h, int bx, int by) {
		Rect src = new Rect();
		Rect dst = new Rect();
		src.left = bx;
		src.top = by;
		src.right = bx + w;
		src.bottom = by + h;

		dst.left = x;
		dst.top = y;
		dst.right = x + w;
		dst.bottom = y + h;

		// canvas.drawBitmap(mBitmap, src, dst, mPaint);
		src = null;
		dst = null;
	}

}
