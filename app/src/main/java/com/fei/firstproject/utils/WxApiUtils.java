package com.fei.firstproject.utils;

import android.content.Context;

import com.fei.firstproject.config.AppConfig;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * Created by Administrator on 2017/9/13.
 */

public class WxApiUtils {

    private static WxApiUtils instance;
    private IWXAPI iwxapi;

    public WxApiUtils(Context mContext) {
        iwxapi = WXAPIFactory.createWXAPI(mContext, AppConfig.APP_ID, true);
        iwxapi.registerApp(AppConfig.APP_ID);
    }

    public static WxApiUtils getInstance(Context mContext) {
        if (instance == null) {
            instance = new WxApiUtils(mContext);
        }
        return instance;
    }

    public IWXAPI getIwxapi() {
        return iwxapi;
    }
}
