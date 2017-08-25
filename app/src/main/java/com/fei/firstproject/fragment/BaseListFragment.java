package com.fei.firstproject.fragment;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import com.fei.firstproject.R;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/8/25.
 * 没有头部，只有recyclerView
 */

public abstract class BaseListFragment extends BaseFragment {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private int currentPage = 1;

    @Override
    public int getContentViewResId() {
        return R.layout.fragment_base_list;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        initListener();
        initRecyclerView();
        initData();
    }

    private void initListener() {
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

    private void initRecyclerView() {
        setRecycleViewSetting(recyclerView);
    }

    private void setRecycleViewSetting(RecyclerView recycleViewSetting) {
        LinearLayoutManager manager = new LinearLayoutManager(activity);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(activity, LinearLayout.VERTICAL);
        recycleViewSetting.setLayoutManager(manager);
        recycleViewSetting.addItemDecoration(dividerItemDecoration);
    }

    public abstract void initData();
}
