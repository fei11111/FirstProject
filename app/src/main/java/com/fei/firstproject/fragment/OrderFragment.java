package com.fei.firstproject.fragment;

/**
 * Created by Administrator on 2017/8/25.
 */

public class OrderFragment extends BaseListFragment {

    private int index;

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
    public void initRequest() {

    }

    @Override
    public void initData() {
        index = getArguments().getInt("index", 0);
    }
}
