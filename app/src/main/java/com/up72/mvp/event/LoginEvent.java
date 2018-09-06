package com.up72.mvp.event;

/**
 * 登录事件
 * Created by liyingfeng on 2015/11/17.
 */
public class LoginEvent {
    public boolean isLogin;

    public LoginEvent(boolean isLogin) {
        this.isLogin = isLogin;
    }
}