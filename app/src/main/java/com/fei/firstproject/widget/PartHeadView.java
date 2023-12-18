package com.fei.firstproject.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.fei.firstproject.R;
import com.fei.firstproject.databinding.ViewPartHeadBinding;


/**
 * Created by Administrator on 2017/8/3.
 */

public class PartHeadView extends RelativeLayout {

    TextView tvTitle;
    ImageView ivArrow;
    TextView tvDesc;
    ImageView ivLeftView;
    private Context mContext;
    private String title;
    private String desc;
    private int leftIcon = -1;

    public PartHeadView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public PartHeadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initAttribute(attrs);
        initView();
    }

    public PartHeadView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initAttribute(attrs);
        initView();
    }

    private void initAttribute(AttributeSet attrs) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.PartHeadView);
        title = typedArray.getString(R.styleable.PartHeadView_title);
        desc = typedArray.getString(R.styleable.PartHeadView_desc);
        leftIcon = typedArray.getResourceId(R.styleable.PartHeadView_leftIcon, -1);
        typedArray.recycle();
    }

    private void initView() {
        setClickable(true);
        ViewPartHeadBinding binding = ViewPartHeadBinding.inflate(LayoutInflater.from(getContext()),this,true);
        tvTitle = binding.tvTitle;
        ivArrow = binding.ivArrow;
        tvDesc = binding.tvDesc;
        ivLeftView = binding.ivLeftView;
        initContent();
    }

    private void initContent() {
        tvTitle.setText(title);
        tvDesc.setText(desc);
        if (leftIcon != -1) {
            ivLeftView.setVisibility(View.VISIBLE);
            ivLeftView.setImageResource(leftIcon);
        } else {
            ivLeftView.setVisibility(View.GONE);
        }
    }

    public void setDesc(String desc) {
        this.desc = desc;
        tvDesc.setText(desc);
    }

    public String getDesc() {
        return desc;
    }
}
