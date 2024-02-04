package com.fei.action.wifi.simple

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.common.base.BaseActivity
import com.common.viewmodel.EmptyViewModel
import com.fei.firstproject.databinding.ActivityApWifiBinding

/**
 * 普通wifi
 */
class ApWifiActivity : BaseActivity<EmptyViewModel, ActivityApWifiBinding>() {

    private var wifiManager: WifiManager? = null
    private var wifiScanReceiver: BroadcastReceiver? = null

    override fun createObserver() {
    }

    override fun initViewAndData(savedInstanceState: Bundle?) {
        mBinding.btnScan.setOnClickListener { scanWifi() }
        mBinding.btnGetWifiName.setOnClickListener { getWifiName() }
    }

    private fun getWifiName() {

        if (!openWifi()) return

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)
            }
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            //9.0开始要开定位
            if (!isLocationEnabled()) {
                Toast.makeText(this@ApWifiActivity, "Please enable Location", Toast.LENGTH_SHORT)
                    .show()
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                return
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { //android12以上监听修改wifi
            val request = NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build()
            val connectivityManager =
                this.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkCallback =
                object : ConnectivityManager.NetworkCallback(FLAG_INCLUDE_LOCATION_INFO) {
                    override fun onCapabilitiesChanged(
                        network: Network,
                        networkCapabilities: NetworkCapabilities
                    ) {
                        val wifiInfo = networkCapabilities.transportInfo as WifiInfo
                        Log.i("tag", "wifi名字 = ${wifiInfo.ssid}")
                        super.onCapabilitiesChanged(network, networkCapabilities)
                        connectivityManager.unregisterNetworkCallback(this)
                    }
                }
            connectivityManager.registerNetworkCallback(request, networkCallback)
        } else { //android12以下定时获取当前wifi信息
            val wifimanager =
                applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            Log.i("tag", "wifi名字 = ${wifimanager.connectionInfo.ssid}")
        }
    }

    /**
     * 扫码wifi
     */
    private fun scanWifi() {

        if (!openWifi()) return

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)
            }
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            //9.0开始要开定位
            if (!isLocationEnabled()) {
                Toast.makeText(this@ApWifiActivity, "Please enable Location", Toast.LENGTH_SHORT)
                    .show()
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                return
            }
        }

        wifiManager =
            applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiScanReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val success = intent?.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
                Log.i("tag", "onReceive:$success")
                if (success == true) {
                    scanSuccess()
                } else {
                    scanFail()
                }

            }
        }

        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        registerReceiver(wifiScanReceiver, intentFilter)

        /**
         *
         * 使用 WifiManager.startScan() 扫描的频率适用以下限制。
         *
         * Android 8.0 和 Android 8.1：
         *
         * 每个后台应用可以在 30 分钟内扫描一次。
         *
         * Android 9：
         *
         * 每个前台应用可以在 2 分钟内扫描四次。这样便可在短时间内进行多次扫描。
         *
         * 所有后台应用总共可以在 30 分钟内扫描一次。
         *
         * Android 10 及更高版本：
         *
         * 适用 Android 9 的节流限制。新增一个开发者选项，用户可以关闭节流功能以便进行本地测试（位于开发者选项 > 网络 > WLAN 扫描调节下）。
         *
         */


        Log.i("tag", "wifi已打开")

        val success = wifiManager!!.startScan()
        if (!success) {
            // scan failure handling
            Log.i("tag", "开启扫描wifi失败")
            scanFail()
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
//            Log.i("tag","android p 直接获取wifi列表")
//            scanSuccess()
//        }else {
//            val success = wifiManager!!.startScan()
//            if (!success) {
//                // scan failure handling
//                Log.i("tag", "开启扫描wifi失败")
//                scanFail()
//            }
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (wifiScanReceiver != null) {
            unregisterReceiver(wifiScanReceiver)
        }
    }

    private fun scanFail() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val wifiList = wifiManager?.scanResults
        if (wifiList != null) {
            for (scanResult in wifiList) {
                Log.i("scanResult历史", scanResult.toString())
            }
        } else {
            Log.i("scanResult历史", "wifiList is null")
        }
        // potentially use older scan results ...
    }

    private fun scanSuccess() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val wifiList = wifiManager?.scanResults
        if (wifiList != null) {
            for (scanResult in wifiList) {
                Log.i("scanResult", scanResult.toString())
            }
        } else {
            Log.i("scanResult", "wifiList is null")
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationMode: Int
        val locationProviders: String
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            locationMode = Settings.Secure.getInt(
                contentResolver,
                Settings.Secure.LOCATION_MODE,
                Settings.Secure.LOCATION_MODE_OFF
            )
            locationMode != Settings.Secure.LOCATION_MODE_OFF
        } else {
            locationProviders = Settings.Secure.getString(
                contentResolver,
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED
            )
            !TextUtils.isEmpty(locationProviders)
        }
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

    fun openWifi(): Boolean {
        val wifiManager = applicationContext.getSystemService(
            WIFI_SERVICE
        ) as WifiManager
        if (!wifiManager.isWifiEnabled) {
            val panelIntent = Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY)
            val componentName = panelIntent.resolveActivity(packageManager)
            if (componentName != null) {
                startActivity(panelIntent)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
            } else {
                return wifiManager.setWifiEnabled(true)//android Q 默认返回false
            }

        } else {
            return true;
        }
        return false
    }

    private fun isOpenWifi(): Boolean {
        val wifiManager = applicationContext.getSystemService(
            WIFI_SERVICE
        ) as WifiManager
        return wifiManager.isWifiEnabled
    }


    fun setMobileNetworkEnabled() {
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
                        println("onAvailable")
                        println(network) // 29 移动数据 100 wifi;
                        if (Build.VERSION.SDK_INT >= 23) {
                            connectivityManager.bindProcessToNetwork(network)
                        } else {
                            ConnectivityManager.setProcessDefaultNetwork(network)
                        }
                    }
                }
            connectivityManager.requestNetwork(request, callback)
        }
    }

    var net: Network? = null
    fun setWifiNetworkEnabled(isSave: Boolean) {
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
                        super.onAvailable(network)
                        println("onAvailable")
                        println("network 结果 = $network") // 29 移动数据 100 wifi;
                        if (Build.VERSION.SDK_INT >= 23) {
                            connectivityManager.bindProcessToNetwork(network)
                        } else {
                            ConnectivityManager.setProcessDefaultNetwork(network)
                        }
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
        val wifiManager =
            getApplicationContext().getSystemService(Context.WIFI_SERVICE) as WifiManager

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