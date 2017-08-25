package com.fei.firstproject.fragment;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fei.firstproject.R;
import com.fei.firstproject.activity.CaptureActivity;
import com.fei.firstproject.activity.LoginActivity;
import com.fei.firstproject.activity.MyOrderActivity;
import com.fei.firstproject.activity.SettingsActivity;
import com.fei.firstproject.adapter.MeFragmentAdapter;
import com.fei.firstproject.config.AppConfig;
import com.fei.firstproject.decoration.DividerGridItemDecoration;
import com.fei.firstproject.entity.UserEntity;
import com.fei.firstproject.event.AllEvent;
import com.fei.firstproject.event.EventType;
import com.fei.firstproject.http.BaseWithoutBaseEntityObserver;
import com.fei.firstproject.http.factory.RetrofitFactory;
import com.fei.firstproject.utils.LogUtils;
import com.fei.firstproject.utils.SPUtils;
import com.fei.firstproject.utils.Utils;
import com.fei.firstproject.web.WebActivity;
import com.fei.firstproject.widget.NoScrollRecyclerView;
import com.fei.firstproject.widget.SettingView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;

import static android.app.Activity.RESULT_OK;

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

    private static final int REQUEST_PERMISSION_CODE_CAMERA = 100;
    private static final int REQUEST_FRAGMENT_CODE_CAMERA = 200;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void permissionsDeniedCallBack(int requestCode) {
        if (requestCode == REQUEST_PERMISSION_CODE_CAMERA) {
            //相机权限
            showMissingPermissionDialog(getString(R.string.need_camera_permission_to_scan), requestCode);
        }
    }

    @Override
    public void permissionsGrantCallBack(int requestCode) {
        if (requestCode == REQUEST_PERMISSION_CODE_CAMERA) {
            //相机权限
            startActivityWithCode(new Intent(activity, CaptureActivity.class), REQUEST_FRAGMENT_CODE_CAMERA);
        }
    }

    @Override
    public void permissionDialogDismiss(int requestCode) {
        if (requestCode == REQUEST_PERMISSION_CODE_CAMERA) {
            Utils.showToast(activity, getString(R.string.camera_permission_fail));
        }
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
            String tokenId = SPUtils.get(activity, "tokenId", "").toString();
            String deviceId = SPUtils.get(activity, "deviceId", "").toString();
            getUserInfo(tokenId, deviceId);
        }
    }

    private void getUserInfo(String tokenId, String deviceId) {
        Map<String, String> map = new HashMap<>();
        map.put("token", tokenId);
        map.put("deviceID", deviceId);
        Observable<UserEntity> userInfo = RetrofitFactory.getBigDb().getUserInfo(map);
        userInfo.compose(this.<UserEntity>createTransformer(false)).subscribe(new BaseWithoutBaseEntityObserver<UserEntity>(activity) {
            @Override
            protected void onHandleSuccess(UserEntity userEntity) {
                refreshLayout.finishRefresh();
                if (userEntity != null) {
                    if (userEntity.getSuccess().equals("YES")) {
                        activity.refreshUserInfoWhenLogin(userEntity);
                    } else {
                        activity.refreshUserInfoWhenLogout();
                    }
                } else {
                    activity.refreshUserInfoWhenLogout();
                }
            }

            @Override
            protected void onHandleError(String msg) {
                refreshLayout.finishRefresh();
                super.onHandleError(msg);
                activity.refreshUserInfoWhenLogout();
            }
        });
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
        recycler_me.setAdapter(new MeFragmentAdapter(activity, R.array.list_me_drawable, getResources().getStringArray(R.array.list_me_str)));
    }

    private void initViewOther() {
        GridLayoutManager manager = new GridLayoutManager(activity, 3);
        RecyclerView.ItemDecoration itemDecoration = new DividerGridItemDecoration(activity);
        recycler_other.setLayoutManager(manager);
        recycler_other.addItemDecoration(itemDecoration);
        recycler_other.setAdapter(new MeFragmentAdapter(activity, R.array.list_other_drawable, getResources().getStringArray(R.array.list_other_str)));
    }

    @OnClick(R.id.iv_scan)
    void clickScan(View view) {
        checkPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CODE_CAMERA);
    }

    @OnClick(R.id.ll_me_info)
    void clickMeInfo(View view) {
        if (AppConfig.ISLOGIN) {
            startActivityWithoutCode(new Intent(activity, SettingsActivity.class));
        } else {
            startActivity(new Intent(activity, LoginActivity.class));
        }
    }

    @OnClick(R.id.rl_order)
    void clickMyOrder(View view) {
//        if (AppConfig.ISLOGIN) {
            startActivityWithoutCode(new Intent(activity, MyOrderActivity.class));
//        } else {
//            activity.showDialogWhenUnLogin();
//        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtils.i("tag", "fragment - onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_FRAGMENT_CODE_CAMERA) {
                if (data != null) {
                    String result = data.getStringExtra("result");
                    if (!TextUtils.isEmpty(result)) {
                        if (result.contains("http") || result.contains("https")) {
                            Intent intent = new Intent(activity, WebActivity.class);
                            intent.putExtra("url", result);
                            startActivityWithoutCode(intent);
                        } else {
                            Utils.showToast(activity, getResources().getString(R.string.scan_error));
                        }
                    }
                }
            }
        }
    }
}
