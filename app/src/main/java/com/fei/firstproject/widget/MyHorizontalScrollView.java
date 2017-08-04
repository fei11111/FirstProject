package com.fei.firstproject.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

/**
 * Created by Administrator on 2017/8/1.
 */

public class MyHorizontalScrollView extends HorizontalScrollView {

    private float down_x;
    private float move_x;

    public MyHorizontalScrollView(Context context) {
        super(context);
    }

    public MyHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            down_x = ev.getX();
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            move_x = ev.getX();
            int scrollX = getScrollX();
            int max = getChildAt(0).getMeasuredWidth() - getMeasuredWidth();
            if (scrollX == max && down_x - move_x > 60f) {
                return false;
            } else {
                scrollBy((int) (down_x - move_x), 0);
                down_x = move_x;
                return true;
            }
        }
        return true;
    }
}
