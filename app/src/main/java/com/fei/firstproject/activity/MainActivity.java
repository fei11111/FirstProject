package com.fei.firstproject.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fei.firstproject.R;
import com.fei.firstproject.config.AppConfig;
import com.fei.firstproject.fragment.MainFragment;
import com.fei.firstproject.fragment.MakeFragment;
import com.fei.firstproject.fragment.MeFragment;
import com.fei.firstproject.fragment.manager.FragmentInstanceManager;
import com.fei.firstproject.utils.LogUtils;
import com.fei.firstproject.utils.Utils;
import com.fei.firstproject.web.WebActivity;
import com.fei.firstproject.widget.AppHeadView;

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
    @BindView(R.id.appHeadView)
    AppHeadView appHeadView;
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
    //权限请求
    private static final int REQUEST_PERMISSION_CODE_STORAGE = 100;
    private static final int REQUEST_PERMISSION_CODE_CAMERA = 101;
    //Activity请求返回
    private static final int REQUEST_ACTIVITY_CODE_CAMERA = 200;

    @Override
    public void permissionsDeniedCallBack(int requestCode) {
        if (requestCode == REQUEST_PERMISSION_CODE_STORAGE) {
            showMissingPermissionDialog(getString(R.string.need_storage_permission), requestCode);
        } else if (requestCode == REQUEST_PERMISSION_CODE_CAMERA) {
            showMissingPermissionDialog(getString(R.string.need_camera_permission_to_scan), requestCode);
        }
    }

    @Override
    public void permissionsGrantCallBack(int requestCode) {
        if (requestCode == REQUEST_PERMISSION_CODE_CAMERA) {
            //相机权限
            startActivityWithCode(new Intent(this, CaptureActivity.class), REQUEST_ACTIVITY_CODE_CAMERA);
        }
    }

    @Override
    public void permissionDialogDismiss(int requestCode) {
        if (requestCode == REQUEST_PERMISSION_CODE_STORAGE) {
            Utils.showToast(this, getString(R.string.storage_permission_fail));
        } else if (requestCode == REQUEST_PERMISSION_CODE_CAMERA) {
            Utils.showToast(this, getString(R.string.camera_permission_fail));
        }
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_main;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        initPermission();
        initToolBar();
        initSetting();
        initListener();
    }

    private void initPermission() {
        checkPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE_STORAGE);
    }

    @Override
    public void initRequest() {

    }

    private void initSetting() {
        mFragmentManager = getSupportFragmentManager();
        llBottomMain.performClick();
    }

    private void initToolBar() {
        setSupportActionBar(toolbar);
        if (AppConfig.ISLOGIN) {
            appHeadView.setFlHeadLeftVisible(View.INVISIBLE);
        } else {
            appHeadView.setFlHeadLeftVisible(View.VISIBLE);
        }
    }

    private void initListener() {
        appHeadView.setOnLeftRightClickListener(new AppHeadView.onAppHeadViewListener() {
            @Override
            public void onLeft(View view) {
                checkPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CODE_CAMERA);
            }

            @Override
            public void onRight(View view) {
                if (AppConfig.ISLOGIN) {
                    startActivityWithoutCode(new Intent(MainActivity.this, MessageActivity.class));
                } else {
                    showDialogWhenUnLogin();
                }
            }

            @Override
            public void onEdit(TextView v, int actionId, KeyEvent event) {
                Utils.hideKeyBoard(MainActivity.this);
                if (mainFragment != null) {
                    mainFragment.getRecommendPlan();
                }
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
        appHeadView.setFlHeadLeftVisible(View.VISIBLE);
        appHeadView.setFlHeadRightVisible(View.VISIBLE);
        appHeadView.setMiddleSearchVisible(View.VISIBLE);
        appHeadView.setTvTitleVisible(View.GONE);
    }

    //设置头部是文本模式
    private void setAppHeadViewTitleMode(String title) {
        appHeadView.setFlHeadLeftVisible(View.INVISIBLE);
        appHeadView.setFlHeadRightVisible(View.INVISIBLE);
        appHeadView.setMiddleSearchVisible(View.GONE);
        appHeadView.setTvTitleVisible(View.VISIBLE);
        appHeadView.setMiddleText(title);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtils.i("tag","activity - onActivityResult");
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_ACTIVITY_CODE_CAMERA) {
                if (data != null) {
                    String result = data.getStringExtra("result");
                    if (!TextUtils.isEmpty(result)) {
                        if (result.contains("http") || result.contains("https")) {
                            Intent intent = new Intent(this, WebActivity.class);
                            intent.putExtra("url", result);
                            startActivityWithoutCode(intent);
                        } else {
                            Utils.showToast(this, getResources().getString(R.string.scan_error));
                        }
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public AppHeadView getAppHeadView() {
        return appHeadView;
    }

}
