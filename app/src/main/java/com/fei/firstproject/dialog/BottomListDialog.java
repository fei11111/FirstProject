package com.fei.firstproject.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fei.firstproject.R;
import com.fei.firstproject.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * Created by Administrator on 2017/8/3.
 */

public class BottomListDialog extends BottomSheetDialog {

    @BindView(R.id.tv_dialog_confirm)
    TextView tvDialogConfirm;
    @BindView(R.id.tv_dialog_cancle)
    TextView tvDialogCancle;
    @BindView(R.id.tv_dialog_title)
    TextView tvDialogTitle;
    @BindView(R.id.lv_dialog)
    ListView lvDialog;
    @BindView(R.id.rl_dialog_head)
    RelativeLayout rlDialogHead;

    private Context mContext;
    private OnCancleListener onCancleListener;
    private OnItemClickListener onItemClickListener;
    private OnConfirmListener onConfirmListener;
    private ListAdapter adapter;
    private String title;
    private int rlDialogHeadVisibility = -1;

    public void setOnCancleListener(OnCancleListener onCancleListener) {
        this.onCancleListener = onCancleListener;
    }

    public void setOnConfirmListener(OnConfirmListener onConfirmListener) {
        this.onConfirmListener = onConfirmListener;
        tvDialogConfirm.setVisibility(View.VISIBLE);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public BottomListDialog(Context context) {
        super(context);
        mContext = context;
    }

    public BottomListDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
    }

    private void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_list_dialog, null);
        setContentView(view);
        ButterKnife.bind(this, view);

        if (!TextUtils.isEmpty(title)) {
            tvDialogTitle.setText(title);
        }
        if (adapter != null) {
            lvDialog.setAdapter(adapter);
        }

        if (rlDialogHeadVisibility != -1) {
            if (rlDialogHeadVisibility == 0) {
                rlDialogHead.setVisibility(View.VISIBLE);
            } else if (rlDialogHeadVisibility == 4) {
                rlDialogHead.setVisibility(View.INVISIBLE);
            } else if (rlDialogHeadVisibility == 8) {
                rlDialogHead.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        initLayoutParam();
    }

    private void initLayoutParam() {
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAdapter(ListAdapter adapter) {
        this.adapter = adapter;
    }

    public void setRlDialogHeadVisibility(int visibility) {
        rlDialogHeadVisibility = visibility;
    }

    @OnClick(R.id.tv_dialog_cancle)
    void clickCancle(View view) {
        if (onCancleListener != null) {
            onCancleListener.onClick(view);
        }
        this.dismiss();
    }

    @OnClick(R.id.tv_dialog_confirm)
    void clickConfirm(View view) {
        if (onConfirmListener != null) {
            onConfirmListener.onClick(view);
        }
        this.dismiss();
    }

    @OnItemClick(R.id.lv_dialog)
    void clickItem(AdapterView<?> parent, View view, int position, long id) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(parent, view, position, id);
            this.dismiss();
        }
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        View view = getDelegate().findViewById(android.support.design.R.id.design_bottom_sheet);
        final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(view);
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            //设置BottomSheetBehavior状态为STATE_COLLAPSED
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    public interface OnCancleListener {
        void onClick(View view);
    }

    public interface OnConfirmListener {
        void onClick(View view);
    }

    public interface OnItemClickListener {
        void onItemClick(AdapterView<?> parent, View view, int position, long id);
    }

}
