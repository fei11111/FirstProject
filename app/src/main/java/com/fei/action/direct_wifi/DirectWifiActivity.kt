package com.fei.action.direct_wifi

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.common.base.BaseActivity
import com.common.viewmodel.EmptyViewModel
import com.fei.firstproject.databinding.ActivityDirectWifiBinding
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket

class DirectWifiActivity : BaseActivity<EmptyViewModel, ActivityDirectWifiBinding>() {

    private val PERMISSION_REQUEST_CODE = 1

    private var wifiManager: WifiManager? = null
    private var wifiP2pManager: WifiP2pManager? = null
    private var channel: WifiP2pManager.Channel? = null
    private var wifiP2pReceiver: BroadcastReceiver? = null
    private var intentFilter: IntentFilter? = null
    private var connectDevice: WifiP2pDevice? = null
    private var deviceListAdapter: DeviceListAdapter? = null
    override fun createObserver() {

    }

    override fun initViewAndData(savedInstanceState: Bundle?) {

    }

    private fun connectGroup(
        wifiP2pDevice: WifiP2pDevice
    ) {
        // 如果连接对象为群组GroupOwner
        if (wifiP2pDevice.isGroupOwner) {
            Log.d("TAG", "connectWiFiP2P: " + wifiP2pDevice.deviceName)
            val config: WifiP2pConfig = WifiP2pConfig()
            config.deviceAddress = wifiP2pDevice.deviceAddress
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ActivityCompat.checkSelfPermission(
                        this@DirectWifiActivity,
                        android.Manifest.permission.NEARBY_WIFI_DEVICES
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
            }
            wifiP2pManager!!.connect(channel, config, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    Log.i("TAG", "onSuccess: connect")
                }

                override fun onFailure(i: Int) {
                    Log.i("TAG", "onFailure: connect $i")
                }
            })
        } else {
            Log.i("TAG", "对方设备不是群组")
        }
    }

    private fun requestGroupInfo() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this@DirectWifiActivity,
                    android.Manifest.permission.NEARBY_WIFI_DEVICES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }
        wifiP2pManager!!.requestGroupInfo(channel) { wifiP2pGroup ->
            if (wifiP2pGroup != null) {
                Log.i("tag", "onGroupInfoAvailable: wifiP2pGroup != null")
            } else {
                Log.i("tag", "onGroupInfoAvailable: wifiP2pGroup == null")
            }
        }
    }

    private fun removeGroup() {
        wifiP2pManager!!.removeGroup(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Log.i("TAG", "onSuccess: removeGroup")
            }

            override fun onFailure(i: Int) {
                Log.i("TAG", "onFailure: removeGroup")
            }
        })
    }

    private fun cancelConnect() {
        wifiP2pManager!!.cancelConnect(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Log.d("tag", "cancel success")
            }

            override fun onFailure(p0: Int) {
                Log.d("tag", "cancel fail")
            }

        })
    }

    var serverSocket: ServerSocket? = null
    private fun startServer() {
        // 创建服务器 Socket
        if (serverSocket != null) {
            return
        }
        try {
            serverSocket = ServerSocket(8888)
            Log.d("Server", "服务器 Socket 已创建")
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // 开启接收线程监听客户端连接
        val acceptThread = Thread {
            while (true) {
                try {
                    // 等待客户端连接
                    val socket: Socket = serverSocket!!.accept()
                    Log.d("Server", "客户端已连接：" + socket.inetAddress)

                    // 读取客户端发送的数据
                    val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
                    val data = reader.readLine()

                    // 发送响应给客户端
                    val writer =
                        BufferedWriter(OutputStreamWriter(socket.getOutputStream()))
                    writer.write("服务端收到了数据[$data]")
                    writer.newLine()
                    writer.flush()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        acceptThread.start()
    }

    var clientSocket: Socket? = null
    private fun sendMessage() {
        if (clientSocket == null && connectDevice != null) {
            createClientSocket(connectDevice!!.deviceAddress)
        }
        if (clientSocket == null) {
            return
        }
        val thread = Thread {
            try {
                // 向服务端发送数据
                val writer = BufferedWriter(OutputStreamWriter(clientSocket!!.getOutputStream()))
                writer.write("Hello, World!")
                writer.newLine()
                writer.flush()

                // 读取服务端响应数据
                val reader = BufferedReader(InputStreamReader(clientSocket!!.getInputStream()))
                val data = reader.readLine()
                Log.d("Client", "服务端返回的响应：$data")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        thread.start()
    }

    /**
     * 连接群组 、连接设备一样
     */
    private fun connect(device: WifiP2pDevice) {
        if (ActivityCompat.checkSelfPermission(
                this@DirectWifiActivity,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this@DirectWifiActivity,
                    android.Manifest.permission.NEARBY_WIFI_DEVICES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        /**
         * WifiP2pDevice.AVAILABLE：可连接
         * WifiP2pDevice.CONNECTED：已连接
         * WifiP2pDevice.INVITED：已请求连接
         */

        when (device.status) {
            WifiP2pDevice.AVAILABLE -> {
                // 请求连接
                Log.i("tag", "发起连接 $device")

                val config = WifiP2pConfig()
                config.deviceAddress = device.deviceAddress
                wifiP2pManager!!.connect(channel,
                    config,
                    object : WifiP2pManager.ActionListener {
                        override fun onSuccess() {
                            // Connection initiated
                            Toast.makeText(
                                this@DirectWifiActivity,
                                "发起成功",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.i("tag", "发起成功")
                        }

                        override fun onFailure(reason: Int) {
                            // Connection failed
                            Toast.makeText(
                                this@DirectWifiActivity,
                                "发起失败",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.i("tag", "发起失败")
                        }
                    })
            }

            WifiP2pDevice.CONNECTED -> {
                // 断开连接
                wifiP2pManager!!.removeGroup(channel, object : WifiP2pManager.ActionListener {
                    override fun onSuccess() {
                        Log.i("tag", "断开成功")
                    }

                    override fun onFailure(p0: Int) {
                        Log.i("tag", "断开失败")
                    }

                })
            }

            WifiP2pDevice.INVITED -> {
                // 接受连接
                // 关闭连接请求
                wifiP2pManager!!.cancelConnect(channel, object : WifiP2pManager.ActionListener {
                    override fun onSuccess() {
                        Log.i("TAG", "cancelConnect success.");
                    }

                    override fun onFailure(p0: Int) {
                        Log.i("TAG", "cancelConnect failed.");
                    }
                })
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }


    override fun onPause() {
        super.onPause()

    }

    private fun createGroup() {
        // Check if location permission is granted
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(android.Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_CODE
            )
            return
        }

        // Check if location service is enabled
        if (!wifiManager!!.isWifiEnabled) {
            Toast.makeText(this, "Please enable WiFi", Toast.LENGTH_SHORT).show()
            startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
            return
        }
        wifiP2pManager!!.createGroup(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Toast.makeText(this@DirectWifiActivity, "Group created", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(reason: Int) {
                Toast.makeText(this@DirectWifiActivity, "Failed to create group", Toast.LENGTH_SHORT)
                    .show()
            }
        })
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


    private fun joinGroup() {
        // Check if location permission is granted
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ),
                PERMISSION_REQUEST_CODE
            )
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.NEARBY_WIFI_DEVICES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf<String>(
                        android.Manifest.permission.NEARBY_WIFI_DEVICES
                    ),
                    PERMISSION_REQUEST_CODE
                )
                return
            }
        }

        // Check if location service is enabled
        if (!wifiManager!!.isWifiEnabled) {
            Toast.makeText(this, "Please enable WiFi", Toast.LENGTH_SHORT).show()
            startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
            return
        }

        if (!isLocationEnabled()) {
            Toast.makeText(this, "Please enable Location", Toast.LENGTH_SHORT).show()
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            return
        }

        wifiP2pManager!!.discoverPeers(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                // Peers discovery initiated
                Toast.makeText(this@DirectWifiActivity, "Discovering peers", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onFailure(reason: Int) {
                Toast.makeText(
                    this@DirectWifiActivity,
                    "Failed to discover peers $reason",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        })
    }

    private val connectionInfoListener =
        WifiP2pManager.ConnectionInfoListener { info ->
            if (info != null) {
                if (info.groupFormed && info.isGroupOwner) {
                    // 服务端不需要做额外处理
                    Log.d("Client", "本设备为服务端");
                    startServer()
                } else if (info.groupFormed) {
                    Log.d("Client", "本设备为客户端")
                    Log.d("Client", "服务端ip = ${info.groupOwnerAddress.hostAddress}")
                    createClientSocket(info.groupOwnerAddress.hostAddress)
                }
            } else {
                Log.d("tag", "connectionInfo 为空");
            }
        }

    private fun createClientSocket(address: String) {
        val thread = Thread {
            if (clientSocket == null) {
                try {
                    clientSocket = Socket()
                    val inetSocketAddress = InetSocketAddress(address, 8888)
                    clientSocket!!.connect(inetSocketAddress, 5000)
                    Log.d("Client", "连接服务器成功：" + clientSocket!!.inetAddress)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
        thread.start()
    }

    private fun stopPeerDiscovery() {
        Log.i("tag", "停止找寻设备")
        wifiP2pManager!!.stopPeerDiscovery(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Log.i("tag", "停止成功")
            }

            override fun onFailure(p0: Int) {
                Log.i("tag", "停止成功")
            }

        })
    }

    /**
     * discover 收到的设备信息
     */
    private val peerListListener = object : WifiP2pManager.PeerListListener {
        override fun onPeersAvailable(peers: WifiP2pDeviceList?) {
            if (peers != null) {
                val peerList = peers.deviceList
                if (peerList.isNotEmpty()) {
                    Log.i("tag", peerList.toString())
                    if (deviceListAdapter == null) {
                        deviceListAdapter = DeviceListAdapter(this@DirectWifiActivity, peerList.toList())
                        mBinding.recyclerView?.adapter = deviceListAdapter
                    } else {
                        deviceListAdapter?.setList(peerList.toList())
                        deviceListAdapter?.notifyDataSetChanged()
                    }
                    peerList.forEach {
                        if (it.deviceName == "P6PRO_DEVICE") {
                            connectDevice = it
                            Log.i("tag", "找到设备了")
                            return@forEach
                        }
                    }

                } else {
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(wifiP2pReceiver)
    }
}