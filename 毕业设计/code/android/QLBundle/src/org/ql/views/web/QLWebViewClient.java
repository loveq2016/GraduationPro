package org.ql.views.web;

import android.webkit.WebView;
import android.webkit.WebViewClient;

public class QLWebViewClient extends WebViewClient {

	public interface onHandleCustomSchemeListener{
		public boolean onHandleCustomUrl(WebView webView,String url);
	}
	
	private String handleCustomScheme = null;
	private onHandleCustomSchemeListener handleCustomSchemeListener = null;
	
	public void setHandleSchemeHead(String scheme){
		handleCustomScheme = scheme;
	}
	
	public void setOnHandleCustomSchemeListener(onHandleCustomSchemeListener l){
		handleCustomSchemeListener = l;
	}
	
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		if (handleCustomScheme != null && url.startsWith(handleCustomScheme)) {
			if (handleCustomSchemeListener != null) {
				return handleCustomSchemeListener.onHandleCustomUrl(view,url);
			}
		}
		view.loadUrl(url);
		return true;
	}
}
