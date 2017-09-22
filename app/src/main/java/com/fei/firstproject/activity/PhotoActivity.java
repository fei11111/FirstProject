package com.fei.firstproject.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.fei.firstproject.R;
import com.fei.firstproject.adapter.PhotoPagerAdapter;

import java.util.List;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/9/21.
 */

public class PhotoActivity extends BaseActivity {

    @BindView(R.id.vp_photo)
    ViewPager vpPhoto;
    @BindView(R.id.tv_page)
    TextView tvPage;

    private List<String> pics;

    @Override
    public void permissionsDeniedCallBack(int requestCode) {

    }

    @Override
    public void permissionsGrantCallBack(int requestCode) {

    }

    @Override
    public void permissionDialogDismiss(int requestCode) {

    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_photo;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        initData();
        initPage();
        initViewPager();
    }

    private void initViewPager() {
        PhotoPagerAdapter photoPagerAdapter = new PhotoPagerAdapter(this, pics);
        vpPhoto.setAdapter(photoPagerAdapter);
        initListener(photoPagerAdapter);
    }

    private void initListener(PhotoPagerAdapter photoPagerAdapter) {
        photoPagerAdapter.setOnPhotoTpListener(new PhotoPagerAdapter.OnPhotoTpListener() {
            @Override
            public void onPhotoTap() {
                onBackPressed();
            }
        });
        vpPhoto.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tvPage.setText((position + 1) + "/" + pics.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initPage() {
        tvPage.setText("1/" + pics.size());
    }

    private void initData() {
        pics = getIntent().getStringArrayListExtra("pics");
        if (pics == null) {
            onBackPressed();
        }
    }

    @Override
    public void initRequest() {

    }

}
