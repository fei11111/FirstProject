package com.fei.firstproject.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fei.firstproject.R;

/**
 * Created by Administrator on 2017/8/21.
 */

public class MyMessageAdapter extends RecyclerView.Adapter<MyMessageAdapter.MyMessageViewHolder> {

    private Context mContext;

    public MyMessageAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public MyMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_my_message, parent, false);
        return new MyMessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyMessageViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 3;
    }

    class MyMessageViewHolder extends RecyclerView.ViewHolder {
        public MyMessageViewHolder(View itemView) {
            super(itemView);
        }
    }
}
