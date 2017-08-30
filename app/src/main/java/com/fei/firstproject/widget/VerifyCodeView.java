package com.fei.firstproject.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.fei.firstproject.R;

import java.util.Random;

/**
 * Created by Administrator on 2017/8/30.
 * 验证码
 */

public class VerifyCodeView extends View {

    private static final char[] LETTERS = {
            '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm',
            'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };
    private String code;
    private int codeLength = 4;
    //padding值
    private int base_padding_left = 0,
            range_padding_left = 0,
            base_padding_top = 0,
            range_padding_top = 0;
    private int padding_left, padding_top;
    private int fontSize;
    //验证码的默认宽高
    private Paint mPaint;
    private Random random = new Random();
    private Bitmap bitmap;
    private int viewWidth;
    private int viewHeight;

    public VerifyCodeView(Context context) throws Exception {
        super(context);
        throw new Exception("没有new方法实现");
    }

    public VerifyCodeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        initPaint();
        initCode();
        float density = getResources().getDisplayMetrics().density;
        base_padding_left = (int) (10 * density + 0.5f);
        base_padding_top = (int) (20 * density + 0.5f);
        range_padding_left = (int) (15 * density + 0.5f);
        range_padding_top = (int) (15 * density + 0.5f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (viewWidth == 0) {
            viewWidth = getMeasuredWidth();
            viewHeight = getMeasuredHeight();
        }
    }

    private void initCode() {
        code = randomCode();
    }

    private void initPaint() {
        fontSize = getResources().getDimensionPixelSize(R.dimen.tx_25);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(1);
        mPaint.setTextSize(fontSize);
    }

    private int randomColor() {
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);
        return Color.rgb(red, green, blue);
    }

    private String randomCode() {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < codeLength; i++) {
            stringBuffer.append(LETTERS[random.nextInt(LETTERS.length)]);
        }
        return stringBuffer.toString();
    }

    //随机生成文字样式，颜色，粗细，倾斜度
    private void randomTextStyle(Paint paint) {
        int color = randomColor();
        paint.setColor(color);
        paint.setFakeBoldText(random.nextBoolean());  //true为粗体，false为非粗体
//        float skewX = random.nextInt(11) / 10;
//        skewX = random.nextBoolean() ? skewX : -skewX;
//        paint.setTextSkewX(skewX); //float类型参数，负数表示右斜，整数左斜
    }

    //随机生成padding值
    private void randomPadding() {
        padding_left += base_padding_left + random.nextInt(range_padding_left);
        padding_top = base_padding_top + random.nextInt(range_padding_top);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (bitmap == null) {
            drawCode();
        }
        canvas.drawBitmap(bitmap, 0, 0, mPaint);
        super.onDraw(canvas);
    }

    private void drawCode() {
        bitmap = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        for (int i = 0; i < codeLength; i++) {
            randomTextStyle(mPaint);
            randomPadding();
            canvas.drawText(code.charAt(i) + "", padding_left, padding_top, mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_UP:
                refreshCode();
                return false;
        }
        return super.onTouchEvent(event);
    }

    public void refreshCode() {
        bitmap = null;
        code = randomCode();
        padding_left = 0;
        padding_top = 0;
        invalidate();
    }

    public String getCode() {
        return code;
    }
}
