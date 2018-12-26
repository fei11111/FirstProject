package com.fei.firstproject.activity;

import android.view.View;

import com.fei.firstproject.R;
import com.fei.firstproject.adapter.MyAttentionAdapter;
import com.fei.firstproject.config.AppConfig;
import com.fei.firstproject.entity.BaseEntity;
import com.fei.firstproject.entity.ExpertEntity;
import com.fei.firstproject.http.HttpMgr;
import com.fei.firstproject.http.inter.CallBack;
import com.fei.firstproject.utils.Utils;
import com.fei.firstproject.widget.AppHeadView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/29.
 */

public class MyAttentionActivity extends BaseListActivity {

    private MyAttentionAdapter myAttentionAdapter;

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
    public void initRequest() {
        getExpert();
    }

    @Override
    public void initView() {
        appHeadView.setFlHeadRightVisible(View.INVISIBLE);
        appHeadView.setMiddleStyle(AppHeadView.TEXT);
        appHeadView.setMiddleText(getString(R.string.my_attention));
    }

    public void getExpert() {
        if (currentPage < 1) currentPage = 1;
        Map<String, String> map = new HashMap<>();
        map.put("userId", AppConfig.user.getId());
        map.put("currentPage", currentPage + "");
        HttpMgr.getExpert(this, map, new CallBack<List<ExpertEntity>>() {
            @Override
            public void onSuccess(List<ExpertEntity> expertEntities) {
                dismissLoading();
                refreshLayout.finishRefresh();
                refreshLayout.finishLoadmore();
                if (expertEntities != null && expertEntities.size() > 0) {
                    refreshLayout.setVisibility(View.VISIBLE);
                    if (myAttentionAdapter == null) {
                        myAttentionAdapter = new MyAttentionAdapter(MyAttentionActivity.this, expertEntities);
                        myAttentionAdapter.setOnCancelAttentionListener(onCancelAttentionListener);
                        recyclerView.setAdapter(myAttentionAdapter);
                    } else {
                        if (currentPage == 1) {
                            myAttentionAdapter.setExpertEntities(expertEntities);
                        } else {
                            myAttentionAdapter.addExpertEntities(expertEntities);
                        }
                        myAttentionAdapter.notifyDataSetChanged();
                    }
                } else {
                    if (currentPage == 1) {
                        showNoDataView();
                    } else {
                        currentPage--;
                        Utils.showToast(MyAttentionActivity.this, "没有更多数据");
                    }
                }
            }

            @Override
            public void onFail() {
                currentPage--;
                refreshLayout.finishRefresh();
                refreshLayout.finishLoadmore();
                showRequestErrorView();
            }
        });
    }

    private MyAttentionAdapter.OnCancelAttentionListener onCancelAttentionListener = new MyAttentionAdapter.OnCancelAttentionListener() {
        @Override
        public void cancle(ExpertEntity expertEntity) {
            cancelAttention(expertEntity);
        }
    };

    private void cancelAttention(final ExpertEntity expertEntity) {
        proShow();
        Map<String, String> map = new HashMap<>();
        map.put("userId", AppConfig.user.getId());
        map.put("expertId", expertEntity.getExpertId());
        HttpMgr.cancleAttention(this, map, new CallBack<BaseEntity>() {
            @Override
            public void onSuccess(BaseEntity baseEntity) {
                proDisimis();
                if (baseEntity.isState()) {
                    boolean remove = myAttentionAdapter.getExpertEntities().remove(expertEntity);
                    if (remove) {
                        myAttentionAdapter.notifyDataSetChanged();
                        Utils.showToast(MyAttentionActivity.this, "已取消关注");
                    } else {
                        Utils.showToast(MyAttentionActivity.this, "取消关注失败");
                    }
                } else {
                    Utils.showToast(MyAttentionActivity.this, "取消关注失败");
                }
            }

            @Override
            public void onFail() {
                proDisimis();
            }
        });
    }
}
