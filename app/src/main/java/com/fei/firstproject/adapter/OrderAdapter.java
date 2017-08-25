package com.fei.firstproject.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fei.firstproject.R;

/**
 * Created by Fei on 2017/8/25.
 */

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHoder> {

    private Context mContext;

    public OrderAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public OrderViewHoder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_order,parent,false);
        return new OrderViewHoder(view);
    }

    @Override
    public void onBindViewHolder(OrderViewHoder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 20;
    }

    class OrderViewHoder extends RecyclerView.ViewHolder {

        public OrderViewHoder(View itemView) {
            super(itemView);
        }
    }
}
