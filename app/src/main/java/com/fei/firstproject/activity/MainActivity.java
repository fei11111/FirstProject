package com.fei.firstproject.activity;

import android.Manifest;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

import com.fei.firstproject.R;
import com.fei.firstproject.adapter.FragmentAdapter;
import com.fei.firstproject.dialog.CustomeProgressDialog;
import com.fei.firstproject.fragment.MainFragment;
import com.fei.firstproject.fragment.MeFragment;
import com.fei.firstproject.fragment.ShoppingCartFragment;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnPageChange;

public class MainActivity extends BaseActivity {

    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.vp_main)
    ViewPager vpMain;
    @BindView(R.id.ll_bottom_main)
    LinearLayout llBottomMain;
    @BindView(R.id.ll_bottom_shopping_cart)
    LinearLayout llBottomShoppingCart;
    @BindView(R.id.ll_bottom_me)
    LinearLayout llBottomMe;

    private static final int REQUEST_CODE_1 = 100;

    @Override
    public void requestPermissionsBeforeInit() {

        checkPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_1);
    }

    @Override
    public void permissionsDeniedCallBack(int requestCode) {
        showMissingPermissionDialog("需要访问存储权限");
    }

    @Override
    public void permissionsGrantCallBack(int requestCode) {

    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void init(Bundle savedInstanceState) {
        initViewPager();
    }

    private void initViewPager() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new MainFragment());
        fragments.add(new ShoppingCartFragment());
        fragments.add(new MeFragment());
        vpMain.setAdapter(new FragmentAdapter(getSupportFragmentManager(), fragments));
        llBottomMain.setSelected(true);
    }

    @OnClick(R.id.fab)
    void login(View view) {
        CustomeProgressDialog dialog = new CustomeProgressDialog(this);
        dialog.show();
    }

    @OnPageChange(value = R.id.vp_main, callback = OnPageChange.Callback.PAGE_SELECTED)
    void pageChange(int select) {
        resetAllState();
        switch (select) {
            case 0:
                llBottomMain.setSelected(true);
                break;
            case 1:
                llBottomShoppingCart.setSelected(true);
                break;
            case 2:
                llBottomMe.setSelected(true);
                break;
        }
    }

    @OnClick({R.id.ll_bottom_main, R.id.ll_bottom_shopping_cart, R.id.ll_bottom_me})
    void bottom(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ll_bottom_main:
                vpMain.setCurrentItem(0);
                break;
            case R.id.ll_bottom_shopping_cart:
                vpMain.setCurrentItem(1);
                break;
            case R.id.ll_bottom_me:
                vpMain.setCurrentItem(2);
                break;
        }
    }

    private void resetAllState() {
        llBottomMain.setSelected(false);
        llBottomShoppingCart.setSelected(false);
        llBottomMe.setSelected(false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
