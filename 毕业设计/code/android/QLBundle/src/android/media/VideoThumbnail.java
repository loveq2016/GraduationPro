package android.media;

import android.graphics.Bitmap;
import android.os.Build;
import android.provider.MediaStore.Images;

/**
 * 可根据视频文件绝对路径获取视频缩略图<p>
 * @author xjm
 */
public class VideoThumbnail {
	
	/**
	 * 获取视频文件缩略图、图片尺寸使用默认大小
	 * @param filePath 视频文件绝对路径
	 * @return 成功返回缩略图Bitmap对象，失败则返回null
	 */
	public final static Bitmap createVideoThumbnail(String filePath) {
		return createVideoThumbnail(filePath, 0, 0);
	}

	/**
	 * 获取视频文件缩略图，当width<或者height<0时，使用默认大小sdk<=7为96x96、sdk>7为640x480
	 * @param filePath 视频文件绝对路径
	 * @param width	缩略图宽度
	 * @param height 缩略图高度
	 * @return 成功返回缩略图Bitmap对象，失败则返回null
	 */
	public final static Bitmap createVideoThumbnail(String filePath, int width,int height) {
		Bitmap bitmap = null;
		if (Build.VERSION.SDK_INT <= 7) {// sdk2.1以下
			MediaMetadataRetriever retriever = new MediaMetadataRetriever();
			try {
				retriever.setDataSource(filePath);
				retriever.setMode(MediaMetadataRetriever.MODE_CAPTURE_FRAME_ONLY);
				bitmap = retriever.captureFrame();
				if (width > 0 && height > 0)
					bitmap = ThumbnailUtils.extractThumbnail(bitmap, width,height);
				else
					bitmap = ThumbnailUtils.extractThumbnail(bitmap, 96, 96);
			} catch (IllegalArgumentException ex) {
				// Assume this is a corrupt video file
			} catch (RuntimeException ex) {
				// Assume this is a corrupt video file.
			} finally {
				try {
					retriever.release();
				} catch (RuntimeException ex) {
				}
			}
		} else {
			if (width > 0 && height > 0){
				bitmap = ThumbnailUtils.createVideoThumbnail(filePath,Images.Thumbnails.MINI_KIND);// 640*480
				bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height);
			}else{
				 bitmap = ThumbnailUtils.createVideoThumbnail(filePath,Images.Thumbnails.MICRO_KIND);//96*96
			}
		}
		return bitmap;
	}
}
