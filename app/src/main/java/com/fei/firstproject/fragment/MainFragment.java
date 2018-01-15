package com.fei.firstproject.fragment;

import android.Manifest;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.fei.banner.Banner;
import com.fei.firstproject.R;
import com.fei.firstproject.activity.ExpertiseClinicActivity;
import com.fei.firstproject.activity.FieldManageActivity;
import com.fei.firstproject.activity.MarketInfoActivity;
import com.fei.firstproject.activity.ProductLibActivity;
import com.fei.firstproject.activity.RecommendPlanActivity;
import com.fei.firstproject.adapter.NcwAdapter;
import com.fei.firstproject.adapter.RecommendPlanAdapter;
import com.fei.firstproject.config.AppConfig;
import com.fei.firstproject.entity.BaseEntity;
import com.fei.firstproject.entity.NcwEntity;
import com.fei.firstproject.entity.RecommendEntity;
import com.fei.firstproject.http.BaseObserver;
import com.fei.firstproject.http.BaseWithoutBaseEntityObserver;
import com.fei.firstproject.http.factory.RetrofitFactory;
import com.fei.firstproject.image.GlideImageLoader;
import com.fei.firstproject.inter.OnItemClickListener;
import com.fei.firstproject.utils.LocationUtils;
import com.fei.firstproject.utils.LogUtils;
import com.fei.firstproject.utils.Utils;
import com.fei.firstproject.web.WebActivity;
import com.fei.firstproject.widget.NoScrollRecyclerView;
import com.fei.firstproject.widget.PartHeadView;
import com.fei.firstproject.widget.TextSwitchView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
    @BindView(R.id.rv_ncw)
    NoScrollRecyclerView rvNcw;
    @BindView(R.id.ll_ncw)
    LinearLayoutCompat llNcw;
    @BindView(R.id.rv_recommend_plan)
    NoScrollRecyclerView rvRecommendPlan;
    @BindView(R.id.ll_recommend_plan)
    LinearLayoutCompat llRecommendPlan;
    @BindView(R.id.sv_ncw)
    PartHeadView svNcw;
    @BindView(R.id.sv_recommend_plan)
    PartHeadView svRecommendPlan;
    @BindView(R.id.ll_head_line)
    LinearLayout ll_head_line;

    private List<String> imageUrls = new ArrayList<>();
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private RecommendPlanAdapter recommendPlanAdapter;
    private NcwAdapter ncwAdapter;
    private static final int REQUEST_PERMISSION_CODE_LOCATION = 100;
    private LocationUtils locationUtils;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        recommendPlanAdapter = null;
        ncwAdapter = null;
    }

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
    public void permissionsDeniedCallBack(int requestCode) {
        if (requestCode == REQUEST_PERMISSION_CODE_LOCATION) {
            showMissingPermissionDialog("在设置-应用-就是帅-权限中开启定位权限，才能获取天气", REQUEST_PERMISSION_CODE_LOCATION);
        }
    }

    @Override
    public void permissionsGrantCallBack(int requestCode) {
        if (requestCode == REQUEST_PERMISSION_CODE_LOCATION) {
            locationUtils = LocationUtils.getInstance(activity.getApplicationContext());
            locationUtils.setOnLocationCallBackListener(onLocationCallBackListener);
            locationUtils.startLocation();
        }
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
        initPermission();
        initMenu();
        //重力感应
        initSensor();
    }

    private LocationUtils.OnLocationCallBackListener onLocationCallBackListener = new LocationUtils.OnLocationCallBackListener() {
        @Override
        public void onSuccess(AMapLocation aMapLocation) {
            WeatherSearchQuery mquery = new WeatherSearchQuery(aMapLocation.getCity(), WeatherSearchQuery.WEATHER_TYPE_LIVE);
            WeatherSearch mweathersearch = new WeatherSearch(activity);
            mweathersearch.setOnWeatherSearchListener(onWeatherSearchListener);
            mweathersearch.setQuery(mquery);
            mweathersearch.searchWeatherAsyn(); //异步搜索
        }

        @Override
        public void onFail() {

        }
    };

    private WeatherSearch.OnWeatherSearchListener onWeatherSearchListener = new WeatherSearch.OnWeatherSearchListener() {
        @Override
        public void onWeatherLiveSearched(LocalWeatherLiveResult weatherLiveResult, int rCode) {
            if (rCode == 1000) {
                if (weatherLiveResult != null && weatherLiveResult.getLiveResult() != null) {
                    LocalWeatherLive weatherlive = weatherLiveResult.getLiveResult();
                    String[] resources = {weatherlive.getCity(),
                            weatherlive.getWeather() + " " + weatherlive.getTemperature() + "℃",
                            "湿度 " + weatherlive.getHumidity() + "%",
                            weatherlive.getWindDirection() + "风  " + weatherlive.getWindPower() + "级"};
                    initSwitch(resources);
                }
            }
        }

        @Override
        public void onWeatherForecastSearched(LocalWeatherForecastResult localWeatherForecastResult, int i) {

        }
    };

    private void initPermission() {
        checkPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_PERMISSION_CODE_LOCATION);
    }

    @Override
    public void initRequest() {
        //农财网
        getNcw();
        //推荐方案
        getRecommendPlan();
    }

    public void getRecommendPlan() {
        //province=&county=&city=&
        String searchWord = activity.getAppHeadView().getEtSearchText();
        Map<String, String> map = new HashMap<>();
        map.put("searchWord", searchWord);
        map.put("pageSize", "3");
        map.put("currentPage", "1");
        Observable<BaseEntity<List<RecommendEntity>>> recommendPlan =
                RetrofitFactory.getBtPlantWeb().getRecommendPlan(map);
        recommendPlan.compose(this.<BaseEntity<List<RecommendEntity>>>createTransformer(false))
                .subscribe(new BaseObserver<List<RecommendEntity>>(activity) {
                    @Override
                    protected void onHandleSuccess(final List<RecommendEntity> recommendEntities) {
                        refreshLayout.finishRefresh();
                        if (recommendEntities != null && recommendEntities.size() > 0) {
                            llRecommendPlan.setVisibility(View.VISIBLE);
                            if (recommendPlanAdapter == null) {
                                setRecycleViewSetting(rvRecommendPlan);
                                recommendPlanAdapter = new RecommendPlanAdapter(activity, recommendEntities, 3);
                                rvRecommendPlan.setAdapter(recommendPlanAdapter);
                                recommendPlanAdapter.setOnItemClickListener(new OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View view) {
                                        int position = rvRecommendPlan.getChildAdapterPosition(view);
                                        RecommendEntity recommendEntity = recommendPlanAdapter.getRecommendEntities().get(position);
                                        String url = AppConfig.PLANT_DESC_URL + "?id=" + recommendEntity.getId() + "&version="
                                                + recommendEntity.getVersion()
                                                + "&cropCode=" + recommendEntity.getCropCode()
                                                + "&categoryCode=" + recommendEntity.getCropCategoryCode()
                                                + "&title=" + recommendEntity.getTitle();
                                        Intent intent = new Intent(activity, WebActivity.class);
                                        intent.putExtra("url", url);
                                        startActivityWithoutCode(intent);
                                    }
                                });
                            } else {
                                recommendPlanAdapter.setRecommendEntities(recommendEntities);
                                recommendPlanAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    protected void onHandleError(String msg) {
                        super.onHandleError(msg);
                        refreshLayout.finishRefresh();
                    }
                });
    }

    private void getNcw() {
        final Observable<List<NcwEntity>> ncw = RetrofitFactory.getNcw().getNcw();
        ncw.compose(this.<List<NcwEntity>>createTransformer(true)).subscribe(new BaseWithoutBaseEntityObserver<List<NcwEntity>>(activity) {
            @Override
            protected void onHandleSuccess(final List<NcwEntity> ncwEntities) {
                refreshLayout.finishRefresh();
                if (ncwEntities != null && ncwEntities.size() > 0) {
                    dismissLoading();
                    refreshLayout.setVisibility(View.VISIBLE);
                    llNcw.setVisibility(View.VISIBLE);
                    if (ncwAdapter == null) {
                        setRecycleViewSetting(rvNcw);
                        ncwAdapter = new NcwAdapter(activity, ncwEntities);
                        ncwAdapter.setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(View view) {
                                int position = rvNcw.getChildAdapterPosition(view);
                                String url = ncwAdapter.getNcwEntities().get(position).getUrl();
                                Intent intent = new Intent(activity, WebActivity.class);
                                intent.putExtra("url", url + "&a=show");
                                startActivityWithoutCode(intent);
                            }
                        });
                        rvNcw.setAdapter(ncwAdapter);
                    } else {
                        ncwAdapter.setNcwEntities(ncwEntities);
                        ncwAdapter.notifyDataSetChanged();
                    }
                } else {
                    showNoDataView();
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                refreshLayout.finishRefresh();
                showRequestErrorView();
            }
        });
    }

    private void setRecycleViewSetting(RecyclerView recycleViewSetting) {
        LinearLayoutManager manager = new LinearLayoutManager(activity);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(activity, LinearLayout.VERTICAL);
        recycleViewSetting.setLayoutManager(manager);
        recycleViewSetting.addItemDecoration(dividerItemDecoration);
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

    private View createItemView(int tabWidth, final String menuName, int drawable) {
        View itemView = LayoutInflater.from(activity).inflate(R.layout.item_head_menu, null);
        ImageView iv_head_menu = ButterKnife.findById(itemView, R.id.iv_head_menu);
        TextView tv_head_menu = ButterKnife.findById(itemView, R.id.tv_head_menu);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(tabWidth, LinearLayout.LayoutParams.MATCH_PARENT);
        itemView.setLayoutParams(params);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (menuName.equals(getResources().getString(R.string.tjgl))) {
                    startActivityWithoutCode(new Intent(activity, FieldManageActivity.class));
                } else if (menuName.equals(getResources().getString(R.string.zjzs))) {
                    startActivityWithoutCode(new Intent(activity, ExpertiseClinicActivity.class));
                } else if (menuName.equals(getResources().getString(R.string.ncsc))) {

                } else if (menuName.equals(getResources().getString(R.string.nydz))) {

                } else if (menuName.equals(getResources().getString(R.string.cpk))) {
                    startActivityWithoutCode(new Intent(activity, ProductLibActivity.class));
                } else if (menuName.equals(getResources().getString(R.string.schq))) {
                    startActivityWithoutCode(new Intent(activity, MarketInfoActivity.class));
                } else if (menuName.equals(getResources().getString(R.string.cl))) {

                }
            }
        });
        iv_head_menu.setImageResource(drawable);
        tv_head_menu.setText(menuName);
        return itemView;
    }

    private void initSwitch(String[] resource) {
        ll_head_line.setVisibility(View.VISIBLE);
        tsv.setResources(resource);
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

    @OnClick(R.id.sv_ncw)
    void clickNcw(View view) {
        Intent intent = new Intent(activity, WebActivity.class);
        intent.putExtra("url", RetrofitFactory.NCW_URL);
        startActivityWithoutCode(intent);
    }

    @OnClick(R.id.sv_recommend_plan)
    void clickRecommendPlan(View view) {
        startActivityWithoutCode(new Intent(activity, RecommendPlanActivity.class));
    }
}
