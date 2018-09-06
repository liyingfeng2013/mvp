package com.up72.mvp;

import android.app.Application;

import com.up72.library.UP72System;
import com.up72.library.utils.Log;
import com.up72.mvp.manager.UserManager;

/**
 * Application
 * Created by LYF on 2016/12/9.
 */
public class UP72Application extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        UserManager.getInstance().init(this);

        //根据当前运行环境开启和关闭日志（不用管）
        Log.debug = BuildConfig.DEBUG;
        //初始化网络状态
//        ConnectivityChangeReceiver.initNetState(this);
        //初始化相关功能
        UP72System.getInstance().init(this);
    }
}