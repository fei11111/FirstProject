package com.fei.firstproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.fei.firstproject.R;
import com.luck.picture.lib.photoview.OnPhotoTapListener;
import com.luck.picture.lib.photoview.PhotoView;

import java.util.ArrayList;
import java.util.List;
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
            PhotoView photoView = view.findViewById(R.id.photoView);
            photoView.setOnPhotoTapListener(new OnPhotoTapListener() {
                @Override
                public void onPhotoTap(ImageView view, float x, float y) {
                    if (onPhotoTpListener != null) {
                        onPhotoTpListener.onPhotoTap();
                    }
                }
            });
            photoView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (onPhotoTpListener != null) {
                        onPhotoTpListener.onLongTap(position);
                    }
                    return false;
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

        void onLongTap(int position);
    }
}
