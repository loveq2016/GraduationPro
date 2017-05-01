package app.view;

import cn.jpush.android.api.c;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import app.yy.geju.R;

/*
 * GZYY    2016-8-3  下午4:22:08
 */

public class ZSZEditView extends RelativeLayout {

	private ImageView leftIv;
	private EditText rightEt;
	private View bottomView;
	private View view;
	private TypedArray ta;

	private String hint;
	private Boolean bottomViewShow;
	private Drawable leftIVDrawable;

	public ZSZEditView(Context context, AttributeSet attrs) {
		super(context, attrs);

		view = LayoutInflater.from(context).inflate(R.layout.zsz_views_edittext, this, true);
		ta = context.obtainStyledAttributes(attrs, R.styleable.ZSZEditeView);
		initAttrs();
		initView();
		initData();
		ta.recycle();
	}

	private void initAttrs() {
		leftIVDrawable = ta.getDrawable(R.styleable.ZSZEditeView_leftImageView);
		hint = ta.getString(R.styleable.ZSZEditeView_hint);
		bottomViewShow = ta.getBoolean(R.styleable.ZSZEditeView_bottomViewNoShow, false);

	}

	private void initView() {
		leftIv = (ImageView) view.findViewById(R.id.left_iv);
		rightEt = (EditText) view.findViewById(R.id.right_et);
		bottomView = (View) view.findViewById(R.id.bottom_view);
	}

	private void initData() {
		leftIv.setBackgroundDrawable(leftIVDrawable);
		rightEt.setHint(hint);

		if (bottomViewShow) {
			bottomView.setVisibility(GONE);
		}

	}

	/*
	 * 1是number，2是textPassword
	 */

	public void setInputTypeTextPassword(int i) {
		switch (i) {
		case 1:
			rightEt.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_VARIATION_NORMAL);
			break;
		case 2:
			rightEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

			break;

		default:
			break;
		}

	}

	// -----------------------------------------

	public Editable getText() {
		return rightEt.getText();
	}
}
