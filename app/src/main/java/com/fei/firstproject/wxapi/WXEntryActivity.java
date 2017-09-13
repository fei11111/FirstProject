package com.fei.firstproject.wxapi;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fei.firstproject.R;
import com.fei.firstproject.utils.LogUtils;
import com.fei.firstproject.utils.WxApiUtils;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/9/1.
 */

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    @BindView(R.id.iv_dialog_cancle)
    ImageView ivDialogCancle;
    @BindView(R.id.tv_dialog_content)
    TextView tvDialogContent;
    @BindView(R.id.btn_dialog_confirm)
    Button btnDialogConfirm;

    @Override
    public void onReq(BaseReq baseReq) {
        LogUtils.i("tag", baseReq.toString());
    }

    @Override
    public void onResp(BaseResp baseResp) {
        int code = baseResp.errCode;
        int result;
        switch (code) {
            case BaseResp.ErrCode.ERR_OK:
                setTitle("分享成功");
                result = R.string.sharewechat_success;//成功
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = R.string.sharewechat_cancel;//取消
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = R.string.sharewechat_deny;//被拒绝
                break;
            default:
                result = R.string.sharewechat_back;//返回
                break;
        }
        tvDialogContent.setText(result);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_tip_dialog);
        ButterKnife.bind(this);
        initWx();
    }

    private void initWx() {
        WxApiUtils.getInstance(this).getIwxapi().handleIntent(getIntent(), this);
    }

    @OnClick({R.id.btn_dialog_confirm, R.id.iv_dialog_cancle})
    void clickCancle(View view) {
        finish();
    }
}
