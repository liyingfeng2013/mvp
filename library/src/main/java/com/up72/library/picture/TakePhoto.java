package com.up72.library.picture;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * 相机拍照
 * Created by LYF on 17.3.7.
 */
public class TakePhoto {
    private static final int REQUEST_CAMERA = 914;
    //相机
    private void camera(Context context) {
       /* Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempOldImg));
        startActivityForResult(intent, REQUEST_CAMERA);
        overridePendingTransition(0, 0);*/
    }
}