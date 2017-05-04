package app.logic.activity.main;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.ql.utils.QLToastUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import app.config.DemoApplication;
import app.config.http.HttpConfig;
import app.logic.activity.about.SettingActivity;
import app.logic.activity.announce.MyOrgActivity;
import app.logic.activity.calendar.MainCalendarActivity;
import app.logic.activity.card.CardListActivity2;
import app.logic.activity.checkin.MyOrganizaActivity;
import app.logic.activity.live.LiveListActivty;
import app.logic.activity.org.OrganizationListActivity2;
import app.logic.activity.user.QRCodePersonal;
import app.logic.activity.user.ShowBigImageActivity;
import app.logic.activity.user.UserInfoActivity;
import app.logic.adapter.YYBaseListAdapter;
import app.logic.controller.OrganizationController;
import app.logic.controller.UserManagerController;
import app.logic.pojo.OrganizationInfo;
import app.logic.pojo.UserInfo;
import app.utils.common.FrescoHelper;
import app.utils.common.FrescoImageShowThumb;
import app.utils.common.Listener;
import app.utils.helpers.ImagePickerHelper;
import app.utils.helpers.PropertySaveHelper;
import app.utils.helpers.QRHelper;
import app.yy.geju.R;

/**
 * SiuJiYung create at 2016-6-2 下午8:01:59
 */

public class UserCenterFragment extends Fragment implements OnItemClickListener, OnClickListener {
    private ImageView headImageView;
    private ImageView qlCodeImageView;
    private TextView nameTextView;
    private TextView titleTextView;
    private ListView functionListView;
    private GridView funtionGridView;
    private QRHelper qrHelper;
    private ImageView toRightView;
    private String headerFilePath = "";
    private File tempPath = null;
    private boolean haveOrgListStatus = false;
    private ImagePickerHelper pickerHelper;

    private YYBaseListAdapter<FunctionItem> funcationAdapter = new YYBaseListAdapter<UserCenterFragment.FunctionItem>(getActivity()) {
        @Override
        public View createView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.view_funtion_item2, null);
            }
            FunctionItem _item = (FunctionItem) getItem(position);
            ((TextView) convertView.findViewById(R.id.funcation_item_title_tv)).setText(_item.itemName);
            ((ImageView) convertView.findViewById(R.id.iv1)).setBackgroundResource(_item.iconResId);
            return convertView;
        }
    };

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.from(getActivity()).inflate(R.layout.activity_user_center, null);
            setView(view);
        }
        ViewGroup parent = (ViewGroup) view.getParent(); //user_center_name_ll
        if (parent != null) {
            parent.removeView(view);
        }
        return view;
    }

    private void setView(View view) {

        headImageView = (ImageView) view.findViewById(R.id.user_center_userimgview);
        qlCodeImageView = (ImageView) view.findViewById(R.id.user_center_qrcode_iv);
        functionListView = (ListView) view.findViewById(R.id.user_center_fun_lv);
        nameTextView = (TextView) view.findViewById(R.id.user_center_name_tv);
        titleTextView = (TextView) view.findViewById(R.id.user_center_title_tv);
        funtionGridView = (GridView) view.findViewById(R.id.user_center_fun_gv);
        toRightView = (ImageView) view.findViewById(R.id.user_center_user_info_flag_img);

        headImageView.setOnClickListener(this);
        view.findViewById(R.id.user_center_name_ll).setOnClickListener(this);
        toRightView.setOnClickListener(this);

        qrHelper = new QRHelper();
        FunctionItem item = null;
        String[] itemName = {"我的格局", "名片管理", "发布公告", "签到", "日程", "统计", "直播", "视频通话", "设置"};
        int[] itemIconIds = {R.drawable.my_org_list_icon, R.drawable.icon_card, R.drawable.icon_notice, R.drawable.icon_sign, R.drawable.icon_day, R.drawable.icon_count, R.drawable.icon_tv
                , R.drawable.icon_video, R.drawable.icon_set};
        ArrayList<FunctionItem> items = new ArrayList<UserCenterFragment.FunctionItem>();
        int idx = 10;
        int index = 0;
        for (String _name : itemName) {
            item = new FunctionItem();
            item.itemName = _name;
            item.itemId = idx;
            item.iconResId = itemIconIds[index];
            item.openEnable = true;
            idx++;
            items.add(item);
            index++;
        }
        funcationAdapter.setDatas(items);
        // functionListView.setAdapter(funcationAdapter);
        // functionListView.setOnItemClickListener(this);
        funtionGridView.setAdapter(funcationAdapter);
        funtionGridView.setOnItemClickListener(this);
        //getMyOrgList(); (接松周的逻辑，之前是在这个界面就判断有不有权限了，现在不是了，在下一个界面判断)
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserInfo();  //用户信息
        if (tempPath != null) {
//            Bitmap bitmap = BitmapFactory.decodeFile(tempPath.getAbsolutePath());
//            headImageView.setImageBitmap(bitmap);
            //FrescoHelper.asyncLoad(getActivity() ,Uri.parse(tempPath.getAbsolutePath()), headImageView);
//            GenericDraweeHierarchy hierarchy = headImageView.getHierarchy();
//            hierarchy.setPlaceholderImage(BitmapDrawable.createFromPath(tempPath.getAbsolutePath()));

        }
    }

    /**
     * 获取用户信息
     */
    private void loadUserInfo() {
        UserInfo userInfo = UserManagerController.getCurrUserInfo();
        String memberId = userInfo.getWp_member_info_id();
        UserManagerController.getUserInfo(getActivity(), memberId, new Listener<Integer, UserInfo>() {
            @Override
            public void onCallBack(Integer status, UserInfo reply) {
                if (reply != null) {
                    PropertySaveHelper.getHelper().save(reply, "kUSER_INFO_KEY");
                    UserManagerController.updateUserInfo(reply);
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refrashUI();
                    }
                });
            }
        });
    }

    /**
     * 向UI线程提交任务
     */
    private void refrashUI() {
        UserInfo info = UserManagerController.getCurrUserInfo();
        nameTextView.setText(info.getNickName());
        titleTextView.setText("格局号:" + info.getPhone());
        UserInfo _tmpInfo = new UserInfo();
        _tmpInfo.setName(info.getName());
        _tmpInfo.setNickName(info.getNickName());
        _tmpInfo.setId(info.getId());
        _tmpInfo.setWp_member_info_id(info.getWp_member_info_id());
        _tmpInfo.setPicture_url(info.getPicture_url());
        _tmpInfo.setPhone(info.getPhone());
        Gson gson = new Gson();
        String usrInfoJson = gson.toJson(_tmpInfo);
        String headImgPath = HttpConfig.getUrl(info.getPicture_url());
        Picasso.with(getContext()).load(headImgPath).placeholder(R.drawable.default_user_icon).fit().centerCrop().into(headImageView);

        //FrescoHelper.asyncLoad(getActivity() ,Uri.parse(headImgPath), headImageView);
        //Fresco 加入图片缓存
        //headImageView.setImageURI(headImgPath);
//        ImageRequest imageRequest =ImageRequest.fromUri(headImgPath);
//        ImagePipeline imagePipeline =Fresco.getImagePipeline();
//        DataSource<CloseableReference<CloseableImage>> dataSource =
//                imagePipeline.fetchDecodedImage(imageRequest, null);
//        dataSource.subscribe(new BaseDataSubscriber<CloseableReference<CloseableImage>>() {
//            @Override
//            protected void onNewResultImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
//                boolean isCache = dataSource.hasResult();
//                        System.out.println("===========result"+isCache);
//            }
//
//            @Override
//            protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
//
//            }
//        }, CallerThreadExecutor.getInstance());
        toRightView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        FunctionItem _item = (FunctionItem) funcationAdapter.getItem(arg2);
        if (_item == null) {
            return;
        }
        switch (_item.itemId) {
            case 10:
                // 我的格局
                Intent intent1 = new Intent();
                intent1.setClass(UserCenterFragment.this.getContext(), OrganizationListActivity2.class);
                startActivity(intent1);
                break;
            case 11:
                // 名片管理
                Intent intent = new Intent();
                intent.setClass(UserCenterFragment.this.getContext(), CardListActivity2.class);
                startActivity(intent);
                break;
            case 12:
                // 发布公告
                Intent intent2 = new Intent();
                intent2.setClass(UserCenterFragment.this.getContext(), MyOrgActivity.class);
                startActivity(intent2);
//			if (haveOrgListStatus) {
//				Intent intent2 = new Intent();
//				intent2.setClass(UserCenterFragment.this.getContext(), AnnounceActivity.class);
//				startActivity(intent2);
//			} else {
//				QLToastUtils.showToast(getActivity(), "没有权限");
//			}
                break;
            case 13:
                // 签到
                Intent checkInIntent = new Intent();
                checkInIntent.setClass(getContext(), MyOrganizaActivity.class);//跳转到我的组织列表界面
                startActivity(checkInIntent);
                break;
            case 14:
                // 日程
                Intent calendarIntent = new Intent();
                calendarIntent.setClass(getContext(), MainCalendarActivity.class);
                startActivity(calendarIntent);
                break;
            case 15:
                QLToastUtils.showToast(getContext(), "即将更新，敬请期待.");
                // 统计
                break;
            case 16:
                // 版本判断。当手机系统小于 18 时，才有必要去判断权限是否获取
                if (Build.VERSION.SDK_INT <  18 ) {
                    QLToastUtils.showToast( getContext() , "手机版本过低，支持4.3及以上版本");
                    return;
                }
                Intent liveintent = new Intent();
                liveintent.setClass(getContext(), LiveListActivty.class);
                startActivity(liveintent);
                break;
            case 17:
                QLToastUtils.showToast(getContext(), "即将更新，敬请期待.");
                break;
            case 18:
                getActivity().startActivity(new Intent(getActivity(), SettingActivity.class));
                break;
        }
    }

    class FunctionItem {
        String itemName;
        int itemId;
        int iconResId;
        boolean openEnable;
    }

    /**
     * 显示头像放大图
     */
    private void showUserImg(Bitmap bitmap) {
        startActivity(new Intent(getContext(), ShowBigImageActivity.class).putExtra(ShowBigImageActivity.PIC_BITMAP, bitmap));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.user_center_userimgview:
                Bitmap bitmap = null ;
                try {
                    bitmap = ((BitmapDrawable) headImageView.getDrawable()).getBitmap();
                }catch (Exception e){
                    bitmap = BitmapFactory.decodeResource( getActivity().getResources() , R.drawable.ic_launcher);
                }
                showUserImg(bitmap);
                break;
            case R.id.user_center_name_ll:
                Intent intent = new Intent();
                intent.setClass(UserCenterFragment.this.getContext(), UserInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.user_center_user_info_flag_img:
                //Intent intent2 = new Intent();
                //intent2.setClass(UserCenterFragment.this.getContext(), UserInfoActivity.class);
                //startActivity(intent2);
                showQRCode() ;
                break;
            default:
                break;
        }
    }

    /**
     * 获取我的组织列表
     */
    private void getMyOrgList() {
        OrganizationController.getMyOrganizationList(getActivity().getApplicationContext(), new Listener<Void, List<OrganizationInfo>>() {

            @Override
            public void onCallBack(Void status, List<OrganizationInfo> reply) {
                if (reply == null || reply.size() < 1) {
                    haveOrgListStatus = false;
                    return;
                }
                for (OrganizationInfo info : reply) {
                    if (!haveOrgListStatus) {
                        getOrgInfo(info.getOrg_id());
                    }
                }
            }
        });
    }

    /**
     * 获取组织详情
     */
    private synchronized void getOrgInfo(String org_id) {
        OrganizationController.getOrganizationInfo(getActivity(), org_id, new Listener<Void, List<OrganizationInfo>>() {
            @Override
            public void onCallBack(Void status, List<OrganizationInfo> reply) {
                if (reply == null || reply.size() < 1) {
                    return;
                }
                if (reply.get(0).getIsadmin() == 1) {
                    haveOrgListStatus = true;
                }
            }
        });
    }

    private void showQRCode() {
        UserInfo userInfo = UserManagerController.getCurrUserInfo();
        QRCodePersonal personalInfo = new QRCodePersonal();
        personalInfo.setPhone(userInfo.getPhone());
        personalInfo.setNickName(userInfo.getNickName());
        personalInfo.setPicture_url(userInfo.getPicture_url());
        personalInfo.setLocation(userInfo.getLocation());
        Gson gson = new Gson();
        String usrInfoJson = gson.toJson( personalInfo );  //String usrInfoJson = gson.toJson(userInfo);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int imgWidth = (int) dm.scaledDensity * 200;
        int imgHeight = (int) dm.scaledDensity * 200;
        Bitmap qriBitmap = qrHelper.createQRImage(usrInfoJson, imgWidth, imgHeight);
        String userPic = HttpConfig.getUrl(userInfo.getPicture_url());
        WindowManager m = getActivity().getWindowManager();
        Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
        WindowManager.LayoutParams p2 = new WindowManager.LayoutParams((int) (d.getWidth() * 0.70), (int) (d.getHeight() * 0.30));
        View dialog_view = getActivity().getLayoutInflater().inflate(R.layout.dialog_qrode, null);
        ImageView qrcode = (ImageView) dialog_view.findViewById(R.id.qrcode);
        SimpleDraweeView pic = (SimpleDraweeView) dialog_view.findViewById(R.id.im_personpic);
        TextView nick = (TextView) dialog_view.findViewById(R.id.tx_name);
        TextView phone = (TextView) dialog_view.findViewById(R.id.tx_phone);
        FrescoImageShowThumb.showThrumb(Uri.parse(userPic),pic);

//        FrescoHelper.asyncLoad(getActivity() ,Uri.parse(userPic), pic);
//        UserInfo info = UserManagerController.getCurrUserInfo();
//        Bitmap bitmap = ((BitmapDrawable) headImageView.getDrawable()).getBitmap();
//        pic.setImageBitmap(bitmap);
//        if (!TextUtils.isEmpty(info.getPicture_url())){
//            pic.setImageURI(HttpConfig.getUrl(info.getPicture_url()));
//        }

        nick.setText(userInfo.getNickName());
        phone.setText("格局号：" + userInfo.getPhone());
        String decodeString = null;
        try {
            decodeString = new String(usrInfoJson.getBytes(), "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            decodeString = usrInfoJson;
        }
        qrcode.setImageBitmap(showHeaderQRcodeBitmap(headImageView, decodeString, imgWidth, imgHeight));
        Dialog qrDialog = new Dialog(getContext() , R.style.dialog);
        qrDialog.setContentView(dialog_view, p2);
        qrDialog.show();
    }

    // 显示装有图片的二维码
    private Bitmap showHeaderQRcodeBitmap(ImageView imageView,String text, int imgWidth, int imgHeight) {
        Bitmap bitmap =null ;
        try {
            bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        }catch (Exception e){
            bitmap = BitmapFactory.decodeResource( getActivity().getResources() , R.drawable.ic_launcher);
        }

//        UserInfo info = UserManagerController.getCurrUserInfo();
//        Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.default_user_icon)).getBitmap();
//        if (!TextUtils.isEmpty(info.getPicture_url())){
////            bitmap = BitmapFactory.decodeFile(HttpConfig.getUrl(info.getPicture_url()));
//            //获取缓存的bitmap
//            ImagePipeline imagePipeline = Fresco.getImagePipeline();
//            ImageRequest imageRequest = ImageRequest.fromUri(Uri.parse(HttpConfig.getUrl(info.getPicture_url())));
//            DataSource<CloseableReference<CloseableImage>> dataSource =
//                    imagePipeline.fetchImageFromBitmapCache(imageRequest, CallerThreadExecutor.getInstance());
//            try {
//                CloseableReference<CloseableImage> imageReference = dataSource.getResult();
//                if (imageReference != null) {
//                    try {
//                        CloseableBitmap image = (CloseableBitmap) imageReference.get();
//                        // do something with the image
//                        bitmap = image.getUnderlyingBitmap();
//                    } finally {
//                        CloseableReference.closeSafely(imageReference);
//                    }
//                }
//            } finally {
//                dataSource.close();
//            }
////            FileBinaryResource resource = (FileBinaryResource)Fresco.getImagePipelineFactory().getMainDiskStorageCache().getResource(new SimpleCacheKey(uri.toString()));
////            File file = resource.getFile();
//        }
        // 图片宽度的一半
        //int IMAGE_HALFWIDTH = 30;
        DemoApplication.QRInsideImg = 30 ;
        int IMAGE_HALFWIDTH = DemoApplication.QRInsideImg;
        // 缩放图片
        Matrix matrix = new Matrix();
        float sx = (float) 2 * IMAGE_HALFWIDTH / bitmap.getWidth();
        float sy = (float) 2 * IMAGE_HALFWIDTH / bitmap.getHeight();
        matrix.setScale(sx, sy);
        // 重新构造一个40*40的图片
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        return qrHelper.createBitmapToHeader(text, bitmap, imgWidth, imgHeight);
    }
}
