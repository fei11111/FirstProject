package com.fei.firstproject.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.fei.firstproject.R;

/**
 * Created by Administrator on 2017/12/25.
 * 倒数
 */

public class ReciprocalView extends View {

    private int width;
    private int height;
    private RectF rectF;
    private Paint mPaint;
    private Paint textPaint;
    private float ratio = 0;
    private Path mPath;
    private PathMeasure pathMeasure;
    private Path mDst;
    private String content = "";

    public ReciprocalView(Context context) {
        this(context, null);
    }

    public ReciprocalView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ReciprocalView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(getResources().getColor(R.color.colorTextSub));
        mPaint.setStrokeWidth(2);
        mPaint.setStyle(Paint.Style.STROKE);
        mDst = new Path();
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(30);
        textPaint.setColor(getResources().getColor(R.color.colorTextSub));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        rectF = new RectF(0, 0, width, height);
        mPath = new Path();
        mPath.addCircle(width / 2, height / 2, height / 2, Path.Direction.CW);
        pathMeasure = new PathMeasure(mPath, false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!TextUtils.isEmpty(content)) {
            mDst.reset();
            ratio += 0.005f;
            if (ratio > 1) {
                ratio = 0;
            }
            float v = ratio * pathMeasure.getLength();
            pathMeasure.getSegment(v, pathMeasure.getLength(), mDst, true);
            canvas.drawPath(mDst, mPaint);
            float v1 = textPaint.measureText(content);
            int left = (int) (width / 2 - v1 / 2);
            Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
            int top = (int) (height / 2 + (Math.abs(fontMetrics.ascent) - fontMetrics.descent) / 2);
            canvas.drawText(content, left, top, textPaint);
            invalidate();
        }
    }

    public void setContent(String content) {
        this.content = content;
        invalidate();
    }
}

