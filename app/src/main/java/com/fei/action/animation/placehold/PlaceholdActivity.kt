package com.fei.action.animation.placehold

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.transition.TransitionManager
import com.common.base.BaseActivity
import com.common.viewmodel.EmptyViewModel
import com.fei.firstproject.databinding.ActivityPlaceholderBinding

class PlaceholdActivity:BaseActivity<EmptyViewModel,ActivityPlaceholderBinding>() {
    override fun createObserver() {

    }

    override fun initViewAndData(savedInstanceState: Bundle?) {

    }

    fun replace(view: View) {
        // 使用约束布局除了减少层级嵌套外，还可以更方便的处理过渡动画
        TransitionManager.beginDelayedTransition(view.parent as ViewGroup)
        mBinding.placeholder.setContentId(view.id)
    }

}