package com.fei.firstproject.activity;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.fei.banner.view.BannerViewPager;
import com.fei.firstproject.R;
import com.fei.firstproject.adapter.UrgentExpertiseAdapter;
import com.fei.firstproject.config.AppConfig;
import com.fei.firstproject.entity.UrgentExpertEntity;
import com.fei.firstproject.http.BaseWithoutBaseEntityObserver;
import com.fei.firstproject.http.factory.RetrofitFactory;
import com.fei.firstproject.utils.Utils;
import com.fei.firstproject.widget.AppHeadView;
import com.fei.firstproject.widget.NoScrollRecyclerView;
import com.fei.firstproject.widget.PartHeadView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Observable;
import okhttp3.ResponseBody;

import static com.fei.firstproject.R.id.ll_urgent_pro;
import static com.fei.firstproject.R.id.vp_pro;

/**
 * Created by Administrator on 2017/9/12.
 */

public class ExpertiseClinicActivity extends BaseActivity {

    @BindView(R.id.ll_expertise_room)
    LinearLayout llExpertiseRoom;
    @BindView(R.id.ll_emergency_room)
    LinearLayout llEmergencyRoom;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.ll_left)
    LinearLayout llLeft;
    @BindView(vp_pro)
    BannerViewPager vpPro;
    @BindView(R.id.ll_right)
    LinearLayout llRight;
    @BindView(ll_urgent_pro)
    LinearLayout llUrgentPro;
    @BindView(R.id.phv_unsolve_question)
    PartHeadView phvUnsolveQuestion;
    @BindView(R.id.rv_unsolve_question)
    NoScrollRecyclerView rvUnsolveQuestion;
    @BindView(R.id.phv_hot_question)
    PartHeadView phvHotQuestion;
    @BindView(R.id.rv_hot_question)
    NoScrollRecyclerView rvHotQuestion;

    private UrgentExpertiseAdapter urgentExpertiseAdapter;

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
        return R.layout.activity_expertise_clinic;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        initListener();
    }

    private void initListener() {
        appHeadView.setOnLeftRightClickListener(new AppHeadView.onAppHeadViewListener() {
            @Override
            public void onLeft(View view) {
                onBackPressed();
            }

            @Override
            public void onRight(View view) {
                Utils.hideKeyBoard(ExpertiseClinicActivity.this);
                getHotQuestion();
            }

            @Override
            public void onEdit(TextView v, int actionId, KeyEvent event) {
                Utils.hideKeyBoard(ExpertiseClinicActivity.this);
                getHotQuestion();
            }
        });
    }

    private void getHotQuestion() {
        String question = appHeadView.getEtSearchText();
        dismissLoading();
        refreshLayout.setVisibility(View.VISIBLE);
    }

    private void getUrgentExpertise() {
        Observable<ResponseBody> urgentExpertEntity = RetrofitFactory.getBtWeb().getUrgentExpertEntity();
        urgentExpertEntity.compose(this.<ResponseBody>createTransformer(false))
                .subscribe(new BaseWithoutBaseEntityObserver<ResponseBody>(this) {
                    @Override
                    protected void onHandleSuccess(ResponseBody responseBody) {
                        try {
                            String response = responseBody.string();
                            JSONObject json = new JSONObject(response);
                            String data = json.getString("data");
                            if (!TextUtils.isEmpty(data)) {
                                List<UrgentExpertEntity> urgentExpertEntities = JSON.parseArray(data,
                                        UrgentExpertEntity.class);
                                if (urgentExpertEntities != null && urgentExpertEntities.size() > 0) {
                                    llUrgentPro.setVisibility(View.VISIBLE);
                                    if (urgentExpertiseAdapter == null) {
                                        urgentExpertiseAdapter = new UrgentExpertiseAdapter(ExpertiseClinicActivity.this, urgentExpertEntities, vpPro);
                                        vpPro.setAdapter(urgentExpertiseAdapter);
                                    } else {
                                        urgentExpertiseAdapter.setExpertEntities(urgentExpertEntities);
                                        urgentExpertiseAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void initRequest() {
        if (AppConfig.ISLOGIN) {
        }
        getHotQuestion();
        getUrgentExpertise();
    }
}
