package com.fei.firstproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.fei.firstproject.R;
import com.fei.firstproject.entity.ShareEntity;
import com.fei.firstproject.inter.OnItemClickListener;
import com.fei.firstproject.utils.GlideUtils;
import com.fei.firstproject.widget.FlowLayout;
import com.fei.firstproject.widget.RoundImageView;

import java.util.List;


/**
 * Created by Administrator on 2017/9/14.
 */

public class ShareAdapter extends RecyclerView.Adapter<ShareAdapter.ShareViewHolder> {

    private Context mContext;
    private List<ShareEntity> shareEntities;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public ShareAdapter(Context mContext, List<ShareEntity> shareEntities) {
        this.mContext = mContext;
        this.shareEntities = shareEntities;
    }

    public void setShareEntities(List<ShareEntity> shareEntities) {
        this.shareEntities = shareEntities;
    }

    @Override
    public ShareViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_share, parent, false);
        return new ShareViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ShareViewHolder holder, int position) {
        ShareEntity shareEntity = shareEntities.get(position);
        holder.tvTitle.setText(shareEntity.getUserName() + "-" + shareEntity.getCropName());
        holder.tvDate.setText(shareEntity.getCreateTime());
        holder.tvContent.setText(shareEntity.getGroupName() + ":" + shareEntity.getValue());
        holder.flPic.removeAllViews();
        List<ShareEntity.ImageEntity> imgPath = shareEntity.getImgPath();
        for (int i = 0; i < imgPath.size(); i++) {
            holder.flPic.addView(createImage(imgPath.get(i).getPath()));
        }
    }

    private View createImage(String url) {
        RoundImageView iv = new RoundImageView(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mContext.getResources().getDimensionPixelSize(R.dimen.size_45), mContext.getResources().getDimensionPixelSize(R.dimen.size_45));
        params.setMargins(0, 0, 10, 0);
        iv.setLayoutParams(params);
        iv.setType(RoundImageView.TYPE_ROUND);
        iv.setBorderRadius(5);
        Glide.with(mContext)
                .load(url)
                .transition(new DrawableTransitionOptions().crossFade(2000))
                .apply(GlideUtils.getOptions())
                .into(iv);
        return iv;
    }

    @Override
    public int getItemCount() {
        return shareEntities.size();
    }

    class ShareViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvDate;
        TextView tvTitle;
        TextView tvContent;
        FlowLayout flPic;

        public ShareViewHolder(View itemView) {
            super(itemView);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvContent = (TextView) itemView.findViewById(R.id.tv_content);
            flPic = (FlowLayout) itemView.findViewById(R.id.fl_pic);
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
