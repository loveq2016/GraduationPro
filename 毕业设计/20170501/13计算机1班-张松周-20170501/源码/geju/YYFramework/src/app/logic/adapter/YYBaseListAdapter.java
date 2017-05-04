package app.logic.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.canson.view.swipemenulistview.SmoothMenuAdapter;
import org.ql.utils.image.QLAsyncImage;

import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import android.R.integer;
import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.text.style.ParagraphStyle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import app.config.http.HttpConfig;
import app.logic.pojo.YYChatSessionInfo;
import app.utils.common.FrescoImageShowThumb;
import app.yy.geju.R;

/**
 * 
 * SiuJiYung create at 2016-6-2 下午8:50:00
 * 
 */

public abstract class YYBaseListAdapter<T> extends BaseAdapter implements SmoothMenuAdapter {

	protected Context mContext;
	private ArrayList<T> datas = new ArrayList<T>();
	private ArrayList<Object> fastSortDatas = null;
	private HashMap<String, String> selectMap = new HashMap<String, String>();
	private ArrayList<String> indexList = new ArrayList<String>();
	private boolean showIndex;
	private boolean multableSelectEnable;

	public YYBaseListAdapter(Context context) {
		super();
		mContext = context;
		showIndex = false;
	}

	public void setMultableSelectEnable( boolean enable ) {
		multableSelectEnable = enable;
	}

	public List<String> getIndex() {
		return indexList;
	}

	public void showIndex(boolean show) {
		showIndex = show;
	}

	public boolean isShowIndex() {
		return showIndex;
	}

	public boolean isSelected(T item) {
		String itemName = item.toString();
		return selectMap.containsKey(itemName);
	}

	public void selectItem(T item) {
		if (!multableSelectEnable) {
			selectMap.clear();
		}
		String itemName = item.toString();
		selectMap.put(itemName, "");
	}

	public boolean menuEnable(int position) {
		return true;
	}

	// protected void buildIndex(){
	// if (!showIndex) {
	// return;
	// }
	// sectionMap = new HashMap<String, List<T>>();
	// HashMap<String, String> nameMap = new HashMap<String, String>();
	// for (T t : datas) {//创建目录索引
	// String name = t.toString();
	// String indexName = name.substring(0, 1);
	// nameMap.put(indexName,"");
	// }
	// //分割内容
	// Set<String> nameSet = nameMap.keySet();
	// ArrayList<T> _tmpList = null;
	// fastSortDatas = new ArrayList<Object>();
	// for (String _name : nameSet) {
	// _tmpList = new ArrayList<T>();
	// indexList.add(_name);
	// for (T t : datas) {
	// String name = t.toString();
	// String indexName = name.substring(0, 1);
	// if (_name.equals(indexName)) {
	// _tmpList.add(t);
	// }
	// }
	// sectionMap.put(_name, _tmpList);
	// fastSortDatas.add(_name);
	// fastSortDatas.addAll(_tmpList);
	// }
	// }

	public void setDatas(List<T> sessionInfos) {
		datas.clear();
		if (sessionInfos != null && sessionInfos.size() > 0) {
			datas.addAll(sessionInfos);
		}
		// buildIndex();
		notifyDataSetChanged();
	}

	public void add(T t) {
		datas.add(t);
	}

	public void removeItemAt(int position) {
		if (fastSortDatas != null) {
			if (position < 0 || position > fastSortDatas.size()) {
				return;
			}
			fastSortDatas.remove(position);
			return;
		}
		if (position < 0 || position > datas.size()) {
			return;
		}
		datas.remove(position);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return fastSortDatas != null ? fastSortDatas.size() : datas.size();
	}

	private Object getItemAtPosition(int position) {
		if (fastSortDatas != null) {

			return null;
		}
		if (position > -1 && position < datas.size()) {
			return datas.get(position);
		}
		return null;
	}

	@Override
	public T getItem(int position) {
		Object object = getItemAtPosition(position);
		if (position > -1 && position < getCount()) {
			if (showIndex) {
				return null;
			}
			return datas.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = createView(position, convertView, parent);
		HashMap<String, View> viewHolder = (HashMap<String, View>) view.getTag();
		if (viewHolder == null) {
			viewHolder = new HashMap<String, View>();
			view.setTag(viewHolder);
		}
		return view;
	}

	public void setTextColorForTextView(int color, int viewId, View parentView) {
		TextView tv = (TextView) parentView.findViewById(viewId);
		tv.setTextColor(color);
	}

	public void setValueForTextView(String value, int viewId, View parentView) {
		if (value == null) {
			value = "";
		}
		TextView tv = (TextView) parentView.findViewById(viewId);
		tv.setText(value);
	}

	public void setValueForImageView(String imgPath, int errorResId, int viewId, View parentView) {
		if (errorResId < 0) {
			errorResId = R.drawable.default_user_icon;
		}
		Uri uri = Uri.parse(imgPath);
		ImageView iv = (ImageView) parentView.findViewById(viewId);
		Picasso.with(mContext).load(uri).placeholder(errorResId).into(iv);
	}

	public void setValueForImageViewFit(String imgPath, int errorResId, int viewId, View parentView) {
		if (errorResId < 0) {
			errorResId = R.drawable.default_user_icon;
		}
		Uri uri = Uri.parse(imgPath);
		ImageView iv = (ImageView) parentView.findViewById(viewId);
		Picasso.with(mContext).load(uri).placeholder(errorResId).fit().centerCrop().into(iv);
	}

	public void saveView(String name, int viewId, View parentView) {
		HashMap<String, View> viewHolder = (HashMap<String, View>) parentView.getTag();
		if (viewHolder != null && viewHolder.containsKey(name)) {
			return;
		}
		if (viewHolder == null) {
			viewHolder = new HashMap<String, View>();
			parentView.setTag(viewHolder);
		}
		View view = parentView.findViewById(viewId);
		viewHolder.put(name, view);
	}

	public <V> V getViewForName(String name, View parentView) {
		HashMap<String, View> viewHolder = (HashMap<String, View>) parentView.getTag();
		if (viewHolder != null && viewHolder.containsKey(name)) {
			return (V) viewHolder.get(name);
		}
		return null;
	}

	public void setTextToViewText(String txt, String viewName, View parentView) {
		TextView tv = getViewForName(viewName, parentView);
		if (tv == null) {
			return;
		}
		tv.setText(txt);
	}

	public void setImageToImageView(String pathOrUrl, String viewName, int holderImageId, View parentView) {
		SimpleDraweeView imageView = getViewForName(viewName, parentView);
		if ( imageView == null ) {
			return;
		}
		holderImageId = holderImageId < 0 ? R.drawable.default_user_icon : holderImageId;
//		imageView.setImageURI(pathOrUrl);
		FrescoImageShowThumb.showThrumb(Uri.parse(pathOrUrl),imageView);
		// Picasso.with(mContext).load(Uri.parse(pathOrUrl)).fit().centerInside().placeholder(holderImageId).into(imageView);
//		Picasso.with(mContext).load(Uri.parse(pathOrUrl)).fit().centerInside().into(imageView);
		// Picasso.Builder picBuilder = new Picasso.Builder(mContext);
		// Picasso picasso = picBuilder.build();
		// ImageView imageView = getViewForName(viewName,parentView);
		// if (imageView == null) {
		// return;
		// }
		// RequestCreator requestCreator = picasso.load(Uri.parse(pathOrUrl));
		// requestCreator.centerCrop();
		// requestCreator.into(imageView);
		// if (holderImageId > 0) {
		// requestCreator.placeholder(holderImageId);
		// }
	}

	public void setImageToImageViewCenterCrop(String pathOrUrl, String viewName, int holderImageId, View parentView) {
		SimpleDraweeView imageView = getViewForName(viewName, parentView);
		if (imageView == null) {
			return;
		}
//		holderImageId = holderImageId < 0 ? R.drawable.default_user_icon : holderImageId;
//		Picasso.with(mContext).load(Uri.parse(pathOrUrl)).placeholder(R.drawable.default_user_icon).error(R.drawable.default_user_icon).fit().centerCrop().into(imageView);
		FrescoImageShowThumb.showThrumb(Uri.parse(pathOrUrl),imageView);
//		imageView.setImageURI(pathOrUrl);
	}

	public abstract View createView(int position, View convertView, ViewGroup parent);

}
