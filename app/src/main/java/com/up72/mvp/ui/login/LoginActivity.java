package com.up72.mvp.ui.login;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.up72.mvp.R;
import com.up72.mvp.base.BaseActivity;

/**
 * 说点什么
 * Created by LYF on 2016/12/7.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener, LoginContract.View {
    private EditText etUserName, etPassword;

    private LoginContract.Presenter presenter;

    @Override
    protected int getContentView() {
        return R.layout.login_act;
    }

    @Override
    protected void initView() {
        etUserName = (EditText) findViewById(R.id.et_username);
        etPassword = (EditText) findViewById(R.id.et_password);
    }

    @Override
    protected void initListener() {
        findViewById(R.id.btn_login).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        presenter = new LoginPresenter(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                presenter.login(etUserName.getText().toString().trim(), etPassword.getText().toString().trim());
                break;
        }
    }

    @Override
    public void onUserError(@NonNull String error) {
        etUserName.setError(error);
    }

    @Override
    public void onPasswordError(@NonNull String error) {
        etPassword.setError(error);
    }

    @Override
    public void loading(boolean show) {
    }

    @Override
    public void onLoginSuccess() {
        Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onLoginFailure(@NonNull String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }
}