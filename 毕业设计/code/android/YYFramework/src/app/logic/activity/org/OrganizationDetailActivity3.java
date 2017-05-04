package app.logic.activity.org;

import org.ql.activity.customtitle.ActActivity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import app.logic.activity.ActTitleHandler;
import app.logic.pojo.OrganizationInfo;
import app.yy.geju.R;

/*
 * GZYY    2016-12-20  下午3:28:02
 * author: zsz
 */

public class OrganizationDetailActivity3 extends ActActivity {

	public static final String ORG_INFO = "ORG_INFO";

	private ActTitleHandler titleHandler;
	private OrganizationInfo orgInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		titleHandler = new ActTitleHandler();
		setAbsHandler(titleHandler);

		setContentView(R.layout.activity_organization_detail3);

		initActTitle();

	}

	private void initActTitle() {
		orgInfo = (OrganizationInfo) getIntent().getSerializableExtra(ORG_INFO);
		titleHandler.replaseLeftLayout(this, true);
		titleHandler.getLeftLayout().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		((TextView) titleHandler.getLeftLayout().findViewById(R.id.left_tv)).setText(orgInfo.getOrg_name());

	}

}
