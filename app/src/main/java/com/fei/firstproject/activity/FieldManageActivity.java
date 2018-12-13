package com.fei.firstproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.fei.firstproject.R;
import com.fei.firstproject.adapter.CropAdapter;
import com.fei.firstproject.adapter.RecommendPlanAdapter;
import com.fei.firstproject.adapter.ShareAdapter;
import com.fei.firstproject.config.AppConfig;
import com.fei.firstproject.entity.BaseEntity;
import com.fei.firstproject.entity.CropEntity;
import com.fei.firstproject.entity.RecommendEntity;
import com.fei.firstproject.entity.ShareEntity;
import com.fei.firstproject.http.BaseObserver;
import com.fei.firstproject.http.BaseWithoutBaseEntityObserver;
import com.fei.firstproject.http.factory.RetrofitFactory;
import com.fei.firstproject.inter.OnItemClickListener;
import com.fei.firstproject.utils.Utils;
import com.fei.firstproject.widget.NoScrollRecyclerView;
import com.fei.firstproject.widget.PartHeadView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2017/9/12.
 */

public class FieldManageActivity extends BaseActivity {

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
    private ShareAdapter shareAdapter;
    private CropAdapter cropAdapter;

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
    public void initTitle() {
        setBackTitle(getString(R.string.tjgl));
    }

    @Override
    public void init(Bundle savedInstanceState) {
        initRecyclerView();
        initMenu();
    }

    @Override
    public void initRequest() {
        if (AppConfig.ISLOGIN) {
            ivBanner.setVisibility(View.GONE);
            hsvFieldManage.setVisibility(View.VISIBLE);
            getFieldIndex();
        } else {
            ivBanner.setVisibility(View.VISIBLE);
            getRecommendPlan();
        }
        getSharePlan();
    }

    private void initRecyclerView() {
        setGridRecycleViewSetting(rvCrop, this, 2);
        setLinearRecycleViewSetting(rvRecommendPlan, this);
        setLinearRecycleViewSetting(rvShare, this);
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
                } else if (menuName.equals(getResources().getString(R.string.jdsc))) {

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

    public void getRecommendPlan() {
        //province=&county=&city=&
        String searchWord = appHeadView.getEtSearchText();
        Map<String, String> map = new HashMap<>();
        map.put("searchWord", searchWord);
        map.put("pageSize", "3");
        map.put("currentPage", "1");
        Observable<BaseEntity<List<RecommendEntity>>> recommendPlan =
                RetrofitFactory.getBtPlantWeb().getRecommendPlan(map);
        recommendPlan.compose(this.<BaseEntity<List<RecommendEntity>>>createTransformer(true))
                .subscribe(new BaseObserver<List<RecommendEntity>>(this) {
                    @Override
                    protected void onHandleSuccess(final List<RecommendEntity> recommendEntities) {
                        refreshLayout.finishRefresh();
                        if (recommendEntities != null && recommendEntities.size() > 0) {
                            refreshLayout.setVisibility(View.VISIBLE);
                            llRecommendPlan.setVisibility(View.VISIBLE);
                            if (recommendPlanAdapter == null) {
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
        final Observable<BaseEntity<List<ShareEntity>>> share = RetrofitFactory.getBtPlantWeb().getShare(map);
        share.compose(this.<BaseEntity<List<ShareEntity>>>createTransformer(false))
                .subscribe(new BaseObserver<List<ShareEntity>>(this) {
                    @Override
                    protected void onHandleSuccess(final List<ShareEntity> shareEntities) {
                        if (shareEntities != null && shareEntities.size() > 0) {
                            llShare.setVisibility(View.VISIBLE);
                            if (shareAdapter == null) {
                                shareAdapter = new ShareAdapter(FieldManageActivity.this, shareEntities);
                                shareAdapter.setOnItemClickListener(new OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View view) {
                                        int position = rvShare.getChildAdapterPosition(view);
                                        List<ShareEntity.ImageEntity> list = shareEntities.get(position).getImgPath();
                                        ArrayList<String> pics = new ArrayList<String>();
                                        for (ShareEntity.ImageEntity imageEntity : list) {
                                            pics.add(imageEntity.getPath());
                                        }
                                        Intent intent = new Intent(FieldManageActivity.this, PhotoActivity.class);
                                        intent.putExtra("pics", pics);
                                        startActivityWithoutCode(intent);
                                    }
                                });
                                rvShare.setAdapter(shareAdapter);
                            } else {
                                shareAdapter.setShareEntities(shareEntities);
                                shareAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
    }

    public void getFieldIndex() {
        Map<String, String> map = new HashMap<>();
        map.put("userId", AppConfig.user.getId());
        map.put("roleIds", AppConfig.user.getRoleId());
        map.put("menuId", "82");
        Observable<ResponseBody> fieldIndex = RetrofitFactory.getBtPlantWeb().getFieldIndex(map);
        fieldIndex.compose(this.<ResponseBody>createTransformer(false))
                .subscribe(new BaseWithoutBaseEntityObserver<ResponseBody>(this) {
                    @Override
                    protected void onHandleSuccess(ResponseBody responseBody) {
                        refreshLayout.finishRefresh();
                        refreshLayout.setVisibility(View.VISIBLE);
                        llCrop.setVisibility(View.VISIBLE);
                        try {
                            String response = responseBody.string();
                            if (TextUtils.isEmpty(response)) return;
                            JSONObject json = new JSONObject(response);
                            String croplist = json.getString("croplist");
                            List<CropEntity> cropEntities = JSON.parseArray(croplist, CropEntity.class);
                            if (cropEntities != null && cropEntities.size() > 0) {
                                if (cropAdapter == null) {
                                    cropAdapter = new CropAdapter(FieldManageActivity.this, cropEntities);
                                    rvCrop.setAdapter(cropAdapter);
                                } else {
                                    cropAdapter.setCropEntities(cropEntities);
                                    cropAdapter.notifyDataSetChanged();
                                }
                            } else {
                                cropAdapter = new CropAdapter(FieldManageActivity.this);
                                rvCrop.setAdapter(cropAdapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    protected void onHandleError(String msg) {
                        super.onHandleError(msg);
                        refreshLayout.finishRefresh();
                    }
                });
    }

}
