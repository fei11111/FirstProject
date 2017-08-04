package com.fei.firstproject.inter;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Administrator on 2017/7/27.
 */

public interface RequestApi {

    @GET("versionUpdate/update_info.do")
    Call<ResponseBody> update();
}
