package com.fei.firstproject.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.fei.firstproject.R;
import com.fei.firstproject.entity.ProductLibEntity;
import com.fei.firstproject.inter.OnItemClickListener;
import com.fei.firstproject.utils.GlideUtils;
import com.fei.firstproject.widget.FlowLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/9/11.
 */

public class ProductLibAdapter extends RecyclerView.Adapter<ProductLibAdapter.ProductLibViewHolder> {

    private Context mContext;
    private List<ProductLibEntity> productLibEntities;
    private OnItemClickListener onItemClickListener;
    private String imgHostUrl;

    public ProductLibAdapter(Context mContext, List<ProductLibEntity> productLibEntities, String imgHostUrl) {
        this.mContext = mContext;
        this.productLibEntities = productLibEntities;
        this.imgHostUrl = imgHostUrl;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setProductLibEntities(List<ProductLibEntity> productLibEntities) {
        this.productLibEntities = productLibEntities;
    }

    public void addProductLibEntities(List<ProductLibEntity> productLibEntities) {
        if (this.productLibEntities != null && productLibEntities != null) {
            this.productLibEntities.addAll(productLibEntities);
        }
    }

    public List<ProductLibEntity> getProductLibEntities() {
        return productLibEntities;
    }

    @Override
    public ProductLibViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_product_lib, parent, false);
        return new ProductLibViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductLibViewHolder holder, int position) {
        ProductLibEntity productLibEntity = productLibEntities.get(position);
        holder.tvProduct.setText(productLibEntity.getMatieralName());
        String label = productLibEntity.getLabel();
        holder.flContainer.removeAllViews();
        if (!TextUtils.isEmpty(label)) {
            if (label.contains(",")) {
                String[] split = label.split(",");
                for (int i = 0; i < split.length; i++) {
                    holder.flContainer.addView(createLabel(split[i]));
                }
            } else {
                holder.flContainer.addView(createLabel(label));
            }
        }

        String imgPath = imgHostUrl + productLibEntity.getImagePath();
        if (!TextUtils.isEmpty(imgPath) && (imgPath.contains("http") || imgPath.contains("https"))) {
            Glide.with(mContext)
                    .load(imgPath)
                    .transition(new DrawableTransitionOptions().crossFade(2000))
                    .apply(GlideUtils.getOptions())
                    .into(holder.ivProductLib);
        }
    }

    @Override
    public int getItemCount() {
        return productLibEntities.size();
    }

    private View createLabel(String label) {
        TextView tv = new TextView(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 10, 10);
        tv.setPadding(5, 5, 5, 5);
        tv.setLayoutParams(params);
        tv.setText(label);
        tv.setBackgroundResource(R.drawable.shape_image_rect_border_bg);
        tv.setTextColor(mContext.getResources().getColor(R.color.colorTextSub));
        return tv;
    }

    class ProductLibViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.iv_product_lib)
        ImageView ivProductLib;
        @BindView(R.id.tv_product)
        TextView tvProduct;
        @BindView(R.id.fl_container)
        FlowLayout flContainer;

        public ProductLibViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(v);
            }
        }
    }
}
