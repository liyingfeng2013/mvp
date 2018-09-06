package com.up72.mvp.ui;

import com.up72.mvp.model.VersionModel;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * 版本更新接口
 * Created by LYF on 2016/12/28.
 */
public interface VersionService {
    @FormUrlEncoded
    @POST("daily/app/version")
    Call<VersionModel> getVersionInfo(@Field("type") int type);
}