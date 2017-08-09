package com.fei.firstproject.http.inter;

import com.fei.firstproject.bean.UserBean;

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

    @GET("App/login.shtml")
    Call<UserBean> login(@QueryMap Map<String, String> map);
}
