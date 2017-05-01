package app.logic.activity.notice;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import org.ql.activity.customtitle.ActActivity;

import java.util.ArrayList;
import java.util.List;

import app.config.http.HttpConfig;
import app.logic.activity.ActTitleHandler;
import app.logic.activity.announce.ShowBigImageActivity;
import app.logic.activity.main.HomeActivity;
import app.logic.controller.AnnounceController;
import app.logic.pojo.NoticeInfo;
import app.utils.common.Listener;
import app.yy.geju.R;

/*
 * GZYY    2016-12-8  上午11:47:18
 * author: zsz
 */

public class DefaultNoticeActivity extends ActActivity implements AdapterView.OnItemClickListener {

	public static final String NOTICE_ID = "NOTICE_ID";
	public static final String NAME = "NAME";

	private View lin_view ;
	private String notice_id , name;
	private TextView name_tv, maker_tv, createTime_tv, content_tv; //发布者，标题 ，时间，内容
	private GridView mGridView;
	private ImagesGridAdpter mAdpter;
	private ArrayList<String> mUrls;
	private ActTitleHandler titleHandler = new ActTitleHandler(); 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
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
		lin_view.setVisibility( View.VISIBLE); //线显示

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
				
				NoticeInfo info = reply.get(0);

				name_tv.setText(info.getMsg_title());
				if(name ==null || TextUtils.isEmpty(name)){
					maker_tv.setText(info.getMsg_creator());
				}else{
					maker_tv.setText(name);
				}

				if (!TextUtils.isEmpty(info.getMsg_create_time())) {
					createTime_tv.setText( info.getMsg_create_time() );
//					String[] times = info.getMsg_create_time().split(" ");
//					String[] strings = times[0].split("-");
//					String[] lastTimesStrings = times[1].split(":");
//					createTime_tv.setText(strings[1] + "-" + strings[2] + " " + lastTimesStrings[0] + ":" + lastTimesStrings[1]);
				}
				
//				if( !TextUtils.isEmpty( info.getMsg_notice_img()) || !TextUtils.isEmpty( info.getMsg_cover()) ){					
//					if( !TextUtils.isEmpty( info.getMsg_notice_img()) ){
//						Picasso.with( DefaultNoticeActivity.this ).load(  HttpConfig.getUrl(info.getMsg_notice_img())).centerCrop().fit().into( notice_iv );
//					}else {
//						Picasso.with( DefaultNoticeActivity.this ).load(  HttpConfig.getUrl(info.getMsg_cover())).centerCrop().fit().into( notice_iv );
//					}					
//					notice_iv.setVisibility( View.VISIBLE ) ;  //图片显示
//					lin_view.setVisibility( View.GONE) ;       //线隐藏
//					QLToastUtils.showToast( DefaultNoticeActivity.this, "URL = "+ info.getMsg_notice_img() ) ;
//				}else {
//					QLToastUtils.showToast( DefaultNoticeActivity.this, "不存在图片 = "+ info.getMsg_cover() ) ;
//				}
				
                if(!TextUtils.isEmpty(info.getMsg_notice_img())) {
					String[] urls = info.getMsg_notice_img().split(",");
					for (String url : urls) {
						mUrls.add(HttpConfig.getUrl(url));
					}
				    mGridView.setVisibility(View.VISIBLE);
				    lin_view.setVisibility(View.GONE);
				}
				content_tv.setText(info.getMsg_content());
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
		Intent intent = new Intent(this, ShowBigImageActivity.class);
		intent.putExtra(ShowBigImageActivity.KEY_PIC_REMOTE_PATH, mUrls.get(position));
		startActivity(intent);
	}
}
