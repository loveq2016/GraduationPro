package org.ql.utils.image;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.ql.utils.debug.QLLog;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.VideoThumbnail;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.ImageView;

/**
 * @author xjm
 */
public class QLAsyncImage {
	private HashMap<String, SoftReference<Bitmap>> map;
	private ExecutorService executorService;
	private Activity activity;
	private QLImageUtil imageUtil;
	private final String TAG = QLAsyncImage.class.getSimpleName();
	private int sampleSize = 3;

	/** 图片宽度 */
	private int bitmapW = QLImageUtil.WRAP_CONTENT;

	/** 图片高度 */
	private int bitmapH = QLImageUtil.WRAP_CONTENT;

	/** 图片圆角 */
	private float roundPx = 0;

	/** SD卡缓存路径 */
	private String imgBufferParent = "";

	/** 图片后缀 */
	private String imgSuffix = ".ql";

	/** 加载视频截图 */
	public static final int LOAD_FORM_VIDEO_FILEPATH = 1;

	/** 加载网络图片 */
	public static final int LOAD_FORM_NETWORK_URL = 2;

	/** 加载SD卡图片 */
	public static final int LOAD_FORM_SDCARD_FILEPATH = 3;

	/** 加载类型 */
	private int loadType = LOAD_FORM_NETWORK_URL;

	/** 是否创建缓存图片 */
	private boolean createImgBuffer = true;

	/** 是否加载SD卡缓存图片 */
	private boolean loadImgBuffer = true;
	
	private boolean zoom = false;
	
	private int defaultHolderImageResId = -1;

	/**
	 * 图片圆角
	 * @param roundPx
	 */
	public void setRoundPx(float roundPx) {
		Environment.getExternalStorageState();
		this.roundPx = roundPx;
	}

	public void setConsultW(int consultW) {
		imageUtil.setConsultW(consultW);
	}

	public void setConsultH(int consultH) {
		imageUtil.setConsultH(consultH);
	}
	
	public void setHolderImageResID(int id){
		defaultHolderImageResId = id;
	}

	/**
	 * 设置加载类型
	 * 
	 * @param loadType
	 */
	public void setLoadType(int loadType) {
		this.loadType = loadType;
	}

	/**
	 * SD卡缓存路径
	 * @param imgBufferParent
	 */
	public void setImgBufferParent(String imgBufferParent) {
		this.imgBufferParent = imgBufferParent;
	}
	
	public String getImgBufferParent() {
		if(TextUtils.isEmpty(imgBufferParent)){
			String sdcard = Environment.getExternalStorageDirectory().toString();
			imgBufferParent = new File(sdcard,"android/data/"+activity.getPackageName()+"/cache/image").getAbsolutePath();
		}
		return imgBufferParent;
	}

	/** 是否创建缓存图片 */
	public void setCreateImgBuffer(boolean createImgBuffer) {
		this.createImgBuffer = createImgBuffer;
	}

	/** 是否加载SD卡缓存图片 */
	public void setLoadImgBuffer(boolean loadImgBuffer) {
		this.loadImgBuffer = loadImgBuffer;
	}

	/**
	 * 设置图片后缀
	 * 
	 * @param imgSuffix
	 */
	public void setImgSuffix(String imgSuffix) {
		this.imgSuffix = imgSuffix;
	}

	public void setSampleSize(int sampleSize) {
		this.sampleSize = sampleSize;
	}

	public QLAsyncImage(Activity activity) {
		this(activity, LOAD_FORM_NETWORK_URL);
	}
	
	/**
	 * 特殊需求，压缩图片避免oom。所有url最后必须手动加上"__zoom"标志
	 * @param activity
	 * @param zoom
	 * @param w
	 * @param h
	 */
	public QLAsyncImage(Activity activity,boolean zoom,int w,int h) {
		this(activity, LOAD_FORM_NETWORK_URL);
		this.zoom = zoom;
		this.bitmapH = h;
		this.bitmapW = w;
	}

	public QLAsyncImage(Activity activity, int bitmapW, int bitmapH, float roundPx) {
		this(activity, LOAD_FORM_NETWORK_URL, bitmapW, bitmapH, roundPx);
	}

	public QLAsyncImage(Activity activity, int loadType) {
		this(activity, loadType, 5);
	}

	public QLAsyncImage(Activity activity, int loadType, int bitmapW,
			int bitmapH, float roundPx) {
		this(activity, loadType, 5, bitmapW, bitmapH, roundPx);
	}

	public QLAsyncImage(Activity activity, int loadType, int threads) {
		this(activity, loadType, threads, QLImageUtil.WRAP_CONTENT,QLImageUtil.WRAP_CONTENT, 0);
	}

	public QLAsyncImage(Activity activity, int loadType, int threads,int bitmapW, int bitmapH, float roundPx) {
		if(activity == null) {
			throw new NullPointerException();
		}
		this.activity = activity;
		this.loadType = loadType;
		this.bitmapW = bitmapW;
		this.bitmapH = bitmapH;
		this.roundPx = roundPx;
		imageUtil = new QLImageUtil();
		map = new HashMap<String, SoftReference<Bitmap>>();
		executorService = Executors.newFixedThreadPool(threads);
	}

	private final Handler handler = new Handler();
	private final CopyOnWriteArrayList<Object> locker = new CopyOnWriteArrayList<Object>();
	
	private void setResult(ImageCallback imageCallback, Bitmap result,String requestUrl) {
		if (null != imageCallback && activity != null && !activity.isFinishing()) {
			imageCallback.imageLoaded(result, requestUrl);
		}
	}

	public void release(){
		if(map!=null&&map.size()>0){
			Iterator<Entry<String, SoftReference<Bitmap>>> its = map.entrySet().iterator();
			while (its.hasNext()) { 
				Entry<String, SoftReference<Bitmap>> entry = its.next(); 
			    SoftReference<Bitmap> val = entry.getValue(); 
			    Bitmap bitmap = val.get();
			    if(null!=bitmap&&!bitmap.isRecycled()){
			    	QLLog.e("释放-->"+bitmap.hashCode());
			    	bitmap.recycle();
			    	bitmap = null;
			    }
			} 
			System.gc();
		}
	}
	
	public void loadImage(final String url,final ImageView imageView,final int holderImgRes){
		if(TextUtils.isEmpty(url) || imageView == null) {
			return ;
		}
		
		//=====上锁=====//
		Object lock = url;
		if(!locker.contains(lock)){
			locker.add(lock);
		}else{
			for(Iterator<Object> it = locker.iterator();it.hasNext();){
				Object key = it.next();
				if(key.equals(lock)){
					lock = key;
				}
			}
		}
		synchronized (lock) {
			if (loadType == LOAD_FORM_NETWORK_URL) {
				sampleSize = ((url.indexOf("iphoneandroid") != -1) || url.indexOf("iphone") != -1) ? sampleSize : 1;
			}
			QLLog.d(TAG, "imageUrl = " + url);
			QLLog.d(TAG, "inSampleSize = " + sampleSize);
			Bitmap bitmap = null;
			//程序缓存
			if (map.containsKey(url)) {
				SoftReference<Bitmap> softReference = map.get(url);
				bitmap = softReference.get();
				if (bitmap != null) {
					QLLog.d(TAG, "使用程序缓存图片");
					final Bitmap bm = bitmap;
					imageView.post(new Runnable() {
						@Override
						public void run() {
							if (bm == null && holderImgRes !=-1 ) {
								imageView.setImageResource(holderImgRes);
							}else{
								imageView.setImageBitmap(bm);
							}
						}
					});
					return ;
				}
			}
			// 加载SD卡缓存图片
			if (loadImgBuffer && (bitmap = getSdCardBufferImage(url)) != null) {
				QLLog.d(TAG, "用SD卡缓存图片");
				//设置圆角
				if (bitmap != null && !((bitmapW == QLImageUtil.WRAP_CONTENT || bitmapW == QLImageUtil.ORIGINAL_CONTENT) && (bitmapH == QLImageUtil.WRAP_CONTENT || bitmapH == QLImageUtil.ORIGINAL_CONTENT) && roundPx == 0)){
					bitmap = imageUtil.zoomBitmap(activity, bitmap, bitmapW,bitmapH, roundPx);
				}
				map.put(url, new SoftReference<Bitmap>(bitmap));
				final Bitmap bm = bitmap;
				imageView.post(new Runnable() {
					@Override
					public void run() {
						if (bm == null && holderImgRes !=-1 ) {
							imageView.setImageResource(holderImgRes);
						}else{
							imageView.setImageBitmap(bm);
						}
					}
				});
				return;
			}
			//缓存中没有图像，则从网络上取出数据，并将取出的数据缓存到内存中
			executorService.submit(new Runnable(){
				@Override
				public void run(){
					Bitmap bitmap = 
						(loadType==LOAD_FORM_NETWORK_URL) ? LoadImageFromInternet(url):
						(loadType==LOAD_FORM_VIDEO_FILEPATH) ? loadImageVideoFile(url):
						(loadType==LOAD_FORM_SDCARD_FILEPATH) ? getSdCardImage(url):
						null;
					//缓存到SD卡
					if(createImgBuffer && bitmap != null){
						if(zoom){
							String tmp = url.substring(0, url.indexOf("__zoom"));
							doBufferImage(tmp,bitmap);
						}
						doBufferImage(url,bitmap);
					}
					//缓存到文件夹后再设置圆角
					if (bitmap != null && !((bitmapW == QLImageUtil.WRAP_CONTENT || bitmapW == QLImageUtil.ORIGINAL_CONTENT) && (bitmapH == QLImageUtil.WRAP_CONTENT || bitmapH == QLImageUtil.ORIGINAL_CONTENT) && roundPx == 0)){
						bitmap = imageUtil.zoomBitmap(activity, bitmap, bitmapW,bitmapH, roundPx);
					}
					
					final Bitmap result = bitmap;
					map.put(url, new SoftReference<Bitmap>(result));
					imageView.post(new Runnable() {
						@Override
						public void run() {
							if (result == null && holderImgRes != -1) {
								imageView.setImageResource(holderImgRes);
							}else{
								imageView.setImageBitmap(result);
							}
						}
					});
				}
			},url);
		}
	}
	
	public void loadImage(final String url,final ImageView imageView){
		loadImage(url, imageView,defaultHolderImageResId);
	}

	public Bitmap loadImage(final String url, final ImageCallback callback) {
		if(TextUtils.isEmpty(url)) {
			return null;
		}
		
		//=====上锁=====//
		Object lock = url;
		if(!locker.contains(lock)){
			locker.add(lock);
		}else{
			for(Iterator<Object> it = locker.iterator();it.hasNext();){
				Object key = it.next();
				if(key.equals(lock)){
					lock = key;
				}
			}
		}
		synchronized (lock) {
			if (loadType == LOAD_FORM_NETWORK_URL) {
				sampleSize = ((url.indexOf("iphoneandroid") != -1) || url.indexOf("iphone") != -1) ? sampleSize : 1;
			}
			QLLog.d(TAG, "imageUrl = " + url);
			QLLog.d(TAG, "inSampleSize = " + sampleSize);
			Bitmap bitmap = null;
			//程序缓存
			if (map.containsKey(url)) {
				SoftReference<Bitmap> softReference = map.get(url);
				bitmap = softReference.get();
				if (bitmap != null) {
					QLLog.d(TAG, "使用程序缓存图片");
					setResult(callback, bitmap, url);
					return bitmap;
				}
			}
			// 加载SD卡缓存图片
			if (loadImgBuffer && (bitmap = getSdCardBufferImage(url)) != null) {
				QLLog.d(TAG, "用SD卡缓存图片");
				//设置圆角
				if (bitmap != null && !((bitmapW == QLImageUtil.WRAP_CONTENT || bitmapW == QLImageUtil.ORIGINAL_CONTENT) && (bitmapH == QLImageUtil.WRAP_CONTENT || bitmapH == QLImageUtil.ORIGINAL_CONTENT) && roundPx == 0)){
					bitmap = imageUtil.zoomBitmap(activity, bitmap, bitmapW,bitmapH, roundPx);
				}
				map.put(url, new SoftReference<Bitmap>(bitmap));
				setResult(callback, bitmap, url);
				return bitmap;
			}
			//缓存中没有图像，则从网络上取出数据，并将取出的数据缓存到内存中
			executorService.submit(new Runnable(){
				@Override
				public void run(){
					Bitmap bitmap = 
						(loadType==LOAD_FORM_NETWORK_URL) ? LoadImageFromInternet(url):
						(loadType==LOAD_FORM_VIDEO_FILEPATH) ? loadImageVideoFile(url):
						(loadType==LOAD_FORM_SDCARD_FILEPATH) ? getSdCardImage(url):
						null;
					//缓存到SD卡
					if(createImgBuffer && bitmap != null){
						if(zoom){
							String tmp = url.substring(0, url.indexOf("__zoom"));
							doBufferImage(tmp,bitmap);
						}
						doBufferImage(url,bitmap);
					}
					//缓存到文件夹后再设置圆角
					if (bitmap != null && !((bitmapW == QLImageUtil.WRAP_CONTENT || bitmapW == QLImageUtil.ORIGINAL_CONTENT) && (bitmapH == QLImageUtil.WRAP_CONTENT || bitmapH == QLImageUtil.ORIGINAL_CONTENT) && roundPx == 0)){
						bitmap = imageUtil.zoomBitmap(activity, bitmap, bitmapW,bitmapH, roundPx);
					}
					
					final Bitmap result = bitmap;
					map.put(url, new SoftReference<Bitmap>(result));
					handler.post(new Runnable() {
						@Override
						public void run() {
							setResult(callback, result, url);
						}
					});
				}
			},url);
		}
		return null;
	}
	
	/**
	 * @param url
	 * @return
	 */
	public Bitmap loadImage(final String url) {
		if(TextUtils.isEmpty(url)) {
			return null;
		}
		
		//=====上锁=====//
		Object lock = url;
		if(!locker.contains(lock)){
			locker.add(lock);
		}else{
			for(Iterator<Object> it = locker.iterator();it.hasNext();){
				Object key = it.next();
				if(key.equals(lock)){
					lock = key;
				}
			}
		}
		synchronized (lock) {
			if (loadType == LOAD_FORM_NETWORK_URL) {
				sampleSize = ((url.indexOf("iphoneandroid") != -1) || url.indexOf("iphone") != -1) ? sampleSize : 1;
			}
			QLLog.d(TAG, "imageUrl = " + url);
			QLLog.d(TAG, "inSampleSize = " + sampleSize);
			Bitmap bitmap = null;
			//程序缓存
			if (map.containsKey(url)) {
				SoftReference<Bitmap> softReference = map.get(url);
				bitmap = softReference.get();
				if (bitmap != null) {
					QLLog.d(TAG, "使用程序缓存图片");
					return bitmap;
				}
			}
			// 加载SD卡缓存图片
			if (loadImgBuffer && (bitmap = getSdCardBufferImage(url)) != null) {
				QLLog.d(TAG, "用SD卡缓存图片");
				//设置圆角
				if (bitmap != null && !((bitmapW == QLImageUtil.WRAP_CONTENT || bitmapW == QLImageUtil.ORIGINAL_CONTENT) && (bitmapH == QLImageUtil.WRAP_CONTENT || bitmapH == QLImageUtil.ORIGINAL_CONTENT) && roundPx == 0)){
					bitmap = imageUtil.zoomBitmap(activity, bitmap, bitmapW,bitmapH, roundPx);
				}
				map.put(url, new SoftReference<Bitmap>(bitmap));
				return bitmap;
			}
			//缓存中没有图像，则从网络上取出数据，并将取出的数据缓存到内存中
			bitmap = 
					(loadType==LOAD_FORM_NETWORK_URL) ? LoadImageFromInternet(url) :
					(loadType==LOAD_FORM_VIDEO_FILEPATH) ? loadImageVideoFile(url) :
					(loadType==LOAD_FORM_SDCARD_FILEPATH) ? getSdCardImage(url) :
					null;
					//缓存到SD卡
					if(createImgBuffer && bitmap != null){
						if(zoom){
							String tmp = url.substring(0, url.indexOf("__zoom"));
							doBufferImage(tmp,bitmap);
						}
						doBufferImage(url,bitmap);
					}
					//缓存到文件夹后再设置圆角
					if (bitmap != null && !((bitmapW == QLImageUtil.WRAP_CONTENT || bitmapW == QLImageUtil.ORIGINAL_CONTENT) && (bitmapH == QLImageUtil.WRAP_CONTENT || bitmapH == QLImageUtil.ORIGINAL_CONTENT) && roundPx == 0)){
						bitmap = imageUtil.zoomBitmap(activity, bitmap, bitmapW,bitmapH, roundPx);
					}
					map.put(url, new SoftReference<Bitmap>(bitmap));
					return bitmap;
		}
	}

	/**
	 * 根据视频所在SD卡路径获取图片
	 * 
	 * @param url
	 * @return
	 */
	private Bitmap loadImageVideoFile(String url) {
		QLLog.d(TAG, "根据视频所在SD卡路径获取图片");
		if (!isSdCardExist(activity)) {
			return null;
		}
		Bitmap bitmap = VideoThumbnail.createVideoThumbnail(url);
		return bitmap;
	}

	public synchronized Bitmap LoadImageFromInternet(String url) {
		if(zoom&&url.indexOf("__zoom")!=-1){
			url = url.substring(0, url.indexOf("__zoom"));
		}
		Bitmap bitmap = null;
		try {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inSampleSize = sampleSize;
			 
			byte[] data = getImageByteArray(url);
			if(data!=null){				
				bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
				QLLog.i("图片下载","下载成功:" + url);
				return bitmap;
			}else{
				QLLog.e("图片下载失败！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			QLLog.e("图片下载失败！");
			return null;
		}
		return bitmap;
	}
	
	public static byte[] getImageByteArray(String path){
		try {
			URL url  = new URL(path);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setConnectTimeout(8 * 1000);
			conn.setDoInput(true);
			conn.setUseCaches(true);
			conn.setRequestMethod("GET");
			conn.connect();
			InputStream inStream = conn.getInputStream();
			return readFromInput(inStream);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static byte[] readFromInput(InputStream inStream){
		if (inStream == null) {
			return null;
		}
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		try {
			while ((len = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		byte[] data = outStream.toByteArray();
		try {
			inStream.close();
			outStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}

	/** 加载SD卡图片 */
	private Bitmap getSdCardImage(String path) {
		if (!isSdCardExist(activity)) {
			return null;
		}
		Bitmap bitmap = null;
		File f = new File(path);
		if (f != null && f.exists()) {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inSampleSize = sampleSize;
			bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), opts);
		}
		return bitmap;
	}

	/**
	 * 加载SD卡缓存图片
	 * 
	 * @param url
	 * @return
	 */
	private Bitmap getSdCardBufferImage(String url) {
		if (!isSdCardExist(activity)) {
			return null;
		}
		Bitmap bitmap = null;
		try {
			String fileName = getImageFileName(url);
			File f = new File(getImgBufferParent(),fileName);
			if (f != null && f.exists()) {
				bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
			}
			if (bitmap != null
					&& !((bitmapW == QLImageUtil.WRAP_CONTENT || bitmapW == QLImageUtil.ORIGINAL_CONTENT)
							&& (bitmapH == QLImageUtil.WRAP_CONTENT || bitmapH == QLImageUtil.ORIGINAL_CONTENT) && roundPx == 0)) {
				bitmap = imageUtil.zoomBitmap(activity, bitmap, bitmapW,
						bitmapH, roundPx);
			}
		} catch (IOException e) {
		} catch (NoSuchAlgorithmException e) {
		}catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	/**
	 * 保存缓存图片
	 * 
	 * @param url
	 * @param bitmap
	 */
	private void doBufferImage(String url, Bitmap bitmap) {
		try {
			String fileName;
			if (!isSdCardExist(activity)
					|| (fileName = getImageFileName(url)) == null) {
				return;
			}
			File f = new File(new File(getImgBufferParent()), fileName);
			if (!f.getParentFile().exists()) {
				f.getParentFile().mkdirs();
			}
			if (f.exists()) {
				f.delete();
			}
			// return;
			f.createNewFile();
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f));
			if (url.indexOf(".png") > 0) {
				bitmap.compress(Bitmap.CompressFormat.PNG, 40, bos);
			} else {
				bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bos);
			}
			bos.flush();
			bos.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} catch (NoSuchAlgorithmException e) {
		}
	}

	/**
	 * 获取缓存图片文件名
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */
	private String getImageFileName(String url) throws IOException,
			NoSuchAlgorithmException {
		String parent = md5(url.getBytes());
		return parent != null ? parent + imgSuffix : null;
	}

	/**
	 * MD5加密图片路径
	 * 
	 * @param source
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	private String md5(byte[] source) throws NoSuchAlgorithmException {
		char hexDigits[] = { // 用来将字节转换成 16 进制表示的字符
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
				'e', 'f' };
		java.security.MessageDigest md = java.security.MessageDigest
				.getInstance("MD5");
		md.update(source);
		byte tmp[] = md.digest(); // MD5 的计算结果是一个 128 位的长整数，
		// 用字节表示就是 16 个字节
		char str[] = new char[16 * 2]; // 每个字节用 16 进制表示的话，使用两个字符，
		// 所以表示成 16 进制需要 32 个字符
		int k = 0; // 表示转换结果中对应的字符位置
		for (int i = 0; i < 16; i++) { // 从第一个字节开始，对 MD5 的每一个字节
			// 转换成 16 进制字符的转换
			byte byte0 = tmp[i]; // 取第 i 个字节
			str[k++] = hexDigits[byte0 >>> 4 & 0xf]; // 取字节中高 4 位的数字转换,
			// >>> 为逻辑右移，将符号位一起右移
			str[k++] = hexDigits[byte0 & 0xf]; // 取字节中低 4 位的数字转换
		}
		String s = new String(str);
		return s != null ? s : null; // 换后的结果转换为字符串
	}

	public interface ImageCallback {
		public void imageLoaded(Bitmap bitmap, String imageUrl);
	}

	/**
	 * 判断SD卡是否存在
	 * 
	 * @param context
	 * @return
	 */
	private boolean isSdCardExist(Context context) {
		boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		return sdCardExist;
	}
}
