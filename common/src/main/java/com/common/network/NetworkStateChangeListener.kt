package com.common.network
/**
 * FileName: NetworkStateChangeListener
 * @author: linyh19
 * Date: 2021/11/25 11:45
 * Description:
 * 网络状态改变监听起
 */
interface NetworkStateChangeListener {

    /**
     * 网络类型更改回调
     * @param type Int 网络类型
     * @return Unit
     */
    fun networkTypeChange(type: Int)

    /**
     * 网络连接状态更改回调
     * @param isConnected Boolean 是否已连接
     * @return Unit
     */
    fun networkConnectChange(isConnected: Boolean)
}