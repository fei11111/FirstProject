package com.fei.firstproject.activity;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.fei.firstproject.R;
import com.fei.firstproject.widget.AppHeadView;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import butterknife.BindView;

/**
 * Created by Fei on 2017/8/23.
 */

public abstract class BaseListActivity extends BaseActivity {

    @BindView(R.id.appHeadView)
    AppHeadView appHeadView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.listView)
    ListView listView;

    protected int currentPage = 1;

    @Override
    public int getContentViewResId() {
        return R.layout.activity_base_list;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        initListener();
        initAppHeadView();
    }

    private void initListener() {
        appHeadView.setOnLeftRightClickListener(new AppHeadView.onAppHeadViewListener() {
            @Override
            public void onLeft(View view) {
                onBackPressed();
            }

            @Override
            public void onRight(View view) {
                currentPage = 1;
                initRequest();
            }

            @Override
            public void onEdit(TextView v, int actionId, KeyEvent event) {
                currentPage = 1;
                initRequest();
            }
        });

        refreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                currentPage++;
                initRequest();
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                currentPage = 1;
                initRequest();
            }
        });

    }

    //设置头部
    public abstract void initAppHeadView();

}
