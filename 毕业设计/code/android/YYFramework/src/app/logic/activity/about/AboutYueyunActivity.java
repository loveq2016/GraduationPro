package app.logic.activity.about;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import app.logic.activity.ActTitleHandler;
import app.logic.activity.InitActActivity;
import app.utils.helpers.SystemBuilderUtils;
import app.yy.geju.R;

/*
 * GZYY    2016-10-25  上午10:34:35
 */

public class AboutYueyunActivity extends InitActActivity implements OnClickListener {

	private ActTitleHandler mHandler;
	private TextView phoneTv, netTv, codeNameTv, yueyunInfoTv;

	@Override
	protected void initActTitleView() {
		mHandler = new ActTitleHandler();
		setAbsHandler(mHandler);

	}

	@Override
	protected void initView(Bundle savedInstanceState) {
		setContentView(R.layout.activity_aboutyueyun);
		setTitle("");
		mHandler.replaseLeftLayout(this, true);
		mHandler.getLeftLayout().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();

			}
		});
		((TextView) mHandler.getLeftLayout().findViewById(R.id.left_tv)).setText("关于我们");

		phoneTv = (TextView) findViewById(R.id.yueyun_phone_tv);
		netTv = (TextView) findViewById(R.id.yueyun_net_tv);
		codeNameTv = (TextView) findViewById(R.id.codeName_tv);
		yueyunInfoTv = (TextView) findViewById(R.id.yueyunInfo_tv);

		phoneTv.setOnClickListener(this);
		netTv.setOnClickListener(this);

	}

	@Override
	protected void initData() {
		codeNameTv.setText("软件版本：" + SystemBuilderUtils.getInstance().getAppVersionName(this));

		yueyunInfoTv.setText("		" + getResources().getString(R.string.yueyuninfo));
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.yueyun_phone_tv:
			startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneTv.getText().toString())).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
			break;

		case R.id.yueyun_net_tv:
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(netTv.getText().toString())));
			break;

		default:
			break;
		}

	}

}
