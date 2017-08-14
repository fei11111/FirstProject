package com.fei.firstproject.http.inter;

import com.fei.firstproject.entity.BaseEntity;
import com.fei.firstproject.entity.NcwEntity;
import com.fei.firstproject.entity.RecommendEntity;
import com.fei.firstproject.entity.UserEntity;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by Administrator on 2017/7/27.
 */

public interface RequestApi {

    //本来应该是以Observable<BaseEntity<UserEntity>> 形式返回，因为后台没同意，所以BaseEntity没用

    @GET("versionUpdate/update_info.do")
    Call<ResponseBody> update();

    @GET("App/login.shtml")
    Observable<BaseEntity<UserEntity>> login(@QueryMap Map<String, String> map);

    @GET("api.php?op=content")
    Observable<List<NcwEntity>> getNcw();

    @GET("app/field/getRecommendPlans.do")
    Observable<BaseEntity<List<RecommendEntity>>> getRecommendPlan(@QueryMap Map<String, String> map);
}
