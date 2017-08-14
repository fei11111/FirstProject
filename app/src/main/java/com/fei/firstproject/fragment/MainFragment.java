package com.fei.firstproject.fragment;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fei.banner.Banner;
import com.fei.firstproject.R;
import com.fei.firstproject.adapter.NcwAdapter;
import com.fei.firstproject.adapter.RecommandPlanAdapter;
import com.fei.firstproject.entity.BaseEntity;
import com.fei.firstproject.entity.NcwEntity;
import com.fei.firstproject.entity.RecommendEntity;
import com.fei.firstproject.http.BaseObserver;
import com.fei.firstproject.http.BaseWithoutBaseEntityObserver;
import com.fei.firstproject.http.RxSchedulers;
import com.fei.firstproject.http.factory.RetrofitFactory;
import com.fei.firstproject.image.GlideImageLoader;
import com.fei.firstproject.utils.LogUtils;
import com.fei.firstproject.utils.Utils;
import com.fei.firstproject.widget.MyHorizontalScrollView;
import com.fei.firstproject.widget.NoScrollListView;
import com.fei.firstproject.widget.TextSwitchView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;

import static android.content.Context.SENSOR_SERVICE;

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
    @BindView(R.id.hsv_main)
    MyHorizontalScrollView hsvMain;
    @BindView(R.id.lv_ncw)
    NoScrollListView lvNcw;
    @BindView(R.id.ll_ncw)
    LinearLayoutCompat llNcw;
    @BindView(R.id.lv_recommend_plan)
    NoScrollListView lvRecommendPlan;
    @BindView(R.id.ll_recommend_plan)
    LinearLayoutCompat llRecommendPlan;

    private List<String> imageUrls = new ArrayList<>();
    private SensorManager mSensorManager;
    private Sensor mSensor;

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(lsn, mSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(lsn);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

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
        LogUtils.i("tag", "main");
        initBanner();
        initSwitch();
        initMenu();
        //重力感应
        initSensor();
        initData();
    }

    private void initData() {
        //农财网
        initNcw();
        //推荐方案
        initRecommendPlan();
    }

    private void initRecommendPlan() {
        //province=&county=&city=&
        Map<String, String> map = new HashMap<>();
        map.put("province", "");
        map.put("county", "");
        map.put("city", "");
        Observable<BaseEntity<List<RecommendEntity>>> recommendPlan =
                RetrofitFactory.getBtPlantWeb().getRecommendPlan(map);
        recommendPlan
                .compose(RxSchedulers.compose(activity, this.<BaseEntity<List<RecommendEntity>>>bindToLifecycle()))
                .subscribe(new BaseObserver<List<RecommendEntity>>(activity) {
                    @Override
                    protected void onHandleSuccess(List<RecommendEntity> recommendEntities) {
                        if (recommendEntities != null && recommendEntities.size() > 0) {
                            llRecommendPlan.setVisibility(View.VISIBLE);
                            lvRecommendPlan.setAdapter(new RecommandPlanAdapter(activity, recommendEntities));
                        }
                    }
                });
    }

    private void initNcw() {
        Observable<List<NcwEntity>> ncw = RetrofitFactory.getNcw().getNcw();
        ncw.compose(RxSchedulers.compose(activity, this.<List<NcwEntity>>bindToLifecycle())).subscribe(new BaseWithoutBaseEntityObserver<List<NcwEntity>>(activity) {
            @Override
            protected void onHandleSuccess(List<NcwEntity> ncwEntities) {
                if (ncwEntities != null && ncwEntities.size() > 0) {
                    LogUtils.i("tag", ncwEntities.toString());
                    llNcw.setVisibility(View.VISIBLE);
                    lvNcw.setAdapter(new NcwAdapter(activity, ncwEntities));
                }
            }
        });
    }

    private void initSensor() {
        mSensorManager = (SensorManager) activity.getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    private void initMenu() {
        int screenWidth = Utils.getScreen(activity)[0];
        int tabWidth = screenWidth / 7 * 2;
        String[] menuNames = activity.getResources().getStringArray(R.array.list_menu_head_str);
        int[] menuDrawables = Utils.getDrawableByArray(activity, R.array.list_menu_head_drawable);
        for (int i = 0; i < menuNames.length; i++) {
            llMenu.addView(createItemView(tabWidth, menuNames[i], menuDrawables[i]));
        }
    }

    private View createItemView(int tabWidth, String menuName, int drawable) {
        View itemView = LayoutInflater.from(activity).inflate(R.layout.item_head_menu, null);
        ImageView iv_head_menu = ButterKnife.findById(itemView, R.id.iv_head_menu);
        TextView tv_head_menu = ButterKnife.findById(itemView, R.id.tv_head_menu);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(tabWidth, LinearLayout.LayoutParams.MATCH_PARENT);
        itemView.setLayoutParams(params);
        iv_head_menu.setImageResource(drawable);
        tv_head_menu.setText(menuName);
        return itemView;
    }

    private void initSwitch() {
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
    }

    SensorEventListener lsn = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[SensorManager.DATA_X];
            if (hsvMain != null) {
                if (x > 0 && x < 0.2f) return;
                hsvMain.scrollBy((int) (x), 0);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub
        }
    };

}
