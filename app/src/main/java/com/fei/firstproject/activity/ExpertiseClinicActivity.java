package com.fei.firstproject.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSON;
import com.common.viewmodel.EmptyViewModel;
import com.fei.firstproject.R;
import com.fei.firstproject.adapter.HotQuestionAdapter;
import com.fei.firstproject.adapter.UnSolveQuestionAdapter;
import com.fei.firstproject.adapter.UrgentExpertisePagerAdapter;
import com.fei.firstproject.config.AppConfig;
import com.fei.firstproject.databinding.ActivityExpertiseClinicBinding;
import com.fei.firstproject.entity.HotQuestionEntity;
import com.fei.firstproject.entity.UnSolveQuestionEntity;
import com.fei.firstproject.entity.UrgentExpertEntity;
import com.fei.firstproject.http.HttpMgr;
import com.fei.firstproject.http.inter.CallBack;
import com.fei.firstproject.utils.Utils;
import com.fei.firstproject.widget.AppHeadView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2017/9/12.
 */

public class ExpertiseClinicActivity extends BaseProjectActivity<EmptyViewModel, ActivityExpertiseClinicBinding> {

    private UrgentExpertisePagerAdapter urgentExpertiseAdapter;
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
    public void initTitle() {
        appHeadView.setFlHeadLeftPadding(getResources().getDimensionPixelSize(R.dimen.size_10));
        appHeadView.setLeftStyle(AppHeadView.IMAGE);
        appHeadView.setFlHeadLeftVisible(View.VISIBLE);
        appHeadView.setLeftDrawable(R.drawable.selector_head_left_arrow);
        appHeadView.setMiddleStyle(AppHeadView.SEARCH);
        appHeadView.setMiddleSearchHint(getString(R.string.hot_question_hint));
        appHeadView.setFlHeadRightVisible(View.VISIBLE);
        appHeadView.setRightStyle(AppHeadView.TEXT);
        appHeadView.setRightText(getString(R.string.search));
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
        setLinearRecycleViewSetting(mChildBinding.rvHotQuestion, this);
        setLinearRecycleViewSetting(mChildBinding.rvUnsolveQuestion, this);
    }

    private void initArrow() {
        mChildBinding.ivLeftArrow.setEnabled(false);
    }

    private void initListener() {
        initAppHeadViewListener();
        initViewPagerListener();
    }

    private void initViewPagerListener() {
        mChildBinding.vpPro.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    mChildBinding.ivLeftArrow.setEnabled(false);
                } else if (urgentExpertiseSize != -1) {
                    if (position + 1 == urgentExpertiseSize) {
                        mChildBinding.ivRightArrow.setEnabled(false);
                    } else {
                        mChildBinding.ivLeftArrow.setEnabled(true);
                        mChildBinding.ivRightArrow.setEnabled(true);
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
        HttpMgr.getHotQuestion(this, map, new CallBack<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) {
                dismissLoading();
                refreshLayout.finishRefresh();
                try {
                    String response = responseBody.string();
                    List<HotQuestionEntity> hotQuestionEntities = JSON.parseArray(response,
                            HotQuestionEntity.class);
                    if (hotQuestionEntities != null && hotQuestionEntities.size() > 0) {
                        refreshLayout.setVisibility(View.VISIBLE);
                        mChildBinding.llHotQuestion.setVisibility(View.VISIBLE);
                        if (hotQuestionAdapter == null) {
                            hotQuestionAdapter = new HotQuestionAdapter(ExpertiseClinicActivity.this, hotQuestionEntities);
                            mChildBinding.rvHotQuestion.setAdapter(hotQuestionAdapter);
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
            public void onFail() {
                refreshLayout.finishRefresh();
                showRequestErrorView();
            }
        });
    }

    private void getUrgentExpertise() {
        HttpMgr.getUrgentExpertise(this, new CallBack<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) {
                try {
                    String response = responseBody.string();
                    JSONObject json = new JSONObject(response);
                    String data = json.getString("data");
                    if (!TextUtils.isEmpty(data)) {
                        List<UrgentExpertEntity> urgentExpertEntities = JSON.parseArray(data,
                                UrgentExpertEntity.class);
                        if (urgentExpertEntities != null && urgentExpertEntities.size() > 0) {
                            mChildBinding.llUrgentPro.setVisibility(View.VISIBLE);
                            if (urgentExpertiseAdapter == null) {
                                urgentExpertiseAdapter = new UrgentExpertisePagerAdapter(ExpertiseClinicActivity.this, urgentExpertEntities, mChildBinding.vpPro);
                                mChildBinding.vpPro.setAdapter(urgentExpertiseAdapter);
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

            @Override
            public void onFail() {

            }
        });
    }

    private void getUnSolveQuestion() {
        Map<String, String> map = new HashMap<>();
        map.put("userId", AppConfig.user.getId());
        map.put("more", "N");
        HttpMgr.getUnSolveQuestion(this, map, new CallBack<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) {
                try {
                    String response = responseBody.string();
                    List<UnSolveQuestionEntity> unSolveQuestionEntities = JSON.parseArray(response,
                            UnSolveQuestionEntity.class);
                    if (unSolveQuestionEntities != null && unSolveQuestionEntities.size() > 0) {
                        mChildBinding.llUnsolveQuestion.setVisibility(View.VISIBLE);
                        if (unSolveQuestionAdapter == null) {
                            unSolveQuestionAdapter = new UnSolveQuestionAdapter(ExpertiseClinicActivity.this, unSolveQuestionEntities);
                            mChildBinding.rvUnsolveQuestion.setAdapter(unSolveQuestionAdapter);
                        } else {
                            unSolveQuestionAdapter.setUnSolveQuestionEntities(unSolveQuestionEntities);
                            urgentExpertiseAdapter.notifyDataSetChanged();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail() {

            }
        });
    }

    void clickLeftRight(View view) {

        if (urgentExpertiseSize == -1) return;
        int currentItem = mChildBinding.vpPro.getCurrentItem();
        switch (view.getId()) {
            case R.id.ll_left:
                if (currentItem != 0) {
                    mChildBinding.vpPro.setCurrentItem(currentItem - 1);
                }
                break;
            case R.id.ll_right:
                if (currentItem != urgentExpertiseSize - 1) {
                    mChildBinding.vpPro.setCurrentItem(currentItem + 1);
                }
                break;
        }
    }

    @Override
    public void createObserver() {
        mChildBinding.llRight.setOnClickListener(v -> clickLeftRight(v));
        mChildBinding.llLeft.setOnClickListener(v -> clickLeftRight(v));
    }

    @Override
    public void initViewAndData(Bundle savedInstanceState) {
        initListener();
        initArrow();
        initRecycleView();
    }
}
