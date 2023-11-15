package com.common.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;

import androidx.annotation.RequiresApi;


public class NetworkReceiver extends BroadcastReceiver {

    private NetworkStateChangeListener onNetworkChangeListener;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            if (onNetworkChangeListener == null) {
                return;
            }
            int netType = NetStateUtils.getAPNType(context);
            onNetworkChangeListener.networkConnectChange(netType != 0);
            onNetworkChangeListener.networkTypeChange(netType);
        }
    }

    public void setOnNetworkChangeListener(NetworkStateChangeListener onNetworkChangeListener) {
        this.onNetworkChangeListener = onNetworkChangeListener;
    }

    public void removeListener() {
        if (this.onNetworkChangeListener != null) {
            this.onNetworkChangeListener = null;
        }
    }
}
