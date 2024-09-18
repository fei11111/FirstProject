package com.fei.action.bluetooth.simple

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.common.base.BaseActivity
import com.common.viewmodel.EmptyViewModel
import com.fei.firstproject.databinding.ActivitySimpleBlueToothBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.IOException
import java.io.OutputStream
import java.lang.reflect.InvocationTargetException
import java.util.UUID


/**
 * 经典蓝牙
 */
class SimpleBluetoothActivity : BaseActivity<EmptyViewModel, ActivitySimpleBlueToothBinding>() {

    private val REQUEST_ENABLE_BT = 0x11
    private var bluetoothManager: BluetoothManager? = null
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var mBluetoothBroadcastReceiver: BluetoothBroadcastReceiver? = null
    private var isBtEnable = false //是否可以开启蓝牙
    private val deviceList = ArrayList<BluetoothDevice>(10)
    private var adapter: SimpleBluetoothAdapter? = null
    private var mBluetoothsocket: BluetoothSocket? = null

    companion object {
        private const val TAG = "SimpleBluetoothActivity"
    }

    private inner class BluetoothBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val action = intent.action
            Log.i(TAG, "Action received is $action")
            if (ActivityCompat.checkSelfPermission(
                    context!!,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }

            //蓝牙搜索
            if (BluetoothDevice.ACTION_FOUND == action) {
                val scanDevice =
                    intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                Log.i(TAG, scanDevice.toString())
                if (scanDevice == null || scanDevice.name == null) {
                    return
                }
                Log.i(
                    TAG,
                    scanDevice.name + " " + scanDevice.address + " " + scanDevice.type + " "
                )
//                val btType = scanDevice.type
//                if (btType == BluetoothDevice.DEVICE_TYPE_LE || btType == BluetoothDevice.DEVICE_TYPE_UNKNOWN) {
//                    return
//                }

                //将搜索到的蓝牙设备加入列表
                deviceList.add(scanDevice)
                val rssi = intent.extras!!.getShort(BluetoothDevice.EXTRA_RSSI)
                Log.i(TAG, "rssi=$rssi")
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED == action) {
                val btDevice =
                    intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                if (btDevice != null) {
                    val state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1)
                    if (state == BluetoothDevice.BOND_NONE) {
                        Log.i(TAG, "已取消与设备" + btDevice.name + "的配对")
                    } else if (state == BluetoothDevice.BOND_BONDED) {
                        Log.i(TAG, "与设备" + btDevice.name + "配对成功")
                    }
                }
            } else if (BluetoothAdapter.ACTION_STATE_CHANGED == action) {
                val blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0)
                when (blueState) {
                    BluetoothAdapter.STATE_TURNING_ON -> Log.i(
                        TAG,
                        "STATE_TURNING_ON 手机蓝牙正在开启"
                    )

                    BluetoothAdapter.STATE_ON -> Log.i(TAG, "STATE_ON 手机蓝牙开启")
                    BluetoothAdapter.STATE_TURNING_OFF -> Log.i(
                        TAG,
                        "STATE_TURNING_OFF 手机蓝牙正在关闭"
                    )

                    BluetoothAdapter.STATE_OFF -> Log.i(TAG, "STATE_OFF 手机蓝牙关闭")
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED == action) {
                adapter!!.notifyDataSetChanged() //通知ListView适配器更新
            }
        }
    }

    private inner class SimpleBluetoothAdapter :
        RecyclerView.Adapter<SimpleBluetoothAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            init {
                itemView.setOnClickListener {
                    val device = deviceList[adapterPosition]
                    if (ActivityCompat.checkSelfPermission(
                            mContext,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return@setOnClickListener
                    }
                    if (device.bondState != BluetoothDevice.BOND_BONDED) {
                        pair(device)
                    } else {
                        unpair(device)
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                View.inflate(
                    parent.context,
                    android.R.layout.simple_list_item_1,
                    null
                )
            )
        }

        override fun getItemCount(): Int {
            return deviceList.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if (ActivityCompat.checkSelfPermission(
                    mContext,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            (holder.itemView as TextView).text =
                "名字 = " + deviceList[position].name + "地址 = " + deviceList[position].address + "类型 =  " + deviceList[position].type

        }
    }

    /**
     * 配对
     */
    private fun pair(device: BluetoothDevice) {
        //在配对之前，停止搜索
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        if (bluetoothAdapter!!.isDiscovering) {
            bluetoothAdapter!!.cancelDiscovery()
        }
        if (device.bondState != BluetoothDevice.BOND_BONDED) { //没配对才配对
            try {
                val createBondMethod = BluetoothDevice::class.java.getMethod("createBond")
                val returnValue = createBondMethod.invoke(device) as Boolean
                if (returnValue) {
                    Log.i(TAG, "配对成功...")
                }
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            }
        }else {
            connect(device)
        }
    }

    /**
     * 取消配对
     */
    fun unpair(device: BluetoothDevice) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        Log.d(TAG, "attemp to cancel bond:" + device.name)
        try {
            val removeBondMethod = device.javaClass.getMethod("removeBond")
            val returnValue = removeBondMethod.invoke(device) as Boolean
            if (returnValue) {
                Log.d(TAG, "解配对成功...")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "attemp to cancel bond fail!")
        }
    }


    /**
     * 连接 （在配对之后调用）
     * @param device
     */
    private fun connect(device: BluetoothDevice) {
        if (device == null) {
            Log.d(TAG, "bond device null")
            return
        }

        //连接之前把扫描关闭
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        if (bluetoothAdapter!!.isDiscovering) {
            bluetoothAdapter!!.cancelDiscovery()
        }

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try {
                mBluetoothsocket = device.createRfcommSocketToServiceRecord(
                    UUID.fromString("00001106-0000-1000-8000-00805F9B34FB")
                )
                if (mBluetoothsocket != null && !mBluetoothsocket!!.isConnected) {
                    mBluetoothsocket!!.connect()
                }
            } catch (e: IOException) {
                Log.e(TAG, "socket连接失败")
                try {
                    mBluetoothsocket!!.close()
                } catch (e1: IOException) {
                    e1.printStackTrace()
                    Log.e(TAG, "socket关闭失败")
                }
            }
            }
        }
    }

    /**
     * 读取数据
     */
    private suspend fun readDataFromSocket():String{
                val sb = StringBuffer()
                var inputStream:BufferedInputStream? = null
                try {
                    inputStream =  BufferedInputStream(mBluetoothsocket!!.inputStream)
                    var length = 0;
                    val buf = ByteArray(1024)

                    while ((inputStream.read(buf)).also { length = it } != -1) {
                        sb.append(String(buf,0,length));
                    }
                    return sb.toString()
                } catch (e: IOException) {
                    e.printStackTrace()
                }finally {
                    try {
                        inputStream!!.close();
                    } catch (e:IOException ) {
                        e.printStackTrace();
                    }
            }
        return ""
    }

    /**
     * 写数据
     */
    private suspend fun writeDataToSocket(str:String){
        var outputstream:OutputStream? = null
        try {
            outputstream = mBluetoothsocket!!.outputStream
            outputstream.write(str.toByteArray())
        } catch (e: IOException) {
            e.printStackTrace()
        }finally {
            try {
                outputstream!!.close();
            } catch (e:IOException ) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 蓝牙是否连接
     * @return
     */
    private fun isConnectBlue(): Boolean {
        return mBluetoothsocket != null && mBluetoothsocket!!.isConnected()
    }

    /**
     * 断开连接
     * @return
     */
   private fun cancelConnect(): Boolean {
        if (mBluetoothsocket != null && mBluetoothsocket!!.isConnected()) {
            try {
                mBluetoothsocket!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
                return false
            }
        }
        mBluetoothsocket = null
        return true
    }


    override fun createObserver() {
        mBinding.btnScan.setOnClickListener {
            if (isBtEnable) {
                scanBluetooth()
            }
        }
        mBinding.btnStopScan.setOnClickListener {
            if(isBtEnable) {
                stopScanBluetooth()
            }
        }
    }

    override fun initViewAndData(savedInstanceState: Bundle?) {
        if (isSupportBluetooth() && isOpenBluetooth()) {
            isBtEnable = true
            initRecyclerView()
            registerBtReceiver()
        }
    }

    private fun initRecyclerView() {
        adapter = SimpleBluetoothAdapter()
        mBinding.recycler.layoutManager =
            LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        mBinding.recycler.adapter = adapter
    }

    /**
     * 注册蓝牙广播
     */
    private fun registerBtReceiver() {
        mBluetoothBroadcastReceiver = BluetoothBroadcastReceiver()
        val filter = IntentFilter()
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED) //连接成功
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED) //连接断开
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED) //开始扫描
        filter.addAction(BluetoothDevice.ACTION_FOUND) //扫描到设备会通过该广播获取到
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED) //设备的绑定状态
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED) //蓝牙状态
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED) //发起扫描
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED) //结束扫描
        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED) //连接状态
        mContext.registerReceiver(mBluetoothBroadcastReceiver!!, filter)
        Log.i(TAG, "广播注册成功")
    }

    /**
     * 开始扫描,会自动停止扫描
     */
    @SuppressLint("MissingPermission")
    private fun scanBluetooth() {
        if (bluetoothAdapter!!.isDiscovering) {
            bluetoothAdapter!!.cancelDiscovery();
        }
        bluetoothAdapter!!.startDiscovery()
        Log.i(TAG, "开始扫描")
    }

    @SuppressLint("MissingPermission")
    private fun  stopScanBluetooth() {
        if (bluetoothAdapter!!.isDiscovering) {
            bluetoothAdapter!!.cancelDiscovery();
        }
    }

    /**
     * 是否支持蓝牙
     */
    private fun isSupportBluetooth(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            bluetoothManager = getSystemService(BluetoothManager::class.java)
            bluetoothAdapter = bluetoothManager!!.adapter ?: return false
        }

//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED ||
//            ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                requestPermissions(
//                    arrayOf(
//                        Manifest.permission.ACCESS_FINE_LOCATION,
//                        Manifest.permission.ACCESS_COARSE_LOCATION
//                    ), 0x11
//                )
//            }
//            return false
//        }
//
//        //定位开启
//        if (!isLocationEnabled()) {
//            Toast.makeText(
//                this@SimpleBluetoothActivity,
//                "Please enable Location",
//                Toast.LENGTH_SHORT
//            )
//                .show()
//            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
//            return false
//        }
        return true
    }

    //定位是否打开
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

    /**
     * 蓝牙是否打开
     */
    private fun isOpenBluetooth(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ), 0x11
                )
            }
            return false
        }
        if (bluetoothAdapter!!.isEnabled) {
            return true
        } else {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                scanBluetooth()
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onDestroy() {
        super.onDestroy()

        if (mBluetoothsocket!= null && mBluetoothsocket!!.isConnected) {
            cancelConnect()
        }
        if (bluetoothAdapter != null && bluetoothAdapter!!.isDiscovering) {
            bluetoothAdapter!!.cancelDiscovery()
        }
        if (mBluetoothBroadcastReceiver != null) {
            mContext.unregisterReceiver(mBluetoothBroadcastReceiver!!)
        }
    }
}