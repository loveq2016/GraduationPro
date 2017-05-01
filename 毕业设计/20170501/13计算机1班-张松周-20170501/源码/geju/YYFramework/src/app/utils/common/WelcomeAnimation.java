package app.utils.common;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class WelcomeAnimation extends Animation {
	private float centerX;
	private float centerY;
	private float formDegrees;
	private float fromZ;
	private Camera mCamera;
	private float toDegrees;
	private float toZ;

	public WelcomeAnimation(float centerX, float centerY, float formDegrees,float toDegrees, float fromZ, float toZ) {
		this.centerX = centerX;
		this.centerY = centerY;
		this.formDegrees = formDegrees;
		this.toDegrees = toDegrees;
		this.fromZ = fromZ;
		this.toZ = toZ;
	}

	protected void applyTransformation(float interpolatedTime, Transformation t) {
		float f1 = this.formDegrees + interpolatedTime * (this.toDegrees - this.formDegrees);
		float f2 = this.fromZ + interpolatedTime * (this.toZ - this.fromZ);
		Matrix matrix = t.getMatrix();
		mCamera.save();
		mCamera.translate(0.0F, 0.0F, f2);
		mCamera.rotateY(f1);
		mCamera.getMatrix(matrix);
		mCamera.restore();
		matrix.preTranslate(-this.centerX, -this.centerY);
		matrix.postTranslate(this.centerX, this.centerY);
	}
	
	@Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
		this.mCamera = new Camera();
        super.initialize(width, height, parentWidth, parentHeight);
    }
}
