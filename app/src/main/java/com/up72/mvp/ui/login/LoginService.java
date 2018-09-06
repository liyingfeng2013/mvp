package com.up72.mvp.ui.login;

import com.up72.mvp.model.UserModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * 说点什么
 * Created by LYF on 2016/12/12.
 */
interface LoginService {
    @FormUrlEncoded
    @POST("interface/jx/member/login.jsp")
    Call<UserModel> login(@Field("userName") String userName, @Field("password") String password);

    @FormUrlEncoded
    @POST("interface/jx/member/login.jsp")
    Call<List<UserModel>> login1(@Field("userName") String userName, @Field("password") String password);
}