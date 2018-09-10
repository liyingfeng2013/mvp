package com.xh.common.util.pinned;

import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

public class PinnedItemDecoration extends RecyclerView.ItemDecoration {
    private SparseArray<View> cache = new SparseArray<>();

    /**
     * 把要固定的View绘制在上层
     */
    @SuppressWarnings("unchecked")
    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        //确保是PinnedHeaderAdapter的adapter,确保有View
        if (parent.getAdapter() instanceof PinnedAdapter && parent.getChildCount() > 0) {
            PinnedAdapter adapter = (PinnedAdapter) parent.getAdapter();
            //找到要固定的pin view
            View firstView = parent.getChildAt(0);
            int firstAdapterPosition = parent.getChildAdapterPosition(firstView);
            int pinnedHeaderPosition = getPinnedHeaderViewPosition(firstAdapterPosition, adapter);
            if (pinnedHeaderPosition != -1) {
                //要固定的view
                View pinnedHeaderView = cache.get(pinnedHeaderPosition);
                if (pinnedHeaderView == null) {
                    RecyclerView.ViewHolder pinnedHeaderViewHolder = adapter.onCreateViewHolder(parent, adapter.getItemViewType(pinnedHeaderPosition));
                    adapter.onBindViewHolder(pinnedHeaderViewHolder, pinnedHeaderPosition);
                    pinnedHeaderView = pinnedHeaderViewHolder.itemView;
                    cache.put(pinnedHeaderPosition, pinnedHeaderView);
                }
                ensurePinnedHeaderViewLayout(pinnedHeaderView, parent);
                int sectionPinOffset = 0;
                for (int index = 0; index < parent.getChildCount(); index++) {
                    if (adapter.isPinnedPosition(parent.getChildAdapterPosition(parent.getChildAt(index)))) {
                        View sectionView = parent.getChildAt(index);
                        int sectionTop = sectionView.getTop();
                        int pinViewHeight = pinnedHeaderView.getHeight();
                        if (sectionTop < pinViewHeight && sectionTop > 0) {
                            sectionPinOffset = sectionTop - pinViewHeight;
                        }
                    }
                }
                int saveCount = c.save();
                c.translate(0, sectionPinOffset);
                c.clipRect(0, 0, parent.getWidth(), pinnedHeaderView.getMeasuredHeight());
                pinnedHeaderView.draw(c);
                c.restoreToCount(saveCount);
            }
        }
    }

    /**
     * 根据第一个可见的adapter的位置去获取临近的一个要固定的position的位置
     *
     * @param adapterFirstVisible 第一个可见的adapter的位置
     * @return -1：未找到 >=0 找到位置
     */
    private int getPinnedHeaderViewPosition(int adapterFirstVisible, @Nullable PinnedAdapter adapter) {
        if (adapter == null) {
            return -1;
        }
        for (int index = adapterFirstVisible; index >= 0; index--) {
            if (adapter.isPinnedPosition(index)) {
                return index;
            }
        }
        return -1;
    }

    private void ensurePinnedHeaderViewLayout(View pinView, RecyclerView recyclerView) {
        if (pinView.isLayoutRequested()) {
            //用的是RecyclerView的宽度测量，和RecyclerView的宽度一样
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) pinView.getLayoutParams();
            if (layoutParams == null) {
                throw new NullPointerException("PinnedHeaderItemDecoration");
            }
            int widthSpec = View.MeasureSpec.makeMeasureSpec(
                    recyclerView.getMeasuredWidth() - layoutParams.leftMargin - layoutParams.rightMargin, View.MeasureSpec.EXACTLY);

            int heightSpec;
            if (layoutParams.height > 0) {
                heightSpec = View.MeasureSpec.makeMeasureSpec(layoutParams.height, View.MeasureSpec.EXACTLY);
            } else {
                heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            }
            pinView.measure(widthSpec, heightSpec);
            pinView.layout(0, 0, pinView.getMeasuredWidth(), pinView.getMeasuredHeight());
        }
    }
}