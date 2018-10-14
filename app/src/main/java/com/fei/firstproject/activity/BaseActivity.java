package com.fei.firstproject.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.fei.firstproject.R;
import com.fei.firstproject.config.AppConfig;
import com.fei.firstproject.decoration.DividerGridItemDecoration;
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
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.ObservableTransformer;

/**
 * Created by Administrator on 2017/7/27.
 */

public abstract class BaseActivity extends RxAppCompatActivity implements IBase {

    @Nullable
    @BindView(R.id.pb_loading)
    ProgressBar pbLoading;
    @Nullable
    @BindView(R.id.ll_no_data)
    LinearLayout llNoData;
    @Nullable
    @BindView(R.id.rl_default)
    RelativeLayout rlDefault;
    @Nullable
    @BindView(R.id.btn_request_error)
    Button btnRequestError;
    @Nullable
    @BindView(R.id.ll_request_error)
    LinearLayout llRequestError;
    @Nullable
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @Nullable
    @BindView(R.id.appHeadView)
    AppHeadView appHeadView;

    private Unbinder unbinder;
    protected CustomeProgressDialog progressDialog;
    private TipDialog tipDialog;

    /**
     * @param isShow 是否显示错误的view
     * */
    protected <T> ObservableTransformer<T, T> createTransformer(final boolean isShow) {
        return RxSchedulers.compose(this, this.<T>bindToLifecycle(), new RxSchedulers.OnConnectError() {
            @Override
            public void onError() {
                if (isShow) {
                    showRequestErrorView();
                }
            }
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTheme();
        setContentView(getContentViewResId());
        EventBus.getDefault().register(this);
        unbinder = ButterKnife.bind(this);
        initProgress();
        initlistener();
        init(savedInstanceState);
        initRequest();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dealEvent(AllEvent allEvent) {
        if (allEvent.getEventType() == EventType.APP_FONT_CHANGE) {
            recreate();
        }
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
        unbinder.unbind();
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

    protected void showRequestErrorView() {
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
                    startActivity(new Intent(BaseActivity.this, LoginActivity.class));
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

    public void setGridRecycleViewSetting(RecyclerView recycleViewSetting, Activity activity,int count) {
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
