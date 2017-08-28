package com.fei.firstproject.fragment;

import android.os.Bundle;
import android.view.View;

import com.fei.firstproject.adapter.OrderAdapter;
import com.fei.firstproject.entity.OrderEntity;
import com.fei.firstproject.http.BaseWithoutBaseEntityObserver;
import com.fei.firstproject.http.factory.RetrofitFactory;
import com.fei.firstproject.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

/**
 * Created by Administrator on 2017/8/25.
 */

public class OrderFragment extends BaseListFragment {

    private static final String INDEX = "index";
    private int index = 0;
    private OrderAdapter orderAdapter;

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
        //isAll=1&currentPage=1&userId=1119200&
        //待付款  orderFlagId=1&userId=1119200&
        //待发货 orderFlagId=10&userId=1119200&
        //待收货 orderFlagId=11&userId=1119200&
        //待评价 orderFlagId=4&userId=1119200&
        Map<String, String> map = new HashMap<>();
        map.put("currentPage", currentPage + "");
        map.put("userId","1119200");
        switch (index) {
            case 0:
                map.put("isAll", "1");
                break;
            case 1:
                map.put("orderFlagId", "1");
                break;
            case 2:
                map.put("orderFlagId", "10");
                break;
            case 3:
                map.put("orderFlagId", "11");
                break;
            case 4:
                map.put("orderFlagId", "4");
                break;
        }

        getOrder(map);
    }

    private void getOrder(Map<String, String> map) {
        final Observable<OrderEntity> order = RetrofitFactory.getBigDb().getOrder(map);
        order.compose(this.<OrderEntity>createTransformer(true))
                .subscribe(new BaseWithoutBaseEntityObserver<OrderEntity>(activity) {
                    @Override
                    protected void onHandleSuccess(OrderEntity orderEntity) {
                        dismissLoading();
                        refreshLayout.finishRefresh();
                        refreshLayout.finishLoadmore();
                        List<OrderEntity.DataBean> data = orderEntity.getData();
                        if (data != null && data.size() > 0) {
                            refreshLayout.setVisibility(View.VISIBLE);
                            if (orderAdapter == null) {
                                orderAdapter = new OrderAdapter(activity, data);
                                recyclerView.setAdapter(orderAdapter);
                            } else {
                                if (currentPage == 1) {
                                    orderAdapter.setOrders(data);
                                } else if (currentPage > 1) {
                                    orderAdapter.addOrders(data);
                                }
                                orderAdapter.notifyDataSetChanged();
                            }
                        } else {
                            if (currentPage == 1) {
                                showNoDataView();
                            } else if (currentPage > 1) {
                                currentPage--;
                                Utils.showToast(activity, "没有更多数据");
                            }
                        }
                    }

                    @Override
                    protected void onHandleError(String msg) {
                        super.onHandleError(msg);
                        currentPage--;
                        showRequestErrorView();
                        refreshLayout.finishRefresh();
                        refreshLayout.finishLoadmore();
                    }
                });
    }

    public static OrderFragment newInstance(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt(INDEX, position);
        OrderFragment orderFragment = new OrderFragment();
        orderFragment.setArguments(bundle);
        return orderFragment;
    }

    @Override
    public void initData() {
        index = getArguments().getInt(INDEX, 0);
    }

}
