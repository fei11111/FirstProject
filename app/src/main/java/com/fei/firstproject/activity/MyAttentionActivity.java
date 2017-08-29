package com.fei.firstproject.activity;

import android.view.View;

import com.fei.firstproject.R;
import com.fei.firstproject.adapter.MyAttentionAdapter;
import com.fei.firstproject.config.AppConfig;
import com.fei.firstproject.entity.BaseEntity;
import com.fei.firstproject.entity.ExpertEntity;
import com.fei.firstproject.http.BaseObserver;
import com.fei.firstproject.http.factory.RetrofitFactory;
import com.fei.firstproject.widget.AppHeadView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

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
    public void initAppHeadView() {
        appHeadView.setFlHeadRightVisible(View.INVISIBLE);
        appHeadView.setMiddleStyle(AppHeadView.TEXT);
        appHeadView.setMiddleText(getString(R.string.my_attention));
    }

    public void getExpert() {
        if (currentPage < 1) currentPage = 1;
        Map<String, String> map = new HashMap<>();
        map.put("userId", AppConfig.user.getId());
        map.put("currentPage", currentPage + "");
        Observable<BaseEntity<List<ExpertEntity>>> expert = RetrofitFactory.getBtWeb().getExpert(map);
        expert.compose(this.<BaseEntity<List<ExpertEntity>>>createTransformer(true))
                .subscribe(new BaseObserver<List<ExpertEntity>>(this) {
                    @Override
                    protected void onHandleSuccess(List<ExpertEntity> expertEntities) {
                        dismissLoading();
                        refreshLayout.finishRefresh();
                        refreshLayout.finishLoadmore();
                        if (expertEntities != null && expertEntities.size() > 0) {
                            recyclerView.setVisibility(View.VISIBLE);
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
