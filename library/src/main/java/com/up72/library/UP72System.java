package com.up72.library;

import android.app.Application;
import android.content.Context;

import com.up72.library.exception.CrashHandler;
import com.up72.library.utils.FileUtils;

/**
 * Description
 * Created by LYF on 2015/9/22.
 */
public class UP72System {

    private static class Holder {
        private static UP72System up72System = new UP72System();
    }

    private UP72System() {
    }

    public static UP72System getInstance() {
        return Holder.up72System;
    }

    public void init(Context context) {
        if (context instanceof Application) {
            //初始化全局异常监听,如果不需要请注释并删除exception/CrashHandler.java文件
            CrashHandler.getInstance().init(context);
            //初始化文件缓存,如果不需要请注释，并删除utils/FileUtils.java文件
            FileUtils.getInstance().init(context);
        } else {
            throw new IllegalStateException("Should initialize in application");
        }
    }
}