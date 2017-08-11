package com.fei.firstproject.activity;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.fei.firstproject.R;
import com.fei.firstproject.http.manager.RetrofitManager;
import com.fei.firstproject.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    void sign_in() {
        checkPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_CODE_1);
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
        //转换
        //http://blog.chengyunfeng.com/?p=964
        proShow();
        RetrofitManager.getInstance().getRequestApi().login(map)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        proDisimis();
                        try {
                            JSONObject json = new JSONObject(response.body().string());
                            if (json.has("tokenId")) {
                                String tokenId = json.getString("tokenId");
                                Log.i("tag", tokenId);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        proDisimis();
                    }
                });

//        String uuid = UUID.randomUUID().toString().substring(0, 10);
//        String userName = "_bbyy" + uuid;
//        AppConfig.user = new UserBean();
//        AppConfig.user.setUserName(userId);
//        AppConfig.user.setName(userName);
//        AppConfig.ISLOGIN = true;
//        SPUtils.put(MyApplication.getInstance(), "user", AppConfig.user);
//        finish();
    }
}

