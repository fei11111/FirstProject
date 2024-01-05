package com.fei.action.wifi.rtt

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import com.common.base.BaseActivity
import com.common.viewmodel.EmptyViewModel
import com.fei.firstproject.databinding.ActivityRttWifiBinding

class RttWifiActivity : BaseActivity<EmptyViewModel, ActivityRttWifiBinding>() {
    override fun createObserver() {
    }

    override fun initViewAndData(savedInstanceState: Bundle?) {
        Log.i("tag",isSupportRtt().toString())
    }

    private fun isSupportRtt(): Boolean {
        return packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI_RTT)
    }
}