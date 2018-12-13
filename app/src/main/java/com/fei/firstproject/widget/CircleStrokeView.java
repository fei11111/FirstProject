package com.fei.firstproject.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.fei.firstproject.R;

public class CircleStrokeView extends View {

    private static final float DEFAULT_CIRCLEWIDTH = 15;
    private Paint bgPaint;
    private Paint circlePaint;
    private float circleWidth;
    private int circleColor;
    private int bgCircleColor;
    private int centerX;
    private int centerY;
    private int radius;
    private RectF mRectF;
    private float sweepAngle = 0;

    public CircleStrokeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleStrokeView);
            circleWidth = typedArray.getDimension(R.styleable.CircleStrokeView_circleWidth, DEFAULT_CIRCLEWIDTH);
            bgCircleColor = typedArray.getColor(R.styleable.CircleStrokeView_bgCircleColor, context.getResources().getColor(R.color.colorWhite));
            circleColor = typedArray.getColor(R.styleable.CircleStrokeView_circleColor, context.getResources().getColor(R.color.colorBlueDark));
            typedArray.recycle();
        }
        initPaint();
    }

    private void initPaint() {
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(bgCircleColor);
        bgPaint.setStrokeWidth(circleWidth);
        bgPaint.setStyle(Paint.Style.STROKE);
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(circleColor);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(circleWidth);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        int min = Math.min(measuredWidth, measuredHeight);
        centerX = measuredWidth / 2;
        centerY = measuredHeight / 2;
        radius = (int) (min / 2 - circleWidth / 2);
        mRectF = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBgCirlce(canvas);
        drawRunCircle(canvas);
    }

    private void drawRunCircle(Canvas canvas) {
        if (sweepAngle <= 0)
            return;
        canvas.save();
        canvas.rotate(-90, centerX, centerY);
        canvas.drawArc(mRectF, 0, sweepAngle, false, circlePaint);
        canvas.restore();
    }

    private void drawBgCirlce(Canvas canvas) {
        canvas.drawCircle(centerX, centerY, radius, bgPaint);
    }

    public void startAnimator(final int total, final TextView tvTotal, final TextView tvPercent, final int time) {
        ValueAnimator animator = ValueAnimator.ofInt(0, total);
        animator.setDuration(time);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                sweepAngle = (int) animation.getAnimatedValue();
                if (tvTotal != null) {
                    tvTotal.setText(sweepAngle + "");
                }
                int percent = (int) (sweepAngle / total * 100);
                if (tvPercent != null) {
                    tvPercent.setText(percent + "");
                }
                sweepAngle = sweepAngle / total * 360;
                invalidate();
            }
        });
        animator.start();
    }

}
