package com.fei.firstproject.activity;

import android.view.View;

import com.fei.firstproject.R;
import com.fei.firstproject.widget.AppHeadView;

/**
 * Created by Administrator on 2017/9/11.
 */

public class RetailersInfoActivity extends BaseListActivity {

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
    public void initView() {
        initAppHeadView();
    }

    private void initAppHeadView() {
        appHeadView.setFlHeadRightVisible(View.INVISIBLE);
        appHeadView.setMiddleStyle(AppHeadView.TEXT);
        appHeadView.setMiddleText(getResources().getString(R.string.retailers_info));
    }
}
