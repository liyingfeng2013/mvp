package com.up72.mvp.ui.album;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.up72.library.widget.ZoomImageView;
import com.up72.mvp.R;

/**
 * 图片查看器适配器
 * Created by LYF on 2017/1/16.
 */
class PhotoViewerAdapter extends PagerAdapter {
    private Context context;
    private String[] paths;

    PhotoViewerAdapter(Context context, String[] paths) {
        this.context = context;
        this.paths = paths;
    }

    @Override
    public int getCount() {
        return paths == null ? 0 : paths.length;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ZoomImageView imageView = new ZoomImageView(context);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        Glide.with(container.getContext()).load(this.paths[position]).placeholder(R.drawable.ic_photo).crossFade().into(imageView);
        if (imageView.getParent() != null) {
            ViewGroup parent = (ViewGroup) imageView.getParent();
            parent.removeView(imageView);
        }
        container.addView(imageView);
        return imageView;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}