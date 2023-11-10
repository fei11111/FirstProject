package com.fei.firstproject.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.fei.firstproject.R;
import com.fei.firstproject.entity.UnSolveQuestionEntity;
import com.fei.firstproject.inter.OnItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Fei on 2017/9/12.
 */

public class UnSolveQuestionAdapter extends RecyclerView.Adapter<UnSolveQuestionAdapter.HotQuestionViewHolder> {

    private Context mContext;
    private List<UnSolveQuestionEntity> unSolveQuestionEntities;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setUnSolveQuestionEntities(List<UnSolveQuestionEntity> unSolveQuestionEntities) {
        this.unSolveQuestionEntities = unSolveQuestionEntities;
    }

    public UnSolveQuestionAdapter(Context mContext, List<UnSolveQuestionEntity> unSolveQuestionEntities) {
        this.mContext = mContext;
        this.unSolveQuestionEntities = unSolveQuestionEntities;
    }

    @Override
    public HotQuestionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_question, parent, false);
        return new HotQuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HotQuestionViewHolder holder, int position) {
        UnSolveQuestionEntity unSolveQuestionEntity = unSolveQuestionEntities.get(position);
        holder.tvName.setVisibility(View.GONE);
        holder.tvTitle.setText(unSolveQuestionEntity.getQuestion());
        holder.tvAnswer.setVisibility(View.GONE);
        String questionTime = unSolveQuestionEntity.getQuestionTime();
        if (!TextUtils.isEmpty(questionTime) && questionTime.length() > 17) {
            holder.tvDate.setText(questionTime.substring(0, 16));
        } else {
            holder.tvDate.setText(questionTime);
        }
    }

    @Override
    public int getItemCount() {
        return unSolveQuestionEntities.size();
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
