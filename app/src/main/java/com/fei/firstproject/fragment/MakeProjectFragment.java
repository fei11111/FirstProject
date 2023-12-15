package com.fei.firstproject.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.common.viewmodel.EmptyViewModel;
import com.fei.firstproject.R;
import com.fei.firstproject.databinding.FragmentMakeBinding;
import com.fei.firstproject.utils.LogUtils;

/**
 * Created by Administrator on 2017/7/29.
 */

public class MakeProjectFragment extends BaseProjectFragment<EmptyViewModel, FragmentMakeBinding> {

    private boolean isShow = true;
    private Animation inAnimation;
    private Animation outAnimation;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (isShow) {
                mBinding.fabShoppingCar.startAnimation(outAnimation);
            } else {
                mBinding.fabShoppingCar.startAnimation(inAnimation);
                mHandler.sendEmptyMessageDelayed(1, 4000);
            }
            isShow = !isShow;
        }
    };

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            mHandler.removeMessages(1);
        } else {
            if (isShow) {
                mHandler.sendEmptyMessageDelayed(1, 2000);
            }
        }
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

    private void initAnimation() {
        inAnimation = AnimationUtils.loadAnimation(activity, R.anim.actionbutton_in_animation);
        outAnimation = AnimationUtils.loadAnimation(activity, R.anim.actionbutton_out_animation);
    }

    @Override
    public void initRequest() {

    }

    void clickShoppingCar() {
        mBinding.fabShoppingCar.setOnClickListener(v->{
            if (isShow) {
                //点击
                mHandler.removeMessages(1);
                mHandler.sendEmptyMessageDelayed(1, 2000);
            } else {
                mHandler.sendEmptyMessage(1);
            }
        });

    }


    void clickFastMake(View view) {
        //快速定制

    }

    void clickFertilizer(View view) {
        //肥料定制
    }

    void clickPlanMake(View view) {
        //方案定制
    }

    void clickPlanSearch(View view) {
        //方案查询
    }

    @Override
    public void createObserver() {
        clickShoppingCar();
    }

    @Override
    public void initViewAndData(Bundle savedInstanceState) {
        LogUtils.i("tag", "make");
        initAnimation();
    }
}
