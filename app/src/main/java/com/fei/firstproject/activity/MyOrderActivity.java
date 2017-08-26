package com.fei.firstproject.activity;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.fei.firstproject.R;
import com.fei.firstproject.fragment.OrderFragment;
import com.fei.firstproject.widget.AppHeadView;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/8/25.
 */

public class MyOrderActivity extends BaseActivity {

    @BindView(R.id.appHeadView)
    AppHeadView appHeadView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.tableLayout)
    TabLayout tableLayout;

    //全部
    //待付款
    //待收货
    //待发货
    //待评价
    private int[] titles = {R.string.all, R.string.wait_obligation, R.string.wait_receive, R.string.wait_post, R.string.wait_evaluate};
    private OrderFragment allFragment;
    private OrderFragment obligationFragment;
    private OrderFragment receiveFragment;
    private OrderFragment postFragment;
    private OrderFragment evaluateFragment;
    private FragmentManager mFragmentManager;

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
        return R.layout.activity_my_order;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        initSetting();
        initListener();
        initTableLayout();
    }

    private void initSetting() {
        mFragmentManager = getSupportFragmentManager();
    }

    private void initTableLayout() {
        for (int title : titles) {
            tableLayout.addTab(tableLayout.newTab().setText(getString(title)));
        }
    }

    private void initListener() {
        initAppHeadViewListener();
        initTableLayoutListener();
    }

    private void initTableLayoutListener() {
        tableLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectFragment(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                hideFragment(tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void initAppHeadViewListener() {
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

    private void selectFragment(int position) {
        switch (position) {
            case 0:
                if (allFragment == null) {
                    allFragment = OrderFragment.newInstance(position);
                    switchFragment(allFragment, true);
                } else {
                    switchFragment(allFragment, false);
                }
                break;
            case 1:
                if (obligationFragment == null) {
                    obligationFragment = OrderFragment.newInstance(position);
                    switchFragment(obligationFragment, true);
                } else {
                    switchFragment(obligationFragment, false);
                }
                break;
            case 2:
                if (receiveFragment == null) {
                    receiveFragment = OrderFragment.newInstance(position);
                    switchFragment(receiveFragment, true);
                } else {
                    switchFragment(receiveFragment, false);
                }
                break;
            case 3:
                if (postFragment == null) {
                    postFragment = OrderFragment.newInstance(position);
                    switchFragment(postFragment, true);
                } else {
                    switchFragment(postFragment, false);
                }
                break;
            case 4:
                if (evaluateFragment == null) {
                    evaluateFragment = OrderFragment.newInstance(position);
                    switchFragment(evaluateFragment, true);
                } else {
                    switchFragment(evaluateFragment, false);
                }
                break;
        }
    }

    // 提供方法切换Fragment
    private void switchFragment(Fragment fragment, boolean isNeedAdd) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        if (isNeedAdd) {
            transaction.add(R.id.fl_order_container, fragment);
        }
        transaction.show(fragment);
        transaction.commit();
    }


    //隐藏所有的fragment
    private void hideFragment(int position) {
        Fragment fragment = getFragment(position);
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        if (fragment != null) {
            transaction.hide(fragment);
        }
        transaction.commit();
    }

    private Fragment getFragment(int position) {
        switch (position) {
            case 0:
                return allFragment;
            case 1:
                return obligationFragment;
            case 2:
                return receiveFragment;
            case 3:
                return postFragment;
            case 4:
                return evaluateFragment;
        }
        return null;
    }

    @Override
    public void initRequest() {
    }

}
