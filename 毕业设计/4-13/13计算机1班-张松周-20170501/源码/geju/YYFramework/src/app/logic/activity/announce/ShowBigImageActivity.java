package app.logic.activity.announce;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

import com.hyphenate.easeui.widget.photoview.EasePhotoView;
import com.hyphenate.easeui.widget.photoview.PhotoViewAttacher;
import com.squareup.picasso.Picasso;

import org.ql.activity.customtitle.ActActivity;

import java.io.File;

import app.yy.geju.R;

/*
 * GZYY    2016-10-27  下午4:59:12
 */

public class ShowBigImageActivity extends ActActivity {
    public static final String KEY_PIC_LOCAL_PATH = "key_pic_local_path";
    public static final String KEY_PIC_REMOTE_PATH = "key_pic_remote_path";

    private EasePhotoView mPhotoView;
    private String mPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_big_image);

        mPhotoView = (EasePhotoView) findViewById(R.id.image);
        mPath = getIntent().getStringExtra(KEY_PIC_LOCAL_PATH);

        if (!TextUtils.isEmpty(getIntent().getStringExtra(KEY_PIC_LOCAL_PATH))) {
            File imgFile = new File(mPath);
            Picasso.with(this).load(imgFile).fit().centerInside().into(mPhotoView);
        } else if (!TextUtils.isEmpty(getIntent().getStringExtra(KEY_PIC_REMOTE_PATH))) {
            Picasso.with(this).load(getIntent().getStringExtra(KEY_PIC_REMOTE_PATH)).fit().centerInside().into(mPhotoView);
        }

        mPhotoView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mPhotoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                finish();
            }
        });
    }

}
