package com.fei.firstproject.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by Administrator on 2017/7/28.
 */

public class PathUtls {

    private static String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static String appPath = rootPath + File.separator + "fei";
    public static String imgPath = appPath + File.separator + "image";
    public static String logPath = appPath + File.separator + "log";
    public static String cachePath = appPath + File.separator + "cache";
}
