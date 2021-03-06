package com.fei.firstproject.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.fei.firstproject.R;
import com.fei.firstproject.entity.CropEntity;
import com.fei.firstproject.utils.GlideUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Administrator on 2017/9/14.
 */

public class CropAdapter extends RecyclerView.Adapter<CropAdapter.CropViewHolder> {

    private Context mContext;
    private List<CropEntity> cropEntities;

    public CropAdapter(Context mContext, List<CropEntity> cropEntities) {
        this.mContext = mContext;
        this.cropEntities = cropEntities;
    }

    public CropAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setCropEntities(List<CropEntity> cropEntities) {
        this.cropEntities = cropEntities;
    }

    @Override
    public CropViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_crop, parent, false);
        return new CropViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CropViewHolder holder, int position) {

        if (position == getItemCount() - 1) {
            holder.llCrop.setVisibility(View.GONE);
            holder.flFwt.setVisibility(View.GONE);
            holder.rlAdd.setVisibility(View.VISIBLE);
        } else {
            holder.llCrop.setVisibility(View.VISIBLE);
            holder.flFwt.setVisibility(View.GONE);
            holder.rlAdd.setVisibility(View.GONE);
            CropEntity cropEntity = cropEntities.get(position);
            holder.tvCropName.setText(cropEntity.getCropName());
            holder.tvLand.setText(cropEntity.getLandName());
            holder.tvDate.setText(cropEntity.getCreateDate());
            String imgPath = cropEntity.getImgPath();
            if (!TextUtils.isEmpty(imgPath) && (imgPath.contains("http") || imgPath.contains("https"))) {
                Glide.with(mContext)
                        .load(imgPath)
                        .transition(new DrawableTransitionOptions().crossFade(2000))
                        .apply(GlideUtils.getOptions())
                        .into(holder.iv);
            }
            if (cropEntity.getInTemple() != 0) {
                holder.flFwt.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return cropEntities == null ? 1 : cropEntities.size() + 1;
    }

    class CropViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv)
        ImageView iv;
        @BindView(R.id.tv_crop_name)
        TextView tvCropName;
        @BindView(R.id.tv_land)
        TextView tvLand;
        @BindView(R.id.tv_date)
        TextView tvDate;
        @BindView(R.id.ll_crop)
        LinearLayout llCrop;
        @BindView(R.id.rl_add)
        RelativeLayout rlAdd;
        @BindView(R.id.fl_fwt)
        FrameLayout flFwt;

        public CropViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
