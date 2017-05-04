package app.utils.customview;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import app.yy.geju.R;

public class AppDialog extends Dialog{

	private Context context;
	public AppDialog(Context context) {
		this(context, R.style.app_dialog);
	}
	
	public AppDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
	}

	public AppDialog(Context context, boolean cancelable) {
		super(context, R.style.app_dialog);
		setCancelable(cancelable);
		this.context = context;
	}
	
	public AppDialog(Context context,OnCancelListener cancelListener) {
		super(context, R.style.app_dialog);
		setOnCancelListener(cancelListener);
		this.context = context;
	}
	
	public AppDialog(Context context, boolean cancelable,OnCancelListener cancelListener) {
		super(context, R.style.app_dialog);
		setCancelable(cancelable);
		setOnCancelListener(cancelListener);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_dialog);
	}

	@Override
	public void show() {
		if(context != null && context instanceof Activity && !((Activity)context).isFinishing())
			super.show();
	}
}
