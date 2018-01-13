package com.fei.firstproject.activity;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fei.firstproject.R;
import com.fei.firstproject.widget.AppHeadView;
import com.fei.firstproject.widget.VerifyCodeView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Fei on 2017/8/31.
 */

public class RegisterActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.login_progress)
    ProgressBar loginProgress;
    @BindView(R.id.et_username)
    AutoCompleteTextView etUsername;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.et_vertify_code)
    EditText etVertifyCode;
    @BindView(R.id.tv_code)
    VerifyCodeView tvCode;
    @BindView(R.id.btn_register)
    Button btnRegister;
    @BindView(R.id.email_login_form)
    LinearLayout emailLoginForm;
    @BindView(R.id.login_form)
    ScrollView loginForm;

    @Override
    public void permissionsDeniedCallBack(int requestCode) {

    }

    @Override
    public void permissionsGrantCallBack(int requestCode) {

    }

    @Override
    public void permissionDialogDismiss(int requestCode) {

    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_register;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        initListener();
    }

    private void initListener() {
        appHeadView.setOnLeftRightClickListener(new AppHeadView.onAppHeadViewListener() {
            @Override
            public void onLeft(View view) {
                onBackPressed();
            }

            @Override
            public void onRight(View view) {
            }

            @Override
            public void onEdit(TextView v, int actionId, KeyEvent event) {

            }
        });
    }

    @Override
    public void initRequest() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
