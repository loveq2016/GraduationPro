package org.ql.activity.customtitle;

import org.ql.bundle.R;
import org.ql.utils.debug.QLLog;

import android.R.integer;
import android.app.Activity;
import android.os.Build;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DefaultHandler extends SystemHandler implements
		View.OnClickListener {

	protected LinearLayout leftLayout, rightLayout;
	protected RelativeLayout centerLayout;
	protected ImageView titleImageView;
	protected TextView titleTextView;
	protected Button leftButton, rightButton;
	protected Toolbar titleToolbar;

	public LinearLayout getLeftLayout() {
		return leftLayout;
	}

	public LinearLayout getRightLayout() {
		return rightLayout;
	}

	public Button getLeftDefButton() {
		return leftButton;
	}

	public TextView getTitleTextView() {
		return titleTextView;
	}

	public RelativeLayout getCenterLayout() {
		return centerLayout;
	}

	public Button getRightDefButton() {
		if (rightButton != null)
			rightButton.setVisibility(View.VISIBLE);
		return rightButton;
	}

	public void addLeftView(View view, boolean removeAll) {
		if (activity == null || leftLayout == null || view == null)
			return;
		if (removeAll)
			leftLayout.removeAllViews();
		leftLayout.addView(view);
	}

	public void addRightView(View view, boolean removeAll) {
		if (activity == null || leftLayout == null || view == null)
			return;
		if (removeAll)
			rightLayout.removeAllViews();
		rightLayout.addView(view);
	}

	@Override
	public int getLayout() {
		return R.layout.act__default;
		// return R.layout.act_yy;
	}

	@Override
	public void onCreate(Activity activity) {
//		setBackgroudColor(0xffff0000);
		super.onCreate(activity);
		// titleToolbar = (Toolbar)activity.findViewById(R.id.titlebar);
		leftLayout = (LinearLayout) activity.findViewById(R.id.leftLayout);
		rightLayout = (LinearLayout) activity.findViewById(R.id.rightLayout);
		leftButton = (Button) activity.findViewById(android.R.id.button1);
		rightButton = (Button) activity.findViewById(android.R.id.button2);

		centerLayout = (RelativeLayout) activity
				.findViewById(R.id.centerLayout);
		titleImageView = (ImageView) activity.findViewById(R.id.centerIv);
		titleTextView = (TextView) activity.findViewById(R.id.title);

		leftButton.setOnClickListener(this);
		rightButton.setOnClickListener(this);
		centerLayout.setOnClickListener(this);

	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		/*
		 * int leftW = leftLayout.getWidth(); int rightW =
		 * rightLayout.getWidth();
		 * 
		 * int w = leftW > rightW ? leftW : rightW;
		 * leftLayout.getLayoutParams().width = w;
		 * rightLayout.getLayoutParams().width = w;
		 */
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == android.R.id.button1) {
			activity.onBackPressed();
		} else if (id == android.R.id.button2) {

		}
	}

}
