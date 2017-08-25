package com.fei.firstproject.fragment;

import android.os.Bundle;
import android.view.View;

import com.fei.firstproject.adapter.OrderAdapter;
import com.fei.firstproject.utils.LogUtils;

/**
 * Created by Administrator on 2017/8/25.
 */

public class OrderFragment extends BaseListFragment {

    private static final String INDEX = "index";
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
        LogUtils.i("tag", "index = " + index);
        test();
    }

    private void test() {
        dismissLoading();
        refreshLayout.setVisibility(View.VISIBLE);
        recyclerView.setAdapter(new OrderAdapter(activity));
    }
}
