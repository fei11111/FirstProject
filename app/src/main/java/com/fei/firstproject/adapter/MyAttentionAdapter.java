package com.fei.firstproject.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.fei.firstproject.R;
import com.fei.firstproject.entity.ExpertEntity;
import com.fei.firstproject.inter.OnItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2017/8/29.
 */

public class MyAttentionAdapter extends RecyclerView.Adapter<MyAttentionAdapter.MyAttentionViewHolder> {

    private Context mContext;
    private List<ExpertEntity> expertEntities;
    private OnItemClickListener onItemClickListener;

    public MyAttentionAdapter(Context mContext, List<ExpertEntity> expertEntities) {
        this.mContext = mContext;
        this.expertEntities = expertEntities;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public MyAttentionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_my_attention, parent, false);
        return new MyAttentionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyAttentionViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 20;
    }

    class MyAttentionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.iv_icon)
        CircleImageView ivIcon;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.ll_my_attention_left)
        LinearLayout llMyAttentionLeft;
        @BindView(R.id.btn_attention)
        Button btnAttention;
        @BindView(R.id.tv_service_duration)
        TextView tvServiceDuration;
        @BindView(R.id.tv_spec_crop)
        TextView tvSpecCrop;
        @BindView(R.id.tv_spec_skill)
        TextView tvSpecSkill;
        @BindView(R.id.rb_star)
        RatingBar rbStar;

        public MyAttentionViewHolder(View itemView) {
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
