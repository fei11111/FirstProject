package com.fei.firstproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fei.firstproject.R;
import com.fei.firstproject.entity.RecommendEntity;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/8/14.
 */

public class RecommendPlanAdapter extends BaseAdapter {

    private Context mContext;
    private List<RecommendEntity> recommendEntities;
    private int count = -1;

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

    @Override
    public int getCount() {
        return recommendEntities.size() >= 3 ? count > 0 ? count : recommendEntities.size() : recommendEntities.size();
    }

    @Override
    public Object getItem(int i) {
        return recommendEntities.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_recommend_plan, viewGroup, false);
            holder.iv_recommand_plan = ButterKnife.findById(view, R.id.iv_recommend_plan);
            holder.tv_recommand_plan_title = ButterKnife.findById(view, R.id.tv_recommend_plan_title);
            holder.tv_recommand_plan_desp = ButterKnife.findById(view, R.id.tv_recommend_plan_desp);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        RecommendEntity recommendEntity = recommendEntities.get(i);
        holder.tv_recommand_plan_title.setText(recommendEntity.getTitle());
        holder.tv_recommand_plan_desp.setText(recommendEntity.getContent());
        Glide.with(mContext)
                .load(recommendEntity.getImgPath())
                .placeholder(R.drawable.ic_app)
                .crossFade()
                .error(R.drawable.ic_pic_error)
                .into(holder.iv_recommand_plan);
        return view;
    }

    class ViewHolder {
        ImageView iv_recommand_plan;
        TextView tv_recommand_plan_title;
        TextView tv_recommand_plan_desp;
    }
}