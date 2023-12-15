package com.fei.firstproject.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.common.base.BaseActivity;
import com.common.viewmodel.BaseViewModel;
import com.fei.firstproject.R;
import com.fei.firstproject.config.AppConfig;
import com.fei.firstproject.databinding.ActivityBaseBinding;
import com.fei.firstproject.decoration.DividerGridItemDecoration;
import com.fei.firstproject.decoration.DividerItemDecoration;
import com.fei.firstproject.dialog.CustomeProgressDialog;
import com.fei.firstproject.dialog.TipDialog;
import com.fei.firstproject.entity.UserEntity;
import com.fei.firstproject.event.AllEvent;
import com.fei.firstproject.event.EventType;
import com.fei.firstproject.http.RxSchedulers;
import com.fei.firstproject.inter.IBase;
import com.fei.firstproject.utils.LogUtils;
import com.fei.firstproject.utils.SPUtils;
import com.fei.firstproject.widget.AppHeadView;
import com.google.android.material.appbar.AppBarLayout;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.ObservableTransformer;

/**
 * Created by Administrator on 2017/7/27.
 */

public abstract class BaseProjectActivity<VM extends BaseViewModel, VB extends ViewBinding> extends BaseActivity<VM, ActivityBaseBinding> implements IBase {

    ProgressBar pbLoading;
    LinearLayout llNoData;
    RelativeLayout rlDefault;
    Button btnRequestError;
    LinearLayout llRequestError;
    SmartRefreshLayout refreshLayout;
    AppHeadView appHeadView;
    Toolbar toolbar;
    AppBarLayout appBarLayout;

    protected CustomeProgressDialog progressDialog;
    private TipDialog tipDialog;

    protected VB mChildBinding;

    /**
     * @param isShow 是否显示错误的view
     */
    protected <T> ObservableTransformer<T, T> createTransformer(final boolean isShow) {
        return RxSchedulers.compose(this, this.<T>bindToLifecycle(), () -> {
            if (isShow) {
                showRequestErrorView();
            }
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        initTheme();
        initBus();
        super.onCreate(savedInstanceState);
        initProgress();
        initTitle();
        initRequest();
    }

    private void initBus() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        mChildBinding = getChildViewBinding();
        mBinding.flContainer.addView(mChildBinding.getRoot());
        pbLoading = mChildBinding.getRoot().findViewById(R.id.pb_loading);
        llNoData = mChildBinding.getRoot().findViewById(R.id.ll_no_data);
        rlDefault = mChildBinding.getRoot().findViewById(R.id.rl_default);
        btnRequestError = mChildBinding.getRoot().findViewById(R.id.btn_request_error);
        llRequestError = mChildBinding.getRoot().findViewById(R.id.ll_request_error);
        refreshLayout = mChildBinding.getRoot().findViewById(R.id.refreshLayout);
        appHeadView = mBinding.appHeadView;
        appBarLayout = mBinding.appBarLayout;
        toolbar = mBinding.toolbar;
    }

    private VB getChildViewBinding() {
        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            Type typeArgument = ((ParameterizedType) type).getActualTypeArguments()[1];
            Class<VB> vbClass = (Class<VB>) typeArgument;
            try {
                Method method = vbClass.getMethod("inflate", LayoutInflater.class);
                return (VB) method.invoke(null, getLayoutInflater());
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(this.getLocalClassName());
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(this.getLocalClassName());
        MobclickAgent.onPause(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dealEvent(AllEvent allEvent) {
        if (allEvent.getEventType() == EventType.APP_FONT_CHANGE) {
            recreate();
        }
    }

    protected void setBackTitle(String title) {
        appHeadView.setFlHeadLeftPadding(getResources().getDimensionPixelSize(R.dimen.size_10));
        appHeadView.setLeftStyle(AppHeadView.IMAGE);
        appHeadView.setFlHeadLeftVisible(View.VISIBLE);
        appHeadView.setLeftDrawable(R.drawable.selector_head_left_arrow);
        appHeadView.setMiddleStyle(AppHeadView.TEXT);
        appHeadView.setMiddleText(title);
        appHeadView.setFlHeadRightVisible(View.INVISIBLE);
    }

    protected void initTheme() {
        int mode = (int) SPUtils.get(this, "fontMode", 0);
        if (mode == 0) {
            setTheme(R.style.NormalFontStyle);
        } else if (mode == 1) {
            setTheme(R.style.SmallFontStyle);
        } else if (mode == 2) {
            setTheme(R.style.BigFontStyle);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initlistener() {
        if (btnRequestError != null) {
            btnRequestError.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showLoading();
                    initRequest();
                }
            });
        }

        if (refreshLayout != null) {
            refreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
                @Override
                public void onLoadmore(RefreshLayout refreshlayout) {
                    initRequest();
                }

                @Override
                public void onRefresh(RefreshLayout refreshlayout) {
                    initRequest();
                }
            });
        }

        if (appHeadView != null) {
            appHeadView.setOnLeftRightClickListener(new AppHeadView.onAppHeadViewListener() {
                @Override
                public void onLeft(View view) {
                    onBackPressed();
                }

                @Override
                public void onRight(View view) {

                }

                @Override
                public void onEdit(TextView v, int actionId, KeyEvent event) {

                }
            });
        }
    }

    private void initProgress() {
        progressDialog = new CustomeProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);
    }

    /**
     * 显示进度条
     */
    public void proShow() {
        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    /**
     * 取消进度条
     */
    public void proDisimis() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    /**
     * 请求权限
     *
     * @param mNeedPermissions
     * @return
     */
    protected void checkPermissions(String[] mNeedPermissions, int requestCode) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            permissionsGrantCallBack(requestCode);
            return;
        }
        if (mNeedPermissions == null || mNeedPermissions.length == 0) return;
        List<String> needRequesetPermissionList = findDeniedPermissions(mNeedPermissions);
        if (null != needRequesetPermissionList && needRequesetPermissionList.size() > 0) {
            ActivityCompat.requestPermissions(this,
                    needRequesetPermissionList.toArray(new String[needRequesetPermissionList.size()]), requestCode);
        } else {
            permissionsGrantCallBack(requestCode);
        }
    }

    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param mNeedPermissions
     * @return
     */
    private List<String> findDeniedPermissions(String[] mNeedPermissions) {
        List<String> needRequestPermissionList = new ArrayList<>();
        for (String perm : mNeedPermissions) {
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {
                needRequestPermissionList.add(perm);
            }
        }
        return needRequestPermissionList;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        LogUtils.i("tag", "activity");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (!vertifyPermission(grantResults)) {
            permissionsDeniedCallBack(requestCode);
        } else {
            permissionsGrantCallBack(requestCode);
        }
    }

    protected void showMissingPermissionDialog(String message, final int requestCode) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage(message);
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();
        builder.setPositiveButton("去设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startAppSettings();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                permissionDialogDismiss(requestCode);
                dialog.dismiss();
            }
        });
        builder.show();
    }

    /**
     * 启动应用的设置
     */
    private void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    /**
     * 检测是否说有的权限都已经授权
     *
     * @param grantResults
     * @return
     */
    private boolean vertifyPermission(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    protected void showLoading() {
        if (pbLoading != null && llRequestError != null && rlDefault != null && llNoData != null) {
            rlDefault.setVisibility(View.VISIBLE);
            pbLoading.setVisibility(View.VISIBLE);
            llRequestError.setVisibility(View.GONE);
            llNoData.setVisibility(View.GONE);
        }

        if (refreshLayout != null) {
            refreshLayout.setVisibility(View.GONE);
        }
    }

    protected void dismissLoading() {
        if (pbLoading != null && llRequestError != null && rlDefault != null && llNoData != null) {
            rlDefault.setVisibility(View.GONE);
            pbLoading.setVisibility(View.GONE);
            llRequestError.setVisibility(View.GONE);
            llNoData.setVisibility(View.GONE);
        }
    }

    public void showRequestErrorView() {
        if (pbLoading != null && llRequestError != null && rlDefault != null && llNoData != null) {
            rlDefault.setVisibility(View.VISIBLE);
            llRequestError.setVisibility(View.VISIBLE);
            pbLoading.setVisibility(View.GONE);
            llNoData.setVisibility(View.GONE);
        }
        if (refreshLayout != null) {
            refreshLayout.setVisibility(View.GONE);
        }
    }

    protected void showNoDataView() {
        if (pbLoading != null && llRequestError != null && rlDefault != null && llNoData != null) {
            rlDefault.setVisibility(View.VISIBLE);
            llNoData.setVisibility(View.VISIBLE);
            pbLoading.setVisibility(View.GONE);
            llRequestError.setVisibility(View.GONE);
        }

        if (refreshLayout != null) {
            refreshLayout.setVisibility(View.GONE);
        }
    }

    //登录
    public void refreshUserInfoWhenLogin(UserEntity userEntity) {
        AppConfig.ISLOGIN = true;
        AppConfig.user = userEntity;
        SPUtils.put(this, "user", userEntity);
        EventBus.getDefault().post(new AllEvent(EventType.APP_LOGIN));
    }

    //退出登录
    public void refreshUserInfoWhenLogout() {
        AppConfig.ISLOGIN = false;
        AppConfig.user = null;
        SPUtils.remove(this, "user");
        SPUtils.remove(this, "tokenId");
        EventBus.getDefault().post(new AllEvent(EventType.APP_LOGOUT));
    }

    //提示登录
    public void showDialogWhenUnLogin() {
        if (tipDialog == null) {
            tipDialog = new TipDialog(this);
            tipDialog.setCanceledOnTouchOutside(false);
            tipDialog.setContentText(getResources().getString(R.string.login_tip));
            tipDialog.setConfirmButtonText(getResources().getString(R.string.go_to_login));
            tipDialog.setOnConfirmListener(new TipDialog.OnConfirmListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(BaseProjectActivity.this, LoginActivity.class));
                }
            });
        }
        tipDialog.show();
    }

    @Nullable
    public AppHeadView getAppHeadView() {
        return appHeadView;
    }

    public void setLinearRecycleViewSetting(RecyclerView recycleViewSetting, Activity activity) {
        recycleViewSetting.setNestedScrollingEnabled(false);
        LinearLayoutManager manager = new LinearLayoutManager(activity);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(activity, LinearLayout.VERTICAL);
        recycleViewSetting.setLayoutManager(manager);
        recycleViewSetting.addItemDecoration(dividerItemDecoration);
    }

    public void setGridRecycleViewSetting(RecyclerView recycleViewSetting, Activity activity, int count) {
        recycleViewSetting.setNestedScrollingEnabled(false);
        GridLayoutManager manager = new GridLayoutManager(activity, count);
        RecyclerView.ItemDecoration itemDecoration = new DividerGridItemDecoration(activity);
        recycleViewSetting.setLayoutManager(manager);
        recycleViewSetting.addItemDecoration(itemDecoration);
    }


    @Override
    public void startActivityWithCodeAndPair(Intent intent, int requestCode, Pair<View, String>... sharedElements) {
        startActivityForResult(intent, requestCode, ActivityOptionsCompat.makeSceneTransitionAnimation(this, sharedElements).toBundle());
    }


    @Override
    public void startActivityWithCode(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode, ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle());
    }

    @Override
    public void startActivityWithoutCode(Intent intent) {
        startActivityWithCode(intent, -1);
    }
}
