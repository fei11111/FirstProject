package com.fei.firstproject.fragment;

import android.os.Bundle;

/**
 * Created by Administrator on 2017/8/25.
 */

public class OrderFragment extends BaseListFragment {

    private static final String INDEX = "index";
    private int index;

    //isAll=1&currentPage=1&userId=1119200&
    //待付款  orderFlagId=1&userId=1119200&
    //待发货 orderFlagId=10&userId=1119200&
    //待收货 orderFlagId=11&userId=1119200&
    //待评价 orderFlagId=4&userId=1119200&

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

    public static OrderFragment newInstance(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt(INDEX, position);
        OrderFragment orderFragment = new OrderFragment();
        orderFragment.setArguments(bundle);
        return orderFragment;
    }

    @Override
    public void initData() {
        index = getArguments().getInt(INDEX, 0);
    }
}
