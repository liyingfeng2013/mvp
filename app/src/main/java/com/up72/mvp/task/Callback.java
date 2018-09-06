package com.up72.mvp.task;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.up72.library.utils.Log;

import retrofit2.Call;
import retrofit2.Response;

/**
 * 说点什么
 * Created by LYF on 2016/12/27.
 */
public abstract class Callback<T> implements retrofit2.Callback<T> {
    private Log log = new Log(getClass());

    @Override
    public final void onResponse(Call<T> call, Response<T> response) {
        onSuccess(response.body());
    }

    @Override
    public final void onFailure(Call<T> call, Throwable t) {
        log.e(t);
        if (t instanceof ResultException) {
            onFailure(t.getMessage());
        } else {
            onFailure("请求失败！");
        }
    }

    public abstract void onSuccess(@Nullable T t);

    public abstract void onFailure(@NonNull String error);
}