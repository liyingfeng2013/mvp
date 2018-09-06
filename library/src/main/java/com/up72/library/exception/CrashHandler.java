package com.up72.library.exception;

import android.content.Context;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.ref.WeakReference;

public class CrashHandler implements UncaughtExceptionHandler {

    private static CrashHandler instance;
    private WeakReference<Context> weakReference;

    public static CrashHandler getInstance() {
        if (instance == null) {
            instance = new CrashHandler();
        }
        return instance;
    }

    public void init(Context ctx) {
        weakReference = new WeakReference<>(ctx);
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 核心方法，当程序crash 会回调此方法， Throwable中存放这错误日志
     */
    @Override
    public void uncaughtException(Thread arg0, Throwable arg1) {
        // TODO: 2016/5/25 保存异常信息
        try {
            if (arg1 != null) {
                arg1.printStackTrace();
                if (weakReference != null && weakReference.get() != null) {
                    // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
                    FileWriter writer = new FileWriter(weakReference.get().getExternalCacheDir() + "/error_log_" + System.currentTimeMillis() + ".txt", true);
                    writer.write(arg1.toString());
                    writer.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

       /* Toast.makeText(context, "出错了", Toast.LENGTH_SHORT).show();
        android.os.Process.killProcess(android.os.Process.myPid());*/
    }
}