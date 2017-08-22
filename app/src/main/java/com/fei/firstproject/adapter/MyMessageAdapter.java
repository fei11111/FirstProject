package com.fei.firstproject.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fei.firstproject.R;
import com.fei.firstproject.entity.MessageEntity;

import java.util.List;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/8/21.
 */

public class MyMessageAdapter extends RecyclerView.Adapter<MyMessageAdapter.MyMessageViewHolder> {

    private Context mContext;
    private List<MessageEntity> messageEntities;

    public MyMessageAdapter(Context mContext, List<MessageEntity> messageEntities) {
        this.mContext = mContext;
        this.messageEntities = messageEntities;
    }

    @Override
    public MyMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_my_message, parent, false);
        return new MyMessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyMessageViewHolder holder, int position) {
        MessageEntity messageEntity = messageEntities.get(position);
        holder.tvTitle.setText(messageEntity.getTitle());
        holder.tvContent.setText(messageEntity.getContent());
    }

    public void addMessageList(List<MessageEntity> messageList) {
        if (messageList != null && messageEntities != null) {
            messageEntities.addAll(messageList);
        }
    }

    @Override
    public int getItemCount() {
        return messageEntities.size();
    }

    class MyMessageViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.iv_right_arrow)
        ImageView ivRightArrow;
        @BindView(R.id.ll_right_time)
        LinearLayout llRightTime;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_content)
        TextView tvContent;

        public MyMessageViewHolder(View itemView) {
            super(itemView);
        }
    }
}
