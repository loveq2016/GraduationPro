package app.logic.activity.user;

import org.canson.view.photoview.PhotoViewAttacher;
import org.ql.activity.customtitle.ActActivity;

import com.hyphenate.easeui.widget.photoview.EasePhotoView;
import com.squareup.picasso.Picasso;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;

import app.config.http.HttpConfig;
import app.logic.activity.InitActActivity;
import app.yy.geju.R;

/*
 * GZYY    2016-10-27  下午4:59:12
 */

public class ShowBigImageActivity extends ActActivity {

    public static final String PIC_URL = "PIC_URL";
    public static final String PIC_BITMAP = "PIC_BITMAP";


    private Bitmap mBitmap;

    private EasePhotoView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT,
        // WindowManager.LayoutParams.FILL_PARENT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_big_image);

        imageView = (EasePhotoView) findViewById(R.id.image);

        String url = getIntent().getStringExtra(PIC_URL);


        if (TextUtils.isEmpty(url)) {
            Bitmap bitmap = getIntent().getParcelableExtra(PIC_BITMAP);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.default_user_icon));
            }

        } else {
            Picasso.with(this).load(url).placeholder(R.drawable.default_user_icon).error(R.drawable.default_user_icon).fit().centerInside().into(imageView);
        }


//
//		String imgUrl = getIntent().getStringExtra(PIC_URL);
//		mBitmap = getIntent().getParcelableExtra(PIC_BITMAP);
//		if (imgUrl != null && !TextUtils.isEmpty(imgUrl)) {
//			Picasso.with(this).load(imgUrl).fit().centerInside().into(image);
//		} else if (mBitmap != null) {
//			image.setImageBitmap(mBitmap);
//		} else {
//			image.setImageDrawable(getResources().getDrawable(R.drawable.default_user_icon));
//		}
//
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        imageView.setOnPhotoTapListener(new com.hyphenate.easeui.widget.photoview.PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                finish();
            }
        });

    }

}
