package com.fei.firstproject.http.exceptiion;

/**
 * Created by Administrator on 2017/9/22.
 */

public class ApiException extends Exception {

    public int code;
    public String message;

    public ApiException(Throwable throwable, int code) {
        super(throwable);
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
