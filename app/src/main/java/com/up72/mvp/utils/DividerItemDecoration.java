package com.up72.mvp.utils;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 万能分隔线
 * Created by liyingfeng on 2016/9/9.
 */
public class DividerItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public DividerItemDecoration() {
        this(8);
    }

    public DividerItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        int childCount = parent.getAdapter().getItemCount();
        if (parent.getLayoutManager() instanceof GridLayoutManager) {
            outRect.top = space;
            GridLayoutManager gridLayoutManager = (GridLayoutManager) parent.getLayoutManager();
            int spanCount = gridLayoutManager.getSpanCount();
            int span = gridLayoutManager.getSpanSizeLookup().getSpanSize(position);
            int spanIndex = gridLayoutManager.getSpanSizeLookup().getSpanIndex(position, spanCount);
            // FIXME: 2016/9/9 最长的一条默认不添加左右边距，如果需要去掉此if语句
            if (span < spanCount) {
                if (position < spanCount) {
                    outRect.top = space;
                }
                if (spanIndex == 0) {
                    outRect.left = space;
                    outRect.right = space / 2;
                } else if (spanIndex + span >= spanCount) {
                    outRect.left = space / 2;
                    outRect.right = space;
                } else {
                    outRect.left = space / 2;
                    outRect.right = space / 2;
                }
            }
        } else if (parent.getLayoutManager() instanceof LinearLayoutManager) {
            outRect.bottom = space;
            if (position == 0) {
                outRect.top = space;
            }
        }
    }
}