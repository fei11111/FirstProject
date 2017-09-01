package com.fei.firstproject.widget;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.fei.firstproject.R;

/**
 * Created by Administrator on 2017/9/1.
 */

public class LimitLayout extends FrameLayout {

    private int maxWidth;
    private int maxHeight;

    public LimitLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public LimitLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LimitLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        maxWidth = getResources().getDimensionPixelSize(R.dimen.size_320);
        maxHeight = getResources().getDisplayMetrics().heightPixels / 2;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        widthSize = widthSize <= maxWidth ? widthSize
                : (int) maxWidth;
        heightSize = heightSize <= maxHeight ? heightSize
                : (int) maxHeight;
        int maxHeightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize,
                heightMode);
        int maxWidthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize,
                widthMode);
        super.onMeasure(maxWidthMeasureSpec, maxHeightMeasureSpec);
    }

}
