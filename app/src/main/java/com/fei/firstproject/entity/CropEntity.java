package com.fei.firstproject.entity;

import java.io.Serializable;

/**
 * @author huangjf
 * @version 创建时间：2016-4-18 下午2:19:02
 * @description
 */
public class CropEntity implements Serializable {


    /**
     * plandRegion : 粤兴二道10
     * county : 南山区
     * cropId : 50
     * userId : 35fef0aaec0848bb94a1aa92c4df4b99
     * apFormId : 661bc6cf15614f53a2efe9fe4feceaaf
     * province : 广东省
     * cropName : 番茄
     * landName : 地块-56
     * noticeCount : 0
     * landId : fc50bd01353e44b4808b56fb9b7bd211
     * inTemple : 1
     * cropCode : 20020
     * city : 深圳市
     * provinceId
     * imgPath 图片地址
     * createDate 创建时间
     */

    private String provinceId;
    private String plandRegion;
    private String county;
    private int cropId;
    private String userId;
    private String apFormId;
    private String province;
    private String cropName;
    private String landName;
    private int noticeCount;
    private String landId;
    private int inTemple;
    private String cropCode;
    private String city;
    private String jpgUrl;
    private String id;
    private String imgPath;
    private String createDate;
    private String childid;

    public String getChildid() {
        return childid;
    }

    public void setChildid(String childid) {
        this.childid = childid;
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJpgUrl() {
        return jpgUrl;
    }

    public void setJpgUrl(String jpgUrl) {
        this.jpgUrl = jpgUrl;
    }

    public String getPlandRegion() {
        return plandRegion;
    }

    public void setPlandRegion(String plandRegion) {
        this.plandRegion = plandRegion;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public int getCropId() {
        return cropId;
    }

    public void setCropId(int cropId) {
        this.cropId = cropId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getApFormId() {
        return apFormId;
    }

    public void setApFormId(String apFormId) {
        this.apFormId = apFormId;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCropName() {
        return cropName;
    }

    public void setCropName(String cropName) {
        this.cropName = cropName;
    }

    public String getLandName() {
        return landName;
    }

    public void setLandName(String landName) {
        this.landName = landName;
    }

    public int getNoticeCount() {
        return noticeCount;
    }

    public void setNoticeCount(int noticeCount) {
        this.noticeCount = noticeCount;
    }

    public String getLandId() {
        return landId;
    }

    public void setLandId(String landId) {
        this.landId = landId;
    }

    public int getInTemple() {
        return inTemple;
    }

    public void setInTemple(int inTemple) {
        this.inTemple = inTemple;
    }

    public String getCropCode() {
        return cropCode;
    }

    public void setCropCode(String cropCode) {
        this.cropCode = cropCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
