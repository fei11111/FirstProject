package com.fei.firstproject.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.fei.firstproject.R;
import com.fei.firstproject.widget.CityPickerLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CityDialog extends Dialog {

    Context mContext;
    @BindView(R.id.tv_dialog_confirm)
    TextView tvDialogConfirm;
    @BindView(R.id.tv_dialog_cancle)
    TextView tvDialogCancle;
    @BindView(R.id.tv_dialog_title)
    TextView tvDialogTitle;
    @BindView(R.id.cityPicker)
    CityPickerLayout cityPicker;

    private OnConfirmListener onConfirmListener;

    public void setOnConfirmListener(OnConfirmListener onConfirmListener) {
        this.onConfirmListener = onConfirmListener;
    }

    public CityDialog(Context context) {
        super(context, R.style.DialogBottomStyle);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initSetting();
    }

    private void initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_city_dialog, null);
        setContentView(view);
        ButterKnife.bind(this, view);
    }

    private void initSetting() {
        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;
        lp.y = 0;
        dialogWindow.setAttributes(lp);
        setCanceledOnTouchOutside(true);
    }

    @OnClick(R.id.tv_dialog_cancle)
    void clickCancle(View view) {
        this.dismiss();
    }

    @OnClick(R.id.tv_dialog_confirm)
    void clickConfirm(View view) {
        if (onConfirmListener != null) {
            onConfirmListener.onClick(cityPicker.getProvince_string(), cityPicker.getCity_string(), cityPicker.getCountry_string());
        }
        this.dismiss();
    }

    public interface OnConfirmListener {
        void onClick(String province, String city, String couny);
    }

}
