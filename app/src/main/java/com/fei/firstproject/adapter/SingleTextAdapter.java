package com.fei.firstproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.fei.firstproject.R;
import com.fei.firstproject.widget.FocuseTextView;

import java.util.List;

/**
 * Created by Administrator on 2017/9/1.
 */

public class SingleTextAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> names;

    public SingleTextAdapter(Context mContext, List<String> names) {
        this.mContext = mContext;
        this.names = names;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_single_text, parent, false);
            holder.tvName = convertView.findViewById(R.id.tv_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvName.setText(names.get(position));

        return convertView;
    }

    class ViewHolder {
        FocuseTextView tvName;
    }
}
