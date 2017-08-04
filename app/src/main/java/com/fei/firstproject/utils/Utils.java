package com.fei.firstproject.utils;

import android.app.Activity;
import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ViewDragHelper;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.fei.firstproject.toast.ToastCompat;

import java.lang.reflect.Field;

/**
 * Created by Administrator on 2017/7/28.
 */

public class Utils {

    public static void showToast(Context context, String text) {
        ToastCompat.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void showToastLong(Context context, String text) {
        ToastCompat.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 抽屉滑动范围控制
     *
     * @param activity
     * @param drawerLayout
     * @param displayWidthPercentage 占全屏的份额0~1
     */
    public static void setDrawerLeftEdgeSize(Activity activity, DrawerLayout drawerLayout, float displayWidthPercentage) {
        if (activity == null || drawerLayout == null)
            return;
        try {
            // find ViewDragHelper and set it accessible
            Field leftDraggerField = drawerLayout.getClass().getDeclaredField("mLeftDragger");
            leftDraggerField.setAccessible(true);
            ViewDragHelper leftDragger = (ViewDragHelper) leftDraggerField.get(drawerLayout);
            // find edgesize and set is accessible
            Field edgeSizeField = leftDragger.getClass().getDeclaredField("mEdgeSize");
            edgeSizeField.setAccessible(true);
            int edgeSize = edgeSizeField.getInt(leftDragger);
            // set new edgesize
            // Point displaySize = new Point();
            DisplayMetrics dm = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
            edgeSizeField.setInt(leftDragger, Math.max(edgeSize, (int) (dm.widthPixels * displayWidthPercentage)));
        } catch (NoSuchFieldException e) {
//            Log.e("NoSuchFieldException", e.getMessage().toString());
        } catch (IllegalArgumentException e) {
//            Log.e("llegalArgumentException", e.getMessage().toString());
        } catch (IllegalAccessException e) {
//            Log.e("IllegalAccessException", e.getMessage().toString());
        }
    }
}
