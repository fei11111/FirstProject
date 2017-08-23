package com.fei.firstproject.activity;

import android.view.View;

import com.fei.firstproject.R;
import com.fei.firstproject.adapter.MyMessageAdapter;
import com.fei.firstproject.entity.BaseEntity;
import com.fei.firstproject.entity.MessageEntity;
import com.fei.firstproject.http.BaseObserver;
import com.fei.firstproject.http.factory.RetrofitFactory;
import com.fei.firstproject.utils.Utils;
import com.fei.firstproject.widget.AppHeadView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

/**
 * Created by Administrator on 2017/8/21.
 */

public class MessageActivity extends BaseListActivity {

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
    public void initAppHeadView() {
        appHeadView.setFlHeadRightVisible(View.INVISIBLE);
        appHeadView.setMiddleStyle(AppHeadView.TEXT);
        appHeadView.setMiddleText(getResources().getString(R.string.my_message));
    }

    private void getMessage() {
        if (currentPage < 1) currentPage = 1;
        Map<String, String> map = new HashMap<>();
        map.put("currentPage", currentPage + "");
        map.put("pageSize", "10");
        map.put("userId", "1119200");//AppConfig.user.getId()
        final Observable<BaseEntity<List<MessageEntity>>> message = RetrofitFactory.getBtWeb().getMessage(map);
        message.compose(this.<BaseEntity<List<MessageEntity>>>createTransformer(true))
                .subscribe(new BaseObserver<List<MessageEntity>>(this) {
                    @Override
                    protected void onHandleSuccess(List<MessageEntity> messageEntities) {
                        refreshLayout.finishRefresh();
                        refreshLayout.finishLoadmore();
                        dismissLoading();
                        if (messageEntities != null && messageEntities.size() > 0) {
                            refreshLayout.setVisibility(View.VISIBLE);
                            if (messageAdapter == null) {
                                messageAdapter = new MyMessageAdapter(MessageActivity.this, messageEntities);
                                listView.setAdapter(messageAdapter);
                            } else {
                                if (currentPage == 1) {
                                    messageAdapter.setMessageList(messageEntities);
                                } else if (currentPage > 1) {
                                    messageAdapter.addMessageList(messageEntities);
                                }
                                messageAdapter.notifyDataSetChanged();
                            }
                        } else {
                            if (currentPage == 1) {
                                showNoDataView();
                            } else if (currentPage > 1) {
                                Utils.showToast(MessageActivity.this, "没有更多数据");
                            }
                        }
                    }

                    @Override
                    protected void onHandleError(String msg) {
                        super.onHandleError(msg);
                        currentPage--;
                        refreshLayout.finishRefresh();
                        refreshLayout.finishLoadmore();
                        showRequestErrorView();
                    }
                });
    }
}
