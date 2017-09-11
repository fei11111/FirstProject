package com.fei.firstproject.entity;

/**
 * Created by Fei on 2017/9/11.
 */

public class RetailStoresEntity {

    /**
     * "storeName": "赵伟",         零售店名称
     * "address": "4111179624556311", 详细地址
     * "linkman": "",            联系人
     * "storeId": "4168948",    零售店编号
     * "mobile": "18206945546"  联系电话
     */
    private String storeName;
    private String address;
    private String linkman;
    private String storeId;
    private String mobile;

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLinkman() {
        return linkman;
    }

    public void setLinkman(String linkman) {
        this.linkman = linkman;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

}
