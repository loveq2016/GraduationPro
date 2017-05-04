package app.logic.activity.livestream;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.ql.activity.customtitle.ActActivity;
import org.ql.utils.QLToastUtils;
import org.ql.utils.network.QLHttpUtil;

import java.util.ArrayList;
import java.util.List;

import app.config.http.HttpConfig;
import app.logic.activity.ActTitleHandler;
import app.logic.adapter.YYBaseListAdapter;
import app.logic.controller.LivestreamController;
import app.logic.pojo.LivestreamInfo;
import app.logic.pojo.OrgListByBuilderInfo;
import app.utils.common.Listener;
import app.view.DialogNewStyleController;
import app.view.dialog.SelectOrgToLivestreamDailog;
import app.yy.geju.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GZYY on 17/2/13.
 */

public class LiveOrgListActivity extends ActActivity implements AdapterView.OnItemClickListener {


    /**
     * @item: item_livestream_orglist_activity
     */
    private YYBaseListAdapter<LivestreamInfo> mAdapter = new YYBaseListAdapter<LivestreamInfo>(this) {
        @Override
        public View createView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(LiveOrgListActivity.this).inflate(R.layout.item_livestream_orglist_activity, null);
                saveView("item_livestream_iv", R.id.item_livestream_iv, convertView);
                saveView("item_livestream_org_tv", R.id.item_livestream_org_tv, convertView);
                saveView("item_livestream_nickname_tv", R.id.item_livestream_nickname_tv, convertView);
            }
            LivestreamInfo info = getItem(position);
            if (info != null) {
                ImageView contentIv = getViewForName("item_livestream_iv", convertView);
                TextView orgNameTv = getViewForName("item_livestream_org_tv", convertView);
                TextView nickNameTv = getViewForName("item_livestream_nickname_tv", convertView);

                Picasso.with(LiveOrgListActivity.this).load(HttpConfig.getUrl(info.getOrg_logo_url())).error(R.drawable.default_user_icon).fit().centerCrop().into(contentIv);
                orgNameTv.setText(info.getOrg_name());
                nickNameTv.setText(info.getOrg_builder_name());
            }
            return convertView;
        }
    };

    @BindView(R.id.livestream_gv)
    GridView mGridView;
    @BindView(R.id.tag_iv)
    ImageView mTagIv;
    @BindView(R.id.empty_view)
    LinearLayout emptyView;
    @BindView(R.id.empty_view_iv)
    ImageView emptyViewIV;
    @BindView(R.id.empty_view_tv)
    TextView emptyViewTv;

    private ActTitleHandler titleHandler = new ActTitleHandler();

    private List<LivestreamInfo> mData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbsHandler(titleHandler);
        setContentView(R.layout.activity_live_orglist);

        ButterKnife.bind(this);
        initTitle();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    /**
     *
     */
    private void initTitle() {
        setTitle("");
        titleHandler.replaseLeftLayout(this, true);
        ((TextView) titleHandler.getLeftLayout().findViewById(R.id.left_tv)).setText("直播详情");
        titleHandler.getLeftLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(this);
    }


    /**
     * updata ui
     */
    private void updataUI() {

    }


    /**
     * getData
     */
    private void getData() {
        LivestreamController.getLivestreamList(this, new Listener<String, List<LivestreamInfo>>() {
            @Override
            public void onCallBack(String s, List<LivestreamInfo> reply) {
                if (reply == null || reply.size() < 1) {
                    emptyView.setVisibility(View.VISIBLE);
                    mGridView.setVisibility(View.GONE);
                    return;
                }
                mData.clear();
                mData.addAll(reply);
                mAdapter.setDatas(mData);
                emptyView.setVisibility(View.GONE);
                mGridView.setVisibility(View.VISIBLE);
            }
        });
    }


    /**
     * 获取可以直播的数据列表
     */
    private void getOrgListByBuilder() {
        LivestreamController.getOrgListByBuilder(this, new Listener<String, List<OrgListByBuilderInfo>>() {
            @Override
            public void onCallBack(String s, List<OrgListByBuilderInfo> reply) {
                SelectOrgToLivestreamDailog dialog = new SelectOrgToLivestreamDailog(LiveOrgListActivity.this, reply);
                dialog.show();
            }
        });

    }


    @OnClick(R.id.tag_iv)
    void selectOrgToLiveStream(View view) {
        getOrgListByBuilder();

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


    }
}
