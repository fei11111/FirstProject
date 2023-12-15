package com.fei.firstproject.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;

import com.fei.firstproject.R;
import com.fei.firstproject.adapter.BottomExpandAdapter;
import com.fei.firstproject.databinding.ViewExpandListDialogBinding;
import com.fei.firstproject.entity.ObjectEntity;
import com.fei.firstproject.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;


public class BottomExpandListDialog extends BottomSheetDialog {

    ExpandableListView elv;

    private Context mContext;
    private BottomExpandAdapter adapter;
    private List<ObjectEntity> objectEntityList;

    public BottomExpandListDialog(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    public BottomExpandListDialog(@NonNull Context context, int theme) {
        super(context, theme);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        initLayoutParam();
    }

    private void init() {
        ViewExpandListDialogBinding binding = ViewExpandListDialogBinding.inflate(LayoutInflater.from(mContext));
        setContentView(binding.getRoot());
        elv = binding.elv;

        if (objectEntityList == null) {
            try {
                throw new Exception("没有设置数据");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (adapter == null) {
            adapter = new BottomExpandAdapter(mContext, objectEntityList);
            elv.setAdapter(adapter);
        }

    }

    private void initLayoutParam() {
        int screenHeight = mContext.getResources().getDisplayMetrics().heightPixels;
        int statusBarHeight = Utils.getStatusBarHeight(mContext);
        int height = screenHeight - statusBarHeight;
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, height == 0 ? ViewGroup.LayoutParams.MATCH_PARENT : height);
    }

    public void setObjectEntityList(List<ObjectEntity> objectEntityList) {
        this.objectEntityList = objectEntityList;
    }
}
