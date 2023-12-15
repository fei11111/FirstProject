package com.fei.firstproject.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.fei.firstproject.utils.LogUtils;


/**
 * Created by Administrator on 2017/7/29.
 */

public class MyViewPager extends ViewPager {

    private float down_x;
    private float move_x;

    private DrawerLayout drawerLayout;

    public void setDrawerLayout(DrawerLayout drawerLayout) {
        this.drawerLayout = drawerLayout;
    }

    public MyViewPager(Context context) {
        super(context);
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                LogUtils.i("tag", "viewpager_dispatch_down");
                break;
            case MotionEvent.ACTION_MOVE:
                LogUtils.i("tag", "viewpager_dispatch_move");
                break;
            case MotionEvent.ACTION_UP:
                LogUtils.i("tag", "viewpager_dispatch_up");
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                LogUtils.i("tag", "viewpager_Intercept_down");
                break;
            case MotionEvent.ACTION_MOVE:
                LogUtils.i("tag", "viewpager_Intercept_move");
                break;
            case MotionEvent.ACTION_UP:
                LogUtils.i("tag", "viewpager_Intercept_up");
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                LogUtils.i("tag", "viewpager_onTouchEvent_down");
                break;
            case MotionEvent.ACTION_MOVE:
                LogUtils.i("tag", "viewpager_onTouchEvent_move");
                break;
            case MotionEvent.ACTION_UP:
                LogUtils.i("tag", "viewpager_onTouchEvent_up");
                break;
        }
        return super.onTouchEvent(ev);
    }

//    @OnTouch(R.id.vp_main)
//    boolean touch(View view, MotionEvent motionEvent) {
//        int action = motionEvent.getAction();
//        switch (action) {
//            case MotionEvent.ACTION_DOWN:
//                Log.i("tag", "viewpager_touch_down");
//                down_x = motionEvent.getX();
//                drawerLayout.requestDisallowInterceptTouchEvent(true);
//                break;
//            case MotionEvent.ACTION_MOVE:
//                Log.i("tag", "viewpager_touch_move");
//                move_x = motionEvent.getX();
//                int currentItem = getCurrentItem();
//                if (currentItem == 0 && move_x - down_x > 60f) {
//                    drawerLayout.requestDisallowInterceptTouchEvent(false);
//                } else {
//                    drawerLayout.requestDisallowInterceptTouchEvent(true);
//                }
//                break;
//            case MotionEvent.ACTION_UP:
//                Log.i("tag", "viewpager_touch_up");
//                break;
//        }
//        return false;
//    }


}
