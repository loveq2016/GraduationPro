package app.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class JYCheckButton extends ImageButton{

	class CheckStatus{
		int code;
		Drawable drawable;
	}
	
	private ArrayList<CheckStatus> statuList;
	private CheckStatus currStatus;
	private CheckStatus defaultStatus;
	
	public JYCheckButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		statuList = new ArrayList<JYCheckButton.CheckStatus>();
		defaultStatus = new CheckStatus();
		defaultStatus.code = 1;
	}
	
	public void setDefaultImage(Drawable defDrawable){
		defaultStatus.drawable = defDrawable;
	}

	public void addCheckStatus(int statuCode,Drawable drawable){
		CheckStatus status = new CheckStatus();
		status.code = statuCode;
		status.drawable = drawable==null?defaultStatus.drawable:drawable;
		statuList.add(status);
	}
	
	public void setToStatu(int code){
		
	}
	
	public int getStatu(){
		return currStatus.code;
	}
}
