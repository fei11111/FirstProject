package com.fei.firstproject.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.fei.firstproject.R;
import com.fei.firstproject.databinding.ViewTipDialogBinding;


/**
 * Created by Administrator on 2017/8/3.
 */

public class TipDialog extends Dialog {

    ImageView ivDialogCancle;
    TextView tvDialogContent;
    Button btnDialogConfirm;

    private Context mContext;
    private OnConfirmListener onConfirmListener;
    private String confirmText;
    private String contentText;

    public void setOnConfirmListener(OnConfirmListener onConfirmListener) {
        this.onConfirmListener = onConfirmListener;
    }

    public TipDialog(Context context) {
        super(context, R.style.DialogCenterStyle);
        mContext = context;
    }

    public TipDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        ViewTipDialogBinding binding = ViewTipDialogBinding.inflate(LayoutInflater.from(mContext));
        setContentView(binding.getRoot());

        ivDialogCancle = binding.ivDialogCancle;
        tvDialogContent = binding.tvDialogContent;
        btnDialogConfirm = binding.btnDialogConfirm;

        binding.ivDialogCancle.setOnClickListener(v -> clickCancle(binding.ivDialogCancle));
        binding.btnDialogConfirm.setOnClickListener(v -> {
            clickConfirm(binding.btnDialogConfirm);
        });

        if (!TextUtils.isEmpty(contentText)) {
            tvDialogContent.setText(contentText);
        }

        if (!TextUtils.isEmpty(confirmText)) {
            btnDialogConfirm.setText(confirmText);
        }
    }

    public void setContentText(String content) {
        this.contentText = content;
        if (tvDialogContent != null) {
            tvDialogContent.setText(contentText);
        }
    }

    public void setConfirmButtonText(String btnText) {
        this.confirmText = btnText;
        if (btnDialogConfirm != null) {
            btnDialogConfirm.setText(confirmText);
        }
    }

    void clickCancle(View view) {
        this.dismiss();
    }

    void clickConfirm(View view) {
        if (onConfirmListener != null) {
            onConfirmListener.onClick(view);
        }
        this.dismiss();
    }

    @Override
    public void show() {
        super.show();
    }

    public interface OnConfirmListener {
        void onClick(View view);
    }

}
