package com.fei.firstproject.fragment;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.fei.banner.Banner;
import com.fei.firstproject.R;
import com.fei.firstproject.image.GlideImageLoader;
import com.fei.firstproject.widget.TextSwitchView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/7/29.
 */

public class MainFragment extends BaseFragment {

    @BindView(R.id.banner)
    Banner banner;
    @BindView(R.id.tsv)
    TextSwitchView tsv;
    @BindView(R.id.ll_menu)
    LinearLayout llMenu;

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
    public void permissionDialogDismiss(int requestCode) {

    }

    @Override
    public int getContentViewResId() {
        return R.layout.fragment_main;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        initBanner();
        tsv.setTimeDelay(2000);
    }

    private void initBanner() {
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
//
//        if (ids.get(i).equals("83")) {
//            bt.setCompoundDrawablesWithIntrinsicBounds(null, getResources()
//                    .getDrawable(R.drawable.icon_zjzs), null, null);
////                    } else if (tabs.get(i).equals("田间管理")) {
//        } else if (ids.get(i).equals("82")) {
//            bt.setCompoundDrawablesWithIntrinsicBounds(null, getResources()
//                    .getDrawable(R.drawable.icon_tjgl), null, null);
////                    } else if (tabs.get(i).equals("农业定制")) {
//        } else if (ids.get(i).equals("84")) {
//            bt.setCompoundDrawablesWithIntrinsicBounds(null, getResources()
//                    .getDrawable(R.drawable.icon_nydz), null, null);
////                    } else if (tabs.get(i).equals("测量")) {
//        } else if (ids.get(i).equals("87")) {
//            bt.setCompoundDrawablesWithIntrinsicBounds(null, getResources()
//                    .getDrawable(R.drawable.icon_setting), null, null);
////                    } else if (tabs.get(i).equals("市场行情")) {
//        } else if (ids.get(i).equals("103")) {
//            bt.setCompoundDrawablesWithIntrinsicBounds(null, getResources()
//                    .getDrawable(R.drawable.icon_schx), null, null);
////                    } else if (tabs.get(i).equals("农财商城")) {
//        } else if (ids.get(i).equals("109")) {
//            bt.setCompoundDrawablesWithIntrinsicBounds(null, getResources()
//                    .getDrawable(R.drawable.icon_ds), null, null);
////                    } else if (tabs.get(i).equals("产品库")) {
//        } else if (ids.get(i).equals("124")) {
//            bt.setCompoundDrawablesWithIntrinsicBounds(null, getResources()
//                    .getDrawable(R.drawable.icon_productlibrary), null, null);
//        }
    }
}
