package com.fei.firstproject.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.common.viewmodel.EmptyViewModel;
import com.fei.firstproject.R;
import com.fei.firstproject.databinding.ActivityProductDetailBinding;
import com.fei.firstproject.entity.ProductDetailEntity;
import com.fei.firstproject.http.HttpMgr;
import com.fei.firstproject.http.inter.CallBack;
import com.fei.firstproject.utils.Utils;
import com.fei.firstproject.widget.AppHeadView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2017/9/11.
 */

public class ProductDetailActivity extends BaseProjectActivity<EmptyViewModel, ActivityProductDetailBinding> {

    private String matieralId;
    private static final int REQUEST_PERMISSION_CODE_LOCATION = 100;

    @Override
    public void permissionsDeniedCallBack(int requestCode) {
        if (requestCode == REQUEST_PERMISSION_CODE_LOCATION) {
            showMissingPermissionDialog("需要获取定位权限", REQUEST_PERMISSION_CODE_LOCATION);
        }
    }

    @Override
    public void permissionsGrantCallBack(int requestCode) {
        if (requestCode == REQUEST_PERMISSION_CODE_LOCATION) {
            startActivityWithoutCode(new Intent(this, RetailersInfoActivity.class));
        }
    }

    @Override
    public void permissionDialogDismiss(int requestCode) {
        if (requestCode == REQUEST_PERMISSION_CODE_LOCATION) {
            Utils.showToast(this, "获取定位权限失败，无法跳转页面");
        }
    }

    @Override
    public void initTitle() {
        appHeadView.setFlHeadLeftPadding(getResources().getDimensionPixelSize(R.dimen.size_10));
        appHeadView.setLeftStyle(AppHeadView.IMAGE);
        appHeadView.setFlHeadLeftVisible(View.VISIBLE);
        appHeadView.setLeftDrawable(R.drawable.selector_head_left_arrow);
        appHeadView.setMiddleStyle(AppHeadView.TEXT);
        appHeadView.setMiddleText(getString(R.string.product_detail));
        appHeadView.setFlHeadRightVisible(View.VISIBLE);
        appHeadView.setRightStyle(AppHeadView.IMAGE);
        appHeadView.setRightDrawable(R.drawable.selector_ic_location);
    }

    private void initListener() {
        appHeadView.setOnLeftRightClickListener(new AppHeadView.onAppHeadViewListener() {
            @Override
            public void onLeft(View view) {
                onBackPressed();
            }

            @Override
            public void onRight(View view) {
                checkPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSION_CODE_LOCATION);
            }

            @Override
            public void onEdit(TextView v, int actionId, KeyEvent event) {

            }
        });
    }

    private void initData() {
        matieralId = getIntent().getStringExtra("matieralId");
        if (TextUtils.isEmpty(matieralId)) {
            return;
        }
    }

    @Override
    public void initRequest() {
        getProductDetail();
        getProductCase();
    }

    private void getProductCase() {

    }

    private void getProductDetail() {
        HttpMgr.getProductDetail(this, matieralId, new CallBack<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) {
                dismissLoading();
                refreshLayout.finishRefresh();
                try {
                    String response = responseBody.string();
                    if (!TextUtils.isEmpty(response)) {
                        JSONObject json = new JSONObject(response);
                        String infos = json.getString("infos");
                        if (!TextUtils.isEmpty(infos)) {
                            ProductDetailEntity productDetailEntity = JSON.parseObject(infos, ProductDetailEntity.class);
                            String reserve1 = productDetailEntity.getReserve1();
                            if (!TextUtils.isEmpty(reserve1)) {
                                mChildBinding.llProductDetailHead.setVisibility(View.VISIBLE);
                                mChildBinding.tvProductDetailTitle.setText(reserve1);
                            } else {
                                mChildBinding.llProductDetailHead.setVisibility(View.GONE);
                            }
                            String reserve2 = productDetailEntity.getReserve2();
                            if (!TextUtils.isEmpty(reserve2)) {
                                if (reserve2.length() > 10) {
                                    reserve2 = reserve2.substring(0, 10);
                                }
                                mChildBinding.tvProductDetailDate.setText(reserve2);
                            }
                            String content = productDetailEntity.getContent();
                            if (!TextUtils.isEmpty(content)) {
                                refreshLayout.setVisibility(View.VISIBLE);
                                mChildBinding.wvProductDetail.loadDataWithBaseURL(null, productDetailEntity.getContent(), "text/html", "UTF-8", null);
                            } else {
                                showNoDataView();
                            }
                        } else {
                            showNoDataView();
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
            public void onFail() {
                showRequestErrorView();
            }
        });
    }

    @Override
    public void createObserver() {

    }

    @Override
    public void initViewAndData(Bundle savedInstanceState) {
        initData();
        initListener();
    }
}
