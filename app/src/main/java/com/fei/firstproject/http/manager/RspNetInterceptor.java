package com.fei.firstproject.http.manager;

import com.fei.firstproject.MyApplication;
import com.fei.firstproject.utils.NetUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/7/28.
 */

public class RspNetInterceptor implements Interceptor {
    private final int maxAge = 60 * 60 * 24 * 7;
    private final int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        // Add Cache Control only for GET methods
        if (request.method().equals("GET")) {
            Response originalResponse = chain.proceed(chain.request());
            if (NetUtils.isConnected(MyApplication.getInstance())) {
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .build();
            } else {

                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
        }

        Response originalResponse = chain.proceed(request);
        return originalResponse;
    }
}
