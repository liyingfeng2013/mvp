package com.up72.library.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

/**
 * 系统相关
 * Created by LYF on 2016/10/30.
 */
public class SystemUtils {
    /**
     * 获取当前应用版本序号
     *
     * @param context 当前上下文
     * @return 当前应用版本序号
     */
    public static int getVersionCode(@NonNull Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 获取当前应用版本名
     *
     * @param context 当前上下文
     * @return 当前应用版本名
     */
    @NonNull
    public static String getVersionName(@NonNull Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 打开浏览器
     */
    public static void openBrowser(@NonNull Context context, String urlStr) {
        if (urlStr != null && urlStr.length() > 0) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri url = Uri.parse(urlStr);
            intent.setData(url);
            context.startActivity(intent);
        }
    }

    /**
     * 进入全屏
     *
     * @param activity       当前activity
     * @param statusBarColor 状态栏颜色
     */
    public static void enterFullscreen(@NonNull Activity activity, @ColorInt int statusBarColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int systemUiVisibility = activity.getWindow().getDecorView().getSystemUiVisibility();
            int flags = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            systemUiVisibility |= flags;
            activity.getWindow().getDecorView().setSystemUiVisibility(systemUiVisibility);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.getWindow().setStatusBarColor(statusBarColor);
            }
        } else {
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    /**
     * 退出全屏
     *
     * @param activity       当前activity
     * @param statusBarColor 状态栏颜色
     */
    public static void exitFullscreen(@NonNull Activity activity, @ColorInt int statusBarColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int systemUiVisibility = activity.getWindow().getDecorView().getSystemUiVisibility();
            int flags = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            systemUiVisibility &= ~flags;
            activity.getWindow().getDecorView().setSystemUiVisibility(systemUiVisibility);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.getWindow().setStatusBarColor(statusBarColor);
            }
        } else {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    /**
     * 判断键盘是否显示
     *
     * @param activity 当前activity
     * @return true显示，false隐藏
     */
    public static boolean isKeyboardShown(@NonNull Activity activity) {
        View rootView = activity.getWindow().getDecorView();
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int keyHeight = metrics.heightPixels / 3;
        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        int heightDiff = rootView.getBottom() - r.bottom;
        return heightDiff > keyHeight;
    }

    /**
     * 判断当前网络是否为WIFI
     *
     * @param context 当前上下文
     * @return true为wifi
     */
    public static boolean isWifi(@NonNull Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkINfo = cm.getActiveNetworkInfo();
        return networkINfo != null
                && networkINfo.getType() == ConnectivityManager.TYPE_WIFI;
    }
}