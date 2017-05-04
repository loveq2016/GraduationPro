package app.view;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import app.yy.geju.R;

/*
 * GZYY    2016-12-15  上午11:45:20
 * author: zsz
 */

public abstract class YYDialogFragment extends DialogFragment {

	private int contentView;

	private int style = 0;

	@Override
	@NonNull
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		if (style == 0) {
			style = R.style.ZSZDialog;
		}
		Dialog dialog = new Dialog(getActivity(), style);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		dialog.setContentView(contentView);

		dialog.setCanceledOnTouchOutside(true);

		// 设置宽度为屏宽，靠近屏幕底部
		Window window = dialog.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.gravity = Gravity.BOTTOM;
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		window.setAttributes(lp);

		bindingDialogViewId();
		logic();

		return dialog;

	}

	public void setDialogStyle(int style) {
		this.style = style;
	}

	/**
	 * 设置view
	 * 
	 * @param layout
	 *            setDialogContentViewYYDialogFragment
	 */
	public void setDialogContentView(int layout) {
		this.contentView = layout;
	}

	public abstract void bindingDialogViewId();

	public abstract void logic();

}
