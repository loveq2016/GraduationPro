package app.logic.activity;

import org.ql.activity.customtitle.DefaultHandler;
import org.ql.utils.debug.UmengInterface;
import org.ql.utils.debug.UmengInterface.DebugCountListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import app.utils.common.Listener;
import app.yy.geju.R;

public class ActTitleHandler extends DefaultHandler {

	private Listener<Void, Void> onExitActListener;


	@Override
	public void onCreate(Activity activity) {
		super.onCreate(activity);
		rightButton.setVisibility(View.VISIBLE);
	}

	public void setOnExitActListener(Listener<Void, Void> l) {
		onExitActListener = l;
	}

	@Override
	public int getLayout() {
		return R.layout.act__ty;
	}
//	public View getTitleContentView(){
//
//		return findViewById(R.id.titleBackground);
//	}

	@Override
	public void onClick(View v) {
		if (v.getId() == android.R.id.button2) {
			if (onExitActListener != null) {
				onExitActListener.onCallBack(null, null);
			}
			if (activity == null || activity.isFinishing()) {
				return;
			}
			// Intent intent = new Intent();
			// int flags = Intent.FLAG_ACTIVITY_CLEAR_TOP |
			// Intent.FLAG_ACTIVITY_NEW_TASK;
			// intent.setFlags(flags);
			// // intent.setClass(activity, TYHomeActivity.class);
			// activity.startActivity(intent);
			// activity.finish();
			return;
		}
		super.onClick(v);
	}

	public void replaseLeftLayout(Context context, boolean status) {
		if (status) {
			this.addLeftView(LayoutInflater.from(context).inflate(R.layout.title_leftview_layout, null), true);
		}
	}

	UmengInterface.DebugCountListener countListener = new DebugCountListener() {

		@Override
		public void onResumeDebugCallBack() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPauseDebugCallBack() {
			// TODO Auto-generated method stub

		}
	};

}
