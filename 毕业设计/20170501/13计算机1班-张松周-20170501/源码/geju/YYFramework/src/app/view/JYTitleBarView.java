package app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import app.yy.geju.R;

public class JYTitleBarView extends LinearLayout{

	private ImageButton leftBtn;
	private ImageButton rightBtn;
	private TextView titleView;
	
	public JYTitleBarView(Context context){
		super(context);
	}
	
	public JYTitleBarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.initView(context);
	}
	
	private void initView(Context context){
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.theme_titlebar, this);
		leftBtn = (ImageButton)this.findViewById(R.id.btn_title_left);
		rightBtn = (ImageButton)this.findViewById(R.id.btn_title_right);
		titleView = (TextView)this.findViewById(R.id.title);
		
		leftBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
	}

	public ImageButton getLeftBtn() {
		return leftBtn;
	}

	public ImageButton getRightBtn() {
		return rightBtn;
	}

	public TextView getTitleView() {
		return titleView;
	}
}
