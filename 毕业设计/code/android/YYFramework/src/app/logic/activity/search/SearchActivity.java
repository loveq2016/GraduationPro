package app.logic.activity.search;

import java.util.ArrayList;
import java.util.List;

import org.QLConstant;
import org.ql.activity.customtitle.ActActivity;
import org.ql.utils.QLToastUtils;

import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.picasso.Picasso;

import android.R.integer;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import app.config.http.HttpConfig;
import app.logic.activity.notice.DefaultNoticeActivity;
import app.logic.activity.org.DPMListActivity;
import app.logic.activity.user.PreviewFriendsInfoActivity;
import app.logic.adapter.YYBaseListAdapter;
import app.logic.controller.AnnounceController;
import app.logic.controller.OrganizationController;
import app.logic.controller.UserManagerController;
import app.logic.pojo.NoticeInfo;
import app.logic.pojo.OrganizationInfo;
import app.logic.pojo.SearchInfo;
import app.logic.pojo.SearchItemInfo;
import app.logic.pojo.UserInfo;
import app.logic.pojo.YYChatSessionInfo;
import app.utils.common.FrescoImageShowThumb;
import app.utils.common.Listener;
import app.utils.helpers.ChartHelper;
import app.view.YYListView;
import app.yy.geju.R;

/*
 * GZYY    2016-12-6  下午6:05:07
 * author: zsz
 */

public class SearchActivity extends ActActivity {

    private UserInfo userInfo;

    private EditText search_edt;
    private RelativeLayout search_default_rl;
    private YYListView listView;

    private LayoutInflater inflater;

    private List<SearchItemInfo> datas = new ArrayList<SearchItemInfo>();
    private YYBaseListAdapter<SearchItemInfo> mAdapter = new YYBaseListAdapter<SearchItemInfo>(this) {

        @Override
        public View createView(int position, View convertView, ViewGroup parent) {
            if (getItemViewType(position) == 2) {
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.item_search_listview_org, null);
                    saveView("item_iv", R.id.item_iv, convertView);
                    saveView("item_name_tv", R.id.item_name_tv, convertView);
                    saveView("item_title", R.id.item_title, convertView);
                }
                SearchItemInfo info = getItem(position);
                if (info != null) {
                    SimpleDraweeView imageView = getViewForName("item_iv", convertView);
//                    imageView.setImageURI(HttpConfig.getUrl(info.getOrgDatas().getOrg_logo_url()));
                    FrescoImageShowThumb.showThrumb(Uri.parse(HttpConfig.getUrl(info.getOrgDatas().getOrg_logo_url())),imageView);
//                    Picasso.with(SearchActivity.this).load(HttpConfig.getUrl(info.getOrgDatas().getOrg_logo_url())).fit().centerCrop().into(imageView);
                    setTextToViewText(info.getOrgDatas().getOrg_name(), "item_name_tv", convertView);
                    TextView titleTv = getViewForName("item_title", convertView);
                    titleTv.setText("格局");
                    titleTv.setVisibility(info.isTitleStatus() ? View.VISIBLE : View.GONE);

                }

            } else if (getItemViewType(position) == 1) {
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.item_search_listview_org, null);
                    saveView("item_iv", R.id.item_iv, convertView);
                    saveView("item_name_tv", R.id.item_name_tv, convertView);
                    saveView("item_title", R.id.item_title, convertView);
                }
                SearchItemInfo info = getItem(position);
                if (info != null) {
                    setImageToImageViewCenterCrop(HttpConfig.getUrl(info.getNoticeDatas().getMsg_cover()), "item_iv", -1, convertView);
                    setTextToViewText(info.getNoticeDatas().getMsg_title(), "item_name_tv", convertView);
                    TextView titleTv = getViewForName("item_title", convertView);
                    titleTv.setText("公告");
                    titleTv.setVisibility(info.isTitleStatus() ? View.VISIBLE : View.GONE);
                }

            } else {
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.item_search_listview_message, null);
                    saveView("item_iv", R.id.item_iv, convertView);
                    saveView("item_name_tv", R.id.item_name_tv, convertView);
                    saveView("item_title", R.id.item_title, convertView);
                    saveView("item_centent_tv", R.id.item_centent_tv, convertView);
                }
                SearchItemInfo info = getItem(position);
                if (info != null) {
                    setImageToImageViewCenterCrop(HttpConfig.getUrl(info.getChatDatas().getPicture_url()), "item_iv", -1, convertView);
                    if(info.getChatDatas().getFriend_name()!=null && !TextUtils.isEmpty(info.getChatDatas().getFriend_name())){
                        setTextToViewText(info.getChatDatas().getFriend_name(), "item_name_tv", convertView);
                    }else{
                        setTextToViewText(info.getChatDatas().getNickName(), "item_name_tv", convertView);
                    }
                    TextView titleTv = getViewForName("item_title", convertView);
                    titleTv.setText("联系人");
                    titleTv.setVisibility(info.isTitleStatus() ? View.VISIBLE : View.GONE);
                }

            }

            return convertView;

        }

        /**
         * 0是对话消息,1是公告，2是组织
         */
        public int getItemViewType(int position) {
            SearchItemInfo info = getItem(position);
            if (info.getOrgDatas() != null) {
                return 2;
            }
            if (info.getNoticeDatas() != null) {
                return 1;
            }
            return 0;
        }

        public int getViewTypeCount() {
            return 3;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search);
        userInfo = UserManagerController.getCurrUserInfo();
        inflater = LayoutInflater.from(this);
        findViewById(R.id.left_iv).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initView();
        addListener();
    }

    private void initView() {
        search_edt = (EditText) findViewById(R.id.search_edt);
        search_default_rl = (RelativeLayout) findViewById(R.id.search_default_rl);
        listView = (YYListView) findViewById(R.id.listView);
        listView.setPullLoadEnable(false, true);
        listView.setPullRefreshEnable(false);
        listView.setAdapter(mAdapter);
    }

    private void addListener() {
        search_edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s.toString())) {
                    getData(s.toString());
                } else {
                    search_default_rl.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SearchItemInfo info = datas.get(position - 1);
                if (info == null) {
                    return;
                }
                if (info.getOrgDatas() != null) {
                    Intent intent = new Intent(SearchActivity.this, DPMListActivity.class);
                    intent.putExtra(DPMListActivity.kORG_ID, info.getOrgDatas().getOrg_id());
                    intent.putExtra(DPMListActivity.kORG_NAME, info.getOrgDatas().getOrg_name());
                    startActivity(intent);
                } else if (info.getNoticeDatas() != null) {
                    startActivity(new Intent(SearchActivity.this, DefaultNoticeActivity.class).putExtra(DefaultNoticeActivity.NOTICE_ID, info.getNoticeDatas().getMsg_id()));
                } else {
                    String tagerIdString = info.getChatDatas().getWp_other_info_id();
                    if (QLConstant.client_id.equals(tagerIdString)) {
                        QLToastUtils.showToast(SearchActivity.this, "该用户是自己");
                        return;
                    }
//                    ChartHelper.startChart(SearchActivity.this, info.getChatDatas().getWp_other_info_id(), "");
                    Intent openFriendDetailsIntent = new Intent(SearchActivity.this, PreviewFriendsInfoActivity.class);
                    openFriendDetailsIntent.putExtra(PreviewFriendsInfoActivity.kFROM_CHART_ACTIVITY, false);
                    openFriendDetailsIntent.putExtra(PreviewFriendsInfoActivity.kUSER_MEMBER_ID, info.getChatDatas().getWp_other_info_id());
                    startActivity(openFriendDetailsIntent);
                }
            }
        });
    }

    private synchronized void getData(String keyword) {
        UserManagerController.getSearchAllMessage(this, keyword, new Listener<Integer, SearchInfo>() {

            @Override
            public void onCallBack(Integer status, SearchInfo reply) {
                if (reply != null) {
                    datas.clear();
                    int titleStatus = 0;
                    if (reply.getAssociation() != null) {
                        for (OrganizationInfo info : reply.getAssociation()) {
                            SearchItemInfo itemInfo = new SearchItemInfo();
                            itemInfo.setOrgDatas(info);
                            datas.add(itemInfo);
                            itemInfo.setTitleStatus(titleStatus == 0 ? true : false);
                            titleStatus = 1;
                        }
                    }

                    titleStatus = 0;
                    if (reply.getMessage() != null) {
                        for (NoticeInfo info : reply.getMessage()) {
                            SearchItemInfo itemInfo = new SearchItemInfo();
                            itemInfo.setNoticeDatas(info);
                            datas.add(itemInfo);
                            itemInfo.setTitleStatus(titleStatus == 0 ? true : false);
                            titleStatus = 1;
                        }
                    }

                    titleStatus = 0;
                    if (reply.getMember() != null) {
                        for (YYChatSessionInfo info : reply.getMember()) {
                            SearchItemInfo itemInfo = new SearchItemInfo();
                            itemInfo.setChatDatas(info);
                            datas.add(itemInfo);
                            itemInfo.setTitleStatus(titleStatus == 0 ? true : false);
                            titleStatus = 1;
                        }
                    }
                    mAdapter.setDatas(datas);
                    if (datas.size() > 0) {
                        search_default_rl.setVisibility(View.GONE);
                        listView.setVisibility(View.VISIBLE);
                    } else {
                        search_default_rl.setVisibility(View.VISIBLE);
                        listView.setVisibility(View.GONE);
                    }
                }
            }
        });
    }
}
