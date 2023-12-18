package com.common.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.common.ktx.inflateBindingWithGeneric
import com.common.viewmodel.BaseViewModel
import com.common.viewmodel.ViewModelFactory
import com.trello.rxlifecycle4.components.support.RxFragment
import java.lang.reflect.ParameterizedType

abstract class BaseFragment<VM : BaseViewModel, VB : ViewBinding> : RxFragment(), IBaseView {

    //是否第一次加载
    private var isFirst: Boolean = true

    protected lateinit var viewModel: VM
    protected lateinit var mBinding: VB

    /** 当前界面 Context 对象*/
    protected lateinit var mContext: FragmentActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = requireActivity()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = inflateBindingWithGeneric(layoutInflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createViewModel()
        lifecycle.addObserver(viewModel)
        initViewAndData(savedInstanceState)
        createObserver()
    }

    override fun onResume() {
        super.onResume()
        onVisible()
    }

    /**
     * 是否需要懒加载
     */
    private fun onVisible() {
        if (lifecycle.currentState == Lifecycle.State.STARTED && isFirst) {
            lazyLoadData()
            isFirst = false
        }
    }

    abstract fun lazyLoadData()

    abstract fun createObserver()

    /**
     * 创建 ViewModel
     */
    private fun createViewModel() {
        val type = javaClass.genericSuperclass
        if (type is ParameterizedType) {
            val tp = type.actualTypeArguments[0]
            val tClass = tp as? Class<VM> ?: BaseViewModel::class.java
            viewModel = ViewModelProvider(this, ViewModelFactory()).get(tClass) as VM
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
    }
}