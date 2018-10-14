package com.fei.firstproject.fragment;

import android.os.Bundle;
import android.view.View;

import com.fei.firstproject.R;
import com.fei.firstproject.adapter.OrderAdapter;
import com.fei.firstproject.config.AppConfig;
import com.fei.firstproject.dialog.TipDialog;
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
    private TipDialog tipDialog;

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
        map.put("userId", AppConfig.user.getId());
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
                        refreshLayout.setRefreshing(false);
                        refreshLayout.setLoadingMore(false);
                        List<OrderEntity.DataBean> data = orderEntity.getData();
                        if (data != null && data.size() > 0) {
                            refreshLayout.setVisibility(View.VISIBLE);
                            if (orderAdapter == null) {
                                activity.setLinearRecycleViewSetting(recyclerView, activity);
                                orderAdapter = new OrderAdapter(activity, data);
                                orderAdapter.setOnCancelPayListener(onCancelPayListener);
                                orderAdapter.setOnConfirmReceiveListener(onConfirmReceiveListener);
                                orderAdapter.setOnEvaluateListener(onEvaluateListener);
                                orderAdapter.setOnPayListener(onPayListener);
                                orderAdapter.setOnRemindDeliveryListener(onRemindDeliveryListener);
                                orderAdapter.setOnSalesReturnListener(onSalesReturnListener);
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
                        refreshLayout.setRefreshing(false);
                        refreshLayout.setLoadingMore(false);
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

    private OrderAdapter.OnPayListener onPayListener = new OrderAdapter.OnPayListener() {
        @Override
        public void pay(OrderEntity.DataBean dataBean) {
            showTipDialog("是否确认支付？");
        }
    };

    private OrderAdapter.OnCancelPayListener onCancelPayListener = new OrderAdapter.OnCancelPayListener() {
        @Override
        public void cancel(String orderHeadId, int orderFlagId) {
            showTipDialog("是否确认撤单？");
        }
    };

    private OrderAdapter.OnConfirmReceiveListener onConfirmReceiveListener = new OrderAdapter.OnConfirmReceiveListener() {
        @Override
        public void confirm(String orderHeadId) {
            showTipDialog("是否确认已收货？");
        }
    };

    private OrderAdapter.OnEvaluateListener onEvaluateListener = new OrderAdapter.OnEvaluateListener() {
        @Override
        public void evaluate(String orderHeadId) {

        }
    };

    private OrderAdapter.OnRemindDeliveryListener onRemindDeliveryListener = new OrderAdapter.OnRemindDeliveryListener() {
        @Override
        public void remind(String orderHeadId) {

        }
    };

    private OrderAdapter.OnSalesReturnListener onSalesReturnListener = new OrderAdapter.OnSalesReturnListener() {
        @Override
        public void salesReturn(String orderHeadId) {
            showTipDialog("是否确认退货？");
        }
    };

    private void showTipDialog(String tip) {
        if (tipDialog == null) {
            tipDialog = new TipDialog(activity);
            tipDialog.setCanceledOnTouchOutside(false);
            tipDialog.setConfirmButtonText(getResources().getString(R.string.confirm));
            tipDialog.setOnConfirmListener(new TipDialog.OnConfirmListener() {
                @Override
                public void onClick(View view) {
                    Utils.showToast(activity, "之后就没有下文了");
                }
            });
        }
        tipDialog.setContentText(tip);
        tipDialog.show();
    }

}
