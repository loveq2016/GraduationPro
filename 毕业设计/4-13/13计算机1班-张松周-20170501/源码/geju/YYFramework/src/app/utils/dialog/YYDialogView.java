package app.utils.dialog;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
/*
 * GZYY    2016-11-1  下午6:25:54
 */
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import app.logic.adapter.YYBaseListAdapter;
import app.logic.pojo.DepartmentInfo;
import app.logic.pojo.OrganizationInfo;
import app.view.YYListView;
import app.yy.geju.R;

public abstract class YYDialogView<T> extends Dialog implements OnItemClickListener {

	private Context context;
	private List<T> datas;
	private YYBaseListAdapter<T> mAdapter;

	private YYListView mListView;

	public YYDialogView(Context context, int themeResId, List<T> datas) {
		super(context, themeResId);
		this.context = context;
		this.datas = datas;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dialog_set_dpm);
		// 解决dialog窗口背景黑色问题
		getWindow().setBackgroundDrawableResource(android.R.color.transparent);

		mListView = (YYListView) findViewById(R.id.dpm_listView);

		mAdapter = new YYBaseListAdapter<T>(context) {

			@Override
			public View createView(int position, View convertView, ViewGroup parent) {
				if (convertView == null) {
					convertView = LayoutInflater.from(context).inflate(R.layout.item_dialog_select_dpm, null);
					saveView("dpm_tv", R.id.dpm_tv, convertView);
				}
				T info = mAdapter.getItem(position);
				if (info != null) {
					if (info instanceof DepartmentInfo) {
						setTextToViewText(((DepartmentInfo) info).getDpm_name(), "dpm_tv", convertView);
					} else if (info instanceof OrganizationInfo) {
						setTextToViewText(((OrganizationInfo) info).getOrg_name(), "dpm_tv", convertView);
					}
				}
				return convertView;

			}
		};
		mAdapter.setDatas(datas);

		if (datas.size() > 5) {
			WindowManager manager = ((Activity) context).getWindowManager();
			Display display = manager.getDefaultDisplay();
			WindowManager.LayoutParams params = this.getWindow().getAttributes();
			params.width = (int) ((display.getWidth()) * 0.8);
			params.height = (int) ((display.getHeight()) * 0.45);
			this.getWindow().setAttributes(params);
		}

		mListView.setAdapter(mAdapter);
		mListView.setPullRefreshEnable(false);
		mListView.setPullLoadEnable(false, false);
		mListView.setOnItemClickListener(this);

		// this.show();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		OnCreateDialogItemClickListener(parent, view, position, id);
		this.dismiss();
	}

	public abstract void OnCreateDialogItemClickListener(AdapterView<?> parent, View view, int position, long id);
}
