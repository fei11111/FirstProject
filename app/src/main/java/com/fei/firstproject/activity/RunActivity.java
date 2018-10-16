package com.fei.firstproject.activity;

import android.os.Bundle;

import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.fei.firstproject.R;
import com.scwang.smartrefresh.layout.util.DelayedRunable;

import butterknife.BindView;

public class RunActivity extends BaseActivity {

    @BindView(R.id.swipeLayout)
    SwipeToLoadLayout swipeLayout;

    @Override
    public void permissionsDeniedCallBack(int requestCode) {

    }

    @Override
    public void permissionsGrantCallBack(int requestCode) {

    }

    @Override
    public void permissionDialogDismiss(int requestCode) {

    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_run;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        initListener();
    }

    private void initListener() {
        if (swipeLayout != null) {
            swipeLayout.setRefreshEnabled(true);
            swipeLayout.setLoadMoreEnabled(false);
            swipeLayout.setOnRefreshListener(new OnRefreshListener() {
                @Override
                public void onRefresh() {
                    initRequest();
                }
            });
        }
    }

    @Override
    public void initRequest() {
        runOnUiThread(new DelayedRunable(new Runnable() {
            @Override
            public void run() {
                swipeLayout.setRefreshing(false);
            }
        }, 2000));
    }
}
