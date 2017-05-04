package app.utils.common;

import android.view.View;
/**
 * 
 * @author SuiJiYung
 *</br>
 *加载view 回调 listener
 */
public abstract class LoadingMaskListener {
	
	public LoadingMaskListener(){};
	
	public abstract void onLoading(View loadingView);
	public abstract void onLoadingFinish(View loadingView);
}
