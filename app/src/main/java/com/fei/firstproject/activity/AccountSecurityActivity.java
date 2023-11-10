package com.fei.firstproject.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.fei.firstproject.R;
import com.fei.firstproject.config.AppConfig;
import com.fei.firstproject.entity.UserEntity;
import com.fei.firstproject.utils.Utils;
import com.fei.firstproject.widget.PartHeadView;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/9/7.
 */

public class AccountSecurityActivity extends BaseActivity {

    @BindView(R.id.tv_user_name)
    TextView tvUserName;
    @BindView(R.id.phv_phone)
    PartHeadView phvPhone;
    @BindView(R.id.phv_update_password)
    PartHeadView phvUpdatePassword;

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
        return R.layout.activity_account_security;
    }

    @Override
    public void initTitle() {
        setBackTitle(getString(R.string.account_security));
    }

    @Override
    public void init(Bundle savedInstanceState) {
        initInfo();
    }

    private void initInfo() {
        UserEntity user = AppConfig.user;
        String nameString = user.getNameString();
        String userName = user.getUserName();
        String name = user.getName();
        if (!TextUtils.isEmpty(userName)) {
            tvUserName.setText(userName);
        } else if (!TextUtils.isEmpty(nameString)) {
            tvUserName.setText(nameString);
        } else if (!TextUtils.isEmpty(name)) {
            tvUserName.setText(name);
        }

        String mobile = user.getMobile();
        if (!TextUtils.isEmpty(mobile) && mobile.length() == 11) {
            mobile = Utils.userNameReplaceWithStar(mobile);
        }
        phvPhone.setDesc(mobile);

    }

    @Override
    public void initRequest() {

    }

}
