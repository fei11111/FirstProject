package com.fei.firstproject.http.manager;

import android.util.Log;

import com.fei.firstproject.config.AppConfig;
import com.fei.firstproject.http.inter.RequestApi;
import com.fei.firstproject.utils.PathUtls;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2017/7/27.
 */

public class RetrofitManager {

    private static RetrofitManager instance = null;
    private Retrofit retrofit;
    private RequestApi requestApi;
    private String baseUrl = "http://192.168.1.198:8080/bigdb/";

    public RetrofitManager() {
        initRetrofit();
    }

    private void initRetrofit() {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.connectTimeout(15, TimeUnit.SECONDS);
        httpClientBuilder.readTimeout(20, TimeUnit.SECONDS);
        httpClientBuilder.writeTimeout(20, TimeUnit.SECONDS);
        httpClientBuilder.addNetworkInterceptor(new RspNetInterceptor());
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                //打印retrofit日志
                Log.i("RetrofitLog", "retrofitBack = " + message);
            }
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClientBuilder.addInterceptor(loggingInterceptor);
        File cacheFile = new File(PathUtls.cachePath);
        Cache cache = new Cache(cacheFile, AppConfig.CACHE_SIZE);
        httpClientBuilder.cache(cache);
        retrofit = new Retrofit.Builder().client(httpClientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(baseUrl)
                .build();

        requestApi = retrofit.create(RequestApi.class);
    }

    public static RetrofitManager getInstance() {
        if (instance == null) {
            synchronized (RetrofitManager.class) {
                if (instance == null) {
                    instance = new RetrofitManager();
                }
            }
        }
        return instance;
    }

    public RequestApi getRequestApi(){
        return requestApi;
    }

}
