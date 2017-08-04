package com.fei.firstproject.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.fei.firstproject.R;
import com.fei.firstproject.utils.Utils;

/**
 * Created by Administrator on 2017/8/4.
 */

public class ItemDecoration extends RecyclerView.ItemDecoration {

    private Drawable mDivider;
    private Paint mPaint;
    private Context mContext;
    private int space;

    public ItemDecoration(Context mContext) {
        this.mContext = mContext;
        int color = mContext.getResources().getColor(R.color.colorGray);
        space = Utils.dip2px(mContext, 2);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(color);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
//        if (parent.getLayoutManager() != null) {
//            if (parent.getLayoutManager() instanceof LinearLayoutManager && !(parent.getLayoutManager() instanceof GridLayoutManager)) {
//                if (((LinearLayoutManager) parent.getLayoutManager()).getOrientation() == LinearLayoutManager.HORIZONTAL) {
//                    outRect.set(space, 0, space, 0);
//                } else {
//                    outRect.set(0, space, 0, space);
//                }
//            } else {
//                outRect.set(space, space, space, space);
//            }
//        }
        outRect.set(0, space, 0, 0);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int top = parent.getPaddingTop();
        int bottom = parent.getHeight() - parent.getPaddingBottom();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
            int left = view.getWidth() + layoutParams.rightMargin;
            int right = left + space;
            c.drawRect(left, top, right, bottom,mPaint);
        }
    }
}
