package app.logic.activity.notice;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import org.ql.activity.customtitle.ActActivity;
import org.ql.utils.QLToastUtils;

import java.util.ArrayList;
import java.util.List;

import app.config.http.HttpConfig;
import app.logic.activity.ActTitleHandler;
import app.logic.activity.announce.AnnounceReportActivity;
import app.logic.activity.main.HomeActivity;
import app.logic.controller.AnnounceController;
import app.logic.pojo.NoticeInfo;
import app.utils.common.FrescoImageShowThumb;
import app.utils.common.Listener;
import app.utils.helpers.ShareHelper;
import app.yy.geju.R;
import cn.sharesdk.framework.ShareSDK;

/*
 * GZYY    2016-12-8  上午11:47:18
 * author: zsz
 */

public class DefaultNoticeActivity extends ActActivity implements AdapterView.OnItemClickListener,OnClickListener {

	public static final String NOTICE_ID = "NOTICE_ID";
	public static final String NAME = "NAME";

	private View lin_view ;
	private String notice_id , name;
	private TextView name_tv, maker_tv, createTime_tv, content_tv; //发布者，标题 ，时间，内容
	private TextView goodTv,addTv,reportTv;
	private GridView mGridView;
	private ImagesGridAdpter mAdpter;
	private ArrayList<String> mUrls;
	private ActTitleHandler titleHandler = new ActTitleHandler();
    private NoticeInfo noticeInfo;
	private String orgImageUrl;
	private SimpleDraweeView orgImage;
	private boolean isLike =false,isAdd=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        //初始化的代码尽量放到调用分享的activity的入口oncreat下就好，
        // 尽量不要再application里初始化，也可以多次调用初始化ShareSDK，
        // 初始化ShareSDK必须放到所有调用ShareSDK的最前端。
        ShareSDK.initSDK(this);
		setAbsHandler(titleHandler);
		setContentView(R.layout.activity_default_notice);
		//初始化TootBar
		intiTooaBar() ;
		//初始化View
		initView();
		//获取信息ID
		notice_id = getIntent().getStringExtra(NOTICE_ID);
		name = getIntent().getStringExtra(NAME);

		if (notice_id != null) {
			getDatas();  //获取数据
		}
	}
	
	/**
	 * 初始化TooTBar
	 */
	private void intiTooaBar(){
		titleHandler.replaseLeftLayout(this, true);
		setTitle("");
		((TextView) titleHandler.getLeftLayout().findViewById( R.id.left_tv) ).setText("公告详情");
		titleHandler.getLeftLayout().setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		titleHandler.getRightDefButton().setText("分享");
		titleHandler.getRightDefButton().setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(null==noticeInfo){
					QLToastUtils.showToast(DefaultNoticeActivity.this ,"公告信息获取失败");
					return;
				}
                ShareHelper.showShare(DefaultNoticeActivity.this,"我在【格局】看到一个不错资讯，向您推荐一下！",noticeInfo.getMsg_title(),orgImageUrl,HttpConfig.SHARE_NOTICE+notice_id);
			}
		});
	}

	/**
	 * 初始化View
	 */
	private void initView() {
		lin_view = findViewById( R.id.lin_view);
		name_tv = (TextView) findViewById(R.id.notice_name_tv);  //标题
		maker_tv = (TextView) findViewById(R.id.notice_maker_tv);//发布者
		createTime_tv = (TextView) findViewById(R.id.noitce_createTime_tv);  //时间
		content_tv = (TextView) findViewById(R.id.notice_content_tv);   //内容
		orgImage = (SimpleDraweeView) findViewById(R.id.org_image);
		lin_view.setVisibility( View.VISIBLE); //线显示

		goodTv = (TextView) findViewById(R.id.good_text);
		addTv = (TextView) findViewById(R.id.add_text);
		reportTv = (TextView) findViewById(R.id.report_text);
		goodTv.setOnClickListener(this);
		addTv.setOnClickListener(this);
		reportTv.setOnClickListener(this);
		orgImageUrl = getIntent().getStringExtra(OrgNoticeDefaultActivity.ORG_IMAGE);
		FrescoImageShowThumb.showThrumb(Uri.parse(HttpConfig.getUrl(orgImageUrl)),orgImage);

		mGridView = (GridView) findViewById(R.id.noitce_images);
		mUrls = new ArrayList<>();
		mAdpter = new ImagesGridAdpter(mUrls, this, mGridView);
		mGridView.setAdapter(mAdpter);
		mGridView.setOnItemClickListener(this);
		mGridView.setVisibility(View.GONE);
	}

	/**
	 * 获取公告详情
	 */
	private void getDatas() {
		
		AnnounceController.getAnnounceDetail(this, notice_id, new Listener<Void, List<NoticeInfo>>() {

			@Override
			public void onCallBack( Void status, List<NoticeInfo> reply ) {
				
				if (reply == null || reply.size() < 1) {
					Toast.makeText(DefaultNoticeActivity.this, "没有可读数据", Toast.LENGTH_SHORT).show();
					return;
				}

                noticeInfo = reply.get(0);

				name_tv.setText(noticeInfo.getMsg_title());
				if(name ==null || TextUtils.isEmpty(name)){
					maker_tv.setText(noticeInfo.getMsg_creator());
				}else{
					maker_tv.setText(name);
				}

				if (!TextUtils.isEmpty(noticeInfo.getMsg_create_time())) {
					createTime_tv.setText( noticeInfo.getMsg_create_time() );
//					String[] times = info.getMsg_create_time().split(" ");
//					String[] strings = times[0].split("-");
//					String[] lastTimesStrings = times[1].split(":");
//					createTime_tv.setText(strings[1] + "-" + strings[2] + " " + lastTimesStrings[0] + ":" + lastTimesStrings[1]);
				}
				
				if (noticeInfo.getIsLike()==1){
					Drawable drawable= getResources().getDrawable(R.drawable.icon_hand_n);
					// 这一步必须要做,否则不会显示.
					drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
					goodTv.setCompoundDrawables(drawable,null,null,null);
					isLike = true;
				}else{
					Drawable drawable= getResources().getDrawable(R.drawable.icon_hand_s);
					// 这一步必须要做,否则不会显示.
					drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
					goodTv.setCompoundDrawables(drawable,null,null,null);
					isLike = false;
				}

				if (noticeInfo.getIsParticipate()== 1){
					Drawable drawable= getResources().getDrawable(R.drawable.icon_star_n);
					// 这一步必须要做,否则不会显示.
					drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
					addTv.setCompoundDrawables(drawable,null,null,null);
					addTv.setText("已参与("+noticeInfo.getParticipateNum()+")");
					isAdd = true;
				}else{
					Drawable drawable= getResources().getDrawable(R.drawable.icon_star_s);
					// 这一步必须要做,否则不会显示.
					drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
					addTv.setCompoundDrawables(drawable,null,null,null);
					addTv.setText("我要参与("+noticeInfo.getParticipateNum()+")");
					isAdd = false;
				}

				goodTv.setText(noticeInfo.getLikeNum()+"");
				
                if(!TextUtils.isEmpty(noticeInfo.getMsg_notice_img())) {
					String[] urls = noticeInfo.getMsg_notice_img().split(",");
					for (String url : urls) {
						mUrls.add(HttpConfig.getUrl(url));
					}
				    mGridView.setVisibility(View.VISIBLE);
				    lin_view.setVisibility(View.GONE);
				}
				content_tv.setText(noticeInfo.getMsg_content());
				mAdpter.fixGridViewHeight(mGridView);

				//应该发起一次公告读取提醒（到服务器）
				//发送广播
				Intent intenUN = new Intent(HomeActivity.UPDATANOTICE);
				DefaultNoticeActivity.this.sendBroadcast( intenUN );
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(this, SeeImageActivity.class);
		//intent.putExtra(ShowBigImageActivity.KEY_PIC_REMOTE_PATH, mUrls.get(position));
		intent.putStringArrayListExtra(SeeImageActivity.DATAS,mUrls);
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.good_text:
				setData(isLike?"0":"1",0,null);
				break;
			case R.id.add_text:
				setData(isAdd?"0":"1",1,null);
				break;
			case R.id.report_text:
				startActivity(new Intent(this, AnnounceReportActivity.class).putExtra("notice_id",notice_id));
				break;
		}
	}

	private void setData(String content,final int type,String content_pic){
		showWaitDialog();
		AnnounceController.addMsgExtentionInfo(this, content, type, content_pic, notice_id, new Listener<Boolean, String>() {
			@Override
			public void onCallBack(Boolean aBoolean, String reply) {
				dismissWaitDialog();
				QLToastUtils.showToast(DefaultNoticeActivity.this,reply);
				if (aBoolean){
					if (type ==0){
						if (isLike){
							Drawable drawable= getResources().getDrawable(R.drawable.icon_hand_s);
							// 这一步必须要做,否则不会显示.
							drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
							goodTv.setCompoundDrawables(drawable,null,null,null);
							isLike = false;
							noticeInfo.setLikeNum(noticeInfo.getLikeNum()-1);
							goodTv.setText(noticeInfo.getLikeNum()+"");
						}else{
							Drawable drawable= getResources().getDrawable(R.drawable.icon_hand_n);
							// 这一步必须要做,否则不会显示.
							drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
							goodTv.setCompoundDrawables(drawable,null,null,null);
							isLike = true;
							noticeInfo.setLikeNum(noticeInfo.getLikeNum()+1);
							goodTv.setText(noticeInfo.getLikeNum()+"");
						}
					}else if (type == 1){
						if (isAdd){
							Drawable drawable= getResources().getDrawable(R.drawable.icon_star_s);
							// 这一步必须要做,否则不会显示.
							drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
							addTv.setCompoundDrawables(drawable,null,null,null);
							isAdd = false;
							noticeInfo.setParticipateNum(noticeInfo.getParticipateNum()-1);
							addTv.setText("我要参与("+noticeInfo.getParticipateNum()+")");
						}else{
							Drawable drawable= getResources().getDrawable(R.drawable.icon_star_n);
							// 这一步必须要做,否则不会显示.
							drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
							addTv.setCompoundDrawables(drawable,null,null,null);
							isAdd = true;
							noticeInfo.setParticipateNum(noticeInfo.getParticipateNum()+1);
							addTv.setText("已参与("+noticeInfo.getParticipateNum()+")");
						}
					}
				}
			}
		});
	}
}
