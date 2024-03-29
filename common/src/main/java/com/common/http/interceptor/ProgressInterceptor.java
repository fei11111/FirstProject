package com.common.http.interceptor;

import com.common.http.download.ProgressResponseBody;
import com.common.log.Lg;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by goldze on 2017/5/10.
 */

public class ProgressInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        return originalResponse.newBuilder()
                .body(new ProgressResponseBody(originalResponse.body(),chain.request().tag(String.class)))
                .build();
    }
}
