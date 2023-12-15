package com.fei.firstproject.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.common.viewmodel.EmptyViewModel;
import com.fei.firstproject.R;
import com.fei.firstproject.adapter.SingleTextAdapter;
import com.fei.firstproject.config.AppConfig;
import com.fei.firstproject.databinding.ActivitySettingBinding;
import com.fei.firstproject.dialog.BottomListDialog;
import com.fei.firstproject.dialog.TipDialog;
import com.fei.firstproject.event.AllEvent;
import com.fei.firstproject.event.EventType;
import com.fei.firstproject.http.HttpMgr;
import com.fei.firstproject.http.inter.CallBack;
import com.fei.firstproject.utils.SPUtils;
import com.fei.firstproject.utils.Utils;
import com.fei.firstproject.widget.AppHeadView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;

/**
 * Created by Fei on 2017/8/31.
 */

public class SettingActivity extends BaseProjectActivity<EmptyViewModel, ActivitySettingBinding> {

    private TipDialog tipDialog;
    private static final int REQUEST_PERMISSION_TELEPHONE = 100;
    private BottomListDialog bottomListDialog;
    private SingleTextAdapter singleTextAdapter;

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
    public void initTitle() {
        setBackTitle(getString(R.string.account_setting));
    }

    private void initInfo() {
        mChildBinding.tvName.setText(AppConfig.user.getUserName());
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

    @Override
    public void createObserver() {
        clickAddreddressBook();
        clickSelfInfo();
        clickLogout();
        clickAccountSecurity();
        clickMyAddress();
        clickAboutUs();
        clickFontSize();
    }

    //通讯录
    void clickAddreddressBook() {
        mChildBinding.phvAddressBook.setOnClickListener(v -> {
            startActivityWithoutCode(new Intent(this, AddressBookActivity.class));
        });

    }

    //个人信息
    void clickSelfInfo() {
        mChildBinding.phvSelfInfo.setOnClickListener(v -> {
            startActivityWithoutCode(new Intent(this, SelfInfoActivity.class));
        });

    }

    //退出
    void clickLogout() {
        mChildBinding.btnLogout.setOnClickListener(v -> {
            checkPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_PERMISSION_TELEPHONE);
        });
    }

    //账户安全
    void clickAccountSecurity() {
        mChildBinding.phvAccountSecurity.setOnClickListener(v -> {
            startActivityWithoutCode(new Intent(this, AccountSecurityActivity.class));
        });

    }

    //地址
    void clickMyAddress() {
        mChildBinding.phvMyAddress.setOnClickListener(v -> {
            startActivityWithoutCode(new Intent(this, MyAddressActivity.class));
        });

    }

    //关于我们
    void clickAboutUs() {
        mChildBinding.phvAboutUs.setOnClickListener(v -> {
            startActivityWithoutCode(new Intent(this, AboutUsActivity.class));
        });

    }

    //设置字体
    void clickFontSize() {
        mChildBinding.phvFontSize.setOnClickListener(v -> {
            if (bottomListDialog == null) {
                bottomListDialog = new BottomListDialog(this);
                bottomListDialog.setTitle("设置字体");
                List<String> name = new ArrayList<>();
                name.add("小");
                name.add("适中(建议)");
                name.add("大");
                singleTextAdapter = new SingleTextAdapter(this, name);
                bottomListDialog.setOnItemClickListener(new BottomListDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 0) {
                            SPUtils.put(SettingActivity.this, "fontMode", 1);//小
                        } else if (position == 1) {
                            SPUtils.put(SettingActivity.this, "fontMode", 0);//适中
                        } else if (position == 2) {
                            SPUtils.put(SettingActivity.this, "fontMode", 2);//大
                        }
                        EventBus.getDefault().post(new AllEvent(EventType.APP_FONT_CHANGE));
                    }
                });
                bottomListDialog.setAdapter(singleTextAdapter);
            }
            bottomListDialog.show();
        });

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
            HttpMgr.logout(this, map, new CallBack<ResponseBody>() {
                @Override
                public void onSuccess(ResponseBody responseBody) {
                    proDisimis();
                    refreshUserInfoWhenLogout();
                    finish();
                }

                @Override
                public void onFail() {
                    proDisimis();
                }
            });
        }
    }



    @Override
    public void initViewAndData(Bundle savedInstanceState) {
        initInfo();
        initListener();
    }
}
