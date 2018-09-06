package com.up72.mvp.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.up72.library.utils.Log;
import com.up72.mvp.R;
import com.up72.mvp.event.LoginEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * BaseFragment
 * Created by LYF on 2016/12/15.
 */
public abstract class BaseFragment extends Fragment {

    protected Log log = new Log(getClass());

    @Nullable
    @Override
    public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getContentView(), container, false);
    }

    @Override
    public final void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
        initView(view);
        initListener(view);
        initData(view);
    }

    @LayoutRes
    protected abstract int getContentView();

    protected abstract void initView(@NonNull View view);

    protected abstract void initListener(@NonNull View view);

    protected abstract void initData(@NonNull View view);

    protected final void initTitle(String strContent) {
        this.initTitle(0, null, strContent, null, 0);
    }

    protected final void initTitle(int resLeft, String strContent) {
        this.initTitle(resLeft, null, strContent, null, 0);
    }

    protected final void initTitle(int resLeft, String strContent, String strRight) {
        this.initTitle(resLeft, null, strContent, strRight, 0);
    }

    protected final void initTitle(int resLeft, String strContent, int resRight) {
        this.initTitle(resLeft, null, strContent, null, resRight);
    }

    protected final void initTitle(String strLeft, String strContent, String strRight) {
        this.initTitle(0, strLeft, strContent, strRight, 0);
    }

    protected final void initTitle(int resLeft, String strLeft, String strContent, String strRight, int resRight) {
        View view = getView();
        if (view == null) {
            return;
        }
        ImageView ivTitleLeft = (ImageView) view.findViewById(R.id.iv_title_left);
        ImageView ivTitleRight = (ImageView) view.findViewById(R.id.iv_title_right);
        TextView tvTitleLeft = (TextView) view.findViewById(R.id.tv_title_left);
        TextView tvTitleContent = (TextView) view.findViewById(R.id.tv_title_content);
        TextView tvTitleRight = (TextView) view.findViewById(R.id.tv_title_right);

        if (resLeft == 0) {
            ivTitleLeft.setVisibility(View.GONE);
        } else {
            ivTitleLeft.setVisibility(View.VISIBLE);
            ivTitleLeft.setImageResource(resLeft);
            ivTitleLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickTitleLeft(v);
                }
            });
        }

        if (resRight == 0) {
            ivTitleRight.setVisibility(View.GONE);
        } else {
            ivTitleRight.setVisibility(View.VISIBLE);
            ivTitleRight.setImageResource(resRight);
            ivTitleRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickTitleRight(v);
                }
            });
        }
        if (strLeft == null) {
            tvTitleLeft.setVisibility(View.GONE);
        } else {
            tvTitleLeft.setVisibility(View.VISIBLE);
            tvTitleLeft.setText(strLeft);
            tvTitleLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickTitleLeft(v);
                }
            });
        }
        if (strContent == null) {
            tvTitleContent.setVisibility(View.GONE);
        } else {
            tvTitleContent.setVisibility(View.VISIBLE);
            tvTitleContent.setText(strContent);
        }
        if (strRight == null) {
            tvTitleRight.setVisibility(View.GONE);
        } else {
            tvTitleRight.setVisibility(View.VISIBLE);
            tvTitleRight.setText(strRight);
            tvTitleRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickTitleRight(v);
                }
            });
        }
    }

    protected void onClickTitleLeft(View v) {
    }

    protected void onClickTitleRight(View v) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginEvent(LoginEvent event) {
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            Glide.with(this).pauseRequests();
        } else {
            Glide.with(this).resumeRequests();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Glide.with(this).resumeRequests();
    }

    @Override
    public void onPause() {
        super.onPause();
        Glide.with(this).pauseRequests();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}