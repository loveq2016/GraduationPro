package app.utils.file;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.CharsetDecoder;

import android.R.integer;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
/**
 * 
 * @author SuiJiYung
 *</br>
 */
public class YYFileManager {
	
	/** 
    *  
    * @param imgPath 
    * @param bitmap 
    * @return 
    */  
   public static String imgToBase64(String imgPath) {  
	   Bitmap bitmap = null;
       if (imgPath !=null && imgPath.length() > 0) {  
           bitmap = readBitmap(imgPath);  
       }  
       if(bitmap == null){  
           return null;
       }  
       ByteArrayOutputStream out = null;  
       try {  
           out = new ByteArrayOutputStream();  
           bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);  
           out.flush();  
           out.close();  
           byte[] imgBytes = out.toByteArray();  
           return Base64.encodeToString(imgBytes, Base64.DEFAULT);  
       } catch (Exception e) {  
           return null;  
       } finally {  
           try {  
               out.flush();  
               out.close();  
           } catch (IOException e) {  
               e.printStackTrace();  
           }  
       }  
   }  
 
   private static Bitmap readBitmap(String imgPath) {  
       try {  
           return BitmapFactory.decodeFile(imgPath);  
       } catch (Exception e) {  
           return null;  
       }  
   }  
 
   /** 
    *  
    * @param base64Data 
    * @param imgName 
    * @param imgFormat 图片格式 
    */  
   public static void base64ToBitmap(String base64Data,String imgName,String imgFormat) {
       byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);  
       Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);  
 
       File myCaptureFile = new File("/sdcard/", imgName);  
       FileOutputStream fos = null;  
       try {  
           fos = new FileOutputStream(myCaptureFile);  
       } catch (FileNotFoundException e) {  
           e.printStackTrace();  
       }  
       boolean isTu = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);  
       if (isTu) {  
           try {  
               fos.flush();  
               fos.close();  
           } catch (IOException e) {  
               e.printStackTrace();  
           }  
       } else {  
           try {  
               fos.close();  
           } catch (IOException e) {  
               e.printStackTrace();  
           }  
       }  
   } 
   
	/**
	 * 读取文件
	 * @param context
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static byte[] readFile(Context context,String path) throws IOException{
		if (path == null ) {
			return null;
		}
		File file = new File(path);
		if (!file.exists()) {
			return null;
		}
		byte[] buffer = null;
		FileInputStream fis = new FileInputStream(file);
		int lng = fis.available();
		buffer = new byte[lng];
		fis.read(buffer);
		fis.close();
		return buffer;
	}

	/**
	 * 从assets文件夹中读取文件
	 * @param context
	 * @param file
	 * @return
	 */
	public static StringBuffer readTextFileFromAssets(Context context,String file){
		AssetManager am = context.getAssets();
		StringBuffer sb = new StringBuffer();
		try {
			InputStream is = am.open(file);
			byte[] buffer = new byte[256];
			int readbytes = -1;
			byte[] buf = new byte[is.available()];
			int hadWrite = 0;
			while ((readbytes = is.read(buffer)) > 0) {
				System.arraycopy(buffer, 0, buf, hadWrite, readbytes);
				hadWrite += readbytes;
			};
			sb.append(new String(buf, "utf-8"));
			buffer = null;
			buf = null;
			is.close();
		} catch (IOException e) {
			
		}
		return sb;
	}
	
	/**
	 * 从SD卡读取文件
	 * @param path
	 * @return
	 */
	public static StringBuffer readTextFileFromPath(String path){
		StringBuffer sb = new StringBuffer();
		try {
			File file = new File(path);
			FileInputStream is = new FileInputStream(file);
			byte[] buffer = new byte[256];
			int readbytes = -1;
			byte[] buf = new byte[is.available()];
			int hadWrite = 0;
			while ((readbytes = is.read(buffer)) > 0) {
				System.arraycopy(buffer, 0, buf, hadWrite, readbytes);
				hadWrite += readbytes;
			};
			sb.append(new String(buf, "utf-8"));
			buffer = null;
			buf = null;
			is.close();
		} catch (IOException e) {
			
		}
		return sb;
	}
}
