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

        
        
        
        
        
        
        
        
        
        


