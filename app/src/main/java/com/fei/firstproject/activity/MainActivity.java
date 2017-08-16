package com.fei.firstproject.activity;

import android.Manifest;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.fei.firstproject.R;
import com.fei.firstproject.config.AppConfig;
import com.fei.firstproject.fragment.MainFragment;
import com.fei.firstproject.fragment.MakeFragment;
import com.fei.firstproject.fragment.MeFragment;
import com.fei.firstproject.fragment.manager.FragmentInstanceManager;
import com.fei.firstproject.utils.LogUtils;
import com.fei.firstproject.utils.Utils;
import com.fei.firstproject.widget.AppHeadView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.ll_bottom_main)
    LinearLayout llBottomMain;
    @BindView(R.id.ll_bottom_make)
    LinearLayout llBottomMake;
    @BindView(R.id.ll_bottom_me)
    LinearLayout llBottomMe;
    @BindView(R.id.apv)
    AppHeadView apv;
    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.bottom_layout)
    LinearLayout bottomLayout;
    @BindView(R.id.fl_main_container)
    FrameLayout flMainContainer;

    private FragmentManager mFragmentManager;
    private MainFragment mainFragment;
    private MakeFragment makeFragment;
    private MeFragment meFragment;
    private static final int REQUEST_CODE_1 = 100;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void requestPermissionsBeforeInit() {
        checkPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_1);
    }

    @Override
    public void permissionsDeniedCallBack(int requestCode) {
        showMissingPermissionDialog("需要访问存储权限", requestCode);
    }

    @Override
    public void permissionsGrantCallBack(int requestCode) {

    }

    @Override
    public void permissionDialogDismiss(int requestCode) {
        if (requestCode == REQUEST_CODE_1) {
            Utils.showToast(this, "无法访问存储，将影响APP使用");
        }
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_main;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        initToolBar();
        mFragmentManager = getSupportFragmentManager();
        llBottomMain.performClick();
    }

    private void initToolBar() {
        if (AppConfig.ISLOGIN) {
            apv.setFlHeadLeftVisible(View.INVISIBLE);
        } else {
            apv.setFlHeadLeftVisible(View.VISIBLE);
        }
        initToolBarListener();
    }

    private void initToolBarListener() {
        apv.setOnLeftRightClickListener(new AppHeadView.OnLeftRightClickListener() {
            @Override
            public void onLeft(View view) {
                LogUtils.i("tag", "可点击");
            }

            @Override
            public void onRight(View view) {
                LogUtils.i("tag", "可点击");
            }
        });
    }

    @OnClick({R.id.ll_bottom_main, R.id.ll_bottom_make, R.id.ll_bottom_me})
    void clickBottom(View view) {
        int id = view.getId();
        resetAllState();
        switch (id) {
            case R.id.ll_bottom_main:
                llBottomMain.setSelected(true);
                setAppHeadViewSearchMode();
                if (mainFragment == null) {
                    mainFragment = (MainFragment) FragmentInstanceManager.getInstance().getFragmet(MainFragment.class);
                    switchFragment(mainFragment, true);
                } else {
                    switchFragment(mainFragment, false);
                }
                break;
            case R.id.ll_bottom_make:
                llBottomMake.setSelected(true);
                setAppHeadViewTitleMode(getResources().getString(R.string.make));
                if (makeFragment == null) {
                    makeFragment = (MakeFragment) FragmentInstanceManager.getInstance().getFragmet(MakeFragment.class);
                    switchFragment(makeFragment, true);
                } else {
                    switchFragment(makeFragment, false);
                }
                break;
            case R.id.ll_bottom_me:
                llBottomMe.setSelected(true);
                setAppHeadViewTitleMode(getResources().getString(R.string.me));
                if (meFragment == null) {
                    meFragment = (MeFragment) FragmentInstanceManager.getInstance().getFragmet(MeFragment.class);
                    switchFragment(meFragment, true);
                } else {
                    switchFragment(meFragment, false);
                }
                break;
        }
    }

    //设置头部是搜索模式
    private void setAppHeadViewSearchMode() {
        apv.setFlHeadLeftVisible(View.VISIBLE);
        apv.setFlHeadRightVisible(View.VISIBLE);
        apv.setMiddleSearchVisible(View.VISIBLE);
        apv.setTvTitleVisible(View.GONE);
    }

    //设置头部是文本模式
    private void setAppHeadViewTitleMode(String title) {
        apv.setFlHeadLeftVisible(View.INVISIBLE);
        apv.setFlHeadRightVisible(View.INVISIBLE);
        apv.setMiddleSearchVisible(View.GONE);
        apv.setTvTitleVisible(View.VISIBLE);
        apv.setTvTitleText(title);
    }

    private void resetAllState() {
        llBottomMain.setSelected(false);
        llBottomMake.setSelected(false);
        llBottomMe.setSelected(false);
    }

    //隐藏所有的fragment
    private void hideFragment(FragmentTransaction transaction) {
        if (mainFragment != null) {
            transaction.hide(mainFragment);
        }
        if (makeFragment != null) {
            transaction.hide(makeFragment);
        }
        if (meFragment != null) {
            transaction.hide(meFragment);
        }
    }

    // 提供方法切换Fragment
    public void switchFragment(Fragment fragment, boolean isNeedAdd) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        if (isNeedAdd) {
            transaction.add(R.id.fl_main_container, fragment);
        }
        hideFragment(transaction);
        transaction.show(fragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
