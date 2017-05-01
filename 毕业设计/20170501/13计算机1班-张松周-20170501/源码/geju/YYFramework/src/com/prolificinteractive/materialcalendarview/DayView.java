package com.prolificinteractive.materialcalendarview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
//import android.graphics.AvoidXfermode.Mode;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.provider.ContactsContract.Contacts.Data;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckedTextView;
import app.logic.singleton.ZSZSingleton;
import app.logic.singleton.ZSZSingleton.changeOrgToBlUListener;
import app.yy.geju.R;

import com.prolificinteractive.materialcalendarview.MaterialCalendarView.ShowOtherDates;
import com.prolificinteractive.materialcalendarview.format.DayFormatter;

import java.util.List;

import org.ql.utils.QLToastUtils;

import static com.prolificinteractive.materialcalendarview.MaterialCalendarView.showDecoratedDisabled;
import static com.prolificinteractive.materialcalendarview.MaterialCalendarView.showOtherMonths;
import static com.prolificinteractive.materialcalendarview.MaterialCalendarView.showOutOfRange;

/**
 * Display one day of a {@linkplain MaterialCalendarView}
 */
@SuppressLint("ViewConstructor")
public class DayView extends CheckedTextView {

	private CalendarDay date;
	private int selectionColor = Color.GRAY ; //Color.GRAY

	private final int fadeTime;
	private Drawable customBackground = null;
	private Drawable selectionDrawable;
	private Drawable mCircleDrawable;
	private Drawable mFlagDrawable;
	private DayFormatter formatter = DayFormatter.DEFAULT;

	private Context context;

	private boolean isInRange = true;
	private boolean isInMonth = true;
	private boolean isDecoratedDisabled = false;
	@ShowOtherDates
	private int showOtherDates = MaterialCalendarView.SHOW_DEFAULTS;

	private int default_px = (int) getResources().getDimension(R.dimen.dp_10);

	@SuppressLint("NewApi")
	public DayView(Context context, CalendarDay day) {
		super(context);
		this.context = context;

		fadeTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

		setSelectionColor(this.selectionColor);

		setGravity(Gravity.CENTER);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			setTextAlignment(TEXT_ALIGNMENT_CENTER);
		}

		setDay(day);

		// setBackgroundColor(context.getResources().getColor(R.color.text_tiyuhui_label));
	}

	public void setDay(CalendarDay date) {
		this.date = date;
		// setText(getLabel());
		setText(" ");
	}

	
	

	/**
	 * Set the new label formatter and reformat the current label. This
	 * preserves current spans.
	 * 
	 * @param formatter
	 *            new label formatter
	 */
	public void setDayFormatter(DayFormatter formatter) {
		this.formatter = formatter == null ? DayFormatter.DEFAULT : formatter;
		CharSequence currentLabel = getText();
		Object[] spans = null;
		if (currentLabel instanceof Spanned) {
			spans = ((Spanned) currentLabel).getSpans(0, currentLabel.length(), Object.class);
		}
		SpannableString newLabel = new SpannableString(getLabel());
		if (spans != null) {
			for (Object span : spans) {
				newLabel.setSpan(span, 0, newLabel.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
		// setText(newLabel);
		setText(" ");
	}

	@NonNull
	public String getLabel() {
		return formatter.format(date);
	}

	public void setSelectionColor(int color) {
		this.selectionColor = color;
		regenerateBackground();
	}

	/**
	 * @param drawable
	 *            custom selection drawable
	 */
	public void setSelectionDrawable(Drawable drawable) {
		if (drawable == null) {
			this.selectionDrawable = null;
		} else {
			this.selectionDrawable = drawable.getConstantState().newDrawable(getResources());
		}
		regenerateBackground();
	}

	/**
	 * @param drawable
	 *            background to draw behind everything else
	 */
	public void setCustomBackground(Drawable drawable) {
		if (drawable == null) {
			this.customBackground = null;
		} else {
			this.customBackground = drawable.getConstantState().newDrawable(getResources());
		}
		invalidate();
	}

	public CalendarDay getDate() {
		return date;
	}

	private void setEnabled() {
		boolean enabled = isInMonth && isInRange && !isDecoratedDisabled;
		super.setEnabled(isInRange && !isDecoratedDisabled);

		boolean showOtherMonths = showOtherMonths(showOtherDates);
		boolean showOutOfRange = showOutOfRange(showOtherDates) || showOtherMonths;
		boolean showDecoratedDisabled = showDecoratedDisabled(showOtherDates);

		boolean shouldBeVisible = enabled;

		if (!isInMonth && showOtherMonths) {
			shouldBeVisible = true;
		}

		if (!isInRange && showOutOfRange) {
			shouldBeVisible |= isInMonth;
		}

		if (isDecoratedDisabled && showDecoratedDisabled) {
			shouldBeVisible |= isInMonth && isInRange;
		}

		if (!isInMonth && shouldBeVisible) {
			setTextColor(getTextColors().getColorForState(new int[] { -android.R.attr.state_enabled }, Color.GRAY));
		}
		setVisibility(shouldBeVisible ? View.VISIBLE : View.INVISIBLE);
	}

	protected void setupSelection(@ShowOtherDates int showOtherDates, boolean inRange, boolean inMonth) {
		this.showOtherDates = showOtherDates;
		this.isInMonth = inMonth;
		this.isInRange = inRange;
		setEnabled();
	}

	private final Rect tempRect = new Rect();
	Paint paint = new Paint();
	

	@Override
	protected void onDraw(@NonNull Canvas canvas) {
		super.onDraw(canvas);
		paint.setAntiAlias(true);  //画笔设为抗锯齿
		if (customBackground != null) {
			customBackground.setBounds( tempRect );
			customBackground.setState( getDrawableState() );
			customBackground.draw(canvas);
		}
		mCircleDrawable.setBounds(tempRect);
		// mFlagDrawable.setBounds(tempRect);
		float cx = getWidth() / 2;
		float cy = getHeight() / 2;
		paint.setColor(0xffffffff); //设置画笔的颜色0xff000000黑色 ，该白色
		paint.setTextSize(getTextSize());
		//|| (ZSZSingleton.getZSZSingleton().getDate() !=null && ZSZSingleton.getZSZSingleton().getDate().getDate() == date.getDay() )
		if ( isPressed() || isChecked()) {
			paint.setColor(0xffffffff);     //选中时画笔的颜色			
			mCircleDrawable.draw(canvas);
								
		} else if ( ZSZSingleton.getZSZSingleton().getFlagDays() != null) {
			   for ( CalendarDay calendarDay : ZSZSingleton.getZSZSingleton().getFlagDays() ) {
				   if (date.getDay() == calendarDay.getDay() && date.getMonth() == calendarDay.getMonth() && date.getYear() == calendarDay.getYear()) {					      					
						int temp = (int) (default_px / 1.5);											
						int xy = (int) ((cx-temp)-1) ;
						paint.setStyle(Paint.Style.FILL);   //FILL为实心 
						canvas.drawCircle( cx , cy + xy , 5 , paint );  //画圆
						paint.reset();
						paint.setTextSize(getTextSize());
						paint.setColor(0xffffffff);  //画完后设置为白色
					}
			 }
		}		
		CalendarDay today = ZSZSingleton.getZSZSingleton().getToday();
		if( today != null ){			
			if( date.getYear() == today.getYear() && date.getMonth() == today.getMonth() && date.getDay() == today.getDay()){
				paint.setColor(0xcccccccc);
				int temp = (int) (default_px / 1.5);											
				paint.setStyle( Paint.Style.STROKE );   
				canvas.drawCircle( cx, cy, cx - temp, paint );  //画圆
				paint.reset();
				paint.setTextSize(getTextSize());
				paint.setColor(0xffffffff);  //画完后设置为白色
			}
		}
		
		Rect rect = new Rect();
		paint.getTextBounds(getLabel(), 0 , getLabel().length() , rect );
		int strwid = rect.width()/2;
		int strhei = rect.height()/2;
		canvas.drawText( getLabel(), cx-strwid, cy+strhei, paint); //绘制文本

	}

	private void regenerateBackground() {
		if (selectionDrawable != null) {
			// setBackgroundDrawable(selectionDrawable);
		} else {
			mCircleDrawable = generateBackground(selectionColor, fadeTime, tempRect);
			// setBackgroundDrawable(mCircleDrawable);
			mFlagDrawable = flagBackground(Color.parseColor("#FF703E"));
		}
	}

	// 日程标志颜色
	private static Drawable flagBackground(int color) {
		StateListDrawable drawable = new StateListDrawable();
		drawable.addState(new int[] {}, generateCircleDrawable(color));
		return drawable;
	}

	private static Drawable generateBackground(int color, int fadeTime, Rect bounds) {
		StateListDrawable drawable = new StateListDrawable();
		drawable.setExitFadeDuration(fadeTime);
		// drawable.addState(new int[] { android.R.attr.state_checked },
		// generateCircleDrawable(color));
		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
		// drawable.addState(new int[] { android.R.attr.state_pressed },
		// generateRippleDrawable(color, bounds));
		// } else {
		// drawable.addState(new int[] { android.R.attr.state_pressed },
		// generateCircleDrawable(color));
		// }
		drawable.addState(new int[] {}, generateCircleDrawable(color));
		// drawable.addState(new int[] {},
		// generateCircleDrawable(Color.TRANSPARENT));

		return drawable;
	}

	private static Drawable generateCircleDrawable(final int color) {
		ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
		drawable.getPaint().setColor(color);
		return drawable;
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private static Drawable generateRippleDrawable(final int color, Rect bounds) {
		ColorStateList list = ColorStateList.valueOf(color);
		Drawable mask = generateCircleDrawable(Color.WHITE);
		RippleDrawable rippleDrawable = new RippleDrawable(list, null, mask);
		// API 21
		if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
			rippleDrawable.setBounds(bounds);
		}

		// API 22. Technically harmless to leave on for API 21 and 23, but not
		// worth risking for 23+
		if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP_MR1) {
			int center = (bounds.left + bounds.right) / 2;
			rippleDrawable.setHotspotBounds(center, bounds.top, center, bounds.bottom);
		}

		return rippleDrawable;
	}

	/**
	 * @param facade
	 *            apply the facade to us
	 */
	@SuppressLint("NewApi")
	void applyFacade(DayViewFacade facade) {
		this.isDecoratedDisabled = facade.areDaysDisabled();
		setEnabled();
		setCustomBackground(facade.getBackgroundDrawable());
		setSelectionDrawable(facade.getSelectionDrawable());

		// Facade has spans
		List<DayViewFacade.Span> spans = facade.getSpans();
		if (!spans.isEmpty()) {
			String label = getLabel();
			SpannableString formattedLabel = new SpannableString(getLabel());
			for (DayViewFacade.Span span : spans) {
				formattedLabel.setSpan(span.span, 0, label.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			setText(formattedLabel);
		}
		// Reset in case it was customized previously
		else {
			// setText(getLabel());
			setText(" ");
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		int width = right - left;
		int height = bottom - top;
		// int r = Math.min(width, height);
		calculateBounds(width, height);
		regenerateBackground();
	}

	private void calculateBounds(int width, int height) {
		final int radius = Math.min(height, width);
		// Lollipop platform bug. Rect offset needs to be divided by 4 instead
		// of 2
		final int offsetDivisor = Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP ? 4 : 2;
		final int offset = Math.abs(height - width) / offsetDivisor;

		int temp = (int) (default_px / 1.5);

		if (width >= height) {
			tempRect.set(offset + temp, 0 + temp, radius + offset - temp, height - temp);
		} else {
			tempRect.set(0, offset, width, radius + offset);
		}
	}
}
