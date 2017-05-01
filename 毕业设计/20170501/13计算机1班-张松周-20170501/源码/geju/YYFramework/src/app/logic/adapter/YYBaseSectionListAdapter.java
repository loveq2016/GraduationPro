package app.logic.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.squareup.picasso.Picasso;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import app.yy.geju.R;

/**
 * 
 * SiuJiYung create at 2016年8月10日 下午1:59:00
 * 
 */

public abstract class YYBaseSectionListAdapter<T> extends BaseAdapter {

	private final Map<String, Adapter> sections = new LinkedHashMap<String, Adapter>();
	private YYBaseListAdapter<String> headers;
	protected Context mContext;
	private ArrayList<String> indexList = new ArrayList<String>();
	private Comparator<String> sortComparator;

	public final static int TYPE_SECTION_HEADER = 0;
	public final static int TYPE_SECTION_CONTENT = 1;

	public YYBaseSectionListAdapter(Context context, int headerLayoutResId) {
		mContext = context;
		headers = new YYBaseListAdapter<String>(context) {
			@Override
			public View createView(int position, View convertView, ViewGroup parent) {
				String title = indexList.get(position);
				return createTitleView(title, convertView, parent);
			}
		};
	}

	public YYBaseSectionListAdapter(Context context) {
		mContext = context;
		headers = new YYBaseListAdapter<String>(context) {
			@Override
			public View createView(int position, View convertView, ViewGroup parent) {
				String title = null;
				if (position > -1 && position < indexList.size()) {
					title = indexList.get(position);
				} else {
					title = "";
				}
				return createTitleView(title, convertView, parent);
			}
		};
	}

	/**
	 * 创建section title view,如需自定义，请重写该方法
	 * 
	 * @param title
	 * @param convertView
	 * @param parent
	 * @return
	 */
	public View createTitleView(String title, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.section_list_view_title, null);
		}
		TextView tv = (TextView) convertView.findViewById(R.id.list_header_title);
		tv.setText(title);
		return convertView;
	}

	public abstract View createView(T info, int position, View convertView, ViewGroup parent);

	/**
	 * 创建索引列表
	 * 
	 * @param datas
	 * @return
	 */
	public Map<String, String> createNameMap(List<T> datas) {
		HashMap<String, String> nameMap = new HashMap<String, String>();
		for (T t : datas) {
			// 创建目录索引
			String name = getSortName(t);
			nameMap.put(name, "");
		}
		return nameMap;
	}

	/**
	 * 默认为获取对象的首字符；如果需要更改索引规则，请重写该方法
	 * 
	 * @param t
	 * @return
	 */
	public String getSortName(T t) {
		String name = t.toString();
		return name;
	}

	/**
	 * 默认获取第一个字符进行排序，需要更改算法请重写该方法
	 */
	public List<String> sortNames(Set<String> names) {
		if (sortComparator == null) {
			sortComparator = new Comparator<String>() {
				@Override
				public int compare(String lhs, String rhs) {
					String lhs_first = lhs.substring(0, 1);
					String rhs_first = rhs.substring(0, 1);
					char lhs_char = lhs_first.charAt(0);
					char rhs_char = rhs_first.charAt(0);
					return lhs_char - rhs_char;
				}
			};
		}

		ArrayList<String> _tmpList = new ArrayList<String>(names);
		Collections.sort(_tmpList, sortComparator);
		return _tmpList;
	}

	public void clean() {

	}

	public void setDatas(List<T> datas) {
		indexList.clear();
		headers.setDatas(null);
		sections.clear();

		Map<String, String> nameMap = createNameMap(datas);
		List<String> index = sortNames(nameMap.keySet());
		if (index != null) {
			indexList.addAll(index);
		}
		// 分割内容
		ArrayList<T> _tmpList = null;
		for (String _name : indexList) {
			_tmpList = new ArrayList<T>();
			for (T t : datas) {
				String name = getSortName(t);
				if (_name.equals(name)) {
					_tmpList.add(t);
				}
			}
			YYBaseListAdapter<T> adapter = new YYBaseListAdapter<T>(mContext) {
				@Override
				public View createView(int position, View convertView, ViewGroup parent) {
					T t = getItem(position);
					return YYBaseSectionListAdapter.this.createView(t, position, convertView, parent);
				}
			};
			adapter.setDatas(_tmpList);
			addSection(_name, adapter);
		}
		notifyDataSetChanged();
	}

	private void addSection(String section, Adapter adapter) {
		this.headers.add(section);
		this.sections.put(section, adapter);
	}

	public Object getItem(int position) {
		for (Object section : this.sections.keySet()) {
			Adapter adapter = sections.get(section);
			int size = adapter.getCount() + 1;
			// check if position inside this section
			if (position == 0)
				return section;
			if (position < size)
				return adapter.getItem(position - 1);
			// otherwise jump into next section
			position -= size;
		}
		return null;
	}

	public int getCount() {
		// total together all sections, plus one for each section header
		int total = 0;
		for (Adapter adapter : this.sections.values())
			total += adapter.getCount() + 1;
		return total;
	}

	public int getViewTypeCount() {
		// assume that headers count as one, then total all sections
		int total = 1;
		for (Adapter adapter : this.sections.values())
			total += adapter.getViewTypeCount();
		return total;
	}

	public int getItemViewType(int position) {
		int type = 1;
		for (Object section : this.sections.keySet()) {
			Adapter adapter = sections.get(section);
			int size = adapter.getCount() + 1;

			// check if position inside this section
			if (position == 0)
				return TYPE_SECTION_HEADER;
			if (position < size)
				return type + adapter.getItemViewType(position - 1);

			// otherwise jump into next section
			position -= size;
			type += adapter.getViewTypeCount();
		}
		return -1;
	}

	public boolean areAllItemsSelectable() {
		return false;
	}

	public boolean isEnabled(int position) {
		return (getItemViewType(position) != TYPE_SECTION_HEADER);
	}

	public void removeItemAtPosition(int position) {
		// Object getItem(position);
		getItemViewType(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh = null;
		if (convertView == null) {
			convertView = new LinearLayout(mContext);
			convertView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

			vh = new ViewHolder();
			convertView.setTag(vh);
		}
		vh = (ViewHolder) convertView.getTag();
		((LinearLayout) convertView).removeAllViews();
		int sectionnum = 0;
		for (Object section : this.sections.keySet()) {
			Adapter adapter = sections.get(section);
			int size = adapter.getCount() + 1;

			// check if position inside this section
			if (position == 0) {
				vh.titleConvertView = headers.getView(sectionnum, convertView, parent);
				((LinearLayout) convertView).addView(vh.titleConvertView);
				return convertView;
			} else if (position < size) {
				vh.contentConvertView = adapter.getView(position - 1, convertView, parent);
				((LinearLayout) convertView).addView(vh.contentConvertView);
				return convertView;
			}
			// otherwise jump into next section
			position -= size;
			sectionnum++;
		}
		return null;
	}

	protected View getConvertView(View convertView, boolean sectionTitle) {
		ViewHolder vHolder = (ViewHolder) convertView.getTag();
		if (sectionTitle) {
			return vHolder.titleConvertView;
		}
		return vHolder.contentConvertView;
	}

	class ViewHolder {
		View titleConvertView;
		View contentConvertView;
	}

	@Override
	public long getItemId(int position) {
		return position;
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
		ImageView imageView = getViewForName(viewName, parentView);
		if (imageView == null) {
			return;
		}
		holderImageId = holderImageId < 0 ? R.drawable.default_user_icon : holderImageId;
		Picasso.with(mContext).load(Uri.parse(pathOrUrl)).fit().centerCrop().placeholder(holderImageId).into(imageView);
	}

	public Comparator<String> getSortComparator() {
		return sortComparator;
	}

	public void setSortComparator(Comparator<String> sortComparator) {
		this.sortComparator = sortComparator;
	}

}
