package com.fei.firstproject.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.fei.firstproject.R;
import com.fei.firstproject.config.AppConfig;
import com.fei.firstproject.entity.UserEntity;
import com.fei.firstproject.event.AllEvent;
import com.fei.firstproject.event.EventType;
import com.fei.firstproject.fragment.MainFragment;
import com.fei.firstproject.fragment.MakeFragment;
import com.fei.firstproject.fragment.MeFragment;
import com.fei.firstproject.fragment.manager.FragmentInstanceManager;
import com.fei.firstproject.http.HttpMgr;
import com.fei.firstproject.http.inter.CallBack;
import com.fei.firstproject.jniTest.SignCheck;
import com.fei.firstproject.utils.LogUtils;
import com.fei.firstproject.utils.SPUtils;
import com.fei.firstproject.utils.Utils;
import com.fei.firstproject.utils.WxApiUtils;
import com.fei.firstproject.widget.AppHeadView;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @BindView(R.id.ll_bottom_main)
    LinearLayout llBottomMain;
    @BindView(R.id.ll_bottom_make)
    LinearLayout llBottomMake;
    @BindView(R.id.ll_bottom_me)
    LinearLayout llBottomMe;
    @BindView(R.id.bottom_layout)
    LinearLayout bottomLayout;
    @BindView(R.id.fl_main_container)
    FrameLayout flMainContainer;

    private FragmentManager mFragmentManager;
    private MainFragment mainFragment;
    private MakeFragment makeFragment;
    private MeFragment meFragment;

    //权限请求
    private static final int REQUEST_PERMISSION_CODE_CAMERA = 101;
    //Activity请求返回
    private static final int REQUEST_ACTIVITY_CODE_CAMERA = 200;

    private int tagPosition = 0;

    @Override
    public void permissionsDeniedCallBack(int requestCode) {
        if (requestCode == REQUEST_PERMISSION_CODE_CAMERA) {
            showMissingPermissionDialog(getString(R.string.need_camera_permission_to_scan), requestCode);
        }
    }

    @Override
    public void permissionsGrantCallBack(int requestCode) {
        if (requestCode == REQUEST_PERMISSION_CODE_CAMERA) {
            //相机权限
            startActivityForResult(new Intent(this, CaptureActivity.class), REQUEST_ACTIVITY_CODE_CAMERA);
        }
    }

    @Override
    public void permissionDialogDismiss(int requestCode) {
        if (requestCode == REQUEST_PERMISSION_CODE_CAMERA) {
            Utils.showToast(this, getString(R.string.camera_permission_fail));
        }
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_main;
    }

    @Override
    public void initTitle() {
        appHeadView.setLeftStyle(AppHeadView.IMAGE);
        appHeadView.setFlHeadLeftVisible(View.VISIBLE);
        appHeadView.setMiddleStyle(AppHeadView.SEARCH);
        appHeadView.setMiddleSearchHint(getResources().getString(R.string.recommend_tip));
        appHeadView.setRightStyle(AppHeadView.IMAGE);
        appHeadView.setFlHeadRightVisible(View.VISIBLE);

//        LogUtils.i("MainActivity",Utils.getSignature(this));
//        LogUtils.i("MainActivity",SignCheck.isRight(this)+"");
    }

    @Override
    public void init(Bundle savedInstanceState) {
        initView(savedInstanceState);
        initToolBar();
        initListener();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dealEvent(AllEvent allEvent) {
        if (tagPosition == 0) {
            if (allEvent.getEventType() == EventType.APP_LOGIN) {
                appHeadView.setLeftStyle(AppHeadView.IMAGE);
                appHeadView.setLeftDrawable(R.drawable.selector_ic_scan);
            } else if (allEvent.getEventType() == EventType.APP_LOGOUT) {
                appHeadView.setLeftStyle(AppHeadView.TEXT);
                appHeadView.setLeftText(getResources().getString(R.string.login));
            }
        }
    }

    private void sendWx() {
        WXTextObject textObject = new WXTextObject();
        textObject.text = "测试";

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObject;
        msg.description = "测试";

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = "test";
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneTimeline;//WXSceneSession会话 WXSceneTimeline朋友圈
        WxApiUtils.getInstance(getApplicationContext()).getIwxapi().sendReq(req);
    }

    @Override
    public void initRequest() {
        if (!AppConfig.ISLOGIN) return;
        String tokenId = SPUtils.get(this, "tokenId", "").toString();
        String deviceId = SPUtils.get(this, "deviceId", "").toString();
        Map<String, String> map = new HashMap<>();
        map.put("token", tokenId);
        map.put("deviceID", deviceId);
        HttpMgr.getUserInfo(this, map, new CallBack<UserEntity>() {
            @Override
            public void onSuccess(UserEntity userEntity) {
                if (userEntity != null) {
                    if (userEntity.getSuccess().equals("YES")) {
                        refreshUserInfoWhenLogin(userEntity);
                    } else {
                        refreshUserInfoWhenLogout();
                    }
                } else {
                    refreshUserInfoWhenLogout();
                }
            }

            @Override
            public void onFail() {
                refreshUserInfoWhenLogout();
            }
        });
    }

    private void initView(Bundle savedInstanceState) {
        mFragmentManager = getSupportFragmentManager();
        if (savedInstanceState != null) {
            mainFragment = (MainFragment) mFragmentManager.findFragmentByTag("mainFragment");
            makeFragment = (MakeFragment) mFragmentManager.findFragmentByTag("makeFragment");
            meFragment = (MeFragment) mFragmentManager.findFragmentByTag("meFrament");
            tagPosition = savedInstanceState.getInt("position");
        }
        setTab(tagPosition);
    }

    private void initToolBar() {
        setSupportActionBar(toolbar);
    }

    private void initListener() {
        appHeadView.setOnLeftRightClickListener(new AppHeadView.onAppHeadViewListener() {
            @Override
            public void onLeft(View view) {
                if (mainFragment != null && mainFragment.isVisible()) {
                    if (AppConfig.ISLOGIN) {
                        checkPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CODE_CAMERA);
                    } else {
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    }
                } else if (meFragment != null && meFragment.isVisible()) {
                    if (AppConfig.ISLOGIN) {
                        startActivityWithoutCode(new Intent(MainActivity.this, SettingActivity.class));
                    } else {
                        showDialogWhenUnLogin();
                    }
                }
            }

            @Override
            public void onRight(View view) {
                if (mainFragment != null && mainFragment.isVisible()) {
//                    if (AppConfig.ISLOGIN) {
//                        startActivityWithoutCode(new Intent(MainActivity.this, MessageActivity.class));
//                    } else {
//                        showDialogWhenUnLogin();
//                    }
                    startActivityWithoutCode(new Intent(MainActivity.this, AlbumActivity.class));
                } else if (meFragment != null && meFragment.isVisible()) {
                    sendWx();
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
                tagPosition = 0;
                break;
            case R.id.ll_bottom_make:
                tagPosition = 1;
                break;
            case R.id.ll_bottom_me:
                tagPosition = 2;
                break;
        }
        setTab(tagPosition);
    }

    private void setTab(int position) {
        switch (position) {
            case 0:
                setAppHeadViewSearchMode();
                llBottomMain.setSelected(true);
                if (mainFragment == null) {
                    mainFragment = (MainFragment) FragmentInstanceManager.getInstance().getFragmet(MainFragment.class);
                    switchFragment(mainFragment, true, "mainFragment");
                } else {
                    switchFragment(mainFragment, false, "mainFragment");
                }
                break;
            case 1:
                setAppHeadViewTitleMode();
                llBottomMake.setSelected(true);
                if (makeFragment == null) {
                    makeFragment = (MakeFragment) FragmentInstanceManager.getInstance().getFragmet(MakeFragment.class);
                    switchFragment(makeFragment, true, "makeFragment");
                } else {
                    switchFragment(makeFragment, false, "makeFragment");
                }
                break;
            case 2:
                llBottomMe.setSelected(true);
                setAppHeadViewTitleImageMode();
                if (meFragment == null) {
                    meFragment = (MeFragment) FragmentInstanceManager.getInstance().getFragmet(MeFragment.class);
                    switchFragment(meFragment, true, "meFrament");
                } else {
                    switchFragment(meFragment, false, "meFrament");
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
        if (!AppConfig.ISLOGIN) {
            //未登录
            appHeadView.setLeftStyle(AppHeadView.TEXT);
            appHeadView.setLeftText(getResources().getString(R.string.login));
        } else {
            //登录
            appHeadView.setLeftStyle(AppHeadView.IMAGE);
            appHeadView.setLeftDrawable(R.drawable.selector_ic_scan);
        }
        appHeadView.setRightDrawable(R.drawable.selector_ic_main_message);
    }

    //设置头部是文本模式
    private void setAppHeadViewTitleMode() {
        appHeadView.setFlHeadLeftVisible(View.INVISIBLE);
        appHeadView.setFlHeadRightVisible(View.INVISIBLE);
        appHeadView.setMiddleSearchVisible(View.GONE);
        appHeadView.setTvTitleVisible(View.VISIBLE);
        appHeadView.setMiddleText(getString(R.string.make));
    }

    //设置头部是中间文本，左右图片
    private void setAppHeadViewTitleImageMode() {
        appHeadView.setFlHeadLeftVisible(View.VISIBLE);
        appHeadView.setFlHeadRightVisible(View.VISIBLE);
        appHeadView.setMiddleSearchVisible(View.GONE);
        appHeadView.setTvTitleVisible(View.VISIBLE);
        appHeadView.setMiddleText("");
        appHeadView.setLeftStyle(AppHeadView.IMAGE);
        appHeadView.setLeftDrawable(R.drawable.selector_ic_setting);
        appHeadView.setRightDrawable(R.drawable.selector_ic_share);
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
    private void switchFragment(Fragment fragment, boolean isNeedAdd, String tag) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        if (isNeedAdd) {
            transaction.add(R.id.fl_main_container, fragment, tag);
        }
        hideFragment(transaction);
        transaction.show(fragment);
        transaction.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("position", tagPosition);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtils.i("tag", "activity - onActivityResult");
        if (requestCode == REQUEST_ACTIVITY_CODE_CAMERA) {
            if (resultCode == RESULT_OK) {
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
}
