package com.up72.mvp.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.up72.library.utils.Log;

/**
 * 自动加载更多的RecyclerView
 * Created by LYF on 2016/9/23.
 */
public class LoadMoreRecyclerView extends RecyclerView {
    private static final int TYPE_LOAD_MORE = 1000001;
    private static final int TYPE_EMPTY_VIEW = 1000002;

    private boolean isRegisterDataObserver = false;
    private View emptyView;
    private TextView footerView;
    private final AdapterDataObserver dataObserver = new DataObserver();
    private WrapAdapter wrapAdapter;
    private OnLoadMoreListener onLoadMoreListener;

    private boolean isHasFooter = true;
    private boolean loading = false;
    private boolean hasMore = false;

    private Log log = new Log(getClass());

    public LoadMoreRecyclerView(Context context) {
        super(context);
        init();
    }

    public LoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        isRegisterDataObserver = false;
        setOverScrollMode(OVER_SCROLL_NEVER);
        reset();
    }

    private TextView getFooter() {
        if (footerView == null) {
            footerView = new TextView(getContext());
            footerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            footerView.setGravity(Gravity.CENTER);
            footerView.setPadding(16, 16, 16, 16);
            footerView.setVisibility(GONE);
        }
        return footerView;
    }

    public void setOnLoadMoreListener(@Nullable OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void onComplete(boolean hasMore) {
        this.hasMore = hasMore;
        this.loading = false;
        if (onLoadMoreListener != null && isHasFooter && wrapAdapter != null && wrapAdapter.getItemCount() > 0) {
            getFooter().setText(hasMore ? "正在加载" : "没有更多");
        }
    }

    public void reset() {
        this.loading = false;
        this.hasMore = true;
    }

    @Override
    public void onScrolled(int dx, int dy) {
        if (dy > 0) {
            if (footerView != null && footerView.getVisibility() != VISIBLE) {
                footerView.setVisibility(VISIBLE);
            }
        }
    }

    @Override
    public void onScrollStateChanged(int state) {
        if (state == RecyclerView.SCROLL_STATE_IDLE && onLoadMoreListener != null && hasMore) {
            LayoutManager layoutManager = getLayoutManager();
            int lastVisibleItemPosition = getLastVisibleItemPosition(getLayoutManager());
            if (!loading && layoutManager.getChildCount() > 0 && lastVisibleItemPosition >= layoutManager.getItemCount() - 1 && layoutManager.getItemCount() > layoutManager.getChildCount()) {
                loading = true;
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onLoadMoreListener.onLoadMore();
                    }
                }, 500);
            }
        }
    }

    private int getLastVisibleItemPosition(LayoutManager layoutManager) {
        int lastVisibleItemPosition = -1;
        if (layoutManager != null) {
            if (layoutManager instanceof GridLayoutManager) {
                lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                int[] into = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
                ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(into);
                lastVisibleItemPosition = into[0];
                for (int index : into) {
                    if (lastVisibleItemPosition < index) {
                        lastVisibleItemPosition = index;
                    }
                }
            } else {
                lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            }
        }
        return lastVisibleItemPosition;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        wrapAdapter = new WrapAdapter(adapter);
        super.setAdapter(wrapAdapter);
        if (!isRegisterDataObserver) {
            adapter.registerAdapterDataObserver(dataObserver);
        }
        dataObserver.onChanged();
    }

    //region DataObserver
    private class DataObserver extends AdapterDataObserver {
        @Override
        public void onChanged() {
            if (wrapAdapter != null) {
                wrapAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            if (wrapAdapter != null) {
                wrapAdapter.notifyItemRangeChanged(positionStart, itemCount);
            }
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            if (wrapAdapter != null) {
                wrapAdapter.notifyItemRangeChanged(positionStart, itemCount, payload);
            }
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            if (wrapAdapter != null) {
                wrapAdapter.notifyItemRangeInserted(positionStart, itemCount);
            }
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            if (wrapAdapter != null) {
                wrapAdapter.notifyItemRangeRemoved(positionStart, itemCount);
            }
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            if (wrapAdapter != null) {
                wrapAdapter.notifyItemRangeRemoved(fromPosition, toPosition);
            }
        }
    }
    //endregion

    //region WrapAdapter
    private class WrapAdapter extends Adapter {

        private Adapter adapter;

        private WrapAdapter(@NonNull Adapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public int getItemViewType(int position) {
            if (onLoadMoreListener != null && isHasFooter && position == getItemCount() - 1) {
                return TYPE_LOAD_MORE;
            }
            return adapter.getItemViewType(position);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_LOAD_MORE) {
                return new LoadMoreViewHolder(getFooter());
            }
            return adapter.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (getItemViewType(position) != TYPE_LOAD_MORE) {
                adapter.onBindViewHolder(holder, position);
            }
        }

        @Override
        public int getItemCount() {
            return onLoadMoreListener == null || !isHasFooter ? adapter.getItemCount() : adapter.getItemCount() + 1;
        }

        @Override
        public void setHasStableIds(boolean hasStableIds) {
            if (adapter != null) {
                adapter.setHasStableIds(hasStableIds);
            }
        }

        @Override
        public long getItemId(int position) {
            if (adapter != null && position < adapter.getItemCount()) {
                return adapter.getItemId(position);
            }
            return -1;
        }

        @Override
        public void onViewRecycled(ViewHolder holder) {
            if (adapter != null) {
                adapter.onViewRecycled(holder);
            }
        }

        @Override
        public boolean onFailedToRecycleView(ViewHolder holder) {
            if (adapter != null) {
                return adapter.onFailedToRecycleView(holder);
            }
            return super.onFailedToRecycleView(holder);
        }

        @Override
        public void onViewAttachedToWindow(ViewHolder holder) {
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                int position = holder.getLayoutPosition();
                if (onLoadMoreListener != null && isHasFooter && position == getItemCount() - 1) {
                    StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                    p.setFullSpan(true);
                }
            }
            if (adapter != null) {
                adapter.onViewAttachedToWindow(holder);
            }
        }

        @Override
        public void onViewDetachedFromWindow(ViewHolder holder) {
            if (adapter != null) {
                adapter.onViewDetachedFromWindow(holder);
            }
        }

        @Override
        public void registerAdapterDataObserver(AdapterDataObserver observer) {
            isRegisterDataObserver = true;
            if (adapter != null) {
                adapter.registerAdapterDataObserver(observer);
            }
        }

        @Override
        public void unregisterAdapterDataObserver(AdapterDataObserver observer) {
            isRegisterDataObserver = false;
            if (adapter != null) {
                adapter.unregisterAdapterDataObserver(observer);
            }
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            if (adapter != null) {
                adapter.onAttachedToRecyclerView(recyclerView);
            }
        }

        @Override
        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            if (adapter != null) {
                adapter.onDetachedFromRecyclerView(recyclerView);
            }
        }

        private class LoadMoreViewHolder extends ViewHolder {
            LoadMoreViewHolder(View itemView) {
                super(itemView);
            }
        }
    }
    //endregion

    public interface OnLoadMoreListener {
        void onLoadMore();
    }
}