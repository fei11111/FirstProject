package com.fei.action.wifi

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import com.common.base.BaseActivity
import com.common.viewmodel.EmptyViewModel
import com.fei.firstproject.databinding.ActivityApWifiBinding

class ApWifiActivity : BaseActivity<EmptyViewModel, ActivityApWifiBinding>() {

    private final val WIFI_REQUEST = 0X100

    override fun createObserver() {
    }

    override fun initViewAndData(savedInstanceState: Bundle?) {

    }

    fun disconnectDeviceWifi() {
        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val manager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        if (wifiManager.isWifiEnabled) {
            if (Build.VERSION.SDK_INT >= 23) {
                // Android 10及以上版本使用此方法
                manager.bindProcessToNetwork(null)//清除绑定，使用null重新绑定network。不然app进程的所有http请求都将继续在目标network上继续进行，即使断开了连接
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    wifiManager.removeNetworkSuggestions(emptyList())
                }

            } else {
                // Android 9及以下版本使用此方法
                ConnectivityManager.setProcessDefaultNetwork(null)
                wifiManager.disconnect()
                wifiManager.disableNetwork(wifiManager.connectionInfo.networkId)
            }
        }
    }

    /**
     * https://blog.csdn.net/nn690960430/article/details/105497251/
     *
     * @param id
     * @param password
     */
    fun connectDeviceWifi(id: String?, password: String?) {
        //3、链接wifi
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val builder = WifiNetworkSpecifier.Builder()
            builder.setSsid(id!!)
            builder.setWpa2Passphrase(password!!)
            val wifiNetworkSpecifier = builder.build()
            val networkRequestBuilder = NetworkRequest.Builder()
            networkRequestBuilder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            networkRequestBuilder.removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            networkRequestBuilder.setNetworkSpecifier(wifiNetworkSpecifier)
            var networkRequest = networkRequestBuilder.build()

            val manager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    manager.bindProcessToNetwork(network)
                    println("******** connectDeviceWifi onAvailable return.")
                    // 连接成功
                }

                override fun onUnavailable() {
                    println("******** connectDeviceWifi onUnavailable return.")
                    // 失败
                }
            }

            manager.registerNetworkCallback(
                networkRequest,
                callback!!
            )
            manager.requestNetwork(networkRequest, callback!!)

        } else {
            // 1、注意热点和密码均包含引号，此处需要需要转义引号
            val ssid = "\"" + id + "\""
            val psd = "\"" + password + "\""

            //2、配置wifi信息
            val conf = WifiConfiguration()
            conf.SSID = ssid
            val enc = "WPA"
            when (enc) {
                "WEP" -> {
                    // 加密类型为WEP
                    conf.wepKeys[0] = psd
                    conf.wepTxKeyIndex = 0
                    conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
                    conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40)
                }

                "WPA" ->                 // 加密类型为WPA
                    conf.preSharedKey = psd

                "OPEN" ->                 //开放网络
                    conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
            }
            val wifiManager = getApplicationContext().getSystemService(
                WIFI_SERVICE
            ) as WifiManager
            val networkId = wifiManager.addNetwork(conf)
            if (networkId != -1) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(networkId, true);
                wifiManager.reconnect();
            }
        }
    }

    fun openWifi() {
        val wifiManager = applicationContext.getSystemService(
            WIFI_SERVICE
        ) as WifiManager
        if (!wifiManager.isWifiEnabled) {
            val panelIntent = Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY)
            val componentName = panelIntent.resolveActivity(packageManager)
            if (componentName != null) {
                startActivityForResult(panelIntent, WIFI_REQUEST)
            } else {
                wifiManager.setWifiEnabled(true)//android Q 默认返回false
            }

        } else {
            println("wifi已经打开")
        }
    }

    private fun isOpenWifi():Boolean {
        val wifiManager = applicationContext.getSystemService(
            WIFI_SERVICE
        ) as WifiManager
       return wifiManager.isWifiEnabled
    }


    fun setMobileNetworkEnabled(result: MethodChannel.Result) {
//        if (mobileDataEnabling) return
//        if (wifiEnabling) return
//        mobileDataEnabling = true
        println("移动网络测试")
        println(Build.VERSION.SDK_INT)
        if (Build.VERSION.SDK_INT >= 21) {
            val connectivityManager = getSystemService(
                CONNECTIVITY_SERVICE
            ) as ConnectivityManager
            val builder = NetworkRequest.Builder()
            builder.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            builder.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            // TRANSPORT_CELLULAR
            val request = builder.build()
            val callback: ConnectivityManager.NetworkCallback =
                object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        super.onAvailable(network)
                        mobileDataEnabling = false
                        println("onAvailable")
                        println(network) // 29 移动数据 100 wifi;
                        if (Build.VERSION.SDK_INT >= 23) {
                            connectivityManager.bindProcessToNetwork(network)
                        } else {
                            ConnectivityManager.setProcessDefaultNetwork(network)
                        }
                        result.success("MobileNetworkEnabled")
                    }
                }
            connectivityManager.requestNetwork(request, callback)
        }
    }

    var net: Network? = null
    fun setWifiNetworkEnabled(result: MethodChannel.Result, isSave: Boolean) {
        println("WIFI 测试")
        var requestCount = 0
        if (Build.VERSION.SDK_INT >= 21) {
            val connectivityManager = getSystemService(
                CONNECTIVITY_SERVICE
            ) as ConnectivityManager
            val builder = NetworkRequest.Builder()
            // builder.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
            builder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            val request = builder.build()
            val callback =
                object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        println("network = $network") // 29 移动数据 100 wifi;
                        if (isSave && requestCount != 0) return
                        requestCount = 1
                        if (!isSave && net != null && net!!.networkHandle == network.networkHandle) {
                            //不保存，说明不连接net
                            return
                        }
                        net = network
                        wifiEnabling = false
                        super.onAvailable(network)
                        println("onAvailable")
                        println("network 结果 = $network") // 29 移动数据 100 wifi;
                        if (Build.VERSION.SDK_INT >= 23) {
                            connectivityManager.bindProcessToNetwork(network)
                        } else {
                            ConnectivityManager.setProcessDefaultNetwork(network)
                        }
                        result.success("WifiNetworkEnabled")
                    }
                }
            try {
                connectivityManager.requestNetwork(request, callback)
            } catch (e: Exception) {
            }
        }
    }

    var wifiLock: WifiManager.WifiLock? = null
    private fun bindWifi(id: String, password: String) {
        Log.i("aa", "bindWifi")
        val wifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager

        if (wifiManager.isWifiEnabled) {
            wifiLock =
                wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, "MyWifiLock")
            wifiLock?.acquire()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val networkSuggestions = wifiManager.networkSuggestions
                wifiManager.removeNetworkSuggestions(networkSuggestions)
            }
            val suggestion = WifiNetworkSuggestion.Builder()
                .setSsid(id)
                .setWpa2Passphrase(password)
                .build();
            val suggestions = arrayListOf<WifiNetworkSuggestion>()
            suggestions.add(suggestion)
            val status = wifiManager.addNetworkSuggestions(suggestions)
            if (status != WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
                // 连接失败
                Log.d("aa", "添加失败");
            } else {
                Log.d("aa", "添加成功");
            }
        } else {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                return
            }
            wifiManager.configuredNetworks?.forEach {
                if (stringReplace(it.SSID) != id) {
                    wifiManager.disableNetwork(it.networkId)
                }
            }
        }
    }

    //符号替换
    private fun stringReplace(str: String): String? {
        return str.replace("\"", "")
    }

    private fun unbindWifi(id: String, password: String) {
        if (wifiLock?.isHeld == true) {
            wifiLock?.release()
        }

        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Log.d("WifiUtils", "移除建议");
            val networkSuggestions = wifiManager.networkSuggestions
            wifiManager.removeNetworkSuggestions(networkSuggestions)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val suggestion = WifiNetworkSuggestion.Builder()
                .setSsid(id)
                .setWpa2Passphrase(password)
                .build();
            val suggestions = arrayListOf<WifiNetworkSuggestion>()
            suggestions.add(suggestion)
            val status = wifiManager.removeNetworkSuggestions(suggestions)
            if (status != WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
                // 连接失败
                Log.d("aa", "移除失败");
            } else {
                Log.d("aa", "移除成功");
            }
        } else {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            wifiManager.configuredNetworks?.forEach {
                wifiManager.configuredNetworks?.forEach {
                    wifiManager.disableNetwork(it.networkId)
                }
            }
        }
    }

}