package com.fei.firstproject.activity;

import android.content.Intent;
import android.view.View;

import com.fei.firstproject.R;
import com.fei.firstproject.widget.AppHeadView;

import butterknife.OnClick;

/**
 * Created by Administrator on 2017/9/4.
 */

public class MyAddressActivity extends BaseListActivity {

    private static final int REQUEST_ACTIVITY_CODE_ADD = 200;

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
        dismissLoading();
        refreshLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void initView() {
        initBaseListButton();
        initAppHeadView();
    }

    private void initAppHeadView() {
        appHeadView.setFlHeadRightVisible(View.INVISIBLE);
        appHeadView.setMiddleStyle(AppHeadView.TEXT);
        appHeadView.setMiddleText(getResources().getString(R.string.my_address));
    }

    private void initBaseListButton() {
        btn_base_list.setVisibility(View.VISIBLE);
        btn_base_list.setText(R.string.add_address);
    }

    @OnClick(R.id.btn_base_list)
    void clickAddAddress(View view) {
        startActivityWithCode(new Intent(this, AddAddressOrUpdateActivity.class), REQUEST_ACTIVITY_CODE_ADD);
    }


}
