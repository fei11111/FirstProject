package com.fei.firstproject.http.manager;

import com.fei.firstproject.bean.UserBean;
import com.fei.firstproject.config.AppConfig;
import com.fei.firstproject.http.inter.RequestApi;
import com.fei.firstproject.utils.PathUtls;

import java.io.File;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
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

    public void login(Map<String, String> map, Observer<UserBean> observer) {

    }
}
