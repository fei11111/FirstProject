package com.fei.firstproject;

import android.app.Application;

import com.fei.firstproject.bean.UserBean;
import com.fei.firstproject.config.AppConfig;
import com.fei.firstproject.utils.SPUtils;

/**
 * Created by Administrator on 2017/7/28.
 */

public class MyApplication extends Application {

    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        AppConfig.user = (UserBean) SPUtils.get(this, "user", null);
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
