package com.fei.firstproject.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.fei.firstproject.R;

/**
 * Created by Administrator on 2017/8/8.
 */

public class TextSwitchView extends TextSwitcher implements ViewSwitcher.ViewFactory {

    private Context mContext;
    private String[] resources = {
            "阵雨28℃",
            "阵雨转雷阵雨32℃/27℃",
    };
    private int position = 0;
    private long timeDelay;

    private Handler mHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            checkPosition();
            TextSwitchView.this.setText(resources[position]);
            mHander.sendMessageDelayed(Message.obtain(), timeDelay);
        }
    };

    private void checkPosition() {
        position++;
        if (position == resources.length) {
            position = 0;
        }
    }

    public TextSwitchView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public TextSwitchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        setFactory(this);
        setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.in_animation));
        setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.out_animation));
    }

    @Override
    public View makeView() {
        TextView tv = new TextView(mContext);
//        tv.setTextSize(mContext.getResources().getDimension(R.dimen.tx_8));
        tv.setTextColor(ContextCompat.getColor(mContext, R.color.colorText));
        tv.setGravity(Gravity.CENTER_VERTICAL);
        Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.shape_red_dot);
        tv.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        tv.setCompoundDrawablePadding(30);
        return tv;
    }

    public void setTimeDelay(long timeDelay) {
        TextSwitchView.this.setText(resources[position]);
        this.timeDelay = timeDelay;
        mHander.sendMessageDelayed(Message.obtain(), timeDelay);
    }
}
