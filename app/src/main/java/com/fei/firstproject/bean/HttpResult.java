package com.fei.firstproject.bean;

/**
 * Created by Administrator on 2017/8/10.
 */

public class HttpResult<T> {
    private int resultCode;
    private String resultMessage;

    private T data;
}
