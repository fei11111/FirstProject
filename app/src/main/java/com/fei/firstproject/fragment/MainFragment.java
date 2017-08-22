package com.fei.firstproject.fragment;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fei.banner.Banner;
import com.fei.firstproject.R;
import com.fei.firstproject.activity.MainActivity;
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
import com.fei.firstproject.widget.NoScrollListView;
import com.fei.firstproject.widget.TextSwitchView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
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
    HorizontalScrollView hsvMain;
    @BindView(R.id.lv_ncw)
    NoScrollListView lvNcw;
    @BindView(R.id.ll_ncw)
    LinearLayout llNcw;
    @BindView(R.id.lv_recommend_plan)
    NoScrollListView lvRecommendPlan;
    @BindView(R.id.ll_recommend_plan)
    LinearLayout llRecommendPlan;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.pb_loading)
    ProgressBar pbLoading;
    @BindView(R.id.ll_no_data)
    LinearLayout llNoData;
    @BindView(R.id.btn_request_error)
    Button btnRequestError;
    @BindView(R.id.ll_request_error)
    LinearLayout llRequestError;
    @BindView(R.id.rl_default)
    RelativeLayout rlDefault;
    Unbinder unbinder;

    private List<String> imageUrls = new ArrayList<>();
    private SensorManager mSensorManager;
    private Sensor mSensor;

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) {
            stopBanner();
            mSensorManager.unregisterListener(lsn);
        } else {
            startBanner();
            mSensorManager.registerListener(lsn, mSensor, SensorManager.SENSOR_DELAY_GAME);
        }
        super.onHiddenChanged(hidden);
    }

    private void startBanner() {
        //banner设置方法全部调用完毕时最后调用
        if (banner != null) {
            banner.startAutoPlay();
        }
    }

    private void stopBanner() {
        if (banner != null) {
            banner.stopAutoPlay();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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
        initListener();
        initBanner();
        initSwitch();
        initMenu();
        //重力感应
        initSensor();
        initData();
    }

    private void initListener() {
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                //推荐方案
                getRecommendPlan();
            }
        });
    }

    private void initData() {
        //农财网
        getNcw();
        //推荐方案
        getRecommendPlan();
    }

    private void getRecommendPlan() {
        //province=&county=&city=&
        String searchWord = ((MainActivity) activity).getAppHeadView().getEtSearchText();
        Map<String, String> map = new HashMap<>();
        map.put("province", "");
        map.put("county", "");
        map.put("city", "");
        map.put("searchWord", searchWord);
        Observable<BaseEntity<List<RecommendEntity>>> recommendPlan =
                RetrofitFactory.getBtPlantWeb().getRecommendPlan(map);
        recommendPlan
                .compose(RxSchedulers.compose(activity, this.<BaseEntity<List<RecommendEntity>>>bindToLifecycle(), new RxSchedulers.OnConnectError() {
                    @Override
                    public void onError() {

                    }
                }))
                .subscribe(new BaseObserver<List<RecommendEntity>>(activity) {
                    @Override
                    protected void onHandleSuccess(List<RecommendEntity> recommendEntities) {
                        refreshLayout.finishRefresh();
                        if (recommendEntities != null && recommendEntities.size() > 0) {
                            llRecommendPlan.setVisibility(View.VISIBLE);
                            lvRecommendPlan.setAdapter(new RecommandPlanAdapter(activity, recommendEntities));
                        }
                    }
                });
    }

    private void getNcw() {
        Observable<List<NcwEntity>> ncw = RetrofitFactory.getNcw().getNcw();
        ncw.compose(RxSchedulers.compose(activity, this.<List<NcwEntity>>bindToLifecycle(), new RxSchedulers.OnConnectError() {
            @Override
            public void onError() {
                pbLoading.setVisibility(View.GONE);
                llRequestError.setVisibility(View.VISIBLE);
            }
        })).subscribe(new BaseWithoutBaseEntityObserver<List<NcwEntity>>(activity) {
            @Override
            protected void onHandleSuccess(List<NcwEntity> ncwEntities) {
                pbLoading.setVisibility(View.GONE);
                if (ncwEntities != null && ncwEntities.size() > 0) {
                    LogUtils.i("tag", ncwEntities.toString());
                    llNcw.setVisibility(View.VISIBLE);
                    lvNcw.setAdapter(new NcwAdapter(activity, ncwEntities));
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                pbLoading.setVisibility(View.GONE);
                llRequestError.setVisibility(View.VISIBLE);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }
}
