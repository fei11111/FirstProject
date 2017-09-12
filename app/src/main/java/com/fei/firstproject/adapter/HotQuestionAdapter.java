package com.fei.firstproject.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fei.firstproject.R;
import com.fei.firstproject.entity.HotQuestionEntity;
import com.fei.firstproject.inter.OnItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Fei on 2017/9/12.
 */

public class HotQuestionAdapter extends RecyclerView.Adapter<HotQuestionAdapter.HotQuestionViewHolder> {

    private Context mContext;
    private List<HotQuestionEntity> hotQuestionEntityList;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setHotQuestionEntityList(List<HotQuestionEntity> hotQuestionEntityList) {
        this.hotQuestionEntityList = hotQuestionEntityList;
    }

    public HotQuestionAdapter(Context mContext, List<HotQuestionEntity> hotQuestionEntityList) {
        this.mContext = mContext;
        this.hotQuestionEntityList = hotQuestionEntityList;
    }

    @Override
    public HotQuestionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_question, parent, false);
        return new HotQuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HotQuestionViewHolder holder, int position) {
        HotQuestionEntity hotQuestionEntity = hotQuestionEntityList.get(position);
        holder.tvName.setText(hotQuestionEntity.getUserName());
        holder.tvTitle.setText(hotQuestionEntity.getQuestion());
        String expertName = hotQuestionEntity.getExpertName();
        if (TextUtils.isEmpty(expertName)) {
            holder.tvAnswer.setText("还没有人回答");
        } else {
            holder.tvAnswer.setText(expertName + "已回答");
        }
        String expertorConfTime = hotQuestionEntity.getExpertorConfTime();
        if (!TextUtils.isEmpty(expertorConfTime) && expertorConfTime.length() > 17) {
            holder.tvDate.setText(expertorConfTime.substring(0, 16));
        } else {
            holder.tvDate.setText(expertorConfTime);
        }
    }

    @Override
    public int getItemCount() {
        return hotQuestionEntityList.size();
    }

    class HotQuestionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_answer)
        TextView tvAnswer;
        @BindView(R.id.tv_date)
        TextView tvDate;

        public HotQuestionViewHolder(View itemView) {
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
