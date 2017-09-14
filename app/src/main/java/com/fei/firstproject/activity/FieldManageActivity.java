package com.fei.firstproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fei.firstproject.R;
import com.fei.firstproject.adapter.RecommendPlanAdapter;
import com.fei.firstproject.config.AppConfig;
import com.fei.firstproject.entity.BaseEntity;
import com.fei.firstproject.entity.RecommendEntity;
import com.fei.firstproject.entity.ShareEntity;
import com.fei.firstproject.http.BaseObserver;
import com.fei.firstproject.http.factory.RetrofitFactory;
import com.fei.firstproject.inter.OnItemClickListener;
import com.fei.firstproject.utils.Utils;
import com.fei.firstproject.web.WebActivity;
import com.fei.firstproject.widget.AppHeadView;
import com.fei.firstproject.widget.NoScrollRecyclerView;
import com.fei.firstproject.widget.PartHeadView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;

/**
 * Created by Administrator on 2017/9/12.
 */

public class FieldManageActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.ll_menu)
    LinearLayout llMenu;
    @BindView(R.id.hsv_field_manage)
    HorizontalScrollView hsvFieldManage;
    @BindView(R.id.rv_crop)
    NoScrollRecyclerView rvCrop;
    @BindView(R.id.sv_recommend_plan)
    PartHeadView svRecommendPlan;
    @BindView(R.id.rv_recommend_plan)
    NoScrollRecyclerView rvRecommendPlan;
    @BindView(R.id.ll_recommend_plan)
    LinearLayoutCompat llRecommendPlan;
    @BindView(R.id.sv_share)
    PartHeadView svShare;
    @BindView(R.id.rv_share)
    NoScrollRecyclerView rvShare;
    @BindView(R.id.ll_share)
    LinearLayoutCompat llShare;
    @BindView(R.id.iv_banner)
    ImageView ivBanner;
    @BindView(R.id.ll_crop)
    LinearLayoutCompat llCrop;

    private RecommendPlanAdapter recommendPlanAdapter;

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
        return R.layout.activity_field_manage;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        initListener();
    }

    @Override
    public void initRequest() {
        if (AppConfig.ISLOGIN) {
            ivBanner.setVisibility(View.GONE);
            hsvFieldManage.setVisibility(View.VISIBLE);
            getSharePlan();
            initMenu();
        } else {
            ivBanner.setVisibility(View.VISIBLE);
            getRecommendPlan();
        }
    }

    private void initMenu() {
        int screenWidth = Utils.getScreen(this)[0];
        int tabWidth = screenWidth / 7 * 2;
        String[] menuNames = getResources().getStringArray(R.array.list_menu_field_manage_str);
        int[] menuDrawables = Utils.getDrawableByArray(this, R.array.list_menu_field_manage_drawable);
        for (int i = 0; i < menuNames.length; i++) {
            llMenu.addView(createItemView(tabWidth, menuNames[i], menuDrawables[i]));
        }
    }

    private View createItemView(int tabWidth, final String menuName, int drawable) {
        View itemView = LayoutInflater.from(this).inflate(R.layout.item_head_menu, null);
        ImageView iv_head_menu = ButterKnife.findById(itemView, R.id.iv_head_menu);
        TextView tv_head_menu = ButterKnife.findById(itemView, R.id.tv_head_menu);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(tabWidth, LinearLayout.LayoutParams.MATCH_PARENT);
        itemView.setLayoutParams(params);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (menuName.equals(getResources().getString(R.string.tjgl))) {
                    startActivityWithoutCode(new Intent(FieldManageActivity.this, FieldManageActivity.class));
                } else if (menuName.equals(getResources().getString(R.string.zjzs))) {
                    startActivityWithoutCode(new Intent(FieldManageActivity.this, ExpertiseClinicActivity.class));
                } else if (menuName.equals(getResources().getString(R.string.ncsc))) {

                } else if (menuName.equals(getResources().getString(R.string.nydz))) {

                } else if (menuName.equals(getResources().getString(R.string.cpk))) {
                    startActivityWithoutCode(new Intent(FieldManageActivity.this, ProductLibActivity.class));
                } else if (menuName.equals(getResources().getString(R.string.schq))) {

                } else if (menuName.equals(getResources().getString(R.string.cl))) {

                }
            }
        });
        iv_head_menu.setImageResource(drawable);
        tv_head_menu.setText(menuName);
        return itemView;
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

    public void getRecommendPlan() {
        //province=&county=&city=&
        String searchWord = appHeadView.getEtSearchText();
        Map<String, String> map = new HashMap<>();
        map.put("searchWord", searchWord);
        map.put("pageSize", "3");
        map.put("currentPage", "1");
        Observable<BaseEntity<List<RecommendEntity>>> recommendPlan =
                RetrofitFactory.getBtPlantWeb().getRecommendPlan(map);
        recommendPlan.compose(this.<BaseEntity<List<RecommendEntity>>>createTransformer(false))
                .subscribe(new BaseObserver<List<RecommendEntity>>(this) {
                    @Override
                    protected void onHandleSuccess(final List<RecommendEntity> recommendEntities) {
                        refreshLayout.finishRefresh();
                        if (recommendEntities != null && recommendEntities.size() > 0) {
                            llRecommendPlan.setVisibility(View.VISIBLE);
                            if (recommendPlanAdapter == null) {
                                setRecycleViewListSetting(rvRecommendPlan);
                                recommendPlanAdapter = new RecommendPlanAdapter(FieldManageActivity.this, recommendEntities, 3);
                                rvRecommendPlan.setAdapter(recommendPlanAdapter);
                                recommendPlanAdapter.setOnItemClickListener(new OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View view) {
                                        int position = rvRecommendPlan.getChildAdapterPosition(view);
                                        RecommendEntity recommendEntity = recommendPlanAdapter.getRecommendEntities().get(position);
                                        String url = AppConfig.PLANT_DESC_URL + "?id=" + recommendEntity.getId() + "&version="
                                                + recommendEntity.getVersion()
                                                + "&cropCode=" + recommendEntity.getCropCode()
                                                + "&categoryCode=" + recommendEntity.getCropCategoryCode()
                                                + "&title=" + recommendEntity.getTitle();
                                        Intent intent = new Intent(FieldManageActivity.this, WebActivity.class);
                                        intent.putExtra("url", url);
                                        startActivityWithoutCode(intent);
                                    }
                                });
                            } else {
                                recommendPlanAdapter.setRecommendEntities(recommendEntities);
                                recommendPlanAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    protected void onHandleError(String msg) {
                        super.onHandleError(msg);
                        refreshLayout.finishRefresh();
                    }
                });
    }

    private void getSharePlan() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("currentPage", "1");
        map.put("pageSize", "5");
        Observable<BaseEntity<List<ShareEntity>>> share = RetrofitFactory.getBtPlantWeb().getShare(map);
        share.compose(this.<BaseEntity<List<ShareEntity>>>createTransformer(false))
                .subscribe(new BaseObserver<List<ShareEntity>>(this) {
                    @Override
                    protected void onHandleSuccess(List<ShareEntity> shareEntities) {

                    }
                });
    }

    private void setRecycleViewListSetting(RecyclerView recycleViewSetting) {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, LinearLayout.VERTICAL);
        recycleViewSetting.setLayoutManager(manager);
        recycleViewSetting.addItemDecoration(dividerItemDecoration);
    }

}
