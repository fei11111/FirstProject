package com.fei.firstproject.utils;

import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.AMapException;

/**
 * Created by Administrator on 2017/9/12.
 */

public class LocationUtils {

    private static LocationUtils instance;
    private Context mContext;
    private AMapLocationClient mapLocationClient;
    private AMapLocationClientOption mapLocationClientOption;
    private OnLocationCallBackListener onLocationCallBackListener;

    public LocationUtils(Context mContext) {
        this.mContext = mContext;
        initLocationOption();
        initLocationClient();
    }

    public void setOnLocationCallBackListener(OnLocationCallBackListener onLocationCallBackListener) {
        this.onLocationCallBackListener = onLocationCallBackListener;
    }

    public static LocationUtils getInstance(Context mContext) {
        if (instance == null) {
            instance = new LocationUtils(mContext);
        }
        return instance;
    }

    private void initLocationClient() {
        try {
            mapLocationClient = new AMapLocationClient(mContext);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        mapLocationClient.setLocationOption(mapLocationClientOption);
        mapLocationClient.setLocationListener(aMapLocationListener);
    }

    private void initLocationOption() {
        mapLocationClientOption = new AMapLocationClientOption();
        mapLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mapLocationClientOption.setInterval(3000);
        mapLocationClientOption.setNeedAddress(true);
        mapLocationClientOption.setHttpTimeOut(10000);
        mapLocationClientOption.setLocationCacheEnable(true);
    }

    public void startLocation() {
        mapLocationClient.startLocation();
    }

    private AMapLocationListener aMapLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    stopLocation();
                    if (onLocationCallBackListener != null) {
                        try {
                            onLocationCallBackListener.onSuccess(aMapLocation);
                        } catch (AMapException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } else {
                    if (onLocationCallBackListener != null) {
                        onLocationCallBackListener.onFail();
                    }
                }
            } else {
                if (onLocationCallBackListener != null) {
                    onLocationCallBackListener.onFail();
                }
            }
        }
    };

    public void stopLocation() {
        if (mapLocationClient != null) {
            mapLocationClient.stopLocation();
        }
    }

    public void destoryLocation() {
        if (mapLocationClient != null) {
            mapLocationClient.onDestroy();
        }
    }

    public interface OnLocationCallBackListener {
        void onSuccess(AMapLocation aMapLocation) throws AMapException;

        void onFail();
    }
}
