# FirstProject

插件安排：AWS Toolkit

该项目由个人独立完成，图标和图片是阿里巴巴图标矢量图和千库网找的，接口由之前项目抽取出与业务无关的使用，业务有关的页面没有继续绘制<br/>
(因为是之前项目中抽取出来的接口和页面，所以没有写注释)<br />
1.主要面向接口编程<br />
2.并无业务逻辑，以模仿其他App为主<br />
3.主要用到BaseAcitivity,BaseListActivity,BaseFragment，BaseListFragment<br/>
BaseActivity,BaseFragment由默认布局完成，且要加@Nullable标签，由loading，
no_data,还有request_error页面完成，还包含刷新RefreshLayout。还包含一些基本设置，如
ProgressDialog初始化，ButterKnife运用，授权等。注意授权Activity和Fragment有区别
BseListActivity,BaseListFragment则集成Base,且增加了RecycleView和底部按钮，
基本用来绘制只用到ListView的界面。<br/>
4.视频播放MediaPlayer和surfaceView结合，视频下载并通知栏显示下载进度。<br/>
5.用到框架用:<br/>1)网络框架：Retrofit2<br/>2)上拉刷新框架：smartrefresh<br/>3)
圆形头像框架：RoundImageView<br/>4)图片加载框架: glide<br />5)定位：高德地图<br/>6)扫描：zxing<br/>7)
viewpager:jazzyviewPager<br/>8)其他：EventBus<br/>9)图片选择：PictureSelector<br/>10)
图片查看：PhotoView<br/>

6.WeakHandler 替换之前用的Handler

7.设备唯一ID
String id = Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID);

8.后台启动APP
Android10中, 当App无前台显示的Activity时,其启动Activity会被系统拦截, 导致启动无效。
个人觉得官方这样做的目的是在用户使用的过程中, 不希望被其他App强制打断, 但这样就对闹钟类,
带呼叫功能的App不太友好了。
对此官方给予的折中方案是使用全屏Intent(full-screen intent), 既创建通知栏通知时, 加入full-screen
intent 设置, 示例代码如下(基于官方文档修改):

//AndroidManifest中
<uses-permission android:name="android.permission.USE_FULL_SCREEN_INTENT" />

Intent fullScreenIntent = new Intent(this,CallActivity.class);
PendingIntent pendingIntent = PendingIntent.getActivity(
this,0,fullScreenIntent,PendingIntent.FLAG_UPDATE_CURRENT);
NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"channelId")
.setSmallIcon(R.drawable.ic_app)
.setContentTitle("title")
.setContentText("content")
//以下为关键3行
.setPriority(NotificationCompat.PRIORITY_HIGH)
.setCategory(NotificationCompat.CATEGORY_CALL)
.setFullScreenIntent(pendingIntent,true);
NotificationManager notificationManager = (NotificationManager) getSystemService(
Context.NOTIFICATION_SERVICE);
notificationManager.notify(1,builder.build());

9.文件路径
getFilesDir()
/data/user/0/com.fei.firstproject/files

getExternalFilesDir(DIRECTORY_PICTURES)
/storage/emulated/0/Android/data/com.fei.firstproject/files/Pictures

10.兼容android10以上需要将沙盒中的视频或图片文件保存在外部存储时

<uses-permission android:name="android.permission.READ_MEDIA_IMAGES"/>

权限
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
checkPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, PERMISSION_READ_STORAGE);
} else {
checkPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ_STORAGE);
}

保存到外部沙盒
FileUtil.savePictureFile(context,源文件,Environment.DIRECTORY_DCIM(目标路径))
FileUtil.saveVideoFile(context,源文件,Environment.DIRECTORY_MUSIC(目标路径))
获取手机所有图片
FileUtil.loadPhotoFiles(AlbumActivity.this);
BitmapFactory.Options options = new BitmapFactory.Options();
options.inJustDecodeBounds = true;
try {
FileUtil.getBitmapFromUri(mContext, uri, null, options);
int outWidth = options.outWidth;
int outHeight = options.outHeight;
if (outWidth > outHeight) {
options.inSampleSize = outWidth / imageWidth;
} else {
options.inSampleSize = outHeight / imageWidth;
}
options.outWidth = imageWidth;
options.outHeight = imageWidth;
options.inPreferredConfig = Bitmap.Config.RGB_565;
options.inJustDecodeBounds = false;
return FileUtil.getBitmapFromUri(mContext, uri, null, options);
} catch (IOException e) {
throw new RuntimeException(e);
}

11.连接无可访问外网wifi，用移动网访问接口

12.自动连接wifi，断开wifi

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val builder = WifiNetworkSpecifier.Builder()
                    builder.setSsid(id!!)
                    builder.setWpa2Passphrase(password!!)
        
                    val wifiNetworkSpecifier = builder.build()
                    val networkRequestBuilder = NetworkRequest.Builder()
                    networkRequestBuilder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        //            networkRequestBuilder.addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED)
        //            networkRequestBuilder.addCapability(NetworkCapabilities.NET_CAPABILITY_TRUSTED)
        networkRequestBuilder.setNetworkSpecifier(wifiNetworkSpecifier)
        var networkRequest = networkRequestBuilder.build()
        
                    val manager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
                    if (networkRequest == null) {
                        networkRequest = NetworkRequest.Builder()
                            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        //                    .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .build()
        }
        
                    callback = object : ConnectivityManager.NetworkCallback() {
                        override fun onAvailable(network: Network) {
                            manager.bindProcessToNetwork(network)
                            //                    String currentSSID = WifiService.this.getWifiServiceInfo(null);
        //
        //                    String ssid = callbackContext.getString("ssid");
        //                    if (currentSSID.equals(ssid)) {
        //                        WifiService.this.getConnectedSSID(callbackContext);
        //                    } else {
        //                        callbackContext.error("CONNECTED_SSID_DOES_NOT_MATCH_REQUESTED_SSID");
        //                    }
        //                    WifiService.this.networkCallback = this;
        println("******** connectDeviceWifi onAvailable return.")
        // 连接成功
        result.success("1")
        }
        
                        override fun onUnavailable() {
        //                    callbackContext.error("CONNECTION_FAILED");
        println("******** connectDeviceWifi onUnavailable return.")
        // 失败
        result.success("0")
        }
        }
        
                    manager.registerNetworkCallback(
                        networkRequest,
                        callback!!
                    )
                    manager.requestNetwork(networkRequest, callback!!)
        
                } else {
                    val wifiManager = getSystemService(
                        WIFI_SERVICE
                    ) as WifiManager
                    val result = wifiManager.addNetwork(conf)
                    print("result = $result")
                    val list = wifiManager.configuredNetworks
                    for (i in list) {
                        if (i.SSID != null && i.SSID == ssid) {
                            wifiManager.disconnect()
                            wifiManager.enableNetwork(i.networkId, true)
                            wifiManager.reconnect()
                            break
                        }
                    }
                }
    
        fun disconnectDeviceWifi(result: MethodChannel.Result) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val manager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
                manager.bindProcessToNetwork(null)//清除绑定，使用null重新绑定network。不然app进程的所有http请求都将继续在目标network上继续进行，即使断开了连接
                if (callback != null) {
                    manager.unregisterNetworkCallback(callback!!)
                }
            }
                val wifiManager = this.getSystemService(
                    WIFI_SERVICE
                ) as WifiManager
                try {
                    wifiManager.isWifiEnabled = false
                    result.success(true)
                } catch (e: Exception) {
                    println("失败")
                    println(e)
                    result.success(true)
                }
    
        }
    
    //锁定wifi
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
    
    private fun stringReplace(str: String): String? {
        return str.replace("\"", "")
    }
    
    private fun unbindWifi(id: String, password: String) {
        if (wifiLock?.isHeld == true) {
            wifiLock?.release()
        }
    
        val wifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager
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

13.中文转拼音

    implementation 'com.github.promeg:tinypinyin:2.0.3' // TinyPinyin核心包，约80KB
    implementation 'com.github.promeg:tinypinyin-lexicons-android-cncity:2.0.3'
    Pinyin.toPinyin("中文", "")

14.判断定位是否打开

     /**
     * 判断是否打开定位
     */
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

15.直连WiFi（以 Android 13（API 级别 33）或更高版本为目标平台且管理 Wi-Fi 连接，则应请求 NEARBY_WIFI_DEVICES 运行时权限）

        wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        wifiP2pManager = getSystemService(WIFI_P2P_SERVICE) as WifiP2pManager
        channel = wifiP2pManager!!.initialize(this, mainLooper, null)
    
        intentFilter = IntentFilter()
        intentFilter!!.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        intentFilter!!.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        intentFilter!!.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        intentFilter!!.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
        intentFilter!!.addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION)
    
        wifiP2pReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION == action) {
                    Log.i("tag", "表明Wi-Fi对等网络（P2P）是否已经启用")
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
                    Log.i("tag", "表明可用的对等点的列表发生了改变");
                    // Request available peers from the WifiP2pManager
                    if (ActivityCompat.checkSelfPermission(
                            this@MainActivity,
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            this@MainActivity,
                            arrayOf<String>(android.Manifest.permission.ACCESS_FINE_LOCATION),
                            PERMISSION_REQUEST_CODE
                        )
                    } else {
                        wifiP2pManager!!.requestPeers(channel, peerListListener)
                    }
                } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION == action) {
                    Log.i("tag", "表明Wi-Fi对等网络的连接状态发生了改变");
                    // Respond to new connection or disconnections
    
                    // 获取 NetworkInfo 对象
                    val networkInfo: NetworkInfo? =
                        intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO)
    
                    // 获取 WifiP2pInfo 对象
                    val wifiP2pInfo: WifiP2pInfo? =
                        intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO)
    
                    // 获取 WifiP2pGroup 对象
                    val wifiP2pGroup: WifiP2pGroup? =
                        intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_GROUP)
    
                    if (networkInfo!!.isConnected()) {
                        if (wifiP2pInfo!!.isGroupOwner) {
                            Toast.makeText(
                                this@MainActivity,
                                "设备连接，本设备为GroupOwner",
                                Toast.LENGTH_LONG
                            ).show();
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "设备连接，本设备非GroupOwner",
                                Toast.LENGTH_LONG
                            ).show();
                        }
                    } else {
                        Toast.makeText(this@MainActivity, "设备断开", Toast.LENGTH_LONG).show();
                    }

//                    if (wifiP2pManager != null) {
//                        wifiP2pManager!!.requestConnectionInfo(
//                            channel,
//                            connectionInfoListener
//                        )
//                    } else {
//                        Log.i("tag", "wifiP2pManager为空")
//                    }


                } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION == action) {
                    Log.i("tag", "表明该设备的配置信息发生了改变");
                    val device: WifiP2pDevice? =
                        intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)
                    Log.i(
                        "tag",
                        "本设备信息发生变化：" + device!!.deviceName + ", " + device.deviceAddress
                    )
                    // Respond to this device's wifi state changing
                } else if (WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION == action) {
                    Log.i("tag", "WIFI_P2P_DISCOVERY_CHANGED_ACTION")
    
                    //todo
                    //搜索状态发生改变时的广播
                }
            }
        }
        registerReceiver(wifiP2pReceiver, intentFilter)
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
                        this@MainActivity,
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
                    this@MainActivity,
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
                this@MainActivity,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
    
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this@MainActivity,
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
                                this@MainActivity,
                                "发起成功",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.i("tag", "发起成功")
                        }
    
                        override fun onFailure(reason: Int) {
                            // Connection failed
                            Toast.makeText(
                                this@MainActivity,
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
                Toast.makeText(this@MainActivity, "Group created", Toast.LENGTH_SHORT).show()
            }
    
            override fun onFailure(reason: Int) {
                Toast.makeText(this@MainActivity, "Failed to create group", Toast.LENGTH_SHORT)
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


    private fun discoverPeers() {
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
                isDiscover = true
                // Peers discovery initiated
                Toast.makeText(this@MainActivity, "Discovering peers", Toast.LENGTH_SHORT)
                    .show()
            }
    
            override fun onFailure(reason: Int) {
                Toast.makeText(
                    this@MainActivity,
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
                        deviceListAdapter = DeviceListAdapter(this@MainActivity, peerList.toList())
                        recyclerView?.adapter = deviceListAdapter
                    } else {
                        deviceListAdapter?.setList(peerList.toList())
                        deviceListAdapter?.notifyDataSetChanged()
                    }
                    peerList.forEach {
                        //todo Android_79b2
                        if (it.deviceName == "P6PRO_DEVICE") {
                            connectDevice = it
                            Log.i("tag", "找到设备了")
                            return@forEach
                        }
                    }
                    isDiscover = false
                } else {
                }
            }
        }
    }


​        

```
/***********************************FFMPEG*************************************************/
1.ubuntu-22.04.2镜像
2.vmware 17.5.0
3.ffmpeg 6.0.1和ndk r21-linux，ffmpeg 在linux上解压tar xf 文件 
3.终端安装gcc
	sudo apt install gcc
4.安装samba，可以在windows传输文件	
	1.sudo apt install samba
	2.在home下新建目录用来存放传输的文件，例如huangjf
	3.sudo apt install vim
	4.sudo vim /etc/samba/smb.conf，配置传输信息
		在最底部添加
		[Project]
			comment = project
			path = /home/用户名/huangjf
			browseable = yes
			writable = yes
	5.sudo touch /etc/samba/smbpasswd    //新建/etc/samba/smbpasswd文件
	   sudo smbpasswd -a shine    //设置的valid users，设置用户密码
	6.sudo /etc/init.d/samba restart
	7.在windows上测试：\\linux上的ip地址
	8.linux联网模式改成桥接模式
5.放入ndk21已解压的文件夹和ffmpeg6.0已解压的文件夹
6.修改ffmpeg6.0文件夹configure文件

# SLIBNAME_WITH_MAJOR='$(SLIBNAME).$(LIBMAJOR)'
# LIB_INSTALL_EXTRA_CMD='$$(RANLIB) "$(LIBDIR)/$(LIBNAME)"'
# SLIB_INSTALL_NAME='$(SLIBNAME_WITH_VERSION)'
# SLIB_INSTALL_LINKS='$(SLIBNAME_WITH_MAJOR) $(SLIBNAME)'
# 替换成如下
SLIBNAME_WITH_MAJOR='$(SLIBPREF)$(FULLNAME)-$(LIBMAJOR)$(SLIBSUF)'
LIB_INSTALL_EXTRA_CMD='$$(RANLIB)"$(LIBDIR)/$(LIBNAME)"'
SLIB_INSTALL_NAME='$(SLIBNAME_WITH_MAJOR)'
SLIB_INSTALL_LINKS='$(SLIBNAME)'

7.修改一个地方，libavcodec.a和libswscale.a包含同一个half2float.o，在链接libavcodec.a和libswscale.a时会出现函数重定义的错误。
需要在执行脚本之前修改libswscale文件夹下的Makefile，将OBJS下的half2float.o删除。脚本中NDK等路径根据自己电脑的路径自行修改。
8.在ffmpeg6.0文件夹里添加脚本文件,注意文件格式是linux，不要是windows，右下角可以设置
9.切换到root用户sudo su - ，进行编译
10. 执行./build.sh编译，会生成libffmpeg.so

\\192.168.30.128\Project\android-ndk-r21e-linux-x86_64\android-ndk-r21e\toolchains\llvm\prebuilt\linux-x86_64\bin
/home/root01/android-ndk-r21e-linux-x86_64/android-ndk-r21e/toolchains/llvm/prebuilt/linux-x86_64

arm64-v8a平台编译脚本：
#!/bin/sh
# NDK 所在的路径
NDK=/home/root01/huangjf/android-ndk-r21e-linux-x86_64/android-ndk-r21e
# 需要编译出的平台，这里是 arm64-v8a
ARCH=aarch64
# 支持的最低 Android API
API=21
# 编译后输出目录，在 ffmpeg 源码目录下的 /android/arm64-v8a
OUTPUT=$(pwd)/android/arm64-v8a
# NDK 交叉编译工具链所在路径
TOOLCHAIN=/home/root01/huangjf/android-ndk-r21e-linux-x86_64/android-ndk-r21e/toolchains/llvm/prebuilt/linux-x86_64
 
SYSROOT_L=$TOOLCHAIN/sysroot/usr/lib/aarch64-linux-android
GCC_L=$NDK/toolchains/aarch64-linux-android-4.9/prebuilt/linux-x86_64/lib/gcc/aarch64-linux-android/4.9.x
 
build() {
   ./configure \
   --target-os=android \
   --prefix=$OUTPUT \
   --arch=$ARCH \
   --cpu=armv8-a \
   --sysroot=$TOOLCHAIN/sysroot \
   --enable-static \
   --disable-ffmpeg \
   --disable-ffplay \
   --disable-ffprobe \
   --disable-debug \
   --disable-doc \
   --disable-avdevice \
   --disable-shared \
   --enable-cross-compile \
   --enable-asm \
   --enable-small \
   --enable-avcodec \
   --enable-avformat \
   --enable-avutil \
   --enable-swresample \
   --enable-swscale \
   --enable-avfilter \
   --enable-postproc \
   --enable-network \
   --enable-bsfs \
   --enable-filters \
   --enable-encoders \
   --enable-gpl \
   --enable-muxers \
   --enable-parsers \
   --enable-protocols \
   --enable-nonfree \
   --enable-jni \
   --cross-prefix=$TOOLCHAIN/bin/aarch64-linux-android- \
   --cc=$TOOLCHAIN/bin/aarch64-linux-android$API-clang \
   --cxx=$TOOLCHAIN/bin/aarch64-linux-android$API-clang++ \
   --extra-cflags="-fpic -march=armv8-a -I$OUTPUT/include" \
   --extra-ldflags="-lc -ldl -lm -lz -llog -lgcc -L$OUTPUT/lib"
 
   make clean all
   make -j12
   make install
}


package_library() {
   $TOOLCHAIN/bin/aarch64-linux-android-ld -L$OUTPUT/lib -L$GCC_L \
    -rpath-link=$SYSROOT_L/$API -L$SYSROOT_L/$API -soname libffmpeg.so \
    -shared -nostdlib -Bsymbolic --whole-archive --no-undefined -o $OUTPUT/libffmpeg.so \
    -lavcodec -lpostproc -lavfilter -lswresample -lavformat -lavutil -lswscale -lgcc \
    -lc -ldl -lm -lz -llog \
    --dynamic-linker=/system/bin/linker
    # 设置动态链接器，不同平台的不同，android 使用的是/system/bin/linker
}
build
package_library


armeabi-v7a平台编译脚本：
#!/bin/sh
# NDK 所在路径
NDK=/home/root01/huangjf/android-ndk-r21e-linux-x86_64/android-ndk-r21e
# 需要编译出的平台，这里是arm
ARCH=arm
# 支持的最低Android API
API=21
# 编译后输出目录，在ffmpeg 源码目录下的android/armeabi-v7a
OUTPUT=$(pwd)/android/armeabi-v7a
# NDK 交叉编译工具链所在路径
TOOLCHAIN=/home/root01/huangjf/android-ndk-r21e-linux-x86_64/android-ndk-r21e/toolchains/llvm/prebuilt/linux-x86_64

SYSROOT_L=$TOOLCHAIN/sysroot/usr/lib/arm-linux-androideabi
GCC_L=$NDK/toolchains/arm-linux-androideabi-4.9/prebuilt/linux-x86_64/lib/gcc/arm-linux-androideabi/4.9.x

#执行.configure文件(./configure)
#运行./configure --help查看配置 裁剪
#换行后不要用空格，可以使用tab

build() {
   ./configure \
   #指定目标平台
   --target-os=android \
   --prefix=$OUTPUT \
   #指定cpu架构
   --arch=$ARCH \
   #指定cpu类型
   --cpu=armv7-a \
   #配置编译环境c语言的头文件环境
   --sysroot=$TOOLCHAIN/sysroot \
   #编译动态库
   --enable-static \
   #根据需要进行裁剪
   --disable-ffmpeg \
   --disable-ffplay \
   --disable-ffprobe \
   --disable-debug \
   --disable-doc \
   --disable-avdevice \
   --disable-shared \
   --enable-asm \
   #开启交叉编译
   --enable-cross-compile \
   --enable-small \
   --enable-avcodec \
   --enable-avformat \
   --enable-avutil \
   --enable-swresample \
   --enable-swscale \
   --enable-avfilter \
   --enable-postproc \
   --enable-network \
   --enable-bsfs \
   --enable-filters \
   --enable-encoders \
   --enable-gpl \
   --enable-muxers \
   --enable-parsers \
   --enable-protocols \
   --enable-nonfree \
   --enable-jni \
   #指定交叉编译工具目录
   --cross-prefix=$TOOLCHAIN/bin/arm-linux-androideabi- \
   --cc=$TOOLCHAIN/bin/armv7a-linux-androideabi$API-clang \
   --cxx=$TOOLCHAIN/bin/armv7a-linux-androideabi$API-clang++ \
   --extra-cflags="-fpic -march=armv7-a -mcpu=cortex-a8 -mfpu=vfpv3-d16 -mfloat-abi=softfp -mthumb -I$OUTPUT/include" \
   --extra-ldflags="-lc -ldl -lm -lz -llog -lgcc -L$OUTPUT/lib"
   
   make clean all
   make -j12
   make install
}
 
package_library() {
   $TOOLCHAIN/bin/arm-linux-androideabi-ld -L$OUTPUT/lib -L$GCC_L \
    -rpath-link=$SYSROOT_L/$API -L$SYSROOT_L/$API -soname libffmpeg.so \
    -shared -nostdlib -Bsymbolic --whole-archive --no-undefined -o $OUTPUT/libffmpeg.so \
    -lavcodec -lpostproc -lavfilter -lswresample -lavformat -lavutil -lswscale -lgcc \
    -lc -ldl -lm -lz -llog \
    --dynamic-linker=/system/bin/linker
    # 设置动态链接器，不同平台的不同，android 使用的是/system/bin/linker
}
 
 
build
package_library


x86平台编译脚本：
#!/bin/sh
# NDK 所在路径
NDK=/home/root01/huangjf/android-ndk-r21e-linux-x86_64/android-ndk-r21e
# 需要编译出的平台，这里是 i686
ARCH=i686
# 支持的最低 Android API
API=21
# 编译后输出目录，在 ffmpeg 源码目录下的 /android/x86
OUTPUT=$(pwd)/android/x86
# NDK 交叉编译工具链所在路径
TOOLCHAIN=/home/root01/huangjf/android-ndk-r21e-linux-x86_64/android-ndk-r21e/toolchains/llvm/prebuilt/linux-x86_64

SYSROOT_L=$TOOLCHAIN/sysroot/usr/lib/i686-linux-android
GCC_L=$NDK/toolchains/x86-4.9/prebuilt/linux-x86_64/lib/gcc/i686-linux-android/4.9.x
 
build() {
   ./configure \
   --target-os=android \
   --prefix=$OUTPUT \
   --arch=$ARCH \
   --cpu=i686 \
   --sysroot=$TOOLCHAIN/sysroot \
   --enable-static \
   --disable-ffmpeg \
   --disable-ffplay \
   --disable-ffprobe \
   --disable-debug \
   --disable-doc \
   --disable-avdevice \
   --disable-shared \
   --disable-asm \
   --enable-neon \
   --enable-hwaccels \
   --enable-cross-compile \
   --enable-small \
   --enable-avcodec \
   --enable-avformat \
   --enable-avutil \
   --enable-swresample \
   --enable-swscale \
   --enable-avfilter \
   --enable-postproc \
   --enable-network \
   --enable-bsfs \
   --enable-filters \
   --enable-encoders \
   --enable-gpl \
   --enable-muxers \
   --enable-parsers \
   --enable-protocols \
   --enable-nonfree \
   --enable-jni \
   --cross-prefix=$TOOLCHAIN/bin/i686-linux-android- \
   --cc=$TOOLCHAIN/bin/i686-linux-android$API-clang \
   --cxx=$TOOLCHAIN/bin/i686-linux-android$API-clang++ \
   --extra-cflags="-fpic -march=i686 -mtune=intel -mssse3 -mfpmath=sse -m32 -I$OUTPUT/include" \
   --extra-ldflags="-lc -ldl -lm -lz -llog -lgcc -L$OUTPUT/lib"
 
   make clean all
   make -j12
   make install
}
   #--enable-mediacodec \
 
 
package_library() {
   $TOOLCHAIN/bin/i686-linux-android-ld -L$OUTPUT/lib -L$GCC_L \
    -rpath-link=$SYSROOT_L/$API -L$SYSROOT_L/$API -soname libffmpeg.so \
    -shared -nostdlib -Bsymbolic --whole-archive --no-undefined -o $OUTPUT/libffmpeg.so \
    -lavcodec -lpostproc -lavfilter -lswresample -lavformat -lavutil -lswscale -lgcc \
    -lc -ldl -lm -lz -llog \
    --dynamic-linker=/system/bin/linker
    # 设置动态链接器，不同平台的不同，android 使用的是/system/bin/linker
}
 
 
build
package_library

x86_64平台编译脚本：
#!/bin/sh
# NDK 所在的路径
NDK=/home/root01/huangjf/android-ndk-r21e-linux-x86_64/android-ndk-r21e
# 需要编译出的平台，这里是 x86_64
ARCH=x86_64
# 支持的最低 Android API
API=21
# 编译后输出目录，在 ffmpeg 源码目录下的 /android/x86_64
OUTPUT=$(pwd)/android/x86_64
# NDK 交叉编译工具链所在路径
TOOLCHAIN=/home/root01/huangjf/android-ndk-r21e-linux-x86_64/android-ndk-r21e/toolchains/llvm/prebuilt/linux-x86_64
 
SYSROOT_L=$TOOLCHAIN/sysroot/usr/lib/x86_64-linux-android
GCC_L=$NDK/toolchains/x86_64-4.9/prebuilt/linux-x86_64/lib/gcc/x86_64-linux-android/4.9.x
 
build() {
   ./configure \
   --target-os=android \
   --prefix=$OUTPUT \
   --arch=$ARCH \
   --cpu=x86-64 \
   --sysroot=$TOOLCHAIN/sysroot \
   --enable-static \
   --disable-ffmpeg \
   --disable-ffplay \
   --disable-ffprobe \
   --disable-debug \
   --disable-doc \
   --disable-avdevice \
   --disable-shared \
   --enable-cross-compile \
   --enable-asm \
   --enable-small \
   --enable-avcodec \
   --enable-avformat \
   --enable-avutil \
   --enable-swresample \
   --enable-swscale \
   --enable-avfilter \
   --enable-postproc \
   --enable-network \
   --enable-bsfs \
   --enable-filters \
   --enable-encoders \
   --enable-gpl \
   --enable-muxers \
   --enable-parsers \
   --enable-protocols \
   --enable-nonfree \
   --enable-jni \
   --cross-prefix=$TOOLCHAIN/bin/x86_64-linux-android- \
   --cc=$TOOLCHAIN/bin/x86_64-linux-android$API-clang \
   --cxx=$TOOLCHAIN/bin/x86_64-linux-android$API-clang++ \
   --extra-cflags="-fpic -march=x86-64 -msse4.2 -mpopcnt -m64 -mtune=intel -I$OUTPUT/include" \
   --extra-ldflags="-lc -ldl -lm -lz -llog -lgcc -L$OUTPUT/lib"
 
   make clean all
   make -j12
   make install
}
 
package_library() {
   $TOOLCHAIN/bin/x86_64-linux-android-ld -L$OUTPUT/lib -L$GCC_L \
    -rpath-link=$SYSROOT_L/$API -L$SYSROOT_L/$API -soname libffmpeg.so \
    -shared -nostdlib -Bsymbolic --whole-archive --no-undefined -o $OUTPUT/libffmpeg.so \
    -lavcodec -lpostproc -lavfilter -lswresample -lavformat -lavutil -lswscale -lgcc \
    -lc -ldl -lm -lz -llog \
    --dynamic-linker=/system/bin/linker
    # 设置动态链接器，不同平台的不同，android 使用的是/system/bin/linker
}
 
 
build
package_library



//合并
# armv8a
# 需要编译出的平台，这里是arm
ARCH=aarch64
CPU=armv8-a
CROSS_PREFIX=$TOOLCHAIN/bin/aarch64-linux-android-
CC=$TOOLCHAIN/bin/aarch64-linux-android$API-clang
CXX=$TOOLCHAIN/bin/aarch64-linux-android$API-clang++
OUTPUT=$(pwd)/android/arm64-v8a
CFLAGS="-fpic -march=armv8-a -I$OUTPUT/include"
SYSROOT_L=$TOOLCHAIN/sysroot/usr/lib/aarch64-linux-android
GCC_L=$NDK/toolchains/aarch64-linux-android-4.9/prebuilt/linux-x86_64/lib/gcc/aarch64-linux-android/4.9.x
LIBRARY=$TOOLCHAIN/bin/aarch64-linux-android-ld
 
 
build
package_library


# armv7a
# 需要编译出的平台，这里是arm
ARCH=arm
CPU=armv7-a
CROSS_PREFIX=$TOOLCHAIN/bin/arm-linux-androideabi-
CC=$TOOLCHAIN/bin/armv7a-linux-androideabi$API-clang
CXX=$TOOLCHAIN/bin/armv7a-linux-androideabi$API-clang++
OUTPUT=$(pwd)/android/armeabi-v7a
CFLAGS="-fpic -march=armv7-a -mcpu=cortex-a8 -mfpu=vfpv3-d16 -mfloat-abi=softfp -mthumb -I$OUTPUT/include"
SYSROOT_L=$TOOLCHAIN/sysroot/usr/lib/arm-linux-androideabi
GCC_L=$NDK/toolchains/arm-linux-androideabi-4.9/prebuilt/linux-x86_64/lib/gcc/arm-linux-androideabi/4.9.x
LIBRARY=$TOOLCHAIN/bin/arm-linux-androideabi-ld
 
 
build
package_library


#x86_64/android-ndk-r21e
ARCH=x86_64
CPU=x86-64
CROSS_PREFIX=$TOOLCHAIN/bin/x86_64-linux-android-
CC=$TOOLCHAIN/bin/x86_64-linux-android$API-clang
CXX=$TOOLCHAIN/bin/x86_64-linux-android$API-clang++
OUTPUT=$(pwd)/android/x86_64
CFLAGS="-fpic -march=x86-64 -msse4.2 -mpopcnt -m64 -mtune=intel -I$OUTPUT/include"
SYSROOT_L=$TOOLCHAIN/sysroot/usr/lib/x86_64-linux-android
GCC_L=$NDK/toolchains/x86_64-4.9/prebuilt/linux-x86_64/lib/gcc/x86_64-linux-android/4.9.x
LIBRARY=$TOOLCHAIN/bin/x86_64-linux-android-ld
build
package_library

#x86
ARCH=i686
CPU=i686
CROSS_PREFIX=$TOOLCHAIN/bin/i686-linux-android-
CC=$TOOLCHAIN/bin/i686-linux-android$API-clang
CXX=$TOOLCHAIN/bin/i686-linux-android$API-clang++
OUTPUT=$(pwd)/android/x86
CFLAGS="-fpic -march=i686 -mtune=intel -mssse3 -mfpmath=sse -m32 -I$OUTPUT/include"
SYSROOT_L=$TOOLCHAIN/sysroot/usr/lib/i686-linux-android
GCC_L=$NDK/toolchains/x86-4.9/prebuilt/linux-x86_64/lib/gcc/i686-linux-android/4.9.x
LIBRARY=$TOOLCHAIN/bin/i686-linux-android-ld
build
package_library


enable-static
disable-shared

   --enable-libx264 \
   --enable-libx265 \
   #指定禁用所有组件
   --disable-all \	
/***********************************FFMPEG*************************************************/
```

```
/**********************************IJKPLAYER***********************************************/
1.ubuntu-22.04.2镜像
2.vmware 17.5.0
3.搭建samba
4.window平台连接linux新建ijk目录
sudo apt-get update
sudo apt-get install git
sudo apt-get install yasm
sudo apt-get install make
5.切换root，输入"sudo su -"
6.安装jdk8 "sudo apt-get install openjdk-8-jdk"
7.export  JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
8.window下载android-ndk-r10eandroid和android-sdk_r24.4.1-linux，解压到linux系统
sdk可以使用命令：sudo wget http://dl.google.com/android/android-sdk_r24.4.1-linux.tgz 
9.解压sdk：tar -zxvf android-sdk_r24.4.1-linux.tgz
10.sh android-sdk-linux/tools/android update sdk -u 下载脚本
11.vim /etc/profile末尾加入
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
export PATH=/home/root01/huangjf/ijk/android-sdk-linux/platform-tools:$PATH
export PATH=/home/root01/huangjf/ijk/android-sdk-linux/tools:$PATH
export ANDROID_NDK=/home/root01/huangjf/ijk/android-ndk-r10e-linux-x86_64/android-ndk-r10e
export PATH=/home/root01/huangjf/ijk/android-ndk-r10e-linux-x86_64:$PATH
export PATH=$ANDROID_NDK:$PATH
11.1source /etc/profile
11.2 ndk-build --version 可以查看版本

实现vmware与windows复制粘贴
sudo apt-get autoremove open-vm-tools
sudo apt-get install open-vm-tools
sudo apt-get install open-vm-tools-desktop

12.将ijkplayer代码复制到linux系统或使用git：git clone https://github.com/Bilibili/ijkplayer.git ijkplayer(这里不行，一定要控制台下载，否则后面会编译报错)
用：1.wget https://codeload.github.com/bilibili/ijkplayer/zip/refs/tags/k0.8.8
       2.unzip k0.8.8

13.cd ijkplayer 初始化脚本：./init-android.sh,执行之前注释##pull_fork "armv5"

   把armv5去掉
	init-android-openssl.sh
	init-android.sh
	init-android-libsoxr.sh
	./android/contrib/compile-openssl.sh
	./android/contrib/compile-libsoxr.sh
	./android/compile-ijk.sh
    
    出现格式报错（tools/pull-repo-base.sh: 行 2: $'\r': 未找到命令）
    解决：sudo apt-get install dos2unix
    用命令转化：dos2unix test.sh
	1.转化tools下所有sh文件
	2.转化init-config.sh
	3.转化init-android-libyuv.sh
	4.转化init-android-soundtouch.sh
	5.转化init-android-openssl.sh
	6.转化android/contrib/compile-openssl.sh
 	7.android/contrib/tools 所有sh
	8.android/contrib所有sh

    连接github超时
    解决：vi /etc/hosts
    加入：140.82.114.3 github.com

   问题：RPC 失败。curl 92 HTTP/2 stream 0 was not closed cleanly: CANCEL (err 8)
   解决：git config --global http.postBuffer 524288000
      	git config --global --unset http.proxy
	git config --global --unset https.proxy

13.1去除init-android-openssl.sh里armv5
14.输入./init-android-openssl.sh
15.cd config 在module-default.sh增加以下几行配置：
export COMMON_FF_CFG_FLAGS="$COMMON_FF_CFG_FLAGS --disable-linux-perf"
export COMMON_FF_CFG_FLAGS="$COMMON_FF_CFG_FLAGS --disable-vda"
export COMMON_FF_CFG_FLAGS="$COMMON_FF_CFG_FLAGS --disable-ffserver"
export COMMON_FF_CFG_FLAGS="$COMMON_FF_CFG_FLAGS --disable-bzlib"
16.然后删除 module.sh文件，重新生成module.sh 文件
   ln -s module-lite.sh module.sh
17.cd android/contrib
   去除/compile-openssl.s “armv5”
   ./compile-openssl.sh clean
   ./compile-openssl.sh all
18.cd ../
cd android/contrib,删除所有sh里的“armv5”
./compile-ffmpeg.sh clean
./compile-ffmpeg.sh all
   问题：.....config/module.sh: line 1: module-lite.sh: command not found
    解决方式：进入ijkpalyer-master／config
    rm module.sh
    ln -s module-lite.sh module.sh
19.cd ..
删除所有sh里的“armv5”
./compile-ijk.sh clean
./compile-ijk.sh all
20.添加拍照和录像功能
向config 目录下的module-lite.sh 文件末尾添加以下内容：（目录在ijkplayer/config）
export COMMON_FF_CFG_FLAGS="$COMMON_FF_CFG_FLAGS --enable-protocol=rtp"
export COMMON_FF_CFG_FLAGS="$COMMON_FF_CFG_FLAGS --enable-demuxer=rtsp"
export COMMON_FF_CFG_FLAGS="$COMMON_FF_CFG_FLAGS --enable-muxer=mjpeg"
export COMMON_FF_CFG_FLAGS="$COMMON_FF_CFG_FLAGS --enable-decoder=mjpeg"
export COMMON_FF_CFG_FLAGS="$COMMON_FF_CFG_FLAGS --enable-demuxer=mjpeg"
export COMMON_FF_CFG_FLAGS="$COMMON_FF_CFG_FLAGS --enable-encoder=mjpeg"
21.解决报错问题，下一帧比上一帧时间短
cd extra/ffmpeg/libavformat
vi mux.c
    if (sti->cur_dts && sti->cur_dts != AV_NOPTS_VALUE &&
        ((!(s->oformat->flags & AVFMT_TS_NONSTRICT) &&
          st->codecpar->codec_type != AVMEDIA_TYPE_SUBTITLE &&
          st->codecpar->codec_type != AVMEDIA_TYPE_DATA &&
          sti->cur_dts >= pkt->dts) || sti->cur_dts > pkt->dts)) {}

22.然后重新编译ffmpeg库，在 ijkplayer-android / android / contrib 目录下：
./compile-ffmpeg.sh clean  //清理以前编译的ffmpeg库文件
./compile-ffmpeg.sh all
21.添加代码
https://blog.csdn.net/iqq_37382732/article/details/109101681
22.编译ijkplayer
cd ..
./compile-ijk.sh clean
./compile-ijk.sh armv7a
/**********************************IJKPLAYER***********************************************/
```

```
/***********************************************性能优化*************************************************************************/
1.启动优化
	1.1 提高线程等级，核心线程绑定CPU大核
		Process.setThreadPriority(int priority) / Process.setThreadPriority(int pid, int priority)
		Thread.setPriority(int priority)
		1.1.1主线程的优先级调整很简单，直接在 Application 的 attachBaseContext() 调用 Process.setThreadPriority(-19)，将主线程设置为最高级别优先级即可。
		1.1.2 获取渲染线程id，设置Process.setThreadPriority(int pid, int priority)，在绑定CPU大核
	1.2 线程池配置
	插桩方式确认任务属于哪种类型
		CPU线程池：CPU线程池  == Executors.newFixedThreadPool
                核心=最大=CPU核
                keepAliveTime=0
                workqueue=无限大或设置
		IO线程池：IO线程池(设置线程优先级比CPU高)
                核心=0
                最大=60以上
                keepAlive=60s
                workqueue=SynchronousQueue
	1.3 减少CPU闲置，执行预准备任务
		times函数判断CPU是否闲置，当CPU速率一定在0.1以下，就可认为是闲置状态
	1.4 dex重排序 用Redex
2.ANR
    2.1 ANR 日志准备（traces.txt + mainlog）
    2.2 在 traces.txt 找到 ANR 信息（发生 ANR 时间节点、主线程状态、事故点、ANR 类型）
    2.3 在mainlog 日志分析发生 ANR 时的 CPU 状态
    2.4 在 traces.txt 分析发生 ANR 时的 GC 情况（分析内存）
    简单说就是我们至少需要两份文件：/data/anr/traces.txt 和 mainlog 日志。如果有eventlog 能更快的定位到 ANR 的类型，当然 traces.txt 和 mainlog 也能分析得到。
traces.txt 文件通过命令 adb pull /data/anr/ 获取，如果没有权限，用adb bugreport
mainlog 日志需要在程序运行时就时刻记录 adb logcat -v time -b main > mainlog.log。
	mainlog 日志 搜索关键词 ANR in
	在 eventlog 日志 搜索关键词 am_anr
	ANR 定位分析总结如下：
        在 traces.txt 找到发生 ANR 时间节点、主线程的状态、ANR 类型和事故点
        在 mainlog 日志查看 CPU 状态
        根据以上步骤收集的信息大致判断问题原因
        是 CPU 问题还是 非 CPU 问题
        如果是非 CPU 问题，那么看 GC 处理信息
        在 traces.txt 分析 CG 信息
        结合项目代码和以上步骤分析到的原因，定位到问题修复 ANR
        其实 ANR 发生的原因本质上只有三个：
        线程挂起
        CPU 不给资源
        GC 触发 STW 导致线程执行时间被拉长

3.UI卡顿
4.全局异常捕获
5.布局优化
    5.1 手机开发者选项开启显示GPU过度绘制调试开关，分别有蓝色、淡绿、淡红、深红四种不同情况
    5.2 手机开发者选项开启GPU呈现模式分析
    5.3 theme去除主题默认背景
    	<resource>
            <style name="Theme.NoBackground" parent="android:Theme">
                <item name="android:windowBackground">@android:color/transparent</item>
            </style>
		</resources>
	5.4 截取部分，避免过度绘制
	5.5 背景图使用9-patches
	5.6 减少层级嵌套
	5.7 布局标签：<include><merge><viewstub>
		每次在调用 LayoutInflater.inflate() 时，必须为 <merge> 布局文件提供一个view，作为它的父容器：
		LayoutInflater.from(parent.getContext()).inflate(R.layout.merge_layout, parent, true);
	5.8 硬件加速
6.内存性能优化：
    6.1 Android系统通过在Android Lollipop替换Dalvik为ART，且在Android N添加JIT的方式提升编译安装性能
    6.2 对象内存管理，避免内存抖动、内存泄漏
    6.3 按需要提供变量的基本数据类型
    6.4 避免自动装箱拆箱的转换
    6.5 在某些场景使用Android提供的SparseArray集合组和ArrayMap代替HashMap能达到内存高效
    6.6 尽量使用for each循环
    6.7 使用Annotation @IntDef实现枚举
    6.8 常量使用static final声明能节约内存
    6.9 使用StringBuilder或StringBuffer拼接字符串+
    6.10 生命周期内不变的变量声明为本地变量对象，不在方法内创建节省内存
    6.11 对象数量固定不变的列表，使用数组比集合更内存高效
    6.12 尽量少创建临时对象，因为会频繁触发垃圾回收；避免实例化非必要对象，因为会对内存和计算性能带来影响
    6.13 对于要大量创建耗费资源的对象时，使用对象池模式或享元模式
    	对象池
    	public abstract class ObjectPool<T> {
            // 使用两个SparseArray数组保存对象集合，防止这些对象在借出后被系统回收
            private SparseArray<T> freePool;
            private SparseArray<T> lentPool;
            private int maxCapacity;

            public ObjectPool(int initialCapacity, int maxCapacity) {
                initialize(initialCapacity);
                this.maxCapacity = maxCapacity;
            }

            public ObjectPool(int maxCapacity) {
                this(maxCapacity / 2, maxCapacity);
            }

            public T acquire() {
                T t = null;
                synchronized(freePool) {
                    int freeSize = freePool.size();
                    for (int i = 0; i < freeSize; i++) {
                        int key = freePool.keyAt(i);
                        t = freePool.get(key);
                        if (t != null) {
                            this.lentPool.put(key, t);
                            this.freePool.remove(key);
                            return t;
                        }
                    }
                    if (t == null && lentPool.size() + freeSize < maxCapacity) {
                        t = create();
                        lentPool.put(lentPool.size() + freeSize, t);
                    }
                }
                return t;
            }

            public void release(T t) {
                if (t == null) {
                    return null;
                }
                int key = lentPool.indexOfValue(t);
                restore(t);
                this.freePool.put(key, t);
                this.lentPool.remove(key);
            }

            protected abstract T create();

            protected void restore(T t) {}

            private void initialize(final int initialCapacity) {
                lentPool = new SparseArray<>();
                freePool = new SparseArray<>();
                for (int i = 0; i < initialCapacity; i++) {
                    freePool.put(i, create());
                }
            }
        }
        享元模式
        public interface Courier<T> {
            void equip(T param);
        }

        // 享元对象
        public class PackCourier implements Courier<Pack> {
            private Van van;

            // id为内部状态且用于唯一标识一个对象，用在Factory中实现对象复用
            public PackCourier(int id) {
                super(id);
                van = new Van(id);
            }

            // pack为外部状态
            public void equip(Pack pack) {
                van.load(pack);
            }
        }

        // 客户，通过courier.equip(pack)将外部状态pack传入享元对象courier
        public class Delivery extends Id {
            private Courier<Pack> courier;

            public Delivery(int id) {
                super(id);
                courier = new Factory().getCourier(0);
            }

            public void deliver(Pack pack, Destination destination) {
                courier.equip(pack);
            }
        }

        public class Factory {
            private static SparseArray<Courier> pool;

            public Factory() {
                if (pool == null)
                    pool = new SparseArray<>();
            }

            public Courier getCourier(int type) {
                Courier courier = pool.get(type);
                if (courier == null) {
                    courier = create(type);
                    pool.put(type, courier);
                }
                return courier;
            }

            private Courier create(int type) {
                Courier courier = null;
                switch(type) {
                    case 0:
                        courier = new PackCourier(0);
                }
                return courier;
            }
        }

        // 每个Delivery都是由同一个Courier完成操作
        for (int i = 0; i < DEFAULT_COURIER_NUMBER; i++) {
            new Delivery(i).deliver(new Pack(i), new Destination(i));
        }
        
        6.14 bitmap优化
        private BitmapPool bitmapPool;
        public Bitmap decodeBitmap(Context context, File file, int reqWidth, int reqHeight) {
            bitmapPool = GlideApp.get(context).getBitmapPool();

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inMutable = true; // 复用 inBitmap 需要将 inMutable 设置为 true
            options.inBitmap = bitmapPool.getDirty(options.outWidth, options.outHeight, options.inPreferredConfig);
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            // 处理 BitmapFactory.decodeXxx() 复用失败时，按普通方式加载处理
            if (bitmap == null && options.inBitmap != null) {
                bitmapPool.put(options.inBitmap);
                options.inBitmap = null;
                bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            }
            if (bitmap != null && options.inBitmap != null) {
                bitmapPool.put(options.inBitmap);
            }
            return bitmap;
        }
        
        private int calculateSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
            // 图片原始宽高
            int width = options.outWidth;
            int height = options.outHeight;

            // 默认不缩放
            int inSampleSize = 1; 
            // 当原始图片宽高大于控件所需宽或高才进行缩放
            if (width > reqWidth || height > reqHeight) {
                // 取宽度比与高度比的最大值
                int widthRound = Math.round(width * 1f / reqWidth);
                int heightRound = Math.round(height * 1f / reqHeight);

                inSampleSize = Math.max(widthRound, heightRound);
            }

            return inSampleSize;
        }
7.第三方框架优化
	7.1 okhttp
		Dispatcher 提供了配置异步网络请求的最大数量 maxRequests 属性，默认最大请求数量为64
		Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(32);//重新配置为32
        new OkHttpClient.Builder()
            .dispatcher(dispatcher);
    7.2 Glide
    	限制 Glide 异步图片加载开启线程数量
    	private static final int DEFAULT_DISK_CACHE_SIZE = 20 * 1024 * 1024;
		private static final int LOW_DISK_CACHE_SIZE = 5 * 1024 * 1024;
    	@GlideModule
        public class MyGlideModule extends AppGlideModule {
            @Override
            public boolean isManifestParsingEnabled() {
                return false;
            }

            @Override
            public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
                super.applyOptions(context, builder);               
                // 默认使用 ARGB_8888，改为RGB_565
                builder.setDefaultRequestOptions(new RequestOptions().format(DecodeFormat.PREFER_RGB_565)
                        .set(Downsampler.ALLOW_HARDWARE_CONFIG, true)
                        .encodeQuality(70)
                        .timeout(25000));
                // 设置磁盘缓存大小
                builder.setDiskCache(new InternalCacheDiskCacheFactory(context, DiskCache.Factory.DEFAULT_DISK_CACHE_DIR, DEFAULT_DISK_CACHE_SIZE));
                // 设置图片异步加载的线程数量为2个
                builder.setSourceExecutors(GlideExecutor.newSourceExecutor(
                        2, 
                        "source", 
                        GlideExecutor.UncaughtThrowableStrategy.DEFAULT));
                // 设置缓存大小，设置为原始缓存的一半
                MemorySizeCalculator calculator = new MemorySizeCalculator.Builder(context).build();
                    builder.setMemoryCache(new LruResourceCache(calculator.getMemoryCacheSize() / 2));
                    builder.setBitmapPool(new LruBitmapPool(calculator.getBitmapPoolSize() / 2));
                    builder.setArrayPool(new LruArrayPool(calculator.getBitmapPoolSize() / 2));
                    }
        }
        
        列表滑动暂停加载图片
        recyclerView.addOnScrollListener(new RecyclerView.onScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                GlideRequests requests = Glide.with(context);
                if (newState != RecyclerView.SCROLL_STATE_IDLE) {
                    if (!requests.isPaused()) {
                        requests.pauseRequests(); // 列表滑动时暂停图片加载
                    }
                } else {
                    if (requests.isPaused()) {
                        requests.resumeRequests(); // 列表停止滑动时恢复图片加载
                    }
                }
            }
        });



     





        
/***********************************************性能优化*************************************************************************/
```
管道（半双工,父写子读，或子写父读）
1.特殊文件
2.父子进程才能使用,fd[0],fd[1]
3.不能自己写自己读
4.数据一旦读走，就不能再次读

信号

共享内存

socket