package com.fei.firstproject.activity;

import android.content.Intent;
import android.view.View;

import com.fei.firstproject.R;
import com.fei.firstproject.adapter.RecommendPlanAdapter;
import com.fei.firstproject.config.AppConfig;
import com.fei.firstproject.entity.BaseEntity;
import com.fei.firstproject.entity.RecommendEntity;
import com.fei.firstproject.http.BaseObserver;
import com.fei.firstproject.http.factory.RetrofitFactory;
import com.fei.firstproject.inter.OnItemClickListener;
import com.fei.firstproject.utils.Utils;
import com.fei.firstproject.widget.AppHeadView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

/**
 * Created by Fei on 2017/8/23.
 */

public class RecommendPlanActivity extends BaseListActivity {

    private RecommendPlanAdapter recommendPlanAdapter;

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
        getRecommendPlan();
    }

    @Override
    public void initView() {
        appHeadView.setRightStyle(AppHeadView.TEXT);
        appHeadView.setRightText(getResources().getString(R.string.search));
        appHeadView.setMiddleStyle(AppHeadView.SEARCH);
        appHeadView.setMiddleSearchHint(getString(R.string.recommend_tip));
    }

    public void getRecommendPlan() {
        if (currentPage < 1) currentPage = 1;
        String searchWord = appHeadView.getEtSearchText();
        Map<String, String> map = new HashMap<>();
        map.put("searchWord", searchWord);
        map.put("pageSize", "20");
        map.put("currentPage", currentPage + "");
        Observable<BaseEntity<List<RecommendEntity>>> recommendPlan =
                RetrofitFactory.getBtPlantWeb().getRecommendPlan(map);
        recommendPlan.compose(this.<BaseEntity<List<RecommendEntity>>>createTransformer(true))
                .subscribe(new BaseObserver<List<RecommendEntity>>(this) {
                    @Override
                    protected void onHandleSuccess(final List<RecommendEntity> recommendEntities) {
                        dismissLoading();
                        refreshLayout.finishRefresh();
                        refreshLayout.finishLoadmore();
                        if (recommendEntities != null && recommendEntities.size() > 0) {
                            refreshLayout.setVisibility(View.VISIBLE);
                            if (recommendPlanAdapter == null) {
                                recommendPlanAdapter = new RecommendPlanAdapter(RecommendPlanActivity.this, recommendEntities);
                                recyclerView.setAdapter(recommendPlanAdapter);
                                recommendPlanAdapter.setOnItemClickListener(new OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View view) {
                                        int position = recyclerView.getChildAdapterPosition(view);
                                        RecommendEntity recommendEntity = recommendPlanAdapter.getRecommendEntities().get(position);
                                        String url = AppConfig.PLANT_DESC_URL + "?id=" + recommendEntity.getId() + "&version="
                                                + recommendEntity.getVersion()
                                                + "&cropCode=" + recommendEntity.getCropCode()
                                                + "&categoryCode=" + recommendEntity.getCropCategoryCode()
                                                + "&title=" + recommendEntity.getTitle();
                                        Intent intent = new Intent(RecommendPlanActivity.this, WebActivity.class);
                                        intent.putExtra("url", url);
                                        startActivityWithoutCode(intent);
                                    }
                                });
                            } else {
                                if (currentPage == 1) {
                                    recommendPlanAdapter.setRecommendEntities(recommendEntities);
                                } else if (currentPage > 1) {
                                    recommendPlanAdapter.addRecommendEntities(recommendEntities);
                                }
                                recommendPlanAdapter.notifyDataSetChanged();
                            }
                        } else {
                            if (currentPage == 1) {
                                showNoDataView();
                            } else if (currentPage > 1) {
                                currentPage--;
                                Utils.showToast(RecommendPlanActivity.this, "没有更多数据");
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
}
