package com.fei.firstproject.activity;

import android.os.Bundle;
import android.widget.EditText;

import com.fei.firstproject.R;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/9/7.
 */

public class AboutUsActivity extends BaseActivity {

    @BindView(R.id.et)
    EditText et;

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
