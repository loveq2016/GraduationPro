package org.ql.utils.image;

import java.io.File;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Hashtable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;

public class BitmapCache {
	private static BitmapCache cache;
	/** 用于Chche内容的存储 */
	private Hashtable<Object, MySoftRef> hashRefs;
	/** 垃圾Reference的队列（所引用的对象已经被回收，则将该引用存入队列中） */
	private ReferenceQueue<Bitmap> q;

	/**
	 * 继承SoftReference，使得每一个实例都具有可识别的标识。
	 */
	private class MySoftRef extends SoftReference<Bitmap> {
		private Object _key = 0;

		public MySoftRef(Bitmap bmp, ReferenceQueue<Bitmap> q, Object key) {
			super(bmp, q);
			_key = key;
		}
	}

	private BitmapCache() {
		hashRefs = new Hashtable<Object, MySoftRef>();
		q = new ReferenceQueue<Bitmap>();
	}

	private boolean isSdCardExist(Context context) {
		boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		return sdCardExist;
	}

	/**
	 * 取得缓存器实例
	 */
	public static BitmapCache getInstance() {
		if (cache == null) {
			cache = new BitmapCache();
		}
		return cache;
	}

	/**
	 * 以软引用的方式对一个Bitmap对象的实例进行引用并保存该引用
	 */
	public void addCacheBitmap(Object key, Bitmap bmp) {
		cleanCache();
		MySoftRef ref = new MySoftRef(bmp, q, key);
		hashRefs.put(key, ref);
	}

	public Bitmap getCacheBitmap(Object key) {
		Bitmap bmp = null;
		if (hashRefs.containsKey(key)) {
			MySoftRef ref = (MySoftRef) hashRefs.get(key);
			bmp = (Bitmap) ref.get();
		}
		return bmp;
	}

	/**
	 * 依据所指定的drawable下的图片资源ID号（可以根据自己的需要从网络或本地path下获取），重新获取相应Bitmap对象的实例
	 */
	public Bitmap getBitmap(Context context, int resId) {
		Bitmap bmp = null;
		// 缓存中是否有该Bitmap实例的软引用，如果有，从软引用中取得。
		if (hashRefs.containsKey(resId)) {
			MySoftRef ref = (MySoftRef) hashRefs.get(resId);
			bmp = (Bitmap) ref.get();
		}
		// 如果没有软引用，或者从软引用中得到的实例是null，重新构建一个实例，
		// 并保存对这个新建实例的软引用
		if (bmp == null) {
			// 传说decodeStream直接调用JNI>>nativeDecodeAsset()来完成decode，
			// 无需再使用java层的createBitmap，从而节省了java层的空间。
			bmp = BitmapFactory.decodeStream(context.getResources().openRawResource(resId));
			this.addCacheBitmap(resId, bmp);
		}
		return bmp;
	}

	/**
	 * 依据所指定的drawable下的图片资源ID号（可以根据自己的需要从网络或本地path下获取），重新获取相应Bitmap对象的实例
	 */
	public Bitmap getBitmap(Context context, int resId, int sampleSize) {
		Bitmap bmp = null;
		// 缓存中是否有该Bitmap实例的软引用，如果有，从软引用中取得。
		if (hashRefs.containsKey(resId)) {
			MySoftRef ref = (MySoftRef) hashRefs.get(resId);
			bmp = (Bitmap) ref.get();
		}
		// 如果没有软引用，或者从软引用中得到的实例是null，重新构建一个实例，
		// 并保存对这个新建实例的软引用
		if (bmp == null) {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inSampleSize = sampleSize;
			bmp = BitmapFactory.decodeStream(context.getResources().openRawResource(resId), null, opts);
			this.addCacheBitmap(resId, bmp);
		}
		return bmp;
	}

	/** 加载SD卡图片 */
	public Bitmap getSdCardImage(Context context, String path, int sampleSize) {
		if (TextUtils.isEmpty(path) || !isSdCardExist(context))
			return null;
		Bitmap bitmap = null;
		File f = new File(path);
		if (f != null && f.exists()) {
			if(sampleSize > 0){
				BitmapFactory.Options opts = new BitmapFactory.Options();
				opts.inSampleSize = sampleSize;
				bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), opts);
			}else{
				bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
			}
		}
		return bitmap;
	}

	/** 清除垃圾引用 */
	public void cleanCache() {
		MySoftRef ref = null;
		while ((ref = (MySoftRef) q.poll()) != null) {
			hashRefs.remove(ref._key);
		}
	}

	/**
	 * 清除Cache内的全部内容
	 */
	public void clearCache() {
		cleanCache();
		hashRefs.clear();
		System.gc();
		System.runFinalization();
	}
}