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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fei.firstproject.R;
import com.fei.firstproject.config.AppConfig;
import com.fei.firstproject.dialog.TipDialog;
import com.fei.firstproject.http.BaseWithoutBaseEntityObserver;
import com.fei.firstproject.http.factory.RetrofitFactory;
import com.fei.firstproject.utils.SPUtils;
import com.fei.firstproject.utils.Utils;
import com.fei.firstproject.widget.AppHeadView;
import com.fei.firstproject.widget.PartHeadView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import okhttp3.ResponseBody;

/**
 * Created by Fei on 2017/8/31.
 */

public class SettingActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.btn_logout)
    Button btnLogout;
    @BindView(R.id.phv_self_info)
    PartHeadView phvSelfInfo;
    @BindView(R.id.phv_account_security)
    PartHeadView phvAccountSecurity;
    @BindView(R.id.phv_my_address)
    PartHeadView phvMyAddress;
    @BindView(R.id.phv_about_us)
    PartHeadView phvAboutUs;
    @BindView(R.id.iv_user_head)
    CircleImageView ivUserHead;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.iv_vip)
    ImageView ivVip;

    private TipDialog tipDialog;
    private static final int REQUEST_PERMISSION_TELEPHONE = 100;

    @Override
    public void permissionsDeniedCallBack(int requestCode) {
        showMissingPermissionDialog("需要获取设备信息才能退出", requestCode);
    }

    @Override
    public void permissionsGrantCallBack(int requestCode) {
        if (requestCode == REQUEST_PERMISSION_TELEPHONE) {
            showTipDialog();
        }
    }

    @Override
    public void permissionDialogDismiss(int requestCode) {
        if (requestCode == REQUEST_PERMISSION_TELEPHONE) {
            Utils.showToast(this, "授权不成功，将无法退出当前账户");
        }
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_setting;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        initInfo();
        initListener();
    }

    private void initInfo() {
        tvName.setText(AppConfig.user.getUserName());
    }

    @Override
    public void initRequest() {

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

    //个人信息
    @OnClick(R.id.phv_self_info)
    void clickSelfInfo(View view) {
        startActivityWithoutCode(new Intent(this, SelfInfoActivity.class));
    }

    @OnClick(R.id.btn_logout)
    void clickLogout(View view) {
        checkPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_PERMISSION_TELEPHONE);
    }

    @OnClick(R.id.phv_account_security)
    void clickAccountSecurity(View view) {
        startActivityWithoutCode(new Intent(this, AccountSecurityActivity.class));
    }

    @OnClick(R.id.phv_my_address)
    void clickMyAddress(View view) {
        startActivityWithoutCode(new Intent(this, MyAddressActivity.class));
    }

    @OnClick(R.id.phv_about_us)
    void clickAboutUs(View view) {
        startActivityWithoutCode(new Intent(this, AboutUsActivity.class));
    }

    private void showTipDialog() {
        if (tipDialog == null) {
            tipDialog = new TipDialog(this);
            tipDialog.setCanceledOnTouchOutside(false);
            tipDialog.setContentText(getResources().getString(R.string.logout_tip));
            tipDialog.setConfirmButtonText(getResources().getString(R.string.confirm));
            tipDialog.setOnConfirmListener(new TipDialog.OnConfirmListener() {
                @Override
                public void onClick(View view) {
                    logout();
                }
            });
        }
        tipDialog.show();
    }

    private void logout() {
        String tokenId = SPUtils.get(this, "tokenId", "").toString();
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = tm.getDeviceId();
        Map<String, String> map = new HashMap<>();
        map.put("tokenId", tokenId);
        map.put("deviceID", deviceId);
        if (TextUtils.isEmpty(tokenId)) {
            refreshUserInfoWhenLogout();
            finish();
        } else {
            proShow();
            Observable<ResponseBody> logout = RetrofitFactory.getBigDb().logout(map);
            logout.compose(this.<ResponseBody>createTransformer(false))
                    .subscribe(new BaseWithoutBaseEntityObserver<ResponseBody>(this) {
                        @Override
                        protected void onHandleSuccess(ResponseBody responseBody) {
                            proDisimis();
                            refreshUserInfoWhenLogout();
                            finish();
                        }

                        @Override
                        protected void onHandleError(String msg) {
                            proDisimis();
                            super.onHandleError(msg);
                        }
                    });
        }
    }

}
