package com.fei.firstproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.common.viewmodel.EmptyViewModel;
import com.fei.firstproject.R;
import com.fei.firstproject.adapter.MultiTextAdapter;
import com.fei.firstproject.adapter.SingleTextAdapter;
import com.fei.firstproject.config.AppConfig;
import com.fei.firstproject.databinding.ActivitySelfInfoBinding;
import com.fei.firstproject.dialog.BottomListDialog;
import com.fei.firstproject.dialog.CityDialog;
import com.fei.firstproject.dialog.TipDialog;
import com.fei.firstproject.entity.ChangeRoleEntity;
import com.fei.firstproject.entity.RoleEntity;
import com.fei.firstproject.entity.SelfInfoEntity;
import com.fei.firstproject.entity.UserEntity;
import com.fei.firstproject.http.BaseWithoutBaseEntityObserver;
import com.fei.firstproject.http.HttpMgr;
import com.fei.firstproject.http.factory.RetrofitFactory;
import com.fei.firstproject.http.inter.CallBack;
import com.fei.firstproject.utils.GlideUtils;
import com.fei.firstproject.utils.PictureUtils;
import com.fei.firstproject.widget.PartHeadView;
import com.luck.picture.lib.config.PictureConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.reactivex.rxjava3.core.Observable;

/**
 * Created by Administrator on 2017/8/31.
 */

public class SelfInfoActivity extends BaseProjectActivity<EmptyViewModel, ActivitySelfInfoBinding> {

    private boolean isEdit = true;
    private CityDialog cityDialog;
    private TipDialog tipDialog;
    private BottomListDialog listDialog;
    private SingleTextAdapter singleTextAdapter;
    private BottomListDialog consultationWayDialog;
    private MultiTextAdapter multiTextAdapter;

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
        setBackTitle(getString(R.string.self_info));
    }

    @Override
    public void initRequest() {
        getSelfInfo();
    }

    public void getSelfInfo() {
        Map<String, String> map = new HashMap<>();
        UserEntity user = AppConfig.user;
        map.put("userId", user.getId());
        map.put("roleId", user.getRoleId());
        HttpMgr.getSelfInfo(this, map, new CallBack<SelfInfoEntity>() {
            @Override
            public void onSuccess(SelfInfoEntity selfInfoEntity) {
                dismissLoading();
                refreshLayout.finishRefresh();
                if (selfInfoEntity != null) {
                    refreshLayout.setVisibility(View.VISIBLE);
                    mChildBinding.tvAccount.setText(selfInfoEntity.getUserDesc());
                    mChildBinding.tvUserName.setText(selfInfoEntity.getUserName());
                    mChildBinding.tvAccountCreateTime.setText(selfInfoEntity.getCreateTime());
                    mChildBinding.tvCurrentRole.setText(selfInfoEntity.getRoleName());
                    mChildBinding.phvChangeRole.setDesc(selfInfoEntity.getRoleName());
                    for (int i = 0; i < selfInfoEntity.getRoleList().size(); i++) {
                        showInfoByRole(selfInfoEntity, selfInfoEntity.getRoleList().get(i));
                    }
                } else {
                    showRequestErrorView();
                }
            }

            @Override
            public void onFail() {
                refreshLayout.finishRefresh();
                showRequestErrorView();
            }
        });
    }

    private void showInfoByRole(SelfInfoEntity bean, RoleEntity role) {
        String roleId = role.getRoleId();
        if (roleId.equals("20")) {
            mChildBinding.llFarmer.setVisibility(View.VISIBLE);
            mChildBinding.tvFarmerName.setText(role.getGrownName());
            mChildBinding.phvFarmerLocation.setDesc(role.getAddress());
            mChildBinding.etFarmerDetailAddress.setText(role.getAddr());
        } else if (roleId.equals("30")) {
            mChildBinding.llServiceStation.setVisibility(View.VISIBLE);
            mChildBinding.tvServiceStationName.setText(role.getServiceName());
            mChildBinding.tvServiceStationLegalRepresentative.setText(role.getLegalRepresentative());
            mChildBinding.phvServiceStationLocation.setDesc(role.getAddress());
            mChildBinding.etServiceStationDetailAddress.setText(role.getAddr());
        } else if (roleId.equals("40")) {
            mChildBinding.llExpertise.setVisibility(View.VISIBLE);
            String path = bean.getJpgPath();
            if (!TextUtils.isEmpty(path)) {
                Glide.with(this)
                        .load("http://218.18.114.97:3392/btFile" + path)
                        .transition(new DrawableTransitionOptions().crossFade(2000))
                        .apply(GlideUtils.getOptions())
                        .into(mChildBinding.ivExpertiseIcon);
            }
            mChildBinding.tvExpertiseName.setText(role.getExpertName());
            mChildBinding.tvExpertiseSpecialitySkill.setText(role.getExpertise());
            mChildBinding.tvExpertiseLevel.setText(role.getExpertLevelDesc());
            mChildBinding.tvExpertiseSpecialityCrop.setText(role.getPlantCrop());
            mChildBinding.tvExpertiseWorkingTime.setText(role.getServiceTime());
            mChildBinding.phvExpertiseLocation.setDesc(role.getAddress());
            mChildBinding.etExpertiseDetailAddress.setText(role.getAddr());
            mChildBinding.phvConsultationWay.setDesc(role.getReserve1());
            if (role.isonline()) {
                mChildBinding.phvOnlineSetting.setDesc("在线");
            } else {
                mChildBinding.phvOnlineSetting.setDesc("离线");
            }
        }
    }

    void clickSave() {
        mChildBinding.btnSave.setOnClickListener(v -> {
            if (isEdit) {
                //编辑
                mChildBinding.btnSave.setText(getString(R.string.save));
                mChildBinding.etExpertiseDetailAddress.setEnabled(true);
                mChildBinding.etFarmerDetailAddress.setEnabled(true);
                mChildBinding.etServiceStationDetailAddress.setEnabled(true);
                mChildBinding.phvChangeRole.setClickable(true);
                mChildBinding.phvConsultationWay.setClickable(true);
                mChildBinding.phvExpertiseLocation.setClickable(true);
                mChildBinding.phvFarmerLocation.setClickable(true);
                mChildBinding.phvOnlineSetting.setClickable(true);
                mChildBinding.phvServiceStationLocation.setClickable(true);
            } else {
                //保存
                showSaveTipDialog();
            }
            isEdit = !isEdit;
        });
    }

    private void showSaveTipDialog() {
        if (tipDialog == null) {
            tipDialog = new TipDialog(this);
            tipDialog.setContentText("是否保存？");
            tipDialog.setOnConfirmListener(new TipDialog.OnConfirmListener() {
                @Override
                public void onClick(View view) {
                    save();
                }
            });
        }
        tipDialog.show();
    }

    private void save() {

    }

    void clickLocation() {
        mChildBinding.phvExpertiseLocation.setOnClickListener(v->{
            showCityDialog(mChildBinding.phvExpertiseLocation);
        });
        mChildBinding.phvFarmerLocation.setOnClickListener(v->{
            showCityDialog(mChildBinding.phvFarmerLocation);
        });
        mChildBinding.phvServiceStationLocation.setOnClickListener(v->{
            showCityDialog(mChildBinding.phvServiceStationLocation);
        });
    }

    private void showCityDialog(View view){
        if (isEdit) return;
        if (cityDialog == null) {
            cityDialog = new CityDialog(SelfInfoActivity.this);
            cityDialog.setOnConfirmListener(new CityDialog.OnConfirmListener() {
                @Override
                public void onClick(String province, String city, String couny) {
                    String address = province + city + couny;
                    switch (view.getId()) {
                        case R.id.phv_expertise_location:
                            mChildBinding.phvExpertiseLocation.setDesc(address);
                            break;
                        case R.id.phv_farmer_location:
                            mChildBinding.phvFarmerLocation.setDesc(address);
                            break;
                        case R.id.phv_service_station_location:
                            mChildBinding.phvServiceStationLocation.setDesc(address);
                            break;
                    }
                }
            });
        }
        cityDialog.show();
    }

    void clickChangeRole() {
        if (isEdit) return;
        mChildBinding.phvChangeRole.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Observable<List<ChangeRoleEntity>> role = RetrofitFactory.getBtWeb().changeRole(AppConfig.user.getId());
                role.compose(createTransformer(false))
                        .subscribe(new BaseWithoutBaseEntityObserver<List<ChangeRoleEntity>>(mContext) {
                            @Override
                            protected void onHandleSuccess(List<ChangeRoleEntity> changeRoleEntities) {
                                List<String> names = new ArrayList<String>();
                                for (ChangeRoleEntity entity :
                                        changeRoleEntities) {
                                    names.add(entity.getROLE_NAME());
                                }
                                showListDialog(getString(R.string.change_role), names, mChildBinding.phvChangeRole);
                            }
                        });
            }
        });
    }

    void clickOnlineSetting() {
        mChildBinding.phvOnlineSetting.setOnClickListener(v -> {
            if (isEdit) return;
            List<String> names = new ArrayList<>();
            names.add("在线");
            names.add("离线");
            showListDialog(getString(R.string.online_setting), names, mChildBinding.phvOnlineSetting);
        });
    }

    void clickConsultationWay() {
        mChildBinding.phvConsultationWay.setOnClickListener(v -> {
            if (isEdit) return;
            List<String> names = new ArrayList<String>();
            List<String> checkNames = new ArrayList<String>();
            names.add("在线咨询");
            names.add("电话咨询");
            names.add("视频咨询");
            String desc = mChildBinding.phvConsultationWay.getDesc();
            if (!TextUtils.isEmpty(desc)) {
                String[] split = desc.split("/");
                List<String> list = Arrays.asList(split);
                checkNames.addAll(list);
            }
            showConsultationWayDialog(names, checkNames);
        });
    }

    void clickExpertiseIcon() {
        //头像
        mChildBinding.rlExpertiseIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEdit) return;
                PictureUtils.getCirclePicture(SelfInfoActivity.this);
            }
        });

    }

    private void showConsultationWayDialog(List<String> names, List<String> checkNames) {
        if (consultationWayDialog == null) {
            consultationWayDialog = new BottomListDialog(this);
            consultationWayDialog.setTitle(getString(R.string.consultation_way));
            multiTextAdapter = new MultiTextAdapter(this, names, checkNames);
            consultationWayDialog.setAdapter(multiTextAdapter);
            consultationWayDialog.setOnConfirmListener(new BottomListDialog.OnConfirmListener() {
                @Override
                public void onClick(View view) {
                    List<String> checkList = multiTextAdapter.getCheckList();
                    String str = "";
                    for (String text :
                            checkList) {
                        str += text + "/";
                    }
                    if (!TextUtils.isEmpty(str)) {
                        str = str.substring(0, str.length() - 1);
                    }
                    mChildBinding.phvConsultationWay.setDesc(str);
                }
            });
        } else {
            multiTextAdapter.setNames(names);
            multiTextAdapter.notifyDataSetChanged();
        }
        consultationWayDialog.show();
    }

    private void showListDialog(String title, List<String> names, final PartHeadView partHeadView) {
        if (listDialog == null) {
            listDialog = new BottomListDialog(this);
            singleTextAdapter = new SingleTextAdapter(this, names);
            listDialog.setAdapter(singleTextAdapter);
            listDialog.setOnItemClickListener(new BottomListDialog.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    partHeadView.setDesc((String) singleTextAdapter.getItem(position));
                }
            });
        } else {
            singleTextAdapter.setNames(names);
            singleTextAdapter.notifyDataSetChanged();
        }
        listDialog.setTitle(title);
        listDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
//                    List<LocalMedia> selectList = PictureSelector.obtainSelectorList(data);
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                    break;
            }
        }
    }

    @Override
    public void createObserver() {
        clickChangeRole();
        clickExpertiseIcon();
        clickConsultationWay();
        clickOnlineSetting();
        clickLocation();
        clickSave();
    }

    @Override
    public void initViewAndData(Bundle savedInstanceState) {

    }
}
