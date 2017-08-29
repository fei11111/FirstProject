package com.fei.firstproject.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fei.firstproject.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnTextChanged;

/**
 * Created by Fei on 2017/8/15.
 */

public class AppHeadView extends RelativeLayout {

    @BindView(R.id.iv_head_left)
    ImageView ivHeadLeft;
    @BindView(R.id.tv_head_left)
    TextView tvHeadLeft;
    @BindView(R.id.fl_head_left)
    FrameLayout flHeadLeft;
    @BindView(R.id.iv_head_right)
    ImageView ivHeadRight;
    @BindView(R.id.tv_head_right)
    TextView tvHeadRight;
    @BindView(R.id.fl_head_right)
    FrameLayout flHeadRight;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.rl_search)
    RelativeLayout rlSearch;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_delete)
    ImageView ivDelete;

    private Context mContext;
    private int leftVisible = View.VISIBLE;
    private int leftStyle = 0;
    private String leftText;
    private int leftDrawable = -1;
    private int leftBackground = -1;
    private int leftPadding = 0;
    private int rightVisible = View.VISIBLE;
    private int rightStyle = 0;
    private String rightText;
    private int rightDrawable = -1;
    private int rightBackground = -1;
    private int rightPadding = 0;
    private int middleStyle = 0;
    private String middleText;
    private String middleSearchHint;
    private onAppHeadViewListener onAppHeadViewListener;

    public static final int TEXT = 0;
    public static final int SEARCH = 1;
    public static final int IMAGE = 2;

    public void setOnLeftRightClickListener(onAppHeadViewListener onAppHeadViewListener) {
        this.onAppHeadViewListener = onAppHeadViewListener;
    }

    public AppHeadView(Context context) throws Exception {
        super(context);
        throw (new Exception("没有直接new这种形式"));
    }

    public AppHeadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initAttribute(attrs);
    }

    public AppHeadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initAttribute(attrs);
    }

    private void initAttribute(AttributeSet attrs) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.AppHeadView);
        leftVisible = typedArray.getInt(R.styleable.AppHeadView_leftVisible, View.VISIBLE);
        leftStyle = typedArray.getInt(R.styleable.AppHeadView_leftStyle, 0);
        leftDrawable = typedArray.getResourceId(R.styleable.AppHeadView_leftDrawable, -1);
        leftText = typedArray.getString(R.styleable.AppHeadView_leftText);
        leftBackground = typedArray.getInt(R.styleable.AppHeadView_leftBackground, -1);
        leftPadding = typedArray.getDimensionPixelSize(R.styleable.AppHeadView_leftPadding, 0);
        rightVisible = typedArray.getInt(R.styleable.AppHeadView_rightVisible, View.VISIBLE);
        rightStyle = typedArray.getInt(R.styleable.AppHeadView_rightStyle, 0);
        rightDrawable = typedArray.getResourceId(R.styleable.AppHeadView_rightDrawable, -1);
        rightText = typedArray.getString(R.styleable.AppHeadView_rightText);
        rightBackground = typedArray.getResourceId(R.styleable.AppHeadView_rightBackground, -1);
        rightPadding = typedArray.getDimensionPixelSize(R.styleable.AppHeadView_rightPadding, 0);
        middleStyle = typedArray.getInt(R.styleable.AppHeadView_middleStyle, 0);
        middleText = typedArray.getString(R.styleable.AppHeadView_middleText);
        middleSearchHint = typedArray.getString(R.styleable.AppHeadView_middleSearchHint);
        typedArray.recycle();
        initView();
    }

    private void initView() {
        LayoutInflater.from(mContext).inflate(R.layout.app_head_view, this);
        ButterKnife.bind(this);
        initContent();
    }

    private void initContent() {
        setLayoutvisible(leftVisible, flHeadLeft);
        setLeftRightStyle(leftStyle, ivHeadLeft, tvHeadLeft);
        setImageResource(leftDrawable, ivHeadLeft);
        setText(leftText, tvHeadLeft);
        setBackgroundResource(leftBackground, flHeadLeft);
        setFrameLayoutPadding(flHeadLeft, leftPadding);
        setLayoutvisible(rightVisible, flHeadRight);
        setLeftRightStyle(rightStyle, ivHeadRight, tvHeadRight);
        setImageResource(rightDrawable, ivHeadRight);
        setText(rightText, tvHeadRight);
        setBackgroundResource(rightBackground, flHeadRight);
        setFrameLayoutPadding(flHeadRight, rightPadding);
        setMiddleStyle(middleStyle, rlSearch, tvTitle);
        setText(middleText, tvTitle);
        setMiddleSearchHint(middleSearchHint, etSearch);
    }

    private void setLayoutvisible(int visible, FrameLayout fl) {
        fl.setVisibility(visible);
    }

    private void setLeftRightStyle(int style, ImageView iv, TextView tv) {
        switch (style) {
            case TEXT:
                iv.setVisibility(View.GONE);
                tv.setVisibility(View.VISIBLE);
                break;
            case IMAGE:
                iv.setVisibility(View.VISIBLE);
                tv.setVisibility(View.GONE);
                break;
        }
    }

    private void setMiddleStyle(int style, RelativeLayout relativeLayout, TextView tv) {
        switch (style) {
            case TEXT:
                relativeLayout.setVisibility(View.GONE);
                tv.setVisibility(View.VISIBLE);
                break;
            case SEARCH:
                relativeLayout.setVisibility(View.VISIBLE);
                tv.setVisibility(View.GONE);
                break;
        }
    }

    private void setText(String text, TextView tv) {
        if (!TextUtils.isEmpty(text)) {
            tv.setText(text);
        }
    }

    private void setImageResource(int id, ImageView iv) {
        if (id != -1) {
            iv.setImageResource(id);
        }
    }

    private void setBackgroundResource(int id, FrameLayout fl) {
        if (id != -1) {
            fl.setBackgroundResource(id);
        }
    }

    private void setFrameLayoutPadding(FrameLayout fl, int padding) {
        fl.setPadding(0, padding, 0, padding);
    }

    private void setMiddleSearchHint(String text, EditText editText) {
        if (!TextUtils.isEmpty(text)) {
            editText.setHint(text);
        }
    }

    @OnClick({R.id.fl_head_left, R.id.fl_head_right})
    void onLeftRightClick(View view) {
        if (onAppHeadViewListener == null) return;
        switch (view.getId()) {
            case R.id.fl_head_left:
                onAppHeadViewListener.onLeft(view);
                break;
            case R.id.fl_head_right:
                etSearch.clearFocus();
                onAppHeadViewListener.onRight(view);
                break;
        }
    }

    @OnEditorAction(R.id.et_search)
    boolean search(TextView v, int actionId,
                   KeyEvent event) {
        if (onAppHeadViewListener == null) return false;
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            //点击搜索要做的操作
            etSearch.clearFocus();
            onAppHeadViewListener.onEdit(v, actionId, event);
            return true;
        }
        return false;
    }

    @OnTextChanged(value = R.id.et_search, callback = OnTextChanged.Callback.TEXT_CHANGED)
    void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!TextUtils.isEmpty(s)) {
            ivDelete.setVisibility(View.VISIBLE);
        } else {
            ivDelete.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.iv_delete)
    void delteEtText() {
        etSearch.setText("");
    }

    public void setFlHeadLeftVisible(int visible) {
        setLayoutvisible(visible, flHeadLeft);
    }

    public void setFlHeadRightVisible(int visible) {
        setLayoutvisible(visible, flHeadRight);
    }

    public void setMiddleSearchVisible(int visible) {
        rlSearch.setVisibility(visible);
    }

    public void setTvTitleVisible(int visible) {
        tvTitle.setVisibility(visible);
    }

    public void setMiddleText(String text) {
        tvTitle.setText(text);
    }

    public void setEtSearchText(String text) {
        etSearch.setText(text);
    }

    public String getEtSearchText() {
        return etSearch.getText().toString();
    }

    public void setLeftStyle(int leftStyle) {
        setLeftRightStyle(leftStyle, ivHeadLeft, tvHeadLeft);
    }

    public void setRightStyle(int rightStyle) {
        setLeftRightStyle(rightStyle, ivHeadRight, tvHeadRight);
    }

    public void setMiddleStyle(int middleStyle) {
        setMiddleStyle(middleStyle, rlSearch, tvTitle);
    }

    public void setLeftText(String leftText) {
        if (!TextUtils.isEmpty(leftText)) {
            tvHeadLeft.setText(leftText);
        }
    }

    public void setRightText(String rightText) {
        if (!TextUtils.isEmpty(rightText)) {
            tvHeadRight.setText(rightText);
        }
    }

    public void setMiddleSearchHint(String middleSearchHint) {
        if (!TextUtils.isEmpty(middleSearchHint)) {
            etSearch.setHint(middleSearchHint);
        }
    }

    public void setLeftDrawable(int drawable) {
        ivHeadLeft.setImageResource(drawable);
    }

    public void setRightDrawable(int drawable) {
        ivHeadRight.setImageResource(drawable);
    }

    public interface onAppHeadViewListener {
        public void onLeft(View view);

        public void onRight(View view);

        public void onEdit(TextView v, int actionId,
                           KeyEvent event);
    }
}
