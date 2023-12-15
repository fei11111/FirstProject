package com.fei.firstproject.activity;

import android.view.View;

import com.common.viewmodel.EmptyViewModel;
import com.fei.firstproject.R;
import com.fei.firstproject.adapter.MyMessageAdapter;
import com.fei.firstproject.config.AppConfig;
import com.fei.firstproject.entity.MessageEntity;
import com.fei.firstproject.http.HttpMgr;
import com.fei.firstproject.http.inter.CallBack;
import com.fei.firstproject.utils.Utils;
import com.fei.firstproject.widget.AppHeadView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/21.
 */

public class MessageActivity extends BaseListActivity<EmptyViewModel> {

    private MyMessageAdapter messageAdapter;

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
        getMessage();
    }

    @Override
    public void initView() {
        appHeadView.setFlHeadRightVisible(View.INVISIBLE);
        appHeadView.setMiddleStyle(AppHeadView.TEXT);
        appHeadView.setMiddleText(getResources().getString(R.string.my_message));
    }

    private void getMessage() {
        if (currentPage < 1) currentPage = 1;
        Map<String, String> map = new HashMap<>();
        map.put("currentPage", currentPage + "");
        map.put("pageSize", "10");
        map.put("userId", AppConfig.user.getId());
        HttpMgr.getMessage(this, map, new CallBack<List<MessageEntity>>() {
            @Override
            public void onSuccess(List<MessageEntity> messageEntities) {
                dismissLoading();
                refreshLayout.finishRefresh();
                refreshLayout.finishLoadmore();
                if (messageEntities != null && messageEntities.size() > 0) {
                    refreshLayout.setVisibility(View.VISIBLE);
                    if (messageAdapter == null) {
                        messageEntities.addAll(messageEntities);
                        messageEntities.addAll(messageEntities);
                        messageEntities.addAll(messageEntities);
                        messageEntities.addAll(messageEntities);
                        messageEntities.addAll(messageEntities);
                        messageAdapter = new MyMessageAdapter(MessageActivity.this, messageEntities);
                        recyclerView.setAdapter(messageAdapter);
                    } else {
                        if (currentPage == 1) {
                            messageAdapter.setMessageEntities(messageEntities);
                        } else if (currentPage > 1) {
                            messageAdapter.addMessageList(messageEntities);
                        }
                        messageAdapter.notifyDataSetChanged();
                    }
                } else {
                    if (currentPage == 1) {
                        showNoDataView();
                    } else if (currentPage > 1) {
                        currentPage--;
                        Utils.showToast(MessageActivity.this, "没有更多数据");
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

    @Override
    public void createObserver() {

    }
}
