package com.fei.firstproject.entity;

/**
 * @author huangjf
 * @version 创建时间：2016-7-20 上午9:41:16
 * @description
 */
public class UrgentExpertEntity {
	
	/*
	 * {
    "expertise": "科学施肥",
    "plantDesc": "叶菜",
    "serviceTime": "5",
    "imgPath": "",
    "userId": "20956118025395221",
    "userName": "吴小丽",
    "levelStart": 5
    "online"

	 *
	 * */

	private String expertise;
	private String plantDesc;
	private String serviceTime;
	private String imgPath;
	private String userId;
	private String userName;
	private String levelStart;
	private boolean online;
	public String getExpertise() {
		return expertise;
	}
	public void setExpertise(String expertise) {
		this.expertise = expertise;
	}
	public String getPlantDesc() {
		return plantDesc;
	}
	public void setPlantDesc(String plantDesc) {
		this.plantDesc = plantDesc;
	}
	public String getServiceTime() {
		return serviceTime;
	}
	public void setServiceTime(String serviceTime) {
		this.serviceTime = serviceTime;
	}
	public String getImgPath() {
		return imgPath;
	}
	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public boolean isOnline() {
		return online;
	}

	public String getLevelStart() {
		return levelStart;
	}

	public void setLevelStart(String levelStart) {
		this.levelStart = levelStart;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}
	@Override
	public String toString() {
		return "UrgentExpertBean [expertise=" + expertise + ", plantDesc="
				+ plantDesc + ", serviceTime=" + serviceTime + ", imgPath="
				+ imgPath + ", userId=" + userId + ", userName=" + userName
				+ ", levelStart=" + levelStart + ", online=" + online + "]";
	}
	
}
