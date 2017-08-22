package com.fei.firstproject.activity;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.fei.firstproject.R;
import com.fei.firstproject.adapter.MyMessageAdapter;
import com.fei.firstproject.config.AppConfig;
import com.fei.firstproject.decoration.DividerItemDecoration;
import com.fei.firstproject.entity.BaseEntity;
import com.fei.firstproject.entity.MessageEntity;
import com.fei.firstproject.http.BaseObserver;
import com.fei.firstproject.http.factory.RetrofitFactory;
import com.fei.firstproject.utils.Utils;
import com.fei.firstproject.widget.AppHeadView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import io.reactivex.Observable;

/**
 * Created by Administrator on 2017/8/21.
 */

public class MessageActivity extends BaseActivity {

    @BindView(R.id.appHeadView)
    AppHeadView appHeadView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.recycler_message)
    RecyclerView recyclerMessage;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    private int currentPage = 1;
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
    public int getContentViewResId() {
        return R.layout.activity_message;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        initRecycler();
        initListener();
    }

    @Override
    public void initRequest() {
        getMessage();
    }

    private void getMessage() {
        if (currentPage < 1) currentPage = 1;
        Map<String, String> map = new HashMap<>();
        map.put("currentPage", currentPage + "");
        map.put("pageSize", "10");
        map.put("userId", AppConfig.user.getId());
        final Observable<BaseEntity<List<MessageEntity>>> message = RetrofitFactory.getBtWeb().getMessage(map);
        message.compose(this.<BaseEntity<List<MessageEntity>>>createTransformer())
                .subscribe(new BaseObserver<List<MessageEntity>>(this) {
                    @Override
                    protected void onHandleSuccess(List<MessageEntity> messageEntities) {
                        dismissLoading();
                        if (messageEntities.size() > 0) {
                            refreshLayout.setVisibility(View.VISIBLE);
                            if (messageAdapter == null) {
                                messageAdapter = new MyMessageAdapter(MessageActivity.this, messageEntities);
                                recyclerMessage.setAdapter(messageAdapter);
                            } else {
                                messageAdapter.addMessageList(messageEntities);
                                messageAdapter.notifyDataSetChanged();
                            }
                        } else {
                            if (messageAdapter == null) {
                                showNoDataView();
                            } else {
                                Utils.showToast(MessageActivity.this, "没有更多数据");
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        showRequestErrorView();
                    }
                });
    }

    private void initListener() {
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

    private void initRecycler() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        recyclerMessage.setLayoutManager(linearLayoutManager);
        recyclerMessage.addItemDecoration(itemDecoration);
    }

}
