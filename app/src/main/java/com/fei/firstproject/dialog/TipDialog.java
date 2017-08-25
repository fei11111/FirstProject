package com.fei.firstproject.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.fei.firstproject.R;

/**
 * Created by Administrator on 2017/8/3.
 */

public class TipDialog extends ProgressDialog {

    private Context mContext;

    public TipDialog(Context context) {
        super(context, R.style.DialogAnimationStyle);
        mContext = context;
    }

    public TipDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_tip_dialog, null);
        setContentView(view);
    }


    @Override
    public void show() {
        super.show();
    }

}
