package com.fei.firstproject.wxapi;

import android.app.Activity;
import android.content.Intent;

import com.fei.firstproject.utils.LogUtils;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

/**
 * Created by Administrator on 2017/9/1.
 */

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        LogUtils.i("tag", baseReq.toString());
    }

    @Override
    public void onResp(BaseResp baseResp) {
        LogUtils.i("tag", baseResp.toString());
    }
}
