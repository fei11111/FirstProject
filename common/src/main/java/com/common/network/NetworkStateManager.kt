package com.common.network

import android.util.Log

/**
 * FileName: NerworkStateFactory
 * @author: linyh19
 * Date: 2021/11/26 10:21
 * Description:
 */
object NetworkStateManager {
    private var isCreateClient = false
    private fun createNetworkClient() {
        if (isCreateClient) {
            Log.i("NetworkStateManager", "createNetworkClient: 已经初始化过了")
            return
        }
        NetworkStateClientLowVersion.registerReceiver()
        isCreateClient = true
    }

    fun registerListener(listener: NetworkStateChangeListener) {
        createNetworkClient()
        NetworkStateClientLowVersion.addListener(listener)
    }

    fun unRegisterListener() {
        NetworkStateClientLowVersion.removeListener()
    }
    /**高版本直接不用广播**/
//    fun createNetworkClient() {
//        if (isCreateClient) {
//            Log.i("NetworkStateManager", "createNetworkClient: 已经初始化过了")
//            return
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            NetworkStateClient.register()
//        } else {
//            NetworkStateClientLowVersion.registerReceiver()
//        }
//        isCreateClient = true
//    }
//
//    fun registerListener(listener: NetworkStateChangeListener) {
//        createNetworkClient()
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            NetworkStateClient.addListener(listener)
//        } else {
//            NetworkStateClientLowVersion.addListener(listener)
//        }
//    }
//
//    fun unRegisterListener() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            NetworkStateClient.removeListener()
//        } else {
//            NetworkStateClientLowVersion.removeListener()
//        }
//    }
}