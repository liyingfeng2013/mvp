package com.up72.mvp.ui.album;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.up72.mvp.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * 图片查看器
 * Created by LYF on 2017/1/16.
 */
public class PhotoViewerActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {
    public static final String KEY_SOURCE_PATHS = "KEY_SOURCE_PATHS";
    public static final String KEY_SELECT_PATHS = "KEY_SELECT_PATHS";
    public static final String KEY_INDEX = "KEY_INDEX";
    public static final String KEY_MAX = "KEY_MAX";
    private TextView tvTitle;
    private TextView tvCheck;
    private ViewPagerFixed vpContent;
    private PhotoViewerAdapter adapter;

    private List<String> sourcePaths = new ArrayList<>();
    private List<String> selectPaths = new ArrayList<>();

    private int max = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_viewer);
        this.tvTitle = (TextView) findViewById(R.id.tv_title);
        this.tvCheck = (TextView) findViewById(R.id.tv_check);


        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.fl_content);
        vpContent = new ViewPagerFixed(this);
        frameLayout.addView(vpContent);

        vpContent.addOnPageChangeListener(this);
        findViewById(R.id.iv_back).setOnClickListener(this);
        this.tvCheck.setOnClickListener(this);

        String title = "图片";
        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle.containsKey(KEY_SOURCE_PATHS)) {
                String[] sourceArray = bundle.getStringArray(KEY_SOURCE_PATHS);
                if (sourceArray != null) {
                    sourcePaths.addAll(Arrays.asList(sourceArray));
                }
                String[] selectArray = bundle.getStringArray(KEY_SELECT_PATHS);
                if (selectArray != null) {
                    selectPaths.addAll(Arrays.asList(selectArray));
                }

                if (bundle.containsKey(KEY_MAX)) {
                    max = bundle.getInt(KEY_MAX, 1);
                }

                int index = 0;
                if (bundle.containsKey(KEY_INDEX)) {
                    index = bundle.getInt(KEY_INDEX, 0);
                }
                vpContent.setAdapter(adapter = new PhotoViewerAdapter(this, sourceArray));
                if (index > -1 && index < adapter.getCount()) {
                    vpContent.setCurrentItem(index, false);
                    if (index < sourcePaths.size()) {
                        tvCheck.setSelected(selectPaths.contains(sourcePaths.get(index)));
                    }
                }
                title = String.format(Locale.getDefault(), "图片 (%d/%d)", index + 1, adapter.getCount());
            }
        }
        vpContent.setOffscreenPageLimit(2);
        vpContent.setPageTransformer(false, new ZoomInTransform());
        tvTitle.setText(title);
        tvCheck.setText(String.format(Locale.getDefault(), "选择(%d/%d)", selectPaths.size(), max));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        tvTitle.setText(String.format(Locale.getDefault(), "图片 (%d/%d)", position + 1, adapter.getCount()));
        if (position > -1 && position < sourcePaths.size()) {
            tvCheck.setSelected(selectPaths.contains(sourcePaths.get(position)));
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_check:
                int currentItem = vpContent.getCurrentItem();
                if (currentItem > -1 && currentItem < sourcePaths.size()) {
                    boolean b = !v.isSelected();
                    String s = sourcePaths.get(currentItem);
                    if (b) {
                        if (max == 1) {
                            selectPaths.clear();
                            selectPaths.add(s);
                        } else if (selectPaths.size() >= max) {
                            break;
                        } else if (!selectPaths.contains(s)) {
                            selectPaths.add(s);
                        }
                    } else {
                        selectPaths.remove(s);
                    }
                    v.setSelected(b);
                    tvCheck.setText(String.format(Locale.getDefault(), "选择(%d/%d)", selectPaths.size(), max));
                }
                break;
        }
    }

    @Override
    public void finish() {
        Bundle bundle = new Bundle();
        bundle.putStringArray(PhotoViewerActivity.KEY_SELECT_PATHS, selectPaths.toArray(new String[selectPaths.size()]));
        setResult(RESULT_OK, new Intent().putExtras(bundle));
        super.finish();
    }

    private class ZoomInTransform implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);
            } else if (position <= 1) //a页滑动至b页 ； a页从 0.0 -1 ；b页从1 ~ 0.0
            { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vMargin = pageHeight * (1 - scaleFactor) / 2;
                float hMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(hMargin - vMargin / 2);
                } else {
                    view.setTranslationX(-hMargin + vMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE)
                        / (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }

    private class ViewPagerFixed extends android.support.v4.view.ViewPager {
        public ViewPagerFixed(Context context) {
            super(context);
        }

        public ViewPagerFixed(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public boolean onTouchEvent(MotionEvent ev) {
            try {
                return super.onTouchEvent(ev);
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            }
            return false;
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            try {
                return super.onInterceptTouchEvent(ev);
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }
}