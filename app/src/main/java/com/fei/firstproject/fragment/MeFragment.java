package com.fei.firstproject.fragment;

import android.os.Bundle;

import com.fei.firstproject.R;

/**
 * Created by Administrator on 2017/7/29.
 */

public class MeFragment extends BaseFragment {

    @Override
    public void requestPermissionsBeforeInit() {

    }

    @Override
    public void permissionsDeniedCallBack(int requestCode) {

    }

    @Override
    public void permissionsGrantCallBack(int requestCode) {

    }

    @Override
    public int getContentViewResId() {
        return R.layout.fragment_me;
    }

    @Override
    public void init(Bundle savedInstanceState) {

    }
}
