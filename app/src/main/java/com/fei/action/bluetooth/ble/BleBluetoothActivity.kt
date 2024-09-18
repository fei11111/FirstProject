package com.fei.action.bluetooth.ble

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.common.base.BaseActivity
import com.common.viewmodel.EmptyViewModel
import com.fei.firstproject.databinding.ActivitySimpleBlueToothBinding
import java.util.UUID

/**
 *    author : huangjf
 *    date   : 2024/9/5 11:26
 *    desc   :
 */
class BleBluetoothActivity:BaseActivity<EmptyViewModel,ActivitySimpleBlueToothBinding>() {

    private val REQUEST_ENABLE_BT = 0x11
    private var bluetoothManager: BluetoothManager? = null
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var isBtEnable = false //是否可以开启蓝牙
    private val deviceList = ArrayList<BluetoothDevice>(10)
    private var adapter: SimpleBluetoothAdapter? = null
    private var bluetoothGatt: BluetoothGatt? = null

    companion object {
        const val TAG = "BleBluetoothActivity"
    }

    override fun createObserver() {
        mBinding.btnScan.setOnClickListener {
            if (isBtEnable) {
                scanBluetooth()
            }
        }
        mBinding.btnStopScan.setOnClickListener {
            if(isBtEnable) {
                stopScan()
            }
        }
    }

    override fun initViewAndData(savedInstanceState: Bundle?) {
        if (isSupportBluetooth() && isOpenBluetooth()) {
            isBtEnable = true
            initRecyclerView()
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


    /**
     * 是否支持蓝牙
     */
    private fun isSupportBluetooth(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            bluetoothManager = getSystemService(BluetoothManager::class.java)
            bluetoothAdapter = bluetoothManager!!.adapter ?: return false
        }

        return true
    }


    private fun initRecyclerView() {
        adapter = SimpleBluetoothAdapter()
        mBinding.recycler.layoutManager =
            LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        mBinding.recycler.adapter = adapter
    }

    /**
     * 连接回调
     */
    private val gattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                //已连接
                Log.i(TAG, "已连接")
                //发现服务
                bluetoothGatt?.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                //已断开连接
                Log.i(TAG, "已断开连接")
            }
        }

        @SuppressLint("MissingPermission")
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            // 调用 mBleGatt?.discoverServices() 时触发该回调
            if (status != BluetoothGatt.GATT_SUCCESS) {
                //失败
                return
            }
            //获取指定GATT服务，UUID 由远程设备提供
            val bleGattService = bluetoothGatt?.getService(UUID.fromString("8888888"))
            //获取指定GATT特征，UUID 由远程设备提供
            val bleGattCharacteristic = bleGattService?.getCharacteristic(UUID.fromString("777777"))
            //启用特征通知，如果远程设备修改了特征，则会触发 onCharacteristicChange() 回调
            bluetoothGatt?.setCharacteristicNotification(bleGattCharacteristic, true)
            //启用客户端特征配置【固定写法】
            val bleGattDescriptor =
                bleGattCharacteristic?.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
            bleGattDescriptor?.value = BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
            bluetoothGatt?.writeDescriptor(bleGattDescriptor)
        }

        //启用客户端特征配置结果回调
        override fun onDescriptorWrite(gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS ){
                //此时蓝牙设备连接才算真正连接成功，即具备读写数据的能力
                Log.i(TAG, "writeDescriptor 成功")
            }
        }

        //App修改特征回调，即 App 给设备发送数据结果回调
        override fun onCharacteristicWrite(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS){
                //数据写入成功
                Log.i(TAG, "writeCharacteristic 成功")
            }else{
                //数据写入失败
                Log.i(TAG, "writeCharacteristic 失败")
            }

        }

        //远程设备修改特征描述回调，即设备给 App 发送数据
        override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?
        ) {
            //调用 characteristic?.value 获取远程设备发送过来的数据
        }
    }

    /**
     * 扫描回调
     */
    private val scanCallback =object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            result?.device?.let {
                Log.i(TAG, "扫描到的设备mac = ${it.address}" )
                if (!deviceList.contains(it)) {
                    deviceList.add(it)
                    adapter!!.notifyDataSetChanged()
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.i(TAG, "扫描失败 = $errorCode" )
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)
            Log.i(TAG, "扫描失败 = ${results.toString()}" )
        }
    }


    /**
     * 开始扫描,会不断扫描，返回null
     */
    @SuppressLint("MissingPermission")
    private fun scanBluetooth() {
        /// 设置扫描策略
        val settingBuilder : ScanSettings.Builder =
            ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
                .setReportDelay(3000)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            settingBuilder.setLegacy(true)
//                .setMatchMode(ScanSettings.MATCH_MODE_STICKY)
//                .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
//                .setNumOfMatches(ScanSettings.MATCH_NUM_MAX_ADVERTISEMENT)
//        }
//        val scanSettings = settingBuilder.build()
//        // 设置过滤条件
//        val scanFilterList = arrayListOf<ScanFilter>()
//        val scanFilter = ScanFilter.Builder()
//            .setServiceUuid(ParcelUuid(UUID.fromString("73538150-66e0-4533-8d0d-c18ee70f39f8")))// 替换程自己的UUID
//            .build()
//        scanFilterList.add(scanFilter)
        bluetoothAdapter!!.bluetoothLeScanner.startScan(scanCallback)
    }

    /**
     * 停止扫描
     */
    @SuppressLint("MissingPermission")
    private fun stopScan() {
        bluetoothAdapter!!.bluetoothLeScanner.stopScan(scanCallback)
    }

    private inner class SimpleBluetoothAdapter :
        RecyclerView.Adapter<SimpleBluetoothAdapter.ViewHolder>() {

        @SuppressLint("MissingPermission")
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            init {
                itemView.setOnClickListener {
                    val device = deviceList[adapterPosition]
                    connectGatt(device)
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

        @SuppressLint("MissingPermission")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            (holder.itemView as TextView).text =
                "名字 = " + deviceList[position].name + "地址 = " + deviceList[position].address + "类型 =  " + deviceList[position].type

        }
    }

    @SuppressLint("MissingPermission")
    private fun connectGatt(device: BluetoothDevice) {
        device.connectGatt(mContext,false,gattCallback)
    }

    @SuppressLint("MissingPermission")
    private fun disconnectGatt() {
        /**
         * 断开连接
         */
        bluetoothGatt?.disconnect()
        bluetoothGatt?.close()

    }

    override fun onDestroy() {
        super.onDestroy()
        stopScan()
        disconnectGatt()
    }
}
