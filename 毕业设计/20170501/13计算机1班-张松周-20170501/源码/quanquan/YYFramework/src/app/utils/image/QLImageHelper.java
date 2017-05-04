package app.utils.image;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.ql.activity.customtitle.ActActivity;
import org.ql.app.alert.AlertDialog;
import org.ql.utils.QLToastUtils;
import org.ql.utils.image.BitmapCache;

import u.aly.bm;

import android.Manifest;
import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract.CommonDataKinds.Contactables;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import app.logic.singleton.YYSingleton;
import app.utils.common.Listener;
import app.utils.common.MD5;
import app.utils.common.PermissionHelper;
import app.utils.common.Public;
import app.utils.debug.YYDebugHandler;
import app.utils.helpers.ImagePickerHelper;

import com.loveplusplus.PictureUtil;

/**
 * 图片帮助类
 * @author SiuJiYung create at 2013-12-9 上午10:56:29 </br>
 */
public class QLImageHelper {


	private static QLImageHelper imgHelper = null;

	public static QLImageHelper getInstance() {
		if (imgHelper == null) {
			imgHelper = new QLImageHelper();
		}
		return imgHelper;
	}

	private QLImageHelper() {
		
	}

	public void addBitmapToMemoryCache(String imgName, Bitmap img) {
		
	}

	public Bitmap getBitmapFromMemoryCache(String imgName) {
		return null;
	}

	public Bitmap loadBitmapWithResId(int resId) {
		String imgName = String.valueOf(resId);
		Bitmap bitmap = getBitmapFromMemoryCache(imgName);
		if (bitmap == null) {

		}
		return null;
	}

	public static int reckonThumbnail(int oldWidth, int oldHeight,int newWidth, int newHeight) {
		if ((oldHeight > newHeight && oldWidth > newWidth)
				|| (oldHeight <= newHeight && oldWidth > newWidth)) {
			int be = (int) (oldWidth / (float) newWidth);
			if (be <= 1)
				be = 1;
			return be;
		} else if (oldHeight > newHeight && oldWidth <= newWidth) {
			int be = (int) (oldHeight / (float) newHeight);
			if (be <= 1)
				be = 1;
			return be;
		}
		return 1;
	}
	

	public static Bitmap PicZoom(Bitmap bmp, int width, int height) {
		int bmpWidth = bmp.getWidth();
		int bmpHeght = bmp.getHeight();
		Matrix matrix = new Matrix();
		matrix.postScale((float) width / bmpWidth, (float) height / bmpHeght);

		return Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeght, matrix, true);
	}
	
	public static boolean compressPhotos(String from,String to,int maxWidth,int maxHeight,int maxSizeInKb){
		//TODO
		if (from == null) {
			return false;
		}
		File file = new File(from);
		if (!file.exists()) {
			return false;
		}
		maxSizeInKb = maxSizeInKb < 100?100:maxSizeInKb;
		
		Bitmap tmp = BitmapFactory.decodeFile(from);
		Bitmap image = PicZoom(tmp,maxWidth,maxHeight);
		tmp.recycle();
		tmp = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中  
        int options = 100;
        int tupSize = 120;
        while (baos.toByteArray().length / maxSizeInKb > tupSize && options > 0) {  //循环判断如果压缩后图片是否大于200kb,大于继续压缩         
            baos.reset();//重置baos即清空baos  
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中  
            options -= 10;//每次都减少10  
        }  
        try {
	        	byte[] buffer = baos.toByteArray();
	    		baos.close();
	    		image.recycle();
	        image = null;
	    		to = to == null?from:to;
	    		FileOutputStream fileos = new FileOutputStream(to);
	    		fileos.write(buffer);
	    		fileos.flush();
	    		fileos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public static Bitmap createWaterBitmap(HashMap<String, String> waterString,List<String> sortList,float textSize,int picMaxWidth,int textColor) {
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);
		paint.setColor(textColor);
		paint.setTextSize(textSize);// 字体大小
		paint.setTypeface(Typeface.DEFAULT_BOLD);// 采用默认的宽度

		int offset = 5;
		int maxWidth = offset, totalHeight = offset,keyMaxWidth = 0,valueMaxWidth = 0;
		int  py = offset;
		int midLinePx = offset;
		int lineHeight = 0;
		List<String> tmpSortList = null;
		boolean createSorteList = false;
		if (sortList == null) {
			tmpSortList = new ArrayList<String>();
			createSorteList = true;
		}else{
			tmpSortList = sortList;
		}

		for (String key : waterString.keySet()) {
			if (createSorteList) {
				tmpSortList.add(key);
			}
			String valueString = waterString.get(key);
			Rect keyRect = getTextRect((key + ":"), paint);
			int tmpKeyWidth = keyRect.width();
			int tmpValueWidth = getTextRect(valueString, paint).width();
			lineHeight = keyRect.height();
			valueMaxWidth = tmpValueWidth > valueMaxWidth?tmpValueWidth:valueMaxWidth;
			keyMaxWidth = tmpKeyWidth > keyMaxWidth?tmpKeyWidth:keyMaxWidth;
		}
		picMaxWidth-=20;
		midLinePx = keyMaxWidth + 10;
		maxWidth = keyMaxWidth + valueMaxWidth + 20;
		maxWidth = maxWidth > picMaxWidth ? picMaxWidth:maxWidth;
		HashMap<String, Integer> linesHashMap = new HashMap<String, Integer>();
		
//		int charCountInLine = (maxWidth - midLinePx - 10) / (getTextRect("啊", paint).width() + 2);
		int lineCounts = 0;
		for(String key:waterString.keySet()){
			String valueString = waterString.get(key);
			int _count = getTextRect(valueString,paint).width() / (maxWidth - midLinePx - 10) + 1;
			linesHashMap.put(key, _count);
			lineCounts += _count;
		}	
		totalHeight = (lineHeight + 5 ) * (lineCounts + 1);

		Bitmap bitmap = Bitmap.createBitmap(maxWidth + 10, totalHeight + 10,Config.ARGB_4444);
		Canvas canvas = new Canvas(bitmap);

		
		py = 25;
		String _key = null;
		String _drawText = null;
		String _drawTitle = null;
		
		
		for(int idx = 0; idx < tmpSortList.size();++idx){
			_key = tmpSortList.get(idx);
			_drawText = waterString.get(_key);
			_drawTitle = _key+":";
			//draw title
			int titleWidth = getTextRect(_drawTitle, paint).width();
			int contentWidth = getTextRect(_drawText, paint).width();
			int lineCount = linesHashMap.get(_key);
			int _offset = _drawText.length() % lineCount;
			_offset = _offset > 0?1:0;
			int charCountInLine = lineCount > 1?((_drawText.length() - _drawText.length()%(lineCount - _offset))/(lineCount - _offset)):_drawText.length();
			int px = (midLinePx - titleWidth);
			canvas.drawText(_drawTitle, px, py, paint);
			//draw conten
			int lineNum = 0;
			int _start = 0;
			int _end = 0;
			if (!TextUtils.isEmpty(_drawText)) {
				try {
					do{
						_start = lineNum * charCountInLine;
						_end = _start + charCountInLine;
						_end = _end > (_drawText.length() - 1)?_drawText.length():_end;
						canvas.drawText(_drawText, _start, _end, midLinePx + 5, py, paint);
						py += (lineHeight + 5);
						lineNum++;
					}while(lineNum < lineCount);
				} catch (Exception e) {
					Log.e("====canvas error===="+_drawText, "lineCount:"+lineCount+" lineNum:"+lineNum+" start:"+_start+" end:"+_end);
				}
			}else{
				py += (lineHeight + 5);
			}
		}
		
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		return bitmap;
	}
	
	
	/**
	 * 添加水印
	 * @param context
	 * @param src
	 * @param info
	 * @return
	 */
	public static Bitmap canvasWatermark(Context context,Bitmap src,HashMap<String, String> map,float textSize,int textColor) {
		int w = src.getWidth();
		int h = src.getHeight();
		Bitmap bitmap = Bitmap.createBitmap(w, h, Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		
		Paint p = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);
		p.setDither(true); //获取跟清晰的图像采样  
		p.setFilterBitmap(true);//过滤一些 
		p.setAntiAlias(true);//消除锯齿
		
		p.setTextSize(textSize);//字体大小  
		p.setTypeface(Typeface.DEFAULT_BOLD);//采用默认的宽度  
		p.setTextAlign(Align.CENTER);
		p.setColor(textColor);//采用的颜色 
		
		FontMetrics fontMetrics = p.getFontMetrics(); 
		float fontHeight = fontMetrics.bottom - fontMetrics.top; //单行文字高度
		
		//原图
		canvas.drawBitmap(src, 0, 0, p);
		src.recycle();
		src = null;
		
		float sunHeight = fontHeight * (map.size());//字体区域高度
			
		Bitmap fontBitmap = Bitmap.createBitmap(w, (int)sunHeight, Config.ARGB_8888);
		Canvas fontCanvas = new Canvas(fontBitmap);
		Rect rect = new Rect(0, 0, w,(int)fontHeight);
		int i=0;
		for (String key : map.keySet()) {
			String value = map.get(key);
			String str = key+":"+value;
			Bitmap btm = Bitmap.createBitmap(w,(int)fontHeight, Config.ARGB_8888);
			Canvas fc = new Canvas(btm);
			float baseY = rect.height() - (rect.height() - fontHeight) / 2 - fontMetrics.bottom;
			fc.drawText(str,w>>1,baseY, p);
			fc.save(Canvas.ALL_SAVE_FLAG);
			fc.restore();
				
			fontCanvas.drawBitmap(btm, 0,(i*btm.getHeight()), p);
			btm.recycle();
			btm = null;
			i++;
		}
		fontCanvas.save(Canvas.ALL_SAVE_FLAG);
		fontCanvas.restore();
		
		canvas.drawBitmap(fontBitmap, 0,0, p);
		fontBitmap.recycle();
		fontBitmap = null;
		
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		
		return bitmap;
	}
	
	/**
	 * 读照片出来
	 * @param photoPath
	 * @return
	 */
	public static Bitmap readBitmap(String photoPath,int width,int height) {
		String key = photoPath+"?width="+width+"&height"+height;
		Bitmap bitmap = BitmapCache.getInstance().getCacheBitmap(key);
		if(bitmap != null)
			return bitmap;
		
		width = width < 1?320:width;
		height = height < 1?480:height;
		// 将保存在本地的图片取出并缩小后显示在界面上
		Bitmap camorabitmap = null; 
		Bitmap smallBitMap = null;
		try {
			camorabitmap = BitmapFactory.decodeFile(photoPath);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			try {
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 2;
				camorabitmap = BitmapFactory.decodeFile(photoPath,options);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		
		if (null != camorabitmap) {
			// 下面这两句是对图片按照一定的比例缩放，这样就可以完美地显示出来。
			int scale = QLImageHelper.reckonThumbnail(camorabitmap.getWidth(), camorabitmap.getHeight(),width, height);
			smallBitMap = QLImageHelper.PicZoom(camorabitmap,camorabitmap.getWidth() / scale,camorabitmap.getHeight() / scale);
			camorabitmap = null;
			System.gc();
		}
		BitmapCache.getInstance().addCacheBitmap(key, smallBitMap);
		return smallBitMap;
	}


	/**
	 * 计算文字所在矩形，可以得到宽高
	 * @param text
	 * @param paint
	 * @return
	 */
	public static Rect getTextRect(String text, Paint paint) {
		Rect rect = new Rect();
		if (!TextUtils.isEmpty(text)) {
			paint.getTextBounds(text, 0, text.length(), rect);
		}
		return rect;
	}

	/**
	 * 添加文字水印到指定图片
	 * @param pathName
	 * @param info
	 * @return
	 * @throws Exception 
	 */
	public static String addWatermarkToPhoto(Context context,String pathName,HashMap<String, String> info,List<String> sortList, String newPath) throws Exception {
		
		//生成水印图片
		String waterBitmapKey = pathName+"watermark";
		Bitmap watermarkBitmap = createWaterBitmap(info,sortList, 16.0f,660,Color.WHITE);
		BitmapCache waterCache = BitmapCache.getInstance();
		waterCache.addCacheBitmap(waterBitmapKey, watermarkBitmap);
		Bitmap src = PictureUtil.getSmallBitmap(pathName,900,800);
		waterCache.addCacheBitmap(pathName+"src", src);
		System.gc();
		
		Bitmap newBitmap = createWatermarkBitmap(src, waterBitmapKey,true);
		
		if (src != null) {
			src.recycle();
		}
		src = null;
		if (watermarkBitmap != null) {
			watermarkBitmap.recycle();
		}
		watermarkBitmap = null;
		waterCache.cleanCache();
		if (saveBitmapToPath(newBitmap, newPath)) {//保存水印照片到指定路径
			if (newBitmap != null) {
				newBitmap.recycle();
			}
			newBitmap = null;
			return newPath;
		}
		YYDebugHandler.getShareInstance().reportError(context,"save image to <"+ newPath +"> failed.");
		return null;
	}
	
	public static String getFileMd5(String pathName){
		if (pathName == null) {
			return null;
		}
		FileInputStream fis = null;
		File file = new File(pathName);
		String md5String = null;
		if (file.exists()) {
			try {
				fis = new FileInputStream(file);
				byte[] buffer = new byte[1024];
				int count = fis.available();
				byte[] fileBytes = new byte[count];
				int lenght = -1;
				int writeLenght = 0;
				while((lenght = fis.read(buffer)) > 0){
					System.arraycopy(buffer, 0, fileBytes, writeLenght, lenght);
					writeLenght += lenght;
				}
				buffer = null;
				fis.close();
				md5String = MD5.encode(fileBytes);
				fileBytes = null;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}catch (IOException ioe) {
			}finally{
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e) {}
				}
			}
		}
		return md5String;
	}
	
	/**
	 * 创建水印
	 * @param src
	 * @param watermark
	 * @return
	 */
	private static Bitmap createWatermarkBitmap(Bitmap src, String watermarkstr,boolean recycle) {
		if (src == null) {
			return null;
		}
		Bitmap watermark = BitmapCache.getInstance().getCacheBitmap(watermarkstr);
		if (watermark == null) {
			return null;
		}
		int w = src.getWidth();
		int h = src.getHeight();
		int ww = watermark.getWidth();
		int wh = watermark.getHeight();
		int px = w - ww - 15;
		px = px < 10 ? 10 : px;
		// create the new blank bitmap
		Bitmap newb = Bitmap.createBitmap(w, h, Config.RGB_565);
		// 创建一个新的和SRC长度宽度一样的位图
		Canvas cv = new Canvas(newb);
		// draw src into
		cv.drawBitmap(src, 0, 0, null);// 在 0，0坐标开始画入src
		// draw watermark into
		cv.drawBitmap(watermark, px, h - wh, null);// 在src的右下角画入水印
		// save all clip
		cv.save(Canvas.ALL_SAVE_FLAG);// 保存
		// store
		cv.restore();// 存储
		return newb;
	}
	/**
	 * 压缩照片
	 * @param image
	 * @return 字节byte[]
	 */
	public static byte[] compressImag(Bitmap image) {  		  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中  
        int options = 100;
        int tupSize = 120;
        while (baos.toByteArray().length / 2048 > tupSize && options > 0) {  //循环判断如果压缩后图片是否大于200kb,大于继续压缩         
            baos.reset();//重置baos即清空baos  
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中  
            options -= 10;//每次都减少10  
        }  
        return baos.toByteArray();  
    } 
	
	/**
	 * 压缩图片	
	 * @param from 图片路径
	 * @param to 保存路径，如果为null，则覆盖原来的图片
	 * @param maxSizeInKb 期望图片最大大小（kb)
	 * @return
	 */
	public static boolean compressImage(String from,String to,int maxSizeInKb){
		if (from == null) {
			return false;
		}
		File file = new File(from);
		if (!file.exists()) {
			return false;
		}
		maxSizeInKb = maxSizeInKb < 100?100:maxSizeInKb;
		
		Bitmap image = BitmapFactory.decodeFile(from);
		
		image = YYSingleton.getInstance().rotateBitmapByDegree(image, YYSingleton.getInstance().getBitmapDegree(from));
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中  
        int options = 100;
        int tupSize = 120;
        while (baos.toByteArray().length / maxSizeInKb > tupSize && options > 0) {  //循环判断如果压缩后图片是否大于200kb,大于继续压缩         
            baos.reset();//重置baos即清空baos  
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中  
            options -= 10;//每次都减少10  
        }  
        try {
	        	byte[] buffer = baos.toByteArray();
	    		baos.close();
	    		to = to == null?from:to;
	    		FileOutputStream fileos = new FileOutputStream(to);
	    		fileos.write(buffer);
	    		fileos.flush();
	    		fileos.close();

			if (image!=null){
				image.recycle();
				System.gc();
				image = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	/**
	 * 压缩照片
	 * @param image
	 * @return Bitmap
	 */
	private static Bitmap compressImage(Bitmap bmap) {
		
		byte[] tmpBimByte=compressImag(bmap);
		ByteArrayInputStream isBm = new ByteArrayInputStream(tmpBimByte);//把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
		return bitmap;

	}


	/**
	 * 保存bitmap到文件
	 * @param bmp
	 * @param filePath
	 * @return
	 */
	public static boolean saveBitmapToPath(Bitmap bmp, String filePath) throws Exception{
		if (bmp == null || filePath == null) {
			throw new Exception("bitmap or file path is null.filePath="+filePath);
		}
		FileOutputStream fos = null;
		File imgFile = new File(filePath);
		boolean existsFile = false;
		if (!(existsFile = imgFile.exists())) {
			File parentFile = imgFile.getParentFile();
			if (parentFile != null) {
				parentFile.mkdirs();
			}
			existsFile = imgFile.createNewFile();
		}
		fos = new FileOutputStream(filePath);
		
		Bitmap bm=compressImage(bmp);//压缩照片
		boolean result = bm.compress(Bitmap.CompressFormat.JPEG,100, fos);//写进输出流
		if (fos != null) {
			fos.flush();
			fos.close();
			fos = null;
		}
		bm.recycle();
		return result;
	}

//	public static final File FILE_SDCARD = Environment.getExternalStorageDirectory();
//	public static final File FILE_LOCAL = new File(FILE_SDCARD, "YYData");
//	public static final File FILE_PIC_SCREENSHOT = new File(FILE_LOCAL,"images/screenshots");
//	public static final File filePath = FILE_PIC_SCREENSHOT;
	public static String localTempImageFileName;

	public static final int FLAG_CHOOSE_PHONE = 6;
	
	public static String createDefaultImageName(){
		Random r = new Random();
		int rint = r.nextInt(9999);
		localTempImageFileName = "tmp" + Public.getTimeWithFormat("yyyyMMddHH") + rint + ".jpg";
		return localTempImageFileName;
	}
	
	public static  File getPhotoDir(){
		File root_dir = Environment.getExternalStorageDirectory();
		File photo_tmp_dir = new File(root_dir,"YYData/images/photos");
		return photo_tmp_dir;
	}
	
	public static boolean openCamera(Context context){
		return openCamera(context, null,ImagePickerHelper.kImageSource_CameraOnly);
	}
	
	/**
	 * 打开摄像机
	 * @param context
	 * @param fileName
	 * @param sourceType
	 * @return
	 */
	public static boolean openCamera(Context context ,String fileName,int sourceType){
		
		if (!Public.isSdCardExist(context, true)) {
			QLToastUtils.showToast(context, "请插入内存卡");
			return false;
		}
		
		long avaliableSpace = Public.SdCardSpace(context);
		if (avaliableSpace < 10 * 1024) {
			QLToastUtils.showToast(context, "SD 卡内存少于10MB，请先清理一下存储空间。");
			return false;
		}
		
		String imageName = fileName == null?createDefaultImageName():fileName;
		File photo_tmp_dir = getPhotoDir();
		if (!photo_tmp_dir.exists()) {
			photo_tmp_dir.mkdirs();
		}
		
		try {
			Intent intentPhone = null;
			if (sourceType == ImagePickerHelper.kImageSource_Atlas) {
				if (Build.VERSION.SDK_INT < 19) {
					intentPhone = new Intent();
					intentPhone.setAction(Intent.ACTION_GET_CONTENT);
					intentPhone.setType("image/*");
					intentPhone.addCategory(Intent.CATEGORY_OPENABLE);
				} else {
					intentPhone = new Intent();
					intentPhone.setAction(Intent.ACTION_PICK);
					intentPhone.setType("image/*");
				}
			}else {
				intentPhone = new Intent();
				PermissionHelper helper = PermissionHelper.getHelper();
				boolean havePermission = helper.checkPermissions((Activity)context, 11, null ,"android.permission.CAMERA");
				if (!havePermission) {
					QLToastUtils.showToast(context, "请授权我们使用照相机");
					return false;
				}
				intentPhone.setAction(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				File f = new File(photo_tmp_dir, imageName);
				Uri u = Uri.fromFile(f);
				intentPhone.putExtra(MediaStore.EXTRA_OUTPUT, u);
			}
			((Activity)context).startActivityForResult(intentPhone, FLAG_CHOOSE_PHONE);
			return true;
		} catch (Exception e) {
			AlertDialog.Builder alert = new AlertDialog.Builder(context);
			alert.setTitle("提示");
			alert.setMessage("无法启动摄像机，请检查摄像机是否可用。");
			alert.setNegativeButton("确定", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which) {
					
				}
			});
		}
		return false;
	}
}
