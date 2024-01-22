package com.fei.action.optimize.startup

import android.app.Application
import android.content.Context

class MyApplication:Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        //主线程设置为最高级别优先级即可
        android.os.Process.setThreadPriority(-19)
    }
}