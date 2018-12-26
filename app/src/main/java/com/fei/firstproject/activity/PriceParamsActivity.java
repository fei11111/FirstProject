package com.fei.firstproject.activity;

import android.content.Intent;
import android.view.View;

import com.fei.firstproject.R;
import com.fei.firstproject.adapter.SingleCheckAdapter;
import com.fei.firstproject.http.HttpMgr;
import com.fei.firstproject.http.inter.CallBack;
import com.fei.firstproject.utils.Utils;
import com.fei.firstproject.widget.AppHeadView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/1/15.
 */

public class PriceParamsActivity extends BaseListActivity {

    private SingleCheckAdapter singleCheckAdapter;
    private String type;

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
        getParams();
    }

    @Override
    public void initView() {
        type = getIntent().getStringExtra("type");
        appHeadView.setRightStyle(AppHeadView.TEXT);
        appHeadView.setRightText(getResources().getString(R.string.search));
        appHeadView.setMiddleStyle(AppHeadView.SEARCH);
        appHeadView.setMiddleSearchHint(getString(R.string.variety_tip));
    }

    public void getParams() {
        //type=1&keyword=&currentPage=2&pageSize=10
        if (currentPage < 1) currentPage = 1;
        Map<String, String> map = new HashMap<>();
        map.put("type", type);
        map.put("keyword", appHeadView.getEtSearchText().toString());
        map.put("pageSize", "15");
        map.put("currentPage", currentPage + "");
        HttpMgr.getParams(this, map, new CallBack<List<String>>() {
            @Override
            public void onSuccess(List<String> strings) {
                dismissLoading();
                refreshLayout.finishRefresh();
                refreshLayout.finishLoadmore();
                if (strings != null && strings.size() > 0) {
                    refreshLayout.setVisibility(View.VISIBLE);
                    if (singleCheckAdapter == null) {
                        singleCheckAdapter = new SingleCheckAdapter(PriceParamsActivity.this, strings);
                        singleCheckAdapter.setOnItemCheckListener(new SingleCheckAdapter.OnItemCheckListener() {
                            @Override
                            public void onCheck(String name) {
                                Intent intent = new Intent();
                                if (type.equals("1")) {
                                    intent.putExtra("product", name);
                                } else {
                                    intent.putExtra("market", name);
                                }
                                intent.putExtra("type", type);
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        });
                        recyclerView.setAdapter(singleCheckAdapter);
                    } else {
                        if (currentPage == 1) {
                            singleCheckAdapter.setNames(strings);
                        } else if (currentPage > 1) {
                            singleCheckAdapter.addNames(strings);
                        }
                        singleCheckAdapter.notifyDataSetChanged();
                    }
                } else {
                    if (currentPage == 1) {
                        showNoDataView();
                    } else if (currentPage > 1) {
                        currentPage--;
                        Utils.showToast(PriceParamsActivity.this, "没有更多数据");
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
}
