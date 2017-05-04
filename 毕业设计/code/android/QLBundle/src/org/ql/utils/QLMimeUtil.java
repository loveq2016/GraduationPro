package org.ql.utils;

import java.util.Hashtable;

public class QLMimeUtil {
	private static Hashtable<String, String> mimeTable = new Hashtable<String, String>();

	static {
		mimeTable.put("jpg", "image/jpeg");
		mimeTable.put("jpeg", "image/jpeg");
		mimeTable.put("png", "image/png");
		mimeTable.put("bmp", "image/bmp");

		mimeTable.put("mp4", "video/mpeg4");
		mimeTable.put("flv", "video/mpeg4");
		mimeTable.put("3gp", "video/3gpp");
		mimeTable.put("wmv", "video/mpeg");
		mimeTable.put("mpeg", "video/mpeg");
		mimeTable.put("mpg", "video/mpeg");
		mimeTable.put("mp3", "audio/mp3");
		mimeTable.put("wma", "audio/mp3");
		
		mimeTable.put("apk", "application/vnd.android.package-archive");
	}

	public static String getMimeType(String ext) {
		return mimeTable.get(ext.toLowerCase());
	}

	public static String getFileExtension(String url) {
		if ((url != null) && (url.indexOf('.') != -1)) {
			return url.substring(url.lastIndexOf('.') + 1);
		}
		return "";
	}

	public static boolean isImageType(String mime) {
		return (mime != null) && (mime.toLowerCase().startsWith("image/"));
	}

	public static boolean isVideoType(String mime) {
		return (mime != null) && (mime.toLowerCase().startsWith("video/"));
	}

	public static boolean isAudioType(String mime) {
		return (mime != null) && (mime.toLowerCase().startsWith("audio/"));
	}
}
