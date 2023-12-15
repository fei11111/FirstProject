package com.fei.firstproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.fei.firstproject.R;
import com.fei.firstproject.entity.MessageEntity;
import com.fei.firstproject.inter.OnItemClickListener;

import java.util.List;
/**
 * Created by Administrator on 2017/8/21.
 */

public class MyMessageAdapter extends RecyclerView.Adapter<MyMessageAdapter.MyMessageViewHolder> {

    private Context mContext;
    private List<MessageEntity> messageEntities;
    private OnItemClickListener onItemClickListener;

    public List<MessageEntity> getMessageEntities() {
        return messageEntities;
    }

    public void setMessageEntities(List<MessageEntity> messageEntities) {
        this.messageEntities = messageEntities;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public MyMessageAdapter(Context mContext, List<MessageEntity> messageEntities) {
        this.mContext = mContext;
        this.messageEntities = messageEntities;
    }

    public void addMessageList(List<MessageEntity> messageEntities) {
        if (this.messageEntities != null && messageEntities != null) {
            this.messageEntities.addAll(messageEntities);
        }
    }

    @Override
    public MyMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_my_message, parent, false);
        return new MyMessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyMessageViewHolder holder, int position) {
        MessageEntity messageEntity = messageEntities.get(position);
        int flag = messageEntity.getFlag();
        if (flag == 1) {
            //已读
            holder.tvTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        } else {
            holder.tvTitle.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().getDrawable(R.drawable.shape_red_dot), null, null, null);
        }
        holder.tvTitle.setText(messageEntity.getTitle());
        holder.tvContent.setText(messageEntity.getContent());
        holder.tvTime.setText(messageEntity.getSendTime());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return messageEntities.size();
    }

    class MyMessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvTime;
        ImageView ivRightArrow;
        LinearLayout llRightTime;
        TextView tvTitle;
        TextView tvContent;

        public MyMessageViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tvTime = itemView.findViewById(R.id.tv_time);
            ivRightArrow = itemView.findViewById(R.id.iv_right_arrow);
            llRightTime = itemView.findViewById(R.id.ll_right_time);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvContent = itemView.findViewById(R.id.tv_content);

        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(v);
            }
        }
    }
}
