package com.fei.firstproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.fei.firstproject.R;
import com.fei.firstproject.entity.ObjectEntity;

import java.util.List;

public class BottomExpandAdapter extends BaseExpandableListAdapter {

    private List<ObjectEntity> objectEntityList;
    private Context mContext;

    public BottomExpandAdapter(Context mContext, List<ObjectEntity> objectEntityList) {
        this.objectEntityList = objectEntityList;
        this.mContext = mContext;
    }

    @Override
    public int getGroupCount() {
        return objectEntityList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return objectEntityList.get(groupPosition).getObjects().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return objectEntityList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return objectEntityList.get(groupPosition).getObjects().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_expand_list, parent, false);
            holder.tv = convertView.findViewById(R.id.tv_item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv.setText(objectEntityList.get(groupPosition).getName());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_expand_list, parent, false);
            holder.tv = convertView.findViewById(R.id.tv_item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv.setText(objectEntityList.get(groupPosition).getObjects().get(childPosition).getName());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void setObjectEntityList(List<ObjectEntity> objectEntityList) {
        this.objectEntityList = objectEntityList;
    }

    class ViewHolder {
        TextView tv;
    }
}
