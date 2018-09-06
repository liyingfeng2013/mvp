package com.up72.mvp.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.up72.mvp.ui.WebViewActivity;
import com.up72.mvp.ui.login.LoginActivity;

/**
 * 页面跳转
 * Created by LYF on 2015/11/2.
 */
public class RouteManager {
    private static class Holder {
        private static RouteManager routeManager = new RouteManager();
    }

    private RouteManager() {
    }

    public static RouteManager getInstance() {
        return Holder.routeManager;
    }

    private void startActivity(Context context, Class<?> cls) {
        startActivity(context, cls, -1, null, -1);
    }

    private void startActivity(Context context, Class<?> cls, int flags) {
        startActivity(context, cls, -1, null, flags);
    }

    private void startActivity(Context context, Class<?> cls, Bundle data) {
        startActivity(context, cls, -1, data, -1);
    }

    private void startActivity(Context context, Class<?> cls, int requestCode, Bundle data) {
        startActivity(context, cls, requestCode, data, -1);
    }

    private void startActivity(Context context, Class<?> cls, Bundle data, int flags) {
        startActivity(context, cls, -1, data, flags);
    }

    private void startActivity(Context context, Class<?> cls, int requestCode, Bundle data, int flags) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, cls);
        if (data != null) {
            intent.putExtras(data);
        }
        if (flags != -1) {
            intent.addFlags(flags);
        }
        if (context instanceof Activity) {
            if (requestCode == 0) {
                context.startActivity(intent);
            } else {
                ((Activity) context).startActivityForResult(intent, requestCode);
            }
            ((Activity) context).overridePendingTransition(0, 0);
        } else {
            context.startActivity(intent);
        }
    }

    /**
     * 登录界面
     */
    public void toLogin(Context context) {
        this.startActivity(context, LoginActivity.class);
    }

    /**
     * Web页面
     *
     * @param url web页面的url
     */
    public void toWebView(Context context, @NonNull String url) {
        Bundle data = new Bundle();
        data.putString(WebViewActivity.WEB_URL, url);
        startActivity(context, WebViewActivity.class, data);
    }
}