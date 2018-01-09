package com.fei.firstproject.entity;

import android.support.v4.app.NotificationCompat;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/1/5.
 */

public class DownLoadEntity implements Serializable {

    private String name;
    private String downloadUrl;
    private boolean isInstall;//是否要安装
    private int flag;//下载标志
    private String savePath;//保存文件的路径
    private NotificationCompat.Builder builder;//为了刷新界面
    private long totalLength;//下载总长度
    private long progress;//下载进度
    private boolean isDone;//下载完成

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
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

    public long getTotalLength() {
        return totalLength;
    }

    public void setTotalLength(long totalLength) {
        this.totalLength = totalLength;
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}
