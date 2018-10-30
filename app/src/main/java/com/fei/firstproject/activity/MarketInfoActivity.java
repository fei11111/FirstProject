package com.fei.firstproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fei.firstproject.R;
import com.fei.firstproject.adapter.PriceInfoAdapter;
import com.fei.firstproject.entity.BaseEntity;
import com.fei.firstproject.entity.PriceInfoEntity;
import com.fei.firstproject.http.BaseObserver;
import com.fei.firstproject.http.factory.RetrofitFactory;
import com.fei.firstproject.utils.Utils;
import com.fei.firstproject.widget.AppHeadView;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/1/15.
 */

public class MarketInfoActivity extends BaseActivity {

    private static final int REQUEST_ACTIVITY_CODE_PARAMS = 100;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_variety)
    TextView tvVariety;
    @BindView(R.id.rl_variety)
    RelativeLayout rlVariety;
    @BindView(R.id.tv_gather)
    TextView tvGather;
    @BindView(R.id.rl_gather)
    RelativeLayout rlGather;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.rl_price)
    RelativeLayout rlPrice;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.rl_time)
    RelativeLayout rlTime;
    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.iv_variety)
    ImageView ivVariety;
    @BindView(R.id.iv_gather)
    ImageView ivGather;
    @BindView(R.id.iv_price)
    ImageView ivPrice;
    @BindView(R.id.iv_time)
    ImageView ivTime;

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
    public int getContentViewResId() {
        return R.layout.activity_market_info;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        initListener();
        initRecyclerView();
        initAnimation();
    }

    private void initAnimation() {
        upAnimation = AnimationUtils.loadAnimation(this, R.anim.image_up_animation);
        downAnimation = AnimationUtils.loadAnimation(this, R.anim.image_down_animation);
    }

    private void initRecyclerView() {
        setLinearRecycleViewSetting(recyclerView, this);
    }

    private void initListener() {
        appHeadView.setOnLeftRightClickListener(new AppHeadView.onAppHeadViewListener() {
            @Override
            public void onLeft(View view) {
                onBackPressed();
            }

            @Override
            public void onRight(View view) {

            }

            @Override
            public void onEdit(TextView v, int actionId, KeyEvent event) {

            }
        });

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
        RetrofitFactory.getBtPlantWeb().getPriceInfo(map).
                compose(this.<BaseEntity<List<PriceInfoEntity>>>createTransformer(true))
                .subscribe(new BaseObserver<List<PriceInfoEntity>>(this) {
                    @Override
                    protected void onHandleSuccess(List<PriceInfoEntity> priceInfoEntities) {
                        dismissLoading();
                        refreshLayout.finishRefresh();
                        refreshLayout.finishLoadmore();
                        if (priceInfoEntities != null && priceInfoEntities.size() > 0) {
                            refreshLayout.setVisibility(View.VISIBLE);
                            if (priceInfoAdapter == null) {
                                priceInfoAdapter = new PriceInfoAdapter(MarketInfoActivity.this, priceInfoEntities);
                                recyclerView.setAdapter(priceInfoAdapter);
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
                    protected void onHandleError(String msg) {
                        super.onHandleError(msg);
                        currentPage--;
                        refreshLayout.finishRefresh();
                        refreshLayout.finishLoadmore();
                        showRequestErrorView();
                    }
                });
    }

    @OnClick({R.id.rl_price, R.id.rl_time, R.id.rl_variety, R.id.rl_gather})
    void clickPriceOrTime(View view) {
        switch (view.getId()) {
            case R.id.rl_price:
                if (rlPrice.isSelected()) {
                    ivPrice.startAnimation(downAnimation);
                    rlPrice.setSelected(false);
                } else {
                    ivPrice.startAnimation(upAnimation);
                    rlPrice.setSelected(true);
                }
                break;
            case R.id.rl_time:
                if (rlTime.isSelected()) {
                    ivTime.startAnimation(downAnimation);
                    rlTime.setSelected(false);
                } else {
                    ivTime.startAnimation(upAnimation);
                    rlTime.setSelected(true);
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
}
