package com.fei.firstproject.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                et.removeTextChangedListener(this);
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

                et.setText(sb.toString());
                et.setSelection(sb.length());
                et.addTextChangedListener(this);
            }
        });
    }

    @Override
    public void initRequest() {

    }

}
