package com.up72.mvp.utils;

import android.support.annotation.NonNull;

import com.up72.mvp.Constants;

/**
 * url工具类
 * Created by LYF on 2016/12/28.
 */
public class UrlUtils {
    @NonNull
    public static String getFullUrl(String url) {
        if (url == null) {
            return "";
        } else {
            if (url.length() > 0 && !url.toLowerCase().startsWith("http")) {
                url = Constants.baseImageUrl + url;
            }
            return url;
        }
    }
}