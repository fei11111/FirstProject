package com.fei.firstproject.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fei.firstproject.R;
import com.fei.firstproject.entity.OrderEntity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Fei on 2017/8/25.
 */

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHoder> {

    private Context mContext;
    private List<OrderEntity.DataBean> orders;
    private OnCancelPayListener onCancelPayListener;
    private OnPayListener onPayListener;
    private OnSalesReturnListener onSalesReturnListener;
    private OnEvaluateListener onEvaluateListener;
    private OnRemindDeliveryListener onRemindDeliveryListener;
    private OnConfirmReceiveListener onConfirmReceiveListener;

    public void setOnCancelPayListener(OnCancelPayListener onCancelPayListener) {
        this.onCancelPayListener = onCancelPayListener;
    }

    public void setOnPayListener(OnPayListener onPayListener) {
        this.onPayListener = onPayListener;
    }

    public void setOnSalesReturnListener(OnSalesReturnListener onSalesReturnListener) {
        this.onSalesReturnListener = onSalesReturnListener;
    }

    public void setOnEvaluateListener(OnEvaluateListener onEvaluateListener) {
        this.onEvaluateListener = onEvaluateListener;
    }

    public void setOnRemindDeliveryListener(OnRemindDeliveryListener onRemindDeliveryListener) {
        this.onRemindDeliveryListener = onRemindDeliveryListener;
    }

    public void setOnConfirmReceiveListener(OnConfirmReceiveListener onConfirmReceiveListener) {
        this.onConfirmReceiveListener = onConfirmReceiveListener;
    }

    public OrderAdapter(Context mContext, List<OrderEntity.DataBean> orders) {
        this.mContext = mContext;
        this.orders = orders;
    }

    public List<OrderEntity.DataBean> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderEntity.DataBean> orders) {
        this.orders = orders;
    }

    public void addOrders(List<OrderEntity.DataBean> orders) {
        if (orders != null && this.orders != null) {
            this.orders.addAll(orders);
        }
    }

    @Override
    public OrderViewHoder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_order, parent, false);
        return new OrderViewHoder(view);
    }

    @Override
    public void onBindViewHolder(OrderViewHoder holder, int position) {
        final OrderEntity.DataBean dataBean = orders.get(position);
        holder.tvService.setText(dataBean.getUser_service_name());
        holder.tvStatus.setText(getOrderName(dataBean.getOrder_flag_id()));
        holder.tvCount.setText(dataBean.getQty_order() + "公斤");
        holder.tvMoney.setText(dataBean.getAtm_payable() + "");
        holder.tvDesc.setText("您配置的氮磷钾化肥比为:" + dataBean.getN() + "-" + dataBean.getP() + "-" + dataBean.getK());
        holder.btnCancle.setVisibility(View.VISIBLE);
        holder.btnPay.setVisibility(View.VISIBLE);
        holder.btnCancle.setEnabled(true);
        holder.btnPay.setEnabled(true);
        final String order_head_id = dataBean.getOrder_head_id();//获取订单编号
        final int order_flag_id = dataBean.getOrder_flag_id();//订单状态

        switch (dataBean.getOrder_flag_id()) {
            case 1:// 待付款
                holder.btnCancle.setText(mContext.getString(R.string.cancel_pay));
                holder.btnPay.setText(mContext.getString(R.string.immediately_pay));
                holder.btnCancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        if (onCancelPayListener != null) {
                            onCancelPayListener.cancel(order_head_id, order_flag_id);
                        }
                    }
                });
                holder.btnPay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        if (onPayListener != null) {
                            onPayListener.pay(dataBean);
                        }
                    }
                });
                break;
            case 2:// 撤单成功
                holder.btnCancle.setVisibility(View.GONE);
                holder.btnPay.setText(mContext.getString(R.string.close_transaction));
                holder.btnPay.setEnabled(false);
                break;
            case 3:// 待配置
                holder.btnCancle.setVisibility(View.GONE);
                holder.btnPay.setText(mContext.getString(R.string.cancel_pay));
                holder.btnPay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        if (onCancelPayListener != null) {
                            onCancelPayListener.cancel(order_head_id, order_flag_id);
                        }
                    }
                });
                break;
            case 12:// 待配置
                holder.btnCancle.setVisibility(View.GONE);
                holder.btnPay.setText(mContext.getString(R.string.cancel_pay));
                holder.btnPay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        if (onCancelPayListener != null) {
                            onCancelPayListener.cancel(order_head_id, order_flag_id);
                        }
                    }
                });
                break;
            case 4:// 待评价
                holder.btnCancle.setText(mContext.getString(R.string.sales_return));
                holder.btnCancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        if (onSalesReturnListener != null) {
                            onSalesReturnListener.salesReturn(order_head_id);
                        }
                    }
                });
                holder.btnPay.setText(mContext.getString(R.string.go_to_evaluate));
                holder.btnPay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        if (onEvaluateListener != null) {
                            onEvaluateListener.evaluate(order_head_id);
                        }
                    }
                });
                break;
            case 5:// 评价完成
                holder.btnCancle.setVisibility(View.GONE);
                holder.btnPay.setText(mContext.getString(R.string.complete_transaction));
                holder.btnPay.setEnabled(false);
                break;
            case 7://退货审核中
                holder.btnCancle.setVisibility(View.GONE);
                holder.btnPay.setText(mContext.getString(R.string.sales_returning));
                holder.btnPay.setEnabled(false);
                break;
            case 8://撤单审核中
                holder.btnCancle.setVisibility(View.GONE);
                holder.btnPay.setText(mContext.getString(R.string.cancel_paying));
                holder.btnPay.setEnabled(false);
                break;
            case 10://待发货
                holder.btnCancle.setVisibility(View.GONE);
                holder.btnPay.setText(mContext.getString(R.string.remind_delivery));
                holder.btnPay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        if (onRemindDeliveryListener != null) {
                            onRemindDeliveryListener.remind(order_head_id);
                        }
                    }
                });
                break;
            case 9://退货成功
                holder.btnCancle.setVisibility(View.GONE);
                holder.btnPay.setText(mContext.getString(R.string.close_transaction));
                holder.btnPay.setEnabled(false);
                break;
            case 11://待收货
                holder.btnCancle.setVisibility(View.GONE);
                holder.btnPay.setText(mContext.getString(R.string.confrim_receive));
                holder.btnPay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        if (onConfirmReceiveListener != null) {
                            onConfirmReceiveListener.confirm(order_head_id);
                        }
                    }
                });
                break;

            default:
                break;
        }
    }

    /**
     * 订单状态
     *
     * @param i
     * @return
     */
    private String getOrderName(int i) {
        String order = null;
        switch (i) {
            case 1:
                order = "待付款";
                break;
            case 2:
                order = "撤单成功";
                break;
            case 3:
                order = "待配置";
                break;
            case 4:
                order = "待评价";
                break;
            case 5:
                order = "评价完成";
                break;
            case 7:
                order = "退货审核中";
                break;
            case 8:
                order = "撤单审核中";
                break;
            case 9:
                order = "退货成功";
                break;
            case 10:
                order = "待发货";
                break;
            case 11:
                order = "待收货";
                break;
            case 12:
                order = "待配置";
                break;
            default:
                break;
        }
        return order;
    }

    //撤单
    public interface OnCancelPayListener {
        void cancel(String orderHeadId, int orderFlagId);
    }

    //付款
    public interface OnPayListener {
        void pay(OrderEntity.DataBean dataBean);
    }

    //退货
    public interface OnSalesReturnListener {
        void salesReturn(String orderHeadId);
    }

    //评价
    public interface OnEvaluateListener {
        void evaluate(String orderHeadId);
    }

    //提醒发货
    public interface OnRemindDeliveryListener {
        void remind(String orderHeadId);
    }

    //确认收货
    public interface OnConfirmReceiveListener {
        void confirm(String orderHeadId);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    class OrderViewHoder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_service)
        TextView tvService;
        @BindView(R.id.tv_status)
        TextView tvStatus;
        @BindView(R.id.iv_order_pic)
        ImageView ivOrderPic;
        @BindView(R.id.tv_desc)
        TextView tvDesc;
        @BindView(R.id.tv_count)
        TextView tvCount;
        @BindView(R.id.tv_money)
        TextView tvMoney;
        @BindView(R.id.btn_pay)
        Button btnPay;
        @BindView(R.id.btn_cancle)
        Button btnCancle;

        public OrderViewHoder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
