package com.up72.mvp.widget.refresh;

import android.content.Context;
import android.util.AttributeSet;

import com.up72.library.refresh.BaseRefreshLayout;
import com.up72.library.refresh.IRefreshHead;

/**
 * 说点什么
 * Created by LYF on 2016/11/1.
 */
public class RefreshLayout extends BaseRefreshLayout {
    private FooterView footer;

    public RefreshLayout(Context context) {
        super(context);
        init();
    }

    public RefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        attachHeadView(new HeadView());
        footer = new FooterView();
        attachFooterView(footer);
    }

    public void setRefreshView(IRefreshHead refreshView) {
        if (refreshView != null) {
            attachHeadView(refreshView);
        }
    }

    @Override
    public void finishPull() {
        if (footer != null && isRefresh()) {
            footer.setNoMore(false);
        }
        super.finishPull();
    }

    public void finishPullNoMore() {
        super.finishPull(null, false);
    }
}