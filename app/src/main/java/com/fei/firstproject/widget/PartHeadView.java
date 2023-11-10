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

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/8/3.
 */

public class PartHeadView extends RelativeLayout {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_arrow)
    ImageView ivArrow;
    @BindView(R.id.tv_desc)
    TextView tvDesc;
    @BindView(R.id.iv_left_view)
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
        LayoutInflater.from(mContext).inflate(R.layout.view_part_head, this);
        ButterKnife.bind(this);
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
