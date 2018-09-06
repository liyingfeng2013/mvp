package com.up72.library.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Description
 * Created by LYF on 2015/9/22.
 */
public class TimeUtils {

    /**
     * 毫秒转指定格式字符串
     *
     * @param milliseconds 毫秒
     * @param format       (例如：2006年11月17日 15时19分56秒 星期五（"yyyy年MM月dd日 HH时mm分ss秒 E "）)
     *                     G:年代标志符,y: 年, M :月, d :日, h: 时 在上午或下午 (1~12), H :时 在一天中
     *                     (0~23), m: 分, s :秒, S: 毫秒, E: 星期, D: 一年中的第几天, F :一月中第几个星期几, w:
     *                     一年中第几个星期, W:一月中第几个星期, a :上午 / 下午, 标记符 k: 时 在一天中 (1~24), K: 时
     *                     在上午或下午 (0~11) ,z: 时区
     * @return 指定格式字符串，异常则为null
     */
    public static String msToString(long milliseconds, String format) {
        try {
            String str = String.valueOf(milliseconds);
            int size = 13 - str.length();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    str += "0";
                }
                milliseconds = Long.valueOf(str);
            } else if (size < 0) {
                str = str.substring(0, 13);
                milliseconds = Long.valueOf(str);
            }
            if (format == null || format.trim().equals("") || format.trim().toUpperCase().equals("NULL")) {
                format = "yyyy-MM-dd HH:mm:ss";
            }
            return new SimpleDateFormat(format, Locale.SIMPLIFIED_CHINESE).format(new Date(milliseconds));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static long getTime(String time) {
        if (time == null || time.length() != 17) {
            return 0;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.SIMPLIFIED_CHINESE);
        try {
            Date date = simpleDateFormat.parse(time);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void getFormatTime(String time, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.SIMPLIFIED_CHINESE);
    }
}