package app.utils.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import org.ql.activity.customtitle.ActivityInterface;
import org.ql.activity.customtitle.Activitys;
import org.ql.activity.customtitle.OnActActivityResultListener;
import org.ql.activity.customtitle.onActPermissionCheckResultListener;
import org.ql.app.alert.AlertDialog;

import java.io.File;
import java.util.Random;

import app.utils.common.Listener;
import app.utils.common.Public;
import app.utils.image.QLImageHelper;
import app.yy.geju.R;

/**
 * SiuJiYung create at 2016年8月12日 下午5:29:56
 */

public class ImagePickerHelper implements OnActActivityResultListener, onActPermissionCheckResultListener {

    public static int kImageSource_UserSelect = 2;
    public static int kImageSource_Atlas = 1;
    public static int kImageSource_CameraOnly = 0;

    private Listener<Void, String> onRecevieImageListener;
    private ActivityInterface mActivity;
    private Context mContext;
    private String currImageName;
    private OnActActivityResultListener onActActivityResultListener;
    private int imageMaxWidth;
    private int imageMaxHeight;
    private int imgSourceType;

    private String sdFilePath;

    private boolean replace = false;

    private ImagePickerHelper() {
        imageMaxWidth = 800;
        imageMaxHeight = 600;
        imgSourceType = kImageSource_UserSelect;
    }

    public void setImageSourceType(int sourceType) {
        imgSourceType = sourceType;
    }

    public int getImageSourceType() {
        return imgSourceType;
    }

    public static ImagePickerHelper createNewImagePickerHelper(Context activity) {
        ImagePickerHelper helper = new ImagePickerHelper();
        helper.setActivity((ActivityInterface) activity);
        helper.mContext = activity;
        /**
         * 增加权限接口
         */
        helper.setActivityPermissionListener(activity);

        return helper;
    }

    private void setActivityPermissionListener(Context mContext) {
        ((Activitys) mContext).setOnPermissionCheckResultListener(this);
    }

    public void setOnActActivityResultListener(OnActActivityResultListener l) {
        onActActivityResultListener = l;
    }

    private void setActivity(ActivityInterface activity) {
        mActivity = activity;
        mActivity.setActivityResultListener(this);
    }

    public void setOnReciveImageListener(Listener<Void, String> l) {
        onRecevieImageListener = l;
    }

    public void openCamera() {
        currImageName = QLImageHelper.createDefaultImageName();
        if (imgSourceType == kImageSource_UserSelect) {
            showSelectActionDialog();
        } else {
            QLImageHelper.openCamera(mContext, currImageName, imgSourceType);
        }
    }

    private AlertDialog dialog = null;

    private void showSelectActionDialog() {
        if (dialog == null) {
            AlertDialog.Builder builder = null;

            View dialogView = null;
            if (replace) {
                builder = new AlertDialog.Builder(new ContextThemeWrapper(mContext, R.style.ZSZDialog));
                builder.setIcon(0);
                dialog = builder.create();
                // dialog = new AlertDialog(mContext, R.style.ZSZDialog);

                dialogView = LayoutInflater.from(mContext).inflate(R.layout.view_select_imgpicker_action_new, null);
                dialog.setView(dialogView);
                dialogView.findViewById(R.id.img_picker_new_carmen).setOnClickListener(onViewClickListener);
                dialogView.findViewById(R.id.img_picker_new_alerm).setOnClickListener(onViewClickListener);
                dialogView.findViewById(R.id.img_picker_new_cancel).setOnClickListener(onViewClickListener);
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);

                Window window = dialog.getWindow();
                WindowManager.LayoutParams lp = window.getAttributes();
                lp.gravity = Gravity.BOTTOM;
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                window.setAttributes(lp);

            } else {
                builder = new AlertDialog.Builder(mContext);
                dialogView = LayoutInflater.from(mContext).inflate(R.layout.view_select_imgpicker_action, null);
                builder.setIcon(0);
                dialog = builder.create();
                dialogView.findViewById(R.id.img_picker_alerm).setOnClickListener(onViewClickListener);
                dialogView.findViewById(R.id.img_picker_camera).setOnClickListener(onViewClickListener);
                dialog.setView(dialogView);
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);
            }

        } else if (dialog.isShowing()) {
            return;
        }
        hideKeyboard();
        try{
            if (mContext !=null)
                dialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void hideKeyboard() {
        if (((Activity) mContext).getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (((Activity) mContext).getCurrentFocus() != null)
                ((InputMethodManager) ((Activity) mContext).getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(((Activity) mContext).getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void setReplaceContentLayout(boolean status) {
        this.replace = status;
    }

    public AlertDialog getAlertDialog() {
        return dialog;
    }

    private OnClickListener onViewClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int vid = v.getId();
            switch (vid) {
                case R.id.img_picker_alerm:
                    imgSourceType = kImageSource_Atlas;
                    QLImageHelper.openCamera(mContext, currImageName, imgSourceType);

                    break;
                case R.id.img_picker_camera:
                    imgSourceType = kImageSource_CameraOnly;
                    QLImageHelper.openCamera(mContext, currImageName, imgSourceType);
                    break;
                case R.id.img_picker_new_carmen:
//                    imgSourceType = kImageSource_CameraOnly;
//                    QLImageHelper.openCamera(mContext, currImageName, imgSourceType);

                    requestPermissionForCamera();
                    break;
                case R.id.img_picker_new_alerm:
                    imgSourceType = kImageSource_Atlas;
                    QLImageHelper.openCamera(mContext, currImageName, imgSourceType);
                    break;
                case R.id.img_picker_new_cancel:
                    break;
            }
            dialog.dismiss();
        }
    };


    private static final int REQUEST_CODE_CAMERA = 101;

    /**
     * 相机的请求权限
     */
    private void requestPermissionForCamera() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) mContext, new String[]{android.Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
                return;
            }
        }
        imgSourceType = kImageSource_CameraOnly;
        QLImageHelper.openCamera(mContext, currImageName, imgSourceType);

    }


    @Override
    public void onPermissionCheckResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                imgSourceType = kImageSource_CameraOnly;
                QLImageHelper.openCamera(mContext, currImageName, imgSourceType);
            }
        }

    }


    public File getPhotoPath() {
        if (currImageName == null) {
            return null;
        }
        return new File(QLImageHelper.getPhotoDir(), currImageName);
    }

    private String compressImage(String path) {
        Random r = new Random();
        int rint = r.nextInt(9999);
        String fileName = "/tmp" + Public.getTimeWithFormat("yyyyMMddHH") + rint + ".jpg";

        String newPath = QLImageHelper.getPhotoDir() + fileName;
        QLImageHelper.compressImage(path, newPath, 500);
        return newPath;
    }

    public String getSDFilePath() {
        return sdFilePath;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == QLImageHelper.FLAG_CHOOSE_PHONE && resultCode == Activity.RESULT_OK) {
            if (onActActivityResultListener != null) {
                onActActivityResultListener.onActivityResult(requestCode, resultCode, data);
            }
            if (onRecevieImageListener != null) {
                String _path = null;
                if (imgSourceType == kImageSource_Atlas) {
                    Uri uri = data.getData();
                    Cursor cursor = null;
                    String columName = null;
                    if (Build.VERSION.SDK_INT >= 19) {
                        // Android 4.4
                        String[] filePathColumn = {ImageColumns.DATA};
                        columName = ImageColumns.DATA;
                        cursor = mContext.getContentResolver().query(uri, null, null, null, null);
                    } else {
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        columName = MediaStore.Images.Media.DATA;
                        cursor = ((Activity) mContext).managedQuery(uri, null, null, null, null);
                    }
                    if (cursor != null && cursor.moveToFirst()) {
                        int column_index = cursor.getColumnIndexOrThrow(columName);
                        _path = cursor.getString(column_index);
                        cursor.close();
                    } else if (cursor == null) {
                        _path = uri.getPath();
                    }
                    if (_path == null) {
                        Bitmap bm = null;
                        try {
                            File _tmpRootPathFile = QLImageHelper.getPhotoDir();
                            File _tmpFilePath = new File(_tmpRootPathFile, QLImageHelper.createDefaultImageName());
                            boolean saveSessuss = QLImageHelper.saveBitmapToPath(bm, _tmpFilePath.getAbsolutePath());
                            if (saveSessuss) {
                                _path = _tmpFilePath.getAbsolutePath();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    File photoPathFile = getPhotoPath();
                    _path = photoPathFile.getAbsolutePath();
                }
                final String location = _path;

                if (openSysCrop) {
//					String compressPath = compressImage(location);
                    File file = new File(location);
//					imageUri = Uri.fromFile(file);
                    openSysImgCrop(Uri.fromFile(file));

                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final String compressPath = compressImage(location);
                            ((Activity) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    onRecevieImageListener.onCallBack(null, compressPath);
                                }
                            });
                        }
                    }).start();
                }

            }
        }

//        if (requestCode == FLAG_OPEN_CROP && resultCode == Activity.RESULT_OK) {
//            final String _pathString = imageUri.getPath();
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    final String compressPath = compressImage(_pathString);
//                    ((Activity) mContext).runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            onRecevieImageListener.onCallBack(null, compressPath);
//                        }
//                    });
//                }
//            }).start();
//
//        }

        if (requestCode == FLAG_OPEN_CROP) {
            if (resultCode == Activity.RESULT_OK){
                final String _pathString = imageUri.getPath();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final String compressPath = compressImage(_pathString);
                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (onRecevieImageListener !=null)
                                    onRecevieImageListener.onCallBack(null, compressPath);
                            }
                        });
                    }
                }).start();
            } else {
                if (onRecevieImageListener != null) {
                    onRecevieImageListener.onCallBack(null, null);
                }
            }
        }
    }

    private boolean openSysCrop = false;
    public static final int FLAG_OPEN_CROP = 100;
    private Uri imageUri;

    /**
     * 设置打开系统的裁剪
     *
     * @param status setOpenSysCropImagePickerHelper
     */
    public void setOpenSysCrop(boolean status) {
        this.openSysCrop = status;
    }

    private int aspectX = 1;
    private int aspectY = 1;
    private int outputX = 150;
    private int outputY = 150;

    /**
     * 设置Crop样式
     *
     * @param aspectX 比例
     * @param aspectY 比例
     * @param outputX 输出大小
     * @param outputY 输出大小 setCropStyleImagePickerHelper
     */
    public void setCropStyle(int aspectX, int aspectY, int outputX, int outputY) {
        this.aspectX = aspectX;
        this.aspectY = aspectY;
        this.outputX = outputX;
        this.outputY = outputY;
    }

    private void openSysImgCrop(Uri uri) {

        Intent intent = new Intent();
        intent.setAction("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", aspectX);// 裁剪框比例
        intent.putExtra("aspectY", aspectY);
        intent.putExtra("outputX", outputX);// 输出图片大小
        intent.putExtra("outputY", outputY);

        intent.putExtra("return-data", false);
        String compressPath = compressImage(uri.getPath());
        File file = new File(compressPath);
        imageUri = Uri.fromFile(file);
//        imageUri = uri;
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        ((Activity) mContext).startActivityForResult(intent, FLAG_OPEN_CROP);
    }


}
