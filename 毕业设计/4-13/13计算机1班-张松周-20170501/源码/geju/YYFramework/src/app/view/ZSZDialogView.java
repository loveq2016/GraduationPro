package app.view;

import java.util.List;

import org.ql.utils.QLDateUtils;
import org.ql.utils.QLToastUtils;

import cn.jpush.a.a.a.d;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import app.logic.activity.org.DPMDetailsActivity;
import app.logic.adapter.YYBaseListAdapter;
import app.logic.controller.OrganizationController;
import app.logic.pojo.DepartmentInfo;
import app.utils.common.Listener;
import app.yy.geju.R;

/*
 * GZYY    2016-8-25  上午10:34:41
 */

public class ZSZDialogView extends Dialog implements OnItemClickListener {

	private Context context;
	private List<DepartmentInfo> listDpm;
	private LayoutInflater inflater;
	private YYListView listView;
	private String org_id;
	private String user_id;
	private SuccessAddMenberToDPMListener successListener;

	public ZSZDialogView(Context context, int themeResId, List<DepartmentInfo> listDpm, String org_id, String user_id) {
		super(context, themeResId);
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.listDpm = listDpm;
		this.org_id = org_id;
		this.user_id = user_id;
	}

	public ZSZDialogView(Context context, int themeResId) {
		super(context, themeResId);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dialog_set_dpm);

		// 解决dialog窗口背景黑色问题
		getWindow().setBackgroundDrawableResource(android.R.color.transparent);

		listView = (YYListView) findViewById(R.id.dpm_listView);

		adapter.setDatas(listDpm);

		if (listDpm.size() > 5) {
			WindowManager manager = ((Activity) context).getWindowManager();
			Display display = manager.getDefaultDisplay();
			WindowManager.LayoutParams params = this.getWindow().getAttributes();
			params.width = (int) ((display.getWidth()) * 0.8);
			params.height = (int) ((display.getHeight()) * 0.45);
			this.getWindow().setAttributes(params);
		}

		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		listView.setPullRefreshEnable(false);
		listView.setPullLoadEnable(false, false);
	}

	private YYBaseListAdapter<DepartmentInfo> adapter = new YYBaseListAdapter<DepartmentInfo>(context) {

		@Override
		public View createView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_dialog_select_dpm, null);
				saveView("dpm_tv", R.id.dpm_tv, convertView);
			}
			DepartmentInfo info = adapter.getItem(position);
			if (info != null) {
				setTextToViewText(info.getDpm_name(), "dpm_tv", convertView);
			}

			return convertView;

		}
	};

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		DepartmentInfo info = adapter.getItem(position - 1);
		if (info != null) {
			// QLToastUtils.showToast(context, info.getDpm_name() +
			// info.getDpm_id() + "组织" + org_id);
			OrganizationController.addMemberToDPM(context, org_id, info.getDpm_id(), user_id, new Listener<Integer, String>() {
				@Override
				public void onCallBack(Integer status, String reply) {
					successListener.onSuccess();
					if (status != 1) {
						String msg = reply == null ? "添加失败" : reply;
						QLToastUtils.showToast(context, msg);
						return;
					}

				}
			});

		}
		this.dismiss();
	}

	public void setSuccessAddMenberToDPMListener(SuccessAddMenberToDPMListener listener) {
		this.successListener = listener;
	}

	// 回调移动成功
	public interface SuccessAddMenberToDPMListener {
		void onSuccess();
	}
}
