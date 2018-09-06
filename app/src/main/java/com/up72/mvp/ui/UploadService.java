package com.up72.mvp.ui;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * 上传图片
 * Created by LYF on 2016/12/28.
 */
public interface UploadService {
    @POST("component/upload/uploadify")
    Call<Map<String, String>> uploadImage(@Body RequestBody body);
}