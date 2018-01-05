package com.fei.firstproject.entity;

import android.support.v4.app.NotificationCompat;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/1/5.
 */

public class DownLoadEntity implements Serializable {

    private String name;
    private String downloadUrl;
    private String imgUrl;
    private boolean isInstall;//是否要安装
    private int flag;//下载标志
    private String savePath;
    private NotificationCompat.Builder builder;//为了刷新界面

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public boolean isInstall() {
        return isInstall;
    }

    public void setInstall(boolean install) {
        isInstall = install;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public NotificationCompat.Builder getBuilder() {
        return builder;
    }

    public void setBuilder(NotificationCompat.Builder builder) {
        this.builder = builder;
    }
}
