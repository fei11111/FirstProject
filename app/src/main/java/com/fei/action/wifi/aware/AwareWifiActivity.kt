package com.fei.action.wifi.aware

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.aware.AttachCallback
import android.net.wifi.aware.DiscoverySessionCallback
import android.net.wifi.aware.PeerHandle
import android.net.wifi.aware.PublishConfig
import android.net.wifi.aware.PublishDiscoverySession
import android.net.wifi.aware.SubscribeConfig
import android.net.wifi.aware.SubscribeDiscoverySession
import android.net.wifi.aware.WifiAwareManager
import android.net.wifi.aware.WifiAwareSession
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.common.base.BaseActivity
import com.common.viewmodel.EmptyViewModel
import com.fei.firstproject.databinding.ActivityAwareWifiBinding

/**
 * 感知wifi
 */
class AwareWifiActivity : BaseActivity<EmptyViewModel, ActivityAwareWifiBinding>() {

    private var wifiAwareManager: WifiAwareManager? = null
    private var session: WifiAwareSession? = null

    override fun createObserver() {
    }

    override fun initViewAndData(savedInstanceState: Bundle?) {
        mBinding.btnRegister.setOnClickListener {
            receiveRigst()
        }
        mBinding.btnPush.setOnClickListener {
            publish()
        }
        mBinding.btnSubscribe.setOnClickListener {
            subscribe()
        }
    }

    private fun subscribe() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val config = PublishConfig.Builder().setServiceName("AWARE").build()
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.NEARBY_WIFI_DEVICES
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                return
            }
            session?.publish(config, object : DiscoverySessionCallback() {
                override fun onPublishStarted(session: PublishDiscoverySession) {
                    super.onPublishStarted(session)
                }

                override fun onMessageReceived(peerHandle: PeerHandle?, message: ByteArray?) {
                    super.onMessageReceived(peerHandle, message)
                }
            }, null)
        }
    }

    /**
     * 发布
     */
    private fun publish() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val config: SubscribeConfig = SubscribeConfig.Builder()
                .setServiceName("AWARE")
                .build()
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.NEARBY_WIFI_DEVICES
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                return
            }
            session?.subscribe(config, object : DiscoverySessionCallback() {

                override fun onSubscribeStarted(session: SubscribeDiscoverySession) {
                }

                override fun onServiceDiscovered(
                    peerHandle: PeerHandle,
                    serviceSpecificInfo: ByteArray,
                    matchFilter: List<ByteArray>
                ) {
                }
            }, null)
        }

    }

    private fun receiveRigst() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!isSupportAwareWifi()) {
                Log.i("tag", "不支持")
                return
            }
            wifiAwareManager = getSystemService(Context.WIFI_AWARE_SERVICE) as WifiAwareManager
            val filter = IntentFilter(WifiAwareManager.ACTION_WIFI_AWARE_STATE_CHANGED)
            val myReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    if (wifiAwareManager?.isAvailable == true) {
                        attach()
                    } else {
                    }
                }
            }
            registerReceiver(myReceiver, filter)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun attach() {
        wifiAwareManager!!.attach(
            object : AttachCallback() {
                override fun onAttachFailed() {
                    super.onAttachFailed()
                    Log.i("tag", "onAttachFailed")
                }

                override fun onAttached(session: WifiAwareSession?) {
                    super.onAttached(session)
                    Log.i("tag", "onAttached")
                    this@AwareWifiActivity.session = session
                }

                override fun onAwareSessionTerminated() {
                    super.onAwareSessionTerminated()
                    Log.i("tag", "onAwareSessionTerminated")
                }
            }, Handler(mainLooper)
        )
    }

    /**
     * 事发后支持感知
     */
    private fun isSupportAwareWifi(): Boolean {
        return packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI_AWARE)
    }

}