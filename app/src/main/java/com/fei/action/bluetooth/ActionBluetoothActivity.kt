package com.fei.action.bluetooth

import android.content.Intent
import android.os.Bundle
import com.common.base.BaseActivity
import com.common.viewmodel.EmptyViewModel
import com.fei.action.bluetooth.ble.BleBluetoothActivity
import com.fei.action.bluetooth.simple.SimpleBluetoothActivity
import com.fei.firstproject.databinding.ActivityActionBluetoothBinding

/**
 *    author : huangjf
 *    date   : 2024/9/7 14:20
 *    desc   :
 */
class ActionBluetoothActivity: BaseActivity<EmptyViewModel, ActivityActionBluetoothBinding>() {
    override fun createObserver() {
        mBinding.btnBluetooth.setOnClickListener {
            startActivity(Intent(this, SimpleBluetoothActivity::class.java))
        }
        mBinding.btnBle.setOnClickListener {
            startActivity(Intent(this, BleBluetoothActivity::class.java))
        }
    }

    override fun initViewAndData(savedInstanceState: Bundle?) {
    }
}