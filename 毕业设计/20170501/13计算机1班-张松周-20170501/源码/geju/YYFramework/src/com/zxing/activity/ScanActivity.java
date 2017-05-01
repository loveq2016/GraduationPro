package com.zxing.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.hyphenate.easeui.ui.EaseBaseActivity;

import java.io.UnsupportedEncodingException;

import app.logic.activity.ActTitleHandler;
import app.yy.geju.R;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.FinishListener;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

public class ScanActivity extends EaseBaseActivity implements QRCodeView.Delegate {
    private static final String TAG = ScanActivity.class.getSimpleName();
    private static final int REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY = 666;

    private QRCodeView mQRCodeView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActTitleHandler maHandler = new ActTitleHandler();
        setAbsHandler(maHandler);
        setContentView(R.layout.activity_scan);
//        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        maHandler.replaseLeftLayout(this, true);
        maHandler.getLeftLayout().setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });
        setTitle("扫描二维码");
        initSetting();
        mQRCodeView = (ZXingView) findViewById(R.id.zxingview);
        mQRCodeView.setDelegate(this);
    }

    /**
     * 初始化窗口设置
     */
    private void initSetting() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 保持屏幕处于点亮状态
        // window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); // 全屏
//		requestWindowFeature(Window.FEATURE_NO_TITLE); // 隐藏标题栏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 竖屏
    }

    @Override
    protected void onStart() {
        super.onStart();
        mQRCodeView.startCamera();
//        mQRCodeView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);

        mQRCodeView.showScanRect();
        mQRCodeView.startSpot();
    }

    @Override
    protected void onStop() {
        mQRCodeView.stopCamera();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mQRCodeView.stopSpot();
        mQRCodeView.onDestroy();
        super.onDestroy();
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        Log.i(TAG, "result:" + result);
//        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
        vibrate();
//        mQRCodeView.startSpot();
        Intent resultIntent = new Intent();
		Bundle bundle = new Bundle();
        bundle.putString("result", result);
        resultIntent.putExtras(bundle);
		this.setResult(RESULT_OK, resultIntent);
		this.finish();
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.e(TAG, "打开相机出错");
        displayFrameworkBugMessageAndExit();
    }

    /**
     * 若摄像头被占用或者摄像头有问题则跳出提示对话框
     */
    private void displayFrameworkBugMessageAndExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(getString(R.string.msg_camera_framework_bug));
        builder.setPositiveButton(R.string.button_ok, new FinishListener(this));
        builder.setOnCancelListener(new FinishListener(this));
        builder.show();
    }

    // 解决中文乱码问题
	private String recode(String str) {
		String formart = "";
		try {
			str = new String(str.getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return formart;
	}

    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.start_spot:
//                mQRCodeView.startSpot();
//                break;
//            case R.id.stop_spot:
//                mQRCodeView.stopSpot();
//                break;
//            case R.id.start_spot_showrect:
//                mQRCodeView.startSpotAndShowRect();
//                break;
//            case R.id.stop_spot_hiddenrect:
//                mQRCodeView.stopSpotAndHiddenRect();
//                break;
//            case R.id.show_rect:
//                mQRCodeView.showScanRect();
//                break;
//            case R.id.hidden_rect:
//                mQRCodeView.hiddenScanRect();
//                break;
//            case R.id.start_preview:
//                mQRCodeView.startCamera();
//                break;
//            case R.id.stop_preview:
//                mQRCodeView.stopCamera();
//                break;
//            case R.id.open_flashlight:
//                mQRCodeView.openFlashlight();
//                break;
//            case R.id.close_flashlight:
//                mQRCodeView.closeFlashlight();
//                break;
//            case R.id.scan_barcode:
//                mQRCodeView.changeToScanBarcodeStyle();
//                break;
//            case R.id.scan_qrcode:
//                mQRCodeView.changeToScanQRCodeStyle();
//                break;
//            case R.id.choose_qrcde_from_gallery:
//                /*
//                从相册选取二维码图片，这里为了方便演示，使用的是
//                https://github.com/bingoogolapple/BGAPhotoPicker-Android
//                这个库来从图库中选择二维码图片，这个库不是必须的，你也可以通过自己的方式从图库中选择图片
//                 */
//                startActivityForResult(BGAPhotoPickerActivity.newIntent(this, null, 1, null, false), REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY);
//                break;
//        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        mQRCodeView.showScanRect();
//
//        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY) {
//            final String picturePath = BGAPhotoPickerActivity.getSelectedImages(data).get(0);
//
//            /*
//            这里为了偷懒，就没有处理匿名 AsyncTask 内部类导致 Activity 泄漏的问题
//            请开发在使用时自行处理匿名内部类导致Activity内存泄漏的问题，处理方式可参考 https://github.com/GeniusVJR/LearningNotes/blob/master/Part1/Android/Android%E5%86%85%E5%AD%98%E6%B3%84%E6%BC%8F%E6%80%BB%E7%BB%93.md
//             */
//            new AsyncTask<Void, Void, String>() {
//                @Override
//                protected String doInBackground(Void... params) {
//                    return QRCodeDecoder.syncDecodeQRCode(picturePath);
//                }
//
//                @Override
//                protected void onPostExecute(String result) {
//                    if (TextUtils.isEmpty(result)) {
//                        Toast.makeText(TestScanActivity.this, "未发现二维码", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(TestScanActivity.this, result, Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }.execute();
//        }
//    }


}