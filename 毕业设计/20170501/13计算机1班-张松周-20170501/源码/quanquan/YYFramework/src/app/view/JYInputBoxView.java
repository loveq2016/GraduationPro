package app.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import app.yy.geju.R;
/**
 * 
 * @author SiuJiYung
 * create at 2013-12-5 下午3:34:37
 * </br>
 * 自定义输入框
 */
public class JYInputBoxView extends FrameLayout{

	private ImageView inputBoxBgView;
	private ImageView inputBoxIconView;
	private EditText inputBoxView;
	private TextView inputBoxTitleView;
	private String title;
	
	public JYInputBoxView(Context context,AttributeSet attrs){
		super(context, attrs);
		this.init(context);
		TypedArray typedList= context.obtainStyledAttributes(attrs,R.styleable.JYInputBoxViewStype); 
		String tmpTitle = typedList.getString(R.styleable.JYInputBoxViewStype_inputBoxTitle);
		Drawable tmpIconBitmap = typedList.getDrawable(R.styleable.JYInputBoxViewStype_inputBoxIcon);
		BitmapDrawable bdBitmapDrawable = (BitmapDrawable)tmpIconBitmap;
		this.setTitle(tmpTitle);
		if (bdBitmapDrawable != null) {
			this.setTitleIcon(bdBitmapDrawable.getBitmap());
		}
//		typedList.getInt(R.styleable.j, defValue)
		typedList.recycle();
	}

	private void init(Context context){
		LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mInflater.inflate(R.layout.item_edittextview,this);
		inputBoxBgView = (ImageView)this.findViewById(R.id.item_inputBoxBgView);
		inputBoxIconView = (ImageView)this.findViewById(R.id.item_editTextViewIcon);
		inputBoxTitleView = (TextView)this.findViewById(R.id.item_editTextViewTitle);
		inputBoxView = (EditText)this.findViewById(R.id.item_editTextViewContenBox);
		inputBoxView.setBackgroundColor(Color.WHITE);
	}
	
	public void setInputType(int type){
		inputBoxView.setInputType(type);
	}
	
	public String getInputText(){
		String strTmpString = inputBoxView.getText().toString();
		return strTmpString;
	}
	
	public void setInputText(String text){
		inputBoxView.setText(text);
	}
	
	public void setBackgroudImage(Bitmap backgroudImg){
		inputBoxBgView.setImageBitmap(backgroudImg);
	}
	
	public void setTitleIcon(Bitmap icon){
		inputBoxIconView.setImageBitmap(icon);
	}
	
	public void setTitleIconWithResId(int resId){
		inputBoxIconView.setImageResource(resId);
	}
	
	public void setTitle(String title){
		this.title = title;
		inputBoxTitleView.setText(this.title+":");
		inputBoxView.setHint(this.title);
	}
	
	public String getTitle(){
		return this.title;
	}
	
}
