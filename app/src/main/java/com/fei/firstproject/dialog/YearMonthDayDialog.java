package com.fei.firstproject.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import com.fei.firstproject.R;

import java.lang.reflect.Field;

import butterknife.ButterKnife;


public class YearMonthDayDialog extends Dialog {

    Context mContext;

    @SuppressLint("InlinedApi")
    public YearMonthDayDialog(Context context) {
        super(context, R.style.DialogBottomStyle);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_date_dialog, null);
        setContentView(view);
        ButterKnife.bind(this, view);
    }

    @SuppressLint("NewApi")
    private void setDatePickerDividerColor(DatePicker datePicker) {
        // Divider changing:

        // 获取 mSpinners
        LinearLayout llFirst = (LinearLayout) datePicker.getChildAt(0);

        // 获取 NumberPicker 
        LinearLayout mSpinners = (LinearLayout) llFirst.getChildAt(0);
        for (int i = 0; i < mSpinners.getChildCount(); i++) {

            View view = mSpinners.getChildAt(i);

            if (view instanceof NumberPicker) {
                NumberPicker picker = (NumberPicker) mSpinners.getChildAt(i);

                Field[] pickerFields = NumberPicker.class.getDeclaredFields();
                for (Field pf : pickerFields) {
                    if (pf.getName().equals("mSelectionDivider")) {
                        pf.setAccessible(true);
                        try {
                            pf.set(picker, new ColorDrawable(mContext.getResources().getColor(R.color.colorPrimary)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            }
        }
    }

    protected void onStop() {
        /**
         * 注释
         */
        // super.onStop();
    }
} 