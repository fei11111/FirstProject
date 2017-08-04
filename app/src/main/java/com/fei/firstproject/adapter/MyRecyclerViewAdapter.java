package com.fei.firstproject.adapter;

import android.content.Context;
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

    private int[] res = {R.drawable.ic_my_ask, R.drawable.ic_my_answer, R.drawable.ic_my_attention,
            R.drawable.ic_my_collection, R.drawable.ic_my_file};
    private int[] desps = {R.string.my_ask, R.string.my_answer, R.string.my_attention, R.string.my_attention,
            R.string.my_collection, R.string.my_file};

    private Context mContext;

    public MyRecyclerViewAdapter(Context mContext) {
        this.mContext = mContext;
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
