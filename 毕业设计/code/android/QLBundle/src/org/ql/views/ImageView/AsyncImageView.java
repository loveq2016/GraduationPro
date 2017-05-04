package org.ql.views.ImageView;

import org.ql.utils.image.QLAsyncImage;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class AsyncImageView extends ImageView {

	private QLAsyncImage imageLoader;
	
	public AsyncImageView(Context context) {
		super(context);
	}

	public AsyncImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AsyncImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	
	public void loadImageWithUrl(Activity activity,String url){
		if (imageLoader == null) {
			imageLoader = new QLAsyncImage(activity);
		}
		imageLoader.loadImage(url, this);
	}

}
