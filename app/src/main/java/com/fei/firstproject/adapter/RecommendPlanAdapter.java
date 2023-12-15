package com.fei.firstproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.fei.firstproject.R;
import com.fei.firstproject.entity.RecommendEntity;
import com.fei.firstproject.inter.OnItemClickListener;
import com.fei.firstproject.utils.GlideUtils;

import java.util.List;


/**
 * Created by Administrator on 2017/8/14.
 */

public class RecommendPlanAdapter extends RecyclerView.Adapter<RecommendPlanAdapter.RecommendViewHoder> {

    private Context mContext;
    private List<RecommendEntity> recommendEntities;
    private int count = -1;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public RecommendPlanAdapter(Context mContext, List<RecommendEntity> recommendEntities) {
        this.mContext = mContext;
        this.recommendEntities = recommendEntities;
    }

    public RecommendPlanAdapter(Context mContext, List<RecommendEntity> recommendEntities, int count) {
        this.mContext = mContext;
        this.recommendEntities = recommendEntities;
        this.count = count;
    }

    public void setRecommendEntities(List<RecommendEntity> recommendEntities) {
        if (recommendEntities != null) {
            this.recommendEntities = recommendEntities;
        }
    }

    public void addRecommendEntities(List<RecommendEntity> recommendEntities) {
        if (this.recommendEntities != null && recommendEntities != null) {
            this.recommendEntities.addAll(recommendEntities);
        }
    }

    public List<RecommendEntity> getRecommendEntities() {
        return recommendEntities;
    }

    @Override
    public RecommendViewHoder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_recommend_plan, parent, false);
        return new RecommendViewHoder(view);
    }

    @Override
    public void onBindViewHolder(RecommendViewHoder holder, int position) {
        RecommendEntity recommendEntity = recommendEntities.get(position);
        holder.tvRecommendPlanTitle.setText(recommendEntity.getTitle());
        holder.tvRecommendPlanDesp.setText(recommendEntity.getContent());
        Glide.with(mContext)
                .load(recommendEntity.getImgPath())
                .transition(new DrawableTransitionOptions().crossFade(2000))
                .apply(GlideUtils.getOptions())
                .into(holder.ivRecommendPlan);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return recommendEntities.size() >= 3 ? count > 0 ? count : recommendEntities.size() : recommendEntities.size();
    }

    class RecommendViewHoder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView ivRecommendPlan;
        ImageView ivRecommendPlanArrow;
        TextView tvRecommendPlanTitle;
        TextView tvRecommendPlanDesp;

        public RecommendViewHoder(View itemView) {
            super(itemView);
            ivRecommendPlan = itemView.findViewById(R.id.iv_recommend_plan);
            ivRecommendPlanArrow = itemView.findViewById(R.id.iv_recommend_plan_arrow);
            tvRecommendPlanTitle = itemView.findViewById(R.id.tv_recommend_plan_title);
            tvRecommendPlanDesp = itemView.findViewById(R.id.tv_recommend_plan_desp);
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
