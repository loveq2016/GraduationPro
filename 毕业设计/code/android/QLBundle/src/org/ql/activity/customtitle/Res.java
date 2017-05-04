package org.ql.activity.customtitle;

import android.app.Activity;
import android.content.Context;

public final class Res{
		
	private final Activity mActivity;
	public Res(Activity mActivity){
		this.mActivity = mActivity;
	}
	
	public final static String ID = "id";
	public final static String XML = "xml";
	public final static String RAW = "raw";
	public final static String ATTR = "attr";
	public final static String ANIM = "anim";
	public final static String ARRAY = "array";
	public final static String STYLE = "style";
	public final static String COLOR = "color";
	public final static String STRING = "string";
	public final static String LAYOUT = "layout";
	public final static String DRAWABLE = "drawable";
	
	public int xml(String name){
		return xml(mActivity,name);
	}
	public int xml(Context context,String name){
		return xml(context,name,context.getPackageName());
	}
	public int xml(String name,String defPackage){
		return xml(mActivity,name,defPackage);
	}
	public int xml(Context context,String name, String defPackage){
		return res(context,name,XML,defPackage);
	}
	
	public int style(String name){
		return style(mActivity,name);
	}
	public int style(Context context,String name){
		return style(context,name,context.getPackageName());
	}
	public int style(String name,String defPackage){
		return style(mActivity,name,defPackage);
	}
	public int style(Context context,String name, String defPackage){
		return res(context,name,STYLE,defPackage);
	}
	
	public int string(String name){
		return string(mActivity,name);
	}
	public int string(Context context,String name){
		return string(context,name,context.getPackageName());
	}
	public int string(String name,String defPackage){
		return string(mActivity,name,defPackage);
	}
	public int string(Context context,String name, String defPackage){
		return res(context,name,STRING,defPackage);
	}
	
	public int layout(String name){
		return layout(mActivity,name);
	}
	public int layout(Context context,String name){
		return layout(context,name,context.getPackageName());
	}
	public int layout(String name,String defPackage){
		return layout(mActivity,name,defPackage);
	}
	public int layout(Context context,String name, String defPackage){
		return res(context,name,LAYOUT,defPackage);
	}
	
	public int id(String name){
		return id(mActivity,name);
	}
	public int id(Context context,String name){
		return id(context,name,context.getPackageName());
	}
	public int id(String name,String defPackage){
		return id(mActivity,name,defPackage);
	}
	public int id(Context context,String name, String defPackage){
		return res(context,name,ID,defPackage);
	}
	
	public int drawable(String name){
		return drawable(mActivity,name);
	}
	public int drawable(Context context,String name){
		return drawable(context,name,context.getPackageName());
	}
	public int drawable(String name,String defPackage){
		return drawable(mActivity,name,defPackage);
	}
	public int drawable(Context context,String name, String defPackage){
		return res(context,name,DRAWABLE,defPackage);
	}
	
	public int color(String name){
		return color(mActivity,name);
	}
	public int color(Context context,String name){
		return color(context,name,context.getPackageName());
	}
	public int color(String name,String defPackage){
		return color(mActivity,name,defPackage);
	}
	public int color(Context context,String name, String defPackage){
		return res(context,name,COLOR,defPackage);
	}
	
	public int attr(String name){
		return attr(mActivity,name);
	}
	public int attr(Context context,String name){
		return attr(context,name,context.getPackageName());
	}
	public int attr(String name,String defPackage){
		return attr(mActivity,name,defPackage);
	}
	public int attr(Context context,String name, String defPackage){
		return res(context,name,ATTR,defPackage);
	}
	
	public int array(String name){
		return array(mActivity,name);
	}
	public int array(Context context,String name){
		return array(context,name,context.getPackageName());
	}
	public int array(String name,String defPackage){
		return array(mActivity,name,defPackage);
	}
	public int array(Context context,String name, String defPackage){
		return res(context,name,ARRAY,defPackage);
	}
	
	public int anim(String name){
		return anim(mActivity,name);
	}
	public int anim(Context context,String name){
		return anim(context,name,context.getPackageName());
	}
	public int anim(String name,String defPackage){
		return anim(mActivity,name,defPackage);
	}
	public int anim(Context context,String name, String defPackage){
		return res(context,name,ANIM,defPackage);
	}
	
	public int raw(String name){
		return raw(mActivity,name);
	}
	public int raw(Context context,String name){
		return raw(context,name,context.getPackageName());
	}
	public int raw(String name,String defPackage){
		return raw(mActivity,name,defPackage);
	}
	public int raw(Context context,String name, String defPackage){
		return res(context,name,RAW,defPackage);
	}
	
	public int res(String name, String defType){
		return res(mActivity,name, defType);
	}
	public int res(Context context,String name, String defType){
		return res(context,name, defType,context.getPackageName());
	}
	public int res(String name, String defType, String defPackage){
		return res(mActivity,name, defType,defPackage);
	}
	public int res(Context context,String name, String defType, String defPackage){
		return context.getResources().getIdentifier(name, defType,defPackage);
	}
	
}