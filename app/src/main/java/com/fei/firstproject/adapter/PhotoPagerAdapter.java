package com.fei.firstproject.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.fei.firstproject.R;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Administrator on 2017/9/21.
 */

public class PhotoPagerAdapter extends PagerAdapter {

    private Context mContext;
    private List<String> pics;
    private List<View> views = new ArrayList<>();
    private OnPhotoTpListener onPhotoTpListener;

    public void setOnPhotoTpListener(OnPhotoTpListener onPhotoTpListener) {
        this.onPhotoTpListener = onPhotoTpListener;
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
            PhotoView photoView = (PhotoView) view.findViewById(R.id.photoView);
            photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float x, float y) {
                    if (onPhotoTpListener != null) {
                        onPhotoTpListener.onPhotoTap();
                    }
                }
            });
            Glide.with(mContext)
                    .load(pics.get(position))
                    .into(photoView);
            views.add(view);
        }
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public interface OnPhotoTpListener {
        void onPhotoTap();
    }
}
