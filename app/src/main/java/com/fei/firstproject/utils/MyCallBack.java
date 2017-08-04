package com.fei.firstproject.utils;

import android.util.Log;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/7/28.
 */

public class MyCallBack<T> implements Callback<T> {

    private static final String TAG = "网络请求返回";

    @Override
    public void onResponse(Call<T> call, Response<T> response) {

    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        if (t instanceof SocketTimeoutException) {
            //服务器响应超时
            Log.i(TAG, "服务器响应超时");
        } else if (t instanceof ConnectException) {
            //服务器请求超时
        } else if (t instanceof RuntimeException) {
            //
        }
    }
}
