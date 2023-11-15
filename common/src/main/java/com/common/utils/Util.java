package com.common.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;

/**
 * Created by goldze on 2017/5/14.
 * 常用工具类
 */
public final class Util {

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    private Util() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 初始化工具类
     *
     * @param context 上下文
     */
    public static void init(@NonNull final Context context) {
        Util.context = context.getApplicationContext();
    }

    /**
     * 获取ApplicationContext
     *
     * @return ApplicationContext
     */
    public static Context getContext() {
        if (context != null) {
            return context;
        }
        throw new NullPointerException("should be initialized in application");
    }

    /*** 获取当前apk的版本号** @param mContext* @return*/
    public static int getVersionCode(Context mContext) {
        int versionCode = 0;
        try {
            //获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }


}
