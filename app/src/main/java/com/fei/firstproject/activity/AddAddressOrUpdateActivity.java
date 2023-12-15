package com.fei.firstproject.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.common.viewmodel.EmptyViewModel;
import com.fei.firstproject.R;
import com.fei.firstproject.config.AppConfig;
import com.fei.firstproject.databinding.ActivityAddOrUpdateAddressBinding;
import com.fei.firstproject.dialog.CityDialog;
import com.fei.firstproject.entity.AddressEntity;
import com.fei.firstproject.http.HttpMgr;
import com.fei.firstproject.http.inter.CallBack;
import com.fei.firstproject.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2017/9/4.
 */

public class AddAddressOrUpdateActivity extends BaseProjectActivity<EmptyViewModel, ActivityAddOrUpdateAddressBinding> {

    private static final int REQUEST_PERMISSION_CODE_MAP = 100;
    private static final int REQUEST_ACTIVITY_CODE_MAP = 200;
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
        mChildBinding.etContacts.setText(addressEntity.getReceiptUserName());
        mChildBinding.etPhone.setText(addressEntity.getReceiptTel());
        String[] split = addressEntity.getReceiptAddr().split(" ");
        mChildBinding.tvAddress.setText(split[0]);
        if (split.length == 2) {
            mChildBinding.etDetailAddress.setText(split[1]);
        }
    }

    @Override
    public void initRequest() {

    }

    void clickAddress() {
        mChildBinding.rlAddress.setOnClickListener(v -> {
            checkPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_CODE_MAP);
        });
    }

    void clickAddAddress(View view) {
        mChildBinding.btnAddAddress.setOnClickListener(v -> {
            String contrats = mChildBinding.etContacts.getText().toString();
            if (TextUtils.isEmpty(contrats)) {
                mChildBinding.etContacts.setError("联系人不能为空");
                return;
            }
            String phone = mChildBinding.etPhone.getText().toString();
            if (TextUtils.isEmpty(phone)) {
                mChildBinding.etPhone.setError("电话不能为空");
            } else {
                if (!Utils.matchCheck(phone, 0)) {
                    mChildBinding.etPhone.setError("电话号码根式不对");
                    return;
                }
            }
            String address = mChildBinding.tvAddress.getText().toString();
            if (TextUtils.isEmpty(address)) {
                mChildBinding.tvAddress.setError("收货地址不能为空");
                return;
            }
            String detail_address = mChildBinding.etDetailAddress.getText().toString();
            if (TextUtils.isEmpty(detail_address)) {
                mChildBinding.etDetailAddress.setError("详细地址不能为空");
                return;
            }

            if (mChildBinding.rbMale.isChecked()) {
                contrats += mChildBinding.rbMale.getText().toString();
            } else if (mChildBinding.rbFemale.isChecked()) {
                contrats += mChildBinding.rbFemale.getText().toString();
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
        });
    }

    private void save(Map<String, String> map) {
        HttpMgr.addAddress(this, map, new CallBack<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) {
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

            @Override
            public void onFail() {

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
        HttpMgr.editAddress(this, map, new CallBack<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) {
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

            @Override
            public void onFail() {

            }
        });
    }

    @Override
    public void createObserver() {
        clickAddress();
    }

    @Override
    public void initViewAndData(Bundle savedInstanceState) {

    }
}
