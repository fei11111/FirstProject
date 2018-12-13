package com.fei.firstproject.activity;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.amap.api.location.AMapLocation;
import com.fei.firstproject.R;
import com.fei.firstproject.adapter.RetailerInfoAdapter;
import com.fei.firstproject.entity.RetailStoresEntity;
import com.fei.firstproject.http.BaseWithoutBaseEntityObserver;
import com.fei.firstproject.http.factory.RetrofitFactory;
import com.fei.firstproject.utils.LocationUtils;
import com.fei.firstproject.utils.Utils;
import com.fei.firstproject.widget.AppHeadView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2017/9/11.
 */

public class RetailersInfoActivity extends BaseListActivity {

    private String province;
    private String city;
    private String district;
    private RetailerInfoAdapter retailerInfoAdapter;
    private LocationUtils locationUtils;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void permissionsDeniedCallBack(int requestCode) {

    }

    @Override
    public void permissionsGrantCallBack(int requestCode) {

    }

    @Override
    public void permissionDialogDismiss(int requestCode) {

    }

    @Override
    public void initRequest() {
        if (!TextUtils.isEmpty(province) && !TextUtils.isEmpty(city) && !TextUtils.isEmpty(district)) {
            getRetailersInfo();
        }
    }

    @Override
    public void initView() {
        initAppHeadView();
        initAppHeadViewListener();
        initLocation();
    }

    private void initAppHeadViewListener() {
        appHeadView.setOnLeftRightClickListener(new AppHeadView.onAppHeadViewListener() {
            @Override
            public void onLeft(View view) {
                onBackPressed();
            }

            @Override
            public void onRight(View view) {
                currentPage = 1;
                locationUtils.startLocation();
            }

            @Override
            public void onEdit(TextView v, int actionId, KeyEvent event) {
            }
        });
    }

    private void initLocation() {
        locationUtils = LocationUtils.getInstance(getApplicationContext());
        locationUtils.setOnLocationCallBackListener(onLocationCallBackListener);
        locationUtils.startLocation();
    }

    private void initAppHeadView() {
        appHeadView.setFlHeadRightVisible(View.INVISIBLE);
        appHeadView.setMiddleStyle(AppHeadView.TEXT);
        appHeadView.setMiddleText(getResources().getString(R.string.retailers_info));
        appHeadView.setFlHeadRightVisible(View.VISIBLE);
        appHeadView.setRightStyle(AppHeadView.IMAGE);
        appHeadView.setRightDrawable(R.drawable.selector_ic_location);
    }

    private LocationUtils.OnLocationCallBackListener onLocationCallBackListener = new LocationUtils.OnLocationCallBackListener() {
        @Override
        public void onSuccess(AMapLocation aMapLocation) {
            province = aMapLocation.getProvince();//省信息
            city = aMapLocation.getCity();//城市信息
            district = aMapLocation.getDistrict();//城区信息
            getRetailersInfo();
        }

        @Override
        public void onFail() {
            showRequestErrorView();
        }
    };

    private void getRetailersInfo() {
        Map<String, String> map = new HashMap<>();
        map.put("currentPage", currentPage + "");
        map.put("address1", province);//省
        map.put("address2", city);//市
        map.put("address3", district);//区
        Observable<ResponseBody> retailStores = RetrofitFactory.getBtPlantWeb().getRetailStores(map);
        retailStores.compose(this.<ResponseBody>createTransformer(true))
                .subscribe(new BaseWithoutBaseEntityObserver<ResponseBody>(this) {
                    @Override
                    protected void onHandleSuccess(ResponseBody responseBody) {
                        dismissLoading();
                        refreshLayout.finishRefresh();
                        refreshLayout.finishLoadmore();
                        try {
                            String response = responseBody.string();
                            JSONObject json = new JSONObject(response);
                            String infos = json.getString("infos");
                            if (!TextUtils.isEmpty(infos)) {
                                List<RetailStoresEntity> retailStoresEntities = JSON.parseArray(infos, RetailStoresEntity.class);
                                if (retailStoresEntities != null && retailStoresEntities.size() > 0) {
                                    refreshLayout.setVisibility(View.VISIBLE);
                                    if (retailerInfoAdapter == null) {
                                        retailerInfoAdapter = new RetailerInfoAdapter(RetailersInfoActivity.this, retailStoresEntities);
                                        retailerInfoAdapter.setOnToHereListener(new RetailerInfoAdapter.OnToHereListener() {
                                            @Override
                                            public void onClick(String address) {
                                                getLocation(address);
                                            }
                                        });
                                        recyclerView.setAdapter(retailerInfoAdapter);
                                    } else {
                                        if (currentPage == 1) {
                                            retailerInfoAdapter.setRetailStoresEntityList(retailStoresEntities);
                                        } else if (currentPage > 1) {
                                            retailerInfoAdapter.addRetailStoresEntityList(retailStoresEntities);
                                        }
                                        retailerInfoAdapter.notifyDataSetChanged();
                                    }
                                } else {
                                    if (currentPage == 1) {
                                        showNoDataView();
                                    } else if (currentPage > 1) {
                                        currentPage--;
                                        Utils.showToast(RetailersInfoActivity.this, "没有更多数据");
                                    }
                                }
                            } else {
                                showNoDataView();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    protected void onHandleError(String msg) {
                        super.onHandleError(msg);
                        currentPage--;
                        refreshLayout.finishRefresh();
                        refreshLayout.finishLoadmore();
                        showRequestErrorView();
                    }
                });

    }

    private void getLocation(String address) {
        proShow();
        final Observable<ResponseBody> location = RetrofitFactory.getBtPlantWeb().getLocation(address);
        location.compose(this.<ResponseBody>createTransformer(false))
                .subscribe(new BaseWithoutBaseEntityObserver<ResponseBody>(this) {
                    @Override
                    protected void onHandleSuccess(ResponseBody responseBody) {
                        proDisimis();
                        try {
                            String response = responseBody.string();
                            JSONObject result = new JSONObject(response);
                            boolean message = result.getBoolean("message");
                            if (message == true) {
                                String lat = result.getString("lat");
                                String lng = result.getString("lng");
                                String[] location = {lat, lng};
                                if (!TextUtils.isEmpty(lat) && !TextUtils.isEmpty(lng)) {
                                    Utils.toGaodeMap(RetailersInfoActivity.this, location);
                                }
                            } else {
                                Utils.showToast(RetailersInfoActivity.this, "查询不到该地址");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    protected void onHandleError(String msg) {
                        super.onHandleError(msg);
                        proDisimis();
                    }
                });
    }
}
