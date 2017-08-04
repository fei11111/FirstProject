package com.fei.firstproject.inter;

import android.os.Bundle;

/**
 * Created by Administrator on 2017/7/29.
 */

public interface BaseInterface {

    public void requestPermissionsBeforeInit();

    public void permissionsDeniedCallBack(int requestCode);

    public void permissionsGrantCallBack(int requestCode);

    public int getContentViewResId();

    public void init(Bundle savedInstanceState);

}
