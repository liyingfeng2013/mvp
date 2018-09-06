package com.up72.mvp.manager;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.up72.library.utils.PrefsUtils;
import com.up72.mvp.event.LoginEvent;
import com.up72.mvp.model.UserModel;

import org.greenrobot.eventbus.EventBus;

/**
 * 用户数据管理
 * Created by LYF on 2015/11/11.
 */
public class UserManager {
    private static final String KEY_USER_INFO = "KEY_USER_INFO";
    private Context mContext;
    private UserModel userModel;
    private boolean auto = true;

    private static class Holder {
        static UserManager userManager = new UserManager();
    }

    private UserManager() {
    }

    public static UserManager getInstance() {
        return Holder.userManager;
    }

    public void init(@NonNull Context context) {
        this.mContext = context.getApplicationContext();
        String json = PrefsUtils.read(mContext, KEY_USER_INFO, "");
        if (json != null && json.length() > 0) {
            this.userModel = fromJson(json);
        }
    }

    //检查是否初始化过
    private void checkInit() {
        if (this.mContext == null) {
            throw new ExceptionInInitializerError("UserManager is not initialized!");
        }
    }

    /**
     * 判断用户是否登录
     *
     * @return true：登录，false：未登录
     */
    public boolean isLogin() {
        return this.userModel != null;
    }

    /**
     * 获取用户的实例 - 直接引用，修改信息的话使用getUserModelClone()
     *
     * @return UserModel
     */
    public UserModel getUserModel() {
        checkInit();
        return userModel;
    }

    /**
     * 获取用户的实例(拷贝对象)
     *
     * @return UserModel
     */
    public UserModel getUserModelClone() {
        checkInit();
        if (this.userModel == null) {
            return null;
        }
        return fromJson(toJson(this.userModel));
    }

    /**
     * 保存或更改用户信息
     *
     * @param model NonNull UserModel
     */
    public void save(@NonNull UserModel model) {
        checkInit();
        this.userModel = model;
        if (auto) {
            PrefsUtils.write(mContext, KEY_USER_INFO, toJson(model));
        }
        EventBus.getDefault().post(new LoginEvent(true));
    }

    /**
     * 退出登陆
     */
    public void logout() {
        checkInit();
        this.userModel = null;
        PrefsUtils.write(mContext, KEY_USER_INFO, "");
        EventBus.getDefault().post(new LoginEvent(false));
    }

    private static String toJson(UserModel tempUser) {
        return new Gson().toJson(tempUser);
    }

    private static UserModel fromJson(String json) {
        return new Gson().fromJson(json, UserModel.class);
    }

    public boolean isAuto() {
        return auto;
    }

    public void setAuto(boolean auto) {
        this.auto = auto;
    }
}