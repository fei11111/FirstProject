package com.fei.firstproject.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.fei.firstproject.R;
import com.fei.firstproject.config.AppConfig;
import com.fei.firstproject.download.inter.ProgressListener;
import com.fei.firstproject.entity.DownLoadEntity;
import com.fei.firstproject.http.factory.RetrofitFactory;
import com.fei.firstproject.http.inter.RequestApi;
import com.fei.firstproject.utils.LogUtils;
import com.fei.firstproject.utils.NetUtils;
import com.fei.firstproject.utils.SPUtils;
import com.fei.firstproject.utils.Utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

import static com.fei.firstproject.config.AppConfig.notificationId;

/**
 * Created by Administrator on 2018/1/5.
 * http://blog.csdn.net/vipzjyno1/article/details/25248021/
 */

public class DownLoadService extends Service {

    private NotificationManager notificationManager;

    /*  开始下载*/
    private static final int DOWNLOAD_START = 100;
    /* 下载中 和 完成下载*/
    private static final int DOWNLOAD_FINISHED = 101;
    /* 下载失败 */
    private static final int DOWNLOAD_ERROR = 102;
    /* 保存成功 */
    private static final int SAVE_FINISHED = 103;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            DownLoadEntity downLoadEntity = (DownLoadEntity) msg.obj;
            switch (msg.what) {
                case DOWNLOAD_START:
                    Utils.showToast(DownLoadService.this, "下载中...");
                    break;
                case DOWNLOAD_FINISHED:
                    if (downLoadEntity != null) {
                        Utils.showToast(DownLoadService.this, downLoadEntity.getName() + "下载完成");
                    }
                    break;
                case DOWNLOAD_ERROR:
                    if (downLoadEntity != null) {
                        notificationManager.cancel(downLoadEntity.getFlag());
                        Utils.showToast(DownLoadService.this, downLoadEntity.getName() + "下载失败");
                    }
                    break;
                case SAVE_FINISHED:
                    if (downLoadEntity != null) {
                        Utils.showToast(DownLoadService.this, downLoadEntity.getName() + "已保存到" + downLoadEntity.getSavePath());
                    }
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.i("tag", "DownLoadService-onCreate");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            DownLoadEntity downloadEntity = (DownLoadEntity) intent.getSerializableExtra("downloadEntity");
            if (downloadEntity != null) {
                setNotificationProgrss(downloadEntity);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void refreshNotification(DownLoadEntity downLoadEntity) {
        if (downLoadEntity != null) {
            if (downLoadEntity.isDone() && downLoadEntity.getProgress() == downLoadEntity.getTotalLength()) {
                notificationManager.cancel(downLoadEntity.getFlag());
                Message message = Message.obtain();
                message.what = DOWNLOAD_FINISHED;
                message.obj = downLoadEntity;
                mHandler.sendMessage(message);
                LogUtils.i("tag", "发送下载成功");
                if (downLoadEntity.isInstall()) {
                    String savePath = downLoadEntity.getSavePath();
                    if (!TextUtils.isEmpty(savePath)) {
                        installApk(savePath);
                    }
                }
            } else {
                NotificationCompat.Builder builder = downLoadEntity.getBuilder();
                if (builder != null) {
                    RemoteViews contentView = builder.getContentView();
                    if (contentView != null) {
                        long progress = downLoadEntity.getProgress();
                        long total = downLoadEntity.getTotalLength();
                        contentView.setProgressBar(R.id.pb_notification, 100, (int) ((progress * 1.0f / total) * 100), false);
                        contentView.setTextViewText(R.id.tv_notification_progress, Utils.bytes2kb(progress) + "/" + Utils.bytes2kb(total));
                        notificationManager.notify(downLoadEntity.getFlag(), builder.build());
                    }
                }
            }
        }
    }

    protected void setNotificationProgrss(final DownLoadEntity downloadEntity) {
        notificationId++;
        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());
        RemoteViews mRemoteViews = new RemoteViews(getPackageName(), R.layout.view_notification);
        mRemoteViews.setImageViewResource(R.id.iv_notification, R.drawable.ic_app);
        mRemoteViews.setTextViewText(R.id.tv_notification_name, downloadEntity.getName());
        mRemoteViews.setProgressBar(R.id.pb_notification, 100, 0, false);
        mBuilder
                .setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
                .setTicker("下载中...")
                .setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级

                .setOngoing(true)
                .setCustomContentView(mRemoteViews)//设置通知栏标题
                .setContentIntent(getDefalutIntent(Notification.FLAG_ONGOING_EVENT))
                .setSmallIcon(R.drawable.ic_download);
        Notification notify = mBuilder.build();
        notify.flags = Notification.FLAG_ONGOING_EVENT;
        notificationManager.notify(notificationId, notify);

        downloadEntity.setBuilder(mBuilder);
        downloadEntity.setFlag(AppConfig.notificationId);
        SPUtils.put(this, "notificationId", notificationId);
        //下载
        download(downloadEntity);
    }

    public PendingIntent getDefalutIntent(int flags) {
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, new Intent(), flags);
        return pendingIntent;
    }

    /**
     * 下载
     */
    private void download(final DownLoadEntity downloadEntity) {
        mHandler.sendEmptyMessage(DOWNLOAD_START);
        final RequestApi downLoad = RetrofitFactory.getDownLoad(new ProgressListener() {
            @Override
            public void onProgress(long progress, long total, boolean done) {
                LogUtils.i("tag", "progress--" + progress + "--total--" + total);
                synchronized (downloadEntity) {
                    downloadEntity.setProgress(progress);
                    downloadEntity.setTotalLength(total);
                    if (!downloadEntity.isDone()) {
                        if (done) {
                            downloadEntity.setBuilder(null);
                            downloadEntity.setDone(true);
                        }
                        refreshNotification(downloadEntity);
                    }
                }
            }
        });
        downLoad.downloadFile(downloadEntity.getDownloadUrl())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        // 可添加网络连接判断等
                        if (!NetUtils.isConnected(DownLoadService.this)) {
                            Message message = Message.obtain();
                            message.obj = downloadEntity;
                            message.what = DOWNLOAD_ERROR;
                            mHandler.sendMessage(message);
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull ResponseBody responseBody) {
                        if (responseBody != null) {
                            try {
                                InputStream is = responseBody.byteStream();
                                File file = new File(downloadEntity.getSavePath());
                                if (file.exists()) {
                                    //存在，删除
                                    file.delete();
                                }
                                FileOutputStream fos = new FileOutputStream(file);
                                BufferedInputStream bis = new BufferedInputStream(is);
                                byte[] buffer = new byte[1024];
                                int len;
                                while ((len = bis.read(buffer)) != -1) {
                                    fos.write(buffer, 0, len);
                                    fos.flush();
                                }
                                fos.close();
                                bis.close();
                                is.close();
                                Message message = Message.obtain();
                                message.what = SAVE_FINISHED;
                                message.obj = downloadEntity;
                                mHandler.sendMessage(message);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Message message = Message.obtain();
                        message.obj = downloadEntity;
                        message.what = DOWNLOAD_ERROR;
                        mHandler.sendMessage(message);
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    /**
     * 安装APK文件
     */
    private void installApk(String filePath) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(new File(filePath)),
                "application/vnd.android.package-a rchive");
        startActivity(intent);
    }
}
