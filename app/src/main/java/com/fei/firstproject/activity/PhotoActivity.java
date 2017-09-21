package com.fei.firstproject.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.fei.firstproject.R;
import com.fei.firstproject.adapter.PhotoPagerAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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
        vpPhoto.setAdapter(new PhotoPagerAdapter(this, pics));
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
