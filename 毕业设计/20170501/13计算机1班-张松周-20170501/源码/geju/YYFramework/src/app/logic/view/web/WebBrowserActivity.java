package app.logic.view.web;

import org.ql.activity.customtitle.ActActivity;

import android.os.Bundle;
import android.webkit.WebView;
import app.logic.activity.ActTitleHandler;
import app.yy.geju.R;

/**
*
* SiuJiYung create at 2016年7月6日 上午9:27:19
*
*/

public class WebBrowserActivity extends ActActivity {
	
	public static final String kBROWSER_TITLE = "kBROWSER_TITLE";
	public static final String KBROWSER_HOME_URL = "KBROWSER_HOME_URL";
	
	private String homePage;
	private WebViewSkip webviewSkip;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActTitleHandler handler = new ActTitleHandler();
		setAbsHandler(handler);
		setContentView(R.layout.activity_web_browser);
		homePage = getIntent().getStringExtra(KBROWSER_HOME_URL);
		String _title_str = getIntent().getStringExtra(kBROWSER_TITLE);
		if (_title_str != null) {
			setTitle(_title_str);
		}
		WebView webView = (WebView)findViewById(R.id.web_browser_view);
		webviewSkip = new WebViewSkip();
		webviewSkip.initClient(webView);
		if (homePage != null) {
			webView.loadUrl(homePage);
		}
	}
	
}
