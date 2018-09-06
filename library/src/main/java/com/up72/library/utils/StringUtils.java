package com.up72.library.utils;

import android.support.annotation.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具
 */
public class StringUtils {
    public static boolean isEmpty(String value) {
        return null == value || ("").equals(value.trim()) || ("null").equals(value.toLowerCase());
    }

    public static int add(int a, int b) {
        return a + b;
    }

    /**
     * 在字符串中间拼接指定符号
     *
     * @param array    数组
     * @param splitter 指定字符
     * @return 拼接后的字符串, 例如(["a","b","c"]) 用#拼接，结果为 a#b#c
     */
    @NonNull
    public static String join(@NonNull Object[] array, String splitter) {
        if (array.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Object s : array) {
            sb.append(s);
            sb.append(splitter);
        }
        return sb.toString().substring(0, sb.toString().length() - 1);
    }

    /**
     * 手机号验证
     */
    public static boolean isMobile(final String mobile) {
        if (mobile == null) {
            return false;
        }
        String str = "^[1][34578]\\d{9}$";
        Pattern p = Pattern.compile(str); // 验证手机号
        return p.matcher(mobile).matches();
    }

    /**
     * 座机号验证
     */
    public static boolean isTel(final String phone) {
        if (phone.length() > 9) {
            Pattern p = Pattern.compile("^[0][1-9]{2,3}-\\d{5,10}$");// 验证带区号的
            return p.matcher(phone).matches();
        } else {
            Pattern p = Pattern.compile("^[1-9]\\d{5,8}$");// 验证没有区号的
            return p.matcher(phone).matches();
        }
    }

    public static boolean isEmail(String email) {
        if (email == null) {
            return false;
        }
        String str = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    public static boolean isPassword(String value) {
        Pattern p = Pattern.compile("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,16}$");
        Matcher m = p.matcher(value);
        return m.matches();
    }

    public static boolean isUri(String value) {
        Pattern p = Pattern.compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
        Matcher m = p.matcher(value);
        return m.matches();
    }
}