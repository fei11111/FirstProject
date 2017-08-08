package com.fei.firstproject.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fei.firstproject.R;
import com.fei.firstproject.adapter.MyRecyclerViewAdapter.MyViewHolder;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/8/4.
 */

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private int[] res;//图片
    private String[] desps;//描述

    private Context mContext;

    public MyRecyclerViewAdapter(Context mContext, int ids, String[] desps) {
        this.mContext = mContext;
        this.desps = desps;
        initDrawable(ids);
    }

    private void initDrawable(int ids) {
        TypedArray ar = mContext.getResources().obtainTypedArray(ids);
        int len = ar.length();
        res = new int[len];
        for (int i = 0; i < len; i++)
            res[i] = ar.getResourceId(i, 0);
        ar.recycle();
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


    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_icon)
        ImageView ivIcon;
        @BindView(R.id.tv_desc)
        TextView tvDesc;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
