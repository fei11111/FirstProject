package com.fei.firstproject.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fei.firstproject.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/8/3.
 */

public class SettingView extends RelativeLayout {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_arrow)
    ImageView ivArrow;
    @BindView(R.id.tv_desc)
    TextView tvDesc;
    private Context mContext;
    private String title;
    private String desc;

    public SettingView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public SettingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initAttribute(attrs);
        initView();
    }

    public SettingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initAttribute(attrs);
        initView();
    }

    private void initAttribute(AttributeSet attrs) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.SettingView);
        title = typedArray.getString(R.styleable.SettingView_title);
        desc = typedArray.getString(R.styleable.SettingView_desc);
        typedArray.recycle();
    }

    private void initView() {
        setClickable(true);
        LayoutInflater.from(mContext).inflate(R.layout.view_setting, this);
        ButterKnife.bind(this);
        initContent();
    }

    private void initContent() {
        tvTitle.setText(title);
        tvDesc.setText(desc);
    }

}
