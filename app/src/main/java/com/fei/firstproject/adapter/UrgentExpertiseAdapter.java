package com.fei.firstproject.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fei.banner.view.BannerViewPager;
import com.fei.firstproject.R;
import com.fei.firstproject.entity.UrgentExpertEntity;
import com.scwang.smartrefresh.header.material.CircleImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/29.
 */

public class UrgentExpertiseAdapter extends PagerAdapter {

    private Context mContext;
    private List<UrgentExpertEntity> expertEntities;
    private BannerViewPager vpPro;
    private List<View> views = new ArrayList<>();

    public void setExpertEntities(List<UrgentExpertEntity> expertEntities) {
        this.expertEntities = expertEntities;
    }

    public List<UrgentExpertEntity> getExpertEntities() {
        return expertEntities;
    }

    public UrgentExpertiseAdapter(Context mContext, List<UrgentExpertEntity> expertEntities, BannerViewPager vpPro) {
        this.mContext = mContext;
        this.expertEntities = expertEntities;
        this.vpPro = vpPro;
    }

    @Override
    public int getCount() {
        return expertEntities.size();
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
            view = LayoutInflater.from(mContext).inflate(R.layout.item_urgent_expertise, null);
            TextView tvName = (TextView) view.findViewById(R.id.tv_name);
            TextView tvServiceDuration = (TextView) view.findViewById(R.id.tv_service_duration);
            TextView tvSpecCrop = (TextView) view.findViewById(R.id.tv_spec_crop);
            TextView tvSpecSkill = (TextView) view.findViewById(R.id.tv_spec_skill);
            RatingBar rbStar = (RatingBar) view.findViewById(R.id.rb_star);
            CircleImageView ivIcon = (CircleImageView) view.findViewById(R.id.iv_icon);
            UrgentExpertEntity expertEntity = expertEntities.get(position);
            tvName.setText(expertEntity.getUserName());
            tvServiceDuration.setText(expertEntity.getServiceTime());
            tvSpecCrop.setText(expertEntity.getPlantDesc());
            tvSpecSkill.setText(expertEntity.getExpertise());
            String levelStart = expertEntity.getLevelStart();
            if (!TextUtils.isEmpty(levelStart)) {
                rbStar.setRating(Float.valueOf(levelStart));
            } else {
                rbStar.setRating(0);
            }
            Glide.with(mContext)
                    .load("http://218.18.114.97:3392/btFile" + expertEntity.getImgPath())
                    .placeholder(R.drawable.ic_app)
                    .crossFade()
                    .error(R.drawable.ic_pic_error)
                    .into(ivIcon);
            views.add(view);
        }
        container.addView(view);
        vpPro.setObjectForPosition(view, position);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}
