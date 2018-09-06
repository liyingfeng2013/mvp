package com.up72.mvp.task;

/**
 * 自定义数据返回异常
 * Created by LYF on 2016/12/27.
 */
class ResultException extends RuntimeException  {
    ResultException(String message) {
        super(message);
    }
}
