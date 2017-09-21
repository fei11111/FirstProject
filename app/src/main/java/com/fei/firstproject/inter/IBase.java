package com.fei.firstproject.inter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.view.View;

/**
 * Created by Administrator on 2017/7/29.
 */

public interface IBase {

    /**
     * 权限被拒返回
     */
    public void permissionsDeniedCallBack(int requestCode);

    /**
     * 授权后返回
     */
    public void permissionsGrantCallBack(int requestCode);

    /**
     * dialog提示被拒返回
     */
    public void permissionDialogDismiss(int requestCode);

    /**
     * 返回布局
     */
    public int getContentViewResId();

    /**
     * 初始化
     */
    public void init(Bundle savedInstanceState);

    /**
     * 初始化接口
     */
    public void initRequest();

    /**
     * 没有请求码启动Activity
     */
    public void startActivityWithoutCode(Intent intent);

    /**
     * 有请求码启动Activity
     */
    public void startActivityWithCode(Intent intent, int requestCode);

    /**
     * 有请求码和共享元素启动Activity
     */
    public void startActivityWithCodeAndPair(Intent intent, int requestCode, Pair<View, String>... sharedElements);

}
