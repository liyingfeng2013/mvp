package com.up72.library.utils;

import junit.framework.Assert;

import org.junit.Test;

public class StringUtilsTest {
    @Test
    public void isEmpty() {
        Assert.assertEquals(true, StringUtils.isEmpty(null));
        Assert.assertEquals(true, StringUtils.isEmpty("null"));
        Assert.assertEquals(true, StringUtils.isEmpty("Null"));
        Assert.assertEquals(true, StringUtils.isEmpty("nUll"));
        Assert.assertEquals(false, StringUtils.isEmpty("string"));
        Assert.assertEquals(false, StringUtils.isEmpty("null1"));
    }

    @Test
    public void join() {
        Assert.assertEquals("1&2&3&4", StringUtils.join(new Integer[]{1, 2, 3, 4}, "&"));
        Assert.assertEquals("1&2&3&4", StringUtils.join(new Long[]{1L, 2L, 3L, 4L}, "&"));
        Assert.assertEquals("1.0&2.0&3.0&4.0", StringUtils.join(new Double[]{1D, 2D, 3D, 4D}, "&"));
        Assert.assertEquals("1.0&2.0&3.0&4.0", StringUtils.join(new Float[]{1F, 2F, 3F, 4F}, "&"));
        Assert.assertNotNull(StringUtils.join(new Double[]{}, "&"));

        Assert.assertEquals(false, StringUtils.isEmail("www.baidu.com"));
        Assert.assertEquals(true, StringUtils.isEmail("555@qq.com"));
        Assert.assertEquals(false, StringUtils.isEmail(null));

        Assert.assertEquals(false, StringUtils.isMobile(null));
        Assert.assertEquals(true, StringUtils.isMobile("13655555555"));
        Assert.assertEquals(true, StringUtils.isMobile("14333586549"));
        Assert.assertEquals(false, StringUtils.isMobile("16333586549"));
        Assert.assertEquals(false, StringUtils.isMobile("01111112222"));
        Assert.assertEquals(false, StringUtils.isMobile("1788888888"));
        Assert.assertEquals(false, StringUtils.isMobile("11888888880"));

        Assert.assertEquals(false, StringUtils.isMobile(""));
        Assert.assertEquals(false, StringUtils.isMobile("6546s4f64wagjwopjfgoawjgojwoajgosajgoawjgeo"));

        Assert.assertEquals(3, StringUtils.add(1, 2));
        Assert.assertEquals(400000000, StringUtils.add(200000000, 200000000));
    }
}