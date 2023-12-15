package com.fei.firstproject.activity;

import android.os.Bundle;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.common.viewmodel.EmptyViewModel;
import com.fei.firstproject.R;
import com.fei.firstproject.databinding.ActivityMapBinding;
import com.fei.firstproject.utils.LogUtils;


/**
 * Created by Fei on 2017/9/4.
 */

public class MapActivity extends BaseProjectActivity<EmptyViewModel, ActivityMapBinding> {

    private AMap aMap;

    @Override
    protected void onResume() {
        mChildBinding.map.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mChildBinding.map.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mChildBinding.map.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mChildBinding.map.onSaveInstanceState(outState);
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
    public void initTitle() {
        setBackTitle(getString(R.string.confirm_address));
    }

    @Override
    public void initRequest() {

    }

    private void initListener() {
        aMap.setOnMyLocationChangeListener(location -> {
            Bundle extras = location.getExtras();
            String address = extras.getString("Address", "");
            LogUtils.i("tag", location.toString());
            final LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(latLng);
            aMap.moveCamera(cameraUpdate);
            mChildBinding.tvAddress.setText(address);
        });
    }

    private void initMap() {
        aMap = mChildBinding.map.getMap();
        UiSettings uiSettings = aMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(false);
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setLogoPosition(AMapOptions.LOGO_MARGIN_RIGHT);
        uiSettings.setScrollGesturesEnabled(true);
        MyLocationStyle myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
        myLocationStyle.strokeWidth(2);
        myLocationStyle.strokeColor(R.color.colorWhite);//设置定位蓝点精度圆圈的边框颜色的方法。
        myLocationStyle.radiusFillColor(R.color.colorBlue);
        myLocationStyle.showMyLocation(true);
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
    }

    @Override
    public void createObserver() {

    }

    @Override
    public void initViewAndData(Bundle savedInstanceState) {
        mChildBinding.map.onCreate(savedInstanceState);
        initMap();
        initListener();
    }
}
