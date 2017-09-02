package com.fei.firstproject.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.fei.firstproject.R;

import java.lang.reflect.Field;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class YearMonthDayDialog extends Dialog {

    @BindView(R.id.tv_dialog_confirm)
    TextView tvDialogConfirm;
    @BindView(R.id.tv_dialog_cancle)
    TextView tvDialogCancle;
    @BindView(R.id.tv_dialog_title)
    TextView tvDialogTitle;
    @BindView(R.id.datePicker)
    DatePicker datePicker;

    private Context mContext;
    private int year;
    private int month;
    private int day;

    private OnComfirmListener onComfirmListener;

    public void setOnComfirmListener(OnComfirmListener onComfirmListener) {
        this.onComfirmListener = onComfirmListener;
    }

    @SuppressLint("InlinedApi")
    public YearMonthDayDialog(Context context) {
        super(context, R.style.YearMonthDayStyle);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        initCalendar();
        initView();
        initData();
        initSetting();
        setPicker(datePicker);
    }

    private void initSetting() {
        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        int widthPixels = mContext.getResources().getDisplayMetrics().widthPixels;
        lp.width = widthPixels;
        lp.x = 0;
        lp.y = 0;
        dialogWindow.setAttributes(lp);
        setCanceledOnTouchOutside(true);
    }

    private void initData() {
        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            }
        });
    }

    private void initCalendar() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    private void initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_date_dialog, null);
        setContentView(view);
        ButterKnife.bind(this, view);
    }

    @SuppressLint("NewApi")
    private void setPicker(DatePicker datePicker) {
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

    @OnClick(R.id.tv_dialog_confirm)
    void clickConfirm(View view) {
        if (onComfirmListener != null) {
            onComfirmListener.onConfirm(datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth());
        }
        dismiss();
    }

    @OnClick(R.id.tv_dialog_cancle)
    void clickCancle(View view) {
        dismiss();
    }

    public interface OnComfirmListener {
        void onConfirm(int year, int monthOfYear, int dayOfMonth);
    }
} 