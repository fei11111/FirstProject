package com.fei.banner.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.nineoldandroids.view.ViewHelper;

import java.util.HashMap;
import java.util.LinkedHashMap;


public class BannerViewPager extends ViewPager {

    private boolean scrollable = true;
    private View mLeft;
    private View mRight;
    private float mRot;
    private State mState;
    private int oldPage;
    private int count = 0;

    public void setObjectForPosition(Object obj, int position) {
        mObjs.put(Integer.valueOf(position), obj);
    }

    public void setCount(int count) {
        this.count = count;
    }

    private enum State {
        IDLE,
        GOING_LEFT,
        GOING_RIGHT
    }

    private HashMap<Integer, Object> mObjs = new LinkedHashMap<Integer, Object>();

    private static final boolean API_11;

    static {
        API_11 = Build.VERSION.SDK_INT >= 11;
    }

    public BannerViewPager(Context context) {
        super(context);
    }

    public BannerViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        setClipChildren(false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return this.scrollable && super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return this.scrollable && super.onInterceptTouchEvent(ev);
    }

    public void setScrollable(boolean scrollable) {
        this.scrollable = scrollable;
    }

    @Override
    protected void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        if (position == count) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            return;
        }

        if (mState == State.IDLE && positionOffset > 0) {
            oldPage = getCurrentItem();
            mState = position == oldPage ? State.GOING_RIGHT : State.GOING_LEFT;
        }
        boolean goingRight = position == oldPage;
        if (mState == State.GOING_RIGHT && !goingRight)
            mState = State.GOING_LEFT;
        else if (mState == State.GOING_LEFT && goingRight)
            mState = State.GOING_RIGHT;

        float effectOffset = isSmall(positionOffset) ? 0 : positionOffset;

        mLeft = findViewFromObject(position);
        mRight = findViewFromObject(position + 1);
        animateFade(mLeft, mRight, effectOffset);
        animateCube(mLeft, mRight, effectOffset, false);
        super.onPageScrolled(position, positionOffset, positionOffsetPixels);

        if (effectOffset == 0) {
            disableHardwareLayer();
            mState = State.IDLE;
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void disableHardwareLayer() {
        if (!API_11) return;
        View v;
        for (int i = 0; i < getChildCount(); i++) {
            v = getChildAt(i);
            if (v.getLayerType() != View.LAYER_TYPE_NONE)
                v.setLayerType(View.LAYER_TYPE_NONE, null);
        }
    }

    private void animateCube(View left, View right, float positionOffset, boolean in) {
        if (mState != State.IDLE) {
            if (left != null) {
                manageLayer(left, true);
                mRot = (in ? 90.0f : -90.0f) * positionOffset;
                ViewHelper.setPivotX(left, left.getMeasuredWidth());
                ViewHelper.setPivotY(left, left.getMeasuredHeight() * 0.5f);
                ViewHelper.setRotationY(left, mRot);
            }
            if (right != null) {
                manageLayer(right, true);
                mRot = -(in ? 90.0f : -90.0f) * (1 - positionOffset);
                ViewHelper.setPivotX(right, 0);
                ViewHelper.setPivotY(right, right.getMeasuredHeight() * 0.5f);
                ViewHelper.setRotationY(right, mRot);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void manageLayer(View v, boolean enableHardware) {
        if (!API_11) return;
        int layerType = enableHardware ? View.LAYER_TYPE_HARDWARE : View.LAYER_TYPE_NONE;
        if (layerType != v.getLayerType())
            v.setLayerType(layerType, null);
    }

    protected void animateFade(View left, View right, float positionOffset) {
        if (left != null) {
            ViewHelper.setAlpha(left, 1 - positionOffset);
        }
        if (right != null) {
            ViewHelper.setAlpha(right, positionOffset);
        }
    }

    private boolean isSmall(float positionOffset) {
        return Math.abs(positionOffset) < 0.0001;
    }

    public View findViewFromObject(int position) {
        Object o = mObjs.get(Integer.valueOf(position));
        if (o == null) {
            return null;
        }
        PagerAdapter a = getAdapter();
        View v;
        for (int i = 0; i < getChildCount(); i++) {
            v = getChildAt(i);
            if (a.isViewFromObject(v, o))
                return v;
        }
        return null;
    }

}
