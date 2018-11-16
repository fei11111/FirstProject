package com.fei.firstproject.http;

import android.content.Context;

import com.fei.firstproject.entity.BaseEntity;
import com.fei.firstproject.entity.NcwEntity;
import com.fei.firstproject.entity.RecommendEntity;
import com.fei.firstproject.http.factory.RetrofitFactory;
import com.fei.firstproject.http.inter.CallBack;
import com.fei.firstproject.inter.IBase;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

/**
 * 网络请求
 */

public class HttpMgr {

    public static void getRecommendPlan(IBase context, Map<String, String> map, CallBack<List<RecommendEntity>> callBack) {
        Observable<BaseEntity<List<RecommendEntity>>> recommendPlan =
                RetrofitFactory.getBtPlantWeb().getRecommendPlan(map);
        HttpUtils.getInstance().request(context, recommendPlan, true, false, callBack);
    }

    public static void getNcw(IBase context, CallBack<List<NcwEntity>> callBack) {
        Observable<List<NcwEntity>> ncw = RetrofitFactory.getNcw().getNcw();
        HttpUtils.getInstance().request(context, ncw, false, true, callBack);
    }

}
