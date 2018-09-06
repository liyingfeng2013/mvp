package com.up72.mvp.ui.login;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.up72.mvp.model.UserModel;
import com.up72.mvp.task.Callback;
import com.up72.mvp.task.Task;

/**
 * 说点什么
 * Created by LYF on 2016/12/8.
 */
public class LoginPresenter implements LoginContract.Presenter {
    private final LoginContract.View loginView;

    public LoginPresenter(LoginContract.View loginView) {
        this.loginView = loginView;
    }

    @Override
    public void login(String userName, String password) {
        if (loginView != null) {
            if (userName == null || userName.length() == 0) {
                loginView.onUserError("请输入用户名");
            } else if (password == null || password.length() == 0) {
                loginView.onPasswordError("请输入密码");
            } else {
                loginView.loading(true);
                Task.create(LoginService.class).login(userName, password).enqueue(new Callback<UserModel>() {
                    @Override
                    public void onSuccess(UserModel userModel) {
                        loginView.loading(false);
                        loginView.onLoginSuccess();
                    }

                    @Override
                    public void onFailure(@NonNull String error) {
                        loginView.loading(false);
                        loginView.onLoginFailure(error);
                    }
                });
            }
        }
    }

    private void sendRequest(String userName, String password) {
        Task.create(LoginService.class).login(userName, password).enqueue(new Callback<UserModel>() {
            @Override
            public void onSuccess(@Nullable UserModel userModel) {

            }

            @Override
            public void onFailure(@NonNull String error) {

            }
        });
    }
}