package com.up72.mvp.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * 说点什么
 * Created by LYF on 2016/12/19.
 */
public class UserModel {
    private long id;
    @NonNull
    private String name = "";

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }
}