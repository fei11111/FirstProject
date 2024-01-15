package com.fei.action.wifi

import android.content.Intent
import android.os.Bundle
import com.common.base.BaseActivity
import com.common.viewmodel.EmptyViewModel
import com.fei.action.wifi.aware.AwareWifiActivity
import com.fei.action.wifi.direct.DirectWifiActivity
import com.fei.action.wifi.rtt.RttWifiActivity
import com.fei.action.wifi.simple.ApWifiActivity
import com.fei.firstproject.databinding.ActivityActionWifiBinding

class ActionWifiActivity : BaseActivity<EmptyViewModel, ActivityActionWifiBinding>() {
    override fun createObserver() {
    }

    override fun initViewAndData(savedInstanceState: Bundle?) {
        mBinding.btnApWifi.setOnClickListener {
            startActivity(Intent(this, ApWifiActivity::class.java))
        }
        mBinding.btnDirectWifi.setOnClickListener {
            startActivity(Intent(this, DirectWifiActivity::class.java))
        }
        mBinding.btnAwareWifi.setOnClickListener {
            startActivity(Intent(this, AwareWifiActivity::class.java))
        }
        mBinding.btnRttWifi.setOnClickListener {
            startActivity(Intent(this, RttWifiActivity::class.java))
        }
    }
}