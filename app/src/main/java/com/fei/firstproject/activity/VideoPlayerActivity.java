package com.fei.firstproject.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
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
import com.fei.firstproject.dialog.TipDialog;
import com.fei.firstproject.entity.DownLoadEntity;
import com.fei.firstproject.service.DownLoadService;
import com.fei.firstproject.utils.LogUtils;
import com.fei.firstproject.utils.PathUtils;
import com.fei.firstproject.utils.ScreenRotateUtil;
import com.fei.firstproject.utils.Utils;

import java.io.File;
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
    private static final int HIDE_BOTTOM_PROGRESS = 2;
    private static final int HIDE_CENTER_PROGRESS = 3;

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
    @BindView(R.id.ll_left_controller)
    LinearLayout llLeftController;
    @BindView(R.id.tv_progress_time)
    TextView tvProgressTime;
    @BindView(R.id.ll_progress)
    LinearLayout llProgress;
    @BindView(R.id.tv_sound)
    TextView tvSound;
    @BindView(R.id.ll_sound)
    LinearLayout llSound;
    @BindView(R.id.rl_content)
    RelativeLayout rlContent;
    @BindView(R.id.iv_download)
    ImageView ivDownload;
    @BindView(R.id.ll_right_controller)
    LinearLayout llRightController;

    private MediaPlayer mediaPlayer;
    private int currentPosition = -1;
    private String totalTime;
    private Animation inAnimation;
    private Animation outAnimation;
    private boolean isHide = false;
    private float downX;
    private float downY;
    private boolean isMove = false;
    private boolean isPrepared = false;//判断是否准备好了
    private AudioManager audioManager;
    private int videoWidth;
    private int videoHeight;
    private DownLoadEntity downLoadEntity;
    private TipDialog tipDialog;

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
                    tvCurrentTime.setText(formatToDoubleDigit(minute) + ":" + formatToDoubleDigit(second) + "/" + totalTime);
                    mHandler.sendEmptyMessageDelayed(REFRESH_WHAT, 1000);
                }
            } else if (msg.what == HIDE_BOTTOM_PROGRESS) {
                //底下进度条,3秒过后没有隐藏就开启动画隐藏
                rlController.startAnimation(outAnimation);
                isHide = !isHide;
            } else if (msg.what == HIDE_CENTER_PROGRESS) {
                //中间进度条
                llProgress.setVisibility(View.GONE);
                llSound.setVisibility(View.GONE);
            }
        }
    };

    @Override
    protected void onResume() {
        ScreenRotateUtil.getInstance(getApplicationContext()).start(this);
        ScreenRotateUtil.getInstance(getApplicationContext()).setEffetSysSetting(true);
        if (currentPosition > 0) {//进入后台后返回
            if (mediaPlayer == null) {
                initMediaPlayer();
            } else {
                mediaPlayer.seekTo(currentPosition);
                mediaPlayer.start();
                ivPlay.setImageResource(R.drawable.ic_pause);
                mHandler.sendEmptyMessageDelayed(REFRESH_WHAT, 1000);
            }
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        ScreenRotateUtil.getInstance(getApplicationContext()).stop();
        if (mediaPlayer != null) {
            currentPosition = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
            ivPlay.setImageResource(R.drawable.ic_play);
            mHandler.removeMessages(REFRESH_WHAT);
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        isMove = false;
        isHide = true;
        isPrepared = false;
        llSound.setVisibility(View.GONE);
        llProgress.setVisibility(View.GONE);
        stop();
    }

    @Override
    protected void onDestroy() {
        currentPosition = -1;
        mHandler.removeMessages(HIDE_BOTTOM_PROGRESS);
        mHandler.removeMessages(REFRESH_WHAT);
        mHandler.removeMessages(HIDE_CENTER_PROGRESS);
        super.onDestroy();
    }

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
    public void initTitle() {
        appHeadView.setVisibility(View.GONE);
    }

    @Override
    public void init(Bundle savedInstanceState) {
        String url = getIntent().getStringExtra("url");
        if (!TextUtils.isEmpty(url)) {
            initDownloadEntity(url);
            initAnimation();
            initMediaPlayer();
            initListener();
            initSurface();
        }
    }

    @Override
    public void initRequest() {

    }

    private void initDownloadEntity(String url) {
        //得到url后先生存DownLoadEntity
        int i = url.lastIndexOf("/");
        String fileName = url.substring(i + 1);
        //sp里未保存过
        downLoadEntity = new DownLoadEntity();
        downLoadEntity.setDownloadUrl(url);
        downLoadEntity.setInstall(false);
        downLoadEntity.setName(fileName);
        downLoadEntity.setSavePath(PathUtils.getDownloadPath() + File.separator + "." + fileName);//加了点作为隐藏文件
        downLoadEntity.setFlag(-1);
        downLoadEntity.setDone(false);
        downLoadEntity.setProgress(0);
        downLoadEntity.setTotalLength(0);
        downLoadEntity.setBuilder(null);
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
                    if (isPrepared) {
                        currentPosition = progress;
                        refreshView();
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (isPrepared) {
                    proShow();
                }
            }
        });
    }

    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
//                proDisimis();
                isPrepared = true;
                if (currentPosition != -1) {
                    mediaPlayer.seekTo(currentPosition);
                }
                mediaPlayer.start();
                ivPlay.setImageResource(R.drawable.ic_pause);
                //进度条
                int duration = mediaPlayer.getDuration();
                sbTime.setMax(duration);
                sbProgress.setMax(duration);
                //总时长
                int time = mediaPlayer.getDuration() / 1000;
                int minute = time / 60;
                int second = time % 60;
                totalTime = Utils.formatToDoubleDigit(minute) + ":" + Utils.formatToDoubleDigit(second);
                //当前时间
                tvCurrentTime.setText("00:00/" + totalTime);
                mHandler.sendEmptyMessageDelayed(REFRESH_WHAT, 1000);
                mHandler.sendEmptyMessageDelayed(HIDE_BOTTOM_PROGRESS, 1000);
            }
        });
        mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                if (width == 0 || height == 0) {
                    return;
                }
                videoWidth = width;
                videoHeight = height;
                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);
                int screenWidth = dm.widthPixels;

                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp.addRule(RelativeLayout.CENTER_IN_PARENT);
                lp.width = screenWidth;
                lp.height = screenWidth * height / width;
                surfaceView.setLayoutParams(lp);
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                complete();
            }
        });
        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                LogUtils.i("tag", "percent-" + percent);
                if (mediaPlayer != null && mediaPlayer.isPlaying() && progressDialog.isShowing()) {
                    proDisimis();
                }
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                proDisimis();
                if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
                    Utils.showToast(VideoPlayerActivity.this, "服务器错误");
                } else if (what == MediaPlayer.MEDIA_ERROR_UNKNOWN) {
                    Utils.showToast(VideoPlayerActivity.this, "视频文件错误");
                }
                return true;
            }
        });
    }

    /**
     * 播放完成后
     */
    private void complete() {
        isPrepared = false;
        ivPlay.setImageResource(R.drawable.ic_play);
        mediaPlayer.stop();
        currentPosition = 0;
        getVideoFromLocal(false);
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
                isHide = false;
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
                isHide = true;
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
            proShow();
            getVideoFromLocal(true);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            stop();
        }

    }

    /**
     * 判断本地是否有该视频,本地有就播放本地
     */
    private void getVideoFromLocal(boolean isPlay) {
        if (downLoadEntity == null) return;
        String savePath = downLoadEntity.getSavePath();
        if (new File(savePath).exists()) {
            play(isPlay, savePath);
        } else {
            play(isPlay, downLoadEntity.getDownloadUrl());
        }
    }

    /**
     * 开始
     */
    private void play(boolean isPlay, String path) {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.setDisplay(surfaceView.getHolder());

            try {
                mediaPlayer.setDataSource(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //播放时屏幕保持唤醒
            mediaPlayer.setScreenOnWhilePlaying(true);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            if (isPlay) {
                mediaPlayer.prepareAsync();
            }
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
        if (isPrepared) {
            pause();
        } else {
            if (mediaPlayer != null) {
                if (mediaPlayer.getDuration() > 0) {
                    //设置了资源才进入准备状态
                    proShow();
                    mediaPlayer.prepareAsync();
                }
            }
        }
    }

    /**
     * 切换横竖屏
     */
    @OnClick(R.id.iv_full_screen)
    void clickFullScreen(View view) {
        if (!isPrepared) return;
        ScreenRotateUtil.getInstance(this).toggleRotate();
    }

    /**
     * 下载
     */
    @OnClick(R.id.iv_download)
    void clickDownLoad(View view) {
        if (!isPrepared) return;
        if (downLoadEntity != null) {
            if (new File(downLoadEntity.getSavePath()).exists()) {
                //文件存在
                if (tipDialog == null) {
                    tipDialog = new TipDialog(this);
                    tipDialog.setCanceledOnTouchOutside(false);
                    tipDialog.setContentText(getResources().getString(R.string.file_exit_if_download));
                    tipDialog.setConfirmButtonText(getResources().getString(R.string.go_to_download));
                    tipDialog.setOnConfirmListener(new TipDialog.OnConfirmListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(VideoPlayerActivity.this, DownLoadService.class);
                            intent.putExtra("downloadEntity", downLoadEntity);
                            startService(intent);
                        }
                    });
                }
                tipDialog.show();
            } else {
                Intent intent = new Intent(this, DownLoadService.class);
                intent.putExtra("downloadEntity", downLoadEntity);
                startService(intent);
            }
        }
        mHandler.removeMessages(HIDE_BOTTOM_PROGRESS);
        rlController.clearAnimation();
        mHandler.sendEmptyMessage(HIDE_BOTTOM_PROGRESS);
    }

    /**
     * 触屏
     */
    @OnTouch(R.id.rl_content)
    boolean onTouchSurface(View view, MotionEvent motionEvent) {
        if (!isPrepared || mediaPlayer == null) {
            return false;
        } else {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isMove = false;
                    downX = motionEvent.getX();
                    downY = motionEvent.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float moveX = motionEvent.getX();
                    float moveY = motionEvent.getY();
                    int distanceX = (int) (moveX - downX);
                    int distanceY = (int) (moveY - downY);
                    if (Math.abs(distanceX) < 50 && Math.abs(distanceY) > 100) {
                        mHandler.removeMessages(HIDE_CENTER_PROGRESS);
                        llProgress.setVisibility(View.GONE);
                        isMove = true;
                        //音量
                        if (distanceY < 0) {
                            //增大
                            setVolume(true);
                        } else {
                            //降低
                            setVolume(false);
                        }
                        downX = moveX;
                        downY = moveY;
                    } else if (Math.abs(distanceY) < 50 && Math.abs(distanceX) > 100) {
                        mHandler.removeMessages(HIDE_CENTER_PROGRESS);
                        llSound.setVisibility(View.GONE);
                        isMove = true;
                        //快进/后退
                        currentPosition = currentPosition + distanceX * 50;
                        if (currentPosition < 0) {
                            currentPosition = 0;
                        } else if (currentPosition > mediaPlayer.getDuration()) {
                            currentPosition = mediaPlayer.getDuration();
                        }
                        llProgress.setVisibility(View.VISIBLE);
                        sbProgress.setProgress(currentPosition);
                        int time = currentPosition / 1000;
                        int minute = time / 60;
                        int second = time % 60;
                        tvProgressTime.setText(formatToDoubleDigit(minute) + ":" + formatToDoubleDigit(second));
                        refreshView();
                        downX = moveX;
                        downY = moveY;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (!isMove) {
                        //未移动
                        mHandler.removeMessages(HIDE_BOTTOM_PROGRESS);
                        rlController.clearAnimation();
                        if (isHide) {
                            rlController.startAnimation(inAnimation);
                            //出现后3秒后隐藏
                            mHandler.sendEmptyMessageDelayed(HIDE_BOTTOM_PROGRESS, 3000);
                        } else {
                            rlController.startAnimation(outAnimation);
                        }
                    } else {
                        mHandler.sendEmptyMessageDelayed(HIDE_CENTER_PROGRESS, 1000);
                    }
                    break;
            }
            return true;
        }
    }

    /**
     * 设置音量
     */

    private void setVolume(boolean flag) {
        // 获取音量管理器
        if (audioManager == null) {
            audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        }
        // 获取当前音量
        int curretnV = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        //得到听筒模式的最大值
        int streamMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        if (flag) {
            curretnV++;
            if (curretnV > streamMaxVolume) {
                curretnV = streamMaxVolume;
            }
            //增加音量
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
        } else {
            curretnV--;
            if (curretnV < 0) {
                curretnV = 0;
            }
            //增加音量
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
        }
        llSound.setVisibility(View.VISIBLE);
        int sound = (int) (curretnV * 1f / streamMaxVolume * 100);
        tvSound.setText(sound + "%");
        mHandler.sendEmptyMessageDelayed(HIDE_CENTER_PROGRESS, 1000);
        /**
         * 1.AudioManager.STREAM_MUSIC 多媒体 2.AudioManager.STREAM_ALARM 闹钟
         * 3.AudioManager.STREAM_NOTIFICATION 通知 4.AudioManager.STREAM_RING 铃音
         * 5.AudioManager.STREAM_SYSTEM 系统提示音 6.AudioManager.STREAM_VOICE_CALL
         * 电话
         *
         * AudioManager.FLAG_SHOW_UI:显示音量控件
         */
    }


    private void refreshView() {
        sbTime.setProgress(currentPosition);
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(currentPosition);
            int time = currentPosition / 1000;
            int minute = time / 60;
            int second = time % 60;
            tvCurrentTime.setText(formatToDoubleDigit(minute) + ":" + formatToDoubleDigit(second) + "/" + totalTime);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (isPrepared) {
            isMove = false;
            isHide = true;
            mHandler.removeMessages(HIDE_BOTTOM_PROGRESS);
            mHandler.removeMessages(REFRESH_WHAT);
            mHandler.removeMessages(HIDE_CENTER_PROGRESS);
            llSound.setVisibility(View.GONE);
            llProgress.setVisibility(View.GONE);
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            int screenWidth = dm.widthPixels;
            int screenHeight = dm.heightPixels;

            if (newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                //竖屏
                ivFullScreen.setImageResource(R.drawable.ic_open_full_sreen);
                RelativeLayout.LayoutParams surfaceParams = (RelativeLayout.LayoutParams) surfaceView.getLayoutParams();
                surfaceParams.width = screenWidth;
                surfaceParams.height = screenWidth * videoHeight / videoWidth;
                surfaceView.setLayoutParams(surfaceParams);
            } else {
                //横屏
                ivFullScreen.setImageResource(R.drawable.ic_close_full_screen);
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) surfaceView.getLayoutParams();
                layoutParams.width = screenWidth;
                layoutParams.height = screenHeight - Utils.getStatusBarHeight(this);
                surfaceView.setLayoutParams(layoutParams);
            }
            //屏幕转化后需要重新启动handler
            mHandler.sendEmptyMessageDelayed(REFRESH_WHAT, 1000);
            mHandler.sendEmptyMessageDelayed(HIDE_BOTTOM_PROGRESS, 1000);
        }
    }

}
