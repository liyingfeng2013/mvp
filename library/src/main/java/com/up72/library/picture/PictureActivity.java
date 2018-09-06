package com.up72.library.picture;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.up72.library.R;
import com.up72.library.picture.crop.Crop;
import com.up72.library.utils.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PictureActivity extends AppCompatActivity {
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_NATIVE = 2;
    private View rootView;
    private File tempOldImg, tempNewImg;
    private boolean isCrop = false;
    private int maxWidth = 800;
    private int maxHeight = 800;
    private int width = 0;
    private int height = 0;
    private long maxKB = 0;

    private boolean isProcessing = false;//是否正在处理照片

    private Log log = new Log(getClass());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        rootView = findViewById(R.id.rootView);
        LinearLayout layContent = (LinearLayout) findViewById(R.id.layContent);
        TextView tvCancel = (TextView) findViewById(R.id.tvCancel);
        TextView tvNative = (TextView) findViewById(R.id.tvNative);
        TextView tvCamera = (TextView) findViewById(R.id.tvCamera);

        View.OnClickListener onClickCancel = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        };
        if (layContent != null) {
            layContent.setOnClickListener(onClickCancel);
        }
        if (tvCancel != null) {
            tvCancel.setOnClickListener(onClickCancel);
        }
        if (tvNative != null) {
            tvNative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //打开相册
                    photos();
                }
            });
        }
        if (tvCamera != null) {
            tvCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //打开相机
                    camera();
                }
            });
        }
        if (!initCacheDir()) {
            log.e("图片选择-----缓存目录初始化失败");
            finish();
        }
        initData();
    }

    @Override
    protected void onDestroy() {
        if (tempOldImg != null) {
            tempOldImg = null;
        }
        if (tempNewImg != null) {
            tempNewImg = null;
        }
        super.onDestroy();
    }

    private void initData() {
        int type = Picture.Type.DEFAULT;
        if (getIntent() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                if (bundle.containsKey(Picture.Extra.OPEN_TYPE)) {
                    type = bundle.getInt(Picture.Extra.OPEN_TYPE, 0);
                }
                if (bundle.containsKey(Picture.Extra.IS_CROP)) {
                    isCrop = bundle.getBoolean(Picture.Extra.IS_CROP, false);
                }
                if (bundle.containsKey(Picture.Extra.DEFAULT_WIDTH)) {
                    width = bundle.getInt(Picture.Extra.DEFAULT_WIDTH, 0);
                }
                if (bundle.containsKey(Picture.Extra.DEFAULT_HEIGHT)) {
                    height = bundle.getInt(Picture.Extra.DEFAULT_HEIGHT, 0);
                }
                if (bundle.containsKey(Picture.Extra.MAX_WIDTH)) {
                    maxWidth = bundle.getInt(Picture.Extra.MAX_WIDTH, 800);
                }
                if (bundle.containsKey(Picture.Extra.MAX_HEIGHT)) {
                    maxHeight = bundle.getInt(Picture.Extra.MAX_HEIGHT, 800);
                }
                if (bundle.containsKey(Picture.Extra.MAX_KB)) {
                    maxKB = bundle.getLong(Picture.Extra.MAX_KB, 0);
                }
            }
            log.d("isCrop:" + isCrop + "  width:" + width + "  height:" + height + "  maxWidth:" + maxWidth + "  maxHeight:" + maxHeight + "  maxKB:" + maxKB);
        }
        switch (type) {
            case Picture.Type.DEFAULT:
                rootView.setVisibility(View.VISIBLE);
                break;
            case Picture.Type.CAMERA:
                rootView.setVisibility(View.GONE);
                camera();
                break;
            case Picture.Type.PHOTO:
                rootView.setVisibility(View.GONE);
                photos();
                break;
            default:
                rootView.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * 初始化缓存文件目录
     *
     * @return true 成功
     */
    private boolean initCacheDir() {
        String path = null;
        File file;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) || !Environment.isExternalStorageRemovable()) {
            file = getExternalCacheDir();
        } else {
            file = getCacheDir();
        }
        if (file != null && file.exists()) {
            path = file.getAbsolutePath();
        }
        if (path != null) {
            File fileDir = new File(path + "/" + "tempImages");
            if (!fileDir.exists() && !fileDir.mkdirs()) {
                return false;
            }

            tempOldImg = new File(fileDir, System.currentTimeMillis() + "tempOldHead.jpg");
            tempNewImg = new File(fileDir, System.currentTimeMillis() + "tempNewHead.jpg");
            try {
                if (!tempOldImg.exists() && !tempOldImg.createNewFile()) {
                    return false;
                }
                if (!tempNewImg.exists() && !tempNewImg.createNewFile()) {
                    return false;
                }
            } catch (IOException e) {
                log.e(e);
                return false;
            }
            return true;
        }
        return false;
    }

    //相机
    private void camera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempOldImg));
        startActivityForResult(intent, REQUEST_CAMERA);
        overridePendingTransition(0, 0);
    }

    //相册
    private void photos() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_NATIVE);
        overridePendingTransition(0,0);
    }

    // 裁剪图片
    private void startPhotoZoom(Uri uri) {
        Crop.of(uri, Uri.fromFile(tempNewImg)).withAspect(width, height).withMaxSize(maxWidth, maxHeight).start(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CAMERA:
                    if (tempOldImg != null) {
                        if (isCrop) {
                            startPhotoZoom(Uri.fromFile(tempOldImg));
                        } else {
                            resultActivity(tempOldImg.getAbsolutePath(), tempOldImg.getAbsolutePath());
                        }
                    }
                    break;
                case REQUEST_NATIVE:
                    if (data != null) {
                        if (isCrop) {
                            startPhotoZoom(data.getData());
                        } else {
                            resultActivity(getPathFromUri(data.getData(), tempNewImg.getAbsolutePath()), tempNewImg.getAbsolutePath());
                        }
                    }
                    break;
                case Crop.REQUEST_CROP:
                    if (tempNewImg != null) {
                        resultActivity(tempNewImg.getAbsolutePath(), tempNewImg.getAbsolutePath());
                    }
                    break;
            }
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, "图片裁剪失败", Toast.LENGTH_SHORT).show();
        } else {
            finish();
        }
    }

    @NonNull
    public String getPathFromUri(Uri uri, String tempFilePath) {
        if (uri == null) {
            return "";
        }
        String filePath = "";
        if ("file".equals(uri.getScheme())) {
            return uri.getPath();
        } else if ("content".equals(uri.getScheme())) {
            final String[] filePathColumn = {MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME};
            Cursor cursor = null;
            ContentResolver resolver = getContentResolver();
            try {
                cursor = resolver.query(uri, filePathColumn, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    final int columnIndex = (uri.toString().startsWith("content://com.google.android.gallery3d")) ?
                            cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME) :
                            cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
                    if (columnIndex != -1) {
                        filePath = cursor.getString(columnIndex);
                    }
                }
            } catch (IllegalArgumentException e) {
                filePath = getFromMediaUriPfd(resolver, uri, tempFilePath);
            } catch (SecurityException ignored) {
                filePath = "";
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (filePath == null || filePath.equals("")) {
            filePath = writeNewFile(getContentResolver(), uri, tempFilePath);
        }
        return filePath;
    }

    @NonNull
    private String getFromMediaUriPfd(ContentResolver resolver, Uri uri, String tempFilePath) {
        if (uri == null) {
            return "";
        }
        FileInputStream input = null;
        FileOutputStream output = null;
        try {
            ParcelFileDescriptor pfd = resolver.openFileDescriptor(uri, "r");
            FileDescriptor fd;
            if (pfd != null) {
                fd = pfd.getFileDescriptor();
                input = new FileInputStream(fd);
                output = new FileOutputStream(tempFilePath);

                int read;
                byte[] bytes = new byte[4096];
                while ((read = input.read(bytes)) != -1) {
                    output.write(bytes, 0, read);
                }
            }
        } catch (IOException e) {
            log.e(e);
            tempFilePath = "";
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
                if (output != null) {
                    output.close();
                }
            } catch (IOException e) {
                log.e(e);
            }
        }
        if (tempFilePath == null) {
            tempFilePath = "";
        }
        return tempFilePath;
    }

    private String writeNewFile(ContentResolver resolver, Uri uri, String tempFilePath) {
        if (uri == null) {
            return "";
        }
        InputStream input = null;
        FileOutputStream output = null;
        try {
            input = resolver.openInputStream(uri);
            if (input != null) {
                output = new FileOutputStream(tempFilePath);
                int read;
                byte[] bytes = new byte[4096];
                while ((read = input.read(bytes)) != -1) {
                    output.write(bytes, 0, read);
                }
            }
        } catch (IOException e) {
            log.e(e);
            tempFilePath = "";
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
                if (output != null) {
                    output.close();
                }
            } catch (IOException e) {
                log.e(e);
            }
        }
        if (tempFilePath == null) {
            tempFilePath = "";
        }
        return tempFilePath;
    }

    private void resultActivity(@NonNull final String path, @NonNull final String newPath) {
        if (isProcessing) {
            return;
        }
        isProcessing = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent resultData = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString(Picture.IMAGE_PATH, process(path, newPath, maxWidth, maxHeight, maxKB));
                resultData.putExtras(bundle);
                setResult(RESULT_OK, resultData);
                PictureActivity.this.finish();
                isProcessing = true;
            }
        }).start();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    /**
     * 读取照片exif信息中的旋转角度
     *
     * @param path 照片路径
     * @return 角度
     */
    private int readDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 图片处理
     *
     * @param path    图片绝对路径
     * @param newPath 图片绝对路径
     * @param w       最大宽
     * @param h       最大高
     * @param maxKB   最大大小
     * @return 图片绝对路径
     */
    private String process(final String path, final String newPath, final int w, final int h, final long maxKB) {
        //图片角度
        final int degree = readDegree(path);

        Bitmap bitmap;

        //region 图片尺寸比例压缩
        BitmapFactory.Options opts = new BitmapFactory.Options();
        // 设置为true只获取图片大小
        opts.inJustDecodeBounds = true;//只读边,不读内容
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        // 返回为空
        BitmapFactory.decodeFile(path, opts);
        int width = opts.outWidth;
        int height = opts.outHeight;
        Bitmap.CompressFormat format;
        if (path.endsWith(".jpg")) {
            format = Bitmap.CompressFormat.JPEG;
        } else if (path.endsWith(".jpeg")) {
            format = Bitmap.CompressFormat.JPEG;
        } else if (path.endsWith(".png")) {
            format = Bitmap.CompressFormat.PNG;
        } else {
            format = Bitmap.CompressFormat.JPEG;
        }
        float scaleWidth = 0.f, scaleHeight = 0.f;
        if (width > w || height > h) {//如果宽度大于传入的宽度  或者 高度大于传入的高度大于
            // 缩放
            scaleWidth = ((float) width) / w;
            scaleHeight = ((float) height) / h;
        }
        opts.inJustDecodeBounds = false;
        //缩放后的高度和宽度取最大值
        float scale = Math.max(scaleWidth, scaleHeight);
        //inSampleSize只能是2的n次方，这里去最近的
        double n = Math.log(scale) / Math.log(2);
        n = Math.rint(n);
        opts.inSampleSize = (int) Math.pow(2, n);//此处是最后的宽高值
        bitmap = BitmapFactory.decodeFile(path, opts);
        //endregion

        //region 质量压缩图片
        if (maxKB > 0) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(format, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            int options = 100;
            while (baos.toByteArray().length / 1024 > maxKB) {  //循环判断如果压缩后图片是否大于maxKB,大于继续压缩
                if (options < 11) {
                    break;
                }
                baos.reset();//重置baos即清空baosptions%，把压缩后的数据存放到baos中
                options -= 10;//每次都减少10
                bitmap.compress(format, options, baos);//这里压缩
            }
            ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
            log.d("质量压缩");
            Bitmap newBitmap = BitmapFactory.decodeStream(isBm, null, null);
            if (newBitmap != null) {
                if (bitmap != newBitmap) {
                    bitmap.recycle();
                }
                bitmap = newBitmap;
            }
        }
        //endregion

        //region 旋转有角度的图片
        if (degree != 0) {
            log.d("旋转有角度的图片");
            Matrix matrix = new Matrix();
            matrix.postRotate(degree);
            Bitmap newBitmap = null;
            try {
                // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
                newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
            if (newBitmap != null) {
                if (bitmap != newBitmap) {
                    bitmap.recycle();
                }
                bitmap = newBitmap;
            }
        }
        //endregion

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(newPath);
            bitmap.compress(format, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            // 如果图片还没有回收，强制回收
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
                System.gc();
            }
            log.d("bitmap写入本地");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return newPath;
    }
}