package com.fei.firstproject.fragment;

import android.os.Bundle;

import com.fei.banner.Banner;
import com.fei.firstproject.R;
import com.fei.firstproject.image.GlideImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/7/29.
 */

public class MainFragment extends BaseFragment {

    @BindView(R.id.banner)
    Banner banner;

    private List<String> imageUrls = new ArrayList<>();

    @Override
    public void requestPermissionsBeforeInit() {

    }

    @Override
    public void permissionsDeniedCallBack(int requestCode) {

    }

    @Override
    public void permissionsGrantCallBack(int requestCode) {

    }

    @Override
    public int getContentViewResId() {
        return R.layout.fragment_main;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        //设置图片集合
        imageUrls.clear();
        imageUrls.add("http://imglf0.ph.126.net/1EnYPI5Vzo2fCkyy2GsJKg==/2829667940890114965.jpg");
        imageUrls.add("http://exp.cdn-hotels.com/hotels/4000000/3900000/3893200/3893187/3893187_25_y.jpg");
        imageUrls.add("http://s3.lvjs.com.cn/trip/original/20140818131550_1792868513.jpg");
        banner.setImages(imageUrls);
        //banner设置方法全部调用完毕时最后调用
        banner.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
