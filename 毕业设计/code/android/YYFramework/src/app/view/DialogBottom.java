package app.view;

import cn.jpush.a.a.a.c;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import app.yy.geju.R;

/*
 * GZYY    2016-12-15  下午2:04:44
 * author: zsz
 */

public class DialogBottom extends Dialog {

	private Context context;
	private View contentView;

	public DialogBottom(Context context, int themeResId) {
		super(context, themeResId);
		init(context);
	}

	public DialogBottom(Context context) {
		super(context, R.style.sex_dialog);
		init(context);
	}

	public DialogBottom(Context context, View contentView, int themeResId) {
		super(context, themeResId);
		this.contentView = contentView;
		this.context = context;
	}

	public DialogBottom(Context context, View contentView) {
		super(context, R.style.sex_dialog);
		this.contentView = contentView;
		this.context = context;
	}

	private void init(Context context) {
		this.context = context;

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		// getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

		this.setContentView(contentView);

		Window window = getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.gravity = Gravity.BOTTOM;
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		window.setAttributes(lp);

	}

}
