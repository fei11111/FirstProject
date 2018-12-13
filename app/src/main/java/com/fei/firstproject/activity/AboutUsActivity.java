package com.fei.firstproject.activity;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.fei.firstproject.R;
import com.fei.firstproject.widget.AppHeadView;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/9/7.
 */

public class AboutUsActivity extends BaseActivity {

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
        return R.layout.activity_about_us;
    }

    @Override
    public void initTitle() {
        setBackTitle(getString(R.string.about_us));
    }

    @Override
    public void init(Bundle savedInstanceState) {

    }

    @Override
    public void initRequest() {

    }

}
