package com.fei.firstproject.activity;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.fei.banner.view.BannerViewPager;
import com.fei.firstproject.R;
import com.fei.firstproject.adapter.HotQuestionAdapter;
import com.fei.firstproject.adapter.UnSolveQuestionAdapter;
import com.fei.firstproject.adapter.UrgentExpertiseAdapter;
import com.fei.firstproject.config.AppConfig;
import com.fei.firstproject.entity.HotQuestionEntity;
import com.fei.firstproject.entity.UnSolveQuestionEntity;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import okhttp3.ResponseBody;

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
    @BindView(R.id.ll_right)
    LinearLayout llRight;
    @BindView(R.id.phv_unsolve_question)
    PartHeadView phvUnsolveQuestion;
    @BindView(R.id.rv_unsolve_question)
    NoScrollRecyclerView rvUnsolveQuestion;
    @BindView(R.id.phv_hot_question)
    PartHeadView phvHotQuestion;
    @BindView(R.id.rv_hot_question)
    NoScrollRecyclerView rvHotQuestion;
    @BindView(R.id.iv_left_arrow)
    ImageView ivLeftArrow;
    @BindView(R.id.iv_right_arrow)
    ImageView ivRightArrow;
    @BindView(R.id.vp_pro)
    BannerViewPager vpPro;
    @BindView(R.id.ll_urgent_pro)
    LinearLayout llUrgentPro;
    @BindView(R.id.ll_unsolve_question)
    LinearLayoutCompat llUnsolveQuestion;
    @BindView(R.id.ll_hot_question)
    LinearLayoutCompat llHotQuestion;

    private UrgentExpertiseAdapter urgentExpertiseAdapter;
    private int urgentExpertiseSize = -1;
    private HotQuestionAdapter hotQuestionAdapter;
    private UnSolveQuestionAdapter unSolveQuestionAdapter;

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
        initArrow();
        initRecycleView();
    }

    @Override
    public void initRequest() {
        if (AppConfig.ISLOGIN) {
            getUnSolveQuestion();
        }
        getHotQuestion();
        getUrgentExpertise();
    }

    private void initRecycleView() {
        setRecycleViewSetting(rvHotQuestion);
        setRecycleViewSetting(rvUnsolveQuestion);
    }

    private void initArrow() {
        ivLeftArrow.setEnabled(false);
    }

    private void initListener() {
        initAppHeadViewListener();
        initViewPagerListener();
    }

    private void initViewPagerListener() {
        vpPro.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    ivLeftArrow.setEnabled(false);
                } else if (urgentExpertiseSize != -1) {
                    if (position + 1 == urgentExpertiseSize) {
                        ivRightArrow.setEnabled(false);
                    } else {
                        ivLeftArrow.setEnabled(true);
                        ivRightArrow.setEnabled(true);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initAppHeadViewListener() {
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
        Map<String, String> map = new HashMap<String, String>();
        map.put("question", question);
        map.put("currentPage", 1 + "");
        map.put("limit", 5 + "");
        final Observable<ResponseBody> hotQuestion = RetrofitFactory.getBtWeb().getHotQuestion(map);
        hotQuestion.compose(this.<ResponseBody>createTransformer(true))
                .subscribe(new BaseWithoutBaseEntityObserver<ResponseBody>(this) {
                    @Override
                    protected void onHandleSuccess(ResponseBody responseBody) {
                        dismissLoading();
                        refreshLayout.finishRefresh();
                        try {
                            String response = responseBody.string();
                            List<HotQuestionEntity> hotQuestionEntities = JSON.parseArray(response,
                                    HotQuestionEntity.class);
                            if (hotQuestionEntities != null && hotQuestionEntities.size() > 0) {
                                refreshLayout.setVisibility(View.VISIBLE);
                                llHotQuestion.setVisibility(View.VISIBLE);
                                if (hotQuestionAdapter == null) {
                                    hotQuestionAdapter = new HotQuestionAdapter(ExpertiseClinicActivity.this, hotQuestionEntities);
                                    rvHotQuestion.setAdapter(hotQuestionAdapter);
                                } else {
                                    hotQuestionAdapter.setHotQuestionEntityList(hotQuestionEntities);
                                    hotQuestionAdapter.notifyDataSetChanged();
                                }
                            } else {
                                showNoDataView();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            showNoDataView();
                        }
                    }

                    @Override
                    protected void onHandleError(String msg) {
                        super.onHandleError(msg);
                        refreshLayout.finishRefresh();
                        showRequestErrorView();
                    }
                });
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
                                    urgentExpertiseSize = urgentExpertEntities.size();
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

    private void getUnSolveQuestion() {
        Map<String, String> map = new HashMap<>();
        map.put("userId", AppConfig.user.getId());
        map.put("more", "N");
        Observable<ResponseBody> unSolveQuestion = RetrofitFactory.getBtWeb().getUnSolveQuestion(map);
        unSolveQuestion.compose(this.<ResponseBody>createTransformer(false))
                .subscribe(new BaseWithoutBaseEntityObserver<ResponseBody>(this) {
                    @Override
                    protected void onHandleSuccess(ResponseBody responseBody) {
                        try {
                            String response = responseBody.string();
                            List<UnSolveQuestionEntity> unSolveQuestionEntities = JSON.parseArray(response,
                                    UnSolveQuestionEntity.class);
                            if (unSolveQuestionEntities != null && unSolveQuestionEntities.size() > 0) {
                                llUnsolveQuestion.setVisibility(View.VISIBLE);
                                if (unSolveQuestionAdapter == null) {
                                    unSolveQuestionAdapter = new UnSolveQuestionAdapter(ExpertiseClinicActivity.this, unSolveQuestionEntities);
                                    rvUnsolveQuestion.setAdapter(unSolveQuestionAdapter);
                                } else {
                                    unSolveQuestionAdapter.setUnSolveQuestionEntities(unSolveQuestionEntities);
                                    urgentExpertiseAdapter.notifyDataSetChanged();
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void setRecycleViewSetting(RecyclerView recycleViewSetting) {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, LinearLayout.VERTICAL);
        recycleViewSetting.setLayoutManager(manager);
        recycleViewSetting.addItemDecoration(dividerItemDecoration);
    }

    @OnClick({R.id.ll_left, R.id.ll_right})
    void clickLeftRight(View view) {
        if (urgentExpertiseSize == -1) return;
        int currentItem = vpPro.getCurrentItem();
        switch (view.getId()) {
            case R.id.ll_left:
                if (currentItem != 0) {
                    vpPro.setCurrentItem(currentItem - 1);
                }
                break;
            case R.id.ll_right:
                if (currentItem != urgentExpertiseSize - 1) {
                    vpPro.setCurrentItem(currentItem + 1);
                }
                break;
        }
    }

}
