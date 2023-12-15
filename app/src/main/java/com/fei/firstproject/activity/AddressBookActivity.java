package com.fei.firstproject.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.common.viewmodel.EmptyViewModel;
import com.fei.firstproject.R;
import com.fei.firstproject.adapter.ContactsAdapter;
import com.fei.firstproject.databinding.ActivityAddressBookBinding;
import com.fei.firstproject.decoration.DividerItemDecoration;
import com.fei.firstproject.entity.ContactsEntity;
import com.fei.firstproject.utils.LogUtils;
import com.fei.firstproject.widget.LetterView;
import com.github.promeg.pinyinhelper.Pinyin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Administrator on 2017/12/18.
 * 参考：http://blog.csdn.net/u012702547/article/details/51234227
 */

/**
 * 通讯录
 */
public class AddressBookActivity extends BaseProjectActivity<EmptyViewModel, ActivityAddressBookBinding> {

    private static final int REQUEST_PERMISSION_CODE_CONTACTS = 100;

    private List<ContactsEntity> contactsEntities = new ArrayList<>();
    private LinearLayoutManager manager;
    private ContactsAdapter contactsAdapter;
    private Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (contactsEntities.size() > 0) {
                dismissLoading();
                setRecycleViewSetting(mChildBinding.recyclerView);
                if (contactsAdapter == null) {
                    contactsAdapter = new ContactsAdapter(AddressBookActivity.this, contactsEntities);
                    mChildBinding.recyclerView.setAdapter(contactsAdapter);
                }
            } else {
                showNoDataView();
            }
        }
    };

    @Override
    public void permissionsDeniedCallBack(int requestCode) {
        showNoDataView();
        showMissingPermissionDialog(getString(R.string.need_contacts_permission), requestCode);
    }

    @Override
    public void permissionsGrantCallBack(int requestCode) {
        getContactsFromPhone();
    }

    /**
     * 开启线程处理数据
     */
    private void getContactsFromPhone() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ContentResolver resolver = getContentResolver();
                //这个循环的是data表
                Cursor cursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
                while (cursor.moveToNext()) {
                    String name = "";
                    int columnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                    if (columnIndex != -1) {
                        name = cursor.getString(columnIndex);
                    }
                    String phone = "";
                    columnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    if (columnIndex != -1) {
                        phone = cursor.getString(columnIndex);
                    }
                    ContactsEntity contactsEntity = new ContactsEntity();
                    contactsEntity.setName(name);
                    contactsEntity.setPhone(phone);
                    String convertToPinyinString = Pinyin.toPinyin(name, "").toUpperCase();
                    LogUtils.i("tag", convertToPinyinString);
                    contactsEntity.setPinyin(convertToPinyinString);
                    String substring = convertToPinyinString.substring(0, 1);
                    if (substring.matches("[A-Z]")) {
                        contactsEntity.setFirstLetter(substring);
                    } else {
                        contactsEntity.setFirstLetter("#");
                    }
                    contactsEntities.add(contactsEntity);
                }
                Collections.sort(contactsEntities, new Comparator<ContactsEntity>() {
                    @Override
                    public int compare(ContactsEntity o1, ContactsEntity o2) {
                        if (o1.getFirstLetter().equals("#")) {
                            return 1;
                        } else if (o2.getFirstLetter().equals("#")) {
                            return -1;
                        } else {
                            return o1.getFirstLetter().compareTo(o2.getFirstLetter());
                        }
                    }
                });
                LogUtils.i("tag", contactsEntities.toString());
                mHandler.sendEmptyMessage(0);
            }
        }).start();
    }

    @Override
    public void permissionDialogDismiss(int requestCode) {
        showNoDataView();
    }

    @Override
    public void initTitle() {
        setBackTitle(getString(R.string.friends_list));
    }


    private void initListener() {
        initLetterListener();
        initRecycleListener();
    }

    private void initRecycleListener() {
        mChildBinding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisibleItemPosition = manager.findFirstVisibleItemPosition();
                int selectionFromPosition = contactsAdapter.getSelectionFromPosition(firstVisibleItemPosition);
                mChildBinding.letter.updateViewByLetter(selectionFromPosition);
            }
        });
    }

    private void initLetterListener() {
        mChildBinding.letter.setOnLetterListener(new LetterView.onLetterListener() {
            @Override
            public void onLetterCallBack(String letter) {
                mChildBinding.tvLetterTip.setVisibility(View.VISIBLE);
                mChildBinding.tvLetterTip.setText(letter);
                int absolutePositionFromSelection = contactsAdapter.getAbsolutePositionFromSelection(letter.charAt(0));
                manager.scrollToPositionWithOffset(absolutePositionFromSelection, 0);
            }

            @Override
            public void onRelease() {
                mChildBinding.tvLetterTip.setVisibility(View.GONE);
            }
        });
    }

    private void setRecycleViewSetting(RecyclerView recycleViewSetting) {
        manager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, LinearLayout.VERTICAL);
        recycleViewSetting.setLayoutManager(manager);
        recycleViewSetting.addItemDecoration(dividerItemDecoration);
    }

    @Override
    public void initRequest() {
    }

    @Override
    public void createObserver() {
        initListener();
    }

    @Override
    public void initViewAndData(Bundle savedInstanceState) {
        checkPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_PERMISSION_CODE_CONTACTS);
    }
}
