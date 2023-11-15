package com.common.network

import android.content.IntentFilter
import com.common.base.BaseApplication

/**
 * FileName: NetworkStateChangeListener
 * @author: linyh19
 * Date: 2021/11/25 14:45
 * Description:
 * 网络状态监听
 */
object NetworkStateClientLowVersion {
    private var mReceiver: NetworkReceiver? = null
    fun setReceiver(receiver: NetworkReceiver) {
        receiver.let { mReceiver = it }
    }

    /**
     * 设置监听器
     * @param listener NetworkStateChangeListener 监听器
     * @return Unit
     */
    fun addListener(listener: NetworkStateChangeListener) {
        mReceiver.let {
            it?.setOnNetworkChangeListener(listener)
        }
    }

    /**
     * 移除监听器
     * @return Unit
     */
    fun removeListener() {
        mReceiver.let {
            it?.removeListener()
        }
    }

    /**
     * 注册广播接收者
     */
    fun registerReceiver() {
        val networkReceiver = NetworkReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(NetworkCallbackImpl.ANDROID_NET_CHANGE_ACTION)
        setReceiver(networkReceiver)
        BaseApplication.instance?.applicationContext?.registerReceiver(networkReceiver, intentFilter)
    }

}