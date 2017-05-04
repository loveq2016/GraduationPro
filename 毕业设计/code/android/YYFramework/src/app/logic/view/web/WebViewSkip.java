package app.logic.view.web;

import org.ql.views.web.QLWebViewClient;
import org.ql.views.web.QLWebViewClient.onHandleCustomSchemeListener;

import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class WebViewSkip {
	QLWebViewClient webClient;
	
	public void initClient(WebView webView){
		webClient=new QLWebViewClient();
		webClient.setHandleSchemeHead("tyserver");
		onHandleCustomSchemeListener listener=new onHandleCustomSchemeListener() {
			@Override
			public boolean onHandleCustomUrl(WebView webView, String url) {
				// 解析自定义url
				//tyserver://?class=类名&module=类方法&callback=回调javascript函数名

				return false;
			}
		};
		webClient.setOnHandleCustomSchemeListener(listener);
		webView.setWebViewClient(webClient);
		webView.getSettings().setAllowFileAccess(true);
		webView.getSettings().setAllowContentAccess(true);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		webView.getSettings().setAppCacheEnabled(true);
		webView.getSettings().setDatabaseEnabled(true);
	}

}
