package com.fei.firstproject.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fei.firstproject.R;
import com.fei.firstproject.config.AppConfig;
import com.fei.firstproject.dialog.CityDialog;
import com.fei.firstproject.entity.AddressEntity;
import com.fei.firstproject.http.BaseWithoutBaseEntityObserver;
import com.fei.firstproject.http.factory.RetrofitFactory;
import com.fei.firstproject.utils.Utils;
import com.fei.firstproject.widget.AppHeadView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2017/9/4.
 */

public class AddAddressOrUpdateActivity extends BaseActivity {

    private static final int REQUEST_PERMISSION_CODE_MAP = 100;
    private static final int REQUEST_ACTIVITY_CODE_MAP = 200;
    @BindView(R.id.btn_add_address)
    Button btnAddAddress;
    @BindView(R.id.et_contacts)
    EditText etContacts;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.tv_address_left)
    TextView tvAddressLeft;
    @BindView(R.id.iv_arrow)
    ImageView ivArrow;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.rl_address)
    RelativeLayout rlAddress;
    @BindView(R.id.et_detail_address)
    EditText etDetailAddress;
    @BindView(R.id.rb_male)
    RadioButton rbMale;
    @BindView(R.id.rb_female)
    RadioButton rbFemale;

    private CityDialog cityDialog;
    private AddressEntity addressEntity;

    @Override
    public void permissionsDeniedCallBack(int requestCode) {
        showMissingPermissionDialog("需要获取定位功能才能添加准确地址", REQUEST_PERMISSION_CODE_MAP);
    }

    @Override
    public void permissionsGrantCallBack(int requestCode) {
        if (requestCode == REQUEST_PERMISSION_CODE_MAP) {
            startActivityWithCode(new Intent(this, MapActivity.class), REQUEST_ACTIVITY_CODE_MAP);
        }
    }

    @Override
    public void permissionDialogDismiss(int requestCode) {
        Utils.showToast(this, "获取定位功能失败，无法添加地址");
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_add_or_update_address;
    }

    @Override
    public void init(Bundle savedInstanceState) {
    }

    public void initTitle() {
        addressEntity = (AddressEntity) getIntent().getSerializableExtra("addressEntity");
        if (addressEntity == null) {
            //新增
            setBackTitle(getString(R.string.add_address));
        } else {
            //编辑
            setBackTitle(getString(R.string.edit_address));
            initData(addressEntity);
        }
    }

    private void initData(AddressEntity addressEntity) {
        etContacts.setText(addressEntity.getReceiptUserName());
        etPhone.setText(addressEntity.getReceiptTel());
        String[] split = addressEntity.getReceiptAddr().split(" ");
        tvAddress.setText(split[0]);
        if (split.length == 2) {
            etDetailAddress.setText(split[1]);
        }
    }

    @Override
    public void initRequest() {

    }

    @OnClick(R.id.rl_address)
    void clickAddress(View view) {
        checkPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_PERMISSION_CODE_MAP);

//        if (cityDialog == null) {
//            cityDialog = new CityDialog(this);
//            cityDialog.setOnConfirmListener(new CityDialog.OnConfirmListener() {
//                @Override
//                public void onClick(String province, String city, String couny) {
//                    tvAddress.setText(province + city + couny);
//                }
//            });
//        }
//        cityDialog.show();
    }

    @OnClick(R.id.btn_add_address)
    void clickAddAddress(View view) {
        String contrats = etContacts.getText().toString();
        if (TextUtils.isEmpty(contrats)) {
            etContacts.setError("联系人不能为空");
            return;
        }
        String phone = etPhone.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("电话不能为空");
        } else {
            if (!Utils.matchCheck(phone, 0)) {
                etPhone.setError("电话号码根式不对");
                return;
            }
        }
        String address = tvAddress.getText().toString();
        if (TextUtils.isEmpty(address)) {
            tvAddress.setError("收货地址不能为空");
            return;
        }
        String detail_address = etDetailAddress.getText().toString();
        if (TextUtils.isEmpty(detail_address)) {
            etDetailAddress.setError("详细地址不能为空");
            return;
        }

        if (rbMale.isChecked()) {
            contrats += rbMale.getText().toString();
        } else if (rbFemale.isChecked()) {
            contrats += rbFemale.getText().toString();
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("userId", AppConfig.user.getId());
        map.put("receiptUserName", contrats);
        map.put("receiptAddr", address + detail_address);
        map.put("receiptTel", phone);

        if (addressEntity == null) {
            save(map);
        } else {
            addressEntity.setReceiptAddr(address + detail_address);
            addressEntity.setReceiptUserName(contrats);
            addressEntity.setReceiptTel(phone);
            edit(addressEntity);
        }

    }

    private void save(Map<String, String> map) {
        Observable<ResponseBody> addAddress = RetrofitFactory.getBigDb().addAddress(map);
        addAddress.compose(this.<ResponseBody>createTransformer(false))
                .subscribe(new BaseWithoutBaseEntityObserver<ResponseBody>(this) {
                    @Override
                    protected void onHandleSuccess(ResponseBody responseBody) {
                        try {
                            String string = responseBody.string();
                            JSONObject result = new JSONObject(string);
                            String status = result.getString("status");
                            if (status.equals("1")) {
                                Utils.showToast(AddAddressOrUpdateActivity.this, "添加成功!");
                                setResult(RESULT_OK);
                                finish();
                            } else {
                                Utils.showToast(AddAddressOrUpdateActivity.this, "添加失败!");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void edit(AddressEntity addressEntity) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("userId", AppConfig.user.getId());
        map.put("receiptUserName", addressEntity.getReceiptUserName());
        map.put("receiptAddr", addressEntity.getReceiptAddr());
        map.put("receiptTel", addressEntity.getReceiptTel());
        map.put("receiptAddrId", addressEntity.getReceiptAddrId());
        Observable<ResponseBody> editAddress = RetrofitFactory.getBigDb().editAddress(map);
        editAddress.compose(this.<ResponseBody>createTransformer(false))
                .subscribe(new BaseWithoutBaseEntityObserver<ResponseBody>(this) {
                    @Override
                    protected void onHandleSuccess(ResponseBody responseBody) {
                        try {
                            String string = responseBody.string();
                            JSONObject result = new JSONObject(string);
                            String status = result.getString("status");
                            if (status.equals("1")) {
                                Utils.showToast(AddAddressOrUpdateActivity.this, "修改成功!");
                                setResult(RESULT_OK);
                                finish();
                            } else {
                                Utils.showToast(AddAddressOrUpdateActivity.this, "修改成功!");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

}
