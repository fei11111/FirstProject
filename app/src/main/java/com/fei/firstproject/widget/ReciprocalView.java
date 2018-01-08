package com.fei.firstproject.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.fei.firstproject.R;
import com.fei.firstproject.utils.Utils;

/**
 * Created by Administrator on 2017/12/25.
 * 倒数
 */

public class ReciprocalView extends View {

    private Paint mPaint;
    private Paint textPaint;
    private float ratio = 0;
    private Path srcPath;
    private PathMeasure pathMeasure;
    private Path dstPath;
    private String content = "";
    private int centerX = 0;
    private int centerY = 0;

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
        initPaint();
        initPath();
    }

    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(getResources().getColor(R.color.colorTextSub));
        mPaint.setStrokeWidth(2);
        mPaint.setStyle(Paint.Style.STROKE);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(Utils.sp2px(getContext(),11f));
        textPaint.setColor(getResources().getColor(R.color.colorTextSub));
    }

    private void initPath() {
        dstPath = new Path();
        srcPath = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (centerX == 0) {
            int width = getMeasuredWidth();
            int height = getMeasuredHeight();
            centerX = width / 2;
            centerY = height / 2;
            srcPath.addCircle(centerX, centerY, centerX - 5, Path.Direction.CW);
            pathMeasure = new PathMeasure(srcPath, false);
        }
        if (!TextUtils.isEmpty(content)) {
            dstPath.reset();
            ratio += 0.005f;
            if (ratio > 1) {
                ratio = 0;
            }
            float v = ratio * pathMeasure.getLength();
            pathMeasure.getSegment(v, pathMeasure.getLength(), dstPath, true);
            canvas.drawPath(dstPath, mPaint);
            float textLength = textPaint.measureText(content);
            int left = (int) (centerX - textLength / 2);
            Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
            int top = (int) (centerY + (Math.abs(fontMetrics.ascent) - fontMetrics.descent) / 2);
            canvas.drawText(content, left, top, textPaint);
            invalidate();
        }
    }

    public void setContent(String content) {
        this.content = content;
        invalidate();
    }

}

