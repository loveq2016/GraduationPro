package app.logic.roomavatar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.widget.ImageView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import app.config.http.HttpConfig;
import app.yy.geju.R;

/**
 * Created by Administrator on 2017/3/15 0015.
 * author : YSF
 * 网络请求图片用的
 */

public class NetControl {

    private static final int TIME_OUT = 8000;
    private static Context mContext;
    private static File cacheDir;
    private ExecutorService exService;
    private OnNetCallBackListener   onNetCallBackListener ;
    private OnBitmapCallbackListener onBitmapCallbackListener ;

    public void setOnNetCallBackListener(OnNetCallBackListener onNetCallBackListener) {
        this.onNetCallBackListener = onNetCallBackListener;
    }

    public void setOnBitmapCallbackListener(OnBitmapCallbackListener onBitmapCallbackListener) {
        this.onBitmapCallbackListener = onBitmapCallbackListener;
    }

    /**
     * 构造方法
     */
    public NetControl( Context context ) {
        //初始化一个固定有4条线程的线程池
        exService = Executors.newFixedThreadPool(4);
        mContext = context;
        //获取应用缓存路径
        cacheDir = mContext.getCacheDir();
    }

    /**
     * 从网络中获取本地图片
     * @param path
     */
    public void getImageFromNet( final String path ) {
        //用线程池来执行任务
        exService.execute( new Runnable() {
            @Override
            public void run() {
                System.out.println(" Path = "+ path );
                if( path == null || TextUtils.isEmpty( path )){
                    Bitmap bitmap = BitmapFactory.decodeResource( mContext.getResources() , R.drawable.ic_launcher);
                    onNetCallBackListener.onNetCallback( bitmap );
                }else{
                    //从url中截取出图片的名字
                    String fileName = path.substring(path.lastIndexOf("/") + 1 );
                    System.out.println(" fileName = "+ fileName );
                    File file = new File(cacheDir, fileName);
                    //看看图片文件是否存在
                    if (file.exists()) {//存在则说明本地有此图片，直接从本地读取
                        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                        onNetCallBackListener.onNetCallback( bitmap );
                    } else {//本地无该图，联网获取
                        fromNet(path);
                    }
                }
            }
        });
    }

    /**
     * 网络获取图片
     * @param path
     */
    private void fromNet(String path) {
        Bitmap bitmap = null ;
        try {
            URL url = new URL( HttpConfig.getUrl( path ) );
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(TIME_OUT);
            conn.connect();
            int code = conn.getResponseCode();
            if ( code == HttpURLConnection.HTTP_OK ) {
                InputStream inputStream = conn.getInputStream();
                //把流解码成图片
                bitmap = BitmapFactory.decodeStream(inputStream);
                //回调结果
                //从url中截取出图片的名字
                String fileName = path.substring(path.lastIndexOf("/") + 1);
                File file = new File(cacheDir, fileName);
                FileOutputStream fos = new FileOutputStream(file);
                //把图片存到本地缓存中 第一个参数，图片格式  第二个参数 存储质量  第三个参数输出流
                bitmap.compress( Bitmap.CompressFormat.JPEG , 100 , fos );
                onNetCallBackListener.onNetCallback( bitmap );
            }
        } catch (Exception e) {
            e.printStackTrace();
            bitmap = BitmapFactory.decodeResource( mContext.getResources() , R.drawable.ic_launcher);
            onNetCallBackListener.onNetCallback( bitmap );
        }
    }

    /**
     * 网络回调接口
     */
    public interface OnNetCallBackListener {
        void onNetCallback(Bitmap bitmap);
    }

    /**
     * Bitmap 回调接口
     */
    public interface OnBitmapCallbackListener {
        void onBitmapCallback(String url, Bitmap bitmap, ImageView coverView);
    }

}
