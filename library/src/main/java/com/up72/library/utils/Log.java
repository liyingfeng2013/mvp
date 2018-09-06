package com.up72.library.utils;

/**
 * 日志工具
 * Created by liyingfeng on 2015/8/18.
 */
public class Log {
    private String tag;
    public static boolean debug = false;

    public Log(String tag) {
        this.tag = "### UP72 ### " + tag;
    }

    public Log(Class<?> clazz) {
        this.tag = "### UP72 ### " + clazz.getName() + "-" + clazz.hashCode();
    }

    public void d(String msg) {
        if (debug) {
            android.util.Log.d(tag, msg);
        }
    }

    public void i(String msg) {
        if (debug) {
            android.util.Log.i(tag, msg);
        }
    }

    public void w(String msg) {
        if (debug) {
            android.util.Log.w(tag, msg);
        }
    }

    public void e(String msg) {
        if (debug) {
            android.util.Log.e(tag, msg);
        }
    }

    public void e(Throwable e) {
        if (debug) {
            android.util.Log.e(tag, "", e);
        }
    }
}