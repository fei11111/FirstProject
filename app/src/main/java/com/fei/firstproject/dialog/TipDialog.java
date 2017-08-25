package com.fei.firstproject.dialog;

import android.app.Dialog;
import android.content.Context;
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
    private OnCancleListener onCancleListener;

    public void setOnConfirmListener(OnConfirmListener onConfirmListener) {
        this.onConfirmListener = onConfirmListener;
    }

    public void setOnCancleListener(OnCancleListener onCancleListener) {
        this.onCancleListener = onCancleListener;
    }

    public TipDialog(Context context) {
        super(context, R.style.DialogAnimationStyle);
        mContext = context;
        init();
    }

    public TipDialog(Context context, OnConfirmListener onConfirmListener, OnCancleListener onCancleListener) {
        super(context, R.style.DialogAnimationStyle);
        mContext = context;
        this.onConfirmListener = onConfirmListener;
        this.onCancleListener = onCancleListener;
        init();
    }

    public TipDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
        init();
    }

    public TipDialog(Context context, int theme, OnConfirmListener onConfirmListener, OnCancleListener onCancleListener) {
        super(context, theme);
        mContext = context;
        this.onConfirmListener = onConfirmListener;
        this.onCancleListener = onCancleListener;
        init();
    }

    private void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_tip_dialog, null);
        setContentView(view);
        ButterKnife.bind(this, view);
    }

    public void setContentText(String content) {
        if (!TextUtils.isEmpty(content)) {
            tvDialogContent.setText(content);
        }
    }

    public void setConfirmButtonText(String btnText) {
        if (!TextUtils.isEmpty(btnText)) {
            btnDialogConfirm.setText(btnText);
        }
    }

    @OnClick(R.id.iv_dialog_cancle)
    void clickCancle(View view) {
        if (onCancleListener != null) {
            onCancleListener.onClick(view);
        }
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

    public interface OnCancleListener {
        void onClick(View view);
    }
}
