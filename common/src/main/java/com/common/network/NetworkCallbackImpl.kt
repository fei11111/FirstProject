package com.common.network

import android.annotation.SuppressLint
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities

/**
 * FileName: NetworkCallbackImpl
 * @author: linyh19
 * Date: 2021/11/25 11:43
 * Description:
 * 实时监听网络状态变化的[ConnectivityManager.NetworkCallback]实现类
 */
@SuppressLint("NewApi")
class NetworkCallbackImpl : ConnectivityManager.NetworkCallback() {
    companion object {
        const val ANDROID_NET_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE"

    }

    /**
     * 当前网络类型
     */
    var currentNetworkType = -1

    /**
     * 当前网络是否已连接
     */
    var isConnected = false

    /**
     * 注册的监听
     */
    var changeCall: NetworkStateChangeListener? = null

    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        isConnected = true
        changeCall?.networkConnectChange(isConnected)
    }

    override fun onLost(network: Network) {
        super.onLost(network)
        isConnected = false
        changeCall?.networkConnectChange(isConnected)
        changeCall?.networkTypeChange(NetSate.NET_STATE_NONE)
    }

    @SuppressLint("NewApi")
    override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
        super.onCapabilitiesChanged(network, networkCapabilities)
        if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
            currentNetworkType = when {
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    NetSate.NET_STATE_WIFI
                }
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    NetSate.NET_STATE_TRANSPORT_CELLULAR
                }
                else -> {
                    NetSate.NET_STATE_WIFI
                }
            }
            changeCall?.networkTypeChange(currentNetworkType)
        }
    }

}