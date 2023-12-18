package com.common.base

import android.content.Context
import android.os.Bundle
import android.view.Window
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.common.ktx.inflateBindingWithGeneric
import com.common.network.NetworkStateChangeListener
import com.common.network.NetworkStateManager
import com.common.utils.AndroidBugFixUtil
import com.common.viewmodel.BaseViewModel
import com.common.viewmodel.ViewModelFactory
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity
import java.lang.reflect.ParameterizedType


/**
 * FileName: XActivity
 * @author: linyh19
 * Date: 2022/1/20 14:52
 * Description:
 */
abstract class BaseActivity<VM : BaseViewModel, VB : ViewBinding> : RxAppCompatActivity(),
    IBaseView, NetworkStateChangeListener {

    lateinit var viewModel: VM

    protected lateinit var mBinding: VB

    protected lateinit var mContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        mContext = this
        mBinding = inflateBindingWithGeneric(layoutInflater,true)
        setContentView(mBinding.root)
        init(savedInstanceState)
    }

    /**
     * 初始化
     * */
    private fun init(savedInstanceState: Bundle?) {
        //注册 UI事件
        initViewAndData(savedInstanceState)
        createViewModel()
        lifecycle.addObserver(viewModel)
        createObserver()
    }

    abstract fun createObserver()

    /**
     * 创建 ViewModel
     */
    @Suppress("UNCHECKED_CAST")
    private fun createViewModel() {
        val type = javaClass.genericSuperclass
        if (type is ParameterizedType) {
            val tp = type.actualTypeArguments[0]
            val tClass = tp as? Class<VM> ?: BaseViewModel::class.java
            viewModel = ViewModelProvider(this, ViewModelFactory()).get(tClass) as VM
        }
    }

    override fun onResume() {
        super.onResume()
        if (openNetworkSateMoniter()) {
            NetworkStateManager.registerListener(this)
        }
    }

    override fun onPause() {
        super.onPause()
        if (openNetworkSateMoniter()) {
            NetworkStateManager.unRegisterListener()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 解决某些特定机型会触发的Android本身的Bug
        AndroidBugFixUtil().fixSoftInputLeaks(this)
    }

    open fun openNetworkSateMoniter(): Boolean = false

    /**
     * 网络连接状态更改回调
     * @param isConnected Boolean 是否已连接
     * @return Unit
     */
    override fun networkConnectChange(isConnected: Boolean) {}

    /**
     * 网络类型更改回调
     * @param type Int 网络类型
     * @return Unit
     */
    override fun networkTypeChange(type: Int) {}
}