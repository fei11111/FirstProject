package com.fei.firstproject.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.fei.firstproject.R;
import com.fei.firstproject.activity.LoginActivity;
import com.fei.firstproject.activity.SettingsActivity;
import com.fei.firstproject.adapter.MyRecyclerViewAdapter;
import com.fei.firstproject.config.AppConfig;
import com.fei.firstproject.decoration.DividerGridItemDecoration;
import com.fei.firstproject.widget.NoScrollRecyclerView;
import com.fei.firstproject.widget.SettingView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/7/29.
 */

public class MeFragment extends BaseFragment {

    @BindView(R.id.rl_order)
    SettingView rlOrder;
    @BindView(R.id.ll_wait_obligation)
    LinearLayout llWaitObligation;
    @BindView(R.id.ll_wait_post)
    LinearLayout llWaitPost;
    @BindView(R.id.ll_wait_receive)
    LinearLayout llWaitReceive;
    @BindView(R.id.ll_wait_evaluate)
    LinearLayout llWaitEvaluate;
    @BindView(R.id.nsrv)
    NoScrollRecyclerView nsrv;
    @BindView(R.id.nsrv2)
    NoScrollRecyclerView nsrv2;

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
        return R.layout.fragment_me;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        Log.i("tag","me");
        initRecyclerView();
    }

    private void initRecyclerView() {
        initView1();
        initView2();
    }

    private void initView1() {
        GridLayoutManager manager = new GridLayoutManager(activity, 3);
        RecyclerView.ItemDecoration itemDecoration = new DividerGridItemDecoration(activity);
        nsrv.setLayoutManager(manager);
        nsrv.addItemDecoration(itemDecoration);
        nsrv.setAdapter(new MyRecyclerViewAdapter(activity, R.array.list_me_drawable, getResources().getStringArray(R.array.list_me_str)));
    }

    private void initView2() {
        GridLayoutManager manager = new GridLayoutManager(activity, 3);
        RecyclerView.ItemDecoration itemDecoration = new DividerGridItemDecoration(activity);
        nsrv2.setLayoutManager(manager);
        nsrv2.addItemDecoration(itemDecoration);
        nsrv2.setAdapter(new MyRecyclerViewAdapter(activity, R.array.list_other_drawable, getResources().getStringArray(R.array.list_other_str)));
    }

    @OnClick(R.id.ll_me_info)
    void clickMeInfo(View view) {
        if (AppConfig.ISLOGIN) {
            startActivityWithoutCode(new Intent(activity, SettingsActivity.class));
        } else {
            startActivityWithoutCode(new Intent(activity, LoginActivity.class));
        }
    }
}
