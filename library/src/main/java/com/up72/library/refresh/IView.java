package com.up72.library.refresh;

import android.view.View;
import android.view.ViewGroup;

public interface IView {
    /**
     * @return 当前的view
     */
    View getTargetView(ViewGroup parent);

    /**
     * 拖拽的回调
     *
     * @param scrollY 大于0表示下拉的距离,小于0表示上拉的距离
     * @param enable  head和footer制约的标记
     */
    void onPull(float scrollY, boolean enable);

    /**
     * 手指放开
     *
     * @param scrollY 手指放开时的拖拽距离
     */
    void onFingerUp(float scrollY);

    /**
     * 收尾
     */
    void detach();

    /**
     * 关联IPull
     *
     * @param iPull
     */
    void pullLayout(IPull iPull);

    /**
     * 拉回
     *
     * @param isBeingDragged true表示手指还在拖动状态
     */
    void finishPull(boolean isBeingDragged);

    /**
     * 拉回
     *
     * @param isBeingDragged true表示手指还在拖动状态
     * @param msg            提示消息
     * @param result         是否成功
     */
    void finishPull(boolean isBeingDragged, CharSequence msg, boolean result);
}
