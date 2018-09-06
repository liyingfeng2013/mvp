package com.up72.mvp.utils;

import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * 说点什么
 * Created by LYF on 17.3.29.
 */
public class IntentUtils {

    @Nullable
    public static String getString(@Nullable Intent intent, @Nullable String key) {
        return getString(intent, key, null);
    }

    @Nullable
    public static String getString(@Nullable Intent intent, @Nullable String key, @Nullable String defaultValue) {
        if (intent != null && intent.getExtras() != null && key != null
                && intent.getExtras().containsKey(key)) {
            String result = intent.getExtras().getString(key, defaultValue);
            if (result != null && result.length() > 0) {
                return result;

            }
        }
        return defaultValue;
    }

    public static int getInt(@Nullable Intent intent, @Nullable String key) {
        return getInt(intent, key, 0);
    }

    public static int getInt(@Nullable Intent intent, @Nullable String key, int defaultValue) {
        if (intent != null && intent.getExtras() != null && key != null
                && intent.getExtras().containsKey(key)) {
            return intent.getExtras().getInt(key, defaultValue);
        }
        return defaultValue;
    }
}