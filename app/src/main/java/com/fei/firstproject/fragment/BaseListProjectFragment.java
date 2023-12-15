package com.fei.firstproject.fragment;

import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.common.viewmodel.BaseViewModel;
import com.fei.firstproject.databinding.FragmentBaseListBinding;


/**
 * Created by Administrator on 2017/8/25.
 * 没有头部，只有recyclerView
 */

public abstract class BaseListProjectFragment<VM extends BaseViewModel> extends BaseProjectFragment<VM, FragmentBaseListBinding> {

    RecyclerView recyclerView;

    protected int currentPage = 1;

    @Override
    public void initViewAndData(Bundle savedInstanceState) {
        initListener();
        initData();
    }

    private void initListener() {
        refreshLayout.setLoadMoreEnabled(true);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 1;
                initRequest();
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                currentPage++;
                initRequest();
            }
        });

    }

    public abstract void initData();
}
