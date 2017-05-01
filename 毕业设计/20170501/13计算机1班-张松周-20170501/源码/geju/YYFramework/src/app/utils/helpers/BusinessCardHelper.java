package app.utils.helpers;

import java.io.File;

import org.ql.activity.customtitle.ActivityInterface;
import org.ql.activity.customtitle.Activitys;
import org.ql.activity.customtitle.OnActActivityResultListener;
import org.ql.utils.QLToastUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import app.config.YYAppConfig;
import app.utils.common.Listener;
import app.utils.image.QLImageHelper;

import com.hanvon.HWCloudManager;

/**
 * 
 * SiuJiYung create at 2016-6-3 下午4:03:16
 * 
 */

public class BusinessCardHelper implements OnActActivityResultListener {

	public interface OnScanPictureListener {
		public void onAnalyzing();

		public void onScanResult(String result, String imgPath);
	}

	private OnScanPictureListener listener;
	private Context context;
	private ImagePickerHelper helper;
	private Context mContext;

	public BusinessCardHelper() {

	}

	public BusinessCardHelper(Context context) {
		this.mContext = context;
	}

	public void setOnScanPictureListener(OnScanPictureListener l) {
		listener = l;
	}

	/**
	 * 启动拍照
	 * 
	 * @param activity
	 */
	public void startScanCard(Context activity) {
		((ActivityInterface) activity).setActivityResultListener(this);
		Log.i(getClass().getSimpleName(), "set result listener suessful.");
		context = activity;
		helper = ImagePickerHelper.createNewImagePickerHelper(activity);
		helper.setImageSourceType(ImagePickerHelper.kImageSource_UserSelect);
//		helper.setOnActActivityResultListener(this);
		helper.setOnReciveImageListener(new Listener<Void, String>() {
			@Override
			public void onCallBack(Void aVoid, String reply) {
				if (TextUtils.isEmpty(reply)){
					return;
				}
				if (helper.getImageSourceType() == ImagePickerHelper.kImageSource_Atlas) {
					String _path = reply;
					File imFile = new File(_path);
					if (imFile.exists()) {
						Log.i("***", "start analyzing....");
						if (listener != null) {
							listener.onAnalyzing();
							Log.i("***", "analyzing....");
						}
						dealPhoto(_path);
					}
					return;
				}
				File f = helper.getPhotoPath();
				String srcPathString = f.getAbsolutePath();
				File imgFile = new File(srcPathString);
				if (imgFile.exists()) {
					Log.i("***", "start analyzing....");
					if (listener != null) {
						listener.onAnalyzing();
						Log.i("***", "analyzing....");
					}
					dealPhoto(srcPathString);
				} else {
					QLToastUtils.showToast(context, "获取照片失败，找不到该照片路径。");
				}
			}
		});

		helper.setReplaceContentLayout(true);
		helper.setCropStyle(2, 1, 1800, 900);
		helper.setOpenSysCrop(true);

		helper.openCamera();
		// QLImageHelper.openCamera(context);
	}

	private void scanCard(Context context, String path) {
		HWCloudManager whCloudManager = new HWCloudManager(context, YYAppConfig.HANVAN_KEY);
		String result_json = whCloudManager.cardLanguage("auto", path);
		Log.i(getClass().getSimpleName(), "" + result_json);
		if (listener != null) {
			listener.onScanResult(result_json, path);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		System.gc();
		if (requestCode == QLImageHelper.FLAG_CHOOSE_PHONE && resultCode == Activity.RESULT_OK) {
			// File f = new
			// File(QLImageHelper.getPhotoDir(),QLImageHelper.createDefaultImageName());
			// String srcPathString = f.getAbsolutePath();

			if (helper.getImageSourceType() == ImagePickerHelper.kImageSource_Atlas) {
				String _path = data.getData().getPath();
				File imFile = new File(_path);
				if (imFile.exists()) {
					Log.i("***", "start analyzing....");
					if (listener != null) {
						listener.onAnalyzing();
						Log.i("***", "analyzing....");
					}
					dealPhoto(_path);
				}
				return;
			}
			File f = helper.getPhotoPath();
			String srcPathString = f.getAbsolutePath();
			File imgFile = new File(srcPathString);
			if (imgFile.exists()) {
				Log.i("***", "start analyzing....");
				if (listener != null) {
					listener.onAnalyzing();
					Log.i("***", "analyzing....");
				}
				dealPhoto(srcPathString);
			} else {
				QLToastUtils.showToast(context, "获取照片失败，找不到该照片路径。");
			}
		}
	}

	// 照片预处理
	private void dealPhoto(final String path) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				try {
					Log.i("***", "analyzing xxxx");
					QLImageHelper.compressPhotos(path, null, 800, 800, 300);
//					System.gc();
					scanCard(context, path);
					System.gc();
					Log.i("***", "analyzing finish");
				} catch (Exception e) {
					e.printStackTrace();
					if (listener != null) {
						listener.onScanResult(e.getMessage(), null);
					}
				}

				Looper.loop();
			}
		}).start();

	}

}
