package com.fei.firstproject.config;


import com.fei.firstproject.entity.UserEntity;

/**
 * Created by Administrator on 2017/7/28.
 */

public class AppConfig {

    public static boolean ISLOGIN = false;
    public static boolean DEBUG = true;
    public static int CACHE_SIZE = 2 * 1024 * 1024;
    public static UserEntity user;

    // 正式环境
    public static final String HOST = "http://218.18.114.100:8880";
    public static final String HOST2 = "http://218.18.114.97:3392";
    public static final String HOST3 = "http://218.18.114.97:3391";

    // 测试IP
//    public static final String HOST = "http://192.168.1.198:8080";
//    public static final String HOST2 = "http://192.168.1.195:3389";
//    public static final String HOST3 = "http://192.168.1.214:3391";

    // 田间管理-方案详情
    public static final String PLANT_DESC_URL = HOST3
            + "/bt-plant-web/brandsPlant/app/brandsPlanPage";
    // 简易定制
    public static final String SIMPLE_CUSTOM_URL = HOST + "/"
            + "bigdb/plantCrop/api/plant_crop_list.do";
}
