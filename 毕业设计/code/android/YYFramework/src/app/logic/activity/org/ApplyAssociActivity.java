package app.logic.activity.org;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.picasso.Picasso;

import org.QLConstant;
import org.ql.activity.customtitle.ActActivity;
import org.ql.views.listview.QLXListView;

import java.util.ArrayList;
import java.util.List;

import app.config.http.HttpConfig;
import app.logic.activity.ActTitleHandler;
import app.logic.adapter.YYBaseListAdapter;
import app.logic.controller.OrganizationController;
import app.logic.pojo.OrganizationInfo;
import app.utils.common.FrescoImageShowThumb;
import app.utils.common.Listener;
import app.yy.geju.R;

/**
 * 申请组织Activity YSF
 * Created by Administrator on 2017/1/14 0014.
 */

public class ApplyAssociActivity extends ActActivity implements AdapterView.OnItemClickListener, QLXListView.IXListViewListener, TextView.OnEditorActionListener {

    private ActTitleHandler titleHandler = new ActTitleHandler();
    private QLXListView mListView;
    private EditText searchEditText;
    private LinearLayout search ,air ;
    private List<OrganizationInfo> datas = new ArrayList<>();
    private boolean isSelectState = false ;

    private YYBaseListAdapter<OrganizationInfo> mAdapter = new YYBaseListAdapter<OrganizationInfo>(this) {
        @Override
        public View createView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_row_orginfo3, null);
                saveView("item_head_iv", R.id.item_head_iv, convertView);
                saveView("item_name_tv", R.id.item_name_tv, convertView);
                saveView("item_status_tv", R.id.item_status_tv, convertView);
            }
            OrganizationInfo info = (OrganizationInfo) getItem(position);
            if (info != null) {
                String url = HttpConfig.getUrl(info.getOrg_logo_url());
                SimpleDraweeView headIV = getViewForName("item_head_iv", convertView);
                FrescoImageShowThumb.showThrumb(Uri.parse(url),headIV);
//                headIV.setImageURI(url);
//                Picasso.with(ApplyAssociActivity.this).load(url).placeholder(R.drawable.default_user_icon).error(R.drawable.default_user_icon).fit().centerCrop().into(headIV);
                setTextToViewText(info.getOrg_name(), "item_name_tv", convertView);
                TextView statusTv = getViewForName("item_status_tv", convertView);
                if (info.getApply_status().equals("1")) {
                    statusTv.setText("审核中");
                    statusTv.setTextColor(getResources().getColor(R.color.new_textView_color));
                    statusTv.setBackgroundDrawable(null);
                } else if (info.getApply_status().equals("2") || info.getMember_id().equals(QLConstant.client_id)) {
                    statusTv.setText("已加入");
                    statusTv.setTextColor(getResources().getColor(R.color.new_textView_color));
                    statusTv.setBackgroundDrawable(null);
                } else if (info.getApply_status().equals("3")) {
                    statusTv.setText("已被拒绝");
                    statusTv.setTextColor(getResources().getColor(R.color.new_textView_color));
                    statusTv.setBackgroundDrawable(null);
                } else {
                    statusTv.setText("申请");
                    statusTv.setTextColor(getResources().getColor(R.color.white));
                    statusTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_join_org_btn_bg));
                }
                statusTv.setVisibility(View.GONE);
            }
            return convertView;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbsHandler(titleHandler);
        setContentView(R.layout.activity_join_org);
        titleHandler.replaseLeftLayout(this, true);
        titleHandler.getLeftLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView) titleHandler.getLeftLayout().findViewById(R.id.left_tv)).setText("加入组织");
        setTitle("");
        searchEditText = (EditText) findViewById(R.id.search_edt);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String txt = s.toString();
                if(TextUtils.isEmpty(txt)){
                    isSelectState = false ;
                }else {
                    isSelectState = true ;
                }
                startSearchOrg(txt);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        air = (LinearLayout) findViewById( R.id.empty_view);
        search = (LinearLayout) findViewById(R.id.search_bg);
        search.setBackgroundColor(getResources().getColor(R.color.white));
        air.setVisibility( View.GONE);
        findViewById(R.id.search_edt_bg).setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_search_edt_bg));
        mListView = (QLXListView) findViewById(R.id.friends_list_view);
        mListView.setPullLoadEnable(false, true);
        mListView.setPullRefreshEnable(true);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setXListViewListener(this);

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        startSearchOrg(searchEditText.getText().toString());
    }

    /**
     * 搜索協會
     *
     * @param keywork
     */
    private void startSearchOrg(String keywork) {

        if (TextUtils.isEmpty(keywork)) {
            keywork = "";
            ((TextView)findViewById( R.id.empty_tv01)).setText("没有可加入的组织");
        }else{
            ((TextView)findViewById( R.id.empty_tv01)).setText("没有搜索到对应的组织");
        }
        OrganizationController.searchOrganizations(this, keywork, "", new Listener<Integer, List<OrganizationInfo>>() {
            @Override
            public void onCallBack(Integer integer, List<OrganizationInfo> reply) {
                dismissWaitDialog();
                mListView.stopLoadMore();
                mListView.stopRefresh();
                datas.clear();
                if (integer == 1) {
                    //mAdapter.setDatas(reply);
                    datas.addAll( reply);
                }
                mAdapter.setDatas(datas);
                if( datas.size()>0){
                    air.setVisibility( View.GONE);
                }else{
                    air.setVisibility( View.VISIBLE);
                }
                search.setVisibility( View.VISIBLE);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        OrganizationInfo info = (OrganizationInfo) parent.getAdapter().getItem(position);
        if (info == null) {
            return;
        }
        Intent intent = new Intent(ApplyAssociActivity.this, ApplyToJoinActivity.class);
        intent.putExtra(ApplyToJoinActivity.ORG_ID, info.getOrg_id());
        //intent.putExtra("orgid" , info.getOrg_id());
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        // TODO Auto-generated method stub
        startSearchOrg(searchEditText.getText().toString());
    }

    @Override
    public void onLoadMore() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_SEARCH == actionId) {
            String keyword = v.getText().toString();
            if (keyword == null || TextUtils.isEmpty(keyword)) {
                return false;
            }
            setWaitingDialogText("搜索中...");
            showWaitDialog();
            startSearchOrg(keyword);
        }
        return false;
    }
}
