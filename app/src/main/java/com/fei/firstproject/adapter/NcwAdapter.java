package com.fei.firstproject.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fei.firstproject.R;
import com.fei.firstproject.entity.NcwEntity;
import com.fei.firstproject.inter.OnItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/8/14.
 */

public class NcwAdapter extends RecyclerView.Adapter<NcwAdapter.NcwViewHolder> {

    private List<NcwEntity> ncwEntities;
    private Context mContext;
    private OnItemClickListener onItemClickListener;

    public NcwAdapter(Context mContext, List<NcwEntity> ncwEntities) {
        this.mContext = mContext;
        this.ncwEntities = ncwEntities;
    }

    public void setNcwEntities(List<NcwEntity> ncwEntities) {
        this.ncwEntities = ncwEntities;
    }

    public List<NcwEntity> getNcwEntities() {
        return ncwEntities;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public NcwViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_ncw, parent, false);
        return new NcwViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NcwViewHolder holder, int position) {
        NcwEntity ncwEntity = ncwEntities.get(position);
        holder.tvNcwTitle.setText(ncwEntity.getTitle());
        holder.tvNwcDesp.setText(ncwEntity.getDescription().replaceAll("\\s*", ""));
        Glide.with(mContext)
                .load(ncwEntity.getThumb())
                .placeholder(R.drawable.ic_app)
                .crossFade()
                .error(R.drawable.ic_pic_error)
                .into(holder.ivNcw);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return ncwEntities.size() > 3 ? 3 : ncwEntities.size();
    }

    class NcwViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.iv_ncw)
        ImageView ivNcw;
        @BindView(R.id.tv_ncw_title)
        TextView tvNcwTitle;
        @BindView(R.id.tv_nwc_desp)
        TextView tvNwcDesp;

        public NcwViewHolder(View itemView) {
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
