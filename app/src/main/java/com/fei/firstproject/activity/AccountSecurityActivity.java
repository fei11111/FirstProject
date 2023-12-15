package com.fei.firstproject.activity;

import android.os.Bundle;
import android.text.TextUtils;

import com.common.viewmodel.EmptyViewModel;
import com.fei.firstproject.R;
import com.fei.firstproject.config.AppConfig;
import com.fei.firstproject.databinding.ActivityAccountSecurityBinding;
import com.fei.firstproject.entity.UserEntity;
import com.fei.firstproject.utils.Utils;

/**
 * Created by Administrator on 2017/9/7.
 */

public class AccountSecurityActivity extends BaseProjectActivity<EmptyViewModel, ActivityAccountSecurityBinding> {


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
    public void initTitle() {
        setBackTitle(getString(R.string.account_security));
    }


    private void initInfo() {
        UserEntity user = AppConfig.user;
        String nameString = user.getNameString();
        String userName = user.getUserName();
        String name = user.getName();
        if (!TextUtils.isEmpty(userName)) {
            mChildBinding.tvUserName.setText(userName);
        } else if (!TextUtils.isEmpty(nameString)) {
            mChildBinding.tvUserName.setText(nameString);
        } else if (!TextUtils.isEmpty(name)) {
            mChildBinding.tvUserName.setText(name);
        }

        String mobile = user.getMobile();
        if (!TextUtils.isEmpty(mobile) && mobile.length() == 11) {
            mobile = Utils.userNameReplaceWithStar(mobile);
        }
        mChildBinding.phvPhone.setDesc(mobile);

    }

    @Override
    public void initRequest() {

    }

    @Override
    public void createObserver() {

    }

    @Override
    public void initViewAndData(Bundle savedInstanceState) {
        initInfo();
    }
}
