package com.up72.mvp.widget.refresh;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.up72.library.refresh.AnimationCallback;
import com.up72.library.refresh.ILoadFooter;
import com.up72.library.refresh.IPull;
import com.up72.mvp.R;

/**
 * 自定义加载底部
 * Created by liyingfeng on 2016/11/1.
 */
public class FooterView implements ILoadFooter {
    private View view;
    private TextView tvFooter;
    private int height;
    private boolean isLoading;
    private boolean release = true;
    private IPull iPull;
    private boolean isNoMore = false;

    public void setNoMore(boolean noMore) {
        this.isNoMore = noMore;
    }

    private void reset() {
        isLoading = false;
        if (isNoMore) {
            tvFooter.setText(R.string.load_no_more);
        } else {
            tvFooter.setText(R.string.load_up);
        }
    }

    @Override
    public boolean isLoading() {
        return isLoading;
    }

    @Override
    public View getTargetView(ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_load_more, parent, false);
            tvFooter = (TextView) view.findViewById(R.id.tvLoadMore);
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
        if (!enable || isLoading || isNoMore) {
            return;
        }
        int y = (int) -scrollY;
        if (y <= height && release) {
            release = false;
            tvFooter.setText(R.string.load_up);
        } else if (y > height && !release) {
            release = true;
            tvFooter.setText(R.string.load_release);
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
        if (isLoading) {
            return;
        }
        int y = (int) -scrollY;
        if (y <= height) {
            iPull.animToStartPosition(new AnimationCallback() {
                @Override
                public void onAnimationEnd() {
                    reset();
                }
            });
        } else {
            isLoading = true;
            iPull.animToRightPosition(-height, new AnimationCallback() {
                @Override
                public void onAnimationEnd() {
                    if (isNoMore) {
                        finishPull(false);
                    } else {
                        tvFooter.setText(R.string.load_more);
                        iPull.pullUpCallback();
                    }
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
        if (isLoading) {
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    iPull.animToStartPosition(new AnimationCallback() {
                        @Override
                        public void onAnimationStart() {
                            reset();
                        }
                    });
                }
            }, 1000);
        }
    }

    @Override
    public void finishPull(boolean isBeingDragged, CharSequence msg, boolean result) {
        this.isNoMore = !result;
        finishPull(isBeingDragged);
    }
}
