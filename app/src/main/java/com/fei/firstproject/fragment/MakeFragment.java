package com.fei.firstproject.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.fei.firstproject.R;
import com.fei.firstproject.utils.LogUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/7/29.
 */

public class MakeFragment extends BaseFragment {

    @BindView(R.id.fab_shopping_car)
    FloatingActionButton fabShoppingCar;

    private boolean isShow = true;
    private Animation inAnimation;
    private Animation outAnimation;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (isShow) {
                fabShoppingCar.startAnimation(outAnimation);
            } else {
                fabShoppingCar.startAnimation(inAnimation);
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

    @Override
    public int getContentViewResId() {
        return R.layout.fragment_make;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        LogUtils.i("tag", "make");
        initAnimation();
    }

    private void initAnimation() {
        inAnimation = AnimationUtils.loadAnimation(activity, R.anim.actionbutton_in_animation);
        outAnimation = AnimationUtils.loadAnimation(activity, R.anim.actionbutton_out_animation);
    }

    @Override
    public void initRequest() {

    }

    @OnClick(R.id.fab_shopping_car)
    void clickShoppingCar(View view) {
        if (isShow) {
            //点击
            mHandler.removeMessages(1);
            mHandler.sendEmptyMessageDelayed(1, 2000);
        } else {
            mHandler.sendEmptyMessage(1);
        }
    }

    @OnClick(R.id.ll_fast_make)
    void clickFastMake(View view) {
        //快速定制

    }

    @OnClick(R.id.ll_fertilizer)
    void clickFertilizer(View view) {
        //肥料定制
    }

    @OnClick(R.id.ll_plan_make)
    void clickPlanMake(View view) {
        //方案定制
    }

    @OnClick(R.id.ll_plan_search)
    void clickPlanSearch(View view) {
        //方案查询
    }

}
