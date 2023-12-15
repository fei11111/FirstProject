package com.fei.firstproject.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import com.common.viewmodel.EmptyViewModel;
import com.fei.firstproject.R;
import com.fei.firstproject.databinding.ActivityAboutUsBinding;


/**
 * Created by Administrator on 2017/9/7.
 */

public class AboutUsActivity extends BaseProjectActivity<EmptyViewModel, ActivityAboutUsBinding> {


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
        setBackTitle(getString(R.string.about_us));
    }

    @Override
    public void initRequest() {

    }

    @Override
    public void createObserver() {

    }

    @Override
    public void initViewAndData(Bundle savedInstanceState) {
        mChildBinding.et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mChildBinding.et.removeTextChangedListener(this);
                if (s == null || s.length() == 0) {
                    return;
                }
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < s.length(); i++) {
                    if (s.charAt(i) != ' ') {
                        sb.append(s.charAt(i));
                        if (sb.length() == 4 || sb.length() == 8) {
                            sb.insert(sb.length() - 1, ' ');
                        }
                    }
                }

                mChildBinding.et.setText(sb.toString());
                mChildBinding.et.setSelection(sb.length());
                mChildBinding.et.addTextChangedListener(this);
            }
        });
    }
}
