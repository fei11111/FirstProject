package com.fei.firstproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.fei.firstproject.R;
import com.fei.firstproject.inter.OnItemClickListener;
import com.fei.firstproject.utils.Utils;
/**
 * Created by Administrator on 2017/8/4.
 */

public class MeFragmentAdapter extends RecyclerView.Adapter<MeFragmentAdapter.MyViewHolder> {

    private int[] res;//图片
    private String[] desps;//描述
    private Context mContext;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public MeFragmentAdapter(Context mContext, int ids, String[] desps) {
        this.mContext = mContext;
        this.desps = desps;
        res = Utils.getDrawableByArray(mContext, ids);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_my, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.ivIcon.setImageResource(res[position]);
        holder.tvDesc.setText(desps[position]);
    }

    @Override
    public int getItemCount() {
        return res.length;
    }


    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView ivIcon;
        TextView tvDesc;

        public MyViewHolder(View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_icon);
            tvDesc = itemView.findViewById(R.id.tv_desc);
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
