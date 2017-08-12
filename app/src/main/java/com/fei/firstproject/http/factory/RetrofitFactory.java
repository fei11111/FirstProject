package com.fei.firstproject.http.factory;

import android.util.Log;

import com.fei.firstproject.config.AppConfig;
import com.fei.firstproject.http.inter.RequestApi;
import com.fei.firstproject.http.manager.RspNetInterceptor;
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

public class RetrofitFactory {


    public static final String BIGDB_URL = AppConfig.HOST + "/bigdb/";
    public static final String NCW_URL = "http://batian.ncw365.com/";
    public static final String BT_WEB_URL = AppConfig.HOST2 + "/bt-web/app/";
    public static final String BT_PLANT_WEB_URL = AppConfig.HOST3 + "/bt-plant-web/";
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

    private static Retrofit.Builder builder = new Retrofit.Builder()
            // 添加Gson转换器
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(httpClient);

    private static RequestApi requestBDApi = builder.
            baseUrl(BIGDB_URL).
            build().
            create(RequestApi.class);

    private static RequestApi requestNcwApi = builder
            .baseUrl(NCW_URL)
            .build()
            .create(RequestApi.class);

    private static RequestApi requestBtWebApi = builder
            .baseUrl(BT_WEB_URL)
            .build()
            .create(RequestApi.class);

    private static RequestApi requestBtPlantWebApi = builder
            .baseUrl(BT_PLANT_WEB_URL)
            .build()
            .create(RequestApi.class);

    public static RequestApi getBigDb() {
        return requestBDApi;
    }

    public static RequestApi getNcw() {
        return requestNcwApi;
    }

    public static RequestApi getBtWeb() {
        return requestBtWebApi;
    }

    public static RequestApi getBtPlantWeb() {
        return requestBtPlantWebApi;
    }

}
