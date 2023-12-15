package com.fei.firstproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.fei.firstproject.R;
import com.fei.firstproject.entity.ExpertEntity;
import com.fei.firstproject.inter.OnItemClickListener;
import com.fei.firstproject.utils.GlideUtils;
import com.fei.firstproject.widget.RoundImageView;

import java.util.List;

/**
 * Created by Administrator on 2017/8/29.
 */

public class MyAttentionAdapter extends RecyclerView.Adapter<MyAttentionAdapter.MyAttentionViewHolder> {

    private Context mContext;
    private List<ExpertEntity> expertEntities;
    private OnItemClickListener onItemClickListener;
    private OnCancelAttentionListener onCancelAttentionListener;

    public MyAttentionAdapter(Context mContext, List<ExpertEntity> expertEntities) {
        this.mContext = mContext;
        this.expertEntities = expertEntities;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnCancelAttentionListener(OnCancelAttentionListener onCancelAttentionListener) {
        this.onCancelAttentionListener = onCancelAttentionListener;
    }

    public void setExpertEntities(List<ExpertEntity> expertEntities) {
        this.expertEntities = expertEntities;
    }

    public void addExpertEntities(List<ExpertEntity> expertEntities) {
        if (expertEntities != null && this.expertEntities != null) {
            this.expertEntities.addAll(expertEntities);
        }
    }

    public List<ExpertEntity> getExpertEntities() {
        return expertEntities;
    }

    @Override
    public MyAttentionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_expertise, parent, false);
        return new MyAttentionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyAttentionViewHolder holder, int position) {
        final ExpertEntity expertEntity = expertEntities.get(position);
        holder.tvName.setText(expertEntity.getExpertName());
        holder.tvServiceDuration.setText(expertEntity.getServiceTime());
        holder.tvSpecCrop.setText(expertEntity.getPlantNames());
        holder.tvSpecSkill.setText(expertEntity.getExpertise());
        holder.rbStar.setRating(expertEntity.getLevel_start());
        holder.btnAttention.setText(mContext.getString(R.string.already_attention));
        holder.btnAttention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCancelAttentionListener != null) {
                    onCancelAttentionListener.cancle(expertEntity);
                }
            }
        });

        Glide.with(mContext)
                .load("http://218.18.114.97:3392/btFile" + expertEntity.getImgPath())
                .transition(new DrawableTransitionOptions().crossFade(2000))
                .apply(GlideUtils.getOptions())
                .into(holder.ivIcon);
    }

    @Override
    public int getItemCount() {
        return expertEntities.size();
    }

    class MyAttentionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        RoundImageView ivIcon;
        TextView tvName;
        LinearLayout llMyAttentionLeft;
        Button btnAttention;
        TextView tvServiceDuration;
        TextView tvSpecCrop;
        TextView tvSpecSkill;
        RatingBar rbStar;

        public MyAttentionViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            ivIcon = (RoundImageView) itemView.findViewById(R.id.iv_icon);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            llMyAttentionLeft = (LinearLayout) itemView.findViewById(R.id.ll_my_attention_left);
            btnAttention = (Button) itemView.findViewById(R.id.btn_attention);
            tvServiceDuration = (TextView) itemView.findViewById(R.id.tv_service_duration);
            tvSpecCrop = (TextView) itemView.findViewById(R.id.tv_spec_crop);
            tvSpecSkill = (TextView) itemView.findViewById(R.id.tv_spec_skill);
            rbStar = (RatingBar) itemView.findViewById(R.id.rb_star);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(v);
            }
        }
    }

    public interface OnCancelAttentionListener {
        void cancle(ExpertEntity expertEntity);
    }
}
