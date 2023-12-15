package com.fei.firstproject.activity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.common.viewmodel.EmptyViewModel;
import com.fei.firstproject.R;
import com.fei.firstproject.databinding.ActivityMyOrderBinding;
import com.fei.firstproject.fragment.OrderProjectFragment;
import com.fei.firstproject.utils.LogUtils;
import com.google.android.material.tabs.TabLayout;

/**
 * Created by Administrator on 2017/8/25.
 */

public class MyOrderActivity extends BaseProjectActivity<EmptyViewModel, ActivityMyOrderBinding> {

    //全部
    //待付款
    //待收货
    //待发货
    //待评价
    private int[] titles = {R.string.all, R.string.wait_obligation, R.string.wait_receive, R.string.wait_post, R.string.wait_evaluate};
    private OrderProjectFragment allFragment;
    private OrderProjectFragment obligationFragment;
    private OrderProjectFragment receiveFragment;
    private OrderProjectFragment postFragment;
    private OrderProjectFragment evaluateFragment;
    private FragmentManager mFragmentManager;
    private int selectPostion = 0;
    public static String SELECT_POSITION_EXTRA = "select_postion_extra";

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
    public void initTitle() {
        setBackTitle(getString(R.string.my_order));
    }


    private void initSetting() {
        mFragmentManager = getSupportFragmentManager();
        selectPostion = getIntent().getIntExtra(SELECT_POSITION_EXTRA, selectPostion);
    }

    private void initTableLayout() {
        for (int i = 0; i < titles.length; i++) {
            if (i == selectPostion) {
                mChildBinding.tableLayout.addTab(mChildBinding.tableLayout.newTab().setText(getString(titles[i])), true);
            } else {
                mChildBinding.tableLayout.addTab(mChildBinding.tableLayout.newTab().setText(getString(titles[i])), false);
            }
        }
    }

    private void initListener() {
        initTableLayoutListener();
    }

    private void initTableLayoutListener() {
        mChildBinding.tableLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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

    private void selectFragment(int position) {
        switch (position) {
            case 0:
                LogUtils.i("tag", "进入tab0");
                if (allFragment == null) {
                    allFragment = OrderProjectFragment.newInstance(position);
                    switchFragment(allFragment, true);
                } else {
                    switchFragment(allFragment, false);
                }
                break;
            case 1:
                LogUtils.i("tag", "进入tab1");
                if (obligationFragment == null) {
                    obligationFragment = OrderProjectFragment.newInstance(position);
                    switchFragment(obligationFragment, true);
                } else {
                    switchFragment(obligationFragment, false);
                }
                break;
            case 2:
                LogUtils.i("tag", "进入tab2");
                if (receiveFragment == null) {
                    receiveFragment = OrderProjectFragment.newInstance(position);
                    switchFragment(receiveFragment, true);
                } else {
                    switchFragment(receiveFragment, false);
                }
                break;
            case 3:
                LogUtils.i("tag", "进入tab3");
                if (postFragment == null) {
                    postFragment = OrderProjectFragment.newInstance(position);
                    switchFragment(postFragment, true);
                } else {
                    switchFragment(postFragment, false);
                }
                break;
            case 4:
                LogUtils.i("tag", "进入tab4");
                if (evaluateFragment == null) {
                    evaluateFragment = OrderProjectFragment.newInstance(position);
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

    @Override
    public void createObserver() {

    }

    @Override
    public void initViewAndData(Bundle savedInstanceState) {
        initSetting();
        initListener();
        initTableLayout();
    }
}
