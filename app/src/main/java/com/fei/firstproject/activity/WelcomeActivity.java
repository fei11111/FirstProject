package com.fei.firstproject.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.common.viewmodel.EmptyViewModel;
import com.fei.firstproject.R;
import com.fei.firstproject.databinding.ActivityWelcomeBinding;
import com.fei.firstproject.utils.Utils;


/**
 * Created by Administrator on 2017/12/25.
 */

public class WelcomeActivity extends BaseProjectActivity<EmptyViewModel, ActivityWelcomeBinding> {

    private static final int REQUEST_PERMISSION_CODE_STORAGE = 100;

    private static final int messageWhat = 1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int time = msg.arg1;
            time--;
            if (time == 0) {
                mChildBinding.spView.setContent(time + "s");
                Glide.with(mChildBinding.givSplash).onStop();
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                Glide.with(mChildBinding.givSplash).onDestroy();
                WelcomeActivity.this.finish();
            } else {
                mChildBinding.spView.setContent(time + "s");
                Message m = Message.obtain();
                m.what = messageWhat;
                m.arg1 = time;
                mHandler.sendMessageDelayed(m, 1000);
            }
        }
    };

    @Override
    public void permissionsDeniedCallBack(int requestCode) {
        if (requestCode == REQUEST_PERMISSION_CODE_STORAGE) {
            showMissingPermissionDialog(getString(R.string.need_storage_permission), requestCode);
        }
    }

    @Override
    public void permissionsGrantCallBack(int requestCode) {

    }

    @Override
    public void permissionDialogDismiss(int requestCode) {
        if (requestCode == REQUEST_PERMISSION_CODE_STORAGE) {
            Utils.showToast(this, getString(R.string.storage_permission_fail));
        }
    }

    @Override
    public void initTitle() {
        appHeadView.setVisibility(View.GONE);
    }


    private void initPermission() {
        checkPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE_STORAGE);
    }

    private void initListener() {
        mChildBinding.spView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.removeMessages(messageWhat);
                Glide.with(mChildBinding.givSplash).onStop();
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                Glide.with(mChildBinding.givSplash).onDestroy();
                WelcomeActivity.this.finish();
            }
        });
    }

    private void initView() {
        Glide.with(this)
                .load("http://img9.jiwu.com/jiwu_news_pics/20170103/1483428523282_000.gif")
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                        WelcomeActivity.this.finish();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        mChildBinding.spView.setContent("3s");
                        Message msg = Message.obtain();
                        msg.what = messageWhat;
                        msg.arg1 = 3;
                        mHandler.sendMessageDelayed(msg, 1000);
                        return false;
                    }
                }).into(mChildBinding.givSplash);
    }

    @Override
    public void initRequest() {

    }

    @Override
    public void createObserver() {

    }

    @Override
    public void initViewAndData(Bundle savedInstanceState) {
        initView();
        initListener();
        initPermission();
    }
}
