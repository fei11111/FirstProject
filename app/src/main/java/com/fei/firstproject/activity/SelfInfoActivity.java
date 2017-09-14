package com.fei.firstproject.activity;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fei.firstproject.R;
import com.fei.firstproject.adapter.MultiTextAdapter;
import com.fei.firstproject.adapter.SingleTextAdapter;
import com.fei.firstproject.config.AppConfig;
import com.fei.firstproject.dialog.BottomListDialog;
import com.fei.firstproject.dialog.CityDialog;
import com.fei.firstproject.dialog.TipDialog;
import com.fei.firstproject.entity.BaseEntity;
import com.fei.firstproject.entity.ChangeRoleEntity;
import com.fei.firstproject.entity.RoleEntity;
import com.fei.firstproject.entity.SelfInfoEntity;
import com.fei.firstproject.entity.UserEntity;
import com.fei.firstproject.http.BaseObserver;
import com.fei.firstproject.http.BaseWithoutBaseEntityObserver;
import com.fei.firstproject.http.factory.RetrofitFactory;
import com.fei.firstproject.widget.AppHeadView;
import com.fei.firstproject.widget.PartHeadView;
import com.fei.firstproject.widget.RoundImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;

/**
 * Created by Administrator on 2017/8/31.
 */

public class SelfInfoActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.tv_account)
    TextView tvAccount;
    @BindView(R.id.tv_user_name)
    TextView tvUserName;
    @BindView(R.id.tv_current_role)
    TextView tvCurrentRole;
    @BindView(R.id.phv_change_role)
    PartHeadView phvChangeRole;
    @BindView(R.id.tv_account_create_time)
    TextView tvAccountCreateTime;
    @BindView(R.id.tv_farmer_name)
    TextView tvFarmerName;
    @BindView(R.id.phv_farmer_location)
    PartHeadView phvFarmerLocation;
    @BindView(R.id.et_farmer_detail_address)
    EditText etFarmerDetailAddress;
    @BindView(R.id.ll_farmer)
    LinearLayoutCompat llFarmer;
    @BindView(R.id.tv_service_station_name)
    TextView tvServiceStationName;
    @BindView(R.id.tv_service_station_legal_representative)
    TextView tvServiceStationLegalRepresentative;
    @BindView(R.id.phv_service_station_location)
    PartHeadView phvServiceStationLocation;
    @BindView(R.id.et_service_station_detail_address)
    EditText etServiceStationDetailAddress;
    @BindView(R.id.ll_service_station)
    LinearLayoutCompat llServiceStation;
    @BindView(R.id.iv_arrow)
    ImageView ivArrow;
    @BindView(R.id.iv_expertise_icon)
    RoundImageView ivExpertiseIcon;
    @BindView(R.id.tv_expertise_name)
    TextView tvExpertiseName;
    @BindView(R.id.tv_expertise_speciality_skill)
    TextView tvExpertiseSpecialitySkill;
    @BindView(R.id.tv_expertise_speciality_crop)
    TextView tvExpertiseSpecialityCrop;
    @BindView(R.id.tv_expertise_level)
    TextView tvExpertiseLevel;
    @BindView(R.id.tv_expertise_working_time)
    TextView tvExpertiseWorkingTime;
    @BindView(R.id.phv_expertise_location)
    PartHeadView phvExpertiseLocation;
    @BindView(R.id.et_expertise_detail_address)
    EditText etExpertiseDetailAddress;
    @BindView(R.id.phv_consultation_way)
    PartHeadView phvConsultationWay;
    @BindView(R.id.phv_online_setting)
    PartHeadView phvOnlineSetting;
    @BindView(R.id.ll_expertise)
    LinearLayoutCompat llExpertise;
    @BindView(R.id.rl_content)
    RelativeLayout rlContent;

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
    public int getContentViewResId() {
        return R.layout.activity_self_info;
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

            }

            @Override
            public void onEdit(TextView v, int actionId, KeyEvent event) {

            }
        });
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
        final Observable<BaseEntity<SelfInfoEntity>> selfInfo = RetrofitFactory.getBtWeb().getSelfInfo(map);
        selfInfo.compose(this.<BaseEntity<SelfInfoEntity>>createTransformer(true))
                .subscribe(new BaseObserver<SelfInfoEntity>(this) {
                    @Override
                    protected void onHandleSuccess(SelfInfoEntity selfInfoEntity) {
                        dismissLoading();
                        refreshLayout.finishRefresh();
                        if (selfInfoEntity != null) {
                            refreshLayout.setVisibility(View.VISIBLE);
                            tvAccount.setText(selfInfoEntity.getUserDesc());
                            tvUserName.setText(selfInfoEntity.getUserName());
                            tvAccountCreateTime.setText(selfInfoEntity.getCreateTime());
                            tvCurrentRole.setText(selfInfoEntity.getRoleName());
                            phvChangeRole.setDesc(selfInfoEntity.getRoleName());
                            for (int i = 0; i < selfInfoEntity.getRoleList().size(); i++) {
                                showInfoByRole(selfInfoEntity, selfInfoEntity.getRoleList().get(i));
                            }
                        } else {
                            showRequestErrorView();
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

    private void showInfoByRole(SelfInfoEntity bean, RoleEntity role) {
        String roleId = role.getRoleId();
        if (roleId.equals("20")) {
            llFarmer.setVisibility(View.VISIBLE);
            tvFarmerName.setText(role.getGrownName());
            phvFarmerLocation.setDesc(role.getAddress());
            etFarmerDetailAddress.setText(role.getAddr());
        } else if (roleId.equals("30")) {
            llServiceStation.setVisibility(View.VISIBLE);
            tvServiceStationName.setText(role.getServiceName());
            tvServiceStationLegalRepresentative.setText(role.getLegalRepresentative());
            phvServiceStationLocation.setDesc(role.getAddress());
            etServiceStationDetailAddress.setText(role.getAddr());
        } else if (roleId.equals("40")) {
            llExpertise.setVisibility(View.VISIBLE);
            String path = bean.getJpgPath();
            if (!TextUtils.isEmpty(path)) {
                Glide.with(this)
                        .load("http://218.18.114.97:3392/btFile" + path)
                        .placeholder(R.drawable.ic_app)
                        .crossFade()
                        .error(R.drawable.ic_pic_error)
                        .into(ivExpertiseIcon);
            }
            tvExpertiseName.setText(role.getExpertName());
            tvExpertiseSpecialitySkill.setText(role.getExpertise());
            tvExpertiseLevel.setText(role.getExpertLevelDesc());
            tvExpertiseSpecialityCrop.setText(role.getPlantCrop());
            tvExpertiseWorkingTime.setText(role.getServiceTime());
            phvExpertiseLocation.setDesc(role.getAddress());
            etExpertiseDetailAddress.setText(role.getAddr());
            phvConsultationWay.setDesc(role.getReserve1());
            if (role.isonline()) {
                phvOnlineSetting.setDesc("在线");
            } else {
                phvOnlineSetting.setDesc("离线");
            }
        }
    }

    @OnClick(R.id.btn_save)
    void clickSave(View view) {
        if (isEdit) {
            //编辑
            btnSave.setText(getString(R.string.save));
            etExpertiseDetailAddress.setEnabled(true);
            etFarmerDetailAddress.setEnabled(true);
            etServiceStationDetailAddress.setEnabled(true);
            phvChangeRole.setClickable(true);
            phvConsultationWay.setClickable(true);
            phvExpertiseLocation.setClickable(true);
            phvFarmerLocation.setClickable(true);
            phvOnlineSetting.setClickable(true);
            phvServiceStationLocation.setClickable(true);
        } else {
            //保存
            showSaveTipDialog();
        }
        isEdit = !isEdit;
    }

    private void showSaveTipDialog() {
        if (tipDialog == null) {
            tipDialog = new TipDialog(this);
            tipDialog.setTitle("确定保存？");
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

    @OnClick({R.id.phv_expertise_location, R.id.phv_farmer_location, R.id.phv_service_station_location})
    void clickLocation(final View view) {
        if (isEdit) return;
        if (cityDialog == null) {
            cityDialog = new CityDialog(SelfInfoActivity.this);
            cityDialog.setOnConfirmListener(new CityDialog.OnConfirmListener() {
                @Override
                public void onClick(String province, String city, String couny) {
                    String address = province + city + couny;
                    switch (view.getId()) {
                        case R.id.phv_expertise_location:
                            phvExpertiseLocation.setDesc(address);
                            break;
                        case R.id.phv_farmer_location:
                            phvFarmerLocation.setDesc(address);
                            break;
                        case R.id.phv_service_station_location:
                            phvServiceStationLocation.setDesc(address);
                            break;
                    }
                }
            });
        }
        cityDialog.show();
    }

    @OnClick(R.id.phv_change_role)
    void clickChangeRole(View view) {
        if (isEdit) return;
        Observable<List<ChangeRoleEntity>> role = RetrofitFactory.getBtWeb().changeRole(AppConfig.user.getId());
        role.compose(this.<List<ChangeRoleEntity>>createTransformer(false))
                .subscribe(new BaseWithoutBaseEntityObserver<List<ChangeRoleEntity>>(this) {
                    @Override
                    protected void onHandleSuccess(List<ChangeRoleEntity> changeRoleEntities) {
                        List<String> names = new ArrayList<String>();
                        for (ChangeRoleEntity entity :
                                changeRoleEntities) {
                            names.add(entity.getROLE_NAME());
                        }
                        showListDialog(getString(R.string.change_role), names, phvChangeRole);
                    }
                });
    }

    @OnClick(R.id.phv_online_setting)
    void clickOnlineSetting(View view) {
        if (isEdit) return;
        List<String> names = new ArrayList<>();
        names.add("在线");
        names.add("离线");
        showListDialog(getString(R.string.online_setting), names, phvOnlineSetting);
    }

    @OnClick(R.id.phv_consultation_way)
    void clickConsultationWay(View view) {
        if (isEdit) return;
        List<String> names = new ArrayList<String>();
        List<String> checkNames = new ArrayList<String>();
        names.add("在线咨询");
        names.add("电话咨询");
        names.add("视频咨询");
        String desc = phvConsultationWay.getDesc();
        if (!TextUtils.isEmpty(desc)) {
            String[] split = desc.split("/");
            List<String> list = Arrays.asList(split);
            checkNames.addAll(list);
        }
        showConsultationWayDialog(names, checkNames);
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
                    phvConsultationWay.setDesc(str);
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
}
