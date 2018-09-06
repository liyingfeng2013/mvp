package com.up72.mvp.task;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * response result
 * Created by LYF on 2016/12/12.
 */
class Result {
    @SerializedName("status")
    private String state = "";
    private String message = "";
    private Object data;

    boolean isSuccess() {
        return state != null && state.equals("10000");
    }

    void setState(String state) {
        this.state = state;
    }

    String getMessage() {
        return message;
    }

    void setMessage(String message) {
        this.message = message;
    }

    String getData() {
        return new Gson().toJson(data);
    }

    void setData(Object data) {
        this.data = data;
    }
}