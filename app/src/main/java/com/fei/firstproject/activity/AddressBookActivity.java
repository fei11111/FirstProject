package com.fei.firstproject.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fei.firstproject.R;
import com.fei.firstproject.adapter.ContactsAdapter;
import com.fei.firstproject.entity.ContactsEntity;
import com.fei.firstproject.utils.LogUtils;
import com.fei.firstproject.widget.AppHeadView;
import com.fei.firstproject.widget.LetterView;
import com.github.promeg.pinyinhelper.Pinyin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/12/18.
 * 参考：http://blog.csdn.net/u012702547/article/details/51234227
 */

public class AddressBookActivity extends BaseActivity {

    private static final int REQUEST_PERMISSION_CODE_CONTACTS = 100;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.letter)
    LetterView letter;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tv_letter_tip)
    TextView tvLetterTip;

    private List<ContactsEntity> contactsEntities = new ArrayList<>();
    private LinearLayoutManager manager;
    private ContactsAdapter contactsAdapter;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (contactsEntities.size() > 0) {
                dismissLoading();
                setRecycleViewSetting(recyclerView);
                if (contactsAdapter == null) {
                    contactsAdapter = new ContactsAdapter(AddressBookActivity.this, contactsEntities);
                    recyclerView.setAdapter(contactsAdapter);
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
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
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
    public int getContentViewResId() {
        return R.layout.activity_address_book;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        initListener();
        checkPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_PERMISSION_CODE_CONTACTS);
    }

    private void initListener() {
        initAppHeadViewListener();
        initLetterListener();
        initRecycleListener();
    }

    private void initRecycleListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisibleItemPosition = manager.findFirstVisibleItemPosition();
                int selectionFromPosition = contactsAdapter.getSelectionFromPosition(firstVisibleItemPosition);
                letter.updateViewByLetter(selectionFromPosition);
            }
        });
    }

    private void initLetterListener() {
        letter.setOnLetterListener(new LetterView.onLetterListener() {
            @Override
            public void onLetterCallBack(String letter) {
                tvLetterTip.setVisibility(View.VISIBLE);
                tvLetterTip.setText(letter);
                int absolutePositionFromSelection = contactsAdapter.getAbsolutePositionFromSelection(letter.charAt(0));
                manager.scrollToPositionWithOffset(absolutePositionFromSelection,0);
            }

            @Override
            public void onRelease() {
                tvLetterTip.setVisibility(View.GONE);
            }
        });
    }

    private void initAppHeadViewListener() {
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

    private void setRecycleViewSetting(RecyclerView recycleViewSetting) {
        manager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, LinearLayout.VERTICAL);
        recycleViewSetting.setLayoutManager(manager);
        recycleViewSetting.addItemDecoration(dividerItemDecoration);
    }

    @Override
    public void initRequest() {
    }

}
