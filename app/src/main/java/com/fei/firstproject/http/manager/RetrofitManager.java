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

    private static String BASE_URL = "http://192.168.1.198:8080/bigdb/";
    private static File cacheFile = new File(PathUtls.cachePath);
    private static Cache cache = new Cache(cacheFile, AppConfig.CACHE_SIZE);

    private static OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .addNetworkInterceptor(new RspNetInterceptor())
            .addInterceptor(new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    //打印retrofit日志
                    Log.i("RetrofitLog", "retrofitBack = " + message);
                }
            }).setLevel(HttpLoggingInterceptor.Level.BODY))
            .cache(cache).build();

    private static RequestApi requestApi = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            // 添加Gson转换器
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(httpClient)
            .build()
            .create(RequestApi.class);

    public static RequestApi getInstance() {
        return requestApi;
    }

}
