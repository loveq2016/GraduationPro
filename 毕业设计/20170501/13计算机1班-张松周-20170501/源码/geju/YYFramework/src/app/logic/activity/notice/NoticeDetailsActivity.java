package app.logic.activity.notice;

import java.util.List;

import org.ql.activity.customtitle.ActActivity;
/*
 * GZYY    2016-8-10  上午11:30:19
 */

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import app.logic.activity.ActTitleHandler;
import app.logic.activity.InitActActivity;
import app.logic.controller.AnnounceController;
import app.logic.pojo.NoticeDetailInfo;
import app.logic.pojo.NoticeInfo;
import app.utils.common.Listener;
import app.yy.geju.R;

public class NoticeDetailsActivity extends InitActActivity {
	private ActTitleHandler titleHandler;
	private TextView title_tv, name_tv, time_tv, context_tv;
	private ImageView head_iv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	@Override
	protected void initActTitleView() {
		titleHandler = new ActTitleHandler();
		setAbsHandler(titleHandler);
	}

	@Override
	protected void initView(Bundle savedInstanceState) {
		setContentView(R.layout.activity_noticedetail);
		title_tv = (TextView) findViewById(R.id.detail_title);
		name_tv = (TextView) findViewById(R.id.detail_name);
		time_tv = (TextView) findViewById(R.id.detail_time);
		context_tv = (TextView) findViewById(R.id.detail_context);
//		head_iv = (ImageView)findViewById(R.id.deta);

		setTitle("公告");
		titleHandler.addLeftView(LayoutInflater.from(this).inflate(R.layout.title_leftview_layout, null), true);
		titleHandler.getLeftLayout().findViewById(R.id.left_iv).setVisibility(View.VISIBLE);
		titleHandler.getLeftLayout().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	@Override
	protected void initData() {

		String msg_id = getIntent().getStringExtra(FragmentUnread.CLICK_ITEMINFO);

		getItemInfo(msg_id);
	}

	private void getItemInfo(String msg_id) {
		AnnounceController.getAnnounceDetail(this, msg_id, new Listener<Void, List<NoticeInfo>>() {

			@Override
			public void onCallBack(Void status, List<NoticeInfo> reply) {
				if (reply == null || reply.size() < 1) {
					return;
				}
				NoticeInfo info = reply.get(0);

				title_tv.setText(info.getMsg_title());
				name_tv.setText(info.getMsg_creator_id());
				time_tv.setText(info.getMsg_create_time());
				context_tv.setText(info.getMsg_content());
			}
		});

	}
}
