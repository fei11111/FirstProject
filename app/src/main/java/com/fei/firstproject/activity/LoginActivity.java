package com.fei.firstproject.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import com.common.viewmodel.EmptyViewModel;
import com.fei.firstproject.R;
import com.fei.firstproject.databinding.ActivityLoginBinding;
import com.fei.firstproject.entity.UserEntity;
import com.fei.firstproject.http.HttpMgr;
import com.fei.firstproject.http.inter.CallBack;
import com.fei.firstproject.utils.SPUtils;
import com.fei.firstproject.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseProjectActivity<EmptyViewModel, ActivityLoginBinding> {

    private static final int REQUEST_PERMISSION_TELEPHONE = 100;

    @Override
    public void permissionsDeniedCallBack(int requestCode) {
        showMissingPermissionDialog("需要获取设备信息才能登录", requestCode);
    }

    @Override
    public void permissionsGrantCallBack(int requestCode) {
        if (requestCode == REQUEST_PERMISSION_TELEPHONE) {
            attemptLogin();
        }
    }


    @Override
    public void permissionDialogDismiss(int requestCode) {
        if (requestCode == REQUEST_PERMISSION_TELEPHONE) {
            Utils.showToast(this, "授权不成功，将无法登陆/注册");
        }
    }

    @Override
    public void initTitle() {
        setBackTitle(getString(R.string.login));
    }

    @Override
    public void initRequest() {

    }

    void textChanged(EditText et) {
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String userNameText = mChildBinding.etUsername.getText().toString();
                String passwordText = mChildBinding.etPassword.getText().toString();
                String code = mChildBinding.etVertifyCode.getText().toString();
                if (!TextUtils.isEmpty(userNameText) && !TextUtils.isEmpty(passwordText) && !TextUtils.isEmpty(code)) {
                    mChildBinding.btnLogin.setEnabled(true);
                } else {
                    mChildBinding.btnLogin.setEnabled(false);
                }
            }
        });

    }

    void clickSignIn() {
        mChildBinding.btnLogin.setOnClickListener(v -> {
            String code = mChildBinding.tvCode.getCode().toLowerCase();
            String inputCode = mChildBinding.etVertifyCode.getText().toString().toLowerCase();
            if (code.equals(inputCode)) {
                checkPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_PERMISSION_TELEPHONE);
            } else {
                mChildBinding.etVertifyCode.setError(getString(R.string.vertify_code_error));
                mChildBinding.tvCode.refreshCode();
            }
        });

    }

    void clickRegister() {
        mChildBinding.tvRegister.setOnClickListener(v -> {
            startActivityWithoutCode(new Intent(this, RegisterActivity.class));
        });

    }

    private void attemptLogin() {
        Utils.hideKeyBoard(this);
        final String userNameText = mChildBinding.etUsername.getText().toString();
        String passwordText = mChildBinding.etPassword.getText().toString();
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        final String deviceId = tm.getDeviceId();
        Map<String, String> map = new HashMap<>();
        map.put("password", passwordText);
        map.put("deviceId", deviceId);
        map.put("mobile", userNameText);
        proShow();
        HttpMgr.login(this, map, new CallBack<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) {
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
            public void onFail() {
                proDisimis();
            }
        });
    }


    private void getUserInfo(String tokenId, String deviceId) {
        proShow();
        Map<String, String> map = new HashMap<>();
        map.put("token", tokenId);
        map.put("deviceID", deviceId);
        HttpMgr.getUserInfo(this, map, new CallBack<UserEntity>() {
            @Override
            public void onSuccess(UserEntity userEntity) {
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
            public void onFail() {
                proDisimis();
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_close_in_animation, R.anim.activity_close_out_animation);
    }

    @Override
    public void createObserver() {
        textChanged(mChildBinding.etPassword);
        textChanged(mChildBinding.etUsername);
        textChanged(mChildBinding.etVertifyCode);
        clickSignIn();
        clickRegister();
    }

    @Override
    public void initViewAndData(Bundle savedInstanceState) {

    }
}

