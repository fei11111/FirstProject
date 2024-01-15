package com.fei.action.optimize

import android.content.Intent
import android.os.Bundle
import com.common.base.BaseActivity
import com.common.viewmodel.EmptyViewModel
import com.fei.action.optimize.startup.StartUpOpActivity
import com.fei.firstproject.databinding.ActivityActionOptimizeBinding

class ActionOptimizeActivity : BaseActivity<EmptyViewModel, ActivityActionOptimizeBinding>() {
    override fun createObserver() {
    }

    override fun initViewAndData(savedInstanceState: Bundle?) {
        mBinding.btnStartup.setOnClickListener {
            startActivity(Intent(this, StartUpOpActivity::class.java))
        }
    }
}