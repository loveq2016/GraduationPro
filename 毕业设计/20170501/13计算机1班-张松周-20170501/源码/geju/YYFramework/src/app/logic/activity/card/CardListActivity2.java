package app.logic.activity.card;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.utils.URLEncodedUtils;
import org.ql.activity.customtitle.ActActivity;
import org.ql.utils.QLToastUtils;
import org.ql.views.listview.QLXListView.IXListViewListener;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import app.config.http.HttpConfig;
import app.logic.activity.ActTitleHandler;
import app.logic.adapter.YYBaseListAdapter;
import app.logic.controller.UserManagerController;
import app.logic.pojo.CardInfo;
import app.logic.pojo.HWBusinessCardInfo;
import app.utils.common.FrescoImageShowThumb;
import app.utils.common.Listener;
import app.utils.helpers.BusinessCardHelper.OnScanPictureListener;
import app.utils.helpers.BusinessCardHelper;
import app.utils.helpers.ImagePickerHelper;
import app.utils.helpers.YYUtils;
import app.view.YYListView;
import app.yy.geju.R;

/*
 * GZYY    2016-12-22  上午11:55:05
 * author: zsz
 */

public class CardListActivity2 extends ActActivity implements OnScanPictureListener {

    private ActTitleHandler titleHandler;
    private View rightView;

    private YYListView listView;
    private ImagePickerHelper imagePickerHelper;
    private BusinessCardHelper cardHelper;

    private boolean hasDatas;

    private List<CardInfo> datas = new ArrayList<CardInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        titleHandler = new ActTitleHandler();
        setAbsHandler(titleHandler);
        setContentView(R.layout.activity_card_list2);

        initTitle();
        initView();
        addListener();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        showWaitDialog();
        getDatas();
    }

    private void initTitle() {

        setTitle("");
        titleHandler.replaseLeftLayout(this, true);
        ((TextView) titleHandler.getLeftLayout().findViewById(R.id.left_tv)).setText("名片管理");
        titleHandler.getLeftLayout().setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rightView = LayoutInflater.from(this).inflate(R.layout.title_activity_right_view, null);
        ImageView rightBtn = (ImageView) rightView.findViewById(R.id.right_title_view);
        titleHandler.addRightView(rightView, true);
        rightBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                openCarmen();

            }
        });
        // titleHandler.setBackgroudColor(getResources().getColor(R.color.transparent));
    }

    private void initView() {
        listView = (YYListView) findViewById(R.id.card_listView);
        listView.setPullLoadEnable(false);
        listView.setPullLoadEnable(false, true);

        listView.setAdapter(mAdapter);

        cardHelper = new BusinessCardHelper(CardListActivity2.this);
        cardHelper.setOnScanPictureListener(this);

    }

    private void addListener() {

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CardInfo info = mAdapter.getItem(position - 1);
                if (info != null) {
                    if (!hasDatas) {
                        openCarmen();
                    } else {
                        Intent intent = new Intent(CardListActivity2.this, CardDetailsActivity2.class);
                        intent.putExtra(CardDetailsActivity2.CARD_ID, info.getBc_id());
                        startActivity(intent);
                    }

                }

            }
        });

        listView.setXListViewListener(new IXListViewListener() {

            @Override
            public void onRefresh() {
                getDatas();

            }

            @Override
            public void onLoadMore() {

            }
        });

    }

    private void getDatas() {
        UserManagerController.getMyCardList(this, new Listener<Void, List<CardInfo>>() {

            @Override
            public void onCallBack(Void status, List<CardInfo> reply) {
                listView.stopRefresh();
                listView.stopLoadMore();
                dismissWaitDialog();
                datas.clear();
                if (reply != null && reply.size() > 0) {
                    datas.addAll(reply);

                    hasDatas = true;
                } else {
                    hasDatas = false;

                    CardInfo cardInfo = new CardInfo();
                    cardInfo.setDafaultImg(R.drawable.img_default_card);
                    cardInfo.setBc_name("设置我的第一张名片");
                    datas.add(cardInfo);
                }

                mAdapter.setDatas(datas);

            }
        });
    }

    private void openCarmen() {
         cardHelper.startScanCard(this);
//        imagePickerHelper = ImagePickerHelper.createNewImagePickerHelper(this);
//        imagePickerHelper.setImageSourceType(ImagePickerHelper.kImageSource_UserSelect);
//        imagePickerHelper.setOnReciveImageListener(new Listener<Void, String>() {
//
//            @Override
//            public void onCallBack(Void status, String reply) {
//                if (!TextUtils.isEmpty(reply)) {
//                    Intent intent = new Intent(CardListActivity2.this, CardDetailsActivity2.class);
//                    CardInfo cardInfo = new CardInfo();
//                    cardInfo.setBc_pic_url(reply);
//
//                    intent.putExtra(CardDetailsActivity2.CARD_INFO, cardInfo);
////                    intent.putExtra(CardDetailsActivity2.OPEN_MODE, false);
//                    startActivity(intent);
//
//                }
//
//            }
//        });
//        imagePickerHelper.setReplaceContentLayout(true);
//        imagePickerHelper.setCropStyle(2, 1, 1800, 900);
//        imagePickerHelper.setOpenSysCrop(true);
//        imagePickerHelper.openCamera();
    }

    private YYBaseListAdapter<CardInfo> mAdapter = new YYBaseListAdapter<CardInfo>(this) {

        @Override
        public View createView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(CardListActivity2.this).inflate(R.layout.item_card_list_activity, null);
                saveView("item_card_iv", R.id.item_card_iv, convertView);
                saveView("item_card_name_tv", R.id.item_card_name_tv, convertView);
                saveView("item_card_creator_tv", R.id.item_card_creator_tv, convertView);
                saveView("item_card_com_tv", R.id.item_card_com_tv, convertView);
            }
            CardInfo info = getItem(position);
            if (info != null) {
                SimpleDraweeView cardIv = getViewForName("item_card_iv", convertView);
                TextView nameTv = getViewForName("item_card_name_tv", convertView);
                TextView comTv = getViewForName("item_card_com_tv", convertView);
                TextView creatorTv = getViewForName("item_card_creator_tv", convertView);
                if (hasDatas) {
//                    Picasso.with(CardListActivity2.this).load(HttpConfig.getUrl(info.getBc_pic_url())).error(R.drawable.default_user_icon).fit().centerCrop().into(cardIv);
//                    cardIv.setImageURI(HttpConfig.getUrl(info.getBc_pic_url()));
                    FrescoImageShowThumb.showThrumb(Uri.parse(HttpConfig.getUrl(info.getBc_pic_url())),cardIv);
                    nameTv.setText(info.getBc_name());
                    if (info.getBc_orgName() != null) {
                        comTv.setText(info.getBc_orgName().get(0));
                    }
                    creatorTv.setText(info.getBc_title());
                    comTv.setVisibility(View.VISIBLE);

                } else {
                    Picasso.with(CardListActivity2.this).load(info.getDafaultImg()).error(R.drawable.default_user_icon).fit().centerCrop().into(cardIv);
                    comTv.setVisibility(View.GONE);
                    nameTv.setText(info.getBc_name());
                    creatorTv.setText("");

                }


            }
            return convertView;

        }
    };

    @Override
    public void onAnalyzing() {
        setWaitingDialogText("正在分析名片...请稍后");
        showWaitDialog();

    }

    @Override
    public void onScanResult(String result, String imgPath) {
        dismissWaitDialog();
        if (TextUtils.isEmpty(imgPath)) {
            QLToastUtils.showToast(CardListActivity2.this, "获取照片失败");
            return;
        }

        Intent intent = new Intent(CardListActivity2.this, CardDetailsActivity2.class);
        CardInfo cardInfo = new CardInfo();
        cardInfo.setBc_pic_url(imgPath);

        if (!TextUtils.isEmpty(result)) {
            // QLToastUtils.showToast(this, result);
//            Gson gson = new Gson();
//            HWBusinessCardInfo info = gson.fromJson(gson.toJson(result), new TypeToken<HWBusinessCardInfo>(){}.getType());


            try {
                Gson gson = new Gson();
                HWBusinessCardInfo hwCardInfo = gson.fromJson(result, HWBusinessCardInfo.class);
                cardInfo.setBc_cellPhone(getValuesMatchRex(hwCardInfo.getMobile(), "\\d{11,}"));// 使用模糊匹配即可
                cardInfo.setBc_email(getValuesMatchRex(hwCardInfo.getEmail(), "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*"));
                if (hwCardInfo.getIm() != null && hwCardInfo.getIm().size() > 0) {
                    ArrayList<String> _tmpList = new ArrayList<String>();
                    for (String _tmp : hwCardInfo.getIm()) {
                        if (YYUtils.isTencenQQNumber(_tmp) || YYUtils.isEmail(_tmp) || YYUtils.canUserForAccount(_tmp)) {
                            _tmpList.add(_tmp);
                        }
                    }
                    cardInfo.setBc_im(_tmpList);
                }

                if (hwCardInfo.getName() != null && hwCardInfo.getName().size() > 0) {
                    cardInfo.setBc_name(hwCardInfo.getName().get(0));
                }
                if (hwCardInfo.getComp() != null && hwCardInfo.getComp().size() > 0) {
                    ArrayList<String> _tmpList = new ArrayList<String>();
                    for (String _tmp : hwCardInfo.getComp()) {
                        if (!YYUtils.matchRex(_tmp, "^[1-9]\\d*")) {
                            _tmpList.add(_tmp);
                        }
                    }
                    cardInfo.setBc_orgName(_tmpList);
                }
                cardInfo.setBc_tel(getValuesMatchRex(hwCardInfo.getHtel(), "\\d{3}-\\d{8}|\\d{4}-\\d{7}"));
                if (hwCardInfo.getTitle() != null && hwCardInfo.getTitle().size() > 0) {
                    cardInfo.setBc_title(hwCardInfo.getTitle().get(0));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        intent.putExtra(AddCardActivity.kCARD_SCAN_INFO, cardInfo);
        intent.putExtra(CardDetailsActivity3.CARD_INFO, cardInfo);
        intent.putExtra(CardDetailsActivity3.OPEN_MODE, false);
        startActivity(intent);
    }

    private List<String> getValuesMatchRex(List<java.lang.String> list, java.lang.String rex) {
        if (list == null || list.size() < 1) {
            return null;
        }
        if (rex == null || TextUtils.isEmpty(rex)) {
            return list;
        }
        ArrayList<java.lang.String> _tmpList = new ArrayList<java.lang.String>();
        for (java.lang.String _tmp : list) {
            if (YYUtils.matchRex(_tmp, rex)) {
                _tmpList.add(_tmp);
            }
        }
        return _tmpList;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 150) {

        }

    }
}
