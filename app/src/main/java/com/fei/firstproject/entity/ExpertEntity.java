package com.fei.firstproject.entity;

/**
 * Created by Administrator on 2017/8/29.
 */

public class ExpertEntity {

    private String imgPath;//头像地址
    private String plantNames;//专长作物
    private String serviceTime;//服务时间
    private int level_start;//星级
    private String expertise;//技术专长
    private String expertName;//专家名字
    private String expertId ;//专家Id
    private boolean online;

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getPlantNames() {
        return plantNames;
    }

    public void setPlantNames(String plantNames) {
        this.plantNames = plantNames;
    }

    public String getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(String serviceTime) {
        this.serviceTime = serviceTime;
    }

    public int getLevel_start() {
        return level_start;
    }

    public void setLevel_start(int level_start) {
        this.level_start = level_start;
    }

    public String getExpertise() {
        return expertise;
    }

    public void setExpertise(String expertise) {
        this.expertise = expertise;
    }

    public String getExpertName() {
        return expertName;
    }

    public void setExpertName(String expertName) {
        this.expertName = expertName;
    }

    public String getExpertId() {
        return expertId;
    }

    public void setExpertId(String expertId) {
        this.expertId = expertId;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}
