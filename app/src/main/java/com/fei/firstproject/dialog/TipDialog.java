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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/8/3.
 */

public class TipDialog extends Dialog {

    @BindView(R.id.iv_dialog_cancle)
    ImageView ivDialogCancle;
    @BindView(R.id.tv_dialog_content)
    TextView tvDialogContent;
    @BindView(R.id.btn_dialog_confirm)
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_tip_dialog, null);
        setContentView(view);
        ButterKnife.bind(this, view);

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

    @OnClick(R.id.iv_dialog_cancle)
    void clickCancle(View view) {
        this.dismiss();
    }

    @OnClick(R.id.btn_dialog_confirm)
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
