package com.up72.mvp.ui.login;

import android.support.annotation.NonNull;

import com.up72.mvp.base.BasePresenter;
import com.up72.mvp.base.BaseView;

/**
 * 说点什么
 * Created by LYF on 2016/12/8.
 */
public interface LoginContract {
    interface View extends BaseView {
        void onUserError(@NonNull String error);

        void onPasswordError(@NonNull String error);

        void loading(boolean show);

        void onLoginSuccess();

        void onLoginFailure(@NonNull String error);
    }

    interface Presenter extends BasePresenter {
        void login(String userName, String password);
    }
}