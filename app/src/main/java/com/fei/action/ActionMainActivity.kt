package com.fei.action

import android.content.Intent
import android.os.Bundle
import com.common.base.BaseActivity
import com.common.viewmodel.EmptyViewModel
import com.fei.action.optimize.ActionOptimizeActivity
import com.fei.action.wifi.ActionWifiActivity
import com.fei.action.wifi.aware.AwareWifiActivity
import com.fei.action.wifi.direct.DirectWifiActivity
import com.fei.action.wifi.rtt.RttWifiActivity
import com.fei.action.wifi.simple.ApWifiActivity
import com.fei.firstproject.databinding.ActivityActionMainBinding

class ActionMainActivity : BaseActivity<EmptyViewModel, ActivityActionMainBinding>() {
    override fun createObserver() {
    }

    override fun initViewAndData(savedInstanceState: Bundle?) {
        mBinding.btnWifi.setOnClickListener {
            startActivity(Intent(this, ActionWifiActivity::class.java))
        }
        mBinding.btnOptimize.setOnClickListener {
            startActivity(Intent(this, ActionOptimizeActivity::class.java))
        }

    }
}