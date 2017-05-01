package app.utils.download.thread;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.ql.utils.QLToastUtils;

import android.content.Context;
import app.logic.activity.user.Welcome2Activity;
import app.logic.pojo.UpdataAppInfo;
import app.logic.singleton.ZSZSingleton;
import app.logic.singleton.ZSZSingleton.DownloadListener;
import app.utils.service.YYDownloadService;

/*
 * GZYY    2016-9-5  下午2:53:25
 */

public class AppVersionDownloadThread extends Thread {

	private Context context;
	//
	private DownnloadFileSizeListener downnloadFileSizeListener;
	private UpdataAppInfo info;

	private HttpURLConnection connection = null;
	private InputStream inputStream = null;
	private RandomAccessFile randomAccessFile = null;
	private boolean checkFileSize = false;

	private int mFinished = 0;

	public AppVersionDownloadThread(Context context, UpdataAppInfo info) {
		this.context = context;
		this.info = info;
	}

	@Override
	public void run() {

		try {
			URL url = new URL(info.getApp_update_url());
			connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(30000);
			connection.setRequestMethod("GET");
			if (connection.getResponseCode() == 200) {
				// 创建目录
				File dir = new File(Welcome2Activity.DOWNLOAD_PATH);
				if (!dir.exists()) {
					dir.mkdir();
				}
				// 创建文件
				File appFile = new File(dir, info.getApp_name() + ".apk");

				randomAccessFile = new RandomAccessFile(appFile, "rwd");
				inputStream = connection.getInputStream();
				int len = -1;
				byte[] buffer = new byte[1024 * 4];
				while ((len = inputStream.read(buffer)) != -1) {
					randomAccessFile.write(buffer, 0, len);
					mFinished += len;
					
					ZSZSingleton.getZSZSingleton().getUpdataDownloadProgressListener().onCallBack(mFinished*100/connection.getContentLength());
					
				}
//				System.out.println("－－－－－－－－－－－－OTATO ！！！" + "文件下载完成！！");

				ZSZSingleton.getZSZSingleton().getStatusDownloadFileCompleteListener().onCallBack(Welcome2Activity.DOWNLOAD_PATH + "/" + info.getApp_name() + ".apk");

			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			closeStream();
		}
	}

	// 下载封装
	private void downloadEnc() {
		// 创建目录
		File dir = new File(Welcome2Activity.DOWNLOAD_PATH);
		if (!dir.exists()) {
			dir.mkdir();
		}
		// 创建文件
		File appFile = new File(dir, info.getApp_name() + ".apk");
		try {
			randomAccessFile = new RandomAccessFile(appFile, "rwd");
			inputStream = connection.getInputStream();
			int len = -1;
			byte[] buffer = new byte[1024 * 4];
			while ((len = inputStream.read(buffer)) != -1) {
				randomAccessFile.write(buffer, 0, len);
			}
			System.out.println("－－－－－－－－－－－－OTATO ！！！" + "文件下载完成！！");

			ZSZSingleton.getZSZSingleton().getStatusDownloadFileCompleteListener().onCallBack(Welcome2Activity.DOWNLOAD_PATH + "/" + info.getApp_name() + ".apk");

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			closeStream();
		}
	}

	// 关闭流
	private void closeStream() {

		if (randomAccessFile != null) {
			try {
				randomAccessFile.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (inputStream != null) {
			try {
				inputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (connection != null) {
			connection.disconnect();
		}

	}

	// 监听返回文件大小
	public void setDownloadFileSizeListener(DownnloadFileSizeListener listener) {
		this.downnloadFileSizeListener = listener;
	}

	public interface DownnloadFileSizeListener {
		void downloadFileSize(long fileSize);
	}

	// 是否设置监听文件的大小，直接下载（删除本地文件，重新下载）默认是false
	public void setCheckFileSize(boolean isCheck) {
		this.checkFileSize = isCheck;
	}

	public boolean getCheckFileSize() {
		return checkFileSize;
	}

}
