package com.fei.firstproject.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;

@SuppressLint("NewApi")
public class StatusBarUtils {
    /**
     * 不可用颜色值
     */
    private static final int COLOR_INVALID_VAL = -1;
    /**
     * 默认颜色值
     */
    private static final int COLOR_DEFAULT = Color.parseColor("#4CAF50");

    /**
     * 设置沉浸式状态栏颜色
     *
     * @param activity
     * @param statusColor
     */
    public static void compat(Activity activity, int statusColor) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (statusColor != COLOR_INVALID_VAL) {
                    activity.getWindow().setStatusBarColor(statusColor);
                }
                return;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                    && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                int color = COLOR_DEFAULT;
                ViewGroup contentView = (ViewGroup) activity
                        .findViewById(android.R.id.content);
                if (statusColor != COLOR_INVALID_VAL) {
                    color = statusColor;
                }
                View statusBarView = contentView.getChildAt(0);
                // 改变颜色时避免重复添加statusBarView
                if (statusBarView != null
                        && statusBarView.getMeasuredHeight() == getStatusBarHeight(activity)) {
                    statusBarView.setBackgroundColor(color);
                    return;
                }
                statusBarView = new View(activity);
                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        getStatusBarHeight(activity));
                statusBarView.setBackgroundColor(color);
                contentView.addView(statusBarView, lp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置沉浸式状态栏颜色
     *
     * @param activity
     * @param statusColor
     */
    public static void compat(Activity activity) {
        compat(activity, COLOR_INVALID_VAL);
    }

    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier(
                "status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}