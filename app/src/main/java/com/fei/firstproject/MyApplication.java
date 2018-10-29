package com.fei.firstproject;

import android.app.Application;

import com.fei.firstproject.config.AppConfig;
import com.fei.firstproject.entity.UserEntity;
import com.fei.firstproject.utils.SPUtils;
import com.umeng.commonsdk.UMConfigure;

/**
 * Created by Administrator on 2017/7/28.
 */

public class MyApplication extends Application {

    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        init();
    }

    private void init() {
        initUserInfo();
        initCrashHandler();
        initUm();
    }

    /**
     * 初始化友盟
     * */
    private void initUm() {
        UMConfigure.init(this, "5bd41efdf1f556aad800019f", "FirstProject", UMConfigure.DEVICE_TYPE_PHONE, null);
    }

    private void initCrashHandler() {
        CrashHandler.getInstance().init();
    }

    private void initUserInfo() {
        AppConfig.user = (UserEntity) SPUtils.get(this, "user", null);
        AppConfig.notificationId = (int) SPUtils.get(this, "notificationId", 10);
        if (AppConfig.user == null) {
            AppConfig.ISLOGIN = false;
        } else {
            AppConfig.ISLOGIN = true;
        }
    }

    public static MyApplication getInstance() {
        return instance;
    }

}
