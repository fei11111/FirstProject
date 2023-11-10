package com.fei.firstproject.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import androidx.recyclerview.widget.RecyclerView;

import com.fei.firstproject.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/1/15.
 */

public class SingleCheckAdapter extends RecyclerView.Adapter<SingleCheckAdapter.SingleViewHolder> {

    private Context mContext;
    private List<String> names;
    private OnItemCheckListener onItemCheckListener;

    public SingleCheckAdapter(Context mContext, List<String> names) {
        this.mContext = mContext;
        this.names = names;
        names.add(0, "全部");
    }

    @Override
    public SingleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_single_check, parent, false);
        return new SingleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SingleViewHolder holder,@SuppressLint("RecyclerView") final int position) {
        holder.ctv.setText(names.get(position));
        holder.ctv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckedTextView checkedTextView = (CheckedTextView) v;
                checkedTextView.toggle();
                if (onItemCheckListener != null) {
                    String s = names.get(position);
                    if (s.equals("全部")) {
                        s = "";
                    }
                    onItemCheckListener.onCheck(s);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    class SingleViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ctv)
        CheckedTextView ctv;

        public SingleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemCheckListener {
        void onCheck(String name);
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public void addNames(List<String> names) {
        if (this.names != null && names != null) {
            this.names.addAll(names);
        }
    }

    public void setOnItemCheckListener(OnItemCheckListener onItemCheckListener) {
        this.onItemCheckListener = onItemCheckListener;
    }
}
