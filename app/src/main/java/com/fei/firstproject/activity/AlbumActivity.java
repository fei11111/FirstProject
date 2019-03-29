package com.fei.firstproject.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.widget.GridView;

import com.fei.firstproject.R;
import com.fei.firstproject.adapter.AlbumAdapter;
import com.fei.firstproject.utils.ThreadPoolFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class AlbumActivity extends BaseActivity {

    @BindView(R.id.gv_album)
    GridView gvAlbum;

    private final int PERMISSION_READ_STORAGE = 100;
    private final int MSG_WHAT = 1;

    private Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_WHAT) {
                dismissLoading();
                List<String> list = (List<String>) msg.obj;
                gvAlbum.setAdapter(new AlbumAdapter(AlbumActivity.this, list, gvAlbum));
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
                List<String> result = new ArrayList<String>();
                Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

                ContentResolver contentResolver = getContentResolver();
                Cursor cursor = contentResolver.query(uri, null, null, null, null);
                if (cursor != null && cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        int index = cursor
                                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        String path = cursor.getString(index); // 文件地址
                        File file = new File(path);
                        if (file.exists()) {
                            result.add(path);
                        }
                    }
                    Message msg = Message.obtain();
                    msg.obj = result;
                    msg.what = MSG_WHAT;
                    mHandler.sendMessage(msg);
                }
            }
        });
    }

    @Override
    public void permissionDialogDismiss(int requestCode) {

    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_album;
    }

    @Override
    public void initTitle() {

    }

    @Override
    public void init(Bundle savedInstanceState) {

    }

    @Override
    public void initRequest() {
        checkPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ_STORAGE);
    }

}
