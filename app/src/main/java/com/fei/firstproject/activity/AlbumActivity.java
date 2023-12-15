package com.fei.firstproject.activity;

import android.Manifest;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.common.utils.FileUtil;
import com.common.viewmodel.EmptyViewModel;
import com.fei.firstproject.R;
import com.fei.firstproject.adapter.AlbumAdapter;
import com.fei.firstproject.databinding.ActivityAlbumBinding;
import com.fei.firstproject.utils.ThreadPoolFactory;

import java.util.List;


public class AlbumActivity extends BaseProjectActivity<EmptyViewModel, ActivityAlbumBinding> {

    private final int PERMISSION_READ_STORAGE = 100;
    private final int MSG_WHAT = 1;

    private Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_WHAT) {
                dismissLoading();
                List<Uri> list = (List<Uri>) msg.obj;
                mChildBinding.gvAlbum.setAdapter(new AlbumAdapter(AlbumActivity.this, list, mChildBinding.gvAlbum));
            }

        }
    };

    @Override
    public void permissionsDeniedCallBack(int requestCode) {
        if (requestCode == PERMISSION_READ_STORAGE) {
            showMissingPermissionDialog("读取相册失败", PERMISSION_READ_STORAGE);
        }
    }

    @Override
    public void permissionsGrantCallBack(int requestCode) {
        if (requestCode == PERMISSION_READ_STORAGE) {
            getAlbumFromLocal();
        }
    }

    private void getAlbumFromLocal() {
        ThreadPoolFactory.getNormalPool().execute(new Runnable() {
            @Override
            public void run() {
                List<Uri> uris = FileUtil.loadPhotoFiles(AlbumActivity.this);
                Message msg = Message.obtain();
                msg.obj = uris;
                msg.what = MSG_WHAT;
                mHandler.sendMessage(msg);
            }
        });
    }

    @Override
    public void permissionDialogDismiss(int requestCode) {

    }

    @Override
    public void initTitle() {
        appHeadView.setMiddleText(getString(R.string.album));
    }


    @Override
    public void initRequest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, PERMISSION_READ_STORAGE);
        } else {
            checkPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ_STORAGE);
        }
    }

    @Override
    public void createObserver() {

    }

    @Override
    public void initViewAndData(Bundle savedInstanceState) {

    }
}
