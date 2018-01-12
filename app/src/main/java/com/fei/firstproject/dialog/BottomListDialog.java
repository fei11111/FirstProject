package com.fei.firstproject.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

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

    /**
     * @hide
     */
    @IntDef({VISIBLE, INVISIBLE, GONE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Visibility {
    }


    /**
     * This view is visible.
     * Use with {@link #setVisibility} and <a href="#attr_android:visibility">{@code
     * android:visibility}.
     */
    public static final int VISIBLE = 0x00000000;

    /**
     * This view is invisible, but it still takes up space for layout purposes.
     * Use with {@link #setVisibility} and <a href="#attr_android:visibility">{@code
     * android:visibility}.
     */
    public static final int INVISIBLE = 0x00000004;

    /**
     * This view is invisible, and it doesn't take any space for layout
     * purposes. Use with {@link #setVisibility} and <a href="#attr_android:visibility">{@code
     * android:visibility}.
     */
    public static final int GONE = 0x00000008;


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
        init();
    }

    public BottomListDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
        init();
    }

    private void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_list_dialog, null);
        setContentView(view);
        ButterKnife.bind(this, view);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int screenHeight = mContext.getResources().getDisplayMetrics().heightPixels;
        int statusBarHeight = Utils.getStatusBarHeight(mContext);
        int height = screenHeight - statusBarHeight;
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, height == 0 ? ViewGroup.LayoutParams.MATCH_PARENT : height);
        setDialogState();
    }

    /**
     * 修复bug
     * 原因是当我们向下拖拽时，BottomSheetBehavior的状态变为了STATE_HIDDEN，而BottomSheetDialog在内部用BottomSheetBehavior对状态做了处理，
     * 再次show之后，系统未恢复bottomSheetDialogBehavior的状态，还是隐藏
     * */
    private void setDialogState() {
        View view = getDelegate().findViewById(android.support.design.R.id.design_bottom_sheet);
        final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(view);
        //实现对状态改变的监听
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    dismiss();
                    //设置BottomSheetBehavior状态为STATE_COLLAPSED
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    public BottomListDialog setTitle(String title) {
        tvDialogTitle.setText(title);
        return this;
    }

    public BottomListDialog setAdapter(ListAdapter adapter) {
        lvDialog.setAdapter(adapter);
        return this;
    }

    public void setRlDialogHeadVisibility(@Visibility int visibility) {
        rlDialogHead.setVisibility(visibility);
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
