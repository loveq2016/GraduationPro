package app.logic.activity.announce;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ql.activity.customtitle.ActActivity;
import org.ql.activity.customtitle.OnActActivityResultListener;
import org.ql.utils.QLToastUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import app.config.http.HttpConfig;
import app.logic.activity.ActTitleHandler;
import app.logic.activity.main.HomeActivity;
import app.logic.controller.AnnounceController;
import app.logic.pojo.OrganizationInfo;
import app.utils.common.Listener;
import app.utils.helpers.ImagePickerHelper;
import app.view.DialogBottom;
import app.yy.geju.R;

/*
 * GZYY    2016-8-3  上午10:14:03
 */

public class AnnounceActivity extends ActActivity implements OnClickListener, AdapterView.OnItemClickListener ,View.OnTouchListener{

    public static final String ORGNAME = "ORGNAME";
    public static final String ORGINFO = "ORGINFO";

    /**
     * 获取到的图片路径
     */
    private String picPath;
    /**
     * 头像名称
     */
    private static final String IMAGE_FILE_NAME = "image.jpg";
    /**
     * 请求码
     */
    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int RESULT_REQUEST_CODE = 2;
    private ImagePickerHelper pickerHelper;
    private ActTitleHandler handler;
    private EditText title_et, text_et;    //公告标题和内容
    private ArrayList<String> mPicPaths;
    private GridView mImagePickGridView;
    private AddImagesGridAdpter mImagePickAdapter;
    private String org_id;
    private Intent mIntent;
    private DialogBottom mDialog;
    private OrganizationInfo org;
    private Bitmap photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new ActTitleHandler();
        setAbsHandler(handler);
        setContentView(R.layout.activity_announce2);
        //初始化TooBar
        initActHandler();
        //初始化View
        initView();
        //初始化对话框
        //initDialog();
    }

    /**
     * 初始化TootBar
     */
    private void initActHandler() {
        //获取Intent对象
        mIntent = getIntent();
        org = (OrganizationInfo) mIntent.getSerializableExtra(ORGINFO);
        setTitle("");
        handler.getRightDefButton().setText("发布");
        handler.getRightDefButton().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //发布公告
                sendAnnounce(org.getOrg_id());
            }
        });
        handler.addLeftView(LayoutInflater.from(this).inflate(R.layout.title_leftview_layout, null), true);
        handler.getCenterLayout().findViewById(android.R.id.title).setOnClickListener(this);
        handler.getCenterLayout().findViewById(R.id.centerIv).setVisibility(View.GONE);
        TextView tv = (TextView) handler.getLeftLayout().findViewById(R.id.left_tv);
        tv.setText(mIntent.getStringExtra(ORGNAME));
        handler.getLeftLayout().setOnClickListener(new OnClickListener() {
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
        text_et = (EditText) findViewById(R.id.text_et);   //公告内容
        title_et = (EditText) findViewById(R.id.title_et); //公告标题
        mImagePickGridView = (GridView) findViewById(R.id.grid_view_image_pick);
        mPicPaths = new ArrayList<>();
        mImagePickAdapter = new AddImagesGridAdpter(mPicPaths, this, mImagePickGridView);
        mImagePickGridView.setOnItemClickListener(this);
        mImagePickGridView.setAdapter(mImagePickAdapter);
        mImagePickAdapter.fixGridViewHeight(mImagePickGridView);
        text_et.setOnTouchListener( this );
        title_et.setOnTouchListener( this );
    }

    /**
     * 初始化对话框
     */
    private void initDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_select_imgpicker_action, null);
        view.findViewById(R.id.img_picker_alerm).setOnClickListener(this);//打开相册
        view.findViewById(R.id.img_picker_camera).setOnClickListener(this);//打开相机
        view.findViewById(R.id.del_rl).setOnClickListener(this);//对话消失
        mDialog = new DialogBottom(this, view);
    }

    /**
     * 发送公告
     *
     * @param org_id
     */
    private void sendAnnounce(final String org_id) {
        final String msg_title = title_et.getText().toString();
        final String msg_content = text_et.getText().toString();
        if (org_id == null || TextUtils.isEmpty(org_id)) {
            QLToastUtils.showToast(AnnounceActivity.this, "你暂时还没有组织可选择");
            return;
        }

        if (!msg_title.equals("") && !msg_content.equals("")) {
            showWaitDialog();//正在处理，请稍后


            if (mPicPaths == null || mPicPaths.size()==0 || (mPicPaths.size() == 1 && mPicPaths.get(0).equals(""))){
                AnnounceController.announceUser2(AnnounceActivity.this, msg_title, org_id, msg_content, "", new Listener<Boolean, String>() {
                    @Override
                    public void onCallBack(Boolean status, String reply) {
                        dismissWaitDialog();
                        QLToastUtils.showToast(AnnounceActivity.this, reply);

                        if (status) {
                            QLToastUtils.showToast(AnnounceActivity.this, "发送成功");
                            Intent intenUN = new Intent(HomeActivity.UPDATANOTICE);
                            AnnounceActivity.this.sendBroadcast(intenUN);
                            finish();
                        } else {
                            QLToastUtils.showToast(AnnounceActivity.this, "发送失败");
                        }
                    }
                });
                return;
            }
            // 去掉最后的空串
            if(mPicPaths.size()>0 && mPicPaths.get(mPicPaths.size()-1).equals("")){
                mPicPaths.remove(mPicPaths.size() - 1);
            }


            new Thread(new Runnable() {
                @Override
                public void run() {
                    final ArrayList<String> ids = new ArrayList<>();
                    for (int i = 0; i < mPicPaths.size(); i++) {
                        final int ii = i;
                        FileUploader.uploadFile(new File(mPicPaths.get(i)), HttpConfig.getUrl(HttpConfig.UPLOAD_IMAGE), null, new FileUploader.Callback() {
                            @Override
                            public void onSuccess(String data) {
                                Log.d("CHEN", "result --> " + data);
                                try {
                                    JSONArray array = new JSONObject(data).getJSONArray("root");
                                    ids.add(array.getString(0));
                                } catch (JSONException ignored) {
                                }

                                if (ii == mPicPaths.size() - 1) {
                                    String newIds = "";
                                    for (int i = 0; i < ids.size(); i++) {
                                        newIds = newIds + ids.get(i) + ",";
                                    }
                                    newIds = newIds.substring(0, newIds.length() - 1);
                                    Log.d("CHEN", newIds);

                                    AnnounceController.announceUser2(AnnounceActivity.this, msg_title, org_id, msg_content, newIds, new Listener<Boolean, String>() {
                                        @Override
                                        public void onCallBack(Boolean status, String reply) {
                                            dismissWaitDialog();
                                            QLToastUtils.showToast(AnnounceActivity.this, reply);

                                            if (status) {
                                                QLToastUtils.showToast(AnnounceActivity.this, "发送成功");
                                                Intent intenUN = new Intent(HomeActivity.UPDATANOTICE);
                                                AnnounceActivity.this.sendBroadcast(intenUN);
                                                finish();
                                            } else {
                                                QLToastUtils.showToast(AnnounceActivity.this, "发送失败");
                                            }
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onFailed(Exception e) {
                                if (mPicPaths != null && !mPicPaths.get(mPicPaths.size()-1).equals("")){
                                    mPicPaths.add("");
                                }
                                dismissWaitDialog();
                                QLToastUtils.showToast(AnnounceActivity.this, "发送失败");
                            }
                        });
                    }
                }
            }).start();


//            AnnounceController.announceUser2(AnnounceActivity.this, msg_title, org_id, msg_content, getRandomString(8), new Listener<Boolean, String>() {
//                @Override
//                public void onCallBack(Boolean status, String reply) {
//                    dismissWaitDialog();//关闭对话框
//                    QLToastUtils.showToast(AnnounceActivity.this, reply);
//                    if (status) {
//                        QLToastUtils.showToast(AnnounceActivity.this, "发送成功");
//                        // xj_img.setImageDrawable(getResources().getDrawable(R.drawable.btn_add_pic));//设为默认图片
//                        // xj_img.setEnabled(true);
//                        // img.setVisibility(View.GONE);//隐藏不占位置
//                        // del_img.setVisibility(View.GONE);
//                        //text_et.setText("");//标题和内容都清空
//                        //title_et.setText("");
//                        //ZSZSingleton.getZSZSingleton().getSendNoticeUpdataUnreadListener().onCallBack();
//                        Intent intenUN = new Intent(HomeActivity.UPDATANOTICE);
//                        //发送广播
//                        AnnounceActivity.this.sendBroadcast(intenUN);
//                        finish();
//                    } else {
//                        QLToastUtils.showToast(AnnounceActivity.this, "发送失败");
//                    }
//                }
//            });

        } else {
            QLToastUtils.showToast(AnnounceActivity.this, "请输入完整的信息");
        }
    }

//    public String getRandomString(int n) {
//        String chars = "0123456789abcdefghijklmnopqrstuvwxyz";
//        StringBuilder sb = new StringBuilder();
//        Random random = new Random();
//        for(int i = 0; i < n; i++) {
//            sb.append(chars.charAt(random.nextInt(chars.length())));
//        }
//        return sb.toString();
//    }

//    @Override
//    public void onClick(View v) {
//        int id = v.getId();
//        switch (id) {
//
//		case R.id.img_picker_alerm://打开相册
//			 Intent intentFromGallery = new Intent();
//             intentFromGallery.setType("image/*"); // 设置文件类型
//             intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
//             startActivityForResult(intentFromGallery, IMAGE_REQUEST_CODE);
//             mDialog.dismiss();
//			break;
//		case R.id.img_picker_camera://打开相机
//			 Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//             // 判断存储卡是否可以用，可用进行存储
//             String state = Environment.getExternalStorageState();
//             if (state.equals(Environment.MEDIA_MOUNTED)) {
//                 File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
//                 File file = new File(path , IMAGE_FILE_NAME);
//                 intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
//             }
//             startActivityForResult(intentFromCapture, CAMERA_REQUEST_CODE);
//             mDialog.dismiss();
//			break;
//		
//		case R.id.del_rl://对话框消失
//			mDialog.dismiss();
//			break;
//		
//            case R.id.xj://打开相册或相机
//			
//			if( mDialog.isShowing()){
//				mDialog.dismiss();
//			}else{
//				mDialog.show();
//			}
//
//                open();
//
//                break;
//
//            case R.id.del:
//                xj_img.setImageDrawable(getResources().getDrawable(R.drawable.btn_add_pic));//设为默认图片
//                img.setVisibility(View.GONE);//隐藏不占位置
//                del_img.setVisibility(View.GONE);//隐藏不占位置
//                xj_img.setEnabled(true);
//
//                break;
//            default:
//                break;
//        }
//    }


//	@Override
//	protected void onActivityResult( int requestCode, int resultCode, Intent data) {
//	   // 结果码不等于取消时候
//	   if (resultCode != RESULT_CANCELED) {
//	        switch (requestCode) {
//	            case IMAGE_REQUEST_CODE :
//	                 startPhotoZoom(data.getData()); //实现剪裁图片
//	                 break;
//	            case CAMERA_REQUEST_CODE :
//	                    // 判断存储卡是否可以用，可用进行存储
//	                 String state = Environment.getExternalStorageState();
//	                 if (state.equals(Environment.MEDIA_MOUNTED)) {
//	                      File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
//	                      File tempFile = new File(path, IMAGE_FILE_NAME);
//	                      startPhotoZoom(Uri.fromFile(tempFile));//实现剪裁图片
//	                  } else {
//	                      Toast.makeText(getApplicationContext(), "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
//	                  }
//	                  break;
//	             case RESULT_REQUEST_CODE : // 图片缩放完成后
//	                  if (data != null) {
//	                      getImageToView(data);
//
//	                   }
//	                  break;
//	            }
//	        }
//	   super.onActivityResult( requestCode , resultCode , data );
//	}

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 340);
        intent.putExtra("outputY", 340);
        intent.putExtra("return-data", true); // true:不返回uri，false：返回uri
        startActivityForResult(intent, RESULT_REQUEST_CODE);
    }


    /**
     * 保存裁剪之后的图片数据
     *
     * @param
     */
    private void getImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            photo = extras.getParcelable("data");
//          Drawable drawable = new BitmapDrawable( this.getResources(), photo );
//          xj_img.setImageBitmap(photo);
//          img.setImageBitmap(photo);
//          xj_img.setImageDrawable(drawable);
//          img.setImageDrawable(drawable);
//          img.setVisibility(View.VISIBLE);
//          del_img.setVisibility(View.VISIBLE);
//          xj_img.setEnabled(false);
        }
    }

    //打开相机或相册
    private void open() {
        pickerHelper = ImagePickerHelper.createNewImagePickerHelper(this);//获取实例对象
        pickerHelper.setImageSourceType(ImagePickerHelper.kImageSource_UserSelect);
        pickerHelper.setOnReciveImageListener(new Listener<Void, String>() {
            @Override
            public void onCallBack(Void status, String reply) {
                //关闭对话框
                dismissWaitDialog();
                if (!TextUtils.isEmpty(reply)) {
                    if (mPicPaths.size() > 0 && mPicPaths.get(mPicPaths.size() - 1).equals("")) {
                        mPicPaths.remove(mPicPaths.size() - 1);
                    }
                    mImagePickAdapter.add(reply);
//					UserManagerController.uploadUserHeadImage(this, reply, new Listener<Integer, String>() {
//						@Override
//						public void onCallBack(Integer status, String reply) {
//							dismissWaitDialog();
//						}
//					});
                }
            }
        });
        pickerHelper.setOnActActivityResultListener(new OnActActivityResultListener() {
            @Override
            public void onActivityResult(int requestCode, int resultCode, Intent data) {
                showWaitDialog();
            }
        });
        pickerHelper.setCropStyle(1, 1, 1800, 1800);
        pickerHelper.setOpenSysCrop(true); //打开系统裁剪功能
        pickerHelper.setReplaceContentLayout(true);
        pickerHelper.openCamera();//打开相机del
    }

    /**
     * bitmap转为base64
     *
     * @param bitmap
     * @return
     */
    public String bitmapToBase64(Bitmap bitmap) {
        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);  //100%的品质

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

//    public String imageToBase64(String path) {
//        Bitmap bitmap = null;
//        if (path != null && path.length() > 0) {
//            bitmap = BitmapFactory.decodeFile(path);
//        }
//        if(bitmap == null){
//            return null;
//        }
//        ByteArrayOutputStream out = null;
//        try {
//            out = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
//            out.flush();
//            out.close();
//            byte[] imgBytes = out.toByteArray();
//            return Base64.encodeToString(imgBytes, Base64.DEFAULT);
//        } catch (Exception e) {
//            return null;
//        } finally {
//            try {
//                if (out != null) {
//                    out.flush();
//                    out.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == mPicPaths.size() - 1) {
            open();
        } else {
            Intent intent = new Intent(this, ShowBigImageActivity.class);
            intent.putExtra(ShowBigImageActivity.KEY_PIC_LOCAL_PATH, mPicPaths.get(position));
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {

    }


    /**
     * EditText竖直方向是否可以滚动
     * @param editText  需要判断的EditText
     * @return  true：可以滚动   false：不可以滚动
     */
    private boolean canVerticalScroll(EditText editText) {
        //滚动的距离
        int scrollY = editText.getScrollY();
        //控件内容的总高度
        int scrollRange = editText.getLayout().getHeight();
        //控件实际显示的高度
        int scrollExtent = editText.getHeight() - editText.getCompoundPaddingTop() -editText.getCompoundPaddingBottom();
        //控件内容总高度与实际显示高度的差值
        int scrollDifference = scrollRange - scrollExtent;
        if(scrollDifference == 0) {
            return false;
        }
        return (scrollY > 0) || (scrollY < scrollDifference - 1);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        //触摸的是EditText并且当前EditText可以滚动则将事件交给EditText处理；否则将事件交由其父类处理
        if ((view.getId() == R.id.text_et && canVerticalScroll(text_et)) || (view.getId() == R.id.title_et && canVerticalScroll(title_et))) {
            view.getParent().requestDisallowInterceptTouchEvent(true);
            if (event.getAction() == MotionEvent.ACTION_UP) {
                view.getParent().requestDisallowInterceptTouchEvent(false);
            }
        }
        return false;
    }
}
