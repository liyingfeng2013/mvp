package com.up72.mvp.widget.refresh;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.up72.library.refresh.AnimationCallback;
import com.up72.library.refresh.IPull;
import com.up72.library.refresh.IRefreshHead;
import com.up72.mvp.R;

/**
 * 自定义刷新头
 * Created by liyingfeng on 2016/11/1.
 */
public class HeadView implements IRefreshHead {
    private View view;
    private TextView tvRefresh;
    private int height;
    private boolean isRefreshing = false;
    private boolean isReleased = false;
    private IPull iPull;

    @Override
    public boolean isRefreshing() {
        return isRefreshing;
    }

    @Override
    public void refreshImmediately() {

    }

    @Override
    public void autoRefresh() {

    }

    private void reset() {
        isRefreshing = false;
        tvRefresh.setText(R.string.refresh_normal);
    }

    @Override
    public View getTargetView(ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_refresh, parent, false);
            tvRefresh = (TextView) view.findViewById(R.id.tvRefresh);
            reset();
        }
        return view;
    }

    @Override
    public void onPull(float scrollY, boolean enable) {
        if (height <= 0) {
            height = view.getMeasuredHeight();
        }
        if (height < 10) {
            return;
        }
        if (!enable || isRefreshing) {
            return;
        }
        if (scrollY <= height && isReleased) {
            isReleased = false;
            tvRefresh.setText(R.string.refresh_normal);
        } else if (scrollY > height && !isReleased) {
            isReleased = true;
            tvRefresh.setText(R.string.refresh_release);
        }
    }

    @Override
    public void onFingerUp(float scrollY) {
        if (height <= 0) {
            height = view.getMeasuredHeight();
        }
        if (height < 10) {
            return;
        }
        if (isRefreshing) {
            return;
        }
        if (scrollY <= height) {
            iPull.animToStartPosition(null);
        } else {
            isRefreshing = true;
            iPull.animToRightPosition(height, new AnimationCallback() {
                @Override
                public void onAnimationEnd() {
                    tvRefresh.setText(R.string.refreshing);
                    iPull.pullDownCallback();
                }
            });
        }
    }

    @Override
    public void detach() {
    }

    @Override
    public void pullLayout(IPull iPull) {
        this.iPull = iPull;
    }

    @Override
    public void finishPull(boolean isBeingDragged) {
        if (isRefreshing) {
            tvRefresh.setText(R.string.refresh_done);
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    iPull.animToStartPosition(new AnimationCallback() {
                        @Override
                        public void onAnimationEnd() {
                            reset();
                        }
                    });
                }
            }, 1000);
        }
    }

    @Override
    public void finishPull(boolean isBeingDragged, CharSequence msg, boolean result) {
        finishPull(isBeingDragged);
    }
}