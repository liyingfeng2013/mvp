package com.up72.mvp.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import com.up72.library.picture.Picture;
import com.up72.library.utils.SystemUtils;
import com.up72.mvp.R;
import com.up72.mvp.base.BaseActivity;
import com.up72.mvp.model.VersionModel;
import com.up72.mvp.task.Callback;
import com.up72.mvp.task.Task;
import com.up72.mvp.ui.album.AlbumActivity;

import java.io.File;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MainActivity extends BaseActivity {

//    private TextView tvContent;

    @Override
    protected int getContentView() {
        return R.layout.main_act;
    }

    @Override
    protected void initView() {
//        this.tvContent = (TextView) findViewById(R.id.tv_content);
    }

    @Override
    protected void initListener() {
    }

    @Override
    protected void initData() {
//        RouteManager.getInstance().toLogin(this);
        Intent intent = new Intent(this, AlbumActivity.class);
        startActivity(intent);
//        Picture.of(this).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String path = Picture.getImagePath(requestCode, resultCode, data);
        if ("".equals(path)) {
        }
    }

    //上传文件
    private void uploadFile(@NonNull File file) {
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("originalName", "android")
                .addFormDataPart("Filedata", file.getName(), RequestBody.create(MultipartBody.FORM, file))
                .build();
        Task.create(UploadService.class).uploadImage(requestBody).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onSuccess(@Nullable Map<String, String> map) {
                if (map != null && map.containsKey("relativePath")) {
                    String url = map.get("relativePath");
                    //上传路径
                }
            }

            @Override
            public void onFailure(@NonNull String error) {

            }
        });
    }

    //检查版本更新
    private void checkVersion() {
        Task.create(VersionService.class).getVersionInfo(0).enqueue(new Callback<VersionModel>() {
            @Override
            public void onSuccess(@Nullable final VersionModel versionModel) {
                if (versionModel != null && versionModel.getCode() > SystemUtils.getVersionCode(MainActivity.this)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setCancelable(!versionModel.isMust());
                    builder.setTitle("发现新版本：" + versionModel.getName() + " （" + versionModel.getSize() + "）");
                    builder.setMessage(versionModel.getContent());
                    builder.setPositiveButton("去下载更新", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            SystemUtils.openBrowser(MainActivity.this, versionModel.getDownloadUrl());
                            if (versionModel.isMust()) {
                                MainActivity.this.finish();
                            }
                        }
                    });
                    builder.setNegativeButton(versionModel.isMust() ? "退出" : "取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (versionModel.isMust()) {
                                MainActivity.this.finish();
                            }
                        }
                    });
                    builder.show();
                }
            }

            @Override
            public void onFailure(@NonNull String error) {

            }
        });
    }
}