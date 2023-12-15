package com.fei.firstproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.common.viewmodel.EmptyViewModel;
import com.fei.firstproject.R;
import com.fei.firstproject.adapter.PriceInfoAdapter;
import com.fei.firstproject.databinding.ActivityMarketInfoBinding;
import com.fei.firstproject.entity.PriceInfoEntity;
import com.fei.firstproject.http.HttpMgr;
import com.fei.firstproject.http.inter.CallBack;
import com.fei.firstproject.utils.Utils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2018/1/15.
 */

public class MarketInfoActivity extends BaseProjectActivity<EmptyViewModel, ActivityMarketInfoBinding> {

    private static final int REQUEST_ACTIVITY_CODE_PARAMS = 100;

    private int currentPage = 1;
    private PriceInfoAdapter priceInfoAdapter;
    private Animation upAnimation;
    private Animation downAnimation;
    private String province = "";
    private String market = "";
    private String product = "";

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
    public void initTitle() {
        setBackTitle(getString(R.string.maket_info));
    }

    private void initAnimation() {
        upAnimation = AnimationUtils.loadAnimation(this, R.anim.image_up_animation);
        downAnimation = AnimationUtils.loadAnimation(this, R.anim.image_down_animation);
    }

    private void initRecyclerView() {
        setLinearRecycleViewSetting(mChildBinding.recyclerView, this);
    }

    private void initListener() {
        refreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                currentPage++;
                initRequest();
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                currentPage = 1;
                initRequest();
            }
        });
    }

    @Override
    public void initRequest() {
        getPriceInfo();
    }

    private void getPriceInfo() {
        if (currentPage < 1) currentPage = 1;
        Map<String, String> map = new HashMap<>();
        map.put("pageSize", "15");
        map.put("currentPage", currentPage + "");
        map.put("province", province);
        map.put("market", market);
        map.put("product", product);
        map.put("sortFieldType", "0");
        map.put("sortOrderType", "0");
        HttpMgr.getPriceInfo(this, map, new CallBack<List<PriceInfoEntity>>() {
            @Override
            public void onSuccess(List<PriceInfoEntity> priceInfoEntities) {
                dismissLoading();
                refreshLayout.finishRefresh();
                refreshLayout.finishLoadmore();
                if (priceInfoEntities != null && priceInfoEntities.size() > 0) {
                    refreshLayout.setVisibility(View.VISIBLE);
                    if (priceInfoAdapter == null) {
                        priceInfoAdapter = new PriceInfoAdapter(MarketInfoActivity.this, priceInfoEntities);
                        mChildBinding.recyclerView.setAdapter(priceInfoAdapter);
                    } else {
                        if (currentPage == 1) {
                            priceInfoAdapter.setPriceInfoEntities(priceInfoEntities);
                        } else if (currentPage > 1) {
                            priceInfoAdapter.addPriceInfoEntities(priceInfoEntities);
                        }
                        priceInfoAdapter.notifyDataSetChanged();
                    }
                } else {
                    if (currentPage == 1) {
                        showNoDataView();
                    } else if (currentPage > 1) {
                        currentPage--;
                        Utils.showToast(MarketInfoActivity.this, "没有更多数据");
                    }
                }
            }

            @Override
            public void onFail() {
                currentPage--;
                refreshLayout.finishRefresh();
                refreshLayout.finishLoadmore();
                showRequestErrorView();
            }
        });
    }


    void clickPriceOrTime(View view) {
        switch (view.getId()) {
            case R.id.rl_price:
                if (mChildBinding.rlPrice.isSelected()) {
                    mChildBinding.ivPrice.startAnimation(downAnimation);
                    mChildBinding.rlPrice.setSelected(false);
                } else {
                    mChildBinding.ivPrice.startAnimation(upAnimation);
                    mChildBinding.rlPrice.setSelected(true);
                }
                break;
            case R.id.rl_time:
                if (mChildBinding.rlTime.isSelected()) {
                    mChildBinding.ivTime.startAnimation(downAnimation);
                    mChildBinding.rlTime.setSelected(false);
                } else {
                    mChildBinding.ivTime.startAnimation(upAnimation);
                    mChildBinding.rlTime.setSelected(true);
                }
                break;
            case R.id.rl_variety:
                Intent intent = new Intent(this, PriceParamsActivity.class);
                intent.putExtra("type", "1");
                startActivityWithCode(intent, REQUEST_ACTIVITY_CODE_PARAMS);
                break;
            case R.id.rl_gather:
                Intent intent2 = new Intent(this, PriceParamsActivity.class);
                intent2.putExtra("type", "0");
                startActivityWithCode(intent2, REQUEST_ACTIVITY_CODE_PARAMS);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_ACTIVITY_CODE_PARAMS) {
                currentPage = 1;
                String type = data.getStringExtra("type");
                if (!TextUtils.isEmpty(type)) {
                    if (type.equals("1")) {
                        product = data.getStringExtra("product");
                    } else {
                        market = data.getStringExtra("market");
                    }
                }
                initRequest();
            }
        }
    }

    @Override
    public void createObserver() {
        clickPriceOrTime(mChildBinding.rlPrice);
        clickPriceOrTime(mChildBinding.rlTime);
        clickPriceOrTime(mChildBinding.rlVariety);
        clickPriceOrTime(mChildBinding.rlGather);
    }

    @Override
    public void initViewAndData(Bundle savedInstanceState) {
        initListener();
        initRecyclerView();
        initAnimation();
    }
}
