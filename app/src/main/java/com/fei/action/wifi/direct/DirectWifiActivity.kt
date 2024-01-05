package com.fei.action.wifi.direct

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
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
import androidx.recyclerview.widget.LinearLayoutManager
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
import kotlin.random.Random

/***
 *
 *  点对点wifi
 * 以下 API 还需要启用位置信息模式：
 *
 * discoverPeers()
 * discoverServices()
 * requestPeers()
 *
 */
class DirectWifiActivity : BaseActivity<EmptyViewModel, ActivityDirectWifiBinding>() {

    private val PERMISSION_REQUEST_CODE = 1
    private val wifiManager: WifiManager? by lazy(LazyThreadSafetyMode.NONE) {
        applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }
    private val wifiP2pManager: WifiP2pManager? by lazy(LazyThreadSafetyMode.NONE) {
        getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager?
    }
    private var channel: WifiP2pManager.Channel? = null
    private var wifiP2pReceiver: BroadcastReceiver? = null
    private var intentFilter: IntentFilter? = null
    private var connectDevice: WifiP2pDevice? = null
    private var deviceListAdapter: DeviceListAdapter? = null
    private var isDiscover = false
    private var isRequestPeer = false //防止多次request

    override fun createObserver() {

    }

    override fun initViewAndData(savedInstanceState: Bundle?) {
        mBinding.btnCreateGroup.setOnClickListener {
            createGroup()
        }
        mBinding.btnConnectGroup.setOnClickListener {
            connectDevice?.let { connectGroup(it) }
        }
        mBinding.btnRemoveGroup.setOnClickListener {
            removeGroup()
        }
        mBinding.btnDiscover.setOnClickListener {
            discoverPeers()
        }
        mBinding.btnConnect.setOnClickListener {
            connectDevice?.let { connect(it) }
        }
        mBinding.btnSend.setOnClickListener {
            sendMessage()
        }
        mBinding.btnCancel.setOnClickListener {
            cancelConnect()
        }
        mBinding.btnRequestGroupInfo.setOnClickListener {
            requestGroupInfo()
        }
        mBinding.btnConnectInfo.setOnClickListener {
            requestConnectionInfo()
        }

        if (initP2p()) {
            receiveReigst()
        }
    }

    private fun receiveReigst() {
        intentFilter = IntentFilter()
        intentFilter!!.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        intentFilter!!.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        intentFilter!!.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        intentFilter!!.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)

        wifiP2pReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION == action) {
                    Log.i("tag", "当在设备上启用或停用 Wi-Fi 点对点时广播。")
                    val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                    if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                        // WiFi P2P is enabled
                        Log.i("tag", "状态改变 WiFi P2P is enabled")

                        //todo
                        //可搜索周围P2P设备 discoverPeers

                    } else {
                        // WiFi P2P is not enabled
                        Log.i("tag", "状态改变 WiFi P2P is not enabled");
                    }
                } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION == action) {
                    Log.i(
                        "tag",
                        "当您调用 discoverPeers() 时广播。如果您在应用中处理此 intent，则通常需要调用 requestPeers() 来获取更新后的对等设备列表。"
                    );
                    // Request available peers from the WifiP2pManager

                    if (isDiscover && !isRequestPeer) {
                        Log.i("tag", "发起requestPeers");
                        isRequestPeer = true
                        requestPeers()
                    }
                } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION == action) {
                    Log.i(
                        "tag",
                        "表明Wi-Fi对等网络的连接状态发生了改变,应用可以使用 requestConnectionInfo()、requestNetworkInfo() 或 requestGroupInfo() 来检索当前连接信息。    "
                    );
                    // Respond to new connection or disconnections
                    requestConnectionInfo()
                } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION == action) {
                    Log.i("tag", "表明该设备的配置信息发生了改变");
                    val device: WifiP2pDevice? =
                        intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)
                    Log.i(
                        "tag",
                        "本设备信息发生变化：" + device!!.deviceName + ", " + device.deviceAddress
                    )
                    requestDeviceInfo()
                    // Respond to this device's wifi state changing
                }
            }
        }
        registerReceiver(wifiP2pReceiver, intentFilter)
    }

    private fun requestDeviceInfo() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
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
            wifiP2pManager!!.requestDeviceInfo(channel!!,
                object : WifiP2pManager.DeviceInfoListener {
                    override fun onDeviceInfoAvailable(p0: WifiP2pDevice?) {
                        Log.i("tag", p0!!.toString())
                    }
                })

        }
    }


    private fun requestPeers() {
        if (ActivityCompat.checkSelfPermission(
                this@DirectWifiActivity,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@DirectWifiActivity,
                arrayOf<String>(android.Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_CODE
            )
            return
        }
        wifiP2pManager!!.requestPeers(channel, peerListListener)
    }

    private fun requestConnectionInfo() {
        wifiP2pManager!!.requestConnectionInfo(
            channel,
            connectionInfoListener
        )
    }

    private fun connectGroup(
        wifiP2pDevice: WifiP2pDevice
    ) {
        // 如果连接对象为群组GroupOwner
        if (wifiP2pDevice.isGroupOwner) {
            connect(wifiP2pDevice)
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

    private var serverSocket: ServerSocket? = null
    private fun startServer() {
        // 创建服务器 Socket
        if (serverSocket != null) {
            return
        }
        try {
            serverSocket = ServerSocket(8888)
            Toast.makeText(this@DirectWifiActivity,"服务器 Socket 已创建",Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // 开启接收线程监听客户端连接
        val acceptThread = Thread {

                try {
                    // 等待客户端连接
                    val socket: Socket = serverSocket!!.accept()
                    Toast.makeText(this@DirectWifiActivity,"客户端已连接",Toast.LENGTH_LONG).show()

                    while (true) {
                        // 读取客户端发送的数据
                        val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
                        val data = reader.readLine()

                        runOnUiThread {
                            Toast.makeText(this@DirectWifiActivity,"收到数据$data",Toast.LENGTH_LONG).show()
                        }

                        // 发送响应给客户端
                        val writer =
                            BufferedWriter(OutputStreamWriter(socket.getOutputStream()))
                        writer.write(Random.nextInt(1000).toString())
                        writer.newLine()
                        writer.flush()
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                }
        }
        acceptThread.start()
    }

    private var clientSocket: Socket? = null
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
                writer.write(Random.nextInt(1000).toString())
                writer.newLine()
                writer.flush()

                // 读取服务端响应数据
                val reader = BufferedReader(InputStreamReader(clientSocket!!.getInputStream()))
                val data = reader.readLine()

                runOnUiThread {
                    Toast.makeText(this@DirectWifiActivity,"收到数据$data",Toast.LENGTH_LONG).show()
                }

                Log.d("Client", "服务端返回的响应：$data")
            } catch (e: IOException) {
                e.printStackTrace()
                clientSocket!!.close()
                clientSocket = null
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
//                wifiP2pManager!!.removeGroup(channel, object : WifiP2pManager.ActionListener {
//                    override fun onSuccess() {
//                        Log.i("tag", "断开成功")
//                    }
//
//                    override fun onFailure(p0: Int) {
//                        Log.i("tag", "断开失败")
//                    }
//
//                })
                Log.i("tag","已经连接")
                requestConnectionInfo()
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

        if(serverSocket!=null) {
            return
        }

        // Check if location permission is granted
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
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
                Toast.makeText(
                    this@DirectWifiActivity,
                    "Failed to create group",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        })
    }

    /**
     * 初始化
     */
    private fun initP2p(): Boolean {
        // Device capability definition check
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI_DIRECT)) {
            Log.e("TAG", "Wi-Fi Direct is not supported by this device.")
            return false
        }

        // Hardware capability check
        if (wifiManager == null) {
            Log.e("TAG", "Cannot get Wi-Fi system service.")
            return false
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!wifiManager!!.isP2pSupported) {
                Log.e("TAG", "Wi-Fi Direct is not supported by the hardware or Wi-Fi is off.")
                return false
            }
        }

        if (wifiP2pManager == null) {
            Log.e("TAG", "Cannot get Wi-Fi Direct system service.")
            return false
        }
        channel = wifiP2pManager!!.initialize(this@DirectWifiActivity, mainLooper, null)
        if (channel == null) {
            Log.e("TAG", "Cannot initialize Wi-Fi Direct.")
            return false
        }
        return true
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


    private fun discoverPeers() {
        // Check if location permission is granted
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                PERMISSION_REQUEST_CODE
            )
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this@DirectWifiActivity,
                    android.Manifest.permission.NEARBY_WIFI_DEVICES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this@DirectWifiActivity,
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
            Toast.makeText(this@DirectWifiActivity, "Please enable WiFi", Toast.LENGTH_SHORT).show()

            val panelIntent = Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY)
            val componentName = panelIntent.resolveActivity(packageManager)
            if (componentName != null) {
                startActivity(panelIntent)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
            } else {
                wifiManager!!.setWifiEnabled(true)//android Q 默认返回false
            }

            return
        }

        if (!isLocationEnabled()) {
            Toast.makeText(this@DirectWifiActivity, "Please enable Location", Toast.LENGTH_SHORT)
                .show()
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            return
        }

        wifiP2pManager!!.discoverPeers(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                // Peers discovery initiated
                isDiscover = true
                Toast.makeText(this@DirectWifiActivity, "Discovering peers", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onFailure(reason: Int) {
                isDiscover = false
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
                    clientSocket!!.bind(null)
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
        @SuppressLint("NotifyDataSetChanged")
        override fun onPeersAvailable(peers: WifiP2pDeviceList?) {
            if (peers != null) {
                val peerList = peers.deviceList
                Log.i("tag",peerList.toString())
                if (peerList.isNotEmpty()) {
                    if (deviceListAdapter == null) {
                        deviceListAdapter =
                            DeviceListAdapter(this@DirectWifiActivity, peerList.toList())
                        mBinding.recyclerView.layoutManager = LinearLayoutManager(this@DirectWifiActivity, LinearLayoutManager.VERTICAL, false)
                        mBinding.recyclerView.adapter = deviceListAdapter
                    } else {
                        deviceListAdapter?.setList(peerList.toList())
                        deviceListAdapter?.notifyDataSetChanged()
                    }
                    peerList.forEach {
                        if (it.deviceName == "Android_79b2") {
                            connectDevice = it
                            isDiscover = false
                            Log.i("tag", "找到设备了")
                            return@forEach
                        }
                    }

                    if (isDiscover) {
                        //未发现
                        Log.i("tag", "未找到设备")
                        isDiscover = false
                    }

                } else {
                    Log.i("tag", "未找到设备")
                    isDiscover = false
                }
            } else {
                Log.i("tag", "未找到设备")
                isDiscover = false
            }
            isRequestPeer = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (wifiP2pReceiver != null) {
            unregisterReceiver(wifiP2pReceiver)
        }
        if(clientSocket!=null) {
            clientSocket!!.close()
        }
        if(serverSocket!=null) {
            serverSocket!!.close()
        }
    }
}