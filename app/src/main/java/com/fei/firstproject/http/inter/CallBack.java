package com.fei.firstproject.http.inter;

public interface CallBack<T> {

    void onSuccess(T t);

    void onFail();

}
