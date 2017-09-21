package com.fei.firstproject.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.fei.firstproject.R;
import com.fei.firstproject.inter.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by Administrator on 2017/9/21.
 */

public class PhotoPagerAdapter extends PagerAdapter {

    private Context mContext;
    private List<String> pics;
    private List<View> views = new ArrayList<>();
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public PhotoPagerAdapter(Context mContext, List<String> pics) {
        this.mContext = mContext;
        this.pics = pics;
    }

    @Override
    public int getCount() {
        return pics.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = null;
        if (views.size() >= position + 1) {
            view = views.get(position);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.view_photo, null);
            final ProgressBar pbLoading = (ProgressBar) view.findViewById(R.id.pb_loading);
            final PhotoView photoView = (PhotoView) view.findViewById(R.id.photoView);
            pbLoading.setVisibility(View.VISIBLE);
            photoView.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .load(pics.get(position))
                    .into(photoView);
            photoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(v);
                    }
                }
            });
            views.add(view);
        }
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
