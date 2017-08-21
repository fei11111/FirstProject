package com.fei.firstproject.activity;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.fei.firstproject.R;
import com.fei.firstproject.adapter.MyMessageAdapter;
import com.fei.firstproject.widget.AppHeadView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/8/21.
 */

public class MessageActivity extends BaseActivity {

    @BindView(R.id.appHeadView)
    AppHeadView appHeadView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.recycler_message)
    RecyclerView recyclerMessage;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    @Override
    public void requestPermissionsBeforeInit() {

    }

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
        return R.layout.activity_message;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        initRecycler();
        initListener();
    }

    private void initListener() {
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

    private void initRecycler() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerMessage.setLayoutManager(linearLayoutManager);
        MyMessageAdapter messageAdapter = new MyMessageAdapter(this);
        recyclerMessage.setAdapter(messageAdapter);
    }

}
