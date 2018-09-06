package com.up72.library.utils;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

/**
 * 说点什么
 * Created by LYF on 17.3.22.
 */
public class TimeUtilsTest {
    @Test
    public void test() {
       /* long time = TimeUtils.getTime("20170322172400000");
        String timeFormat = TimeUtils.msToString(time, "yyyy-MM-dd HH:mm:ss.SSS");
        Assert.assertEquals("2017-03-22 17:24:00.000", timeFormat);
        Assert.assertEquals(0x10, 16);*/

        String[] arrays=new String[]{"111","222","333"};
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("key",arrays);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String str=jsonObject.toString();
        Assert.assertEquals(0x10, 16);
    }
}