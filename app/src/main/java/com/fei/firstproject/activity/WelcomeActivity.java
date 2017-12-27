package com.fei.firstproject.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.fei.firstproject.R;
import com.fei.firstproject.widget.ReciprocalView;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/12/25.
 */

public class WelcomeActivity extends BaseActivity {

    @BindView(R.id.giv_splash)
    ImageView givSplash;
    @BindView(R.id.spView)
    ReciprocalView spView;

    private static final int messageWhat = 1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int time = msg.arg1;
            time--;
            if (time == 0) {
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                WelcomeActivity.this.finish();
            } else {
                spView.setContent(time + "s");
                Message m = Message.obtain();
                m.what = messageWhat;
                m.arg1 = time;
                mHandler.sendMessageDelayed(m, 1000);
            }
        }
    };

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
        return R.layout.activity_splash;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        initView();
        initListener();
    }

    private void initListener() {
        spView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.removeMessages(messageWhat);
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                WelcomeActivity.this.finish();
            }
        });
    }

    private void initView() {
        Glide.with(this)
                .load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1514192826178&di=81727587ce4f9b5461210b35a734b921&imgtype=0&src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F01e51c58608f42a8012060c8d8d0e8.gif")
                .transition(new DrawableTransitionOptions().crossFade(2000))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        spView.setContent("3s");
                        Message msg = Message.obtain();
                        msg.what = messageWhat;
                        msg.arg1 = 3;
                        mHandler.sendMessageDelayed(msg, 1000);
                        return false;
                    }
                }).into(givSplash);
    }

    @Override
    public void initRequest() {

    }
}
