package com.up72.mvp;

/**
 * 常量类
 * Created by LYF on 2016/12/12.
 */
public class Constants {

    private static final String baseUrlOfOnline = "http://jx.up72.com/";
    private static final String baseUrlOfDebug = "http://jx.up72.com/";
    public static final String baseHostUrl = BuildConfig.DEBUG ? baseUrlOfDebug : baseUrlOfOnline;
    public static final String baseImageUrl = baseHostUrl;
}