package org.ql.views.gridview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**    
* 类名称：QLNotScrollGridView <br>  
* 类描述：全部展开的gridview  <br>
* 创建者：anan <br>
* 创建时间：2013-1-25 下午12:32:38 <br>   
* 修改者：anan <br>    
* 修改时间：2013-1-25 下午12:32:38 <br>    
* 修改备注：  <br>
* @version    
*/   
public class QLNotScrollGridView extends GridView {

	private boolean haveScrollbar = false;

	public QLNotScrollGridView(Context context) {
		super(context);
	}

	public QLNotScrollGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public QLNotScrollGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * 设置是否有ScrollBar，当要在ScollView中显示时，应当设置为false。 默认为 true
	 * 
	 * @param haveScrollbars
	 */
	public void setHaveScrollbar(boolean haveScrollbar) {
		this.haveScrollbar = haveScrollbar;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (haveScrollbar == false) {
			int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
			super.onMeasure(widthMeasureSpec, expandSpec);
		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}
}
