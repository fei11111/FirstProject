package com.fei.firstproject.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fei.firstproject.R;
import com.fei.firstproject.activity.LoginActivity;
import com.fei.firstproject.activity.SettingsActivity;
import com.fei.firstproject.adapter.MyRecyclerViewAdapter;
import com.fei.firstproject.config.AppConfig;
import com.fei.firstproject.decoration.DividerGridItemDecoration;
import com.fei.firstproject.event.AllEvent;
import com.fei.firstproject.event.EventType;
import com.fei.firstproject.utils.LogUtils;
import com.fei.firstproject.widget.NoScrollRecyclerView;
import com.fei.firstproject.widget.SettingView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
    @BindView(R.id.recycler_me)
    NoScrollRecyclerView recycler_me;
    @BindView(R.id.recycler_other)
    NoScrollRecyclerView recycler_other;
    @BindView(R.id.tv_name)
    TextView tvName;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
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
        LogUtils.i("tag", "me");
        EventBus.getDefault().register(this);
        initInfo();
        initRecyclerView();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    void dealEvent(AllEvent allEvent) {
        if (allEvent.getEventType() == EventType.APP_LOGIN) {
            tvName.setText(AppConfig.user.getUserName());
            refreshLayout.setEnableRefresh(true);
        } else if (allEvent.getEventType() == EventType.APP_LOGOUT) {
            tvName.setText(getResources().getString(R.string.nologin));
            refreshLayout.setEnableRefresh(false);
        }
    }

    private void initInfo() {
        if (AppConfig.ISLOGIN) {
            tvName.setText(AppConfig.user.getUserName());
            refreshLayout.setEnableRefresh(true);
        } else {
            tvName.setText(getResources().getString(R.string.nologin));
            refreshLayout.setEnableRefresh(false);
        }
    }

    @Override
    public void initRequest() {
        if (AppConfig.ISLOGIN) {
            getUserInfo();
        }
    }

    private void getUserInfo() {
//        Observable<UserEntity> userInfo = RetrofitFactory.getBigDb().getUserInfo(AppConfig.user.getTokenId());
//        userInfo.compose(this.<UserEntity>createTransformer(false)).subscribe(new BaseWithoutBaseEntityObserver<UserEntity>(activity) {
//            @Override
//            protected void onHandleSuccess(UserEntity userEntity) {
//                SPUtils.put(activity, "user", userEntity);
//                AppConfig.ISLOGIN = true;
//                AppConfig.user = userEntity;
//            }
//        });
    }

    private void initRecyclerView() {
        initViewMe();
        initViewOther();
    }

    private void initViewMe() {
        GridLayoutManager manager = new GridLayoutManager(activity, 3);
        RecyclerView.ItemDecoration itemDecoration = new DividerGridItemDecoration(activity);
        recycler_me.setLayoutManager(manager);
        recycler_me.addItemDecoration(itemDecoration);
        recycler_me.setAdapter(new MyRecyclerViewAdapter(activity, R.array.list_me_drawable, getResources().getStringArray(R.array.list_me_str)));
    }

    private void initViewOther() {
        GridLayoutManager manager = new GridLayoutManager(activity, 3);
        RecyclerView.ItemDecoration itemDecoration = new DividerGridItemDecoration(activity);
        recycler_other.setLayoutManager(manager);
        recycler_other.addItemDecoration(itemDecoration);
        recycler_other.setAdapter(new MyRecyclerViewAdapter(activity, R.array.list_other_drawable, getResources().getStringArray(R.array.list_other_str)));
    }

    @OnClick(R.id.ll_me_info)
    void clickMeInfo(View view) {
        if (AppConfig.ISLOGIN) {
            startActivityWithoutCode(new Intent(activity, SettingsActivity.class));
        } else {
            startActivity(new Intent(activity, LoginActivity.class));
        }
    }

}
