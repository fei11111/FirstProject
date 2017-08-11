package com.fei.firstproject.http.inter;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by Administrator on 2017/7/27.
 */

public interface RequestApi {

    @GET("versionUpdate/update_info.do")
    Call<ResponseBody> update();

    //http://192.168.1.198:8080/bigdb/App/login.shtml
    @GET("App/login.shtml")
    Call<ResponseBody> login(@QueryMap Map<String, String> map);

//    @GET("App/getUserInFoByToken.shtml")
//    Observable<UserBean> getUserInfo()
}
