package app.view;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import app.yy.geju.R;

/*
 * GZYY    2016-12-15  上午11:00:27
 * author: zsz
 */

public class DialogFragmentView extends android.support.v4.app.DialogFragment {

	@Override
	@NonNull
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		Dialog dialog = new Dialog(getActivity(), R.style.ZSZDialog);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_sex_dialogfragment);
		dialog.setCanceledOnTouchOutside(true);

		Window window = dialog.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.gravity = Gravity.BOTTOM;
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		window.setAttributes(lp);
		

		return dialog;

	}
	

}
