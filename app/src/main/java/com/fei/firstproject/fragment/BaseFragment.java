package com.fei.firstproject.fragment;

import android.app.Activity;
import android.content.Context;
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
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fei.firstproject.inter.BaseInterface;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2017/7/27.
 */

public abstract class BaseFragment extends Fragment implements BaseInterface {

    private Unbinder unbinder;
    protected Activity activity;
    protected static final int REQUEST_CODE_1 = 100;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getContentViewResId(), container, false);
        unbinder = ButterKnife.bind(this, view);

        init(savedInstanceState);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * 请求权限
     *
     * @param mNeedPermissions
     * @return
     */
    protected void checkPermissions(String[] mNeedPermissions, int requestCode) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        if (mNeedPermissions == null || mNeedPermissions.length == 0) return;
        List<String> needRequesetPermissionList = findDeniedPermissions(mNeedPermissions);
        if (null != needRequesetPermissionList && needRequesetPermissionList.size() > 0) {
            ActivityCompat.requestPermissions(activity,
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
            if (ContextCompat.checkSelfPermission(activity, perm) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.shouldShowRequestPermissionRationale(activity, perm)) {
                needRequestPermissionList.add(perm);
            }
        }
        return needRequestPermissionList;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i("tag", "fragment");
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
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
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

    @Override
    public void startActivityWithCodeAndPair(Intent intent, int requestCode, Pair<View, String>... sharedElements) {
        ActivityOptionsCompat transitionActivityOptions = null;
        if (sharedElements == null) {
            transitionActivityOptions =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(activity);
        } else {
            transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, sharedElements);
        }
        startActivityForResult(intent, requestCode, transitionActivityOptions.toBundle());
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
