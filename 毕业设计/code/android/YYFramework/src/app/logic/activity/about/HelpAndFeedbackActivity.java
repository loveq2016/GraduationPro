package app.logic.activity.about;

import org.ql.utils.QLToastUtils;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import app.logic.activity.ActTitleHandler;
import app.logic.activity.InitActActivity;
import app.logic.activity.main.UserCenterFragment;
import app.logic.controller.UserManagerController;
import app.utils.common.Listener;
import app.yy.geju.R;

/*
 * GZYY    2016-10-25  上午10:03:58
 */

public class HelpAndFeedbackActivity extends InitActActivity implements OnClickListener {

	private ActTitleHandler mHandler;
	private Button submit_btn;
	private EditText content_et;

	@Override
	protected void initActTitleView() {
		mHandler = new ActTitleHandler();
		setAbsHandler(mHandler);
	}

	@Override
	protected void initView(Bundle savedInstanceState) {
		setContentView(R.layout.activity_helpandfeedback);
		setTitle("");
		mHandler.replaseLeftLayout(this, true);
		mHandler.getLeftLayout().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		((TextView) mHandler.getLeftLayout().findViewById(R.id.left_tv)).setText("反馈");

		submit_btn = (Button) findViewById(R.id.submit_btn);
		content_et = (EditText) findViewById(R.id.help_content_et);
		content_et.setOnClickListener(this);
		submit_btn.setOnClickListener(this);

	}

	@Override
	protected void initData() {

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.submit_btn:
			postContent();
			break;

		default:
			break;
		}

	}

	// 提交问题
	private void postContent() {
		String contentString = content_et.getText().toString();
		if (contentString == null || TextUtils.isEmpty(contentString)) {
			QLToastUtils.showToast(this, "内容不能为空");
			return;
		}

		UserManagerController.postHelpAndFeedback(this, contentString, new Listener<Integer, String>() {

			@Override
			public void onCallBack(Integer status, String reply) {
				if (status == 1) {
					QLToastUtils.showToast(HelpAndFeedbackActivity.this, "提交成功");
					finish();
					return;
				}
				QLToastUtils.showToast(HelpAndFeedbackActivity.this, "提交失败");
			}
		});

	}

}
