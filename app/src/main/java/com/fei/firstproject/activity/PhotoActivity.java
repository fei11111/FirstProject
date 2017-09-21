package com.fei.firstproject.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.fei.firstproject.R;
import com.fei.firstproject.adapter.PhotoPagerAdapter;
import com.fei.firstproject.inter.OnItemClickListener;

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
    private int currentIndex = 0;

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
        photoPagerAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view) {
                onBackPressed();
            }
        });
        vpPhoto.setAdapter(photoPagerAdapter);
    }

    private void initPage() {
        tvPage.setText((currentIndex + 1) + "/" + pics.size());
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
