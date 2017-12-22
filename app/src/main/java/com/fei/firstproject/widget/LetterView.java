package com.fei.firstproject.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.fei.firstproject.R;

/**
 * Created by Administrator on 2017/12/18.
 */

public class LetterView extends View {

    private static final String[] letters = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L",
            "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#"};
    private Paint mPaint;
    private int viewHeight = 0;
    private int viewWidth = 0;
    private int currentPosition = 0;
    private int perHeight;
    private onLetterListener onLetterListener;

    public LetterView(Context context) {
        super(context);
    }

    public LetterView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public LetterView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.tx_18));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (viewWidth == 0) {
            viewHeight = getMeasuredHeight();
            viewWidth = getMeasuredWidth();
            perHeight = viewHeight / letters.length;
        }

        for (int i = 0; i < letters.length; i++) {
            if (i == currentPosition) {
                mPaint.setColor(Color.BLUE);
            } else {
                mPaint.setColor(Color.BLACK);
            }
            float width = mPaint.measureText(letters[i]);
            canvas.drawText(letters[i], viewWidth / 2 - width / 2, perHeight * (i + 1), mPaint);
        }
    }

    public void updateViewByLetter(int selection) {
        for (int i = 0; i < letters.length; i++) {
            if (letters[i].charAt(0) == selection) {
                currentPosition = i;
                invalidate();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float y = event.getY();
        int position = (int) (y / perHeight);
        currentPosition = position;
        if (event.getAction() == MotionEvent.ACTION_UP) {
            setBackgroundColor(getResources().getColor(android.R.color.transparent));
            if (onLetterListener != null) {
                onLetterListener.onRelease();
            }
        } else {
            setBackgroundColor(getResources().getColor(R.color.colorAlphaBackground));
            if (onLetterListener != null) {
                if (position >= 0 && position < letters.length) {
                    onLetterListener.onLetterCallBack(letters[position]);
                }
            }
        }
        if (position >= 0 && position < letters.length) {
            invalidate();
        }
        return true;
    }

    public void setOnLetterListener(LetterView.onLetterListener onLetterListener) {
        this.onLetterListener = onLetterListener;
    }

    public interface onLetterListener {
        void onLetterCallBack(String letter);

        void onRelease();
    }
}
