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
import com.fei.firstproject.databinding.ViewCityDialogBinding;
import com.fei.firstproject.widget.CityPickerLayout;

public class CityDialog extends Dialog {

    Context mContext;
    TextView tvDialogConfirm;
    TextView tvDialogCancle;
    TextView tvDialogTitle;
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
        ViewCityDialogBinding binding = ViewCityDialogBinding.inflate(LayoutInflater.from(mContext));
        setContentView(binding.getRoot());
        cityPicker = binding.cityPicker;
        tvDialogConfirm = binding.tvDialogConfirm;
        tvDialogCancle = binding.tvDialogCancle;
        tvDialogTitle = binding.tvDialogTitle;
        clickCancle(binding.tvDialogCancle);
        clickConfirm(binding.tvDialogConfirm);
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

    void clickCancle(View view) {
        this.dismiss();
    }

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
