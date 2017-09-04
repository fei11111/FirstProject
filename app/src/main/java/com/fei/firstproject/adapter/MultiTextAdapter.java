package com.fei.firstproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

import com.fei.firstproject.R;

import java.util.List;

/**
 * Created by Administrator on 2017/9/1.
 */

public class MultiTextAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> names;
    private List<String> checkList;

    public MultiTextAdapter(Context mContext, List<String> names, List<String> checkList) {
        this.mContext = mContext;
        this.names = names;
        this.checkList = checkList;
    }

    public List<String> getCheckList() {
        return checkList;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    @Override
    public int getCount() {
        return names.size();
    }

    @Override
    public Object getItem(int position) {
        return names.get(position);
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_multi_text, parent, false);
            holder.tvName = (CheckedTextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String name = names.get(position);
        holder.tvName.setText(name);
        if (checkList.contains(name)) {
            holder.tvName.setChecked(true);
        } else {
            holder.tvName.setChecked(false);
        }
        holder.tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckedTextView ctv = (CheckedTextView) v;
                ((CheckedTextView) v).toggle();
                String s = ctv.getText().toString();
                if (ctv.isChecked()) {
                    checkList.add(s);
                } else {
                    checkList.remove(s);
                }
            }
        });
        return convertView;
    }

    class ViewHolder {
        CheckedTextView tvName;
    }
}
