package com.fei.action.animation

import android.content.Intent
import android.os.Bundle
import com.common.base.BaseActivity
import com.common.viewmodel.EmptyViewModel
import com.fei.action.animation.placehold.PlaceholdActivity
import com.fei.firstproject.databinding.ActivityActionAnimationBinding

class ActionAnimationActivity : BaseActivity<EmptyViewModel, ActivityActionAnimationBinding>() {
    override fun createObserver() {
    }

    override fun initViewAndData(savedInstanceState: Bundle?) {
        mBinding.btnPlace.setOnClickListener {
            startActivity(Intent(this, PlaceholdActivity::class.java))
        }
    }
}