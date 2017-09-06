package com.fei.firstproject.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.fei.firstproject.R;
import com.fei.firstproject.adapter.MyAddressAdapter;
import com.fei.firstproject.config.AppConfig;
import com.fei.firstproject.entity.AddressEntity;
import com.fei.firstproject.http.BaseWithoutBaseEntityObserver;
import com.fei.firstproject.http.factory.RetrofitFactory;
import com.fei.firstproject.utils.Utils;
import com.fei.firstproject.widget.AppHeadView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.OnClick;
import io.reactivex.Observable;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2017/9/4.
 */

public class MyAddressActivity extends BaseListActivity {

    private static final int REQUEST_ACTIVITY_CODE_ADD = 200;
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
        startActivityWithCode(new Intent(this, AddAddressOrUpdateActivity.class), REQUEST_ACTIVITY_CODE_ADD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ACTIVITY_CODE_ADD) {
            if (resultCode == RESULT_OK) {
                initRequest();
            }
        }
    }

    public void getAddress() {
        final Observable<ResponseBody> address = RetrofitFactory.getBigDb().getAddress(AppConfig.user.getId());
        address.compose(this.<ResponseBody>createTransformer(true))
                .subscribe(new BaseWithoutBaseEntityObserver<ResponseBody>(this) {
                    @Override
                    protected void onHandleSuccess(ResponseBody responseBody) {
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
                    protected void onHandleError(String msg) {
                        super.onHandleError(msg);
                        refreshLayout.finishRefresh();
                        refreshLayout.finishLoadmore();
                        showRequestErrorView();
                    }
                });
    }

    private MyAddressAdapter.OnItemContentClickLstener onItemContentClickLstener = new MyAddressAdapter.OnItemContentClickLstener() {
        @Override
        public void onEditAddress(AddressEntity addressEntity) {

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

    private void delAddress(AddressEntity addressEntity) {

    }

    private void setDefaultAddress(String receiptAddrId, final MyAddressAdapter.OnCallBack onCallBack) {
        Map<String, String> map = new HashMap<>();
        map.put("userId", AppConfig.user.getId());
        map.put("receiptAddrId", receiptAddrId);
        Observable<ResponseBody> defaultAddress = RetrofitFactory.getBigDb().setDefaultAddress(map);
        defaultAddress.compose(this.<ResponseBody>createTransformer(false))
                .subscribe(new BaseWithoutBaseEntityObserver<ResponseBody>(this) {
                    @Override
                    protected void onHandleSuccess(ResponseBody responseBody) {
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
                    protected void onHandleError(String msg) {
                        super.onHandleError(msg);
                    }
                });
    }
}
