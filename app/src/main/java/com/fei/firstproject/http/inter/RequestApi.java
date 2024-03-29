package com.fei.firstproject.http.inter;

import com.fei.firstproject.entity.BaseEntity;
import com.fei.firstproject.entity.ChangeRoleEntity;
import com.fei.firstproject.entity.ExpertEntity;
import com.fei.firstproject.entity.MessageEntity;
import com.fei.firstproject.entity.NcwEntity;
import com.fei.firstproject.entity.OrderEntity;
import com.fei.firstproject.entity.PriceInfoEntity;
import com.fei.firstproject.entity.ProductCaseEntity;
import com.fei.firstproject.entity.RecommendEntity;
import com.fei.firstproject.entity.SelfInfoEntity;
import com.fei.firstproject.entity.ShareEntity;
import com.fei.firstproject.entity.UserEntity;

import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;


/**
 * Created by Administrator on 2017/7/27.
 */

public interface RequestApi {

    //本来应该是以Observable<BaseEntity<UserEntity>> 形式返回，因为后台没统一，所以BaseEntity没用

    @GET("versionUpdate/update_info.do")
    Call<ResponseBody> update();

    @GET("App/login.shtml")
    Observable<ResponseBody> login(@QueryMap Map<String, String> map);

    @GET("api.php?op=content")
    Observable<List<NcwEntity>> getNcw();

    @GET("app/notice/listNotices.do")
    Observable<BaseEntity<List<MessageEntity>>> getMessage(@QueryMap Map<String, String> map);

    @FormUrlEncoded
    @POST("app/field/getRecommendPlansMore.do")
    Observable<BaseEntity<List<RecommendEntity>>> getRecommendPlan(@FieldMap Map<String, String> map);

    @GET("App/getUserInFoByToken.shtml")
    Observable<UserEntity> getUserInfo(@QueryMap Map<String, String> map);

    @GET("ordermanage/api/list_dzorderhead_payment.do")
    Observable<OrderEntity> getOrder(@QueryMap Map<String, String> map);

    @GET("app/clinic/myAttention/getAttentionExperts.do")
    Observable<BaseEntity<List<ExpertEntity>>> getExpert(@QueryMap Map<String, String> map);

    @GET("app/clinic/myAttention/delAttention.do")
    Observable<BaseEntity> cancleAttention(@QueryMap Map<String, String> map);

    @GET("App/appLogout.do")
    Observable<ResponseBody> logout(@QueryMap Map<String, String> map);

    @GET("app/myAccount/getExpertByUserId")
    Observable<BaseEntity<SelfInfoEntity>> getSelfInfo(@QueryMap Map<String, String> map);

    @GET("/bt-web/app/getRole")
    Observable<List<ChangeRoleEntity>> changeRole(@Query("userId") String userId);

    @FormUrlEncoded
    @POST("App/addReceiptAddress.do")
    Observable<ResponseBody> addAddress(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("App/updReceiptAddress.do")
    Observable<ResponseBody> editAddress(@FieldMap Map<String, String> map);

    @GET("App/getReceiptAddress.do")
    Observable<ResponseBody> getAddress(@Query("userId") String userId);

    @GET("App/setDefaultReceiptAddress.do")
    Observable<ResponseBody> setDefaultAddress(@QueryMap Map<String, String> map);

    @GET("App/delReceiptAddress.do")
    Observable<ResponseBody> delAddress(@QueryMap Map<String, String> map);

    @FormUrlEncoded
    @POST("productKnowledge/app/productKnowledgeList")
    Observable<ResponseBody> getProductLib(@FieldMap Map<String, String> map);

    @GET
    Observable<ResponseBody> getCondition(@Url String url);

    @GET("productKnowledge/app/findProductById")
    Observable<ResponseBody> getProductDetail(@Query("matieralId") String matieralId);

    @GET("plantApFrom/app/queryCheckModelLand")
    Observable<BaseEntity<List<ProductCaseEntity>>> getProductCase(@QueryMap Map<String, String> map);

    @FormUrlEncoded
    @POST("productKnowledge/app/getRetailstoresByAddress")
    Observable<ResponseBody> getRetailStores(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("productKnowledge/app/getLatAndLngByAddress")
    Observable<ResponseBody> getLocation(@Field("address") String address);

    @GET("app/getUrgentExpert.do")
    Observable<ResponseBody> getUrgentExpertEntity();

    @FormUrlEncoded
    @POST("app/getHotQuestion.do")
    Observable<ResponseBody> getHotQuestion(@FieldMap Map<String, String> map);

    @GET("app/selUnresolvedQue.do")
    Observable<ResponseBody> getUnSolveQuestion(@QueryMap Map<String, String> map);

    @GET("app/field/searchRecordShares.do")
    Observable<BaseEntity<List<ShareEntity>>> getShare(@QueryMap Map<String, String> map);

    @GET("app/field/fieldIndex.do")
    Observable<ResponseBody> getFieldIndex(@QueryMap Map<String, String> map);

    @GET
    Observable<ResponseBody> downloadFile(@Url String url);

    @FormUrlEncoded
    @POST("app/information/getPriceInfo")
    Observable<BaseEntity<List<PriceInfoEntity>>> getPriceInfo(@FieldMap Map<String, String> map);

    @GET("app/information/getPriceParams")
    Observable<BaseEntity<List<String>>> getParams(@QueryMap Map<String, String> map);

}
