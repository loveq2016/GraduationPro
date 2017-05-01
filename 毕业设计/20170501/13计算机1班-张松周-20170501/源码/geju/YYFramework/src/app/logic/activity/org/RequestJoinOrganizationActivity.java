package app.logic.activity.org;

import org.ql.activity.customtitle.ActActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import app.logic.activity.ActTitleHandler;
import app.logic.activity.main.HomeActivity;
import app.yy.geju.R;

/**
*
* SiuJiYung create at 2016年8月12日 上午9:28:10
*
*/

public class RequestJoinOrganizationActivity extends ActActivity implements OnClickListener {

	private ActTitleHandler titleHandler = new ActTitleHandler();
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbsHandler(titleHandler);
		setContentView(R.layout.activity_request_join_org_success);
		setTitle("加入组织");
		TextView tView = (TextView)findViewById(R.id.request_txt_tv);
		Button  btn = (Button)findViewById(R.id.request_btn);
		btn.setOnClickListener(this);
		tView.setText(Html.fromHtml("申请成功</br>等待组织审核"));
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		intent.setClass(this, HomeActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
}
