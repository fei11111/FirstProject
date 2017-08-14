package com.fei.firstproject.entity;

/**
 * Created by Administrator on 2017/8/10.
 */

public class BaseEntity<T> {

    private boolean state;
    private String message;
    private T data;

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
