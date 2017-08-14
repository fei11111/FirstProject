package com.fei.firstproject.utils;

import android.util.Log;

import com.fei.firstproject.config.AppConfig;


/**
 * @author Kelvin
 * @version 1.0
 * @Title: zLog.java
 * @Package com.frame.utils
 * @Description: 日志打印类
 * @date: 2014年6月12日 下午2:44:44
 */
public class LogUtils {

    public static void v(final String tag, final String msg) {
        if (AppConfig.DEBUG) {
            Log.v(tag, "--> " + msg);
        }
    }

    public static void d(final String tag, final String msg) {
        if (AppConfig.DEBUG) {
            Log.d(tag, "--> " + msg);
        }
    }

    public static void i(final String tag, final String msg) {
        if (AppConfig.DEBUG) {
            Log.i(tag, "--> " + msg);
        }
    }

    public static void w(final String tag, final String msg) {
        if (AppConfig.DEBUG) {
            Log.w(tag, "--> " + msg);
        }
    }

    public static void e(final String tag, final String msg) {
        if (AppConfig.DEBUG) {
            Log.e(tag, "--> " + msg);
        }
    }

    public static void print(String tag, String msg) {
        System.out.println("tag==" + msg);
    }
}
