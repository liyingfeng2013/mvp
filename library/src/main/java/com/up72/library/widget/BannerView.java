package com.up72.library.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.up72.library.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * 轮播图
 * Created by liyingfeng on 2016/6/14.
 */
public class BannerView extends FrameLayout implements ViewPager.OnPageChangeListener {
    private static final int INTERVAL = 3000;
    private ViewPager mViewPager;
    private LinearLayout mLayDots;
    private View currentDot;
    private Drawable defaultDrawable = null;
    private BannerAdapter mAdapter;
    private OnClickListener mOnClickListener;
    private float lastX, lastY;

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(1);
            mHandler.postDelayed(this, INTERVAL);
        }
    };

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                    break;
            }
            return false;
        }
    });

    public BannerView(Context context) {
        super(context);
        init();
    }

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mViewPager = new ViewPager(getContext());
        LayoutParams vpParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mViewPager.setLayoutParams(vpParams);
        addView(mViewPager, vpParams);
        mViewPager.setAdapter(mAdapter = new BannerAdapter());
        mViewPager.addOnPageChangeListener(this);

        mLayDots = new LinearLayout(getContext());
        mLayDots.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams dotsParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        dotsParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        addView(mLayDots, dotsParams);
    }

    /**
     * 设置数据源
     *
     * @param args String[]网络图片的地址数组
     */
    public void setData(String[] args) {
        if (mAdapter == null) {
            mViewPager.setAdapter(mAdapter = new BannerAdapter());
        }
        List<String> list = null;
        if (args != null) {
            list = new ArrayList<>();
            list.addAll(Arrays.asList(args));
        }
        mLayDots.removeAllViews();
        mAdapter.replaceAll(list);
        if (list != null && list.size() > 1) {
            int size = mAdapter.getRealCount();
            int margins = 8;
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(margins, margins, margins, margins);
            for (int i = 0; i < size; i++) {
                ImageView imageView = new ImageView(getContext());
                imageView.setLayoutParams(layoutParams);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setImageResource(R.drawable.bg_dot_circle);
                mLayDots.addView(imageView);
            }

            mHandler.removeCallbacks(mRunnable);
            mViewPager.setCurrentItem(list.size() * 100, false);
            mHandler.postDelayed(mRunnable, INTERVAL);
            currentDot = mLayDots.getChildAt(mViewPager.getCurrentItem() % mLayDots.getChildCount());
            currentDot.setSelected(true);
        }
    }

    /**
     * 设置默认的图片
     *
     * @param defaultRes 默认图片
     */
    public void setDefaultRes(@DrawableRes int defaultRes) {
        this.defaultDrawable = ContextCompat.getDrawable(getContext(), defaultRes);
    }

    /**
     * 设置默认的图片
     *
     * @param defaultDrawable 默认图片
     */
    public void setDefaultDrawable(@NonNull Drawable defaultDrawable) {
        this.defaultDrawable = defaultDrawable;
    }

    /**
     * 设置点的位置
     *
     * @param gravity Gravity
     */
    public void setDotsGravity(int gravity) {
        if (mLayDots != null) {
            LayoutParams layoutParams = (LayoutParams) mLayDots.getLayoutParams();
            if (layoutParams == null) {
                layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            }
            layoutParams.gravity = gravity;
            mLayDots.setLayoutParams(layoutParams);
        }
    }

    /**
     * 获取当前点击图片的位置
     *
     * @return position
     */
    public int getCurrentPosition() {
        if (mViewPager != null && mAdapter != null && mAdapter.getRealCount() > 0) {
            return mViewPager.getCurrentItem() % mAdapter.getRealCount();
        }
        return -1;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        this.mOnClickListener = l;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        position %= mLayDots.getChildCount();
        if (currentDot != null) {
            currentDot.setSelected(false);
        }
        currentDot = mLayDots.getChildAt(position);
        currentDot.setSelected(true);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        mHandler.removeCallbacks(mRunnable);
        if (hasWindowFocus && mAdapter != null && mAdapter.getCount() > 1) {
            mHandler.postDelayed(mRunnable, INTERVAL);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mHandler != null && mRunnable != null) {
            mHandler.removeCallbacks(mRunnable);
        }
        super.onDetachedFromWindow();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = ev.getX();
                lastY = ev.getY();
                mHandler.removeCallbacks(mRunnable);
                break;
            case MotionEvent.ACTION_UP:
                if (Math.abs(ev.getX() - lastX) < 10 && Math.abs(ev.getY() - lastY) < 10) {
                    if (mOnClickListener != null) {
                        mOnClickListener.onClick(this);
                    }
                }
                if (mAdapter != null && mAdapter.getCount() > 1) {
                    mHandler.postDelayed(mRunnable, INTERVAL);
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private final class BannerAdapter extends PagerAdapter {
        private HashMap<Integer, ImageView> hm = new HashMap<>();// 放置图片的列表
        private List<String> dataList = new ArrayList<>();
        private int realCount = 0;

        private void replaceAll(List<String> list) {
            realCount = 0;
            dataList.clear();
            hm.clear();
            if (list != null) {
                realCount = list.size();
                if (realCount == 2) {
                    list.add(list.get(0));
                    list.add(list.get(1));
                }
                dataList.addAll(list);
            }
            notifyDataSetChanged();
        }

        private int getRealCount() {
            return realCount;
        }

        @Override
        public int getCount() {
            return dataList == null ? 0 : ((dataList.size() == 0 || dataList.size() == 1) ? dataList.size() : Integer.MAX_VALUE);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            position %= dataList.size();
            if (position < 0) {
                position = dataList.size() + position;
            }
            ImageView imageView = null;
            if (hm.containsKey(position)) {
                imageView = hm.get(position);
            }
            if (imageView == null) {
                imageView = new ImageView(container.getContext());
                imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                if (defaultDrawable != null) {
                    Glide.with(container.getContext()).load(dataList.get(position)).placeholder(defaultDrawable).error(defaultDrawable).crossFade().into(imageView);
                } else {
                    Glide.with(container.getContext()).load(dataList.get(position)).crossFade().into(imageView);
                }
                hm.put(position, imageView);
            }
            if (imageView.getParent() != null) {
                ViewGroup parent = (ViewGroup) imageView.getParent();
                parent.removeView(imageView);
            }
            container.addView(imageView);
            return imageView;
        }
    }
}