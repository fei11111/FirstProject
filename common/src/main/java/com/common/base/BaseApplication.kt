package com.common.base

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.common.BuildConfig
import com.common.log.Lg
import com.common.utils.Util
import com.tencent.mars.xlog.Log
import com.tencent.mmkv.MMKV

/**
 * Created by fei on 2017/6/15.
 */
open class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        setApplication(this)
        MMKV.initialize(this)
        initLog()
    }

    private fun initLog() {
        val logFilePath = externalCacheDir.toString() + "/log/"
        Lg.config(this)
            .setDir(logFilePath + "xlog")
            .setGlobalTag("@@@")
            .setConsoleSwitch(true)
            .setCacheDays(5)
            .setLogLevel(Log.LEVEL_VERBOSE)
            .build()
        Lg.i("<---------------- common test app start ---------------->")
    }


    companion object {
        var sInstance: Application? = null

        /**
         * 当主工程没有继承BaseApplication时，可以使用setApplication方法初始化BaseApplication
         *
         * @param application
         */
        @Synchronized
        fun setApplication(application: Application) {
            sInstance = application
            //初始化工具类
            Util.init(application)
            //注册监听每个activity的生命周期,便于堆栈式管理
            application.registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
                override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                    AppManager.getAppManager().addActivity(activity)
                }

                override fun onActivityStarted(activity: Activity) {
                    AppManager.getAppManager().addResumeActivity(activity);
                }
                override fun onActivityResumed(activity: Activity) {}
                override fun onActivityPaused(activity: Activity) {}
                override fun onActivityStopped(activity: Activity) {
                    AppManager.getAppManager().removeResumeActivity(activity);
                }
                override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
                override fun onActivityDestroyed(activity: Activity) {
                    AppManager.getAppManager().removeActivity(activity)
                }
            })
        }

        val instance: Application?
            /**
             * 获得当前app运行的Application
             */
            get() {
                if (sInstance == null) {
                    throw NullPointerException("please inherit BaseApplication or call setApplication.")
                }
                return sInstance
            }
    }
}