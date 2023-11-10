package com.fei.firstproject.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by Administrator on 2017/9/1.
 */

public class LimitListView extends ListView {

    private int maxHeight;

    public LimitListView(@NonNull Context context) {
        super(context);
        init();
    }

    public LimitListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LimitListView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        maxHeight = getResources().getDisplayMetrics().heightPixels / 2;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        heightSize = heightSize <= maxHeight ? heightSize
                : maxHeight;
        int maxHeightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize,
                heightMode);
        super.onMeasure(widthMeasureSpec, maxHeightMeasureSpec);
    }

}
