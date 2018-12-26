package com.fei.firstproject.http;

import com.fei.firstproject.config.AppConfig;
import com.fei.firstproject.entity.BaseEntity;
import com.fei.firstproject.entity.ExpertEntity;
import com.fei.firstproject.entity.MessageEntity;
import com.fei.firstproject.entity.NcwEntity;
import com.fei.firstproject.entity.PriceInfoEntity;
import com.fei.firstproject.entity.RecommendEntity;
import com.fei.firstproject.entity.SelfInfoEntity;
import com.fei.firstproject.entity.ShareEntity;
import com.fei.firstproject.entity.UserEntity;
import com.fei.firstproject.http.factory.RetrofitFactory;
import com.fei.firstproject.http.inter.CallBack;
import com.fei.firstproject.inter.IBase;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

/**
 * 网络请求
 * 备注：isshowErrorView 可在封装一层，不用每次回调失败都自己写showErrorView
 */

public class HttpMgr {

    public static void getRecommendPlan(IBase context, Map<String, String> map, CallBack<List<RecommendEntity>> callBack) {
        Observable<BaseEntity<List<RecommendEntity>>> recommendPlan =
                RetrofitFactory.getBtPlantWeb().getRecommendPlan(map);
        HttpUtils.getInstance().request(context, recommendPlan, true, true, callBack);
    }

    public static void getNcw(IBase context, CallBack<List<NcwEntity>> callBack) {
        Observable<List<NcwEntity>> ncw = RetrofitFactory.getNcw().getNcw();
        HttpUtils.getInstance().request(context, ncw, false, false, callBack);
    }

    public static void addAddress(IBase context, Map<String, String> map, CallBack<ResponseBody> callBack) {
        Observable<ResponseBody> addAddress = RetrofitFactory.getBigDb().addAddress(map);
        HttpUtils.getInstance().request(context, addAddress, false, false, callBack);
    }

    public static void editAddress(IBase context, Map<String, String> map, CallBack<ResponseBody> callBack) {
        Observable<ResponseBody> editAddress = RetrofitFactory.getBigDb().editAddress(map);
        HttpUtils.getInstance().request(context, editAddress, false, false, callBack);
    }

    public static void getHotQuestion(IBase context, Map<String, String> map, CallBack<ResponseBody> callback) {
        Observable<ResponseBody> hotQuestion = RetrofitFactory.getBtWeb().getHotQuestion(map);
        HttpUtils.getInstance().request(context, hotQuestion, false, true, callback);
    }

    public static void getUrgentExpertise(IBase context, CallBack<ResponseBody> callBack) {
        Observable<ResponseBody> urgentExpertEntity = RetrofitFactory.getBtWeb().getUrgentExpertEntity();
        HttpUtils.getInstance().request(context, urgentExpertEntity, false, false, callBack);
    }

    public static void getUnSolveQuestion(IBase context, Map<String, String> map, CallBack<ResponseBody> callBack) {
        Observable<ResponseBody> unSolveQuestion = RetrofitFactory.getBtWeb().getUnSolveQuestion(map);
        HttpUtils.getInstance().request(context, unSolveQuestion, false, false, callBack);
    }

    public static void getSharePlan(IBase context, Map<String, String> map, CallBack<List<ShareEntity>> callBack) {
        Observable<BaseEntity<List<ShareEntity>>> share = RetrofitFactory.getBtPlantWeb().getShare(map);
        HttpUtils.getInstance().request(context, share, true, true, callBack);
    }

    public static void getFieldIndex(IBase context, Map<String, String> map, CallBack<ResponseBody> callBack) {
        Observable<ResponseBody> fieldIndex = RetrofitFactory.getBtPlantWeb().getFieldIndex(map);
        HttpUtils.getInstance().request(context, fieldIndex, false, false, callBack);
    }

    public static void login(IBase context, Map<String, String> map, CallBack<ResponseBody> callBack) {
        Observable<ResponseBody> login = RetrofitFactory.getBigDb().login(map);
        HttpUtils.getInstance().request(context, login, false, false, callBack);
    }

    public static void getUserInfo(IBase context, Map<String, String> map, CallBack<UserEntity> callBack) {
        Observable<UserEntity> userInfo = RetrofitFactory.getBigDb().getUserInfo(map);
        HttpUtils.getInstance().request(context, userInfo, false, false, callBack);
    }

    public static void getPriceInfo(IBase context, Map<String, String> map, CallBack<List<PriceInfoEntity>> callBack) {
        Observable<BaseEntity<List<PriceInfoEntity>>> priceInfo = RetrofitFactory.getBtPlantWeb().getPriceInfo(map);
        HttpUtils.getInstance().request(context, priceInfo, true, true, callBack);
    }

    public static void getMessage(IBase context, Map<String, String> map, CallBack<List<MessageEntity>> callBack) {
        Observable<BaseEntity<List<MessageEntity>>> message = RetrofitFactory.getBtWeb().getMessage(map);
        HttpUtils.getInstance().request(context, message, true, true, callBack);
    }

    public static void getAddress(IBase context, CallBack<ResponseBody> callBack) {
        Observable<ResponseBody> address = RetrofitFactory.getBigDb().getAddress(AppConfig.user.getId());
        HttpUtils.getInstance().request(context, address, false, true, callBack);
    }

    public static void delAddress(IBase context, Map<String, String> map, CallBack<ResponseBody> callBack) {
        Observable<ResponseBody> delAddress = RetrofitFactory.getBigDb().delAddress(map);
        HttpUtils.getInstance().request(context, delAddress, false, false, callBack);
    }

    public static void setDefaultAddress(IBase context, Map<String, String> map, CallBack<ResponseBody> callBack) {
        Observable<ResponseBody> defaultAddress = RetrofitFactory.getBigDb().setDefaultAddress(map);
        HttpUtils.getInstance().request(context, defaultAddress, false, false, callBack);
    }

    public static void getExpert(IBase context, Map<String, String> map, CallBack<List<ExpertEntity>> callBack) {
        Observable<BaseEntity<List<ExpertEntity>>> expert = RetrofitFactory.getBtWeb().getExpert(map);
        HttpUtils.getInstance().request(context, expert, true, true, callBack);
    }

    public static void cancleAttention(IBase context, Map<String, String> map, CallBack<BaseEntity> callBack) {
        Observable<BaseEntity> observable = RetrofitFactory.getBtWeb().cancleAttention(map);
        HttpUtils.getInstance().request(context, observable, false, false, callBack);
    }

    public static void getParams(IBase context, Map<String, String> map, CallBack<List<String>> callBack) {
        Observable<BaseEntity<List<String>>> params = RetrofitFactory.getBtPlantWeb().getParams(map);
        HttpUtils.getInstance().request(context, params, true, true, callBack);
    }

    public static void getProductDetail(IBase context, String matieralId, CallBack<ResponseBody> callBack) {
        Observable<ResponseBody> productDetail = RetrofitFactory.getBtPlantWeb().getProductDetail(matieralId);
        HttpUtils.getInstance().request(context, productDetail, false, true, callBack);
    }

    public static void getProductLib(IBase context, Map<String, String> map, CallBack<ResponseBody> callBack) {
        Observable<ResponseBody> productLib = RetrofitFactory.getBtPlantWeb().getProductLib(map);
        HttpUtils.getInstance().request(context, productLib, false, true, callBack);
    }

    public static void getCondition(IBase context, String url, CallBack<ResponseBody> callBack) {
        Observable<ResponseBody> condition = RetrofitFactory.getBtPlantWeb().getCondition(url);
        HttpUtils.getInstance().request(context, condition, false, false, callBack);
    }

    public static void getRetailersInfo(IBase context, Map<String, String> map, CallBack<ResponseBody> callBack) {
        Observable<ResponseBody> retailStores = RetrofitFactory.getBtPlantWeb().getRetailStores(map);
        HttpUtils.getInstance().request(context, retailStores, false, true, callBack);
    }

    public static void getLocation(IBase context, String address, CallBack<ResponseBody> callBack) {
        Observable<ResponseBody> location = RetrofitFactory.getBtPlantWeb().getLocation(address);
        HttpUtils.getInstance().request(context, location, false, false, callBack);
    }

    public static void getSelfInfo(IBase context, Map<String, String> map, CallBack<SelfInfoEntity> callBack) {
        Observable<BaseEntity<SelfInfoEntity>> selfInfo = RetrofitFactory.getBtWeb().getSelfInfo(map);
        HttpUtils.getInstance().request(context, selfInfo, true, false, callBack);
    }

    public static void logout(IBase context, Map<String, String> map, CallBack<ResponseBody> callBack) {
        Observable<ResponseBody> logout = RetrofitFactory.getBigDb().logout(map);
        HttpUtils.getInstance().request(context, logout, false, false, callBack);
    }

}
