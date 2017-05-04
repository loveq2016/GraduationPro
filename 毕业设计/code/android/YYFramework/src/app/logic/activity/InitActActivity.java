package app.logic.activity;

import org.ql.activity.customtitle.ActActivity;

import android.os.Bundle;

/*
 * GZYY    2016-8-10  上午11:34:47
 */

public abstract class InitActActivity extends ActActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initActTitleView();
		initView(savedInstanceState);
		initData();

	}

	protected abstract void initActTitleView();

	protected abstract void initView(Bundle savedInstanceState);

	protected abstract void initData();

}
