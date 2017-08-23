package com.fei.firstproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fei.firstproject.R;
import com.fei.firstproject.entity.NcwEntity;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/8/14.
 */

public class NcwAdapter extends BaseAdapter {

    private List<NcwEntity> ncwEntities;
    private Context mContext;

    public NcwAdapter(Context mContext,List<NcwEntity> ncwEntities) {
        this.mContext = mContext;
        this.ncwEntities = ncwEntities;
    }

    public void setNcwEntities(List<NcwEntity> ncwEntities) {
        this.ncwEntities = ncwEntities;
    }

    @Override
    public int getCount() {
        return ncwEntities.size() >= 3 ? 3 : ncwEntities.size();
    }

    @Override
    public Object getItem(int i) {
        return ncwEntities.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_ncw, viewGroup, false);
            holder.iv_ncw = ButterKnife.findById(view, R.id.iv_ncw);
            holder.tv_ncw_title = ButterKnife.findById(view, R.id.tv_ncw_title);
            holder.tv_nwc_desp = ButterKnife.findById(view, R.id.tv_nwc_desp);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        NcwEntity ncwEntity = ncwEntities.get(i);
        holder.tv_ncw_title.setText(ncwEntity.getTitle());
        holder.tv_nwc_desp.setText(ncwEntity.getDescription().replaceAll("\\s*", ""));
        Glide.with(mContext)
                .load(ncwEntity.getThumb())
                .placeholder(R.drawable.ic_app)
                .crossFade()
                .error(R.drawable.ic_pic_error)
                .into(holder.iv_ncw);
        return view;
    }

    class ViewHolder {
        ImageView iv_ncw;
        TextView tv_ncw_title;
        TextView tv_nwc_desp;
    }
}
