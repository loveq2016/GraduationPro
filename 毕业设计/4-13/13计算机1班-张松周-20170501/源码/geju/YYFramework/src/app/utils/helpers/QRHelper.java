package app.utils.helpers;

import org.ql.activity.customtitle.ActivityInterface;
import org.ql.activity.customtitle.OnActActivityResultListener;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.zxing.activity.ScanActivity;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;

import app.config.DemoApplication;
import app.utils.common.Listener;

/**
 * 
 * SiuJiYung create at 2016年6月15日 上午10:41:21
 * 
 */

public class QRHelper implements OnActActivityResultListener {

	private static final int kRequestCode = 123;
	private Activity mActivity ;
	private Listener< Void , String > scanListener;

	public void setOnScanResultListener(Listener<Void, String> l) {
		scanListener = l;
	}

	/**
	 * 扫描二维码
	 * 
	 * @param activity
	 */
	public void scanQRCode(Activity activity) {
		((ActivityInterface) activity).setActivityResultListener(this);
		Intent intent = new Intent();
		intent.setClass(activity, ScanActivity.class);
		activity.startActivityForResult(intent, kRequestCode);
	}

	/**
	 * 创建二维码
	 * 
	 * @param txt
	 * @param width
	 * @param height
	 * @return
	 */
	public Bitmap createQRImage(String txt, int width, int height) {
		try {
			MultiFormatWriter mfw = new MultiFormatWriter();
			BitMatrix matrix = mfw.encode(txt, BarcodeFormat.QR_CODE, width, height);
			// MatrixToImageWriter
			// 二维矩阵转为一维像素数组,也就是一直横着排了
			int[] pixels = new int[width * height];
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					if (matrix.get(x, y)) {
						pixels[y * width + x] = 0xff000000;
					}
				}
			}
			Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			// 通过像素数组生成bitmap,具体参考api
			bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
			matrix.clear();
			mfw = null;
			pixels = null;
			matrix = null;
			return bitmap;
		} catch (WriterException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Bitmap createHeaderQRImage(String txt, Bitmap bitmap) {
		// 图片宽度的一半
		int IMAGE_HALFWIDTH = 20;

		// 缩放图片
		Matrix matrix = new Matrix();
		float sx = (float) 2 * IMAGE_HALFWIDTH / bitmap.getWidth();
		float sy = (float) 2 * IMAGE_HALFWIDTH / bitmap.getHeight();
		matrix.setScale(sx, sy);
		// 重新构造一个40*40的图片
		bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
		return null;
	}

	public Bitmap createBitmapToHeader(String str, Bitmap bitmap, int imgWidth, int imgHeight) {
		// 图片宽度的一半
		//int IMAGE_HALFWIDTH = 30;
		int IMAGE_HALFWIDTH = DemoApplication.QRInsideImg;
		// 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
		try {
			BitMatrix matrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, imgWidth, imgHeight);
			// int width = matrix.getWidth();
			// int height = matrix.getHeight();
			int width = imgWidth;
			int height = imgHeight;
			// 二维矩阵转为一维像素数组,也就是一直横着排了
			int halfW = width / 2;
			int halfH = height / 2;
			int[] pixels = new int[width * height];
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					if (x > halfW - IMAGE_HALFWIDTH && x < halfW + IMAGE_HALFWIDTH && y > halfH - IMAGE_HALFWIDTH && y < halfH + IMAGE_HALFWIDTH) {
						pixels[y * width + x] = bitmap.getPixel(x - halfW + IMAGE_HALFWIDTH, y - halfH + IMAGE_HALFWIDTH);
					} else {
						if (matrix.get(x, y)) {
							pixels[y * width + x] = 0xff000000;
						}
					}
				}
			}
			Bitmap bitmap2 = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			// 通过像素数组生成bitmap
			bitmap2.setPixels(pixels , 0 , width, 0, 0, width, height);
			return bitmap2;
		} catch (WriterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void onActivityResult( int requestCode , int resultCode , Intent data ) {
		if ( kRequestCode == requestCode && resultCode == Activity.RESULT_OK) {
			String qrCode = data.getExtras().getString("result");
			if (scanListener != null) {
				scanListener.onCallBack(null, qrCode);
			}
		}
	}
}
