package com.fei.firstproject.http.inter;

public interface CallBack<T> {

    public void onSuccess(T t);

    public void onFail();

}
