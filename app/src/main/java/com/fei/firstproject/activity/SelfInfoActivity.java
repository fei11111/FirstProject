package com.fei.firstproject.activity;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fei.firstproject.R;
import com.fei.firstproject.config.AppConfig;
import com.fei.firstproject.entity.BaseEntity;
import com.fei.firstproject.entity.RoleEntity;
import com.fei.firstproject.entity.SelfInfoEntity;
import com.fei.firstproject.entity.UserEntity;
import com.fei.firstproject.http.BaseObserver;
import com.fei.firstproject.http.factory.RetrofitFactory;
import com.fei.firstproject.widget.AppHeadView;
import com.fei.firstproject.widget.PartHeadView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;
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
    @BindView(R.id.tv_farmer_location)
    TextView tvFarmerLocation;
    @BindView(R.id.et_farmer_detail_address)
    EditText etFarmerDetailAddress;
    @BindView(R.id.ll_farmer)
    LinearLayoutCompat llFarmer;
    @BindView(R.id.tv_service_station_name)
    TextView tvServiceStationName;
    @BindView(R.id.tv_service_station_legal_representative)
    TextView tvServiceStationLegalRepresentative;
    @BindView(R.id.tv_service_station_location)
    TextView tvServiceStationLocation;
    @BindView(R.id.et_service_station_detail_address)
    EditText etServiceStationDetailAddress;
    @BindView(R.id.ll_service_station)
    LinearLayoutCompat llServiceStation;
    @BindView(R.id.iv_arrow)
    ImageView ivArrow;
    @BindView(R.id.iv_expertise_icon)
    CircleImageView ivExpertiseIcon;
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
    @BindView(R.id.tv_expertise_location)
    TextView tvExpertiseLocation;
    @BindView(R.id.tv_expertise_detail_address)
    TextView tvExpertiseDetailAddress;
    @BindView(R.id.phv_consultation_way)
    PartHeadView phvConsultationWay;
    @BindView(R.id.phv_online_setting)
    PartHeadView phvOnlineSetting;
    @BindView(R.id.ll_expertise)
    LinearLayoutCompat llExpertise;
    @BindView(R.id.rl_content)
    RelativeLayout rlContent;

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
            tvFarmerLocation.setText(role.getAddress());
            etFarmerDetailAddress.setText(role.getAddr());
        } else if (roleId.equals("30")) {
            llServiceStation.setVisibility(View.VISIBLE);
            tvServiceStationName.setText(role.getServiceName());
            tvServiceStationLegalRepresentative.setText(role.getLegalRepresentative());
            tvServiceStationLocation.setText(role.getAddress());
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
            tvExpertiseLocation.setText(role.getAddress());
            tvExpertiseDetailAddress.setText(role.getAddr());
            phvConsultationWay.setDesc(role.getReserve1());
            if (role.isonline()) {
                phvOnlineSetting.setDesc("在线");
            } else {
                phvOnlineSetting.setDesc("离线");
            }
        }
    }
}
