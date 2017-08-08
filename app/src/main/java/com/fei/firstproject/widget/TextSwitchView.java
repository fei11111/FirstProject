package com.fei.firstproject.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
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
            "静夜思",
            "床前明月光", "疑是地上霜",
            "举头望明月",
            "低头思故乡"
    };
    private int position = 0;
    private long timeDelay;

    private Handler mHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            position = getPosition();
            TextSwitchView.this.setText(resources[position]);
            mHander.sendMessageDelayed(Message.obtain(), timeDelay);
        }
    };

    private int getPosition() {
        position++;
        if (position == resources.length) {
            position = 0;
        }
        return position;
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
        tv.setTextSize(mContext.getResources().getDimension(R.dimen.tx_12));
        tv.setTextColor(mContext.getResources().getColor(R.color.colorText));
        return tv;
    }

    public void setTimeDelay(long timeDelay) {
        this.timeDelay = timeDelay;
        mHander.sendMessageDelayed(Message.obtain(), timeDelay);
    }
}
