package com.fei.firstproject.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.fei.firstproject.R;
import com.fei.firstproject.entity.RetailStoresEntity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Fei on 2017/9/11.
 */

public class RetailerInfoAdapter extends RecyclerView.Adapter<RetailerInfoAdapter.RetailerInfoViewHolder> {

    private Context mContext;
    private List<RetailStoresEntity> retailStoresEntityList;
    private OnToHereListener onToHereListener;

    public void setOnToHereListener(OnToHereListener onToHereListener) {
        this.onToHereListener = onToHereListener;
    }

    public void setRetailStoresEntityList(List<RetailStoresEntity> retailStoresEntityList) {
        this.retailStoresEntityList = retailStoresEntityList;
    }

    public void addRetailStoresEntityList(List<RetailStoresEntity> retailStoresEntityList) {
        if (this.retailStoresEntityList != null && retailStoresEntityList != null) {
            this.retailStoresEntityList.addAll(retailStoresEntityList);
        }
    }

    public RetailerInfoAdapter(Context mContext, List<RetailStoresEntity> retailStoresEntityList) {
        this.mContext = mContext;
        this.retailStoresEntityList = retailStoresEntityList;
    }

    @Override
    public RetailerInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_retailer_info, parent, false);
        return new RetailerInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RetailerInfoViewHolder holder, int position) {
        final RetailStoresEntity retailStoresEntity = retailStoresEntityList.get(position);
        holder.tvAddress.setText(retailStoresEntity.getAddress());
        holder.tvName.setText(retailStoresEntity.getLinkman());
        holder.tvShop.setText(retailStoresEntity.getStoreName());
        holder.tvTel.setText(retailStoresEntity.getMobile());
        holder.btnToHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onToHereListener != null) {
                    onToHereListener.onClick(retailStoresEntity.getAddress());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return retailStoresEntityList.size();
    }

    class RetailerInfoViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.btn_to_here)
        Button btnToHere;
        @BindView(R.id.tv_shop)
        TextView tvShop;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_tel)
        TextView tvTel;
        @BindView(R.id.tv_address)
        TextView tvAddress;

        public RetailerInfoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnToHereListener {
        void onClick(String address);
    }

}
