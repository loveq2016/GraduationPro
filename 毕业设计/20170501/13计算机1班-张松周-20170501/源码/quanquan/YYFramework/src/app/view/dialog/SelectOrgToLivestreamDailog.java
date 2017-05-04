package app.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

//import com.easemob.livestream.activity.StartLiveActivity;

import org.ql.utils.QLToastUtils;
import org.ql.utils.network.QLHttpUtil;

import java.util.List;

import app.logic.adapter.YYBaseListAdapter;
import app.logic.pojo.OrgListByBuilderInfo;
import app.view.YYListView;
import app.yy.geju.R;

/**
 * Created by GZYY on 17/2/15.
 */

public class SelectOrgToLivestreamDailog extends Dialog implements AdapterView.OnItemClickListener {

    private Context mContent;
    private List<OrgListByBuilderInfo> mData;


    public SelectOrgToLivestreamDailog(Context context) {
        super(context, R.style.sex_dialog);
        init(context, null);
    }

    public SelectOrgToLivestreamDailog(Context context, List<OrgListByBuilderInfo> data) {
        super(context, R.style.sex_dialog);
        init(context, data);
    }


    private void init(Context context, List<OrgListByBuilderInfo> data) {
        this.mContent = context;
        this.mData = data;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_select_livestream);

        initView();

        updataUI();

        setDialogSize();

    }

    private YYListView mListView;
    private LinearLayout createOrgView;
    private LinearLayout emptyView;
    private YYBaseListAdapter<OrgListByBuilderInfo> mAdapter = new YYBaseListAdapter<OrgListByBuilderInfo>(mContent) {
        @Override
        public View createView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContent).inflate(R.layout.item_dialog_select_org, null);
                saveView("item_content_tv", R.id.item_content_tv, convertView);
                saveView("item_line_v", R.id.item_line_v, convertView);
            }
            OrgListByBuilderInfo info = getItem(position);
            if (info != null) {
                TextView contentTv = getViewForName("item_content_tv", convertView);
                View lineView = getViewForName("item_line_v", convertView);
                contentTv.setText(info.getOrg_name());

            }
            return convertView;
        }
    };


    /**
     * init contentView
     */
    private void initView() {
        mListView = (YYListView) findViewById(R.id.dialog_org_list_lv);
        createOrgView = (LinearLayout) findViewById(R.id.dialog_createOrg_ll);
        emptyView = (LinearLayout) findViewById(R.id.dialog_empty_data_lv);

        mListView.setPullLoadEnable(false, true);
        mListView.setPullRefreshEnable(false);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
    }

    /**
     * updata UI
     */
    private void updataUI() {
        mAdapter.setDatas(mData);
        if (mData == null || mData.size() < 1) {
            emptyView.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * set dialog size
     */
    private void setDialogSize() {
        WindowManager manager = ((Activity) mContent).getWindowManager();
        Display display = manager.getDefaultDisplay();
        WindowManager.LayoutParams params = this.getWindow().getAttributes();
        if (mData == null || mData.size() < 1) {
            params.width = (int) ((display.getWidth()) * 0.8);
            params.height = (int) ((display.getHeight()) * 0.35);
        } else {
            params.width = (int) ((display.getWidth()) * 0.8);
            params.height = (int) ((display.getHeight()) * 0.45);
        }
        this.getWindow().setAttributes(params);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        OrgListByBuilderInfo info = mAdapter.getItem(position - 1);
        if (info != null) {
//            ((Activity) mContent).startActivity(new Intent(mContent, StartLiveActivity.class).putExtra(StartLiveActivity.ORG_ID, info.getOrg_id()));
        }
    }

}
