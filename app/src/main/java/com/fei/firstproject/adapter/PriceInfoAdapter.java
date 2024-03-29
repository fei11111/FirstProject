package com.fei.firstproject.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.fei.firstproject.R;
import com.fei.firstproject.entity.PriceInfoEntity;

import java.util.List;



/**
 * Created by Administrator on 2018/1/15.
 */

public class PriceInfoAdapter extends RecyclerView.Adapter<PriceInfoAdapter.PriceInfoViewHolder> {

    private Context mContext;
    private List<PriceInfoEntity> priceInfoEntities;

    public PriceInfoAdapter(Context mContext, List<PriceInfoEntity> priceInfoEntities) {
        this.mContext = mContext;
        this.priceInfoEntities = priceInfoEntities;
    }

    @Override
    public PriceInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_price_info, parent, false);
        return new PriceInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PriceInfoViewHolder holder, int position) {
        PriceInfoEntity priceInfoEntity = priceInfoEntities.get(position);
        holder.tvMarket.setText(priceInfoEntity.getMarket());
        holder.tvPrice.setText(priceInfoEntity.getPrice() + priceInfoEntity.getUnit());
        holder.tvProduct.setText(priceInfoEntity.getProduct());
        holder.tvProvince.setText(priceInfoEntity.getProvince());
        String postDateTime = priceInfoEntity.getPostDateTime();
        if (!TextUtils.isEmpty(postDateTime)) {
            if (postDateTime.length() > 10) {
                postDateTime = postDateTime.substring(0, 10);
            }
            holder.tvTime.setText(postDateTime);
        }
    }

    @Override
    public int getItemCount() {
        return priceInfoEntities.size();
    }

    class PriceInfoViewHolder extends RecyclerView.ViewHolder {

        TextView tvProduct;
        TextView tvProvince;
        TextView tvPrice;
        TextView tvMarket;
        TextView tvTime;

        public PriceInfoViewHolder(View itemView) {
            super(itemView);
            tvProduct = itemView.findViewById(R.id.tv_product);
            tvProvince = itemView.findViewById(R.id.tv_province);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvMarket = itemView.findViewById(R.id.tv_market);
            tvTime = itemView.findViewById(R.id.tv_time);
        }
    }

    public void setPriceInfoEntities(List<PriceInfoEntity> priceInfoEntities) {
        this.priceInfoEntities = priceInfoEntities;
    }

    public void addPriceInfoEntities(List<PriceInfoEntity> priceInfoEntities) {
        if (this.priceInfoEntities != null && priceInfoEntities != null) {
            this.priceInfoEntities.addAll(priceInfoEntities);
        }
    }
}
