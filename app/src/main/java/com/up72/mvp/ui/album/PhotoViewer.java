package com.up72.mvp.ui.album;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * 图片查看器
 * Created by LYF on 17.3.31.
 */
public class PhotoViewer {
    /**
     * requestCode
     */
    private static final int REQUEST_CODE = 16154;
    private Context mContext;
    private Intent mIntent;

    public static PhotoViewer of(Context context) {
        return new PhotoViewer(context);
    }

    private PhotoViewer(Context context) {
        this.mContext = context;
        this.mIntent = new Intent();
    }

    /**
     * 设置数据源
     *
     * @param sourcePaths 要显示的图片网络路径或本地文件路径
     * @return PhotoViewer
     */
    public PhotoViewer setSource(String[] sourcePaths) {
        return setSource(sourcePaths, 0);
    }

    /**
     * 设置数据源
     *
     * @param sourcePaths 要显示的图片网络路径或本地文件路径
     * @param position    [0,sourcePaths.size()) 默认显示图片的索引
     * @return PhotoViewer
     */
    public PhotoViewer setSource(String[] sourcePaths, int position) {
        mIntent.putExtra(PhotoViewerActivity.KEY_SOURCE_PATHS, sourcePaths);
        mIntent.putExtra(PhotoViewerActivity.KEY_INDEX, position);
        return this;
    }

    /**
     * 设置最大可选图片数量
     *
     * @param max [1,max) 最大选择数量
     * @return PhotoViewer
     */
    public PhotoViewer setMax(int max) {
        mIntent.putExtra(PhotoViewerActivity.KEY_MAX, max < 1 ? 1 : max);
        return this;
    }

    /**
     * 设置默认选中的图片
     *
     * @param selectPaths 要选中的图片网络路径或本地文件路径
     * @return PhotoViewer
     */
    public PhotoViewer setSelect(String[] selectPaths) {

        return this;
    }

    public void start() {
        if (mContext instanceof Activity) {
            Activity activity = (Activity) mContext;
            mIntent.setClass(activity, PhotoViewerActivity.class);
            activity.startActivityForResult(mIntent, REQUEST_CODE);
            activity.overridePendingTransition(0, 0);
        }
    }

    /**
     * 获得返回的图片
     *
     * @return 选中的图片
     */
    @Nullable
    public static String[] getSelectPaths(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getExtras() != null && data.getExtras().containsKey(PhotoViewerActivity.KEY_SELECT_PATHS)) {
                String[] paths = data.getExtras().getStringArray(PhotoViewerActivity.KEY_SELECT_PATHS);
                if (paths != null) {
                    return paths;
                }
            }
        }
        return null;
    }
}