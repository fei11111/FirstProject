package com.fei.firstproject.http.inter;

import com.fei.firstproject.entity.BaseEntity;
import com.fei.firstproject.entity.MessageEntity;
import com.fei.firstproject.entity.NcwEntity;
import com.fei.firstproject.entity.OrderEntity;
import com.fei.firstproject.entity.RecommendEntity;
import com.fei.firstproject.entity.UserEntity;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * Created by Administrator on 2017/7/27.
 */

public interface RequestApi {

    //本来应该是以Observable<BaseEntity<UserEntity>> 形式返回，因为后台没同意，所以BaseEntity没用

    @GET("versionUpdate/update_info.do")
    Call<ResponseBody> update();

    @GET("App/login.shtml")
    Observable<ResponseBody> login(@QueryMap Map<String, String> map);

    @GET("api.php?op=content")
    Observable<List<NcwEntity>> getNcw();

    @GET("app/field/getRecommendPlansMore.do")
    Observable<BaseEntity<List<RecommendEntity>>> getRecommendPlan(@QueryMap Map<String, String> map);

    @POST("app/notice/listNotices.do")
    Observable<BaseEntity<List<MessageEntity>>> getMessage(@QueryMap Map<String, String> map);

    @GET("App/getUserInFoByToken.shtml")
    Observable<UserEntity> getUserInfo(@QueryMap Map<String, String> map);

    @GET("ordermanage/api/list_dzorderhead_payment.do")
    Observable<OrderEntity> getOrder(@QueryMap Map<String, String> map);

}
