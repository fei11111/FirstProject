package com.fei.action

import android.content.Intent
import android.os.Bundle
import com.common.base.BaseActivity
import com.common.viewmodel.EmptyViewModel
import com.fei.action.animation.ActionAnimationActivity
import com.fei.action.ffmpeg.IjkActivity
import com.fei.action.optimize.ActionOptimizeActivity
import com.fei.action.touch.TouchActivity
import com.fei.action.wifi.ActionWifiActivity
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
        mBinding.btnFfmpeg.setOnClickListener {
            startActivity(Intent(this, IjkActivity::class.java))
        }
        mBinding.btnAnim.setOnClickListener {
            startActivity(Intent(this, ActionAnimationActivity::class.java))
        }
        mBinding.btnTouch.setOnClickListener {
            startActivity(Intent(this,TouchActivity::class.java))
        }
    }
}