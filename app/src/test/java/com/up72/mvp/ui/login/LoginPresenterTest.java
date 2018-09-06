package com.up72.mvp.ui.login;

import com.up72.mvp.model.UserModel;
import com.up72.mvp.task.Task;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * 说点什么
 * Created by LYF on 17.2.28.
 */
public class LoginPresenterTest {

    @Test
    public void testLogin() {
        try {
            UserModel userModel = Task.create(LoginService.class).login("18600248798", "123456").execute().body();
            Assert.fail(userModel.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}