package com.xh.common.util.pinned;

import android.support.v7.widget.RecyclerView;

/**
 * Created by LYF on 2018/9/10.
 */
public abstract class PinnedAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {
    public abstract boolean isPinnedPosition(int position);
}