package app.logic.view.web;

import org.ql.activity.customtitle.ActActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import app.logic.activity.ActTitleHandler;
import app.logic.activity.TYBaseActivity;
import app.logic.activity.main.HomeActivity;
import app.yy.geju.R;

/**
*
* SiuJiYung create at 2016年7月6日 上午9:27:19
*
*/

public class WebBrowserActivity extends ActActivity implements View.OnClickListener{
	
	public static final String kBROWSER_TITLE = "kBROWSER_TITLE";
	public static final String KBROWSER_HOME_URL = "KBROWSER_HOME_URL";

	ActTitleHandler handler = new ActTitleHandler();
	private String homePage;
	private View rightView;
	private TextView mTitle;
	private WebViewSkip webviewSkip;
	private WebView webView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbsHandler(handler);
		setContentView(R.layout.activity_web_browser);

		initView();
		homePage = getIntent().getStringExtra(KBROWSER_HOME_URL);
		String _title_str = getIntent().getStringExtra(kBROWSER_TITLE);
		if (_title_str != null) {
			setTitle(_title_str);
		}
		webView = (WebView)findViewById(R.id.web_browser_view);
		webviewSkip = new WebViewSkip();
		webviewSkip.initClient(webView);

		webView.setWebViewClient(webViewClient);
		webView.setWebChromeClient(webChromeClient);

		if (homePage != null) {
			webView.loadUrl(homePage);
		}
	}

	private void initView(){
		rightView = LayoutInflater.from(this).inflate(R.layout.activity_webview_rightlayout, null);
		handler.addRightView(rightView, true);

		ImageButton refresh_Ibt = (ImageButton) rightView.findViewById(R.id.webview_refresh);
		refresh_Ibt.setOnClickListener(this);

		ImageButton close_Ibt = (ImageButton) rightView.findViewById(R.id.webview_close);

		refresh_Ibt.setOnClickListener(this);
		close_Ibt.setOnClickListener(this);

		handler.replaseLeftLayout(this, true);
		((TextView)handler.getCenterLayout().findViewById(android.R.id.title)).setText("");
		handler.getLeftLayout().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (webView !=null && webView.canGoBack()){
					webView.goBack();
				}else
					finish();
			}
		});

		mTitle = (TextView) handler.getLeftLayout().findViewById(R.id.left_tv);
		mTitle.setText("加载中...");
	}

	private WebChromeClient webChromeClient = new WebChromeClient(){
		@Override
		public void onReceivedTitle(WebView view, String title) {
			super.onReceivedTitle(view, title);
			if (mTitle !=null)
				mTitle.setText(title);
		}

		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			super.onProgressChanged(view, newProgress);
		}
	};

	private WebViewClient webViewClient = new WebViewClient(){
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
			return super.shouldOverrideUrlLoading(view, request);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
//			showWaitDialog();
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
//			dismissWaitDialog();
			super.onPageFinished(view, url);
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.webview_refresh:
				if (webView !=null)
					webView.reload();
				break;
			case R.id.webview_close:
				finish();
				break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		dismissWaitDialog();
		if (keyCode == KeyEvent.KEYCODE_BACK){
			if (webView !=null && webView.canGoBack()){
				webView.goBack();
				return true;
			}

		}
		return super.onKeyDown(keyCode, event);
	}
}
