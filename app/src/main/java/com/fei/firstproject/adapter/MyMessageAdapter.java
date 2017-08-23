package com.fei.firstproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fei.firstproject.R;
import com.fei.firstproject.entity.MessageEntity;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/8/21.
 */

public class MyMessageAdapter extends BaseAdapter {

    private Context mContext;
    private List<MessageEntity> messageEntities;

    public MyMessageAdapter(Context mContext, List<MessageEntity> messageEntities) {
        this.mContext = mContext;
        this.messageEntities = messageEntities;
    }

    public void setMessageList(List<MessageEntity> messageEntities) {
        if (messageEntities != null) {
            this.messageEntities = messageEntities;
        }
    }

    public void addMessageList(List<MessageEntity> messageEntities) {
        if (this.messageEntities != null && messageEntities != null) {
            this.messageEntities.addAll(messageEntities);
        }
    }

    @Override
    public int getCount() {
        return messageEntities.size();
    }

    @Override
    public Object getItem(int position) {
        return messageEntities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_my_message, parent, false);
            holder.tvContent = ButterKnife.findById(convertView, R.id.tv_content);
            holder.tvTime = ButterKnife.findById(convertView, R.id.tv_time);
            holder.ivRightArrow = ButterKnife.findById(convertView, R.id.iv_right_arrow);
            holder.llRightTime = ButterKnife.findById(convertView, R.id.ll_right_time);
            holder.tvTitle = ButterKnife.findById(convertView, R.id.tv_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
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
        return convertView;
    }

    class ViewHolder {
        TextView tvTime;
        ImageView ivRightArrow;
        LinearLayout llRightTime;
        TextView tvTitle;
        TextView tvContent;
    }
}
