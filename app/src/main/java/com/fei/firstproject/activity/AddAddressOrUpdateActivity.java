package com.fei.firstproject.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.fei.firstproject.R;
import com.fei.firstproject.utils.Utils;
import com.fei.firstproject.widget.AppHeadView;

import butterknife.OnClick;

/**
 * Created by Administrator on 2017/9/4.
 */

public class AddAddressOrUpdateActivity extends BaseActivity {

    private static final int REQUEST_PERMISSION_CODE_MAP = 100;
    private static final int REQUEST_ACTIVITY_CODE_MAP = 200;

    @Override
    public void permissionsDeniedCallBack(int requestCode) {
        showMissingPermissionDialog("需要获取定位权限才能添加准确地址", REQUEST_PERMISSION_CODE_MAP);
    }

    @Override
    public void permissionsGrantCallBack(int requestCode) {
        if (requestCode == REQUEST_PERMISSION_CODE_MAP) {
            startActivityWithCode(new Intent(this, MapActivity.class), REQUEST_ACTIVITY_CODE_MAP);
        }
    }

    @Override
    public void permissionDialogDismiss(int requestCode) {
        Utils.showToast(this, "获取定位权限失败，无法添加地址");
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_add_or_update_address;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        initTitle();
        initListener();
    }

    private void initTitle() {

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

    @OnClick(R.id.rl_address)
    void clickAddress(View view) {
        checkPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_CODE_MAP);
    }
}
