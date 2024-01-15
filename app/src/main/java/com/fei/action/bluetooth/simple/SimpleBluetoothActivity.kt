package com.fei.action.bluetooth.simple

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.common.base.BaseActivity
import com.common.viewmodel.EmptyViewModel
import com.fei.firstproject.databinding.ActivitySimpleBlueToothBinding

class SimpleBluetoothActivity : BaseActivity<EmptyViewModel, ActivitySimpleBlueToothBinding>() {

    private val REQUEST_ENABLE_BT = 0x11
    private var bluetoothManager: BluetoothManager? = null
    private var bluetoothAdapter: BluetoothAdapter? = null

    override fun createObserver() {
    }

    override fun initViewAndData(savedInstanceState: Bundle?) {
        if (isSupportBluetooth() && isOpenBluetooth()) {
            scanBluetooth()
        }
    }

    private fun scanBluetooth() {

    }

    /**
     * 是否支持蓝牙
     */
    private fun isSupportBluetooth(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            bluetoothManager = getSystemService(BluetoothManager::class.java)
            bluetoothAdapter = bluetoothManager!!.adapter ?: return false
        }
        return false
    }

    /**
     * 蓝牙是否打开
     */
    private fun isOpenBluetooth(): Boolean {
        if (bluetoothAdapter!!.isEnabled) {
            return true
        } else {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                return false
            }
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
}