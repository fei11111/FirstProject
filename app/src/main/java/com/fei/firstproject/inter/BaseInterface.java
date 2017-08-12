package com.fei.firstproject.inter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.view.View;

/**
 * Created by Administrator on 2017/7/29.
 */

public interface BaseInterface {

    public void requestPermissionsBeforeInit();

    public void permissionsDeniedCallBack(int requestCode);

    public void permissionsGrantCallBack(int requestCode);

    public int getContentViewResId();

    public void init(Bundle savedInstanceState);

    public void permissionDialogDismiss(int requestCode);

    public void startActivityWithoutCode(Intent intent);

    public void startActivityWithCode(Intent intent, int requestCode);

    public void startActivityWithCodeAndPair(Intent intent, int requestCode, Pair<View, String>... sharedElements);

}
