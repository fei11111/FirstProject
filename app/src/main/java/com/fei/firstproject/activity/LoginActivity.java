package com.fei.firstproject.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.fei.firstproject.R;
import com.fei.firstproject.entity.BaseEntity;
import com.fei.firstproject.entity.UserEntity;
import com.fei.firstproject.http.BaseObserver;
import com.fei.firstproject.http.RxSchedulers;
import com.fei.firstproject.http.factory.RetrofitFactory;
import com.fei.firstproject.utils.Utils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import io.reactivex.Observable;

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

    private static final int REQUEST_CODE_1 = 100;

    @Override
    public void requestPermissionsBeforeInit() {

    }

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
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
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
        //password=10160411920&deviceID=863978010682477&mobile=111920
        final String userNameText = etUsername.getText().toString();
        String passwordText = etPassword.getText().toString();
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = tm.getDeviceId();
        Map<String, String> map = new HashMap<>();
        map.put("password", passwordText);
        map.put("deviceId", deviceId);
        map.put("mobile", userNameText);
        //http://www.jianshu.com/p/9674f6df910d
        proShow();
        Observable<BaseEntity<UserEntity>> login = RetrofitFactory.getBigDb().login(map);
        login.compose(RxSchedulers.compose(this, this.<BaseEntity<UserEntity>>bindToLifecycle())).subscribe(new BaseObserver<UserEntity>(this) {
            @Override
            protected void onHandleSuccess(UserEntity userEntity) {
                proDisimis();
            }
        });
    }
}

