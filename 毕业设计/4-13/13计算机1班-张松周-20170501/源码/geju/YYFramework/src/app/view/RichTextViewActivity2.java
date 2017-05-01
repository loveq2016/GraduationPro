package app.view;

import java.util.List;

import org.ql.utils.QLFileUtil;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import app.logic.activity.ActTitleHandler;
import app.logic.activity.InitActActivity;
import app.logic.controller.AnnounceController;
import app.logic.pojo.NoticeInfo;
import app.utils.common.Listener;
import app.yy.geju.R;

/*
 * GZYY    2016-8-15  上午10:54:12
 */

public class RichTextViewActivity2 extends InitActActivity {

	public static final String CLICKINFO_ID = "CLICKINFO";

	private ActTitleHandler titleHandler;

	private NoticeInfo info;

	private String info_title, info_name, info_time, info_content;

	private WebView webView;
	private WebViewClient webViewClient;
	private String content_html;

	@Override
	protected void initActTitleView() {
		titleHandler = new ActTitleHandler();
		setAbsHandler(titleHandler);

	}

	@Override
	protected void initView(Bundle savedInstanceState) {
		setContentView(R.layout.activity_rich_text_view);
		setTitle("公告");

		replaseLeftTitltLayout();

		webViewClient = new WebViewClient();

		webView = (WebView) findViewById(R.id.webview);
		webView.setWebViewClient(webViewClient);
		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
			}
		});

	}

	@Override
	protected void initData() {
		String msg_id = getIntent().getStringExtra(CLICKINFO_ID);
		if (!msg_id.equals("")) {
			getItemInfo(msg_id);
		}
		// Toast.makeText(this, info_name, Toast.LENGTH_SHORT).show();
		content_html = QLFileUtil.getFromAssets(this, "article_tmp.htm", false);

	}

	// 获取网络信息
	private void getItemInfo(String msg_id) {
		AnnounceController.getAnnounceDetail(this, msg_id, new Listener<Void, List<NoticeInfo>>() {

			@Override
			public void onCallBack(Void status, List<NoticeInfo> reply) {
				if (reply == null || reply.size() < 1) {
					Toast.makeText(RichTextViewActivity2.this, "没有可读数据", Toast.LENGTH_SHORT).show();
					return;
				}

				info = reply.get(0);

				content_html = content_html.replace("_title_", info.getMsg_title());
				content_html = content_html.replace("_createTime_", info.getMsg_create_time());
				content_html = content_html.replace("_from_", info.getMsg_creator());
				content_html = content_html.replace("_content_", info.getMsg_content());

				webView.loadData(content_html, "text/html;charset=UTF-8", null);

			}
		});
	}

	private void replaseLeftTitltLayout() {
		titleHandler.addLeftView(LayoutInflater.from(this).inflate(R.layout.title_leftview_layout, null), true);
		titleHandler.getLeftLayout().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

}
