package app.logic.activity.user;

import org.ql.activity.customtitle.ActActivity;

import com.squareup.picasso.Picasso;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView.ScaleType;
import app.view.SmoothImageView;
import app.yy.geju.R;

/*
 * GZYY    2016-10-27  下午5:14:08
 */

public class SpaceImageDetailActivity extends ActActivity {

	private int mPosition;
	private int mLocationX;
	private int mLocationY;
	private int mWidth;
	private int mHeight;
	SmoothImageView imageView = null;

	// private QLAsyncImage asyImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String bis = getIntent().getStringExtra("images");
		// mDatas=BitmapFactory.decodeByteArray(bis, 0, bis.length);
		// asyImage=new QLAsyncImage(SpaceImageDetailActivity.this);

		mPosition = getIntent().getIntExtra("position", 0);
		mLocationX = getIntent().getIntExtra("locationX", 0);
		mLocationY = getIntent().getIntExtra("locationY", 0);
		mWidth = getIntent().getIntExtra("width", 0);
		mHeight = getIntent().getIntExtra("height", 0);

		imageView = new SmoothImageView(this);
		imageView.setOriginalInfo(mWidth, mHeight, mLocationX, mLocationY);
		imageView.transformIn();
		imageView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
		imageView.setScaleType(ScaleType.FIT_CENTER);
		imageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		setContentView(imageView);
		// imageView.setImageBitmap(mDatas);

		// if(bis == null){
		// imageView.setImageResource(R.drawable.login_username);
		// }else{
		Picasso.with(SpaceImageDetailActivity.this).load(bis).placeholder(R.drawable.default_user_icon).into(imageView);
		// }
		/*
		 * asyImage.loadImage(bis,new ImageCallback() {
		 * 
		 * @Override public void imageLoaded(Bitmap bitmap, String imageUrl) {
		 * // TODO Auto-generated method stub if(bitmap!=null)
		 * imageView.setImageBitmap(bitmap); } });
		 */

		// ImageLoader.getInstance().displayImage(mDatas.get(mPosition),
		// imageView);

		// imageView.setImageResource(R.drawable.temp);
		// ScaleAnimation scaleAnimation = new ScaleAnimation(0.5f, 1.0f, 0.5f,
		// 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
		// 0.5f);
		// scaleAnimation.setDuration(300);
		// scaleAnimation.setInterpolator(new AccelerateInterpolator());
		// imageView.startAnimation(scaleAnimation);

	}

	@Override
	public void onBackPressed() {
		imageView.setOnTransformListener(new SmoothImageView.TransformListener() {
			@Override
			public void onTransformComplete(int mode) {
				if (mode == 2) {
					finish();
				}
			}
		});
		imageView.transformOut();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (isFinishing()) {
			overridePendingTransition(0, 0);
		}
	}
}
