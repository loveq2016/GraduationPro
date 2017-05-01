package app.logic.roomavatar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.media.ThumbnailUtils;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import app.yy.geju.R;

/**
 * Created by Administrator on 2017/3/15 0015.
 */

public class CreatAvatar {

    private Context mContext ;
    private List<MyBitmapEntity> mEntityList;

    public CreatAvatar( Context mContext ){
        this.mContext = mContext ;
    }

    public Bitmap avatarBitmap( List<Bitmap> bitmaps){
        Bitmap combineBitmap = BitmapFactory.decodeResource( mContext.getResources() , R.drawable.ic_launcher) ;
        if( bitmaps==null || bitmaps.size()==0){
            return combineBitmap;
        }
        System.out.println("length = "+ bitmaps.size() );
        mEntityList = getBitmapEntitys( bitmaps.size() );
        if( bitmaps.size() >= 4){
            Bitmap[] mBitmaps = {
                    ThumbnailUtils.extractThumbnail( bitmaps.get(0) , (int) mEntityList.get(0).width, (int) mEntityList.get(0).width),
                    ThumbnailUtils.extractThumbnail( bitmaps.get(1) , (int) mEntityList.get(0).width, (int) mEntityList.get(0).width),
                    ThumbnailUtils.extractThumbnail( bitmaps.get(2) , (int) mEntityList.get(0).width, (int) mEntityList.get(0).width),
                    ThumbnailUtils.extractThumbnail( bitmaps.get(3) , (int) mEntityList.get(1).width, (int) mEntityList.get(1).width)};
            combineBitmap = getCombineBitmaps( mEntityList , mBitmaps );
        }else if( bitmaps.size() == 3 ){
            Bitmap[] mBitmaps = {
                    ThumbnailUtils.extractThumbnail( bitmaps.get(0) , (int) mEntityList.get(0).width, (int) mEntityList.get(0).width),
                    ThumbnailUtils.extractThumbnail( bitmaps.get(1) , (int) mEntityList.get(0).width, (int) mEntityList.get(0).width),
                    ThumbnailUtils.extractThumbnail( bitmaps.get(2) , (int) mEntityList.get(0).width, (int) mEntityList.get(0).width)};
            combineBitmap = getCombineBitmaps( mEntityList , mBitmaps );
        }else if(bitmaps.size() == 2 ){
            Bitmap[] mBitmaps = {
                    ThumbnailUtils.extractThumbnail( bitmaps.get(0) , (int) mEntityList.get(0).width, (int) mEntityList.get(0).width),
                    ThumbnailUtils.extractThumbnail( bitmaps.get(0) , (int) mEntityList.get(0).width, (int) mEntityList.get(0).width)};
            combineBitmap = getCombineBitmaps( mEntityList , mBitmaps );
        }else{
            combineBitmap = bitmaps.get(0);
        }

        return combineBitmap ;
    }

    public List<MyBitmapEntity> getBitmapEntitys(int count) {
        List<MyBitmapEntity> mList = new LinkedList<MyBitmapEntity>();
        String value = readData( mContext , String.valueOf(count) , R.raw.data );
        String[] arr1 = value.split(";");
        int length = arr1.length;
        for (int i = 0; i < length; i++) {
            String content = arr1[i];
            String[] arr2 = content.split(",");
            MyBitmapEntity entity = null;
            for (int j = 0; j < arr2.length; j++) {
                entity = new MyBitmapEntity();
                entity.x = Float.valueOf(arr2[0]);
                entity.y = Float.valueOf(arr2[1]);
                entity.width = Float.valueOf(arr2[2]);
                entity.height = Float.valueOf(arr2[3]);
            }
            mList.add(entity);
        }
        return mList;
    }

    /**
     *
     * @param mContext
     * @param key
     * @param resId
     * @return
     */
    public static String readData(Context mContext, String key, int resId) {
        Properties props = new Properties();
        try {
            InputStream in = new BufferedInputStream( mContext.getResources().openRawResource(resId));
            props.load(in);
            in.close();
            String value = props.getProperty(key);
            return value;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param mEntityList
     * @param bitmaps
     * @return
     */
    public Bitmap getCombineBitmaps(List<CreatAvatar.MyBitmapEntity> mEntityList, Bitmap... bitmaps) {
        Bitmap newBitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888 );
        for (int i = 0; i < mEntityList.size(); i++) {
            newBitmap = mixtureBitmap(newBitmap, bitmaps[i], new PointF(mEntityList.get(i).x, mEntityList.get(i).y));
        }
        return newBitmap;
    }

    /**
     * Mix two Bitmap as one.
     * @param
     * @param
     * @param
     * second bitmap is painted.
     * @return
     */
    public Bitmap mixtureBitmap(Bitmap first , Bitmap second , PointF fromPoint) {
        if (first == null || second == null || fromPoint == null) {
            return null;
        }
        Bitmap newBitmap = Bitmap.createBitmap(first.getWidth(), first.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas cv = new Canvas(newBitmap);
        cv.drawBitmap(first, 0, 0, null);
        cv.drawBitmap(second, fromPoint.x, fromPoint.y, null);
        cv.save(Canvas.ALL_SAVE_FLAG);
        cv.restore();
        return newBitmap;
    }

    public static class MyBitmapEntity {
        float x;
        float y;
        float width;
        float height;
        static int devide = 1;
        int index = -1;

        @Override
        public String toString() {
            return "MyBitmap [x=" + x + ", y=" + y + ", width=" + width + ", height=" + height + ", devide=" + devide + ", index=" + index + "]";
        }
    }
}
