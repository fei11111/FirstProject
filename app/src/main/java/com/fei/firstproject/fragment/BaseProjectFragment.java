package com.fei.firstproject.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.viewbinding.ViewBinding;

import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.common.base.BaseFragment;
import com.common.viewmodel.BaseViewModel;
import com.fei.firstproject.R;
import com.fei.firstproject.activity.BaseProjectActivity;
import com.fei.firstproject.http.RxSchedulers;
import com.fei.firstproject.inter.IBase;
import com.fei.firstproject.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.ObservableTransformer;


/**
 * Created by Administrator on 2017/7/27.
 */

public abstract class BaseProjectFragment<VM extends BaseViewModel, VB extends ViewBinding> extends BaseFragment<VM, VB> implements IBase {


    @Nullable
    ProgressBar pbLoading;
    @Nullable
    LinearLayout llNoData;
    @Nullable
    RelativeLayout rlDefault;
    @Nullable
    Button btnRequestError;
    @Nullable
    LinearLayout llRequestError;
    @Nullable
    SwipeToLoadLayout refreshLayout;

    protected BaseProjectActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (BaseProjectActivity) context;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnRequestError = mBinding.getRoot().findViewById(R.id.btn_request_error);
        refreshLayout = mBinding.getRoot().findViewById(R.id.refreshLayout);
        pbLoading = mBinding.getRoot().findViewById(R.id.pb_loading);
        llNoData = mBinding.getRoot().findViewById(R.id.ll_no_data);
        rlDefault = mBinding.getRoot().findViewById(R.id.rl_default);
        llRequestError = mBinding.getRoot().findViewById(R.id.ll_request_error);

        initlistener();

    }

    @Override
    public void lazyLoadData() {
        initRequest();
    }

    @Override
    public void initTitle() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    protected <T> ObservableTransformer<T, T> createTransformer(final boolean isShow) {
        return RxSchedulers.compose(activity, this.<T>bindToLifecycle(), new RxSchedulers.OnConnectError() {
            @Override
            public void onError() {
                if (isShow) {
                    showRequestErrorView();
                }
            }
        });
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
            refreshLayout.setRefreshEnabled(true);
            refreshLayout.setLoadMoreEnabled(false);
            refreshLayout.setOnRefreshListener(new OnRefreshListener() {
                @Override
                public void onRefresh() {
                    initRequest();
                }
            });
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
            requestPermissions(needRequesetPermissionList.toArray(new String[needRequesetPermissionList.size()]), requestCode);
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
            if (ContextCompat.checkSelfPermission(activity, perm) != PackageManager.PERMISSION_GRANTED
                    || shouldShowRequestPermissionRationale(perm)) {
                needRequestPermissionList.add(perm);
            }
        }
        return needRequestPermissionList;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        LogUtils.i("tag", "fragment");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (!vertifyPermission(grantResults)) {
            permissionsDeniedCallBack(requestCode);
        } else {
            permissionsGrantCallBack(requestCode);
        }
    }

    protected void showMissingPermissionDialog(String message, final int requestCode) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
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
        intent.setData(Uri.parse("package:" + activity.getPackageName()));
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

    @Override
    public void startActivityWithCodeAndPair(Intent intent, int requestCode, Pair<View, String>... sharedElements) {
        startActivityForResult(intent, requestCode, ActivityOptionsCompat.makeSceneTransitionAnimation(activity, sharedElements).toBundle());
    }


    @Override
    public void startActivityWithCode(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode, ActivityOptionsCompat.makeSceneTransitionAnimation(activity).toBundle());
    }

    @Override
    public void startActivityWithoutCode(Intent intent) {
        startActivityWithCode(intent, -1);
    }
}
