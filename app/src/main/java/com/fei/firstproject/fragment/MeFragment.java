package com.fei.firstproject.fragment;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.widget.LinearLayout;

import com.fei.firstproject.R;
import com.fei.firstproject.adapter.MyRecyclerViewAdapter;
import com.fei.firstproject.decoration.ItemDecoration;
import com.fei.firstproject.widget.NoScrollRecyclerView;
import com.fei.firstproject.widget.SettingView;

import butterknife.BindView;

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
    public int getContentViewResId() {
        return R.layout.fragment_me;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        initRecyclerView();
    }

    private void initRecyclerView() {
        GridLayoutManager manager = new GridLayoutManager(activity, 3);
        nsrv.setLayoutManager(manager);
        nsrv.addItemDecoration(new ItemDecoration(1, R.color.colorGray, 1));
        nsrv.setAdapter(new MyRecyclerViewAdapter(activity));
    }
}
