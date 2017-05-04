package app.view;

import java.io.File;

import org.ql.activity.customtitle.ActActivity;
import org.ql.utils.QLFileUtil;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import app.logic.activity.ActTitleHandler;
import app.yy.geju.R;

/**
 *
 *@author SiuJiYung  
 * create at 2016-5-16上午11:45:40
 */
public class RichTextViewActivity extends ActActivity {

	public static final String FILE_NAME_OF_ASSETS = "FILE_NAME_OF_ASSETS";
	public static final String TITLE_NAME = "TITLE_NAME";
	public static final String TXT_CONTENT = "TXT_CONTENT";
	
	private WebView webView;
	private WebViewClient webViewClient;
	private String assetFileName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActTitleHandler handler = new ActTitleHandler();
		setAbsHandler(handler);
		setContentView(R.layout.activity_rich_text_view);
		String _title_str = getIntent().getStringExtra(TITLE_NAME);
		if (_title_str == null) {
			_title_str = "查看文档";
		}
		setTitle(_title_str);
		assetFileName = getIntent().getStringExtra(FILE_NAME_OF_ASSETS);
		webViewClient = new WebViewClient();
		webView = (WebView)findViewById(R.id.webview);
		webView.setWebViewClient(webViewClient);
		webView.setWebChromeClient(new WebChromeClient(){
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
			}
		});
		
		String txt = null;
		if (assetFileName != null) {
			txt = QLFileUtil.getFromAssets(this, assetFileName, false);
		}else{
			txt = getIntent().getStringExtra(TXT_CONTENT);
		}
		if (txt == null) {
			txt = "";
		}
		String content_html = QLFileUtil.getFromAssets(this, "textview.htm", false);
		content_html = content_html.replace("_title_", "");
		content_html = content_html.replace("_content_", txt);
		webView.loadData(content_html, "text/html;charset=UTF-8", null);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	
}
