package com.fei.firstproject.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.fei.firstproject.R;
import com.fei.firstproject.adapter.MyAddressAdapter;
import com.fei.firstproject.config.AppConfig;
import com.fei.firstproject.entity.AddressEntity;
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

import butterknife.OnClick;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2017/9/4.
 */

public class MyAddressActivity extends BaseListActivity {

    private static final int REQUEST_ACTIVITY_CODE_ADD_OR_UPDATE = 200;
    private MyAddressAdapter myAddressAdapter;

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
    public void initRequest() {
        getAddress();
    }

    @Override
    public void initView() {
        initBaseListButton();
        initAppHeadView();
    }

    private void initAppHeadView() {
        appHeadView.setFlHeadRightVisible(View.INVISIBLE);
        appHeadView.setMiddleStyle(AppHeadView.TEXT);
        appHeadView.setMiddleText(getResources().getString(R.string.my_address));
    }

    private void initBaseListButton() {
        btn_base_list.setVisibility(View.VISIBLE);
        btn_base_list.setText(R.string.add_address);
    }

    @OnClick(R.id.btn_base_list)
    void clickAddAddress(View view) {
        startActivityWithCode(new Intent(this, AddAddressOrUpdateActivity.class), REQUEST_ACTIVITY_CODE_ADD_OR_UPDATE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ACTIVITY_CODE_ADD_OR_UPDATE) {
            if (resultCode == RESULT_OK) {
                getAddress();
            }
        }
    }

    public void getAddress() {
        HttpMgr.getAddress(this, new CallBack<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) {
                dismissLoading();
                refreshLayout.finishRefresh();
                refreshLayout.finishLoadmore();
                try {
                    String string = responseBody.string();
                    if (!TextUtils.isEmpty(string)) {
                        JSONObject json = new JSONObject(string);
                        String receiptAddress = json.getString("receiptAddress");
                        if (!TextUtils.isEmpty(receiptAddress)) {
                            List<AddressEntity> addressEntities = JSON.parseArray(receiptAddress, AddressEntity.class);
                            if (addressEntities != null && addressEntities.size() > 0) {
                                refreshLayout.setVisibility(View.VISIBLE);
                                if (myAddressAdapter == null) {
                                    myAddressAdapter = new MyAddressAdapter(MyAddressActivity.this, addressEntities);
                                    myAddressAdapter.setOnItemContentClickLstener(onItemContentClickLstener);
                                    recyclerView.setAdapter(myAddressAdapter);
                                } else {
                                    myAddressAdapter.setAddressEntities(addressEntities);
                                    myAddressAdapter.notifyDataSetChanged();
                                }
                            } else {
                                showNoDataView();
                            }
                        } else {
                            showNoDataView();
                        }
                    } else {
                        showNoDataView();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    showNoDataView();
                } catch (JSONException e) {
                    e.printStackTrace();
                    showNoDataView();
                }
            }

            @Override
            public void onFail() {
                refreshLayout.finishRefresh();
                refreshLayout.finishLoadmore();
                showRequestErrorView();
            }
        });
    }

    private MyAddressAdapter.OnItemContentClickLstener onItemContentClickLstener = new MyAddressAdapter.OnItemContentClickLstener() {
        @Override
        public void onEditAddress(AddressEntity addressEntity) {
            Intent intent = new Intent(MyAddressActivity.this, AddAddressOrUpdateActivity.class);
            intent.putExtra("addressEntity", addressEntity);
            startActivityWithCode(intent, REQUEST_ACTIVITY_CODE_ADD_OR_UPDATE);
        }

        @Override
        public void onDefaultAddress(String receiptAddrId, MyAddressAdapter.OnCallBack onCallBack) {
            setDefaultAddress(receiptAddrId, onCallBack);
        }

        @Override
        public void onDelAddress(AddressEntity addressEntity) {
            delAddress(addressEntity);
        }
    };

    private void delAddress(final AddressEntity addressEntity) {
        Map<String, String> map = new HashMap<>();
        map.put("userId", AppConfig.user.getId());
        map.put("receiptAddrId", addressEntity.getReceiptAddrId());
        HttpMgr.delAddress(this, map, new CallBack<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) {
                try {
                    String string = responseBody.string();
                    if (!TextUtils.isEmpty(string)) {
                        JSONObject json = new JSONObject(string);
                        if (json.getString("status").equals("1")) {
                            myAddressAdapter.getAddressEntities().remove(addressEntity);
                            myAddressAdapter.notifyDataSetChanged();
                            Utils.showToast(MyAddressActivity.this, "删除成功");
                        } else {
                            Utils.showToast(MyAddressActivity.this, "删除失败");
                        }
                    } else {
                        Utils.showToast(MyAddressActivity.this, "删除失败");
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

    private void setDefaultAddress(String receiptAddrId, final MyAddressAdapter.OnCallBack onCallBack) {
        Map<String, String> map = new HashMap<>();
        map.put("userId", AppConfig.user.getId());
        map.put("receiptAddrId", receiptAddrId);
        HttpMgr.setDefaultAddress(this, map, new CallBack<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) {
                try {
                    String string = responseBody.string();
                    if (!TextUtils.isEmpty(string)) {
                        JSONObject json = new JSONObject(string);
                        if (json.getString("status").equals("1")) {
                            onCallBack.onCallBack();
                        } else {
                            Utils.showToast(MyAddressActivity.this, "设置失败");
                        }
                    } else {
                        Utils.showToast(MyAddressActivity.this, "设置失败");
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
}
