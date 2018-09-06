package com.up72.library.picture;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 图片选择
 * Created by LYF on 2016/6/17.
 */
public class Picture {
    /**
     * requestCode
     */
    private static final int REQUEST_CODE = 16154;
    /**
     * 文件的绝对路径
     */
    static final String IMAGE_PATH = "IMAGE_PATH";
    private Context mContext;
    private Intent mIntent;

    interface Extra {
        String DEFAULT_WIDTH = "DEFAULT_WIDTH";
        String DEFAULT_HEIGHT = "DEFAULT_HEIGHT";
        String MAX_WIDTH = "MAX_WIDTH";
        String MAX_HEIGHT = "MAX_HEIGHT";
        String IS_CROP = "IS_CROP";
        String MAX_KB = "MAX_KB";
        String OPEN_TYPE = "OPEN_TYPE";
    }

    public static Picture of(Context context) {
        return new Picture(context);
    }

    private Picture() {
    }

    private Picture(Context context) {
        this.mContext = context;
        this.mIntent = new Intent();
    }

    /**
     * 裁剪
     */
    public Picture crop() {
        this.mIntent.putExtra(Extra.IS_CROP, true);
        return this;
    }

    /**
     * 设置裁剪比例，例：1:2 —— withAspect(1,2)
     * </br>
     * 需先调用crop才有效
     *
     * @param width  宽
     * @param height 高
     */
    public Picture withAspect(int width, int height) {
        this.mIntent.putExtra(Extra.DEFAULT_WIDTH, width);
        this.mIntent.putExtra(Extra.DEFAULT_HEIGHT, height);
        return this;
    }

    /**
     * 设置正方形裁剪
     * </br>
     * 需先调用crop才有效
     */
    public Picture asSquare() {
        this.mIntent.putExtra(Extra.DEFAULT_WIDTH, 1);
        this.mIntent.putExtra(Extra.DEFAULT_HEIGHT, 1);
        return this;
    }

    /**
     * 设置裁剪图片后，图片的最大宽高
     *
     * @param width  最大宽
     * @param height 最大高
     */
    public Picture withMaxSize(int width, int height) {
        this.mIntent.putExtra(Extra.MAX_WIDTH, width);
        this.mIntent.putExtra(Extra.MAX_HEIGHT, height);
        return this;
    }

    /**
     * 设置图片最大大小
     *
     * @param maxKB 最大大小，单位KB
     */
    public Picture withMaxKB(long maxKB) {
        this.mIntent.putExtra(Extra.MAX_KB, maxKB);
        return this;
    }

    public void start() {
        start(Type.DEFAULT);
    }

    public void start(@Type int type) {
        if (mContext instanceof Activity) {
            mIntent.putExtra(Extra.OPEN_TYPE, type);
            Activity activity = (Activity) mContext;
            mIntent.setClass(activity, PictureActivity.class);
            activity.startActivityForResult(mIntent, REQUEST_CODE);
            activity.overridePendingTransition(0, 0);
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({Type.DEFAULT, Type.CAMERA, Type.PHOTO})
    public @interface Type {
        /**
         * 默认，手动选择
         */
        int DEFAULT = 0;
        /**
         * 相机
         */
        int CAMERA = 1;
        /**
         * 相册
         */
        int PHOTO = 2;
    }

    @Nullable
    public static String getImagePath(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getExtras() != null && data.getExtras().containsKey(Picture.IMAGE_PATH)) {
                String path = data.getExtras().getString(Picture.IMAGE_PATH, "");
                if (path != null && path.length() > 0) {
                    return path;
                }
            }
        }
        return null;
    }
}