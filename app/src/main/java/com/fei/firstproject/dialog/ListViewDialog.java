package com.fei.firstproject.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;

import com.fei.firstproject.R;
import com.fei.firstproject.widget.LimitListView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * Created by Administrator on 2017/8/3.
 */

public class ListViewDialog extends Dialog {

    @BindView(R.id.lv_dialog)
    LimitListView lvDialog;
    @BindView(R.id.btn_dialog_cancle)
    Button btnDialogCancle;

    private Context mContext;
    private OnCancleListener onCancleListener;
    private OnItemClickListener onItemClickListener;

    public void setOnCancleListener(OnCancleListener onCancleListener) {
        this.onCancleListener = onCancleListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public ListViewDialog(Context context) {
        super(context, R.style.DialogAnimationStyle);
        mContext = context;
        init();
    }

    public void setAdapter(ListAdapter adapter) {
        lvDialog.setAdapter(adapter);
    }

    public ListViewDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
        init();
    }


    private void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_list_dialog, null);
        setContentView(view);
        ButterKnife.bind(this, view);
    }

    @OnClick(R.id.btn_dialog_cancle)
    void clickCancle(View view) {
        if (onCancleListener != null) {
            onCancleListener.onClick(view);
        }
        this.dismiss();
    }

    @OnItemClick(R.id.lv_dialog)
    void clickItem(AdapterView<?> parent, View view, int position, long id) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(parent, view, position, id);
        }
        this.dismiss();
    }

    @Override
    public void show() {
        super.show();
    }

    public interface OnCancleListener {
        void onClick(View view);
    }

    public interface OnItemClickListener {
        void onItemClick(AdapterView<?> parent, View view, int position, long id);
    }
}
