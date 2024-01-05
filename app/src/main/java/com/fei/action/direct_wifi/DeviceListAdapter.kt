package com.fei.action.direct_wifi

import android.content.Context
import android.net.wifi.p2p.WifiP2pDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fei.firstproject.R

class DeviceListAdapter(private val context: Context, private var list: List<WifiP2pDevice>) :
    RecyclerView.Adapter<DeviceListAdapter.DeviceListHolder>() {


    class DeviceListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName = itemView.findViewById<TextView>(R.id.tv_name);
        private val tvAddress = itemView.findViewById<TextView>(R.id.tv_address)

        fun bind(wifiP2pDevice: WifiP2pDevice) {
            tvName.text = wifiP2pDevice.deviceName
            tvAddress.text = wifiP2pDevice.deviceAddress
        }
    }

    fun setList(list: List<WifiP2pDevice>) {
        this.list = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceListHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.activity_direct_wifi, parent, false)
        return DeviceListHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: DeviceListHolder, position: Int) {
        holder.bind(list[position])
    }
}