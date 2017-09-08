package com.fei.firstproject.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/9/6.
 */

public class AddressEntity implements Serializable{

    private String receiptUserName;
    private String receiptTel;
    private String receiptAddr;
    private String defaultFlagId; //默认地址Y/N
    private String receiptAddrId; //地址ID

    public String getReceiptUserName() {
        return receiptUserName;
    }

    public void setReceiptUserName(String receiptUserName) {
        this.receiptUserName = receiptUserName;
    }

    public String getReceiptTel() {
        return receiptTel;
    }

    public void setReceiptTel(String receiptTel) {
        this.receiptTel = receiptTel;
    }

    public String getReceiptAddr() {
        return receiptAddr;
    }

    public void setReceiptAddr(String receiptAddr) {
        this.receiptAddr = receiptAddr;
    }

    public String getDefaultFlagId() {
        return defaultFlagId;
    }

    public void setDefaultFlagId(String defaultFlagId) {
        this.defaultFlagId = defaultFlagId;
    }

    public String getReceiptAddrId() {
        return receiptAddrId;
    }

    public void setReceiptAddrId(String receiptAddrId) {
        this.receiptAddrId = receiptAddrId;
    }
}
