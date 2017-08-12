package com.fei.firstproject.entity;

/**
 * Created by Administrator on 2017/8/10.
 */

public class BaseEntity<T> {

    private int resultCode;
    private String resultMessage;
    private T data;

    public boolean isSuccess() {
        return resultCode == 0;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
