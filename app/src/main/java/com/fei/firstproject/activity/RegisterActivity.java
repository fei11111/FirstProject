package com.fei.firstproject.activity;

import android.os.Bundle;

import com.common.viewmodel.EmptyViewModel;
import com.fei.firstproject.R;
import com.fei.firstproject.databinding.ActivityRegisterBinding;


/**
 * Created by Fei on 2017/8/31.
 */

public class RegisterActivity extends BaseProjectActivity<EmptyViewModel, ActivityRegisterBinding> {

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
        setBackTitle(getString(R.string.register));
    }

    @Override
    public void initRequest() {

    }

    @Override
    public void createObserver() {

    }

    @Override
    public void initViewAndData(Bundle savedInstanceState) {

    }
}
