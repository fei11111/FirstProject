package com.fei.firstproject.activity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.fei.firstproject.R;
import com.fei.firstproject.utils.Utils;

import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTouch;

import static com.fei.firstproject.utils.Utils.formatToDoubleDigit;

/**
 * Created by Administrator on 2018/1/2.
 * http://blog.csdn.net/huaxun66/article/details/53333747
 */

public class VideoPlayerActivity extends BaseActivity {

    private static final int REFRESH_WHAT = 1;
    private static final int HIDE_WHAT = 2;

    @BindView(R.id.iv_play)
    ImageView ivPlay;
    @BindView(R.id.tv_current_time)
    TextView tvCurrentTime;
    @BindView(R.id.iv_full_screen)
    ImageView ivFullScreen;
    @BindView(R.id.sb_time)
    SeekBar sbTime;
    @BindView(R.id.surfaceView)
    SurfaceView surfaceView;
    @BindView(R.id.rl_controller)
    RelativeLayout rlController;
    @BindView(R.id.sb_progress)
    SeekBar sbProgress;
    @BindView(R.id.ll_play_time)
    LinearLayout llPlayTime;

    private MediaPlayer mediaPlayer;
    private int currentPosition = 0;
    private Animation inAnimation;
    private Animation outAnimation;
    private boolean isHide = false;
    private boolean isPlaying = false;//记录锁屏时播放状态
    private boolean isLock = false;//记录是否是锁屏，锁屏后才调用onResume
    private float downX;
    private float downY;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == REFRESH_WHAT) {
                if (mediaPlayer != null) {
                    currentPosition = mediaPlayer.getCurrentPosition();
                    sbTime.setProgress(currentPosition);
                    int time = currentPosition / 1000;
                    int minute = time / 60;
                    int second = time % 60;
                    tvCurrentTime.setText(formatToDoubleDigit(minute) + ":" + formatToDoubleDigit(second));
                    mHandler.sendEmptyMessageDelayed(REFRESH_WHAT, 1000);
                }
            } else if (msg.what == HIDE_WHAT) {
                rlController.startAnimation(outAnimation);
                sbProgress.setVisibility(View.GONE);
                isHide = !isHide;
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
        return R.layout.activity_video;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        initAnimation();
        initMediaPlayer();
        initListener();
        initSurface();
    }

    private void initSurface() {
        SurfaceHolder holder = surfaceView.getHolder();
        holder.addCallback(new MyCallBack());
    }

    private void initListener() {
        sbTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    currentPosition = progress;
                    refreshView();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer.start();
                //进度条
                int duration = mediaPlayer.getDuration();
                sbTime.setMax(duration);
                sbProgress.setMax(duration);
                //总时长
                int time = mediaPlayer.getDuration() / 1000;
                int minute = time / 60;
                int second = time % 60;
                //当前时间
                tvCurrentTime.setText("00:00/" + Utils.formatToDoubleDigit(minute) + ":" + Utils.formatToDoubleDigit(second));
                mHandler.sendEmptyMessageDelayed(REFRESH_WHAT, 1000);
                mHandler.sendEmptyMessageDelayed(HIDE_WHAT, 1000);
            }
        });
    }

    /**
     * 动画效果
     */
    private void initAnimation() {
        inAnimation = AnimationUtils.loadAnimation(this, R.anim.dialog_bottom_in_animation);
        inAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                rlController.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        outAnimation = AnimationUtils.loadAnimation(this, R.anim.dialog_bottom_out_animation);
        outAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                rlController.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private class MyCallBack implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            play();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            stop();
        }
    }

    @Override
    public void initRequest() {

    }

    /**
     * 开始
     */
    private void play() {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.setDisplay(surfaceView.getHolder());
            Uri uri = Uri.parse("http://220.170.49.103/5/q/c/b/t/qcbtgdrzcagiurhsrcszksmyhgtlvx/he.yinyuetai.com/0FF7014EAEF781F14E9784C3B30944E0.flv");
            try {
                mediaPlayer.setDataSource(this, uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepareAsync();
        }
    }

    /**
     * 暂停/开始
     */
    private void pause() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                currentPosition = mediaPlayer.getCurrentPosition();
                ivPlay.setImageResource(R.drawable.ic_play);
                mHandler.removeMessages(REFRESH_WHAT);
            } else {
                mediaPlayer.seekTo(currentPosition);
                mediaPlayer.start();
                ivPlay.setImageResource(R.drawable.ic_pause);
                mHandler.sendEmptyMessageDelayed(REFRESH_WHAT, 1000);
            }
        }
    }

    private void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    /**
     * 暂停/开始
     */
    @OnClick(R.id.iv_play)
    void clickPlay(View view) {
        pause();
    }

    @OnTouch(R.id.surfaceView)
    boolean onTouchSurface(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mHandler.removeMessages(HIDE_WHAT);
                downX = motionEvent.getX();
                downY = motionEvent.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = motionEvent.getX();
                float moveY = motionEvent.getY();
                int distanceX = (int) (moveX - downX);
                int distanceY = (int) (moveY - downY);

                if (Math.abs(distanceY) < 50) {
                    if (distanceX > 100) {
                        //前进
                        sbProgress.setVisibility(View.VISIBLE);
                        sbProgress.setProgress(currentPosition);
                        currentPosition = currentPosition + 1500;
                        sbProgress.setProgress(currentPosition);
                        refreshView();
                        downX = moveX;
                        downY = moveY;
                    } else if (distanceX < -100) {
                        //后退
                        sbProgress.setVisibility(View.VISIBLE);
                        sbProgress.setProgress(currentPosition);
                        currentPosition = currentPosition - 1500;
                        sbProgress.setProgress(currentPosition);
                        refreshView();
                        downX = moveX;
                        downY = moveY;
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                if (isHide) {
                    rlController.startAnimation(inAnimation);
                } else {
                    rlController.startAnimation(outAnimation);
                }
                mHandler.sendEmptyMessageDelayed(HIDE_WHAT, 3000);
                isHide = !isHide;
                break;
        }
        return true;
    }

    private void refreshView() {
        sbTime.setProgress(currentPosition);
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(currentPosition);
            int time = currentPosition / 1000;
            int minute = time / 60;
            int second = time % 60;
            tvCurrentTime.setText(formatToDoubleDigit(minute) + ":" + formatToDoubleDigit(second));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isLock) {
            if (mediaPlayer != null) {
                mediaPlayer.seekTo(currentPosition);
                if (isPlaying) {
                    mediaPlayer.start();
                    ivPlay.setImageResource(R.drawable.ic_pause);
                    mHandler.sendEmptyMessageDelayed(REFRESH_WHAT, 1000);
                } else {
                    ivPlay.setImageResource(R.drawable.ic_play);
                    mHandler.removeMessages(REFRESH_WHAT);
                }
            }
        }
        isLock = false;
    }

    @Override
    protected void onPause() {
        if (mediaPlayer != null) {
            isPlaying = mediaPlayer.isPlaying();
            currentPosition = mediaPlayer.getCurrentPosition();
            if (isPlaying) {
                mediaPlayer.pause();
                ivPlay.setImageResource(R.drawable.ic_play);
                mHandler.removeMessages(REFRESH_WHAT);
            }
        }
        isLock = true;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        stop();
        mHandler.removeMessages(HIDE_WHAT);
        mHandler.removeMessages(REFRESH_WHAT);
        super.onDestroy();
    }
}
