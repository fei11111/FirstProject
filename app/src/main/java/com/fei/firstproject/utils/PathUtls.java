package com.fei.firstproject.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by Administrator on 2017/7/28.
 */

public class PathUtls {

    private static String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static String appPath = rootPath + File.separator + "fei";
    private static String imgPath = appPath + File.separator + "image";
    private static String cachePath = appPath + File.separator + "cache";
    private static String logPath = appPath + File.separator + "log";
    private static String downloadPath = appPath + File.separator + "download";

    public static String getRootPath() {
        return rootPath;
    }

    public static String getAppPath() {
        return appPath;
    }

    public static String getImgPath() {
        File dirFile = new File(imgPath);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        return imgPath;
    }

    public static String getLogPath() {
        File dirFile = new File(logPath);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        return logPath;
    }

    public static String getCachePath() {
        File dirFile = new File(cachePath);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        return cachePath;
    }

    public static String getDownloadPath() {
        File dirFile = new File(downloadPath);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        return downloadPath;
    }
}
