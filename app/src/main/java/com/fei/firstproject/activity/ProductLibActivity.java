package com.fei.firstproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.common.viewmodel.EmptyViewModel;
import com.fei.firstproject.R;
import com.fei.firstproject.adapter.ProductLibAdapter;
import com.fei.firstproject.adapter.SingleTextAdapter;
import com.fei.firstproject.databinding.ActivityProductLibBinding;
import com.fei.firstproject.dialog.BottomListDialog;
import com.fei.firstproject.entity.ProductAssitEntity;
import com.fei.firstproject.entity.ProductLibEntity;
import com.fei.firstproject.http.HttpMgr;
import com.fei.firstproject.http.factory.RetrofitFactory;
import com.fei.firstproject.http.inter.CallBack;
import com.fei.firstproject.inter.OnItemClickListener;
import com.fei.firstproject.utils.Utils;
import com.fei.firstproject.widget.AppHeadView;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2017/9/8.
 */

public class ProductLibActivity extends BaseProjectActivity<EmptyViewModel, ActivityProductLibBinding> {

    private int currentPage = 1;
    private BottomListDialog bottomListDialog;
    private ProductLibAdapter productLibAdapter;
    private SingleTextAdapter singleTextAdapter;

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
        appHeadView.setFlHeadLeftPadding(getResources().getDimensionPixelSize(R.dimen.size_10));
        appHeadView.setLeftStyle(AppHeadView.IMAGE);
        appHeadView.setFlHeadLeftVisible(View.VISIBLE);
        appHeadView.setLeftDrawable(R.drawable.selector_head_left_arrow);
        appHeadView.setMiddleStyle(AppHeadView.SEARCH);
        appHeadView.setMiddleSearchHint(getString(R.string.product_lib_search_hint));
        appHeadView.setRightStyle(AppHeadView.TEXT);
        appHeadView.setRightText(getString(R.string.search));
        appHeadView.setFlHeadRightVisible(View.VISIBLE);
    }

    private void initRecyclerView() {
        setLinearRecycleViewSetting(mChildBinding.recyclerView, this);
    }

    private void initListener() {
        appHeadView.setOnLeftRightClickListener(new AppHeadView.onAppHeadViewListener() {
            @Override
            public void onLeft(View view) {
                onBackPressed();
            }

            @Override
            public void onRight(View view) {
                currentPage = 1;
                Utils.hideKeyBoard(ProductLibActivity.this);
                showLoading();
                initRequest();
            }

            @Override
            public void onEdit(TextView v, int actionId, KeyEvent event) {
                currentPage = 1;
                Utils.hideKeyBoard(ProductLibActivity.this);
                showLoading();
                initRequest();
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
        getProductLib();
    }

    public void getProductLib() {
        if (currentPage < 1) currentPage = 1;
        String series = "";
        String technology = "";
        String type = "";
        if (mChildBinding.tvSeries.getTag() != null) {
            series = (String) mChildBinding.tvSeries.getTag();
        }
        if (mChildBinding.tvCraft.getTag() != null) {
            technology = (String) mChildBinding.tvCraft.getTag();
        }
        if (mChildBinding.tvType.getTag() != null) {
            type = (String) mChildBinding.tvType.getTag();
        }
        Map<String, String> map = new HashMap<>();
        map.put("searchWord", appHeadView.getEtSearchText());
        map.put("pageSize", "15");
        map.put("currentPage", currentPage + "");
        map.put("series", series);
        map.put("technology", technology);
        map.put("type", type);
        HttpMgr.getProductLib(this, map, new CallBack<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) {
                dismissLoading();
                refreshLayout.finishRefresh();
                refreshLayout.finishLoadmore();
                try {
                    String string = responseBody.string();
                    JSONObject json = new JSONObject(string);
                    String imgHost = json.getString("imgHost");
                    if (TextUtils.isEmpty(imgHost)) {
                        imgHost = "";
                    }
                    String infos = json.getString("infos");
                    if (TextUtils.isEmpty(infos)) {
                        showNoDataView();
                    } else {
                        final JSONObject info = new JSONObject(infos);
                        String data = info.getString("data");
                        final List<ProductLibEntity> productLibEntities = JSON.parseArray(data, ProductLibEntity.class);
                        if (productLibEntities != null && productLibEntities.size() > 0) {
                            refreshLayout.setVisibility(View.VISIBLE);
                            if (productLibAdapter == null) {
                                productLibAdapter = new ProductLibAdapter(ProductLibActivity.this, productLibEntities, imgHost);
                                productLibAdapter.setOnItemClickListener(new OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View view) {
                                        int position = mChildBinding.recyclerView.getChildAdapterPosition(view);
                                        ProductLibEntity productLibEntity = productLibAdapter.getProductLibEntities().get(position);
                                        Intent intent = new Intent(ProductLibActivity.this, ProductDetailActivity.class);
                                        intent.putExtra("matieralId", productLibEntity.getMatieralId());
                                        startActivityWithoutCode(intent);
                                    }
                                });
                                mChildBinding.recyclerView.setAdapter(productLibAdapter);
                            } else {
                                if (currentPage == 1) {
                                    productLibAdapter.setProductLibEntities(productLibEntities);
                                } else if (currentPage > 1) {
                                    productLibAdapter.addProductLibEntities(productLibEntities);
                                }
                                productLibAdapter.notifyDataSetChanged();
                            }
                        } else {
                            if (currentPage == 1) {
                                showNoDataView();
                            } else if (currentPage > 1) {
                                currentPage--;
                                Utils.showToast(ProductLibActivity.this, "没有更多数据");
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
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

    void clickCondition(View view) {
        String url = "";
        TextView tv = null;
        switch (view.getId()) {
            case R.id.rl_series:
                url = RetrofitFactory.BT_PLANT_WEB_URL + "productKnowledge/app/findSeriesList";
                tv = mChildBinding.tvSeries;
                break;
            case R.id.rl_craft:
                url = RetrofitFactory.BT_PLANT_WEB_URL + "productKnowledge/app/findTechnologyList";
                tv = mChildBinding.tvCraft;
                break;
            case R.id.rl_type:
                url = RetrofitFactory.BT_PLANT_WEB_URL + "productKnowledge/app/findTypeList";
                tv = mChildBinding.tvType;
                break;
        }
        getCondition(tv, url);
    }

    private void getCondition(final TextView tv, String url) {
        proShow();
        HttpMgr.getCondition(this, url, new CallBack<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) {
                proDisimis();
                try {
                    String response = responseBody.string();
                    JSONObject json = new JSONObject(response);
                    if (json.has("infos")) {
                        String infos = json.getString("infos");
                        if (TextUtils.isEmpty(infos)) {
                            showNoDataView();
                            Utils.showToast(ProductLibActivity.this, "没有数据");
                        } else {
                            List<ProductAssitEntity> beans = JSON.parseArray(infos, ProductAssitEntity.class);
                            if (beans != null && beans.size() > 0) {
                                List<String> list = new ArrayList<>();
                                list.add(0, "全部");
                                for (ProductAssitEntity bean : beans
                                ) {
                                    list.add(bean.getBNAME());
                                }
                                showConditionDialog(list, tv);
                            } else {
                                Utils.showToast(ProductLibActivity.this, "没有数据");
                                showNoDataView();
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail() {
                proDisimis();
            }
        });
    }

    private void showConditionDialog(final List<String> name, final TextView tv) {
        String title = "";
        switch (tv.getId()) {
            case R.id.tv_series:
                title = "系列";
                break;
            case R.id.tv_craft:
                title = "工艺";
                break;
            case R.id.tv_type:
                title = "类型";
                break;
        }
        bottomListDialog = new BottomListDialog(this);
        singleTextAdapter = new SingleTextAdapter(this, name);
        bottomListDialog.setAdapter(singleTextAdapter);
        final String str = title;
        bottomListDialog.setOnItemClickListener(new BottomListDialog.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text = name.get(position);
                if (text.equals("全部")) {
                    tv.setText(str);
                    text = "";
                } else {
                    tv.setText(str + "\n" + text);
                }
                tv.setTag(text);
                currentPage = 1;
                getProductLib();
            }
        });
        bottomListDialog.setTitle(title);
        bottomListDialog.show();
//        bottomListDialog.setListViewHeightBasedOnChildren();
    }

    @Override
    public void createObserver() {
        clickCondition(mChildBinding.rlSeries);
        clickCondition(mChildBinding.rlCraft);
        clickCondition(mChildBinding.rlType);
    }

    @Override
    public void initViewAndData(Bundle savedInstanceState) {
        initListener();
        initRecyclerView();
    }
}
