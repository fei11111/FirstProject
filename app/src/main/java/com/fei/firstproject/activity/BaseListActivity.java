package com.fei.firstproject.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.fei.firstproject.R;
import com.fei.firstproject.utils.Utils;
import com.fei.firstproject.widget.AppHeadView;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import butterknife.BindView;

/**
 * Created by Fei on 2017/8/23.
 * 有头部,内容是RecyclerView
 */

public abstract class BaseListActivity extends BaseActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @Nullable
    @BindView(R.id.btn_base_list)
    Button btn_base_list;

    protected int currentPage = 1;

    @Override
    public int getContentViewResId() {
        return R.layout.activity_base_list;
    }

    @Override
    public void initTitle() {
        appHeadView.setFlHeadLeftPadding(getResources().getDimensionPixelSize(R.dimen.size_10));
        appHeadView.setLeftStyle(AppHeadView.IMAGE);
        appHeadView.setFlHeadLeftVisible(View.VISIBLE);
        appHeadView.setLeftDrawable(R.drawable.selector_head_left_arrow);
    }

    @Override
    public void init(Bundle savedInstanceState) {
        initListener();
        initView();
        initRecyclerView();
    }

    private void initRecyclerView() {
        setLinearRecycleViewSetting(recyclerView, this);
    }

    void initListener() {
        appHeadView.setOnLeftRightClickListener(new AppHeadView.onAppHeadViewListener() {
            @Override
            public void onLeft(View view) {
                onBackPressed();
            }

            @Override
            public void onRight(View view) {
                currentPage = 1;
                Utils.hideKeyBoard(BaseListActivity.this);
                showLoading();
                initRequest();
            }

            @Override
            public void onEdit(TextView v, int actionId, KeyEvent event) {
                currentPage = 1;
                Utils.hideKeyBoard(BaseListActivity.this);
                showLoading();
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
    public abstract void initView();

}
