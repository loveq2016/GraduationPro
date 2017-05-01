package app.logic.activity;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import app.yy.geju.R;
/**
 * 
 * @author SuiJiYung
 *</br>
 */
public abstract class BasePageActivity extends TYActivity{
	
	protected ViewPager pageView;
	protected int pageWidth;
	protected ArrayList<View> pageList;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		pageView = (ViewPager)this.findViewById(R.id.viewpager);
		pageList = new ArrayList<View>();
		
	}
	
	public void setPageWidth(int pageWidth) {
		this.pageWidth = pageWidth;
	}
	
	public int getPageWidth(){
		return this.pageWidth;
	}
}
