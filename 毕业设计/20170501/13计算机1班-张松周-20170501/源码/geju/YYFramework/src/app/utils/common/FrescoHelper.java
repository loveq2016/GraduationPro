package app.utils.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Looper;

import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableBitmap;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;

import app.yy.geju.R;

/**
 * Created by ChenJiaPei on 2017-03-27.
 */
public class FrescoHelper {

    private FrescoHelper() {
        // Just a util, cannot be instantiated.
    }


    // 由于是 getUnderlyingBitmap() 获得 bitmap, 速度太慢，不建议用
    @SuppressWarnings("unused")
    @Deprecated
    public static void syncLoad(Uri uri, final SimpleDraweeView draweeView) {
        final ImageRequest request = ImageRequest.fromUri(uri);
        final ImagePipeline pipeline = Fresco.getImagePipeline();
        final DataSource<CloseableReference<CloseableImage>> dataSource = pipeline.fetchImageFromBitmapCache(request, draweeView.getContext());
        try {
            CloseableReference<CloseableImage> imageRef = dataSource.getResult();
            if (imageRef != null) {
                try {
                    final CloseableBitmap image = (CloseableBitmap) imageRef.get();
                    draweeView.setImageBitmap(image.getUnderlyingBitmap());
                } finally {
                    CloseableReference.closeSafely(imageRef);
                }
            }
        } finally {
            dataSource.close();
        }
    }

    public static void asyncLoad(final Uri uri, final SimpleDraweeView draweeView) {
        final ImageRequest request = ImageRequest.fromUri(uri);
        final ImagePipeline pipeline = Fresco.getImagePipeline();
        final DataSource<CloseableReference<CloseableImage>> dataSource = pipeline.fetchDecodedImage(request, draweeView.getContext());
        dataSource.subscribe(new BaseBitmapDataSubscriber() {
            @Override
            protected void onNewResultImpl(final Bitmap bitmap) {
                if (Looper.getMainLooper() != Looper.myLooper()) {
                    draweeView.post(new Runnable() {
                        @Override
                        public void run() {
                            draweeView.setImageBitmap(bitmap);
                        }
                    });
                } else {
                    draweeView.setImageBitmap(bitmap);
                }
            }

            @Override
            protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {

            }
        }, CallerThreadExecutor.getInstance());
    }
}
