package com.fei.firstproject.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fei.firstproject.R;
import com.fei.firstproject.entity.UserEntity;
import com.fei.firstproject.http.BaseWithoutBaseEntityObserver;
import com.fei.firstproject.http.factory.RetrofitFactory;
import com.fei.firstproject.utils.SPUtils;
import com.fei.firstproject.utils.Utils;
import com.fei.firstproject.widget.AppHeadView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import io.reactivex.Observable;
import okhttp3.ResponseBody;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity {

    @BindView(R.id.et_username)
    AutoCompleteTextView etUsername;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.btn_login)
    Button rvSignIn;
    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.tv_register)
    TextView tvRegister;
    @BindView(R.id.appHeadView)
    AppHeadView appHeadView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.llweixin)
    LinearLayout llweixin;
    @BindView(R.id.ll_qq)
    LinearLayout llQq;

    private static final int REQUEST_CODE_1 = 100;

    @Override
    public void permissionsDeniedCallBack(int requestCode) {
        showMissingPermissionDialog("需要获取设备信息才能登录", requestCode);
    }

    @Override
    public void permissionsGrantCallBack(int requestCode) {
        if (requestCode == REQUEST_CODE_1) {
            attemptLogin();
        }
    }


    @Override
    public void permissionDialogDismiss(int requestCode) {
        if (requestCode == REQUEST_CODE_1) {
            Utils.showToast(this, "授权不成功，将无法登陆/注册");
        }
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_login;
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

    @OnTextChanged(value = {R.id.et_password, R.id.et_username}, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void textChanged() {
        String userNameText = etUsername.getText().toString();
        String passwordText = etPassword.getText().toString();
        if (!TextUtils.isEmpty(userNameText) && !TextUtils.isEmpty(passwordText)) {
            rvSignIn.setEnabled(true);
        } else {
            rvSignIn.setEnabled(false);
        }
    }

    @OnClick(R.id.btn_login)
    void clickSignIn(View view) {
        checkPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_CODE_1);
    }

    @OnClick(R.id.tv_register)
    void clickRegister(View view) {
        startActivityWithoutCode(new Intent(this, RegisterActivity.class));
    }

    private void attemptLogin() {
        Utils.hideKeyBoard(this);
        final String userNameText = etUsername.getText().toString();
        String passwordText = etPassword.getText().toString();
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        final String deviceId = tm.getDeviceId();
        Map<String, String> map = new HashMap<>();
        map.put("password", passwordText);
        map.put("deviceId", deviceId);
        map.put("mobile", userNameText);
        proShow();
        Observable<ResponseBody> login = RetrofitFactory.getBigDb().login(map);
        login.compose(this.<ResponseBody>createTransformer(false))
                .subscribe(new BaseWithoutBaseEntityObserver<ResponseBody>(this) {
                    @Override
                    protected void onHandleSuccess(ResponseBody responseBody) {
                        proDisimis();
                        try {
                            String response = responseBody.string();
                            if (!TextUtils.isEmpty(response)) {
                                JSONObject json = new JSONObject(response);
                                if (json.has("tokenId")) {
                                    String tokenId = json.getString("tokenId");
                                    SPUtils.put(LoginActivity.this, "tokenId", tokenId);
                                    SPUtils.put(LoginActivity.this, "deviceId", deviceId);
                                    getUserInfo(tokenId, deviceId);
                                } else {
                                    if (json.has("returnMsg")) {
                                        String returnMsg = json.getString("returnMsg");
                                        Utils.showToast(LoginActivity.this, returnMsg);
                                    } else {
                                        Utils.showToast(LoginActivity.this, getString(R.string.login_fail_and_retry));
                                    }
                                }
                            } else {
                                Utils.showToast(LoginActivity.this, getString(R.string.login_fail_and_retry));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    protected void onHandleError(String msg) {
                        super.onHandleError(msg);
                        proDisimis();
                    }
                });
    }


    private void getUserInfo(String tokenId, String deviceId) {
        proShow();
        Map<String, String> map = new HashMap<>();
        map.put("token", tokenId);
        map.put("deviceID", deviceId);
        Observable<UserEntity> userInfo = RetrofitFactory.getBigDb().getUserInfo(map);
        userInfo.compose(this.<UserEntity>createTransformer(false)).subscribe(new BaseWithoutBaseEntityObserver<UserEntity>(this) {
            @Override
            protected void onHandleSuccess(UserEntity userEntity) {
                proDisimis();
                if (userEntity != null) {
                    Utils.showToast(LoginActivity.this, userEntity.getReturnMsg());
                    if (userEntity.getSuccess().equals("YES")) {
                        refreshUserInfoWhenLogin(userEntity);
                        finish();
                    }
                } else {
                    Utils.showToast(LoginActivity.this, getString(R.string.login_fail_and_retry));
                }
            }

            @Override
            protected void onHandleError(String msg) {
                super.onHandleError(msg);
                proDisimis();
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_close_in_animation, R.anim.activity_close_out_animation);
    }

}

