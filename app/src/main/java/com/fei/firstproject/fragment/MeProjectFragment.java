package com.fei.firstproject.fragment;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.core.widget.NestedScrollView;

import com.common.viewmodel.EmptyViewModel;
import com.fei.firstproject.R;
import com.fei.firstproject.activity.LoginActivity;
import com.fei.firstproject.activity.MyAskActivity;
import com.fei.firstproject.activity.MyAttentionActivity;
import com.fei.firstproject.activity.MyOrderActivity;
import com.fei.firstproject.activity.RunActivity;
import com.fei.firstproject.activity.SelfInfoActivity;
import com.fei.firstproject.activity.VideoPlayerActivity;
import com.fei.firstproject.activity.WebActivity;
import com.fei.firstproject.adapter.MeFragmentAdapter;
import com.fei.firstproject.config.AppConfig;
import com.fei.firstproject.databinding.FragmentMeBinding;
import com.fei.firstproject.entity.UserEntity;
import com.fei.firstproject.event.AllEvent;
import com.fei.firstproject.event.EventType;
import com.fei.firstproject.http.BaseWithoutBaseEntityObserver;
import com.fei.firstproject.http.factory.RetrofitFactory;
import com.fei.firstproject.inter.OnItemClickListener;
import com.fei.firstproject.utils.LogUtils;
import com.fei.firstproject.utils.SPUtils;
import com.fei.firstproject.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;


/**
 * Created by Administrator on 2017/7/29.
 */

public class MeProjectFragment extends BaseProjectFragment<EmptyViewModel, FragmentMeBinding> {
    private MeFragmentAdapter meAdatper;
    private MeFragmentAdapter otherAdapter;


    private static final int REQUEST_PERMISSION_CODE_STORAGE = 100;
    private static final int REQUEST_FRAGMENT_CODE_CAMERA = 200;

    private boolean isAppHeadTitleChange = false;

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (isAppHeadTitleChange) {
                if (AppConfig.ISLOGIN) {
                    activity.getAppHeadView().setMiddleText(AppConfig.user.getUserName());
                } else {
                    activity.getAppHeadView().setMiddleText(getString(R.string.me));
                }
            } else {
                activity.getAppHeadView().setMiddleText(getString(R.string.me));
            }
        }
    }

    @Override
    public void permissionsDeniedCallBack(int requestCode) {
        if (requestCode == REQUEST_PERMISSION_CODE_STORAGE) {
            //存储权限
            showMissingPermissionDialog(getString(R.string.need_storage_permission), requestCode);
        }
    }

    @Override
    public void permissionsGrantCallBack(int requestCode) {
        if (requestCode == REQUEST_PERMISSION_CODE_STORAGE) {
            //存储权限
//            String url = "http://192.168.1.214:3391/btFile/videos/9cd31488-0707-46d6-aaa7-83a4a27c5e0d.mp4";
            String url = "http://220.170.49.103/5/q/c/b/t/qcbtgdrzcagiurhsrcszksmyhgtlvx/he.yinyuetai.com/0FF7014EAEF781F14E9784C3B30944E0.flv";
            Intent intent = new Intent(activity, VideoPlayerActivity.class);
            intent.putExtra("url", url);
            startActivity(intent);
        }
    }

    @Override
    public void permissionDialogDismiss(int requestCode) {
        if (requestCode == REQUEST_PERMISSION_CODE_STORAGE) {
            Utils.showToast(activity, getString(R.string.storage_permission_fail));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    private void initListener() {
        mBinding.swipeTarget.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (AppConfig.ISLOGIN) {
                if (scrollY >= mBinding.tvName.getY() + Utils.dip2px(activity, 16)) {
                    activity.getAppHeadView().setMiddleText(AppConfig.user.getUserName());
                    isAppHeadTitleChange = true;
                } else {
                    activity.getAppHeadView().setMiddleText(getString(R.string.me));
                    isAppHeadTitleChange = false;
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dealEvent(AllEvent allEvent) {
        if (allEvent.getEventType() == EventType.APP_LOGIN) {
            mBinding.tvName.setText(AppConfig.user.getUserName());
            mBinding.tvCollectGoods.setText("5");
            mBinding.tvCollectStore.setText("10");
        } else if (allEvent.getEventType() == EventType.APP_LOGOUT) {
            mBinding.tvName.setText(getResources().getString(R.string.nologin));
            mBinding.tvCollectGoods.setText("0");
            mBinding.tvCollectStore.setText("0");
        }
    }

    private void initInfo() {
        if (AppConfig.ISLOGIN) {
            mBinding.tvName.setText(AppConfig.user.getUserName());
        } else {
            mBinding.tvName.setText(getResources().getString(R.string.nologin));
        }
    }

    @Override
    public void initRequest() {
        if (AppConfig.ISLOGIN) {
            String tokenId = SPUtils.get(activity, "tokenId", "").toString();
            String deviceId = SPUtils.get(activity, "deviceId", "").toString();
            getUserInfo(tokenId, deviceId);
        } else {
            refreshLayout.setRefreshing(false);
            activity.showDialogWhenUnLogin();
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
                refreshLayout.setRefreshing(false);
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
                refreshLayout.setRefreshing(false);
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
        activity.setGridRecycleViewSetting(mBinding.recyclerMe, activity, 3);
        meAdatper = new MeFragmentAdapter(activity, R.array.list_me_drawable, getResources().getStringArray(R.array.list_me_str));
        meAdatper.setOnItemClickListener(meItemClickListener);
        mBinding.recyclerMe.setAdapter(meAdatper);
    }

    private void initViewOther() {
        activity.setGridRecycleViewSetting(mBinding.recyclerOther, activity, 3);
        otherAdapter = new MeFragmentAdapter(activity, R.array.list_other_drawable, getResources().getStringArray(R.array.list_other_str));
        otherAdapter.setOnItemClickListener(otherItemClickListener);
        mBinding.recyclerOther.setAdapter(otherAdapter);
    }

    private OnItemClickListener meItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(View view) {
            if (meAdatper != null) {
                if (AppConfig.ISLOGIN) {
                    int position = mBinding.recyclerMe.getChildAdapterPosition(view);
                    switch (position) {
                        case 0:
                            startActivityWithoutCode(new Intent(activity, MyAskActivity.class));
                            break;
                        case 3:
                            startActivityWithoutCode(new Intent(activity, MyAttentionActivity.class));
                            break;
                    }
                } else {
                    activity.showDialogWhenUnLogin();
                }
            }
        }
    };

    private OnItemClickListener otherItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(View view) {
            if (otherAdapter != null) {
                if (AppConfig.ISLOGIN) {
                    int position = mBinding.recyclerOther.getChildAdapterPosition(view);
                    switch (position) {
                        case 4:
                            checkPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE_STORAGE);
                            break;
                        case 5:
                            startActivityWithoutCode(new Intent(activity, RunActivity.class));
                    }
                } else {
                    activity.showDialogWhenUnLogin();
                }
            }
        }
    };

    void clickMeInfo() {
        mBinding.llMeInfo.setOnClickListener(v -> {
            if (AppConfig.ISLOGIN) {
                startActivityWithoutCode(new Intent(activity, SelfInfoActivity.class));
            } else {
                startActivity(new Intent(activity, LoginActivity.class));
            }
        });
    }

    void clickMyOrder() {
        mBinding.rlOrder.setOnClickListener(v -> {
            if (AppConfig.ISLOGIN) {
                Intent intent = new Intent(activity, MyOrderActivity.class);
                intent.putExtra(MyOrderActivity.SELECT_POSITION_EXTRA, 0);
                startActivityWithoutCode(intent);
            } else {
                activity.showDialogWhenUnLogin();
            }
        });
    }

    void clickWaitObligation() {
        mBinding.llWaitObligation.setOnClickListener(v -> {
            if (AppConfig.ISLOGIN) {
                Intent intent = new Intent(activity, MyOrderActivity.class);
                intent.putExtra(MyOrderActivity.SELECT_POSITION_EXTRA, 1);
                startActivityWithoutCode(intent);
            } else {
                activity.showDialogWhenUnLogin();
            }
        });

    }

    void clickWaitReceive() {
        mBinding.llWaitReceive.setOnClickListener(v -> {
            if (AppConfig.ISLOGIN) {
                Intent intent = new Intent(activity, MyOrderActivity.class);
                intent.putExtra(MyOrderActivity.SELECT_POSITION_EXTRA, 2);
                startActivityWithoutCode(intent);
            } else {
                activity.showDialogWhenUnLogin();
            }
        });

    }


    void clickWaitPost() {
        mBinding.llWaitPost.setOnClickListener(v -> {
            if (AppConfig.ISLOGIN) {
                Intent intent = new Intent(activity, MyOrderActivity.class);
                intent.putExtra(MyOrderActivity.SELECT_POSITION_EXTRA, 3);
                startActivityWithoutCode(intent);
            } else {
                activity.showDialogWhenUnLogin();
            }
        });

    }

    void clickWaitEvaluate() {
        mBinding.llWaitEvaluate.setOnClickListener(v -> {
            if (AppConfig.ISLOGIN) {
                Intent intent = new Intent(activity, MyOrderActivity.class);
                intent.putExtra(MyOrderActivity.SELECT_POSITION_EXTRA, 4);
                startActivityWithoutCode(intent);
            } else {
                activity.showDialogWhenUnLogin();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtils.i("tag", "fragment - onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_FRAGMENT_CODE_CAMERA:
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
                    break;
            }
        }
    }

    @Override
    public void createObserver() {
        clickMeInfo();
        clickMyOrder();
        clickWaitObligation();
        clickWaitReceive();
        clickWaitPost();
        clickWaitEvaluate();
    }

    @Override
    public void initViewAndData(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        LogUtils.i("tag", "me");
        initInfo();
        initRecyclerView();
        initListener();
    }
}
