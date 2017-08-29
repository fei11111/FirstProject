package com.fei.firstproject.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2017/8/29.
 */

public class MyAttentionAdapter extends RecyclerView.Adapter<MyAttentionAdapter.MyAttentionViewHolder> {

    @Override
    public MyAttentionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(MyAttentionViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class MyAttentionViewHolder extends RecyclerView.ViewHolder{
        public MyAttentionViewHolder(View itemView) {
            super(itemView);
        }
    }

}
